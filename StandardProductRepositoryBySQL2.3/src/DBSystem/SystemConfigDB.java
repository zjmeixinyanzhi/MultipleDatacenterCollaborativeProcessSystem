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

/**
 * @author caoyang
 * 
 */
public class SystemConfigDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;

	public SystemConfigDB() {
		System.out.println("SystemConfigDB::public SystemConfigDB() | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>SystemConfigDB::public RequestDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "systemconfigdb";
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

	// 获取配置文件
	public synchronized String getOuterLog4jConfigFile() {
		String configFile = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable;
			System.out.println(strSql);
			if (null == conn) {
				return configFile;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				// 配置文件路径
				configFile = rs.getString("outerLog4jConfigFile");
			}
			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOuterLog4jConfigFile();
			}
			e.printStackTrace();
			return configFile;
		}

		return configFile;
	}

	// \u83b7\u53d6\u914d\u7f6e\u6587\u4ef6
	public synchronized String getHostName() {
		String configFile = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable;
			System.out.println(strSql);
			if (null == conn) {
				return configFile;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				// \u914d\u7f6e\u6587\u4ef6\u8def\u5f84
				configFile = rs.getString("ip");
			}
			// \u5173\u95ed\u76f8\u5173\u8fde\u63a5
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOuterLog4jConfigFile();
			}
			e.printStackTrace();
			return configFile;
		}

		return configFile;
	}

}
