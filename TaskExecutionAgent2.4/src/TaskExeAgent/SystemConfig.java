/*
 *程序名称 		: SystemConfig.java
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
package TaskExeAgent;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.util.Date;
import java.sql.Date;
import java.util.Hashtable;
import java.util.Properties;

import DBManage.SystemConfigDB;

//import SystemManage.SystemResource.DataCenter;

/**
 * @author caoyang
 * 
 */
public class SystemConfig {
	// 用来存放数据产品FTP配置信息: user:passwd@ftpIP
	private static String dataProductFtpConfig;
	// 用来存放各数据分中心的配置信息: ip:port
	private static Hashtable<String, ServerConfig> dataCenterConfigList;
	// 系统配置信息数据库对象
	private static SystemConfigDB sysConfigDB;
	// 用来存放系统配置信息数据库配置信息
	private static DBConfig configuartionDBConfig;
	// 用来存放生产请求订单数据库的配置信息
	private static DBConfig requestOrderDBConfig; // 追加
	// 用来存放订单数据库的配置信息
	private static DBConfig orderDBConfig;
	// 用来存放处理流程数据库的配置信息
	private static DBConfig workflowDBConfig;
	// 用来存放算法资源数据库的配置信息
	private static DBConfig algorithmDBConfig;
	// 用来存放调度规则数据库的配置信息
	private static DBConfig scheduleRuleDBConfig;
	// 用来存放系统资源库配置信息
	private static DBConfig sysResourceDBConfig;
	// 用来存放消息与web接口函数映射关系
	private static Hashtable<String, String> msg2APIConfig;
	// 用来存放主中心系统服务发布端的配置信息
	private static ServerConfig daemonServerConfig;
	// 用来存放系统日志文件路径
	private static String logFilesPath;
	// Log4j日志系统配置文件
	private static String systemLog4jConfigFile;
	// 用来记录共享缓存区的清理时间间隔
	private static int publicBufferCleanupDates;
	// 用来记录上一次清理时间
	private static Date lastCleanupDate;
	// 用来记录数据缓存区的挂载点
	private static String bufferMountPoint;
	// 用来记录共享缓存区的最大使用率，超过后马上自动清理空间
	private static float publicBufferMaxUsage;
	// 用来设置daemon的轮询时间频率
	private static float daemonSleepTimeInMillisecond;
	// 用来创建并获取全局的日志管理类
	private static SystemLogger logger;
	// 系统数据库配置文件URL
	private static String sysConfigFile;
	// War包工作目录
	private static String sysPath;
	// 系统环境变量配置文件Path
	private static String serverConfigFile;
	// 系统环境变量
	private static ServerConfig serverConfig;

	
	// 设置系统配置文件
	public static void setConfigFile(String sysConfigFile) {
		System.out
				.println("SystemConfig::public static void setConfigFile( String sysConfigFile ) | 设置系统配置文件");

		// test
		System.out.println("ConfigFile : " + sysConfigFile);

		String pathDecollator = "/";
		String OS = System.getProperty("os.name").toLowerCase();
		// if( OS.indexOf( "linux" ) >= 0 ){
		// ;
		// }
		if (OS.indexOf("windows") >= 0) {
			pathDecollator = "\\";
		}

		SystemConfig.sysConfigFile = sysConfigFile;
		// SystemConfig.sysPath = sysConfigFile.substring( 0,
		// sysConfigFile.lastIndexOf( "/" ) ) + "/";
		// SystemConfig.sysPath = sysConfigFile.substring( 0,
		// sysConfigFile.lastIndexOf( "/" ) ); //@@@ 需要确认工作目录的设定，具体是在哪个地方。
		SystemConfig.sysPath = sysConfigFile.substring(0,
				sysConfigFile.lastIndexOf(pathDecollator));

		// test
		System.out.println("********************* sysconfigfile : "
				+ SystemConfig.sysConfigFile);
		System.out.println("********************* sysPath       : "
				+ SystemConfig.sysPath);
	}

