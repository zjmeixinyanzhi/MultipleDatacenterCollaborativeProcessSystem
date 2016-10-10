/*
 *程序名称 		: DataCenter.java
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
package ResourceManage;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author caoyang
 * 
 */
// 用来存放各个数据中心的状态
public class DataCenter {
	private DecimalFormat dFormat;
	// 数据中心ID
	private int id;
	// 数据分中心名称
	private String name;
	// 类型
	private String type;
	// ip地址
	private String hostip;
	// 主机名称
	private String hostname;
	// 可用状态
	private boolean isWorking;
	// CPU使用率
	private double cpu;
	// 内存使用率
	private double mem;
	// 网络
	private double network;
	// IO
	private double io;
	// 系统订单生产的磁盘使用率
	private double diskUsage;
	// 负载率
	private double loadone;
	// 资源监控最后更新时间
	private String lastUpdateTime;

	// 其他配置信息
	// 公共缓冲区路径
	private String publicBufferDir;
	// 公共缓冲区使用率
	private double publicBufferUsage;
	// 公共缓冲区大小
	private double publicBufferSize;
	//代理系统部门简称
	private String DataBaseSchemasName;	
	// Globus中间件安装路径
	private String GLOBUS_HOME;
	// GridFTP Server端口
	private String GridFTPServerPort;
	// 子中心执行代理服务器端口
	private String ProxySystemPort;
	// 用户家目录
	private String userHomePath;

	// 构造函数
	public DataCenter() {
		this.dFormat = new DecimalFormat("#.00");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		this.id = -1;
		this.name = "";
		this.type = "";
		this.hostip = "";
		this.hostname = "";
		this.isWorking = false;
		this.cpu = 0.0f;
		this.mem = 0.0f;
		this.network = 0.0f;
		this.io = 0.0f;
		this.diskUsage = 0.0f;
		this.loadone = 0.0f;
		// 先取出时间，然后再更新
		this.lastUpdateTime = "1970-01-01 00:00:00";
		this.DataBaseSchemasName="";
		this.publicBufferDir="";
		this.publicBufferUsage=0.0f;
		this.publicBufferSize=0.0f;
		this.GLOBUS_HOME="";
		this.GridFTPServerPort="";
		this.ProxySystemPort="";
		this.userHomePath = "";
	}

	// setter, getter方法
	public int getID() {
		// System.out.println( "DataCenter::public int getID() | " );
		return this.id;
	}

	public String getName() {
		// System.out.println( "DataCenter::public String getName() | " );
		return this.name;
	}

	public String getType() {
		// System.out.println( "DataCenter::public String getType() | " );
		return this.type;
	}

	public String getHostIp() {
		// System.out.println( "DataCenter::public String getHostIp() | " );
		return this.hostip;
	}

	public String getHostName() {
		// System.out.println( "DataCenter::public String getHostName() | " );
		return hostname;
	}

	public boolean getIsWorking() {
		// System.out.println( "DataCenter::public boolean getIsWorking() | " );
		return this.isWorking;
	}

	public double getCPU() {
		// System.out.println( "DataCenter::public double getCPU() | " );
		return this.cpu;
	}

	public double getMemory() {
		// System.out.println( "DataCenter::public double getMemory() | " );
		return this.mem;
	}

	public double getNetWork() {
		// System.out.println( "DataCenter::public double getNetWork() | " );
		return this.network;
	}

	public double getIO() {
		// System.out.println( "DataCenter::public double getIO() | " );
		return this.io;
	}

	public double getDiskUsage() {
		// System.out.println( "DataCenter::public double getDiskUsage() | " );
		return this.diskUsage;
	}

	public double getLoadOne() {
		// System.out.println( "DataCenter::public double getLoadOne() | " );
		return this.loadone;
	}

	public void setID(int id) {
		// System.out.println( "DataCenter::public void setID( int id ) | " );
		if (-1 == this.id) {
			this.id = id;
		}
	}

	public void setName(String name) {
		// System.out.println( "DataCenter::public void setName(String name) | "
		// );
		if (null == name) {
			return;
		}
		this.name = name;
	}

	public void setType(String type) {
		// System.out.println(
		// "DataCenter::public void setType( String type ) | " );
		if (null == type) {
			return;
		}
		this.type = type;
	}

	public void setHostIp(String ip) {
		// System.out.println(
		// "DataCenter::public void setHostIp( String ip ) | " );
		if (null == ip) {
			return;
		}
		if ((!ip.isEmpty()) && (ip.length() <= 17)) {
			this.hostip = ip;
		}
	}

