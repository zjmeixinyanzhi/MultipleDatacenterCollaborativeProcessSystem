/*
 *程序名称 		: Algorithm.java
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
package TaskSchedular;

import java.util.ArrayList;

import com.sun.xml.internal.txw2.output.DataWriter;

import DBManage.DataCenter;
import DBManage.SystemResourceDB;
import SystemManage.ServerConfig;

/**
 * @author caoyang
 *
 */
public class Algorithm {
	//算法资源ID
	private int algorithmId;
	//用来存放算法名称
	public String algorithmName;
	//简单描述信息
	public String description;
	//用来存放处理系统名称
	public String procSystemName;
	//用来存放处理系统的服务器配置信息
	//public ServerConfig procSystemConfig;
	public String procSystemConfig;
	//算法具体路径
	public String algorithmFilePath;
	//用来存放算法参数
	public ArrayList< String > argsList;	//@@@
	//参数限定
	public ArrayList< String > schemaList;	//@@@ 怎么一个格式？
	//用来存放算法资源状态 Avaliable/NotAvaliable
	public String status;
	//数据中心ID
	public int resourceId;
	//数据中心信息
	public DataCenter dataCenter;
	//算法评估值
	public float evaluate;
	//算法类型（同三级订单订单类型）
	public String algorithmType;
	//算法所需的数据类型
	public String dataType;
	
	
	//构造函数
	//###public Algorithm ( String procSystemName,
	//###					ServerConfig procSystemConfig = null,
	//###					String algorithmName,
	//###					ArrayList<args>	argsList = null,
	//###					String status = null ){
	//###	this. procSystemName   = procSystemName;
	//###	this. procSystemConfig = procSystemConfig;
	//###	this. algorithmName    = algorithmName;
	//###	this. argsList         = argsList;
	//###	this. status           = status;
	//###}
	
	//###// set方法负责设置算法资源信息
	//###public void setProcSystemName (String procSystemName);
	//###public void setProcSystemConfig (ServerConfig procSystemConfig);
	//###public void setAlgorithmName ( String algorithmName);
	//###public void setArgsList (ArrayList(args)argsList);
	//###public void setStatus (String status);
	//###
	//###// get方法负责获取算法资源信息
	//###public void String getProcSystemName (String);
	//###public void ServerConfig getProcSystemConfig();
	//###public void String getAlgorithmName ();
	//###public void ArrayList(args)getArgsList ();
	//###public void String getStatus();

	//构造函数
	public Algorithm (){
		System.out.println( "Algorithm::public Algorithm () | 构造函数" );
		
		this.algorithmId       = -1;
		this.algorithmName     = "";
		this.description       = "";
		this.procSystemName    = "";
		//this.procSystemConfig  = new ServerConfig();
		this.procSystemConfig  = "";
		this.algorithmFilePath = "";
		this.argsList          = new ArrayList< String >();
		this.schemaList        = new ArrayList< String >();
		this.status            = "";
		this.resourceId        = -1;
		this.dataCenter        = null;
		this.evaluate          = 0.0f;
		this.algorithmType     = "";
		this.dataType          = "";
	}

	//构造函数
//	public Algorithm ( String algorithmName,
//						String description,
//						String procSystemName,
//						ServerConfig procSystemConfig,
//						String algorithmFilePath,
//						ArrayList< String > argsList,
//						ArrayList< String > schemaList,
//						String status,
//						int resourceId ){
//		System.out.println( "Algorithm::public Algorithm ( String algorithmName, String description, String procSystemName, "
//							+ "ServerConfig procSystemConfig, String algorithmFilePath, String algorithmName, "
//							+ "ArrayList< String > argsList, ArrayList< String > schemaList, String status, int resourceId ) | 构造函数" );
//		
//		SystemResourceDB systemResourceDB = new SystemResourceDB();
//		this.algorithmId       = -1;
//		this.algorithmName     = algorithmName;
//		this.description       = description;
//		this.procSystemName    = procSystemName;
//		this.procSystemConfig  = procSystemConfig;
//		this.algorithmFilePath = algorithmFilePath;
//		this.argsList          = argsList;
//		this.schemaList        = schemaList;
//		this.status            = status;
//		this.resourceId        = resourceId;
//		this.dataCenter        = systemResourceDB.getDataCenter( this.resourceId );
//		this.evaluate          = 0.0f;
//		
//	}
	
	public Algorithm ( String algorithmName,
						String description,
						String procSystemName,
						String procSystemConfig,
						String algorithmFilePath,
						ArrayList< String > argsList,
						ArrayList< String > schemaList,
						String status,
						int resourceId,
						String algorithmType,
						String dataType ){
		System.out.println( "Algorithm::public Algorithm ( String algorithmName, String description, String procSystemName, "
						+ "String procSystemConfig, String algorithmFilePath, String algorithmName, "
						+ "ArrayList< String > argsList, ArrayList< String > schemaList, String status, int resourceId ) | 构造函数" );
		
		SystemResourceDB systemResourceDB = new SystemResourceDB();
		this.algorithmId       = -1;
		this.algorithmName     = algorithmName;
		this.description       = description;
		this.procSystemName    = procSystemName;
		this.procSystemConfig  = procSystemConfig;
		this.algorithmFilePath = algorithmFilePath;
		this.argsList          = argsList;
		this.schemaList        = schemaList;
		this.status            = status;
		this.resourceId        = resourceId;
		this.dataCenter        = systemResourceDB.getDataCenter( this.resourceId );
		this.evaluate          = 0.0f;
		this.algorithmType     = algorithmType;
		this.dataType          = dataType;
		
	}
	