	// 从系统配置文件载入系统配置信息
	public static boolean loadSystemConfig() {
		System.out
				.println("SystemConfig::public static boolean loadSystemConfig() | 从系统配置文件载入系统配置信息");
		boolean flag = true;

		// 从配置文件获取系统配置数据库信息
		configuartionDBConfig = loadConfig(sysConfigFile, "sysConfigDB");
		// 获取系统的环境变量（目录信息）:a、执行获取脚本得到属性文件；b、解析属性文件变量
		// a、执行系统环境变量获取脚本
		SystemConfig.serverConfigFile = SystemConfig.sysPath
				+ "/serverConfig.property";
		String getEnvShell = SystemConfig.sysPath + "/getEnv.sh";
		// test
		// System.out.println("--------"+getEnvShell);
		// 脚本执行权限设置
		String command = "chmod +x " + getEnvShell;
		flag &= executeShell(command);
		// 执行获取系统环境变量脚本
		command = getEnvShell + " " + SystemConfig.serverConfigFile;
		flag &= executeShell(command);

		// b、解析属性文件变量
		serverConfig = loadServerConfig(SystemConfig.serverConfigFile);

		// 创建系统配置信息数据库对象
		// ###sysConfigDB = new SystemConfigDB( configuartionDBConfig );
		sysConfigDB = new SystemConfigDB(configuartionDBConfig);
		// !!!boolean flag = sysConfigDB.connet();
		flag &= sysConfigDB.isConnected();
		return flag;
	}

	// 从系统配置信息数据库载入基本的系统配置信息
	public static boolean loadBasicConfig() {
		System.out
				.println("SystemConfig::public static boolean loadBasicConfig() | 从系统配置信息数据库载入基本的系统配置信息");
		// 获取数据产品FTP配置信息
		dataProductFtpConfig = sysConfigDB.getFTP();
		// 获取数据中心配置信息
		dataCenterConfigList = sysConfigDB.getDataCenter();
		// 获取生产请求订单数据库配置信息
		requestOrderDBConfig = sysConfigDB.getRequestDB(); // 追加
		// 获取订单数据库配置信息
		orderDBConfig = sysConfigDB.getDB();
		// 获取工作流库（处理流程库）配置信息
		workflowDBConfig = sysConfigDB.getWFDB();
		// 获取算法库配置信息
		algorithmDBConfig = sysConfigDB.getAlgrithmDB();
		// 获取调度规则库配置信息
		scheduleRuleDBConfig = sysConfigDB.getScheduleRuleDB();
		// 获取系统资源库配置信息
		sysResourceDBConfig = sysConfigDB.getSystemResourceDB();
		// 获取消息类型与webservice接口关系列表配置信息
		msg2APIConfig = sysConfigDB.getMsg2API();

		// 获取系统日志文件存放路径
		logFilesPath = serverConfig.getLogPath();
		//更新服务器基本配置信息：算法、日志、订单路径等
		if (!sysConfigDB.setServerConfig(serverConfig)) {
			return false;
		}		
		// 设置log4j文件路径
		systemLog4jConfigFile = SystemConfig.sysPath + "/log4j.properties";
		// 更新文件路径到数据库
		if (!sysConfigDB.setLoggerInfo(systemLog4jConfigFile)) {
			return false;
		}
		
		
		
		
		
		// 创建系统全局日志对象
		logger = new SystemLogger(logFilesPath);
		// 获取系统数据缓存区定时清理时间的信息
		// @@@publicBufferCleanupDates = sysConfigDB.getBufCleanupDates();
		// 获取系统数据缓存区最近一次清理时间
		lastCleanupDate = sysConfigDB.getLastCleanupDate();
		// 用来记录数据缓存区的挂载点
		// !!!private static String bufferMountPoint;
		bufferMountPoint = sysConfigDB.getBufferMountPoint();
		// 获取系统数据缓存区的最大数据使用率
		publicBufferMaxUsage = sysConfigDB.getBufMaxUsage();
		// 获取系统的daemon轮询时间频率
		daemonSleepTimeInMillisecond = sysConfigDB
				.getDaemonSleepTimeInMillisecond();
		// 对获取的配置信息进行合法性检查
		if (isValidated()) // 类的辅助函数，不在此文件中具体定义
			return true;
		else
			return false;
	}

	// set函数
	// 设置数据产品FTP配置信息: user:passwd@ftpIP
	public static void setDataProductFtpConfig(String dataProductFtpConfig) {
		System.out
				.println("SystemConfig::public static void setDataProductFtpConfig( String dataProductFtpConfig ) | 设置数据产品FTP配置信息");
		SystemConfig.dataProductFtpConfig = dataProductFtpConfig;
	}

	// 设置各数据分中心的配置信息: ip:port
	public static void setDataCenterConfigList(
			Hashtable<String, ServerConfig> dataCenterConfigList) {
		System.out
				.println("SystemConfig::public static void setDataCenterConfigList( Hashtable< String, ServerConfig > dataCenterConfigList ) | 设置各数据分中心的配置信息");
		SystemConfig.dataCenterConfigList = dataCenterConfigList;
	}

