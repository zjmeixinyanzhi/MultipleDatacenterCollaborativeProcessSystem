package org.kepler.moml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

import SystemManage.SystemLogger;

/**
 * 创建时间：2014-12-14 下午7:17:59 项目名称：MCA_KeplerWorkflow_Engine 2014-12-14
 * 
 * @author 张杰
 * @version 1.0 文件名称：MomlExport.java 类说明：对XML格式的字符串进行合法性检验，并添加DTD等信息
 */
public class MomlExport {
	// xml格式的目标工作流
	public Document doc = null;
	// 导出文件路径
	public String filePath = null;
	// MOML字符串
	public String momlString = null;

	// 日志系统
	private Logger logger = SystemLogger.getSysLogger();

	public MomlExport(String moml) {
		// TODO Auto-generated constructor stub
		this.momlString = moml;
	}

	// 添加DTD
	private boolean addDTD() {
		try {
			// 增加抛出异常，这儿三级订单模块添加不正确，会引起不满足XML格式规范而出错
			doc = DocumentHelper.parseText(momlString);
			doc.addDocType("entity", "-//UC Berkeley//DTD MoML 1//EN",
					"\nhttp://ptolemy.eecs.berkeley.edu/xml/dtd/MoML_1.dtd");
			return true;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error("添加三级订单模块不符合XML格式规范！");
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

	// 导出String字符串的MoML
	public String exportString() {
		// 借助增加DTD完成对XML文件的校验
		if (!addDTD()) {
			logger.error("添加DTD失败！");
			return null;
		}
		return momlString;
	}

	// 导出XML文件
	public boolean exportFile(String filePath) {
		if (!addDTD()) {
			logger.error("添加DTD失败！");
			return false;
		}
		// 导出文件
		try {
			XMLWriter output;
			output = new XMLWriter(new FileWriter(new File(filePath)));
			output.write(doc);
			output.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Kepler工作流文件输出失败！");
			logger.error(e);
			e.printStackTrace();
			return false;
		}
	}

}