	public void setHostName(String hostname) {
		// System.out.println(
		// "DataCenter::public void setHostName( String hostname ) | " );
		if (null == hostname) {
			return;
		}
		this.hostname = hostname;
	}

	public void setIsWorking(boolean isWorking) {
		// System.out.println(
		// "DataCenter::public void setIsWorking(boolean isWorking) | " );
		this.isWorking = isWorking;
	}

	public void setCPU(double cpu) {
		// System.out.println( "DataCenter::public void setCPU(double cpu) | "
		// );
		try {
			this.cpu = Double.valueOf(this.dFormat.format(cpu));
		} catch (NumberFormatException e) {
			this.cpu = -1.0f;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMemory(double memory) {
		// System.out.println(
		// "DataCenter::public void setMemory(double memory) | " );
		try {
			this.mem = Double.valueOf(this.dFormat.format(memory));
		} catch (NumberFormatException e) {
			this.mem = -1.0f;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setNetWork(double network) {
		// System.out.println(
		// "DataCenter::public void setNetWork( double network ) | " );
		try {
			this.network = Double.valueOf(this.dFormat.format(network));
		} catch (NumberFormatException e) {
			this.network = -1.0f;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setIO(double io) {
		// System.out.println( "DataCenter::public void setIO( double io ) | "
		// );
		try {
			this.io = Double.valueOf(this.dFormat.format(io));
		} catch (NumberFormatException e) {
			this.io = -1.0f;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDiskUsage(double diskUsage) {
		// System.out.println(
		// "DataCenter::public void setDiskUsage(double diskUsage) | " );
		try {
			this.diskUsage = Double.valueOf(this.dFormat.format(diskUsage));
		} catch (NumberFormatException e) {
			this.diskUsage = -1.0f;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDataBaseSchemasName() {
		return DataBaseSchemasName;
	}

	public void setDataBaseSchemasName(String dataBaseSchemasName) {
		DataBaseSchemasName = dataBaseSchemasName;
	}

	public void setLoadOne(double loadone) {
		// System.out.println(
		// "DataCenter::public void setLoadOne( double loadone ) | " );
		try {
			this.loadone = Double.valueOf(this.dFormat.format(loadone));
		} catch (NumberFormatException e) {
			this.loadone = -1.0f;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUserHomePath() {
		return userHomePath;
	}

	public void setUserHomePath(String userHomePath) {
		this.userHomePath = userHomePath;
	}

	public String getPublicBufferDir() {
		return publicBufferDir;
	}

	public double getPublicBufferUsage() {
		return publicBufferUsage;
	}

	public double getPublicBufferSize() {
		return publicBufferSize;
	}

	public String getGLOBUS_HOME() {
		return GLOBUS_HOME;
	}

	public String getGridFTPServerPort() {
		return GridFTPServerPort;
	}

	public String getProxySystemPort() {
		return ProxySystemPort;
	}

	public void setPublicBufferDir(String publicBufferDir) {
		this.publicBufferDir = publicBufferDir;
	}

	public void setPublicBufferUsage(double publicBufferUsage) {
		try {
			this.publicBufferUsage = Double.valueOf(this.dFormat.format(publicBufferUsage));
		} catch (NumberFormatException e) {
			this.cpu = -1.0f;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPublicBufferSize(double publicBufferSize) {
		this.publicBufferSize = publicBufferSize;
	}

	public void setGLOBUS_HOME(String gLOBUS_HOME) {
		GLOBUS_HOME = gLOBUS_HOME;
	}

	public void setGridFTPServerPort(String gridFTPServerPort) {
		GridFTPServerPort = gridFTPServerPort;
	}

	public void setProxySystemPort(String proxySystemPort) {
		ProxySystemPort = proxySystemPort;
	}

	public String toString() {
		String strValue = "";

		strValue += "id=" + this.id + ",";
		strValue += "name=" + this.name + ",";
		strValue += "type=" + this.type + ",";
		strValue += "hostip=" + this.hostip + ",";
		strValue += "hostname=" + this.hostname + ",";
		strValue += "isWorking=" + (this.isWorking ? "true" : "false") + ",";
		strValue += "cpu=" + this.cpu + ",";
		strValue += "mem=" + this.mem + ",";
		strValue += "io=" + this.io + ",";
		strValue += "diskUsage=" + this.diskUsage + ",";
		strValue += "loadone=" + this.loadone + ",";
		strValue += "LastUpdateTime=" + this.lastUpdateTime + ",";
		return strValue;
	}
}
