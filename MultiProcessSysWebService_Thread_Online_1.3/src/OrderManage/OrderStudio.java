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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.xml.Sql;

import com.sun.java.swing.plaf.windows.WindowsTreeUI.CollapsedIcon;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import DBManage.DataCenter;
import DBManage.L2OrderDB;
import DBManage.L3OrderDB;
import DBManage.L4OrderDB;
import DBManage.RequestDB;
import DBManage.RsDataCacheDB;
import RSDataManage.RSData;
import SystemManage.DBConfig;
import SystemManage.SystemConfig;
import SystemManage.SystemLogger;
import TaskSchedular.Algorithm;
import TaskSchedular.RSDataSchedular;
import Workflow.WorkflowAdapter;
import Workflow.WorkflowSchedular;

/**
 * @author caoyang
 * 
 */
public class OrderStudio {
	// 用来存放订单数据库对象
	private L2OrderDB l2OrderDB;
	// 三级订单数据库对象
	private L3OrderDB l3OrderDB;
	// 四级订单数据库对象
	private L4OrderDB l4OrderDB;

	// 用来存放生产请求数据库表对象
	private RequestDB requestDB;
	private static byte[] lockWrite_L2Order = new byte[0];

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// 构造函数 初始化二级订单
	public OrderStudio() {
		System.out.println("OrderStudio::public OrderStudio() | 构造函数");

		// 创建订单请求数据库对象
		this.l2OrderDB = new L2OrderDB();
		this.l3OrderDB = new L3OrderDB();
		this.l4OrderDB = new L4OrderDB();
		this.requestDB = new RequestDB();
	}

	// 获取订单请求列表
	public ArrayList<OrderRequest> getOrderRequestList() {
		System.out
				.println("OrderStudio::public ArrayList<OrderRequest> getOrderRequestList() | 获取订单请求列表");
		// 连接订单请求数据库，并获取当前订单请求列表
		ArrayList<OrderRequest> requestList = requestDB.getOrderRequest();
		return requestList;
	}

	// 获取订单请求列表
	public ArrayList<OrderRequest> getOrderRequestList(String strCondition) {
		System.out
				.println("OrderStudio::public ArrayList<OrderRequest> getOrderRequestList( String strCondition ) | 获取订单请求列表");
		// 连接订单请求数据库，并获取当前订单请求列表
		ArrayList<OrderRequest> requestList;
		switch (strCondition) {
		case "UnDataList":
			requestList = requestDB.getUnDataStatusOrderRequest();
			break;
		case "UnFeasibility":
			requestList = requestDB.getUnFeasibilityOrderRequest();
			break;
		case "UnTransform":
			requestList = requestDB.getUnTransformOrderRequest();
			break;
		default:
			requestList = new ArrayList<OrderRequest>();
		}

		return requestList;
	}

	public boolean setOrderRequestStatus(String strOrderRequestId, int status) {
		return this.requestDB.setStatus(strOrderRequestId, status);
	}

	// 设置与融合/同化相关联的共性产品信息
	public boolean setOrderRequestRetrievalDataList(String strOrderRequestId,
			ArrayList<String> retrievalDataList) {
		return this.requestDB.setRetrievalDataList(strOrderRequestId,
				retrievalDataList);
	}

	// 设置订单请求列表
	public boolean setOrderRequestList(ArrayList<OrderRequest> requestList) {
		System.out
				.println("OrderStudio::public boolean setOrderRequestList( ArrayList<OrderRequest> requestList ) | 设置订单请求列表");

		boolean flag = requestDB.setOrderRequest(requestList);
		// 返回
		return flag;
	}

	// 订单的数据解析
	public OrderDatas parseData(L2ExternalOrder order) {
		System.out
				.println("OrderStudio::public OrderDatas parseData(L2ExternalOrder order) | 订单的数据解析");

		return null;
	}

