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
 * 创建时间：2015-1-30 上午9:38:43 项目名称：MCA_KeplerWorkflow_Engine 2015-1-30
 * 
 * @author 张杰
 * @version 1.0 文件名称：DataInfo.java 类说明：负责缓存数据信息提取
 */
public class RSData {
	// 数据文件名称
	public String filename = "";
	// 文件路径
	public String filepath = "";
	// 数据ID
	public String dataid = "";
	//辅助数据（只针对融合同化订单）
	public String auxDatas="";

	public RSData() {

	}

	public RSData(HashMap<String, String> dataMap) {

	}

	// 只有四个属性：Name、url、Rows、Samples、ULX、ULY
	// Name=MOD03.A2009001.0140.005.2010232224042.hdf,url=10.3.11.101:/public/testData/MOD03.A2009001.0140.005.2010232224042.hdf,Rows=38.4886,Samples=117.604,ULX=38.4886,ULY=117.604;
	public RSData(String strData) {

		if (strData.contains(";")) {
			strData = strData.replaceAll(";", "");
		}

		String[] keyValues = strData.split(",");

		for (int i = 0; i < keyValues.length; i++) {
			String key ="";
//			System.out.println(keyValues[i]);
			String[] kvStrings=keyValues[i].split("=");
			if ( kvStrings.length==2 &&null!=kvStrings[0]) {
				key= kvStrings[0];
			}
			
			String value="";
			if (kvStrings.length==2&& kvStrings[1]!=null) {
				value=kvStrings[1];
			}			
			// System.out.println(key + " " + value);

			if (key.equals("Name")) {
				this.filename = value;
			}
			if (key.equals("url")) {
				this.filepath = value;
			}
			if (key.equals("dataid")) {
				this.dataid = value;
			}
			if (key.equals("RetrievalDataProducts")) {
				this.auxDatas=value;
			}
		}

		// 处理字符串

	}

	// 数据条目转为字符串
	public String getRSDataString() {
		String dataString = "Name=" + this.filename + ",dataid=" + this.dataid
				+ ",url=" + this.filepath+ ",RetrievalDataProducts=" + this.auxDatas + ";";
		return dataString;
	}

	// 获取数据路径
	public String getDataUrl() {
		return this.filepath;
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

		
		RSData data = new RSData("Name=FY3AMERSI.1000.2014181030230.H00V00.031567.hdf,ULLat=43.504997,ULLong=85.074997,LRLat=21.924999,LRLong=120.144997,url=/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140630_0300_1000M_MS.HDF/FY3AMERSI.1000.2014181030230.H00V00.031567.hdf");
		
		
//		data.filepath = "10.3.10.1:/dataIO/863_Project/863-Daemon/Project1DataService/testData_GN/HJ1A-CCD1-13-92-20140412-L20001142931/HJ1A-CCD1-13-92-20140412-L20001142931-4.TIF";
		System.out.println(data.getDataUrl());
		System.out.println(data.getDataUrlWithoutIP());
		System.out.println(data.getRSDataString());
		

	}

}
