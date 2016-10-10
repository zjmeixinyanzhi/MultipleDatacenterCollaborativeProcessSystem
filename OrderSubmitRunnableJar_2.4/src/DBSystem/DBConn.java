/*
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
package DBSystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author caoyang
 * 
 */
public class DBConn {
	//
	public static Connection con = null;

	// 默认数据库参数选项
	private static String driver; // 连接数据库驱动
	private static String ip; // 默认数据库服务器地址
	private static String port; // 默认数据库服务器端口

	private static String mccps_sid; // 默认数据库SID
	private static String mccps_user; // 默认访问数据库用户
	private static String mccps_password; // 默认访问数据库用户密码

	protected boolean bIsConnection;

	public static int n = 0;

	public DBConn() {
		System.out.println("DBConn::public DBConn() | 构造函数");
		this.bIsConnection = false;
	}

	public DBConn(String ip, String port, String mccps_sid, String user,
			String passwd) {
		System.out.println("DBConn::public DBConn( ) | 构造函数");
		this.bIsConnection = false;

		// 获取数据库参数选项
		DBConn.driver = "com.mysql.jdbc.Driver"; // 连接数据库驱动
		DBConn.ip = ip; // 默认数据库服务器地址
		DBConn.port = port; // 默认数据库服务器端口

		DBConn.mccps_sid = mccps_sid; // 默认数据库SID
		DBConn.mccps_user = user; // 默认访问数据库用户
		DBConn.mccps_password = passwd; // 默认访问数据库用户密码

	}

	// 获取数据库连接
	public static synchronized Connection getConnection() {
		System.out
				.println("DBConn::public static synchronized Connection getConnection() | 获取数据库连接");
		n++;

		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String DBURL = "jdbc:mysql://" + ip + ":" + port + "/" + mccps_sid;
		try {
			con = DriverManager
					.getConnection(DBURL, mccps_user, mccps_password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 测试连接数
		// System.out.println(">>>>当前连接数"+n);

		return con;
	}

	// 判断是否连接数据库成功
	public boolean isConnected() {
		System.out
				.println("DBConn::public static synchronized boolean isConnected() | 判断是否连接数据库成功");

		return this.bIsConnection;
	}

	public static synchronized void close() {

		try {
			if (null != con) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
