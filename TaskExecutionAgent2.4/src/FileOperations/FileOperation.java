package FileOperations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.Properties;

import org.apache.log4j.Logger;

import DBManage.DBConn;
import DBManage.SystemConfigDB;
import TaskExeAgent.SystemLogger;

/**
 * 创建时间：2016-1-19 下午8:33:03 项目名称：DataQueryAcquireService_1.1 2016-1-19
 * 
 * @author 张杰
 * @version 1.0 文件名称：FileOperation.java 类说明：文件操作，主要用户获取文件元数据信息
 */
public class FileOperation {
	// 日志操作
	Logger logger = SystemLogger.getSysLogger();
	public File file;
	
	public FileOperation(String filePath) {
		this.file=new File(filePath);

	}
	
	//获取文件路径
	public String getFilePath() {
		return this.file.getAbsolutePath();
	}
	

	//获取文件大小：不用File.length()方法，避免大文件获取大小出错
	public long getFileSize() {
		FileChannel fc = null;
		long size = -1;
		try {
			if (file.exists() && file.isFile()) {
				FileInputStream fis = new FileInputStream(file);
				fc = fis.getChannel();
				size = fc.size();
			} else {
				logger.info("file doesn't exist or is not a file");
			}
		} catch (FileNotFoundException e) {
			logger.error(e);
			return -1;
		} catch (IOException e) {
			logger.error(e);
			return -1;
		} finally {
			if (null != fc) {
				try {
					fc.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}

		return size;
	}
	
	// 获取属性文件的属性值
	public String getAlgorithmResult(String sourceFile,String property) {

		Properties prop = new Properties();
		String result = "";
		InputStream in = null;
		try {
			in = new FileInputStream(new File(sourceFile));
			prop.load(in);
			result = prop.getProperty(property);
		} catch (IOException e) {
			// e.printStackTrace();
			logger.info("Result.log not found!");
			return "Running";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
				in = null;
			}
		}
		return result;
	}
	

	public static void main(String[] args) {
		// 数据库操作
//		DBConn connection = new DBConn("10.3.10.1", "3306", "mccps", "caoyang","123456");
//		SystemConfigDB systemConfigDB = new SystemConfigDB();
		FileOperation operation = new FileOperation("D:\\test\\MOD35_L2.A2014184.0440.006.2015074013252.hdf.zip");
		System.out
				.println(operation.getFileSize());

	}

}
