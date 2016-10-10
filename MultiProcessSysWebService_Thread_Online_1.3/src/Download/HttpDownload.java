package Download;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import DBManage.RsDataCacheDB;
import RSDataManage.RSData;

public class HttpDownload {

	/** http下载 */
	public static boolean httpDownload(String httpUrl, String saveFile) {
		// 下载网络文件
		int bytesum = 0;
		int byteread = 0;

		URL url = null;
		try {
			url = new URL(httpUrl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		
		try {
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			FileOutputStream fs = new FileOutputStream(saveFile);

			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				//System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) {
//		String sourceUrl="http://10.3.14.2:8080/RSDS/files/219/219.zip";
//		httpDownload(sourceUrl, "D://test/hello.zip");
		
		//数据下载并发测试
		RsDataCacheDB rsDataCacheDB=new RsDataCacheDB();
		ArrayList<RSData> rsDatas=rsDataCacheDB.search(" where dataid not like '%QP%' and datastatus='Available'");
		
		
		
		
		
		
	}

}
