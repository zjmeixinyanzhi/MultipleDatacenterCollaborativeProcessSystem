package DataService;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DBManage.DBConn;
import DBManage.L2OrderDB;
import DBManage.RsDataCacheDB;
import DBManage.SystemConfigDB;
import DBManage.TestDBConnection;
import Download.HttpDownload;
import FileOperation.FileOperation;
import FileOperation.TimeConsumeCount;
import SystemManage.SystemLogger;
import RSDataManage.RSData;
import ServiceInterface.WFDataServerProxy;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sun.xml.rpc.processor.model.Operation;

/**
 * 创建时间：2016-1-19 上午10:21:20 项目名称：DataQueryAcquireService_1.1 2016-1-19
 * 
 * @author 张杰
 * @version 1.0 文件名称：DataAcquire.java 类说明：
 */
public class DataAcquire {
	// 查询条件
	public String testQueryRequest;
	// 数据服务Url
	public String dataServiceURL;
	// 数据缓存库
	public RsDataCacheDB rsDataCacheDB = new RsDataCacheDB();
	// 目的地址
	public String destPath;

	// 数据服务客户端
	public WFDataServerProxy proxy;
	// 查询反馈待准备的数据列表:内部类
	public FeedbackDataList feedbackDatalists = new FeedbackDataList();
	// 数据结果列表
	public ArrayList<RSData> resultDataLists = new ArrayList<RSData>();
	// 未获取数据的dataId列表:
	public ArrayList<String> prepareingDatalists = new ArrayList<String>();
	// 错误数据dataId列表:
	public ArrayList<String> unAvaliableDatalists = new ArrayList<String>();
	// 错误类型
	public String errorFlag = "";
	// 元数据返回条目限制个数：避免一次性取回过多的数据
	public int countLimit = 10;
	// 时间统计
	public TimeConsumeCount timeConsumeCount;
	// 文件操作
	public String recordFile = "";
	public FileOperation operation;

	// 日志操作
	public Logger logger = SystemLogger.getSysLogger();

	// 无参构造函数
	public DataAcquire() {
		this.timeConsumeCount = new TimeConsumeCount();
		this.operation = new FileOperation();
	}

	// 构造
	public DataAcquire(String queryCondition, String webserviceUrl,
			String destPath) {
		this.testQueryRequest = queryCondition;
		this.dataServiceURL = webserviceUrl;
		this.destPath = destPath;
		this.timeConsumeCount = new TimeConsumeCount();
		this.operation = new FileOperation();
	}

	// 获取基本元数据信息
	public boolean getBaseMetaInfos() {
		this.proxy = new WFDataServerProxy(dataServiceURL);

		// 1、开始查询startQuery
		String result = "";
		String queryId;
		try {
			result = proxy.startQuery(testQueryRequest);

		} catch (RemoteException e1) {
			errorFlag = "Webservice";
			logger.error("获取QueryID失败！请检查Webservice是否可以联通！");
			e1.printStackTrace();
			return false;
		}
		try {
			queryId = getElementText(result, "//DataQuery/Feedback/QueryId");
		} catch (DocumentException e) {
			errorFlag = "XMLParsing";
			logger.error("获取QueryID失败！请检查返回值是否符合规范！");
			e.printStackTrace();
			return false;
		}

		if (null == queryId || queryId.equals("")) {
			logger.error("返回的请求Id为空！");
			errorFlag = "Webservice";
			return false;
		}

		// 2、数据获取结果：getDescription
		// System.out.println(queryId);

		int countTemp = 0;
		// 关闭查询请求参数
		String closeQueryRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><CloseQuery><QueryId>"
				+ queryId + "</QueryId></CloseQuery>";

		// 获取数据详细信息的请求参数

		String getDataDescriptionRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataDescription><Conditions><QueryId>"
				+ queryId
				+ "</QueryId><Size>"
				+ this.countLimit
				+ "</Size></Conditions></DataDescription>";

		ArrayList<String> dataLists = new ArrayList<String>();
		//
		int count = 0;
		// 按照最大结果限制数目依次取回元数据信息

		do {
			dataLists = null;
			String descripts;
			try {
				descripts = proxy.getDataDescription(getDataDescriptionRequest);
				// 提取结果放入dataLists中
				// System.out.println(descripts);

				try {
					dataLists = getDataList(descripts, "//result_list");
				} catch (DocumentException e) {
					errorFlag = "XMLParsing";
					logger.error("获取getDataDescription失败！请检查返回值是否符合规范！");
					e.printStackTrace();
					return false;
				}
				//
				this.feedbackDatalists.addData(dataLists);
			} catch (RemoteException e) {
				errorFlag = "Webservice";
				logger.error("获取结果getDataDescription失败！请检查Webservice是否可以联通！");
				e.printStackTrace();
				// 关闭连接
				try {
					logger.info("关闭查询closeQuery失败");
					proxy.closeQuery(closeQueryRequest);
				} catch (RemoteException e1) {
					logger.error("关闭查询closeQuery失败！");
					e1.printStackTrace();
				}
				return false;
			}

			// 记录客户端获取元数据信息的总数据
			countTemp += dataLists.size();
			// System.out.println("当前数据个数：" + dataLists.size());
			// System.out.println("总的数据个数：" + countTemp + " ");

			count++;

		} while (dataLists.size() >= countLimit && count < 5);

		// 关闭查询连接
		try {
			logger.info("关闭查询连接过程中！");
			proxy.closeQuery(closeQueryRequest);
		} catch (RemoteException e) {
			logger.error("关闭查询closeQuery失败！");
			e.printStackTrace();
		}

		return true;
	}

