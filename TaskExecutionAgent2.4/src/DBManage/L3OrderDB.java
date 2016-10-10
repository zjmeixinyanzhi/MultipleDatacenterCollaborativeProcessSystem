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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.net.TimeTCPClient;
import org.apache.log4j.Logger;

import sun.java2d.opengl.OGLContext;
import OrderManage.L3InternalOrder;
import RSDataManage.RSData;
import RSDataManage.RSDataProcess;
import TaskExeAgent.SystemLogger;

/**
 * @author caoyang
 * 
 */
public class L3OrderDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public L3OrderDB() {
		logger.info("L3OrderDB::public L3OrderDB() | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				logger.error("<Error>L3OrderDB::public L3OrderDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "l3orderdb";
	}

	protected void finalize() {
		// try{
		// conn.close();
		// } catch( SQLException e ){
		// String strSqlState = e.getSQLState();
		// if( strSqlState.equals( "08S01" ) ){
		// return;
		// }
		// e.printStackTrace();
		// }
	}

	public static synchronized void closeConnected() {
		try {
			if (null != conn) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 向订单库插入订单
	public synchronized boolean addOrder(L3InternalOrder order) {
		logger.info("L3OrderDB::public boolean addOrder( L2ExternalOrder order ) | 向订单库插入订单");

		// 将订单插入数据库的订单表中
		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(JobId,JobId_L1,JobId_L2,OrderType,OrderLevel,productName,"
					+ "geoCoverageStr,startDate,endDate,workingStatus,dataList,SubmitDate,AlgorithmName,AlgorithmPath,dataProductList,retrievalDataList,ReSubmitTimes)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 订单ID（L3）
			pstmt.setString(1, order.jobId);
			// 一级订单ID（L1）
			pstmt.setString(2, order.jobId_L1);
			// 二级订单ID（L2）
			pstmt.setString(3, order.jobId_L2);
			// 用来存放订单类型
			pstmt.setString(4, order.orderType);
			// 订单级别
			pstmt.setString(5, order.orderLevel);
			// 产品名称
			pstmt.setString(6, order.productName);
			// 地理区域
			pstmt.setString(7, order.geoCoverageStr);
			// 时相（成像时间）开始时间
			pstmt.setTimestamp(8, new Timestamp(order.startDate.getTime()));
			// 时相（成像时间）结束时间
			pstmt.setTimestamp(9, new Timestamp(order.endDate.getTime()));
			// 订单状态
			pstmt.setString(10, order.workingStatus);
			// 数据列表
			if (order.dataList.size() > 0) {
				Iterator<RSData> iterator = order.dataList.iterator();
				String strDataList = "";
				while (iterator.hasNext()) {
					RSData rsData = (RSData) iterator.next();
					strDataList += rsData.getRSDataString();
				}
				pstmt.setString(11, strDataList);
			} else {
				// 如果没有获取到数据列表就赋值为空字符串
				pstmt.setString(11, "");
			}
			// 订单提交时间
			pstmt.setTimestamp(12, new Timestamp(order.submitDate.getTime()));
			// 算法资源名称
			pstmt.setString(13, order.algorithmName);
			// 算法资源程序
			pstmt.setString(14, order.algorithmPath);
			// 产品列表
			if ((null != order.strDataProductList)
					&& (order.strDataProductList.size() > 0)) {
				String[] strList = new String[order.strDataProductList.size()];
				order.strDataProductList.toArray(strList);
				pstmt.setString(15, String.join(";", strList));
			} else {
				pstmt.setString(15, "");
			}
			// 与融合/同化相关联的共性产品信息
			String strRetrievalDataList = "";
			if (order.retrievalDataList != null
					&& !order.retrievalDataList.isEmpty()) {
				for (Iterator<String> data_curr = order.retrievalDataList
						.iterator(); data_curr.hasNext();) {
					strRetrievalDataList += data_curr.next() + ";";
				}
			}
			pstmt.setString(16, strRetrievalDataList);
			pstmt.setInt(17, order.reSubmitTimes);

			// test
			// System.out.println(strSql);
			// System.out.println(pstmt.toString());

			// 执行插入
			pstmt.execute();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			// if( ( strSqlState.equals( "08S01" ) ) || ( strSqlState.equals(
			// "40001" ) ) )
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return addOrder(order);
			}
			e.printStackTrace();
			logger.error("向订单库插入订单失败！\n" + e);
			return false;
		}
		return true;
	}

	// 向订单库查询订单
	// 参数1：condition 其值类似于“JobId = ?”
	public synchronized ArrayList<L3InternalOrder> search(String condition) {
		logger.info("L3OrderDB::public ArrayList< L3InternalOrder > search ( String condition ) | 向订单库查询订单");

		if (null == condition) {
			condition = "";
		}

		ArrayList<L3InternalOrder> orderList = new ArrayList<L3InternalOrder>();
		L3InternalOrder l3Order = null;
		try {
			// 这个后期应该会被修改 不能通过这样的形式去查询数据库
			String strSql = "SELECT * FROM " + this.dbTable + " WHERE "
					+ condition;

			if (null == conn) {
				logger.error("SQL执行连接初始化失败！");
				return orderList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			while (rs.next()) {
				l3Order = new L3InternalOrder();
				// 订单ID（L3）
				l3Order.jobId = rs.getString("JobId");
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
				// 用来记录订单的数据列表
				l3Order.dataList = new ArrayList<RSData>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					RSDataProcess process = new RSDataProcess(strDataList);
					process.doProcess();
					l3Order.dataList = process.DataList;
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
				l3Order.strDataProductList = new ArrayList<String>();
				if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					l3Order.strDataProductList.addAll(Arrays
							.asList(strDataSplitArray));
				}

				// PBS订单列表
				String strPbsOrderLists = rs.getString("PbsOrderLists");
				l3Order.pbsOrderLists = new ArrayList<String>();
				if (!strPbsOrderLists.equals("")) {
					String[] strDataSplitArray = strPbsOrderLists.split(";");
					l3Order.pbsOrderLists.addAll(Arrays
							.asList(strDataSplitArray));
				}
				
				//重新提交次数
				l3Order.reSubmitTimes=rs.getInt("ReSubmitTimes");

				/*
				 * else{ l3Order.strDataProductList = new ArrayList< String >();
				 * }
				 */
				// 与融合/同化相关联的共性产品信息
				l3Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if ((null != strRetrievalDataList)
						&& (!strRetrievalDataList.equals(""))) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l3Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}

				orderList.add(l3Order);
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
			orderList.clear();
			logger.error("向订单库插入订单失败！\n" + e);
			return orderList;
		}
		return orderList;
	}

	// 获取三级订单列表
	public synchronized ArrayList<L3InternalOrder> getOrderList(String status) {
		logger.info("L3OrderDB::public ArrayList< L3InternalOrder > getOrderList(String status) | 获取三级订单列表");
		ArrayList<L3InternalOrder> l3OrderList = new ArrayList<L3InternalOrder>();

		L3InternalOrder l3Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable
					+ " WHERE workingStatus = '" + status + "'";

			if (null == conn) {
				return l3OrderList;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				l3Order = new L3InternalOrder();
				// 订单ID（L3）
				l3Order.jobId = rs.getString("JobId");
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
				// 用来记录生产的共性数据产品名称
				l3Order.productName = rs.getString("productName");
				// 地理区域
				l3Order.geoCoverageStr = rs.getString("geoCoverageStr");
				// 开始时间
				l3Order.startDate = rs.getDate("startDate");
				// 结束时间
				l3Order.endDate = rs.getDate("endDate");
				// 订单状态：Ready/Finish/Error
				l3Order.workingStatus = rs.getString("workingStatus");
				// 用来记录订单的数据列表
				l3Order.dataList = new ArrayList<RSData>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					RSDataProcess process = new RSDataProcess(strDataList);
					process.doProcess();
					l3Order.dataList = process.getDataList();
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
				l3Order.strDataProductList = new ArrayList<String>();
				if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					l3Order.strDataProductList.addAll(Arrays
							.asList(strDataSplitArray));
				} else {
					l3Order.strDataProductList = new ArrayList<String>();
				}

				// 与融合/同化相关联的共性产品信息
				l3Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if ((null != strRetrievalDataList)
						&& (!strRetrievalDataList.equals(""))) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l3Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				
				
				// PBS订单列表
				String strPbsOrderLists = rs.getString("PbsOrderLists");
				
				l3Order.pbsOrderLists = new ArrayList<String>();
				if (strPbsOrderLists==null) {
					
				}
				else if(!strPbsOrderLists.equals("")) {
					String[] strDataSplitArray = strPbsOrderLists.split(";");
					l3Order.pbsOrderLists.addAll(Arrays
							.asList(strDataSplitArray));
				}
				
				//重新提交次数
				l3Order.reSubmitTimes=rs.getInt("ReSubmitTimes");
				
				//test
//				System.out.println("__"+l3Order.pbsOrderLists.size()+" "+l3Order.pbsOrderLists.get(0)+"_");

				l3OrderList.add(l3Order);
			}

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOrderList(status);
			}
			e.printStackTrace();
			l3OrderList.clear();
			logger.error("获取三级订单列表失败！");
			return l3OrderList;
		}

		return l3OrderList;
	}

	// 获取三级订单
	public synchronized L3InternalOrder getOrder(String strOrderId) {
		logger.info("L3OrderDB::public L3InternalOrder getOrder( String strOrderId ) | 获取三级订单");

		L3InternalOrder l3Order = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable
					+ " WHERE JobId = ?";

			if (null == conn) {
				return l3Order;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			pstmt.setString(1, strOrderId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				l3Order = new L3InternalOrder();
				// 订单ID（L3）
				l3Order.jobId = rs.getString("JobId");
				// 优先级 0-9
				l3Order.priority = rs.getInt("Priority");
				// 一级订单ID（L1）
				l3Order.jobId_L1 = rs.getString("JobId_L1");
				// 一级订单ID（L2）
				l3Order.jobId_L2 = rs.getString("JobId_L2");
				// 用来存放订单类型：CP/FP/AP/VD
				l3Order.orderType = rs.getString("OrderType");
				// 订单级别
				l3Order.orderLevel = rs.getString("OrderLevel");
				// 用来记录生产的共性数据产品名称
				l3Order.productName = rs.getString("productName");
				// 地理区域
				l3Order.geoCoverageStr = rs.getString("geoCoverageStr");
				// 开始时间
				l3Order.startDate = rs.getDate("startDate");
				// 结束时间
				l3Order.endDate = rs.getDate("endDate");
				// 订单状态：Ready/Finish/Error
				l3Order.workingStatus = rs.getString("workingStatus");
				// 用来记录订单的数据列表
				l3Order.dataList = new ArrayList<RSData>();
				String strDataList = rs.getString("dataList");
				if (!strDataList.equals("")) {
					RSDataProcess process = new RSDataProcess(strDataList);
					process.doProcess();
					l3Order.dataList = process.DataList;
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
				l3Order.strDataProductList = new ArrayList<String>();
				if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					l3Order.strDataProductList.addAll(Arrays
							.asList(strDataSplitArray));
				}/*
				 * else{ l3Order.strDataProductList = new ArrayList< String >();
				 * }
				 */
				// 与融合/同化相关联的共性产品信息
				l3Order.retrievalDataList = new ArrayList<String>();
				String strRetrievalDataList = rs.getString("retrievalDataList");
				if ((null != strRetrievalDataList)
						&& (!strRetrievalDataList.equals(""))) {
					String[] strDataSplitArray = strRetrievalDataList
							.split(";");
					l3Order.retrievalDataList.addAll(Arrays
							.asList(strDataSplitArray));
				}
				
				
				// PBS订单列表
				String strPbsOrderLists = rs.getString("PbsOrderLists");
				l3Order.pbsOrderLists = new ArrayList<String>();
				if (!strPbsOrderLists.equals("")) {
					String[] strDataSplitArray = strPbsOrderLists.split(";");
					l3Order.pbsOrderLists.addAll(Arrays
							.asList(strDataSplitArray));
				}
				
				//重新提交次数
				l3Order.reSubmitTimes=rs.getInt("ReSubmitTimes");
				
			}

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOrder(strOrderId);
			}
			e.printStackTrace();
			logger.info("获取三级订单失败！\n" + e);
			return null;
		}

		return l3Order;
	}

	// 设置订单状态
	public synchronized boolean setOrderWorkflowStatus(String l3OrderId,
			String l3OrderStatus) {
		logger.info("L3OrderDB::public boolean setOrderWorkflowStatus( String l3OrderId, String l3OrderStatus ) | 设置订单状态");

		switch (l3OrderStatus) {
		case "Ready":
			break;
		case "Finish":
			break;
		case "Error":
			break;
		case "Running":
			break;
		case "ReSubmitting":
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
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setOrderWorkflowStatus(l3OrderId, l3OrderStatus);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置订单" + l3OrderId + "状态失败！\n" + e);
			return false;
		}

		return true;
	}

	// 设置订单状态
	public synchronized boolean setItemValue(String condition) {
		logger.info("L3OrderDB::public boolean setItemValue(String strSql) | 订单值更新");		
		try {
			String strSql = "UPDATE " + this.dbTable + " " + condition;
			
//			//test
			logger.info(">>"+strSql);

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 执行更新
			if (pstmt.executeUpdate()==0) {
				logger.info("L3Order订单中新的PBS Order更新失败！！");
			}

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setItemValue(condition);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置订单特定值失败！\n" + e);
			return false;
		}

		return true;
	}

	// 删除列表中的三级订单
	public synchronized boolean deleteOrder(
			ArrayList<L3InternalOrder> l3OrderList) {
		logger.info("L3OrderDB::public boolean deleteOrder( ArrayList< L3InternalOrder > l3OrderList ) | 删除列表中的三级订单");

		try {
			Iterator<L3InternalOrder> order_curr = l3OrderList.iterator();
			while (order_curr.hasNext()) {
				String strSql = "DELETE FROM " + this.dbTable
						+ " WHERE JobId = ?";

				if (null == conn) {
					return false;
				}
				PreparedStatement pstmt = conn.prepareStatement(strSql);
				pstmt.setString(1, order_curr.next().jobId);

				pstmt.execute();

				// 关闭相关连接
				pstmt.close();
			}
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return deleteOrder(l3OrderList);
			}
			e.printStackTrace();
			logger.error("删除列表中的三级订单失败！" + e);
			return false;
		}
		return true;
	}

	// 设置订单产品列表
	public synchronized boolean setDataProductList(String l3OrderId,
			ArrayList<String> strDataProductList) {
		logger.info("L3OrderDB::public boolean setDataProductList( String l3OrderId, ArrayList< String > strDataProductList ) | 设置订单产品列表");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET dataProductList = ? WHERE JobId = ?";

			if (null == conn) {
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
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataProductList(l3OrderId, strDataProductList);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置订单产品列表失败！" + e);
			return false;
		}
		return true;
	}

	// 设置订单产品列表
	public synchronized boolean setPbsOrderLists(String l3OrderId,
			ArrayList<String> pbsorderLists) {
		logger.info("L3OrderDB::public boolean setPbsOrderLists( String l3OrderId, ArrayList<String> pbsorderLists ) | 设置订单产品列表");

		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET PbsOrderLists = ? WHERE JobId = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 产品列表
			if (pbsorderLists.size() > 0) {
				String[] strList = new String[pbsorderLists.size()];
				pbsorderLists.toArray(strList);
				pstmt.setString(1, String.join(";", strList));
			} else {
				// 如果没有获取到数据列表就赋值为空字符串
				pstmt.setString(1, "");
			}
			// 订单ID（L3）
			pstmt.setString(2, l3OrderId);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return setDataProductList(l3OrderId, pbsorderLists);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("设置PBS订单列表失败！" + e);
			return false;
		}
		return true;
	}

	// 获取订单产品列表
	public synchronized ArrayList<String> getDataProductList(String l3OrderId) {
		logger.info("L3OrderDB::public ArrayList< String > getDataProductList( String l3OrderId ) | 获取订单产品列表");

		ArrayList<String> strDataProductList = new ArrayList<String>();
		try {
			String strSql = "SELECT * FROM " + this.dbTable
					+ " WHERE JobId = ?";

			if (null == conn) {
				return strDataProductList;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			pstmt.setString(1, l3OrderId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				// 产品列表
				String strDataProduct = rs.getString("dataProductList");
				if (!strDataProduct.equals("")) {
					String[] strDataSplitArray = strDataProduct.split(";");
					strDataProductList.addAll(Arrays.asList(strDataSplitArray));
				}
			}

			// 关闭相关连接
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getDataProductList(l3OrderId);
			}
			e.printStackTrace();
			strDataProductList.clear();
			logger.error("获取订单产品列表失败！" + e);
			return strDataProductList;
		}

		return strDataProductList;
	}
}
