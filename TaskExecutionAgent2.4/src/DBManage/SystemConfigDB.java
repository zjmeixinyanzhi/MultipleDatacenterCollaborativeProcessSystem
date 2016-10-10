/*
 *程序名称 		: SystemConfigDB.java
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
import java.sql.DriverManager;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import TaskExeAgent.DBConfig;
import TaskExeAgent.ServerConfig;
import TaskExeAgent.SystemConfig;
import TaskExeAgent.SystemLogger;

/**
 * @author caoyang
 * 
 */
public class SystemConfigDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;

	// 数据产品FTP配置信息
	private String dataProductFtpConfig;
	// 数据中心配置信息
	private Hashtable<String, ServerConfig> dataCenterConfigList;
	// 生产请求订单数据库配置信息
	private DBConfig requestDBConfig;
	// 订单数据库配置信息
	private DBConfig orderDBConfig;
	// 工作流库（处理流程库）配置信息
	private DBConfig workflowDBConfig;
	// 算法库配置信息
	private DBConfig algorithmDBCongfig;
	// 调度规则库配置信息
	private DBConfig scheduleRuleDBConfig;
	// 系统资源库配置信息
	private DBConfig systemResourceDBConfig;
	// 消息类型与webservice接口关系列表配置信息
	private Hashtable<String, String> msg2APIConfigList;
	// 系统服务
	private DBConfig daemonServerConfig;
	// 系统日志文件存放路径：log4j日志文件有多个,按照大小来进行划分
	private String logFilePath;
	// 系统数据缓存区定时清理时间的信息
	private Date publicBufferCleanupDates;
	// 系统数据缓存区最近一次清理时间
	private Date lastCleanupDate;
	// 用来记录数据缓存区的挂载点
	private String bufferMountPoint;
	// 系统数据缓存区的最大数据使用率
	private float publicBufferMaxUsage;
	// 系统的daemon轮询时间频率
	private float daemonSleepTimeInMillisecond;

	// 构造函数
	public SystemConfigDB(DBConfig config) {
		super(config);
		System.out.println("SystemConfigDB::public SystemConfigDB () | 构造函数");

		// ###//初始化配置数据库的连接信息
		// ###this.configuartionDBConfig = sysConfigDBInfo;
		// ###//初始化连接数据库
		// ###Connection conn = getConnection();
		// ###if( conn != null ){
		// ### connectFlag = true;
		// ###}else{
		// ### connectFlag = false;
		// ###}
		// ###//从配置表读取配置信息
		// ###this.sysConfig = ;
		// ###//…
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>SystemConfigDB::public SystemConfigDB () | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		if (null == config) {
			System.out.println("<Error>Config file is not found.");
			this.dbTable = "systemconfigdb";
			// return;
		} else {
			this.dbTable = config.getDBTable(); // systemconfigdb
		}

		try {
			String strSql = "SELECT * FROM " + this.dbTable;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			while (rs.next()) {
				// 数据产品共享FTP地址
				this.dataProductFtpConfig = rs
						.getString("dataProductFtpConfig");
				// 数据中心配置
				String strDataCenterConfig = rs
						.getString("dataCenterConfigList");
				// this.dataCenterConfigList;
				// 生产请求数据库配置
				String strRequestDBConfig = rs.getString("requestDBConfig");
				// this.requestDBConfig;
				// 订单数据库配置
				String strOrderDBConfig = rs.getString("orderDBConfig");
				// this.orderDBConfig;
				// 处理流程库配置
				String strWorkflowDBConfig = rs.getString("workflowDBConfig");
				// this.workflowDBConfig;
				// 算法资源库配置
				String strAlgorithmDBConfig = rs.getString("algorithmDBConfig");
				// this.algorithmDBCongfig;
				// 调度规则库配置
				String strScheduleRuleDBConfig = rs
						.getString("scheduleRuleDBConfig");
				// this.scheduleRuleDBConfig;
				// 系统资源库配置
				String strSystemResourceDBConfig = rs
						.getString("SystemResourceDBConfig");
				// this.systemResourceDBConfig;
				// 消息与web接口函数映射关系
				String strMsg2APIConfig = rs.getString("msg2APIConfig");
				// this.msg2APIConfigList;
				// 系统服务
				String strDaemonServerConfig = rs
						.getString("daemonServerConfig");
				// this.daemonServerConfig;
				// 系统日志
				this.logFilePath = rs.getString("logFilesPath");
				// 缓存区清理时间
				this.publicBufferCleanupDates = rs
						.getDate("publicBufferCleanupDates");
				// 上一次清理时间
				// @@@
				this.lastCleanupDate = rs.getDate("LastCleanupDate");
				// 缓存区挂载点
				this.bufferMountPoint = rs.getString("bufferMountPoint");
				// 缓存区最大使用率
				String strPublicBufferMaxUsage = rs
						.getString("publicBufferMaxUsage");
				this.publicBufferMaxUsage = Float
						.valueOf(strPublicBufferMaxUsage);
				// Daemon轮询时间（单位ms）
				String strDaemonSleepTimeInMillisecond = rs
						.getString("daemonSleepTimeInMillisecond");
				this.daemonSleepTimeInMillisecond = Float
						.valueOf(strDaemonSleepTimeInMillisecond);

			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				return;
			}
			e.printStackTrace();
		}
	}

	protected void finalize() {
		// try{
		// //conn.commit();
		// conn.close();
		// } catch( SQLException e ){
		// String strSqlState = e.getSQLState();
		// if( strSqlState.equals( "08S01" ) ){
		// return;
		// }
		// e.printStackTrace();
		// }
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

	// 从数据库载入系统配置信息
	// !!!public String loadConfig( String configType ){
	// !!! System.out.println(
	// "SystemConfigDB::boolean isConnected() | 判断是否连接数据库成功" );
	// !!! //从数据库载入相应的信息
	// !!! return "";
	// !!!}

	// 获取数据产品FTP配置信息
	public String getFTP() {
		System.out
				.println("SystemConfigDB::public String getFTP() | 获取数据产品FTP配置信息");

		return this.dataProductFtpConfig;
	}

	// 获取数据中心配置信息
	public Hashtable<String, ServerConfig> getDataCenter() {
		System.out
				.println("SystemConfigDB::public Hashtable< String, ServerConfig > getDataCenter() | 获取数据中心配置信息");
		// ##Hashtable< String, ServerConfig > temp = null;
		// ##return temp;
		return this.dataCenterConfigList;
	}

	// 获取生产请求订单数据库配置信息
	public DBConfig getRequestDB() {
		System.out
				.println("SystemConfigDB::public DBConfig getRequestDB() | 获取生产请求订单数据库配置信息");
		// ###DBConfig temp = null;
		// ###return temp;
		return this.requestDBConfig;
	}

	// 获取订单数据库配置信息
	public DBConfig getDB() {
		System.out
				.println("SystemConfigDB::public DBConfig getDB() | 获取订单数据库配置信息");
		// ###DBConfig temp = null;
		// ###return temp;
		return this.orderDBConfig;
	}

	// 获取工作流库（处理流程库）配置信息
	public DBConfig getWFDB() {
		System.out
				.println("SystemConfigDB::public DBConfig getWFDB() | 获取工作流库（处理流程库）配置信息");
		// ###DBConfig temp = null;
		// ###return temp;
		return this.workflowDBConfig;
	}

	// 获取算法库配置信息
	public DBConfig getAlgrithmDB() {
		System.out
				.println("SystemConfigDB::public DBConfig getAlgrithmDB() | 获取算法库配置信息");
		// ###DBConfig temp = null;
		// ###return temp;
		return this.algorithmDBCongfig;
	}

	// 获取调度规则库配置信息
	public DBConfig getScheduleRuleDB() {
		System.out
				.println("SystemConfigDB::public DBConfig getScheduleRuleDB() | 获取调度规则库配置信息");
		// ###DBConfig temp = null;
		// ###return temp;
		return this.scheduleRuleDBConfig;
	}

	// 获取系统资源库配置信息
	public DBConfig getSystemResourceDB() {
		System.out
				.println("SystemConfigDB::public DBConfig getSystemResourceDB() | 获取系统资源库配置信息");
		// ###DBConfig temp = null;
		// ###return temp;
		return this.systemResourceDBConfig;
	}

	// 获取消息类型与webservice接口关系列表配置信息
	public Hashtable<String, String> getMsg2API() {
		System.out
				.println("SystemConfigDB::public Hashtable< String, String > getMsg2API() | 获取消息类型与webservice接口关系列表配置信息");
		// ###Hashtable< String, String > temp = null;
		// ###return temp;
		return this.msg2APIConfigList;
	}

	// 获取系统服务配置信息
	public DBConfig getDaemonServer() {
		System.out
				.println("SystemConfigDB::public DBConfig getDaemonServer() | 获取系统服务配置信息");
		return this.daemonServerConfig;
	}

	// 获取系统日志文件存放路径
	public String getLogFilePath() {
		System.out
				.println("SystemConfigDB::public String getLogFilePath() | 获取系统日志文件路径");
		return this.logFilePath;
	}

	// 获取系统数据缓存区定时清理时间的信息
	public Date getBufCleanupDates() {
		System.out
				.println("SystemConfigDB::public int getBufCleanupDates() | 获取系统数据缓存区定时清理时间的信息");
		return this.publicBufferCleanupDates;
	}

	// @@@
	// 获取系统数据缓存区最近一次清理时间
	public Date getLastCleanupDate() {
		System.out
				.println("SystemConfigDB::public Date getLastCleanupDate() | 获取系统数据缓存区最近一次清理时间");
		return this.lastCleanupDate;
	}

	// 用来记录数据缓存区的挂载点
	public String getBufferMountPoint() {
		System.out
				.println("SystemConfigDB::public String getBufferMountPoint() | 用来记录数据缓存区的挂载点");
		return this.bufferMountPoint;
	}

	// 获取系统数据缓存区的最大数据使用率
	public float getBufMaxUsage() {
		System.out
				.println("SystemConfigDB::public float getBufMaxUsage() | 获取系统数据缓存区的最大数据使用率");
		return this.publicBufferMaxUsage;
	}

	// 获取系统的daemon轮询时间频率
	public float getDaemonSleepTimeInMillisecond() {
		System.out
				.println("SystemConfigDB::public float getDaemonSleepTimeInMillisecond() | 获取系统的daemon轮询时间频率");
		return this.daemonSleepTimeInMillisecond;
	}

	// 更新Log4j到配置文件
	public boolean setLoggerInfo(String systemLog4jConfigFile) {
		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET  systemLog4jConfigFile= ? where id=1";
			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// Key-Value
			pstmt.setString(1, systemLog4jConfigFile);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setLoggerInfo(systemLog4jConfigFile);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean setServerConfig(ServerConfig serverConfig) {
		try {
			String strSql = "UPDATE "
					+ this.dbTable
					+ " SET  ip=?,hostName=?,ordersPath=?,logFilesPath= ?,algorithPath=?,globusPath=?,webservicePort=?,QsubPath=?,QdelPath=?,mcaWebserviceUrl=? where id=1";
			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// Key-Value
			int count = 0;
			pstmt.setString(++count, serverConfig.getIp());
			pstmt.setString(++count, serverConfig.getHostName());
			pstmt.setString(++count, serverConfig.getOrderPath());
			pstmt.setString(++count, serverConfig.getLogPath());
			pstmt.setString(++count, serverConfig.getAlgorithPath());
			pstmt.setString(++count, serverConfig.getGlobusPath());
			pstmt.setString(++count, serverConfig.getPort());
			pstmt.setString(++count, serverConfig.getQsubPath());
			pstmt.setString(++count, serverConfig.getQdelPath());
			pstmt.setString(++count, serverConfig.getMcaWebserviceUrl());

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setServerConfig(serverConfig);
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