	// 将获取的元数据信息更新到缓存表中
	public boolean insertDataListToCacheDB() {

		Iterator<RSData> iterator = this.feedbackDatalists.datalists.iterator();
		while (iterator.hasNext()) {
			RSData rsData = (RSData) iterator.next();
			rsDataCacheDB.addData2(rsData);
		}
		return true;
	}

	// 获取当前数据列表的数据量
	public long getDataListsValumeSizes(ArrayList<String> dataLists) {
		long sum = 0;
		Iterator<String> iterator = dataLists.iterator();
		while (iterator.hasNext()) {
			String dataid = iterator.next();
			System.out.println(dataid);
			ArrayList<RSData> rsdataLists = this.rsDataCacheDB
					.search(" Where dataid='" + dataid + "'");
			if (rsdataLists == null || rsdataLists.size() < 1) {
				logger.error(dataid + "数据未找到！");
				return sum;
			}
			RSData newDataFromDB = rsdataLists.get(0);
			if (newDataFromDB.datasize > 0) {
				sum += newDataFromDB.datasize;
			} else {
				logger.error(dataid + "数据大小未知！");
			}
		}
		return sum;
	}

	// 获取已经下载的数据列表
	public boolean removeHaveCachedDataLists() {
		Iterator<RSData> iterator = this.feedbackDatalists.datalists.iterator();

		// 原来数据个数
		int sum = this.feedbackDatalists.datalists.size();

		ArrayList<RSData> needRemovedDatalists = new ArrayList<RSData>();

		// 重复数据个数
		int count_Cached = 0;
		while (iterator.hasNext()) {
			RSData rsData = (RSData) iterator.next();
			String dataid = rsData.dataid;
			System.out.println(dataid);
			ArrayList<RSData> rsdataLists = this.rsDataCacheDB
					.search(" Where dataid='" + dataid + "'");
			if (rsdataLists == null || rsdataLists.size() < 1) {
				logger.error(dataid + "数据未找到！");
				continue;
			}
			RSData newDataFromDB = rsdataLists.get(0);
			if (newDataFromDB.datacenter != null
					&& newDataFromDB.dataStatus != null
					&& newDataFromDB.dataStatus.equals("Available")) {
				logger.info(dataid + "数据已存在，无需重复请求与下载！");
				// 找到feedbackDatalists.datalists对应的数据
				Iterator<RSData> it_feedbackDataList = this.feedbackDatalists.datalists
						.iterator();
				while (it_feedbackDataList.hasNext()) {
					RSData dataInFeedbackLists = (RSData) it_feedbackDataList
							.next();
					if (dataInFeedbackLists.dataid.equals(dataid)) {
						needRemovedDatalists.add(dataInFeedbackLists);
						count_Cached++;
					}
				}
			} else {
				logger.info(dataid + "数据状态未知，需要重新请求与下载！");
			}
		}
		if (this.feedbackDatalists.datalists.removeAll(needRemovedDatalists)) {
			logger.info(count_Cached + "个数据已经从原来" + sum + "个数据下载列表中移除！剩余"
					+ this.feedbackDatalists.datalists.size() + "个需下载的数据");
		}
		return true;
	}

