package OrderManage;

import java.util.ArrayList;

/**
 * 创建时间：2016年3月7日 下午3:50:34 项目名称：TaskExecutionAgent2.0 2016年3月7日
 * 
 * @author 张杰
 * @version 1.0 文件名称：L3OrderStatus.java 类说明：三级订单PBS算法处理进度统计,包括订单
 */
public class L3OrderPbsProgressCount {
	
	// 三级订单所属PBS订单的个数 
	public int sumCount; 
	// 运行状态PBS订单个数 R
	public ArrayList<String> runningOrderLists;
//	public int runningCount;	
	// 提交后等待状态PBS订单个数：Q
	public ArrayList<String> submitedOrderLists;
//	public int submitedCount;
	// 成功状态PBS订单个数 C_Success
	public ArrayList<String> finishOrderLists;
//	public int finishCount;
	// 失败状态PBS订单个数：C_Error
	public ArrayList<String> errorOrderLists;
//	public int errorCount;
	// 未知状态PBS订单个数
	public ArrayList<String> unknownOrderLists;
//	public int unknownCount;
//
	
	public L3OrderPbsProgressCount() {
		runningOrderLists=new ArrayList<String>();
		submitedOrderLists=new ArrayList<String>();
		finishOrderLists=new ArrayList<String>();
		errorOrderLists=new ArrayList<String>();
		unknownOrderLists=new ArrayList<String>();		
	}
	
	//获取状态
	public String getProgressCount() {
		return "Sum:"+getSumCount()+"_Running:"
				+ runningOrderLists.size()+"_Submited:"+submitedOrderLists.size()
				+ "_Success:"+finishOrderLists.size()+"_Error:"+errorOrderLists.size()+"_Unknown:"+unknownOrderLists.size();
	}
	
	public void setSumCount(int sumCount) {
		this.sumCount = sumCount;
	}

	//获取未确认状态的个数
	public int getStautsUnConfirmCount() {		
		return runningOrderLists.size()+submitedOrderLists.size();
	}
	//获取出错的订单
	public ArrayList<String> getUnFinishOrderLists(){
		ArrayList<String> arrayList=new ArrayList<String>();
		arrayList.addAll(errorOrderLists);
		arrayList.addAll(unknownOrderLists);
		
		return arrayList;
	}
	
	public int getUnFinishOrderCount(){		
		return errorOrderLists.size()+unknownOrderLists.size();
	}

	public int getSumCount() {
		return runningOrderLists.size()+submitedOrderLists.size()+finishOrderLists.size()+errorOrderLists.size()+unknownOrderLists.size();
	}

	public int getRunningCount() {
		return runningOrderLists.size();
	}

	public int getSubmitedCount() {
		return submitedOrderLists.size();
	}

	public int getFinishCount() {
		return finishOrderLists.size();
	}

	public int getErrorCount() {
		return errorOrderLists.size();
	}

	public int getUnknownCount() {
		return unknownOrderLists.size();
	}
	
	
	public void addRunningOrder(String runningOrder) {
		this.runningOrderLists.add(runningOrder);
	}

	public void addSubmitedOrder(String submitedOrder) {
		this.submitedOrderLists.add(submitedOrder);
	}

	public void addFinishOrder(String finishOrder) {
		this.finishOrderLists.add(finishOrder);
	}

	public void addErrorOrder(String errorOrder) {
		this.errorOrderLists.add(errorOrder);
	}

	public void addUnknownOrder(String unknownOrder) {
		this.unknownOrderLists .add(unknownOrder);
	}
	
	
	public static void main(String[] args) {
		L3OrderPbsProgressCount count=new L3OrderPbsProgressCount();
		
		System.out.println(count.getStautsUnConfirmCount());	
		System.out.println(count.getProgressCount());
		
	}
	
	

}
