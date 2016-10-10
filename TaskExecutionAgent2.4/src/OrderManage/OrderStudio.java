/*
 *程序名称 		: OrderStudio.java
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.xml.Sql;

import Pbs.PbsOrder;
import TaskExeAgent.SystemLogger;
import DBManage.L3OrderDB;
import DBManage.PBSOrderDB;

/**
 * @author caoyang 张杰
 * 
 */
public class OrderStudio {
	// 三级订单数据库对象
	private L3OrderDB l3OrderDB;

	// PBS订单数据库对象
	private PBSOrderDB pbsOrderDB;

	// 用于存放课题三发送过来的生产确认消息
	// @@@public static ArrayList< ProductionConfirmationMsg >
	// productionConfirmationMsgList = new ArrayList< ProductionConfirmationMsg
	// >();
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// 构造函数 初始化二级订单
	public OrderStudio() {
		logger.info("OrderStudio::public OrderStudio() | 构造函数");

		// 创建订单请求数据库对象
		this.l3OrderDB = new L3OrderDB();
		this.pbsOrderDB = new PBSOrderDB();

	}

	// 三级订单入库
	public boolean addOrder(L3InternalOrder order) {
		logger.info("OrderStudio::public boolean addOrder( L3InternalOrder order ) | 三级订单入库");

		return this.l3OrderDB.addOrder(order);
	}

	// 获取三级订单列表
	public ArrayList<L3InternalOrder> getOrderList(String status) {
		logger.info("OrderStudio::public ArrayList< L3InternalOrder > getOrderList(String status) | 获取三级订单列表");

		return this.l3OrderDB.getOrderList(status);
	}

	// 设置三级订单状态
	public boolean setOrderWorkflowStatus(String l3OrderId, String l3OrderStatus) {
		logger.info("OrderStudio::public boolean setOrderWorkflowStatus( String l3OrderId, String l3OrderStatus ) | 设置三级订单状态");
		
		return l3OrderDB.setOrderWorkflowStatus(l3OrderId, l3OrderStatus);
	}

	// 设置三级订单特定值状态
	public boolean setOrderStatus(String condition) {
		logger.info("OrderStudio::public boolean setOrderStatus(String condition) | 设置三级订单特定值状态");
		
		return l3OrderDB.setItemValue(condition);
	}

	// 设置三级订单产品列表
	public boolean setL3OrderDataProductList(String l3OrderId,
			ArrayList<String> strDataProductList) {
		logger.info("OrderStudio::public boolean setL3OrderDataProductList( String l3OrderId, ArrayList< String > strDataProductList ) | 设置三级订单产品列表");

		return this.l3OrderDB.setDataProductList(l3OrderId, strDataProductList);
	}

	// 设置三级PBS订单列表
	public boolean setL3OrderPbsOrderLists(String l3OrderId,
			ArrayList<String> strPbsOrderLists) {
		logger.info("OrderStudio::public boolean setL3OrderPbsOrderLists( String l3OrderId, ArrayList< String > strPbsOrderLists ) | 设置三级订单PBS订单列表");

		return this.l3OrderDB.setPbsOrderLists(l3OrderId, strPbsOrderLists);
	}

	// 获取三级订单产品列表
	public ArrayList<String> getL3OrderDataProductList(String l3OrderId) {
		logger.info("OrderStudio::public ArrayList< String > getL3OrderDataProductList( String l3OrderId ) | 获取三级订单产品列表");

		return this.l3OrderDB.getDataProductList(l3OrderId);
	}

	// 删除列表中的三级订单
	public boolean deleteOrder(ArrayList<L3InternalOrder> l3OrderList) {
		logger.info("OrderStudio::public boolean deleteOrder( ArrayList< L3InternalOrder > l3OrderList ) | 删除列表中的三级订单");

		return this.l3OrderDB.deleteOrder(l3OrderList);
	}

	// PBS订单入库
	public boolean addPbsOrder(PbsOrder order) {
		logger.info("OrderStudio::public boolean addOrder( L3InternalOrder order ) | Pbs订单入库");

		return this.pbsOrderDB.addOrder(order);
	}

	// 获取未确定状态的PBS订单
	public ArrayList<PbsOrder> getUnconfirmPbsOrder() {
		logger.info("OrderStudio::ArrayList<PbsOrder> getUnconfirmPbsOrder() | 获取未确认状态的PBS订单列表");

		return this.pbsOrderDB.getOrderList();
	}

	// 获取未确定状态的PBS订单
	public L3OrderPbsProgressCount getUnconfirmPbsOrder(L3InternalOrder l3Order) {
		logger.info("OrderStudio::L3OrderPbsProgressCount getUnconfirmPbsOrder(L3InternalOrder l3Order) | 获取未确认状态的PBS订单列表");

		return this.pbsOrderDB.getOrderList(l3Order);
	}

	// 获取未确定状态的PBS订单
	public boolean setPbsOrderWorkflowStatus(String pbsOrderId, String status) {
		logger.info("OrderStudio::boolean setPbsOrderWorkflowStatus(String status) | 设置PBS订单的状态");
		return this.pbsOrderDB.setOrderWorkflowStatus(pbsOrderId, status);
	}

	// 获取未确定状态的PBS订单
	public boolean setPbsOrderFinishInfos(String pbsOrderId, Date date,
			ArrayList<String> productLists) {
		logger.info("OrderStudio::boolean setPbsOrderWorkflowStatus(String status) | 设置PBS订单的状态");
		return this.pbsOrderDB.setFinishInfos(pbsOrderId, date, productLists);
	}

}
