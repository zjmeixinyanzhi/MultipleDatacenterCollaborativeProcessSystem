/*
 *程序名称 		: PublicBuffer.java
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

import java.util.Date;

/**
 * @author caoyang
 *
 */
public class PublicBuffer {
	//用来存放数据缓存区的ftp服务配置
	private String ftpConfig;
	//用来存放数据缓存区的挂载点
	private String bufferMountPoint;
	//用来存放数据缓存区的最大使用率
	private float maxUsage;
	//用来存放数据缓存区的磁盘清理时间间隔
	private int cleanupDates;
	//用来存放数据缓存区的上次缓存区清理时间
	private Date lastCleanupDate;
	//系统资源状态管理对象，数据缓存区管理
	private SystemResource sysResource;

	//构造函数 数据缓存区管理模块初始化
	public PublicBuffer(){
		System.out.println( "PublicBuffer::public PublicBuffer() | 构造函数 数据缓存区管理模块初始化" );
		//###//从系统配置信息中获取数据缓存区的配置参数
		//###this.ftpConfig        = SystemConfig.getDataProductFtpConfig();
		//###this.bufferMountPoint = SystemConfig.getBufferMountPoint();
		//###//!!!this.maxUsage         = SystemConfig.getMaxUsage();
		//###//!!!this.cleanupDates     = SystemConfig.getCleanupDates();
		//###this.maxUsage         = SystemConfig.getPublicBufferMaxUsage();
		//###this.cleanupDates     = SystemConfig.getPublicBufferCleanupDates();
		//###this.lastCleanupDate  = SystemConfig.getLastCleanupDate();
		//###this.sysResource      = new SystemResource();
	}
	
	//记录系统日志
	public void cleanup(){
		System.out.println( "PublicBuffer::public void cleanup() | 记录系统日志" );
		//###//获取当前时间
		//###long curTime = System.currentTimeMillis();
		//###long lastCleanupTime = this.lastCleanupDate.getTime();
		//###long duration = Math.abs(curTime - lastCleanupTime);
		//###long cleanupDatesInSeconds = this.cleanupDates.getTime();
		//###boolean ifDoCleanup;
		//###//判断当前是否到了清理时间
		//###if((duration- cleanupDatesInSeconds)>0)
		//###	ifDoCleanup = true;
		//###else
		//###	ifDoCleanup = false;
		//###//获取当前系统资源状态，包括数据缓存区状态
		//###this.sysResource.update();
		//###float bufferUsage=this.sysResource.getPulicBufferUsage();
		//###//如果到了订单清理时间，或者空间使用率达到使用率上限，则扫瞄所有订单目录，将超过缓存时间的订单目录进行清理
		//###if(ifDoCleanup|| bufferUsage>this.maxUsage){
		//###	for(:OrderId:){
		//###		//获取订单目录
		//###		String OrderDir=getOrderDir(OrderId);
		//###		//获取订单创建时间，并计算订单缓存时间
		//###		Date createDate=getCreateDate(OrderDir);
		//###		long orderDuration=
		//###		Math.abs(curTime - createDate.getTime());
		//###		//如果到了订单超过了缓存时间，则删除该订单目录的订单数据
		//###		if((orderDuration - cleanupDatesInSeconds)>0){
		//###			remove(OrderDir);
		//###			OrderStudio.updateOrderDirStatus( orderId, "ORDER_DIR_REMOVED" );
		//###		}
		//###	}
		//###}
	}

	//数据产品订单目录上传
	public boolean upload(String localOrderDir,String orderId){
		System.out.println( "PublicBuffer::public boolean upload(String localOrderDir,String orderId) | 数据产品订单目录上传" );
		return true;
		//###//1、连接FTP Server
		//###String addr, port,user, passwd;
		//###FTPClient ftp = new FTPClient();
		//###parse( this.ftpConfig, addr, port, user, passwd );
		//###ftp.connect(addr, port);
		//###ftp.login(user, passwd);
		//###ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		//###int reply = ftp.getReplyCode();
		//###String bufferOrderDir = this.bufferMountPoint + orderID
		//###ftp.changeWorkingDirectory(bufferOrderDir);
		//###//2、打包订单目录
		//###String orderPackage= package(localOrderDir);
		//###//3、上传订单数据包
		//###File pacakgeFile = new File(orderPackage);
		//###FileInputStream input = new FileInputStream(pacakgeFile);
		//###ftp.storeFile(pacakgeFile.getName(), input);
		//###input.close();
		//###//...
		//###//5.ftp退出
		//###ftp.logout();
		//###return flag;
	}

	//下载数据产品订单的中间数据产品
	public boolean download(String localOrderDir,String orderId){
		System.out.println( "PublicBuffer::public boolean download(String localOrderDir,String orderId) | 下载数据产品订单的中间数据产品" );
		return true;
		//###//1、连接FTP Server
		//###String addr, port, user, passwd;
		//###FTPClient ftp = new FTPClient();
		//###parse( this.ftpConfig, addr, port, user, passwd );
		//###ftp.connect(addr, port);
		//###ftp.login(user, passwd);
		//###ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		//###int reply = ftp.getReplyCode();
		//###String bufferOrderDir＝this.bufferMountPoint + orderID;
		//###ftp.changeWorkingDirectory(bufferOrderDir);
		//###//2、下载订单目录数据
		//###File localFile = new File(localOrderDir+"/"+orderId);
		//###OutputStream is = new FileOutputStream(localFile);
		//###ftp.retrieveFile(orderId.getName(), is);
		//###is.close();
		//###//3、解包订单目录数据
		//###unpackage(localOrderDir+"/"+orderId);
		//###remove(localOrderDir+"/"+orderId);
		//###//...
		//###//4.ftp退出
		//###ftp.logout();
		//###return flag;
	}

}
