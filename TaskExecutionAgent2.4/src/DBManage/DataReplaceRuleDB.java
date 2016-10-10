/*
 *程序名称 		: DataReplaceRuleDB.java
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

/**
 * @author caoyang
 *
 */
public class DataReplaceRuleDB extends DBConn {
	private static Connection conn = null;
	private String            dbTable;

	public DataReplaceRuleDB() {
		System.out.println( "DataReplaceRuleDB::public DataReplaceRuleDB() | 构造函数" );
		
		//初始化连接数据库
		if( null == conn ){
			conn = getConnection();
			if( conn == null ){
				this.bIsConnection = false;
				System.out.println( "<Error>DataReplaceRuleDB::public DataReplaceRuleDB() | conn = getConnection() | conn == null" );
			}else{
				this.bIsConnection = true;
			}
		}
		
		this.dbTable = "datareplaceruledb";
	}
	
	public static synchronized void closeConnected(){
		try {
			if( null != conn ){
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
