/*
 *程序名称 		: ProcessThread4.java
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
package SystemManage;

/**
 * @author caoyang
 *
 */
public class PublicBufferManagerThread extends Thread {
	// 用于公共数据缓存区管理的缓存区管理对象
	private PublicBuffer publicBuffer;

	/**
	 * 
	 */
	public PublicBufferManagerThread() {
		this.publicBuffer = new PublicBuffer();
	}

	/**
	 * @param arg0
	 */
	public PublicBufferManagerThread(Runnable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public PublicBufferManagerThread(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PublicBufferManagerThread(ThreadGroup arg0, Runnable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PublicBufferManagerThread(ThreadGroup arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public PublicBufferManagerThread(Runnable arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public PublicBufferManagerThread(ThreadGroup arg0, Runnable arg1, String arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public PublicBufferManagerThread(ThreadGroup arg0, Runnable arg1, String arg2,
			long arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}
	
	public void run(){
		
		for( ;; ){
			//定期清理缓存空间
			System.out.println( "15.定期清理缓存空间" );
			publicBuffer.cleanup();
			
			try {
				sleep(5000); //暂停，每一秒输出一次
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
