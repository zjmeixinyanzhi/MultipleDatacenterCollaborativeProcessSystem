/*
 *程序名称 		: TaskSchedular.java
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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

import DBManage.AlgorithmDB;
import DBManage.ScheduleRuleDB;
import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import ServiceInterface.TaskExecutionAgentServiceImplProxy;

/**
 * @author caoyang
 *
 */
public class TaskSchedular {
	//用来存放三级内部订单
	protected L3InternalOrder l3Order;
	//用来存放匹配的算法资源
	protected Algorithm matchedAlgorithm;
	//用来存放资源匹配与调度规则
	//###protected Hashtable scheduleRules;
	//用来存放当前正在运行任务的运行任务号：pbs任务号
	protected String runningTaskId;

	//构造函数
	public TaskSchedular( L3InternalOrder l3Order ){
		System.out.println( "TaskSchedular::public TaskSchedular( L3InternalOrder l3Order ) | 构造函数" );
		//###//设置所需调度的任务（三级内部订单）
		//###this.l3Order = l3Order;
		//###//初始化处理系统名称
		//###this.procSystemName = l3Order.getProcSystemName();	//@@@ 这个处理系统是从哪里来？类属性里边没有这个属性，不需要了？
		//###//获取调度规则库的配置信息
		//###DBConfig schedRulesDBConfig = SystemConfig.getScheduleRuleDBConfig();
		//###//创建工作流数据库对象
		//###ScheduleRuleDB schedRuleDB = new ScheduleRuleDB(schedRulesDBConfig);
		//###//从调度规则库载入任务调度规则
		//###this.scheduleRules = schedRuleDB.getschedRules();
		
		//设置所需调度的任务（三级内部订单）
		this.l3Order          = l3Order;
		//初始化匹配的算法资源
		this.matchedAlgorithm = new Algorithm();
		//初始化任务号
		this.runningTaskId    = "";
	}
	
