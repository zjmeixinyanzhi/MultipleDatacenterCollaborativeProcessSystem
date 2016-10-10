/*
 *程序名称 		: workflowSchedular.java
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
package Workflow;

import java.util.ArrayList;
import java.util.Iterator;

import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import TaskSchedular.TaskSchedular;

/**
 * @author caoyang
 *
 */
public class WorkflowSchedular extends Thread {
	//工作流状态定义
	public static final int ACTIVATED = 1;	//(激活)
	public static final int SUSPENDED = 2;	//(挂起)
	public static final int KILLED    = 3;	//(取消)
	public static final int COMPLETED = 4;	//(完成)
	public static final int UNKNOWN   = -1;	//(未知)
	//用来存放工作流对应的xml
	protected String workflowXml;
	//用来存放工作流实例对应的编号
	protected String workflowId;
	//用来存放订单号（二级）
	protected String orderId;
	//用来存放工作流的生产状态：生产到哪个子订单产品
	protected String workflowStatus;
	//用来存放工作流的运行状态：ACTIVATED, COMPLETED,ERROR等
	protected int workflowWorkStatus;
	//用来存放订单解析后的任务运行顺序列表（任务：三级订单对应的任务）
	protected ArrayList<L3InternalOrder> l3OrderTaskList;
	//……

	//构造函数 初始化工作流调度类
	public WorkflowSchedular(String wfXMLFile,String orderId){
		System.out.println( "WorkflowSchedular::public WorkflowSchedular(String wfXMLFile,String orderId) | 构造函数 初始化工作流调度类" );

		this.workflowXml        = wfXMLFile;
		this.orderId            = orderId;
		
		this.workflowId         = "";	//工作流对应的编号
		this.workflowStatus     = "";
		this.workflowWorkStatus = UNKNOWN;
	}

	//调度并运行生产流程   : 没用到
	public void sched(){
		System.out.println( "WorkflowSchedular::public void sched() | 调度并运行生产流程" );
		
		this.workflowWorkStatus = ACTIVATED;
		
		OrderStudio orderStudio = new OrderStudio();
		//更新订单状态
		orderStudio.setL2OrderWorkingStatus( this.orderId, "Queue" );
		
		if( this.l3OrderTaskList != null ){
			//更新订单状态
			orderStudio.setL2OrderWorkingStatus( this.orderId, "Running" );
			
			String strPreL3OrderId = null;
			
			Iterator< L3InternalOrder > it_L3Order = this.l3OrderTaskList.iterator();
			//轮询3级订单任务列表
			while( it_L3Order.hasNext() ){
				L3InternalOrder l3Order = it_L3Order.next();

				//创建任务调度对象
				TaskSchedular newTask = new TaskSchedular( l3Order );
				//更新订单状态
				//根据调度规则，为处理任务（三级内部订单）查找最匹配的算法资源（产品生产分系统），得到算法资源所在服务的配置信息
				if( false == newTask.doMatch() ){
					//出错
					//更新订单状态
					orderStudio.setL2OrderWorkingStatus( this.orderId, "Error" );
					return;
				}
				//执行每个任务
				newTask.sched();
				//等待任务执行完成
				String strL3OrderWorkingStatus = newTask.waitTillDone();	//等待任务（三级订单），直至其执行完成，其执行状态更新为FINISHED 或者ERROR
				
				if( strL3OrderWorkingStatus.equals( "Finish" ) ){
					//完成
					//更新订单生产状态
					orderStudio.setL2OrderProductStatus( this.orderId, l3Order.orderType );
					this.workflowStatus = l3Order.orderType;
				}else{
					//出错
					//更新订单状态
					orderStudio.setL2OrderWorkingStatus( this.orderId, "Error" );
					return;
				}
				
				//向课题三反馈订单状态
				//System.out.println( "13.向课题三反馈订单状态" );
				//---ServiceInterface.IOrderStatusFeedback();
			}
			//更新订单状态
			orderStudio.setL2OrderWorkingStatus( this.orderId, "Finish" );
		}else{
			//更新订单状态
			orderStudio.setL2OrderWorkingStatus( this.orderId, "Error" );
		}
	}

	//设置三级处理订单（任务）的执行顺序列表
	public void setTaskList( ArrayList<L3InternalOrder> l3OrderTaskList ){
		System.out.println( "WorkflowSchedular::public void setTaskList( ArrayList<L3InternalOrder> l3OrderTaskList ) | 设置三级处理订单（任务）的执行顺序列表" );
		this.l3OrderTaskList = l3OrderTaskList;
	}
	
	//获取当前工作流的生产状态,生产到哪个子订单产品
	public String getWFStatus(){
		System.out.println( "WorkflowSchedular::public String getWFStatus() | 获取当前工作流的生产状态,生产到哪个子订单产品" );
		return this.workflowStatus;
	}

	//获取当前工作流的运行状态，ACTIVATED, COMPLETED,ERROR等
	public int getWFWorkStatus(){
		System.out.println( "WorkflowSchedular::public int getWFWorkStatus() | 获取当前工作流的运行状态，ACTIVATED, COMPLETED,ERROR等" );
		return this.workflowWorkStatus;
	}
	
	// 功能：通过修改工作流状态实现工作流挂起
	public void hang(){
		System.out.println( "WorkflowSchedular::public void hang() | 通过修改工作流状态实现工作流挂起" );
		try {
			//更新订单状态
			OrderStudio orderStudio = new OrderStudio();
			orderStudio.setL2OrderWorkingStatus( this.orderId, "Suspend" );
			this.workflowWorkStatus = SUSPENDED;
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// 功能：通过修改工作流状态继续被挂起的工作流
	public void reactivate(){
		System.out.println( "WorkflowSchedular::public void reactivate() | 通过修改工作流状态继续被挂起的工作流" );
		//更新订单状态
		OrderStudio orderStudio = new OrderStudio();
		orderStudio.setL2OrderWorkingStatus( this.orderId, "Running" );
		this.workflowWorkStatus = ACTIVATED;
		notify();
	}
	
	// 功能：杀死运行工作流实例的线程。
	@SuppressWarnings("deprecation")
	public void cancel(){
		System.out.println( "WorkflowSchedular::public void cancel() | 杀死运行工作流实例的线程" );
		this.workflowWorkStatus = KILLED;
		stop();
	}
	

}
