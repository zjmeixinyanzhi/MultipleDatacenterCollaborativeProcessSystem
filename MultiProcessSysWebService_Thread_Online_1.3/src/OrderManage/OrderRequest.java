/*
 *程序名称 		: orderRequest.java
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

//import java.sql.Time;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.sun.tools.javac.resources.javac;

import DBManage.RequestDB;
/**
 * @author caoyang
 *
 */
public class OrderRequest {
	private static byte[] lockReadWrite = new byte[ 0 ];
	//private static OrderStudio orderStudio = new OrderStudio();
	private static RequestDB requestDB = new RequestDB();
	private boolean isDeleted;
	//订单ID（L2）
	public String jobId;
	//工作流ID
	public int workFlowId;
	//优先级 0-9
	public int priority;
	//一级订单ID（L1）
	public String jobId_L1;
	//用来存放订单类型：CP/FP/AP/VD
	public String orderType;
	//订单级别
	public String orderLevel;
	//真实性检验
	public String doValidation;
	//用来记录生产的共性数据产品名称
	public String productName;
	//地理区域
	public String geoCoverageStr;
	//开始时间
	//!!!public Timestamp startDate;
	public Date startDate;
	//结束时间
	//!!!public Timestamp endDate;
	public Date endDate;
	//数据状态
	public String dataStatus;
	//数据列表
	public ArrayList<String> dataList;
	//生产确认状态
	public int confirmationStatus;
	//生产确认内容
	public boolean confirmationMotion;
	//任务单参数
	public String orderParameter;
	//操作员
	public String operatorId;
	//状态  100：失效（获取必要数据、信息失败）
	//     0：初始状态（待处理）
	//     1：已提交数据解析请求
	//     2：已提交生产请求
	//     3：已转化为二级订单
	//private int status;
	public int status;
	//与融合/同化相关联的共性产品信息
	public ArrayList< String > retrievalDataList;
	//原始数据类型
	public String dataType;
	//提交时间
	public java.util.Date submitDate;
	
	//构造函数
	public OrderRequest(){
		System.out.println( "OrderRequest::public OrderRequest() | 构造函数" );
		
		this.isDeleted         = false;
		this.jobId             = "";
		this.workFlowId        = -1;
		this.priority          = 1;
		this.jobId_L1          = "";
		this.orderType         = "";
		this.orderLevel        = "";
		this.doValidation      = "";
		this.productName       = "";
		this.geoCoverageStr    = "";
		//###this.startDate    = new Date(  );
		//###this.endDate      = new Date(  );
		this.orderParameter    = "";
		this.operatorId        = "";
		this.status            = 0;
		this.retrievalDataList = new ArrayList< String >();
		this.dataType          = "Anything";
	}
	
	protected void finalize(){
		;
	}
	
	public static String generateId( String orderType ){
		
		String orderId = null;
		
		synchronized( lockReadWrite ){
			//连接订单请求数据库，并获取当前订单请求列表
			String dbId = requestDB.generateId( orderType );
			if( null == dbId ){
				return orderId;
			}
			//生成订单号,按订单号命名规则
			DateFormat format = new SimpleDateFormat( "yyyyMMdd" );
			format.setLenient( false );
			java.util.Date date = new Date( System.currentTimeMillis() );
			String strDate = format.format( date );
			String l1OrderType = orderType.replace( "2", "1" );
			orderId = l1OrderType + strDate + dbId + orderType + dbId.substring( 1, 4 );	// 订单类型里已包含订单等级
			
			OrderRequest orderRequest = new OrderRequest();
			orderRequest.jobId = orderId;
			OrderRequest.addOrder( orderRequest );
		}

		return orderId;
	}
	
	public static boolean addOrder( OrderRequest order ){
		synchronized( lockReadWrite ){
			ArrayList< OrderRequest > requestList = new ArrayList< OrderRequest >();
			requestList.add( order );
			return requestDB.setOrderRequest( requestList );
		}
	}
	
	public static boolean updateOrder( OrderRequest order ){
		return requestDB.updateOrder( order );
	}
	
	public void delete(){
		if( this.isDeleted ){
			return;
		}
		//删除数据库表中的对应生产请求订单
		this.isDeleted = true;
		ArrayList< OrderRequest > orderRequestList = new ArrayList< OrderRequest >();
		orderRequestList.add( this );
		//orderStudio.deleteOrderRequest( orderRequestList );
		requestDB.deleteOrderRequest( orderRequestList );
	}
	
	public void setOrderRequestID( String id ){
		if( this.isDeleted ){
			return;
		}
		this.jobId = id;
		//如果数据库中有这个生产请求订单，则获取生产请求订单状态
		//this.
	}
	
	public void nextStatus(){
		if( this.isDeleted ){
			return;
		}
		if( ( this.status >= 0 ) && ( this.status < 3 ) ){
			
			this.status++;
			//更新数据库表中的生产请求订单状态
			this.updateStatus();
		}
	}
	
	public void invalidStatus(){
		if( this.isDeleted ){
			return;
		}
		//if( 0 == this.status ){
			this.status = 100;
			//更新数据库表中的生产请求订单状态
			this.updateStatus();
		//}
	}
	
	public static void invalidStatus( String orderRequestId ){
		requestDB.setStatus( orderRequestId, 100 );
	}
	
	public int getStatus(){
		return this.status;
	}
	private void updateStatus(){
		
		requestDB.setStatus( this.jobId, this.status );
	}
	
	//设置与融合/同化相关联的共性产品信息
	public boolean setRetrievalDataList( ArrayList< String > retrievalDataList ){
		if( this.isDeleted ){
			return false;
		}
		this.retrievalDataList = retrievalDataList;
		return requestDB.setRetrievalDataList( this.jobId, retrievalDataList );
	}
	
	public boolean setDataType( String dataType ){
		if( this.isDeleted ){
			return false;
		}
		this.dataType = dataType;
		return requestDB.setDataType( this.jobId, dataType );
	}
	
	public static boolean delete( ArrayList< OrderRequest > orderRequestList ){
		return requestDB.deleteOrderRequest( orderRequestList );
	}
	
	public static boolean delete( OrderRequest orderRequest ){
		ArrayList< OrderRequest > orderRequestList = new ArrayList< OrderRequest >();
		orderRequestList.add( orderRequest );
		return delete( orderRequestList );
	}
	
	public static boolean delete( String strOrderRequestId ){
		OrderRequest orderRequest = new OrderRequest();
		orderRequest.jobId = strOrderRequestId;
		return delete( orderRequest );
	}
	
}
