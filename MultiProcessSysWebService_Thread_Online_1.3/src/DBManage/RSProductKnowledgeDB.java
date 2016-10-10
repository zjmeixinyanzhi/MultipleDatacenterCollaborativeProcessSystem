package DBManage;
 import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.regexp.internal.recompile;

import sun.tools.tree.ThisExpression;
import DataService.InputParametersDataClass;
import DataService.InputParametersProductsClass;
import DataService.OutOrderXmlClass;
import DBManage.RSProductKnowledgeDB;
import RSDataManage.Knowledgebase;
import SystemManage.SystemLogger;


/**
 * 创建时间：2016年3月8日 下午11:06:35
 * 项目名称：MultiProcessSysWebService_Thread_Online
 * 2016年3月8日
 * @author Yan Jining
 * @version 1.0
 * 文件名称：RSProductKnowledgeDB.java
 * 类说明：
 */
public class RSProductKnowledgeDB extends DBConn{
	private String productID;
	private String inputParametersData;
	private String inputParametersProducts;
	private String auxiliarydata;
	
	private static Connection conn = null;
	private static String dbTable;
	// 日志系统
	private static Logger logger = SystemLogger.getSysLogger();	
	//static Connection connection = null;
	//static Statement statement = null;
	private Document document;
	
	
	// 无参构造函数
	public RSProductKnowledgeDB() {
		System.out.println("RSProductKnowledgeDB::public RSProductKnowledgeDB() | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>RSProductKnowledgeDB::public RSProductKnowledgeDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		dbTable = "knowledgebase";
	}

	public static synchronized void closeConnected() {
		try {
			if (null != conn) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	//查询订单库
	public static synchronized ResultSet search(String querryProductID) {
		System.out
				.println("L2OrderDB::publicResultSet search(String querryProductID)  | 向产品输入参数知识库查询参数");

		if (null == querryProductID) {
			querryProductID = "";
		}
		
		ResultSet rs=null;

		try {
			String strSql = "SELECT * FROM "
					+ dbTable
					+ " where productID='"
					+ querryProductID + "'";
		
			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return rs;
			}

			Statement st = conn.createStatement();
			 rs = st.executeQuery(strSql);
			
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("产品输入参数知识库查询失败！");
			e.printStackTrace();
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(querryProductID);
			}
		}
		
		return  rs;
	}
	
	
	//查询订单库
		public static synchronized ArrayList<Knowledgebase> searchAll(String condition) {
			System.out
					.println("L2OrderDB::publicResultSet search(String querryProductID)  | 向产品输入参数知识库查询参数");

			if (null == condition) {
				condition = "";
			}
			
			ArrayList<Knowledgebase> lists=new ArrayList<Knowledgebase>();
			ResultSet rs=null;

			try {
				String strSql = "SELECT * FROM "
						+ dbTable
						+ " "
						+ condition ;
			
				if (null == conn) {
					logger.error("数据库连接初始化失败！");
					return null;
				}
				
				Statement st = conn.createStatement();
				 rs = st.executeQuery(strSql);
				 
				 
				 while (rs.next()) {
					 String productId=rs.getString("productID");
					 String productIdName=rs.getString("productIDName");
					 String spaceRange=rs.getString("spaceRange");
					 String timeRange=rs.getString("timeRange");
					 String inputParametersData=rs.getString("inputParametersData");
					 String inputParametersProducts=rs.getString("inputParametersProducts");
					 
					 Knowledgebase knowledgebase=new Knowledgebase(productId, productIdName, spaceRange, timeRange, inputParametersData, inputParametersProducts);
					 
					 lists.add(knowledgebase);			 
					
				}
				
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error("产品输入参数知识库查询失败！");
				e.printStackTrace();
				String strSqlState = e.getSQLState();
				if (strSqlState.equals("08S01")) {
					conn = getConnection();
					return searchAll(condition);
				}
			}
			
			return  lists;
		}
	
	// 最终返回结果容器(全局变量)
	static HashMap<String, List<OutOrderXmlClass>> OutOrderXMLMapSum = new HashMap<String, List<OutOrderXmlClass>>();

	

	// 构造函数
	public RSProductKnowledgeDB(String productID, String inputParametersData,
			String inputParametersProducts, String auxiliarydata) {
		// TODO Auto-generated constructor stub
		this.productID = productID;
		this.inputParametersData = inputParametersData;
		this.inputParametersProducts = inputParametersProducts;
		this.auxiliarydata = auxiliarydata;
	}

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getInputParametersData() {
		return inputParametersData;
	}

	public void setInputParametersData(String inputParametersData) {
		this.inputParametersData = inputParametersData;
	}

	public String getInputParametersProducts() {
		return inputParametersProducts;
	}

	public void setInputParametersProducts(String inputParametersProducts) {
		this.inputParametersProducts = inputParametersProducts;
	}

	public String getAuxiliarydata() {
		return auxiliarydata;
	}

	public void setAuxiliarydata(String auxiliarydata) {
		this.auxiliarydata = auxiliarydata;
	}
	
	// knowledgeAdd
	public static void knowledgeAdd(String productID, String ProductIDName,
			String inputParametersData, String inputParametersProducts,
			String auxiliarydata) {
		ResultSet resultSet = null;

		String strSql = "insert into knowledgebase(" + "productID, "
				+ "ProductIDName, " + "inputParametersData, "
				+ "inputParametersProducts, " + "auxiliarydata) values('"
				+ productID + "','" + ProductIDName + "','"
				+ inputParametersData + "','" + inputParametersProducts + "','"
				+ auxiliarydata + "')";
	}

	// knowledgeEdit
	public static void knowledgeEdit(String productID,
			String inputParametersData, String inputParametersProducts,
			String auxiliarydata) {
		ResultSet resultSet = null;

		String strSql = "update knowledgebase set inputParametersData='"
				+ inputParametersData + "' inputParametersProducts='"
				+ inputParametersProducts + "' auxiliarydata='" + auxiliarydata
				+ "' where productID='" + productID + "'";

	}

	// knowledgeDelete
	public static void knowledgeDelete(String productID) {
		ResultSet resultSet = null;

		String strSql = "delete from knowledgebase where productID='"
				+ productID + "'";
	}

	// 标准数据XML文件解析及标准数据库查询函数
	private static List<OutOrderXmlClass> InputParametersDataXMLParse(
			String InputParametersDataXML) {
		Map<String, InputParametersDataClass> InputParametersDataXMLMap = new HashMap<String, InputParametersDataClass>();
		List<OutOrderXmlClass> OutOrderXmlDataList = new ArrayList<OutOrderXmlClass>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(
					InputParametersDataXML)));
			NodeList inputParametersData = document.getChildNodes();

