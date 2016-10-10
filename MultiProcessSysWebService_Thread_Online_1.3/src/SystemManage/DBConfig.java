/*
 *程序名称 		: DBConfig.java
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
package SystemManage;

/**
 * @author caoyang
 *
 */
public class DBConfig {
	//用来存放数据库的配置信息
	String ip;			//数据库服务器ip地址
	String port;		//数据库服务器端口
	String sid;			//数据库服务ID
	String user;		//登陆用户名
	String password;	//登陆密码
	String dbtable;		//数据库表
	String productRepositorySID;		//产品库实例

	//构造函数
	public DBConfig( String ip, String port, String sid, String user, String passwd,String dbtable, String productRepository ){
		System.out.println( "DBConfig::public DBConfig( String ip, String port, String sid, String user, String passwd, String dbtable ) | 构造函数" );
		this.ip       = ip;
		this.port     = port;
		this.sid      = sid;
		this.user     = user;
		this.password = passwd;
		this.dbtable=dbtable;
		this.productRepositorySID  = productRepository;
	}
	
	public String getProductRepositorySID() {
		return productRepositorySID;
	}

	public void setProductRepositorySID(String productRepositorySID) {
		this.productRepositorySID = productRepositorySID;
	}

	// set方法负责设置服务器端ip和port端口号
	public void setIP( String ip ){
		System.out.println( "DBConfig::public void setIP( String ip ) | " );
		this.ip=ip;
	}
	public void setPort( String port ){
		System.out.println( "DBConfig::public void setPort( String port ) | " );
		this.port = port;
	}
	public void setSid( String sid ){
		System.out.println( "DBConfig::public void setSid( String sid ) | " );
		this.sid = sid;
	}
	public void setUser( String user ){
		System.out.println( "DBConfig::public void setUser( String user ) | " );
		this.user = user;
	}
	public void setPasswd( String passwd ){
		System.out.println( "DBConfig::public void setPasswd( String passwd ) | " );
		this.password = passwd;
	}
	public void setDBTable( String dbtable ){
		System.out.println( "DBConfig::public void setDBTable( String dbtable ) | " );
		this.dbtable = dbtable;
	}
	
	// get方法负责获取服务器端ip、port端口号、uid、passwd和user
	public String getIP(){
		System.out.println( "DBConfig::public String getIP() | " );
		return this.ip;
	}
	public String getPort(){
		System.out.println( "DBConfig::public String getPort() | " );
		return this.port;
	}
	public String getSid(){
		System.out.println( "DBConfig::public String getSid() | " );
		return this.sid;
	}
	public String getUser(){
		System.out.println( "DBConfig::public String getUser() | " );
		return this.user;
	}
	public String getPasswd(){
		System.out.println( "DBConfig::public String getPasswd() | " );
		return this.password;
	}
	public String getDBTable(){
		System.out.println( "DBConfig::public String getDBTable() | " );
		return this.dbtable;
	}

}
