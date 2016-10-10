package RSDataManage;

public class DataEntity 
{

	/** 
	 数据实体编码	 
	*/
	public Integer Id;

	

	/** 
	 数据实体文件名称	 
	*/
	public String Name;
	

	/** 
	 数据内容日期	 
	*/
	public java.util.Date Date;
		

	/** 
	 文件所在磁盘分区编号 
	*/
//	public int DiskId;
	

	/** 
	 本系统文件目录结构前缀	 
	*/
	public String InnerPrefix;
	

	/** 
	 删除标记	 
	*/
//	public int DeleteFlag;
	

	/** 
	 标准产品删除订单号	 
	*/
//	public long DeleteOrderId;

	

	/** 
	 备份标记 
	*/
//	public int BackupFlag;
	

	/** 
	 标准产品备份订单号	 
	*/
//	public long BackupOrderId;
	
	
	/** 
	 入库(或生产完成)时间	 
	*/
	public java.util.Date ImportDate = new java.util.Date(0);

	

	/** 
	 入库标记 
	*/
	public int ImportFlag;
	

	/** 
	 标准产品入库订单号	 
	*/
	public long ImportOrderId;

	

	/** 
	 区域信息	 
	*/
	public String Area;


		
	/**
	 * 无参构造函数
	 * */
	public DataEntity(){}


	/**
	 * 有参构造函数
	 * */
//	public DataEntity(Integer id, String name, java.util.Date date, int diskId,
//			String innerPrefix, int deleteFlag, long deleteOrderId,
//			int backupFlag, long backupOrderId, java.util.Date importDate,
//			int importFlag, long importOrderId, String area) {
	public DataEntity(Integer id, String name, java.util.Date date, 
	String innerPrefix,java.util.Date importDate,
	int importFlag, long importOrderId, String area) {
		super();
		Id = id;
		Name = name;
		Date = date;
//		DiskId = diskId;
		InnerPrefix = innerPrefix;
//		DeleteFlag = deleteFlag;
//		DeleteOrderId = deleteOrderId;
//		BackupFlag = backupFlag;
//		BackupOrderId = backupOrderId;
		ImportDate = importDate;
		ImportFlag = importFlag;
		ImportOrderId = importOrderId;
		Area = area;
	}



	/**
	 * @return id
	 */
	public Integer getId() {
		return Id;
	}




	/**
	 * @param id 要设置的 id
	 */
	public void setId(Integer id) {
		Id = id;
	}




	/**
	 * @return name
	 */
	public String getName() {
		return Name;
	}




	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		Name = name;
	}




	/**
	 * @return date
	 */
	public java.util.Date getDate() {
		return Date;
	}




	/**
	 * @param date 要设置的 date
	 */
	public void setDate(java.util.Date date) {
		Date = date;
	}




	/**
	 * @return diskId
	 */
//	public int getDiskId() {
//		return DiskId;
//	}




	/**
	 * @param diskId 要设置的 diskId
	 */
//	public void setDiskId(int diskId) {
//		DiskId = diskId;
//	}




	/**
	 * @return innerPrefix
	 */
	public String getInnerPrefix() {
		return InnerPrefix;
	}




	/**
	 * @param innerPrefix 要设置的 innerPrefix
	 */
	public void setInnerPrefix(String innerPrefix) {
		InnerPrefix = innerPrefix;
	}




	/**
	 * @return deleteFlag
	 */
//	public int getDeleteFlag() {
//		return DeleteFlag;
//	}




	/**
	 * @param deleteFlag 要设置的 deleteFlag
	 */
//	public void setDeleteFlag(int deleteFlag) {
//		DeleteFlag = deleteFlag;
//	}




	/**
	 * @return deleteOrderId
	 */
//	public long getDeleteOrderId() {
//		return DeleteOrderId;
//	}




	/**
	 * @param deleteOrderId 要设置的 deleteOrderId
	 */
//	public void setDeleteOrderId(long deleteOrderId) {
//		DeleteOrderId = deleteOrderId;
//	}




	/**
	 * @return backupFlag
	 */
//	public int getBackupFlag() {
//		return BackupFlag;
//	}




	/**
	 * @param backupFlag 要设置的 backupFlag
	 */
//	public void setBackupFlag(int backupFlag) {
//		BackupFlag = backupFlag;
//	}




	/**
	 * @return backupOrderId
	 */
//	public long getBackupOrderId() {
//		return BackupOrderId;
//	}




	/**
	 * @param backupOrderId 要设置的 backupOrderId
	 */
//	public void setBackupOrderId(long backupOrderId) {
//		BackupOrderId = backupOrderId;
//	}




	/**
	 * @return importDate
	 */
	public java.util.Date getImportDate() {
		return ImportDate;
	}




	/**
	 * @param importDate 要设置的 importDate
	 */
	public void setImportDate(java.util.Date importDate) {
		ImportDate = importDate;
	}




	/**
	 * @return importFlag
	 */
	public int getImportFlag() {
		return ImportFlag;
	}




	/**
	 * @param importFlag 要设置的 importFlag
	 */
	public void setImportFlag(int importFlag) {
		ImportFlag = importFlag;
	}




	/**
	 * @return importOrderId
	 */
	public long getImportOrderId() {
		return ImportOrderId;
	}




	/**
	 * @param importOrderId 要设置的 importOrderId
	 */
	public void setImportOrderId(long importOrderId) {
		ImportOrderId = importOrderId;
	}




	/**
	 * @return area
	 */
	public String getArea() {
		return Area;
	}




	/**
	 * @param area 要设置的 area
	 */
	public void setArea(String area) {
		Area = area;
	}



}
