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
package SystemManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import DBManage.SystemConfigDB;

//import SystemManage.SystemResource.DataCenter;

/**
 * @author caoyang
 *
 */
public class SystemConfig {
	//主机IP 
	private static String ip;
	// 用来存放数据产品FTP配置信息: user:passwd
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
	private static String logsPath;
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
	private static SystemLogger systemLogger;
	// 系统配置文件URL
	private static String sysConfigFile;
	// War包工作目录
	private static String sysPath;
	// 系统运行日志Log4j配置文件
	private static String systemLog4jConfigFile;
	// Kepler可执行Jar的运行日志Log4j配置文件
	private static String outerLog4jConfigFile;
	// 产品公共缓存区配置
	private static String commonProductsPublicBufferLocalPath;
	private static String standardProductsPublicBufferLocalPath;
	// Kepler工作流存放位置
	private static String KeplerWorkflowFilePath;	

	// 设置系统配置文件
	public static void setConfigFile(String sysConfigFile) {
		System.out
				.println("SystemConfig::public static void setConfigFile( String sysConfigFile ) | 设置系统配置文件");
		SystemConfig.sysConfigFile = sysConfigFile;
		// SystemConfig.sysPath = sysConfigFile.substring( 0,
		// sysConfigFile.lastIndexOf( "/" ) ) + "/";

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

		SystemConfig.sysPath = sysConfigFile.substring(0,
				sysConfigFile.lastIndexOf(pathDecollator))
				+ pathDecollator;

		System.out.println("********************* sysPath : "
				+ SystemConfig.sysPath);
	}

	// 从系统配置文件载入系统配置信息
	public static boolean loadSystemConfig() {
		System.out
				.println("SystemConfig::public static boolean loadSystemConfig() | 从系统配置文件载入系统配置信息");
		// 从配置文件获取系统配置数据库信息
		configuartionDBConfig = loadConfig(sysConfigFile, "sysConfigDB");
		//初始化数据库 加载系统配置变量
		sysConfigDB = new SystemConfigDB(configuartionDBConfig);
		boolean flag = sysConfigDB.isConnected();
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
		// 获取系统日志文件信息
		logsPath = sysConfigDB.getLogFile();
		
		systemLog4jConfigFile = sysPath + "log4j.properties";
		outerLog4jConfigFile = sysPath + "log4jOuter.properties";	
		if (!sysConfigDB.setLoggerInfo(systemLog4jConfigFile,
				outerLog4jConfigFile)) {
			return false;
		}
		// 创建系统全局日志对象
		systemLogger = new SystemLogger(logsPath);	
		//獲取IP
		ip=sysConfigDB.getIp();
		
		//获取存放路径
		commonProductsPublicBufferLocalPath=sysConfigDB.getCommonProductsPublicBufferLocalPath();
		standardProductsPublicBufferLocalPath=sysConfigDB.getStandardProductsPublicBufferLocalPath();
		KeplerWorkflowFilePath=sysConfigDB.getKeplerWorkflowFilePath();
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
		SystemConfig.logsPath = logFile;
	}

	// 设置全局Log日志对象
	public static void setGlobalLog(String logFile) {
		System.out
				.println("SystemConfig::public static void setGlobalLog( String logFile ) | 设置全局Log日志对象");
		systemLogger = new SystemLogger(logFile);
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
	public static String getLogFile() {
		System.out
				.println("SystemConfig::public static String getLogFile() | 获取系统日志文件路径");
		return logsPath;
	}

	// 获取系统日志文件路径
	// !!!public static Log getGlobalLog(){
	public static SystemLogger getGlobalLog() {
		System.out
				.println("SystemConfig::public static SystemLogger getGlobalLog() | 获取系统日志文件路径");
		return systemLogger;
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

	public static String getSystemLog4jConfigFile() {
		return systemLog4jConfigFile;
	}

	public static void setSystemLog4jConfigFile(String systemLog4jConfigFile) {
		SystemConfig.systemLog4jConfigFile = systemLog4jConfigFile;
	}

	public static String getOuterLog4jConfigFile() {
		return outerLog4jConfigFile;
	}

	public static void setOuterLog4jConfigFile(String outerLog4jConfigFile) {
		SystemConfig.outerLog4jConfigFile = outerLog4jConfigFile;
	}
	
	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		SystemConfig.ip = ip;
	}

	public static String getLogsPath() {
		return logsPath;
	}

	public static void setLogsPath(String logsPath) {
		SystemConfig.logsPath = logsPath;
	}

	public static String getCommonProductsPublicBufferLocalPath() {
		return commonProductsPublicBufferLocalPath;
	}

	public static void setCommonProductsPublicBufferLocalPath(
			String commonProductsPublicBufferLocalPath) {
		SystemConfig.commonProductsPublicBufferLocalPath = commonProductsPublicBufferLocalPath;
	}

	public static String getStandardProductsPublicBufferLocalPath() {
		return standardProductsPublicBufferLocalPath;
	}

	public static void setStandardProductsPublicBufferLocalPath(
			String standardProductsPublicBufferLocalPath) {
		SystemConfig.standardProductsPublicBufferLocalPath = standardProductsPublicBufferLocalPath;
	}

	public static String getKeplerWorkflowFilePath() {
		return KeplerWorkflowFilePath;
	}

	public static void setKeplerWorkflowFilePath(String keplerWorkflowFilePath) {
		KeplerWorkflowFilePath = keplerWorkflowFilePath;
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
			String productDB=properties.getProperty("PRODUCTDB");

			config = new DBConfig(ip, port, sid, user, passwd, dbtable,productDB);

			// test
			System.out.println("--------------------- ip        :" + ip);
			System.out.println("--------------------- port      :" + port);
			System.out.println("--------------------- sid       :" + sid);
			System.out.println("--------------------- user      :" + user);
			System.out.println("--------------------- passwd    :" + passwd);
			System.out.println("--------------------- dbtable   :" + dbtable);
			System.out.println("--------------------- productdb :" + productDB);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return config;
	}
}