	// 二级订单入库
	public L2ExternalOrder addOrder(L2ExternalOrder order) {
		logger.info("OrderStudio::public L2ExternalOrder addOrder( L2ExternalOrder order ) | 二级订单入库");
		// 连接订单请求数据库，并获取当前订单请求列表
		String orderType = order.orderType.substring(0, 4);
		String dbId = l2OrderDB.generateId(orderType);
		if (null == dbId) {
			order.setOrderId("");
			return order;
		}
		// 生成订单号,按订单号命名规则
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		format.setLenient(false);
		java.util.Date date = new Date(System.currentTimeMillis());
		String strDate = format.format(date);
		String orderId = orderType + strDate + dbId; // 订单类型里已包含订单等级
		// 设置订单号
		order.setOrderId(orderId);
		// 将订单存入数据库
		boolean flag = this.l2OrderDB.addOrder(order);
		if (false == flag) {
			return null;
		}
		// 返回
		return order;
	}

	// 三级订单入库
	public L3InternalOrder addOrder(L3InternalOrder order) {
		logger.info("OrderStudio::public L3InternalOrder addOrder( L3InternalOrder order ) | 三级订单入库");
		// 连接订单请求数据库，并获取当前订单请求列表
		String dbId = this.l3OrderDB.generateId(order.orderType);
		// 生成订单号,按订单号命名规则
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		format.setLenient(false);
		java.util.Date date = new Date(System.currentTimeMillis());
		String strDate = format.format(date);
		String orderId = order.getOrderType() + strDate + dbId; // 订单类型里已包含订单等级
		// 设置订单号
		order.setOrderId(orderId);
		// 将订单存入数据库
		boolean flag = this.l3OrderDB.addOrder(order);
		if (false == flag) {
			logger.error("向三级订单库插入订单错误！");
			return null;
		}
		// 返回
		// 拆分三级订单，四级订单入库 (是否需要限定死订单类型？)：DP、GN、RN类型的三级订单需要拆分并入库
		// 数据准备DP订单需要进行数据调度，选择合适的数据与数据中心
		if (order.orderType.equals("L3DP")) {
			addL3DPSubOrder(order);
		}
		// GN、RN等预处理订单在DP调度的数据中心上进行，数据不变
		if (order.orderType.equals("L3GN") || order.orderType.equals("L3RN")|| order.orderType.equals("L3DS")) {
			addL3PreProcessSubOrder(order);
		}
		
		return order;
	}

