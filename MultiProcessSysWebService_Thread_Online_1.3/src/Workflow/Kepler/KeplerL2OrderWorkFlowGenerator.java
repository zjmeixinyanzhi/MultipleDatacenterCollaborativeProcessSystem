package Workflow.Kepler;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.kepler.moml.KeplerActors;
import org.kepler.moml.KeplerLink;
import org.kepler.moml.KeplerRelation;
import org.kepler.moml.MomlExport;

import DBManage.L2OrderDB;
import DBManage.L3OrderDB;
import DBManage.WorkflowDB;
import OrderManage.L2ExternalOrder;
import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import SystemManage.DBConfig;
import SystemManage.SystemConfig;
import SystemManage.SystemLogger;
import Workflow.WorkflowAdapter;
import Workflow.WorkflowSchedular;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.string;

/**
 * 创建时间：2014-11-4 上午8:19:24 项目名称：MCA_KeplerWorkflow_Engine 2014-11-4
 * 
 * @author 张杰
 * @version 1.0 文件名称：KeplerL2OrderWorkFlowGenerator.java
 *          类说明：以二级订单为单位，从数据库中获取产品生产的逻辑工作流，根据逻辑工作流动态生成Kepler实际工作流，
 *          其中工作流的每一步对应一个三级订单。
 */
public class KeplerL2OrderWorkFlowGenerator {

	// 二级订单
	public L2ExternalOrder l2Order;
	// 二级订单对应的逻辑工作流，每一步为一个三级订单
	public String LogicalWorkFlow;
	// 逻辑工作流步骤个数，对应三级订单的个数
	public int StepNum = 0;
	// 工作流对应三级订单集合，一次存储各个三级订单Id
	public String L3OrdersAssemble[] = new String[20];
	// 导出XML的Path
	// public String exportPath;

	// 三级订单前后Port名称的Map
	HashMap<String, String> firstPortNameMap = new HashMap<>();
	HashMap<String, String> lastPortNameMap = new HashMap<>();

	// xml格式的目标工作流
	public Document doc = null;
	// 字符串串格式目标的工作流
	public String KeplerWF = "";
	// 根元素
	public String RootElement = "";
	// 工作流中各模块的集合
	public String Actors = "";
	// 工作流中各关系的集合
	public String Relations = "";
	// 工作流中个连接的集合
	public String Links = "";
	// 错误标记
	public String error_flag = "";
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public KeplerL2OrderWorkFlowGenerator(L2ExternalOrder l2Order) {
		this.l2Order = l2Order;
		System.out.println(">>>>>>>>>>>>>>>>" + this.l2Order.jobId);
	}

	// 获取逻辑工作流信息
	public boolean getLogicalWorkFlowInfo() {
		System.out
				.println("KeplerL2OrderWorkFlowGenerator::public void getLogicalWorkFlowInfo() | \n-->获取二级订单"
						+ l2Order.jobId + "的逻辑工作流");

		// test
		System.out.println("Moml Run:" + this.l2Order.orderType);

		LogicalWorkFlow = this.l2Order.l3orderlist;

		System.out.println("*************  L3OrderList : " + LogicalWorkFlow);

		this.L3OrdersAssemble = LogicalWorkFlow.split(";");
		// 判断是否三级订单列表是否为空
		if (this.L3OrdersAssemble.length == 0) {
			logger.error("三级订单列表为空!");
			return false;
		}

		// 确定处理流程数目
		this.StepNum = this.L3OrdersAssemble.length;

		return true;
	}

