/*
 *程序名称 		: SystemResource.java
 *版权说明  		:
 *版本号		    : 1.0
 *功能			: 
 *开发人		    : caoyang & liuning
 *开发时间		: 2014-05-19
 *修改者		    : 
 *修改时间		: 
 *修改简要说明 	:
 *其他			:	 
 */
package SystemManage;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import SystemManage.SystemLogger;

import org.apache.log4j.Logger;

import javafx.scene.input.DataFormat;
import DBManage.DataCenter;
import DBManage.SystemResourceDB;
import DBManage.TestDBConnection;
import FileOperation.TimeConsumeCount;

/**
 * @author caoyang
 *
 */
public class SystemResource {
	// 用来存放数据中心及可用状态列表
	// !!!private Hashtable dataCenterList;
	private Hashtable<String, ServerConfig> dataCenterList;
	// 用来存放生产分系统及可用状态列表
	// ###!!!private Hashtable processingSystemList;
	// 用来记录共享存储空间的占用率
	private float publicBufferUsage;
	private SystemResourceDB systemResourceDB;

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public SystemResource() {
		this.systemResourceDB = new SystemResourceDB();
	}

	// 对所有系统资源及其状态进行更新
	public void update() {
		System.out
				.println("SystemResource::public void update() | 对所有系统资源及其状态进行更新");
		// ###//更新各个数据中心状态
		// ###for( :i: ){
		// ### //测试数据中心连接或可用状态
		// ### String name = ;
		// ### DataCenter dataCenter = ;
		// ### this.dataCenterList.put(name,dataCenter);
		// ###}
		// ###for(:j:){
		// ### //测试生产分系统可用状态
		// ### String processSystem = ;
		// ### String status = ;
		// ### this.processingSystemList.put(processSystem,status);
		// ###}
		// ###//获取缓存区空间使用率
		// ###this.publicBufferUsage = ;
		// ###//将获取的系统资源状态存入数据库
		// ###SystemResourceDB sysResDB = new SystemResourceDB(
		// SystemConfig.getSysResrouceDBConfig );
		// ###sysResDB.update( this );
		String strCmdFilepath = SystemConfig.getSysPath()
				+ "/build-area/getstatus.sh";
		
		// String strCmdFilepath ="/home/MCA/zjDir/getstatus.sh";
		try {
			Process process = Runtime.getRuntime().exec(
					"chmod 755 " + strCmdFilepath);
			int exitVal = process.waitFor();
			if (0 == exitVal) {
				// System.out.println(
				// "To enhance the authority of success. | Filepath : " +
				// strCmdFilepath );
			} else {
				System.out
						.println("Failed to enhance the authority. | Filepath : "
								+ strCmdFilepath);
			}
		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// String [] nodenameList = { "node01", "node02", "ioserver-ssd" };
		// 待修改：此处监控节点需要在从资源管理systemResourcedb数据库中动态获取，并根据resource名称和ip拼接得到字符串“node01:10.3.10.27”
		ArrayList<DataCenter> dataCenterLists = this.systemResourceDB.search(
				" ", -1);
		
//		System.out.println(dataCenterLists.size());
		

		if (null == dataCenterLists) {
			logger.info("数据中心列表为空：Null！");
		}

		for (Iterator iterator = dataCenterLists.iterator(); iterator.hasNext();) {
			DataCenter dataCenter = (DataCenter) iterator.next();

			String nodename = dataCenter.getHostName();
			String nodeip = dataCenter.getHostIp();
			// String nodename = nodenameList[ i ];
			// String [] nameip = nodenameList[ i ].split( ":" );
			// String command = "/home/MCA/getstatus.sh " + nodename;
			// String command = SystemConfig.getSysPath() + "/getstatus.sh " +
			// nodename;
			String strPara = nodename;
			boolean isFinished = false;
			// String command = strCmdFilepath + " " + nodename;
			String command = strCmdFilepath + " " + strPara;
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
				//
				// 分析返回结果
				HashMap<String, ArrayList<String>> infoList = new HashMap<String, ArrayList<String>>();
				ArrayList<String> value = new ArrayList<String>();
				String cmdReturnValue = null;
				String title = null;
				// 读取一栏消息
				while ((cmdReturnValue = bufferedReader.readLine()) != null) {
					if (cmdReturnValue.contains("ERROR")) {
						strPara = nodeip;
						// logger.info(">>"+cmdReturnValue);
					}

					if (cmdReturnValue.contains("<")) {
						// System.out.println( "< found. | cmdReturnValue = " +
						// cmdReturnValue );
						if ((null != title) && (!value.isEmpty())) {
							// System.out.println( "put to HashMap." );
							infoList.put(title, value);
							value = new ArrayList<String>();
						}
						title = cmdReturnValue;
					} else if (cmdReturnValue.contains("=")) {
						// System.out.println( "= found. | cmdReturnValue = " +
						// cmdReturnValue );
						value.add(cmdReturnValue);
					}
				}

				bufferedReader.close();

				if (!value.isEmpty()) {
					infoList.put(title, value);

				}

				
				// 信息找不到，数据中心不可
				if (infoList.isEmpty()) {
					dataCenter.setIsWorking(false);
					systemResourceDB.update(dataCenter);
					continue;
				}

				double cpuuser = 0;
				value = infoList.get("<CPU_USER>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					// System.out.println("<CPU_USER>");
					// System.out.println( "Last Update Time | " +
					// Second2Date(kvTime[ 1 ].trim())+" "+" Value | "+kvInfo[ 1
					// ].trim().replaceAll( "\"", "" ) );

					cpuuser = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
				}

				double memtotal = 0;
				double memfree = 0;
				value = infoList.get("<MEM_TOTAL>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");

					// System.out.println("<MEM_TOTAL>");
					// System.out.println( "Last Update Time | " +
					// Second2Date(kvTime[ 1 ].trim())+" "+" Value | "+kvInfo[ 1
					// ].trim().replaceAll( "\"", "" ) );

					memtotal = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
				}

				value = infoList.get("<MEM_FREE>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					// System.out.println("<MEM_FREE>");
					// System.out.println( "Last Update Time | " +
					// Second2Date(kvTime[ 1 ].trim())+" "+" Value | "+kvInfo[ 1
					// ].trim().replaceAll( "\"", "" ) );

					memfree = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
				}

				double disktotal = 0;
				double diskfree = 0;
				value = infoList.get("<DISK_TOTAL>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					// System.out.println("<DISK_TOTAL>");
					// System.out.println( "Last Update Time | " +
					// Second2Date(kvTime[ 1 ].trim())+" "+" Value | "+kvInfo[ 1
					// ].trim().replaceAll( "\"", "" ) );

					disktotal = Double.valueOf(kvInfo[1].trim().replaceAll(
							"\"", ""));
				}

				value = infoList.get("<DISK_FREE>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					// System.out.println("<DISK_FREE>");
					// System.out.println( "Last Update Time | " +
					// Second2Date(kvTime[ 1 ].trim())+" "+" Value | "+kvInfo[ 1
					// ].trim().replaceAll( "\"", "" ) );

					diskfree = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
				}

				double load = 0;
				value = infoList.get("<LOAD>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					// System.out.println("<LOAD>");
					// System.out.println( "Last Update Time | " +
					// Second2Date(kvTime[ 1 ].trim())+" "+" Value | "+kvInfo[ 1
					// ].trim().replaceAll( "\"", "" ) );

					load = Double
							.valueOf(kvInfo[1].trim().replaceAll("\"", ""));
				}

				double io = 0;
				value = infoList.get("<IO>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					// System.out.println("<IO>");
					// System.out.println( "Last Update Time | " +
					// Second2Date(kvTime[ 1 ].trim())+" "+" Value | "+kvInfo[ 1
					// ].trim().replaceAll( "\"", "" ) );

					//
					io = Double.valueOf(kvInfo[1].trim().replaceAll("\"", ""));
				}

				DecimalFormat dFormat = new DecimalFormat("#.00");
				double mempercent = (memtotal - memfree) / memtotal * 100.0;
				double diskpercent = (disktotal - diskfree) / disktotal * 100.0;

				// // System.out.println( "mempercent : " + dFormat.format(
				// mempercent ) );
				// // System.out.println( "diskpercent: " + dFormat.format(
				// diskpercent ) );

				dataCenter.setHostName(nodename);
				dataCenter.setHostIp(nodeip);
				dataCenter.setIsWorking(true);
				dataCenter.setCPU(cpuuser);
				dataCenter.setMemory(mempercent);
				dataCenter.setIO(io);
				dataCenter.setDiskUsage(diskpercent);
				dataCenter.setLoadOne(load);

				// save last Time
				String[] kvTime = null;
				value = infoList.get("<CPU_USER>");
				if ((null != value) && (value.size() == 2)) {
					kvTime = value.get(0).split("=");
				}

				String s1 = Second2Date(kvTime[1].trim());
				dataCenter.setLastUpdateTime(s1);

				TimeConsumeCount timeTool = new TimeConsumeCount();
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				timeTool.setStartTime(dateFormat.parse(s1));
				System.out.println(dateFormat.parse(s1));
				timeTool.setEndTimeByCurrentTime();
				// 判断数据中心是否在监控中:30s
				// logger.info("当前监控的计算节点："+nodeip+" 上次心跳间隔："+timeTool.getTimeSpan());
				if (timeTool.getTimeSpan() > 1000 * 60) {
					dataCenter.setIsWorking(false);
//					logger.info(nodeip + "尚无监控信息！");
				} else {
					dataCenter.setIsWorking(true);
				}

				isFinished = this.systemResourceDB.update(dataCenter);
				if (isFinished) {
//					logger.info(nodeip + "信息更新成功！");
				}

			} catch (IOException e) {
				logger.error(e);
			} catch (InterruptedException e) {
				logger.error(e);
			} catch (Exception e) {
				logger.error(e);
			} finally {
				if (process != null) {
					close(process.getOutputStream());
					close(process.getInputStream());
					close(process.getErrorStream());
					process.destroy();
				}
			}
		}
	}

	// 提供给其他系统的监控信息
	public String getSystemMonitorInfo() {
		Date date1 = new Date();
		SimpleDateFormat dFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formate_time = dFormat1.format(date1);
		long time_s = date1.getTime();

		String strCmdFilepath = SystemConfig.getSysPath() + "/build-area/getstatus.sh";
		// String strCmdFilepath ="/home/MCA/package/ln/getstatus.sh";
		try {
			Process process = Runtime.getRuntime().exec(
					"chmod 755 " + strCmdFilepath);
			int exitVal = process.waitFor();
			if (0 == exitVal) {
				// System.out.println(
				// "To enhance the authority of success. | Filepath : " +
				// strCmdFilepath );
			} else {
				System.out
						.println("Failed to enhance the authority. | Filepath : "
								+ strCmdFilepath);
			}
		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// String [] nodenameList = { "node01", "node02", "ioserver-ssd" };
		// 待修改：此处监控节点需要在从资源管理systemResourcedb数据库中动态获取，并根据resource名称和ip拼接得到字符串“node01:10.3.10.27”
		// 从资源列表中加载数据中心信息
		// systemResourceDB
		ArrayList<DataCenter> dataCenterLists = this.systemResourceDB.search(
				" ", -1);

		if (null == dataCenterLists) {
			logger.info("数据中心列表为空：Null！");
		}
		String strResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<SystemMonitorInfo>";
		int i = 0;
		for (Iterator iterator = dataCenterLists.iterator(); iterator.hasNext();) {
			DataCenter dataCenter = (DataCenter) iterator.next();

			String nodename = dataCenter.getHostName();
			String nodeip = dataCenter.getHostIp();

			strResult += "<Node id=\"" + (i + 1) + "\">" + "<Name>" + nodename
					+ "</Name>" + "<IP>" + nodeip + "</IP>";
			i++;
			String strPara = nodename;
			boolean isFinished = false;
			int iLoopIndex = 0;
			isFinished = false;
			// String command = strCmdFilepath + " " + nodename;
			String command = strCmdFilepath + " " + strPara;
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
				HashMap<String, ArrayList<String>> infoList = new HashMap<String, ArrayList<String>>();
				ArrayList<String> value = new ArrayList<String>();
				String cmdReturnValue = null;
				String title = null;
				while ((cmdReturnValue = bufferedReader.readLine()) != null) {
					// System.out.println( cmdReturnValue );

					if (cmdReturnValue.contains("ERROR")) {
						strPara = nodeip;
					}

					if (cmdReturnValue.contains("<")) {
						// System.out.println(
						// "< found. | cmdReturnValue = " + cmdReturnValue
						// );
						if ((null != title) && (!value.isEmpty())) {
							// System.out.println( "put to HashMap." );
							infoList.put(title, value);
							value = new ArrayList<String>();
						}
						title = cmdReturnValue;
					} else if (cmdReturnValue.contains("=")) {
						// System.out.println(
						// "= found. | cmdReturnValue = " + cmdReturnValue
						// );
						value.add(cmdReturnValue);
					}
				}

				bufferedReader.close();

				if (!value.isEmpty()) {
					infoList.put(title, value);
				}

				if (infoList.isEmpty()) {
					//
					strResult += "<Status>NotAvaliable</Status>";
					strResult += "</Node>";
					continue;
				}

				double cpuuser = 0;
				value = infoList.get("<CPU_USER>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");

					strResult += "<Update_Time>"
							+ this.Second2Date(kvTime[1].trim())
							+ "</Update_Time>";

					cpuuser = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
					strResult += "<CPU_USER>" + cpuuser + "</CPU_USER>";
					
					TimeConsumeCount timeTool = new TimeConsumeCount();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					timeTool.setStartTime(dateFormat.parse(this.Second2Date(kvTime[1].trim())));
					timeTool.setEndTimeByCurrentTime();
					// 判断数据中心是否在监控中:30s
					// logger.info("当前监控的计算节点："+nodeip+" 上次心跳间隔："+timeTool.getTimeSpan());
					if (timeTool.getTimeSpan() > 1000 * 60) {
						strResult += "<Status>NotAvaliable</Status>";
					} else {
						strResult += "<Status>Avaliable</Status>";
					}
					
				}

				double memtotal = 0;
				double memfree = 0;
				value = infoList.get("<MEM_TOTAL>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");

					memtotal = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
					strResult += "<MEM_TOTAL>" + memtotal + "</MEM_TOTAL>";
				}

				value = infoList.get("<MEM_FREE>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					memfree = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
					strResult += "<MEM_FREE>" + memfree + "</MEM_FREE>";
				}

				double disktotal = 0;
				double diskfree = 0;
				value = infoList.get("<DISK_TOTAL>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");
					disktotal = Double.valueOf(kvInfo[1].trim().replaceAll(
							"\"", ""));
					strResult += "<DISK_TOTAL>" + disktotal + "</DISK_TOTAL>";
				}

				value = infoList.get("<DISK_FREE>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");

					diskfree = Double.valueOf(kvInfo[1].trim().replaceAll("\"",
							""));
					strResult += "<DISK_FREE>" + diskfree + "</DISK_FREE>";
				}

				double load = 0;
				value = infoList.get("<LOAD>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");

					load = Double
							.valueOf(kvInfo[1].trim().replaceAll("\"", ""));
					strResult += "<LOAD>" + load + "</LOAD>";
				}

				double io = 0;
				value = infoList.get("<IO>");
				if ((null != value) && (value.size() == 2)) {
					String[] kvTime = value.get(0).split("=");
					String[] kvInfo = value.get(1).split("=");

					io = Double.valueOf(kvInfo[1].trim().replaceAll("\"", ""));
					strResult += "<IO>" + io + "</IO>";
				}

				isFinished = true;

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (process != null) {
					process.destroy();
				}
			}
			

			strResult += "</Node>";

		}

		strResult += "</SystemMonitorInfo>";

		return strResult;

	}

	public String Second2Date(String second) {
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time_s1 = Long.parseLong(second) * 1000;
		return dFormat.format(time_s1);
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
	// 获取资源状态
	public DataCenter getDataCenterStatus(String centerName) {
		System.out
				.println("SystemResource::public DataCenter getDataCenterStatus(String centerName) | 获取资源状态");
		return null;
	}

	public boolean getProductionSystemStatus(String systemName) {
		System.out
				.println("SystemResource::public boolean getProductionSystemStatus(String systemName) | 获取资源状态");
		return true;
	}

	public float getPulicBufferUsage() {
		System.out
				.println("SystemResource::public float getPulicBufferUsage() | 获取资源状态");
		return 0;
	}
	public static void main(String[] args) {
		TestDBConnection test=new TestDBConnection();
		test.GetConnection();
		
		SystemResource resource=new SystemResource();
		System.out.println(resource.getSystemMonitorInfo());
		
		
		
	}

}
