package org.kepler;

import org.apache.log4j.Logger;
import org.kepler.ExecutionEngine;

import DBManage.L2OrderDB;
import DBManage.SystemConfigDB;
import OrderManage.L2ExternalOrder;
import SystemManage.SystemConfig;
import SystemManage.SystemLogger;
import Workflow.Kepler.KeplerL2OrderWorkFlowGenerator;
import ptolemy.actor.CompositeActor;

/**
 * 创建时间：2014-12-23 下午9:29:12
 * 项目名称：MultiProcessSys
 * 2014-12-28
 * @author 张杰
 * @version 1.0
 * 文件名称：GenerateAndParseMoml.java
 * 类说明：Kepler工作流的生成与解析
 */
public class GenerateAndParseMoml extends Thread {
	private ExecutionEngine engine;
	public CompositeActor moml = null;

	public String l2OrderId = "";
	public L2ExternalOrder l2Order;
	private boolean isExecuted = false;
	private int count = 0;// 执行次数
	
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public GenerateAndParseMoml(String l2OrderId) {
		this.l2OrderId = l2OrderId;
		try {
			engine = ExecutionEngine.getInstance();
		} catch (Exception e) {
			logger.error("Could not get instance of Kepler ExecutionEngine: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public CompositeActor getMoml() {
		return this.moml;
	}

	public synchronized void run() {
		System.out.println("-->二级订单" + l2OrderId + "Kepler工作流的生成与解析\n");
		System.out.println("GenerateAndParseMoml::public synchronized void run()");
		// 1、 生成工作流Moml
		System.out.println("-->二级订单" + l2OrderId + "Kepler工作流的生成");		
		
		//需要重新获得二级订单，更新三级订单列表等信息
		L2OrderDB l2OrderDB=new L2OrderDB();
		this.l2Order=l2OrderDB.getOrder(this.l2OrderId);
		//test
		//test
		System.out.println("Moml Run:"+this.l2Order.orderType);
		
		
		KeplerL2OrderWorkFlowGenerator kL2WFG = new KeplerL2OrderWorkFlowGenerator(
				this.l2Order);

		kL2WFG.GenerateKeplerWorkFlow();
		// 如果生成出错，根据实际情况判断是否进行重复生产
		// 生产三级订单工作流时，资源匹配出错需要等待重复生成工作流
		while (kL2WFG.error_flag.equals("ErrorAddKeplerMoudles")) {
			try {
				// 暂停时间
				int n = 50;
				for (int i = 0; i < n; i++) {
					logger.info("-->距离下次Kepler工作流生成时间还有：" + (n - i) + "s");
					sleep(n*1000);
				}
				logger.info("-->重新生成" + l2OrderId + "订单的工作流！");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			kL2WFG.GenerateKeplerWorkFlow();
		}
		if (kL2WFG.error_flag.equals("ErrorGetLogicalWorkFlow")
				| kL2WFG.error_flag.equals("ErrorExportWorkFlow")) {
			logger.error("生成二级订单"+this.l2OrderId+"工作流出错！");
			return;
		}
		SystemConfig systemConfig=new SystemConfig();
		
//		kL2WFG.ExportWorkFlowXML(SystemConfig.getSysPath()+"build-area/"+l2OrderId+".xml");		
		kL2WFG.ExportWorkFlowXML(systemConfig.getKeplerWorkflowFilePath()+"/"+l2OrderId+".xml");
		logger.info("-->二级订单" + l2OrderId + "Kepler工作流的解析");

		// 2 解析工作流Moml
		System.out.println("-->Begin Parse:" + l2OrderId);
		try {
			this.moml = (CompositeActor) ExecutionEngine
					.parseMoML(kL2WFG.KeplerWF);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("-->Error Parse:"+l2OrderId);
		}
		logger.info("-->End Parse:" + l2OrderId);
	}
}
