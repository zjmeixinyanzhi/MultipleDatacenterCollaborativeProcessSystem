package TaskExeAgent;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import OrderManage.OrderStudio;
import Pbs.PbsOrder;
import TaskSchedular.TaskExecuter;

/**
 * 创建时间：2016年2月25日 下午8:05:57 项目名称：TaskExecutionAgent2.0 2016年2月25日
 * 
 * @author 张杰
 * @version 1.0 文件名称：PBSOrderUpdataThread.java 类说明：依次确认PBS订单的运行状态
 */
public class PBSOrderUpdataThread extends Thread {

	private OrderStudio orderStudio = new OrderStudio();
	private TaskExecuter executer = new TaskExecuter();
	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public void run() {

		for (;;) {

			// 依次轮询PBS订单，然后获取未确认状态的PBS订单（Submited和Running状态）

			Iterator<PbsOrder> iterator = this.orderStudio
					.getUnconfirmPbsOrder().iterator();
			while (iterator.hasNext()) {
				PbsOrder pbsOrder = (PbsOrder) iterator.next();

				String pbsId = pbsOrder.getPbsid();
				
				if (pbsId==null) {
					continue;
				}
				
				
				String resultLogFile = pbsOrder.getResultLogFile();

				// qstat查询状态
				String qstatFeedback = executer.isTerminate(pbsId);

//				System.out.println(">>"+qstatFeedback);

				// 运行状态
				if (qstatFeedback.equals("Q")) {
					if (this.orderStudio.setPbsOrderWorkflowStatus(pbsId,
							"Submited")) {
						logger.info("更新" + pbsId + "订单状态：Submited");
					}					
				}
				else if (qstatFeedback.equals("R")) {
					if (this.orderStudio.setPbsOrderWorkflowStatus(pbsId,
							"Running")) {
						logger.info("更新" + pbsId + "订单状态：Running");
					}
				}

				// 停止状态
				else if (qstatFeedback.equals("C")||qstatFeedback.equals("E")) {
					boolean isSuccess = true;
					// 判断是否有Result
					//test
//					System.out.println(">>"+pbsOrder.getResultLogFile());
					String logResult = executer.getAlgorithmResult(pbsOrder
							.getResultLogFile(),"Result");
					
					if (logResult==null) {
						// 更新操作
						if (this.orderStudio.setPbsOrderWorkflowStatus(pbsId,
								"Error")) {
							logger.info("更新" + pbsId + "订单状态：Error");

						}						
						continue;
					}
					
					// 如果Success
					if (logResult.equals("Success")) {
						// 更新操作
						//获取相应的产品信息
						ArrayList<String> productsList=executer.getProductsInfo(pbsOrder.getProductDir()+"/ProductLists.xml");
						//获取运行结束时间
						String strFinishDate = executer.getAlgorithmResult(pbsOrder
								.getResultLogFile(),"EndTime");
						
						DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//						format.setLenient(false);
						
						Date finishDate=new Date(System.currentTimeMillis());
//						//放弃使用，因为算法生成时间与Java时间不同
//						try {
//							//finishDate = new Date(format.parse(strFinishDate).getTime());
//							//test
//							//System.out.println(finishDate);
//						} catch (ParseException e) {
//							// TODO Auto-generated catch block
//							logger.error(pbsId+"获取结束时间出错！");
//							e.printStackTrace();
//							continue;
//						}
						if (null!=productsList){
							//更新产品和结束时间
							if (this.orderStudio.setPbsOrderFinishInfos(pbsId, finishDate, productsList)) {
								logger.info("更新" + pbsId + "订单结束时间和产品列表成功！");								
							}
							else {
								logger.info("更新" + pbsId + "订单结束时间和产品列表失败！");	
								continue;
							}																				
						}
						
						else {
							logger.error(pbsId+"订单产品列表为空！");
							if (this.orderStudio.setPbsOrderWorkflowStatus(pbsId,
									"Error")) {
								logger.info("更新" + pbsId + "订单状态：Error");
							}
							continue;
						}
						
						if (this.orderStudio.setPbsOrderWorkflowStatus(pbsId,
								"Finish")) {
							logger.info("更新" + pbsId + "订单状态：Finish");
						}
						
					}

					// 如果Fail
					else if (logResult.equals("Fail")) {

						// 更新操作
						if (this.orderStudio.setPbsOrderWorkflowStatus(pbsId,
								"Error")) {
							logger.info("更新" + pbsId + "订单状态：Error");

						}
						// 结果未知
						else {
							// 更新操作
							if (this.orderStudio.setPbsOrderWorkflowStatus(
									pbsId, "Unknown")) {
								logger.info("更新" + pbsId + "订单状态：Unknown");
							}
						}
					}
				}
				// 无任何返回值
				else {
					logger.error(pbsId + "状态未知！");
					// test
					// System.out.println();
					if (this.orderStudio.setPbsOrderWorkflowStatus(pbsId,
							"Unknown")) {
						logger.info("更新" + pbsId + "订单状态：Unknown");
					}
				}

				// 获取ResultLog中的状态及时间

			}

			// 需要处理完成后更新（几何归一化）
			// pbsOrder.setGeoCoverageStr(l3Order.geoCoverageStr);

			// 更新订单结束时间
			// PBSOrder.setFinish

			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}