	/*
	 * 首次获取文件名和下载链接信息
	 */
	public boolean getFirstAcquireInfos() {

		int errCount = 0;
		int finishCount = 0;

		Iterator<RSData> iterator = this.feedbackDatalists.datalists.iterator();
		System.out.println("总的数据个数：" + this.feedbackDatalists.datalists.size());
		while (iterator.hasNext()) {
			RSData rsData = (RSData) iterator.next();
			// System.out.println("数据ID：" + rsData.dataid);
			// 获取信息
			String obtainInfos = getAcquireInfo(rsData.dataid);
			// 解析数据状态与相应信息
			String dataStatus;
			String dataFileName = null;
			String dataUrl;
			// String localFilePath = "";
			// 下载后的目的文件
			// 加上主机名称
			String destFile = this.destPath;
			long datasize = 0;
			try {
				dataStatus = getElementText(obtainInfos, "//Data/DataStatus");
				dataUrl = getElementText(obtainInfos, "//Data/Url");
				System.out.println(rsData.dataid + " : " + dataStatus);
				System.out.println(dataUrl);
				// 已经有获取信息的数据
				if (dataStatus.equals("Available")&& dataUrl!="") {
					finishCount++;
					dataFileName = getElementText(obtainInfos,
							"//Data/FileName");

					destFile += dataFileName;
					// 数据下载
					// 启动http下载
					this.timeConsumeCount.setStartTimeByCurrentTime();

					if (dataUrl.startsWith("http")) {
						HttpDownload download = new HttpDownload();
						// 判断下下载格式:如果是zip格式，需要解压

						if (dataUrl.toUpperCase().endsWith("ZIP")
								&& (!destFile.endsWith(".zip"))) {
							destFile += ".zip";
						}
						// 暂时不用执行下载
						if (!download.httpDownload(dataUrl, destFile)) {
							logger.error(dataUrl + "数据下载失败！");
						}
					}
					// 启动FTP下载
					else if (dataUrl.startsWith("ftp")) {
						//
					}

					this.timeConsumeCount.setEndTimeByCurrentTime();
					// 获取文件大小和绝对路径
					FileOperation currDataOperation = new FileOperation();
					// /test
					// System.out.println("第一次调用获取大小！");
					datasize = currDataOperation.getFileSize(destFile);
					if (datasize == -1) {
						logger.error("获取文件" + destFile + "大小失败！");
					}
					// test
					// System.out.println(datasize + " " + destFile);
					// 更新获取信息

					// test
					// System.out.println(dataFileName+" "+dataUrl);
					if (dataFileName == null) {
						System.out.println("第一次文件名为空！");
					}

					rsDataCacheDB.updateDataAcquireInfos(rsData.dataid,
							dataFileName, dataStatus, dataUrl, datasize,
							"10.3.10.1:" + destFile);
					
//					operation.appendMethodB(recordFile, "Name=" + dataFileName
//							+ " ,Size= " + datasize + " ,DownLoadTimeConsume="
//							+ timeConsumeCount.getTimeSpan() + "\n");					
				}
				// 正在获取的数据
				else if (dataStatus.equals("Running")) {
					System.out.println(rsData.dataid + "数据正在准备！");
					prepareingDatalists.add(rsData.dataid);
				}
				// 获取出错的数据
				else {
					errCount++;
					unAvaliableDatalists.add(rsData.dataid);
					// 更新结果列表
					rsDataCacheDB.updateDataAcquireInfos(rsData.dataid, "",
							"NotAvailalbe", "", 0, "");
				}

			} catch (DocumentException e) {
				errorFlag = "XMLParsing";
				logger.error("获取getDataDescription失败！请检查返回值是否符合规范！");
				e.printStackTrace();
				return false;
			}
		}

		System.out.println("出错个数：" + errCount);
		System.out.println(this.unAvaliableDatalists.size());
		return true;
	}

