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
import OrderManage.L4InternalOrder;
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
public class L4OrderDB extends DBConn {
	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public L4OrderDB() {
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

		this.dbTable = "l4orderdb";
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
	public synchronized ArrayList<L4InternalOrder> search(String condition) {
		System.out
				.println("L4OrderDB::public ArrayList< L4InternalOrder > search ( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<L4InternalOrder> orderList = new ArrayList<L4InternalOrder>();
		L4InternalOrder l4Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;
			// Test
			// System.out.println(strSql);
			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return orderList;
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
				l4Order = new L4InternalOrder();
				// 订单ID（L4）
				l4Order.jobId = rs.getString("JobId");
				// 优先级 0-9
				l4Order.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				l4Order.jobId_L1 = rs.getString("JobId_L1");
				// 二级订单ID（L2）
				l4Order.jobId_L2 = rs.getString("JobId_L2");
				// 三级订单ID（L3）
				l4Order.jobId_L3 = rs.getString("JobId_L3");
				// 数据所在数据中心IP
				l4Order.DataCenterIP = rs.getString("DataCenterIP");
				// 用来存放订单类型：CP/FP/AP/VD
				l4Order.orderType = rs.getString("OrderType");
				// 订单级别
				l4Order.orderLevel = rs.getString("OrderLevel");
				// 订单状态：Start/Running/Finish/Error/Suspend
				l4Order.workingStatus = rs.getString("workingStatus");
				// 订单的生产状态：RadioNorm /GeoNorm/ CommonProduct/Fusion/Assimilation
				l4Order.orderStatus = rs.getString("orderStatus");
				// 订单的数据状态：Available/NotAvailable/Future
				l4Order.dataStatus = rs.getString("dataStatus");
				// 用来记录订单的数据列表
				l4Order.dataList = new ArrayList<String>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					String[] strDataSplitArray = strDataList.split(";");
					l4Order.dataList.addAll(Arrays.asList(strDataSplitArray));
				}
				// 操作员
				l4Order.operatorId = rs.getString("OPERATORID");
				// 算法资源名称
				l4Order.algorithmName = rs.getString("AlgorithmName");
				// 算法资源程序
				l4Order.algorithmPath = rs.getString("AlgorithmPath");
				// 产品列表
				String strDataProduct = rs.getString("dataProductList");
				if (null == strDataProduct) {
					l4Order.strDataProductList = new ArrayList<String>();
				} else if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					l4Order.strDataProductList.addAll(Arrays
							.asList(strDataSplitArray));
				} else {
					l4Order.strDataProductList = new ArrayList<String>();
				}
				// 算法资源ID
				l4Order.algorithmID = rs.getInt("AlgorithmID");
				// 与融合/同化相关联的共性产品信息
				l4Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if (null == strRetrievalDataList) {
					l4Order.retrievalDataList = new ArrayList<String>();
				} else if (!strRetrievalDataList.equals("")) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l4Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				l4Order.dataType = rs.getString("dataType");

				orderList.add(l4Order);
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("四级订单条件属性查询失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(condition);
			}
			e.printStackTrace();
			// return null;
			orderList.clear();
			return orderList;
		}
		return orderList;
	}

	// 向订单库插入订单
	public synchronized boolean addOrder(L4InternalOrder order) {
		System.out
				.println("L4OrderDB::public boolean addOrder( L4InternalOrder l4orderList ) | 向订单库插入订单");
		boolean isSuccess = false;

		String strSql = "INSERT INTO "
				+ this.dbTable
				+ "(JobId,JobId_L1,JobId_L2,JobId_L3,OrderType,OrderLevel,"
				+ "workingStatus,dataList,AlgorithmName,AlgorithmPath,AlgorithmID,dataType,DataCenterIP)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return isSuccess;
			}

			PreparedStatement pstmt = conn.prepareStatement(strSql);
			int count = 0;

			// 订单ID（L4）
			pstmt.setString(++count, order.jobId);
			// 一级订单ID（L1）
			pstmt.setString(++count, order.jobId_L1);
			// 二级订单ID（L2）
			pstmt.setString(++count, order.jobId_L2);
			// 二级订单ID（L3）
			pstmt.setString(++count, order.jobId_L3);
			// 用来存放订单类型：L3RN/L3GN/L3CP/L3FP/L3AP
			pstmt.setString(++count, order.orderType);
			// 订单级别
			pstmt.setString(++count, order.orderLevel);
			// 订单状态
			pstmt.setString(++count, order.workingStatus);
			// 数据列表
			String strDataList = "";
			if (order.dataList != null || !order.dataList.isEmpty()) {
				for (Iterator<String> data_curr = order.dataList.iterator(); data_curr
						.hasNext();) {
					strDataList += data_curr.next();
					if (!strDataList.endsWith(";")) {
						strDataList += ";";
					}
				}
			}
			pstmt.setString(++count, strDataList);
			// 算法资源名称
			pstmt.setString(++count, order.algorithmName);
			// 算法资源程序
			pstmt.setString(++count, order.algorithmPath);
			// 算法资源ID
			pstmt.setInt(++count, order.algorithmID);
			// 原始数据类型
			pstmt.setString(++count, order.dataType);
			// 所在数据中心IP
			pstmt.setString(++count, order.DataCenterIP);

			// 执行插入
			isSuccess = pstmt.execute();
			
			if (!isSuccess) {
				logger.error("向订单库插入订单,更新行数为0！");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("向四级订单库插入订单失败!");
			logger.error("SQL执行异常", e);
		}

		return isSuccess;
	}

	// 插入订单列表
	public synchronized boolean add(ArrayList<L4InternalOrder> l4orderList) {
		System.out
				.println("L4OrderDB::public boolean addOrder( ArrayList<L4InternalOrder> l4orderList ) | 向订单库插入多个订单");
		boolean isSuccess = false;
		if (l4orderList.isEmpty()) {
			return isSuccess;
		}
		// 依次向订单库中插入订单
		// test
		// System.out.println("Insert L4OrderList Size:"+l4orderList.size());
		for (L4InternalOrder l4InternalOrder : l4orderList) {
			isSuccess &= addOrder(l4InternalOrder);
		}

		return isSuccess;
	}

	// 更新四级订单dataProduct
	public synchronized boolean setOrderWorkingStatus(String l4OrderId,
			String l4OrderStatus) {
		System.out
				.println("L4OrderDB::public boolean setOrderWorkingStatus( String l4OrderId, String l4OrderStatus ) | 设置订单状态");

		switch (l4OrderStatus) {
		case "Ready":
			break;
		case "Finish":
			break;
		case "Error":
			break;
		default:
			logger.info(l4OrderStatus+"状态类型未找到！");
			return false;
		}

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET workingStatus = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 订单状态
			pstmt.setString(1, l4OrderStatus);
			// 三级订单ID
			pstmt.setString(2, l4OrderId);

			// 执行更新
			if (pstmt.executeUpdate()==0) {
				logger.error("更新四级订单dataProduct，行数更新为0！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新四级订单dataProduct失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkingStatus(l4OrderId, l4OrderStatus);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 更新四级订单数据产品列表
	public synchronized boolean setDataProductList(String l4OrderId,
			ArrayList<String> strDataProductList) {

		System.out
				.println("L4OrderDB::public boolean setDataProductList( String l4OrderId, ArrayList< String > strDataProductList ) | 设置订单产品列表");

		//更新前需要查询产品列表是否为空，不空时需要追加，因为有些数据不需要处理
		String strQueryCondition=" WHERE JobId = '"
				+ l4OrderId
				+ "'";
		try {
			L4InternalOrder tempL4InternalOrders=this.search(strQueryCondition).get(0);
			ArrayList<String> preDatalists=tempL4InternalOrders.strDataProductList;
			//非空时追加数据
			if (preDatalists!=null) {
				strDataProductList.addAll(preDatalists);
			}			
		} catch (Exception e) {
			logger.error(l4OrderId+"追加产品失败！");
			e.printStackTrace();
		}
		
		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataProductList = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 产品列表
			if (strDataProductList.size() > 0) {
				String[] strList = new String[strDataProductList.size()];
				strDataProductList.toArray(strList);
				pstmt.setString(1, String.join(";", strList));
			} else {
				// 如果没有获取到数据列表就赋值为空字符串
				pstmt.setString(1, "");
			}
			// 订单ID（L3）
			pstmt.setString(2, l4OrderId);

			// 执行更新
			if (pstmt.executeUpdate()==0) {
				logger.error("更新四级订单数据产品列表,行数更新为0！");
			}
			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新四级订单数据产品列表失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataProductList(l4OrderId, strDataProductList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
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
