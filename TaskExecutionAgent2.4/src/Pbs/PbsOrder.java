package Pbs;

import java.util.Date;
/**
 * 创建时间：2016-2-25 下午8:53:06
 * 项目名称：TaskExecutionAgent2.0
 * 2016-2-25
 * @author 张杰
 * @version 1.0
 * 文件名称：Pbsorderdb.java
 * 类说明：
 */
public class PbsOrder implements java.io.Serializable {

	// Fields
	private String jobId;
	private String pbsid;
	private String dataid;
	private String jobIdL3;
	private String orderType;
	private Integer priority;
	private String productName;
	private Date startDate;
	private Date endDate;
	private String workingStatus;
	private String dataListPath;
	private Date submitDate;
	private Date finishDate;
	private String orderParmeterFile;
	private String resultLogFile;
	private String operatorid;
	private String algorithmName;
	private String algorithmPath;
	private String dataProductList;
	private String productDir;
	private String pbsFile;
	private String dataName;

	// Constructors

	/** default constructor */
	public PbsOrder() {
	}

	/** minimal constructor */
	public PbsOrder(String jobId) {
		this.jobId = jobId;
	}

	/** full constructor */
	public PbsOrder(String jobId, String pbsid, String jobIdL3,
			String orderType, Integer priority, String productName,
			String geoCoverageStr, Date startDate, Date endDate,
			String workingStatus, String dataListPath, Date submitDate,
			Date finishDate, String orderParmeterFile, String resultLogFile,
			String operatorid, String algorithmName, String algorithmPath,
			String dataProductList, String productDir,String pbsFile,String dataid,String dataName) {
		this.jobId = jobId;
		this.pbsid = pbsid;
		this.jobIdL3 = jobIdL3;
		this.orderType = orderType;
		this.priority = priority;
		this.productName = productName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.workingStatus = workingStatus;
		this.dataListPath = dataListPath;
		this.submitDate = submitDate;
		this.finishDate = finishDate;
		this.orderParmeterFile = orderParmeterFile;
		this.resultLogFile = resultLogFile;
		this.operatorid = operatorid;
		this.algorithmName = algorithmName;
		this.algorithmPath = algorithmPath;
		this.dataProductList = dataProductList;
		this.productDir = productDir;
		this.pbsFile=pbsFile;
		this.dataid=dataid;
		this.dataName=dataName;
	}

	// Property accessors

	
	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}
	
	public String getJobId() {
		return this.jobId;
	}

	public String getPbsFile() {
		return pbsFile;
	}

	public void setPbsFile(String pbsFile) {
		this.pbsFile = pbsFile;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getPbsid() {
		return this.pbsid;
	}

	public String getDataName() {
		return dataName;
	}

	public void setDataName(String dataName) {
		this.dataName = dataName;
	}

	public void setPbsid(String pbsid) {
		this.pbsid = pbsid;
	}

	public String getJobIdL3() {
		return this.jobIdL3;
	}

	public void setJobIdL3(String jobIdL3) {
		this.jobIdL3 = jobIdL3;
	}

	public String getOrderType() {
		return this.orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getProductName() {
		return this.productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getWorkingStatus() {
		return this.workingStatus;
	}

	public void setWorkingStatus(String workingStatus) {
		this.workingStatus = workingStatus;
	}

	public String getDataListPath() {
		return this.dataListPath;
	}

	public void setDataListPath(String dataListPath) {
		this.dataListPath = dataListPath;
	}

	public Date getSubmitDate() {
		return this.submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public Date getFinishDate() {
		return this.finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getOrderParmeterFile() {
		return this.orderParmeterFile;
	}

	public void setOrderParmeterFile(String orderParmeterFile) {
		this.orderParmeterFile = orderParmeterFile;
	}

	public String getResultLogFile() {
		return this.resultLogFile;
	}

	public void setResultLogFile(String resultLogFile) {
		this.resultLogFile = resultLogFile;
	}

	public String getOperatorid() {
		return this.operatorid;
	}

	public void setOperatorid(String operatorid) {
		this.operatorid = operatorid;
	}

	public String getAlgorithmName() {
		return this.algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public String getAlgorithmPath() {
		return this.algorithmPath;
	}

	public void setAlgorithmPath(String algorithmPath) {
		this.algorithmPath = algorithmPath;
	}

	public String getDataProductList() {
		return this.dataProductList;
	}

	public void setDataProductList(String dataProductList) {
		this.dataProductList = dataProductList;
	}

	public String getProductDir() {
		return this.productDir;
	}

	public void setProductDir(String productDir) {
		this.productDir = productDir;
	}

}