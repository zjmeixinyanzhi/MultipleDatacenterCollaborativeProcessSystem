package org.kepler.moml;

/**
 * 创建时间：2014-11-1 下午7:49:44 项目名称：MCA_KeplerWorkflow_Engine 2014-11-1
 * 
 * @author 张杰
 * @version 1.0 文件名称：KeplerWorkFlowMoudleTemplate.java
 *          类说明：参照MOML格式，抽取出XML形式放入Kepler的功能模块
 *          ，动态赋值传参，用于组装各个模块动态生成XML格式的Kepler工作流文件
 */
public class KeplerActors {
	// Actor组的位置整体偏移量
	public double x_offset = 0;
	public double y_offset = 0;

	// 默认无位置偏移的构造函数
	public KeplerActors() {
	}

	// 有位置偏移的构造函数
	public KeplerActors(double xoffset, double yoffset) {
		// TODO Auto-generated constructor stub
		this.x_offset = xoffset;
		this.y_offset = yoffset;
	}

	// 加载根元素模块
	public String setRootElement(String CompositeClassEntityName,
			String NamedObjId, String DDFDirectorName) {
		String rootelement = "<entity name=\""
				+ CompositeClassEntityName
				+ "\" class=\"org.kepler.moml.CompositeClassEntity\">"
				+ "<display name=\""
				+ NamedObjId
				+ "\"/>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:58709:4:224\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"org.kepler.moml.CompositeClassEntity\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"null\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"_createdBy\" class=\"ptolemy.kernel.attributes.VersionAttribute\" value=\"9.1.devel\">"
				+ "</property>"
				+ "<property name=\"_windowProperties\" class=\"ptolemy.actor.gui.WindowPropertiesAttribute\" value=\"{bounds={69, 25, 1239, 797}, maximized=false}\">"
				+ "</property>"
				+ "<property name=\"_vergilSize\" class=\"ptolemy.actor.gui.SizeAttribute\" value=\"[918, 635]\">"
				+ "</property>"
				+ "<property name=\"_vergilZoomFactor\" class=\"ptolemy.data.expr.ExpertParameter\" value=\"0.5120000000000042\">"
				+ "</property>"
				+ "<property name=\"_vergilCenter\" class=\"ptolemy.data.expr.ExpertParameter\" value=\"{-117.39453125000921, 523.8749999999969}\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:436:1:urn:lsid:gamma.msi.ucsb.edu/OpenAuth/:276:18:2:urn:lsid:uuid:fa46d0eb-beee-4501-9488-fa5c8f58b9ee:22:2:urn:lsid:kepler-project.org/ns/:1348:11:1:urn:lsid:kepler-project.org/ns/:54265:23:27:urn:lsid:kepler-project.org/ns/:54265:29:1:urn:lsid:kepler-project.org/ns/:54265:30:11:urn:lsid:kepler-project.org/ns/:54265:34:20:urn:lsid:kepler-project.org/ns/:54265:38:1:urn:lsid:kepler-project.org/ns/:56038:33:74:urn:lsid:kepler-project.org/ns/:56038:63:159:urn:lsid:kepler-project.org/ns/:56038:297:27:urn:lsid:kepler-project.org/ns/:56038:301:10:urn:lsid:kepler-project.org/ns/:56038:303:3848\">"
				+ "</property>"
				+ "<property name=\"enableBackwardTypeInference\" class=\"ptolemy.data.expr.Parameter\" value=\"false\">"
				+ "</property>"
				+ "<property name=\""
				+ DDFDirectorName
				+ "\" class=\"ptolemy.domains.ddf.kernel.DDFDirector\">"
				+ "<property name=\"localClock\" class=\"ptolemy.actor.LocalClock\">"
				+ "<property name=\"globalTimeResolution\" class=\"ptolemy.actor.parameters.SharedParameter\" value=\"1E-10\">"
				+ "</property>"
				+ "<property name=\"clockRate\" class=\"ptolemy.data.expr.Parameter\" value=\"1.0\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"iterations\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"maximumReceiverCapacity\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"runUntilDeadlockInOneIteration\" class=\"ptolemy.data.expr.Parameter\" value=\"false\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org:director:5:1\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.domains.ddf.kernel.DDFDirector\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:directorclass:5:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#Director\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#Director\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"{-480, 245}\">"
				+ "</property>" + "</property>";
		return rootelement;
	}

