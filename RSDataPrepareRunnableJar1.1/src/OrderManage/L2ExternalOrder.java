/*
 *程序名称 		: L2ExternalOrder.java
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author caoyang
 *
 */
public class L2ExternalOrder extends Order {
	
	public String jobId_P3L2;
	// 三级订单列表
	public String l3orderlist;
	
	//构造函数
	public L2ExternalOrder(){
		//订单ID（L2）
		this.jobId             = "";
		//工作流ID
		this.workFlowId        = 0;
		//优先级 0-9
		this.priority          = 1;
		//一级订单ID（L1）
		this.jobId_L1          = "";
		//订单ID（L2）
		this.jobId_P3L2        = "";
		//用来存放订单类型：CP/FP/AP/VD
		this.orderType         = "";
		//订单级别
		this.orderLevel        = "";
		//真实性检验
		this.doValidation      = "";
		//用来记录生产的共性数据产品名称
		this.productName       = "";
		//地理区域
		this.geoCoverageStr    = "";	// XML字符串
		//开始时间
		this.startDate         = new Date( 0 );
		//结束时间
		this.endDate           = new Date( 0 );
		//任务单参数
		this.orderParameter    = "";
		//操作员
		this.operatorId        = "";
		//用来记录订单的数据状态：Available/NotAvailable/Future
		this.dataStatus        = "";
		//用来记录订单的数据列表
		this.dataList          = new ArrayList< String >();
		//订单状态
		this.workingStatus     = "";	//"QUEUE"
		//订单生产状态
		this.orderStatus       = "";
		//三级订单列表
		this.l3orderlist       = "";
		//与融合/同化相关联的共性产品信息
		this.retrievalDataList = new ArrayList< String >();
		//原始数据类型
		this.dataType          = "Anything";
	}
}