	/*
	 * 轮询获取文件名和下载链接信息
	 */
	public boolean getPollingAcquireInfos() {
		int errCount = 0;
		int finishCount = 0;

		// 下一轮需要继续获取的数据列表
		ArrayList<String> nextPrepareingDatalists = new ArrayList<String>();

		Iterator<String> iterator = this.prepareingDatalists.iterator();
		System.out.println("正在准备的数据个数：" + this.prepareingDatalists.size());
		while (iterator.hasNext()) {
			String curr_dataId = (String) iterator.next();
			// 获取信息
			String obtainInfos = getAcquireInfo(curr_dataId);
			// 解析数据状态与相应信息
			String dataStatus;
			String dataFileName;
			String dataUrl;
			// 下载后的目的文件
			String destFile = this.destPath;
			long datasize = 0;
			try {
				dataStatus = getElementText(obtainInfos, "//Data/DataStatus");
				dataUrl = getElementText(obtainInfos, "//Data/Url");
				// 已经有获取信息的数据
				if (dataStatus.equals("Available") && dataUrl!="") {
					finishCount++;
					dataFileName = getElementText(obtainInfos,
							"//Data/FileName");
					dataUrl = getElementText(obtainInfos, "//Data/Url");
					

					destFile = this.destPath + dataFileName;
					// 数据下载

					// 下载是否成功？
					boolean flag = false;
					// 启动http下载
					if (dataUrl.startsWith("http")) {
						HttpDownload download = new HttpDownload();
						// 判断下下载格式:如果是zip格式，需要解压
						if (dataUrl.toUpperCase().endsWith("ZIP")
								&& (!destFile.endsWith(".zip"))) {
							destFile += ".zip";
						}

						this.timeConsumeCount.setStartTimeByCurrentTime();

						if (!download.httpDownload(dataUrl, destFile)) {
							logger.error(dataFileName + "数据下载失败！" + dataUrl);
							nextPrepareingDatalists.add(curr_dataId);
						} else {
							logger.error(dataUrl + "数据下载成功！");
						}

						this.timeConsumeCount.setEndTimeByCurrentTime();

					}
					// 启动FTP下载
					else if (dataUrl.startsWith("ftp")) {
						// 需要增加相应的FTP下载的判断
						if (false) {
							nextPrepareingDatalists.add(curr_dataId);
						} else {
							flag = true;
						}
					}
					// 获取文件大小和绝对路径
					FileOperation currDataOperation = new FileOperation();
					// test
					// System.out.println("循环调用调用获取大小！");
					datasize = currDataOperation.getFileSize(destFile);
//					if (dataFileName == null) {
//						dataFileName = "Unknown";
//					}

//					operation.appendMethodB(recordFile, "Name=" + dataFileName
//							+ " ,Size= " + datasize + " ,DownLoadTimeConsume="
//							+ timeConsumeCount.getTimeSpan());

					// 更新获取信息
					rsDataCacheDB.updateDataAcquireInfos(curr_dataId,
							dataFileName, dataStatus, dataUrl, datasize,
							"10.3.10.1:" + destFile);

				}
				// 正在获取的数据
				else if (dataStatus.equals("Running")) {
					// test
					System.out.println(curr_dataId + "正在准备！");
					nextPrepareingDatalists.add(curr_dataId);
					rsDataCacheDB.updateDataAcquireInfos(curr_dataId, "",
							"Running", "", 0, "");
				}
				// 获取出错的数据
				else {
					errCount++;
					unAvaliableDatalists.add(curr_dataId);
					// 更新结果列表
					rsDataCacheDB.updateDataAcquireInfos(curr_dataId, "",
							"NotAvailalbe", "", 0, "");
				}

			} catch (DocumentException e) {
				errorFlag = "XMLParsing";
				logger.error("获取getDataDescription失败！请检查返回值是否符合规范！");
				e.printStackTrace();
			}
		}

		// 更新下一轮需要轮询的数据
		this.prepareingDatalists.clear();
		this.prepareingDatalists = nextPrepareingDatalists;

		return true;
	}

	// 获取数据ID列表
	public ArrayList<String> getDataIdLists() {
		ArrayList<String> dataIdLists = new ArrayList<String>();
		Iterator<RSData> iterator = this.feedbackDatalists.datalists.iterator();
		while (iterator.hasNext()) {
			RSData rsData = (RSData) iterator.next();
			dataIdLists.add(rsData.dataid);
		}
		return dataIdLists;
	}