	// 加载根元素结束模块
	public String setEndRootElement() {
		return "</entity>";
	}

	// 复合类型的WebService模块
	public String setWSWithComplexTypes(String name, String WSDL, String Methoed) {
		// 模块位置
		String location = "[" + (-550.0 + x_offset) + "," + (355.0 + y_offset)
				+ "]";

		String l3ordersubmition = "<entity name=\""
				+ name
				+ "\" class=\"org.sdm.spa.WSWithComplexTypes\">"
				+ "<property name=\"wsdl\" class=\"ptolemy.actor.parameters.PortParameter\" value=\""
				+ WSDL
				+ "\">"
				+ "</property>"
				+ "<property name=\"method\" class=\"ptolemy.data.expr.StringParameter\" value=\""
				+ Methoed
				+ "\">"
				+ "</property>"
				+ "<property name=\"inputMechanism\" class=\"ptolemy.data.expr.StringParameter\" value=\"simple\">"
				+ "</property>"
				+ "<property name=\"outputMechanism\" class=\"ptolemy.data.expr.StringParameter\" value=\"simple\">"
				+ "</property>"
				+ "<property name=\"outputNil\" class=\"ptolemy.data.expr.Parameter\" value=\"false\">"
				+ "</property>"
				+ "<property name=\"username\" class=\"ptolemy.data.expr.StringParameter\" value=\"\">"
				+ "</property>"
				+ "<property name=\"password\" class=\"ptolemy.data.expr.StringParameter\" value=\"\">"
				+ "</property>"
				+ "<property name=\"timeout\" class=\"ptolemy.data.expr.StringParameter\" value=\"600000\">"
				+ "</property>"
				+ "<property name=\"ignoreInvokeErrors\" class=\"ptolemy.data.expr.Parameter\" value=\"false\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:120:1279\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"org.sdm.spa.WSWithComplexTypes\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:519:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#WebServiceActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#WebService\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""
				+ location
				+ "\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:519:1:urn:lsid:uuid:b2569269-343d-487f-afb7-e5bd84b7ab67:17:307\">"
				+ "</property>"
				+ "<port name=\"wsdl\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"strRequestXML\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.kernel.util.Attribute\">"
				+ "</property>"
				+ "<property name=\"_type\" class=\"ptolemy.actor.TypeAttribute\" value=\"string\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"&gt; "
				+ Methoed
				+ "Return\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.kernel.util.Attribute\">"
				+ "</property>"
				+ "<property name=\"_type\" class=\"ptolemy.actor.TypeAttribute\" value=\"string\">"
				+ "</property>" + "</port>" + "</entity>";

		return l3ordersubmition;
	}

	// 加载String Constant常量模块
	public String setStringConstant(String name, String constant) {
		// 模块位置"[-600.0, 260.0]"
		String location = "[" + (-600.0 + x_offset) + "," + (260.0 + y_offset)
				+ "]";

		String submitionparameter = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.Const\">"
				+ "<property name=\"value\" class=\"ptolemy.data.expr.Parameter\" value=\"&quot;"
				+ constant
				+ "&quot;\">"
				+ "</property>"
				+ "<property name=\"firingCountLimit\" class=\"ptolemy.data.expr.Parameter\" value=\"NONE\">"
				+ "</property>"
				+ "<property name=\"NONE\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:292:2\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.Const\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:877:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#ConstantActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#Constant\">"
				+ "</property>"
				+ "<property name=\"kar\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:kar:57:1\">"
				+ "</property>"
				+ "<property name=\"_icon\" class=\"ptolemy.vergil.icon.BoxedValueIcon\">"
				+ "<property name=\"attributeName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"value\">"
				+ "</property>"
				+ "<property name=\"displayWidth\" class=\"ptolemy.data.expr.Parameter\" value=\"40\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""
				+ location
				+ "\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:1:1:urn:lsid:uuid:b2569269-343d-487f-afb7-e5bd84b7ab67:35:1\">"
				+ "</property>"
				+ "<port name=\"trigger\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"multiport\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>" + "</port>" + "</entity>";

		return submitionparameter;
	}

