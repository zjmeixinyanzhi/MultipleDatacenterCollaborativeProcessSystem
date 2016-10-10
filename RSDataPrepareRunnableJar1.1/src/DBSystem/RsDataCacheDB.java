package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import DBSystem.DBConn;
import RSDataInfo.RSData;
import LogSystem.SystemLogger;

public class RsDataCacheDB extends DBConn {
	private static Connection conn = null;
	private String dbTable = null;
	// 日志系统
	private Logger logger = LogSystem.SystemLogger.getInstance().getSysLogger();

	public RsDataCacheDB() {

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>RsDataCacheDB::public RsDataCacheDB() | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "rsdatacachedb";
	}

	// 关闭连接
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

	// 向缓存库查询缓存数据
	// 参数1：condition 其值类似于“filename = ?”
	public synchronized ArrayList<RSData> search(String condition) {
		System.out
				.println("RsDataCacheDB::public ArrayList< RSData > search( String condition ) | 向缓存库查询数据记录");
		if (null == condition) {
			condition = "";
		}

		ArrayList<RSData> rsDataList = new ArrayList<RSData>();
		RSData tempData = null;
		try {
			String strSql = "SELECT * FROM " + this.dbTable + " " + condition;

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return rsDataList;
			}

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			if (!rs.next()) {
				logger.error("向缓存库查询缓存数据:查询结果为0！");
			} else {
				rs.previous();
				while (rs.next()) {
					tempData = new RSData();
					//DataId
					tempData.dataid=rs.getString("dataid");
					// 文件ID
					tempData.fileid = rs.getInt("fileid");
					// 数据文件名称
					tempData.filename = rs.getString("filename");
					// 创建时间
					tempData.createtime = rs.getString("createtime");
					// 卫星平台
					tempData.spacecraft = rs.getString("spacecraft");
					// 传感器
					tempData.sensor = rs.getString("sensor");
					// 几何分辨率
					tempData.resolution = rs.getDouble("resolution");
					// 四角坐标
					tempData.ullat = rs.getDouble("ullat");
					tempData.ullon = rs.getDouble("ullon");
					tempData.urlat = rs.getDouble("urlat");
					tempData.urlon = rs.getDouble("urlon");
					tempData.lllat = rs.getDouble("lllat");
					tempData.lllon = rs.getDouble("lllon");
					tempData.lrlat = rs.getDouble("lrlat");
					tempData.lrlon = rs.getDouble("lrlon");
					// 空间范围
					tempData.bounding = rs.getString("bounding");
					// 数据格式
					tempData.format = rs.getString("format");
					// 数据文件大小
					tempData.datasize = rs.getDouble("datasize");
					// 数据提供商
					tempData.provider = rs.getString("provider");
					// 含云量
					tempData.cloudcover = rs.getDouble("cloudcover");
					// 文件路径
					tempData.filepath = rs.getString("filepath");
					// 数据请求信息
					tempData.requestInfo = rs.getString("requestInfo");
					// 添加数据记录
					rsDataList.add(tempData);
				}
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("缓存库查询缓存数据失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return search(condition);
			}
			e.printStackTrace();
			rsDataList.clear();
			return rsDataList;
		}
		return rsDataList;

	}
	
	//检验数据状态是否可用
	public synchronized boolean isAvaliable(String dataId) {
		
		String status="unKnown";
		try {
			String strSql = "SELECT datastatus FROM " + this.dbTable + " where dataid='"+dataId+"'";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			if (!rs.next()) {
				logger.error("向缓存库查询缓存数据:查询结果为0！该数据不存在");
				return false;
			} else {
				rs.previous();
				while (rs.next()) {
					status= rs.getString("datastatus");
				}
			}

			// 关闭相关连接
			st.close();
			rs.close();
		} catch (SQLException e) {
			logger.error("缓存库查询缓存数据失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return isAvaliable(dataId);
			}
			e.printStackTrace();
			return false;
		}
		
		return status.equals("Available");

	}
	

