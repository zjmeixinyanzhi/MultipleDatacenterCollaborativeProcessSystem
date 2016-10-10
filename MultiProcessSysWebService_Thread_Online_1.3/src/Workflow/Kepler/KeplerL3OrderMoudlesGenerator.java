package Workflow.Kepler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kepler.ExecutionEngine;
import org.kepler.moml.KeplerActors;
import org.kepler.moml.KeplerLink;
import org.kepler.moml.KeplerRelation;

import DBManage.AlgorithmDB;
import DBManage.L3OrderDB;
import DBManage.L4OrderDB;
import OrderManage.L3InternalOrder;
import SystemManage.DBConfig;
import SystemManage.SystemConfig;
import SystemManage.SystemLogger;
import TaskSchedular.Algorithm;
import TaskSchedular.AlgorithmMatch;

/**
 * 创建时间：2014-11-4 上午8:18:34 项目名称：MCA_KeplerWorkflow_Engine 2014-11-4
 * 
 * @author 张杰
 * @version 1.0 文件名称：KeplerL3OrderMoudlesGenerator.java
 *          类说明：以三级订单为单位，从数据库中获取产品生产的参数，动态为三级订单中功能模块传递参数，
 *          其中一个三级订单对应二级订单工作流的一个生产步骤。
 */
public class KeplerL3OrderMoudlesGenerator {

	// 三级订单
	public L3InternalOrder l3order;
	// 所属二级订单Id
	public String L2OrderId;
	// 数据库配置信息
	private String DBParas[] = new String[10];
	// 三级订单衔接Port,第一个和最有一个Port,方便与下一个三级订单进行连接
	public String firstInputPort = "";
	public String lastOutPort = "";

	// WebService字符串参数
	public String XML = "";
	// WebService的WSDL
	public String WSDL = "";
	// WebService的调用函数
	public String WSFuction = "";
	// 查询三级订单执行状态的Java命令
	public String JavaCommand = "";
	// 正确提交三级订单的返回结果
	public String L3OrderSubmitionCorrectResult = "";
	// 错误标记
	public String error_flag = "";

	// 三级订单中各模块的集合
	public String Actors = "";
	// 三级订单中各关系的集合
	public String Relations = "";
	// 三级订单中各个连接的集合
	public String Links = "";
	// 数据库连接参数
	private String DBConnParas = null;

	// 订单提交业务逻辑数据库参数：只有MCCPS的数据库实例
	private String BPLDBConn = "";
	// 订单库和产品库相关的数据库参数：包含MCCPS和RSProductDB
	private String FULLDBConn="";
	// private String DestDir =
	// "10.3.10.27:5000/home/DCA/dataCenterCacheDir_01";// 需要从配置文件中加载
	private String dataPrepareShell = "";

	// L3DS分幅处理参数
	private String dataSwathShell = "";

	// L3SPU标准产品上传参数
	private String standardProductUploadShell = "";

	// L3CPU共性产品上传参数
	private String commonProductUploadShell = "";
	// 通用的更新订单状态模块
	private String commonUpdateL3OrderStatusShell = "";

	// 订单提交外部Jar脚本
	private String orderSubmitShell = "";

	// 外部可执行模块的目标文件夹
	private String OuterRunnableJarBaseDir = "";

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public KeplerL3OrderMoudlesGenerator(L3InternalOrder l3order,
			String l2orderid) {
		// TODO Auto-generated constructor stub
		this.l3order = l3order;
		this.L2OrderId = l2orderid;
		this.L3OrderSubmitionCorrectResult = "input==&quot;&lt;?xml version=\\&quot;1.0\\&quot; encoding=\\&quot;UTF-8\\&quot;?&gt;&lt;NormalizationOrderSubmit&gt;&lt;feedback&gt;&quot;";
	}

	// 查询三级订单数据库，获取三级订单的各个参数
	public boolean GetParameters() {

		String pathDecollator = "/";
		String OS = System.getProperty("os.name").toLowerCase();
		// if( OS.indexOf( "linux" ) >= 0 ){
		// ;
		// }
		if (OS.indexOf("windows") >= 0) {
			pathDecollator = "\\";
		}
		// Jar外部执行程序WaitL3OrderExecuted.jar放到程序根目录下的Jars文件夹中
		// JavaCommand = "java -jar " + SystemConfig.getSysPath()
		// + "build-area/QueryL3OrderExecutionResult.jar";
		// JavaCommand = "java -jar " + SystemConfig.getSysPath()
		// + "build-area" + pathDecollator +
		// "QueryL3OrderExecutionResult.jar";
		// Linux eclipse
		JavaCommand = "java -jar " + SystemConfig.getSysPath() + "build-area"
				+ pathDecollator + "QueryL3OrderExecutionResult.jar";

		this.OuterRunnableJarBaseDir = SystemConfig.getSysPath() + "build-area"
				+ pathDecollator;
		JavaCommand = JavaCommand.replace("\\", "/");
		// 获取数据库配置信息
		try {
			getDBConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			e.printStackTrace();
		}

		// 获取提交订单的XML参数

		// test
		System.out.println(">>>>" + this.l3order.jobId);

		switch (this.l3order.orderType) {
		case "L3DP": {
			getDPParas(this.l3order);
			break;
		}
		case "L3RN": {
			getRNXMLParas(this.l3order);
			break;
		}
		case "L3GN": {
			getGNXMLParas(this.l3order);
			break;
		}
		case "L3DS": {
			getDSXMLParas(this.l3order);
			break;
		}
		case "L3SPU": {
			getSPUXMLParas(this.l3order);
			break;
		}
		case "L3CPU": {
			getCPUXMLParas(this.l3order);
			break;
		}
		case "L3CP": {
			getCPXMLParas(this.l3order);
			break;
		}
		case "L3FP": {
			getFPXMLParas(this.l3order);
			break;
		}
		case "L3AP": {
			getAPXMLParas(this.l3order);
			break;
		}
		default: {
			logger.error("-->错误：未找到对应的订单类型！");
			break;
		}
		}
		return true;
	}

