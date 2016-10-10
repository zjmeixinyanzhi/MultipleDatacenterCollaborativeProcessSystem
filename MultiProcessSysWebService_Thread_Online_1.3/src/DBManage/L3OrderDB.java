/*
 *程序名称 		: L3OrderDB.java
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
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import OrderManage.L2ExternalOrder;
import OrderManage.L3InternalOrder;
import OrderManage.Order;
import RSDataManage.RSData;
import SystemManage.SystemLogger;
import TaskSchedular.Algorithm;

/**
 * @author caoyang
 * 
 */
public class L3OrderDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	/**
	 * 
	 */
	public L3OrderDB() {
		System.out.println("L3OrderDB::public RequestDB() | 构造函数");

		// 初始化连接SQL执行
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>L3OrderDB::public RequestDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "l3orderdb";
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

	// 向订单库插入订单
	public synchronized boolean addOrder(L3InternalOrder order) {
		System.out
				.println("L3OrderDB::public boolean addOrder( L2ExternalOrder order ) | 向订单库插入订单");
		
		if (null==order) {
			logger.error("三级订单对象为空，插入三级订单失败！");
			return false;
		}

		// 将订单插入SQL执行的订单表中
		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(JobId,JobId_L1,JobId_L2,OrderType,OrderLevel,geoCoverageStr,"
					+ "startDate,endDate,workingStatus,dataList,AlgorithmName,AlgorithmPath,dataProductList,AlgorithmID,retrievalDataList,dataType,productName)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 订单ID（L3）
			pstmt.setString(1, order.jobId);
			// 一级订单ID（L1）
			pstmt.setString(2, order.jobId_L1);
			// 二级订单ID（L2）
			pstmt.setString(3, order.jobId_L2);
			// 用来存放订单类型：L3RN/L3GN/L3CP/L3FP/L3AP
			pstmt.setString(4, order.orderType);
			// 订单级别
			pstmt.setString(5, order.orderLevel);
			// 地理区域
			pstmt.setString(6, order.geoCoverageStr);
			pstmt.setDate(7, order.startDate);
			pstmt.setDate(8, order.endDate);
			// 订单状态
			pstmt.setString(9, order.workingStatus);
			// 数据列表
			String strDataList = "";
			if (order.dataList != null || !order.dataList.isEmpty()) {
				for (Iterator<String> data_curr = order.dataList.iterator(); data_curr
						.hasNext();) {
					strDataList += data_curr.next() + ";";
				}
			}
			pstmt.setString(10, strDataList);
			// 算法资源名称
			pstmt.setString(11, order.algorithmName);
			// 算法资源程序
			pstmt.setString(12, order.algorithmPath);
			// 产品列表
			if (order.strDataProductList.size() > 0) {
				String[] strList = new String[order.strDataProductList.size()];
				order.strDataProductList.toArray(strList);
				pstmt.setString(13, String.join(";", strList));
			} else {
				pstmt.setString(13, "");
			}
			// 算法资源ID
			pstmt.setInt(14, order.algorithmID);
			// 与融合/同化相关联的共性产品信息
			String strRetrievalDataList = "";
			if (order.retrievalDataList != null
					&& !order.retrievalDataList.isEmpty()) {
				for (Iterator<String> data_curr = order.retrievalDataList
						.iterator(); data_curr.hasNext();) {
					strRetrievalDataList += data_curr.next() + ";";
				}
			}
			pstmt.setString(15, strRetrievalDataList);
			// 原始数据类型
			pstmt.setString(16, order.dataType);
			//产品名称
			pstmt.setString(17, order.productName);

			// 执行插入
			int count=pstmt.executeUpdate();
			if (count==0) {
				logger.error("三级入库 更新行数为0!请检查三级订单对象参数是否合法！");
			}
			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			
			logger.error("三级入库 执行失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			logger.error(e);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return addOrder(order);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 向订单库查询订单
	// 参数1：condition 其值类似于“JobId = ?”
	public synchronized ArrayList<L3InternalOrder> search(String condition) {
		System.out
				.println("L3OrderDB::public ArrayList< L3InternalOrder > search ( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<L3InternalOrder> orderList = new ArrayList<L3InternalOrder>();
		L3InternalOrder l3Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;
			// Test
			// System.out.println(strSql);
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			
			boolean flag=false;
			
			while (rs.next()) {
				flag=true;
				
				l3Order = new L3InternalOrder();
				// 订单ID（L3）
				l3Order.jobId = rs.getString("JobId");
				// 工作流ID
				l3Order.workFlowId = rs.getInt("WorkFlowId");
				// 优先级 0-9
				l3Order.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				l3Order.jobId_L1 = rs.getString("JobId_L1");
				// 二级订单ID（L2）
				l3Order.jobId_L2 = rs.getString("JobId_L2");
				// 用来存放订单类型：CP/FP/AP/VD
				l3Order.orderType = rs.getString("OrderType");
				// 订单级别
				l3Order.orderLevel = rs.getString("OrderLevel");
				// 真实性检验
				l3Order.doValidation = rs.getString("DoValidation");
				// 用来记录生产的共性数据产品名称
				l3Order.productName = rs.getString("productName");
				// 地理区域
				l3Order.geoCoverageStr = rs.getString("geoCoverageStr");
				// 开始时间
				l3Order.startDate = rs.getDate("startDate");
				// 结束时间
				l3Order.endDate = rs.getDate("endDate");
				// 订单状态：Start/Running/Finish/Error/Suspend
				l3Order.workingStatus = rs.getString("workingStatus");
				// 订单的生产状态：RadioNorm /GeoNorm/ CommonProduct/Fusion/Assimilation
				l3Order.orderStatus = rs.getString("orderStatus");
				// 订单的数据状态：Available/NotAvailable/Future
				l3Order.dataStatus = rs.getString("dataStatus");
				// 用来记录订单的数据列表
				l3Order.dataList = new ArrayList<String>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					String[] strDataSplitArray = strDataList.split(";");
					l3Order.dataList.addAll(Arrays.asList(strDataSplitArray));
				}
				// 用来记录提交时间
				l3Order.submitDate = rs.getDate("SubmitDate");
				// 用来记录完成时间
				l3Order.finishDate = rs.getDate("FinishDate");
				// 任务单参数
				l3Order.orderParameter = rs.getString("OrderPARAMETER");
				// 操作员
				l3Order.operatorId = rs.getString("OPERATORID");
				// 算法资源名称
				l3Order.algorithmName = rs.getString("AlgorithmName");
				// 算法资源程序
				l3Order.algorithmPath = rs.getString("AlgorithmPath");
				// 产品列表
				String strDataProduct = rs.getString("dataProductList");
				if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					l3Order.strDataProductList.addAll(Arrays
							.asList(strDataSplitArray));
				} else {
					l3Order.strDataProductList = new ArrayList<String>();
				}
				// 算法资源ID
				l3Order.algorithmID = rs.getInt("AlgorithmID");
				// 与融合/同化相关联的共性产品信息
				l3Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if (!strRetrievalDataList.equals("")) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l3Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				// 原始数据类型
				l3Order.dataType = rs.getString("dataType");
				// 四级订单列表
				l3Order.l4orderlist = rs.getString("l4OrderList");

				orderList.add(l3Order);
			}
			
			if (!flag) {
				logger.error("查询三级订单结果为空!请检查检索参数："+condition+"是否合法!");
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("查询三级订单执行失败!");
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

	public synchronized String generateId(String strOrderType) {
		System.out.println("L3OrderDB::public String generateId() | 生成订单序号");
		String strId = null;
		try {
			String strSql = "SELECT JobId FROM " + this.dbTable
					+ " WHERE JobId LIKE ? ORDER BY JobId ASC";
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return strId;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			format.setLenient(false);
			java.util.Date date = new Date(System.currentTimeMillis());
			String strDate = format.format(date);
			String strJobId_Query = strOrderType + strDate + "____";
			pstmt.setString(1, strJobId_Query);
			ResultSet rs = pstmt.executeQuery();
			
			
			if (rs.last()) {
				strId = rs.getString("JobId");
				Integer iId = Integer
						.valueOf(strId.substring(strId.length() - 4)) + 1;
				// test
				// System.out.println(">>> current ID "+strId.substring(strId.length()-4));

				strId = String.format("%04d", iId);
				// test
				// System.out.println("New Id "+strId);
			} else {
				strId = "0001";
			}
		
			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("生成三级订单ID失败!请检查订单类型："+strOrderType+"参数是否合法！");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return generateId(strOrderType);
			}
			e.printStackTrace();
			return null;
		}

		return strId;
	}

	// 获取三级订单
	public synchronized L3InternalOrder getOrder(String strOrderId) {
		System.out
				.println("L3OrderDB::public L3InternalOrder getOrder( String strOrderId ) | 获取三级订单");

		L3InternalOrder l3Order = null;
		String strQuery = "WHERE JobId = '" + strOrderId + "'";
		ArrayList<L3InternalOrder> orderList = this.search(strQuery);
		if (!orderList.isEmpty()) {
			l3Order = orderList.get(0);
		}

		return l3Order;
	}

	// 获取三级订单共性产品列表（通过二级订单号）
	// public synchronized L3InternalOrder getRetrievalDataListByL2OrderId(
	// String strL2OrderId ){
	public synchronized ArrayList<String> getRetrievalDataListByL2OrderId(
			String strL2OrderId) {
		System.out
				.println("L3OrderDB::public synchronized L3InternalOrder getRetrievalDataListByL2OrderId( String strL2OrderId ) | 获取三级订单共性产品列表（通过二级订单号）");

		ArrayList<L3InternalOrder> l3OrderList = null;
		L3InternalOrder l3Order = null;

		String strQuery = "WHERE JobId_L2 = '" + strL2OrderId
				+ "'and OrderType = 'L3CP' ";
		l3OrderList = this.search(strQuery);
		Iterator<L3InternalOrder> curr_l3Order = l3OrderList.iterator();
		
		boolean flag=false;
		
		if (curr_l3Order.hasNext()) {
			l3Order = curr_l3Order.next();
			flag=true;
		}
		
		if (!flag) {
			logger.error("获取三级订单共性产品列表失败!");
		}
		

		// return l3Order;
		return l3Order.strDataProductList;
	}

	// 设置订单状态
	public synchronized boolean setOrderWorkingStatus(String l3OrderId,
			String l3OrderStatus) {
		System.out
				.println("L3OrderDB::public boolean setOrderWorkingStatus( String l3OrderId, String l3OrderStatus ) | 设置订单状态");

		switch (l3OrderStatus) {
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

			// 订单状态
			pstmt.setString(1, l3OrderStatus);
			// 三级订单ID
			pstmt.setString(2, l3OrderId);

			// 执行更新
			int result=pstmt.executeUpdate();
			if (result==0) {
				logger.error("更新订单执行结果为0：检查订单号"+l3OrderId+"及订单状态："+l3OrderStatus+"是否正确！");
			}
			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("更新订单执行结果失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkingStatus(l3OrderId, l3OrderStatus);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 获取订单状态
	public synchronized String getOrderWorkingStatus(String l3OrderId) {
		System.out
				.println("L3OrderDB::public String getOrderWorkingStatus( String l3OrderId ) | 获取订单状态");

		String strWorkingStatus = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable
					+ " WHERE JobId = ?";
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return strWorkingStatus;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			pstmt.setString(1, l3OrderId);
			ResultSet rs = pstmt.executeQuery();
			
			boolean flag=false;
			
			if (rs.first()) {
				flag=true;				
				// 订单状态：Ready/Finish/Error
				strWorkingStatus = rs.getString("workingStatus");
			}
			if (!flag) {
				logger.error("查询三级订单为空，请检查三级订单号："+l3OrderId+"参数是否正确!");
			}			

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("获取三级订单WorkdingStatus执行失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOrderWorkingStatus(l3OrderId);
			}
			e.printStackTrace();
			return "";
		}

		return strWorkingStatus;
	}

	// 设置订单产品列表
	public synchronized boolean setDataProductList(String l3OrderId,
			ArrayList<String> strDataProductList) {
		System.out
				.println("L3OrderDB::public boolean setDataProductList( String l3OrderId, ArrayList< String > strDataProductList ) | 设置订单产品列表");

		if (null==strDataProductList) {
			logger.error("三级订单产品列表为空，更新失败！");
			return false;
		}
		
		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataProductList = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
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
			pstmt.setString(2, l3OrderId);

			// 执行更新
			int count=pstmt.executeUpdate();
			if (0==count) {
				logger.error("设置订单产品列表执行失败,请检查三级订单号："+l3OrderId+",产品列表参数是否正确!");
			}		

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("设置订单产品列表执行失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataProductList(l3OrderId, strDataProductList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 获取订单产品列表
	public synchronized ArrayList<String> getDataProductList(String l3OrderId) {
		System.out
				.println("L3OrderDB::public ArrayList< String > getDataProductList( String l3OrderId ) | 获取订单产品列表");

		ArrayList<String> strDataProductList = new ArrayList<String>();
		try {
			String strSql = "SELECT * FROM " + this.dbTable
					+ " WHERE JobId = ?";
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return strDataProductList;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			pstmt.setString(1, l3OrderId);
			ResultSet rs = pstmt.executeQuery();
			
			boolean flag=false;
			
			if (rs.first()) {
				flag=true;
				// 产品列表
				String strDataProduct = rs.getString("dataProductList");
				if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					strDataProductList.addAll(Arrays.asList(strDataSplitArray));
				}
			}
			
			if (!flag) {
				logger.error("三级订单"+l3OrderId+"订单产品列表为空！");
				
			}
			

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("获取订单产品列表执行失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getDataProductList(l3OrderId);
			}
			e.printStackTrace();
			strDataProductList.clear();
			return strDataProductList;
		}

		return strDataProductList;
	}

	// 设置拆分子订单列表
	public synchronized boolean setOrderList(String l3OrderId,
			String strOrderList) {
		System.out
				.println("L3OrderDB::public boolean setOrderList( String l3OrderId, String strOrderList ) | 设置订单列表");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET l4OrderList = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			pstmt.setString(1, strOrderList);
			// 订单ID（L3）
			pstmt.setString(2, l3OrderId);

			// 执行更新
			int count=pstmt.executeUpdate();
			if(0==count){
				logger.error("设置拆分三级订单的四级订单列表，更新行数为0，请检查参数三级订单号"+l3OrderId+",子订单列表"+strOrderList+"参数是否合法！");
			}
			

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("设置拆分子订单列表执行失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderList(l3OrderId, strOrderList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 设置算法资源ID
	public synchronized boolean setAlgorithmID(String l3OrderId,
			Algorithm algorithm) {
		System.out
				.println("L3OrderDB::public boolean setAlgorithmID( String l3OrderId, int algorithmId ) | 设置算法资源ID");

		try {
			String strSql = "UPDATE "
					+ this.dbTable
					+ " SET AlgorithmID = ?, AlgorithmName = ?, AlgorithmPath = ? WHERE JobId = ?";

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 算法资源ID
			pstmt.setInt(1, algorithm.getAlgorithmID());
			// 算法资源名称
			pstmt.setString(2, algorithm.getAlgorithmName());
			// 算法资源程序
			pstmt.setString(3, algorithm.getAlgorithmFilePath());
			// 三级订单ID
			pstmt.setString(4, l3OrderId);

			// 执行更新
			if (pstmt.executeUpdate()==0) {
				logger.error("更新算法资源更新行数为0，请检查参数三级订单号"+l3OrderId+",算法参数是否合法！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("设置算法资源ID执行失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setAlgorithmID(l3OrderId, algorithm);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	// 删除三级订单列表
	public synchronized boolean deleteOrder(
			ArrayList<L3InternalOrder> l3OrderList) {
		System.out
				.println("L3OrderDB::public synchronized boolean deleteOrder( ArrayList< L3InternalOrder > l3OrderList ) | 删除三级订单列表");
		try {
			Iterator<L3InternalOrder> l3Order = l3OrderList.iterator();
			while (l3Order.hasNext()) {
				String strSql = "DELETE FROM " + this.dbTable
						+ " WHERE JobId = ?";
				if (null == conn) {
					logger.error("SQL执行连接初始化失败！");
					return false;
				}
				PreparedStatement pstmt = conn.prepareStatement(strSql);
				pstmt.setString(1, l3Order.next().jobId);

				if (pstmt.executeUpdate()==0) {
					logger.error("删除三级订单列表更新行数为0");
				}

				// 关闭相关连接
				pstmt.close();
			}
		} catch (SQLException e) {
			logger.error("删除三级订单列表执行失败!");
			logger.error("SQL执行异常", e);
						
			String strSqlState = e.getSQLState();
			int iErrorCode = e.getErrorCode();
			System.out
					.println("<Error>L3OrderDB::deleteOrder | SQL error code : "
							+ iErrorCode + " | SQL State : " + strSqlState);
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return deleteOrder(l3OrderList);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 查询所需数据列表，返回数据对象列表
	public synchronized ArrayList<RSData> getRSDataList(String l3OrderId) {
		System.out
				.println("L3OrderDB::public ArrayList<RSData> getRSDataList( String l3OrderId ) | 获取三级订单数据列表");

		ArrayList<RSData> RSDataList = new ArrayList<>();
		try {
			String strSql = "SELECT * FROM " + this.dbTable
					+ " WHERE JobId = ?";
			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return RSDataList;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			pstmt.setString(1, l3OrderId);
			ResultSet rs = pstmt.executeQuery();
			
			boolean flag=false;
			
			if (rs.next()) {
				
				flag=true;
				String dataList = rs.getString("datalist");
				// System.out.println("最初数据列表:" + dataList);

				if (!dataList.contains(";")) {
					return null;
				}
				String[] dataIds = dataList.split(";");				

				// 解析数据条目
				for (int i = 0; i < dataIds.length; i++) {
					// 缓存库中查询元数据信息
										
					// 初始化ESData变量
					RSData currentRsData = new RSData();
					
					RSDataList.add(currentRsData);
				}

			}
			
			if (!flag) {
				logger.error("查询所需数据列表，返回结果为空！");
			}

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			logger.error("查询三级订单所需数据列表执行失败!");
			logger.error("SQL执行异常", e);
			
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getRSDataList(l3OrderId);
			}
			e.printStackTrace();
			return null;
		}

		return RSDataList;

	}

}
