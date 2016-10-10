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
package DBSystem;

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
import java.util.Iterator;

import OrderManage.L3InternalOrder;
import ResourceManage.Algorithm;

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
	
	//向算法资源库查询算法资源：根据订单类型和数据类型确定
	public synchronized ArrayList< Algorithm > search( String algorithmType, String dataType ){
		System.out.println( "AlgorithmDB::public Algorithm search( String type, String dataType ) | 向算法资源库查询算法资源" );
		
		ArrayList< Algorithm > algorithmList = new ArrayList< Algorithm >();
		
		if( ( null == algorithmType ) || ( "".equals( algorithmType ) ) ){
			return algorithmList;
		}
		
		if( null == dataType ){
			return algorithmList;
		}
//		if( "".equals( dataType ) ){
//			dataType = "Anything";
//		}
//		if( !algorithmType.equals( "L3RN" ) ){
//			dataType = "Anything";
//		}
		
		try{
			//String strSql = "SELECT * FROM " + this.dbTable + " WHERE ProcSystemName = '" + strProcSystemName + "'";	//@@@
//			String strSql = "SELECT * FROM " + this.dbTable + " WHERE ProcSystemName = '" + strProcSystemName + "' and dataType = '" + dataType + "'";
//			String strSql = "SELECT * FROM " + this.dbTable + " WHERE algorithmType = '" + algorithmType + "' and dataType = '" + dataType + "'";
			//包含关系，HJ1ACCD1;HJ1ACCD2;HJ1BCCD1;HJ1BCCD2; HJ1ACCD1	模糊查询
			String strSql = "SELECT * FROM " + this.dbTable + " WHERE algorithmType = '" + algorithmType + "' and dataType LIKE '" + "%" + dataType + "%" + "'";
			
			//test
			System.out.println( ">>>>>>>>>>>>>>>" + strSql );
			
			if( null == conn ){
				return algorithmList;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery( strSql );
			while( rs.next() ){
				Algorithm algorithm = new Algorithm();
				
				//算法资源ID
				algorithm.setAlgorithmID( rs.getInt( "AlgorithmID" ) );
				//算法资源名
				algorithm.setAlgorithmName( rs.getString( "AlgorithmName" ) );
				//简单描述信息
				algorithm.setDescription( rs.getString( "Description" ) );
				//处理系统名称	DataServiceSystem/RadNormSystem/GeoNormSystem/CommonProductSystem/FusionSystem/AssimulationSystem/ValidationSystem
				algorithm.setProcSystemName( rs.getString( "ProcSystemName" ) );
				//处理系统服务器配置信息
				algorithm.setProcSystemConfig( rs.getString( "ProcSystemConfig" ) );
				//算法具体路径
				algorithm.setAlgorithmFilePath( rs.getString( "AlgorithmFilePath" ) );
				//参数
				ArrayList< String > argsList = new ArrayList< String >( Arrays.asList( rs.getString( "Parameter" ).split( ";" ) ));
				algorithm.setArgsList( argsList );
				//参数限定
				ArrayList< String > schemaList = new ArrayList< String >( Arrays.asList( rs.getString( "Schema" ).split( ";" ) ));
				algorithm.setSchemaList( schemaList );
				//可用状态（A/NA）
				algorithm.setStatus( rs.getString( "status" ) );
				//数据中心ID
				algorithm.setResourceId( rs.getInt( "resourceId" ) );
				//算法资源类型
				algorithm.setAlgorithmType( rs.getString( "algorithmType" ) );
				//算法资源数据类型
				algorithm.setDataType( rs.getString( "dataType" ) );
				
				algorithmList.add( algorithm );
				
				//test
				System.out.println( "===========Algorithm : " + algorithm.toString() );
			}

			// 关闭相关连接
			st.close();
			rs.close();
		}catch( SQLException e ){
			String strSqlState = e.getSQLState();
			if( strSqlState.equals( "08S01" ) ){
				conn = getConnection();
				return search( algorithmType, dataType );
			}
			e.printStackTrace();
			algorithmList.clear();
			return algorithmList;
		}
		return algorithmList;
	}
	
}