	// 添加SampleDelay模块，避免动态工作流迭代时死锁,此处主要用于DDF工作流的订单提交失败时重复提交
	public String setSampleDelay(String name) {
		// 模块位置"{-245.2, 715.0}"
		String location = "[" + (-245.2 + x_offset) + "," + (715.0 + y_offset)
				+ "]";

		String sampledelay = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.domains.sdf.lib.SampleDelay\">"
				+ "<property name=\"initialOutputs\" class=\"ptolemy.data.expr.Parameter\" value=\"{false}\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org:actor:365:1\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.domains.sdf.lib.SampleDelay\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:1131:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType000\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#ControlActor\">"
				+ "</property>"
				+ "<property name=\"semanticType111\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#WorkflowControl\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""
				+ location
				+ "\">"
				+ "</property>"
				+ "<port name=\"input\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"EAST\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"WEST\">"
				+ "</property>" + "</port>" + "</entity>";

		return sampledelay;
	}

	// 加载外部命令执行模块
	public String setExecution(String name,int fireCount) {
		// 模块位置"[5.0, 225.0]"
		String location = "[" + (5.0 + x_offset) + "," + (225.0 + y_offset)
				+ "]";
		//执行次数限制
		String fireCountLimit="NONE";
		if (fireCount>0) {
			fireCountLimit=fireCount+"";			
		} 

		String waitl3orderexecuted = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.Exec\">"
				+ "<property name=\"NONE\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"command\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"\">"
				+ "</property>"
				+ "<property name=\"directory\" class=\"ptolemy.data.expr.FileParameter\" value=\"$CWD\">"
				+ "</property>"
				+ "<property name=\"environment\" class=\"ptolemy.data.expr.Parameter\" value=\"{{name = &quot;&quot;, value = &quot;&quot;}}\">"
				+ "</property>"
				+ "<property name=\"throwExceptionOnNonZeroReturn\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"waitForProcess\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"firingCountLimit\" class=\"ptolemy.data.expr.Parameter\" value=\"" +
				fireCountLimit +
				"\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:34:12\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.Exec\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:976:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#ExternalExecutionEnvironmentActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#UnixCommand\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""
				+ location
				+ "\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:102:1:urn:lsid:kepler-project.org/ns/:54265:24:8\">"
				+ "</property>"
				+ "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"command\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"error\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"input\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"exitCode\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"previous\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>" + "</port>" + "</entity>";

		return waitl3orderexecuted;
	}

