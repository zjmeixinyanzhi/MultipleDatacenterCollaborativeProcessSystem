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

import FileOperations.CompressingTools;


/**
 * 创建时间：2016年3月17日 下午7:38:16
 * 项目名称：TaskExecutionAgent2.0
 * 2016年3月17日
 * @author 张杰
 * @version 1.0
 * 文件名称：RSProductData.java
 * 类说明：遥感产品类
 */
public class RSProductData {
	// 数据文件名称
	public String filename = "";
	// 文件路径
	public String filepath = "";
	// 数据ID
	public String dataid = "";
	
	//经纬度坐标
	
	public String ULLong="";
	public String LRLat="";
	public String LRLong="";
	public String ULLat="";
	public RSProductData() {

	}

	public RSProductData(HashMap<String, String> dataMap) {

	}

	// 只有四个属性：Name、url、Rows、Samples、ULX、ULY
	// Name=MOD03.A2009001.0140.005.2010232224042.hdf,url=10.3.11.101:/public/testData/MOD03.A2009001.0140.005.2010232224042.hdf,Rows=38.4886,Samples=117.604,ULX=38.4886,ULY=117.604;
	public RSProductData(String strData) {

		if (strData.contains(";")) {
			strData = strData.replaceAll(";", "");
		}

		String[] keyValues = strData.split(",");

		for (int i = 0; i < keyValues.length; i++) {
			String key = keyValues[i].split("=")[0];
			String value = keyValues[i].split("=")[1];
//			 System.out.println(key + " " + value);

			if (key.equals("Name")) {
				this.filename = value;
			}
			if (key.equals("url")) {
				this.filepath = value;
			}
			if (key.equals("dataid")) {
				this.dataid = value;
			}
			if (key.equals("ULLat")) {
				this.ULLat= value;
			}
			if (key.equals("ULLong")) {
				this.ULLong = value;
			}
			if (key.equals("LRLat")) {
				this.LRLat = value;
			}
			if (key.equals("LRLong")) {
				this.LRLong = value;
			}
		}

		// 处理字符串

	}

	// 数据条目转为字符串
	public String getRSDataString() {
		String dataString = "Name=" + this.filename + ",dataid=" + this.dataid
				+ ",url=" + this.filepath + ";";

		return dataString;
	}

	// 获取数据路径
	public String getDataUrl() {
		return this.filepath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}

	public String getULLat() {
		return ULLat;
	}

	public void setULLat(String uLLat) {
		ULLat = uLLat;
	}

	public String getULLong() {
		return ULLong;
	}

	public void setULLong(String uLLong) {
		ULLong = uLLong;
	}

	public String getLRLat() {
		return LRLat;
	}

	public void setLRLat(String lRLat) {
		LRLat = lRLat;
	}

	public String getLRLong() {
		return LRLong;
	}

	public void setLRLong(String lRLong) {
		LRLong = lRLong;
	}


	// 获取数据路径:不包含IP
	public String getDataUrlWithoutIP() {
		if (this.filepath.contains(":")) {
			String[] infos = this.filepath.split(":");
			// System.out.println(infos.length + " " + infos[0] + "\n" +
			// infos[1]);
			// System.out.println(isboolIp("192.168.25.132"));

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
		;
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	public static void main(String[] args) {

		RSProductData data = new RSProductData("Name=FY3AMERSI.1000.2014181030230.H00V00.031567.hdf,ULLat=43.504997,ULLong=85.074997,LRLat=21.924999,LRLong=120.144997,url=/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140630_0300_1000M_MS.HDF/FY3AMERSI.1000.2014181030230.H00V00.031567.hdf");
//		data.filepath = "10.3.10.1:/dataIO/863_Project/863-Daemon/Project1DataService/testData_GN/HJ1A-CCD1-13-92-20140412-L20001142931/HJ1A-CCD1-13-92-20140412-L20001142931-4.TIF";
		System.out.println(data.getDataUrl());
		System.out.println(data.getDataUrlWithoutIP());
		System.out.println(data.getLRLong());

	}

}
