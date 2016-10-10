package org.kepler.moml;

/**
 * 创建时间：2014-12-14 下午3:37:51 项目名称：MCA_KeplerWorkflow_Engine 2014-12-14
 * 
 * @author 张杰
 * @version 1.0 文件名称：KeplerLink.java 
 * 类说明：设置Kepler Actor之间的连接
 */
public class KeplerLink {

	public KeplerLink() {
		// TODO Auto-generated constructor stub
	}

	// 根据模块的前后端口设置连接
	public String setLink(String PreMoudlePortName, String NextMoudlePortName,
			String RelationName) {
		String link = "<link port=\"" + PreMoudlePortName + "\" relation=\""
				+ RelationName + "\"/>" + "<link port=\"" + NextMoudlePortName
				+ "\" relation=\"" + RelationName + "\"/>";
		return link;
	}

	// 设置特殊三个实体间连接
	public String setTriLink(String PreMoudlePortName,
			String NextMoudlePortName, String LastMoudlePortName,
			String RelationName) {
		String trilink = "<link port=\"" + PreMoudlePortName + "\" relation=\""
				+ RelationName + "\"/>" + "<link port=\"" + NextMoudlePortName
				+ "\" relation=\"" + RelationName + "\"/>" + "<link port=\""
				+ LastMoudlePortName + "\" relation=\"" + RelationName + "\"/>";
		return trilink;
	}

}
