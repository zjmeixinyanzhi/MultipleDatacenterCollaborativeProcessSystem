package RSDataInfo;

import java.util.HashMap;

import org.apache.log4j.Logger;

import DBSystem.RsDataCacheDB;
import LogSystem.SystemLogger;
import sun.util.logging.resources.logging;

/**
 * 创建时间：2015-8-13 下午3:06:33 项目名称：RSDataPrepareRunnableJar2.0 2015-8-13
 * 
 * @author 张杰
 * @version 1.0 文件名称：RSDataInspection.java 类说明：数据存在性检查
 */
public class RSDataInspection {
	public RSData currentRsData = null;
	// 数据存在性
	public boolean isExist = false;

	private Logger logger = SystemLogger.getInstance().getSysLogger();

	// 构造函数，参数为数据数据请求信息
	public RSDataInspection(String dataId) {
		// 初始化ESData变量
		RsDataCacheDB rsDataCacheDB = new RsDataCacheDB();
		this.currentRsData = rsDataCacheDB.search(
				" where dataid='" + dataId + "'").get(0);
	}

	// 检查其存在性
	public boolean checkDataExistence() {
		logger.info(this.currentRsData.filepath);
		// 需要利用Gfarm、GRAM或者SSH判断

		this.isExist = true;

		return this.isExist;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
