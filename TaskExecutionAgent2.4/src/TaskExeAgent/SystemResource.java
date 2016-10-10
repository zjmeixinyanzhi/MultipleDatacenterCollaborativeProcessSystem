/*
 *程序名称 		: SystemResource.java
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
package TaskExeAgent;

import java.util.Hashtable;

/**
 * @author caoyang
 *
 */
public class SystemResource {
	//用来存放数据中心及可用状态列表
	//!!!private Hashtable dataCenterList;
	private Hashtable< String, ServerConfig > dataCenterList;
	//用来存放生产分系统及可用状态列表
	//###!!!private Hashtable processingSystemList;
	//用来记录共享存储空间的占用率
	private float publicBufferUsage;
	
	//对所有系统资源及其状态进行更新
	public void update(){
		System.out.println( "SystemResource::public void update() | 对所有系统资源及其状态进行更新" );
		//###//更新各个数据中心状态
		//###for( :i: ){
		//###	//测试数据中心连接或可用状态
		//###	String     name       = ;
		//###	DataCenter dataCenter = ;
		//###	this.dataCenterList.put(name,dataCenter);
		//###}
		//###for(:j:){
		//###	//测试生产分系统可用状态
		//###	String processSystem = ;
		//###	String status        = ;
		//###	this.processingSystemList.put(processSystem,status);
		//###}
		//###//获取缓存区空间使用率
		//###this.publicBufferUsage = ;
		//###//将获取的系统资源状态存入数据库
		//###SystemResourceDB sysResDB = new SystemResourceDB( SystemConfig.getSysResrouceDBConfig );
		//###sysResDB.update( this );
	}
	
	//获取资源状态
	public DataCenter getDataCenterStatus(String centerName){
		System.out.println( "SystemResource::public DataCenter getDataCenterStatus(String centerName) | 获取资源状态" );
		return null;
	}
	public boolean getProductionSystemStatus(String systemName){
		System.out.println( "SystemResource::public boolean getProductionSystemStatus(String systemName) | 获取资源状态" );
		return true;
	}
	public float getPulicBufferUsage(){
		System.out.println( "SystemResource::public float getPulicBufferUsage() | 获取资源状态" );
		return 0;
	}
	
	//用来存放各个数据中心的状态
	public class DataCenter {
		//数据分中心名称
		String name;
		//可用状态
		boolean isWorking ;
		//CPU使用率
		float cpu;
		//内存使用率
		float mem;
		//系统订单生产的磁盘使用率
		float diskUsage;
		//setter, getter方法
		public String getName(){
			System.out.println( "DataCenter::public String getName() | " );
			return "";
		}
		public boolean getIsWorking(){
			System.out.println( "DataCenter::public boolean getIsWorking() | " );
			return true;
		}
		public float getCPU(){
			System.out.println( "DataCenter::public float getCPU() | " );
			return 0;
		}
		public float getMem(){
			System.out.println( "DataCenter::public float getMem() | " );
			return 0;
		}
		public float getDiskUsage(){
			System.out.println( "DataCenter::public float getDiskUsage() | " );
			return 0;
		}
		//!!!public String setName();
		//!!!public String setIsWorking();
		//!!!public String setCPU();
		//!!!public String setMem();
		//!!!public String setDiskUsage();
		public void setName(String name){
			System.out.println( "DataCenter::public void setName(String name) | " );
		}
		public void setIsWorking(boolean isWorking){
			System.out.println( "DataCenter::public void setIsWorking(boolean isWorking) | " );
		}
		public void setCPU(float cpu){
			System.out.println( "DataCenter::public void setCPU(float cpu) | " );
		}
		public void setMem(float mem){
			System.out.println( "DataCenter::public void setMem(float mem) | " );
		}
		public void setDiskUsage(float diskUsage){
			System.out.println( "DataCenter::public void setDiskUsage(float diskUsage) | " );
		}
	}
}
