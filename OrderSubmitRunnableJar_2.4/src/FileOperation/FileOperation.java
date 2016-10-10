package FileOperation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import LogSystem.SystemLogger;

/**
 * 创建时间：2016-1-19 下午8:33:03 项目名称：DataQueryAcquireService_1.1 2016-1-19
 * 
 * @author 张杰
 * @version 1.0 文件名称：FileOperation.java 类说明：文件操作，主要用户获取文件元数据信息
 */
public class FileOperation {
	// 日志操作
	Logger logger = SystemLogger.getInstance().getSysLogger();
	public File file;

	public FileOperation(String filePath) {
		this.file = new File(filePath);

	}
	
	public FileOperation() {
		
	}

	// 获取文件路径
	public String getFilePath() {
		return this.file.getAbsolutePath();
	}

	// 获取文件大小：不用File.length()方法，避免大文件获取大小出错
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

	// 写文件
	public void appendMethodB(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print("输出记录失败");
			e.printStackTrace();
		}
	}
	// 写文件
	public void writeNewFile(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, false);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print("输出记录失败");
			e.printStackTrace();
		}
	}
	
	//复制文件
	public boolean customCopy(File source, File target) {  
        InputStream fis = null;  
        OutputStream fos = null;  
        try {  
            fis = new BufferedInputStream(new FileInputStream(source));  
            fos = new BufferedOutputStream(new FileOutputStream(target));  
            byte[] buf = new byte[4096];  
            int i;  
            while ((i = fis.read(buf)) != -1) {  
                fos.write(buf, 0, i);  
            }  
        }  
        catch (Exception e) {  
            e.printStackTrace(); 
            return false;
        } finally {  
            close(fis);  
            close(fos);  
        }  
        
        return true;
    }  

	

	// 执行Linux命令
	public boolean execShell(String shell) {
		Process process = null;

		try {
			process = Runtime.getRuntime().exec(shell);
			int exitVal = process.waitFor();
			BufferedReader bufferedReader = null;
			if (0 == exitVal) {
				bufferedReader = new BufferedReader(new InputStreamReader(
						process.getInputStream()));
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
			}

			// 分析返回结果
			String qsubReturnValue = null;
			if ((qsubReturnValue = bufferedReader.readLine()) != null) {
				// ("PBS提交信息：" + qsubReturnValue);
				System.out.println(qsubReturnValue);
			}
			bufferedReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (process != null) {
				close(process.getOutputStream());
				close(process.getInputStream());
				close(process.getErrorStream());
				process.destroy();
				// logger.info("process destroy sucessed!");
			}
		}
		return true;
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

	public static void main(String[] args) {
		// 数据库操作

	}

}
