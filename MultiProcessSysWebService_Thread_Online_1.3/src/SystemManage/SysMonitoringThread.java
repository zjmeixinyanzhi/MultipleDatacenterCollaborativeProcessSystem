/*
 *程序名称 		: ProcessThread5.java
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

import DBManage.TestDBConnection;

/**
 * @author caoyang
 *
 */
public class SysMonitoringThread extends Thread {
	// 用于系统资源与状态管理的系统资源管理类对象
	private SystemResource systemResource;

	/**
	 * 
	 */
	public SysMonitoringThread() {
		this.systemResource = new SystemResource();
	}

	/**
	 * @param arg0
	 */
	public SysMonitoringThread(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public SysMonitoringThread(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SysMonitoringThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SysMonitoringThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SysMonitoringThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public SysMonitoringThread(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public SysMonitoringThread(ThreadGroup arg0, Runnable arg1, String arg2,
			long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}
	
	public void run(){
		
		for( ;; ){
			//更新系统资源状态
			System.out.println("Thread[5]--------------SysMonitoring:");
			System.out.println("Thread[5].更新系统资源状态" );
			systemResource.update();
			System.out.println("Thread[5]---------------------------");
			try {
				sleep(5000); //暂停，每一秒输出一次
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		TestDBConnection test=new TestDBConnection();
		test.GetConnection();
		
		SysMonitoringThread thread=new SysMonitoringThread();
		thread.start();
		
	}

}
