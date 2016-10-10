package RSDataManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import OrderManage.L3InternalOrder;
import TaskExeAgent.SystemLogger;

public class RSDataProcess {
	// 所需Data
	public ArrayList<RSData> DataList=new ArrayList<RSData>();
	

	private String datalists;

	// log4j日志系统
	private Logger logger = SystemLogger.getSysLogger();
	
	
	public RSDataProcess(String datalists) {
		this.datalists=datalists;
		
	}


	public boolean doProcess() {
		String info[]=datalists.split(";");
		
		for (int i = 0; i < info.length; i++) {
//			System.out.println(">>"+info[i]);
			RSData data =new RSData(info[i]);
			this.DataList.add(data);		
		}		
		return true;
	}

	// 获取数据对象
	private RSData getData(String dataInfo) {
		// 首先获取bounding内容
		String[] splitBySingleQuotation = dataInfo.split("'");
		String bounding = splitBySingleQuotation[1];
		// 由于bounding中逗号对分割不便，需要替换掉bounding内容
		dataInfo = dataInfo.replace(bounding, "Polygon");
		// 利用“,”拆分，
		String[] dataItems = dataInfo.split(",");
		HashMap<String, String> CurrentDataMap = new HashMap<String, String>(
				dataItems.length);

		String[] queryStringParam;
		for (String qs : dataItems) {
			queryStringParam = qs.split("=");
			// System.out.println(queryStringParam[0]+" "+
			// queryStringParam[1]);
			CurrentDataMap.put(queryStringParam[0], queryStringParam[1]);
		}
		// 将bounding内容替换为原来值
		CurrentDataMap.put("bounding", "'" + bounding + "'");

		RSData rsData = new RSData(CurrentDataMap);

		return rsData;
	}
	
	public ArrayList<RSData> getDataList() {
		return DataList;
	}
	
	public String getDataListString() {
		StringBuilder dataListString=new StringBuilder();
		Iterator<RSData> iterator =DataList.iterator();
		while (iterator.hasNext()) {
			RSData rsData = (RSData) iterator.next();
			dataListString.append(rsData.getRSDataString());			
		}
		return dataListString.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String datalistsString="Name=MuSyQ.VI.1km.2014021000000.H33V09.001.h5,dataid=,url=/public/MuSyQ/NDVI/2014/021/MuSyQ.VI.1km.2014021000000.H33V09.001.h5;Name=MuSyQ.VI.1km.2014026000000.H33V09.001.h5,dataid=,url=/public/MuSyQ/NDVI/2014/026/MuSyQ.VI.1km.2014026000000.H33V09.001.h5;Name=MuSyQ.VI.1km.2014031000000.H33V09.001.h5,dataid=,url=/public/MuSyQ/NDVI/2014/031/MuSyQ.VI.1km.2014031000000.H33V09.001.h5;Name=MuSyQ.VI.1km.2014036000000.H33V09.001.h5,dataid=,url=/public/MuSyQ/NDVI/2014/036/MuSyQ.VI.1km.2014036000000.H33V09.001.h5;";

		RSDataProcess dataProcess=new RSDataProcess(datalistsString);
		dataProcess.doProcess();
		System.out.println(dataProcess.DataList.size());
		
		Iterator<RSData> iterator=dataProcess.DataList.iterator();
		while (iterator.hasNext()) {
			RSData rsData = (RSData) iterator.next();
			
			System.out.println(rsData.getRSDataString());
		}
		
	}

}
