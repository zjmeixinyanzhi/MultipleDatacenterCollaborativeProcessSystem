package OrderManage;

import java.util.ArrayList;
import java.util.Iterator;

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

}
