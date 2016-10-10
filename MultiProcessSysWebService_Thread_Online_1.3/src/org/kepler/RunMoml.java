package org.kepler;

import org.apache.log4j.Logger;

import OrderManage.OrderStudio;
import SystemManage.SystemLogger;
import ptolemy.actor.CompositeActor;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.KernelException;

/**
 * 创建时间：2014-12-23 下午9:45:22 项目名称：MultiProcessSys 2014-12-28
 * 
 * @author 张杰
 * @version 1.0 文件名称：RunMoml.java 类说明：Kepler工作流的执行
 */
public class RunMoml extends Thread {
	private ExecutionEngine engine;
	public CompositeActor moml;
	public String momlName = null;
	private boolean isExecuted = false;
	private int count = 0;// 执行次数
	public int maxExecuteTimes=5;
	
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// 线程计数器
	static private int threadCounts;
	// 线程名称池
	static private String threadNames[];
	static {
		// 假设这里允许系统同时运行最大线程数为10个
		int maxThreadCounts = 20;
		threadNames = new String[maxThreadCounts];
		// 初始化线程名称池
		for (int i = 1; i <= maxThreadCounts; i++) {
			threadNames[i - 1] = "子线程_" + i;
		}
	}

	public RunMoml(String workflowName, CompositeActor moml) {
		this.momlName = workflowName;
		this.moml = moml;
		try {
			engine = ExecutionEngine.getInstance();
		} catch (Exception e) {
			logger.error("could not get instance of ExecutionEngine: "
					+ e.getMessage());
			e.printStackTrace();
		}
		// 线程总数加1
		threadCounts++;
		// 从线程名称池中取出一个未使用的线程名
		for (int i = 0; i < threadNames.length; i++) {
			if (threadNames[i] != null) {
				String temp = threadNames[i];
				// 名被占用后清空
				threadNames[i] = null;
				// 初始化线程名称
				this.setName(temp);
				break;
			}
		}
	}

	// 获取当前运行线程数
	static public int getThreadCounts() {
		synchronized (RunMoml.class) {
			return threadCounts;
		}
	}

	public void run() {
		System.out.println("-->二级订单" + momlName + "Kepler工作流的执行\n");
		System.out.println("RunMoml::public void run()");
		OrderStudio orderStudio = new OrderStudio();
		// 更新二级订单状态：Runing
		orderStudio.setL2OrderWorkingStatus(momlName, "Running");
		while (!isExecuted && count < this.maxExecuteTimes) {
			System.out.print("-->Begin Run Model:" + momlName);
			try {
				engine.runModel(this.moml);
				isExecuted = true;
				// 线程运行完毕后减1
				threadCounts--;
				// 释放线程名称
				String[] threadName = this.getName().split("_");
				// 线程名使用完后放入名称池
				threadNames[Integer.parseInt(threadName[1]) - 1] = this
						.getName();
				logger.info("-->" + momlName + "Kepler工作流执行成功!");
			
			} catch (IllegalActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isExecuted = false;
			} catch (KernelException e) {				
				e.printStackTrace();
				isExecuted = false;
			} finally {
				synchronized (RunMoml.class) {
					count++;
					RunMoml.class.notifyAll();
					
					if (!isExecuted) {
						logger.info("-->Error running model or finding output file:\n");
						
						// 更新二级订单状态：Runing Error
						orderStudio.setL2OrderWorkingStatus(momlName, "RunningError");
						logger.info("-->" + momlName + "工作流执行失败，需要重新执行！已经执行"
								+ count + "次！");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}						
					}
					
				}
				logger.info("----" + this.getName()
							+ " 所占用资源释放完毕，当前系统正在运行的子线程数：" + threadCounts);
				logger.info("-->End Run Model:" + momlName + "\n");
			}
		}
		logger.info("--------------------------------------------结束-----------------------------------------------");
	}
}
