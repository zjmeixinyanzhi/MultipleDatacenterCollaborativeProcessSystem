/*
 *程序名称 		: TaskExecuter.java
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
package TaskSchedular;

import FileOperations.CompressingTools;
import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import Pbs.PbsOrder;
import RSDataManage.RSData;
import TaskExeAgent.SystemConfig;
import TaskExeAgent.SystemLogger;
import TaskSchedular.Algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.xml.sax.SAXException;

import com.sun.org.apache.regexp.internal.recompile;

/**
 * @author caoyang zhangjie
 * 
 */
public class TaskExecuter {
	private L3InternalOrder l3Order;
	private Algorithm algorithm;
	private String status;
	private OrderStudio orderStudio;
	private String mainWorkPath;
	private String orderPath;
	private String orderDataPath;
	private String orderParametersPath;
	private String orderPBSPath;
	private String orderProductsPath;
	private String orderLogsPath;
	private String pbsJobId; // 作业在集群调度中对应的id编号
	private boolean parallel; // 作业是否并行
	private int priority = 1023; // 作业优先级
	private int nodes; // 作业预使用节点数 (串行作业无效)
	private int ppn; // 作业预使用每个节点的cpu数 (串行作业无效)
	private int jobStatus;
	private String qstatShell;

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public TaskExecuter(L3InternalOrder l3Order) {
		logger.info("TaskExcuter::public TaskExecuter( L3InternalOrder l3Order ) | 构造函数");
		this.l3Order = l3Order;
		this.orderStudio = new OrderStudio();
		this.orderPath = SystemConfig.getServerConfig().getOrderPath() + "/";
		this.orderDataPath = this.orderPath + this.l3Order.jobId + "/Data";
		this.orderParametersPath = this.orderPath + this.l3Order.jobId
				+ "/Parameters";
		this.orderPBSPath = this.orderPath + this.l3Order.jobId + "/PBS";
		this.orderProductsPath = this.orderPath + this.l3Order.jobId
				+ "/Products/";
		this.orderLogsPath = this.orderPath + this.l3Order.jobId + "/Logs";
		this.pbsJobId = null;
		this.qstatShell=SystemConfig.getSysPath()+"/qstat.sh";
		this.jobStatus = -1;
	}

	public TaskExecuter(L3InternalOrder l3Order, Algorithm algorithm) {
		logger.info("TaskExcuter::public TaskExecuter( L3InternalOrder l3Order, Algorithm algorithm ) | 构造函数");

		this.l3Order = l3Order;
		this.algorithm = algorithm;
		this.orderStudio = new OrderStudio();
		this.orderPath = SystemConfig.getServerConfig().getOrderPath();
		this.orderDataPath = this.orderPath + this.l3Order.jobId + "/Data";
		this.orderParametersPath = this.orderPath + this.l3Order.jobId
				+ "/Parameters";
		this.orderPBSPath = this.orderPath + this.l3Order.jobId + "/PBS";
		this.orderProductsPath = this.orderPath + this.l3Order.jobId
				+ "/Products/";
		this.orderLogsPath = this.orderPath + this.l3Order.jobId + "/Logs";
		this.pbsJobId = null;
		this.jobStatus = -1;
		this.qstatShell=SystemConfig.getSysPath()+"/qstat.sh";
	}

	public TaskExecuter() {
		// TODO Auto-generated constructor stub
		this.qstatShell=SystemConfig.getSysPath()+"/qstat.sh";
	}

