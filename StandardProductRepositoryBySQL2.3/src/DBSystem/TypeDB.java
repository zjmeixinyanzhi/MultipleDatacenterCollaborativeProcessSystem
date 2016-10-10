package DBSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TypeDB 
{

	private static Connection conn = null;
	private static DBConn dbcommCommon = null;
	public String dbTable = null;
	
	public TypeDB(String ip, String port, String mccps_sid ,String user, String passwd, String dbTable){
		this.dbcommCommon = new DBConn(ip, port, mccps_sid, user, passwd);
		
		//初始化数据库
		if(null == conn){
			conn = dbcommCommon.getConnection();
			if(conn == null){
				dbcommCommon.bIsConnection = false;
			}
			else{
				dbcommCommon.bIsConnection = true;
			}
		}
		this.dbTable = dbTable;
	}
	
	public int getTypeId(String Name){
		int id = -1;
		System.out.println("Sp_typeDB::public int getSp_typeId(String Name) | 返回Sp_typeID");
		try {
			String StrSql = "SELECT * FROM " + this.dbTable + " where Name=?";
			
			if(null == conn){
				return -1;
			}
			PreparedStatement pstmt = conn.prepareStatement(StrSql);
			pstmt.setString(1, Name);
			
			//execute select
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				id = rs.getInt("Id");
			}
			
		} catch (SQLException e) {
			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = dbcommCommon.getConnection();
				return getTypeId(Name);
			}
			e.printStackTrace();
			return -1;
		}
		return id;
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
	
	public static void main(String args[]){
		TypeDB typeDB = new TypeDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", "sp_type");
//		10.3.10.1_3306_mccps_mca_mca_RSProductDB
		int SpTypeId = typeDB.getTypeId("FY3AMERSI");
		System.out.println(SpTypeId);

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
