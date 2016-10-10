package RSDataInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间：2015-1-30 上午9:38:43 项目名称：MCA_KeplerWorkflow_Engine 2015-1-30
 * 
 * @author 张杰
 * @version 1.0 文件名称：DataInfo.java 类说明：负责缓存数据信息提取
 */
public class RSData {
	// 数据元数据信息集合
	HashMap<String, String> DataMap = new HashMap<String, String>();
	// 数据文件ID号
	public int fileid = 0;
	// 数据文件ID号
	public String dataid;
	// 数据文件名称
	public String filename = "";
	// 创建时间
	public String createtime = "2011-06-20 06:00:19";
	// 卫星平台
	public String spacecraft = "";
	// 传感器
	public String sensor = "";
	// 几何分辨率
	public double resolution = 0;
	// 四角坐标
	public double ullat = 0;
	public double ullon = 0;
	public double urlat = 0;
	public double urlon = 0;
	public double lllat = 0;
	public double lllon = 0;
	public double lrlat = 0;
	public double lrlon = 0;
	// 空间范围
	public String bounding = "";
	// 数据格式
	public String format = "";
	// 数据文件大小
	public double datasize = 0;
	// 数据提供商
	public String provider = "";
	// 含云量
	public double cloudcover = 0;
	// 文件路径
	public String filepath = "";
	// 数据请求信息
	public String requestInfo = "";
	// 所在的数据中心
	public String datacenter = "";
	//数据状态
	public String dataStatus="unKnown";

	public RSData() {

	}

	public RSData(HashMap<String, String> dataMap) {
		this.DataMap = dataMap;

		// 缓存库表项与数据记录中信息不对应，filename=Name filePath=url
		this.dataid=DataMap.get("dataid");
		this.filename = DataMap.get("Name");
		this.spacecraft = DataMap.get("spacecraft");
		this.sensor = DataMap.get("sensor");
		this.resolution = getValue(DataMap.get("resolution"));
		this.createtime = DataMap.get("createtime");


		this.ullat = getValue(DataMap.get("ullat"));
		this.ullon = getValue(DataMap.get("ullon"));
		this.urlat = getValue(DataMap.get("urlat"));
		this.urlon = getValue(DataMap.get("urlon"));
		this.lllat = getValue(DataMap.get("lllat"));
		this.lllon = getValue(DataMap.get("lllon"));
		this.lrlat = getValue(DataMap.get("lrlat"));
		this.lrlon = getValue(DataMap.get("lrlon"));

		this.datasize = getValue(DataMap.get("datasize"));
		this.cloudcover = getValue(DataMap.get("cloudcover"));

		this.bounding = DataMap.get("bounding");
		this.provider = DataMap.get("provider");
		this.requestInfo = DataMap.get("requestInfo");
		this.filepath = DataMap.get("url");
		this.format = DataMap.get("format");
		this.dataStatus=dataMap.get("datastatus");

		// 拆分filePath获取数据中心条目
//		if (!(this.filepath.equals(""))) {
//			String[] fileUrl = this.filepath.split("/");
//			this.datacenter = fileUrl[0];
//		}

	}

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

	// 数据条目转为字符串
	public String getRSDataString() {
		System.out
				.println("RSData::public String getRSDataString( ) | 获取数据条目字符串");
		// Name=AQUA_2015_01_20_05_53_GZ.MOD021KM.hdf,url=10.3.10.27/home/DCA/dataCenterCacheDir_01/AQUA_2011_06_20_05_53_GZ.MOD021KM.hdf,createtime=2015-01-20
		// 06:00:19.0,spacecraft=AQUA,sensor=MODIS,resolution=1000,ullat=38.9691,ullon=92.174,urlat=42.8499,urlon=119.422,lllat=1.91512,lllon=104.486,lrlat=4.87663,lrlon=125.193,bounding='Polygon((-180
		// -90,180 -90,180 90,-180 90,-180
		// -90))',format=HDF,datasize=730506076,provider=CEODE,cloudcover=0.15,filepath=10.3.10.1/home/MCA/dataCenterCacheDir_MCA/AQUA_2015_01_20_05_53_GZ.MOD021KM.hdf;";

		String dataString = "Name=" + this.filename + ",url=" + this.filepath
				+ ",dataid=" + this.dataid+",createtime=" + this.createtime + ",spacecraft="
				+ this.spacecraft + ",sensor=" + this.sensor + ",resolution="
				+ this.resolution + ",ullat=" + this.ullat + ",ullon="
				+ this.ullon + ",urlat=" + this.urlat + ",urlon=" + this.urlon
				+ ",lllat=" + this.lllat + ",lllon=" + this.lllon + ",lrlat="
				+ this.lrlat + ",lrlon=" + this.lrlon + ",bounding="
				+ this.bounding + ",format=" + this.format + ",datasize="
				+ this.datasize + ",provider=" + this.provider + ",cloudcover="
				+ this.cloudcover + ",filepath=" + this.filepath +",datastatus=" + this.dataStatus + ";";

		return dataString;
	}

	// 获取数据路径
	public String getDataUrl() {
		return this.filepath;
	}

	// 更新数据请求信息
	public void updateDataInfo(String DataCenter, String filePath) {
		System.out
				.println("RSData::public void updateDataInfo(String DataCenter, String filePath) | 更新数据请求信息");
		// 无数据请求信息时，新增数据请求信息
		if (this.requestInfo == null || this.requestInfo.equals("")) {
			// 新增请求信息
			RSDataRequestInfo dataRequestInfo = new RSDataRequestInfo();
			dataRequestInfo.insertRequestInfo(DataCenter, filePath);
			this.requestInfo = dataRequestInfo.getDataRequestInfoString();
		} else {
			// 有数据请求记录，需要更新对应数据中心的请求信息
			// 判断有多少个请去记录
			String[] DataRequests = this.requestInfo.split(";");
			for (String currentRequest : DataRequests) {
				RSDataRequestInfo dataRequestInfo = new RSDataRequestInfo(
						this.requestInfo);
				// 数据请求发生在同一数据中心时，更新请求次数与最后请求时间
				// 追加请求信息
				if (dataRequestInfo.datacenter.equals(DataCenter)) {
					dataRequestInfo.updateRequestInfo(DataCenter, filePath);
					this.requestInfo = dataRequestInfo
							.getDataRequestInfoString();
				}
				// 不在同一数据中心上，需要追加新的请求记录，用;区分
				else {
					RSDataRequestInfo dataRequestInfo2 = new RSDataRequestInfo();
					dataRequestInfo2.insertRequestInfo(DataCenter, filePath);
					this.requestInfo += dataRequestInfo
							.getDataRequestInfoString();
				}
			}
		}
	}

	public static void main(String[] args) {

		RSData data = new RSData();
		System.out.println(data.getRSDataString());

	}

}
