/*
 *程序名称 		: WorkflowDB.java
 *版权说明  		:
 *版本号		    : 1.0
 *功能			: 
 *开发人		    : caoyang 张杰
 *开发时间		: 2014-05-19
 *修改者		    : 
 *修改时间		: 
 *修改简要说明 	:
 *其他			:	 
 */
package DBManage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import OrderManage.Order;
import OrderManage.OrderRequest;
import RSDataManage.Rsdatatype;
import SystemManage.DBConfig;
import SystemManage.SystemLogger;

/**
 * @author caoyang
 * 
 */
public class WorkflowDB extends DBConn {

	private static Connection conn = null;
	private String dbTable;
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	// 构造函数
	public WorkflowDB() {
		System.out.println("WorkflowDB::public WorkflowDB () | 构造函数");

		// 初始化连接数据库
		if (null == conn) {
			conn = getConnection();
			if (conn == null) {
				this.bIsConnection = false;
				System.out
						.println("<Error>WorkflowDB::public WorkflowDB () | conn = getConnection() | conn == null");
			} else {
				this.bIsConnection = true;
			}
		}

		this.dbTable = "workflowdb";
	}

	public static synchronized void closeConnected() {
		try {
			if (null != conn) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 获取工作流与二级外部订单类型的关系列表
	public synchronized Hashtable<String, String> getOrderType2WorkflowMap() {
		System.out
				.println("WorkflowDB::public Hashtable< String, String > getOrderType2WorkflowMap() | 获取工作流与二级外部订单类型的关系列表");

		// 获取工作流与二级外部订单类型的关系列表
		Hashtable<String, String> workflowMap = new Hashtable<String, String>();

		try {
			String strSql = "SELECT * FROM " + this.dbTable;
			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return workflowMap;
			}
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);

			if (!rs.next()) {
				logger.error(" 获取工作流与二级外部订单类型的关系列表，查询结果为0！");
			} else {
				rs.previous();
				while (rs.next()) {
					// 工作流ID
					int workFlowId = rs.getInt("WorkFlowId");
					// 工作流名称
					String workFlowName = rs.getString("WorkFlowName");
					// 简单描述信息
					String description = rs.getString("Description");
					// 工作流详细描述
					String detail = rs.getString("Detail");
					// 工作流参数
					String wfParameter = rs.getString("WFParameter");
					// 工作流配置文件名称
					String workFlowFile = rs.getString("WORKFLOWFILE");
					// 工作流步骤
					String sequences = rs.getString("SEQUENCES");

					workflowMap.put(workFlowName, workFlowFile);
				}
			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {
			logger.error("获取工作流与二级外部订单类型的关系列表失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return getOrderType2WorkflowMap();
			}
			e.printStackTrace();
			workflowMap.clear();
			return workflowMap;
		}
		return workflowMap;
	}

	// 根据数据类型获取最复杂的二级外部订单类型
	public synchronized String geComprehensiveOrderTypeByDataType(String baseOrderType,TreeSet<String> preSteps) {
		System.out
				.println("WorkflowDB::public Hashtable< String, String > getOrderType2WorkflowMap() | 获取数据类型与二级外部订单类型的关系列表");

		// 获取数据类型与二级外部订单类型的关系列表
		String comprehensiveOrderType="";
		int    maxStep=0; 

		try {
			String strSql = "SELECT * FROM " + this.dbTable +" where WorkFlowName like '%"+baseOrderType+"%'";
			
			if (null == conn) {
				logger.error("数据库连接初始化失败！");
				return comprehensiveOrderType;
			}
			if (preSteps==null) {
				logger.error("预处理步骤为空！");
				return comprehensiveOrderType;
			}
			
			//test
//			System.out.println("<<<<<<<<<<<<"+strSql);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strSql);
			
			boolean flag=true;

			if (!rs.next()) {
				logger.error(" 获取工作流与二级外部订单类型的关系列表，查询结果为0！");
			} else {
				rs.previous();
				while (rs.next()) {
									
					// 工作流名称
					String workFlowName = rs.getString("WorkFlowName");
				
					// 数据类型
					String dataType = rs.getString("DataType");
					// 工作流步骤
					String workFlowFile = rs.getString("WORKFLOWFILE");
					if (workFlowFile!=null && dataType!=null ) {
						//遍历传递的预处理步骤，判断当前工作流是否包含全部的处理流程
						
//						System.out.println("<<<<<<<<<<<<"+workFlowName+":"+workFlowFile);
						
						Iterator<String> iterator=preSteps.iterator();
						while (iterator.hasNext()) {
							flag=true;	
							String currStep = (String) iterator.next();
							flag&=workFlowFile.contains(currStep);
//							System.out.print(currStep+" "+flag);
							if (!flag) {
								continue;
							}
						}
						//当前流程包含全部的预处理步骤
						if (flag) {
							comprehensiveOrderType=workFlowName;
						}
					}					
				}
			}

			// 关闭相关连接
			rs.close();
			st.close();
		} catch (SQLException e) {
			logger.error("获取工作流与二级外部订单类型的关系列表失败!");
			logger.error("SQL执行异常", e);

			String strSqlState = e.getSQLState();
			if (strSqlState.equals("08S01")) {
				conn = getConnection();
				return geComprehensiveOrderTypeByDataType(baseOrderType,preSteps);
			}
			e.printStackTrace();
			return comprehensiveOrderType;
		}
		return comprehensiveOrderType;
	}
	
	public static void main(String[] args) {
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();
		
		RSDataTypeDB rsDataTypeDB=new RSDataTypeDB();
		WorkflowDB db=new WorkflowDB();
		TreeSet<String> preProcesingSteps=new TreeSet<String>();
		String []dataTypeArrays="MERSI@FY3A;MERSI@FY3B".split(";");
		for (String data : dataTypeArrays) {
			String[] dataInfos=data.split("@");
			if (dataInfos.length!=2) {
				continue;
			}
			String condition="where satellite='" +
					dataInfos[1] +
					"' and sensor='" +
					 dataInfos[0]+
					"'";		
			ArrayList<Rsdatatype> rsdataTypes=rsDataTypeDB.search(condition);
			
			if (rsdataTypes!=null) {
				//獲取預處理步驟
				Rsdatatype currRsdatatype=rsdataTypes.get(0);
				if (currRsdatatype==null) {
					continue;
				}
				String []steps=currRsdatatype.getPreprocessing().split(";");
				for (int i = 0; i < steps.length; i++) {
					String currStep = steps[i];
					preProcesingSteps.add(currStep);
					//test
					System.out.println(currStep);
				}
			}				
		}
		
		
		//AQUA_MODIS;TERRA_MODIS;FY-3A-MERSI;FY-3B-MERSI;AVHRR;
//		TreeSet<String> preProcesingSteps=new TreeSet<String>();
		preProcesingSteps.add("L3RN");
		preProcesingSteps.add("L3DS");
		
		System.out.println(">>"+db.geComprehensiveOrderTypeByDataType("L2CP", preProcesingSteps));
		
	}

}
