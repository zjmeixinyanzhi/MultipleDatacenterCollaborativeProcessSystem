/*
 *程序名称 		: ProcessThread3.java
 *版权说明  		:
 *版本号		    : 1.0
 *功能			: 
 *开发人		    : caoyang
 *开发时间		: 2014-05-19
 *修改者		    : 
 *修改时间		: 
 *修改简要说明 	:
 *其他			:	 
 */
package SystemManage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kepler.GenerateAndParseMoml;
import org.kepler.RunMoml;

import OrderManage.L2ExternalOrder;
import OrderManage.OrderRequest;
import OrderManage.OrderStudio;
import ServiceInterface.ICPFtpFeedbackProxy;
import ServiceInterface.ICPHandleOrderProxy;
import Workflow.WorkflowAdapter;

/**
 * @author caoyang
 * 
 */
public class OrderRunningThread extends Thread {
	// 生产请求订单列表
	private ArrayList<OrderRequest> orderRequestList;
	// // 已处理的生产请求订单列表
	// private ArrayList< OrderRequest > orderRequestFinishList;
	// 已被调度，并正在进行生产的生产请求订单列表
	// private ArrayList< Order > orderSchedList;
	private ArrayList<L2ExternalOrder> l2OrderSchedList;
	// 用来进行订单管理的订单管理器
	private OrderStudio orderStudio;

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// // 用于将订单解析为工作流（子订单处理流程）的处理流程解析器
	// private WorkflowAdapter workflowAdapter;
	// // 用于将订单处理流程调度与控制的处理流程调度器
	// private WorkflowSchedular workflowSchedular;

	/**
	 * 
	 */
	public OrderRunningThread() {
		this.orderStudio = new OrderStudio();
		this.l2OrderSchedList = new ArrayList<L2ExternalOrder>();
	}

