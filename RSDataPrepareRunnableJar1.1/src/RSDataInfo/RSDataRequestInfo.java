package RSDataInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import LogSystem.SystemLogger;

/**
 * 创建时间：2015-3-25 下午5:09:31 项目名称：RSDataCacheRunnableJar 2015-3-25
 * 
 * @author 张杰
 * @version 1.0 文件名称：RSDataRequestInfo.java 类说明：
 */
public class RSDataRequestInfo {
	private HashMap<String, String> requestMap = new HashMap<>();
	public String datacenter = null;
	public String filepath = null;
	public int requesttimes = 0;
	public String lastrequesttime = "1970-01-01 00:00:00";

	private Logger logger = SystemLogger.getInstance().getSysLogger();

	// 数据请求信息为空的构造函数
	public RSDataRequestInfo() {

	}

	// 数据请求信息不为空的构造函数
	public RSDataRequestInfo(String requestInfo) {
		// 分割字符串，提取datacenter,filePath,requesttimes,lastrequesttime等信息
		// datacenter=10.3.10.1,filepath=/home/MCA/dataCenterCacheDir_MCA/AQUA_2011_06_20_05_53_GZ.MOD021KM.hdf,requesttimes=5,lastrequesttime=2014-12-20
		// 06:00:19;
		String[] requestItems = requestInfo.split(",");
		for (String Item : requestItems) {
			String[] ItemsKeyValues = Item.split("=");
			requestMap.put(ItemsKeyValues[0], ItemsKeyValues[1]);
		}

		this.datacenter = requestMap.get("datacenter");
		this.filepath = requestMap.get("filepath");
		this.requesttimes = Integer.parseInt(requestMap.get("requesttimes"));
		this.lastrequesttime = requestMap.get("lastrequesttime");
	}

	// 初始化请求信息
	public void insertRequestInfo(String datacenter, String filepath) {
		logger.info("RSData::public void insertRequestInfo(String datacenter, String filepath) | 插入数据缓存信息");
		// 需要校验文件路径格式，请求信息中路径去除数据中心
		this.filepath = filepath;

		this.datacenter = datacenter;
		this.requesttimes = 1;
		// 请求时间改为当前时间
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new Date(System.currentTimeMillis());
		this.lastrequesttime = format.format(date);

	}

	// 更新数据缓存信息:请求次数加一，最后更新时间
	public void updateRequestInfo() {
		// logger.info("RSData::public void updateRequestInfo(String datacenter, String filepath) | 更新数据缓存信息");
		// 位于同一数据中心的同一位置，filepath追加新的位置,用&区分
		// 请求次数加1
		this.requesttimes += 1;
		// 请求时间改为当前时间
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new Date(System.currentTimeMillis());
		this.lastrequesttime = format.format(date);

	}
	
	// 更新数据缓存信息
	public void updateRequestInfo(String datacenter, String filepath) {
		System.out
		.println("RSData::public void updateRequestInfo(String datacenter, String filepath) | 更新数据缓存信息");
		//位于同一数据中心的同一位置，filepath追加新的位置,用&区分
		if (!this.filepath.equals(filepath)) {
//			this.filepath += "&";
			this.filepath = filepath;
		}
		// 请求次数加1
		this.requesttimes += 1;
		// 请求时间改为当前时间
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new Date(System.currentTimeMillis());
		this.lastrequesttime = format.format(date);

	}

	// 获取当前数据缓存信息字符串
	public String getDataRequestInfoString() {
		logger.info("RSData::public String getDataRequestInfoString() | 获取当前数据缓存信息字符串");
		String requestInfoString = "datacenter=" + this.datacenter
				+ ",filepath=" + this.filepath + ",requesttimes="
				+ this.requesttimes + ",lastrequesttime="
				+ this.lastrequesttime + ";";
		return requestInfoString;
	}

}