	public static void addDataCenterConfigList(String name,
			ServerConfig dataCenterConfig) {
		System.out
				.println("SystemConfig::public static void addDataCenterConfigList( String name, ServerConfig dataCenterConfig ) | 设置各数据分中心的配置信息");
		// !!!dataCenterConfigList.add( name, dataCenterConfig );
		dataCenterConfigList.put(name, dataCenterConfig);
	}

	// 设置系统配置信息数据库配置信息
	public static void setConfiguartionDBConfig(DBConfig configuartionDBConfig) {
		System.out
				.println("SystemConfig::public static void setConfiguartionDBConfig( DBConfig configuartionDBConfig ) | 设置系统配置信息数据库配置信息");
		SystemConfig.configuartionDBConfig = configuartionDBConfig;
	}

	// 设置生产请求订单数据库配置信息
	public static void setRequestOrderDBConfig(DBConfig orderDBConfig) {
		System.out
				.println("SystemConfig::public static void setRequestOrderDBConfig( DBConfig orderDBConfig ) | 设置生产请求订单数据库配置信息");
		SystemConfig.requestOrderDBConfig = orderDBConfig;
	}

	// 设置订单数据库配置信息
	public static void setOrderDBConfig(DBConfig orderDBConfig) {
		System.out
				.println("SystemConfig::public static void setOrderDBConfig( DBConfig orderDBConfig ) | 设置订单数据库配置信息");
		SystemConfig.orderDBConfig = orderDBConfig;
	}

	// 设置处理流程数据库的配置信息
	public static void setWorkflowDBConfig(DBConfig workflowDBConfig) {
		System.out
				.println("SystemConfig::public static void setWorkflowDBConfig( DBConfig workflowDBConfig ) | 设置处理流程数据库的配置信息");
		SystemConfig.workflowDBConfig = workflowDBConfig;
	}

	// 设置算法资源数据库的配置信息
	public static void setAlgorithmDBConfig(DBConfig algorithmDBConfig) {
		System.out
				.println("SystemConfig::public static void setAlgorithmDBConfig( DBConfig algorithmDBConfig ) | 设置算法资源数据库的配置信息");
		SystemConfig.algorithmDBConfig = algorithmDBConfig;
	}

	// 设置调度规则数据库的配置信息
	public static void setScheduleRuleDBConfig(DBConfig scheduleRuleDBConfig) {
		System.out
				.println("SystemConfig::public static void setScheduleRuleDBConfig( DBConfig scheduleRuleDBConfig ) | 设置调度规则数据库的配置信息");
		SystemConfig.scheduleRuleDBConfig = scheduleRuleDBConfig;
	}

	// 设置系统资源库的配置信息
	public static void setSysResourceDBConfig(DBConfig sysResourceDBConfig) {
		System.out
				.println("SystemConfig::public static void setSysResourceDBConfig( DBConfig sysResourceDBConfig ) | 设置系统资源库的配置信息");
		SystemConfig.sysResourceDBConfig = sysResourceDBConfig;
	}

	// 设置消息与web接口函数映射关系
	public static void setMsg2APIConfig(Hashtable<String, String> msg2APIConfig) {
		System.out
				.println("SystemConfig::public static void setMsg2APIConfig( Hashtable< String, String > msg2APIConfig ) | 设置消息与web接口函数映射关系");
		SystemConfig.msg2APIConfig = msg2APIConfig;
	}

	public static void addMsg2APIConfig(String MsgName, String APIName) {
		System.out
				.println("SystemConfig::public static void addMsg2APIConfig( String MsgName, String APIName ) | 设置消息与web接口函数映射关系");
		// !!!msg2APIConfig.add( MsgName, APIName );
		msg2APIConfig.put(MsgName, APIName);
	}

	// 设置主中心系统服务发布端的配置信息
	public static void setDaemonServerConfig(ServerConfig daemonServerConfig) {
		System.out
				.println("SystemConfig::public static void setDaemonServerConfig( ServerConfig daemonServerConfig ) | 设置主中心系统服务发布端的配置信息");
		SystemConfig.daemonServerConfig = daemonServerConfig;
	}

	// 设置系统日志文件路径
	public static void setlogFile(String logFile) {
		System.out
				.println("SystemConfig::public static void setlogFile( String logFile ) | 设置系统日志文件路径");
		SystemConfig.logFilesPath = logFile;
	}

