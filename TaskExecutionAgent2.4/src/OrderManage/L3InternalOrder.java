/*
 *程序名称 		: L3InternalOrder.java
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
package OrderManage;

import java.sql.Date;
import java.util.ArrayList;

import RSDataManage.RSData;

/**
 * @author caoyang
 *
 */
public class L3InternalOrder extends Order {
	
	//该订单生产处理的产品生产分系统
	//产品生产系统名称：
	//DataServiceSystem / RadNormSystem / GeoNormSystem / CommonProductSystem / FusionSystem / AssimulationSystem / ValidationSystem
	public String processingSystem;
	//二级订单ID（L2）
	public String jobId_L2;
	//产品列表
	public ArrayList< String > strDataProductList;
	//与融合/同化相关联的共性产品信息
	public ArrayList< String > retrievalDataList;
	//pbs订单队列
	public ArrayList<String> pbsOrderLists;
	//重新提交次数
	public int reSubmitTimes;
	
	
	//构造函数
	public L3InternalOrder(){
		System.out.println( "L3InternalOrder::public L3InternalOrder() | 构造函数" );
		
		//订单ID
		this.jobId              = "";
		//一级订单ID（L1）
		this.jobId_L1           = "";
		//二级订单ID（L2）
		this.jobId_L2           = "";
		//订单级别
		this.orderLevel         = "3";
		//优先级
		this.priority           = 0;
		//产品名称
		this.productName        = "";
		//地理区域
		this.geoCoverageStr     = "";
		//时相（成像时间）开始时间
		this.startDate          = new Date( 0 );
		//时相（成像时间）结束时间
		this.endDate            = new Date( 0 );
		//提交时间
		this.submitDate         = new Date( 0 );
		//完成时间
		this.finishDate         = new Date( 0 );
		//订单状态
		this.workingStatus      = "Ready";	// "Finish" "Error" 订单初始状态为"Ready"
		//任务单参数
		this.orderParameter     = "";
		//操作员
		this.operatorId         = "";
		//算法资源名称
		this.algorithmName      = "";
		//算法资源程序
		this.algorithmPath      = "";
		//数据列表
		this.dataList           = new ArrayList< RSData >();
		//产品列表
		this.strDataProductList = new ArrayList< String >();
		//与融合/同化相关联的共性产品信息
		this.retrievalDataList  = new ArrayList< String >();
		//pbs订单列表
		this.pbsOrderLists=new ArrayList<String>();
		//重新提交次数
		this.reSubmitTimes=0;
	}
	
	//设置该订单生产处理的产品生产分系统
	public void setProcessingSystem( String systemName ){
		System.out.println( "L3InternalOrder::public void setProcessingSystem( String systemName ) | 设置该订单生产处理的产品生产分系统" );
		this.processingSystem = systemName;
	}
	
	//获取该订单生产处理的产品生产分系统
	public String getProcessingSystem(){
		System.out.println( "L3InternalOrder::public String getProcessingSystem() | 获取该订单生产处理的产品生产分系统" );
		return this.processingSystem;
	}

	//获取订单状态
	public String getWorkingStatus() {
		return this.workingStatus;
	}

	public String getJobId_L2() {
		return jobId_L2;
	}

	public void setJobId_L2(String jobId_L2) {
		this.jobId_L2 = jobId_L2;
	}

	public int getReSubmitTimes() {
		return reSubmitTimes;
	}

	public void setReSubmitTimes(int reSubmitTimes) {
		this.reSubmitTimes = reSubmitTimes;
	}

	//设置订单状态
	public void setWorkingStatus(String strOrderStatus) {
		this.workingStatus=strOrderStatus;		
	}

}
