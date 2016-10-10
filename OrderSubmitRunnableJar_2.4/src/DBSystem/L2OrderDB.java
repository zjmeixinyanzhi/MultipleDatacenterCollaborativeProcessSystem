/*
 *程序名称 		: L2OrderDB.java
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
package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.sql.rowset.serial.SerialBlob;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.log4j.Logger;
//import com.mchange.v2.c3p0.ComboPooledDataSource;

import OrderManage.L2ExternalOrder;
import OrderManage.Order;
import LogSystem.SystemLogger;

/**
 * @author caoyang
 * 
 */
public class L2OrderDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	// 构造函数
	public L2OrderDB() {
		System.out.println("L2OrderDB::public L2OrderDB () | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>L2OrderDB::public L2OrderDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "l2orderdb";
	}

	public static synchronized void closeConnected() {
		try {
			if (null != conn) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	// 向订单库插入订单
	public synchronized boolean addOrder(L2ExternalOrder order) {
		System.out
				.println("L2OrderDB::public boolean addOrder( L2ExternalOrder order ) | 向订单库插入订单");
		
		if (null==order) {
			logger.error("二级订单对象为空，插入失败！");
			return false;
		}
		

		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(JobId,WorkFlowId,Priority,JobId_L1,JobId_P3L2,OrderType,OrderLevel,DoValidation,"
					+ "productName,geoCoverageStr,startDate,endDate,workingStatus,orderStatus,dataStatus,dataList,SubmitDate,"
					+ "FinishDate,OrderPARAMETER,OPERATORID,retrievalDataList,dataType)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 订单ID（L2）
			pstmt.setString(1, order.jobId);
			// 工作流ID
			pstmt.setInt(2, order.workFlowId);
			// 优先级 0-9
			pstmt.setInt(3, order.priority);
			// 一级订单ID（L1）
			pstmt.setString(4, order.jobId_L1);
			// （课题三）二级订单ID（L2）
			pstmt.setString(5, order.jobId_P3L2);
			// 用来存放订单类型：CP/FP/AP/VD
			pstmt.setString(6, order.orderType);
			// 订单级别
			pstmt.setString(7, order.orderLevel);
			// 真实性检验
			// pstmt.setString( 8, order.doValidation );
			pstmt.setString(8, "");
			// 用来记录生产的共性数据产品名称
			pstmt.setString(9, order.productName);
			// 地理区域
			pstmt.setString(10, order.geoCoverageStr);
			// 开始时间
			pstmt.setDate(11, order.startDate);
			// 结束时间
			pstmt.setDate(12, order.endDate);
			// 订单状态：Start/Running/Finish/Error/Suspend
			pstmt.setString(13, "");
			// 订单的生产状态：RadioNorm /GeoNorm/ CommonProduct/Fusion/Assimilation
			pstmt.setString(14, "");
			// 订单的数据状态：Available/NotAvailable/Future
			pstmt.setString(15, order.dataStatus);
			String strDataList = "";
			if (order.dataList != null || !order.dataList.isEmpty()) {
				for (Iterator<String> data_curr = order.dataList.iterator(); data_curr
						.hasNext();) {
					strDataList += data_curr.next() + ";";
				}
			}
			pstmt.setString(16, strDataList);
			// 用来记录提交时间
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				format.setLenient(false);
				java.util.Date now_date = new Date(System.currentTimeMillis());
				String strSubmitDate = format.format(now_date);
				order.submitDate = new Date(format.parse(strSubmitDate)
						.getTime());
				pstmt.setDate(17, order.submitDate);
			} catch (java.text.ParseException e) {
				logger.error(e);
			}
			// 用来记录完成时间
			try {
				String strFinishDate = "1980-01-01";
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				format.setLenient(false);
				java.util.Date finishDate = format.parse(strFinishDate);
				pstmt.setDate(18, new Date(finishDate.getTime()));
			} catch (java.text.ParseException e) {
				logger.error(e);
			}

			// 任务单参数
			if (order.orderParameter!=null) {
				pstmt.setString(19, order.orderParameter);
			}else {
				pstmt.setString(19, "");
			}
			
			// 操作员
			pstmt.setString(20, "");
			// 与融合/同化相关联的共性产品信息
			String strRetrievalDataList = "";
			if (order.retrievalDataList != null
					&& !order.retrievalDataList.isEmpty()) {
				for (Iterator<String> data_curr = order.retrievalDataList
						.iterator(); data_curr.hasNext();) {
					strRetrievalDataList += data_curr.next() + ";";
				}
			}
			pstmt.setString(21, strRetrievalDataList);
			// 原始数据类型
			pstmt.setString(22, order.dataType);

			// 执行插入
			int count = pstmt.executeUpdate();
			if (count == 0) {
				logger.error("插入二级订单更新行数为0，请检查二级订单对象参数!");
			}
			// pstmt.execute();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("向二级订单库插入订单执行失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return addOrder(order);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 向订单库查询订单
	// 参数1：condition 其值类似于“JobId = ?”
	public synchronized ArrayList<L2ExternalOrder> search(String condition) {
		System.out
				.println("L2OrderDB::public ArrayList< L2ExternalOrder > search( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<L2ExternalOrder> orderList = new ArrayList<L2ExternalOrder>();
		L2ExternalOrder l2Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return orderList;
			}

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			boolean flag = false;

			while (rs.next()) {

				flag = true;

				l2Order = new L2ExternalOrder();
				// 订单ID（L2）
				l2Order.jobId = rs.getString("JobId");
				// 工作流ID
				l2Order.workFlowId = rs.getInt("WorkFlowId");
				// 优先级 0-9
				l2Order.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				l2Order.jobId_L1 = rs.getString("JobId_L1");
				// 一级订单ID（L1）
				l2Order.jobId_P3L2 = rs.getString("JobId_P3L2");
				// 用来存放订单类型：CP/FP/AP/VD
				l2Order.orderType = rs.getString("OrderType");
				// 订单级别
				l2Order.orderLevel = rs.getString("OrderLevel");
				// 真实性检验
				l2Order.doValidation = rs.getString("DoValidation");
				// 用来记录生产的共性数据产品名称
				l2Order.productName = rs.getString("productName");
				// 地理区域
				l2Order.geoCoverageStr = rs.getString("geoCoverageStr");
				// 开始时间
				l2Order.startDate = rs.getDate("startDate");
				// 结束时间
				l2Order.endDate = rs.getDate("endDate");
				// 订单状态：Start/Running/Finish/Error/Suspend
				l2Order.workingStatus = rs.getString("workingStatus");
				// 订单的生产状态：RadioNorm /GeoNorm/ CommonProduct/Fusion/Assimilation
				l2Order.orderStatus = rs.getString("orderStatus");
				// 订单的数据状态：Available/NotAvailable/Future
				l2Order.dataStatus = rs.getString("dataStatus");
				// 用来记录订单的数据列表
				l2Order.dataList = new ArrayList<String>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					String[] strDataSplitArray = strDataList.split(";");
					l2Order.dataList.addAll(Arrays.asList(strDataSplitArray));
				}
				// 用来记录提交时间
				l2Order.submitDate = rs.getDate("SubmitDate");
				// 用来记录完成时间
				l2Order.finishDate = rs.getDate("FinishDate");
				// 任务单参数
				l2Order.orderParameter = rs.getString("OrderPARAMETER");
				// 操作员
				l2Order.operatorId = rs.getString("OPERATORID");
				// 三级订单列表
				l2Order.l3orderlist = rs.getString("l3OrderList");
				// 与融合/同化相关联的共性产品信息
				l2Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if (!strRetrievalDataList.equals("")) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l2Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				l2Order.dataType = rs.getString("dataType");

				orderList.add(l2Order);
			}

			if (!flag) {
				logger.error("二级订单查询结果为空!请检查检索条件："+condition+"是否合法！");
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("向二级订单库查询订单执行失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(condition);
			}
			e.printStackTrace();
			orderList.clear();
			return orderList;
		}
		return orderList;
	}

	public synchronized String generateId(String orderType) {
		System.out
				.println("L2OrderDB::public String generateId( String orderType ) | 生成订单序号");

		String strId = null;
		try {
			if ((null == orderType) || (orderType.isEmpty())) {
				return strId;
			}

			String strSql = "SELECT JobId FROM "
					+ this.dbTable
					+ " WHERE OrderType LIKE ? and JobId LIKE ? ORDER BY JobId ASC";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return strId;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			format.setLenient(false);
			java.util.Date date = new Date(System.currentTimeMillis());
			String strDate = format.format(date);
			pstmt.setString(1, orderType + "%");
			String strJobId = "____" + strDate + "____";
			pstmt.setString(2, strJobId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.last()) {
				strId = rs.getString("JobId");
				Integer iId = Integer.valueOf(strId.substring(12)) + 1;
				strId = String.format("%04d", iId);
			} else {
				strId = "0001";
			}

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("生成二级订单Id失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				// return generateId();
				return generateId(orderType);
			}
			e.printStackTrace();
			return null;
		}

		return strId;
	}

	// 获取二级订单
	public synchronized L2ExternalOrder getOrder(String strOrderId) {
		System.out
				.println("L2OrderDB::public L2ExternalOrder getOrder( String strOrderId ) | 获取二级订单");

		L2ExternalOrder l2Order = null;
		String strQuery = "WHERE JobId = '" + strOrderId + "'";
		ArrayList<L2ExternalOrder> orderList = this.search(strQuery);
		if (!orderList.isEmpty()) {
			l2Order = orderList.get(0);
		}

		return l2Order;
	}

	// 获取二级订单
	public synchronized L2ExternalOrder getOrderByL1JobId(String strL1OrderId,
			String strL2OrderType) {
		System.out
				.println("L2OrderDB::public synchronized L2ExternalOrder getOrderByL1JobId( String strL1OrderId, String strL2OrderType ) | 获取二级订单");

		L2ExternalOrder l2Order = null;

		String strQuery = "WHERE JobId_L1 = '" + strL1OrderId
				+ "' and OrderType = '" + strL2OrderType
				+ "' and workingStatus = 'Finish'";
		ArrayList<L2ExternalOrder> orderList = this.search(strQuery);
		if (!orderList.isEmpty()) {
			l2Order = orderList.get(0);
		}

		return l2Order;
	}

	// 获取二级订单
	public synchronized L2ExternalOrder getL2CPOrderByL1JobId(
			String strL1OrderId) {

		L2ExternalOrder l2Order = null;
		// String strQuery = "WHERE JobId_L1 = '" + strL1OrderId +
		// "' and OrderType = 'L2CP' and workingStatus = 'Finish'";
		String strQuery = "WHERE JobId_L1 = '" + strL1OrderId
				+ "' and OrderType LIKE 'L2CP%' and workingStatus = 'Finish'";
		ArrayList<L2ExternalOrder> orderList = this.search(strQuery);
		if (!orderList.isEmpty()) {
			l2Order = orderList.get(0);
		}

		return l2Order;
	}

	// 设置三级订单列表
	public synchronized boolean setL3OrderList(String orderId,
			String l3OrderList) {
		System.out
				.println("L2OrderDB::public boolean setL3OrderList( String orderId, String l3OrderList ) | 设置三级订单列表");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET l3OrderList = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 三级订单列表
			pstmt.setString(1, l3OrderList);
			// 二级订单ID
			pstmt.setString(2, orderId);

			// test

//			System.out.println(">> " + System.currentTimeMillis());
			// 执行更新
			int count = pstmt.executeUpdate();
			if (count == 0) {
				logger.error("设置二级订单的三级订单列表更新行数为0!请检查二级订单号："+orderId+",三级订单列表："+l3OrderList+"参数是否合法！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("设置二级订单的三级订单列表失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setL3OrderList(orderId, l3OrderList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 设置订单状态
	public synchronized boolean setOrderWorkingStatus(String l2OrderId,
			String l2OrderStatus) {
		System.out
				.println("L2OrderDB::public boolean setOrderWorkingStatus( String l2OrderId, String l2OrderStatus ) | 设置订单状态");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET workingStatus = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 订单状态
			pstmt.setString(1, l2OrderStatus);
			// 二级订单ID
			pstmt.setString(2, l2OrderId);

			// 执行更新
			int count = pstmt.executeUpdate();
			if (count == 0) {
				logger.error("设置三级订单WorkingStatus更新行数为0，请检查二级订单号："+l2OrderId+",订单状态："+l2OrderStatus+"参数是否合法！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("设置三级订单我WorkingStatus失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkingStatus(l2OrderId, l2OrderStatus);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 设置订单生产状态
	public synchronized boolean setOrderProductStatus(String l2OrderId,
			String l3OrderType) {
		System.out
				.println("L2OrderDB::public boolean setOrderProductStatus( String l2OrderId, String l3OrderType ) | 设置订单生产状态");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET orderStatus = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 订单生产状态
			pstmt.setString(1, l3OrderType);
			// 二级订单ID
			pstmt.setString(2, l2OrderId);

			// 执行更新
			int count = pstmt.executeUpdate();
			if (count == 0) {
				logger.error("设置二级订单OrderProductStatus更新行数为0!请检查二级订单号："+l2OrderId+",订单状态："+l3OrderType+"参数是否合法！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("设置二级订单OrderProductStatus失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderProductStatus(l2OrderId, l3OrderType);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 更新订单
	public synchronized boolean updateOrder(L2ExternalOrder order) {
		System.out
				.println("L2OrderDB::public synchronized boolean updateOrder( L2ExternalOrder order ) | 更新订单");
		
		if (null==order) {
			logger.error("二级订单对象为空，更新订单失败！");
			return false;
		}

		try {
			String strSql = "UPDATE "
					+ this.dbTable
					+ " SET WorkFlowId = ? , Priority = ? , JobId_L1 = ? , JobId_P3L2 = ? , "
					+ "OrderType = ? , OrderLevel = ? , DoValidation = ? , productName = ? , geoCoverageStr = ? , startDate = ? , "
					+ "endDate = ? , workingStatus = ? , orderStatus = ? , dataStatus = ? , dataList = ? , SubmitDate = ? , "
					+ "FinishDate = ? , OrderPARAMETER = ? , OPERATORID = ? , retrievalDataList = ?, dataType = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			int iIndex = 0;
			// 工作流ID
			pstmt.setInt(++iIndex, order.workFlowId);
			// 优先级 0-9
			pstmt.setInt(++iIndex, order.priority);
			// 一级订单ID（L1）
			pstmt.setString(++iIndex, order.jobId_L1);
			// （课题三）二级订单ID（L2）
			pstmt.setString(++iIndex, order.jobId_P3L2);
			// 用来存放订单类型：CP/FP/AP/VD
			pstmt.setString(++iIndex, order.orderType);
			// 订单级别
			pstmt.setString(++iIndex, order.orderLevel);
			// 真实性检验
			pstmt.setString(++iIndex, "");
			// 用来记录生产的共性数据产品名称
			pstmt.setString(++iIndex, order.productName);
			// 地理区域
			pstmt.setString(++iIndex, order.geoCoverageStr);
			// 开始时间
			pstmt.setDate(++iIndex, order.startDate);
			// 结束时间
			pstmt.setDate(++iIndex, order.endDate);
			// 订单状态：Start/Running/Finish/Error/Suspend
			pstmt.setString(++iIndex, "");
			// 订单的生产状态：RadioNorm /GeoNorm/ CommonProduct/Fusion/Assimilation
			pstmt.setString(++iIndex, "");
			// 订单的数据状态：Available/NotAvailable/Future
			pstmt.setString(++iIndex, order.dataStatus);
			// 用来记录订单的数据列表
			String strDataList = "";
			if (order.dataList != null || !order.dataList.isEmpty()) {
				for (Iterator<String> data_curr = order.dataList.iterator(); data_curr
						.hasNext();) {
					strDataList += data_curr.next() + ";";
				}
			}
			pstmt.setString(++iIndex, strDataList);
			// 用来记录提交时间
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				format.setLenient(false);
				java.util.Date now_date = new Date(System.currentTimeMillis());
				String strSubmitDate = format.format(now_date);
				order.submitDate = new Date(format.parse(strSubmitDate)
						.getTime());
				pstmt.setDate(++iIndex, order.submitDate);
			} catch (java.text.ParseException e) {
				logger.error(e);
			}
			// 用来记录完成时间
			try {
				String strFinishDate = "1980-01-01";
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				format.setLenient(false);
				java.util.Date finishDate = format.parse(strFinishDate);
				pstmt.setDate(++iIndex, new Date(finishDate.getTime()));
			} catch (java.text.ParseException e) {
				logger.equals(e);
			}

			// 任务单参数
			pstmt.setString(++iIndex, "");
			// 操作员
			pstmt.setString(++iIndex, "");
			// 与融合/同化相关联的共性产品信息
			String strRetrievalDataList = "";
			if (order.retrievalDataList != null
					&& !order.retrievalDataList.isEmpty()) {
				for (Iterator<String> data_curr = order.retrievalDataList
						.iterator(); data_curr.hasNext();) {
					strRetrievalDataList += data_curr.next() + ";";
				}
			}
			pstmt.setString(++iIndex, strRetrievalDataList);
			// 原始数据类型
			pstmt.setString(++iIndex, order.dataType);
			// 订单ID（L2）
			pstmt.setString(++iIndex, order.jobId);

			// 执行插入
			// pstmt.execute();
			// 执行更新
			int count = pstmt.executeUpdate();
			if (count == 0) {
				logger.error("二级订单更新行数为0!请检查二级订单对象是否合法！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新二级订单失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return updateOrder(order);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 删除二级订单列表
	public synchronized boolean deleteOrder(
			ArrayList<L2ExternalOrder> l2OrderList) {
		System.out
				.println("L2OrderDB::public synchronized boolean deleteOrder( ArrayList< L2ExternalOrder > l2OrderList ) | 删除二级订单列表");
		if (null==l2OrderList) {
			logger.error("二级订单对象列表为空，删除订单失败！");
			return false;
		}
		try {
			Iterator<L2ExternalOrder> l2Order = l2OrderList.iterator();
			while (l2Order.hasNext()) {
				String strSql = "DELETE FROM " + this.dbTable
						+ " WHERE JobId = ?";
				if (null == conn) {
					logger.error("数据库连接初始化失败！");
					return false;
				}
				PreparedStatement pstmt = conn.prepareStatement(strSql);
				pstmt.setString(1, l2Order.next().jobId);

				// 执行更新
				int count=pstmt.executeUpdate();
				if (count==0) {
					logger.error("删除二级订单更新行数为0!");
				}

				// 关闭相关连接
				pstmt.close();
			}
		} catch (SQLException e) {
			logger.error("删除二级订单失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>L2OrderDB::deleteOrder | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return deleteOrder(l2OrderList);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
