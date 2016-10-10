/*
 *程序名称 		: ThreadPublicBufferManager.java
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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DecimalFormat;

import TaskExeAgent.SystemConfig;

/**
 * @author caoyang
 *
 */
public class PublicBufferManagerThread extends Thread {
//	private SystemConfig systemConfig;
	private PublicBuffer publicBuffer;
//	
	public PublicBufferManagerThread(){
//		this.systemConfig = new SystemConfig();
		this.publicBuffer = new PublicBuffer();
	}
	
	public void run(){
		//this.publicBuffer;	//将信息存到PublicBuffer类对象中
		Date   datePublicBufferLastCleanupDate = SystemConfig.getLastCleanupDate();
		int    strPublicBufferCleanupDates     = SystemConfig.getPublicBufferCleanupDates();
		String strBufferMountPoint             = SystemConfig.getBufferMountPoint();
		float  iPublicBufferMaxUsage           = SystemConfig.getPublicBufferMaxUsage();
		
		String strCmdFilepath = SystemConfig.getSysPath() + "/getpublicbufferstatus.sh";
		try {
			Process process = Runtime.getRuntime().exec( "chmod 755 " + strCmdFilepath );
			int exitVal = process.waitFor();
			if( 0 == exitVal ){
				System.out.println( "To enhance the authority of success. | Filepath : " + strCmdFilepath  );
			}else{
				System.out.println( "Failed to enhance the authority. | Filepath : " + strCmdFilepath );
			}
		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for( ;; ){
			//获取数据缓存区占用率，如果超过最大占用率则按设定的规则清理数据缓存区，并更新数据库信息。
			
			//String command = SystemConfig.getSysPath() + "/getpublicbufferstatus.sh " + strBufferMountPoint;
			String command = strCmdFilepath + " " + strBufferMountPoint;
	    	Process process = null;
	    	try {
				process = Runtime.getRuntime().exec( command );
				int exitVal = process.waitFor();
				BufferedReader bufferedReader = null;
				if( 0 == exitVal ){
					bufferedReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
				}else{ 
					bufferedReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
				}
				
				bufferedReader.readLine();
				bufferedReader.readLine();
				
				DecimalFormat dFormat = new DecimalFormat( "#.00" );
				
				//分析返回结果
				String cmdReturnValue       = null;
				String strPublicBufferUsage = null;
				float  fPublicBufferUsage   = -1.0f;
				if( ( cmdReturnValue = bufferedReader.readLine() ) != null ) {
					String [] splitResult = cmdReturnValue.split( " " );
					
					if( null != splitResult ){
						for( int iIndex = 0; iIndex < splitResult.length; iIndex++ ){
							if( splitResult[ iIndex ].contains( "%" ) ){
								fPublicBufferUsage = Float.valueOf( splitResult[ iIndex ].substring( 0, splitResult[ iIndex ].length() ) );
							}
						}
					}
				}
				
				bufferedReader.close();
				
				if( ( fPublicBufferUsage > -1.0 ) && ( fPublicBufferUsage >= iPublicBufferMaxUsage ) ){
					//超过最大使用率，开始清理数据缓存区
					this.publicBuffer.cleanup();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (process != null) {
					close(process.getOutputStream());
					close(process.getInputStream());
					close(process.getErrorStream());
					process.destroy();
				}
			}
	    	
	    	try{
				sleep( 30000 );
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void close(Closeable c){
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
