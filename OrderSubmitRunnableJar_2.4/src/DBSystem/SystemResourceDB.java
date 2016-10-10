/*
 *程序名称 		: SystemResourceDB.java
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
package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.Timestamp;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import LogSystem.SystemLogger;
import ResourceManage.DataCenter;

/**
 * @author caoyang
 * 
 */
public class SystemResourceDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	/**
	 * 
	 */
	public SystemResourceDB() {
		System.out
				.println("SystemResourceDB::public SystemResourceDB() | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>SystemResourceDB::public SystemResourceDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "systemresourcedb";
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

	// 获取数据中心信息列表（通过数据中心名称）
	public synchronized ArrayList<DataCenter> getDataCenterListByName(
			String strDataCenterName) {
		System.out
				.println("SystemResourceDB::public ArrayList< DataCenter > getDataCenterListByName( String strDataCenterName ) | 获取数据中心信息列表（通过数据中心名称）");

		if (strDataCenterName.isEmpty()) {
			return new ArrayList<DataCenter>();
		}

		String strQuery = "WHERE name = '" + strDataCenterName + "'";
		int iQuantity = -1;

		return search(strQuery, iQuantity);
	}

	// 获取数据中心信息
		public synchronized DataCenter getDataCenter(int iDataCenterId) {
			System.out
					.println("SystemResourceDB::public DataCenter getDataCenter( int iDataCenterId ) | 获取数据中心信息");

			DataCenter dataCenter = null;

			String strQuery = "WHERE resourceId = '" + iDataCenterId + "'";
			int iQuantity = 1;

			ArrayList<DataCenter> dataCenterList = search(strQuery, iQuantity);
			if (!dataCenterList.isEmpty()) {
				dataCenter = dataCenterList.get(0);
			}

			return dataCenter;
		}
	
	// 获取数据中心信息列表（通过数据中心名称）
	public synchronized ArrayList<DataCenter> getDataCenterListByIP(
			String strDataCenterIP) {
		logger.info("SystemResourceDB::public ArrayList< DataCenter > getDataCenterListByIP( String strDataCenterIP ) | 获取数据中心信息列表（通过数据中心IP）");

		if (strDataCenterIP.isEmpty()) {
			return null;
		}

		String strQuery = "WHERE hostip = '" + strDataCenterIP + "'";
		int iQuantity = -1;

		return search(strQuery, iQuantity);
	}

	// 获取数据中心信息列表
	private synchronized ArrayList<DataCenter> search(String strQuery,
			int iQuantity) {

		ArrayList<DataCenter> dataCenterList = new ArrayList<DataCenter>();

		if ((null == strQuery) || (strQuery.isEmpty())) {
			return dataCenterList;
		}

		if ((iQuantity <= 0) && (-1 != iQuantity)) {
			return dataCenterList;
		}

		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + strQuery;
			if (null == conn) {
				return dataCenterList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			int iDataCenterIndex = 0;
			while (rs.next()) {
				DataCenter dataCenter = new DataCenter();

				// 数据中心ID
				dataCenter.setID(rs.getInt("resourceId"));
				// 数据中心名称
				dataCenter.setName(rs.getString("name"));
				// 数据中心类型
				dataCenter.setType(rs.getString("type"));
				// 数据中心ip地址
				dataCenter.setHostIp(rs.getString("hostip"));
				// 数据中心主机名称
				dataCenter.setHostName(rs.getString("hostname"));
				// 数据中心状态
				boolean bIsWorking = false;
				String strIsWorking = rs.getString("status");
				if ("Avaliable".equals(strIsWorking)) {
					bIsWorking = true;
				} else if ("NotAvaliable".equals(strIsWorking)) {
					bIsWorking = false;
				}
				dataCenter.setIsWorking(bIsWorking);
				// 数据中心CPU使用率
				dataCenter.setCPU(rs.getDouble("cpu"));
				// 数据中心内存使用率
				dataCenter.setMemory(rs.getDouble("memory"));
				// 数据中心网络使用率
				dataCenter.setNetWork(rs.getDouble("network"));
				// 数据中心IO使用率
				dataCenter.setIO(rs.getDouble("io"));
				// 数据中心磁盘使用率
				dataCenter.setDiskUsage(rs.getDouble("diskUsage"));
				// 数据中心负载率
				dataCenter.setLoadOne(rs.getDouble("loadone"));
				
				//代理系统部门简称
				dataCenter.setDataBaseSchemasName(rs.getString("DataBaseSchemasName"));
				// 公共缓冲区路径
				dataCenter.setPublicBufferDir(rs.getString("publicBufferDir"));
				// 公共缓冲区使用率
				dataCenter.setPublicBufferUsage(rs.getDouble("publicBufferUsage"));
				// 公共缓冲区大小
				dataCenter.setPublicBufferSize(rs.getDouble("publicBufferSize"));
				// Globus中间件安装路径
				dataCenter.setGLOBUS_HOME(rs.getString("GLOBUS_HOME"));
				// GridFTP Server端口
				dataCenter.setGridFTPServerPort(rs.getString("GridFTPServerPort"));
				// 子中心执行代理服务器端口
				dataCenter.setProxySystemPort(rs.getString("ProxySystemPort"));
				// 用户家目录
				dataCenter.setUserHomePath(rs.getString("UserHomePath"));

				dataCenterList.add(dataCenter);

				if (((++iDataCenterIndex) >= iQuantity) && (-1 != iQuantity)) {
					break;
				}
			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(strQuery, iQuantity);
			}
			e.printStackTrace();
			dataCenterList.clear();
			logger.error("获取数据中心信息列表失败！");
			return dataCenterList;
		}

		return dataCenterList;
	}

	public ArrayList<DataCenter> searchByCondition(String condition) {

		ArrayList<DataCenter> dataCenterList = new ArrayList<DataCenter>();
		dataCenterList=search(condition, 1);	

		return dataCenterList;
	}

}
