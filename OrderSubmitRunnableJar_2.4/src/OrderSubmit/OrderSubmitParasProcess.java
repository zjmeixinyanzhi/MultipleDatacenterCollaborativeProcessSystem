package OrderSubmit;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import DBSystem.L2OrderDB;
import DBSystem.L3OrderDB;
import DBSystem.L4OrderDB;
import LogSystem.SystemLogger;
import OrderManage.L2ExternalOrder;
import OrderManage.L3InternalOrder;
import OrderManage.L4InternalOrder;
import OrderManage.Order;

/**
 * 创建时间：2015-8-16 下午4:45:07 项目名称：OrderSubmitRunnableJar 2015-8-16
 * 
 * @author 张杰
 * @version 1.0 文件名称：OrderParasProcess.java 类说明：确定订单提交所需的参数
 */
public class OrderSubmitParasProcess {

	// 提交WebService Url
	public String submitURL = null;
	// 调用的处理方法
	public String method = null;
	// XML参数
	public String strXML = null;
	// 成功提交的返回标志
	public String successResult = "Success";
	// 提交失败的返回标志
	public String errorResult = "Error";
	
	// log4j日志系统
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	// 处理成功标志

	public boolean doProcess() {
		return false;
	}

	public String getSubmitURL() {
		return submitURL;
	}

	public String getMethod() {
		return method;
	}

	public String getStrXML() {
		return strXML;
	}

	public String getSuccessResult() {
		return successResult;
	}

	public void setSubmitURL(String submitURL) {
		this.submitURL = submitURL;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setStrXML(String strXML) {
		this.strXML = strXML;
	}

	public void setSuccessResult(String successResult) {
		this.successResult = successResult;
	}

	public String getErrorResult() {
		return errorResult;
	}

	public void setErrorResult(String errorResult) {
		this.errorResult = errorResult;
	}
	
	//获取当前三级订单的上个三级订单ID
	public L3InternalOrder getPreL3Order(L3InternalOrder curL3order ){
		
		L2OrderDB l2OrderDB = new L2OrderDB();
		L3OrderDB l3OrderDB = new L3OrderDB();
		
		if (curL3order==null) {
			return null;
		}
		
		// 上一个三级订单号
		String prel3OrderId = "";		
		L3InternalOrder preL3Order = new L3InternalOrder();
		
		// 查询上级订单号
		L2ExternalOrder l2ExternalOrder = new L2ExternalOrder();
		try {
			l2ExternalOrder = l2OrderDB.search(
					" where JobId='" + curL3order.jobId_L2 + "'").get(0);
			// 获取下个
			String[] l3Orders = l2ExternalOrder.l3orderlist.split(";");
			for (int i = 0; i < l3Orders.length; i++) {
				if (l3Orders[i].equals(curL3order.jobId)
						&& l3Orders[i - 1] != null) {
					prel3OrderId = l3Orders[i - 1];
					break;
				}
			}
			// 查询
			preL3Order = l3OrderDB
					.search(" where JobId='" + prel3OrderId + "'").get(0);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("查询" + curL3order.jobId + "上级订单出错！");
			return null;
		}
		
		return preL3Order;
	}
	
	//获取当前四级订单的上个四级订单ID
	public L4InternalOrder getPreL4Order(L4InternalOrder curl4Order){
		
		L3OrderDB l3OrderDB = new L3OrderDB();
		L4OrderDB l4OrderDB=new L4OrderDB();
		L3InternalOrder curL3order = new L3InternalOrder();
		L3InternalOrder preL3Order = new L3InternalOrder();
		
		ArrayList<L3InternalOrder> l3InternalOrders = l3OrderDB
				.search(" where JobId='" + curl4Order.jobId_L3 + "'");
		try {
			curL3order = l3InternalOrders.get(0);
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			logger.error("未找到当前四级订单的三级订单" + curl4Order.jobId_L3);
			return null;
		}
		//上个三级订单
		preL3Order=getPreL3Order(curL3order);
		
		if (preL3Order==null) {
			return null;
		}
		L4InternalOrder l4InternalOrder=new L4InternalOrder();
		//上个四级订单
		try {
			String preL4OrderId=preL3Order.jobId+"@"+curl4Order.jobId.split("@")[1];
			l4InternalOrder=l4OrderDB.search(" where JobId='" + preL4OrderId + "'").get(0);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("未找到当前四级订单的下个四级订单" + curl4Order.jobId_L3);
			return null;
		}
		
		return l4InternalOrder;
	}
	
	public static void main(String[] args) {
		
		
		
	}
	
	
}