	// 设置全局Log日志对象
	public static void setGlobalLog(String logFile) {
		System.out
				.println("SystemConfig::public static void setGlobalLog( String logFile ) | 设置全局Log日志对象");
		logger = new SystemLogger(logFile);
	}

	// 设置共享缓存区的清理时间间隔
	public static void setPublicBufferCleanupDates(int publicBufferCleanupDates) {
		System.out
				.println("SystemConfig::public static void setPublicBufferCleanupDates( int publicBufferCleanupDates ) | 设置共享缓存区的清理时间间隔");
		SystemConfig.publicBufferCleanupDates = publicBufferCleanupDates;
	}

	// 设置系统数据缓存区最近一次清理时间
	// !!!public static void setLastCleanupDate( int lastCleanupDate ){
	public static void setLastCleanupDate(Date lastCleanupDate) {
		System.out
				.println("SystemConfig::public static void setLastCleanupDate( Date lastCleanupDate ) | 设置系统数据缓存区最近一次清理时间");
		SystemConfig.lastCleanupDate = lastCleanupDate;
	}

	// 设置系统数据缓存区的目录挂载点
	public static void setBufferMountPoint(String bufferMountPoint) {
		System.out
				.println("SystemConfig::public static void setBufferMountPoint( String bufferMountPoint ) | 设置系统数据缓存区的目录挂载点");
		SystemConfig.bufferMountPoint = bufferMountPoint;
	}

	// 设置共享缓存区的最大使用率
	public static void setPublicBufferMaxUsage(float publicBufferMaxUsage) {
		System.out
				.println("SystemConfig::public static void setPublicBufferMaxUsage( float publicBufferMaxUsage ) | 设置共享缓存区的最大使用率");
		SystemConfig.publicBufferMaxUsage = publicBufferMaxUsage;
	}

	// 设置daemon的轮询时间频率
	public static void setDaemonSleepTimeInMillisecond(
			float daemonSleepTimeInMillisecond) {
		System.out
				.println("SystemConfig::public static void setDaemonSleepTimeInMillisecond( float daemonSleepTimeInMillisecond ) | 设置daemon的轮询时间频率");
		SystemConfig.daemonSleepTimeInMillisecond = daemonSleepTimeInMillisecond;
	}

	// get方法
	// 设置数据产品FTP配置信息: user:passwd@ftpIP
	public static String getDataProductFtpConfig() {
		System.out
				.println("SystemConfig::public static String getDataProductFtpConfig() | 设置数据产品FTP配置信息");
		return dataProductFtpConfig;
	}

	// 设置各数据分中心的配置信息: ip:port
	public static Hashtable<String, ServerConfig> getDataCenterConfigList() {
		System.out
				.println("SystemConfig::public static Hashtable< String, ServerConfig > getDataCenterConfigList() | 设置各数据分中心的配置信息");
		return dataCenterConfigList;
	}

	public static ServerConfig getDataCenterConfig(String name) {
		System.out
				.println("SystemConfig::public static ServerConfig getDataCenterConfig( String name ) | 设置各数据分中心的配置信息");
		return dataCenterConfigList.get(name);
	}

	// 获取系统配置信息数据库配置信息
	public static DBConfig getConfiguartionDBConfig() {
		System.out
				.println("SystemConfig::public static DBConfig getConfiguartionDBConfig() | 获取系统配置信息数据库配置信息");
		return configuartionDBConfig;
	}

	// 获取生产请求订单数据库配置信息
	public static DBConfig getRequestOrderDBConfig() {
		System.out
				.println("SystemConfig::public static DBConfig getRequestOrderDBConfig() | 获取生产请求订单数据库配置信息");
		return requestOrderDBConfig;
	}

	// 获取订单数据库配置信息
	public static DBConfig getOrderDBConfig() {
		System.out
				.println("SystemConfig::public static DBConfig getOrderDBConfig() | 获取订单数据库配置信息");
		return orderDBConfig;
	}

	// 获取处理流程数据库的配置信息
	public static DBConfig getWorkflowDBConfig() {
		System.out
				.println("SystemConfig::public static DBConfig getWorkflowDBConfig() | 获取处理流程数据库的配置信息");
		return workflowDBConfig;
	}

	// 获取算法资源数据库的配置信息
	public static DBConfig getAlgorithmDBConfig() {
		System.out
				.println("SystemConfig::public static DBConfig getAlgorithmDBConfig() | 获取算法资源数据库的配置信息");
		return algorithmDBConfig;
	}

