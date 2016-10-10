package OrderSubmit;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.tempuri.Service1SoapProxy;

import DBSystem.L3OrderDB;
import DBSystem.L4OrderDB;
import LogSystem.SystemLogger;
import OrderManage.L3InternalOrder;
import OrderManage.L4InternalOrder;
import ServiceInterface.ProcessImplProxy;
import ServiceInterface.TaskExecutionAgentServiceImplProxy;

/**
 * 创建时间：2015-8-10 下午2:24:27 项目名称：OrderSubmitRunnableJar 2015-8-10
 * 
 * @author 张杰
 * @version 1.0 文件名称：OrderSubmitThread.java 类说明：提交订单的Webservice调用线程
 */
public class OrderSubmitThread extends Thread {
	// http://10.3.10.27:13080/TaskExecutionAgent/services/TaskExecutionAgentServiceImpl
	private TaskExecutionAgentServiceImplProxy taskExecutorAgentProxy = null;
	private Service1SoapProxy Service1SoapProxy=null;
//	private ProcessImplProxy processImplProxy = null;

	public String orderId = null;
	private boolean submitResult = false;
	public String l3OrderId = null;
	private String dbconn=null;
	

	// log4j日志系统
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	public OrderSubmitThread(String orderId,String dbconn) {
		this.orderId = orderId;
		this.dbconn=dbconn;
	}

	public void run() {
		try {
			if (this.orderId.contains("L3CP")) {
				L3OrderDB l3OrderDB = new L3OrderDB();
			

				L3InternalOrder l3Order = l3OrderDB.getOrder(this.orderId);
				l3OrderDB.setOrderWorkingStatus(l3OrderId, "Running");

				CommonProductOrderSubmitParasProcess process = new CommonProductOrderSubmitParasProcess(
						l3Order);
				if (!process.doProcess()) {
					return;
				}
				//
				
				FileOperation.FileOperation operation=new FileOperation.FileOperation();
				operation.writeNewFile("/dataIO/863_Project/863-Daemon/MCAPublicBuffer/CommonProductBuffer/"
						+ this.orderId
						+ ".xml", process.getStrXML());
				
				// test
//				System.out.println(process.getSubmitURL());
//				System.out.println(process.getStrXML());

				this.Service1SoapProxy=new Service1SoapProxy(process.getSubmitURL());
				this.submitResult=this.Service1SoapProxy.commOrderSubmit(process.getStrXML()).contains(process.getSuccessResult());

			} else if (this.orderId.contains("L3RN")
					|| this.orderId.contains("L3GN")||this.orderId.contains("L3DS")) {
				// String[] l4Orders=this.orderId.split(";");
				
				L4OrderDB l4OrderDB = new L4OrderDB();
				ArrayList<L4InternalOrder> l4InternalOrders = l4OrderDB
						.search("WHERE JobId='" + this.orderId + "'");
				Iterator<L4InternalOrder> it_l4InternalOrders = l4InternalOrders
						.iterator();
				while (it_l4InternalOrders.hasNext()) {
					L4InternalOrder l4InternalOrder = (L4InternalOrder) it_l4InternalOrders
							.next();
					// 提取三级订单Id
					this.l3OrderId = l4InternalOrder.jobId_L3;

					NormalizeOrderSubmitParasProcess process = new NormalizeOrderSubmitParasProcess(
							l4InternalOrder);

					// 获取资源提交WebService及数据信息
					if (!process.doProcess()) {
						return;
					}

					this.taskExecutorAgentProxy = new TaskExecutionAgentServiceImplProxy(
							process.getSubmitURL());
					System.out.println(process.getSubmitURL());
					System.out.println(process.getStrXML());
					
					String orderSubmitResult=this.taskExecutorAgentProxy.normalizationOrderSubmit(process.getStrXML());
					
					this.submitResult = (orderSubmitResult.contains(process.getSuccessResult()))
							|| (orderSubmitResult.contains(process
									.getErrorResult()));
					System.out.println();
//
					// //更新四级订单库状态
					if (this.submitResult) {
						l4OrderDB.setOrderWorkingStatus(this.orderId,
								"Submited");
					} else {
						l4OrderDB.setOrderWorkingStatus(this.orderId,
								"Submit Error");
					}
				}
			}	
			else if (this.orderId.contains("L3FP")||this.orderId.contains("L3AP")) {	
				
				//融合同化订单
				L3OrderDB l3OrderDB = new L3OrderDB();
				this.l3OrderId=this.orderId;
				

				L3InternalOrder l3Order = l3OrderDB.getOrder(this.orderId);
				l3OrderDB.setOrderWorkingStatus(l3OrderId, "Running");
				
				FusionAssimilationOrderSubmitParasProcess process=new FusionAssimilationOrderSubmitParasProcess(l3Order,this.dbconn);
				if (!process.doProcess()) {
					return;
				}
				
				FileOperation.FileOperation operation=new FileOperation.FileOperation();
				operation.writeNewFile("/dataIO/863_Project/863-Daemon/MCAPublicBuffer/CommonProductBuffer/"
						+ this.orderId
						+ ".xml", process.getStrXML());
				
				this.taskExecutorAgentProxy = new TaskExecutionAgentServiceImplProxy(
						process.getSubmitURL());
				System.out.println(process.getSubmitURL());
				System.out.println(process.getStrXML());				
				
				String orderSubmitResult=this.taskExecutorAgentProxy.fusionAssimilationOrderSubmit(process.getStrXML());
				
				this.submitResult = (orderSubmitResult.contains(process.getSuccessResult()))
						|| (orderSubmitResult.contains(process
								.getErrorResult()));
				System.out.println();
				
				// //更新四级订单库状态
				if (this.submitResult) {
					l3OrderDB.setOrderWorkingStatus(this.orderId,
							"Submited");
				} else {
					l3OrderDB.setOrderWorkingStatus(this.orderId,
							"Submit Error");
				}
			}
			else {
				logger.error("<Error>待提交的订单类型出错！");
				return;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			logger.error("执行Webservice订单提交失败，请检查Webservice服务是否合法！");
			e.printStackTrace();
		}
	}

	public boolean getSubmitResult() {
		return this.submitResult;
	}

	public String getL3OrderId() {
		return this.l3OrderId;
	}

}
