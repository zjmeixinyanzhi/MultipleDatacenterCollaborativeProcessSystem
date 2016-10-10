/*
 *程序名称 		: SystemMsg.java
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
package TaskExeAgent;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author caoyang
 *
 */
public class SystemMsg {
	//用来存放与其他系统的消息请求列表
	private ArrayList<WebMessage> msgList;
	//用来存放与其他系统的消息请求列表
	//!!!private Hashtable msg2API;
	private Hashtable< String, String > msg2API;

	//系统初始化
	public void initialize(SystemConfig sysConfig){
		System.out.println( "SystemMsg::public void initialize(SystemConfig sysConfig) | 系统初始化" );
		//###//载入消息类型与webservice接口关系列表
		//###//!!!this.msg2API = sysConfig.getMsg2APITable();
		//###this.msg2API = sysConfig.getMsg2APIConfig();
	}
	
	//消息发送
	public boolean send(String msgType, String msgXML){
		System.out.println( "SystemMsg::public boolean send(String msgType, String msgXML) | 消息发送" );
		return true;
		//###// 构建消息
		//###WebMessage newMsg= new WebMessage(msgType,msgXML);
		//###int msgId=this. msgList.add(newMsg);
		//###boolean flag ;
		//###// 获取webservice接口调用方法名称
		//###// PFS（生产可行性提交），OSF（订单状态反馈），PS（产品提交）
		//###String interfaceName= (String)(this.msg2API).get(msgType);
		//###if(interfaceName.equals( "PFS" )) {
		//###flag = IServiceInterface.IProductFeasibilitySubmit(newMsg);
		//###}
		//###else if(interfaceName.equals( "OSF" )) {
		//###flag = IServiceInterface.IOrderSubmitFeadback(newMsg);
		//###}
		//###else if(interfaceName.equals( "PS" )) {
		//###flag = IServiceInterface.IProductSubmit(newMsg);
		//###}
		//###this.msgList.remove(mgsId);
		//###return flag;
	}
	
	public class WebMessage {
		//目标地址
		String target;
		//消息类型：PFS（生产可行性提交），OSF（订单状态反馈），PS（产品提交）
		String msgType;
		//消息内容：XML字符串
		String msgContentXML;
		//消息发送状态：True/False(已发送／未发送)
		boolean status;

		//构造函数
		public WebMessage(String msgType, String msgXML){
			System.out.println( "WebMessage::public WebMessage(String msgType, String msgXML) | 构造函数" );
			//###this.msgType=msgType;
			//###this.msgContentXML=msgXML;
		}
		//setter, getter方法
		public String getMsgType(){
			System.out.println( "WebMessage::public String getMsgType() | " );
			return "";
		}
		public String getMsgContent(){
			System.out.println( "WebMessage::public String getMsgContent() | " );
			return "";
		}
		public String getTarget(){
			System.out.println( "WebMessage::public String getTarget() | " );
			return "";
		}
		public String setMsgType(){
			System.out.println( "WebMessage::public String setMsgType() | " );
			return "";
		}
		public String setMsgContent(){
			System.out.println( "WebMessage::public String setMsgContent() | " );
			return "";
		}
		public String setTarget(){
			System.out.println( "WebMessage::public String setTarget() | " );
			return "";
		}
	}
}