	// 四级DP订单入库
	public ArrayList<L4InternalOrder> addL3DPSubOrder(L3InternalOrder l3order) {
		logger.info("OrderStudio::public L4InternalOrder addL3SubOrder( L3InternalOrder l3order ) | 四级DP订单入库");
		// 所有拆分的四级订单列表，用于存放四级订单对象，用订单名称区分
		ArrayList<L4InternalOrder> l4orderList = new ArrayList<>();
		try {
			// 根据数据所在数据中心名称四级订单：
			// 分解数据，选出合适的数据中心
			DataCenter optimalDataCenter = null;
			// 三级订单所拆分的四级订单列表，用;隔开
			String l4OrderNameList = "";
			ArrayList<String> rsDataIdLists = l3order.dataList;

			logger.info(l3order.jobId + "数据条目：" + rsDataIdLists.size());
			Iterator<String> it_rsdatalist = rsDataIdLists.iterator();

			while (it_rsdatalist.hasNext()) {
				String rsDataId = (String) it_rsdatalist.next();

				RSData rsData = new RSData();
				RsDataCacheDB db = new RsDataCacheDB();
				ArrayList<RSData> rsDatas = db.search(" where dataid='"
						+ rsDataId + "'");
				if (rsDatas.size() != 0) {
					rsData = rsDatas.get(0);
				}

				if (rsData == null) {
					break;
				}

				String curDataType = rsData.sensor+ "@" +rsData.spacecraft  ;
				System.out.println(">>" + curDataType);

				RSDataSchedular dataSchedular = new RSDataSchedular(rsData);
				dataSchedular.doSchedule();
				optimalDataCenter = dataSchedular.getDataCenter();

				// 查找数据名称与数据类型的对应表:rsData.sensor 与算法匹配相关
				// ************需要增加相应模块*******************//
				// rsData.sensor = "1KM-MODIS";

				// 更新数据信息？？？？：数据汇总新，Url等信息
				// 此处更新到三级订单对象中，并不写入到三级订单库中
				// 更新数据所在的数据中心
				// rsData.datacenter = optimalDataCenter.getHostIp();
				// 更新数据的Url
				// rsData.filepath = dataSchedular.getDataPath();
				// 最优数据信息
				// String newRsData = rsData.getRSDataString();

				// 生成四级订单号：三级订单号+数据中心位置
				String l4orderName = l3order.jobId + "@"
						+ optimalDataCenter.getHostName();

				// System.out.println("\\\\\\\\\\\\\\ l4OrderNameList:"+l4OrderNameList);

				// 遍历已有四级订单对象列表，存在该对象时只更新四级订单对象的数据，否者添加新的订单对象
				// 判断是否已有该四级订单
				if (l4OrderNameList.contains(l4orderName)) {
					// test
//					System.out.println("已经存在四级订单，当前订单四级订单列表名称为："
//							+ l4OrderNameList);
					// 更新又有订单的数据列表
					Iterator<L4InternalOrder> it_l4orderlist = l4orderList
							.iterator();
					while (it_l4orderlist.hasNext()) {
						L4InternalOrder templ4InternalOrder = (L4InternalOrder) it_l4orderlist
								.next();
						// 找出已有订单，更新其数据列表
						// System.out.println(l4orderName + ""
						// + templ4InternalOrder.jobId);

						if (templ4InternalOrder.jobId.equals(l4orderName)) {
							templ4InternalOrder.dataList.add(rsDataId);
							// 跳出当前遍历订单循环

							// 更新订单数据类型
							if (!templ4InternalOrder.dataType
									.contains(curDataType)) {
								String newDataType = templ4InternalOrder.dataType
										+ curDataType + ";";
								templ4InternalOrder.dataType = newDataType;
								// System.out.println(templ4InternalOrder.dataType);
							}

							break;
						}
					}
				} else {
					// 新建四级订单，传递父订单参数
					L4InternalOrder templ4InternalOrder = new L4InternalOrder(
							l3order);
					templ4InternalOrder.jobId = l4orderName;
					templ4InternalOrder.dataList.add(rsDataId);
					templ4InternalOrder.DataCenterIP = optimalDataCenter
							.getHostIp();
					// 更新订单数据类型
					templ4InternalOrder.dataType = curDataType + ";";

					// 更新新的四级订单对象
					l4orderList.add(templ4InternalOrder);

					// 更新三级订单的四级订单名称列表
					l4OrderNameList += l4orderName;
					l4OrderNameList += ";";
				}
				// test
				// System.out.println("///////// L4OrderNameList:"+l4OrderNameList);
			}
			// 更新三级订单的四级订单对象列表
			// test
			// System.out.println(">>>>>>"+l4OrderNameList);
			l4OrderDB.add(l4orderList);

			// 更新三级订单所属的四级订单列表
			// System.out.println(l4OrderNameList);
			l3OrderDB.setOrderList(l3order.jobId, l4OrderNameList);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(l3order.jobId + "对应的DP四级订单入库错误！");
			logger.error(e);
		}

		return l4orderList;

	}