	// 获取调度规则数据库的配置信息
	public static DBConfig getScheduleRuleDBConfig() {
		System.out
				.println("SystemConfig::public static DBConfig getScheduleRuleDBConfig() | 获取调度规则数据库的配置信息");
		return scheduleRuleDBConfig;
	}

	// 获取系统资源库的配置信息
	public static DBConfig getSysResourceDBConfig() {
		System.out
				.println("SystemConfig::public static DBConfig getSysResourceDBConfig() | 获取系统资源库的配置信息");
		return sysResourceDBConfig;
	}

	// 获取消息与web接口函数映射关系
	public static Hashtable<String, String> getMsg2APIConfig() {
		System.out
				.println("SystemConfig::public static Hashtable< String, String > getMsg2APIConfig() | 获取消息与web接口函数映射关系");
		return msg2APIConfig;
	}

	public static String getMsg2APIConfig(String MsgName) {
		System.out
				.println("SystemConfig::public static String getMsg2APIConfig( String MsgName ) | 获取消息与web接口函数映射关系");
		return msg2APIConfig.get(MsgName);
	}

	// 获取主中心系统服务发布端的配置信息
	public static ServerConfig getDaemonServerConfig() {
		System.out
				.println("SystemConfig::public static ServerConfig getDaemonServerConfig() | 获取主中心系统服务发布端的配置信息");
		return daemonServerConfig;
	}

	// 获取系统日志文件路径
	public static String getLogFilesPath() {
		System.out
				.println("SystemConfig::public static String getLogFile() | 获取系统日志文件路径");
		return logFilesPath;
	}

	// 获取系统日志文件路径
	// !!!public static Log getGlobalLog(){
	public static SystemLogger getGlobalLog() {
		System.out
				.println("SystemConfig::public static SystemLogger getGlobalLog() | 获取系统日志文件路径");
		return logger;
	}

	// 获取共享缓存区的清理时间间隔
	public static int getPublicBufferCleanupDates() {
		System.out
				.println("SystemConfig::public static int getPublicBufferCleanupDates() | 获取共享缓存区的清理时间间隔");
		return publicBufferCleanupDates;
	}

	// 获取系统数据缓存区最近一次清理时间
	public static Date getLastCleanupDate() {
		System.out
				.println("SystemConfig::public static Date getLastCleanupDate() | 获取系统数据缓存区最近一次清理时间");
		return lastCleanupDate;
	}

	// 获取系统数据缓存区的目录挂载点
	public static String getBufferMountPoint() {
		System.out
				.println("SystemConfig::public static String getBufferMountPoint() | 获取系统数据缓存区的目录挂载点");
		return bufferMountPoint;
	}

	// 获取共享缓存区的最大使用率
	public static float getPublicBufferMaxUsage() {
		System.out
				.println("SystemConfig::public static float getPublicBufferMaxUsage() | 获取共享缓存区的最大使用率");
		// !!!return publicBufferBufferMaxUsage;
		return publicBufferMaxUsage;
	}

	// 获取daemon的轮询时间频率
	public static float getDaemonSleepTimeInMillisecond() {
		System.out
				.println("SystemConfig::public static float getDaemonSleepTimeInMillisecond() | 获取daemon的轮询时间频率");
		return daemonSleepTimeInMillisecond;
	}

	// 获取WAR包路径
	public static String getSysPath() {
		System.out
				.println("SystemConfig::public static String getSysPath() | 获取WAR包路径");
		return sysPath;
	}

	// 获取Log4j Configure文件路径
	public static String getSystemLog4jConfigFile() {
		return systemLog4jConfigFile;
	}

	// 设置Log4j Configure文件路径
	public static void setSystemLog4jConfigFile(String systemLog4jConfigFile) {
		SystemConfig.systemLog4jConfigFile = systemLog4jConfigFile;
	}
	//获取服务器基本配置信息
	public static ServerConfig getServerConfig() {
		return serverConfig;
	}

	//设置服务器基本配置信息
	public static void setServerConfig(ServerConfig serverConfig) {
		SystemConfig.serverConfig = serverConfig;
	}


	// 配置信息合法性检查
	private static boolean isValidated() {
		System.out
				.println("SystemConfig::private static boolean isValidated() | 配置信息合法性检查");
		return true;
	}

