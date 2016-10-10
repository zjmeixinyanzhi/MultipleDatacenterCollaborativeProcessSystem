package TaskExeAgent;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import Pbs.PbsOrder;
import RSDataManage.RSData;
import TaskSchedular.TaskExecuter;

/**
 * 创建时间：2016年2月25日 下午8:06:29 项目名称：TaskExecutionAgent2.0 2016年2月25日
 * 
 * @author 张杰
 * @version 1.0 文件名称：PBSOrderSubmitThread.java
 *          类说明：将L3Order按照数据拆分成PBS订单，一个数据对应一个PBS订单， 该类功能包括成PBS订单目录及参数文件，提交PBS订单
 */
public class PBSOrderSubmitThread extends Thread {

	private OrderStudio orderStudio = new OrderStudio();
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public void run() {

		for (;;) {
			// 轮询L3Order库，获取Ready状态的订单
			ArrayList<L3InternalOrder> l3OrderList = this.orderStudio
					.getOrderList("Ready");
			Iterator<L3InternalOrder> it_L3Order = l3OrderList.iterator();
			// 从数据库表中获取三级订单生产请求
			// 依次生成PBS订单并提交PBS，然后修改PBS订单状态
			while (it_L3Order.hasNext()) { // 对三级订单生产请求列表循环
				L3InternalOrder l3Order = it_L3Order.next();
				
				// 更新三级订单至Running状态
				this.orderStudio.setOrderWorkflowStatus(l3Order.jobId,
						"Submitting");
				//初始化重新提交次数为0

				TaskExecuter executer = new TaskExecuter(l3Order);
				// 生成订单根目录
				executer.generateWorkDir();
				// 生成
				Iterator<RSData> iterator = l3Order.dataList.iterator();
					//PBS订单集合
				ArrayList<String> pbsOrderLists=new ArrayList<String>();
				// 如果是标准产品生产处理流程订单
				if (l3Order.orderType.equals("L3GN")
						|| l3Order.orderType.equals("L3RN")
						|| l3Order.orderType.equals("L3DS")) {
				

					//批量提交多个PBS订单，一个PBS订单处理一个数据
					while (iterator.hasNext()) {
						RSData rsData = (RSData) iterator.next();
						// 生成PBS订单目录
//						executer.generateWorkDir();

						// 生成参数文件并提交PBS脚本
						PbsOrder pbsorder = executer.submitSingleDataOrder(rsData);

						// test
						// System.out.println(pbsorder.getPbsid());
						pbsorder.setDataName(rsData.filename);
						pbsorder.setDataid(rsData.dataid);
						pbsorder.setJobIdL3(l3Order.jobId);
						pbsorder.setOrderType(l3Order.orderType);
						pbsorder.setProductName(l3Order.productName);
						pbsorder.setDataListPath(rsData.getDataUrlWithoutIP());
//						pbsorder.setJobId(l3Order.jobId);
						// 更新信息到PBS订单数据库
						if (this.orderStudio.addPbsOrder(pbsorder)) {
							pbsOrderLists.add(pbsorder.getPbsid());
						}						
					}
					
					//更新三级订单的PBS订单集合 和 进度
					this.orderStudio.setL3OrderPbsOrderLists(l3Order.jobId,pbsOrderLists);											
					
				}	
				//融合同化订单处理：只在主中心上的执行代理系统上进行
				else if (l3Order.orderType.equals("L3FP")||l3Order.orderType.equals("L3AP")) {
					//只提交一个PBS订单，对应所有数据
					while (iterator.hasNext()) {
						RSData rsData = (RSData) iterator.next();
						
						// 生成参数文件并提交PBS脚本
						PbsOrder pbsorder = executer.submitMultipleDataOrder(rsData);
						
						System.out.println(">>"+rsData.filename);
						System.out.println(">>"+rsData.auxDatas);
						
						pbsorder.setDataid("");
						pbsorder.setJobIdL3(l3Order.jobId);
						pbsorder.setOrderType(l3Order.orderType);
						
						if (this.orderStudio.addPbsOrder(pbsorder)) {
							pbsOrderLists.add(pbsorder.getPbsid());
						}						
						
					}
					
					//更新三级订单的PBS订单集合 和 进度
					this.orderStudio.setL3OrderPbsOrderLists(l3Order.jobId,pbsOrderLists);			
					
				}
				else {
					logger.error(l3Order.jobId+"的订单类型"+l3Order.orderType+"未找到！");
				}
				
				// 更新三级订单至Running状态
				this.orderStudio.setOrderWorkflowStatus(l3Order.jobId,
						"Running");
			}
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
