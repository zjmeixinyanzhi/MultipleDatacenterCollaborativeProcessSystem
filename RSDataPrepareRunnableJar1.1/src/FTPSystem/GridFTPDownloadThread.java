package FTPSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GridFTPDownloadThread extends Thread {
	// 下载文件源地址
	public String sourceUrl = null;
	// 下载文件目的地址
	public String destUrl = null;
	// 下载方式：0为GridFTP下载，默认；1为普通FTP下载
	public int downloadMethod = 0;
	public boolean downloadSuccess = false;

	public GridFTPDownloadThread(String sourceURL, String destURL) {
		this.sourceUrl = sourceURL;
		this.destUrl = destURL;
	}

	// ******需要添加线程控制

	public void run() {
		System.out
				.println("GridFTPDownloadThread::public void run( ) | 启动新的数据下载线程");
		// 校验Url是否合法：包括主机URL、端口、文件路径等等

		// 执行下载命令
		switch (downloadMethod) {
		case 0:
			try {
				if (GsiFTPDownload()) {
					System.out.println(this.sourceUrl + "已经下载到"
							+ this.sourceUrl);
				} else {
					System.out.println(this.sourceUrl + "下载失败！");
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			// GridFTPLiteDownload(server, username, password, remote, local,
			// filesize);
			break;

		default:
			System.out.println("<Error>:文件下载方式未指定！");
			break;
		}
	}

	// 判断是否下载完成
	public boolean isDownloadSuccess() {
		return downloadSuccess;
	}

	// 利用GridFTP下载，支持Gsi安全认证，第三方传输、条带传输、并行传输等等
	public boolean GsiFTPDownload() throws Exception {
		System.out
				.println("GridFTPDownloadThread::public boolean GsiFTPDownload( ) | 执行GsiGridFTP数据下载");
		String GsiFtpDownloadCommand = "/home/MCA/Software/globus_4.0.7/bin/globus-url-copy -vb gsiftp://"
				+ this.sourceUrl + " gsiftp://" + this.destUrl;
		// test
		System.out.println("执行GridFTP下载命令：" + GsiFtpDownloadCommand);

		String[] cmds = { "/bin/sh", "-c", GsiFtpDownloadCommand };
		Process pro;
		try {
			pro = Runtime.getRuntime().exec(cmds);
			BufferedReader bufferedReader = null;
			int exitVal = pro.waitFor();
			if (0 == exitVal) {
				System.out.println("下载命令执行成功！");
				this.downloadSuccess = true;
				bufferedReader = new BufferedReader(new InputStreamReader(
						pro.getInputStream()));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
				}
			} else {
				System.out.println("下载命令执行失败！");
				this.downloadSuccess = false;
				bufferedReader = new BufferedReader(new InputStreamReader(
						pro.getErrorStream()));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return this.downloadSuccess;
	}

	// FTP下载，认证方式为帐号密码
	public boolean GridFTPLiteDownload(String server, String username,
			String password, String remote, String local, String filesize) {
		System.out
				.println("GridFTPDownloadThread::public boolean GridFTPLiteDownload(String server, String username,"
						+ "String password, String remote, String local, String filesize) | 执行GridFTPLite数据下载");

		String ftpDownloadCommand = "/data/home/DCA/software/globus/bin/globus-url-copy -vb ftp://"
				+ username
				+ ":"
				+ password
				+ "@"
				+ server
				+ remote
				+ " "
				+ local;
		// System.out.println(ftpDownloadCommand);
		String[] cmds = { "/bin/sh", "-c", ftpDownloadCommand };
		Process pro;
		try {
			pro = Runtime.getRuntime().exec(cmds);
			int exitVal = pro.waitFor();
			BufferedReader bufferedReader = null;
			if (0 == exitVal) {
				System.out.println("");
				bufferedReader = new BufferedReader(new InputStreamReader(
						pro.getInputStream()));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
				}
			} else {
				bufferedReader = new BufferedReader(new InputStreamReader(
						pro.getErrorStream()));
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
