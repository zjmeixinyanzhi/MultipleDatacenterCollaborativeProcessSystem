/*
 *程序名称 		: L2ExternalOrder.java
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

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.javafx.collections.MappingChange.Map;

import DBManage.L2OrderDB;
/**
 * @author caoyang
 *
 */
public class L2ExternalOrder extends Order {
	private static L2OrderDB l2OrderDB = new L2OrderDB();
	private static HashMap<String, String> orderRequestL2OrderMap = new HashMap< String, String >();
	
	public String jobId_P3L2;
	// 三级订单列表
	public String l3orderlist;
	
	//构造函数
	public L2ExternalOrder(){
		//订单ID（L2）
		this.jobId             = "";
		//工作流ID
		this.workFlowId        = 0;
		//优先级 0-9
		this.priority          = 1;
		//一级订单ID（L1）
		this.jobId_L1          = "";
		//订单ID（L2）
		this.jobId_P3L2        = "";
		//用来存放订单类型：CP/FP/AP/VD
		this.orderType         = "";
		//订单级别
		this.orderLevel        = "";
		//真实性检验
		this.doValidation      = "";
		//用来记录生产的共性数据产品名称
		this.productName       = "";
		//地理区域
		this.geoCoverageStr    = "";	// XML字符串
		//开始时间
		this.startDate         = new Date( 0 );
		//结束时间
		this.endDate           = new Date( 0 );
		//任务单参数
		this.orderParameter    = "";
		//操作员
		this.operatorId        = "";
		//用来记录订单的数据状态：Available/NotAvailable/Future
		this.dataStatus        = "";
		//用来记录订单的数据列表
		this.dataList          = new ArrayList< String >();
		//订单状态
		this.workingStatus     = "";	//"QUEUE"
		//订单生产状态
		this.orderStatus       = "";
		//三级订单列表
		this.l3orderlist       = "";
		//与融合/同化相关联的共性产品信息
		this.retrievalDataList = new ArrayList< String >();
		//原始数据类型
		this.dataType          = "Anything";
	}
	
	//构造函数 初始化二级订单
	public L2ExternalOrder(OrderRequest request){
		System.out.println( "L2ExternalOrder::public L2ExternalOrder(OrderRequest request) | 构造函数 初始化二级订单" );

		//利用订单请求的参数，初始化产品生产订单参数
		//订单ID（L2）
		this.jobId             = "";
		//工作流ID
		this.workFlowId        = request.workFlowId;
		//优先级 0-9
		this.priority          = request.priority;
		//一级订单ID（L1）
		this.jobId_L1          = request.jobId_L1;
		//订单ID（L2）
		this.jobId_P3L2        = request.jobId;
		//用来存放订单类型：CP/FP/AP/VD
		this.orderType         = request.orderType;
		//订单级别
		this.orderLevel        = request.orderLevel;
		//真实性检验
		this.doValidation      = request.doValidation;
		//用来记录生产的共性数据产品名称
		this.productName       = request.productName;
		//地理区域
		this.geoCoverageStr    = request.geoCoverageStr;	// XML字符串
		//开始时间
		this.startDate         = request.startDate;
		//结束时间
		this.endDate           = request.endDate;
		//任务单参数
		this.orderParameter    = request.orderParameter;
		//操作员
		this.operatorId        = request.operatorId;
		//用来记录订单的数据状态：Available/NotAvailable/Future
		this.dataStatus        = request.dataStatus;
		//用来记录订单的数据列表
		this.dataList          = request.dataList;
		//订单状态
		this.workingStatus     = "";	//"QUEUE"
		//订单生产状态
		this.orderStatus       = "";
		//三级订单列表
		this.l3orderlist       = "";
		//与融合/同化相关联的共性产品信息
		this.retrievalDataList = request.retrievalDataList;
		//原始数据类型
		this.dataType          = request.dataType;
	}
	
	public static L2ExternalOrder addOrder( L2ExternalOrder order ){
		//连接订单请求数据库，并获取当前订单请求列表
		String orderType = order.orderType.substring( 0, 4 );
		String dbId = l2OrderDB.generateId( orderType );
		if( null == dbId ){
			order.setOrderId( "" );
			return order;
		}
		//生成订单号,按订单号命名规则
		DateFormat format = new SimpleDateFormat( "yyyyMMdd" );
		format.setLenient( false );
		java.util.Date date = new Date( System.currentTimeMillis() );
		String strDate = format.format( date );
		//String orderId = "L" + order.getOrderLevel() + order.getOrderType() + strDate + dbId;
		String orderId = orderType + strDate + dbId;	// 订单类型里已包含订单等级
		//设置订单号
		order.setOrderId( orderId );
		//将订单存入数据库
		boolean flag = l2OrderDB.addOrder( order );
		if( false == flag ){
			return null;
		}
		//返回
		return order;
	}
	
	public static L2ExternalOrder getOrder( String l2OrderId ){
		// 连接订单请求数据库,并查找订单
		return l2OrderDB.getOrder( l2OrderId );
	}

	// 二级订单获取
	public static L2ExternalOrder getL2CPOrderByL1JobId( String l1OrderId ){
		System.out.println( "L2ExternalOrder::public L2ExternalOrder getL2CPOrderByL1JobId( String l1OrderId ) | 二级订单获取" );
		
		// 连接订单请求数据库,并查找订单
		return l2OrderDB.getL2CPOrderByL1JobId( l1OrderId );
	}
	
	public static void orderRequestL2OrderBind( String orderRequestId, String l2OrderId ){
		orderRequestL2OrderMap.put( orderRequestId, l2OrderId );
	}
	
	public static String getL2OrderIdByMap( String orderRequestId ){
		return orderRequestL2OrderMap.get( orderRequestId );
	}
	
	public static void orderRequestL2OrderRemove( String orderRequestId, String l2OrderId ){
		orderRequestL2OrderMap.remove( orderRequestId, l2OrderId );
	}
	
	public static boolean update( L2ExternalOrder l2Order ){
		return l2OrderDB.updateOrder( l2Order );
	}
	
	public static boolean delete( ArrayList< L2ExternalOrder > l2OrderList ){
		return l2OrderDB.deleteOrder( l2OrderList );
	}
	
	public static boolean delete( L2ExternalOrder l2Order ){
		ArrayList< L2ExternalOrder > l2OrderList = new ArrayList< L2ExternalOrder >();
		l2OrderList.add( l2Order );
		return delete( l2OrderList );
	}
	
	public static boolean delete( String strL2OrderId ){
		L2ExternalOrder l2Order = new L2ExternalOrder();
		l2Order.jobId = strL2OrderId;
		return delete( l2Order );
	}
}
