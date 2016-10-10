package DataService;

import java.io.File;

import DBManage.TestDBConnection;
import RSDataManage.RawDataPrepare;

/**
 * 创建时间：2016年5月5日 下午8:30:41
 * 项目名称：MultiProcessSysWebService_Thread_Online
 * 2016年5月5日
 * @author 张杰
 * @version 1.0
 * 文件名称：LocalRawDataService.java
 * 类说明：临时用途，由于数据服务频繁出错，主要完成将本地已下载的生产1KM NDVI的数据更新到临时缓存库中，并更新到请求订单中,用于全流程测试
 */
public class LocalRawDataService {
	
	public static void main(String[] args) {
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();
		
		//准备数据
		RawDataPrepare dp=new RawDataPrepare("QP_NDVI_1KM");
		File dirFile=new File("/dataIO/863_Project/863-Daemon/Project1DataService/1KM_NDVI_Data/");
		try {
			dp.showAllFiles(dirFile);			
			dp.saveMetaInfoToDB();
			System.out.println(dp.updateDatalist("L1CP201605050001L2CP001"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