	// 四级预处理RN、GN订单订单入库
	public ArrayList<L4InternalOrder> addL3PreProcessSubOrder(
			L3InternalOrder l3order) {

		logger.info("OrderStudio::public L4InternalOrder addL3PreProcessSubOrder( L3InternalOrder l3order ) | 四级预处理订单入库");
		// 四级订单列表
		ArrayList<L4InternalOrder> l4OrderList = new ArrayList<>();
		// 四级订单名称列表
		String l4OrderNameList = "";

		// 需要改進：如何快速確定該訂單號
		// 根据共用的二级订单号查找相应的DP四级订单
		String condition = "WHERE JobId_L2='" + "" + l3order.jobId_L2 + "'"
				+ " and JobId_L1='" + l3order.jobId_L1 + "'"
				+ " and OrderType='L3DP'";
		// test
		// System.out.println(">>>>"+condition);

		try {
			ArrayList<L4InternalOrder> l4DPOrderList = l4OrderDB
					.search(condition);
			Iterator<L4InternalOrder> it_l4DPOrderList = l4DPOrderList
					.iterator();
			for (L4InternalOrder l4DPOrder : l4DPOrderList) {
				// test
				// System.out.println("已有DP:" + l4DPOrder.jobId);

				// 新建四级订单，传递父订单参数
				L4InternalOrder templ4InternalOrder = new L4InternalOrder(
						l3order);
				// 截取数据中心主机名称
				String DataCenterName = l4DPOrder.jobId.substring(
						l4DPOrder.jobId.indexOf("@") + 1,
						l4DPOrder.jobId.length());
				templ4InternalOrder.jobId = l3order.jobId + "@"
						+ DataCenterName;

				templ4InternalOrder.DataCenterIP = l4DPOrder.DataCenterIP;
				templ4InternalOrder.dataList = l4DPOrder.dataList;
				templ4InternalOrder.dataType=l4DPOrder.dataType;

				l4OrderList.add(templ4InternalOrder);
				l4OrderNameList += templ4InternalOrder.jobId + ";";
			}

			// 根据四级DP订单添加四级RN、GN订单
			l4OrderDB.add(l4OrderList);
			// 更新三级订单所属的四级订单列表
			// System.out.println(l4OrderNameList);
			l3OrderDB.setOrderList(l3order.jobId, l4OrderNameList);
			// System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(l3order.jobId + "对应的RN、GN四级订单入库错误！");
			logger.error(e);
		}
		return l4OrderList;
	}

	// 二级订单获取
	public L2ExternalOrder getL2Order(String l2OrderId) {
		System.out
				.println("OrderStudio::public L2ExternalOrder getOrder( String l2OrderId ) | 二级订单获取");
		// 连接订单请求数据库,并查找订单
		return this.l2OrderDB.getOrder(l2OrderId);
	}

	// 二级订单获取
	public L2ExternalOrder getL2CPOrderByL1JobId(String l1OrderId) {
		System.out
				.println("OrderStudio::public L2ExternalOrder getL2CPOrderByL1JobId( String l1OrderId ) | 二级订单获取");

		// 连接订单请求数据库,并查找订单
		return this.l2OrderDB.getL2CPOrderByL1JobId(l1OrderId);
	}

	// 三级订单获取
	public L3InternalOrder getL3Order(String l3OrderId) {
		System.out
				.println("OrderStudio::public L3InternalOrder getL3Order( String l3OrderId ) | 三级订单获取");

		// 连接订单请求数据库,并查找订单
		return this.l3OrderDB.getOrder(l3OrderId);
	}

	// 生产请求订单获取
	public OrderRequest getOrderRequest(String orderRequestId) {
		System.out
				.println("OrderStudio::public OrderRequest getOrderRequest( String orderRequestId ) | 生产请求订单获取");

		// 连接订单请求数据库,并查找订单
		return this.requestDB.getOrder(orderRequestId);
	}

	// 二级订单查询
	public ArrayList<L2ExternalOrder> searchL2Order(String condition) {
		System.out
				.println("OrderStudio::public ArrayList< L2ExternalOrder > searchL2Order( String condition ) | 二级订单查询");

		return this.l2OrderDB.search(condition);
	}

	// 三级订单查询
	public ArrayList<L3InternalOrder> searchL3Order(String condition) {
		System.out
				.println("OrderStudio::public ArrayList< L3InternalOrder > searchL3Order( String condition ) | 三级订单查询");

		return this.l3OrderDB.search(condition);
	}

	// 生产订单可行性判断
	public boolean isFeasiable(OrderRequest orderRequest) {
		logger.info("OrderStudio::public boolean isFeasibale( OrderRequest orderRequest ) | 生产订单可行性判断");

		// 订单格式验证
		String strOrderIdDemo = "L1CP201404020001L2CP001";
		if (orderRequest.jobId.length() != strOrderIdDemo.length()) {
			logger.error("订单不可行：订单编号错误！");
			return false;
		}

		// test
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>orderRequest.JobId = "
				+ orderRequest.jobId + " OrderId Length is OK.");