			for (int i = 0; i < inputParametersData.getLength(); i++) {
				Node data = inputParametersData.item(i);
				NodeList dataInfo = data.getChildNodes();
				for (int j = 0; j < dataInfo.getLength(); j++) {
					Node node = dataInfo.item(j);
					NodeList dataMeta = node.getChildNodes();

					Map rowData = new HashMap();
					for (int k = 0; k < dataMeta.getLength(); k++) {
						if ((!(dataMeta.item(k).getNodeName())
								.equalsIgnoreCase("#text"))
								&& (!(dataMeta.item(k).getNodeName())
										.equalsIgnoreCase("#comment"))) {
							// System.out.println(dataMeta.item(k).getNodeName()
							// + ":" + dataMeta.item(k).getTextContent());
							rowData.put(dataMeta.item(k).getNodeName(),
									dataMeta.item(k).getTextContent());
							// System.out.println("rowData=="+rowData);
						}
					}
					if (rowData.size() != 0) {
						if (!InputParametersDataXMLMap.containsKey(rowData.get(
								"inputdatatype").toString()))
							InputParametersDataXMLMap
									.put(rowData.get("inputdatatype")
											.toString(),
											new InputParametersDataClass(
													rowData.get("inputdatatype")
															.toString(),
													rowData.get("satellite")
															.toString(),
													rowData.get("sensor")
															.toString(),
													rowData.get("productweight")
															.toString()));
						else {
							double old = Double
									.valueOf(InputParametersDataXMLMap.get(
											rowData.get("inputdatatype")
													.toString())
											.getProductweight());
							double cur = Double.valueOf(
									rowData.get("productweight").toString())
									.doubleValue();
							if (old <= cur)
								InputParametersDataXMLMap
										.put(rowData.get("inputdatatype")
												.toString(),
												new InputParametersDataClass(
														rowData.get(
																"inputdatatype")
																.toString(),
														rowData.get("satellite")
																.toString(),
														rowData.get("sensor")
																.toString(),
														rowData.get(
																"productweight")
																.toString()));
						}
					}
				}
			}// end for
//			logger.info("InputParametersDataXML解析完毕");
			// 打印输出
			Iterator it = InputParametersDataXMLMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				// System.out.println("inputdatatype="+InputParametersDataXMLMap.get(entry.getKey()).getInputdatatype());
				// //OutOrderXmlData.add(new
				// OutOrderXmlClass("inputdatatype",InputParametersDataXMLMap.get(entry.getKey()).getInputdatatype()));
				// System.out.println("satellite="+InputParametersDataXMLMap.get(entry.getKey()).getSatellite());
				OutOrderXmlDataList.add(new OutOrderXmlClass("satellite",
						InputParametersDataXMLMap.get(entry.getKey())
								.getSatellite()));
				// System.out.println("sensor="+InputParametersDataXMLMap.get(entry.getKey()).getSensor());
				OutOrderXmlDataList.add(new OutOrderXmlClass("sensor",
						InputParametersDataXMLMap.get(entry.getKey())
								.getSensor()));
				// System.out.println("productweight="+InputParametersDataXMLMap.get(entry.getKey()).getProductweight());
				// //OutOrderXmlData.add(new
				// OutOrderXmlClass("productweight",InputParametersDataXMLMap.get(entry.getKey()).getProductweight()));