	/*
	 * 查询数据获取信息：包括数据名称，数据状态， 参数为未获取下载链接的dataId列表 获取过程：首先获取下载链接（异步过程）
	 */
	public String getAcquireInfo(String dataId) {

		// 返回结果
		String getDataResult = null;

		// System.out.println("数据ID：" + dataId);

		String startDataObtainRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataObtain><DataId>"
				+ dataId + "</DataId></DataObtain>";
		String accessId = "";

		try {
			String startDataObtainResult = proxy
					.startDataObtain(startDataObtainRequest);

			try {
				accessId = getElementText(startDataObtainResult,
						"//DataObtainTask/TaskId");
			} catch (DocumentException e) {
				errorFlag = "XMLParsing";
				logger.error("获取数据获取的TaskId失败！请检查返回值是否符合规范！");
				e.printStackTrace();
				return null;
			}

			if (accessId != null) {
				String getDataResultRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataObtainTask>	<TaskId>"
						+ accessId + "</TaskId></DataObtainTask>";
				getDataResult = proxy.getDataResult(getDataResultRequest);
				// test
				// System.out.println(getDataResult);
			}

		} catch (RemoteException e) {
			errorFlag = "Webservice";
			logger.error("返回数据获取的startDataObtain或getDataResult失败！请检查Webservice是否可以联通！");
			e.printStackTrace();
			return null;
		}
		return getDataResult;
	}

	// 内部类获取元素个数及值
	public static class FeedbackDataList {
		public int count = 0;
		public ArrayList<RSData> datalists = new ArrayList<RSData>();

		public int getCount() {
			if (datalists != null) {
				this.count = datalists.size();
			}
			return count;
		}

		// 添加数据
		public boolean addData(ArrayList<String> list) {

			Iterator<String> iterator = list.iterator();
			while (iterator.hasNext()) {
				String dataInfo = (String) iterator.next();
				// System.out.println(dataInfo);
				HashMap<String, String> map = new HashMap<String, String>();
				// 分割属性
				try {
					// System.out.println(dataInfo);
					String[] rsDataInfo = dataInfo.split(",");
					// 分割键值对
					for (int i = 0; i < rsDataInfo.length; i++) {
						String[] kv = rsDataInfo[i].split("=");
						if (kv.length == 2) {
							map.put(kv[0], kv[1]);
						} else {
							map.put(kv[0], "");
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("e:");
					e.printStackTrace();
					return false;
				}
				RSData data = new RSData(map);

				datalists.add(data);
			}
			return true;
		}

	}

	// 获取XML元素值
	public String getElementText(String strXML, String strElementPath)
			throws DocumentException {
		try {
			Document dom = DocumentHelper.parseText(strXML);
			return dom.selectSingleNode(strElementPath).getText();
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.error("<Error>ServiceImplSoapBindingImpl::getElementText | "
					+ strElementPath + " is invalid.");
			return "";
		}
	}

	// 获取数据列表
	public ArrayList<String> getDataList(String strXML, String strElementPath)
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

					strData += "dataid="
							+ element_data.attributeValue("dataid");

					Iterator iter_dataelement = list_data.iterator();
					while (iter_dataelement.hasNext()) {

						strData += ",";

						Element element_dataelement = (Element) iter_dataelement
								.next();

						strData += element_dataelement.getName() + "="
								+ element_dataelement.getStringValue();
					}
					dataList.add(strData);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.error("<Error>ServiceImplSoapBindingImpl::getDataList | Element path is invalid.");
			return new ArrayList<String>();
		}

		return dataList;
	}

