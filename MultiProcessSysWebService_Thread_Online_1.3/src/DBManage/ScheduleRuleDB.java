/*
 *程序名称 		: ScheduleRuleDB.java
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
package DBManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import SystemManage.DBConfig;
import SystemManage.SystemLogger;
import TaskSchedular.ScheduleRule;

/**
 * @author caoyang
 * 
 */
public class ScheduleRuleDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// 构造函数
	public ScheduleRuleDB() {
		System.out.println("ScheduleRuleDB::public ScheduleRuleDB () | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>ScheduleRuleDB::public ScheduleRuleDB () | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "scheduleruledb";
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

	// 获取调度规则列表
	public synchronized ArrayList<ScheduleRule> getScheduleRuleList() {
		System.out
				.println("ScheduleRuleDB::public ArrayList< ScheduleRule > getScheduleRuleList() | 获取调度规则列表");

		ArrayList<ScheduleRule> scheduleRuleList = new ArrayList<ScheduleRule>();

		ScheduleRule scheduleRule = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable;
			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return scheduleRuleList;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				logger.error(" 获取调度规则列表：查询结果为0！");
			} else {
				rs.previous();

				while (rs.next()) {
					scheduleRule = new ScheduleRule();
					// 规则ID
					scheduleRule.ruleId = rs.getString("RuleId");
					// 规则条件
					scheduleRule.condition = rs.getString("Condition");
					// 约束的变量
					scheduleRule.argName = rs.getString("ArgName");
					// 变量比较值
					scheduleRule.value = rs.getString("Value");

					scheduleRuleList.add(scheduleRule);
				}
			}

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getScheduleRuleList();
			}
			e.printStackTrace();
			scheduleRuleList.clear();
			return scheduleRuleList;
		}

		return scheduleRuleList;
	}

}
