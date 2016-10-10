package SystemManage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.string;

/**
 * @author caoyang
 *
 */
/**
 * 创建时间：2015-9-5 下午9:22:15 项目名称：MultiProcessSysWebService_Thread 2015-9-5
 * 
 * @author 张杰
 * @version 1.0 文件名称：SystemLogger.java 类说明：
 */
public class SystemLogger {
	// 用来存放Log日志对象
	// ###private Log sysLog;
	// 用来存放Log日志读写操作对象
	public static Logger sysLogger = Logger.getLogger(SystemLogger.class
			.getClass());

	public SystemLogger(String logsPath) {
		// log4j配置文件
		String loggerConfigFile = SystemConfig.getSystemLog4jConfigFile();
		// 更新配置文件中logger路径
		updateLog4jConfig(loggerConfigFile,logsPath);
		
		
		//Update Outer Logger
		String outerloggerConfigFile=SystemConfig.getOuterLog4jConfigFile();
		updateOuterLog4jConfig(outerloggerConfigFile, logsPath);
		// 从配置文件中加载log
		PropertyConfigurator.configure(loggerConfigFile);
	}

	public static Logger getSysLogger() {

		return sysLogger;
	}

	private void updateLog4jConfig(String configFile,String logfilePath) {
		// 读取配置文件
		System.out.println("         "+configFile);
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(new File(configFile));
			prop.load(in);
			prop.setProperty("log4j.appender.Debug.File",logfilePath+"/SystemRuntimeLogger");
			prop.setProperty("log4j.appender.Error.File",logfilePath+"/SystemErrorLogger.log");
			FileOutputStream fos = new FileOutputStream(configFile);
			prop.store(fos, "");
			System.out.println("系统日志更新至："+logfilePath);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
				in = null;
			}
		}
	}
	
	private void updateOuterLog4jConfig(String configFile,String logfilePath) {
		// 读取配置文件
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(new File(configFile));
			prop.load(in);
			prop.setProperty("log4j.appender.Debug.File",logfilePath+"/OuterRunnableJarRuntimeLogger");
			prop.setProperty("log4j.appender.Error.File",logfilePath+"/OuterRunableJarErrorLogger.log");
			FileOutputStream fos = new FileOutputStream(configFile);
			prop.store(fos, "");
			System.out.println("系统日志更新至："+logfilePath);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
				in = null;
			}
		}
	}

}
