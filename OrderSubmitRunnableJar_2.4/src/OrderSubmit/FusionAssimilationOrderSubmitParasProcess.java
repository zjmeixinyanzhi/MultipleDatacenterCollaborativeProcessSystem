package OrderSubmit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import DBSystem.AlgorithmDB;
import DBSystem.CommonProductDB;
import DBSystem.DBConn;
import DBSystem.L3OrderDB;
import DBSystem.L4OrderDB;
import DBSystem.RsDataCacheDB;
import DBSystem.SystemResourceDB;
import LogSystem.SystemLogger;
import OrderManage.L3InternalOrder;
import OrderManage.L4InternalOrder;
import RSDataManage.RSData;
import RSDataManage.Rsdatatype;
import ResourceManage.Algorithm;
import ResourceManage.DataCenter;
/**
 * 创建时间：2016年5月25日 上午10:33:52
 * 项目名称：OrderSubmitRunnableJar_2.3
 * 2016年5月25日
 * @author 张杰
 * @version 1.0
 * 文件名称：FusionAssimilationOrderSubmitParasProcess.java
 * 类说明：融合同化订单提交
 */
public class FusionAssimilationOrderSubmitParasProcess extends OrderSubmitParasProcess {
	// 三级订单
	public L3InternalOrder l3order = null;;
	// 所需Data
	public ArrayList<RSData> DataList = null;
	// log4j日志系统
	private Logger logger = SystemLogger.getInstance().getSysLogger();
	
	//无法处理的数据列表
	private ArrayList<String> preDataProductLists;
	
	private String dbconn=null;
	
	

	public FusionAssimilationOrderSubmitParasProcess(L3InternalOrder l3order,String dbconn) {
		this.l3order = l3order;
		this.DataList = new ArrayList<>();
		this.preDataProductLists=new ArrayList<String>();
		this.dbconn=dbconn;
	}
	

	public boolean doProcess() {
		//根据订单类型，获取融合同化所在的数据中心
		SystemResourceDB systemResourceDB = new SystemResourceDB();
		String condition=" where type like '%"
				+ this.l3order.orderType
				+ "%'";
		ArrayList<DataCenter> dataCenterList =systemResourceDB.searchByCondition(condition);
		if (dataCenterList==null) {
			logger.error("未找到能够执行"+this.l3order.orderType+"类型订单的数据中心！");
			return false;			
		}
		//默认就是一个
		DataCenter host = dataCenterList.get(0);
		if (null == host) {
			logger.error("未找到能够执行"+this.l3order.orderType+"类型订单的数据中心！");
			return false;
		}
		
		AlgorithmDB algorithmDB = new AlgorithmDB();
		// 根据订单类型及处理所在数据中心确定Webservice Url
		ArrayList<Algorithm> algorithms_list = algorithmDB.search(
				this.l3order.orderType, "Anything");
		if (null == algorithms_list) {
			logger.error("查询归一化算法失败！");
			return false;
		}
		Iterator<Algorithm> it_algorithms = algorithms_list.iterator();
		while (it_algorithms.hasNext()) {
			Algorithm algorithm = (Algorithm) it_algorithms.next();
			if (!(null == algorithm && !(algorithm.procSystemConfig.equals("")))) {
				String[] url_method = algorithm.procSystemConfig.split(";");
				this.submitURL = "http://" + host.getHostIp() + ":"
						+ host.getProxySystemPort() + "/" + url_method[0];
				this.method = url_method[1];
				break;
			}
		}

		if (null == this.submitURL || null == this.method) {
			logger.error("提交归一化处理Webservice的URL或Method为空！");
			return false;
		}

		// 根据订单信息确定webservice提交参数
		if (!getXMLParas()) {
			logger.error("<Error>组合提交参数错误！");
			return false;
		}		
		
		
		//如果是融合同化订单，需要切换数据库实例到MCCPS，否则会报错		
		String DBConnectionPara =this.dbconn;
		String[] DBConnection = DBConnectionPara.split("_");
		if (!(DBConnection.length == 6||DBConnection.length==5)) {
			System.out.println("<Error>数据库连接参数出错！");
			return false;
		}
		new DBConn(DBConnection[0], DBConnection[1],
				DBConnection[2], DBConnection[3], DBConnection[4]);
		
		return true;
	}
	
