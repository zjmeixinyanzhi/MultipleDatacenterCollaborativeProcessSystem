﻿/*
 *程序名称 		: DBConn.java
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

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import SystemManage.DBConfig;
import SystemManage.SystemConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author caoyang
 *
 */
public class DBConn {
	//用来存放订单数据库的配置
	private static DBConfig              dbConfig = null;
	private static ComboPooledDataSource mccps_ds = null;
	
	// 默认数据库参数选项
	private static String driver;			// 连接数据库驱动
	private static String ip;				// 默认数据库服务器地址
	private static String port;				// 默认数据库服务器端口
	
	private static String mccps_sid;		// 默认数据库SID
	private static String mccps_user;		// 默认访问数据库用户
	private static String mccps_password;	// 默认访问数据库用户密码
	
	private static String minPoolSize;		// 默认访问数据库最小连接数
	private static String maxPoolSize;		// 默认访问数据库最大连接数
	
	protected boolean bIsConnection;
	
	
	
	public DBConn(){
		System.out.println( "DBConn::public DBConn() | 构造函数" );
		dbConfig           = null;
		this.bIsConnection = false;
	}
	
	public DBConn( DBConfig config ){
		System.out.println( "DBConn::public DBConn( DBConfig config ) | 构造函数" );
		dbConfig = config;
		this.bIsConnection = false;
		
		try{
			mccps_ds = null;
			
			if( null == dbConfig ){
				// 默认数据库参数选项
				driver         = "com.mysql.jdbc.Driver";	// 连接数据库驱动
				ip             = "10.3.10.1";				// 默认数据库服务器地址
				port           = "3306";					// 默认数据库服务器端口
				
				mccps_sid      = "mccps";					// 默认数据库SID
				mccps_user     = "caoyang";					// 默认访问数据库用户
				mccps_password = "123456";					// 默认访问数据库用户密码
				
				minPoolSize    = "10";						// 默认访问数据库最小连接数 100
				maxPoolSize    = "20";						// 默认访问数据库最大连接数 500
			}else{
				driver         = "com.mysql.jdbc.Driver";	// 连接数据库驱动
				ip             = dbConfig.getIP();			// 默认数据库服务器地址
				port           = dbConfig.getPort();		// 默认数据库服务器端口
				
				mccps_sid      = dbConfig.getSid();			// 默认数据库SID
				mccps_user     = dbConfig.getUser();		// 默认访问数据库用户
				mccps_password = dbConfig.getPasswd();		// 默认访问数据库用户密码
				
				minPoolSize    = "10";						// 默认访问数据库最小连接数
				maxPoolSize    = "20";						// 默认访问数据库最大连接数
			}
			
			mccps_ds = new ComboPooledDataSource();
			// 设置JDBC的Driver类
			mccps_ds.setDriverClass(driver);
			// 设置JDBC的URL
			mccps_ds.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + mccps_sid);
			// 设置数据库的登录用户名
			mccps_ds.setUser(mccps_user);
			// 设置数据库的登录用户密码
			mccps_ds.setPassword(mccps_password);
			// 设置连接池的最大连接数
			mccps_ds.setMaxPoolSize(Integer.parseInt(maxPoolSize));
			// 设置连接池的最小连接数
			mccps_ds.setMinPoolSize(Integer.parseInt(minPoolSize));
			// 设置初始化连接数
			mccps_ds.setInitialPoolSize( Integer.parseInt( minPoolSize ) );
			// 设置当连接池用完时客户端调用getConnection()后等待获取新连接的时间，超时后将抛出SQLException， 如设为0则无限期等待。单位毫秒，默认为0
			mccps_ds.setCheckoutTimeout( 0 );
			mccps_ds.setIdleConnectionTestPeriod( 120 );
			
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}
		
	// 获取数据库连接
	public static synchronized Connection getConnection(){
		System.out.println( "DBConn::public static synchronized Connection getConnection() | 获取数据库连接" );
		Connection conn = null;
		try{
			//###//System.out.println(mccps_ds.getUser());
			//###System.out.println( "----Database info---------------------------------------" );
			//###System.out.println( "driver      : " + mccps_ds.getDriverClass() );
			//###System.out.println( "jdbc_url    : " + mccps_ds.getJdbcUrl() );
			//###System.out.println( "user        : " + mccps_ds.getUser() );
			//###System.out.println( "password    : " + mccps_ds.getPassword() );
			//###System.out.println( "maxPoolSize : " + mccps_ds.getMaxPoolSize() );
			//###System.out.println( "minPoolSize : " + mccps_ds.getMinPoolSize() );
			//###System.out.println( "--------------------------------------------------------" );

			conn = mccps_ds.getConnection();
			
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println( "<Error>DBConn::public static synchronized Connection getConnection() | catch (SQLException e) | conn == null" );
			return getConnection();
		}
		
		return conn;
	}
	//判断是否连接数据库成功
	public boolean isConnected(){
		System.out.println( "DBConn::public static synchronized boolean isConnected() | 判断是否连接数据库成功" );

		return this.bIsConnection;
	}
	
	public static synchronized void close(){
		if( null != mccps_ds ){
			mccps_ds.close();
			mccps_ds = null;
		}
	}
}
