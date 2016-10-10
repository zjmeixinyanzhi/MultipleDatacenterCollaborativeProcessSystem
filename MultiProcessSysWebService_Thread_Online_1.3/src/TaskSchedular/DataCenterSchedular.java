package TaskSchedular;

import java.util.ArrayList;
import java.util.Iterator;

import DBManage.DataCenter;
import DBManage.ScheduleRuleDB;

/**
 * 创建时间：2015-7-24 上午10:08:28 项目名称：MultiProcessSysWebService_Thread 2015-7-24
 * 
 * @author 张杰
 * @version 1.0 文件名称：DataCenterSchedular.java 类说明：数据中心调度，根据监控信息从多个数据中心中选出最优的数据中心
 */
public class DataCenterSchedular {
	
	//???如何体现主中心优先？？？？

	// 数据中心调度
	public DataCenter doSchedule(ArrayList<DataCenter> datacenterlist) {

		double MinResourceEvaluate = Double.MAX_VALUE;
		DataCenter BestDataCenter = null;
		boolean bMatch = true;

		// 迭代匹配所有资源，选出最优资源
		Iterator<DataCenter> it_datacenter = datacenterlist.iterator();
		while (it_datacenter.hasNext()) {
			// System.out.println(">>>>>>>>>>>>has datacenter!");
			bMatch = true;
			DataCenter tempDataCenter = it_datacenter.next();
			// System.out.println("+++++++++++" + tempDataCenter.toString() );
			// 匹配规则匹配
			bMatch &= RuleMatch(tempDataCenter);
			double cpu = tempDataCenter.getCPU();
			double memory = tempDataCenter.getMemory();
			double network = tempDataCenter.getNetWork();
			double io = tempDataCenter.getIO();
			double diskUsage = tempDataCenter.getDiskUsage();
			double Evaluation = (cpu + memory + network + io + diskUsage) / 5;// 等权重
			// 选出最优
			if (bMatch & (Evaluation < MinResourceEvaluate)) {
				BestDataCenter = tempDataCenter;
				// System.out.println( ">>>>>>>>>>>>bestdatacenter : " +
				// BestDataCenter.toString() );
			}
			else {
				System.out.println(tempDataCenter.getHostName()+"数据中心不满足要求！");
				
			}
		}
		
		
		if (BestDataCenter==null ||BestDataCenter.getID() == -1) {
			System.out.println("<Error> 数据中心匹配出错，资源库中暂时没有满足条件的系统资源！");
			return null;
		}

		return BestDataCenter;
	}

	// 匹配规则比较
	public boolean RuleMatch(DataCenter TempDataCenter) {
		System.out
				.println("MatchSystemResource::public boolean RuleMatch() | 数据中心匹配规则实时匹配");

		// 数据中心可访问性判断
		if (!TempDataCenter.getIsWorking()) {
			System.out
					.println("<Error>" + TempDataCenter.getID() + " 数据中心不可用！");
			return false;
		}

		ScheduleRuleDB RuleDB = new ScheduleRuleDB();
		ArrayList<ScheduleRule> scheduleRuleList = RuleDB.getScheduleRuleList();
		Iterator<ScheduleRule> it_scheduleRule = scheduleRuleList.iterator();

		while (it_scheduleRule.hasNext()) {
			ScheduleRule rule = it_scheduleRule.next();
			String ArgName = rule.argName;
			double fValue = Double.parseDouble(rule.value);
			String[] strConditionSplitArray = rule.condition.split(",");

			switch (ArgName) {
			case "cpu": {
				double bestCPU = TempDataCenter.getCPU();
				if (!ValueCompare(strConditionSplitArray[0], bestCPU, fValue)) {
					System.out.println("<Error> 匹配失败，resourcesId="
							+ TempDataCenter.getID() + "的CPU占用不满足匹配规则！");
					return false;
				}
				break;
			}
			case "io": {
				double io = TempDataCenter.getIO();
				if (!ValueCompare(strConditionSplitArray[0], io, fValue)) {
					System.out.println("<Error> 匹配失败，resourcesId="
							+ TempDataCenter.getID() + "的IO占用不满足匹配规则！");
					return false;
				}
				break;
			}
			case "diskUsage": {
				double diskUsage = TempDataCenter.getDiskUsage();
				if (!ValueCompare(strConditionSplitArray[0], diskUsage, fValue)) {
					System.out.println("<Error> 匹配失败，resourcesId="
							+ TempDataCenter.getID() + "的DiskUsage占用不满足匹配规则！");
					return false;
				}
				break;
			}
			case "network": {
				double network = TempDataCenter.getNetWork();
				if (!ValueCompare(strConditionSplitArray[0], network, fValue)) {
					System.out.println("<Error> 匹配失败，resourcesId="
							+ TempDataCenter.getID() + "的Network占用不满足匹配规则！");
					return false;
				}
				break;
			}
			case "memory": {
				double bestmem = TempDataCenter.getMemory();
				if (!ValueCompare(strConditionSplitArray[0], bestmem, fValue)) {
					System.out.println("<Error> 匹配失败，resourcesId="
							+ TempDataCenter.getID() + "Memory占用不满足匹配规则！");
					return false;
				}
				break;
			}
			default:
				break;
			}
		}
		return true;
	}

	// 值比较
	private boolean ValueCompare(String strCondition, double value1,
			double value2) {
		boolean bMatch = false;
		switch (strCondition) {
		case ">":
			if (value1 > value2) {
				bMatch = true;
			}
			break;
		case "<":
			if (value1 < value2) {
				bMatch = true;
			}
			break;
		case ">=":
			if (value1 >= value2) {
				bMatch = true;
			}
			break;
		case "<=":
			if (value1 <= value2) {
				bMatch = true;
			}
			break;
		case "==":
			if (value1 == value2) {
				bMatch = true;
			}
			break;

		default:
			break;
		}
		return bMatch;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
