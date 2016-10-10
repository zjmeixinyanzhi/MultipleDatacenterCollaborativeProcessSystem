/*
 *程序名称 		: Order.java
 *版权说明  		:
 *版本号		    : 1.0
 *功能			: 
 *开发人		    : caoyang
 *开发时间		: 2014-05-19
 *修改者		    : 
 *修改时间		: 
 *修改简要说明 	:
 *其他			:	 
 */
package OrderManage;

import java.util.Date;
//import java.sql.Timestamp;
import java.util.ArrayList;

import RSDataManage.RSData;
//import java.util.Date;

/**
 * @author caoyang
 *
 */
public class Order {
	
	//订单ID
	public String jobId;
	//一级订单ID（L1）
	public String jobId_L1;
	//用来存放订单类型
	public String orderType;
	//订单级别
	public String orderLevel;
	//优先级 0-9
	public int priority;
	//产品名称
	public String productName;
	//地理区域
	public String geoCoverageStr;
	//时相（成像时间）开始时间
	public Date startDate;
	//时相（成像时间）结束时间
	public Date endDate;
	//用来记录订单状态：Start/Running/Finish/Error/Suspend
	public String workingStatus;
	//用来记录订单的数据列表
	public ArrayList<RSData> dataList;
	public Date submitDate;
	public Date finishDate;
	//任务单参数
	public String orderParameter;
	//操作员
	public String operatorId;
	//算法资源名称
	public String algorithmName;
	//算法资源程序
	public String algorithmPath;
	
	////构造函数 创建空订单用于测试
	//###
	public Order(){
		System.out.println( "Order::public Order() | 构造函数 创建空订单用于测试" );
	}

	//订单参数转化成XML字符串
	public String toXMLStr(){
		System.out.println( "Order::public String toXMLStr() | 订单参数转化成XML字符串" );
		//###String xmlStr;
		//###//订单参数转化成XML字符串
		//###xmlStr.add(this.orderType);
		//###xmlStr.add(this.productName);
		//###//…
		//###return xmlStr;
		return "";
	}

	//###//set函数
	//###public void setOrderType (String orderType);
	public void setOrderId (String id){
		System.out.println( "Order::public void setOrderId (String id) | 设置订单ID" );
		this.jobId = id;
	}
	//###public void setOrderLevel (int level);
	//###public void setDoValidation (boolean doValidation);
	public void setProductName (String productName){
		System.out.println( "Order::public void setProductName (String productName) | 设置产品名称" );
		this.productName = productName;
	}
	//###public void setGeoCoverageStr(String geoCoverageStr);
	//###public void setStartDate (Date startDate);
	//###public void setEndDate (Date endDate);
	//###public void setDataStatus (String dataStatus);
	//###public void setDataList (ArrayList<String> dataList);
	//###public void setWorkingStatus (String workingStatus);
	//###public void setOrderStatus (String orderStatus);
    //###
	//###
	
	//get函数
	public String getOrderType(){
		System.out.println( "Order::public String getOrderType() | 获取订单类型" );
		return this.orderType;
	}
	//###public String getOrderId();
	public int getPriority(){
		System.out.println( "Order::public int getPriority() | 获取订单优先级" );
		return this.priority;
	}
	public String getOrderLevel(){
		System.out.println( "Order::public int getOrderLevel() | 获取订单等级" );
		//return Integer.parseInt( this.orderLevel );
		return this.orderLevel;
	}
	//###public boolean getDoValidation();
	public String getProductName(){
		System.out.println( "Order::public int getOrderLevel() | 获取产品名称" );
		return this.productName;
	}
	//###public String getGeoCoverageStr();
	//###public Date getStartDate();
	//###public Date getEndDate();
	//###public String getDataStatus();
	//###public ArrayList<String> getDataList();
	//###public String getWorkingStatus();
	//###public String getOrderStatus();

}
