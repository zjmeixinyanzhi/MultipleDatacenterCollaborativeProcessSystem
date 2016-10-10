package OrderSubmit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import DBSystem.AlgorithmDB;
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
 * 创建时间：2015-8-16 下午5:09:43 项目名称：OrderSubmitRunnableJar 2015-8-16
 * 
 * @author 张杰
 * @version 1.0 文件名称：NormalizeOrderSubmitParasProcess.java 类说明：RN、GN归一化订单参数处理
 */
public class NormalizeOrderSubmitParasProcess extends OrderSubmitParasProcess {
	// 四级订单
	public L4InternalOrder l4order = null;

	// 所需Data
	public ArrayList<RSData> DataList = null;

	// log4j日志系统
	private Logger logger = SystemLogger.getInstance().getSysLogger();
	
	//无需处理的数据列表
	private ArrayList<String> preDataProductLists;

	public NormalizeOrderSubmitParasProcess(L4InternalOrder l4order) {
		this.l4order = l4order;
		this.DataList = new ArrayList<>();
		this.preDataProductLists=new ArrayList<String>();
	}


	public boolean doProcess() {
		//
		SystemResourceDB systemResourceDB = new SystemResourceDB();
		DataCenter host = systemResourceDB.getDataCenterListByIP(
				this.l4order.DataCenterIP).get(0);
		if (null == host) {
			return false;
		}
		
		AlgorithmDB algorithmDB = new AlgorithmDB();
		// 根据订单类型及处理所在数据中心确定Webservice Url,假设数据类型为30M-TM
		ArrayList<Algorithm> algorithms_list = algorithmDB.search(
				this.l4order.orderType, "Anything");
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

		return true;
	}

	// 获取数据对象
	private String getDatas() {
		
		String strsDatas="";
		
		DBSystem.RSDataTypeDB rsDataTypeDB=new DBSystem.RSDataTypeDB();
		
		//获取所有当前订单的数类型，查询rsdatatypedb库确定对应数据的预处理步骤，进行数据筛选
		String []dataTypes=this.l4order.dataType.split(";");
		System.out.println("????"+String.join(">>", dataTypes));
		HashMap<String, String> dataType_preProcessing=new HashMap<String, String>();	
		for (int i = 0; i < dataTypes.length; i++) {
			String curDataType=dataTypes[i];
			String[] satellite_sensor=curDataType.split("@");
			if (satellite_sensor.length!=2) {
				continue;
			}

			String condition="where satellite='" +
					satellite_sensor[1] +
					"' and sensor='" +
					 satellite_sensor[0]+
					"'";		
			
			ArrayList<Rsdatatype> rsdataTypes=rsDataTypeDB.search(condition);
			
			if (rsdataTypes!=null) {
				//獲取預處理步驟
				Rsdatatype currRsdatatype=rsdataTypes.get(0);
				if (currRsdatatype==null) {
					continue;
				}
				dataType_preProcessing.put(dataTypes[i], currRsdatatype.getPreprocessing());
				
				//test
				System.out.println("   "+dataTypes[i]+"->"+currRsdatatype.getPreprocessing());
			}				
		}
		
		
		//获取上级订单编号
		//换成数据库查询
		String preOrderId="";
		String lastfixOrderId=l4order.jobId.substring(4,l4order.jobId.length());
		switch (this.l4order.orderType) {
		case "L3GN":
			preOrderId="L3DP"+lastfixOrderId;
			break;
		case "L3RN":
			preOrderId="L3GN"+lastfixOrderId;
			break;
		case "L3DS":
			preOrderId="L3RN"+lastfixOrderId;
			break;
		default:
			break;
		}
		System.out.println(preOrderId);
		
		// 四级订单库查找产品数据
		L4OrderDB l4OrderDB=new L4OrderDB();
		ArrayList<L4InternalOrder> orders=l4OrderDB.search(" where JobId='"
				+ preOrderId+ "'");
		if (orders==null) {
			return null;
		}
		
		L4InternalOrder l4InternalOrder=orders.get(0);
		
		int count = 0;
		
		System.out.println(l4InternalOrder.strDataProductList.size());
				
		Iterator<String> iterator=l4InternalOrder.strDataProductList.iterator();
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
			System.out.println(">>"+rsData.dataStatus);
			
			if (rsData.dataStatus==null) {
				logger.info(rsData.filename+"状态未知！");
			}			
			else if (rsData.dataStatus.equals("Avaliable")) {
				//进行数据筛选
				//根据原始数据dataid查询rsdatacachedb，获取当前数据的类型spacecraft和sensor，
				//然后匹配dataType_preProcessing，判断是否需要进行当前处理，如果需要进入处理数据列表，如果不需要，
				//直接进入产品列表，需要更新已经出现的产品列表，需要更新产品你列表
				
				RsDataCacheDB dataCacheDB=new RsDataCacheDB();
				ArrayList<RSData> tempRSDataLists=dataCacheDB.search("where dataid='"
						+ rsData.dataid
						+ "'");
				
				if (tempRSDataLists!=null) {
					RSData tempRawData=tempRSDataLists.get(0);
					//当前数据是否进行当前的处理
					String allpreProcessing=(String)dataType_preProcessing.get((tempRawData.sensor+"@"+tempRawData.spacecraft));
					System.out.println(">>>>>"+allpreProcessing+" "+(this.l4order.orderType));
					
					if (allpreProcessing!=null&&allpreProcessing.contains(this.l4order.orderType)) {
						this.logger.info("需要"
								+ l4InternalOrder.orderType
								+ "处理数据："+productsInfo);
						 strsDatas+="<Data id=\"" + (++count) + "\" Name=\""
									+ rsData.filename + "\">" + "<dataid>" + rsData.dataid
									+ "</dataid>" + "<url>" + rsData.getDataUrl() + "</url>"
									+"<ULLat>"
									+ rsData.ullat
									+ "</ULLat><ULLong>"
									+ rsData.ullon
									+ "</ULLong><LRLat>"
									+ rsData.lrlat
									+ "</LRLat><LRLong>"
									+ rsData.lrlon
									+ "</LRLong>"
									+ "</Data>";	
					}else {
						//拼凑产品类型
						this.logger.info("无需处理直接进入产品列表的数据："+productsInfo);
						this.preDataProductLists.add(productsInfo);
					}
				}					
			}			
		}
		
