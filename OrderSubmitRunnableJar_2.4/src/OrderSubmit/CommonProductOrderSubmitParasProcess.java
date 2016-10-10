package OrderSubmit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import DBSystem.AlgorithmDB;
import DBSystem.DBConn;
import DBSystem.L3OrderDB;
import DBSystem.SystemResourceDB;
import FileOperation.FileOperation;
import LogSystem.SystemLogger;
import OrderManage.L3InternalOrder;
import RSDataManage.RSData;
import ResourceManage.Algorithm;
import ResourceManage.DataCenter;

/**
 * 创建时间：2015-8-16 下午5:09:43 项目名称：OrderSubmitRunnableJar 2015-8-16
 * 
 * @author 张杰
 * @version 1.0 文件名称：NormalizeOrderSubmitParasProcess.java 类说明：RN、GN归一化订单参数处理
 */
public class CommonProductOrderSubmitParasProcess extends
		OrderSubmitParasProcess {
	// 三级订单
	public L3InternalOrder l3order = null;

	// 所需Data
	public ArrayList<RSData> DataList = null;

	// log4j日志系统
	private Logger logger = SystemLogger.getInstance().getSysLogger();
	//标准产品公共缓存区本地地址
	public String standardProductsPublicBufferLocalPath;
	private File standardProductPath;
	//共性产品公共缓存区本地地址
	public String commonProductsPublicBufferLocalPath;
	private File commonProductPath;
	//FTP外网IP
	public String publicFTPIPAddress;
	//FTP帐号密码
	public String ftpUsrPw;
	//文件操作工具
	private FileOperation fileOperation;
	
	

	public CommonProductOrderSubmitParasProcess(L3InternalOrder l3order) {
		this.l3order = l3order;
		this.DataList = new ArrayList<RSData>();
		this.fileOperation= new FileOperation();;
	}

	//
	public boolean doProcess() {
		
		this.commonProductsPublicBufferLocalPath="/dataIO/863_Project/863-Daemon/MCAPublicBuffer/CommonProductBuffer/";
		this.standardProductsPublicBufferLocalPath="/dataIO/863_Project/863-Daemon/MCAPublicBuffer/StandardProductBuffer/";
		//创建标准产品文件夹
		this.standardProductPath=new File(this.standardProductsPublicBufferLocalPath+ l3order.jobId
				+ "/");
		if (!standardProductPath.exists()) {
			standardProductPath.mkdirs();
		}	
		if (fileOperation.execShell("chmod 777 -R "+ standardProductPath.getAbsolutePath())) {
			System.out.println("FTP目录增加权限成功！");
		}
		
	
		//创建共性产品文件夹
		//自动从系统配置中读取
		this.commonProductPath=new File(this.commonProductsPublicBufferLocalPath
				+ l3order.jobId
				+ "/");
		if (!commonProductPath.exists()) {
			commonProductPath.mkdirs();
		}			
		//增加权限
		if (fileOperation.execShell("chmod 777 -R "+ commonProductPath.getAbsolutePath())) {
			System.out.println("FTP目录增加权限成功！");
		}
		
		//获取上级订单号
		//准备数据
		L3InternalOrder preL3InternalOrder=getPreL3Order(l3order);
		if (preL3InternalOrder==null||preL3InternalOrder.strDataProductList==null) {
			return false;
		}
		System.out.println(this.l3order.jobId+"的上级订单"+preL3InternalOrder.jobId);
		this.l3order.dataList=preL3InternalOrder.strDataProductList;
		
		// 根据订单中数据类型，确定处理算法
		// 遍历数据，确定每个数据所对应的算法名称、算法路径
		Iterator<String> it_datalist = l3order.dataList.iterator();
		while (it_datalist.hasNext()) {
			String datainfo = (String) it_datalist.next();
			//test
//			System.out.println(datainfo);			
			this.DataList.add(getData(datainfo));
		}

		// 根据订单信息确定webservice提交参数
		if (!getXMLParas()) {
			System.out.println("<Error>组合提交参数错误！");
			return false;
		}

		return true;
	}

	// 获取数据对象
	private RSData getData(String dataInfo) {
		// 利用“,”拆分，
		String[] dataItems = dataInfo.split(",");
		HashMap<String, String> CurrentDataMap = new HashMap<String, String>(
				dataItems.length);
		
		String[] queryStringParam;
		for (String qs : dataItems) {
			queryStringParam = qs.split("=");
			// System.out.println(queryStringParam[0]+" "+
			// queryStringParam[1]);
			if (queryStringParam.length!=2) {
				continue;			
			}
			CurrentDataMap.put(queryStringParam[0], queryStringParam[1]);
		}
		RSData rsData = new RSData(CurrentDataMap);

		return rsData;
	}

	// 获取参数订单提交参数
	private boolean getXMLParas() {
		
		// 从字符串中分解四角坐标
		ArrayList<String> coverscope = new ArrayList<String>();
		if (!this.l3order.geoCoverageStr.equals("")) {
			String[] strDataSplitArray = this.l3order.geoCoverageStr
					.split(",");
			coverscope.addAll(Arrays.asList(strDataSplitArray));
		} else {
			coverscope.add("-1");
			coverscope.add("-1");
			coverscope.add("-1");
			coverscope.add("-1");
		}

		
		
		
		this.strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<CommOrderSubmit>" + "<L1OrderId>" + this.l3order.jobId_L1
				+ "</L1OrderId>" + "<L2OrderId>" + this.l3order.jobId_L2
				+ "</L2OrderId>" + "<L3OrderId>" + this.l3order.jobId
				+ "</L3OrderId>" + "<Parameters>"
				+ "<ProductType>CP</ProductType>"
				+ "<ProductName>"
				+ this.l3order.productName
				+ "</ProductName>"
				+ "<ULLat>"
				+ coverscope.get(3)
				+ "</ULLat><ULLong>"
				+ coverscope.get(0)
				+ "</ULLong>"
				+ "<LRLat>"
				+ coverscope.get(1)
				+ "</LRLat><LRLong>"
				+ coverscope.get(2)
				+ "</LRLong>"
				+ "<StartDate>"
				+ this.l3order.startDate
				+ "</StartDate>"
				+ "<EndDate>"
				+ this.l3order.endDate
				+ "</EndDate>" + "</Parameters>" + "<Datas>";
		

		AlgorithmDB algorithmDB = new AlgorithmDB();
		// 根据订单类型及处理所在数据中心确定Webservice Url,假设数据类型为30M-TM
		Algorithm algorithms = algorithmDB.search(this.l3order.orderType,
				"Anything").get(0);
		if (null == algorithms) {
			logger.error("查询共性产品提交算法失败！");
			return false;
		}
		// String AlgorithmName=algorithms.getAlgorithmName();
		// String AlgorithmPath=algorithms.getAlgorithmFilePath();
		//
		String[] url_method = algorithms.procSystemConfig.split(";");
		this.submitURL = url_method[0];
		this.method = url_method[1];

		if (null == this.submitURL || null == this.method) {
			logger.error("提交共性产品生产Webservice的URL或Method为空！");
			return false;
		}

		int count = 0;
		Iterator<RSData> iterator = this.DataList.iterator();
		while (iterator.hasNext()) {
			RSData rsData = (RSData) iterator.next();
			
			//将标准产品数据文件拷贝至标准产品目录
//			System.out.println(rsData.getRSDataString());
			if (rsData.getDataUrlWithoutIP()==null) {
				logger.error(rsData.filename+"文件路径为空！将从数据列表中清除，请注意数据是否完备！");
				continue;
			}
			
			File curSPDataFile=new File(rsData.getDataUrl());
			File targetSPDataFile = null;
			if (curSPDataFile.exists()) {
				targetSPDataFile=new File(standardProductPath.getAbsoluteFile()+"/"+curSPDataFile.getName());
				//test
//				System.out.println(targetSPDataFile.getAbsolutePath());
				if (!this.fileOperation.customCopy(curSPDataFile, targetSPDataFile)) {
					logger.error(curSPDataFile.getAbsolutePath()+"标准产品复制失败，将从数据列表中清除，请注意数据是否完备！");
					continue;
				}				
			}	
			
			String currParentDir="";
			String ftpDataUrl="";
			try {
				String strParentDirectory =targetSPDataFile.getParent();
				
				String [] files=strParentDirectory.split("/");
				currParentDir=files[files.length-1];
				
				//从数据中获取配置信息
				ftpDataUrl="ftp://ftpuser:123456@124.16.184.69/StandardProductBuffer/"+currParentDir+"/"+curSPDataFile.getName();
				
			} catch (Exception e) {
				// TODO: handle exception
				this.logger.error("组合数据FTP Url链接错误!请检查具体原因！");
				continue;
			}
			// 根据订单类型及处理所在数据中心确定算法名称和路径,假设数据类型为30M-TM
			count++;
			this.strXML += "<Data id=\"" + count + "\" Name=\""
			+ rsData.filename + "\">" + "<url>" +ftpDataUrl
			+ "</url>";
			
			//不分副产品
			if (rsData.filename.toUpperCase().contains("HXXVXX")) {
//				this.strXML+="<ULLat>0.0</ULLat><ULLong>0.0</ULLong><LRLat>0.0</LRLat><LRLong>0.0</LRLong>";
				this.strXML+="<ULLat>"
						+ rsData.ullat
						+ "</ULLat><ULLong>"
						+ rsData.ullon
						+ "</ULLong><LRLat>"
						+ rsData.lrlat
						+ "</LRLat><LRLong>"
						+ rsData.lrlon
						+ "</LRLong>";
			}
			else {
				this.strXML+="<ULLat>"
						+ rsData.ullat
						+ "</ULLat><ULLong>"
						+ rsData.ullon
						+ "</ULLong><LRLat>"
						+ rsData.lrlat
						+ "</LRLat><LRLong>"
						+ rsData.lrlon
						+ "</LRLong>";
			}			
			this.strXML+= "</Data>";
		}

		this.strXML += "</Datas>"
				+ "<DataProducts>"
				+ "<URL>ftp://124.16.184.69/CommonProductBuffer/"+this.l3order.jobId+"/</URL>"
				+ "<username>ftpuser</username>"
				+ "<password>123456</password>" + "</DataProducts>"
				+ "</CommOrderSubmit>";
		
		System.out.println("**************************"+strXML);

		return true;
	}

	public static void main(String[] args) {
		String DBConnectionPara = "10.3.10.1_3306_mccps_mca_mca";
		String[] DBConnection = DBConnectionPara.split("_");
		DBConn connection = new DBConn(DBConnection[0], DBConnection[1],
				DBConnection[2], DBConnection[3], DBConnection[4]);

		L3OrderDB l3OrderDB = new L3OrderDB();

		ArrayList<L3InternalOrder> l3InternalOrders = l3OrderDB
				.search("WHERE JobId='L3CP201606040001'");
		Iterator<L3InternalOrder> it_l3InternalOrders = l3InternalOrders
				.iterator();
		while (it_l3InternalOrders.hasNext()) {
			L3InternalOrder l4InternalOrder = (L3InternalOrder) it_l3InternalOrders
					.next();

			CommonProductOrderSubmitParasProcess process = new CommonProductOrderSubmitParasProcess(
					l4InternalOrder);
			process.doProcess();
			System.out.println(process.getStrXML());
			System.out.println(process.getMethod());
			System.out.println(process.getSubmitURL());
		}
	}
}