				// 查询标准产品数据库获得对应的遥感元数据
				/*
				 * String sqlStandardData=
				 * "SELECT * FROM datamanager.metadatainfo where CREATE_TIME between '2010-03-14 00:00:00' and '2010-03-15 00:00:00' "
				 * +
				 * "and LR_LON >='73' and UL_LON <='136' and UR_LAT >= '3' and LR_LAT <= '54' "
				 * +
				 * "and satellite='"+InputParametersDataXMLMap.get(entry.getKey
				 * ()).getSatellite()+"' " +
				 * "and sensor='"+InputParametersDataXMLMap
				 * .get(entry.getKey()).getSensor()+"'";
				 * ArrayList<StandardProduct> standardProductLists=new
				 * ArrayList<StandardProduct>();
				 * //standardProductLists=StandardProductDB
				 * .query(sqlStandardData); for(int i = 0;i <
				 * standardProductLists.size(); i ++){
				 * OutOrderXmlDataList.add(new
				 * OutOrderXmlClass("Name",standardProductLists
				 * .get(i).getName())); OutOrderXmlDataList.add(new
				 * OutOrderXmlClass
				 * ("url",standardProductLists.get(i).getInnerPrefix()));
				 * OutOrderXmlDataList.add(new
				 * OutOrderXmlClass("Rows",standardProductLists
				 * .get(i).getArea())); OutOrderXmlDataList.add(new
				 * OutOrderXmlClass
				 * ("Cols",standardProductLists.get(i).getArea())); }
				 */
				/*-------------假代码--------开始------------------*/
				//实际执行过程不在此处检索数据位置
				/*OutOrderXmlDataList.add(new OutOrderXmlClass("Name",
						"Modis-20151011"));
				OutOrderXmlDataList.add(new OutOrderXmlClass("url",
						"/home/pipsCloud/Modis"));
				OutOrderXmlDataList.add(new OutOrderXmlClass("Rows", "400"));
				OutOrderXmlDataList.add(new OutOrderXmlClass("Cols", "400"));*/
				/*-------------假代码--------结束------------------*/
			}
			/*
			 * // 获取ArrayList的大小 System.out.println("OutOrderXmlData size=: "+
			 * OutOrderXmlDataList.size()); for(int i = 0;i <
			 * OutOrderXmlDataList.size(); i ++){
			 * System.out.println("第"+(i+1)+"个元素xmlnodename="
			 * +OutOrderXmlDataList.get(i).getXmlnodename());
			 * System.out.println(
			 * "第"+(i+1)+"个元素xmlnodevalue="+OutOrderXmlDataList
			 * .get(i).getXmlnodevalue()); }
			 */
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return OutOrderXmlDataList;
	}

	// 中间产品XML文件解析及共性产品查询函数
	private static List<InputParametersProductsClass> InputParametersProductsXMLParse(
			String InputParametersProductsXML) {
		List<InputParametersProductsClass> InputParametersProductsXMLList = new ArrayList<InputParametersProductsClass>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(
					InputParametersProductsXML)));
			NodeList inputParametersData = document.getChildNodes();
			for (int i = 0; i < inputParametersData.getLength(); i++) {
				Node data = inputParametersData.item(i);
				NodeList dataInfo = data.getChildNodes();
				for (int j = 0; j < dataInfo.getLength(); j++) {// dataInfo.getLength()代表有几个大类
					Node node = dataInfo.item(j);
					NodeList dataMeta = node.getChildNodes();
					Map rowData = new HashMap();
					for (int k = 0; k < dataMeta.getLength(); k++) {// dataMeta.getLength()代表每个小类有几个
						if ((!(dataMeta.item(k).getNodeName())
								.equalsIgnoreCase("#text"))
								&& (!(dataMeta.item(k).getNodeName())
										.equalsIgnoreCase("#comment"))) {
							// System.out.println(dataMeta.item(k).getNodeName()
							// + ":" + dataMeta.item(k).getTextContent());
							rowData.put(dataMeta.item(k).getNodeName(),
									dataMeta.item(k).getTextContent());
							// System.out.println("rowData=="+rowData);
						}
					}
					if (rowData.size() != 0)
						InputParametersProductsXMLList
								.add(new InputParametersProductsClass(rowData
										.get("productTag").toString(), rowData
										.get("productID").toString()));
				}
			}