	// 从配置文件中获取数据库配置信息
	private void getDBConfig() throws IOException {
		DBConfig dbConfig = SystemConfig.getConfiguartionDBConfig();
		try {
			// kepler数据库连接参数
			DBParas[0] = dbConfig.getIP();
			DBParas[1] = dbConfig.getPort();
			DBParas[2] = dbConfig.getSid();
			DBParas[3] = dbConfig.getUser();
			DBParas[4] = dbConfig.getPasswd();
			DBParas[5] = dbConfig.getProductRepositorySID();
			this.DBConnParas = "{driver = &quot;"
					+ "com.mysql.jdbc.Driver&quot;, password = &quot;"
					+ DBParas[4] + "&quot;, url = &quot;jdbc:mysql://"
					+ DBParas[0] + ":" + DBParas[1] + "/" + DBParas[2]
					+ "&quot;, user = &quot;" + DBParas[3] + "&quot;}";
			// 数据准备阶段数据库连接参数 格式：10.3.10.1_3306_mccps_caoyang_123456
			this.BPLDBConn = DBParas[0] + "_" + DBParas[1] + "_" + DBParas[2]
					+ "_" + DBParas[3] + "_" + DBParas[4] + "";
			// 包含数据库连接参数 格式：10.3.10.1_3306_mccps_caoyang_123456_RSProductDB
			this.FULLDBConn=DBParas[0] + "_" + DBParas[1] + "_" + DBParas[2]
					+ "_" + DBParas[3] + "_" + DBParas[4] + "_"+DBParas[5];

		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO: handle exception
			logger.error("读取数据库配置信息出错，下标越界！");
			throw e;
		}

	}

	// 获取提交订单的XML参数

	private void getDPParas(L3InternalOrder templ3order) {
		if (this.l3order.l4orderlist == null) {
			logger.error(this.l3order.jobId + "'s l4orderlist is null");
		}

		// Linux
		this.dataPrepareShell = "java -jar " + this.OuterRunnableJarBaseDir
				+ "RSDataPrepareRunnableJar-2.1.jar " + this.BPLDBConn + " "
				+ this.l3order.l4orderlist;
		// test
		// System.out.println(">>>>>>>>>>>>>" + dataPrepareShell);
	}

	// 获取RN类型的三级订单参数
	public void getRNXMLParas(L3InternalOrder l3Innerorder) {
		// 参数一为数据库连接参数，参数二为四级订单号

		// 替换分割符：Linux下；代表分割命令
		this.l3order.l4orderlist = this.l3order.l4orderlist
				.replaceAll(";", "_");

		System.out.println("\t" + this.l3order.l4orderlist);

		this.orderSubmitShell = "java -jar " + this.OuterRunnableJarBaseDir
				+ "OrderSubmitRunnableJar-2.2.jar " + this.BPLDBConn + " "
				+ this.l3order.l4orderlist;
	}

	// 获取GN类型的三级订单参数
	public void getGNXMLParas(L3InternalOrder l3Innerorder) {
		// 替换分割符：Linux下；代表分割命令
		this.l3order.l4orderlist = this.l3order.l4orderlist
				.replaceAll(";", "_");

		// 参数一为数据库连接参数，参数二为四级订单号
		this.orderSubmitShell = "java -jar " + this.OuterRunnableJarBaseDir
				+ "OrderSubmitRunnableJar-2.2.jar " + this.BPLDBConn + " "
				+ this.l3order.l4orderlist;

	}

	// 获取CP类型的三级订单参数
	public void getCPXMLParas(L3InternalOrder l3Innerorder) {

		// 参数一为数据库连接参数，参数二为CP订单号
		this.orderSubmitShell = "java -jar " + this.OuterRunnableJarBaseDir
				+ "OrderSubmitRunnableJar-2.2.jar " + this.BPLDBConn + " "
				+ this.l3order.jobId + "_";
	}

	// 获取AP类型的字符串参数
	private void getAPXMLParas(L3InternalOrder l3Innerorder) {
		// 参数一为数据库连接参数，参数二为CP订单号
		this.orderSubmitShell = "java -jar " + this.OuterRunnableJarBaseDir
				+ "OrderSubmitRunnableJar-2.2.jar " + this.FULLDBConn + " "
				+ this.l3order.jobId + "_";
	}

