package OrderManage;

import java.util.ArrayList;
import java.util.Iterator;

import DBManage.L3OrderDB;
import DBManage.TestDBConnection;

public class L4InternalOrder extends L3InternalOrder {
	// 三级订单号
	public String jobId_L3;
	
	//所在数据中心的IP
	public String DataCenterIP="0.0.0.0";
		
	
	public L4InternalOrder(){
		
	}
	
	
	//构造函数 以三级父订单初始化
	public L4InternalOrder(L3InternalOrder l3order) {
		this.startDate=l3order.startDate;
		this.priority=l3order.priority;
		this.jobId_L1=l3order.jobId_L1;
		this.jobId_L2=l3order.jobId_L2;
		this.orderType=l3order.orderType;
		this.orderLevel="4";
		
		this.dataList=new ArrayList<>();			
		this.jobId_L3=l3order.jobId;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();

		L3OrderDB l3OrderDB = new L3OrderDB();
		ArrayList<L3InternalOrder> l3OrderList = l3OrderDB
				.search("where JobId='L3RN201507170001'");
		Iterator<L3InternalOrder> it_l3OrderList = l3OrderList.iterator();
		while (it_l3OrderList.hasNext()) {
			L3InternalOrder l3InternalOrder = (L3InternalOrder) it_l3OrderList
					.next();
			System.out.println(l3InternalOrder.jobId_L2);
		}
	}

}
