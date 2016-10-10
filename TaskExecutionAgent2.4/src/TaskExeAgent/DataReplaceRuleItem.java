/*
 *程序名称 		: DataReplaceRuleItem.java
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

import DBManage.DataReplaceRuleDB;

/**
 * @author caoyang
 *
 */
public class DataReplaceRuleItem {
	private static DataReplaceRuleDB dataReplaceRuleDB = new DataReplaceRuleDB();
	private static byte[]            lockReadWrite     = new byte[ 0 ];
	private        int               id;
	
	public DataReplaceRuleItem(){
		this.id = -1;
	}
}