//			System.out.println("InputParametersProductsXML解析完毕");
			/*
			 * //获取ArrayList的大小
			 * System.out.println("InputParametersProductsXMLList size=: "+
			 * InputParametersProductsXMLList.size()); for(int i = 0;i <
			 * InputParametersProductsXMLList.size(); i ++){
			 * System.out.println("第"
			 * +(i+1)+"个元素Producttag="+InputParametersProductsXMLList
			 * .get(i).getProducttag());
			 * System.out.println("第"+(i+1)+"个元素Productid="
			 * +InputParametersProductsXMLList.get(i).getProductid()); }
			 */
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return InputParametersProductsXMLList;
	}

	// 解析数据表queryArr的三个字段
	public static HashMap<String, List<OutOrderXmlClass>> InstanceQuery(String querryProductID) {
		// 标准数据解析存储容器
		List<OutOrderXmlClass> OutOrderXmlDataListInstance = null;
		// 中间产品解析存储容器
		List<InputParametersProductsClass> InputParametersProductsXMLListInstance = null;
		List<OutOrderXmlClass> OutOrderXmlProductListInstance = new ArrayList<OutOrderXmlClass>();

		// 数据库连接及查询
		try {
			
			ResultSet resultSet = search(querryProductID);
			ResultSetMetaData md = resultSet.getMetaData();// 取数据库的列名
			resultSet.beforeFirst();// 将结果集指针指回到开始位置，这样才能通过while获取rs中的数据
			while (resultSet.next()) {
				for (int i = 1; i <= md.getColumnCount(); i++) {
//					System.out.println("第" + i + "列ColumnName=="
//							+ md.getColumnName(i) + "\nObject=="
//							+ resultSet.getObject(i));
					
					/*----------------inputParametersData---------------------*/
					String querryProductIDValue = md.getColumnName(i);
					if (querryProductIDValue
							.equalsIgnoreCase("inputParametersData")) {

						//if (resultSet.getObject(i).toString() != "") {
							if (resultSet.getObject(i) != null) {
							logger.info("开始解析inputParametersData");
							OutOrderXmlDataListInstance = InputParametersDataXMLParse(resultSet
									.getObject(i).toString());
							OutOrderXMLMapSum.put("inputParametersData"
									+ querryProductID,
									OutOrderXmlDataListInstance);
//							System.out.println(OutOrderXMLMapSum);
						} else {
							logger.info("InputParametersData is null !");
							OutOrderXmlDataListInstance = null;
						}
					}
					/*-------------inputParametersProducts--------------------------*/
					if (querryProductIDValue
							.equalsIgnoreCase("inputParametersProducts")) {
						//if (resultSet.getObject(i).toString() != "") {
						if (resultSet.getObject(i) != null) {
//							System.out.println("开始解析inputParametersProducts");
							InputParametersProductsXMLListInstance = InputParametersProductsXMLParse(resultSet
									.getObject(i).toString());
							// 递归解析InputParametersProductsXMLListInstance
							for (int i1 = 0; i1 < InputParametersProductsXMLListInstance
									.size(); i1++) {
								String producttagTemp = InputParametersProductsXMLListInstance
										.get(i1).getProducttag();
								String productidTemp = InputParametersProductsXMLListInstance
										.get(i1).getProductid();
//								System.out
//										.println("InputParametersProductsXMLListInstance.producttag=="
//												+ producttagTemp);
//								System.out
//										.println("InputParametersProductsXMLListInstance.productid=="
//												+ productidTemp);

								if (producttagTemp.equalsIgnoreCase("1")) {
									// 查询共性产品数据库获得对应的遥感元数据
									/*
									 * String sqlCommonProduct=
									 * "SELECT * FROM datamanager.metadatainfo where auxiliaryDataID='"
									 * +
									 * InputParametersProductsXMLListInstance.get
									 * (i1).getProductid()+"' ";
									 * ArrayList<QuantitativeProduct>
									 * quantitativeProductLists=new
									 * ArrayList<QuantitativeProduct>();
									 * if(AuxiliaryDataDB
									 * .query(sqlCommonProduct)!=null) {
									 * //quantitativeProductLists
									 * =AuxiliaryDataDB.query(sqlCommonProduct);
									 * for(int i11 = 0;i11 <
									 * quantitativeProductLists.size(); i11 ++){
									 * OutOrderXmlProductListInstance.add(new
									 * OutOrderXmlClass
									 * ("Name",quantitativeProductLists
									 * .get(i11).getName()));
									 * OutOrderXmlProductListInstance.add(new
									 * OutOrderXmlClass
									 * ("url",quantitativeProductLists
									 * .get(i11).getInnerPrefix())); } else
									 * OutOrderXmlDataProductMapInstance=
									 * InstanceQuery
									 * (inputParametersProductsXMLMapInstance
									 * .get("1").getProductid()); }
									 */
									/*-------------假代码--------开始------------------*/
									//实际执行过程不在此处检索数据位置
									//InstanceQuery(InputParametersProductsXMLListInstance.get(i1).getProductid());

									/*-------------假代码--------结束------------------*/
								} else {
									// 查询共性产品数据库获得对应的遥感元数据
									/*
									 * String sqlCommonProduct=
									 * "SELECT * FROM datamanager.metadatainfo where auxiliaryDataID='"
									 * +
									 * InputParametersProductsXMLListInstance.get
									 * (i1).getProductid()+"' ";
									 * ArrayList<QuantitativeProduct>
									 * quantitativeProductLists=new
									 * ArrayList<QuantitativeProduct>();
									 * //quantitativeProductLists
									 * =AuxiliaryDataDB.query(sqlCommonProduct);
									 * for(int i11 = 0;i11 <
									 * quantitativeProductLists.size(); i11 ++){
									 * OutOrderXmlProductListInstance.add(new
									 * OutOrderXmlClass
									 * ("Name",quantitativeProductLists
									 * .get(i11).getName()));
									 * OutOrderXmlProductListInstance.add(new
									 * OutOrderXmlClass
									 * ("url",quantitativeProductLists
									 * .get(i11).getInnerPrefix())); }
									 */
									// OutOrderXmlProductListInstance.add(new
									// OutOrderXmlClass("productTag",InputParametersProductsXMLListInstance.get(i1).getProducttag()));
									OutOrderXmlProductListInstance
											.add(new OutOrderXmlClass(
													"productID",
													InputParametersProductsXMLListInstance
															.get(i1)
															.getProductid()));
									/*-------------假代码--------开始------------------*/
									//实际执行过程不在此处检索数据位置
									/*OutOrderXmlProductListInstance
											.add(new OutOrderXmlClass("Name",
													"MST2_20151020"));
									OutOrderXmlProductListInstance
											.add(new OutOrderXmlClass("url",
													"/home/pipsCloud/Modis/MST2"));*/
									/*-------------假代码--------结束------------------*/
									OutOrderXMLMapSum.put(
											"inputParametersProducts"
													+ querryProductID,
											OutOrderXmlProductListInstance);// 存入map
								}
							}

						} else {
							logger.info("inputParametersProducts is null !");
							InputParametersProductsXMLListInstance = null;
						}
					}
				}
			} // end while

		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();} 
		return OutOrderXMLMapSum;
	}

	// Map<String, List<OutOrderXmlClass>>打印接口
	public static void OutOrderXMLMapPrint(
			Map<String, List<OutOrderXmlClass>> outOrderXMLMap2) {
		for (Map.Entry<String, List<OutOrderXmlClass>> entry : outOrderXMLMap2
				.entrySet()) {
//			System.out.println("\nkey=" + entry.getKey());
			List<OutOrderXmlClass> list = entry.getValue();
			Iterator<OutOrderXmlClass> iterator = list.iterator();
			while (iterator.hasNext()) {
				OutOrderXmlClass outOrderXmlClass = (OutOrderXmlClass) iterator
						.next();
//				System.out.println(outOrderXmlClass.getXmlnodename() + "="
//						+ outOrderXmlClass.getXmlnodevalue());
			}
//			System.out.println();
		}

	}

	// 生成xml
	public void outOrderXMLMapCreate(
			Map<String, List<OutOrderXmlClass>> outOrderXMLMapFile,
			String fileName) {
		int datacount = 1;// data个数计数器
		// 初始化
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
		}
		// 创建xml

		Element root = this.document.createElement("Datas");
		this.document.appendChild(root);

		for (Map.Entry<String, List<OutOrderXmlClass>> entry : outOrderXMLMapFile
				.entrySet()) {

			if (entry.getKey().toString().contains("inputParametersData")) {
				List<OutOrderXmlClass> list = entry.getValue();
				for (int i = 0; i < list.size() / 6; i++) {
					Element elements = this.document
							.createElement("inputParametersData");
					elements.setAttribute("id", "" + datacount);
					datacount++;
					// elements.setAttribute("Name", ""+i);
					for (int j = 0; j < 6; j++) {
						Element name = this.document.createElement(list.get(
								i * 6 + j).getXmlnodename());
						name.appendChild(this.document.createTextNode(list.get(
								i * 6 + j).getXmlnodevalue()));
						elements.appendChild(name);
					}
					root.appendChild(elements);
				}

			}// end if

			if (entry.getKey().toString().contains("inputParametersProducts")) {
				List<OutOrderXmlClass> list = entry.getValue();
				for (int i = 0; i < list.size() / 3; i++) {
					Element elements = this.document
							.createElement("inputParametersProducts");
					elements.setAttribute("id", "" + datacount);
					datacount++;
					// elements.setAttribute("Name", ""+i);
					for (int j = 0; j < 3; j++) {
						Element name = this.document.createElement(list.get(
								i * 3 + j).getXmlnodename());
						name.appendChild(this.document.createTextNode(list.get(
								i * 3 + j).getXmlnodevalue()));
						elements.appendChild(name);
					}
					root.appendChild(elements);
				}

			}// end if

			if (entry.getKey().toString().contains("auxiliarydata")) {
				List<OutOrderXmlClass> list = entry.getValue();
				for (int i = 0; i < list.size() / 3; i++) {
					Element elements = this.document
							.createElement("auxiliarydata");
					elements.setAttribute("id", "" + datacount);
					datacount++;
					// elements.setAttribute("Name", ""+i);
					for (int j = 0; j < 3; j++) {
						Element name = this.document.createElement(list.get(
								i * 3 + j).getXmlnodename());
						name.appendChild(this.document.createTextNode(list.get(
								i * 3 + j).getXmlnodevalue()));
						elements.appendChild(name);
					}
					root.appendChild(elements);
				}

			}// end if
		}

		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
