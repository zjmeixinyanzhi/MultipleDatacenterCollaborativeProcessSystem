package DBManage;

import OrderManage.L3InternalOrder;
import OrderManage.OrderRequest;
import SystemManage.SystemConfig;

public class TestDBConnection {

	// 随时从配置文件中加载数据库连接，用于平时调试数据库连接
	public void GetConnection() {
		String configFile = "/home.bak/MCA/Software/apache-tomcat-7.0.55/webapps/MultiProcessSysWebService_Thread/config.property";

		// 载入系统基本配置信息
		SystemConfig.setConfigFile(configFile);
		SystemConfig.loadSystemConfig();
		// 载入其他全部配置信息
		SystemConfig.loadBasicConfig();
//		RequestDB requestDB=new RequestDB();
//		OrderRequest request=requestDB.getOrder("");
//		System.out.println(request.dataList);
	}

	public static void main(String[] args) {
		TestDBConnection test=new TestDBConnection();
		test.GetConnection();
		RequestDB requestDB=new RequestDB();
		
		OrderRequest orderRequest=requestDB.getOrder("L1CP201603080001L2CP001");
		System.out.println(orderRequest.getStatus());

	}

}