	// 依次查询三级订单数据库，获取参数，动态添加Kepler工作流组件
	public boolean AddKeplerMoudles() {
		System.out
				.println("KeplerL2OrderWorkFlowGenerator::public void AddKeplerMoudles() | \n-->依次添加的逻辑工作流中的三级订单工作流模块");
		// 对应多个三级订单类，更方便？还是查询后直接赋值？方便后期处理？类
		// 添加每个三级订单模块
		for (int i = 0; i < StepNum; i++) {

			L3OrderDB l3orderdb = new L3OrderDB();
			System.out.println(">>>>>>>>>>>" + L3OrdersAssemble[i]);
			L3InternalOrder Templ3order = l3orderdb
					.getOrder(L3OrdersAssemble[i]);

			KeplerL3OrderMoudlesGenerator l3mg = new KeplerL3OrderMoudlesGenerator(
					Templ3order, l2Order.jobId);

			System.out.println(">>>>>" + Templ3order.toString());
			System.out.println(">>>>" + Templ3order.jobId);

			if (!l3mg.GetParameters()) {
				// 判断出错类型
				logger.error("添加三级订单"+Templ3order.jobId+"Kepler模块错误！");
				this.error_flag = l3mg.error_flag;
				return false;
			}
			l3mg.AddMoudles();
			Actors += l3mg.Actors;
			Relations += l3mg.Relations;
			Links += l3mg.Links;

			// 获取前后端口名称
			this.firstPortNameMap.put(L3OrdersAssemble[i], l3mg.firstInputPort);
			this.lastPortNameMap.put(L3OrdersAssemble[i], l3mg.lastOutPort);

		}
		// 添加多个三级订单间的连接
		// 指定前后Port名称
		for (int i = 0; i < StepNum - 1; i++) {
			KeplerRelation outerRelation = new KeplerRelation(
					L3OrdersAssemble[i] + "To" + L3OrdersAssemble[i + 1]);
			KeplerLink outerLink = new KeplerLink();
			Relations += outerRelation.setRelation();
			Links += outerLink.setLink(
					lastPortNameMap.get(L3OrdersAssemble[i]),
					firstPortNameMap.get(L3OrdersAssemble[i + 1]),
					outerRelation.getRelationName());
		}
		// 在最后一个三级订单模块后添加一个更新二级订单成功执行的模块、程序退出模块及相应连接
		// 更新数据库链接参数
		getDBConnParas();
		KeplerActors l2TempActors = new KeplerActors(4000, 4000);
		KeplerRelation l2relation = new KeplerRelation(l2Order.jobId);
		KeplerLink l2Link = new KeplerLink();

		Actors += l2TempActors.setStringConstant(l2Order.jobId
				+ "UpdateFinishWorkingStatusSQL",
				"update l2orderdb set workingStatus='Finish' where JobId='"
						+ l2Order.jobId + "'");
		Actors += l2TempActors.setDatabaseBWriter(l2Order.jobId
				+ "UpdateFinishWorkingStatus", getDBConnParas());
		Actors += l2TempActors.setBoolConstant(l2Order.jobId + "StopConfirm",
				true);
		Actors += l2TempActors.setStop(l2Order.jobId + "Stop");

		Relations += l2relation.setRelation();
		// 连接最后一个三级订单的输出名单
		Links += l2Link.setLink(
				lastPortNameMap.get(L3OrdersAssemble[StepNum - 1]),
				l2Order.jobId + "UpdateFinishWorkingStatusSQL.trigger",
				l2relation.getRelationName());
		Relations += l2relation.setRelation();
		Links += l2Link.setLink(l2Order.jobId
				+ "UpdateFinishWorkingStatusSQL.output", l2Order.jobId
				+ "UpdateFinishWorkingStatus.input",
				l2relation.getRelationName());
		Relations += l2relation.setRelation();
		Links += l2Link.setLink(l2Order.jobId
				+ "UpdateFinishWorkingStatus.result", l2Order.jobId
				+ "StopConfirm.trigger", l2relation.getRelationName());
		Relations += l2relation.setRelation();
		Links += l2Link.setLink(l2Order.jobId + "StopConfirm.output",
				l2Order.jobId + "Stop.input", l2relation.getRelationName());

		// 获取根元素
		this.RootElement = l2TempActors.setRootElement(l2Order.jobId
				+ "KWFRuntiome3", l2Order.jobId, l2Order.jobId + "DDFDirector");

		// 组合全部模块
		KeplerWF += RootElement + Actors + Relations + Links + "</entity>";

		return true;
	}

	// 导出XML格式的Kepler工作流
	public boolean ExportWorkFlowXML(String outpath) {
		System.out
				.println("KeplerL2OrderWorkFlowGenerator::public boolean ExportWorkFlowXML(String outpath) | \n导出二级订单的Kepler工作流");
		MomlExport export = new MomlExport(KeplerWF);
		// test
		logger.info(">>>>>" + "导出XML工作流文件！"+outpath);
		export.exportFile(outpath);
		return true;
	}

	// 产生L2Order对应的Kepler工作流
	public boolean GenerateKeplerWorkFlow() {
		System.out
				.println("KeplerL2OrderWorkFlowGenerator::public boolean GenerateKeplerWorkFlow() | \n-->生成二级订单"
						+ l2Order.jobId + "对应的的Kepler工作流");
		// 获取逻辑工作流

		if (!getLogicalWorkFlowInfo()) {
			this.error_flag = "ErrorGetLogicalWorkFlow";
			logger.error(l2Order.jobId + "获取逻辑工作流失败！");
			return false;
		}

		// 添加各个功能模块
		if (!AddKeplerMoudles()) {
			logger.error(l2Order.jobId + "添加各个功能模块失败！");
			this.error_flag = "ErrorAddKeplerMoudles";
			return false;
		}
		// System.out.println(KeplerWF);
		// 添加DTD
		try {
			doc = DocumentHelper.parseText(KeplerWF);
			doc.addDocType("entity", "-//UC Berkeley//DTD MoML 1//EN",
					"\nhttp://ptolemy.eecs.berkeley.edu/xml/dtd/MoML_1.dtd");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error(l2Order.jobId+"Kepler工作流MoML语法检查错误！");
			logger.error(e);
			e.printStackTrace();
		}
		// 导出XML文件,暂时省略
		return true;
	}

	// 从配置文件中获取数据库配置信息
	private String getDBConnParas() {
		// 获取数据库连接信息
		String DBConnParas = null;
		try {
			DBConfig dbConfig = SystemConfig.getConfiguartionDBConfig();
			String DBParas[] = new String[10];
			DBParas[0] = dbConfig.getIP();
			DBParas[1] = dbConfig.getPort();
			DBParas[2] = dbConfig.getSid();
			DBParas[3] = dbConfig.getUser();
			DBParas[4] = dbConfig.getPasswd();
			
			DBConnParas="{driver = &quot;"
					+ "com.mysql.jdbc.Driver&quot;, password = &quot;" + DBParas[4]
					+ "&quot;, url = &quot;jdbc:mysql://" + DBParas[0] + ":"
					+ DBParas[1] + "/" + DBParas[2] + "&quot;, user = &quot;"
					+ DBParas[3] + "&quot;}";

			
		} catch (Exception e) {
			logger.error("获取数据库连接参数失败！");
			logger.error(e);
		}
		
		return DBConnParas;
	}

}