	/**
	 * @param arg0
	 */
	public OrderRunningThread(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public OrderRunningThread(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OrderRunningThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OrderRunningThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OrderRunningThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public OrderRunningThread(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public OrderRunningThread(ThreadGroup arg0, Runnable arg1, String arg2,
			long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public void run() {
		ICPFtpFeedbackProxy ftpFeedbackProxy = new ICPFtpFeedbackProxy();
		ICPHandleOrderProxy handleOrderProxy = new ICPHandleOrderProxy();

		for (;;) {
			try {
				// 获取外部订单列表
				logger.info("Thread[3].获取外部订单列表");
				this.orderRequestList = orderStudio
						.getOrderRequestList("UnTransform");

				// test
				System.out.println("Thread[3]--------------RequestOrder:");
				for (int iIndex = 0; iIndex < this.orderRequestList.size(); iIndex++) {
					System.out.println("Thread[3] "
							+ String.valueOf(iIndex + 1) + " - "
							+ this.orderRequestList.get(iIndex).jobId);
				}
				System.out.println("Thread[3]---------------------------");

				Iterator<OrderRequest> orderRequest_curr = this.orderRequestList
						.iterator();
				while (orderRequest_curr.hasNext()) {
					OrderRequest orderRequest = orderRequest_curr.next();

					// 生成并追加订单
					logger.info("7.生成并追加订单");
					L2ExternalOrder l2Order = new L2ExternalOrder(orderRequest);

					L2ExternalOrder l2OrderStorage = null;
					// if( "L2VD".equals( orderRequest.orderType ) ){
					if (orderRequest.orderType.contains("L2VD")) {
						l2Order.jobId = L2ExternalOrder
								.getL2OrderIdByMap(orderRequest.jobId);
						if (null != l2Order.jobId) {
							L2ExternalOrder.update(l2Order);
							L2ExternalOrder.orderRequestL2OrderRemove(
									orderRequest.jobId, l2Order.jobId);
							l2OrderStorage = l2Order;
						}
					} else {
						l2OrderStorage = orderStudio.addOrder(l2Order);
					}

					if (null != l2OrderStorage) {
						orderRequest.nextStatus();
					} else {
						continue;
					}
					// orderSchedList.add( order );

					// 二级订单拆分、创建三级订单、四级订单、数据调度、工作流调度
					//工作流匹配中因该增加相应的融合/同化订单的工作流程
					WorkflowAdapter workflowAdapter = new WorkflowAdapter();
					workflowAdapter.doMatch(l2OrderStorage);

					// 算法匹配放到生成工作流逻辑中
					//test
					System.out.println(">>" + l2OrderStorage.l3orderlist + " "
							+ (System.currentTimeMillis()));

					// Kepler工作流构建与运行
					// ///////////////////////////////////////////////////////////////////////////////////////

					logger.info("---------------------二级订单"
							+ l2OrderStorage.jobId
							+ "的Kepler工作流生成与执行-----------------------");
					// Kepler工作流的生成与解析
					// test
					//System.out.println("Order Run:" + l2OrderStorage.orderType);

					GenerateAndParseMoml parseMoml = new GenerateAndParseMoml(
							l2OrderStorage.jobId);
					parseMoml.run();

					// Kepler工作流的运行
					// 更新订单状态：Queue
					orderStudio.setL2OrderWorkingStatus(l2OrderStorage.jobId,
							"Queue");

					synchronized (RunMoml.class) {
						int tCount = RunMoml.getThreadCounts();
						// 如果入库线程达到了最大允许的线程数 ，需要与RunMoml中最大线程数maxThreadCounts一致
						int ThreadPool_Count = 20;
						while (tCount >= ThreadPool_Count) {
							logger.info("当前Kepler运行线程数为：" + tCount
									+ "，已达到系统设定最大线程数" + ThreadPool_Count
									+ "，等待其他运行实例执行完毕并释放系统资源后再运行！");
							// 释放锁，等待“线程数”资源，等待其他入库线程执行完毕
							try {
								RunMoml.class.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								logger.error(e);
								e.printStackTrace();
							}
							tCount = RunMoml.getThreadCounts();
						}
						RunMoml runMoml = new RunMoml(l2OrderStorage.jobId,
								parseMoml.getMoml());
						runMoml.start();
					}

					// ///////////////////////////////////////////////////////////////////////////////////////

					this.l2OrderSchedList.add(l2OrderStorage);

				}

				for (int iL2OrderSchedIndex = 0; iL2OrderSchedIndex < this.l2OrderSchedList
						.size();) {
					L2ExternalOrder l2Order = this.l2OrderSchedList
							.get(iL2OrderSchedIndex);
					L2ExternalOrder l2OrderStatusCheck = L2ExternalOrder
							.getOrder(l2Order.jobId);
					// 在这里增加一个处理状态反馈，根据l2OrderStatusCheck的状态，如果没有做完，没有出错，也需要返回每个处理阶段的状态
					if (("Error".equals(l2OrderStatusCheck.workingStatus))
							|| ("Finish"
									.equals(l2OrderStatusCheck.workingStatus))) {
						String l2OrderType = l2OrderStatusCheck.jobId
								.substring(0, 4);
						if (null != l2OrderType) {
							if (l2OrderType.equals("L2CP")) {
								// ---ServiceInterface.IOrderStatusFeedback();
								// 课题三状态反馈接口：成功或失败
								if ("Finish"
										.equals(l2OrderStatusCheck.workingStatus)) {
									// ---ServiceInterface.IProductSubmit()
									// 成功后反馈产品信息
									// 成功后反馈产品信息
									String strXML1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><id>"
											+ l2OrderStatusCheck.jobId_P3L2
											+ "</id>"
											+"<url>"
											+"ftp://124.16.184.69/CommonProductBuffer/"+l2OrderStatusCheck.jobId.replaceFirst("2", "3")
											+"</url>"
											+"<username>"
											+"ftpuser"
											+"</username>"
											+"<password>"
											+"123456"
											+"</password>"
											+"<limitdate>"
											+"2017-01-01"
											+"</limitdate>"
											+ "<memo>XX</memo></condition></root>";
									try {
										ftpFeedbackProxy.commonProductSubmit(strXML1);
										logger.info("向课题三反馈产品");
										
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									
									//调用订单结束接口
									String strXML2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><orderid>"
											+ l2OrderStatusCheck.jobId_L1
											+ "</orderid>"
											+ "<state>"
											+ "finish"
											+ "</state></condition></root>";
									
									try {
										handleOrderProxy.handleProductOrder(strXML2);
										logger.info("成功，调用课题三结束接口");
										
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								}
								
								else if(l2OrderStatusCheck.workingStatus.contains("Error")){
									//调用订单结束接口
									String strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><id>"
											+ l2OrderStatusCheck.jobId_L1
											+ "</id>"
											+ "<state>"
											+ "cancel"
											+ "</state></condition></root>";
									try {
										handleOrderProxy.handleProductOrder(strXML);
										logger.info("成产失败，调用课题三接口");
										
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									
								}
							} else if (l2OrderType.equals("L2VD")) {
								// 课题一真实性检验的接口返回产品
								// 真实性检验报告可以在此下载或者做到Kepler工作流里面
							}
						}

						this.l2OrderSchedList.remove(iL2OrderSchedIndex);
					} else {
						iL2OrderSchedIndex++;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.equals(e);
			}

			try {
				sleep(10000); // 暂停，每一秒输出一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
