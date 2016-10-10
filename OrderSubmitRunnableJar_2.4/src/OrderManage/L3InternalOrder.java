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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * @author caoyang
 * 
 */
public class L3InternalOrder extends Order {
	// 产品生产系统名称：
	// DataServiceSystem / RadNormSystem / GeoNormSystem / CommonProductSystem /
	// FusionSystem / AssimulationSystem / ValidationSystem
	public String processingSystem;
	// 二级订单号
	public String jobId_L2;
	// 算法资源名称
	public String algorithmName;
	// 算法资源程序
	public String algorithmPath;
	// 产品列表
	public ArrayList<String> strDataProductList;
	// 算法资源ID
	public int algorithmID;

	// 四级订单列表：按照数据类型拆分
	public String l4orderlist;

	// 构造函数
	public L3InternalOrder() {
		System.out.println("L3InternalOrder::public L3InternalOrder() | 构造函数");

		this.jobId_L1 = "";
		this.jobId_L2 = "";
		this.orderLevel = "3";
		this.geoCoverageStr = "";
		// 订单状态
		this.workingStatus = "Ready"; // "Finish" "Error" 订单初始状态为"Ready"
		// 算法资源名称
		this.algorithmName = "";
		// 算法资源程序
		this.algorithmPath = "";
		// 产品列表
		this.strDataProductList = new ArrayList<String>();
		// 算法资源ID
		this.algorithmID = -1;
		// 与融合/同化相关联的共性产品信息
		this.retrievalDataList = new ArrayList<String>();
		// 原始数据类型
		this.dataType = "Anything";
	}

	// 构造函数 初始化二级订单
	public L3InternalOrder(L2ExternalOrder order) {
		System.out
				.println("L3InternalOrder::public L3InternalOrder( L2ExternalOrder order ) | 构造函数 初始化二级订单");

		this.jobId_L1 = order.jobId_L1;
		this.jobId_L2 = order.jobId;
		this.orderLevel = "3";
		this.geoCoverageStr = order.geoCoverageStr;
		this.startDate = order.startDate;
		this.endDate = order.endDate;
		// 订单状态
		this.workingStatus = "Ready"; // "Finish" "Error" 订单初始状态为"Ready"
		// 算法资源名称
		this.algorithmName = "";
		// 算法资源程序
		this.algorithmPath = "";
		// 产品列表
		this.strDataProductList = new ArrayList<String>();
		// 算法资源ID
		this.algorithmID = -1;
		// 与融合/同化相关联的共性产品信息
		this.retrievalDataList = order.retrievalDataList;
		// 原始数据类型
		this.dataType = order.dataType;
	}

}
