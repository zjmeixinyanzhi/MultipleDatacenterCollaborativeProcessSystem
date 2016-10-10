package DataService;

import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.DocumentException;

import DBManage.RsDataCacheDB;
import DBManage.TestDBConnection;
import Download.HttpDownload;
import FileOperation.FileOperation;
import FileOperation.TimeConsumeCount;
import RSDataManage.RSData;

/**
 * 创建时间：2016年5月18日 下午11:26:05
 * 项目名称：MultiProcessSysWebService_Thread_Online_1.1
 * 2016年5月18日
 * @author 张杰
 * @version 1.0
 * 文件名称：DataAcquireConcurrentTest.java
 * 类说明：数据获取并发测试，并发量50个
 */
public class DataAcquireConcurrentTest extends Thread {
	public String url;
	public String destFile;
	public String dataFileName;
	public String recordFile;
	
	
	public void run(){
		//并发请求测试
//		DataAcquire dataAcquire=new DataAcquire();
//		try {
//			dataAcquire.main(null);
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("出现错误！");
//		}
		
		
		//并发下载测试
		
		
		FileOperation fileOperation=new FileOperation();
		TimeConsumeCount timeConsumeCount2=new TimeConsumeCount();
		
		
		timeConsumeCount2.setStartTimeByCurrentTime();
		
		if (url.startsWith("http")) {
			HttpDownload download = new HttpDownload();
			// 判断下下载格式:如果是zip格式，需要解压
			if (url.toUpperCase().endsWith("ZIP")&&(!destFile.endsWith(".zip"))) {
				destFile += ".zip";
			}
			
			if (!download.httpDownload(url, destFile)) {
				System.out.println(dataFileName+ "数据下载失败！"+url);

			}
			else {
				System.out.println(dataFileName+ "数据下载成功！"+url);
			}				
		}
		
		timeConsumeCount2.setEndTimeByCurrentTime();
		fileOperation.appendMethodB(recordFile,"RawDataAcqurieTimeConsume="+timeConsumeCount2.getTimeSpan()+"\n");
		
	}
	
	public void printInfo() {
		System.out.println(this.dataFileName+" "+this.destFile+" "+this.url);
		
	}
	
	public static void main(String[] args) {
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();

		
		//测试数据并发获取
//		int count=10;
//		for (int i = 0; i < count; i++) {
//			DataAcquireConcurrentTest concurrentTestThread=new DataAcquireConcurrentTest();
//			concurrentTestThread.start();
//		}	
		
		//测试数据获取
		//数据下载并发测试
		RsDataCacheDB rsDataCacheDB=new RsDataCacheDB();
		ArrayList<RSData> rsDatas=rsDataCacheDB.search(" where dataid not like '%QP%' and datastatus='Available'");
		Iterator<RSData> it=rsDatas.iterator();
		
		
		String destPath="/dataIO/863_Project/863-Daemon/Project1DataService/cocurrentDownload/";
		String file="/home.bak/MCA/zjDir/DataAcquireServiceTest/DownloadRecord.csv";
		
		int count=3;
		
		while (it.hasNext()) {
			RSData rsData = (RSData) it.next();		
			String dataFileName=rsData.filename;			
			String url=rsData.downloadUrl;
			
			if (url==null) {
				continue;
			}
			
			String destFile=destPath+rsData.filename;	
			if (url==null || url.equals("")) {
				System.out.println(dataFileName+" download url is illeagel!");
				continue;
			}
			
			for (int i = 0; i < count; i++) {
				DataAcquireConcurrentTest concurrentTestThread=new DataAcquireConcurrentTest();
				concurrentTestThread.url=rsData.downloadUrl;
				concurrentTestThread.dataFileName=rsData.filename;
				concurrentTestThread.destFile=destPath+i+"_"+rsData.filename;
				concurrentTestThread.recordFile=file;
				
				concurrentTestThread.printInfo();
				
				concurrentTestThread.start();
			}
			
		}
		
	}
}
