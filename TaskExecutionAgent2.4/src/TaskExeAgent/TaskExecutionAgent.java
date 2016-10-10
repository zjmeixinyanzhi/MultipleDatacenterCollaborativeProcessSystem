/*
 *程序名称 		: TaskExecutionAgent.java
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
package TaskExeAgent;

import java.rmi.RemoteException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import ServiceInterface.ServiceImplProxy;
import TaskExeAgent.SystemConfig;
import TaskExeAgent.SystemLogger;
import TaskSchedular.TaskExecuter;

/**
 * @author caoyang ZhangJie
 * 
 */
public class TaskExecutionAgent extends Thread {
	private OrderStudio orderStudio;
	private ArrayList<L3InternalOrder> l3OrderList;
	private ArrayList<L3InternalOrder> finished_L3OrderList;

	private Logger logger = null;

	public TaskExecutionAgent() {

	}

	// 系统初始化
	public void initialize(String configPath) {
		System.out
				.println("TaskExecutionAgent::public void initialize( String sysConfigFile ) | 系统初始化");

		System.out.println("1.初始化：加载系统配置");
		// 载入系统基本配置信息
		String configFile = configPath + "config.property";
		SystemConfig.setConfigFile(configFile);
		// !!!SystemConfig.loadSysConfig();
		SystemConfig systemConfig = new SystemConfig();

		if (!systemConfig.loadSystemConfig()) {
			System.out.println("加载系统环境变量失败！");
		}
		// 载入其他全部配置信息
		boolean flag = systemConfig.loadBasicConfig();
		this.logger = SystemLogger.getSysLogger();
		// 系统配置信息的检查及异常处理
		if (flag) {
			// 加载成功
			logger.info("load configures successfully( 加 载 配 置 参 数 成 功 )");
		} else {
			// 如果加载出错，则记录日志，程序退出
			logger.info("load configures unsuccessfully( 加 载 配 置 参 数 失 败)");
			logger.error("Server starting failed.(服务器启动失败)");
			System.exit(-2);
		}

		this.orderStudio = new OrderStudio();
		this.l3OrderList = new ArrayList<L3InternalOrder>();
		this.finished_L3OrderList = new ArrayList<L3InternalOrder>();
	}

	// 子订单执行代理Daemon
	public void run() {
		logger.info("TaskExecutionAgent::public void run() | 子订单执行代理Daemon");

		// ThreadPublicBufferManager threadPublicBufferManager = new
		// ThreadPublicBufferManager();
		// Thread thread = new Thread( threadPublicBufferManager );
		// thread.start();

		//pbs订单提交
		PBSOrderSubmitThread pbsOrderSubmitThread=new PBSOrderSubmitThread();
		pbsOrderSubmitThread.start();			
		
		//pbs订单更新
		PBSOrderUpdataThread pbsOrderUpdataThread=new PBSOrderUpdataThread();
		pbsOrderUpdataThread.start();
		
		//l3order订单更新
		L3OrderStatusFeedbackThread l3OrderStatusFeedbackThread=new L3OrderStatusFeedbackThread();
		l3OrderStatusFeedbackThread.start();
			

	}

}
