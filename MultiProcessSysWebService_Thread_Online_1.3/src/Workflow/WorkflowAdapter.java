/*
 *程序名称 		: WorkflowAdapter.java
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
package Workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;

import antlr.collections.List;

import DBManage.WorkflowDB;
import OrderManage.L2ExternalOrder;
import OrderManage.L3InternalOrder;
import OrderManage.Order;
import OrderManage.OrderRequest;
import OrderManage.OrderStudio;
import SystemManage.DBConfig;
import SystemManage.SystemConfig;
import SystemManage.SystemLogger;

/**
 * @author caoyang
 *
 */
public class WorkflowAdapter {
	//用来存放订单（二级外部订单）与工作流实例的对应关系列表

	protected Hashtable< String, String > orderType2WorkflowMap;
	//用来存放工作流数据库对象
	protected WorkflowDB workflowDB;
	private static byte[] lockAddL3Order = new byte[ 0 ];
	//日志系统
	private Logger logger=SystemLogger.getSysLogger();

	//构造函数，初始化orderType2WorkflowMap
	public WorkflowAdapter(){
		System.out.println( "WorkflowAdapter::public WorkflowAdapter() | 构造函数，初始化orderType2WorkflowMap" );

		//创建工作流数据库对象
		this.workflowDB = new WorkflowDB();
		//从工作流数据库获取工作流关系表，并初始化orderType2WorkflowMap
		this.orderType2WorkflowMap = this.workflowDB.getOrderType2WorkflowMap();
	}
	
	//订单工作流匹配，并创建返回工作流对象（线程）
	public WorkflowSchedular doMatch( L2ExternalOrder order ){
		logger.info( "WorkflowAdapter::public WorkflowSchedular doMatch( String orderId ) | 订单工作流匹配，并进行订单拆分" );

		//获取订单处理流程的工作流描述XML文件
		String procType = order.orderType;
		int priority    = order.priority;
		//3.创建工作流调度对象（启动新线程）
		WorkflowSchedular wfSchedular = this.doCreate( procType, order, priority );
		
		return wfSchedular;
	}


	//订单的工作流匹配，并处理流程是否存在
	public boolean isMatch( L2ExternalOrder order ){
		System.out.println( "WorkflowAdapter::public boolean isMatch( L2ExternalOrder order ) | 订单的工作流匹配，并处理流程是否存在" );

		if( null == order ){
			return false;
		}
		
		//获取订单处理流程的工作流描述XML文件
		String procType  = order.orderType;
		String wfXMLFile = null;
		if( this.orderType2WorkflowMap != null && !this.orderType2WorkflowMap.isEmpty() ){
			wfXMLFile = this.orderType2WorkflowMap.get( procType );
		}
		//判断工作流描述文件是否存在
		if( null == wfXMLFile ){
			return false;
		}else{
			return true;
		}
	}
	
	//订单的工作流匹配，并处理流程是否存在
	public boolean isMatch( OrderRequest orderRequest ){
		System.out.println( "WorkflowAdapter::public boolean isMatch( OrderRequest orderRequest ) | 订单的工作流匹配，并处理流程是否存在" );
		
		if( null == orderRequest ){
			//test
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>isMatch | OrderRequest is null." );
			return false;
		}

		//获取订单处理流程的工作流描述XML文件
		String procType  = orderRequest.orderType;
//		if( "L2VD".equals( orderRequest.orderType ) ){
//			procType = "L2CP";
//		}
		String wfXMLFile = null;
		if( this.orderType2WorkflowMap != null && !this.orderType2WorkflowMap.isEmpty() ){
			wfXMLFile = this.orderType2WorkflowMap.get( procType );
		}
		
		//test
		System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>orderRequest.JobId = " + orderRequest.jobId + " XMLFile = " + wfXMLFile );
		
		//判断工作流描述文件是否存在
		if( null == wfXMLFile ){
			return false;
		}else{
			return true;
		}
	}
	

	//设置订单类型（二级外部订单）与工作流实例的对应关系列表
	public void setOrderType2WorkflowMap( Hashtable< String, String > order2WFMap ){
		System.out.println( "WorkflowAdapter::public void setOrderType2WorkflowMap( Hashtable< String, String > order2WFMap ) | 设置订单类型（二级外部订单）与工作流实例的对应关系列表" );
		//1.设置工作流匹配关系
		this.orderType2WorkflowMap = order2WFMap;
	}
	

	//订单工作流匹配，并创建工作流对象（线程）
	//procType：订单的产品名称（类型）
	//orderId ：订单号
	//property：订单优先级
	public WorkflowSchedular doCreate( String procType, L2ExternalOrder order, int priority ){
		System.out.println( "WorkflowAdapter::public WorkflowSchedular doCreate(String procType,String orderId,String priority) | 订单工作流匹配，并进行订单拆分" );

		//1.匹配工作流描述XML文件,获得不同类型的订单的处理步骤
		String wfXMLFile = null;
		if( this.orderType2WorkflowMap != null && !this.orderType2WorkflowMap.isEmpty() ){
			wfXMLFile = this.orderType2WorkflowMap.get( procType );
		}
		//2.创建工作流调度对象（启动新线程）
		WorkflowSchedular wfSchedular = new WorkflowSchedular( wfXMLFile, order.jobId );
		wfSchedular.setPriority( priority );
		//3.解析工作流XML文件，获取处理流程（三级订单l3Order（Task）列表）
		ArrayList< L3InternalOrder > l3OrderTaskList = parse( wfXMLFile, order);
		
		wfSchedular.setTaskList( l3OrderTaskList );
		OrderStudio orderStudio = new OrderStudio();
		String l3OrderList = "";
		Iterator< L3InternalOrder > it_L3Order = l3OrderTaskList.iterator();
		synchronized( lockAddL3Order ){
			while( it_L3Order.hasNext() ){
				L3InternalOrder l3Order = it_L3Order.next();
				orderStudio.addOrder( l3Order );				
				l3OrderList += l3Order.jobId + ";";
			}
		}
		
		//在二级订单中存储分解出来的三级订单列表
		orderStudio.setL3OrderList( order.jobId, l3OrderList );
		//test
		System.out.println("增加的三级订单状态："+order.jobId+" "+l3OrderList);
		
		return wfSchedular;
	}

	//解析工作流XML文件，获取处理流程（三级订单l3Order(Task)列表）
	protected ArrayList< L3InternalOrder > parse( String strWFXMLFile, L2ExternalOrder l2Order ){
		System.out.println( "WorkflowAdapter::protected ArrayList< L3InternalOrder > parse( String strWFXMLFile ) | 解析工作流XML文件，获取处理流程（三级订单l3Order(Task)列表）" );
		
		ArrayList< L3InternalOrder > l3OrderList = new ArrayList< L3InternalOrder >();
		
		String [] orderTypeList = null;
		if( !strWFXMLFile.equals( "" ) ){
			orderTypeList = strWFXMLFile.split( "," );
		}

		if( null != orderTypeList ){
			for( int iOrderTypeIndex = 0; iOrderTypeIndex < orderTypeList.length; iOrderTypeIndex++ ){
				L3InternalOrder l3Order = new L3InternalOrder( l2Order );
				l3Order.orderType = orderTypeList[ iOrderTypeIndex ];
				
				//test
				l3Order.dataList = l2Order.dataList;
				l3OrderList.add( l3Order );
			}
		}
		
		return l3OrderList;
	}
}