	// 生成订单工作目录
	public void generateWorkDir() {
		logger.info("TaskExcuter::public void generateWorkDir() | 生成订单工作目录");

		if (null == this.l3Order) {
			return;
		}

		try {
			File dataDir = new File(this.orderDataPath);
			if (dataDir.exists()) {
				logger.info(dataDir.getPath()+"目录已经存在！");
				
			}
			else if (!dataDir.mkdirs()) {
				logger.info("<Error>Mkdir is failed. Path : "
						+ dataDir.getPath());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			logger.error("创建数据目录失败！\n" + e);
		}

		try {
			File parametersDir = new File(this.orderParametersPath);
			if (parametersDir.exists()) {
				logger.info(parametersDir.getPath()+"目录已经存在！");
				
			}
			else if (!parametersDir.mkdirs()) {
				logger.info("<Error>Mkdir is failed. Path : "
						+ parametersDir.getPath());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			logger.error("创建参数目录失败！\n" + e);
		}

		try {
			File pbsDir = new File(this.orderPBSPath);
			if(pbsDir.exists()) {
				logger.info(pbsDir.getPath()+"目录已经存在！");
				
			}
			else if  (!pbsDir.mkdirs()) {
				logger.info("<Error>Mkdir is failed. Path : "
						+ pbsDir.getPath());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			logger.error("创建PBS脚本目录失败！\n" + e);
		}

		try {
			File productsDir = new File(this.orderProductsPath);
			if (productsDir.exists()) {
				logger.info(productsDir.getPath()+"目录已经存在！");
				
			}
			else if (!productsDir.mkdirs()) {
				logger.info("<Error>Mkdir is failed. Path : "
						+ productsDir.getPath());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			logger.error("创建产品目录失败！\n" + e);
		}

		try {
			File logsDir = new File(this.orderLogsPath);
			if (logsDir.exists()) {
				logger.info(logsDir.getPath()+"目录已经存在！");
				
			}
			else if (!logsDir.mkdirs()) {
				logger.info("<Error>Mkdir is failed. Path : "
						+ logsDir.getPath());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			logger.error("创建算法执行日志目录失败！\n" + e);
		}
	}
	
	// 生成数据产品工作目录
	public boolean generateDataProductDir(String path) {
		logger.info("TaskExcuter::public void generateDataProductDir() | 生成数据产品目录");

		if (null == path || path.equals("")) {
			return false;
		}

		try {
			File dataDir = new File(path);
			if (!dataDir.mkdirs()) {
				logger.info("<Error>Mkdir is failed. Path : "
						+ dataDir.getPath());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
			logger.error("创建单个数据产品目录失败！\n" + e);
			return false;
		}
		return true;
	}
	
	

	// 生成处理单个数据订单的参数文件：输入件，PBS脚本文件,
	public String generateSingleDataOrderParasFiles(RSData data) {
		logger.info("TaskExcuter::public void generateParaFile() | 生成"
				+ data.filename + "订单参数文件");

		String strModelName = "Input";
		String strParaFilename = this.orderParametersPath + "/" + strModelName
				+ "_" + data.filename + ".xml";

		// 从字符串中分解四角坐标
		ArrayList<String> coverscope = new ArrayList<String>();
		if (!l3Order.geoCoverageStr.equals("")) {
			String[] strDataSplitArray = l3Order.geoCoverageStr.split(",");
			coverscope.addAll(Arrays.asList(strDataSplitArray));
		} else {
			coverscope.add("-1");
			coverscope.add("-1");
			coverscope.add("-1");
			coverscope.add("-1");
		}

		String startDate = "0000-00-00";
		if (null != l3Order.startDate) {
			startDate = l3Order.startDate.toString();
		}
		String endDate = "0000-00-00";
		if (null != l3Order.endDate) {
			endDate = l3Order.endDate.toString();
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Input>"
				+ "<Parameters>" + "<L1OrderId>"
				+ this.l3Order.jobId_L1
				+ "</L1OrderId>"
				+ "<L2OrderId>"
				+ this.l3Order.jobId_L2
				+ "</L2OrderId>"
				+ "<L3OrderId>"
				+ this.l3Order.jobId
				+ "</L3OrderId>"
				+ "<OrderType>"
				+ this.l3Order.orderType
				+ "</OrderType><ProductName>"
				+ this.l3Order.productName
				+ "</ProductName>"
				+ "<ULLat>"
				+ coverscope.get(3)
				+ "</ULLat><ULLong>"
				+ coverscope.get(0)
				+ "</ULLong><LRLat>"
				+ coverscope.get(1)
				+ "</LRLat><LRLong>"
				+ coverscope.get(2)
				+ "</LRLong>"
				+ "<StartDate>"
				+ startDate
				+ "</StartDate>"
				+ "<EndDate>"
				+ endDate
				+ "</EndDate>"
				+ "<nTasks>4</nTasks>" + "</Parameters>" + "<Datas>");

		// 获取绝对路径
		String obsolutePath = data.getDataUrlWithoutIP();

		// 判断是否需要解压
		if (obsolutePath.toUpperCase().endsWith("ZIP")) {
			CompressingTools zipTools = new CompressingTools();
			if (zipTools.unZipFiles(new File(obsolutePath), this.orderDataPath
					+File.separator+data.filename.replaceAll(".zip", "")+File.separator)) {
				// .zip
				if (data.filename.endsWith(".zip")) {
					data.filename = data.filename.replaceAll(".zip", "");
				}
				obsolutePath = this.orderDataPath + File.separator + data.filename;				
			}
			else {
				logger.error(obsolutePath+"解压失败！数据处理可能会失败！请检查数据文件是否损坏！");
			}	
		}
		else if (obsolutePath.toUpperCase().endsWith("TAR.GZ")) {
			//执行tar JDNI命令解压，可优化成Java方法
			File file = new File(obsolutePath.replaceAll(".tar.gz", ""));
			if(!file.exists()){
				file.mkdir();
			}
			if (execShell("tar -zxvf "+obsolutePath+" -C "+obsolutePath.replaceAll(".tar.gz", ""))) {
//				obsolutePath.replaceAll(".tar.gz", "");
				if (data.filename.endsWith(".tar.gz")) {
					data.filename = data.filename.replaceAll(".tar.gz", "");
				}
				obsolutePath = obsolutePath.replaceAll(".tar.gz", "");	
				//需要给文件的绝对路径，区分MODIS LandSat
				//需要根据具体情况处理				
			}
			else{
				logger.error(obsolutePath+"解压失败！数据处理可能会失败！请检查数据文件是否损坏！");
			}			
		}
		builder.append("<Data id='1'" + " Name=\"" + data.filename + "\" >"
				+ "<url>" + obsolutePath + "</url>" + "</Data>");
		
		builder.append("</Datas></Input>");
		String strXML = builder.toString();
		// test
		logger.info("************ xml      :" + strXML);
		StringToXMLFile(strXML, strParaFilename);

		return strParaFilename;
	}
	
	// 生成处理多个数据订单的参数文件：输入件，PBS脚本文件,
	public String generateMultipleDataOrderParasFiles(RSData rsData) {
		logger.info("TaskExcuter::public void generateParaFile() | 生成"
				+l3Order.jobId + "订单参数文件");
		
		String strParaFilename = this.orderParametersPath + "/Input"
				+ rsData.filename
				+ ".xml";
		
		// 从字符串中分解四角坐标
		ArrayList<String> coverscope = new ArrayList<String>();
		if (!l3Order.geoCoverageStr.equals("")) {
			String[] strDataSplitArray = l3Order.geoCoverageStr.split(",");
			coverscope.addAll(Arrays.asList(strDataSplitArray));
		} else {
			coverscope.add("-1");
			coverscope.add("-1");
			coverscope.add("-1");
			coverscope.add("-1");
		}

		String startDate = "0000-00-00";
		if (null != l3Order.startDate) {
			startDate = l3Order.startDate.toString();
		}
		String endDate = "0000-00-00";
		if (null != l3Order.endDate) {
			endDate = l3Order.endDate.toString();
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<Input>"
				+ "<Parameters>" + "<L1OrderId>"
				+ this.l3Order.jobId_L1
				+ "</L1OrderId>"
				+ "<L2OrderId>"
				+ this.l3Order.jobId_L2
				+ "</L2OrderId>"
				+ "<L3OrderId>"
				+ this.l3Order.jobId
				+ "</L3OrderId>"
				+ "<OrderType>"
				+ this.l3Order.orderType
				+ "</OrderType><ProductName>"
				+ this.l3Order.productName
				+ "</ProductName>"
				+ "<ULLat>"
				+ coverscope.get(3)
				+ "</ULLat><ULLong>"
				+ coverscope.get(0)
				+ "</ULLong><LRLat>"
				+ coverscope.get(1)
				+ "</LRLat><LRLong>"
				+ coverscope.get(2)
				+ "</LRLong>"
				+ "<StartDate>"
				+ startDate
				+ "</StartDate>"
				+ "<EndDate>"
				+ endDate
				+ "</EndDate>"
				+ "<nTasks>4</nTasks>" + "</Parameters>" + "<Datas>");
		
		
		String auxDatas=rsData.auxDatas;
		int count = 0;
		if (auxDatas!=null&&(!auxDatas.equals(""))) {
			String[] cpInfos=auxDatas.split("###");
			for (String path : cpInfos) {
				RSData data = new RSData();
				data.filepath=path;
				File curFile=new File(data.getDataUrlWithoutIP());
				data.filename=curFile.getName();
				
				builder.append("<Data id=\"" + (count++) + "\"" + " Name=\""
						+ data.filename + "\" >" + "<url>" +curFile.getAbsolutePath()
						+ "</url>" + "</Data>");				
			}			
		}		

		builder.append("</Datas></Input>");
		
		String strXML = builder.toString();
		// test
		logger.info("************ xml      :" + strXML);
		StringToXMLFile(strXML, strParaFilename);
		return strParaFilename;
	}
	
	

	// 提交处理单个数据订单，返回该订单对象
	public PbsOrder submitSingleDataOrder(RSData rsData) {
		PbsOrder pbsOrder = new PbsOrder();
		// 生成PBS订单输入参数文件
		String parasFile = generateSingleDataOrderParasFiles(rsData);
		if (parasFile == null || parasFile.equals("")) {
			return null;
		}
		pbsOrder.setOrderParmeterFile(parasFile);
		
		//创建并指定产品目录
		String dataProductPath=this.orderProductsPath+"/"+rsData.filename+"/";
		if (!generateDataProductDir(dataProductPath)) {
			return null;
		}
		pbsOrder.setProductDir(dataProductPath);
		
		// 指定结果文件
		pbsOrder.setResultLogFile(this.orderLogsPath + "/Result_"
				+ rsData.filename + ".log");

		// 生成PBS脚本
		String pbsFile = generatePbsShell(rsData.filename,parasFile,pbsOrder.getResultLogFile(),pbsOrder.getProductDir());
		if (pbsFile == null) {
			return null;
		}
		pbsOrder.setPbsFile(pbsFile);
		
		// 指定其他信息
		pbsOrder.setAlgorithmName(this.l3Order.algorithmName);
		pbsOrder.setAlgorithmPath(this.l3Order.algorithmPath + "/"
				+ this.l3Order.algorithmName);
		pbsOrder.setDataListPath(rsData.filepath);
		;
		pbsOrder.setEndDate(l3Order.startDate);
		pbsOrder.setStartDate(l3Order.startDate);
		pbsOrder.setPriority(this.priority);	

		// 提交PBS任务
		if (!submit(pbsFile)) {
			return null;
		}
        //更新PBS提交Id
		if (this.pbsJobId!=null) {
			pbsOrder.setPbsid(this.pbsJobId);
		}
		
		//System.out.println(this.pbsJobId);
//		System.out.println(pbsOrder.getpb);
		// 更新PBS提交时间
		pbsOrder.setSubmitDate(new java.sql.Date(System.currentTimeMillis()));
		return pbsOrder;
	}
		
	// 提交处理多个数据订单，返回该PBS订单对象
		public PbsOrder submitMultipleDataOrder(RSData rsData) {
			PbsOrder pbsOrder = new PbsOrder();
			
			// 指定结果文件
			String strRuntimeLog=this.orderLogsPath + "/Result"
					+ rsData.filename
					+ ".log";
			pbsOrder.setResultLogFile(strRuntimeLog);
			
			// 生成PBS订单输入参数文件
			// 生成参数文件		
			String parasFile = generateMultipleDataOrderParasFiles(rsData);
			if (parasFile == null || parasFile.equals("")) {
				return null;
			}
			pbsOrder.setOrderParmeterFile(parasFile);
			
			//创建并指定产品目录
			String dataProductPath=this.orderProductsPath+"/";
			if (!generateDataProductDir(dataProductPath)) {
				return null;
			}
			pbsOrder.setProductDir(dataProductPath);

			// 生成PBS脚本
			String pbsFile = generatePbsShell(rsData.filename,parasFile,pbsOrder.getResultLogFile(),pbsOrder.getProductDir());
			if (pbsFile == null) {
				return null;
			}
			pbsOrder.setPbsFile(pbsFile);
			
			// 指定其他信息
			pbsOrder.setAlgorithmName(this.l3Order.algorithmName);
			pbsOrder.setAlgorithmPath(this.l3Order.algorithmPath + "/"
					+ this.l3Order.algorithmName);
			
			
			
//			String[] strList = new String[l3Order.dataList.size()];
//			l3Order.dataList.toArray(strList);
//			pbsOrder.setDataProductList(String.join(",", strList));			
			
			pbsOrder.setEndDate(l3Order.startDate);
			pbsOrder.setStartDate(l3Order.startDate);
			pbsOrder.setPriority(this.priority);	

			// 提交PBS任务
			if (!submit(pbsFile)) {
				return null;
			}
	        //更新PBS提交Id
			if (this.pbsJobId!=null) {
				pbsOrder.setPbsid(this.pbsJobId);
			}
			
			//System.out.println(this.pbsJobId);
//			System.out.println(pbsOrder.getpb);
			// 更新PBS提交时间
			pbsOrder.setSubmitDate(new java.sql.Date(System.currentTimeMillis()));

			return pbsOrder;
		}
	
	

	// 生成单数据处理订单执行脚本
	public String generatePbsShell(String dataName,String inputParaFile,String resultLogFile,String productPath) {
		logger.info("TaskExcuter::public void generatePbsShell() | 生成订单执行脚本");

		String shFile = this.orderPBSPath + "/" + this.l3Order.algorithmName
				+ "_" + dataName + ".sh";

		// 生成执行pbs作业的sh脚本
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(shFile)));
			writer.write("# !/bin/sh"); //
			writer.newLine();
			writer.write("#PBS -p " + priority); //
			// 任务优先级，整数，[-1024，1023]，若无定义则为0.
			writer.newLine();
			// writer.write("#PBS -j oe");
			// writer.newLine();
			writer.write("#PBS -o " + this.orderLogsPath); // 重定向标准输出到path.
			writer.newLine();
			writer.write("#PBS -e " + this.orderLogsPath); // 将标准错误信息重定向到path.
			writer.newLine();
			// writer.write("#PBS -q " + taskQueue); //-q destination ：
			// destination有三种形式： queue , @server,queue@server
			// writer.newLine();
			writer.write("#PBS -l walltime=00:30:00"); // 最长运行时间
			writer.newLine();
			// writer.newLine();

			// 并行作业
			if (this.parallel) {
				writer.write("#PBS -l nodes=" + this.nodes + ":ppn=" + this.ppn);
				writer.newLine();
				writer.write("cd $PBS_O_WORKDIR"); // @@@ //qsub提交的作业的绝对路径
				writer.newLine();
				writer.write("NP=`cat $PBS_NODEFILE|wc -l`"); // @@@
																// //$PBS_NODEFILE，这个环境变量表示由pbs自动分配给作业的节点列表；节点数为命令行中指定的进程数。
				writer.newLine();
				// writer.write( "NPPROGS=$(wc -l < $PBS_NODEFILE)" );
				// writer.newLine();
				String sh = this.l3Order.algorithmPath + "/"
						+ this.l3Order.algorithmName + " " + this.l3Order.jobId;
				writer.write("mpirun -r rsh -f /home/DCA/mpd.hosts -machinefile /home/DCA/mpd.hosts -np $NP -env I_MPI_DEVICE sock "
						+ sh); // @@@
			} else {
				writer.write("#PBS -l nodes=1:ppn=1");
				writer.newLine();
				writer.write("chmod +x "
						+ SystemConfig.getServerConfig().getAlgorithPath()
						+ "/" + this.l3Order.algorithmPath);
				writer.newLine();
				
				//判断MODIS的RN处理算法,指定四个参数
				if (this.l3Order.orderType.equals("L3RN")&& (dataName.startsWith("MOD02")||dataName.startsWith("MYD02"))) {
					writer.write(SystemConfig.getServerConfig().getAlgorithPath()
							+ "/" + this.l3Order.algorithmPath + " "
							+ inputParaFile + " "+productPath +" "
							+ resultLogFile+" "+SystemConfig.getServerConfig().getAlgorithPath());
					writer.newLine();					
				}
				else {
					writer.write(SystemConfig.getServerConfig().getAlgorithPath()
							+ "/" + this.l3Order.algorithmPath + " "
							+ inputParaFile + " "+productPath +" "
							+ resultLogFile);
					writer.newLine();					
				}				
			}
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("生成" + dataName + "订单的PBS脚本错误");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("生成" + dataName + "订单的PBS脚本错误");
			return null;
		}

		return shFile;
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

	// 更新订单状态 从PBS获取状态
	// 作业对应状态代码 1：正确完成；2：错误完成；3:作业超时；-1：作业正在运行
	public void status() {
		logger.info("TaskExcuter::public void status() | 更新订单状态");

		String errorFile = this.orderPBSPath + ".ER";

		File file = new File(errorFile);
		if (file.exists()) {
			if (file.length() == 0) {
				this.jobStatus = 1;
			} else {
				this.jobStatus = 2;

				BufferedReader br = null;
				try { // 判断是否为超时导致的出错
					br = new BufferedReader(new InputStreamReader(
							new FileInputStream(errorFile)));
					String content = br.readLine();
					if (content.contains("walltime")) {
						this.jobStatus = 3;
					}
					br.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
				logger.info("PBS提交信息：" + qsubReturnValue);
//				System.out.println(qsubReturnValue);
				if (Character.isDigit(qsubReturnValue.charAt(0))) {
					this.pbsJobId = qsubReturnValue;
					//test
//					System.out.println("Set PBS Id");
				} else {
					this.pbsJobId = null;
				}
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
				close(process.getOutputStream());
				close(process.getInputStream());
				close(process.getErrorStream());
				process.destroy();
				// logger.info("process destroy sucessed!");
			}
		}
		return true;
	}
	
	// 执行Linux命令,返回输出文件流
	public String execShellFeedbackOutputStream(String shell) {
		Process process = null;
		//test
		//System.out.println(shell);
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
			String returnValue = null;
			if ((returnValue = bufferedReader.readLine()) != null) {
				logger.info("Linux命令执行信息：" + returnValue);
				return returnValue;
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (process != null) {
				close(process.getOutputStream());
				close(process.getInputStream());
				close(process.getErrorStream());
				process.destroy();
				// logger.info("process destroy sucessed!");
			}
		}
		return null;
	}
	
	

	// 提交订单执行脚本
	public boolean submit(String shFile) {
		logger.info("TaskExcuter::public void submit() | 提交订单执行脚本");

		String command = "chmod +x " + shFile;
		if (!execShell(command)) {
			logger.error("PBS腳本增加執行權限失敗！");
			return false;
		}
		command = SystemConfig.getServerConfig().getQsubPath() + " " + shFile;
		logger.info(command);
		if (!execShell(command)) {
			logger.error(command + "执行失败！");
			return false;
		}
		return true;
	}

	// 数据产品上传缓存区
	public boolean uploadToBuffer() {
		logger.info("TaskExcuter::public boolean uploadToBuffer() | 数据产品上传缓存区");
		return true;
	}

	// get AlgorithmResult
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
			return null;
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
	
	//判断是否运行结束 
	public String isTerminate(String pbsId) {
		//先判断是否有结果
		// qstat 822.IOServer-SSD|grep  822.IOServer-SSD |awk '{print $5}'
//		String command="/usr/local/bin/qstat "+pbsId+" | grep "+pbsId+" |awk '{print $5}'";
		String command = "chmod +x " + this.qstatShell;
		if (!execShell(command)) {
			logger.error("PBS腳本qstat.sh增加執行權限失敗！");
			return null;
		}		
		command=this.qstatShell + " "+pbsId;		
		String qsatPbsId=execShellFeedbackOutputStream(command);
		if (qsatPbsId!=null) {
			qsatPbsId=qsatPbsId.toUpperCase();	
		}
		else {
			qsatPbsId="Unknown";
		}
		
		
		return qsatPbsId;
	}
	
	//获取PBS订单产品信息
	public ArrayList<String> getProductsInfo(String productsFile){
	
		FileOperation operation = new FileOperation();
		// update product info
		String products = operation.readFileByLines(productsFile);

		ArrayList<String> strDataProductList = new ArrayList<String>();
		try {
			strDataProductList = getRSDataList(products, "//Output/Datas");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		return strDataProductList;
	}
	
	

	// 
	public String getStatus() {

		// test
		this.jobStatus = 1;

		String l3OrderStatus = getAlgorithmResult(this.orderLogsPath
				+ "/Result.log","Result");
//		System.out.println(this.orderLogsPath + "/Result.log");
		// switch( this.jobStatus ){
		// case 1:
		// l3OrderStatus = "Finish";
		// break;
		// case
		// }
		// if (1 == this.jobStatus) {
		// l3OrderStatus = "Finish";
		// } else {
		// l3OrderStatus = "Error";
		// }
		if (l3OrderStatus.equals("Success")) {
			l3OrderStatus = "Finish";
			FileOperation operation = new FileOperation();
			// update product info
			String products = operation.readFileByLines(this.orderProductsPath
					+ "/" + "ProductLists.xml");

			ArrayList<String> strDataProductList = new ArrayList<String>();
			try {
				strDataProductList = getRSDataList(products, "//Output/Datas");
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			orderStudio.setL3OrderDataProductList(l3Order.jobId,
					strDataProductList);
		}

		// 设置三级订单状态
		logger.info(">>" + l3OrderStatus);
		orderStudio.setOrderWorkflowStatus(l3Order.jobId, l3OrderStatus); // @@@

		return l3OrderStatus;
	}
	
	//
	

	private ArrayList<String> getRSDataList(String strXML, String strElementPath)
			throws DocumentException {
		ArrayList<String> dataList = new ArrayList<String>();
		try {
			Document dom = DocumentHelper.parseText(strXML);
			List list = dom.selectNodes(strElementPath);
			Iterator iter = list.iterator();
			while (iter.hasNext()) {
				List list_datas = ((Element) iter.next()).elements();
				Iterator iter_datas = list_datas.iterator();
				while (iter_datas.hasNext()) {
					String strData = "";
					Element element_data = (Element) iter_datas.next();
					List list_data = element_data.elements();

					// System.out.println( "name:" + element_data.getName() );
					// System.out.println( "id  :" +
					// element_data.attributeValue( "id" ) );
					// System.out.println( "name:" +
					// element_data.attributeValue( "Name" ) );

					strData += "Name=" + element_data.attributeValue("Name");
					// strData += "url=" + element_data.attributeValue("url");
					Iterator iter_dataelement = list_data.iterator();
					while (iter_dataelement.hasNext()) {
						strData += ",";
						Element element_dataelement = (Element) iter_dataelement
								.next();
						// System.out.println( element_dataelement.getName() +
						// "="
						// + element_dataelement.getStringValue() );
						strData += element_dataelement.getName() + "="
								+ element_dataelement.getStringValue();
					}
					dataList.add(strData);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out
					.println("<Error>ServiceImplSoapBindingImpl::getDataList | Element path is invalid.");
			return new ArrayList<String>();
		}
		return dataList;
	}

	// 获取数据产品
	public String getProduct() {
		return "ftp://product";
	}

	// 生成xml文件
	private boolean StringToXMLFile(String strXML, String strXMLFilePath) {

		try {
			Document doc_modelconfig_xml = DocumentHelper.parseText(strXML);

			OutputFormat format = OutputFormat.createPrettyPrint(); // 缩减型格式
			format.setEncoding("UTF-8"); // 设置文件内部文字的编码
			// format.setExpandEmptyElements( true );
			// format.setTrimText( false );
			format.setIndent(true); // 设置是否缩进
			// format.setIndent( "   " ); // 以空格方式实现缩进
			// format.setNewlines( true ); // 设置是否换行

			XMLWriter writer = new XMLWriter(new FileOutputStream(new File(
					strXMLFilePath)), format); // "路径+文件名.xml"
			// 还有另外一种写法，是在创建OutputStreamWriter的时候设置一个encoding，再用这个类对象创建XMLWriter类对象，这个时候再使用format，不知道有什么特殊的作用。
			// String encoding = "UTF-8";//设置文件的编码！！和format不是一回事
			// OutputStreamWriter outstream = new OutputStreamWriter(new
			// FileOutputStream(filename), encoding);
			// logger.info("filename:\t"+filename);
			// XMLWriter writer = new XMLWriter(outstream,format);

			writer.write(doc_modelconfig_xml);
			writer.close();

		} catch (DocumentException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return true;
	}
	
	public static void main(String[] args) {
//		String qsubReturnValue="136.IOServer-SSD";
//		String command="/home.bak/MCA/zjDir/test.sh "+"933";
//		
		TaskExecuter executer=new TaskExecuter();
//		executer.generateSingleDataOrderParasFiles(data);
//		System.out.println(executer.qstatShell);
//		System.out.println(executer.execShellFeedbackOutputStream(command));
		
//		if (Character.isDigit(qsubReturnValue.charAt(0))) {
//			System.out.println( qsubReturnValue);
//		} else {
//			System.out.println(qsubReturnValue+"bdui");
//		}
		
	}
	
}
