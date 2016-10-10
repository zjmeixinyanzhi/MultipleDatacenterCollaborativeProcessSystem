package TaskSchedular;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import DBManage.L3OrderDB;
import DBManage.PBSOrderDB;
import DBManage.TestDBConnection;
import OrderManage.L3InternalOrder;
import Pbs.PbsOrder;
import TaskExeAgent.SystemConfig;
import TaskExeAgent.SystemLogger;

/**
 * 创建时间：2016年4月19日 上午11:21:08
 * 项目名称：TaskExecutionAgent2.1
 * 2016年4月19日
 * @author 张杰
 * @version 1.0
 * 文件名称：ReSubmit.java
 * 类说明：
 */
public class ReSubmit {
	//重新提交
	public ArrayList<String> pbsOrderLists;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();	
	//
	
	
	public ReSubmit() {
		this.pbsOrderLists=new ArrayList<String>();
		
	}
	
	public boolean doReSubmit(L3InternalOrder l3Order,ArrayList<String> orderLists) {
		//重新提交，重新qsub 并更新pbs订单状态
		if (l3Order==null || orderLists==null) {
			return false;
		}
		
		//PBSOrder
		PBSOrderDB pbsOrderDB=new PBSOrderDB();
		L3OrderDB l3OrderDB=new L3OrderDB();
		TaskExecuter executer=new TaskExecuter();
		
		//获取L3Oorder的订单pbs订单列表
		ArrayList<String> pbsOrderLists=l3Order.pbsOrderLists;
		
		Iterator<String> iterator=orderLists.iterator();		
		boolean flag=true;
		while (iterator.hasNext()) {
			String pbsId = (String) iterator.next();
			PbsOrder pbsOrder = new PbsOrder();
			
			System.out.println();
			
			String condition = " where PBSId='" + pbsId + "'";
			ArrayList<PbsOrder> cur_PbsOrders = pbsOrderDB
					.search(condition);
			if (cur_PbsOrders == null) {
				continue;
			}
			pbsOrder = cur_PbsOrders.get(0);
			
			//PBS脚本文件
			String pbsFile =pbsOrder.getPbsFile();
			//获取qsub绝对路径
			String qsubPath=SystemConfig.getServerConfig().getQsubPath();
			//qsub+pbsFile
			if (pbsFile==null || qsubPath==null) {
				flag&=false;
				continue;
			}
			//更新PBS状态未Running
			pbsOrderDB.setOrderWorkflowStatus(pbsId, "Running");
			
			
			//PBS参数文件			
			try {
				String newPBSId=executer.execShellFeedbackOutputStream(qsubPath+" "+pbsFile);
				if (newPBSId.split(".").length!=2) {
					flag&=false;
					continue;
				}
				
				
				//更新新的PBS订单
				pbsOrderDB.setItemValue(" set PBSId='"
						+newPBSId
						+ "' where JobId='"
						+ pbsOrder.getJobId()
						+ "'");							
				//更新三级订单PBS文件
				if (newPBSId!=null) {
					pbsOrderLists.remove(pbsId);
					pbsOrderLists.add(newPBSId);
					logger.info("L3Order订单中新的PBS Order已经替换！");
				}		
				
				
			} catch (Exception e) {
				// TODO: handle exception
				logger.info(e);
				flag&=false;
				continue;	
			}
		}
		//更新PNS列表
		String[] newPBSLists=new String[pbsOrderLists.size()];
				pbsOrderLists.toArray(newPBSLists);
		flag&=l3OrderDB.setItemValue("set PbsOrderLists='"
				+  String.join(";",newPBSLists)
				+ "' where JobId='"
				+ l3Order.jobId
				+ "'");
		
		return flag;
	}	

	public static void main(String[] args) {
		//
		TestDBConnection test=new TestDBConnection();
		test.GetConnection();
		
		L3OrderDB l3OrderDB=new L3OrderDB();
		ArrayList<String> arrayList=new ArrayList<String>();
		arrayList.add("2379.IOServer-SSD");
		arrayList.add("2375.IOServer-SSD");
		
		ReSubmit reSubmit=new ReSubmit();
		reSubmit.doReSubmit(l3OrderDB.search(" JobId='L3GN201603220004@IOServer-SSD'").get(0),arrayList);

	}

}
