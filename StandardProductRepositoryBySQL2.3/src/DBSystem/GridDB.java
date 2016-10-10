package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.spi.DirStateFactory.Result;

import org.omg.CORBA.PUBLIC_MEMBER;

public class GridDB 
{
	private static Connection conn = null;
	private static DBConn dbcommCommon = null;
	public String dbTable = null;
	
	public GridDB(String ip,String port,String mccps_sid,String user,String passwd, String dbTable)
	{
		//初始化数据库连接
		this.dbcommCommon = new DBConn(ip, port, mccps_sid, user, passwd); 
		if(null == conn)
		{
			conn = dbcommCommon.getConnection();
			if(conn == null)
			{
				dbcommCommon.bIsConnection = false;
			}
			else 
			{
				dbcommCommon.bIsConnection = true;
			}
		}
		this.dbTable = dbTable;
	}
	//查询GridID
	public int getGridId(String HxxVxx)
	{
		int id = -1;
		System.out.println("GridDB::public String getGridId(String HxxVxx) | 返回Grid ID");
		try {
			String strSql = "SELECT Id FROM " +this.dbTable+" where Code = ?";
//			System.out.print(strSql);
			if(null == conn)
			{
				return id;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql);
			pstmt.setString(1, HxxVxx);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next())
			{
				id = rs.getInt("Id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}
	public static synchronized void closeConnected()
	{
		try 
		{
			if (conn != null)
			{
				conn.close();
				conn = null;
			}
		} catch (Exception e) {
		}
	}
	
	// 查询经纬度一致的Id
	public double searchGridIDbyLongAlt(double TopLeftLongitude,
			double TopLeftLatitude, double LowerRightLongitude,
			double LowerRightLatitud) {
		double id = -1;
		System.out
				.println("RsDataCacheDB::public double searchGridIDbyLongAlt(  ) | 向lat_long_Gird库查询经纬度一致的记录");

		try {
			if (null == conn) {
				return id;
			}

			String strSql = "SELECT min(Id) FROM "
					+ this.dbTable
					+ " where TopLeftLongitude=? and TopLeftLatitude=? and LowerRightLongitude=? and LowerRightLatitude=?";
			PreparedStatement pstmt = conn.prepareStatement(strSql);

			pstmt.setDouble(1, TopLeftLongitude);
			pstmt.setDouble(2, TopLeftLatitude);
			pstmt.setDouble(3, LowerRightLongitude);
			pstmt.setDouble(4, LowerRightLatitud);
			
			ResultSet  rs=pstmt.executeQuery();
			while (rs.next()) {
				id=rs.getDouble("min(Id)");
				System.out.println("存在经纬度一致的记录，GridId="+id);
			}			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	
	public double getGridLongAltID(double TopLeftLongitude,
			double TopLeftLatitude, double LowerRightLongitude,
			double LowerRightLatitud) {
		double id = -1;
		// 首先查询是否存在经纬度之一的Id
		id = searchGridIDbyLongAlt(TopLeftLongitude, TopLeftLatitude,
				LowerRightLongitude, LowerRightLatitud);
		if (!(id == -1)) {
			return id;
		}
		System.out
				.println("GridDB::public double getInsertID( ) | 插入新的Gird数据并返回ID");

		// 插入新的缓存数据
		try {
			String strSql = "INSERT INTO "
					+ this.dbTable
					+ "(Code,TopLeftLongitude,TopLeftLatitude,LowerRightLongitude,LowerRightLatitude)"
					+ "VALUES(?,?,?,?,?)";

			if (null == conn) {
				return id;
			}
			PreparedStatement pstmt = conn.prepareStatement(strSql,
					Statement.RETURN_GENERATED_KEYS);
			int count = 0;
			// Code
			pstmt.setString(++count, "HXXVXX");
			// TopLeftLongitude
			pstmt.setDouble(++count, TopLeftLongitude);
			// TopLeftLatitude
			pstmt.setDouble(++count, TopLeftLatitude);
			// 位置
			pstmt.setDouble(++count, LowerRightLongitude);
			// 删除标记
			pstmt.setDouble(++count, LowerRightLatitud);
			// 执行插入
			// pstmt.executeUpdate();//返回值为成功后的函数
			pstmt.execute();

			// 检索由于执行此 Statement 对象而创建的所有自动生成的键
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getLong(1);
			}
			// 关闭相关连接
			pstmt.close();
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = dbcommCommon.getConnection();
				return getGridLongAltID(TopLeftLongitude, TopLeftLatitude,
						LowerRightLongitude, LowerRightLatitud);
			}
			e.printStackTrace();
			return id;
		}

		return id;

	}
	
}