	//算法资源匹配   zj已经剥离，不再使用
	public boolean doMatch(){
		System.out.println( "TaskSchedular::public boolean doMatch() | 算法资源匹配" );
		//根据调度规则，为处理任务（三级内部订单）查找最匹配的算法资源（产品生产分系统），得到算法资源所在服务的配置信息
		
		//获取算法资源库配置信息
		
		//创建算法资源库对象
		AlgorithmDB algorithmDB = new AlgorithmDB();
		
		//系统资源库
		//SystemResourceDB systemResourceDB = new SystemResourceDB();
		
		//调度规则库
		ScheduleRuleDB scheduleRuleDB = new ScheduleRuleDB();
		ArrayList< ScheduleRule > scheduleRuleList = scheduleRuleDB.getScheduleRuleList();
		
		//查询当前任务的可用算法资源
		ArrayList< Algorithm > algorithmList = algorithmDB.search( this.l3Order.orderType, this.l3Order.dataType );
		
		//test
		System.out.println( "===================algorithmList size:" + algorithmList.size() );
		
		//循环
		//ArrayList< Algorithm > matchedList = new ArrayList< Algorithm >();
		Iterator< Algorithm > it_algorithm = algorithmList.iterator();
		//float fEvaluateMax = 0.0f;
		float fEvaluateMin = 0.0f;
		//Algorithm algorithmMax = null;
		Algorithm algorithmMin = null;
		while( it_algorithm.hasNext() ){
			boolean bMatch = true;
			Algorithm currAlgorithm = it_algorithm.next();
			
			if( currAlgorithm.dataCenter.getIsWorking() ){
				
				Iterator< ScheduleRule > it_scheduleRule = scheduleRuleList.iterator();
				while( it_scheduleRule.hasNext() ){
					
					//基于规则库的算法资源匹配
					
					//判断 所有规则匹配结束
					
					//循环结束
					
					ScheduleRule scheduleRule = it_scheduleRule.next();
					if( scheduleRule.argName.equals( "cpu" ) ){
						String[] strConditionSplitArray = scheduleRule.condition.split( "," );
						if( strConditionSplitArray.length >= 2 ){
							float fValue = Float.parseFloat( scheduleRule.value );
							bMatch &= matchRule( strConditionSplitArray[ 0 ], currAlgorithm.dataCenter.getCPU(), fValue );
							if( "MAX".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += ( 1 - currAlgorithm.dataCenter.getCPU() / fValue );
							}else if( "MIN".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += currAlgorithm.dataCenter.getCPU() / fValue;
							}else{
								currAlgorithm.evaluate += 1;
							}
						}
					}
					if( scheduleRule.argName.equals( "memory" ) ){
						String[] strConditionSplitArray = scheduleRule.condition.split( "," );
						if( strConditionSplitArray.length >= 2 ){
							float fValue = Float.parseFloat( scheduleRule.value );
							bMatch &= matchRule( strConditionSplitArray[ 0 ], currAlgorithm.dataCenter.getMemory(), fValue );
							if( "MAX".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += ( 1 - currAlgorithm.dataCenter.getMemory() / fValue );
							}else if( "MIN".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += currAlgorithm.dataCenter.getMemory() / fValue;
							}else{
								currAlgorithm.evaluate += 1;
							}
						}
					}
					if( scheduleRule.argName.equals( "network" ) ){
						String[] strConditionSplitArray = scheduleRule.condition.split( "," );
						if( strConditionSplitArray.length >= 2 ){
							float fValue = Float.parseFloat( scheduleRule.value );
							bMatch &= matchRule( strConditionSplitArray[ 0 ], currAlgorithm.dataCenter.getNetWork(), fValue );
							if( "MAX".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += ( 1 - currAlgorithm.dataCenter.getNetWork() / fValue );
							}else if( "MIN".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += currAlgorithm.dataCenter.getNetWork() / fValue;
							}else{
								currAlgorithm.evaluate += 1;
							}
						}
					}
					if( scheduleRule.argName.equals( "io" ) ){
						String[] strConditionSplitArray = scheduleRule.condition.split( "," );
						if( strConditionSplitArray.length >= 2 ){
							float fValue = Float.parseFloat( scheduleRule.value );
							bMatch &= matchRule( strConditionSplitArray[ 0 ], currAlgorithm.dataCenter.getIO(), fValue );
							if( "MAX".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += ( 1 - currAlgorithm.dataCenter.getIO() / fValue );
							}else if( "MIN".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += currAlgorithm.dataCenter.getIO() / fValue;
							}else{
								currAlgorithm.evaluate += 1;
							}
						}
					}
					if( scheduleRule.argName.equals( "diskUsage" ) ){
						String[] strConditionSplitArray = scheduleRule.condition.split( "," );
						if( strConditionSplitArray.length >= 2 ){
							float fValue = Float.parseFloat( scheduleRule.value );
							bMatch &= matchRule( strConditionSplitArray[ 0 ], currAlgorithm.dataCenter.getDiskUsage(), fValue );
							if( "MAX".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += ( 1 - currAlgorithm.dataCenter.getDiskUsage() / fValue );
							}else if( "MIN".equals( strConditionSplitArray[ 1 ] ) ){
								currAlgorithm.evaluate += currAlgorithm.dataCenter.getDiskUsage() / fValue;
							}else{
								currAlgorithm.evaluate += 1;
							}
						}
					}
				}
				
				if( bMatch ){
					//if( fEvaluateMax < currAlgorithm.evaluate ){
					//	fEvaluateMax = currAlgorithm.evaluate;
					//	algorithmMax = currAlgorithm;
					//}
					if( fEvaluateMin > currAlgorithm.evaluate ){
						fEvaluateMin = currAlgorithm.evaluate;
						algorithmMin = currAlgorithm;
					}else if( fEvaluateMin == currAlgorithm.evaluate ){
						fEvaluateMin = currAlgorithm.evaluate;
						algorithmMin = currAlgorithm;
					}
					
					//matchedList.add( currAlgorithm );
				}

			}
			//test
			System.out.println( "---------Match:" + ( bMatch?"True":"False" ) + " isWorking:" + ( currAlgorithm.dataCenter.getIsWorking()?"True":"False" ) + " Evaluate:" + currAlgorithm.evaluate );
		}
		
		//判断 已匹配的数量
		//if( matchedList.size() > 0 ){
		//	//匹配成功 返回匹配结果
		//	this.matchedAlgorithm = matchedList.get( 0 );
		//	return true;
		//}else{
		//	//匹配失败
		//	return false;
		//}
		
		if( algorithmMin != null ){
			this.matchedAlgorithm    = algorithmMin;
			this.l3Order.algorithmID = this.matchedAlgorithm.getAlgorithmID();
			OrderStudio orderStudio = new OrderStudio();
			if( !orderStudio.setL3OrderAlgorithmID( this.l3Order.jobId, this.matchedAlgorithm ) ){
				System.out.println( "<Error>TaskSchedular::public TaskSchedular( L3InternalOrder l3Order ) | 构造函数 | orderStudio.setL3OrderAlgorithmID | ID = " + this.l3Order.jobId + " | AlgorithmID = " + this.l3Order.algorithmID );
				return false;
			}
			
			//test
			System.out.println( "+++++++++++++++++++++++ Matched algorithm : " + this.matchedAlgorithm.toString() );
			
			return true;
		}else{
			return false;
		}

		//test
		//return true;
	}