	// 获取FP类型的字符串参数
	private void getFPXMLParas(L3InternalOrder l3Innerorder) {
		// 参数一为数据库连接参数，参数二为CP订单号
		this.orderSubmitShell = "java -jar " + this.OuterRunnableJarBaseDir
				+ "OrderSubmitRunnableJar-2.2.jar " + this.FULLDBConn + " "
				+ this.l3order.jobId + "_";

	}

	private void getCPUXMLParas(L3InternalOrder templ3order) {
		// 临时通用更改三级订单状态的模型
		this.commonProductUploadShell = "java -jar "
				+ this.OuterRunnableJarBaseDir
				+ "CommonUpdateL3OrderStatusRunnableJar-2.1.jar "
				+ this.FULLDBConn + " " + this.l3order.jobId;

	}

	private void getSPUXMLParas(L3InternalOrder templ3order) {
		// 临时通用更改三级订单状态的模型
		//
		this.standardProductUploadShell = "java -jar "
				+ this.OuterRunnableJarBaseDir
				+ "StandardProductRepositoryRunnableJar.jar "
				+ this.FULLDBConn + " " + this.l3order.jobId;

	}

	private void getDSXMLParas(L3InternalOrder templ3order) {

		// 替换分割符：Linux下；代表分割命令
		this.l3order.l4orderlist = this.l3order.l4orderlist
				.replaceAll(";", "_");

		// 临时通用更改三级订单状态的模型
		this.orderSubmitShell = "java -jar "
				+ this.OuterRunnableJarBaseDir
				+ "OrderSubmitRunnableJar-2.2.jar " + this.BPLDBConn + " "
				+ this.l3order.l4orderlist;

	}

	// 添加各个模块，按照三级订单的类型进行动态添加，方便扩展订单类型的扩展
	public void AddMoudles() {
		System.out
				.println("KeplerL3OrderMoudlesGenerator::public void AddMoudles() | \n-->添加三级订单"
						+ this.l3order.jobId + "工作流功能模块");
		switch (this.l3order.orderType) {
		case "L3DP": {
			AddDPMoudles();
			break;
		}
		case "L3RN": {
			AddRNMoudles();
			break;
		}
		case "L3GN": {
			AddGNMoudles();
			break;
		}
		case "L3DS": {
			AddDSMoudles();
			break;
		}
		case "L3SPU": {
			AddSPUMoudles();
			break;
		}
		case "L3CP": {
			AddCPMoudles();
			break;
		}
		case "L3FP": {
			AddFPMoudles();
			break;
		}
		case "L3AP": {
			AddAPMoudles();
			break;
		}
		case "L3CPU": {
			AddCPUMoudles();
			break;
		}
		default: {
			logger.error("-->错误：未找到对应的订单类型！");
			return;
		}
		}
	}

	// 添加数据准备模块
	private void AddDPMoudles() {
		// DP模块组整体向上偏移1000
		AddDataPrepareMoudles(0, -1000);
	}

	private void AddGNMoudles() {
		// GN模块组整体向下偏移2000
		AddOrderSubmitModles(0, 0);
	}

	private void AddRNMoudles() {
		// RN模块组整体向下偏移0
		AddOrderSubmitModles(0, 1000);
	}

	// 添加数据分幅模块
	private void AddDSMoudles() {
		// AddDataSwathModles(0, 2000);
		AddOrderSubmitModles(0, 2000);

	}

	// 添加标准产品上传模块
	private void AddSPUMoudles() {
		AddStandardProductUploadMoudles(0, 3000);
	}

	private void AddFPMoudles() {
		// FP模块组整体向下偏移6000
		AddOrderSubmitModles(0, 4000);
	}

	private void AddAPMoudles() {
		// AP模块组整体向下偏移6000
		AddOrderSubmitModles(0, 4000);
	}

	private void AddCPMoudles() {
		// CP模块组整体向下偏移4000
		AddOrderSubmitModles(0, 4000);
	}

	private void AddCPUMoudles() {
		// CPU模块组整体向下偏移5000
		AddCommonProductMoudles(0, 5000);

	}

	// 添加各个模块，按照三级订单的类型进行动态添加，方便扩展订单类型的扩展

	// 数据准备模块
	private void AddDataPrepareMoudles(int x_offset, int y_offset) {
		KeplerActors actors = new KeplerActors(x_offset, y_offset);
		KeplerLink link = new KeplerLink();
		KeplerRelation relation = new KeplerRelation(this.l3order.jobId);

		// 2 添加三级订单执行、二级订单状态更改及其连接
		// 数据下载模块
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "RSDataPrepareExecutionCMD", dataPrepareShell);
		Actors += actors.setExecution(this.l3order.jobId
				+ "RSDataPrepareExecution", 1);

