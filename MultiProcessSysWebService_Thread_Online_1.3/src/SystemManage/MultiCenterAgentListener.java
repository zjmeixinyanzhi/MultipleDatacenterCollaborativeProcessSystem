/*
 *程序名称 		: MultiCenterAgentListener.java
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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import DBManage.AlgorithmDB;
import DBManage.DBConn;
import DBManage.L2OrderDB;
import DBManage.L3OrderDB;
import DBManage.RequestDB;
import DBManage.ScheduleRuleDB;
import DBManage.SystemConfigDB;
import DBManage.SystemResourceDB;
import DBManage.WorkflowDB;

//import cn.ac.rsgs.keylab.ghips.oms.ctrl.OMSDaemon;

/**
 * @author caoyang
 *
 */

public class MultiCenterAgentListener implements ServletContextListener {
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println( "MultiCenterAgentListener::public void contextDestroyed(ServletContextEvent arg0) | 销毁多中心代理监听" );
		// TODO Auto-generated method stub
		AlgorithmDB.closeConnected();
		L2OrderDB.closeConnected();
		L3OrderDB.closeConnected();
		RequestDB.closeConnected();
		ScheduleRuleDB.closeConnected();
		SystemConfigDB.closeConnected();
		SystemResourceDB.closeConnected();
		WorkflowDB.closeConnected();
		DBConn.close();
	}

	//创建并启动多中心代理监听
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println( "MultiCenterAgentListener::public void contextInitialized(ServletContextEvent arg0) | 创建并启动多中心代理监听" );
		// 启动daemon
		// 获取系统配置文件路径
		String configPath = arg0.getServletContext().getRealPath( "/" );
		String configFile = configPath + "/config.property";
		//创建多中心代理对象
		MultiCenterAgent agent = new MultiCenterAgent();
		agent.initialize( configFile );
		//创建多中心代理监听线程
		Thread MultiCenterAgentThread = new Thread( agent );
		//启动监听线程
		MultiCenterAgentThread.start();

	}
}