	// 加载数据库查询模块
	public String setDatabaseQuery(String name, String dbConnParas) {
		// 模块位置"[85.0, 230.0]"
		String location = "[" + (85 + x_offset) + "," + (230 + y_offset) + "]";

		String exestatus = "<entity name=\""
				+ name
				+ "\" class=\"org.geon.DatabaseQuery\">"
				+ "<property name=\"dbParams\" class=\"ptolemy.actor.parameters.PortParameter\" value=\""
				+ dbConnParas
				+ "\">"
				+ "</property>"
				+ "<property name=\"outputType\" class=\"ptolemy.data.expr.StringParameter\" value=\"no metadata\">"
				+ "</property>"
				+ "<property name=\"query\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"\">"
				+ "</property>"
				+ "<property name=\"outputEachRowSeparately\" class=\"ptolemy.data.expr.Parameter\" value=\"false\">"
				+ "</property>"
				+ "<property name=\"lowerColumnNames\" class=\"ptolemy.data.expr.Parameter\" value=\"false\">"
				+ "</property>"
				+ "<property name=\"_tableauFactory\" class=\"org.kepler.objectmanager.data.db.QBTableauFactory\">"
				+ "<property name=\"sqlName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"sqlDef\">"
				+ "</property>"
				+ "<property name=\"schemaName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"schemaDef\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:101:337\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"org.geon.DatabaseQuery\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:998:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#DatabaseExternalInputActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#DatabaseInputFunction\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\""
				+ location
				+ "\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:129:1\">"
				+ "</property>"
				+ "<property name=\"\" class=\"ptolemy.vergil.basic.DocAttribute\">"
				+ "<property name=\"description\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"&lt;p&gt;The DatabaseQuery actor performs database queries against an open database and outputs the query results in a specified format.&lt;/p&gt;&#10;&#10;&lt;p&gt;Use the OpenDatabaseConnection actor to establish a database connection and generate a reference to that connection. The reference is passed to the DatabaseQuery actor via the dbcon port.&lt;/p&gt;&#10;&#10;&lt;p&gt;A query is passed to the actor via the query port or parameter. Specify whether to output all results at once, or one row at a time using the outputEachRowSeparately parameter.&lt;/p&gt;&#10;&#10;&lt;p&gt;The outputType parameter specifies the format in which to return results: XML, record, array, string, no metadata, or result set.&lt;/p&gt; \">"
				+ "</property>"
				+ "<property name=\"author\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"efrat jaeger\">"
				+ "</property>"
				+ "<property name=\"version\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"null\">"
				+ "</property>"
				+ "<property name=\"outputType (parameter)\" class=\"ptolemy.data.expr.StringParameter\" value=\"The output format: XML, record, array, string, no metadata, or result set.\">"
				+ "</property>"
				+ "<property name=\"schemaDef (parameter)\" class=\"ptolemy.data.expr.StringParameter\" value=\"The schema definition contains the field names of data types.\">"
				+ "</property>"
				+ "<property name=\"outputEachRowSeparately (parameter)\" class=\"ptolemy.data.expr.StringParameter\" value=\"Specify whether to display the complete result at once or each row separately.\">"
				+ "</property>"
				+ "<property name=\"dbcon (port)\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"An input port that accepts a reference to an established database connection. The OpenDatabaseConnection actor can be used to generate this reference.\">"
				+ "</property>"
				+ "<property name=\"result (port)\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"An output port that broadcasts the query result. Results will be output in the format specified with the outputType parameter: XML, record, array, string, no metadata (i.e., a relational string with no metadata), or result set.\">"
				+ "</property>"
				+ "<property name=\"query (parameter)\" class=\"ptolemy.data.expr.StringParameter\" value=\"An input query string. Queries can be specified via the query port or query parameter.\">"
				+ "</property>"
				+ "</property>"
				+ "<port name=\"dbParams\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"dbcon\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"tokenConsumptionRate\" class=\"ptolemy.data.expr.Parameter\" value=\"1\">"
				+ "</property>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"query\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"_type\" class=\"ptolemy.actor.TypeAttribute\" value=\"string\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"result\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"_type\" class=\"ptolemy.actor.TypeAttribute\" value=\"complex\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"previous\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>" + "</port>" + "</entity>";

		return exestatus;
	}

	// 添加三级订单提交次数判断模块模块
	public String setConditionEvaluation(String name, int times) {
		// 模块位置"[-350.0, 160.0]"
		String location = "[" + (-350.0 + x_offset) + "," + (160.0 + y_offset) + "]";

		String conditionevaluation = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.Expression\">"
				+ "<property name=\"expression\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"in&lt;"
				+ times
				+ "\">"
				+ "<property name=\"_hide\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:58709:101:6\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.Expression\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:950:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#MathOperationActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#GeneralPurpose\">"
				+ "</property>"
				+ "<property name=\"_icon\" class=\"ptolemy.vergil.icon.BoxedValueIcon\">"
				+ "<property name=\"attributeName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"expression\">"
				+ "</property>"
				+ "<property name=\"displayWidth\" class=\"ptolemy.data.expr.Parameter\" value=\"60\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:75:1\">"
				+ "</property>"
				+ "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"WEST\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"in\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"EAST\">"
				+ "</property>" + "</port>" + "</entity>";

		return conditionevaluation;

	}

	// 加载字符串比较器
	public String setStringCompare(String name, String correctResult) {
		// 模块位置"[240.0, 660.0]"
		String location = "[" + (240.0 + x_offset) + "," + (660.0 + y_offset) + "]";

		String compare = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.string.StringCompare\">"
				+ "<property name=\"function\" class=\"ptolemy.data.expr.Parameter\" value=\"contains\">"
				+ "</property>"
				+ "<property name=\"ignoreCase\" class=\"ptolemy.data.expr.Parameter\" value=\"false\">"
				+ "</property>"
				+ "<property name=\"firstString\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"\">"
				+ "</property>"
				+ "<property name=\"secondString\" class=\"ptolemy.actor.parameters.PortParameter\" value=\""
				+ correctResult
				+ "\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:430:9\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.string.StringCompare\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:967:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#StringFunctionActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#StatisticalOperation\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:93:1\">"
				+ "</property>"
				+ "<port name=\"firstString\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"secondString\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>" + "</port>" + "</entity>";
		return compare;
	}