	// set方法负责设置算法资源信息
	public void setAlgorithmID( int algorithmId ){
		System.out.println( "Algorithm::public void setAlgorithmID( int algorithmId ) | " );
		if( -1 == this.algorithmId ){
			this.algorithmId = algorithmId;
		}
	}
	public void setAlgorithmName( String algorithmName ){
		System.out.println( "Algorithm::public void setAlgorithmName( String algorithmName ) | " );
		this.algorithmName = algorithmName;
	}
	public void setDescription( String description ){
		System.out.println( "Algorithm::public void setDescription( String description ) | " );
		this.description = description;
	}
	public void setProcSystemName( String procSystemName ){
		System.out.println( "Algorithm::public void setProcSystemName( String procSystemName ) | " );
		this.procSystemName = procSystemName;
	}
	
//	public void setProcSystemConfig( ServerConfig procSystemConfig ){
//		System.out.println( "Algorithm::public void setProcSystemConfig( ServerConfig procSystemConfig ) | " );
//		this.procSystemConfig = procSystemConfig;
//	}
	public void setProcSystemConfig( String procSystemConfig ){
		System.out.println( "Algorithm::public void setProcSystemConfig( String procSystemConfig ) | " );
		this.procSystemConfig = procSystemConfig;
	}
	
	public void setAlgorithmFilePath( String algorithmFilePath ){
		System.out.println( "Algorithm::public void setAlgorithmFilePath( String algorithmFilePath ) | " );
		this.algorithmFilePath = algorithmFilePath;
	}
	
	public void setArgsList( ArrayList< String >argsList ){
		System.out.println( "Algorithm::public void setAlgorithmName( String algorithmName ) | " );
		this.argsList = argsList;
	}
	
	public void setSchemaList( ArrayList< String >schemaList ){
		System.out.println( "Algorithm::public void setSchemaList( ArrayList< String >schemaList ) | " );
		this.schemaList = schemaList;
	}
	
	public void setStatus( String status ){
		System.out.println( "Algorithm::public void setStatus( String status ) | " );
		this.status = status;
	}
	
	public void setResourceId( int resourceId ){
		System.out.println( "Algorithm::public void setResourceId( int resourceId ) | " );
		SystemResourceDB systemResourceDB = new SystemResourceDB();
		this.resourceId = resourceId;
		this.dataCenter = systemResourceDB.getDataCenter( this.resourceId );
	}
	
	public void setAlgorithmType( String algorithmType ){
		System.out.println( "Algorithm::public void setAlgorithmType( String type ) | " );
		this.algorithmType = algorithmType;
	}
	
	public void setDataType( String dataType ){
		System.out.println( "Algorithm::public void setDataType( String dataType ) | " );
		this.dataType = dataType;
	}
	
	// get方法负责获取算法资源信息
	public int getAlgorithmID(){
		System.out.println( "Algorithm::public int getAlgorithmID() | " );
		return this.algorithmId;
	}
	public String getAlgorithmName(){
		System.out.println( "Algorithm::public String getAlgorithmName() | " );
		return this.algorithmName;
	}
	
	public String getDescription(){
		System.out.println( "Algorithm::public String getDescription() | " );
		return this.description;
	}
	
	public String getProcSystemName(){
		System.out.println( "Algorithm::public String getProcSystemName() | " );
		return this.procSystemName;
	}
	
//	public ServerConfig getProcSystemConfig(){
//		System.out.println( "Algorithm::public ServerConfig getProcSystemConfig() | " );
//		return this.procSystemConfig;
//	}
	public String getProcSystemConfig(){
		System.out.println( "Algorithm::public String getProcSystemConfig() | " );
		return this.procSystemConfig;
	}
	
	public String getAlgorithmFilePath(){
		System.out.println( "Algorithm::public String getAlgorithmFilePath() | " );
		return this.algorithmFilePath;
	}
	
	public ArrayList< String >getArgsList(){
		System.out.println( "Algorithm::public ArrayList< AlgorithmPara >getArgsList() | " );
		return this.argsList;
	}
	
	public ArrayList< String > getSchemaList(){
		System.out.println( "Algorithm::public ArrayList< String > getSchemaList() | " );
		return this.schemaList;
	}
	
	public String getStatus(){
		System.out.println( "Algorithm::public String getStatus() | " );
		return this.status;
	}
	
	public int getResourceId(){
		System.out.println( "Algorithm::public int getResourceId() | " );
		return this.resourceId;
	}
	
	public DataCenter getDataCenter() {
		System.out.println( "Algorithm::public DataCenter getDataCenter() | " );
		return this.dataCenter;
	}
	
	public String getAlgorithmType(){
		System.out.println( "Algorithm::public String getAlgorithmType() | " );
		return this.algorithmType;
	}
	
	public String getDataType(){
		System.out.println( "Algorithm::public String getDataType() | " );
		return this.dataType;
	}
	
	public String toString(){
		String strValue = "";
		
		strValue += "algorithmName="     + this.algorithmName     + ",";
		strValue += "description="       + this.description       + ",";
		strValue += "procSystemName="    + this.procSystemName    + ",";
		strValue += "procSystemConfig="  + this.procSystemConfig  + ",";
		strValue += "algorithmFilePath=" + this.algorithmFilePath + ",";
		//strValue += "argsList=" + this.argsList          + ",";
		//strValue += "schemaList=" + this.schemaList        + ",";
		strValue += "status="            + this.status            + ",";
		strValue += "resourceId="        + this.resourceId        + ",";
		//strValue += "dataCenter=" + this.dataCenter        + ",";
		strValue += "evaluate="          + this.evaluate          + ",";
		strValue += "algorithmType="     + this.algorithmType     + ",";
		strValue += "dataType="          + this.dataType          ;
		
		return strValue;
	}
}
