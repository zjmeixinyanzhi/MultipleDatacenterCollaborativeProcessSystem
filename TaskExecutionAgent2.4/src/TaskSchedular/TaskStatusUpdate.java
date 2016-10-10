package TaskSchedular;

import java.rmi.RemoteException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import DBManage.L3OrderDB;
import DBManage.PBSOrderDB;
import DBManage.TestDBConnection;
import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import Pbs.PbsOrder;
import RSDataManage.RSData;
import RSDataManage.RSProductData;
import ServiceInterface.ServiceImplProxy;
import TaskExeAgent.SystemConfig;
import TaskExeAgent.SystemLogger;

/**
 * 创建时间：2015-11-22 上午10:05:07 项目名称：TaskExecutionAgent 2015-11-22
 * 
 * @author 张杰
 * @version 1.0 文件名称：TaskStatusUpdate.java 类说明：任务状态反馈，主要向主中心反馈订单状态及产品列表
 */
public class TaskStatusUpdate {

	// 当前订单
	public L3InternalOrder l3Order;
	// 状态反馈的主中心Webservice
	private ServiceImplProxy proxy;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public TaskStatusUpdate(L3InternalOrder l3order) {
		this.l3Order = l3order;
		// 获取主中心的反馈IP
		this.proxy= new ServiceImplProxy(SystemConfig
				.getServerConfig().getMcaWebserviceUrl()); // 服务器代理设置
	}