		// 执行结果判读
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "GetExecutedStatusSQL",
				"select workingStatus from l3orderdb where JobId='"
						+ this.l3order.jobId + "'");
		Actors += actors.setDatabaseQuery(this.l3order.jobId
				+ "GetExecutedStatus", DBConnParas);
		Actors += actors.setStringCompare(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation", "Finish");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch");
		// 更新二级订单状态
		// 更新执行成功状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatusSQL",
				"update l2orderdb set orderStatus='" + this.l3order.orderType
						+ "' where JobId='" + L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus", DBConnParas);
		// 更新执行错误状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatusSQL",
				"update l2orderdb set WorkingStatus='Error' where JobId='"
						+ L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus", DBConnParas);
		Actors += actors.setBoolConstant(this.l3order.jobId
				+ "ExecutionErrorStopConfirm", true);
		Actors += actors.setStop(this.l3order.jobId + "ExecutionErrorStop");

		// _________添加提交模块连接__________________
		Relations += relation.setRelation();// 21
		Links += link.setLink(this.l3order.jobId
				+ "RSDataPrepareExecutionCMD.output", this.l3order.jobId
				+ "RSDataPrepareExecution.command", relation.getRelationName());
		Relations += relation.setRelation();// 5
		Links += link.setLink(this.l3order.jobId
				+ "RSDataPrepareExecution.output", this.l3order.jobId
				+ "GetExecutedStatusSQL.trigger", relation.getRelationName());
		Relations += relation.setRelation();// 13
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutedStatusSQL.output", this.l3order.jobId
				+ "GetExecutedStatus.query", relation.getRelationName());

		Relations += relation.setRelation();// 22
		Links += link.setTriLink(this.l3order.jobId
				+ "GetExecutedStatus.result", this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.input", this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.firstString",
				relation.getRelationName());

		Relations += relation.setRelation();// 23
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.output",
				this.l3order.jobId + "GetExecutionStatusBooleanSwitch.control",
				relation.getRelationName());
		Relations += relation.setRelation();// 24
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.trueOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateFinishStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 25
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus.input",
				relation.getRelationName());
		Relations += relation.setRelation();// 26
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.falseOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateErrorStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 27
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus.input",
				relation.getRelationName());

		Relations += relation.setRelation();// 28
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatus.result", this.l3order.jobId
				+ "ExecutionErrorStopConfirm.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 29
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionErrorStopConfirm.output", this.l3order.jobId
				+ "ExecutionErrorStop.input", relation.getRelationName());

		// 定义firstInputPort lastOutputPort名称
		this.firstInputPort = this.l3order.jobId
				+ "RSDataPrepareExecutionCMD.trigger";
		this.lastOutPort = this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatus.result";

	}

	// 分幅模块
	private void AddDataSwathModles(int x_offset, int y_offset) {

		KeplerActors actors = new KeplerActors(x_offset, y_offset);
		KeplerLink link = new KeplerLink();
		KeplerRelation relation = new KeplerRelation(this.l3order.jobId);

		// 2 添加三级订单执行、二级订单状态更改及其连接
		// 标准产品上传模块
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "DataSwathExecutionCMD",
				dataSwathShell);
		Actors += actors.setExecution(this.l3order.jobId
				+ "DataSwathExecution", 1);

		// 执行结果判读
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "GetExecutedStatusSQL",
				"select workingStatus from l3orderdb where JobId='"
						+ this.l3order.jobId + "'");
		Actors += actors.setDatabaseQuery(this.l3order.jobId
				+ "GetExecutedStatus", DBConnParas);
		Actors += actors.setStringCompare(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation", "Finish");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch");
		// 更新二级订单状态
		// 更新执行成功状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatusSQL",
				"update l2orderdb set orderStatus='" + this.l3order.orderType
						+ "' where JobId='" + L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus", DBConnParas);
		// 更新执行错误状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatusSQL",
				"update l2orderdb set WorkingStatus='Error' where JobId='"
						+ L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus", DBConnParas);
		Actors += actors.setBoolConstant(this.l3order.jobId
				+ "ExecutionErrorStopConfirm", true);
		Actors += actors.setStop(this.l3order.jobId + "ExecutionErrorStop");

		// _________添加提交模块连接__________________
		Relations += relation.setRelation();// 21
		Links += link.setLink(this.l3order.jobId
				+ "StandardProductUploadExecutionCMD.output",
				this.l3order.jobId + "StandardProductUploadExecution.command",
				relation.getRelationName());
		Relations += relation.setRelation();// 5
		Links += link.setLink(this.l3order.jobId
				+ "StandardProductUploadExecution.output", this.l3order.jobId
				+ "GetExecutedStatusSQL.trigger", relation.getRelationName());
		Relations += relation.setRelation();// 13
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutedStatusSQL.output", this.l3order.jobId
				+ "GetExecutedStatus.query", relation.getRelationName());

		Relations += relation.setRelation();// 22
		Links += link.setTriLink(this.l3order.jobId
				+ "GetExecutedStatus.result", this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.input", this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.firstString",
				relation.getRelationName());

		Relations += relation.setRelation();// 23
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.output",
				this.l3order.jobId + "GetExecutionStatusBooleanSwitch.control",
				relation.getRelationName());
		Relations += relation.setRelation();// 24
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.trueOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateFinishStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 25
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus.input",
				relation.getRelationName());
		Relations += relation.setRelation();// 26
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.falseOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateErrorStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 27
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus.input",
				relation.getRelationName());

		Relations += relation.setRelation();// 28
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatus.result", this.l3order.jobId
				+ "ExecutionErrorStopConfirm.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 29
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionErrorStopConfirm.output", this.l3order.jobId
				+ "ExecutionErrorStop.input", relation.getRelationName());

		// 定义firstInputPort lastOutputPort名称
		this.firstInputPort = this.l3order.jobId
				+ "StandardProductUploadExecutionCMD.trigger";
		this.lastOutPort = this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatus.result";

	}

	// 标准产品上传模块
	private void AddStandardProductUploadMoudles(int x_offset, int y_offset) {
		// TODO Auto-generated method stub
		KeplerActors actors = new KeplerActors(x_offset, y_offset);
		KeplerLink link = new KeplerLink();
		KeplerRelation relation = new KeplerRelation(this.l3order.jobId);

		// 2 添加三级订单执行、二级订单状态更改及其连接
		// 标准产品上传模块
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "StandardProductUploadExecutionCMD",
				standardProductUploadShell);
		Actors += actors.setExecution(this.l3order.jobId
				+ "StandardProductUploadExecution", 1);

		// 执行结果判读
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "GetExecutedStatusSQL",
				"select workingStatus from l3orderdb where JobId='"
						+ this.l3order.jobId + "'");
		Actors += actors.setDatabaseQuery(this.l3order.jobId
				+ "GetExecutedStatus", DBConnParas);
		Actors += actors.setStringCompare(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation", "Finish");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch");
		// 更新二级订单状态
		// 更新执行成功状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatusSQL",
				"update l2orderdb set orderStatus='" + this.l3order.orderType
						+ "' where JobId='" + L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus", DBConnParas);
		// 更新执行错误状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatusSQL",
				"update l2orderdb set WorkingStatus='Error' where JobId='"
						+ L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus", DBConnParas);
		Actors += actors.setBoolConstant(this.l3order.jobId
				+ "ExecutionErrorStopConfirm", true);
		Actors += actors.setStop(this.l3order.jobId + "ExecutionErrorStop");

		// _________添加提交模块连接__________________
		Relations += relation.setRelation();// 21
		Links += link.setLink(this.l3order.jobId
				+ "StandardProductUploadExecutionCMD.output",
				this.l3order.jobId + "StandardProductUploadExecution.command",
				relation.getRelationName());
		Relations += relation.setRelation();// 5
		Links += link.setLink(this.l3order.jobId
				+ "StandardProductUploadExecution.output", this.l3order.jobId
				+ "GetExecutedStatusSQL.trigger", relation.getRelationName());
		Relations += relation.setRelation();// 13
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutedStatusSQL.output", this.l3order.jobId
				+ "GetExecutedStatus.query", relation.getRelationName());

		Relations += relation.setRelation();// 22
		Links += link.setTriLink(this.l3order.jobId
				+ "GetExecutedStatus.result", this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.input", this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.firstString",
				relation.getRelationName());

		Relations += relation.setRelation();// 23
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.output",
				this.l3order.jobId + "GetExecutionStatusBooleanSwitch.control",
				relation.getRelationName());
		Relations += relation.setRelation();// 24
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.trueOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateFinishStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 25
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus.input",
				relation.getRelationName());
		Relations += relation.setRelation();// 26
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.falseOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateErrorStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 27
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus.input",
				relation.getRelationName());

		Relations += relation.setRelation();// 28
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatus.result", this.l3order.jobId
				+ "ExecutionErrorStopConfirm.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 29
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionErrorStopConfirm.output", this.l3order.jobId
				+ "ExecutionErrorStop.input", relation.getRelationName());

		// 定义firstInputPort lastOutputPort名称
		this.firstInputPort = this.l3order.jobId
				+ "StandardProductUploadExecutionCMD.trigger";
		this.lastOutPort = this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatus.result";

	}

	// 共性产品上传模块
	private void AddCommonProductMoudles(int x_offset, int y_offset) {
		// TODO Auto-generated method stub
		KeplerActors actors = new KeplerActors(x_offset, y_offset);
		KeplerLink link = new KeplerLink();
		KeplerRelation relation = new KeplerRelation(this.l3order.jobId);

		// 2 添加三级订单执行、二级订单状态更改及其连接
		// 标准产品上传模块
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "StandardProductUploadExecutionCMD",
				commonUpdateL3OrderStatusShell);
		Actors += actors.setExecution(this.l3order.jobId
				+ "StandardProductUploadExecution", 1);

		// 执行结果判读
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "GetExecutedStatusSQL",
				"select workingStatus from l3orderdb where JobId='"
						+ this.l3order.jobId + "'");
		Actors += actors.setDatabaseQuery(this.l3order.jobId
				+ "GetExecutedStatus", DBConnParas);
		Actors += actors.setStringCompare(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation", "Finish");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch");
		// 更新二级订单状态
		// 更新执行成功状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatusSQL",
				"update l2orderdb set orderStatus='" + this.l3order.orderType
						+ "' where JobId='" + L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus", DBConnParas);
		// 更新执行错误状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatusSQL",
				"update l2orderdb set WorkingStatus='Error' where JobId='"
						+ L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus", DBConnParas);
		Actors += actors.setBoolConstant(this.l3order.jobId
				+ "ExecutionErrorStopConfirm", true);
		Actors += actors.setStop(this.l3order.jobId + "ExecutionErrorStop");

		// _________添加提交模块连接__________________
		Relations += relation.setRelation();// 21
		Links += link.setLink(this.l3order.jobId
				+ "StandardProductUploadExecutionCMD.output",
				this.l3order.jobId + "StandardProductUploadExecution.command",
				relation.getRelationName());
		Relations += relation.setRelation();// 5
		Links += link.setLink(this.l3order.jobId
				+ "StandardProductUploadExecution.output", this.l3order.jobId
				+ "GetExecutedStatusSQL.trigger", relation.getRelationName());
		Relations += relation.setRelation();// 13
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutedStatusSQL.output", this.l3order.jobId
				+ "GetExecutedStatus.query", relation.getRelationName());

		Relations += relation.setRelation();// 22
		Links += link.setTriLink(this.l3order.jobId
				+ "GetExecutedStatus.result", this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.input", this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.firstString",
				relation.getRelationName());

		Relations += relation.setRelation();// 23
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.output",
				this.l3order.jobId + "GetExecutionStatusBooleanSwitch.control",
				relation.getRelationName());
		Relations += relation.setRelation();// 24
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.trueOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateFinishStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 25
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus.input",
				relation.getRelationName());
		Relations += relation.setRelation();// 26
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.falseOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateErrorStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 27
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus.input",
				relation.getRelationName());

		Relations += relation.setRelation();// 28
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatus.result", this.l3order.jobId
				+ "ExecutionErrorStopConfirm.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 29
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionErrorStopConfirm.output", this.l3order.jobId
				+ "ExecutionErrorStop.input", relation.getRelationName());

		// 定义firstInputPort lastOutputPort名称
		this.firstInputPort = this.l3order.jobId
				+ "StandardProductUploadExecutionCMD.trigger";
		this.lastOutPort = this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatus.result";
	}

	// 订单提交模块：RN、GN、DS CP AP FP订单的提交
	private void AddOrderSubmitModles(int x_offset, int y_offset) {

		KeplerActors actors = new KeplerActors(x_offset, y_offset);
		KeplerLink link = new KeplerLink();
		KeplerRelation relation = new KeplerRelation(this.l3order.jobId);

		// 2 添加三级订单执行、二级订单状态更改及其连接
		// 数据下载模块
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "OrderSubmitionCMD", orderSubmitShell);
		Actors += actors.setExecution(this.l3order.jobId + "OrderSubmition", 1);

		// 执行结果判读
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "GetExecutedStatusSQL",
				"select workingStatus from l3orderdb where JobId='"
						+ this.l3order.jobId + "'");
		Actors += actors.setDatabaseQuery(this.l3order.jobId
				+ "GetExecutedStatus", DBConnParas);
		Actors += actors.setStringCompare(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation", "Finish");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch");
		// 更新二级订单状态
		// 更新执行成功状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatusSQL",
				"update l2orderdb set orderStatus='" + this.l3order.orderType
						+ "' where JobId='" + L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus", DBConnParas);
		// 更新执行错误状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatusSQL",
				"update l2orderdb set WorkingStatus='Error' where JobId='"
						+ L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus", DBConnParas);
		Actors += actors.setBoolConstant(this.l3order.jobId
				+ "ExecutionErrorStopConfirm", true);
		Actors += actors.setStop(this.l3order.jobId + "ExecutionErrorStop");

		// _________添加提交模块连接__________________
		Relations += relation.setRelation();// 21
		Links += link.setLink(this.l3order.jobId + "OrderSubmitionCMD.output",
				this.l3order.jobId + "OrderSubmition.command",
				relation.getRelationName());
		Relations += relation.setRelation();// 5
		Links += link.setLink(this.l3order.jobId + "OrderSubmition.output",
				this.l3order.jobId + "GetExecutedStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 13
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutedStatusSQL.output", this.l3order.jobId
				+ "GetExecutedStatus.query", relation.getRelationName());

		Relations += relation.setRelation();// 22
		Links += link.setTriLink(this.l3order.jobId
				+ "GetExecutedStatus.result", this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.input", this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.firstString",
				relation.getRelationName());

		Relations += relation.setRelation();// 23
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.output",
				this.l3order.jobId + "GetExecutionStatusBooleanSwitch.control",
				relation.getRelationName());
		Relations += relation.setRelation();// 24
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.trueOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateFinishStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 25
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus.input",
				relation.getRelationName());
		Relations += relation.setRelation();// 26
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.falseOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateErrorStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 27
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus.input",
				relation.getRelationName());

		Relations += relation.setRelation();// 28
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatus.result", this.l3order.jobId
				+ "ExecutionErrorStopConfirm.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 29
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionErrorStopConfirm.output", this.l3order.jobId
				+ "ExecutionErrorStop.input", relation.getRelationName());

		// 定义firstInputPort lastOutputPort名称
		this.firstInputPort = this.l3order.jobId + "OrderSubmitionCMD.trigger";
		this.lastOutPort = this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatus.result";

	}

	private void AddDefaultMoudles(double x_offset, double y_offset) {

		KeplerActors actors = new KeplerActors(x_offset, y_offset);
		KeplerLink link = new KeplerLink();
		KeplerRelation relation = new KeplerRelation(this.l3order.jobId);

		// 1 添加提交三级订单模块及其连接
		// xml参数
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "InitialSubmitionXml", XML);
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "RepeatSubmitionXml", XML);
		// 提交webservice
		Actors += actors.setWSWithComplexTypes(this.l3order.jobId
				+ "InitialSubmition", WSDL, WSFuction);
		Actors += actors.setWSWithComplexTypes(this.l3order.jobId
				+ "RepeatSubmition", WSDL, WSFuction);
		// 提交结果判断
		// Actor
		Actors += actors.setBooleanSelect(this.l3order.jobId
				+ "SubmitionBooleanSelect");
		Actors += actors.setStringCompare(this.l3order.jobId
				+ "SubmitionStatusConditionEvaluation", "Success");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetSubmitStatusBooleanSwitch");
		// 添加SampleDelay模块防止重复提交
		Actors += actors.setSampleDelay(this.l3order.jobId + "SampleDelay");
		// 添加重复提交模块
		Actors += actors
				.setTimesRamp(this.l3order.jobId + "SubmitionTimesRamp");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetSubmitTimesBooleanSwitch");
		Actors += actors.setConditionEvaluation(this.l3order.jobId
				+ "SubmitionTimesConditionEvaluation", 5);
		Actors += actors.setSleep(this.l3order.jobId + "SubmitionSleep", 10000);
		// // 提交失败退出
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "UpdateSubmitionErrorSQL",
				"update l3orderdb set workingStatus='Submit Error' where JobId='"
						+ this.l3order.jobId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId
				+ "UpdateSubmitionError", DBConnParas);
		Actors += actors.setStop(this.l3order.jobId + "SubmitionErrorStop");
		Actors += actors.setBoolConstant(this.l3order.jobId
				+ "SubmitionErrorStopConfirm", true);

		// _________添加提交模块连接__________________
		// 提交WebService模块连接
		Relations += relation.setRelation();// 1
		Links += link.setLink(
				this.l3order.jobId + "InitialSubmitionXml.output",
				this.l3order.jobId + "InitialSubmition.strRequestXML",
				relation.getRelationName());
		Relations += relation.setRelation();// 2
		Links += link.setLink(this.l3order.jobId + "InitialSubmition.&gt; "
				+ WSFuction + "Return", this.l3order.jobId
				+ "SubmitionBooleanSelect.falseInput",
				relation.getRelationName());
		Relations += relation.setRelation();// 3
		Links += link.setLink(this.l3order.jobId + "RepeatSubmitionXml.output",
				this.l3order.jobId + "RepeatSubmition.strRequestXML",
				relation.getRelationName());
		Relations += relation.setRelation();// 4
		Links += link.setLink(this.l3order.jobId + "RepeatSubmition.&gt; "
				+ WSFuction + "Return", this.l3order.jobId
				+ "SubmitionBooleanSelect.trueInput",
				relation.getRelationName());
		// 提交选择连接
		Relations += relation.setRelation();// 6
		Links += link.setTriLink(this.l3order.jobId
				+ "SubmitionBooleanSelect.output", this.l3order.jobId
				+ "GetSubmitStatusBooleanSwitch.input", this.l3order.jobId
				+ "SubmitionStatusConditionEvaluation.firstString",
				relation.getRelationName());

		Relations += relation.setRelation();// 7
		Links += link.setTriLink(this.l3order.jobId
				+ "SubmitionStatusConditionEvaluation.output",
				this.l3order.jobId + "GetSubmitStatusBooleanSwitch.control",
				this.l3order.jobId + "SampleDelay.input",
				relation.getRelationName());

		Relations += relation.setRelation();// 8
		Links += link.setLink(this.l3order.jobId + "SampleDelay.output",
				this.l3order.jobId + "SubmitionBooleanSelect.control",
				relation.getRelationName());
		// 错误提交连接
		Relations += relation.setRelation();// 9
		Links += link.setLink(this.l3order.jobId
				+ "GetSubmitStatusBooleanSwitch.falseOutput",
				this.l3order.jobId + "SubmitionTimesRamp.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 10
		Links += link.setTriLink(this.l3order.jobId
				+ "SubmitionTimesRamp.output", this.l3order.jobId
				+ "SubmitionTimesConditionEvaluation.in", this.l3order.jobId
				+ "GetSubmitTimesBooleanSwitch.input",
				relation.getRelationName());
		Relations += relation.setRelation();// 11
		Links += link.setLink(this.l3order.jobId
				+ "SubmitionTimesConditionEvaluation.output",
				this.l3order.jobId + "GetSubmitTimesBooleanSwitch.control",
				relation.getRelationName());
		Relations += relation.setRelation();// 12
		Links += link.setLink(this.l3order.jobId
				+ "GetSubmitTimesBooleanSwitch.trueOutput", this.l3order.jobId
				+ "SubmitionSleep.input", relation.getRelationName());
		Relations += relation.setRelation();// 14
		Links += link.setLink(this.l3order.jobId + "SubmitionSleep.output",
				this.l3order.jobId + "RepeatSubmitionXml.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 15
		Links += link
				.setLink(this.l3order.jobId
						+ "GetSubmitTimesBooleanSwitch.falseOutput",
						this.l3order.jobId + "UpdateSubmitionErrorSQL.trigger",
						relation.getRelationName());
		Relations += relation.setRelation();// 16
		Links += link.setLink(this.l3order.jobId
				+ "UpdateSubmitionErrorSQL.output", this.l3order.jobId
				+ "UpdateSubmitionError.input", relation.getRelationName());
		Relations += relation.setRelation();// 18
		Links += link.setLink(this.l3order.jobId
				+ "UpdateSubmitionError.result", this.l3order.jobId
				+ "SubmitionErrorStopConfirm.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 19
		Links += link.setLink(this.l3order.jobId
				+ "SubmitionErrorStopConfirm.output", this.l3order.jobId
				+ "SubmitionErrorStop.input", relation.getRelationName());

		// ***********************************************************************

		// 2 添加三级订单执行、二级订单状态更改及其连接
		// 等待执行
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "WaitExecutedCMD", JavaCommand + " " + DBParas[0] + " "
				+ DBParas[1] + " " + DBParas[2] + " " + DBParas[3] + " "
				+ DBParas[4] + " " + this.l3order.jobId + " " + "Finish");
		Actors += actors.setExecution(this.l3order.jobId + "WaitExecuted", -1);

		// 执行结果判读
		Actors += actors.setStringConstant(this.l3order.jobId
				+ "GetExecutedStatusSQL",
				"select workingStatus from l3orderdb where JobId='"
						+ this.l3order.jobId + "'");
		Actors += actors.setDatabaseQuery(this.l3order.jobId
				+ "GetExecutedStatus", DBConnParas);
		Actors += actors.setStringCompare(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation", "Finish");
		Actors += actors.setBooleanSwitch(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch");
		// 更新二级订单状态
		// 更新执行成功状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatusSQL",
				"update l2orderdb set orderStatus='" + this.l3order.orderType
						+ "' where JobId='" + L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus", DBConnParas);
		// 更新执行错误状态
		Actors += actors.setStringConstant(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatusSQL",
				"update l2orderdb set WorkingStatus='Error' where JobId='"
						+ L2OrderId + "'");
		Actors += actors.setDatabaseBWriter(this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus", DBConnParas);
		Actors += actors.setBoolConstant(this.l3order.jobId
				+ "ExecutionErrorStopConfirm", true);
		Actors += actors.setStop(this.l3order.jobId + "ExecutionErrorStop");

		// _________添加提交模块连接__________________
		Relations += relation.setRelation();// 20
		Links += link.setLink(this.l3order.jobId
				+ "GetSubmitStatusBooleanSwitch.trueOutput", this.l3order.jobId
				+ "WaitExecutedCMD.trigger", relation.getRelationName());
		Relations += relation.setRelation();// 21
		Links += link.setLink(this.l3order.jobId + "WaitExecutedCMD.output",
				this.l3order.jobId + "WaitExecuted.command",
				relation.getRelationName());

		Relations += relation.setRelation();// 5
		Links += link.setLink(this.l3order.jobId + "WaitExecuted.output",
				this.l3order.jobId + "GetExecutedStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 13
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutedStatusSQL.output", this.l3order.jobId
				+ "GetExecutedStatus.query", relation.getRelationName());

		Relations += relation.setRelation();// 22
		Links += link.setTriLink(this.l3order.jobId
				+ "GetExecutedStatus.result", this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.input", this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.firstString",
				relation.getRelationName());

		Relations += relation.setRelation();// 23
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionStatusConditionEvaluation.output",
				this.l3order.jobId + "GetExecutionStatusBooleanSwitch.control",
				relation.getRelationName());
		Relations += relation.setRelation();// 24
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.trueOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateFinishStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 25
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateFinishStatus.input",
				relation.getRelationName());
		Relations += relation.setRelation();// 26
		Links += link.setLink(this.l3order.jobId
				+ "GetExecutionStatusBooleanSwitch.falseOutput",
				this.l3order.jobId + "To" + L2OrderId
						+ "UpdateErrorStatusSQL.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 27
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatusSQL.output", this.l3order.jobId + "To"
				+ L2OrderId + "UpdateErrorStatus.input",
				relation.getRelationName());

		Relations += relation.setRelation();// 28
		Links += link.setLink(this.l3order.jobId + "To" + L2OrderId
				+ "UpdateErrorStatus.result", this.l3order.jobId
				+ "ExecutionErrorStopConfirm.trigger",
				relation.getRelationName());
		Relations += relation.setRelation();// 29
		Links += link.setLink(this.l3order.jobId
				+ "ExecutionErrorStopConfirm.output", this.l3order.jobId
				+ "ExecutionErrorStop.input", relation.getRelationName());

		// 定义firstInputPort lastOutputPort名称
		this.firstInputPort = this.l3order.jobId
				+ "InitialSubmitionXml.trigger";
		this.lastOutPort = this.l3order.jobId + "To" + L2OrderId
				+ "UpdateFinishStatus.result";

	}

}