	//获取融合订单所依赖的辅助数据
	private String getFPAuxiliaryDatas(String fileName,int count) {
		String strsDatas="";
		
		boolean isUseTmpDatas=true;
		
		
		String DBConnectionPara = this.dbconn;
		String[] DBConnection = DBConnectionPara.split("_");
		if (!(DBConnection.length == 6)) {
			logger.error("<Error>数据库连接参数出错！");
			return strsDatas;
		}		
		
		if (isUseTmpDatas) {
			//采用临时数据方案
			String dataName;
			String dataPath;
			String[] path;
			String[] name;
			String[] chilidname;
			
			
//			CommonProductDB qpdb = new CommonProductDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", "qp_ndvi_1km");
			CommonProductDB qpdb = new CommonProductDB(DBConnection[0], DBConnection[1],DBConnection[5], DBConnection[3], DBConnection[4], "qp_ndvi_1km");
			//遍历文件夹
			dataPath = qpdb.select("InnerPrefix", "GridId", "358");
			dataName = qpdb.select("Name", "GridId", "358");
			path = dataPath.split(";");
			name = dataName.split(";");
			
			strsDatas += "<RetrievalDataProducts>";
			
			String auxDatas="";
			
			int i = 0;
			for(i = 0;i<31;i++){
				chilidname = name[i].split("\\.");
//				System.out.println(name[i]);
				auxDatas+=path[i];
				auxDatas+="###";
			}
			
			strsDatas+=auxDatas+ "</RetrievalDataProducts>";
		}
		else {
			//结合融合算法输入知识库，确定辅助数据类表
			//有序，只保留路径
		}			
		
		return strsDatas;
	}
	
	//获取同化订单所依赖的辅助数据
	private String getAPAuxiliaryDatas(String fileName) {		
		String strsDatas="";		
		boolean isUseTmpDatas=true;
		
		String DBConnectionPara = this.dbconn;
		String[] DBConnection = DBConnectionPara.split("_");
		if (!(DBConnection.length == 6)) {
			logger.error("<Error>数据库连接参数出错！");
			return strsDatas;
		}		
		
		if (isUseTmpDatas) {
			//采用临时数据方案
			String dataName;
			String dataPath;
			String[] path;
			String[] name;
			String[] chilidname;
			
			CommonProductDB qpdb = new CommonProductDB(DBConnection[0], DBConnection[1],DBConnection[5], DBConnection[3], DBConnection[4], "qp_ndvi_30m");
			
			//遍历文件夹
			dataPath = qpdb.select("InnerPrefix", "GridId", "1704");
			dataName = qpdb.select("Name", "GridId", "1704");
			path = dataPath.split(";");
			name = dataName.split(";");
			
			strsDatas += "<RetrievalDataProducts>";
			
			String auxDatas="";
			
			int i = 0;
			for(i = 0;i<name.length;i++){
				chilidname = name[i].split("\\.");
				if(chilidname[3].contains("121")||(chilidname[3].contains("181"))||chilidname[3].contains("241")){
					auxDatas+=path[i];
					auxDatas+="###";
				}
			}			
			strsDatas+=auxDatas+ "</RetrievalDataProducts>";			
		}
		else {
			//结合融合算法输入知识库，确定辅助数据类表
			//有序，只保留路径			
		}				
		return strsDatas;
	}
	
