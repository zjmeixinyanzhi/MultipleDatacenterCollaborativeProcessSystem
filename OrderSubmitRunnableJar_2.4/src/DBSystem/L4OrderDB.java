package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import LogSystem.SystemLogger;
import OrderManage.L3InternalOrder;
import OrderManage.L4InternalOrder;

/**
 * 创建时间：2015-7-14 上午9:58:49 项目名称：MultiProcessSysWebService_Thread 2015-7-14
 * 
 * @author 张杰
 * @version 1.0 文件名称：L4OrderDB.java 类说明：四级订单库操作类
 */
public class L4OrderDB extends DBConn {
	private static Connection conn = null;
	private String dbTable;
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	public L4OrderDB() {
		System.out.println("L4OrderDB::public L4OrderDB() | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>L4OrderDB::public L4OrderDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "l4orderdb";
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

	// 条件属性查询
	public synchronized ArrayList<L4InternalOrder> search(String condition) {
		logger.info("L4OrderDB::public ArrayList< L4InternalOrder > search ( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<L4InternalOrder> orderList = new ArrayList<L4InternalOrder>();
		L4InternalOrder l4Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;
			// Test
			 System.out.println(strSql);
			if (null == conn) {
				System.out.println("系统退出！");
				return orderList;
				// conn=getConnection();
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			while (rs.next()) {
				l4Order = new L4InternalOrder();
				// 订单ID（L4）
				l4Order.jobId = rs.getString("JobId");
				// 优先级 0-9
				l4Order.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				l4Order.jobId_L1 = rs.getString("JobId_L1");
				// 二级订单ID（L2）
				l4Order.jobId_L2 = rs.getString("JobId_L2");
				// 三级订单ID（L3）
				l4Order.jobId_L3 = rs.getString("JobId_L3");
				// 数据所在数据中心IP
				l4Order.DataCenterIP = rs.getString("DataCenterIP");
				// 用来存放订单类型：CP/FP/AP/VD
				l4Order.orderType = rs.getString("OrderType");
				// 订单级别
				l4Order.orderLevel = rs.getString("OrderLevel");
				// 订单状态：Start/Running/Finish/Error/Suspend
				l4Order.workingStatus = rs.getString("workingStatus");
				// 订单的生产状态：RadioNorm /GeoNorm/ CommonProduct/Fusion/Assimilation
				l4Order.orderStatus = rs.getString("orderStatus");				
				// 订单的数据状态：Available/NotAvailable/Future
				l4Order.dataStatus = rs.getString("dataStatus");
				// 用来记录订单的数据列表
				l4Order.dataList = new ArrayList<String>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					String[] strDataSplitArray = strDataList.split(";");
					l4Order.dataList.addAll(Arrays.asList(strDataSplitArray));
				}
				// 操作员
				l4Order.operatorId = rs.getString("OPERATORID");
				// 算法资源名称
				l4Order.algorithmName = rs.getString("AlgorithmName");
				// 算法资源程序
				l4Order.algorithmPath = rs.getString("AlgorithmPath");
				// 产品列表
				String strDataProduct = rs.getString("dataProductList");
				if (null == strDataProduct) {
					l4Order.strDataProductList = new ArrayList<String>();
				} else if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					l4Order.strDataProductList.addAll(Arrays
							.asList(strDataSplitArray));
				} else {
					l4Order.strDataProductList = new ArrayList<String>();
				}
				//test
				System.out.println(l4Order.strDataProductList.size());
				
				// 算法资源ID
				l4Order.algorithmID = rs.getInt("AlgorithmID");
				// 与融合/同化相关联的共性产品信息
				l4Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if (null == strRetrievalDataList) {
					l4Order.retrievalDataList = new ArrayList<String>();
				} else if (!strRetrievalDataList.equals("")) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l4Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				l4Order.dataType = rs.getString("dataType");

				orderList.add(l4Order);
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
			logger.error("四级订单条件属性查询失败！\n"+e);
			orderList.clear();
			return orderList;
		}
		return orderList;
	}


	// 设置四级订单状态
	public synchronized boolean setOrderWorkingStatus(String l4OrderId,
			String l4OrderStatus) {
		logger.info("L4OrderDB::public boolean setOrderWorkingStatus( String l4OrderId, String l4OrderStatus ) | 设置级订单状态");

		switch (l4OrderStatus) {
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
			pstmt.setString(1, l4OrderStatus);
			// 三级三级订单ID
			pstmt.setString(2, l4OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkingStatus(l4OrderId, l4OrderStatus);
			}
			e.printStackTrace();
			logger.error("设置四级订单状态失败！\n"+e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置四级订单状态失败！\n"+e);
			return false;
		}

		return true;
	}
	
	// 更新四级订单数据产品列表
	public synchronized boolean setDataProductList(String l4OrderId,
			ArrayList<String> strDataProductList) {

		System.out
				.println("L4OrderDB::public boolean setDataProductList( String l4OrderId, ArrayList< String > strDataProductList ) | 设置订单产品列表");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataProductList = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 产品列表
			if (strDataProductList.size() > 0) {
				String[] strList = new String[strDataProductList.size()];
				strDataProductList.toArray(strList);
				pstmt.setString(1, String.join(";", strList));
			} else {
				// 如果没有获取到数据列表就赋值为空字符串
				pstmt.setString(1, "");
			}
			// 订单ID（L3）
			pstmt.setString(2, l4OrderId);

			// 执行更新
			if (pstmt.executeUpdate()==0) {
				logger.error("更新四级订单数据产品列表,行数更新为0！");
			}
			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新四级订单数据产品列表失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataProductList(l4OrderId, strDataProductList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
