package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import domain.BDProduct;
//import domain.StandarProduct;
import domain.StandarProduct;

public class BDProductDB {


	private static Connection conn = null;
	private static DBConn dbcommCommon = null;
	public String dbTable = null;

	public BDProductDB(String ip, String port, String mccps_sid,
			String user, String passwd, String dbTable) {
		// 初始化数据库连接 这个连接可能与上一个连接不一致，
		// 因为，用户名密码可能会改变，一定要注意！！！！
		this.dbcommCommon = new DBConn(ip, port, mccps_sid, user, passwd);

		// 初始化连接数据库
		if (null == conn) {
			conn = dbcommCommon.getConnection();
			if (conn == null) {
				dbcommCommon.bIsConnection = false;
			} else {
				dbcommCommon.bIsConnection = true;
			}
		}

		this.dbTable = dbTable;
		
		System.out.println("插入的标准产品库表为："+this.dbTable);

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
	//select
	public String select(String parameter1,String parameter2,String parameter3){
		String  queryResults=null;
		try {
			String strSql = "SELECT "+parameter1+" FROM "+this.dbTable+" where " +parameter2+"= ?";
			if(conn==null){
				return null;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			pstmt.setString(1, parameter3);
//			System.out.println(strSql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				queryResults = rs.getString(parameter1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println(queryResults);

		return queryResults;
	}
	
	public synchronized boolean upData(BDProduct bdProduct) {
		System.out
				.println("RsDataCacheDB::public boolean addData(RSData rsData) | 插入新的缓存数据");
		// 插入新的缓存数据
		try {
//			String strSql = "INSERT INTO "
//					+ this.dbTable
//					+ "(Name,Date,DiskId,InnerPrefix,DeleteFlag,DeleteOrderId,BackupFlag,BackupOrderId,ImportDate,ImportFlag,ImportOrderId,RawDataId,Area,GridId)"
//					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			System.out.println("abc"+this.dbTable);
//			String strSql = "INSERT INTO " + this.dbTable +"(Id,Name,Date,HostName,InnerPrefix,InnerSuffix,MD5Code,ImportDate,ImportFlag,ImportOrderId,ProductionCount,Area,QPTypeId,GridId) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			String strSql = "INSERT INTO " + this.dbTable +"(Name,Date,HostName,InnerPrefix,InnerSuffix,MD5Code,ImportDate,ImportFlag,ImportOrderId,ProductionCount,Area,QPTypeId,GridId) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String strSql = "UPDATE " + this.dbTable +" set InnerSuffix = ? where Name = ?";
//			System.out.println(strSql);
			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql,Statement.RETURN_GENERATED_KEYS);
			int count = 0;
			// 文件ID
			// 获取当前请求库中最大Id
//			pstmt.setLong(++count, Long.parseLong(this.searchMaxFileid()) + 1);
//			System.out.println("   "+this.searchMaxFileid()+1);
//			pstmt.setLong(++count, commonProduct.Id);

			// 获取时间
//			pstmt.setTimestamp(++count, new Timestamp(commonProduct.Date.getTime()));
//			// DiskId
//			pstmt.setInt(++count, standardProduct.DiskId);
			//主机名
//			pstmt.setString(++count,commonProduct.HostName);
			// 位置
//			pstmt.setString(++count, commonProduct.InnerPrefix);
//			// 删除标记
//			pstmt.setDouble(++count, standardProduct.DeleteFlag);
			//InnerSuffix
			pstmt.setString(++count, bdProduct.InnerSuffix);
//			System.out.println("!@#$%^&*()_"+commonProduct.InnerSuffix);
			// 数据文件名称
			pstmt.setString(++count, bdProduct.Name);
			//MD5Code
//			pstmt.setLong(++count, commonProduct.MD5Code);
//			// 删除订单Id
//			pstmt.setDouble(++count, standardProduct.DeleteOrderId);
//			//BackupFlag
//			pstmt.setInt(++count, standardProduct.BackupFlag);
//			// BackupOrderId
//			pstmt.setLong(++count, standardProduct.BackupOrderId);
			// ImportDate
//			pstmt.setTimestamp(++count, new Timestamp(commonProduct.ImportDate.getTime()));
			// ImportFlag
//			pstmt.setLong(++count, commonProduct.ImportFlag);
			// ImportOrderId
//			pstmt.setLong(++count, commonProduct.ImportOrderId);
			// RawDataId
//			pstmt.setLong(++count, commonProduct.ProductionCount);
			// Area
//			pstmt.setString(++count, commonProduct.Area);
			//SPTypeId
//			pstmt.setLong(++count, commonProduct.QPTypeId);
			// GridId
//			System.out.println("GridId="+commonProduct.GridId);
//			pstmt.setLong(++count, commonProduct.GridId);
			
			// 执行插入
//			 pstmt.executeUpdate();//返回值为成功后的函数
//			System.out.println(strSql);
			pstmt.execute();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = dbcommCommon.getConnection();
				return upData(bdProduct);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 插入缓存数据
	public synchronized boolean addData(BDProduct BdProduct) {
		System.out
				.println("RsDataCacheDB::public boolean addData(RSData rsData) | 插入新的缓存数据");
		// 插入新的缓存数据
		try {
//			String strSql = "INSERT INTO "
//					+ this.dbTable
//					+ "(Name,Date,DiskId,InnerPrefix,DeleteFlag,DeleteOrderId,BackupFlag,BackupOrderId,ImportDate,ImportFlag,ImportOrderId,RawDataId,Area,GridId)"
//					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			System.out.println("abc"+this.dbTable);
//			String strSql = "INSERT INTO " + this.dbTable +"(Id,Name,Date,HostName,InnerPrefix,InnerSuffix,MD5Code,ImportDate,ImportFlag,ImportOrderId,ProductionCount,Area,QPTypeId,GridId) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			String strSql = "INSERT INTO " + this.dbTable 
					+"(Name,Date,ImportDate,InnerPrefix,BDTypeId,GridId) VALUES(?,?,?,?,?,?)";
			if (null == conn) {
				return false;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql,Statement.RETURN_GENERATED_KEYS);
			int count = 0;
			// 文件ID
			// 获取当前请求库中最大Id
//			pstmt.setLong(++count, Long.parseLong(this.searchMaxFileid()) + 1);
//			System.out.println("   "+this.searchMaxFileid()+1);
//			pstmt.setLong(++count, commonProduct.Id);
			// 数据文件名称
			pstmt.setString(++count, BdProduct.Name);
			// 获取时间
			pstmt.setTimestamp(++count, new Timestamp(BdProduct.Date.getTime()));
//			// DiskId
//			pstmt.setInt(++count, standardProduct.DiskId);
			// ImportDate
			pstmt.setTimestamp(++count, new Timestamp(BdProduct.ImportDate.getTime()));
			//主机名
//			pstmt.setString(++count,BdProduct.HostName);
			// 位置
			pstmt.setString(++count, BdProduct.InnerPrefix);
//			// 删除标记
//			pstmt.setDouble(++count, standardProduct.DeleteFlag);
			//InnerSuffix
//			pstmt.setString(++count, BdProduct.InnerSuffix);
			//MD5Code
//			pstmt.setLong(++count, BdProduct.MD5Code);
//			// 删除订单Id
//			pstmt.setDouble(++count, standardProduct.DeleteOrderId);
//			//BackupFlag
//			pstmt.setInt(++count, standardProduct.BackupFlag);
//			// BackupOrderId
//			pstmt.setLong(++count, standardProduct.BackupOrderId);

			// ImportFlag
//			pstmt.setLong(++count, commonProduct.ImportFlag);
			// ImportOrderId
//			pstmt.setLong(++count, BdProduct.ImportOrderId);
			// RawDataId
//			pstmt.setLong(++count, commonProduct.RawDataId);
			// Area
//			pstmt.setString(++count, BdProduct.Area);
			//SPTypeId
			pstmt.setLong(++count, BdProduct.BDTypeId);
			// GridId
			System.out.println("GridId="+BdProduct.GridId);
			pstmt.setLong(++count, BdProduct.GridId);
			
			// 执行插入
//			 pstmt.executeUpdate();//返回值为成功后的函数
//			System.out.println(strSql);
			pstmt.execute();

			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = dbcommCommon.getConnection();
				return addData(BdProduct);
			}
			e.printStackTrace();
			return false;
		}
		return true;
	}
	

	private String searchMaxFileid() {
		// TODO Auto-generated method stub
		return "null";
	}

	public void delete(String condition){
		try {
			String strSql = "delete from "
					+ this.dbTable
					+ " "+condition;

			if (null == conn) {
				return;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			int i=pstmt.executeUpdate(strSql);
//			execute(strSql);
			System.out.println(i);
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
				
	}
	




}
