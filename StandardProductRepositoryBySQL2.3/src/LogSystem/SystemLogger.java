package LogSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import DBSystem.SystemConfigDB;

/**
 * 创建时间：2015-11-20 下午3:34:32 项目名称：RSDataPrepareRunnableJar2.0 2015-11-20
 * 
 * @author 张杰
 * @version 1.0 文件名称：SystemLogger.java 类说明：单例模式构建日志系统
 */
public class SystemLogger {

	private static volatile SystemLogger instance = null;

	public Logger sysLogger = Logger.getLogger(SystemLogger.class.getClass());

	private SystemLogger() {
		// 读取系统日志配置，初始化日志系统
		SystemConfigDB systemConfigDB = new SystemConfigDB();
		String configFile = systemConfigDB.getOuterLog4jConfigFile();
		
		PropertyConfigurator.configure(configFile);	
		sysLogger.info("日志系统配置成功！");
	}

	public Logger getSysLogger() {
		return this.sysLogger;
	}

	// 单例
	public static SystemLogger getInstance() {
		if (instance == null) {
			synchronized (SystemLogger.class) {
				if (instance == null) {
					instance = new SystemLogger();
				}
			}
		}
		return instance;
	}

	public static void main(String[] args) {
		Logger logger = SystemLogger.getInstance().sysLogger;
		logger.error("");

	}

}
