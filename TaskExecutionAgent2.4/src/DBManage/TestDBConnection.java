package DBManage;

import java.util.ArrayList;
import java.util.Iterator;

import OrderManage.L3InternalOrder;
import Pbs.PbsOrder;
import TaskExeAgent.SystemConfig;
import TaskSchedular.TaskExecuter;
public class TestDBConnection {

	// 随时从配置文件中加载数据库连接，用于平时调试数据库连接
	public void GetConnection() {
//		String configFile = "/home.bak/MCA/Software/apache-tomcat-7.0.55/webapps/MultiProcessSysWebService_Thread/config.property";

		System.out.println("1.初始化：加载系统配置");
		// 载入系统基本配置信息
		String configFile = "/home.bak/MCA/Software/apache-tomcat-7.0.55_sccps_CNIC/webapps/TaskExecutionAgent/config.property";
		SystemConfig.setConfigFile(configFile);
		// !!!SystemConfig.loadSysConfig();
		SystemConfig systemConfig = new SystemConfig();

		if (!systemConfig.loadSystemConfig()) {
			System.out.println("加载系统环境变量失败！");
		}
		// 载入其他全部配置信息
		boolean flag = systemConfig.loadBasicConfig();
		
		// 载入系统基本配置信息
//		SystemConfig.setConfigFile(configFile);
//		SystemConfig.loadSystemConfig();
//		// 载入其他全部配置信息
//		SystemConfig.loadBasicConfig();
	}
	
	public boolean deleOrderInfos(String l3OrderId) {
		if (l3OrderId==null) {
			return false;
		}		
		
		L3OrderDB l3OrderDB=new L3OrderDB();		
		L3InternalOrder l3InternalOrder=l3OrderDB.search("JobId='"
				+ l3OrderId
				+ "'").get(0);
		
		if (l3InternalOrder==null) {
			System.out.println(l3OrderId+"订单为空！");
			return false;
		}
		
		
		if (l3InternalOrder.pbsOrderLists==null) {
			System.out.println("Pbs订单为空！");
		}
		else {
			//删除PBS订单
			PBSOrderDB pbsOrderDB=new PBSOrderDB();
			ArrayList<String> pbsOrderLists=l3InternalOrder.pbsOrderLists;
			pbsOrderDB.deleteOrder(pbsOrderLists);
			System.out.println("删除所属的"+pbsOrderLists.size()+"个PBS订单成功！");
		}
		//删除订单
		String orderPath=SystemConfig.getServerConfig().getOrderPath()+"/"+l3OrderId;
		
		System.out.println(orderPath);
		
		java.io.File path=new java.io.File(orderPath);
		
		if (path.isDirectory()) {
			TaskExecuter executer=new TaskExecuter();
			
			if (executer.execShell("rm -rf "+path.getAbsolutePath())) {
				System.out.println("删除订单目录成功！");
			}
			
		}
		
		//删除三级订单
		ArrayList<L3InternalOrder> l3orderLists=new ArrayList<L3InternalOrder>();
		l3orderLists.add(l3InternalOrder);
		l3OrderDB.deleteOrder(l3orderLists);
				
		return true;
	}

	public static void main(String[] args) {
		TestDBConnection test=new TestDBConnection();
		test.GetConnection();
		System.out.println();
		if (args.length!=1) {
			System.out.println("请输入一个订单号或多个订单号，用“;”分开！");
			return;
		}
		
		String []lists=args[0].split(";");
		for (int i = 0; i < lists.length; i++) {
			test.deleOrderInfos(lists[i]);
		}
	}

}
