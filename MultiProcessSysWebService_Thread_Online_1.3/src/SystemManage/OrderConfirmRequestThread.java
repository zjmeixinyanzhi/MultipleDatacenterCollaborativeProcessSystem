/*
 *程序名称 		: ProcessThread2.java
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import DBManage.RSDataTypeDB;
import DBManage.WorkflowDB;
import OrderManage.L2ExternalOrder;
import OrderManage.OrderRequest;
import OrderManage.OrderStudio;
import RSDataManage.Rsdatatype;
import ServiceInterface.ICPFeasibilitySubmitProxy;
import ServiceInterface.ProcessImplProxy;

import org.apache.log4j.Logger;


/**
 * @author caoyang
 * 
 */
public class OrderConfirmRequestThread extends Thread {
	// 生产请求订单列表
	private ArrayList<OrderRequest> orderRequestList;
	// 用来进行订单管理的订单管理器
	private OrderStudio orderStudio;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	/**
	 * 
	 */
	public OrderConfirmRequestThread() {
		this.orderStudio = new OrderStudio();
	}

	/**
	 * @param arg0
	 */
	public OrderConfirmRequestThread(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public OrderConfirmRequestThread(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OrderConfirmRequestThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OrderConfirmRequestThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public OrderConfirmRequestThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public OrderConfirmRequestThread(ThreadGroup arg0, Runnable arg1,
			String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public OrderConfirmRequestThread(ThreadGroup arg0, Runnable arg1,
			String arg2, long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public void run() {

		ProcessImplProxy project1Proxy = new ProcessImplProxy(
				"http://localhost:10080/Project1Webservice/services/ProcessImpl"); // 服务器代理设置
//		ICPFeasibilitySubmitProxy project3Proxy = new ICPFeasibilitySubmitProxy();
		ICPFeasibilitySubmitProxy project3Proxy = new ICPFeasibilitySubmitProxy();

		for (;;) {
			try {
				// 获取外部订单列表
				System.out.println("Thread[2].获取外部订单列表");
				this.orderRequestList = orderStudio
						.getOrderRequestList("UnFeasibility"); // 需要获取已响应订单可行数据方案反馈的生产请求订单

				// test
				System.out.println("Thread[2]--------------RequestOrder:");
				for (int iIndex = 0; iIndex < this.orderRequestList.size(); iIndex++) {
					System.out.println("Thread[2] "
							+ String.valueOf(iIndex + 1) + " - "
							+ this.orderRequestList.get(iIndex).jobId);
				}
				System.out.println("Thread[2]---------------------------");

				Iterator<OrderRequest> orderRequest_curr = this.orderRequestList
						.iterator();
				while (orderRequest_curr.hasNext()) {
					OrderRequest orderRequest = orderRequest_curr.next();

					// 查询每条数据的元数据信息【共性产品L2CP/真实性检验订单需要查询L2VD】，
					// （该部分应修改为：仲波老师在数据方案反馈接口中直接给出每个数据的sceneid，分辨率、卫星、传感器信息，这些数据信息用于判断运行哪个生产流程）
					// 如果与仲波老师的数据方案反馈接口修改好之后，以下这部分代码可以去掉
					// String strRequestXML =
					// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					// + "<root>"
					// + "<condition>"
					// + "<sceneid>1000</sceneid>"
					// + "<datafrom>at21</datafrom>"
					// + "</condition>"
					// + "</root>";
					// String strDataViewDetail =
					// project1Proxy.dataProductViewDetail( strRequestXML );

					if (("L2CP".equals(orderRequest.orderType))
							|| ("L2VD".equals(orderRequest.orderType))) {
						String strRequestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<root>"
								+ "<condition>"
								+ "<sceneid>1000</sceneid>"
								+ "<datafrom>at21</datafrom>"
								+ "</condition>"
								+ "</root>";
//						String strDataViewDetail = project1Proxy
//								.dataProductViewDetail(strRequestXML);
						
						/*
						 *  定义一个新的订单类型命名规则：目前是
						 * "L2CP"+“子分类（如1，2，3）”，二级订单类型（处理流程）的子分类对应的名称（与数据datatype有关）
						 * 还需要在workflowdb数据库中，为每个工作流增加 “ordertype” 和
						 * * “datatype”两个字段，用于为订单按数据类型和订单类型匹配逻辑工作流
						 * 其中“ordertype”为：如 L2CP，L2AP，L2FP，L2VD等
						 *  根据包含的数据类型以及基本的订单类型确定最终订单类型
						 *  例如，当前订单类型为L2CP，数据类型中包含FY，确定订单类型为L2CP2，即对应工作流包含分幅操作
						 */
						
						//根据当前基本数据订单类型及数据类型，确定详尽的订单类型
						WorkflowDB workflowDB=new WorkflowDB();
						RSDataTypeDB rsDataTypeDB=new RSDataTypeDB();
						
						//根据数据类型确定预处理步骤
						TreeSet<String> preProcesingSteps=new TreeSet<String>();
						String []dataTypeArrays=orderRequest.dataType.split(";");
						for (String data : dataTypeArrays) {
							String[] dataInfos=data.split("@");
							if (dataInfos.length!=2) {
								continue;
							}
							String condition="where satellite='" +
									dataInfos[1] +
									"' and sensor='" +
									 dataInfos[0]+
									"'";		
							ArrayList<Rsdatatype> rsdataTypes=rsDataTypeDB.search(condition);
							
							if (rsdataTypes!=null) {
								//獲取預處理步驟
								Rsdatatype currRsdatatype=rsdataTypes.get(0);
								if (currRsdatatype==null) {
									continue;
								}
								String []steps=currRsdatatype.getPreprocessing().split(";");
								for (int i = 0; i < steps.length; i++) {
									String currStep = steps[i];
									preProcesingSteps.add(currStep);
								}
							}				
						}
						//根据所有数据的预处理步骤确定
						
						String newOrderType = workflowDB.geComprehensiveOrderTypeByDataType(orderRequest.orderType,preProcesingSteps);
						
						
						
						if (!newOrderType.equals("")) {
							orderRequest.orderType = newOrderType;
						}
						
						System.out.println("\t>>>>:"+orderRequest.orderType);
						OrderRequest.updateOrder(orderRequest);
						
					}
					
					//FP 20151215
					if ("L2AP".equals(orderRequest.orderType)||"L2FP".equals(orderRequest.orderType)) {
						//此处是否需要对接融合同化知识库，判断所依赖的数据是否可行
						logger.info("验证融合同化订单是否可行！");
						//首先判断需要融合/同化的数据是否存在retrievalDataList
						logger.info("待融合同化处理的数据已经存在！");
						//检验数据是否完备，需要调用融合同化知识库，检查所需其他数据是否满足需求
						logger.info("查询融合/同化知识库，确认所依赖的数据是否已经存在！");
					}
					
					// 向生产流程库匹配生产流程：反馈流程匹配结果（true/false）
					logger.info("4.向生产流程库匹配生产流程：反馈流程匹配结果（true/false）");

					// 生产可行性判定：输入orderId，反馈true/false
					logger.info("5.生产可行性判定：输入orderId，反馈true/false");
					boolean isFeasiable = orderStudio.isFeasiable(orderRequest);

					if (isFeasiable) {
						orderRequest.nextStatus();
					} else {
						orderRequest.invalidStatus();

						// if( "L2VD".equals( orderRequest.orderType ) ){
						if (orderRequest.orderType.contains("L2VD")) {
							L2ExternalOrder l2Order = new L2ExternalOrder();
							l2Order.jobId = L2ExternalOrder
									.getL2OrderIdByMap(orderRequest.jobId);
							if (null != l2Order.jobId) {
								L2ExternalOrder.delete(l2Order);
								L2ExternalOrder.orderRequestL2OrderRemove(
										orderRequest.jobId, l2Order.jobId);
							}
						}
					}

					// test
					System.out
							.println(">>>>>>>>>>>>>>>>>>>>>>>>>>orderRequest.JobId = "
									+ orderRequest.jobId
									+ " Feasiable = "
									+ isFeasiable);

					// if( "L2VD".equals( orderRequest.orderType ) ){
					if (orderRequest.orderType.contains("L2VD")) {
						orderStudio.setConfirmationStatus(orderRequest.jobId,
								1, isFeasiable);
					} else {

						// 向课题三发送订单可行性确认消息
						logger.info("6.向课题三发送订单可行性确认消息");

						String strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><id>"
								+ orderRequest.jobId
								+ "</id>"
								+ "<feasibility>"
								+ String.valueOf(isFeasiable)
								+ "</feasibility><remark>XX</remark></condition></root>";
//						String strRet;
						
						 try {
							 project3Proxy.commonProductFeasibilitySubmit(strXML);
						 } catch (RemoteException e) {
						 e.printStackTrace();
						 }

						// 更新生产请求的生产确认状态为：0 待确认
						orderStudio.setConfirmationStatus(orderRequest.jobId,
								0, false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
			}

			try {
				sleep(10000); // 暂停，每一秒输出一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
