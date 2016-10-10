package MainSystem;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sun.org.apache.regexp.internal.recompile;

import DBSystem.DBConn;
import DBSystem.L3OrderDB;
import DBSystem.L4OrderDB;
import DBSystem.RsDataCacheDB;
import DBSystem.SystemConfigDB;
import LogSystem.SystemLogger;
import OrderManage.L4InternalOrder;
import RSDataInfo.RSDPProduct;
import RSDataInfo.RSData;
import RSDataInfo.RSDataCacheInfoFeedback;
import RSDataInfo.RSDataInspection;
import RSDataInfo.RSDataRequestInfo;

/**
 * 创建时间：2015-8-8 下午4:02:44 项目名称：RSDataPrepareRunnableJar2.0 2015-8-8
 * 
 * @author 张杰
 * @version 1.0 文件名称：TestMain.java 类说明： 判断数据是否真的存在 利用Gfarm客户端、或者远程登陆、或者GRAM
 *          如过离线数据，需要传输，需要传输到指定的目录里面 并更新缓存库 状态更新，更新四级订单状态、三级订单状态、二级订单状态
 *          设计具有日志功能：记录工作流运行错误
 */
public class TestMain {
	public static void main(String[] args) {

		// 参数1为数据库连接参数
		// String DBConnectionPara = "10.3.10.1_3306_mccps_mca_mca";
		String DBConnectionPara = args[0];
		String[] DBConnection = DBConnectionPara.split("_");
		if (DBConnection.length != 5) {
			System.out.println("<Error>数据库连接参数出错！");
			return;
		}
		
		DBConn conn = new DBConn(DBConnection[0],
				DBConnection[1], DBConnection[2], DBConnection[3],
				DBConnection[4]);
		
		Logger logger=SystemLogger.getInstance().getSysLogger();
		
		L3OrderDB l3OrderDB = new L3OrderDB();				
		
		//当前三级订单ID
		String l3DPId=null;
		//所有数据是否存在
		boolean allDataFilesIsExist=true;
		String l4OrderLists=args[1];
		
		logger.info("_______"+l4OrderLists+"数据开始准备_______");
		
		//参数二为三级订单的四级订单列表：L3DP201508050001@slave1;L3DP201508050001@master;
		String[] l4Orders=args[1].split(";");
		for (int i = 0; i < l4Orders.length && l4Orders[i].contains("L3DP"); i++) {
			//logger.info(l4Orders[i]);
			//查询订单所需数据，判断数据文件是否真正存在，不存在退出
			L4OrderDB l4OrderDB=new L4OrderDB();	
			ArrayList<L4InternalOrder> l4InternalOrders=l4OrderDB.search("WHERE JobId='"+l4Orders[i]+"'");
			Iterator<L4InternalOrder> it_l4InternalOrders=l4InternalOrders.iterator();
			while (it_l4InternalOrders.hasNext()) {
				L4InternalOrder l4InternalOrder = (L4InternalOrder) it_l4InternalOrders
						.next();
				//logger.info(l4InternalOrder.dataList);				
				l3DPId=l4InternalOrder.jobId_L3;				
				//当前四级订单不存在的数据名称
				String notExistFileName="";
								
				//拆分数据订单、确定URL，判断数据文件是否存在，不存在，更新数据状态
				Iterator<String> it_datalist=l4InternalOrder.dataList.iterator();
				while (it_datalist.hasNext()) {
					String dataid = (String) it_datalist.next();	
					
					RSDataInspection inspection=new RSDataInspection(dataid);
					boolean dataIsExsit=inspection.checkDataExistence();
					if (dataIsExsit) {
						// 查询缓存库，判断数据是否已经缓存
						//更新缓存数据请求信息：更新最后请求时间、请求次数
						RSDataCacheInfoFeedback feedback=new RSDataCacheInfoFeedback(inspection.currentRsData);
						feedback.infoFeedback();
						
					}else {
						//数据不存在：
						logger.info(l3DPId+":"+dataid+"数据不存在！");
						notExistFileName+="CurrentFileName";
						notExistFileName+="&";						
					}
					allDataFilesIsExist&=dataIsExsit;
				}
				//更新四级订单库数据状态
				System.out.println(notExistFileName);
				if (notExistFileName.equals("")) {
					logger.info("l3DPId");
					
					//更新产品列表
					//数据存在，更新数据请求状态
					RSDPProduct rsdpProduct=new RSDPProduct(l4InternalOrder);
//					System.out.println(rsdpProduct.getDPProducts()+"\n ");
					l4OrderDB.setDataProductList(l4InternalOrder.jobId,rsdpProduct.getDPProducts());
					
					l4OrderDB.setDataStatus(l4InternalOrder.jobId, "Available");
					l4OrderDB.setOrderWorkingStatus(l4InternalOrder.jobId, "Finish");
				}
				else {
					logger.error(l3DPId+"共有以下数据不存在:\n"+notExistFileName+"数据不存在！");
					l4OrderDB.setDataStatus(l4InternalOrder.jobId, "notAvailable:"+notExistFileName);
					l4OrderDB.setOrderWorkingStatus(l4InternalOrder.jobId, "Error");					
				}				
			}				
		}
		//更新三级订单库：数据状态、订单状态
		if (allDataFilesIsExist) {
			l3OrderDB.setDataStatus(l3DPId, "Available");
		    l3OrderDB.setOrderWorkingStatus(l3DPId, "Finish");
		}
		else {
			l3OrderDB.setDataStatus(l3DPId, "unAvailable");
		    l3OrderDB.setOrderWorkingStatus(l3DPId, "Error");
		}
		
		logger.info("_______"+l4OrderLists+"数据准备结束_______");
		
		//关闭数据库连接
		L3OrderDB.closeConnected();
		L4OrderDB.closeConnected();
		RsDataCacheDB.closeConnected();
	}

}
