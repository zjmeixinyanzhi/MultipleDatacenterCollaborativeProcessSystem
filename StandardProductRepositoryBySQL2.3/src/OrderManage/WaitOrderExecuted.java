package OrderManage;

import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;
import DBSystem.DBConn;
import DBSystem.L3OrderDB;
import DBSystem.L4OrderDB;
import LogSystem.SystemLogger;

/**
 * 创建时间：2014-11-19 上午10:42:11 项目名称：WaitL3OrderExecuted 2014-11-19
 * 
 * @author 张杰
 * @version 1.0 文件名称：GetJobStatus.java 类说明：
 */
public class WaitOrderExecuted extends Thread {
	// 订单Id
	public String jobId = null;

	public String SuccessFlag = "Finish";
	public String FailureFlag = "Error";
	
	//是否完成处理
	private boolean isSuccessProcessed=false;

	// 最大等待时长（小时记）
	public long MaxWaitHours = 24;

	private L3OrderDB l3OrderDB = null;
	private L4OrderDB l4OrderDB = null;
	
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	public WaitOrderExecuted(String jobId, long maxWaitHours) {
		this.jobId = jobId;
		this.MaxWaitHours = maxWaitHours;
	}

	public void run() {

		this.l3OrderDB = new L3OrderDB();
		this.l4OrderDB = new L4OrderDB();

		if (this.jobId.contains("L3CP")) {
			// 轮询三级订单库
			if (!doWait(0)) {
				return;
			}
		} else if (this.jobId.contains("L3RN") || this.jobId.contains("L3GN")) {
			// 轮询四级订单库，直至结束
			if (!doWait(1)) {
				return;
			}
			// 更新三级订单的数据列表，追加的形式
			String condition = "where JobId=" + "'" + jobId + "'";

			ArrayList<String> prodcuctList = this.l4OrderDB.search(condition)
					.get(0).strDataProductList;
			String l3OrderId = this.jobId.substring(0, this.jobId.indexOf("@"));

			// test
			// System.out.println(">>>"+l3OrderId);

			l3OrderDB.setDataProductListByL4OrderProductList(l3OrderId,
					prodcuctList);
		} else {
			logger.error("<Error>未找到订单类型！");
			return;

		}
	}

	private boolean doWait(int flag) {
		String condition = "where JobId=" + "'" + jobId + "'";
		String result = ""; // 查询记录并显示

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		// 获取开始时间
		String start = df.format(new Date());
		String end = "";
		long hours = 0;
		try {
			Date starttime = df.parse(start);
			// starttime = df.parse("2014-10-15 19:03:00");
			// System.out.println(SuccessFlag);
			// 获取三级订单执行状态，执行完成或查询时间超过一天退出

			while (!(result.equals(SuccessFlag) || result.contains(FailureFlag))
					&& hours != MaxWaitHours) {
				try {
					// SimpleDateFormat df = new
					// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
					// 获取系统当前时间
					end = df.format(new Date());
					Date endtime = df.parse(end);
					// 起止时长
					long diff = endtime.getTime() - starttime.getTime();
					hours = diff / (1000 * 60 * 60);
					logger.info("订单" + jobId + "状态：正在执行,等待完成！\n");
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.error("等待线程出错！\n"+e);
				}

				if (flag == 0) {
					result = this.l3OrderDB.search(condition).get(0).workingStatus;
				} else {
					result = this.l4OrderDB.search(condition).get(0).workingStatus;
					logger.info(">>" + result);
				}
				this.isSuccessProcessed=result.equals(this.SuccessFlag);

			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

		return true;
	}
	
	
	public boolean getSuccessProcessed() {
		return this.isSuccessProcessed;
	}
	
	

	public static void main(String[] args) {

		String DBConnectionPara = args[0];
		String[] DBConnection = DBConnectionPara.split("_");
		if (DBConnection.length != 5) {
			System.out.println("<Error>数据库连接参数出错！");
			return;
		}
		DBConn connection = new DBConn(DBConnection[0], DBConnection[1],
				DBConnection[2], DBConnection[3], DBConnection[4]);

		WaitOrderExecuted waitOrderExecuted = new WaitOrderExecuted(
				"L3GN201508100001@master", 24);
		waitOrderExecuted.start();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

}
