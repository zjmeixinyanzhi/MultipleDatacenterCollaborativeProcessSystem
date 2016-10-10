/*
 *程序名称 		: RequestDB.java
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

import java.sql.Date;
import java.sql.Clob;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.log4j.Logger;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

//import com.mchange.v2.c3p0.ComboPooledDataSource;

//import OrderManage.Order;
import OrderManage.OrderRequest;
import LogSystem.SystemLogger;

/**
 * @author caoyang
 * 
 */
public class RequestDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	private static byte[] lockReadWrite = new byte[0];
	// 日志系统
	SystemLogger systemLogger = new SystemLogger();
	private Logger logger = systemLogger.getSysLogger();

	// 构造函数
	public RequestDB() {
		System.out.println("RequestDB::public RequestDB() | 构造函数");

		// 初始化连接SQL执行
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>RequestDB::public RequestDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "orderrequestdb";
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

	// 获取生产请求订单
	public synchronized OrderRequest getOrder(String orderRequestId) {
		System.out
				.println("RequestDB::public OrderRequest getOrderRequest( String orderRequestId ) | 获取生产请求订单");

		OrderRequest orderRequest = null;

		String strSql = "SELECT * FROM " + this.dbTable + " WHERE JobId = ?";
		try {
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderRequest;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			// 订单ID（L2）
			pstmt.setString(1, orderRequestId);

			ResultSet rs = pstmt.executeQuery();
			
			if (!rs.next()) {
				logger.error("获取生产请求订单：查询结果为0！");
			} else {
				rs.previous();
				if (rs.first()) {
					orderRequest = new OrderRequest();
					// 订单ID（L2）
					orderRequest.jobId = rs.getString("JobId");
					// 工作流ID
					orderRequest.workFlowId = rs.getInt("WorkflowId");
					// 优先级 0-9
					orderRequest.priority = rs.getInt("Priority");
					// 一级订单ID（L1）
					orderRequest.jobId_L1 = rs.getString("JobId_L1");
					// 用来存放订单类型：CP/FP/AP/VD
					orderRequest.orderType = rs.getString("OrderType");
					// 订单级别
					orderRequest.orderLevel = rs.getString("OrderLevel");
					// 真实性检验
					orderRequest.doValidation = rs.getString("DoValidation");
					// 用来记录生产的共性数据产品名称
					orderRequest.productName = rs.getString("productName");
					// 地理区域
					orderRequest.geoCoverageStr = rs
							.getString("geoCoverageStr"); // XML字符串
					// 开始时间
					orderRequest.startDate = rs.getDate("startDate");
					// 结束时间
					orderRequest.endDate = rs.getDate("endDate");
					// 数据状态
					orderRequest.dataStatus = rs.getString("dataStatus");
					// 数据列表
					orderRequest.dataList = new ArrayList<String>();
					String strDataList = rs.getString("dataList");
					if ((null != strDataList) && (!strDataList.equals(""))) {
						String[] strDataSplitArray = strDataList.split(";");
						orderRequest.dataList.addAll(Arrays
								.asList(strDataSplitArray));
					}
					// 任务单参数
					orderRequest.orderParameter = rs
							.getString("OrderPARAMETER");
					// 操作员
					orderRequest.operatorId = rs.getString("OPERATORID");
					// 生产确认内容
					orderRequest.confirmationMotion = rs
							.getBoolean("confirmationMotion");
					// 状态
					orderRequest.status = rs.getInt("status");
					// 与融合/同化相关联的共性产品信息
					orderRequest.retrievalDataList = new ArrayList<String>();
					String strRetrievalDataList = rs
							.getString("retrievalDataList");
					if ((null != strRetrievalDataList)
							&& (!strRetrievalDataList.equals(""))) {
						String[] strDataSplitArray = strRetrievalDataList
								.split(";");
						orderRequest.retrievalDataList.addAll(Arrays
								.asList(strDataSplitArray));
					}
					// 原始数据类型
					orderRequest.dataType = rs.getString("dataType");
				}
			}

			// 关闭相关连接
			pstmt.close();
			rs.close();
		} catch (SQLException e) {

			logger.error("算法查询执行失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out.println("<Error>RequestDB::getOrder | SQL error code : "
					+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOrder(orderRequestId);
			}
			e.printStackTrace();
			return null;
		}
		return orderRequest;
	}

	// 获取当前订单请求列表
	public synchronized ArrayList<OrderRequest> getOrderRequest() {
		System.out
				.println("RequestDB::public ArrayList<OrderRequest> getOrderRequest() | 获取当前订单请求列表");
		ArrayList<OrderRequest> orderRequestList = new ArrayList<OrderRequest>();

		String strSql = "SELECT * FROM " + this.dbTable + " WHERE status = 0";
		try {
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderRequestList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			if (!rs.next()) {
				logger.error("获取当前订单请求列表：查询结果为0！");
			} else {
				rs.previous();
				while (rs.next()) {
					OrderRequest orderRequest = new OrderRequest();
					// 订单ID（L2）
					orderRequest.jobId = rs.getString("JobId");
					// 工作流ID
					orderRequest.workFlowId = rs.getInt("WorkflowId");
					// 优先级 0-9
					orderRequest.priority = rs.getInt("Priority");
					// 一级订单ID（L1）
					orderRequest.jobId_L1 = rs.getString("JobId_L1");
					// 用来存放订单类型：CP/FP/AP/VD
					orderRequest.orderType = rs.getString("OrderType");
					// 订单级别
					orderRequest.orderLevel = rs.getString("OrderLevel");
					// 真实性检验
					orderRequest.doValidation = rs.getString("DoValidation");
					// 用来记录生产的共性数据产品名称
					orderRequest.productName = rs.getString("productName");
					// 地理区域
					orderRequest.geoCoverageStr = rs
							.getString("geoCoverageStr"); // XML字符串
					// 开始时间
					orderRequest.startDate = rs.getDate("startDate");
					// 结束时间
					orderRequest.endDate = rs.getDate("endDate");
					// 任务单参数
					orderRequest.orderParameter = rs
							.getString("OrderPARAMETER");
					// 操作员
					orderRequest.operatorId = rs.getString("OPERATORID");
					// 生产确认内容
					orderRequest.confirmationMotion = rs
							.getBoolean("confirmationMotion");
					// 状态
					orderRequest.status = rs.getInt("status");
					// 与融合/同化相关联的共性产品信息
					orderRequest.retrievalDataList = new ArrayList<String>();
					String strRetrievalDataList = rs
							.getString("retrievalDataList");
					if ((null != strRetrievalDataList)
							&& (!strRetrievalDataList.equals(""))) {
						String[] strDataSplitArray = strRetrievalDataList
								.split(";");
						orderRequest.retrievalDataList.addAll(Arrays
								.asList(strDataSplitArray));
					}
					// 原始数据类型
					orderRequest.dataType = rs.getString("dataType");

					orderRequestList.add(orderRequest);
				}
			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {
			logger.error("订单请求查询失败！!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::getOrderRequest | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOrderRequest();
			}
			e.printStackTrace();
			orderRequestList.clear();
			return orderRequestList;
		}

		return orderRequestList;
	}

	// 获取未做数据解析的订单请求列表
	public synchronized ArrayList<OrderRequest> getUnDataStatusOrderRequest() {
		System.out
				.println("RequestDB::public ArrayList<OrderRequest> getUnDataStatusOrderRequest() | 获取未做数据解析的订单请求列表");
		ArrayList<OrderRequest> orderRequestList = new ArrayList<OrderRequest>();

		String strSql = "SELECT * FROM " + this.dbTable
				+ " WHERE status = 0 and dataStatus is null ORDER BY JobId ASC";

		try {
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderRequestList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			while (rs.next()) {
				OrderRequest orderRequest = new OrderRequest();
				// 订单ID（L2）
				orderRequest.jobId = rs.getString("JobId");
				// 工作流ID
				orderRequest.workFlowId = rs.getInt("WorkflowId");
				// 优先级 0-9
				orderRequest.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				orderRequest.jobId_L1 = rs.getString("JobId_L1");
				// 用来存放订单类型：CP/FP/AP/VD
				orderRequest.orderType = rs.getString("OrderType");
				// 订单级别
				orderRequest.orderLevel = rs.getString("OrderLevel");
				// 真实性检验
				orderRequest.doValidation = rs.getString("DoValidation");
				// 用来记录生产的共性数据产品名称
				orderRequest.productName = rs.getString("productName");
				// 地理区域
				orderRequest.geoCoverageStr = rs.getString("geoCoverageStr"); // XML字符串
				// 开始时间
				orderRequest.startDate = rs.getDate("startDate");
				// 结束时间
				orderRequest.endDate = rs.getDate("endDate");
				// 任务单参数
				orderRequest.orderParameter = rs.getString("OrderPARAMETER");
				// 操作员
				orderRequest.operatorId = rs.getString("OPERATORID");
				// 状态
				orderRequest.status = rs.getInt("status");
				// 与融合/同化相关联的共性产品信息
				orderRequest.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if ((null != strRetrievalDataList)
						&& (!strRetrievalDataList.equals(""))) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					orderRequest.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				orderRequest.dataType = rs.getString("dataType");

				orderRequestList.add(orderRequest);
			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {

			logger.error("未做数据解析的订单查询失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::getUnDataStatusOrderRequest | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getUnDataStatusOrderRequest();
			}
			e.printStackTrace();
			orderRequestList.clear();
			return orderRequestList;
		}

		return orderRequestList;
	}

	// 获取未生产确认的订单请求列表
	public synchronized ArrayList<OrderRequest> getUnFeasibilityOrderRequest() {
		System.out
				.println("RequestDB::public ArrayList<OrderRequest> getUnFeasibilityOrderRequest() | 获取未生产确认的订单请求列表");
		ArrayList<OrderRequest> orderRequestList = new ArrayList<OrderRequest>();
		String strSql = "SELECT * FROM "
				+ this.dbTable
				+ " WHERE status = 1 and confirmationStatus = -1 ORDER BY JobId ASC";
		try {
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderRequestList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			while (rs.next()) {
				OrderRequest orderRequest = new OrderRequest();
				// 订单ID（L2）
				orderRequest.jobId = rs.getString("JobId");
				// 工作流ID
				orderRequest.workFlowId = rs.getInt("WorkflowId");
				// 优先级 0-9
				orderRequest.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				orderRequest.jobId_L1 = rs.getString("JobId_L1");
				// 用来存放订单类型：CP/FP/AP/VD
				orderRequest.orderType = rs.getString("OrderType");
				// 订单级别
				orderRequest.orderLevel = rs.getString("OrderLevel");
				// 真实性检验
				orderRequest.doValidation = rs.getString("DoValidation");
				// 用来记录生产的共性数据产品名称
				orderRequest.productName = rs.getString("productName");
				// 地理区域
				orderRequest.geoCoverageStr = rs.getString("geoCoverageStr"); // XML字符串
				// 开始时间
				orderRequest.startDate = rs.getDate("startDate");
				// 结束时间
				orderRequest.endDate = rs.getDate("endDate");
				// 任务单参数
				orderRequest.orderParameter = rs.getString("OrderPARAMETER");
				// 操作员
				orderRequest.operatorId = rs.getString("OPERATORID");
				// 数据状态
				orderRequest.dataStatus = rs.getString("dataStatus");
				// 数据列表
				orderRequest.dataList = new ArrayList<String>();
				String strDataList = rs.getString("dataList");
				if ((null != strDataList) && (!strDataList.equals(""))) {
					String[] strDataSplitArray = strDataList.split(";");
					orderRequest.dataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 状态
				orderRequest.status = rs.getInt("status");
				// 与融合/同化相关联的共性产品信息
				orderRequest.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if ((null != strRetrievalDataList)
						&& (!strRetrievalDataList.equals(""))) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					orderRequest.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				orderRequest.dataType = rs.getString("dataType");

				orderRequestList.add(orderRequest);
			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {
			logger.error("未生产确认的订单请求查询失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::getUnFeasibilityOrderRequest | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getUnFeasibilityOrderRequest();
			}
			e.printStackTrace();
			orderRequestList.clear();
			return orderRequestList;
		}
		return orderRequestList;
	}

	// 获取生产确认后的订单请求列表
	public synchronized ArrayList<OrderRequest> getUnTransformOrderRequest() {
		System.out
				.println("RequestDB::public ArrayList<OrderRequest> getUnTransformOrderRequest() | 获取生产确认后的订单请求列表");
		ArrayList<OrderRequest> orderRequestList = new ArrayList<OrderRequest>();
		String strSql = "SELECT * FROM "
				+ this.dbTable
				+ " WHERE status = 2 and confirmationStatus = 1 ORDER BY JobId ASC";
		try {
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderRequestList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			while (rs.next()) {
				OrderRequest orderRequest = new OrderRequest();
				// 订单ID（L2）
				orderRequest.jobId = rs.getString("JobId");
				// 工作流ID
				orderRequest.workFlowId = rs.getInt("WorkflowId");
				// 优先级 0-9
				orderRequest.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				orderRequest.jobId_L1 = rs.getString("JobId_L1");
				// 用来存放订单类型：CP/FP/AP/VD
				orderRequest.orderType = rs.getString("OrderType");
				// 订单级别
				orderRequest.orderLevel = rs.getString("OrderLevel");
				// 真实性检验
				orderRequest.doValidation = rs.getString("DoValidation");
				// 用来记录生产的共性数据产品名称
				orderRequest.productName = rs.getString("productName");
				// 地理区域
				orderRequest.geoCoverageStr = rs.getString("geoCoverageStr"); // XML字符串
				// 开始时间
				orderRequest.startDate = rs.getDate("startDate");
				// 结束时间
				orderRequest.endDate = rs.getDate("endDate");
				// 数据状态
				orderRequest.dataStatus = rs.getString("dataStatus");
				// 数据列表
				orderRequest.dataList = new ArrayList<String>();
				String strDataList = rs.getString("dataList");
				if ((null != strDataList) && (!strDataList.equals(""))) {
					String[] strDataSplitArray = strDataList.split(";");
					orderRequest.dataList.addAll(Arrays
							.asList(strDataSplitArray));
				}/*
				 * else{ orderRequest_curr.dataList = new ArrayList< String >();
				 * }
				 */
				// 任务单参数
				orderRequest.orderParameter = rs.getString("OrderPARAMETER");
				// 操作员
				orderRequest.operatorId = rs.getString("OPERATORID");
				// 生产确认内容
				orderRequest.confirmationMotion = rs
						.getBoolean("confirmationMotion");
				// 状态
				orderRequest.status = rs.getInt("status");
				// 与融合/同化相关联的共性产品信息
				orderRequest.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if ((null != strRetrievalDataList)
						&& (!strRetrievalDataList.equals(""))) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					orderRequest.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				orderRequest.dataType = rs.getString("dataType");

				orderRequestList.add(orderRequest);
			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {
			logger.error("获取生产确认后的订单执行失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::getUnTransformOrderRequest | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getUnTransformOrderRequest();
			}
			e.printStackTrace();
			orderRequestList.clear();
			return orderRequestList;
		}

		return orderRequestList;
	}

	// 生成订单序号
	public synchronized String generateId(String orderType) {
		System.out
				.println("RequestDB::public String generateId( String orderType ) | 生成订单序号");
		String strId = null;
		try {
			if ((null == orderType) || (orderType.isEmpty())) {
				return strId;
			}

			String strSql = "SELECT JobId FROM "
					+ this.dbTable
					+ " WHERE OrderType LIKE ? and JobId LIKE ? ORDER BY JobId ASC";

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return strId;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			format.setLenient(false);
			java.util.Date date = new Date(System.currentTimeMillis());
			String strDate = format.format(date);
			pstmt.setString(1, orderType + "%");
			String strJobId = "____" + strDate + "___________";
			pstmt.setString(2, strJobId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.last()) {
				strId = rs.getString("JobId");
				Integer iId = Integer.valueOf(strId.substring(12, 16)) + 1;
				strId = String.format("%04d", iId);
			} else {
				strId = "0001";
			}
			
			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("生成请求订单序号错误!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return generateId(orderType);
			}
			e.printStackTrace();
			return null;
		}

		return strId;
	}

	// 设置订单请求列表
	public synchronized boolean setOrderRequest(
			ArrayList<OrderRequest> requestList) {
		System.out
				.println("RequestDB::public boolean setOrderRequest( ArrayList<OrderRequest> requestList ) | 设置订单请求列表");

		try {
			Iterator<OrderRequest> orderRequest_curr = requestList.iterator();
			while (orderRequest_curr.hasNext()) {
				String strSql = "INSERT INTO "
						+ this.dbTable
						+ "(JobId,WorkFlowId,Priority,JobId_L1,OrderType,OrderLevel,DoValidation,"
						+ "productName,geoCoverageStr,startDate,endDate,status,dataType,submitDate)"
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

				if (null == conn) {
					return false;
				}
				PreparedStatement pstmt = conn.prepareStatement(strSql);

				OrderRequest orderRequest = orderRequest_curr.next();
				// orderRequest_curr = requestList.get(0);

				int iItemIndex = 0;
				// 订单ID（L2）
				pstmt.setString(++iItemIndex, orderRequest.jobId);
				// 工作流ID
				pstmt.setInt(++iItemIndex, orderRequest.workFlowId);
				// 优先级 0-9
				pstmt.setInt(++iItemIndex, orderRequest.priority);
				// 一级订单ID（L1）
				pstmt.setString(++iItemIndex, orderRequest.jobId_L1);
				// 用来存放订单类型：CP/FP/AP/VD
				pstmt.setString(++iItemIndex, orderRequest.orderType);
				// 订单级别
				pstmt.setString(++iItemIndex, orderRequest.orderLevel);
				// 真实性检验
				pstmt.setString(++iItemIndex, orderRequest.doValidation);
				// 用来记录生产的共性数据产品名称
				pstmt.setString(++iItemIndex, orderRequest.productName);
				// 地理区域
				pstmt.setString(++iItemIndex, orderRequest.geoCoverageStr);
				// 开始时间
				pstmt.setDate(++iItemIndex, orderRequest.startDate);
				// 结束时间
				pstmt.setDate(++iItemIndex, orderRequest.endDate);
				// 状态
				pstmt.setInt(++iItemIndex, orderRequest.getStatus());
				// 原始数据类型
				pstmt.setString(++iItemIndex, orderRequest.dataType);

				// 提交时间
				
				if (null==orderRequest.submitDate) {
					pstmt.setTimestamp(++iItemIndex, new Timestamp(
							System.currentTimeMillis()));
					
				}else {
						pstmt.setTimestamp(++iItemIndex, new Timestamp(
						orderRequest.submitDate.getTime()));

				}
				
			
				// @@@ 如果要考虑通用性，则这里需要把所有属性都添加上，比如数据列表、共性产品列表

				// 执行插入
				if (0 == pstmt.executeUpdate()) {
					logger.error("设置订单请求列表:更新行数为0！");
				}

				// 关闭相关连接
				pstmt.close();

				// requestList.remove( 0 );
			}
		} catch (SQLException e) {
			logger.error("设置订单请求列表执行失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::setOrderRequest | SQLException | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			e.printStackTrace();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderRequest(requestList);
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 更新订单生产确认状态
	public synchronized boolean setConfirmationStatus(String orderRequestId,
			int confirmationStatus, boolean confirmationMotion) {
		System.out
				.println("RequestDB::public boolean setConfirmationStatus( String orderId, int confirmationStatus, boolean confirmationMotion ) | 更新订单生产确认状态");

		String strSql = "UPDATE "
				+ this.dbTable
				+ " SET confirmationStatus = ?, confirmationMotion = ? WHERE JobId = ?";
		try {
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 生产确认状态
			pstmt.setInt(1, confirmationStatus);
			// 生产确认内容
			pstmt.setBoolean(2, confirmationMotion);
			// 生产请求订单ID
			pstmt.setString(3, orderRequestId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新订单生产确认状态失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::setConfirmationStatus | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setConfirmationStatus(orderRequestId,
						confirmationStatus, confirmationMotion);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 更新订单数据状态
	public synchronized boolean setDataStatus(String orderId,
			String dataStatus, ArrayList<String> dataList) {
		System.out
				.println("RequestDB::public boolean setDataStatus( String orderId, String dataStatus, String dataList ) | 更新订单数据状态");

		String strSql = "UPDATE " + this.dbTable
				+ " SET dataStatus = ?, dataList = ? WHERE JobId = ?";
		try {
			

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 数据状态
			pstmt.setString(1, dataStatus);
			// 数据列表
			// pstmt.setString( 2, dataList );
			if (dataList.size() > 0) {
				String[] strDataList = new String[dataList.size()];
				dataList.toArray(strDataList);
				//System.out.println(String.join(";", strDataList));
				pstmt.setString(2, String.join(";", strDataList));
			} else {
				// 如果没有获取到数据列表就赋值为空字符串
				pstmt.setString(2, "");
			}
			// 生产请求订单ID
			pstmt.setString(3, orderId);
			
			//test
			//System.out.println(orderId+" "+dataStatus);

			// 执行更新
			int result = pstmt.executeUpdate();
			if (result == 0) {
				logger.error("更新订单数据状态:更新行数为0！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新订单数据状态执行失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::setDataStatus | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataStatus(orderId, dataStatus, dataList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 更新生产请求状态
	public synchronized boolean setStatus(String orderId, int status) {
		System.out
				.println("RequestDB::public synchronized boolean setStatus( String orderId, int status ) | 更新生产请求状态");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET status = ? WHERE JobId = ?";

			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 数据状态
			pstmt.setInt(1, status);
			// 生产请求ID
			pstmt.setString(2, orderId);

			// 执行更新
			if (0 == pstmt.executeUpdate()) {
				logger.error("更新生产请求状态:行数更新为0！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::setStatus | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setStatus(orderId, status);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			logger.error("更新生产请求状态失败!");
			logger.error("SQL执行异常", e);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 设置与融合/同化订单相关联的共性产品信息
	public synchronized boolean setRetrievalDataList(String orderId,
			ArrayList<String> dataList) {
		System.out
				.println("RequestDB::public synchronized boolean setRetrievalDataList( String orderId, ArrayList< String > dataList ) | 设置与融合/同化订单相关联的共性产品信息");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET retrievalDataList = ? WHERE JobId = ?";

			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 共性产品列表
			String[] strRetrievalDataList = new String[dataList.size()];
			dataList.toArray(strRetrievalDataList);
			pstmt.setString(1, String.join(";", strRetrievalDataList));
			// 生产请求ID
			pstmt.setString(2, orderId);

			// 执行更新
			if (0 == pstmt.executeUpdate()) {
				logger.error("设置与融合/同化订单相关联的共性产品信息:行数更新为0！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {

			logger.error("设置与融合/同化订单相关联的共性产品信息失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::setRetrievalDataList | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setRetrievalDataList(orderId, dataList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 设置原始数据类型
	public synchronized boolean setDataType(String orderId, String dataType) {
		System.out
				.println("RequestDB::public synchronized boolean setDataType( String orderId, String dataType ) | 设置原始数据类型");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataType = ? WHERE JobId = ?";

			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 原始数据类型
			pstmt.setString(1, dataType);
			// 生产请求ID
			pstmt.setString(2, orderId);

			// 执行更新
			if (0 == pstmt.executeUpdate()) {
				logger.error("设置原始数据类型:行数更新为0！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("设置原始数据类型失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::setDataType | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataType(orderId, dataType);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 更新订单信息
	public synchronized boolean updateOrder(OrderRequest orderRequest) {
		System.out
				.println("RequestDB::public synchronized boolean updateOrder( OrderRequest orderRequest ) | 更新订单信息");

		try {
			String strSql = "UPDATE "
					+ this.dbTable
					+ " SET WorkFlowId = ?, Priority = ?, JobId_L1 = ?, OrderType = ?, "
					+ "OrderLevel = ?, DoValidation = ?, productName = ?, geoCoverageStr = ?, startDate = ?, endDate = ?, "
					+ "status = ?, dataType = ?, OrderPARAMETER=?  WHERE JobId = ?";

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			int iItemIndex = 0;
			// 工作流ID
			pstmt.setInt(++iItemIndex, orderRequest.workFlowId);
			// 优先级 0-9
			pstmt.setInt(++iItemIndex, orderRequest.priority);
			// 一级订单ID（L1）
			pstmt.setString(++iItemIndex, orderRequest.jobId_L1);
			// 用来存放订单类型：CP/FP/AP/VD
			pstmt.setString(++iItemIndex, orderRequest.orderType);
			// 订单级别
			pstmt.setString(++iItemIndex, orderRequest.orderLevel);
			// 真实性检验
			pstmt.setString(++iItemIndex, orderRequest.doValidation);
			// 用来记录生产的共性数据产品名称
			pstmt.setString(++iItemIndex, orderRequest.productName);
			// 地理区域
			pstmt.setString(++iItemIndex, orderRequest.geoCoverageStr);
			// 开始时间
			pstmt.setDate(++iItemIndex, orderRequest.startDate);
			// 结束时间
			pstmt.setDate(++iItemIndex, orderRequest.endDate);
			// 状态
			pstmt.setInt(++iItemIndex, orderRequest.getStatus());
			// 原始数据类型
			pstmt.setString(++iItemIndex, orderRequest.dataType);
			// @@@ 如果要考虑通用性，则这里需要把所有属性都添加上，比如数据列表、共性产品列表
			
			if (orderRequest.orderParameter!=null) {
				pstmt.setString(++iItemIndex, orderRequest.orderParameter);
			}
			else {
				pstmt.setString(++iItemIndex, "");
			}
			

			// 生产请求订单ID
			pstmt.setString(++iItemIndex, orderRequest.jobId);

			// 执行更新
			if (0 == pstmt.executeUpdate()) {
				logger.error("更新订单信息:行数更新为0！");
			}
			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新订单信息执行失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::setConfirmationStatus | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return updateOrder(orderRequest);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 删除订单请求列表
	public synchronized boolean deleteOrderRequest(
			ArrayList<OrderRequest> requestList) {
		System.out
				.println("RequestDB::public boolean deleteOrderRequest( ArrayList< OrderRequest > requestList ) | 删除订单请求列表");
		try {
			Iterator<OrderRequest> orderRequest_curr = requestList.iterator();
			while (orderRequest_curr.hasNext()) {
				String strSql = "DELETE FROM " + this.dbTable
						+ " WHERE JobId = ?";
				if (null == conn) {
					logger.error("SQL执行连接初始化失败！");
					return false;
				}
				PreparedStatement pstmt = conn.prepareStatement(strSql);
				pstmt.setString(1, orderRequest_curr.next().jobId);

				if (0 == pstmt.executeUpdate()) {
					logger.error("删除订单请求列表:行数更新为0！");
				}

				// 关闭相关连接
				pstmt.close();
			}
		} catch (SQLException e) {
			logger.error("删除订单请求列表失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>RequestDB::deleteOrderRequest | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return deleteOrderRequest(requestList);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
