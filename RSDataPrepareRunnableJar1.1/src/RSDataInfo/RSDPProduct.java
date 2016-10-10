package RSDataInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import DBSystem.RsDataCacheDB;
import LogSystem.SystemLogger;
import OrderManage.L4InternalOrder;
import RSDataInfo.RSData;

/**
 * 创建时间：2016年3月17日 下午10:06:58
 * 项目名称：RSDataPrepareRunnableJar2.1
 * 2016年3月17日
 * @author 张杰
 * @version 1.0
 * 文件名称：RSDPProduct.java
 * 类说明：
 */
public class RSDPProduct {

	// 四级订单
	public L4InternalOrder l4order = null;

	// 所需Data
	public ArrayList<RSData> DataList = null;

	// log4j日志系统
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	public RSDPProduct(L4InternalOrder l4order) {
		this.l4order = l4order;
		this.DataList = new ArrayList<>();
	}
	
	//获取结果
	public String getDPProducts() {
		
		String products="";
		
		// 去除重复的数据
		TreeSet<String> dataTreeSet = new TreeSet<String>();
		
		Iterator<String> it_datalist = l4order.dataList.iterator();
		while (it_datalist.hasNext()) {
			String dataId = (String) it_datalist.next();
			RSData rsData=getData(dataId);
			
			
			if (dataTreeSet.size() != 0
					&& dataTreeSet.contains(rsData.filename)) {
				continue;
			}
			dataTreeSet.add(rsData.filename);
			//Name=FY3AMERSI.1000.2014185032730.H00V00.031624.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2032,originName=FY3A_MERSI_GBAL_L1_20140704_0325_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140704_0325_1000M_MS.HDF/FY3AMERSI.1000.2014185032730.H00V00.031624.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014181025730.H00V00.031567.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2019,originName=FY3A_MERSI_GBAL_L1_20140630_0255_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140630_0255_1000M_MS.HDF/FY3AMERSI.1000.2014181025730.H00V00.031567.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014181030230.H00V00.031567.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2020,originName=FY3A_MERSI_GBAL_L1_20140630_0300_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140630_0300_1000M_MS.HDF/FY3AMERSI.1000.2014181030230.H00V00.031567.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014181044230.H00V00.031568.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2022,originName=FY3A_MERSI_GBAL_L1_20140630_0440_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140630_0440_1000M_MS.HDF/FY3AMERSI.1000.2014181044230.H00V00.031568.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3A_MERSI_GBAL_L1_20140701_0240_1000M_MS.HDF,workingStatus=Error,dataid=__RADI-SUPER_2023;Name=FY3AMERSI.1000.2014182042230.H00V00.031582.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2024,originName=FY3A_MERSI_GBAL_L1_20140701_0420_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140701_0420_1000M_MS.HDF/FY3AMERSI.1000.2014182042230.H00V00.031582.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014183022231.H00V00.031595.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2025,originName=FY3A_MERSI_GBAL_L1_20140702_0220_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140702_0220_1000M_MS.HDF/FY3AMERSI.1000.2014183022231.H00V00.031595.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014183040229.H00V00.031596.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2026,originName=FY3A_MERSI_GBAL_L1_20140702_0400_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140702_0400_1000M_MS.HDF/FY3AMERSI.1000.2014183040229.H00V00.031596.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3A_MERSI_GBAL_L1_20140703_0200_1000M_MS.HDF,workingStatus=Error,dataid=__RADI-SUPER_2027;Name=FY3AMERSI.1000.2014184020730.H00V00.031609.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2028,originName=FY3A_MERSI_GBAL_L1_20140703_0205_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140703_0205_1000M_MS.HDF/FY3AMERSI.1000.2014184020730.H00V00.031609.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014184034230.H00V00.031610.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2029,originName=FY3A_MERSI_GBAL_L1_20140703_0340_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140703_0340_1000M_MS.HDF/FY3AMERSI.1000.2014184034230.H00V00.031610.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014184034730.H00V00.031610.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2030,originName=FY3A_MERSI_GBAL_L1_20140703_0345_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140703_0345_1000M_MS.HDF/FY3AMERSI.1000.2014184034730.H00V00.031610.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3AMERSI.1000.2014185032230.H00V00.031624.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2031,originName=FY3A_MERSI_GBAL_L1_20140704_0320_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3A_MERSI_GBAL_L1_20140704_0320_1000M_MS.HDF/FY3AMERSI.1000.2014185032230.H00V00.031624.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014185065731.H00V00.018976.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2044,originName=FY3B_MERSI_GBAL_L1_20140704_0655_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140704_0655_1000M_MS.HDF/FY3BMERSI.1000.2014185065731.H00V00.018976.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014185065231.H00V00.018976.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2043,originName=FY3B_MERSI_GBAL_L1_20140704_0650_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140704_0650_1000M_MS.HDF/FY3BMERSI.1000.2014185065231.H00V00.018976.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3B_MERSI_GBAL_L1_20140703_0710_1000M_MS.HDF,workingStatus=Unknown,dataid=__RADI-SUPER_2042;Name=FY3BMERSI.1000.2014184053229.H00V00.018961.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2041,originName=FY3B_MERSI_GBAL_L1_20140703_0530_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140703_0530_1000M_MS.HDF/FY3BMERSI.1000.2014184053229.H00V00.018961.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014183073230.H00V00.018948.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2040,originName=FY3B_MERSI_GBAL_L1_20140702_0730_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140702_0730_1000M_MS.HDF/FY3BMERSI.1000.2014183073230.H00V00.018948.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014183055230.H00V00.018947.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2039,originName=FY3B_MERSI_GBAL_L1_20140702_0550_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140702_0550_1000M_MS.HDF/FY3BMERSI.1000.2014183055230.H00V00.018947.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014182075231.H00V00.018934.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2038,originName=FY3B_MERSI_GBAL_L1_20140701_0750_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140701_0750_1000M_MS.HDF/FY3BMERSI.1000.2014182075231.H00V00.018934.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014182061230.H00V00.018933.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2037,originName=FY3B_MERSI_GBAL_L1_20140701_0610_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140701_0610_1000M_MS.HDF/FY3BMERSI.1000.2014182061230.H00V00.018933.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014182060730.H00V00.018933.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2036,originName=FY3B_MERSI_GBAL_L1_20140701_0605_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140701_0605_1000M_MS.HDF/FY3BMERSI.1000.2014182060730.H00V00.018933.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014181081230.H00V00.018920.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2035,originName=FY3B_MERSI_GBAL_L1_20140630_0810_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140630_0810_1000M_MS.HDF/FY3BMERSI.1000.2014181081230.H00V00.018920.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014181080730.H00V00.018920.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2034,originName=FY3B_MERSI_GBAL_L1_20140630_0805_1000M_MS.HDF,url=10.3.10.1:/dataIO/863_Project/863-Daemon/SCA/IOServer-SSD/SCCPS/Orders/L3GN201603160001@IOServer-SSD/Products//FY3B_MERSI_GBAL_L1_20140630_0805_1000M_MS.HDF/FY3BMERSI.1000.2014181080730.H00V00.018920.hdf,ullat=,ullon=,lrlat=,lrlon=;Name=FY3BMERSI.1000.2014181062729.H00V00.018919.hdf,workingStatus=Finish,dataid=__RADI-SUPER_2033,originName=FY3B_MERSI_GBAL_L1_20140630_0625_1000M_MS.HDF,url=1
//			products+="Name="+rsData.filename+",workingStatus=Finish"+",dataid="+rsData.dataid+",url=";
			rsData.dataStatus="Avaliable";
			
			products+=rsData.getRSDataString();			
		}		
		
		return products;
	}
	
	
	// 获取数据对象
		private RSData getData(String dataId) {

			RSData rsData = new RSData();
			// 缓存库中查找数据
			RsDataCacheDB rsDataCacheDB = new RsDataCacheDB();
			ArrayList<RSData> rsDataLists = rsDataCacheDB.search(" where dataid='"
					+ dataId + "'");
			if (null != rsDataLists) {
				rsData = rsDataLists.get(0);
			}
			return rsData;
		}

}
