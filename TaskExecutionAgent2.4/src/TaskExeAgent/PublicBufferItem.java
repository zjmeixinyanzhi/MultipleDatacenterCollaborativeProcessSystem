/*
 *程序名称 		: PublicBufferItem.java
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

import java.sql.Date;

import DBManage.PublicBufferDB;

/**
 * @author caoyang
 *
 */
public class PublicBufferItem {
	private static PublicBufferDB publicBufferDB = new PublicBufferDB();
	private        int            id;
	private        String         dataname;
	private        int            frequency;
	private        Date           lastuseddate;
	private static byte[]         lockReadWrite  = new byte[ 0 ];

	public PublicBufferItem(){
		this.id           = -1;
		this.dataname     = "";
		this.frequency    = -1;
		this.lastuseddate = new Date( 0 );
	}
	
	public boolean setID( int id ){
		if( -1 == this.id ){
			this.id = id;
		}else{
			return false;
		}
		return true;
	}
	
	public boolean setDateName( String dataname ){
		if( null != dataname ){
			this.dataname = dataname;
		}else{
			return false;
		}
		
		return true;
	}
	
	public boolean setFrequency( int frequency ){
		if( frequency >= 0 ){
			this.frequency = frequency;
		}else{
			return false;
		}
		
		return true;
	}
	
	public boolean setLastUsedDate( Date lastUsedDate ){
		if( null != lastUsedDate ){
			this.lastuseddate = lastUsedDate;
		}else{
			return false;
		}
		
		return true;
	}
	
	public int getID(){
		return this.id;
	}
	public String getDataName(){
		return this.dataname;
	}
	public int getFrequency(){
		return this.frequency;
	}
	public Date getLastUsedDate(){
		return this.lastuseddate;
	}
	
	public static boolean add( PublicBufferItem item ){
		return publicBufferDB.add( item );
	}
	
	public static PublicBufferItem getItemByDataName( String dataname ){
		synchronized( lockReadWrite ){
			return publicBufferDB.getItemByDataName( dataname );
		}
	}
	
	public static boolean frequencyPlusByDataName( String dataname, Date lastUsedDate ){
		if( ( null == dataname ) || ( dataname.isEmpty() ) ){
			return false;
		}
		if( null == lastUsedDate ){
			return false;
		}
		synchronized( lockReadWrite ){
			PublicBufferItem item = null;
			item = publicBufferDB.getItemByDataName( dataname );
			if( null == item ){
				return false;
			}
			item.frequency++;
			if( lastUsedDate.after( item.lastuseddate ) ){
				item.lastuseddate = lastUsedDate;
			}else{
				return false;
			}
			return publicBufferDB.update( item );
		}
	}
}
