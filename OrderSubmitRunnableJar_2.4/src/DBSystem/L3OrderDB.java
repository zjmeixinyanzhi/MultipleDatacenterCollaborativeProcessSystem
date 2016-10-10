/*
 *程序名称 		: L3OrderDB.java
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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import LogSystem.SystemLogger;
import OrderManage.L3InternalOrder;

/**
 * @author caoyang
 * 
 */
public class L3OrderDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	/**
	 * 
	 */
	public L3OrderDB() {
		System.out.println("L3OrderDB::public RequestDB() | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>L3OrderDB::public RequestDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "l3orderdb";
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

	// 获取三级订单
	public synchronized L3InternalOrder getOrder(String strOrderId) {
		logger.info("L3OrderDB::public L3InternalOrder getOrder( String strOrderId ) | 获取三级订单");

		L3InternalOrder l3Order = null;
		String strQuery = "WHERE JobId = '" + strOrderId + "'";
		ArrayList<L3InternalOrder> orderList = this.search(strQuery);
		if (!orderList.isEmpty()) {
			l3Order = orderList.get(0);
		}
		
		return l3Order;
	}

	// 向订单库查询订单
	// 参数1：condition 其值类似于“JobId = ?”
	public synchronized ArrayList<L3InternalOrder> search(String condition) {
		logger.info("L3OrderDB::public ArrayList< L3InternalOrder > search ( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<L3InternalOrder> orderList = new ArrayList<L3InternalOrder>();
		L3InternalOrder l3Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;
			// Test
			 System.out.println(strSql);
			if (null == conn) {
				return orderList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			while (rs.next()) {
				l3Order = new L3InternalOrder();
				// 订单ID（L3）
				l3Order.jobId = rs.getString("JobId");
				// 工作流ID
				l3Order.workFlowId = rs.getInt("WorkFlowId");
				// 优先级 0-9
				l3Order.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				l3Order.jobId_L1 = rs.getString("JobId_L1");
				// 二级订单ID（L2）
				l3Order.jobId_L2 = rs.getString("JobId_L2");
				// 用来存放订单类型：CP/FP/AP/VD
				l3Order.orderType = rs.getString("OrderType");
				// 订单级别
				l3Order.orderLevel = rs.getString("OrderLevel");
				// 真实性检验
				l3Order.doValidation = rs.getString("DoValidation");
				// 用来记录生产的共性数据产品名称
				l3Order.productName = rs.getString("productName");
				// 地理区域
				l3Order.geoCoverageStr = rs.getString("geoCoverageStr");
				// 开始时间
				l3Order.startDate = rs.getDate("startDate");
				// 结束时间
				l3Order.endDate = rs.getDate("endDate");
				// 订单状态：Start/Running/Finish/Error/Suspend
				l3Order.workingStatus = rs.getString("workingStatus");
				// 订单的生产状态：RadioNorm /GeoNorm/ CommonProduct/Fusion/Assimilation
				l3Order.orderStatus = rs.getString("orderStatus");
				// 订单的数据状态：Available/NotAvailable/Future
				l3Order.dataStatus = rs.getString("dataStatus");
				// 用来记录订单的数据列表
				l3Order.dataList = new ArrayList<String>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					String[] strDataSplitArray = strDataList.split(";");
					l3Order.dataList.addAll(Arrays.asList(strDataSplitArray));
				}
				// 用来记录提交时间
				l3Order.submitDate = rs.getDate("SubmitDate");
				// 用来记录完成时间
				l3Order.finishDate = rs.getDate("FinishDate");
				// 任务单参数
				l3Order.orderParameter = rs.getString("OrderPARAMETER");
				// 操作员
				l3Order.operatorId = rs.getString("OPERATORID");
				// 算法资源名称
				l3Order.algorithmName = rs.getString("AlgorithmName");
				// 算法资源程序
				l3Order.algorithmPath = rs.getString("AlgorithmPath");
				// 产品列表
				String strDataProduct = rs.getString("dataProductList");
				if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					l3Order.strDataProductList.addAll(Arrays
							.asList(strDataSplitArray));
				} else {
					l3Order.strDataProductList = new ArrayList<String>();
				}
				// 算法资源ID
				l3Order.algorithmID = rs.getInt("AlgorithmID");
				// 与融合/同化相关联的共性产品信息
				l3Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if (!strRetrievalDataList.equals("")) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l3Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				l3Order.dataType = rs.getString("dataType");

				orderList.add(l3Order);
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(condition);
			}
			e.printStackTrace();
			logger.error("查询三级订单失败！" + e);
			orderList.clear();
			return orderList;
		}
		return orderList;
	}

	// 设置三级订单状态
	public synchronized boolean setOrderWorkingStatus(String l3OrderId,
			String l3OrderStatus) {
		logger.info("L3OrderDB::public boolean setOrderWorkingStatus( String l3OrderId, String l3OrderStatus ) | 设置三级订单状态");

		switch (l3OrderStatus) {
		case "Ready":
			break;
		case "Finish":
			break;
		case "Error":
			break;
		default:
			return false;
		}

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET workingStatus = ? WHERE JobId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 三级订单状态
			pstmt.setString(1, l3OrderStatus);
			// 三级三级订单ID
			pstmt.setString(2, l3OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkingStatus(l3OrderId, l3OrderStatus);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置三级订单状态错误！" + e);
			return false;
		}

		return true;
	}

	// 获取数据列表
	public synchronized String getDataList(String l3OrderId) {
		logger.info("L3OrderDB::public String getDataList( String l3OrderId ) | 获取三级订单数据列表");

		String strDataList = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable
					+ " WHERE JobId = ?";
			if (null == conn) {
				return strDataList;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			pstmt.setString(1, l3OrderId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				// 三级订单状态：Ready/Finish/Error
				strDataList = rs.getString("datalist");
			}

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getDataList(l3OrderId);
			}
			e.printStackTrace();
			logger.error("获取三级订单数据列表错误！" + e);
			return "";
		}

		return strDataList;
	}

	// 设置数据状态
	public synchronized boolean setDataStatus(String l3OrderId,
			String l3DataStatus) {
		System.out
				.println("L3OrderDB::public boolean setDataStatus( String l3OrderId, String l3OrderStatus ) | 设置三级订单数据状态");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataStatus = ? WHERE JobId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 三级订单状态
			pstmt.setString(1, l3DataStatus);
			// 三级三级订单ID
			pstmt.setString(2, l3OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataStatus(l3OrderId, l3DataStatus);
			}
			e.printStackTrace();
			logger.error("设置数据状态失败！" + e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置数据状态失败！" + e);
			return false;
		}

		return true;
	}

	// 设置三级订单产品列表
	public synchronized boolean setDataList(String l3OrderId, String strDataList) {
		logger.info("L3OrderDB::public boolean setDataProductList( String l3OrderId, ArrayList< String > strDataList ) | 设置三级订单数据准备列表");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET DataList = ? WHERE JobId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 数据列表
			pstmt.setString(1, strDataList);
			// 三级订单ID（L3）
			pstmt.setString(2, l3OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataList(l3OrderId, strDataList);
			}
			e.printStackTrace();
			logger.info("设置三级订单产品列表失败！");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("设置三级订单产品列表失败！" + e);
			return false;
		}

		return true;
	}

	// 设置三级订单产品列表
	public synchronized boolean setDataProductListByL4OrderProductList(
			String l3OrderId, ArrayList<String> strDataList) {
		logger.info("L3OrderDB::public boolean setDataProductListByL4OrderProductList( String l3OrderId, ArrayList< String > strDataList ) | 设置三级订单数据准备列表");
		if (null == strDataList) {
			return false;
		}

		// 查询原来订单产品列表
		String dataProductList = "";
		ArrayList<String> DataList = getOrder(l3OrderId).strDataProductList;

		for (String string : DataList) {
			dataProductList += string;
			dataProductList += ";";
		}
		// 追加新数据
		for (String string : strDataList) {
			dataProductList += string;
			dataProductList += ";";
		}

		// test
		// System.out.println(">>" + dataProductList);

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataProductList = ? WHERE JobId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 数据列表
			pstmt.setString(1, dataProductList);
			// 三级订单ID（L3）
			pstmt.setString(2, l3OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataProductListByL4OrderProductList(l3OrderId,
						strDataList);
			}
			e.printStackTrace();
			logger.error("设置三级订单产品列表失败！\n"+e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置三级订单产品列表失败！\n"+e);
			return false;
		}

		return true;
	}

}