	// 加载boolean类型的转换器
	public String setBooleanSwitch(String name) {
		// 模块位置"[335.0, 230.0]"
		String location = "[" + (335.0 + x_offset) + "," + (230 + y_offset)
				+ "]";
		
		String boolswitch = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.BooleanSwitch\">"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org:actor:54:1\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.BooleanSwitch\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:930:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#BooleanControlActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#BooleanControl\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<port name=\"control\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"SOUTH\">"
				+ "</property>" + "</port>" + "</entity>";

		return boolswitch;
	}

	// 加载累加器模块
	public String setTimesRamp(String name) {
		// 模块位置"[-380.0, 25.0]"
		String location = "[" + (-380 + x_offset) + "," + (25 + y_offset)
				+ "]";
		
		String ramp = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.Ramp\">"
				+ "<property name=\"NONE\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"init\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"step\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"1\">"
				+ "</property>"
				+ "<property name=\"firingCountLimit\" class=\"ptolemy.data.expr.Parameter\" value=\"NONE\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:371:6\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.Ramp\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:881:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#IterativeMathOperationActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#IterativeOperation\">"
				+ "</property>"
				+ "<property name=\"semanticType22\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#WorkflowInput\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:5:1\">"
				+ "</property>"
				+ "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"WEST\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"trigger\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"multiport\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"EAST\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"init\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"EAST\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"step\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.Parameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"EAST\">"
				+ "</property>" + "</port>" + "</entity>";

		return ramp;
	}

	// 加载睡眠模块，可以指定睡眠时间（单位为微秒）
	public String setSleep(String name, int sleeptime) {
		// 模块位置"[-595.0, 5.0]"
		String location = "[" + (-595 + x_offset) + "," + (5 + y_offset)
				+ "]";
		
		String sleep = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.Sleep\">"
				+ "<property name=\"sleepTime\" class=\"ptolemy.actor.parameters.PortParameter\" value=\""
				+ sleeptime
				+ "L\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:367:10\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.Sleep\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:966:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#ControlActor\">"
				+ "</property>"
				+ "<property name=\"semanticType01\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#WorkflowControl\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:92:1\">"
				+ "</property>"
				+ "<port name=\"input\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"multiport\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"EAST\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"multiport\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"WEST\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"sleepTime\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_cardinal\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"SOUTH\">"
				+ "</property>" + "</port>" + "</entity>";
		return sleep;
	}

	// 加载写数据库模块：参数为数据库连接参数，以及SQL语句
	public String setDatabaseBWriter(String name, String dbConnParas) {
		// 模块位置"[535.0, 165.0]"
		String location = "[" + (535 + x_offset) + "," + (165 + y_offset)
				+ "]";
		
		String updatel2orderstatus = "<entity name=\""
				+ name
				+ "\" class=\"org.sdm.spa.DatabaseWriter\">"
				+ "<property name=\"dbParams\" class=\"ptolemy.actor.parameters.PortParameter\" value=\""
				+ dbConnParas
				+ "\">"
				+ "</property>"
				+ "<property name=\"input\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"&quot;&quot;\">"
				+ "</property>"
				+ "<property name=\"table\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"\">"
				+ "</property>"
				+ "<property name=\"autoIncColumnName\" class=\"ptolemy.actor.parameters.PortParameter\" value=\"\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:112:194\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"org.sdm.spa.DatabaseWriter\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:998:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#DatabaseExternalInputActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#DatabaseInputFunction\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:518:1\">"
				+ "</property>"
				+ "<port name=\"dbParams\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"dbcon\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"tokenConsumptionRate\" class=\"ptolemy.data.expr.Parameter\" value=\"1\">"
				+ "</property>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"input\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"table\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"autoIncColumnName\" class=\"ptolemy.actor.parameters.ParameterPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"autoIncValue\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"result\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"previous\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"next\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>" + "</port>" + "</entity>";

		return updatel2orderstatus;
	}