	// 通过名称查找缓存数据
	public synchronized RSData getDataByFilename(String filename) {
		System.out
				.println("RsDataCacheDB::public synchronized RsDataCacheDB getDataByFilename( String filename ) | 根据数据名获取缓存数据记录");

		RSData tempData = null;

		String strQuery = "WHERE filename = '" + filename + "'";
		// test
		// System.out.println(strQuery);
		ArrayList<RSData> orderList = this.search(strQuery);
		if (!orderList.isEmpty()) {
			tempData = orderList.get(0);
		}
		return tempData;
	}

	// 更新数据请求信息
	public synchronized boolean updateDataRequestInfo(String filename,
			String requestInfo) {
		System.out
				.println("RsDataCacheDB::public boolean updateDataRequestInfo(String requestInfo) | 更新数据请求");
		try {
			String strSql = "UPDATE " + this.dbTable
					+ " SET requestInfo = ? WHERE filename = ?";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 数据请求信息
			pstmt.setString(1, requestInfo);
			// 文件名
			pstmt.setString(2, filename);

			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return updateDataRequestInfo(filename, requestInfo);
			}
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 插入缓存数据
	public synchronized boolean addData(RSData rsData) {
		System.out
				.println("RsDataCacheDB::public boolean addData(RSData rsData) | 插入新的缓存数据");
		// 插入新的缓存数据
		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(fileid,filename,createtime,spacecraft,sensor,resolution,"
					+ "ullat,ullon,urlat,urlon,lllat,lllon,lrlat,lrlon,bounding,format,datasize,provider,cloudcover,filepath,requestInfo)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			int count = 0;
			// 文件ID
			// 获取当前请求库中最大Id
			pstmt.setLong(++count, Long.parseLong(this.searchMaxFileid()) + 1);
			// 数据文件名称
			pstmt.setString(++count, rsData.filename);
			// 创建时间
			pstmt.setString(++count, rsData.createtime);
			// 卫星平台
			pstmt.setString(++count, rsData.spacecraft);
			// 传感器
			pstmt.setString(++count, rsData.sensor);
			// 几何分辨率
			pstmt.setDouble(++count, rsData.resolution);
			// 四角坐标
			pstmt.setDouble(++count, rsData.ullat);
			pstmt.setDouble(++count, rsData.ullon);
			pstmt.setDouble(++count, rsData.urlat);
			pstmt.setDouble(++count, rsData.urlon);
			pstmt.setDouble(++count, rsData.lllat);
			pstmt.setDouble(++count, rsData.lllon);
			pstmt.setDouble(++count, rsData.lrlat);
			pstmt.setDouble(++count, rsData.lrlon);
			// 空间范围
			pstmt.setString(++count, rsData.bounding);
			// 数据格式
			pstmt.setString(++count, rsData.format);
			// 数据文件大小
			pstmt.setDouble(++count, rsData.datasize);
			// 数据提供商
			pstmt.setString(++count, rsData.provider);
			// 含云量
			pstmt.setDouble(++count, rsData.cloudcover);
			// 文件路径
			pstmt.setString(++count, rsData.filepath);
			// 数据请求信息
			pstmt.setString(++count, rsData.requestInfo);

			// 执行插入
			// pstmt.executeUpdate();//返回值为成功后的函数
			pstmt.execute();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			logger.error("缓存库查询缓存数据失败!");
			logger.error("SQL执行异常", e);
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return addData(rsData);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 插入缓存数据:从数据中心获取的部分元数据信息
	public synchronized boolean addData2(RSData rsData) {
		logger.info("RsDataCacheDB::public boolean addData(RSData rsData) | 插入新的缓存数据");
		//插入前首先检测数据是否存在
		String condition=" where dataid='"+rsData.dataid+"'";
		ArrayList<RSData> existDatalists=new ArrayList<RSData>();
		existDatalists=search(condition);
		if (existDatalists.size()>0) {
			logger.info(rsData.dataid+"数据已经存在，无需重新插入！");
			return true;
		}		
		
		// 插入新的缓存数据
		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(fileid,filename,createtime,spacecraft,sensor,resolution,"
					+ "ullat,ullon,urlat,urlon,lllat,lllon,lrlat,lrlon,bounding,format,datasize,provider,cloudcover,filepath,dataid)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			int count = 0;
			// 文件ID
			// 获取当前请求库中最大Id
			pstmt.setLong(++count, Long.parseLong(this.searchMaxFileid()) + 1);
			// 数据文件名称
			pstmt.setString(++count, rsData.filename);
			// 创建时间
			pstmt.setString(++count, rsData.createtime);
			// 卫星平台
			pstmt.setString(++count, rsData.spacecraft);
			// 传感器
			pstmt.setString(++count, rsData.sensor);
			// 几何分辨率
			pstmt.setDouble(++count, rsData.resolution);
			// 四角坐标
			pstmt.setDouble(++count, rsData.ullat);
			pstmt.setDouble(++count, rsData.ullon);
			pstmt.setDouble(++count, rsData.urlat);
			pstmt.setDouble(++count, rsData.urlon);
			pstmt.setDouble(++count, rsData.lllat);
			pstmt.setDouble(++count, rsData.lllon);
			pstmt.setDouble(++count, rsData.lrlat);
			pstmt.setDouble(++count, rsData.lrlon);
			// 空间范围
			pstmt.setString(++count, rsData.bounding);
			// 数据格式
			pstmt.setString(++count, rsData.format);
			// 数据文件大小
			pstmt.setDouble(++count, rsData.datasize);
			// 数据提供商
			pstmt.setString(++count, rsData.provider);
			// 含云量
			pstmt.setDouble(++count, rsData.cloudcover);
			// 文件路径
			pstmt.setString(++count, rsData.filepath);
			// 数据Id
			pstmt.setString(++count, rsData.dataid);
//			System.out.println(rsData.dataid);
			// 执行插入
			// pstmt.executeUpdate();//返回值为成功后的函数
			pstmt.execute();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return addData2(rsData);
			}
			e.printStackTrace();
			logger.error("插入缓存数据!\n" + e);
			return false;
		}
		return true;
	}

	// 更新数据获取信息
	public synchronized boolean updateDataAcquireInfos(String dataId,
			String fileName, String status, String url, long datasize,
			String filePath) {
		logger.info("RsDataCacheDB::public boolean updateDataRequestInfo(String requestInfo) | 更新数据获取链接及数据状态");
		try {
			String strSql = "UPDATE "
					+ this.dbTable
					+ " SET fileName = ?,datastatus= ?, downloadurl=?, datasize=?, filepath=? WHERE dataid = ?";

			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 文件名
			pstmt.setString(1, fileName);
			// 数据状态
			pstmt.setString(2, status);
			// 文件下载路径
			pstmt.setString(3, url);
			// 数据大小
			pstmt.setFloat(4, datasize);
			// 文件路径
			pstmt.setString(5, filePath);
			// 数据Id
			pstmt.setString(6, dataId);
			// 执行更新
			pstmt.executeUpdate();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return updateDataAcquireInfos(dataId, fileName, status, url,
						datasize, filePath);
			}
			e.printStackTrace();
			logger.error("更新数据请求信息失败！\n" + e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("更新数据请求信息失败！\n" + e);
			return false;
		}
		return true;
	}

	// 删除数据
	public synchronized boolean deleteData(RSData rsData) {

		return true;
	}

	// 查询当前数据Id最大值
	public synchronized String searchMaxFileid() {
		String maxId = "-1";
		try {
			String strSql = "SELECT MAX(fileid) FROM " + this.dbTable;

			if (null == conn) {
				logger.error("插入缓存数据失败！");
				return "-1";
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			// 执行查询
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				maxId = rs.getString("MAX(fileid)");
				if (null == maxId) {
					maxId = "0";
				}
				// System.out.println("the MAX(fileid) is " + maxId);
			}
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return searchMaxFileid();
			}
			e.printStackTrace();
			return "-1";
		}
		return maxId;
	}

}
