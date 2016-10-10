package FileOperations;

/**
 * 创建时间：2016年1月23日 下午3:24:37
 * 项目名称：TaskExecutionAgent
 * 2016年1月23日
 * @author 张杰
 * @version 1.0
 * 文件名称：ZipCompressing.java
 * 类说明：
 */

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

import org.apache.naming.java.javaURLContextFactory;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 程序实现了ZIP压缩。共分为2部分 ： 压缩（compression）与解压（decompression）
 * <p>
 * 大致功能包括用了多态，递归等JAVA核心技术，可以对单个文件和任意级联文件夹进行压缩和解压。 需在代码中自定义源输入路径和目标输出路径。
 * <p>
 * 在本段代码中，实现的是压缩部分；解压部分见本包中Decompression部分。
 * 
 * @author HAN
 * 
 */

public class CompressingTools {
	private int k = 1; // 定义递归次数变量

	public CompressingTools() {
		// TODO Auto-generated constructor stub
	}
	private void zip(String zipFileName, File inputFile) throws Exception {
		System.out.println("压缩中...");
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFileName));
		BufferedOutputStream bo = new BufferedOutputStream(out);
		zip(out, inputFile, inputFile.getName(), bo);
		bo.close();
		out.close(); // 输出流关闭
		System.out.println("压缩完成");
	}

	private void zip(ZipOutputStream out, File f, String base,
			BufferedOutputStream bo) throws Exception { // 方法重载
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			if (fl.length == 0) {
				out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
				System.out.println(base + "/");
			}
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹
			}
			System.out.println("第" + k + "次递归");
			k++;
		} else {
			out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
			System.out.println(base);
			FileInputStream in = new FileInputStream(f);
			BufferedInputStream bi = new BufferedInputStream(in);
			int b;
			while ((b = bi.read()) != -1) {
				bo.write(b); // 将字节流写入当前zip目录
			}
			bi.close();
			in.close(); // 输入流关闭
		}
	}

	//解压ZIP包
	public boolean unZipFiles(java.io.File zipfile, String descDir) {
		try {
			
			File file =new File(descDir);
			if(!file.exists()){
				file.mkdirs();
			}
			ZipFile zf = new ZipFile(zipfile);
			for (Enumeration entries = zf.getEntries(); entries
					.hasMoreElements();) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());
				String zipEntryName = entry.getName();
				InputStream in = zf.getInputStream(entry);
				OutputStream out = new FileOutputStream(descDir + zipEntryName);
				byte[] buf1 = new byte[1024];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
				in.close();
				out.close();
				// System.out.println("解压缩完成.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}


	//解压tar.gz
    public static boolean unTargzFile(String tarFileName, String destDir)  
    {
    	File srcTarGzFile=new File(tarFileName);
    	if (!srcTarGzFile.exists()) {
			return false;
		}

    	boolean boo = false;//是否压缩成功
		try {
			unTarGz(srcTarGzFile,destDir);
			boo = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}finally{
			//清理操作
			if(!boo)
				deleteDirectory(new File(destDir));//目标文件夹 。清理
			
		}    	
    	return true;
    } 
    
	/**
	 * 解压tar.gz 文件
	 * @param file 要解压的tar.gz文件对象
	 * @param outputDir 要解压到某个指定的目录下
	 * @throws IOException
	 */
	public static void unTarGz(File file,String outputDir) throws IOException{
		
		TarInputStream tarIn = null;		
		try{			
			tarIn = new TarInputStream(new GZIPInputStream(
					new BufferedInputStream(new FileInputStream(file))),
					1024 * 2);
			createDirectory(outputDir,null);//创建输出目录
			TarEntry entry = null;			
			while( (entry = tarIn.getNextEntry()) != null ){				
				if(entry.isDirectory()){//是目录					
					createDirectory(outputDir,entry.getName());//创建空目录					
				}else{//是文件
					
					File tmpFile = new File(outputDir + "/" + entry.getName());					
					createDirectory(tmpFile.getParent() + "/",null);//创建输出目录					
					OutputStream out = null;					
					try{
						out = new FileOutputStream(tmpFile);
						int length = 0;
						byte[] b = new byte[2048];
						while((length = tarIn.read(b)) != -1){
							out.write(b, 0, length);
						}					
					}catch(IOException ex){
						throw ex;
					}finally{						
						if(out!=null)
							out.close();
					}					
				}
			}
			
		}catch(IOException ex){
			throw new IOException("解压归档文件出现异常",ex);
		} finally{
			try{
				if(tarIn != null){
					tarIn.close();
				}
			}catch(IOException ex){
				throw new IOException("关闭tarFile出现异常",ex);
			}
		}	
	}
	
	/**
	 * 构建目录
	 * @param outputDir
	 * @param subDir
	 */
	public static void createDirectory(String outputDir,String subDir){
		
		File file = new File(outputDir);		
		if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空			
			file = new File(outputDir + "/" + subDir);
			}		
		if(!file.exists()){			
			file.mkdirs();
		}		
	}
	
	/**
	 * 清理文件(目录或文件)
	 * @param file
	 */
	public static void deleteDirectory(File file){
		
		if(file.isFile()){			
			file.delete();//清理文件
		}else{			
			File list[] = file.listFiles();			
			if(list!=null){			
				for(File f: list){
					deleteDirectory(f);
				}
				file.delete();//清理目录
			}			
		}		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CompressingTools zipTools= new CompressingTools();
		try {
		zipTools.unZipFiles(new File("/dataIO/863_Project/863-Daemon/Project1DataService/DownloadFromRemote/HJ1A-CCD1-14-72-20140703-L20001174521.zip"), "/home.bak/MCA/sunshuyan/testnew"+File.separator);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
