/*
 *程序名称 		: MultiCenterAgent.java
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import OrderManage.Order;
import OrderManage.OrderRequest;
import OrderManage.OrderStudio;
import TaskSchedular.TaskSchedular;
import Workflow.WorkflowAdapter;
import Workflow.WorkflowSchedular;

/**
 * @author caoyang
 *
 */
public class MultiCenterAgent extends Thread {
	private Logger logger=null;
	
	// 系统初始化
	public void initialize( String sysConfigFile ){
		System.out.println( "MultiCenterAgent::public void initialize(String sysConfigFile) | 系统初始化" );
		System.out.println( "1.初始化：加载系统配置" );
		
		//载入系统基本配置信息
		SystemConfig.setConfigFile( sysConfigFile );
		SystemConfig.loadSystemConfig();
		//载入其他全部配置信息
		boolean flag = SystemConfig.loadBasicConfig();
		//系统配置信息的检查及异常处理
		this.logger=SystemLogger.getSysLogger();
		
		if( flag ){
			//加载成功
			logger.info("load configures successfully( 加 载 配 置 参 数 成 功 )");
		}else{
			//如果加载出错，则记录日志，程序退出
			logger.info("load configures unsuccessfully( 加 载 配 置 参 数 失 败)");
		   logger.error("Server starting failed.(服务器启动失败)");
			System.exit(-2);
		}
	}

	//系统的多中心运行调度代理Daemon
	
	public void run(){
		logger.info( "MultiCenterAgent::public void run() | 系统的多中心运行调度代理Daemon" );
		
		DataParsingThread dataParsingThread = new DataParsingThread();
		Thread dataParser = new Thread( dataParsingThread );
		dataParser.start();
		
		OrderConfirmRequestThread orderConfirmThread = new OrderConfirmRequestThread();
		Thread orderConfirmer = new Thread( orderConfirmThread );
		orderConfirmer.start();
		
		OrderRunningThread orderRunningThread = new OrderRunningThread();
		Thread orderRunner = new Thread( orderRunningThread );
		orderRunner.start();
		
//		PublicBufferManagerThread publicBufferManagerThread = new PublicBufferManagerThread();
//		Thread publicBufferManager = new Thread( publicBufferManagerThread );
//		publicBufferManager.start();
		//
		SysMonitoringThread sysMonitoringThread = new SysMonitoringThread();
		Thread sysMonitor = new Thread( sysMonitoringThread );
		sysMonitor.start();
//		
	}
	
}
