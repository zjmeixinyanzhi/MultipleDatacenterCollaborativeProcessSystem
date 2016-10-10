package TaskExeAgent;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import DBManage.L3OrderDB;
import OrderManage.L3InternalOrder;
import OrderManage.L3OrderPbsProgressCount;
import OrderManage.OrderStudio;
import Pbs.PbsOrder;
import TaskSchedular.ReSubmit;
import TaskSchedular.TaskExecuter;
import TaskSchedular.TaskStatusUpdate;

/**
 * 创建时间：2016年3月1日 下午3:44:20 项目名称：TaskExecutionAgent2.0 2016年3月1日
 * 
 * @author 张杰
 * @version 1.0 文件名称：L3OrderStatusFeedback.java
 *          类说明：三级订单反馈，轮询三级订单库状态，查询Running状态的订单并查看PBS订单处理进度
 *          ，如果PBS订单全部完成，需向主中心更新相应的状态及产品列表
 */
public class L3OrderStatusFeedbackThread extends Thread {
	private OrderStudio orderStudio = new OrderStudio();
	private TaskExecuter executer = new TaskExecuter();
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public void run() {

		for (;;) {
			// 查询Running状态数据
			ArrayList<L3InternalOrder> l3OrderList = this.orderStudio
					.getOrderList("Running");

			Iterator<L3InternalOrder> it_L3Order = l3OrderList.iterator();
			while (it_L3Order.hasNext()) {
				L3InternalOrder l3InternalOrder = (L3InternalOrder) it_L3Order
						.next();

				// 如果是标准产品生产处理流程订单
				if (l3InternalOrder.orderType.equals("L3GN")
						|| l3InternalOrder.orderType.equals("L3RN")
						|| l3InternalOrder.orderType.equals("L3DS")) {
					// 查询所属的PBS订单是否全部确认状态
					L3OrderPbsProgressCount pbsOrdersProgressCount = this.orderStudio
							.getUnconfirmPbsOrder(l3InternalOrder);
					// test
					// System.out.println(">>>>"+pbsOrdersProgressCount.getProgressCount());

					// 更新L3Order订单状态
					this.orderStudio
							.setOrderStatus("SET ProcessedPercentage = '"
									+ pbsOrdersProgressCount.getProgressCount()
									+ "' WHERE JobId = '"
									+ l3InternalOrder.jobId + "'");

					// 状态全部确认
					if (pbsOrdersProgressCount.getStautsUnConfirmCount() == 0) {

						// 此处分为多种情况：1 全部成功直接反馈或者提交次达到最大提交次数，2
						// 存在失败或者未知情况并且重新提交次数为小于最大提交次数，需要重新提交，
						int resubmitCount = l3InternalOrder.getReSubmitTimes();
						int maxResubmitCount = 3;
						// 全部处理成功 或者 已经达到最大提交次数
						if (pbsOrdersProgressCount.getSumCount() == pbsOrdersProgressCount
								.getFinishCount()
								|| resubmitCount >= maxResubmitCount) {
							// 更新状态

							// 更新三级订单状态,
							l3InternalOrder.workingStatus = "Finish";

							// 如过完成处理，向主中心更新产品与状态
							TaskStatusUpdate taskStatusUpdate = new TaskStatusUpdate(
									l3InternalOrder);

							boolean feedback_flag = true;

							// 更新产品列表及状态
							if (!taskStatusUpdate.feedbackProductlists()) {
								feedback_flag &= false;
								logger.info(l3InternalOrder.jobId
										+ "向主中心更新产品数据失败！");
								feedback_flag &= taskStatusUpdate
										.feedbackStatus("Error");
								continue;
							} else {
								feedback_flag &= taskStatusUpdate
										.feedbackStatus("Finish");
								if (!feedback_flag) {
									logger.info(l3InternalOrder.jobId
											+ "向主中心更新状态失败！");
								}
							}

							// 确认本地三级订单库中状态
							logger.info(l3InternalOrder.jobId + "订单所有数据已经处理完成！");
							if (feedback_flag) {
								this.orderStudio.setOrderWorkflowStatus(
										l3InternalOrder.jobId, "Finish");
							} else {
								this.orderStudio
										.setOrderWorkflowStatus(
												l3InternalOrder.jobId,
												"Feedback Error");
							}
						}
						// 存在错误或者Unknown的订单并且重新提交次数小于，需要重新提交
						else if (pbsOrdersProgressCount.getUnFinishOrderCount() != 0
								&& resubmitCount < maxResubmitCount) {
							// 订单状态改成 ReSubmitting
							this.logger.info(l3InternalOrder.jobId+"存在未确认PBS订单，更新重新提交状态！");
							this.orderStudio.setOrderWorkflowStatus(
									l3InternalOrder.jobId, "ReSubmitting");
							ReSubmit reSubmit = new ReSubmit();
							if (reSubmit.doReSubmit(l3InternalOrder,
									pbsOrdersProgressCount
											.getUnFinishOrderLists())) {
								
								

								logger.info(l3InternalOrder.jobId
										+ "错误PBS订单重新提交成功！");
							}
							// 重新提交失败
							else {
								logger.error(l3InternalOrder.jobId
										+ "错误PBS订单重新提交失败！");
							}

							// 修改状态为运行状态
							this.orderStudio.setOrderWorkflowStatus(
									l3InternalOrder.jobId, "Running");
							// 重新提交次数+1
							L3OrderDB l3OrderDB = new L3OrderDB();
							l3OrderDB.setItemValue("set ReSubmitTimes="
									+ (++resubmitCount) + " where JobId='"
									+ l3InternalOrder.jobId + "'");
						}
					}
				}
				
							
				//融合同化订单处理：只在主中心上的执行代理系统上进行
				else if (l3InternalOrder.orderType.equals("L3FP")||l3InternalOrder.orderType.equals("L3AP")) {
					
					// 查询所属的PBS订单是否全部确认状态
					L3OrderPbsProgressCount pbsOrdersProgressCount = this.orderStudio
							.getUnconfirmPbsOrder(l3InternalOrder);
					
					// 更新L3Order订单状态
					this.orderStudio
							.setOrderStatus("SET ProcessedPercentage = '"
									+ pbsOrdersProgressCount.getProgressCount()
									+ "' WHERE JobId = '"
									+ l3InternalOrder.jobId + "'");

					// 状态全部确认
					if (pbsOrdersProgressCount.getStautsUnConfirmCount() == 0) {

						// 此处分为多种情况：1 全部成功直接反馈或者提交次达到最大提交次数，2
						// 存在失败或者未知情况并且重新提交次数为小于最大提交次数，需要重新提交，
						int resubmitCount = l3InternalOrder.getReSubmitTimes();
						int maxResubmitCount = 3;
						// 全部处理成功 或者 已经达到最大提交次数
						if (pbsOrdersProgressCount.getSumCount() == pbsOrdersProgressCount
								.getFinishCount()
								|| resubmitCount >= maxResubmitCount) {
							// 更新状态

							// 更新三级订单状态,
							l3InternalOrder.workingStatus = "Finish";

							// 如过完成处理，向主中心更新产品与状态
							TaskStatusUpdate taskStatusUpdate = new TaskStatusUpdate(
									l3InternalOrder);

							boolean feedback_flag = true;

							// 更新产品列表及状态
							if (!taskStatusUpdate.feedbackProductlists()) {
								feedback_flag &= false;
								logger.info(l3InternalOrder.jobId
										+ "向主中心更新产品数据失败！");
								feedback_flag &= taskStatusUpdate
										.feedbackStatus("Error");
								continue;
							} else {
								feedback_flag &= taskStatusUpdate
										.feedbackStatus("Finish");
								if (!feedback_flag) {
									logger.info(l3InternalOrder.jobId
											+ "向主中心更新状态失败！");
								}
							}

							// 确认本地三级订单库中状态
							logger.info(l3InternalOrder.jobId + "订单所有数据已经处理完成！");
							if (feedback_flag) {
								this.orderStudio.setOrderWorkflowStatus(
										l3InternalOrder.jobId, "Finish");
							} else {
								this.orderStudio
										.setOrderWorkflowStatus(
												l3InternalOrder.jobId,
												"Feedback Error");
							}
						}
						// 存在错误或者Unknown的订单并且重新提交次数小于，需要重新提交
						else if (pbsOrdersProgressCount.getUnFinishOrderCount() != 0
								&& resubmitCount < maxResubmitCount) {
							// 订单状态改成 ReSubmitting
							this.logger.info(l3InternalOrder.jobId+"存在未确认PBS订单，更新重新提交状态！");
							this.orderStudio.setOrderWorkflowStatus(
									l3InternalOrder.jobId, "ReSubmitting");
							ReSubmit reSubmit = new ReSubmit();
							if (reSubmit.doReSubmit(l3InternalOrder,
									pbsOrdersProgressCount
											.getUnFinishOrderLists())) {
								
								

								logger.info(l3InternalOrder.jobId
										+ "错误PBS订单重新提交成功！");
							}
							// 重新提交失败
							else {
								logger.error(l3InternalOrder.jobId
										+ "错误PBS订单重新提交失败！");
							}

							// 修改状态为运行状态
							this.orderStudio.setOrderWorkflowStatus(
									l3InternalOrder.jobId, "Running");
							// 重新提交次数+1
							L3OrderDB l3OrderDB = new L3OrderDB();
							l3OrderDB.setItemValue("set ReSubmitTimes="
									+ (++resubmitCount) + " where JobId='"
									+ l3InternalOrder.jobId + "'");
						}
					}
					
				}
				else {
					logger.error(l3InternalOrder.jobId+"的订单类型"+l3InternalOrder.orderType+"未找到！");
				}

			}

			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}