//			System.out.println("生成XML文件成功!");
		} catch (TransformerConfigurationException e) {
			System.out.println(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
	}

	// ProductWeightCompute
	public void ProductWeightCompute(String productID) {

	}

	// spectralweightCompute
	public void spectralweightCompute(String productID) {

	}

	// spaceweightCompute
	public void spaceweightCompute(String productID) {

	}

	// timeweightCompute
	public void timeweightCompute(String productID) {

	}
	
	public Map<String, List<OutOrderXmlClass>> InstanceReturn(
			String productIDInstance) {
		// 递归解析
		RSProductKnowledgeDB.InstanceQuery(productIDInstance);
		return OutOrderXMLMapSum;

	}
	
	
	
	/*public static void main(String args[]) {
		RSProductKnowledgeDB rSProductKnowledgeDBInstance = new RSProductKnowledgeDB();
		String productIDInstance = "QP_LST_5KM";
		// 递归解析
		RSProductKnowledgeDB.InstanceQuery(productIDInstance);
		System.out.println("最终返回的订单Map");		
		RSProductKnowledgeDB.OutOrderXMLMapPrint(OutOrderXMLMapSum);
		// 生成XML订单(OutOrderXML)
		rSProductKnowledgeDBInstance.outOrderXMLMapCreate(OutOrderXMLMapSum,
				"OutOrderXML.xml");
	}*/

}
