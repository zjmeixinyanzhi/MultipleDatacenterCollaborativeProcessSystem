package domain;

public class CommonProduct extends DataEntity
{

	

	/** 
	 原始数据编号	 
	*/
	public long ProductionCount;

	

	/** 
	 景经纬度范围	 
	*/
	public long GridId;
	
	public String HostName;
	
	public String InnerSuffix;
	
	public long MD5Code;
	
	public long QPTypeId;

	
	/**
	 * 无参构造函数
	 * */
	public CommonProduct(){}
	


	

	public CommonProduct(Integer id, String name, java.util.Date date,
			String hostName,String innerPrefix, String innerSuffix,long mD5Code,
			java.util.Date importDate,int importFlag, long importOrderId, String area,
			long productionCount,long qPTypeId,long gridId) {
//		super(id, name, date, diskId, innerPrefix, deleteFlag, deleteOrderId,
//				backupFlag, backupOrderId, importDate, importFlag, importOrderId, area);
		super(id, name, date,  innerPrefix,
				importDate, importFlag, importOrderId, area);
		// TODO 自动生成的构造函数存根
//		RawDataId=rawDataId;
		ProductionCount = productionCount;
		GridId=gridId;
		HostName=hostName;
		InnerSuffix=innerSuffix;
		MD5Code=mD5Code;
		QPTypeId=qPTypeId;
		
	}






	/* 
	 * @see java.lang.Object#toString() tostring方法
	 */
	@Override
	public String toString() {
//		return "StandardProduct [RawDataId=" + RawDataId + ", GridId=" + GridId
//				+"HostName,"+HostName+"InnerSuffix,"+InnerSuffix
//				+"MD5Code,"+MD5Code+"SPTypeId,"+SPTypeId
//				+ ", Id=" + Id + ", Name=" + Name + ", Date=" + Date
//				+ ", DiskId=" + DiskId + ", InnerPrefix=" + InnerPrefix
//				+ ", DeleteFlag=" + DeleteFlag + ", DeleteOrderId="
//				+ DeleteOrderId + ", BackupFlag=" + BackupFlag
//				+ ", BackupOrderId=" + BackupOrderId + ", ImportDate="
//				+ ImportDate + ", ImportFlag=" + ImportFlag
//				+ ", ImportOrderId=" + ImportOrderId + ", Area=" + Area + "]";
//	}
		return "CommonProduct [ProductionCount=" + ProductionCount + ", GridId=" + GridId
				+"HostName,"+HostName+"InnerSuffix,"+InnerSuffix
				+"MD5Code,"+MD5Code+"QPTypeId,"+QPTypeId
				+ ", Id=" + Id + ", Name=" + Name + ", Date=" + Date
				+ ", InnerPrefix=" + InnerPrefix +", ImportDate="
				+ ImportDate + ", ImportFlag=" + ImportFlag
				+ ", ImportOrderId=" + ImportOrderId + ", Area=" + Area + "]";
	}





	/**
	 * @return rawDataId
	 */
//	public long getRawDataId() {
//		return RawDataId;
//	}
	public long getProductionCount() {
	return ProductionCount;
}



	/**
	 * @param rawDataId 要设置的 rawDataId
	 */
//	public void setRawDataId(long rawDataId) {
//		RawDataId = rawDataId;
//	}
	public void setProductionCount(long productionCount) {
		ProductionCount = productionCount;
}



	/**
	 * @return gridId
	 */
	public long getGridId() {
		return GridId;
	}



	/**
	 * @param gridId 要设置的 gridId
	 */
	public void setGridId(long gridId) {
		GridId = gridId;
	}
	
	public String getHostName(){
		return HostName;
	}
	
	public void setHostName(String hostName){
		HostName = hostName; 
	}
	
	public String getInnerSuffix(){
		return InnerSuffix;
	}
	
	public void setInnerSuffix(String innerSuffix){
		InnerSuffix = innerSuffix;
	}
	
	public long getMD5Code(){
		return MD5Code;
	} 
	
	public void setMD5Code(long mD5Code){
		MD5Code=mD5Code;
	}
	
	public long getQPTypeId(){
		return QPTypeId;
	}
	
	public void setSPTypeId(long sPTypeId){
		QPTypeId = getQPTypeId();
	}
	




}
