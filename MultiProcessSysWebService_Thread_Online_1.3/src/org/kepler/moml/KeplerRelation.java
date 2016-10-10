package org.kepler.moml;

/**
 * 创建时间：2014-12-14 下午3:43:15 项目名称：MCA_KeplerWorkflow_Engine 2014-12-14
 * 
 * @author 张杰
 * @version 1.0 文件名称：KeplerRelation.java 类说明：设置模块间的关系
 */
public class KeplerRelation {
	public String BaseName = null;
	public String relationName = null;
	private int count = 0;

	public KeplerRelation(String baseName) {
		// TODO Auto-generated constructor stub
		this.BaseName = baseName;
	}

	// 设置模块间的关系，关系命名规则：BaseName+"relation"+Count
	public String setRelation() {
		count++;
		relationName = this.BaseName + "relation" + count;
		String relationName = "<relation name=\"" + this.relationName
				+ "\" class=\"ptolemy.actor.TypedIORelation\">" + "</relation>";
		return relationName;
	}

	// 获取当前Relation名
	public String getRelationName() {
		return this.relationName;
	}
}