	public static void main(String[] args) throws DocumentException {
		// String testURL =
		// "http://166.111.70.2:8580/DDSS/services/WFDataServer?wsdl";
		String testURL = "http://10.3.11.66:80/DDSS/services/WFDataServer?wsdl";
		String testDestFilePath = "/dataIO/863_Project/863-Daemon/Project1DataService/DownloadFromRemote_1KM_NDVI/";
		String datas;
		// 查询ID
		String queryId = null;
		// 返回数据条目限制个数
		int countLimit = 10;

		// 最长等待时间(小时计)
		double WAITHOURS = 24 * 3;

		// 数据库操作
		// DBConn connection = new DBConn("10.3.10.1", "3306", "mccps", "mca",
		// "mca");
		// SystemConfigDB systemConfigDB = new SystemConfigDB();
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();

		ArrayList<String> dataPlanTypeLists = new ArrayList<String>();

		// 知识库查询
		/*
		 * String productName="QP_NDVI_1KM";
		 * 
		 * DBManage.RSProductKnowledgeDB rSProductKnowledgeDBInstance = new
		 * DBManage.RSProductKnowledgeDB(); Map<String,
		 * List<DataService.OutOrderXmlClass>> OutOrderXMLMapSumMain = null;
		 * OutOrderXMLMapSumMain
		 * =rSProductKnowledgeDBInstance.InstanceReturn(productName);
		 * 
		 * System.out.println("最终返回的数据解析结果Map"); for (Map.Entry<String,
		 * List<DataService.OutOrderXmlClass>> entry : OutOrderXMLMapSumMain
		 * .entrySet()) { // System.out.println("\nkey=" + entry.getKey());
		 * List<DataService.OutOrderXmlClass> list = entry.getValue();
		 * Iterator<DataService.OutOrderXmlClass> iterator = list.iterator(); //
		 * String item=""; String satellite=null; String sensor=null; while
		 * (iterator.hasNext()) { DataService.OutOrderXmlClass outOrderXmlClass
		 * = (DataService.OutOrderXmlClass) iterator .next(); if
		 * (outOrderXmlClass.getXmlnodename().equals("satellite")) {
		 * satellite=outOrderXmlClass.getXmlnodevalue(); } if
		 * (outOrderXmlClass.getXmlnodename().equals("sensor")) {
		 * sensor=outOrderXmlClass.getXmlnodevalue(); }
		 * 
		 * System.out.println(outOrderXmlClass.getXmlnodename() + "=" +
		 * outOrderXmlClass.getXmlnodevalue());
		 * 
		 * if (satellite!=null&&sensor!=null) {
		 * dataPlanTypeLists.add(sensor+"@"+satellite); } }
		 * 
		 * System.out.println(); }
		 */

		// if (orderRequest.productName.equals("QP_NDVI_1KM")) {
		 dataPlanTypeLists.add("CCD1@HJ1-1A");
		 dataPlanTypeLists.add("CCD2@HJ1-1A");
		 dataPlanTypeLists.add("HSI@HJ1-1A");
		 dataPlanTypeLists.add("CCD1@HJ1-1B");
		 dataPlanTypeLists.add("CCD2@HJ1-1B");
		 dataPlanTypeLists.add("IRS@HJ1-1B");

//		 dataPlanTypeLists.add("@HJ1A,CCD1");
//		 dataPlanTypeLists.add("@HJ1A,CCD2");
//		 dataPlanTypeLists.add("@HJ1-1A,HSI");
//		 dataPlanTypeLists.add("@HJ1B,CCD1");
//		 dataPlanTypeLists.add("@HJ1B,CCD2");
//		 dataPlanTypeLists.add("@HJ1-1B,IRS");
		// //////////////////////////////////////////////////
		 dataPlanTypeLists.add("MERSI@FY3A");
		  dataPlanTypeLists.add("VIRR@FY3A");
		 dataPlanTypeLists.add("MERSI@FY3B");
		  dataPlanTypeLists.add("VIRR@FY3B");
		// // /////////////////////////////////////////////////////
		 dataPlanTypeLists.add("TM@LANDSAT5");
		 dataPlanTypeLists.add("ETM+@LANDSAT7");
		// // /////////////////////////////////////////////////////
		 dataPlanTypeLists.add("MODIS@AQUA");
		 dataPlanTypeLists.add("MODIS@TERRA");
		// /////////////////////////////////////////////////////
		dataPlanTypeLists.add("MODIS@MCD43B1@MODIS");
		 dataPlanTypeLists.add("MODIS@MCD43C2@MODIS");
		 dataPlanTypeLists.add("MODIS@MCD43B3@MODIS");
		 dataPlanTypeLists.add("MODIS@MCD12Q1@MODIS");
		 dataPlanTypeLists.add("MODIS@MOD03@AQUA");
		 dataPlanTypeLists.add("MODIS@MOD07@AQUA");
		 dataPlanTypeLists.add("MODIS@MYD07@TERRA");
		// /////////////////////////////////////////////////////////
		 dataPlanTypeLists.add("MTSAT-2R@JAMI");

		// }
		// if (orderRequest.productName.equals("QP_NDVI_30M")) {
		// dataPlanTypeLists.add("@HJ1A_CCD1");
		// dataPlanTypeLists.add("@HJ1B_CCD1");
		// dataPlanTypeLists.add("@HJ1A_CCD2");
		// }
		// 根据数据方案依次查询获得数据列表

		// 更新到本地下载目录
		// DataAcquire dataAcquire2 = new DataAcquire("",
		// testURL, testDestFilePath);
		// dataAcquire.getDataListsValumeSizes();
		// /*

		Iterator<String> iterator_dataPlan = dataPlanTypeLists.iterator();

		// 数据方案总的数据量
		long AllDataSize = 0;

		while (iterator_dataPlan.hasNext()) {
			String currDataType = (String) iterator_dataPlan.next();
			// 获取卫星平台和传感器

			String satellite = currDataType.split("@")[1];
			String sensor = currDataType.split("@")[0];

			// 当前数据类型的数据量
			long curDataSize = 0;

			System.out.println(">>" + satellite + " " + sensor);

			// HJ FY3A_MERSI
			String testQueryRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<DataQuery><Conditions>"
					 +
					 "<ULLat>44</ULLat><ULLong>94</ULLong><LRLat>35</LRLat><LRLong>104</LRLong>"
//					+ "<ULLat>43</ULLat><ULLong>95</ULLong><LRLat>36</LRLat><LRLong>103</LRLong>"
					+ "<StartDate>2014-06-29 00:00:00</StartDate><EndDate>2014-07-09 23:59:59</EndDate>"
//					+ "<StartDate>2014-06-29 00:00:00</StartDate><EndDate>2014-07-09 23:59:59</EndDate>"
					+ "<Satellite>"
					+ satellite
					+ "</Satellite>"
					+ "<Sensor>"
					+ sensor
					+ "</Sensor>"
					+ "<Resolution></Resolution>"
					+ "<CloudCover></CloudCover>" + "</Conditions></DataQuery>";

			DataAcquire dataAcquire = new DataAcquire(testQueryRequest,
					testURL, testDestFilePath);

			// 文件
			String file = "/home.bak/MCA/zjDir/DataAcquireServiceTest/Record.csv";
			dataAcquire.recordFile = file;

			FileOperation fileOperation = new FileOperation();
			TimeConsumeCount timeConsumeCount2 = new TimeConsumeCount();

			fileOperation.appendMethodB(file, "satellite=" + satellite + "\n");
			fileOperation.appendMethodB(file, "sensor=" + sensor + "\n");

			// 元数据获取时间
			timeConsumeCount2.setStartTimeByCurrentTime();

			// 获取数据列表
			if (!dataAcquire.getBaseMetaInfos()) {
				System.out.println("获取元数据信息出错！");
			}

			timeConsumeCount2.setEndTimeByCurrentTime();
			fileOperation.appendMethodB(file, "RawDataAcqurieTimeConsume="
					+ timeConsumeCount2.getTimeSpan() + "\n");

			// 查询缓存库获取已经存在的缓存数据：已经存在的数据无需下载
			System.out.println(dataAcquire.feedbackDatalists.getCount());
			if (!dataAcquire.removeHaveCachedDataLists()) {
				System.out.println("移除已经存在的数据失败！");
			}
			// test
			// System.out.println(dataAcquire.feedbackDatalists.getCount());
			// 更新元数据信息
			dataAcquire.insertDataListToCacheDB();

			timeConsumeCount2.setStartTimeByCurrentTime();

			// 第一次尝试获取下载链接并更新获取信息
			dataAcquire.getFirstAcquireInfos();
			// 判断条件，轮询获取下载链接
			TimeConsumeCount timeConsumeCount = new TimeConsumeCount();
			timeConsumeCount.setStartTimeByCurrentTime();

			if (dataAcquire.prepareingDatalists.size() > 0) {
				// 控制条件：准备完成或者超时
				do {
					dataAcquire.getPollingAcquireInfos();
					// 休眠
					try {
						Thread.sleep(1000 * 10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					timeConsumeCount.setEndTimeByCurrentTime();

					curDataSize += dataAcquire
							.getDataListsValumeSizes(dataAcquire
									.getDataIdLists());
					AllDataSize += curDataSize;

				} while (dataAcquire.prepareingDatalists.size() > 0
						&& timeConsumeCount.getTimeSpan() < WAITHOURS * 3600 * 1000);

			}
			timeConsumeCount2.setEndTimeByCurrentTime();
			fileOperation.appendMethodB(file, "DataDownloadTimeConsume="
					+ timeConsumeCount2.getTimeSpan() + "\n");
			// 更新路径，获取大小
		}
		System.out.println("总数据量" + AllDataSize);
		// */
	}

}
