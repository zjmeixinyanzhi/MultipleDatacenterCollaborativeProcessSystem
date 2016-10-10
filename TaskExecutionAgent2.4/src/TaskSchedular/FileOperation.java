package TaskSchedular;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.Properties;

public class FileOperation {

	// 读取文件到字符串
	public String readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		try {
			// System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				builder.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return builder.toString();
	}

	public void generatePbsShell(String shFile) {
		// 生成执行pbs作业的sh脚本
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(shFile)));
			//writer.write("#!/bin/bash");
			//writer.newLine();
			writer.write("#PBS -p " + 100); // 任务优先级，整数，[-1024，1023]，若无定义则为0.
			writer.newLine();
			// writer.write("#PBS -q " + taskQueue); //-q destination ：
			// destination有三种形式： queue , @server,queue@server
			//
			writer.write("#PBS -l walltime=00:25:00"); // 最长运行时间
			writer.newLine();
			// writer.newLine();
			writer.write("#PBS -l nodes=1:ppn=1");
			writer.newLine();
			writer.write("/home/user863/AlgorithRepository/GN_TM_ETM.sh ");
			// writer.newLine();
			writer.write("/home/user863/mu01/SCCPS/Orders/L3GN201511220001@master/Parameters/L3GN.xml ");
			// writer.newLine();
			writer.write("/home/user863/mu01/SCCPS/Orders/L3GN201511220001@master/Products/ ");
			// writer.newLine();
			writer.write("/home/user863/mu01/SCCPS/Orders/L3GN201511220001@master/Logs/Result.log");

			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 写文件
	public static void appendMethodB(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 读取属性文件
	public void updateStatus(String resultFile,boolean flag) {
		// 读取配置文件
		//System.out.println(">>" + resultFile);
		Properties prop = new Properties();
		OutputStream out = null;
		try {
			FileOutputStream fos = new FileOutputStream(resultFile);
			prop.setProperty("Result",flag==true?"Success":"Error");
			prop.setProperty("EndTime", new java.util.Date(System.currentTimeMillis()).toString());
			prop.setProperty("Result", "Success");
			prop.setProperty("EndTime",
					new Date(System.currentTimeMillis()).toString());
			prop.store(fos, "");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	// 文件移动
	public void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);

	}

	// 文件删除
	public void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}

	// 文件复制

	public boolean copyFile(String oldPath, String newPath) {
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				// int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					// bytesum += byteread; //字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Linux命令
	public boolean excuteShell(String command) throws IOException {

		//command="chmod +x "+command+" && "+command;
		// command="chmod +x "+command+" && "+command;
		Process process = null;

		try {
			process = Runtime.getRuntime().exec(command);
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
				System.out.println(qsubReturnValue);
			}

			bufferedReader.close();
			// process.getInputStream().close();//关闭process相关流文件
			// process.getOutputStream().close();
			// process.getErrorStream().close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (process != null) {
				process.getOutputStream().close();
				(process.getInputStream()).close();
				(process.getErrorStream()).close();
				process.destroy();
				// logger.info("process destroy sucessed!");
			}
		}

		return true;

	}
	
	// 提交订单执行脚本
	// public void submit() {
	// logger.info("TaskExcuter::public void submit() | 提交订单执行脚本");
	//
	// String command = "qsub ";
	//
	// command += this.shFile;
	//
	// Process process = null;
	//
	// try {
	// process = Runtime.getRuntime().exec(command);
	// int exitVal = process.waitFor();
	// BufferedReader bufferedReader = null;
	// if (0 == exitVal) {
	// bufferedReader = new BufferedReader(new InputStreamReader(
	// process.getInputStream()));
	// } else {
	// bufferedReader = new BufferedReader(new InputStreamReader(
	// process.getErrorStream()));
	// }
	//
	// // 分析返回结果
	// String qsubReturnValue = null;
	// if ((qsubReturnValue = bufferedReader.readLine()) != null) {
	// if (Character.isDigit(qsubReturnValue.charAt(0))) {
	// this.pbsJobId = qsubReturnValue;
	// } else {
	// this.pbsJobId = null;
	// }
	// }
	//
	// bufferedReader.close();
	// // process.getInputStream().close();//关闭process相关流文件
	// // process.getOutputStream().close();
	// // process.getErrorStream().close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// } finally {
	// if (process != null) {
	// close(process.getOutputStream());
	// close(process.getInputStream());
	// close(process.getErrorStream());
	// process.destroy();
	// // logger.info("process destroy sucessed!");
	// }
	// }
	// }
	

	public static void main(String[] args) {
		FileOperation operation = new FileOperation();

		// 命令脚本
		// String shell = "/home.bak/DCA/zjPackage/test.sh";
		String shell = "/home/user863/AlgorithRepository/PBS/test.sh";
		String content = "#!/bin/sh\n  ";
		operation.generatePbsShell(shell);

		String pbsShell = "qsub " + shell;
		System.out.println(pbsShell);

		try {
			if (operation.excuteShell("chmod +x " + shell)
					&& operation.excuteShell(pbsShell)) {
				System.out.println("命令执行成功！");
			}
			;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