	// 更新
	public boolean feedbackProductlists() { //

		
			// @@@ 共性产品不是向独立的共性产品生产分系统提交么？应该是分系统把报告附上？
			// 共性产品需要真实性检验报告
			if ("L3CP".equals(l3Order.orderType)) {
				// 向真实性检验分系统提交真实性检验报告查询请求 P194
				// @@@ ValidationReportRequest
				String strProductType;
				switch (l3Order.orderType) {
				case "L3CP":
					strProductType = "Retrieval";
					break;
				case "L3FP":
					strProductType = "Fusion";
					break;
				case "L3AP":
					strProductType = "Assumilation";
					break;
				default:
					strProductType = "";
				}
				String strULLat = "";
				String strULLong = "";
				String strLRLat = "";
				String strLRLong = "";
				String strStartDate = "";
				String strEndDate = "";
				String strValidationReportRequestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<ValidationReportRequest>"
						+ "<Parameters>"
						+ "<ProductType>"
						+ strProductType
						+ "</ProductType>"
						+ "<ProductName>"
						+ l3Order.productName
						+ "</ProductName>"
						+ "<ULLat>"
						+ strULLat
						+ "</ULLat>"
						+ "<ULLong>"
						+ strULLong
						+ "</ULLong>"
						+ "<LRLat>"
						+ strLRLat
						+ "</LRLat>"
						+ "<LRLong>"
						+ strLRLong
						+ "</LRLong>"
						+ "<StartDate>"
						+ strStartDate
						+ "</StartDate>"
						+ "<EndDate>"
						+ strEndDate
						+ "</EndDate>"
						+ "</Parameters>"
						+ "</ValidationReportRequest>";

				String strValidationReportXML = "";

				String strReportName;
				String strURL;
				String strVRUserName;
				String strVRPassword;
				try {
					strReportName = getElementText(strValidationReportXML,
							"//ValidationReportRequest/feedback/ReportName");
					strURL = getElementText(strValidationReportXML,
							"//ValidationReportRequest/feedback/url");
					strVRUserName = getElementText(strValidationReportXML,
							"//ValidationReportRequest/feedback/username");
					strVRPassword = getElementText(strValidationReportXML,
							"//ValidationReportRequest/feedback/password");
				} catch (DocumentException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					logger.error("解析真实性检验订单XML出错！\n" + e1);
				}

				// @@@ 通过URL获取真实性检验报告？如何传递？就放到数据产品的文件夹下？
			}

			// 数据产品上传缓存区 产品上传已经独立为主中心的三级订单
			// executer.uploadToBuffer();

			// 向主中心提交数据产品
			// 调用ServiceInterface
			// IDataProductSubmit( executer.getProduct())
			try {
				// String strUrl = "ftp://XXXXXXXXX"; //@@@
				// 数据的FTP在哪里生成？executer.getProduct()
				// String strUserName = "username";
				// String strPassword = "password";
				String strProductRequestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<DataProductSubmit>"
						+ "<L1OrderId>"
						+ l3Order.jobId_L1
						+ "</L1OrderId>"
						+ "<L2OrderId>"
						+ l3Order.jobId_L2
						+ "</L2OrderId>"
						+ "<L3OrderId>"
						+ l3Order.jobId
						+ "</L3OrderId>"
						+
						// "<url>" + strUrl + "</url>" +
						// "<username>" + strUserName + "</username>" +
						// "<password>" + strPassword + "</password>" +
						"<Datas>"
						+ "<Data id=\"1\" Name=\"hb\"><url>ftp://XXXXXX</url><username>username</username><password>password</password><Rows>38.4886</Rows><Samples>117.604</Samples></Data>"
						+ "<Data id=\"2\" Name=\"bj\"><url>ftp://XXXXXX</url><username>username</username><password>password</password><Rows>38.4886</Rows><Samples>117.604</Samples></Data>"
						+ "</Datas>" + "</DataProductSubmit>";

				// 经过几何、辐射校正之后的标准产品参数， 参数固定
				if ("L3GN".equals(l3Order.orderType)
						|| "L3RN".equals(l3Order.orderType)
						|| "L3DS".equals(l3Order.orderType)||"L3FP".equals(l3Order.orderType)
						|| "L3AP".equals(l3Order.orderType)) {

					strProductRequestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
							+ "<DataProductSubmit>"
							+ "<L1OrderId>"
							+ l3Order.jobId_L1
							+ "</L1OrderId>"
							+ "<L2OrderId>"
							+ l3Order.jobId_L2
							+ "</L2OrderId>"
							+ "<L3OrderId>"
							+ l3Order.jobId + "</L3OrderId>" + "<Datas>";

					OrderStudio orderStudio = new OrderStudio();
					PBSOrderDB pbsOrderDB = new PBSOrderDB();

					// PBS订单
					Iterator<String> iterator_pbsOrders = this.l3Order.pbsOrderLists
							.iterator();

					// 产品数据个数
					int count = 0;

					while (iterator_pbsOrders.hasNext()) {
						String pbsId = (String) iterator_pbsOrders.next();
						String condition = " where PBSId='" + pbsId + "'";
						ArrayList<PbsOrder> cur_PbsOrders = pbsOrderDB
								.search(condition);
						if (cur_PbsOrders == null) {
							continue;
						}
						PbsOrder pbsOrder =new PbsOrder();
						try {
							 pbsOrder = cur_PbsOrders.get(0);
							
						} catch (IndexOutOfBoundsException eo) {
							logger.info("未找到该PBS订单"+pbsId);
							continue;
						}
						
//						System.out.println(pbsOrder.getWorkingStatus());

						// 处理失败的数据
						if (!pbsOrder.getWorkingStatus().equals("Finish")) {
							strProductRequestXML += "<Data id=\"" + (++count)
									+ "\" Name=\"" + pbsOrder.getDataName()
									+ "\">";
							strProductRequestXML += "<datastatus>"
									+ pbsOrder.getWorkingStatus()
									+ "</datastatus>" + "<dataid>"
									+ pbsOrder.getDataid() + "</dataid>"
									+ "</Data>";
							this.logger.info(pbsOrder.getDataName() + "未处理成功！");
							continue;
						}

						// 分割产品列表
						String strProducts = pbsOrder.getDataProductList();
						// 数据列表为空时
						if (strProducts == null || strProducts.equals("")) {
							strProductRequestXML += "<Data id=\"" + (++count)
									+ "\" Name=\"" + pbsOrder.getDataName()
									+ "\">";
							strProductRequestXML += "<datastatus>"
									+ pbsOrder.getWorkingStatus()
									+ "</datastatus>" + "<dataid>"
									+ pbsOrder.getDataid() + "</dataid>"
									+ "</Data>";
							this.logger
									.info(pbsOrder.getDataName() + "产品列表为空！");
							continue;
						} else {
							String[] products = strProducts.split(";");
							// 遍历多个产品数据，一般RN GN为一个产品 DS为多个产品
							for (int i = 0; i < products.length; i++) {
								RSProductData rsProductData = new RSProductData(
										products[i]);
								strProductRequestXML += "<Data id=\""
										+ (++count) + "\" Name=\""
										+ rsProductData.getFilename() + "\">"
										+ "<datastatus>"
										+ (pbsOrder.getWorkingStatus())
										+ "</datastatus>" + "<dataid>"
										+ pbsOrder.getDataid() + "</dataid>";
								
								
								strProductRequestXML += "<originName>"
										+ pbsOrder.getDataName()
										+ "</originName>"
										+ "<url>"
										+ SystemConfig.getServerConfig()
												.getIp()
										+ ":"
										+ rsProductData.getDataUrl()
										+ "</url>"
										+ "<ullat>"
										+ rsProductData.getULLat()
										+ "</ullat>"
										+ "<ullon>"
										+ rsProductData.getULLong()
										+ "</ullon>"
										+ "<lrlat>"
										+ rsProductData.getLRLat()
										+ "</lrlat>"
										+ "<lrlon>"
										+ rsProductData.getLRLong()
										+ "</lrlon>"
										+ "</Data>";
							}
						}
					}
					strProductRequestXML += "</Datas>" + "</DataProductSubmit>";
				}
				System.out.println(strProductRequestXML);
				String strRet = proxy.dataProductSubmit(strProductRequestXML);
			} catch (RemoteException e) {
				e.printStackTrace();
				return false;
			}
		return true;
	
	}
	
