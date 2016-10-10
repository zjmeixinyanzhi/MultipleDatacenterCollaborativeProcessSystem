package RSDataInfo;

import java.util.Iterator;

import org.apache.log4j.Logger;

import DBSystem.RsDataCacheDB;
import LogSystem.SystemLogger;

/**
 * 创建时间：2015-8-13 下午3:37:22 项目名称：RSDataPrepareRunnableJar2.0 2015-8-13
 * 
 * @author 张杰
 * @version 1.0 文件名称：RSDataCacheInfoFeedback.java 类说明：遥感数据缓存信息反馈与更新
 */
public class RSDataCacheInfoFeedback {
	public RSData currentData = null;
	private Logger logger = SystemLogger.getInstance().getSysLogger();

	public RSDataCacheInfoFeedback(RSData data) {
		this.currentData = data;
	}

	public boolean infoFeedback() {
/*
//		logger.info("更新缓存信息！");
		RsDataCacheDB rsCacheDB = new RsDataCacheDB();
		// test
		// System.out.println(">>>>>"+this.currentData.filename);
		

		// 数据不存在时 插入数据
		if (null == this.currentData.requestInfo||this.currentData.requestInfo.equals("")) {
			// 构造请求信息
			RSDataRequestInfo rsDataRequestInfo = new RSDataRequestInfo();
			rsDataRequestInfo.insertRequestInfo(this.currentData.datacenter,
					this.currentData.filepath);
			this.currentData.requestInfo.add(rsDataRequestInfo);

			if (rsCacheDB.addData(this.currentData)) {
				logger.info(this.currentData.filename + "缓存条目插入成功！");
			}
		} else {// 数据存在更新，请求信息
				// 更新该缓存数据的请求信息到缓存库
			logger.info(this.currentData.filename + "缓存条目查询成功！");
			// 遍历缓存数据的请求信息
			Iterator<RSDataRequestInfo> it_currentData = QueryCacheData.requestInfo
					.iterator();
			//请求信息中，和当前数据信息一致的下表
			int index=-1;

			while (it_currentData.hasNext()) {
				RSDataRequestInfo rsDataRequestInfo = (RSDataRequestInfo) it_currentData
						.next();
				// 判断数据中心及路径是否一致
				
				if (rsDataRequestInfo.datacenter
						.equals(this.currentData.datacenter)
						&& rsDataRequestInfo.filepath
								.equals(this.currentData.filepath)) {
					index=QueryCacheData.requestInfo.indexOf(rsDataRequestInfo);
					break;
				}
			}

			// 如果数据存在，并更新缓存库中数据请求记录，并更新原数据信息的文件URL
			// 若一致，更新请求次数和最后请求时间
			if (index!=-1) {
				RSDataRequestInfo newRequestInfo=QueryCacheData.requestInfo.get(index);
				newRequestInfo.updateRequestInfo();
				QueryCacheData.requestInfo.set(index, newRequestInfo);
			}
			else {//若不一致，增加新的请求信息
				RSDataRequestInfo newRequestInfo=new RSDataRequestInfo();
				newRequestInfo.insertRequestInfo(this.currentData.datacenter, this.currentData.filepath);
				QueryCacheData.requestInfo.add(newRequestInfo);					
			}

			// 更新缓存库信息
			String newRequstInfoString="";
			it_currentData=QueryCacheData.requestInfo.iterator();
			while (it_currentData.hasNext()) {
				RSDataRequestInfo rsDataRequestInfo = (RSDataRequestInfo) it_currentData
						.next();
				newRequstInfoString+=rsDataRequestInfo.getDataRequestInfoString();
				newRequstInfoString+=";";
			}
			//test
//			System.out.println(">>>>New Info:"+newRequstInfoString);
			rsCacheDB.updateDataRequestInfo(QueryCacheData.filename, newRequstInfoString);
		}
*/
		return true;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