	// 从配置文件获取系统配置数据库信息
	private static DBConfig loadConfig(String configFile, String configType) {
		System.out
				.println("SystemConfig::private static DBConfig loadConfig( String configFile, String configType ) | 从配置文件获取系统配置数据库信息");

		DBConfig config = null;
		try {
			Properties properties = new Properties();
			FileInputStream in = new FileInputStream(new File(configFile));
			// FileInputStream in = new FileInputStream(new
			// File("/home/pips-plus/main/src/yuantao/workspace/OMSWebService/WebContent/database.property"));
			properties.load(in);
			in.close();
			// 初始化默认数据库相关参数选项
			String ip = properties.getProperty("IP");
			String port = properties.getProperty("PORT");
			String sid = properties.getProperty("SID");
			String user = properties.getProperty("USER");
			String passwd = properties.getProperty("PASSWD");
			String dbtable = properties.getProperty("DBTABLE");

			// test
			System.out.println("--------------------- ip      :" + ip);
			System.out.println("--------------------- port    :" + port);
			System.out.println("--------------------- sid     :" + sid);
			System.out.println("--------------------- user    :" + user);
			System.out.println("--------------------- passwd  :" + passwd);
			System.out.println("--------------------- dbtable :" + dbtable);

			config = new DBConfig(ip, port, sid, user, passwd, dbtable);
		} /*
		 * catch (PropertyVetoException e) { e.printStackTrace();
		 * //logger.error("数据库配置文件读取异常", e); }
		 */catch (FileNotFoundException e) {
			e.printStackTrace();
			// logger.error("数据库配置文件读取异常", e);
		} catch (IOException e) {
			e.printStackTrace();
			// logger.error("数据库配置文件读取异常", e);
		}

		return config;
	}

	// 执行获取系统变量脚本
	private static boolean executeShell(String command) {
		Process process = null;
		int exitVal;
		boolean flag = false;

		try {
			process = Runtime.getRuntime().exec(command);
			exitVal = process.waitFor();
			if (0 == exitVal) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (process != null) {
				close(process.getOutputStream());
				close(process.getInputStream());
				close(process.getErrorStream());
				process.destroy();
				// System.out.println("process destroy sucessed!");
			}
		}

		return flag;
	}

	private static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 加载系统环境变量配置
	private static ServerConfig loadServerConfig(String configFile) {
		System.out
				.println("SystemConfig::private static ServerConfig loadServerConfig() | 从配置文件获取系统配置环境变量信息");
		ServerConfig serverConfig = null;

		try {
			Properties properties = new Properties();
			FileInputStream in = new FileInputStream(new File(configFile));
			properties.load(in);
			in.close();
			// 初始化系统环境变量参数选项
			String ip = properties.getProperty("IP");
			String port = properties.getProperty("Port");
			String hostName = properties.getProperty("HostName");
			String orderPath = properties.getProperty("OrderPath");
			String logPath = properties.getProperty("LogPath");
			String algorithPath = properties.getProperty("ALgorithPath");
			String globusLocation = properties.getProperty("GlobusPath");
			String qsubPath=properties.getProperty("QsubPath");
			String qdelPath=properties.getProperty("QdelPath");
			String mcaWebServiceUrl=properties.getProperty("MCAWebServiceUrl");
			// test
			System.out.println("--------------------- ip              :" + ip);
			System.out.println("--------------------- port            :" + port);
			System.out.println("--------------------- hostName        :"
					+ hostName);
			System.out.println("--------------------- orderPath       :"
					+ orderPath);
			System.out.println("--------------------- logPath         :"
					+ logPath);
			System.out.println("--------------------- algorithPath    :"
					+ algorithPath);
			System.out.println("--------------------- globusLocation  :"
					+ globusLocation);
			System.out.println("--------------------- qsubPath        :"
					+ qsubPath);
			System.out.println("--------------------- qdelPath        :"
					+ qdelPath);
			System.out.println("--------------------- mcaWebServiceUrl:"
					+ mcaWebServiceUrl);
			

			serverConfig = new ServerConfig(ip, port, hostName, orderPath,
					logPath, algorithPath, globusLocation,qsubPath,qdelPath,mcaWebServiceUrl);
		} /*
		 * catch (PropertyVetoException e) { e.printStackTrace();
		 * //logger.error("数据库配置文件读取异常", e); }
		 */catch (FileNotFoundException e) {
			e.printStackTrace();
			// logger.error("数据库配置文件读取异常", e);
		} catch (IOException e) {
			e.printStackTrace();
			// logger.error("数据库配置文件读取异常", e);
		}

		return serverConfig;
	}

}