		String regEx = "L1(CP|FP|AP|VD)20[0-5][0-9]([0][1-9]|[1][0-2])([0][1-9]|[1-2][0-9]|[3][0-1])([0-9]{4})L2(CP|FP|AP|VD)([0-9]{3})";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(orderRequest.jobId);

		if ((!"L2VD".equals(orderRequest.orderType)) && (!m.matches())) {
			logger.error("订单不可行：真实性检验订单错误！");
			return false;
		}

		// test
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>orderRequest.JobId = "
				+ orderRequest.jobId + " OrderId Format is OK.");

		// 订单数据判定
		if (null == orderRequest.dataStatus) {
			logger.error("订单不可行：数据列表为空！");
			return false;
		}
		if (!orderRequest.dataStatus.equals("Available")) {
			logger.error("订单不可行：数据不可用！");
			return false;
		}

		// test
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>orderRequest.JobId = "
				+ orderRequest.jobId + " DataStatus is Available.");

		// 订单生产流程匹配
		WorkflowAdapter workflowAdapter = new WorkflowAdapter();
		return workflowAdapter.isMatch(orderRequest);
	}

	// 二级订单状态更新
	public boolean setL2OrderWorkingStatus(String l2OrderId,
			String l2OrderStatus) {
		System.out
				.println("OrderStudio::public boolean setOrderWorkflowStatus( String l2OrderId, String l2OrderStatus ) | 二级订单状态更新");

		return this.l2OrderDB.setOrderWorkingStatus(l2OrderId, l2OrderStatus);
	}

	// 三级子订单（四级订单）状态更新
	public boolean setL3SubOrderWorkingStatus(String l4OrderId,
			String l4OrderStatus) {
		System.out
				.println("OrderStudio::public boolean setL3OrderWorkingStatus( String l3OrderId, String l3OrderStatus ) | 三级订单状态更新");

		return this.l4OrderDB.setOrderWorkingStatus(l4OrderId, l4OrderStatus);
	}

	// 三级订单状态更新
	public boolean setL3OrderWorkingStatus(String l3OrderId,
			String l3OrderStatus) {
		System.out
				.println("OrderStudio::public boolean setL3OrderWorkingStatus( String l3OrderId, String l3OrderStatus ) | 三级订单状态更新");

		return this.l3OrderDB.setOrderWorkingStatus(l3OrderId, l3OrderStatus);
	}

	// 二级订单生产状态更新
	public boolean setL2OrderProductStatus(String l2OrderId, String l3OrderType) {
		System.out
				.println("OrderStudio::public boolean setL2OrderProductStatus( String l2OrderId, String l3OrderType ) | 二级订单生产状态更新");

		return this.l2OrderDB.setOrderProductStatus(l2OrderId, l3OrderType);
	}

	// 订单生产确认状态更新
	public boolean setConfirmationStatus(String orderId,
			int confirmationStatus, boolean confirmationMotion) {
		System.out
				.println("OrderStudio::public boolean setConfirmationStatus( String orderId, int confirmationStatus, boolean confirmationMotion ) | 订单生产确认状态更新");

		return this.requestDB.setConfirmationStatus(orderId,
				confirmationStatus, confirmationMotion);
	}

	// 订单数据状态更新
	public boolean setDataStatus(String orderId, String dataStatus,
			ArrayList<String> dataList) {
		System.out
				.println("OrderStudio::public boolean setDataStatus( String orderId, int confirmationStatus, boolean confirmationMotion ) | 订单数据状态更新");

		return this.requestDB.setDataStatus(orderId, dataStatus, dataList);
	}

	// 获取三级订单状态
	public String getL3OrderWorkingStatus(String l3OrderId) {
		System.out
				.println("OrderStudio::public String getL3OrderWorkingStatus( String l3OrderId ) | 获取三级订单状态");

		return this.l3OrderDB.getOrderWorkingStatus(l3OrderId);
	}

	// 设置三级订单列表
	public boolean setL3OrderList(String orderId, String l3OrderList) {
		System.out
				.println("OrderStudio::public boolean setL3OrderList( String orderId, String l3OrderList ) | 设置三级订单列表");

		return this.l2OrderDB.setL3OrderList(orderId, l3OrderList);
	}

	// 设置四级订单（三级子订单）产品列表
	public boolean setL3SubOrderDataProductList(String l4OrderId,
			ArrayList<String> strDataProductList) {
		System.out
				.println("OrderStudio::public boolean setL3SubOrderDataProductList( String l4OrderId, ArrayList< String > strDataProductList ) | 设置三级订单产品列表");

		return this.l4OrderDB.setDataProductList(l4OrderId, strDataProductList);
	}

	// 设置三级订单产品列表
	public boolean setL3OrderDataProductList(String l3OrderId,
			ArrayList<String> strDataProductList) {
		System.out
				.println("OrderStudio::public boolean setL3OrderDataProductList( String l3OrderId, ArrayList< String > strDataProductList ) | 设置三级订单产品列表");

		return this.l3OrderDB.setDataProductList(l3OrderId, strDataProductList);
	}

	// 获取三级订单产品列表
	public ArrayList<String> getL3OrderDataProductList(String l3OrderId) {
		System.out
				.println("OrderStudio::public ArrayList< String > getL3OrderDataProductList( String l3OrderId ) | 获取三级订单产品列表");

		return this.l3OrderDB.getDataProductList(l3OrderId);
	}

	// 设置三级订单算法资源ID
	public boolean setL3OrderAlgorithmID(String l3OrderId, Algorithm algorithm) {
		System.out
				.println("OrderStudio::public boolean setL3OrderAlgorithmID( String l3OrderId, Algorithm algorithm ) | 设置三级订单算法资源ID");

		return this.l3OrderDB.setAlgorithmID(l3OrderId, algorithm);
	}

	public ArrayList<String> getL3OrderRetrievalDataListByL2OrderId(
			String strL2OrderId) {

		return this.l3OrderDB.getRetrievalDataListByL2OrderId(strL2OrderId);
	}

	// 订单拆分
	// orderId：订单号（二级订单号）
	public ArrayList<String> splitOrder(L2ExternalOrder order) {
		System.out
				.println("OrderStudio::public ArrayList<String> splitOrder(String orderId) | 订单拆分");
		ArrayList<String> splitOrderList = new ArrayList<String>();
		for (int iIndex = 0; iIndex < 1; iIndex++) {
			// 3级子订单生成
			L3InternalOrder l3Order = new L3InternalOrder(order);

			// 子订单入库
			L3InternalOrder l3OrderStorage = this.addOrder(l3Order);
			splitOrderList.add(l3OrderStorage.jobId);

		}
		return splitOrderList;
	}

	// 删除订单请求列表
	public boolean deleteOrderRequest(ArrayList<OrderRequest> requestList) {
		System.out
				.println("OrderStudio::public boolean deleteOrderRequest( ArrayList< OrderRequest > requestList ) | 删除订单请求列表");
		return this.requestDB.deleteOrderRequest(requestList);
	}

	public class OrderDatas {
		public String dataStatus;
		public ArrayList<String> dataList;

		public void setDataStatus(String dataStatus) {
			System.out
					.println("OrderDatas::public void setDataStatus(String dataStatus) | ");
			this.dataStatus = dataStatus;
		}

		public void setDataList(ArrayList<String> dataList) {
			System.out
					.println("OrderDatas::public void setDataList(ArrayList<String> dataList) | ");
			this.dataList = dataList;
		}

		public void addData(String data) {
			System.out
					.println("OrderDatas::public void addData(String data) | ");
			this.dataList.add(data);
		}

		public String getDataStatus() {
			System.out.println("OrderDatas::public String getDataStatus() | ");
			return this.dataStatus;
		}

		public ArrayList<String> getDataList() {
			System.out
					.println("OrderDatas::public ArrayList<String>getDataList() | ");
			return this.dataList;
		}

		public String getData(int i) {
			System.out.println("OrderDatas::public String getData( int i ) | ");
			return this.dataList.get(i);
		}
	}
}
