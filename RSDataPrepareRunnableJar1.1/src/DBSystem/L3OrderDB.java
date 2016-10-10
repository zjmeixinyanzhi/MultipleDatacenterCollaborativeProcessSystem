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

/**
 * @author caoyang
 * 
 */
public class L3OrderDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	private Logger logger=SystemLogger.getInstance().getSysLogger();

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
				logger.error("<Error>L3OrderDB::public RequestDB() | conn = getConnection() | conn == null");
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
			logger.error("设置三级订单状态错误："+e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置三级订单状态错误："+e);
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
			logger.error("获取三级订单数据列表错误："+e);
			return "";
		}

		return strDataList;
	}

	// 设置数据状态
	public synchronized boolean setDataStatus(String l3OrderId,
			String l3DataStatus) {
		logger.info("L3OrderDB::public boolean setDataStatus( String l3OrderId, String l3OrderStatus ) | 设置三级订单数据状态");

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
			logger.error("设置三级订单数据状态错误："+e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置三级订单数据状态错误："+e);
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
			logger.error("设置三级订单数据列表错误："+e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置三级订单数据列表错误："+e);
			return false;
		}

		return true;
	}

}