	// 获取融合同化所需数据对象
	private String getDatas() {
		
		String strsDatas="";
		
		int count=0;
		
		//获取待处理产品数据列表					
		Iterator<String> iterator=this.l3order.retrievalDataList.iterator();
		while (iterator.hasNext()) {
			String productsInfo= (String) iterator.next();
			System.out.println(productsInfo);			
			
			//分割	
			// 利用“,”拆分，
			String[] dataItems = productsInfo.split(",");
			HashMap<String, String> CurrentDataMap = new HashMap<String, String>(
					dataItems.length);

			String[] queryStringParam;
			for (String qs : dataItems) {
				queryStringParam = qs.split("=");
				// System.out.println(queryStringParam[0]+" "+
				// queryStringParam[1]);
				if (queryStringParam.length==2) {
					CurrentDataMap.put(queryStringParam[0], queryStringParam[1]);
				}				
			}
			
			RSData rsData = new RSData(CurrentDataMap);									
			System.out.println(">>"+rsData.filename);
			if (rsData.filename==null) {
				logger.error(productsInfo+"提取共性产品名称失败！");
				continue;
			}
			
			strsDatas+="<Data id=\"" + (++count) + "\" Name=\""
					+ rsData.filename + "\">" + "<dataid>" + rsData.dataid
					+ "</dataid>" + "<url>" + rsData.getDataUrl() + "</url>";		
			
			//融合订单
			if (this.l3order.orderType.equals("L3FP")) {
				strsDatas+=getFPAuxiliaryDatas(rsData.filename,count);		
				strsDatas+="</Data>" ;	
			}
			//同化订单
			else if (this.l3order.orderType.equals("L3AP")) {
				strsDatas+=getAPAuxiliaryDatas(rsData.filename);		
				strsDatas+= "</Data>";	
			}
			else {
				logger.error(this.l3order.jobId+"非融合同化订单！");
				strsDatas+= "</Data>";	
				continue;
			}
			
		}	
		return strsDatas;
	}

	// 获取参数订单提交参数
	private boolean getXMLParas() {
		
		// 根据订单类型及处理所在数据中心确定算法名称和路径,假设数据类型为30M-TM
		String AlgorithmName = null;
		String AlgorithmPath = null;
		AlgorithmDB algorithmDB = new AlgorithmDB();
		ArrayList<Algorithm> algorithms_list = algorithmDB.search(
				this.l3order.orderType, "Anything");
		if (null == algorithms_list) {
			return false;
		}
		
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

		Algorithm algorithm = (Algorithm) algorithms_list.get(0);

		AlgorithmName = algorithm.getAlgorithmName();
		AlgorithmPath = algorithm.getAlgorithmFilePath();

		this.strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FusionAssimilationOrderSubmit>" + "<L1OrderId>"
				+ this.l3order.jobId_L1
				+ "</L1OrderId>"
				+ "<L2OrderId>"
				+ this.l3order.jobId_L2
				+ "</L2OrderId>"
				+ "<L3OrderId>"
				+ this.l3order.jobId
				+ "</L3OrderId>"
				+ "<OrderType>"
				+ this.l3order.orderType
				+ "</OrderType>"
				+ "<AlgorithmName>"
				+ AlgorithmName
				+ "</AlgorithmName>"
				+ "<AlgorithmPath>"
				+ AlgorithmPath
				+ "</AlgorithmPath>"
				+ "<Parameters>"
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
				+ "</EndDate>"
				+ "</Parameters>"
				+ "<Datas>";

		this.strXML +=(getDatas()+ "</Datas>" + "</FusionAssimilationOrderSubmit>");
		 System.out.println(">>>\n 请求参数为：" + this.strXML);

		return true;
	}

	public static void main(String[] args) {
		String DBConnectionPara = "10.3.10.1_3306_mccps_mca_mca";
		String[] DBConnection = DBConnectionPara.split("_");
		DBConn connection = new DBConn(DBConnection[0], DBConnection[1],
				DBConnection[2], DBConnection[3], DBConnection[4]);

		L4OrderDB l4OrderDB = new L4OrderDB();

		ArrayList<L4InternalOrder> l4InternalOrders = l4OrderDB
				.search("WHERE JobId='L3GN201508100001@master'");
		Iterator<L4InternalOrder> it_l4InternalOrders = l4InternalOrders
				.iterator();
		while (it_l4InternalOrders.hasNext()) {
			L4InternalOrder l4InternalOrder = (L4InternalOrder) it_l4InternalOrders
					.next();

			FusionAssimilationOrderSubmitParasProcess process = new FusionAssimilationOrderSubmitParasProcess(
					l4InternalOrder,"");
			process.doProcess();
			System.out.println(process.getStrXML());
			System.out.println(process.getMethod());
			System.out.println(process.getSubmitURL());
		}
	}
}
