package MainSystem;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.NEW;

import DBSystem.DBConn;
import DBSystem.L2OrderDB;
import DBSystem.L3OrderDB;
import DBSystem.L4OrderDB;
import FileOperation.TimeConsumeCount;
import LogSystem.SystemLogger;
import OrderManage.L2ExternalOrder;
import OrderManage.L3InternalOrder;
import OrderManage.WaitOrderExecuted;
import OrderSubmit.OrderSubmitThread;
import ServiceInterface.ICPStateFeedbackProxy;

/**
 * 创建时间：2015-8-8 下午5:04:23 项目名称：GNOrderSubmitRunnableJar 2015-8-8
 * 
 * @author 张杰
 * @version 1.0 文件名称：TestMain.java 类说明：按照四级订单提交相应WebService，多线程程序
 */
public class TestMain {

	public static void main(String[] args) {
		
		//统计计时
		TimeConsumeCount timeConsumeCount=new TimeConsumeCount();
		timeConsumeCount.setStartTimeByCurrentTime();
				
		if (args.length!=2) {
			System.out.println("输入参数不正确，请重新数据！");
			System.exit(0);
		}		
		// 等待时间（H）
		final long WAITHOURS = 24;

		// 数据库连接参数
		// 参数1为数据库连接参数
		// String DBConnectionPara = "10.3.10.1_3306_mccps_caoyang_123456";
		String DBConnectionPara = args[0];
		String[] DBConnection = DBConnectionPara.split("_");
		if (!(DBConnection.length == 6||DBConnection.length==5)) {
			System.out.println("<Error>数据库连接参数出错！");
			return;
		}
		DBConn connection = new DBConn(DBConnection[0], DBConnection[1],
				DBConnection[2], DBConnection[3], DBConnection[4]);

		Logger logger = SystemLogger.getInstance().getSysLogger();

		logger.info("_______" + args[1] + "订单开始提交_______");

		ArrayList<OrderSubmitThread> list = new ArrayList<OrderSubmitThread>();

		boolean isAllSubmit = true;
		String l3OrderId = null;

		// 四级订单号
		// 参数二为RN、GN三级订单的四级订单列表或者CP三级订单号：“L3RN201508100001@master&&L3RN201508100001@slave1&&”或者“L3CP201508100001&&”
		// 依次提交订单
		String[] l4Orders = args[1].split("_");
		for (int i = 0; i < l4Orders.length; i++) {
			OrderSubmitThread orderSubmitThread = new OrderSubmitThread(
					l4Orders[i],DBConnectionPara);
			orderSubmitThread.start();
			list.add(orderSubmitThread);
		}

		// 等待子线程join
		for (OrderSubmitThread submitThread : list) {
			try {
				submitThread.join();
				
				l3OrderId = submitThread.getL3OrderId();
				isAllSubmit &= submitThread.getSubmitResult();

			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("等待提交子线程Join出错！\n"+e);
			}
		}
		// 更新三级订单状态：是否提交
		L3OrderDB l3OrderDB = new L3OrderDB();
		L4OrderDB l4OrderDB=new L4OrderDB();
		if (isAllSubmit) {
			l3OrderDB.setOrderWorkingStatus(l3OrderId, "Submited");
			logger.info("Have Submited");
		} else {
			l3OrderDB.setOrderWorkingStatus(l3OrderId, "Submit Error");
//			l4OrderDB.setOrderWorkingStatus(l4OrderId, l4OrderStatus);
			logger.error("Submit Error");
		}

		// 轮询四级订单工作状态，直到处理成功或失败
		ArrayList<WaitOrderExecuted> wait_List = new ArrayList<WaitOrderExecuted>();
		for (int i = 0; i < l4Orders.length; i++) {
			WaitOrderExecuted waitOrderThread = new WaitOrderExecuted(
					l4Orders[i], WAITHOURS);
			waitOrderThread.start();
			wait_List.add(waitOrderThread);
		}

		boolean isAllProcessed = true;

		// 轮询完毕后，更新三级订单状态：是否处理完成
		for (WaitOrderExecuted waitThread : wait_List) {
			try {
				waitThread.join();
				isAllProcessed &= waitThread.getSuccessProcessed();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("等待提交子线程Join出错！\n"+e);
			}
		}
		
			
		
		if (isAllProcessed) {
			logger.info("设置三级订单Finish状态:"+l3OrderDB.setOrderWorkingStatus(l3OrderId,
					"Finish"));
			//调用课题三接口，反馈状态
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ICPStateFeedbackProxy proxy = new ICPStateFeedbackProxy();
			System.out.println(l3OrderDB.getOrder(l3OrderId));
			String jobId_L2 = l3OrderDB.getOrder(l3OrderId).jobId_L2;
			
			
			L2ExternalOrder l2ExternalOrder = new L2ExternalOrder().getOrder(jobId_L2);
			
			String strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><id>"
					+ l2ExternalOrder.jobId_P3L2
					+ "</id>"
					+"<username>"
					+"mca"
					+"</username>"
					+"<content>"
					+args[1].substring(0, 4)
					+"</content>"
					+"<time>"
					+df.format(new Date())
					+"</time>"
					+ "</condition></root>";
//			System.out.println(strXML);
			try {
				proxy.commonProductStateFeedback(strXML);
				logger.info("向课题三反馈状态"+args[1].substring(0, 4));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("Have Processed Successfully!");
		} else {
			logger.info("设置三级订单Error状态:"+l3OrderDB.setOrderWorkingStatus(l3OrderId, "Error"));
			logger.error("Processed Error!");
		}
		
		timeConsumeCount.setEndTimeByCurrentTime();
		logger.info("_______"+args[1]+"耗时"+timeConsumeCount.getTimeSpan()+"ms_______");		
		
		logger.info("_______"+args[1]+"订单提交结束_______");		
	}
}
