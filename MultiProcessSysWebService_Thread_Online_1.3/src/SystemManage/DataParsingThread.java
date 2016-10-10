/*
 *程序名称 		: ProcessThread1.java
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

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import com.sun.xml.rpc.streaming.Stream;

import DBManage.RequestDB;
import DBManage.RsDataCacheDB;
import DataService.DataAcquire;
import DataService.InputParametersDataClass;
import FileOperation.FileOperation;
import FileOperation.TimeConsumeCount;
import OrderManage.L2ExternalOrder;
import OrderManage.L3InternalOrder;
import OrderManage.OrderRequest;
import OrderManage.OrderStudio;
import ServiceInterface.ICPFeasibilitySubmitProxy;
import ServiceInterface.ICPStateFeedbackProxy;
import ServiceInterface.ProcessImplProxy;

/**
 * @author caoyang
 *
 */
public class DataParsingThread extends Thread {
	// 用来存放外部订单（L2二级外部订单）请求列表
	private ArrayList<OrderRequest> orderRequestList;
	// 用来进行订单管理的订单管理器
	private OrderStudio orderStudio;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	/**
	 * 
	 */
	public DataParsingThread() {
		this.orderStudio = new OrderStudio();
	}

	/**
	 * @param arg0
	 */
	public DataParsingThread(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public DataParsingThread(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DataParsingThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DataParsingThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DataParsingThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public DataParsingThread(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public DataParsingThread(ThreadGroup arg0, Runnable arg1, String arg2,
			long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public void run() {

		// ProcessImplProxy project1Proxy = new ProcessImplProxy(
		// "http://localhost:10080/Project1Webservice/services/ProcessImpl" );
		// //服务器代理设置
		// ProcessImplProxy proxy = new ProcessImplProxy();
		// 换成真实的卫星组网服务
		// 需要改成从配置文件中读取
		String project1WebserviceUrl = "http://10.3.11.66:80/DDSS/services/WFDataServer?wsdl";
		ICPFeasibilitySubmitProxy proxyCPFeasibility = new ICPFeasibilitySubmitProxy();
		ICPStateFeedbackProxy proxyCPStateFeedbackProxy = new ICPStateFeedbackProxy();

		// ICPFeasibilitySubmitProxy proxyCPFeasibility = new
		// ICPFeasibilitySubmitProxy();

		for (;;) {
			try {
				// 获取外部订单列表
				System.out.println("Thread[1] 2.获取外部订单列表");
				this.orderRequestList = orderStudio
						.getOrderRequestList("UnDataList");
				// test
				System.out.println("Thread[1]--------------RequestOrder:");
				for (int iIndex = 0; iIndex < this.orderRequestList.size(); iIndex++) {
					System.out.println("Thread[1] "
							+ String.valueOf(iIndex + 1) + " - "
							+ this.orderRequestList.get(iIndex).jobId);
				}
				System.out.println("Thread[1]---------------------------");

				Iterator<OrderRequest> orderRequest_curr = this.orderRequestList
						.iterator();
				while (orderRequest_curr.hasNext()) {
					OrderRequest orderRequest = orderRequest_curr.next();
					// test
					// System.out.println(
					// "**************************************** curr orderrequest = "
					// + orderRequest.jobId );

					if (("L2FP".equals(orderRequest.orderType))
							|| ("L2AP".equals(orderRequest.orderType))) {
						// 如果是融合/同化订单，则先要通过1级订单号检索共性产品生产订单，并返回该订单。
						// L2ExternalOrder l2CPOrder;
						ArrayList<String> retrievalDataList = new ArrayList<String>();
						L2ExternalOrder l2CPOrder = orderStudio
								.getL2CPOrderByL1JobId(orderRequest.jobId_L1);
						if (null == l2CPOrder) {
							// 共性订单不存在，向课题三反馈订单可行性确认消息。
							// 向课题三发送订单可行性确认消息
							logger.info("订单" + orderRequest.jobId + "对应的共性产品订单"
									+ orderRequest.jobId_L1 + "不存在！");
							logger.info("3.向课题三发送订单" + orderRequest.jobId
									+ "可行性确认消息:共性产品订单不存在！");

							String strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><id>"
									+ orderRequest.jobId
									+ "</id>"
									+ "<feasibility>"
									+ "false"
									+ "</feasibility><remark>XX</remark></condition></root>";
							String strRet;
							
							 System.out.println(">>"+strXML);
							 try {
							 strRet = proxyCPFeasibility.commonProductFeasibilitySubmit(strXML);
							
							 } catch (RemoteException e) {
							 e.printStackTrace();
							 }

							// 更新生产请求状态为失效（或删除）
							orderRequest.invalidStatus();
							continue;
						}

						// 5.共性订单查询 返回 共性产品生产订单（2级）及共性数据产品URL

						
						boolean useTepFAPlan=true;
						if (useTepFAPlan) {
							//临时数据方案：只对一个测试数据进行融合处理，实际融合处理数据来自用户需求
							//Name=MuSyQ.VI.1km.2014076000000.H33V09.001.h5;Name=MuSyQ.VI.1km.2014236000000.H33V09.001.h5;
							retrievalDataList.add("Name=MuSyQ.VI.1km.2014076000000.H33V09.001.h5");
							retrievalDataList.add("Name=MuSyQ.VI.1km.2014236000000.H33V09.001.h5");
							
						}else {
							retrievalDataList = L3InternalOrder
							.getRetrievalDataListByL2OrderId(l2CPOrder.jobId);							
						}
						
						orderRequest.setRetrievalDataList(retrievalDataList);
						orderRequest.geoCoverageStr = l2CPOrder.geoCoverageStr; // @@@
						orderRequest.startDate = l2CPOrder.startDate;
						orderRequest.endDate = l2CPOrder.endDate;
						orderRequest.productName=l2CPOrder.productName;

						OrderRequest.updateOrder(orderRequest);
					}

					// 订单的数据解析（调用内部数据解析接口）
					logger.info("3.订单" + orderRequest.jobId
							+ "的数据解析（调用内部数据解析接口）");
					logger.info("3.←反馈可行数据方案：数据列表和数据状态");

					String strOrderType;
					switch (orderRequest.orderType) {
					case "L2VD":
					case "L2CP":
						// 共性产品
						strOrderType = "Retrieval";
						break;
					case "L2FP":
						// 融合产品
						strOrderType = "Fusion";
						break;
					case "L2AP":
						// 同化产品
						strOrderType = "Assimilation";
						break;
					default:
						strOrderType = "";
						orderRequest.invalidStatus();
						continue;
					}

					// 从字符串中分解四角坐标
					ArrayList<String> coverscope = new ArrayList<String>();
					if (!orderRequest.geoCoverageStr.equals("")) {
						String[] strDataSplitArray = orderRequest.geoCoverageStr
								.split(",");
						coverscope.addAll(Arrays.asList(strDataSplitArray));
					} else {
						coverscope.add("-1");
						coverscope.add("-1");
						coverscope.add("-1");
						coverscope.add("-1");
					}

					String ullat = coverscope.get(3);// 43
					String ullong = coverscope.get(0);// 95
					String lrlat = coverscope.get(1);// 36
					String lrlong = coverscope.get(2);// 103

					String startDate = "0000-00-00 00:00:00";
					if (null != orderRequest.startDate) {
						startDate = orderRequest.startDate.toString()+" 00:00:00";
					}
					String endDate = "0000-00-00 23:59:59";
					if (null != orderRequest.endDate) {
						endDate = orderRequest.endDate.toString()+" 23:59:59";
					}
					// String strXML =
					// "<?xml version=\"1.0\" encoding=\"UTF-8\"?><OrderRSDataRequirement><L1OrderId>"
					// + orderRequest.jobId + "</L1OrderId>"
					// + "<L2OrderId>" + orderRequest.jobId_L1+
					// "</L2OrderId><OrderType>" + strOrderType + "</OrderType>"
					// + "<Parameters><ProductName>" + orderRequest.productName
					// + "</ProductName>"
					// + "<ULLat>" + coverscope.get( 3 ) + "</ULLat><ULLong>" +
					// coverscope.get( 0 ) + "</ULLong>"
					// + "<LRLat>" + coverscope.get( 1 ) + "</LRLat><LRLong>" +
					// coverscope.get( 2 ) + "</LRLong>"
					// + "<StartDate>" + startDate + "</StartDate><EndDate>" +
					// endDate + "</EndDate>"
					// + "</Parameters></OrderRSDataRequirement>";
					// 最终数据方案
					ArrayList<String> finalDataPlanLists = new ArrayList<String>();
					// ArrayList<InputParametersDataClass>
					// finalDataPlanLists=new
					// ArrayList<InputParametersDataClass>();

					/*
					 * 增加知识库：多个数据依赖，需要依次获取数据 以下为产品的数据依赖： QP_NDVI_1KM： FY3A_MERSI
					 * FY3B_MERSI AQUA_MODIS TERRA_MODIS QP_NDVI_30M：HJ1A_CCD1
					 * HJ1B_CCD1 HJ1A_CCD2 QP_NPP_1KM： QP_NPP_300M：
					 */
					ArrayList<String> dataPlanTypeLists = new ArrayList<String>();
					logger.info("当前订单生产类型：" + orderRequest.productName);

					boolean isAdoptTmpDataPlan = true;
					// 默认提交经纬度为：95,36,103,43 ，则采用本地临时的数据方案，否则采用真实的数据方案
					if (ullat.equals("43") && ullong.equals("95")
							&& lrlat.equals("36") && lrlong.equals("103")) {
						System.out.println("经纬度与默认一致！");
						isAdoptTmpDataPlan = true;
					} else {
						isAdoptTmpDataPlan = false;
					}

					if (isAdoptTmpDataPlan) {						
						logger.info("订单"+orderRequest.jobId+"采用本读数据方案！");
						// 临时数据方案：考虑到算法为就绪，几何算法只能处理部分数据
						// finalDataPlanLists.add("77d6f70b-c15a-46ab-812f-2fac9d1bab09__1459315047140__57__452434439");
						
						// ////////////////////////////////////////////////////////////////
						// 临时本地的已经下载数据方案 即L1CP201605050001L2CP001数据列表
						System.out.println("临时本地的已经下载数据方案 即L1CP201605050001L2CP001数据列表!");
						RequestDB requestDB = new RequestDB();
						OrderRequest tempOrderRequest = requestDB
								.getOrder("L1CP201605050001L2CP001");
						finalDataPlanLists.addAll(tempOrderRequest.dataList);
						
						// 更新订单所需要的数据类型
						orderRequest.dataType=tempOrderRequest.dataType;
						OrderRequest.updateOrder(orderRequest);

						// ////////////////////////////////////////////////////////////

					} else {
						// 采用数据解析/请求下载/真實数据方案
						logger.info("订单"+orderRequest.jobId+"采用真实的数据方案！具备知识库解析，数据请求及数据下载等流程！");

						// 知识库代码
						// /*
						DBManage.RSProductKnowledgeDB rSProductKnowledgeDBInstance = new DBManage.RSProductKnowledgeDB();
						Map<String, List<DataService.OutOrderXmlClass>> OutOrderXMLMapSumMain = null;
						OutOrderXMLMapSumMain = rSProductKnowledgeDBInstance
								.InstanceReturn(orderRequest.productName);

						System.out.println("最终返回的数据解析结果为：");
						for (Map.Entry<String, List<DataService.OutOrderXmlClass>> entry : OutOrderXMLMapSumMain
								.entrySet()) {
							// System.out.println("\nkey=" + entry.getKey());
							List<DataService.OutOrderXmlClass> list = entry
									.getValue();
							Iterator<DataService.OutOrderXmlClass> iterator = list
									.iterator();
							//
							String item = "";

							String satellite = null;
							int satelliteCount = 0;
							String sensor = null;
							int sensorCount = 0;

							while (iterator.hasNext()) {
								DataService.OutOrderXmlClass outOrderXmlClass = (DataService.OutOrderXmlClass) iterator
										.next();
								if (outOrderXmlClass.getXmlnodename().equals(
										"satellite")) {
									satellite = outOrderXmlClass
											.getXmlnodevalue();
									satelliteCount++;
								}
								if (outOrderXmlClass.getXmlnodename().equals(
										"sensor")) {
									sensor = outOrderXmlClass.getXmlnodevalue();
									sensorCount++;
								}

								// System.out.println(outOrderXmlClass.getXmlnodename()
								// + "="
								// + outOrderXmlClass.getXmlnodevalue());

								if (satellite != null && sensor != null
										&& satelliteCount == sensorCount) {
									dataPlanTypeLists.add(sensor + "@"
											+ satellite);
								}
							}

							// System.out.println();
						}
						// */
						//清空容器Map
						OutOrderXMLMapSumMain.clear();

						// 数据类型置空
						orderRequest.dataType = "";
						// 根据数据方案依次查询获得数据列表
						Iterator<String> iterator_dataPlan = dataPlanTypeLists
								.iterator();
						int count = 0;
						logger.info(orderRequest.productName + count + "/"
								+ dataPlanTypeLists.size() + "数据方案：");

						// 统计数据准备时间及数据量
						FileOperation operation = new FileOperation();
						TimeConsumeCount consumeCount = new TimeConsumeCount();
						consumeCount.setStartTimeByCurrentTime();
						long dataTotalValume = 0;

						while (iterator_dataPlan.hasNext()) {
							String currDataType = (String) iterator_dataPlan
									.next();
							orderRequest.dataType += (currDataType + ";");
							// 获取卫星平台和传感器
							count++;
							//
							logger.info(orderRequest.productName + "->" + count
									+ "/" + dataPlanTypeLists.size() + "数据方案："
									+ currDataType);
							logger.info(">>" + currDataType);

							String[] infos = currDataType.split("@");
							if (infos.length != 2) {
								logger.info("遥感数据类型出错！");
								continue;
							}
							String satellite = infos[1];
							String sensor = infos[0];
							
							if (satellite==null || sensor==null) {
								this.logger.info("卫星或传感器为空！");
								continue;								
							}
														
							//区分MODIS一些产品的特别的查询条件
							//对于MODIS_MCD12Q1 MODIS_MCD43B1 MODIS_MCD43B3 MODIS_MCD43C2
																			
							if (sensor.equals("MODIS_MCD12Q1")&&startDate.contains("2014")||startDate.contains("2015")||
									startDate.contains("2016")) {
								//MCD12Q1：数据准备时，当年没有该数据的情况下，找最近的时间点的数据
								startDate = "2012" + startDate.substring(4, 19);
								endDate = "2013" + endDate.substring(4, 19);
								
							}
							else if (sensor.equals("MODIS_MCD43B1") || sensor.equals("MODIS_MCD43B3") || sensor.equals("MODIS_MCD43C2")) {
								//数据准备时，可用当年最近时刻来替代
								startDate = orderRequest.startDate.toString()+" 00:00:00";
								endDate = orderRequest.endDate.toString()+" 23:59:59";
								
							}
							else {
								//其他数据方案，无需进行特殊的时空范围的处理
								startDate = orderRequest.startDate.toString()+" 00:00:00";
								endDate = orderRequest.endDate.toString()+" 23:59:59";
								
							}
														

							// 卫星组网数据查询条件
							String queryRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
									+ "<DataQuery><Conditions>" + "<ULLat>"
									+ ullat
									+ "</ULLat><ULLong>"
									+ ullong
									+ "</ULLong><LRLat>"
									+ lrlat
									+ "</LRLat><LRLong>"
									+ lrlong
									+ "</LRLong>"
									+ "<StartDate>"
									+ startDate
									+ "</StartDate><EndDate>"
									+ endDate
									+ "</EndDate>"
									+ "<Satellite>"
									+ satellite
									+ "</Satellite>"
									+ "<Sensor>"
									+ sensor
									+ "</Sensor>"
									+ "<Resolution></Resolution>"
									+ "<CloudCover></CloudCover>"
									+ "</Conditions></DataQuery>";
							logger.info("查询条件：" + queryRequest);
							
							// 符号标记，判断是否采用临时数据方案，否则采用知识库进行真实的数据解析与下载

							// 获取元数据数据
							// /*
							// 存放路径：此处应该不下载，应该只标记数据状态，等到订单确认后再下载
							String testDestFilePath = "/dataIO/863_Project/863-Daemon/Project1DataService/DownloadFromRemote_1KM_NDVI/";
							// 最长等待时间(小时计)
							double WAITHOURS = 24 * 3;
							DataAcquire dataAcquire = new DataAcquire(
									queryRequest, project1WebserviceUrl,
									testDestFilePath);

							// 获取数据列表
							if (!dataAcquire.getBaseMetaInfos()) {
								System.out.println("获取元数据信息出错！");
							}

							// 追加数据列表ID
							finalDataPlanLists.addAll(dataAcquire
									.getDataIdLists());

							// 查询缓存库获取已经存在的缓存数据：已经存在的数据无需下载
							// System.out.println(dataAcquire.feedbackDatalists.getCount());
							if (!dataAcquire.removeHaveCachedDataLists()) {
								System.out.println("移除已经存在的数据失败！");
							}

							// 更新元数据信息
							dataAcquire.insertDataListToCacheDB();
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

								} while (dataAcquire.prepareingDatalists.size() > 0
										&& timeConsumeCount.getTimeSpan() < WAITHOURS * 3600);
							}
							// */
						}

						// 获取所有数据总量，保存到日志中
						consumeCount.setEndTimeByCurrentTime();
						DataAcquire dataAcquire = new DataAcquire();
						dataTotalValume += dataAcquire
								.getDataListsValumeSizes(finalDataPlanLists);
						long timeCount = consumeCount.getTimeSpan();
						String infos = consumeCount.getCurrentTime() + ","
								+ orderRequest.jobId + "DP" + ","
								+ dataTotalValume + "," + timeCount + ",\n";
						System.out.println(">>" + infos);
						operation.appendMethodB(SystemConfig.getLogsPath()
								+ "/TimeConsume.csv", infos);
						System.out.println("耗时记录至："
								+ SystemConfig.getLogsPath()
								+ "/TimeConsume.csv");
					}

					// 增加状态判断：数据可用时状态，出现数据不可用时的状态，此处因该结合知识库，对当前数据集进行最小集检验，是否能够满足需求

					// 判断数据是否全部可用
					logger.info("根据知识库对" + orderRequest.jobId
							+ "数据方案进行合法性检验！检查数据是否完备！");
					boolean isFeasiable = true;
					RsDataCacheDB rsDataCacheDB = new RsDataCacheDB();
					Iterator<String> iterator_dataidlist = finalDataPlanLists
							.iterator();

					// 此处只全部检验是否可用，实际上只检验必须要有的数据即可
					// 此处应该改为需要进行数据完备性检验，不应该全部数据都可获取时，才能够进行产品生产
//					while (iterator_dataidlist.hasNext()) {
//						String dataid = (String) iterator_dataidlist.next();
//					 
//						//	isFeasiable &= rsDataCacheDB.isAvaliable(dataid);
//					}
					// 更新订单所需要的数据类型
					OrderRequest.updateOrder(orderRequest);

					// 订单状态：已经进行数据方案解析
					orderRequest.nextStatus();

					// 更新数据状态及数据列表

					// 更新数据反馈方案
					logger.info("更新订单" + orderRequest.jobId + "数据方案状态");
					if (isFeasiable) {
						String strDataStatus = "Available";
						orderStudio.setDataStatus(orderRequest.jobId,
								strDataStatus, finalDataPlanLists);
						
//						 //调用课题三接口，反馈DP完成状态
//							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//							
//							String strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><condition><id>"
//									+ orderRequest.jobId
//									+ "</id>"
//									+"<username>"
//									+"mca"
//									+"</username>"
//									+"<content>"
//									+"L3DP"
//									+"</content>"
//									+"<time>"
//									+df.format(new Date())
//									+"</time>"
//									+ "</condition></root>";
//							try {
//								proxyCPStateFeedbackProxy.commonProductStateFeedback(strXML);
//								logger.info("向课题三反馈数据准备完成");
//								
//							} catch (RemoteException e) {
//								e.printStackTrace();
//							}
					} else {
						String strDataStatus = "unAvailable";
						orderStudio.setDataStatus(orderRequest.jobId,
								strDataStatus, finalDataPlanLists);
					}

					// 初始化生产确认状态
					orderStudio.setConfirmationStatus(orderRequest.jobId, -1,
							false); // @@@

				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("数据解析线程运行错误！");
				logger.error(e);
			}

			try {
				sleep(10000); // 暂停，每一秒输出一次
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
	}

	private String getElementText(String strXML, String strElementPath)
			throws DocumentException {
		Document dom = DocumentHelper.parseText(strXML);
		return dom.selectSingleNode(strElementPath).getText();
	}
}
