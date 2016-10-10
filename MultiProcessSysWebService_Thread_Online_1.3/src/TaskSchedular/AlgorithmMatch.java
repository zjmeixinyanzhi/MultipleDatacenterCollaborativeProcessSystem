package TaskSchedular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import DBManage.AlgorithmDB;
import DBManage.DataCenter;
import DBManage.L3OrderDB;
import DBManage.ScheduleRuleDB;
import DBManage.SystemResourceDB;
import DBManage.TestDBConnection;

/**
 * 创建时间：2014-11-15 下午11:49:56 项目名称：MCA_KeplerWorkflow_Engine_L3Order 2014-11-15
 * 
 * @author 张杰
 * @version 1.0 文件名称：MatchSystemResource.java 类说明：
 */
public class AlgorithmMatch {
	// 算法类型
	private String l3OrderType = null;
	// 算法处理数据类型
	private String l3OrderDataType = null;
	// 算法所在系统的IP
	private String HostIP = null;
	// 匹配后算法
	public Algorithm MatchedAlgorithm = null;
	// 算法提交Webservice Url
	private String webServiceUrl = null;
	// 算法所在数据中心的绝对路径：数据中心Home目录+算法脚本位置
	private String AlgorithmAbsolutePath = null;

	public AlgorithmMatch(String L3OrderType, String L3OrderDataType,
			String hostIp) {
		this.l3OrderType = L3OrderType;
		this.l3OrderDataType = L3OrderDataType;
		this.HostIP = hostIp;
	}

	// 按照算法所在数据中心、算法类型、处理数据类型匹配算法
	public Algorithm doMatch() {
		System.out
				.println("MatchSystemResource::public boolean doMatch() | 算法资源匹配");
		
		//查找对应算法类型和数据类型的算法
		AlgorithmDB algorithmDB=new AlgorithmDB();
		Algorithm algorithm=algorithmDB.searchUnique(this.l3OrderType, this.l3OrderDataType);
		if (null==algorithm) {
			return null;
		}
		this.MatchedAlgorithm=algorithm;
		
		//获取算法所在数据中心的配置信息
		SystemResourceDB systemResourceDB=new SystemResourceDB();		
		ArrayList<DataCenter> dataCenters=systemResourceDB.getDataCenterListByIP(this.HostIP);
		if (null==dataCenters) {
			return null;
		}
		
		if (dataCenters.size()!=1) {
			System.out.println("算法所在数据中心不唯一，请检查系统配置！");
			return null;
		}
		DataCenter dataCenter=dataCenters.get(0);
		//获取算法绝对路径
		this.AlgorithmAbsolutePath=dataCenter.getUserHomePath()+algorithm.getAlgorithmFilePath().substring(1);
		//获取算法提交的Webservice Url
		this.webServiceUrl="http://"+dataCenter.getHostIp()+":"+dataCenter.getProxySystemPort()+"/"+algorithm.getProcSystemConfig();
		

		return this.MatchedAlgorithm;
	}

	
	//获取算法提交的WebService
	public String getWebServiceUrl() {
		return webServiceUrl;
	}
	//获取算法绝对路径
	public String getAlgorithmAbsolutePath() {
		return AlgorithmAbsolutePath;
	}
	
	
	public static void main(String[] args) {
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();

		AlgorithmMatch algorithmMatch=new AlgorithmMatch("L3RN", "1KM-MODIS", "10.3.10.1");
		algorithmMatch.doMatch();
		System.out.println(algorithmMatch.getWebServiceUrl());
		System.out.println(algorithmMatch.getAlgorithmAbsolutePath());
		
		
		
	}
	

}
