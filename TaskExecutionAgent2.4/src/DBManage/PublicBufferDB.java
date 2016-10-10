/*
 *程序名称 		: PublicBufferDB.java
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
import java.util.ArrayList;

import TaskExeAgent.PublicBufferItem;

/**
 * @author caoyang
 *
 */
public class PublicBufferDB extends DBConn {
	private static Connection conn = null;
	private String            dbTable;

	public PublicBufferDB() {
		System.out.println( "PublicBufferDB::public PublicBufferDB() | 构造函数" );
		
		//初始化连接数据库
		if( null == conn ){
			conn = getConnection();
			if( conn == null ){
				this.bIsConnection = false;
				System.out.println( "<Error>PublicBufferDB::public PublicBufferDB() | conn = getConnection() | conn == null" );
			}else{
				this.bIsConnection = true;
			}
		}
		
		this.dbTable = "publicbufferdb";
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
	
	public synchronized boolean add( PublicBufferItem publicBufferItem ){
		if( null == publicBufferItem ){
			return false;
		}
		return true;
	}
	
	private ArrayList< PublicBufferItem > search( String strQuery ){
		ArrayList< PublicBufferItem > publicBufferItemList = new ArrayList< PublicBufferItem >();
		
		return publicBufferItemList;
	}
	
	public PublicBufferItem getItemByID( int iItemID ){
		String strQuery = "";
		PublicBufferItem item = null;
		ArrayList< PublicBufferItem > publicBufferItemList = null;
		
		publicBufferItemList = search( strQuery );
		
		if( publicBufferItemList.size() > 0 ){
			item = publicBufferItemList.get( 0 );
		}
		
		return item;
	}
	
	public PublicBufferItem getItemByDataName( String strItemDataName ){
		
		PublicBufferItem item = null;
		if( null == strItemDataName ){
			return item;
		}
		ArrayList< PublicBufferItem > publicBufferItemList = null;
		
		String strQuery = "";
		publicBufferItemList = search( strQuery );
		
		if( publicBufferItemList.size() > 0 ){
			item = publicBufferItemList.get( 0 );
		}
		
		return item;
	}
	
	public synchronized boolean update( PublicBufferItem item ){
		return true;
	}
}
