/**
 * ServiceImplSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
//import com.sun.tools.javac.resources.javac;.
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sun.tools.javac.resources.javac;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.string;

import OrderManage.L2ExternalOrder;
import OrderManage.OrderRequest;
import OrderManage.OrderStudio;
import SystemManage.SystemConfig;
//import Workflow.WorkflowAdapter;
//import OrderManage.ProductionConfirmationMsg;
import SystemManage.SystemLogger;
import SystemManage.SystemResource;

public class ServiceImplSoapBindingImpl implements ServiceInterface.ServiceImpl {

	private static ProcessImplProxy project1Proxy;
	private OrderStudio orderStudio;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public ServiceImplSoapBindingImpl() {
		project1Proxy = new ProcessImplProxy(
				"http://localhost:10080/Project1Webservice/services/ProcessImpl"); // 服务器代理设置
		orderStudio = new OrderStudio();
	}

	/*
	 * 方法名称：数据产品检索接口 参数 ：1. strRequestXML：数据产品检索条件（卫星、传感器、时相、空间覆盖、云量等） ： 返回值
	 * ：存档数据产品条目或无存档数据 描述 ：课题三通过课题一检索数据产品
	 */
	public java.lang.String dataProductQuery(java.lang.String strRequestXML)
			throws java.rmi.RemoteException {
		// 调用课题一数据组网分系统提供的数据产品检索接口
		return project1Proxy.dataProductQuery(strRequestXML);
	}

	/*
	 * 方法名称：数据产品详细接口 参数 ：1. strRequestXML：数据产品标识（编号、名称等） ： 返回值
	 * ：数据产品元数据（四角坐标、中心坐标、快视图等） 描述 ：课题三通过课题一查看数据产品详细信息
	 */
	public java.lang.String dataProductViewDetail(java.lang.String strRequestXML)
			throws java.rmi.RemoteException {
		// 调用课题一数据组网分系统提供的数据产品详细接口
		return project1Proxy.dataProductViewDetail(strRequestXML);
	}

	/*
	 * 方法名称：共性产品生产需求提交接口 参数 ：1. strRequestXML：请求XML ： 返回值 ：消息接收成功（共性产品生产任务单号）/
	 * 失败 描述 ：课题三向课题一提交共性产品生产需求，由课题一分析可行性
	 */
	public java.lang.String commonProductRequireSubmit(
			java.lang.String strRequestXML) throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><feedback>";
		ArrayList<OrderRequest> requestList = new ArrayList<OrderRequest>();
		OrderRequest orderRequest = new OrderRequest();
		DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
		format.setLenient(false);
		boolean bRef = false;
		try {
			// 获取请求XML
			// 一级订单号
			String strL1OrderId = getElementText(strRequestXML,
					"//root/condition/id");
			// 二级订单号
			String strL2OrderId = getElementText(strRequestXML,
					"//root/condition/order/id");
			// 订单类型production：生产
			String strL2Type = getElementText(strRequestXML,
					"//root/condition/order/type");
			// 产品类型
			String strL2ProductType = getElementText(strRequestXML,
					"//root/condition/order/producttype");
			// 空间覆盖范围
			String strL2Coverscope = getElementText(strRequestXML,
					"//root/condition/order/coverscope");
			// 开始时间
			String strL2StartDate = getElementText(strRequestXML,
					"//root/condition/order/startdate");
			// 结束时间
			String strL2EndDate = getElementText(strRequestXML,
					"//root/condition/order/enddate");

			// Timestamp tsStartDate = new
			// Timestamp(format.parse(strL2StartDate).getTime());
			// Timestamp tsEndDate = new
			// Timestamp(format.parse(strL2EndDate).getTime());

			orderRequest.jobId_L1 = strL1OrderId;
			orderRequest.jobId = strL2OrderId;
			orderRequest.priority = 1;
			// orderRequest.orderType = strL2Type;
			orderRequest.orderType = "L2CP";
			orderRequest.orderLevel = "2";
			orderRequest.productName = strL2ProductType; // ? //@@@
			orderRequest.geoCoverageStr = strL2Coverscope; // ? //@@@
			// orderRequest.startDate = tsStartDate;
			// orderRequest.endDate = tsEndDate;

			// test
			System.out.println(orderRequest.productName);

			orderRequest.startDate = Date.valueOf(strL2StartDate);
			orderRequest.endDate = Date.valueOf(strL2EndDate);
			// SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			orderRequest.submitDate = new java.util.Date();
			requestList.add(orderRequest);

			bRef = orderStudio.setOrderRequestList(requestList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// catch( DocumentException doce ){
		// ;
		// }

		// 生成（响应）结果XML
		// String strResponseXML = null;

		String strCode;
		String strInfo;
		if (bRef) {
			strCode = "100";
			strInfo = "成功";
		} else {
			strCode = "XXX";
			strInfo = "失败";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo
				+ "</info>" + "</feedback></root>";

		return strResponseXML;
	}

	/*
	 * 方法名称：融合/同化需求提交接口 参数 ：1. strRequestXML：请求XML ： 返回值 ：消息接收成功 / 失败 描述
	 * ：课题一将共性产品生产可行性报告结果反馈给课题三服务平台
	 */
	public java.lang.String faProductRequireSubmit(
			java.lang.String strRequestXML) throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><feedback>";
		ArrayList<OrderRequest> requestList = new ArrayList<OrderRequest>();
		OrderRequest orderRequest = new OrderRequest();
		boolean bRef = false;
		try {
			// 获取请求XML
			// 一级订单号
			String strL1OrderId = getElementText(strRequestXML,
					"//root/condition/id");
			// 二级订单号
			String strL2OrderId = getElementText(strRequestXML,
					"//root/condition/order/id");
			// 订单类型 fuse：融合、assimilate：同化
			String strL2Type = getElementText(strRequestXML,
					"//root/condition/order/type");

			orderRequest.jobId_L1 = strL1OrderId;
			orderRequest.jobId = strL2OrderId;
			orderRequest.priority = 1;
			// orderRequest.orderType = strL2Type;
			if (strL2Type.equals("fuse")) {
				orderRequest.orderType = "L2FP";
			} else if (strL2Type.equals("assimilate")) {
				orderRequest.orderType = "L2AP";
			} else {
				orderRequest.orderType = "";
			}
			orderRequest.orderLevel = "2";
			orderRequest.submitDate = new java.util.Date();
			requestList.add(orderRequest);

			bRef = orderStudio.setOrderRequestList(requestList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// catch( DocumentException doce ){
		// ;
		// }
		// 生成（响应）结果XML
		String strCode;
		String strInfo;
		if (bRef) {
			strCode = "100";
			strInfo = "成功";
		} else {
			strCode = "XXX";
			strInfo = "失败";
		}
		// String strCode = "100";
		// String strInfo = "成功";
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo
				+ "</info>" + "</feedback></root>";

		return strResponseXML;
	}

	/*
	 * 方法名称：共性产品生产确认接口 参数 ：1. strRequestXML：请求XML ： 返回值 ：消息接收成功（共性产品生产任务单号）/ 失败
	 * 描述 ：课题三向课题一提交共性产品生产、融合或同化订单，由课题一分析可行性
	 */
	public java.lang.String commonProductOrderSubmit(
			java.lang.String strRequestXML) throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><feedback>";

		// test
		System.out.println("commonProductOrderSubmit | strRequestXML = "
				+ strRequestXML);
		// System.out.println( strRequestXML );

		boolean bRef = false;
		try {
			// //获取请求XML
			// ProductionConfirmationMsg pcMsg = new
			// ProductionConfirmationMsg();
			// //一级订单号
			// pcMsg.jobId_L1 = getElementText( strRequestXML,
			// "//root/condition/id" );
			// //二级订单号
			// pcMsg.jobId = getElementText( strRequestXML,
			// "//root/condition/order/id" );
			// //订单类型 production：生产、fuse：融合、assimilate：同化
			// pcMsg.orderType = getElementText( strRequestXML,
			// "//root/condition/order/type" );
			// if( pcMsg.orderType.equals( "production" ) ){
			// pcMsg.orderType = "L2CP";
			// }else if( pcMsg.orderType.equals( "fuse" ) ){
			// pcMsg.orderType = "L2FP";
			// }else if( pcMsg.orderType.equals( "assimilate" ) ){
			// pcMsg.orderType = "L2AP";
			// }else{
			// pcMsg.orderType = "";
			// }
			// //是否进行生产、融合或同化
			// String strMotion = getElementText( strRequestXML,
			// "//root/condition/order/motion" );
			// pcMsg.bMotion = Boolean.valueOf( strMotion );
			// //描述信息
			// pcMsg.memo = getElementText( strRequestXML,
			// "//root/condition/order/memo" );
			//
			// boolean bRef = true;
			//
			// bRef = OrderStudio.productionConfirmationMsgList.add( pcMsg );
			//
			// System.out.println( "Service | MsgListSize = " +
			// OrderStudio.productionConfirmationMsgList.size() );
			// Iterator< ProductionConfirmationMsg >
			// productionConfirmationMsg_curr =
			// OrderStudio.productionConfirmationMsgList.iterator();
			// while( productionConfirmationMsg_curr.hasNext() ){
			// ProductionConfirmationMsg pcMsg_test =
			// productionConfirmationMsg_curr.next();
			// System.out.println( "Service | jobId       = " + pcMsg_test.jobId
			// );
			// System.out.println( "Service | jobId_L1    = " +
			// pcMsg_test.jobId_L1 );
			// System.out.println( "Service | orderType   = " +
			// pcMsg_test.orderType );
			// System.out.println( "Service | bMotion     = " +
			// pcMsg_test.bMotion );
			// System.out.println( "Service | memo        = " + pcMsg_test.memo
			// );
			// }

			// 获取请求XML
			String jobId;
			String jobId_L1;
			String orderType;
			boolean bMotion;
			String memo;
			// 一级订单号
			jobId_L1 = getElementText(strRequestXML, "//root/condition/id");
			// 二级订单号
			jobId = getElementText(strRequestXML, "//root/condition/order/id");
			// 订单类型 production：生产、fuse：融合、assimilate：同化
			orderType = getElementText(strRequestXML,
					"//root/condition/order/type");
			if (orderType.equals("production")) {
				orderType = "L2CP";
			} else if (orderType.equals("fuse")) {
				orderType = "L2FP";
			} else if (orderType.equals("assimilate")) {
				orderType = "L2AP";
			} else {
				orderType = "";
			}
			// 是否进行生产、融合或同化
			String strMotion = getElementText(strRequestXML,
					"//root/condition/order/motion");
			bMotion = Boolean.valueOf(strMotion);
			// 描述信息
			memo = getElementText(strRequestXML, "//root/condition/order/memo");

			// test
			System.out.println("commonProductOrderSubmit | jobId_L1  = "
					+ jobId_L1);
			System.out.println("commonProductOrderSubmit | jobId     = "
					+ jobId);
			System.out.println("commonProductOrderSubmit | orderType = "
					+ orderType);
			System.out.println("commonProductOrderSubmit | strMotion = "
					+ strMotion);
			System.out
					.println("commonProductOrderSubmit | memo      = " + memo);

			if (!bMotion) {
				logger.info("FP订单状态无效：" + !bMotion);
				OrderRequest.invalidStatus(jobId);
			}
			bRef = this.orderStudio.setConfirmationStatus(jobId, 1, bMotion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// catch( DocumentException doce ){
		// ;
		// }

		// 生成（响应）结果XML
		// String strCode = "100";
		String strInfo = "成功";
		String strCode;
		// String strInfo;
		if (bRef) {
			strCode = "100";
			strInfo = "成功";
		} else {
			strCode = "XXX";
			strInfo = "失败";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo
				+ "</info>" + "</feedback></root>";
		
		 //调用课题三接口，反馈DP完成状态
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date = new java.util.Date();
			ICPStateFeedbackProxy proxyCPStateFeedbackProxy = new ICPStateFeedbackProxy();
			
			String strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><id>"
					+ getElementText(strRequestXML, "//root/condition/order/id")
					+ "</id>"
					+"<username>"
					+"mca"
					+"</username>"
					+"<content>"
					+"L3DP"
					+"</content>"
					+"<time>"
					+df.format(date)
					+"</time>"
					+ "</condition></root>";
			try {
				proxyCPStateFeedbackProxy.commonProductStateFeedback(strXML);
				logger.info("向课题三反馈数据准备完成");
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		return strResponseXML;
	}

	/*
	 * 方法名称：订单可行数据方案反馈接口 参数 ：1. strRequestXML：请求XML ： 返回值 ：确认分发成功 / 失败 描述
	 * ：课题一共性产品生产分系统、数据同化分系统、数据融合分系统，分别向本系统反馈二级订单 ：的数据解析结果，即可行的订单数据方案的接口
	 */
	public java.lang.String orderRSDataPlan(java.lang.String strRequestXML)
			throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><feedback>";

		// test
		// System.out.println( "orderRSDataPlan | strRequestXML = " +
		// strRequestXML );

		boolean bRef = false;
		try {
			// 获取请求XML

			// 一级订单号
			String strL1OrderId = getElementText(strRequestXML,
					"//OrderRSDataPlan/L1OrderId");
			// 二级订单号
			String strL2OrderId = getElementText(strRequestXML,
					"//OrderRSDataPlan/L2OrderId");
			// 订单类型：Retrieval/Fusion/Assimilation，共性产品/融合/同化产品
			String strOrderType = getElementText(strRequestXML,
					"//OrderRSDataPlan/OrderType"); // @@@ 需要转化成L2CP,L2FP,L2AP
			// 数据状态，Available/NotAvailable/Future （数据可用/缺数据/未来数据）
			String strDataStatus = getElementText(strRequestXML,
					"//OrderRSDataPlan/DataStatus");
			// 数据列表
			// String strDataList =
			// "Name=hb,url=ftp://xxx,Rows=38.4886,Samples=117.604;"; //@@@
			ArrayList<String> strDataList = getDataList(strRequestXML,
					"//OrderRSDataPlan/Datas");

			// 更新数据状态及数据列表
			bRef = orderStudio.setDataStatus(strL1OrderId, strDataStatus,
					strDataList);

			logger.info(strL1OrderId + "更新数据状态:" + bRef);

			// 初始化生产确认状态
			bRef &= orderStudio.setConfirmationStatus(strL1OrderId, -1, false); // @@@
																				// 应该要在OrderStudio中创建一个表来记录、映射数据库中所有的订单，这样就可以让生产确认状态不可逆，或者在只记录有哪些订单已获得生产确认状态也行。
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 生成（响应）结果XML
		String strCode;
		String strInfo;
		if (bRef) {
			strCode = "100";
			strInfo = "成功";
		} else {
			strCode = "XXX";
			strInfo = "失败";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo
				+ "</info>" + "</feedback></root>";

		return strResponseXML;
	}

	/*
	 * 方法名称：订单状态反馈接口（共性/同化/融合/辐射归一化/几何归一化订单） 参数 ：1.
	 * strRequestXML：订单状态（子订单执行状态：完成、失败） ： 返回值 ：消息接收成功 / 失败 描述
	 * ：数据中心的子订单运行代理模块向本系统主中心的系统运行管理模块，反馈共性/同化/融合/ ：辐射归一化/几何归一化订单的完成状态
	 */
	public java.lang.String taskStatus(java.lang.String strRequestXML)
			throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><TaskStatus><feedback>";

		// test
		System.out
				.println("======================= taskStatus | strRequestXML = "
						+ strRequestXML);

		boolean bRef = false;
		try {
			// 获取请求XML

			// 一级订单号
			String strL1OrderId = getElementText(strRequestXML,
					"//TaskStatus/L1OrderId");
			// 二级订单号
			String strL2OrderId = getElementText(strRequestXML,
					"//TaskStatus/L2OrderId");
			// 三级订单号
			String strL3OrderId = getElementText(strRequestXML,
					"//TaskStatus/L3OrderId");
			// 订单类型：RadNormalize/GeoNormalize/Fusion/Assimilation，共性产品/融合/同化产品
			String strOrderType = getElementText(strRequestXML,
					"//TaskStatus/OrderType");
			// 反馈内容描述
			String strStatus = getElementText(strRequestXML,
					"//TaskStatus/Status");
			String strStatusDescription = getElementText(strRequestXML,
					"//TaskStatus/StatusDescription");
			// 时间
//			String strTime = getElementText(strRequestXML, "//TaskStatus/time");

			// 更新三级订单状态：Finish/Error

			// 增加四级订单后，此处逻辑需要更改:如果是RN、GN订单，状态更新位置为四级订单库，其他订单更改原来的的三级订单库
			// 如果是RN、GN的订单，更新三级订单库:根据订单类型和“@”符号判断
			if ((strL3OrderId.contains("RN") || strL3OrderId.contains("GN")|| strL3OrderId.contains("DS"))
					&& strL3OrderId.contains("@")) {
				bRef = orderStudio.setL3SubOrderWorkingStatus(strL3OrderId,
						strStatus);
			} else {
				bRef = orderStudio.setL3OrderWorkingStatus(strL3OrderId,
						strStatus);
			}

			// 更新二级订单状态 目前在WorkflowSchedular中更新
			// bRef &= orderStudio.setL2OrderProductStatus( strL2OrderId,
			// strOrderType ); //@@@ 订单类型要与主中心的定义统一

			// 判断订单是否完成

			// 向课题三反馈订单状态
			// ---ServiceInterface.IOrderStatusFeedback()

			// 向课题三反馈数据产品信息
			// ---SerivceInterface.IProductSubmit()
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 生成（响应）结果XML
		String strCode;
		String strInfo;
		if (bRef) {
			strCode = "100";
			strInfo = "成功";
		} else {
			strCode = "XXX";
			strInfo = "失败";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo
				+ "</info>" + "</feedback></TaskStatus>";

		return strResponseXML;
	}

	/*
	 * 方法名称：订单产品反馈接口（共性/同化/融合/辐射归一化/几何归一化订单） 参数 ：1.
	 * strRequestXML：数据产品信息（产品URL，ftp地址等信息） ： 返回值 ：消息接收成功 / 失败 描述
	 * ：数据中心的子订单执行代理模块向本系统主中心的系统运行管理模块，反馈共性/同化/融合/ ：辐射归一化/几何归一化子订单生产的数据产品信息
	 */
	public java.lang.String dataProductSubmit(java.lang.String strRequestXML)
			throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataProductSubmit><feedback>";

		// test
		System.out.println("dataProductSubmit | strRequestXML = "
				+ strRequestXML);

		boolean bRef = false;
		try {
			// 获取请求XML

			// 一级订单号
			String strL1OrderId = getElementText(strRequestXML,
					"//DataProductSubmit/L1OrderId");
			// 二级订单号
			String strL2OrderId = getElementText(strRequestXML,
					"//DataProductSubmit/L2OrderId");
			// 三级订单号
			String strL3OrderId = getElementText(strRequestXML,
					"//DataProductSubmit/L3OrderId");
			// //FTP地址
			// String strUrl = getElementText( strRequestXML,
			// "//DataProductSubmit/url" );
			// //用户名
			// String strUserName = getElementText( strRequestXML,
			// "//DataProductSubmit/username" );
			// //密码
			// String strPassword = getElementText( strRequestXML,
			// "//DataProductSubmit/password" );
			//
			// String strData = strUrl + "," + strUserName + "," + strPassword;
			// ArrayList< String > strDataProductList = new ArrayList< String
			// >();
			// strDataProductList.add( strData );
			ArrayList<String> strDataProductList = getDataList(strRequestXML,
					"//DataProductSubmit/Datas"); // @@@
													// 数据产品里是否要包含FTP用户名和密码，在数据可行方案里边就没有。。？

			// 设置三级订单产品列表
			// bRef = orderStudio.setL3OrderDataProductList( strL3OrderId,
			// strDataProductList );

			// 增加四级订单后，此处逻辑需要更改:如果是RN、GN订单，状态更新位置为四级订单库，其他订单更改原来的的三级订单库
			// 如果是RN、GN的订单，更新三级订单库:根据订单类型和“@”符号判断，追加数据
			if ((strL3OrderId.contains("RN") || strL3OrderId.contains("GN")|| strL3OrderId.contains("DS"))
					&& strL3OrderId.contains("@")) {
				bRef = orderStudio.setL3SubOrderDataProductList(strL3OrderId,
						strDataProductList);
			}
			// 共性产品订单反馈：需要提取产品数据列表
			else if (strL3OrderId.contains("CP")) {
				// ftp://124.16.184.69/StandardProductBuffer/L3CP201512140001/ProductLists.xml
				// /home.bak/MCA/IOServer-SSD/863-Daemon/MCAPublicBuffer/CommonProductBuffer/L3CP201512130001/
				// 根据FTP路径和缓存文件路径获取ProductLists的文件路径
				String productDataLists = getElementText(strRequestXML,
						"//DataProductSubmit/DataProductList");
				String commonProductsBasePath = SystemConfig
						.getCommonProductsPublicBufferLocalPath();
				String parts[] = commonProductsBasePath.split("/");
				String flagIndex = null;
				for (int i = parts.length - 1; i >= -1; i--) {
					if ((!parts[i].equals(""))
							&& productDataLists.contains(parts[i])) {
						flagIndex = parts[i];
						break;
					}
				}
				if (null == flagIndex) {
					logger.error("共性产品列表地址提取错误！\nFTP地址为：" + productDataLists
							+ "\n本地缓存地址为：" + commonProductsBasePath);
				}
				// 后缀
				String suffix = productDataLists
						.substring(productDataLists.indexOf(flagIndex)
								+ flagIndex.length(), productDataLists.length());
				// ProductLists文件绝对路径
				String productListsPath = commonProductsBasePath + suffix;
				logger.info(strL3OrderId + "订单共性产品列表文件为：" + productListsPath);
				String productsContext = readFileByLines(productListsPath);
				strDataProductList = getDataListAppendLocalPath(productsContext,
						"//Output/Datas",productListsPath);
				
				
				
				
				System.out.println(">>"+strDataProductList.size());
				
				bRef = orderStudio.setL3OrderDataProductList(strL3OrderId,
						strDataProductList);
			} else {
				// 获取ProductLists的位置：根据FTP链接及FTP位置确定
				bRef = orderStudio.setL3OrderDataProductList(strL3OrderId,
						strDataProductList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 生成（响应）结果XML
		String strCode;
		String strInfo;
		if (bRef) {
			strCode = "100";
			strInfo = "成功";
		} else {
			strCode = "XXX";
			strInfo = "失败";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo
				+ "</info>" + "</feedback></DataProductSubmit>";

		return strResponseXML;
	}

	/*
	 * 方法名称：真实性检验订单提交接口 参数 ：1. strRequestXML：请求XML ： 返回值 ：消息接收成功（生产任务单号） / 失败 描述
	 * ：真实性检验分系统向多中心协同处理平台提交共性产品真实性检验订单
	 */
	public java.lang.String validationOrderSubmit(java.lang.String strRequestXML)
			throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ValidationOrderSubmit><feedback>";

		OrderRequest orderRequest = new OrderRequest();
		L2ExternalOrder l2Order = new L2ExternalOrder();
		L2ExternalOrder l2OrderStorage = new L2ExternalOrder();

		boolean bRef = false;
		try {
			// 获取请求XML
			
			System.out.println("VD strXML:"+strRequestXML);
			
			//真实性检验系统生成的任务编号
			String strUserOrderID = getElementText(strRequestXML,
					"//ValidationOrderSubmit/Parameters/UserOrderID");
			String strSubProductID = getElementText(strRequestXML,
					"//ValidationOrderSubmit/Parameters/SubProductID");
			
			
			// 产品类型：真实性检验，共性产品
			String strProductType = getElementText(strRequestXML,
					"//ValidationOrderSubmit/Parameters/ProductType");
			// 产品名称
			String strProductName = getElementText(strRequestXML,
					"//ValidationOrderSubmit/Parameters/ProductName");

			// 单点的经纬度
			String latitude = getElementText(strRequestXML,
					"//ValidationOrderSubmit/Parameters/Latitude");
			String longitude = getElementText(strRequestXML,
					"//ValidationOrderSubmit/Parameters/Longtiude");			

			String strULLat = "";
			String strULLong = "";
			String strLRLat = "";
			String strLRLong = "";

			// 空间覆盖范围
			if (latitude != null && longitude != null) {
				strLRLat=(getValue(latitude)-1)+"";
				strULLat=(getValue(latitude))+1+"";
				strULLong=(getValue(longitude)-1)+"";
				strLRLong=(getValue(longitude)+1)+"";
			}

			// 成像时间
			String time= getElementText(strRequestXML,
					"//ValidationOrderSubmit/Parameters/Time");
			
			//补全时间2015001000000
			if (time.length()==7) {
				time=time+"000000";
			}
			
			java.util.Date midDate=new java.util.Date(ParseDateTime((time)).getTime());
			
			//开始时间
			String strStartDate =DateHandler.getSpecifiedDayBefore(midDate.toLocaleString());
			// 结束时间
			String strEndDate =DateHandler.getSpecifiedDayAfter(midDate.toLocaleString());
			String strCoverscope = strULLat + "," + strULLong + "," + strLRLat
					+ "," + strLRLong;

			orderRequest.orderType = "L2VD";
			orderRequest.jobId_L1 = "";
			orderRequest.orderParameter=strUserOrderID+";"+strSubProductID;
			
			orderRequest.priority = 1;
			orderRequest.orderLevel = "2";
			orderRequest.productName = strProductName;
			orderRequest.geoCoverageStr = strCoverscope;
			orderRequest.startDate = Date.valueOf(strStartDate);
			orderRequest.endDate = Date.valueOf(strEndDate);
			orderRequest.submitDate=new java.util.Date(System.currentTimeMillis());
			
			orderRequest.jobId = OrderRequest
					.generateId(orderRequest.orderType);

//			 bRef = OrderRequest.addOrder( orderRequest );
			bRef = OrderRequest.updateOrder(orderRequest);

			l2Order.orderType = orderRequest.orderType;
			l2Order.jobId_L1 = orderRequest.jobId_L1;
			l2Order.jobId = "";
			l2Order.jobId_P3L2=orderRequest.jobId;
			l2Order.priority = orderRequest.priority;
			l2Order.orderLevel = orderRequest.orderLevel;
			l2Order.productName = orderRequest.productName;
			l2Order.geoCoverageStr = orderRequest.geoCoverageStr;
			l2Order.startDate = orderRequest.startDate;
			l2Order.endDate = orderRequest.endDate;
			l2Order.orderParameter=orderRequest.orderParameter;
			l2OrderStorage = this.orderStudio.addOrder(l2Order);

			L2ExternalOrder.orderRequestL2OrderBind(orderRequest.jobId,
					l2OrderStorage.jobId);

		} catch (Exception e) {
			if ((null != orderRequest.jobId) && (!orderRequest.jobId.isEmpty())) {
				OrderRequest.delete(orderRequest);

				if ((null != l2Order.jobId) && (!l2Order.jobId.isEmpty())) {
					L2ExternalOrder.delete(l2Order);
				}
			}

			e.printStackTrace();
		}

		// 生成（响应）结果XML
		String strCode;
		String strInfo;
		if (bRef) {
			strCode = "100";
			strInfo = "Success";
		} else {
			strCode = "XXX";
			strInfo = "Failure";
		}
		strResponseXML += "<ValidationOrderId>" + l2OrderStorage.jobId
				+ "</ValidationOrderId>" + "<code>" + strCode + "</code>"
				+ "<info>" + strInfo + "</info>"
				+ "</feedback></ValidationOrderSubmit>";
		
		System.out.println(strResponseXML);

		return strResponseXML;
	}

	private String getElementText(String strXML, String strElementPath)
			throws DocumentException {
		try {
			Document dom = DocumentHelper.parseText(strXML);
			return dom.selectSingleNode(strElementPath).getText();
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out
					.println("<Error>ServiceImplSoapBindingImpl::getElementText | XML参数匹配不成功，请检查接口是否符合规范！"+strElementPath);
			return "";
		}
	}
	
	//补全时间满13位
	public static String toSizeString(String s, int size) {
	    StringBuilder buf = new StringBuilder(s);
	    buf.setLength(size);
	    return buf.toString();
	}
	
	//时间转换：一年的多少天转换为日期
	public java.util.Date ParseDateTime(String yyyydayofyearhhmmss){	
		int year = Integer.parseInt(yyyydayofyearhhmmss.substring(0, 4))-1900;//将字符串转化为等效int类型
		int dayOfYear = Integer.parseInt(yyyydayofyearhhmmss.substring(4, 7));
		int month = DateHandler.ParseMonthFromDayOfYear(year, dayOfYear)-1;
		int day = DateHandler.ParseDayFromDayOfYear(year, dayOfYear);
		int hour = Integer.parseInt(yyyydayofyearhhmmss.substring(7, 9));
		int minute = Integer.parseInt(yyyydayofyearhhmmss.substring(9, 11));
		int second = Integer.parseInt(yyyydayofyearhhmmss.substring(11, 13));
		return new java.util.Date(year, month, day, hour, minute, second);
	}

	//获取数值
	public double getValue(String valueString) {
		// System.out.println(valueString);
		double value = 0.000;
		if (valueString == null || valueString.equals("")) {
			return value;
		}
		try {
			value = Double.parseDouble(valueString);
			return value;
		} catch (Exception e) {
			System.out.print(valueString);
			System.out.println("获取" + valueString + "的double值失败！");
			return value;
		}

	}

	// 读取文本内容到字符串
	public String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		try {
			// System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				builder.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return builder.toString();
	}

	private ArrayList<String> getDataList(String strXML, String strElementPath)
			throws DocumentException {
		ArrayList<String> dataList = new ArrayList<String>();

		try {
			Document dom = DocumentHelper.parseText(strXML);

			List list = dom.selectNodes(strElementPath);

			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				List list_datas = ((Element) iter.next()).elements();
				Iterator iter_datas = list_datas.iterator();
				while (iter_datas.hasNext()) {
					String strData = "";
					Element element_data = (Element) iter_datas.next();
					List list_data = element_data.elements();

					// System.out.println( "name:" + element_data.getName() );
					// System.out.println( "id  :" +
					// element_data.attributeValue( "id" ) );
					// System.out.println( "name:" +
					// element_data.attributeValue( "Name" ) );

					strData += "Name=" + element_data.attributeValue("Name");

					Iterator iter_dataelement = list_data.iterator();
					while (iter_dataelement.hasNext()) {

						strData += ",";

						Element element_dataelement = (Element) iter_dataelement
								.next();

						// System.out.println( element_dataelement.getName() +
						// "=" + element_dataelement.getStringValue() );

						strData += element_dataelement.getName() + "="
								+ element_dataelement.getStringValue();
					}

					// System.out.println( ">>>>RSData Plan:"+strData );

					dataList.add(strData);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out
					.println("<Error>ServiceImplSoapBindingImpl::getDataList | Element path is invalid.");
			return new ArrayList<String>();
		}

		return dataList;
	}
	
	//追加本地路径
	private ArrayList<String> getDataListAppendLocalPath(String strXML, String strElementPath,String localPath)
			throws DocumentException {
		ArrayList<String> dataList = new ArrayList<String>();

		try {
			Document dom = DocumentHelper.parseText(strXML);

			List list = dom.selectNodes(strElementPath);

			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				List list_datas = ((Element) iter.next()).elements();
				Iterator iter_datas = list_datas.iterator();
				while (iter_datas.hasNext()) {
					String strData = "";
					Element element_data = (Element) iter_datas.next();
					List list_data = element_data.elements();

					// System.out.println( "name:" + element_data.getName() );
					// System.out.println( "id  :" +
					// element_data.attributeValue( "id" ) );
					// System.out.println( "name:" +
					// element_data.attributeValue( "Name" ) );

					strData += "Name=" + element_data.attributeValue("Name");
					
					strData+=",url="+localPath+"/"+element_data.attributeValue("Name");

					Iterator iter_dataelement = list_data.iterator();
					while (iter_dataelement.hasNext()) {

						strData += ",";

						Element element_dataelement = (Element) iter_dataelement
								.next();

						// System.out.println( element_dataelement.getName() +
						// "=" + element_dataelement.getStringValue() );

						strData += element_dataelement.getName() + "="
								+ element_dataelement.getStringValue();
						
					}

					// System.out.println( ">>>>RSData Plan:"+strData );

					dataList.add(strData);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out
					.println("<Error>ServiceImplSoapBindingImpl::getDataList | Element path is invalid.");
			return new ArrayList<String>();
		}

		return dataList;
	}
	

	// 获取系统监控信息
	@Override
	public String systemMonitorInfo(String strRequestXML)
			throws RemoteException {
		// 匹配请求系统IP
		String strRequestHostName = getElementText(strRequestXML,
				"//RequestInfo/HostName");
		String strRequestHostIP = getElementText(strRequestXML,
				"//RequestInfo/HostIP");
		logger.info(strRequestHostName + "系统：" + strRequestHostIP + "获取系统监控信息！");

		SystemResource systemResource = new SystemResource();

		String strResult = systemResource.getSystemMonitorInfo();
		System.out.println("\n\n" + strResult);
		return strResult;
	}

	@Override
	public String rnDataService(String strRequestXML, String serviceType)
			throws RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" endcoding=\"UTF-8\">";
		switch (serviceType) {
		case "ImageThumb": {
			String strDataID = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/DataID");
			String strUrl = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/Url");
			String strLocalpath = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/Localpath");
			strResponseXML += "<ImageCaculateResult><Datas><Data id=\"1\" dataid=\"758129563\"><createtime>"
					+ "2009-01-01 00:00:00"
					+ "</createttime><spacecraft>"
					+ "TERRA"
					+ "</spacecraft><sensor>"
					+ "MODIS"
					+ "</sensor><resolution>"
					+ "1000000"
					+ "</resolution><ullat>"
					+ "15.8529"
					+ "</ullat><ullon>"
					+ "123.914"
					+ "</ullon><urlat>"
					+ "15.8529"
					+ "</urlat><urlon>"
					+ "145.295"
					+ "</urlon><lllat>"
					+ "-2.14878"
					+ "</lllat><lllon>"
					+ "120.486"
					+ "</lllon><lrlat>"
					+ "-5.16073"
					+ "</lrlat><lrlon>"
					+ "141.262"
					+ "</lrlon><format>"
					+ "HDF-EOS"
					+ "</format><datasize>"
					+ "27112383"
					+ "</datasize><level>"
					+ "L2"
					+ "</level><provider>"
					+ "NASA"
					+ "</provider><cloudcover>"
					+ "85"
					+ "</cloudcover><url>"
					+ "http://10.3.10.28/filepath/filename.jpeg"
					+ "</url><LocalPath>"
					+ "/mnt/hgfs/data/filename3"
					+ "</LocatlPath></Data><Data id=\"1\" dataid=\"758129563\"><createtime>"
					+ "2009-01-01 00:00:00"
					+ "</createttime><spacecraft>"
					+ "TERRA"
					+ "</spacecraft><sensor>"
					+ "MODIS"
					+ "</sensor><resolution>"
					+ "1000000"
					+ "</resolution><ullat>"
					+ "15.8529"
					+ "</ullat><ullon>"
					+ "123.914"
					+ "</ullon><urlat>"
					+ "15.8529"
					+ "</urlat><urlon>"
					+ "145.295"
					+ "</urlon><lllat>"
					+ "-2.14878"
					+ "</lllat><lllon>"
					+ "120.486"
					+ "</lllon><lrlat>"
					+ "-5.16073"
					+ "</lrlat><lrlon>"
					+ "141.262"
					+ "</lrlon><format>"
					+ "HDF-EOS"
					+ "</format><datasize>"
					+ "27112383"
					+ "</datasize><level>"
					+ "L2"
					+ "</level><provider>"
					+ "NASA"
					+ "</provider><cloudcover>"
					+ "85"
					+ "</cloudcover><url>"
					+ strUrl
					+ "</url><LocalPath>"
					+ strLocalpath
					+ "</LocatlPath></Data></Datas></ImageCaculateResult>";
		}
			break;
		case "ImageStatistic": {
			String strDataID = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/DataID");
			String strUrl = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/Url");
			String strRoi = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/Roi");
			String strLocalpath = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/Localpath");
			strResponseXML += "<ImageStatisticResult><DataID>"
					+ strDataID
					+ "</DataID><Url>"
					+ strUrl
					+ "</Url><Roi>"
					+ strRoi
					+ "</Roi><Localpath>"
					+ strLocalpath
					+ "</Localpath><Result>"
					+ "Band Min Max Avg Std 1 0 23.4 12.1 0.6 2 0 25.4 15.1 0.8 3 0 25.4 15.1 0.8"
					+ "<Result/></ImageStatisticResult>";
		}
			break;
		case "ImageCaculate": {
			String strUrl = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/Url");
			String strLocalpath = getElementText(strRequestXML,
					"//ImageStatistic/Conditions/Localpath");
			strResponseXML += "<ImageCaculateResult><Datas><Data id=\"1\"><url>"
					+ strUrl
					+ "</url><LocalPath>"
					+ strLocalpath
					+ "</LocatlPath></Data></Datas></ImageCaculateResult>";
		}
		default: {
			System.out.println("Input parameters are incorrect");
		}
			break;
		}

		return strResponseXML;
	}

	@Override
	public String faProducRequireSubmit(String strRequestXML)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String dataPrdouctQuery(String strRequestXML) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
