/*
 *程序名称 		: ServerConfig.java
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

public class ServerConfig {
	//服务器端名称
	String name;
	//ip地址
	String ip;
	//服务端口
	String port;
	
	//构造函数
	public ServerConfig(){
		System.out.println( "ServerConfig::public ServerConfig() | 构造函数" );
		
		//服务器端名称
		this.name = "";
		//ip地址
		this.ip   = "";
		//服务端口
		this.port = "";
	}
	
	//构造函数
	public ServerConfig( String name, String ip, String port ){
		System.out.println( "ServerConfig::public ServerConfig( String name, String ip, String port ) | 构造函数" );
		this.name = name;
		this.ip   = ip;
		this.port = port;
	}
	
	//set函数 负责设置服务器端ip和port端口号
	void setName( String name ){
		System.out.println( "ServerConfig::void setName( String name ) | " );
		this.name = name;
	}
	void setIP( String ip ){
		System.out.println( "ServerConfig::void setIP( String ip ) | " );
		this.ip = ip;
	}
	void setPort( String port ){
		System.out.println( "ServerConfig::void setPort( String port ) | " );
		this.port = port;
	}
	
	//get函数 负责获取服务器端ip和port端口号
	String getName(){
		System.out.println( "ServerConfig::String getName() | " );
		return this.name;
	}
	String getIP(){
		System.out.println( "ServerConfig::String getIP() | " );
		return this.ip;
	}
	String setPort(){
		System.out.println( "ServerConfig::String setPort() | " );
		return this.port;
	}
	
	//转化为String类对象
	public String toString(){
		System.out.println( "ServerConfig::public String toString() | 转化为String类对象" );
		return ( this.name + "," + this.ip + "," + this.port );
	}

	public void valueOfString( String strServerConfig ){
		System.out.println( "ServerConfig::public void valueOfString( String strServerConfig ) | String类对象转化为属性值" );
		String[] serverConfig = strServerConfig.split( "," );
		if( serverConfig.length != 3 ){
			return;
		}
		this.name = serverConfig[ 0 ];
		this.ip   = serverConfig[ 1 ];
		this.port = serverConfig[ 2 ];
	}
}
