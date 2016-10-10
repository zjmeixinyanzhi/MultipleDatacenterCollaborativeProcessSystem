package TaskSchedular;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import DBManage.DataCenter;
import DBManage.RsDataCacheDB;
import DBManage.SystemResourceDB;
import RSDataManage.RSData;
import RSDataManage.RSDataRequestInfo;
import ServiceInterface.ProcessImplProxy;
import SystemManage.SystemLogger;
import SystemManage.SystemResource;

/**
 * 创建时间：2015-7-23 下午10:29:35 项目名称：MultiProcessSysWebService_Thread 2015-7-23
 * 
 * @author 张杰
 * @version 1.0 文件名称：RSDataSchedular.java 类说明：通过数据调度，从不同数据中心中挑选最佳的数据，并返回数据Url
 */
public class RSDataSchedular {
	// 当前待调度数据 缓存中的相应数据
	public RSData currentData = null;
	
	// 当前数据存在的数据中心列表
	private ArrayList<DataCenter> candidateDataCenterList = new ArrayList<>();
	// 调度后数据所在的数据中心
	private DataCenter optimalDataCenter = null;

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();
	/**
	 * 数据状态 未知状态：unKnown 缓存中已经存在：inCache 在中心数据库库中：inCentralDB
	 * 待向数据中心获取：needAcquired 数据不可用（获取失败）：unAvailable
	 */
	public String dataStatus = "unKnown";

	// 调度后数据所在数据中心的url
	private String dataPath = "";
	// 候选数据中心及其数据Url对应表
	private HashMap<String, String> dataHostandPath = null;

	public RSDataSchedular(RSData rsData) {
		this.currentData = rsData;
	}	

	// 查询缓存库中是否存在数据，是返回true
	public boolean searchInCache() {
		if (null == this.currentData) {
			return false;
		}
		
		if (this.currentData.requestInfo==null||this.currentData.requestInfo.equals("")) {
			return false;
		}
		
		// 分拆缓存中该数据的请求信息
		String[] requestInfos = this.currentData.requestInfo.split(";");
		// test
		logger.info(">>>dataRequestInfo" + this.currentData.requestInfo);

		this.dataHostandPath = new HashMap<String, String>();

		for (int i = 0; i < requestInfos.length; i++) {
			RSDataRequestInfo currRequestInfo = new RSDataRequestInfo(
					requestInfos[i]);
			System.out.print(currRequestInfo.datacenter);
			// 找出当前数据所在的数据中心资源,放入候选资源中
			SystemResourceDB systemResourceDB = new SystemResourceDB();
			this.candidateDataCenterList.add(systemResourceDB
					.getDataCenterListByIP(currRequestInfo.datacenter).get(0));

			// 保存数据中心及Url对应信息
			this.dataHostandPath.put(currRequestInfo.datacenter,
					currRequestInfo.filepath);
		}
		if (this.candidateDataCenterList.size() == 0) {
			return false;
		}
		this.dataStatus = "inCache";
		return true;
	}

	// 如果数据存在缓存中，选出位于最优的数据中心上的数据
	private void getOptimalDataCenter() {
		
		if (!this.dataStatus.equals("inCache")
				&& !this.dataStatus.equals("inCentralDB")) {
			return;
		}

		// 调度选出最优的数据中心
		DataCenterSchedular dataCenterSchedular = new DataCenterSchedular();
		this.optimalDataCenter = dataCenterSchedular
				.doSchedule(this.candidateDataCenterList);
		if (null == this.optimalDataCenter) {
			logger.error("Datacenter is unAvaliable!");
		}
		
		
		// 更具最优数据所在数据中心的Url
//		this.dataPath = this.currentData.filepath;

		// test
		//System.out.println(this.optimalDataCenter.getHostName());
		//logger.info(">>>>>"+this.dataPath);
	}

	// 查询中心数据库，判断数据据是否可用，是返回true
	public boolean searchInCentralDB() {
		
		//此处改为数据分布的算法
		//没有缓存时应该选择一个比较近的数据中心作为下载的地方
		//或者网络状态比较好的地方，这个地方不确定到哪里
		//此处选为主中心作为
		
		/*
		 * 数据分布的算法
		 * 由于没有真正的数据中心，真正数据中心应该是数据已经基本分布的
		 * 更改此处设置可以，比如10.3.10.27上为HJ数据 10.3.10.28为FY数据
		*/
		
		String dataCenterIP = "10.3.10.27";
//		if (this.currentData.spacecraft.contains("FY")) {
//			dataCenterIP="10.3.10.27";
//		}
//		if (this.currentData.spacecraft.contains("HJ")) {
//			dataCenterIP="10.3.10.28";
//		}
		
		logger.info("数据中心IP为：" +dataCenterIP);
		
		// 利用IP正则表达式提取IP地址
//		String regIP = "^((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)$";
//		Pattern p1 = Pattern.compile(regIP);
//		String[] items = getNewDataUrl.split("/");
//		for (int i = 0; i < items.length; i++) {
//			Matcher m1 = p1.matcher(items[i]);
//			boolean rs1 = m1.matches();
//			if (rs1) {
//				dataCenterIP = items[i];
//				break;
//			}
//		}
		
		// 更新数据所在的Url
//		this.dataPath = getNewDataUrl.substring(
//				getNewDataUrl.indexOf(dataCenterIP), getNewDataUrl.length());
		// 找出当前数据所在的数据中心资源,放入候选资源中
		SystemResourceDB systemResourceDB = new SystemResourceDB();
		DataCenter tempDataCenter = systemResourceDB.getDataCenterListByIP(
				dataCenterIP).get(0);
		if (null == tempDataCenter) {
			return false;
		}
		this.candidateDataCenterList.add(tempDataCenter);
		this.dataStatus = "inCentralDB";
		return true;
	}
	
	
	public DataCenter doSchedule() {
		
		// 首先查询缓存库
		if (searchInCache()) {
			getOptimalDataCenter();
		}
		// 查询中心数据库
		else if (searchInCentralDB()) {
			getOptimalDataCenter();
		}
		// 数据获取失败 ？？？或者需要执行等待
		else {
			logger.error("数据不存在于缓存及中心数据库！");
		}
		return this.optimalDataCenter;
	}

	// 获取的最佳数据中心上
	public DataCenter getDataCenter() {
		return this.optimalDataCenter;
	}

	// 获取数据在最优数据中心上的位置
	public String getDataPath() {

		return this.dataPath;
	}

	public static void main(String[] args) {

	}

}
