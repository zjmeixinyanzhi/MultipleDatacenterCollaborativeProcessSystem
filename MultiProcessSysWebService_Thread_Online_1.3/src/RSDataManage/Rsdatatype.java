package RSDataManage;
/**
 * 创建时间：2016-4-28 下午8:46:59
 * 项目名称：MultiProcessSysWebService_Thread_Online
 * 2016-4-28
 * @author 张杰
 * @version 1.0
 * 文件名称：Rsdatatype.java
 * 类说明：
 */
/**
 * Rsdatatype entity. @author MyEclipse Persistence Tools
 */

public class Rsdatatype implements java.io.Serializable {

	// Fields

	private Integer id;
	private String satellite;
	private String sensor;
	private String spname;
	private String preprocessing;
	private String datacenter;

	// Constructors

	/** default constructor */
	public Rsdatatype() {
	}

	/** full constructor */
	public Rsdatatype(String satellite, String sensor, String spname,
			String preprocessing, String datacenter) {
		this.satellite = satellite;
		this.sensor = sensor;
		this.spname = spname;
		this.preprocessing = preprocessing;
		this.datacenter = datacenter;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSatellite() {
		return this.satellite;
	}

	public void setSatellite(String satellite) {
		this.satellite = satellite;
	}

	public String getSensor() {
		return this.sensor;
	}

	public void setSensor(String sensor) {
		this.sensor = sensor;
	}

	public String getSpname() {
		return this.spname;
	}

	public void setSpname(String spname) {
		this.spname = spname;
	}

	public String getPreprocessing() {
		return this.preprocessing;
	}

	public void setPreprocessing(String preprocessing) {
		this.preprocessing = preprocessing;
	}

	public String getDatacenter() {
		return this.datacenter;
	}

	public void setDatacenter(String datacenter) {
		this.datacenter = datacenter;
	}

}