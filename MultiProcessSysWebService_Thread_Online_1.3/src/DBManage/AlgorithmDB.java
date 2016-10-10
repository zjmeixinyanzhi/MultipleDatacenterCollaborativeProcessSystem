/*
 *程序名称 		: AlgorithmDB.java
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
package DBManage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.localHomeType;

import OrderManage.L3InternalOrder;
import SystemManage.DBConfig;
import SystemManage.ServerConfig;
import SystemManage.SystemLogger;
import TaskSchedular.Algorithm;

/**
 * @author caoyang
 * 
 */
public class AlgorithmDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// 构造函数
	public AlgorithmDB() {
		System.out.println("AlgorithmDB::public AlgorithmDB () | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				logger.error("<Error>AlgorithmDB::public AlgorithmDB () | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "algorithmdb";
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

	// 向算法资源库插入算法资源
	public synchronized boolean addAlgorithm(Algorithm algorithm) {
		System.out
				.println("AlgorithmDB::public boolean addOrder( L2ExternalOrder order ) | 向算法资源库插入算法资源");

		if (null==algorithm) {
			logger.error("算法对象为null,插入失败！");
			return false;
		}
		
		// 将订单插入数据库的订单表中
		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(AlgorithmName,Description,ProcSystemName,ProcSystemConfig,"
					+ "AlgorithmFilePath,Parameter,Schema,status,resourceId,algorithmType,dataType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

			if (null == conn) {
				logger.error("AlgorithmDB-->addAlgorithm 数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 算法名
			pstmt.setString(1, algorithm.getAlgorithmName());
			// 简单描述信息
			pstmt.setString(2, algorithm.getDescription());
			// 处理系统名称
			pstmt.setString(3, algorithm.getProcSystemName());
			// 处理系统服务器配置信息
			pstmt.setString(4, algorithm.getProcSystemConfig());
			// 算法具体路径
			pstmt.setString(5, algorithm.getAlgorithmFilePath());
			// 参数
			String[] strArgList = new String[algorithm.getArgsList().size()];
			algorithm.getArgsList().toArray(strArgList);
			pstmt.setString(6, String.join(";", strArgList));
			// 参数限定
			String[] strSchemaList = new String[algorithm.getSchemaList()
					.size()];
			algorithm.getSchemaList().toArray(strSchemaList);
			pstmt.setString(7, String.join(";", strSchemaList));
			// 可用状态（A/NA）
			pstmt.setString(8, algorithm.getStatus());
			// 数据中心ID
			pstmt.setInt(9, algorithm.getResourceId());
			// 算法资源类型
			pstmt.setString(10, algorithm.getAlgorithmType());
			// 算法资源数据类型
			pstmt.setString(11, algorithm.getDataType());

			// 执行插入
			int count=pstmt.executeUpdate();
			
			if (count==0) {
				logger.error("算法插入为空！请检查算法对象是否正确！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("算法插入SQL失败!");
			logger.error("数据库异常", e);

			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return addAlgorithm(algorithm);
			}
			// e.printStackTrace();
			return false;
		}
		return true;
	}

	// 向算法资源库查询算法资源
	public synchronized ArrayList<Algorithm> search(String algorithmType,
			String dataType) {
		System.out
				.println("AlgorithmDB::public Algorithm search( String type, String dataType ) | 向算法资源库查询算法资源");

		ArrayList<Algorithm> algorithmList = new ArrayList<Algorithm>();

		if ((null == algorithmType) || ("".equals(algorithmType))) {
			return algorithmList;
		}

		if (null == dataType) {
			return algorithmList;
		}
		// if( "".equals( dataType ) ){
		// dataType = "Anything";
		// }
		// if( !algorithmType.equals( "L3RN" ) ){
		// dataType = "Anything";
		// }

		String strSql = "SELECT * FROM " + this.dbTable
				+ " WHERE algorithmType = '" + algorithmType
				+ "' and dataType LIKE '" + "%" + dataType + "%" + "'";

		try {
			// String strProcSystemName;
			// switch( algorithmType ) {
			// //二级订单类型
			// case "L2CP":
			// strProcSystemName = "CommonProductSystem";
			// break;
			// case "L2FP":
			// strProcSystemName = "FusionSystem";
			// break;
			// case "L2AP":
			// strProcSystemName = "AssimulationSystem";
			// break;
			// case "L2VD":
			// strProcSystemName = "ValidationSystem";
			// break;
			// //三级订单类型
			// case "L3RN":
			// strProcSystemName = "RadNormSystem";
			// break;
			// case "L3GN":
			// strProcSystemName = "GeoNormSystem";
			// break;
			// case "L3CP":
			// strProcSystemName = "CommonProductSystem";
			// break;
			// case "L3FP":
			// strProcSystemName = "FusionSystem";
			// break;
			// case "L3AP":
			// strProcSystemName = "AssimulationSystem";
			// break;
			//
			// //case "L3CP":
			// // strProcSystemName = "DataServiceSystem"; //@@@
			// // break;
			//
			// default:
			// strProcSystemName = "";
			// return algorithmList;
			// //break;
			// }

			// String strSql = "SELECT * FROM " + this.dbTable +
			// " WHERE ProcSystemName = '" + strProcSystemName + "'"; //@@@
			// String strSql = "SELECT * FROM " + this.dbTable +
			// " WHERE ProcSystemName = '" + strProcSystemName +
			// "' and dataType = '" + dataType + "'";
			// String strSql = "SELECT * FROM " + this.dbTable +
			// " WHERE algorithmType = '" + algorithmType + "' and dataType = '"
			// + dataType + "'";
			// 包含关系，HJ1ACCD1;HJ1ACCD2;HJ1BCCD1;HJ1BCCD2; HJ1ACCD1 模糊查询

			// test
			System.out.println(">>>>>>>>>>>>>>>" + strSql);

			if (null == conn) {
				logger.error("AlgorithmDB-->search 数据库连接初始化失败！");
				return algorithmList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			
			boolean flag=false;
			while (rs.next()) {
				flag=true;
				
				Algorithm algorithm = new Algorithm();

				// 算法资源ID
				algorithm.setAlgorithmID(rs.getInt("AlgorithmID"));
				// 算法资源名
				algorithm.setAlgorithmName(rs.getString("AlgorithmName"));
				// 简单描述信息
				algorithm.setDescription(rs.getString("Description"));
				// 处理系统名称
				// DataServiceSystem/RadNormSystem/GeoNormSystem/CommonProductSystem/FusionSystem/AssimulationSystem/ValidationSystem
				algorithm.setProcSystemName(rs.getString("ProcSystemName"));
				// 处理系统服务器配置信息
				algorithm.setProcSystemConfig(rs.getString("ProcSystemConfig"));
				// 算法具体路径
				algorithm.setAlgorithmFilePath(rs
						.getString("AlgorithmFilePath"));
				// 参数
				ArrayList<String> argsList = new ArrayList<String>(
						Arrays.asList(rs.getString("Parameter").split(";")));
				algorithm.setArgsList(argsList);
				// 参数限定
				ArrayList<String> schemaList = new ArrayList<String>(
						Arrays.asList(rs.getString("Schema").split(";")));
				algorithm.setSchemaList(schemaList);
				// 可用状态（A/NA）
				algorithm.setStatus(rs.getString("status"));
				// 数据中心ID
				algorithm.setResourceId(rs.getInt("resourceId"));
				// 算法资源类型
				algorithm.setAlgorithmType(rs.getString("algorithmType"));
				// 算法资源数据类型
				algorithm.setDataType(rs.getString("dataType"));

				algorithmList.add(algorithm);

				// test
				System.out.println("===========Algorithm : "
						+ algorithm.toString());
			}
			
			if (!flag) {
				logger.error("按条件查询算法结果为空！请检查算法类型："+algorithmType+",数据类型： "+dataType+"是否正确！");
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {

			logger.error("算法执行失败!");
			logger.error("数据库异常", e);

			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(algorithmType, dataType);
			}
			e.printStackTrace();
			algorithmList.clear();
			return algorithmList;
		}
		return algorithmList;
	}

	// 向算法资源库查询算法资源:唯一确定一个
	public synchronized Algorithm searchUnique(String algorithmType,
			String dataType) {
		System.out
				.println("AlgorithmDB::public Algorithm search( String type, String dataType ) | 向算法资源库查询算法资源");

		Algorithm algorithm = new Algorithm();

		if ((null == algorithmType) || ("".equals(algorithmType))) {
			return algorithm;
		}

		if (null == dataType) {
			return algorithm;
		}

		// String strSql = "SELECT * FROM " + this.dbTable +
		// " WHERE algorithmType = '" + algorithmType + "' and dataType = '"
		// + dataType + "'";
		// 包含关系，HJ1ACCD1;HJ1ACCD2;HJ1BCCD1;HJ1BCCD2; HJ1ACCD1 模糊查询
		String strSql = "SELECT * FROM " + this.dbTable
				+ " WHERE algorithmType = '" + algorithmType
				+ "' and dataType LIKE '" + "%" + dataType + "%" + "'";
		try {

			// test
			// System.out.println(">>>>>>>>>>>>>>>" + strSql);

			if (null == conn) {
				logger.error("AlgorithmDB-->searchUnique 数据库连接初始化失败！");
				return algorithm;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			
			boolean flag=false;
			while (rs.next()) {
				flag=true;				
				
				// 算法资源ID
				algorithm.setAlgorithmID(rs.getInt("AlgorithmID"));
				// 算法资源名
				algorithm.setAlgorithmName(rs.getString("AlgorithmName"));
				// 简单描述信息
				algorithm.setDescription(rs.getString("Description"));
				// 处理系统名称
				// DataServiceSystem/RadNormSystem/GeoNormSystem/CommonProductSystem/FusionSystem/AssimulationSystem/ValidationSystem
				algorithm.setProcSystemName(rs.getString("ProcSystemName"));
				// 处理系统服务器配置信息
				algorithm.setProcSystemConfig(rs.getString("ProcSystemConfig"));
				// 算法具体路径
				algorithm.setAlgorithmFilePath(rs
						.getString("AlgorithmFilePath"));
				// 参数
				ArrayList<String> argsList = new ArrayList<String>(
						Arrays.asList(rs.getString("Parameter").split(";")));
				algorithm.setArgsList(argsList);
				// 参数限定
				ArrayList<String> schemaList = new ArrayList<String>(
						Arrays.asList(rs.getString("Schema").split(";")));
				algorithm.setSchemaList(schemaList);
				// 可用状态（A/NA）
				algorithm.setStatus(rs.getString("status"));
				// 数据中心ID
				algorithm.setResourceId(rs.getInt("resourceId"));
				// 算法资源类型
				algorithm.setAlgorithmType(rs.getString("algorithmType"));
				// 算法资源数据类型
				algorithm.setDataType(rs.getString("dataType"));

				// test
				System.out.println("===========Algorithm : "
						+ algorithm.toString());
			}
			if (!flag) {
				logger.error("按条件查询算法结果为空！请检查算法类型："+algorithmType+",数据类型： "+dataType+"是否正确！");
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("算法查询失败!");
			logger.error("数据库异常", e);

			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return searchUnique(algorithmType, dataType);
			}
			e.printStackTrace();
			return algorithm;
		}
		return algorithm;
	}

}