		//如果存在不处理的数据，直接更新到产品列表
		if (this.preDataProductLists!=null) {
			//test
			//System.out.println("无需处理的数据个数："+this.preDataProductLists.size()+"\n"+preDataProductLists.get(0));
			l4OrderDB.setDataProductList(this.l4order.jobId,this.preDataProductLists);	
		}		
		return strsDatas;
	}

	// 获取参数订单提交参数
	//此处处理不好，不能自动识别，
	private boolean getXMLParas() {
		
		// 根据订单类型及处理所在数据中心确定算法名称和路径,假设数据类型为30M-TM
		String AlgorithmName = null;
		String AlgorithmPath = null;
		AlgorithmDB algorithmDB = new AlgorithmDB();
		ArrayList<Algorithm> algorithms_list = algorithmDB.search(
				this.l4order.orderType, "Anything");
		if (null == algorithms_list) {
			return false;
		}

		// 获取上级订单信息
		L3OrderDB l3OrderDB = new L3OrderDB();
		String l3OrderId = this.l4order.jobId_L3;
		L3InternalOrder l3InternalOrder = l3OrderDB.search(
				" where JobId='" + l3OrderId + "'").get(0);
		l3InternalOrder.geoCoverageStr.split("");
		// 从字符串中分解四角坐标
		ArrayList<String> coverscope = new ArrayList<String>();
		if (!l3InternalOrder.geoCoverageStr.equals("")) {
			String[] strDataSplitArray = l3InternalOrder.geoCoverageStr
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
				+ "<NormalizationOrderSubmit>" + "<L1OrderId>"
				+ this.l4order.jobId_L1
				+ "</L1OrderId>"
				+ "<L2OrderId>"
				+ this.l4order.jobId_L2
				+ "</L2OrderId>"
				+ "<L3OrderId>"
				+ this.l4order.jobId
				+ "</L3OrderId>"
				+ "<OrderType>"
				+ this.l4order.orderType
				+ "</OrderType>"
				+ "<AlgorithmName>"
				+ AlgorithmName
				+ "</AlgorithmName>"
				+ "<AlgorithmPath>"
				+ AlgorithmPath
				+ "</AlgorithmPath>"
				+ "<Parameters>"
				+ "<ProductName>"
				+ l3InternalOrder.productName
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
				+ l3InternalOrder.startDate
				+ "</StartDate>"
				+ "<EndDate>"
				+ l3InternalOrder.endDate
				+ "</EndDate>"
				+ "</Parameters>"
				+ "<Datas>";

		this.strXML +=(getDatas()+ "</Datas>" + "</NormalizationOrderSubmit>");
		 System.out.println(">>>\n \n请求参数为：" + this.strXML);

		return true;
	}

	public static void main(String[] args) {
		String DBConnectionPara = "10.3.10.1_3306_mccps_caoyang_123456";
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

			NormalizeOrderSubmitParasProcess process = new NormalizeOrderSubmitParasProcess(
					l4InternalOrder);
			process.doProcess();
			System.out.println(process.getStrXML());
			System.out.println(process.getMethod());
			System.out.println(process.getSubmitURL());
		}
	}
}
