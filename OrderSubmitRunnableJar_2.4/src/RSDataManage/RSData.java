package RSDataManage;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.internal.compiler.ast.ThisReference;

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
	public String dataid;
	// 数据文件ID号
	public int fileid = 0;
	// 数据文件名称
	public String filename = "";
	
	//数据状态
	public String dataStatus="unKnown";
	
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

	public RSData() {

	}

	public RSData(HashMap<String, String> dataMap) {
		this.DataMap = dataMap;

		// 缓存库表项与数据记录中信息不对应，filename=Name filePath=url
		this.dataid=dataMap.get("dataid");
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
		
//		System.out.println(dataMap.get("datastatus"));
		
		if (dataMap.get("datastatus")==null||dataMap.get("datastatus").equals("Error")) {
			this.dataStatus="unAvaliable";
		}
		else if (dataMap.get("datastatus").equals("Finish")||dataMap.get("datastatus").equals("Success")||dataMap.get("datastatus").equals("Avaliable")) {
			this.dataStatus="Avaliable";
		}
		
		
//		System.out.println(this.filepath);

		// 拆分filePath获取数据中心条目
		
		if (this.filepath==null) {
			
		}
		else if (!(this.filepath.equals(""))) {
			String[] fileUrl = this.filepath.split("/");
			this.datacenter = fileUrl[0];
		}

	}

	// 数据条目转为字符串
	public String getRSDataString() {
		System.out
				.println("RSData::public String getRSDataString( ) | 获取数据条目字符串");

		String dataString = "Name=" + this.filename + ",dataid=" + this.dataid +",url=" + this.filepath
				+ ",createtime=" + this.createtime + ",spacecraft="
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
	
	// 获取数据路径
	public String getDataUrl() {
		return this.filepath;
	}
	
	// 获取数据路径:不包含IP
	public String getDataUrlWithoutIP() {
		if (this.filepath==null) {
			return null;
		}
		if (this.filepath.contains(":")) {
			String[] infos = this.filepath.split(":");
			
			if (isboolIp(infos[0]) && infos.length == 2) {
				this.filepath = infos[1];

			}
		}
		// test
		// System.out.println(this.filepath);
		return this.filepath;
	}
	
	// 判斷是否包含IP
	public boolean isboolIp(String ipAddress) {
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
	
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	public static void main(String[] args) {

		RSData data = new RSData();
		System.out.println(data.getRSDataString());

	}

	
	//将本地路径文件转换未FTP地址
	public String getDataUrl2() {
		
		File file = new File(this.filepath);
		String strParentDirectory =file.getParent();
		
		String [] files=strParentDirectory.split("/");
		String currParentDir=files[files.length-1];
		
		return "ftp://ftpuser:123456@124.16.184.69/StandardProductBuffer/"+currParentDir+"/"+file.getName();
	}
	

}
