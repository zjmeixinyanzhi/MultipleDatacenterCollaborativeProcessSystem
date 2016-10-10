package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import LogSystem.SystemLogger;
import OrderManage.L4InternalOrder;

/**
 * 创建时间：2015-7-14 上午9:58:49 项目名称：MultiProcessSysWebService_Thread 2015-7-14
 * 
 * @author 张杰
 * @version 1.0 文件名称：L4OrderDB.java 类说明：四级订单库操作类
 */
public class L4OrderDB extends DBConn {
	private static Connection conn = null;
	private String dbTable;
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	public L4OrderDB() {
		// System.out.println("L4OrderDB::public L4OrderDB() | 构造函数");

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
	
	
	// 更新四级订单数据产品列表
	public synchronized boolean setDataProductList(String l4OrderId,
			String strDataProductList) {

		System.out
				.println("L4OrderDB::public boolean setDataProductList( String l4OrderId, ArrayList< String > strDataProductList ) | 设置订单产品列表");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataProductList = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 产品列表
			if (strDataProductList.length() > 0) {
				pstmt.setString(1,strDataProductList);
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
	
	

	// 条件属性查询
	public synchronized ArrayList<L4InternalOrder> search(String condition) {
		// logger.info("L4OrderDB::public ArrayList< L4InternalOrder > search ( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<L4InternalOrder> orderList = new ArrayList<L4InternalOrder>();
		L4InternalOrder l4Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;

			if (null == conn) {
				System.out.println("系统退出！");
				return orderList;
				// conn=getConnection();
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
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
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(condition);
			}
			e.printStackTrace();
			logger.error("L4OrderDB数据查询失败！" + e);
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

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("L4OrderDB插入数据失败！" + e);
		}

		return isSuccess;
	}

	// 插入订单列表
	public synchronized boolean add(ArrayList<L4InternalOrder> l4orderList) {
		logger.info("L4OrderDB::public boolean addOrder( ArrayList<L4InternalOrder> l4orderList ) | 向订单库插入多个订单");
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

	// 设置四级订单状态
	public synchronized boolean setOrderWorkingStatus(String l4OrderId,
			String l4OrderStatus) {
		logger.info("L4OrderDB::public boolean setOrderWorkingStatus( String l4OrderId, String l4OrderStatus ) | 设置级订单状态");

		switch (l4OrderStatus) {
		case "Ready":
			break;
		case "Finish":
			break;
		case "Error":
			break;
		default:
			return false;
		}

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET workingStatus = ? WHERE JobId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 三级订单状态
			pstmt.setString(1, l4OrderStatus);
			// 三级三级订单ID
			pstmt.setString(2, l4OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkingStatus(l4OrderId, l4OrderStatus);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置四级订单"+l4OrderId+"状态错误"+e);
			return false;
		}

		return true;
	}

	// 设置数据状态
	public synchronized boolean setDataStatus(String l4OrderId,
			String l4DataStatus) {
		logger.info("L4OrderDB::public boolean setDataStatus( String l3OrderId, String l4DataStatus ) | 设置三级订单数据状态");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataStatus = ? WHERE JobId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 三级订单状态
			pstmt.setString(1, l4DataStatus);
			// 三级三级订单ID
			pstmt.setString(2, l4OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataStatus(l4OrderId, l4DataStatus);
			}
			e.printStackTrace();
			logger.error("设置四级订单"+l4OrderId+"错误"+e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置四级订单"+l4OrderId+"错误"+e);
			return false;
		}

		return true;
	}

}
