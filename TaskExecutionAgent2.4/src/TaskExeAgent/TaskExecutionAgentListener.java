package TaskExeAgent;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import DBManage.AlgorithmDB;
import DBManage.DBConn;
import DBManage.DataReplaceRuleDB;
import DBManage.L3OrderDB;
import DBManage.PublicBufferDB;
import DBManage.PBSOrderDB;
import DBManage.SystemConfigDB;
import TaskExeAgent.TaskExecutionAgent;


/**
 * @author caoyang
 *
 */
public class TaskExecutionAgentListener implements ServletContextListener {

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println( "TaskExecutionAgentListener::public void contextDestroyed(ServletContextEvent arg0) | 销毁子订单执行代理监听" );
		
		AlgorithmDB.closeConnected();
		DataReplaceRuleDB.closeConnected();
		L3OrderDB.closeConnected();
		PublicBufferDB.closeConnected();
		PBSOrderDB.closeConnected();
		SystemConfigDB.closeConnected();
		
		DBConn.close();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println( "TaskExecutionAgentListener::public void contextInitialized(ServletContextEvent arg0) | 创建并启动子订单执行代理监听" );
		
		// 启动daemon
		// 获取系统配置文件路径
		String configPath = arg0.getServletContext().getRealPath( "/" );
		//String configFile = configPath + "/config.property";
//		String configFile = configPath + "config.property";
		
		//test
		System.out.println( configPath );
		
		//创建子订单执行代理对象
		TaskExecutionAgent agent = new TaskExecutionAgent();
		agent.initialize( configPath );
		//创建子订单执行代理监听线程
		Thread TaskExecutionAgentThread = new Thread( agent );
		//启动监听线程
		TaskExecutionAgentThread.start();
	}

}