	// 加载bool类型常量模块
	public String setBoolConstant(String name, boolean flag) {
		// 模块位置"[415.0, 265.0]"
		String location = "[" + (415 + x_offset) + "," + (265 + y_offset)
				+ "]";
		
		String bool = "";
		if (flag) {
			bool = "true";
		} else {
			bool = "false";
		}
		String stopconfirm = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.Const\">"
				+ "<property name=\"value\" class=\"ptolemy.data.expr.Parameter\" value=\""
				+ bool
				+ "\">"
				+ "</property>"
				+ "<property name=\"firingCountLimit\" class=\"ptolemy.data.expr.Parameter\" value=\"NONE\">"
				+ "</property>"
				+ "<property name=\"NONE\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org/ns/:56038:304:3\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.Const\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:877:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#ConstantActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#Constant\">"
				+ "</property>"
				+ "<property name=\"kar\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:kar:57:1\">"
				+ "</property>"
				+ "<property name=\"_icon\" class=\"ptolemy.vergil.icon.BoxedValueIcon\">"
				+ "<property name=\"attributeName\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"value\">"
				+ "</property>"
				+ "<property name=\"displayWidth\" class=\"ptolemy.data.expr.Parameter\" value=\"40\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<property name=\"derivedFrom\" class=\"org.kepler.moml.NamedObjIdReferralList\" value=\"urn:lsid:kepler-project.org:actor:1:1\">"
				+ "</property>"
				+ "<port name=\"trigger\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"multiport\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>" + "</port>" + "</entity>";

		return stopconfirm;
	}

	// 加载工作流停止模块
	public String setStop(String name) {
		// 模块位置"[500.0, 245.0]"
		String location = "[" + (500 + x_offset) + "," + (245 + y_offset)
				+ "]";
		
		String stop = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.actor.lib.Stop\">"
				+ "<property name=\"_hideName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "<property name=\"entityId\" class=\"org.kepler.moml.NamedObjId\" value=\"urn:lsid:kepler-project.org:actor:55:1\">"
				+ "</property>"
				+ "<property name=\"class\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"ptolemy.actor.lib.Stop\">"
				+ "<property name=\"id\" class=\"ptolemy.kernel.util.StringAttribute\" value=\"urn:lsid:kepler-project.org:class:931:1\">"
				+ "</property>"
				+ "</property>"
				+ "<property name=\"semanticType00\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:1:1#ControlActor\">"
				+ "</property>"
				+ "<property name=\"semanticType11\" class=\"org.kepler.sms.SemanticType\" value=\"urn:lsid:localhost:onto:2:1#WorkflowControl\">"
				+ "</property>"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>" + "</entity>";
		return stop;
	}

	// DDF Boolean类型的选择器模块
	public String setBooleanSelect(String name) {
		// 模块位置"[-415.0, 385.0]"
		String location = "[" + (-415.0 + x_offset) + "," + (385 + y_offset)
				+ "]";
		
		String select = "<entity name=\""
				+ name
				+ "\" class=\"ptolemy.domains.ddf.lib.DDFBooleanSelect\">"
				+ "<property name=\"_location\" class=\"ptolemy.kernel.util.Location\" value=\"" +
				location +
				"\">"
				+ "</property>"
				+ "<port name=\"trueInput\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"tokenConsumptionRate\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"falseInput\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"tokenConsumptionRate\" class=\"ptolemy.data.expr.Parameter\" value=\"0\">"
				+ "</property>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"control\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"input\"/>"
				+ "<property name=\"tokenConsumptionRate\" class=\"ptolemy.data.expr.Parameter\" value=\"1\">"
				+ "</property>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>"
				+ "</port>"
				+ "<port name=\"output\" class=\"ptolemy.actor.TypedIOPort\">"
				+ "<property name=\"output\"/>"
				+ "<property name=\"_showName\" class=\"ptolemy.data.expr.SingletonParameter\" value=\"true\">"
				+ "</property>" + "</port>" + "</entity>";

		return select;

	}

}
