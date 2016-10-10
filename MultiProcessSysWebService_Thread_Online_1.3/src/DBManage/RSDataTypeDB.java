package DBManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import RSDataManage.*;
import SystemManage.SystemLogger;
import TaskSchedular.RSDataSchedular;

/**
 * 创建时间：2015-7-14 上午9:58:49 项目名称：MultiProcessSysWebService_Thread 2015-7-14
 * 
 * @author 张杰
 * @version 1.0 文件名称：L4OrderDB.java 类说明：四级订单库操作类
 */
public class RSDataTypeDB extends DBConn {
	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public RSDataTypeDB() {
		System.out.println("L4OrderDB::public L4OrderDB() | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>L4OrderDB::public L4OrderDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "rsdatatypedb";
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

	// 条件属性查询
	public synchronized ArrayList<Rsdatatype> search(String condition) {
		System.out
				.println("L4OrderDB::public ArrayList< Rsdatatype > search ( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<Rsdatatype> dataTypeLists = new ArrayList<Rsdatatype>();
	
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;
			// Test
			// System.out.println(strSql);
			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return dataTypeLists;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			
			//判断结果集是否为空
			if (!rs.next()) {  
				logger.error("条件属性查询结果为空，请检查查询条件："+condition);
			}else {
				 //存在记录 rs就要向上移一条记录 因为rs.next会滚动一条记录了  
				 rs.previous();
			}
			
			while (rs.next()) {
				Rsdatatype dataType = new Rsdatatype();
				// ID
				dataType.setId(rs.getInt("id"));
				// Satalite
				dataType.setSatellite(rs.getString("satellite"));
				//Sensor
				dataType.setSensor(rs.getString("sensor"));
				// spname
				dataType.setSpname(rs.getString("spname"));
				// preprocessing 				
				dataType.setPreprocessing(rs.getString("preprocessing"));
				//datacenter
				dataType.setDatacenter(rs.getString("datacenter"));

				dataTypeLists.add(dataType);
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("DataType条件属性查询失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(condition);
			}
			e.printStackTrace();
			// return null;
			dataTypeLists.clear();
			return dataTypeLists;
		}
		return dataTypeLists;
	}



	public static void main(String[] args) {
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();

		L3OrderDB l3OrderDB = new L3OrderDB();
		//
		ArrayList<L3InternalOrder> l3OrderList = l3OrderDB
				.search("where JobId='L3DP201507170001'");
		Iterator<L3InternalOrder> it_l3OrderList = l3OrderList.iterator();
		while (it_l3OrderList.hasNext()) {
			L3InternalOrder l3InternalOrder = (L3InternalOrder) it_l3OrderList
					.next();
			System.out.println(l3InternalOrder.jobId);
			OrderStudio studio = new OrderStudio();
			studio.addL3DPSubOrder(l3InternalOrder);

		}

		ArrayList<L3InternalOrder> l3OrderList1 = l3OrderDB
				.search("where JobId='L3RN201507170001'");
		Iterator<L3InternalOrder> it_l3OrderList1 = l3OrderList1.iterator();
		while (it_l3OrderList1.hasNext()) {
			L3InternalOrder l3InternalOrder = (L3InternalOrder) it_l3OrderList1
					.next();
			System.out.println(l3InternalOrder.jobId);
			OrderStudio studio = new OrderStudio();
			studio.addL3PreProcessSubOrder(l3InternalOrder);

		}
	}
}
