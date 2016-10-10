package FileOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;




public class FileTraversal {
	public static ArrayList<String>filelist=new ArrayList<String>();
	int i = 0;
	public String getFileName(String filePath) 
	{
		String fileInformation = "";
      int fileNum = 0, folderNum = 0;
      File file = new File(filePath);
      if (file.exists()) {
            LinkedList<File> list = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
//                    System.out.println("文件夹:" + file2.getAbsolutePath());
                    list.add(file2);
//                    fileInformation +="Name="+file2.getName()+",url="+file2.getAbsolutePath()+";";
                    folderNum++;
                } else {
//                    System.out.println("文件:" + file2.getAbsolutePath());
//                   独立文件
                	System.out.println("文件:" + file2.getAbsolutePath());
//                	fileInformation +="Name="+file2.getName()+",url="+file2.getAbsolutePath()+";";
                	fileInformation +="Name="+file2.getName()+",url="+file2.getAbsolutePath()+";";
                    fileNum++;
            
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
//                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        list.add(file2);
                        folderNum++;
//                        fileInformation +="Name="+file2.getName()+",url="+file2.getAbsolutePath()+";";
                    } else {
                        System.out.println("文件:" + file2.getAbsolutePath());
                        fileNum++;
                        fileInformation +="Name="+file2.getName()+",url="+file2.getAbsolutePath()+";";
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
      	System.out.println(fileInformation);
       System.out.println("文件夹共有:" + folderNum + ",文件共有:" + fileNum);

        	return fileInformation;
//		String fileInformation = "";
//		File root= new File(filePath);
//		File[] files = root.listFiles();
//		for(File file:files){
//			if(file.isDirectory()){
//				getFileName(file.getAbsolutePath());
//				filelist.add(file.getAbsolutePath());
//			}
//			else{
//				fileInformation +="Name="+file.getName()+",url="+file.getAbsolutePath()+";";
//			}
//		}
////		System.out.println(fileInformation);
//		return fileInformation;
	}
	
	public static void main(String[] args)
	{
		FileTraversal fileTraversal = new FileTraversal();
		fileTraversal.getFileName("/public/MuSyQ/FPAR");
	}
}
