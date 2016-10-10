package FileOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import SystemManage.SystemLogger;


/**
 * 创建时间：2016-1-19 下午8:33:03 项目名称：DataQueryAcquireService_1.1 2016-1-19
 * 
 * @author 张杰
 * @version 1.0 文件名称：FileOperation.java 类说明：文件操作，主要用户获取文件元数据信息
 */
public class FileOperation {
	// 日志操作
	Logger logger = SystemLogger.getSysLogger();

	public FileOperation() {
	}

	// 获取文件大小：不用File.length()方法，避免大文件获取大小出错
	public long getFileSize(String filePath) {
		File file = new File(filePath);		
		FileChannel fc = null;
		long size = -1;
		try {
			if (file.exists() && file.isFile()) {
				FileInputStream fis = new FileInputStream(file);
				fc = fis.getChannel();
				size = fc.size();
			} else {
				logger.info(filePath+"file doesn't exist or is not a file");
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

	// 写文件
	public void appendMethodB(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("输出记录失败");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// 数据库操作
		
		FileOperation fileOperation = new FileOperation();
		
		fileOperation.appendMethodB(null, "Name=" + null
				+ " ,Size= " + null + " ,DownLoadTimeConsume="
				+ null);

	}

}
