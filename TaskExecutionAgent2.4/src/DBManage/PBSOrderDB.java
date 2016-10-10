package DBManage;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import TaskExeAgent.SystemLogger;
import OrderManage.L3InternalOrder;
import OrderManage.L3OrderPbsProgressCount;
import Pbs.PbsOrder;
import RSDataManage.RSData;

/**
 * 创建时间：2016-2-25 下午8:54:24 项目名称：TaskExecutionAgent2.0 2016-2-25
 * 
 * @author 张杰
 * @version 1.0 文件名称：ScheduleRuleDB.java 类说明：PBS订单库
 */
public class PBSOrderDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// 构造函数
	public PBSOrderDB() {
		System.out.println("PBSOrderDB::public PBSOrderDB () | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>PBSOrderDB::public PBSOrderDB () | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}
		this.dbTable = "pbsorderdb";
	}

	public static synchronized void closeConnected() {
		try {
			if (null != conn) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 向订单库插入订单
	public synchronized boolean addOrder(PbsOrder order) {
		logger.info("PBSOrderDB::public boolean addOrder( PbsOrder order ) | 向订单库插入订单");

		// 将订单插入数据库的订单表中
		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(JobId_L3,OrderType,Priority,productName,startDate,endDate,workingStatus,dataListPath,SubmitDate,PBSId,"
					+ "PbsFile,OrderParmeterFile,ResultLogFile,AlgorithmName,AlgorithmPath,ProductDir,dataid,dataName)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// L3订单ID（）
			int count = 1;
			pstmt.setString(count++, order.getJobIdL3());
			// 用来存放订单类型
			pstmt.setString(count++, order.getOrderType());
			// PBS订单优先级
			pstmt.setInt(count++, order.getPriority());
			// 产品名称
			pstmt.setString(count++, order.getProductName());
			// 时相（成像时间）开始时间
			pstmt.setTimestamp(count++, new Timestamp(order.getStartDate()
					.getTime()));
			// 时相（成像时间）结束时间
			pstmt.setTimestamp(count++, new Timestamp(order.getEndDate()
					.getTime()));
			// 订单状态:已经提交
			pstmt.setString(count++, "Submited");
			// 数据列表
			pstmt.setString(count++, order.getDataListPath());
			// 订单提交时间
			pstmt.setTimestamp(count++, new Timestamp(order.getSubmitDate()
					.getTime()));
			// 订单PBS提交号
			pstmt.setString(count++, order.getPbsid());
			// 订单PBS腳本文件
			pstmt.setString(count++, order.getPbsFile());
			// 订单参数文件
			pstmt.setString(count++, order.getOrderParmeterFile());
			// 订单结果文件
			pstmt.setString(count++, order.getResultLogFile());

			// 算法资源名称
			pstmt.setString(count++, order.getAlgorithmName());
			// 算法资源程序
			pstmt.setString(count++, order.getAlgorithmPath());
			// 订单产品目录
			pstmt.setString(count++, order.getProductDir());
			// dataid
			pstmt.setString(count++, order.getDataid());
			// dataName
			pstmt.setString(count++, order.getDataName());

			// test
			// System.out.println(strSql);
			// System.out.println(pstmt.toString());
			// 执行插入
			pstmt.execute();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			// if( ( strSqlState.equals( "08S01" ) ) || ( strSqlState.equals(
			// "40001" ) ) )
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return addOrder(order);
			}
			e.printStackTrace();
			logger.error("向PBS订单库插入订单失败！\n" + e);
			return false;
		}
		return true;
	}
	
	// 删除列表中的三级订单
	public synchronized boolean deleteOrder(
			ArrayList<String> orderList) {
		logger.info("PBSOrderDB::public boolean deleteOrder( ArrayList< L3InternalOrder > orderlist ) | 删除列表中的PBS订单");

		try {
			Iterator<String> order_curr = orderList.iterator();
			while (order_curr.hasNext()) {
				String strSql = "DELETE FROM " + this.dbTable
						+ " WHERE JobId = ?";

				if (null == conn) {
					return false;
				}
				PreparedStatement pstmt = conn.prepareStatement(strSql);
				pstmt.setString(1, order_curr.next());

				pstmt.execute();

				// 关闭相关连接
				pstmt.close();
			}
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return deleteOrder(orderList);
			}
			e.printStackTrace();
			logger.error("删除列表中的PBS订单失败！" + e);
			return false;
		}
		return true;
	}
	

	// 更新订单状态
	public synchronized boolean setOrderWorkflowStatus(String pbsOrderId,
			String orderStatus) {
		logger.info("PbsOrderDB::public boolean setOrderWorkflowStatus( String pbsOrderId, String orderStatus ) | 设置订单状态");

		// System.out.println(orderStatus);
		switch (orderStatus) {
		case "Unknown":
			break;
		case "Finish":
			break;
		case "Error":
			break;
		case "Running":
			break;
		default:
			// System.out.println("Hello false");
			return false;
		}
		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET workingStatus = ? WHERE PBSId = ?";
			// test
			// System.out.println(strSql);

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 订单状态
			pstmt.setString(1, orderStatus);
			// pbs订单ID
			pstmt.setString(2, pbsOrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkflowStatus(pbsOrderId, orderStatus);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置订单" + pbsOrderId + "状态失败！\n" + e);
			return false;
		}
		return true;
	}

	// 更新订单运行结束时间
	// 更新订单四角坐标
	// 更新订单产品列表
	public synchronized boolean setFinishInfos(String PbsOrderId, Date enDate,
			ArrayList<String> strDataProductList) {
		logger.info("PbsOrderDB::public boolean setDataProductList( String l3OrderId, ArrayList< String > strDataProductList ) | 设置订单产品列表");

		try {
			String strSql = "UPDATE "
					+ this.dbTable
					+ " SET dataProductList = ? , FinishDate = ? WHERE PBSId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 产品列表
			if (strDataProductList.size() > 0) {
				String[] strList = new String[strDataProductList.size()];
				strDataProductList.toArray(strList);
				// test
				// System.out.println(String.join(";", strList));

				pstmt.setString(1, String.join(";", strList));
			} else {
				// 如果没有获取到数据列表就赋值为空字符串
				pstmt.setString(1, "");
			}
			// 結束時間
			pstmt.setTimestamp(2, new Timestamp(enDate.getTime()));

			// 订单ID
			pstmt.setString(3, PbsOrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setFinishInfos(PbsOrderId, enDate, strDataProductList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置订单产品列表和提交失败！" + e);
			return false;
		}
		return true;
	}

	// 查询订单
	// 向订单库查询订单
	// 参数1：condition 其值类似于“JobId = ?”
	public synchronized ArrayList<PbsOrder> search(String condition) {
		logger.info("PbsOrderDB::public ArrayList<PbsOrder> search ( String condition ) | 向PBS订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<PbsOrder> orderList = new ArrayList<PbsOrder>();
		PbsOrder pbsOrder = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;
						
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			boolean flag = false;

			while (rs.next()) {
				flag = true;

				pbsOrder = new PbsOrder();
				// 订单ID
				pbsOrder.setJobId(rs.getString("JobId"));
				// PBS ID
				pbsOrder.setPbsid(rs.getString("PBSId"));
				// 订单ID（L3）
				pbsOrder.setJobIdL3(rs.getString("JobId_L3"));
				// 用来存放订单类型
				pbsOrder.setOrderType(rs.getString("OrderType"));
				// 优先级 0-9
				pbsOrder.setPriority(rs.getInt("Priority"));
				// 用来记录生产的共性数据产品名称
				pbsOrder.setProductName(rs.getString("productName"));
				// 开始时间
				pbsOrder.setStartDate(rs.getDate("startDate"));
				// 结束时间
				pbsOrder.setEndDate(rs.getDate("endDate"));
				// 订单状态：Submited/Running/Finish/Error/
				pbsOrder.setWorkingStatus(rs.getString("workingStatus"));
				// 数据路径
				pbsOrder.setDataListPath(rs.getString("dataListPath"));
				// 用来记录提交时间
				pbsOrder.setSubmitDate(rs.getDate("SubmitDate"));
				// 用来记录完成时间
				pbsOrder.setFinishDate(rs.getDate("FinishDate"));
				// PBS脚本文件
				pbsOrder.setPbsFile(rs.getString("PbsFile"));
				// 结果日志文件
				pbsOrder.setResultLogFile(rs.getString("ResultLogFile"));
				// 操作员
				pbsOrder.setOperatorid(rs.getString("OPERATORID"));
				// 算法资源名称
				pbsOrder.setAlgorithmName(rs.getString("AlgorithmName"));
				// 算法资源程序
				pbsOrder.setAlgorithmPath(rs.getString("AlgorithmPath"));
				// 产品列表
				pbsOrder.setDataProductList(rs.getString("dataProductList"));
				// 产品目录
				pbsOrder.setProductDir(rs.getString("ProductDir"));
				// dataid
				pbsOrder.setDataid(rs.getString("dataid"));
				// dataid
				pbsOrder.setDataName(rs.getString("dataName"));

				orderList.add(pbsOrder);
				flag = true;
			}

			if (!flag) {
				// logger.error("查询PBS订单结果为空!请检查检索参数：" + condition + "是否合法!");
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("查询PBS订单执行失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(condition);
			}
			e.printStackTrace();
			// return null;
			orderList.clear();
			return orderList;
		}
		return orderList;
	}
	
	// 设置订单状态
	public synchronized boolean setItemValue(String condition) {
		logger.info("PBSOrderDB::public boolean setOrderStatus(String strSql) | 订单值更新");
		try {
			String strSql = "UPDATE " + this.dbTable + " " + condition;
			
//			//test
			logger.info(">>"+strSql);

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setItemValue(condition);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置订单特定值失败！\n" + e);
			return false;
		}

		return true;
	}
	

	// 获取不同状态的订单列表，参数workingStatus可以为Running Finish Error不同状态
	public synchronized ArrayList<PbsOrder> getOrderList() {
		logger.info("PbsOrderDB::public ArrayList<PbsOrder> getOrderList() | 获取PBS完成的订单列表");
		ArrayList<PbsOrder> pbsOrderList = new ArrayList<PbsOrder>();

		PbsOrder pbsOrder = null;
		String strSql = " WHERE workingStatus = 'Submited' OR workingStatus = 'Running'";
		pbsOrderList = search(strSql);
		return pbsOrderList;
	}

	// 获取指定父订单所属的PBS 订单列表的处理进度
	public synchronized L3OrderPbsProgressCount getOrderList(
			L3InternalOrder l3order) {
		logger.info("PbsOrderDB::public ArrayList<PbsOrder> getOrderList(L3InternalOrder l3order) | 获取PBS订单列表的处理进度统计");
		ArrayList<String> pbsOrderList = l3order.pbsOrderLists;
		L3OrderPbsProgressCount progressCount = new L3OrderPbsProgressCount();

		if (l3order.pbsOrderLists == null || l3order.pbsOrderLists.size() == 0) {
			logger.error(l3order.jobId + "PBS订单为0！");
			return progressCount;
		}

		progressCount.sumCount = pbsOrderList.size();
		Iterator<String> iterator = pbsOrderList.iterator();

		while (iterator.hasNext()) {
			String pbsOrderId = (String) iterator.next();

			String strSql = " WHERE PBSId ='" + pbsOrderId + "'";

			ArrayList<PbsOrder> pbsOrders = search(strSql);

			if (pbsOrders == null || pbsOrders.isEmpty()) {
				logger.info(pbsOrderId + "订单未找到！");
				break;
			}

			PbsOrder pbsOrder = pbsOrders.get(0);

			String orderStatus = pbsOrder.getWorkingStatus();

			// test
			// System.out.println(orderStatus);

			switch (orderStatus) {
			case "Running":
				progressCount.addRunningOrder(pbsOrderId);
				break;
			case "Finish":
				progressCount.addFinishOrder(pbsOrderId);
				break;
			case "Error":
				progressCount.addErrorOrder(pbsOrderId);
				break;
			case "Unknown":
				progressCount.addUnknownOrder(pbsOrderId);
				break;
			case "Submited":
				progressCount.addSubmitedOrder(pbsOrderId);
				break;
			default:
				logger.error(pbsOrder.getPbsid() + "订单状态异常！");
				break;
			}
		}

		logger.info(l3order.jobId + "_" + progressCount.getProgressCount());
		return progressCount;
	}
	
	//获取未确认订单列表
	public ArrayList<PbsOrder> getUnConfirmOrders() {
		ArrayList<PbsOrder> pbsOrders=new ArrayList<PbsOrder>();
		
		
		return pbsOrders;
		
	}
	
	

}
