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

import sun.tools.tree.ThisExpression;
import DataService.InputParametersDataClass;
import DataService.InputParametersProductsClass;
import DataService.OutOrderXmlClass;
import DBManage.RSProductTagDB;
import SystemManage.SystemLogger;

/**
 * 创建时间：2016年4月27日 下午11:26:30 项目名称：MultiProcessSysWebService_Thread_Online
 * 2016年4月27日
 * 
 * @author 张杰
 * @version 1.0 文件名称：RSProductTagDB.java 类说明：
 */
public class RSProductTagDB extends DBConn {
	private String productID;
	private String inputParametersData;
	private String inputParametersProducts;
	private String auxiliarydata;

	private static Connection conn = null;
	private static String dbTable;
	// 日志系统
	private static Logger logger = SystemLogger.getSysLogger();
	// static Connection connection = null;
	// static Statement statement = null;
	private Document document;

	// 无参构造函数
	public RSProductTagDB() {
		System.out
				.println("RSProductKnowledgeDB::public RSProductKnowledgeDB() | 构造函数");

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

		dbTable = "producttagdb";
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

	// //查询订单库
	// public static synchronized ResultSet searchAndUpdate() {
	//
	//
	// ResultSet rs=null;
	//
	// try {
	// String strSql = "SELECT * FROM "
	// + " where productID='"
	// + querryProductID + "'";
	//
	// if (null == conn) {
	// logger.error("数据库连接初始化失败！");
	// return rs;
	// }
	//
	// Statement st = conn.createStatement();
	// rs = st.executeQuery(strSql);
	//
	//
	// }catch (SQLException e) {
	// // TODO Auto-generated catch block
	// logger.error("产品输入参数知识库查询失败！");
	// e.printStackTrace();
	// String strSqlState = e.getSQLState();
	// if (strSqlState.equals("08S01")) {
	// conn = getConnection();
	// return search(querryProductID);
	// }
	// }
	//
	// return rs;
	// }

	// 最终返回结果容器(全局变量)
	static HashMap<String, List<OutOrderXmlClass>> OutOrderXMLMapSum = new HashMap<String, List<OutOrderXmlClass>>();

	// 构造函数
	public RSProductTagDB(String productID, String inputParametersData,
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
	public static void updateAllRawDatas(String allData, String productID) {
		ResultSet resultSet = null;

		String strSql = "update " + "producttagdb" + " set allRawData='"
				+ allData + "' where Tag='" + productID + "'";

		Statement st;
		try {
			st = conn.createStatement();
			st.execute(strSql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// knowledgeEdit
	public static void updateDatas(String rawData,String IntermediateProduct, String productID) {
		ResultSet resultSet = null;

		String strSql = "update " + "producttagdb" + " set rawData='"+ rawData + "',IntermediateProduct='"+IntermediateProduct+"' where Tag='" + productID + "'";
//		System.out.println(strSql);
		Statement st;
		try {
			st = conn.createStatement();
			st.execute(strSql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// knowledgeDelete
	public static void knowledgeDelete(String productID) {
		ResultSet resultSet = null;

		String strSql = "delete from knowledgebase where productID='"
				+ productID + "'";
	}

}
