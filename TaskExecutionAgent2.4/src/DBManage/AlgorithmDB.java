/*
 *程序名称 		: AlgorithmDB.java
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
import java.sql.SQLException;

import TaskExeAgent.DBConfig;

/**
 * @author caoyang
 *
 */
public class AlgorithmDB extends DBConn {
	
	private static Connection conn = null;
	private String            dbTable;
	
	//构造函数
	public AlgorithmDB (){
		System.out.println( "AlgorithmDB::public AlgorithmDB () | 构造函数" );

		//初始化连接数据库
		if( null == conn ){
			conn = getConnection();
			if( conn == null ){
				this.bIsConnection = false;
				System.out.println( "<Error>AlgorithmDB::public AlgorithmDB () | conn = getConnection() | conn == null" );
			}else{
				this.bIsConnection = true;
			}
		}
		
		this.dbTable = "algorithmdb";
	}
	
	protected void finalize(){
		//try{
		//	//conn.commit();
		//	conn.close();
		//} catch( SQLException e ){
		//	String strSqlState = e.getSQLState();
		//	if( strSqlState.equals( "08S01" ) ){
		//		return;
		//	}
		//	e.printStackTrace();
		//}
	}
	
	public static synchronized void closeConnected(){
		try {
			if( null != conn ){
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//public static void setXXX(String xxx) {
	//	return this.XXX=xxx;
	//}
	//public String getXXX() {
	//	return XXX;
	//}


}
