/*
 *程序名称 		: L3InternalOrder.java
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

import DBManage.L3OrderDB;

/**
 * @author caoyang
 * 
 */
public class L3InternalOrder extends Order {
	private static L3OrderDB l3OrderDB = new L3OrderDB();
	// 产品生产系统名称：
	// DataServiceSystem / RadNormSystem / GeoNormSystem / CommonProductSystem /
	// FusionSystem / AssimulationSystem / ValidationSystem
	public String processingSystem;
	// 二级订单号
	public String jobId_L2;
	// 算法资源名称
	public String algorithmName;
	// 算法资源程序
	public String algorithmPath;
	// 产品列表
	public ArrayList<String> strDataProductList;
	// 算法资源ID
	public int algorithmID;

	// 四级订单列表：按照数据所在位置拆分
	public String l4orderlist;

	// 构造函数
	public L3InternalOrder() {
		System.out.println("L3InternalOrder::public L3InternalOrder() | 构造函数");

		this.jobId_L1 = "";
		this.jobId_L2 = "";
		this.orderLevel = "3";
		this.geoCoverageStr = "";
		// 订单状态
		this.workingStatus = "Ready"; // "Finish" "Error" 订单初始状态为"Ready"
		// 算法资源名称
		this.algorithmName = "";
		// 算法资源程序
		this.algorithmPath = "";
		// 产品列表
		this.strDataProductList = new ArrayList<String>();
		// 算法资源ID
		this.algorithmID = -1;
		// 与融合/同化相关联的共性产品信息
		this.retrievalDataList = new ArrayList<String>();
		// 原始数据类型
		this.dataType = "Anything";
	}

	// 构造函数 初始化二级订单
	public L3InternalOrder(L2ExternalOrder order) {
		System.out
				.println("L3InternalOrder::public L3InternalOrder( L2ExternalOrder order ) | 构造函数 初始化二级订单");

		this.jobId_L1 = order.jobId_L1;
		this.jobId_L2 = order.jobId;
		this.orderLevel = "3";
		this.geoCoverageStr = order.geoCoverageStr;
		this.startDate = order.startDate;
		this.endDate = order.endDate;
		// 订单状态
		this.workingStatus = "Ready"; // "Finish" "Error" 订单初始状态为"Ready"
		// 算法资源名称
		this.algorithmName = "";
		// 算法资源程序
		this.algorithmPath = "";
		// 产品列表
		this.strDataProductList = new ArrayList<String>();
		// 算法资源ID
		this.algorithmID = -1;
		// 与融合/同化相关联的共性产品信息
		this.retrievalDataList = order.retrievalDataList;
		// 原始数据类型
		this.dataType = order.dataType;
		//产品类型
		this.productName=order.productName;
	}

	// 设置该订单生产处理的产品生产分系统
	public void setProcessingSystem(String systemName) {
		System.out
				.println("L3InternalOrder::public void setProcessingSystem( String systemName ) | 设置该订单生产处理的产品生产分系统");
		this.processingSystem = systemName;
	}

	// 获取该订单生产处理的产品生产分系统
	public String getProcessingSystem() {
		System.out
				.println("L3InternalOrder::public String getProcessingSystem() | 获取该订单生产处理的产品生产分系统");
		return this.processingSystem;
	}

	public static ArrayList<String> getRetrievalDataListByL2OrderId(
			String strL2OrderId) {

		return l3OrderDB.getRetrievalDataListByL2OrderId(strL2OrderId);
	}

	public static boolean addOrder(L3InternalOrder order) {
		// 连接订单请求数据库，并获取当前订单请求列表
		String dbId = l3OrderDB.generateId(order.orderType);
		// 生成订单号,按订单号命名规则
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		format.setLenient(false);
		java.util.Date date = new Date(System.currentTimeMillis());
		String strDate = format.format(date);

		String orderId = order.getOrderType() + strDate + dbId; // 订单类型里已包含订单等级
		// 设置订单号
		order.setOrderId(orderId);
		// 将订单存入数据库
		return l3OrderDB.addOrder(order);
	}

	public static boolean delete(ArrayList<L3InternalOrder> l3OrderList) {
		return l3OrderDB.deleteOrder(l3OrderList);
	}

	public static boolean delete(L3InternalOrder l3Order) {
		ArrayList<L3InternalOrder> l3OrderList = new ArrayList<L3InternalOrder>();
		l3OrderList.add(l3Order);
		return delete(l3OrderList);
	}

	public static boolean delete(String strL3OrderId) {
		L3InternalOrder l3Order = new L3InternalOrder();
		l3Order.jobId = strL3OrderId;
		return delete(l3Order);
	}
}