	public boolean feedbackStatus(String strOrderStatus) {
		
		// 向主中心更新任务状态
		// 调用ServiceInterface
		// ITaskStatus( strOrderStatus )
		// @@@ StatusDescription暂时用状态来代替
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00"); // "2014-10-16 12:10:07"
		format.setLenient(false);
		java.util.Date now_time = new Date(System.currentTimeMillis());
		String strTime = format.format(now_time);

		String strRequestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<TaskStatus>" + "<L1OrderId>" + l3Order.jobId_L1
				+ "</L1OrderId>" + "<L2OrderId>" + l3Order.jobId_L2
				+ "</L2OrderId>" + "<L3OrderId>" + l3Order.jobId
				+ "</L3OrderId>" + "<OrderType>" + l3Order.orderType
				+ "</OrderType>" + "<Status>" + strOrderStatus + "</Status>"
				+ "<StatusDescription>" + strOrderStatus
				+ "</StatusDescription>" + "<time>" + strTime + "</time>"
				+ "</TaskStatus>";
		try {
			logger.info("向主中心反馈订单" + l3Order.jobId + "执行状态！");
			String strRet = proxy.taskStatus(strRequestXML);
		} catch (RemoteException e) {
			e.printStackTrace();
			logger.error("向主中心反馈订单" + l3Order.jobId + "执行状态WebService出错！\n" + e);
		}

		// this.finished_L3OrderList.add( l3Order );

		return true;
		
	}
	

	private String getElementText(String strXML, String strElementPath)
			throws DocumentException {
		try {
			Document dom = DocumentHelper.parseText(strXML);
			return dom.selectSingleNode(strElementPath).getText();
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.error("<Error>ServiceImplSoapBindingImpl::getElementText | Element path is invalid.");
			return "";
		}
	}

	public static void main(String[] args) {
		TestDBConnection test=new TestDBConnection();
		test.GetConnection();
		
		L3OrderDB l3OrderDB=new L3OrderDB();
		L3InternalOrder l3InternalOrder=l3OrderDB.search(" JobId='L3GN201603210002@IOServer-SSD'").get(0);
		
		TaskStatusUpdate taskStatusUpdate=new TaskStatusUpdate(l3InternalOrder);
		taskStatusUpdate.feedbackProductlists();
		
		
		

	}

}
