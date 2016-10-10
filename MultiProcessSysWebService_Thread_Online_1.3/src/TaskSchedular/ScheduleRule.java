/*
 *程序名称 		: ScheduleRule.java
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
package TaskSchedular;

/**
 * @author caoyang
 *
 */
public class ScheduleRule {
	//规则ID
	public String ruleId;
	//规则条件
	public String condition;
	//约束的变量
	public String argName;
	//变量比较值
	public String value;
	
	public ScheduleRule() {
		this.ruleId    = "";
		this.condition = "";
		this.argName   = "";
		this.value     = "";
	}
}