	//提交并运行处理任务（三级内部订单）
	public void sched(){
		System.out.println( "TaskSchedular::public void sched() | 提交并运行处理任务（三级内部订单）" );
		
		//判断 三级订单的订单类型是否为L3CP
		if( l3Order.orderType.equals( "L3CP" ) ){
		
			//共性产品生产（L3CP）
			//向共性产品分系统提交生产任务
			//返回生产确认
		}else{
			
			//非 共性产品生产（L3CP）
			//向子订单执行代理提交生产任务
			//返回任务号
			//TaskExecutionAgentServiceImplProxy proxy = new TaskExecutionAgentServiceImplProxy( "http://localhost:3080/TaskExecutionAgent/services/TaskExecutionAgentServiceImpl" );	//服务器代理设置
			TaskExecutionAgentServiceImplProxy proxy = new TaskExecutionAgentServiceImplProxy();
			if( l3Order.orderType.equals( "L3RN" ) || l3Order.orderType.equals( "L3GN" ) ){
				//@@@ ProductName是否可以用订单类型来代替还有待商榷
				//@@@ Data中没有ULX、ULY这两个项目，暂时用Rows和Samples代替
				//@@@ 在6.3.10 订单产品反馈接口（共性/同化/融合/辐射归一化/几何归一化订单）中，
				//    子订单执行代理模块向主中心反馈的数据产品信息里边，有FTP的用户名和密码，
				//    在下达三级订单的接口参数里边没有，如何统一？默认不要密码？加上？
				String [] geoCoverageStrList = this.l3Order.geoCoverageStr.split( "," );
				ArrayList< String > dataList = this.l3Order.dataList;
				Iterator< String > it_data = dataList.iterator();
				String strDatas = "<Datas>";
				int iDataIndex = 1;
				while( it_data.hasNext() ){
					String [] strDataKVList = it_data.next().split( "," );
					String [] strKV = strDataKVList[ 0 ].split( "=" );
					strDatas += "<Data id=\"" + iDataIndex + "\" Name=\"" + strKV[ 1 ] + "\">";
					strKV = strDataKVList[ 1 ].split( "=" );
					strDatas += "<url>" + strKV[ 1 ] + "</url>";
					strKV = strDataKVList[ 2 ].split( "=" );
					strDatas += "<Rows>" + strKV[ 1 ] + "</Rows>";
					strKV = strDataKVList[ 3 ].split( "=" );
					strDatas += "<Samples>" + strKV[ 1 ] + "</Samples>";
					strKV = strDataKVList[ 2 ].split( "=" );
					strDatas += "<ULX>" + strKV[ 1 ] + "</ULX>";
					strKV = strDataKVList[ 3 ].split( "=" );
					strDatas += "<ULY>" + strKV[ 1 ] + "</ULY>";
					strDatas += "</Data>";
					iDataIndex++;
				}
				strDatas += "</Datas>";
				String strRequestXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						"<NormalizationOrderSubmit>" +
						"<L1OrderId>" + this.l3Order.jobId_L1  + "</L1OrderId>" +
						"<L2OrderId>" + this.l3Order.jobId_L2  + "</L2OrderId>" +
						"<L3OrderId>" + this.l3Order.jobId     + "</L3OrderId>" +
						"<OrderType>" + this.l3Order.orderType + "</OrderType>" +
						"<AlgorithmName>" + this.matchedAlgorithm.getAlgorithmName() + "</AlgorithmName>" +
						"<AlgorithmPath>" + this.matchedAlgorithm.getAlgorithmFilePath() + "</AlgorithmPath>" +
						"<Parameters>" +
							"<ProductName>" + this.l3Order.orderType + "</ProductName>" +
							"<ULLat>" + geoCoverageStrList[ 0 ] + "</ULLat>" +
							"<ULLong>" + geoCoverageStrList[ 1 ] + "</ULLong>" +
							"<LRLat>" + geoCoverageStrList[ 2 ] + "</LRLat>" +
							"<LRLong>" + geoCoverageStrList[ 3 ] + "</LRLong>" +
							"<StartDate>" + this.l3Order.startDate + "</StartDate>" +
							"<EndDate>" + this.l3Order.endDate + "</EndDate>" +
						"</Parameters>" +
						strDatas +
						"<ReferenceRSDatas>" +
							"<Data id=\"1\" Name=\"hb\">" +
								"<url>ftp://XXXXXXXXXXXXXXXX</url>" +
								"<ULX>38.4886</ULX>" +
								"<ULY>117.604</ULY>" +
								"<Rows>38.4886</Rows>" +
								"<Samples>117.604</Samples>" +
								"<ProjCode>+proj=utm +zone=11 +datum=WGS84</ProjCode>" +
							"</Data>" +
							"<Data id=\"2\" Name=\"bj\">" +
								"<url>ftp://XXXXXXXXXXXXXXXX</url>" +
								"<ULX>38.4886</ULX>" +
								"<ULY>117.604</ULY>" +
								"<Rows>38.4886</Rows>" +
								"<Samples>117.604</Samples>" +
								"<ProjCode>+proj=utm +zone=11 +datum=WGS84</ProjCode>" +
							"</Data>" +
						"</ReferenceRSDatas>" +
					"</NormalizationOrderSubmit>";
				
				try {
					String strRet = proxy.normalizationOrderSubmit( strRequestXML );
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//等待任务（三级订单），直至其执行完成，其执行状态更新为FINISHED 或者ERROR
	public String waitTillDone(){
		System.out.println( "TaskSchedular::public void waitTillDone() | 等待任务（三级订单），直至其执行完成，其执行状态更新为FINISHED 或者ERROR" );
		//查询数据库表三级订单里的订单状态，如果为Finish/Error则退出，否则间隔一段时间查询该订单状态
		//订单状态是子订单运行代理模块通过主中心的Webservice接口TaskStatus来设置更新的
		//@@@ 更新三级订单状态
		OrderStudio orderStudio = new OrderStudio();	//@@@ 如果有很多订单的话 应该会造成连接数不够的问题
		String strWorkingStatus = null;
		do{
			try {
				Thread.sleep(10000); //暂停，每一秒输出一次
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			strWorkingStatus = orderStudio.getL3OrderWorkingStatus( this.l3Order.jobId );
			
			//test
			if( this.l3Order.orderType.equals( "L3CP" ) ){
				orderStudio.setL3OrderWorkingStatus( this.l3Order.jobId, "Finish" );
				strWorkingStatus = "Finish";
			}
		}while( strWorkingStatus.equals( "Ready" ) );
		
		return strWorkingStatus;
		
	}
	
	//基于规则库的算法资源匹配
	//private ArrayList< Algorithm > matchRule( Hashtable scheduleRules, ArrayList< Algorithm > algorithmList ){
	//	;//@@@	基于规则库的算法资源匹配 matchRule  应该是三个表的查询与匹配
	//}
	
	private boolean matchRule( String strCondition, double value1, double value2 ){
		boolean bMatch = false;
		switch ( strCondition ) {
		case ">":
			if( value1 > value2 ){
				bMatch = true;
			}
			break;
		case "<":
			if( value1 < value2 ){
				bMatch = true;
			}
			break;
		case ">=":
			if( value1 >= value2 ){
				bMatch = true;
			}
			break;
		case "<=":
			if( value1 <= value2 ){
				bMatch = true;
			}
			break;
		case "==":
			if( value1 == value2 ){
				bMatch = true;
			}
			break;

		default:
			break;
		}
		return bMatch;
	}
}
