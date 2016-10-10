package TaskExeAgent;
/**
 * 创建时间：2015-11-21 下午6:52:50
 * 项目名称：TaskExecutionAgent
 * 2015-11-21
 * @author 张杰
 * @version 1.0
 * 文件名称：ServerConfig.java
 * 类说明：执行获取环境变量脚本，获取服务器环境变量参数
 */
public class ServerConfig {
	// 系统IP
	public String ip;
	// 系统名称
	public String hostName;
	// 算法目录
	public String algorithPath;
	// 订单目录
	public String orderPath;
	// 系统运行日志目录
	public String logPath;
	// Globus安装目录
	public String globusPath;
	// 代理Port
	public String port;
	//PBS qsub Path
	public String qsubPath;
	//PBS qdel Path
	public String qdelPath;
	//主中心Webservice Url
	public String mcaWebserviceUrl;
	

	public ServerConfig(String ip2, String port2, String hostName2,
			String orderPath2, String logPath2, String algorithPath,
			String globusLocation,String qsubPath,String qdelPath,String mcaWebserviceUrl) {
		this.ip = ip2;
		this.hostName = hostName2;
		this.orderPath = orderPath2;
		this.logPath = logPath2;
		this.algorithPath = algorithPath;
		this.globusPath = globusLocation;
		this.port = port2;
		this.qsubPath=qsubPath;
		this.qdelPath=qdelPath;
		this.mcaWebserviceUrl=mcaWebserviceUrl;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getAlgorithPath() {
		return algorithPath;
	}

	public void setAlgorithPath(String algorithPath) {
		this.algorithPath = algorithPath;
	}

	public String getOrderPath() {
		return orderPath;
	}

	public void setOrderPath(String orderPath) {
		this.orderPath = orderPath;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getGlobusPath() {
		return globusPath;
	}

	public void setGlobusPath(String globusPath) {
		this.globusPath = globusPath;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public String getQsubPath() {
		return qsubPath;
	}

	public void setQsubPath(String qsubPath) {
		this.qsubPath = qsubPath;
	}

	public String getQdelPath() {
		return qdelPath;
	}

	public void setQdelPath(String qdelPath) {
		this.qdelPath = qdelPath;
	}

	public String getMcaWebserviceUrl() {
		return mcaWebserviceUrl;
	}

	public void setMcaWebserviceUrl(String mcaWebserviceUrl) {
		this.mcaWebserviceUrl = mcaWebserviceUrl;
	}


}
