package RSDataManage;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

import DBManage.L3OrderDB;
import DBManage.RequestDB;
import DBManage.RsDataCacheDB;
import DataService.DataAcquire;
import OrderManage.OrderRequest;

/**
 * 创建时间：2016年5月5日 下午9:38:18
 * 项目名称：MultiProcessSysWebService_Thread_Online
 * 2016年5月5日
 * @author 张杰
 * @version 1.0
 * 文件名称：RawDataPrepare.java
 * 类说明：临时用途，由于数据服务频繁出错，主要完成将本地已下载的生产1KM NDVI的数据更新到临时缓存库中，并更新到请求订单中
 */
public class RawDataPrepare {
	//有序
	public TreeMap<String,String> dataMaps=new TreeMap<String,String>();
	
	public L3OrderDB l3OrderDB=new L3OrderDB();
	//
	public String productTag;
	
	//
	public static int count=0;
	public static ArrayList<RSData> rsDataListsArrayList=new ArrayList<RSData>();
	
	public static ArrayList<String> rsDataIdLists=new ArrayList<String>();
	
	
	public RawDataPrepare(String flag) {
		this.productTag=flag;
	}
	
	//插入到缓存库备用缓存库rsdatacachedb2
	public boolean saveMetaInfoToDB() {
		boolean isSuccess=true;
		
		RsDataCacheDB rsDataCacheDB = new RsDataCacheDB();
//		rsDataCacheDB.setDbTable("mccps.rsdatacachedb2");
		
		Iterator<RSData> it=rsDataListsArrayList.iterator();		
		
		while (it.hasNext()) {
			RSData rsData = (RSData) it.next();
			try {
				rsDataCacheDB.addData2(rsData);		
				rsDataCacheDB.updateDataAcquireInfos(rsData.dataid, rsData.filename, rsData.dataStatus, rsData.filepath, (long)rsData.datasize, rsData.filepath);
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				isSuccess&=false;
				continue;
				
			}	
		}		
		return isSuccess;
	}
	
	
	//更新请求订单所需数据信息
	public boolean updateDatalist(String requestId) {
		
		RequestDB requestDB=new RequestDB();
		
		try {
			requestDB.setDataStatus(requestId, "Available",rsDataIdLists );			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}		
		return true;
		
	}
	
	//获取四角经纬度	
	//递归列出文件
	public  void showAllFiles(File dir) throws Exception {
		File[] fs = dir.listFiles();
		
		for (int i = 0; i < fs.length; i++) {
//			System.out.println(fs[i].getAbsolutePath());
			if (!fs[i].isDirectory()) {
				count++;
				
				RSData rsData=new RSData();
				
				rsData.dataid=this.productTag+"_"+fs[i].getName().toUpperCase();
				rsData.filename=fs[i].getName();
				rsData.filepath="10.3.10.27:"+dir.getPath()+"/"+fs[i].getName();
				rsData.dataStatus="Available";
				rsData.datasize=fs[i].length();
				rsData.createtime="2014-07-03 03:40:01";
				
				if (rsData.filename.contains("MOD")) {
					rsData.sensor="MODIS";
					rsData.spacecraft="TERRA";
				}
				else if (rsData.filename.contains("MYD")) {
					rsData.sensor="MODIS";
					rsData.spacecraft="AQUA";
				}else if(rsData.filename.contains("FY3A_MERSI")) {
					rsData.sensor="MERSI";
					rsData.spacecraft="FY3A";
				}
				else if(rsData.filename.contains("FY3B_MERSI")) {
					rsData.sensor="MERSI";
					rsData.spacecraft="FY3B";
				}
				else if(rsData.filename.contains("MCD12Q1")) {
					rsData.sensor="MODIS_MCD12Q1";
					rsData.spacecraft="MODIS";
				}
				else if(rsData.filename.contains("MCD43B1")) {
					rsData.sensor="MODIS_MCD43B1";
					rsData.spacecraft="MODIS";
				}
				else if(rsData.filename.contains("MCD43B3")) {
					rsData.sensor="MODIS_MCD43B3";
					rsData.spacecraft="MODIS";
				}
				else if(rsData.filename.contains("MCD43C2")) {
					rsData.sensor="MODIS_MCD43C2";
					rsData.spacecraft="MODIS";
				}
				
				rsDataIdLists.add(rsData.dataid);
				rsDataListsArrayList.add(rsData);
				
			}		
			else if (fs[i].isDirectory()) {
				try {
					showAllFiles(fs[i]);
				} catch (Exception e) {
				}
			}
		}
	}


	
	
	
	
	

	public static void main(String[] args) {
		
		RawDataPrepare dp=new RawDataPrepare("");
		
		
		File dirFile=new File("/dataIO/863_Project/863-Daemon/MCAPublicBuffer/StandardProductBuffer/L3CP201604220001/");
		try {
			dp.showAllFiles(dirFile);
			dp.updateDatalist("");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Iterator<String> it=dp.dataMaps.keySet().iterator();
//		
//		while (it.hasNext()) {
//			String key = (String) it.next();
//			System.out.println(key+" "+dp.dataMaps.get(key));			
//		}
		
		
		

	}

}
