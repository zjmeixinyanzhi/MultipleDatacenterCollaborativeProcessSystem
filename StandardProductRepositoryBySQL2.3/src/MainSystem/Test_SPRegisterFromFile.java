package MainSystem;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import domain.BDProduct;
import domain.StandarProduct;
import domain.StandarProduct;
import DBSystem.BDProductDB;
import DBSystem.DBConn;
import DBSystem.L3OrderDB;
import DBSystem.StandarProductDB;
import DBSystem.GridDB;
import DBSystem.TypeDB;
import DataNamerParser.ParseDate;
import DataNamerParser.SwitchName;
import FileOperation.FileTraversal;
import LogSystem.SystemLogger;
import Storage.Storage;

public class Test_SPRegisterFromFile 
{

	public static void main(String[] args)
	{
		
		
		
//		//遍历文件夹
////		String filePath = "/public/MuSyQ/LST1000M";
		String filePath = args[0];
//		Storage storage = new Storage();
//		storage.SpStorage(filePath);
		FileTraversal fileTraversal = new FileTraversal();
		//获取文件列表信息 
		String dataListInfo = fileTraversal.getFileName(filePath);
		//拆分文件夹字符串；获取文件名、文件路径
		String[] datalist = dataListInfo.split(";");
		System.out.print(dataListInfo);
		for(int i=0;i<datalist.length;i++)
		{
			//split_1 like "Name="+file.getName()+",url="+file.getAbsolutePath()+";"
			String[] split_1 = datalist[i].split(",");
			HashMap<String, String> DataMap = new HashMap<String, String>(split_1.length);
			for(int i1=0;i1<split_1.length;i1++)
			{
				String substring = split_1[i1];
				String[] split_2 = substring.split("=");

				DataMap.put(split_2[0], split_2[1]);
				
			}
			String Name = DataMap.get("Name");
//			String Path = DataMap.get("url");
			//split_3 like HJ1ACCD1.30.2014181031948.HXXVXX.000000.hdf
			//or MCD12Q1.A2013001.h27v05.051.2014308192438.hdf.h5
			String[] split_3 = Name.split("\\.");
			SwitchName switchName = new SwitchName();
			//BD
			if(switchName.ifBd(split_3[0])==true){
				//获取GridId
				int gridId;
				GridDB gridDB=new GridDB("10.3.10.1", "3306", 
						"RSProductDB", "mca", "mca", "grid_1km_l");
				gridId = gridDB.getGridId(split_3[2]);
				System.out.println("Code>>" + split_3[2] + ">>>" + gridId);
				//获取BDTypeId
				int typeId;
				TypeDB typeDB = new TypeDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", "bd_type");		
				typeId = typeDB.getTypeId(split_3[0].toLowerCase());
				System.out.println("SPTId>>>"+typeId);
				// 将名字中日期规范转为date字段值
				Date datetimeDate = ParseDate.ParseDateTime(split_3[4]);
				//入库
				String tableName;
//				tableName = switchName.ifMOD02(split_3[0]).toLowerCase();
				tableName = split_3[0].toLowerCase();
				String qpTableName = "bd_"+tableName;
//				StandarProduct sp1 = new StandarProduct(i, Name, 
//						datetimeDate,"HostName",DataMap.get("url"), 0, 0, (new Date()), 1, 
//						0, "东南亚", 32590,typeId, gridId);
				BDProductDB bddb = new BDProductDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", qpTableName);
				String oldPath = bddb.select("InnerSuffix", "Name", Name);
				
				
				String newPath;
				
				if (oldPath==null) {
					newPath=DataMap.get("url");
				}else {
					newPath=oldPath+";"+DataMap.get("url");
				}
				BDProduct bd1 = new BDProduct(i, Name, 
						datetimeDate,(new Date()),DataMap.get("url"),"HostName",newPath,  1, 
						0, "东南亚", typeId, gridId);

//				StandarProductDB spdb = new StandarProductDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", qpTableName);

				//判断是否重名
				String nameItemString=bddb.select("Name","Name",Name);
				if(nameItemString!=null&&nameItemString.equals(Name)){
					bddb.upData(bd1);
				}
				else{
					bddb.addData(bd1);
				}		
			}
			//SP
			if(switchName.ifBd(split_3[0])==false){
				int gridId;
				//不分幅情况
				if (split_3[3].equals("HXXVXX")) 
				{
					double TopLeftLongitude = Double.parseDouble(DataMap
							.get("ullon"));// 将字符串转化为等效双精度浮点数
					double TopLeftLatitude = Double.parseDouble(DataMap
							.get("ullat"));
					double LowRightLongitude = Double.parseDouble(DataMap
							.get("lrlon"));
					double LowRightLatitude = Double.parseDouble(DataMap
							.get("lrlat"));

					// 添加经纬度到latlonggrid表
					GridDB gridDB2 = new GridDB("10.3.10.1", "3306",
							"RSProductDB", "mca", "mca","grid_30m_s");
					gridId = (int) gridDB2.getGridLongAltID(TopLeftLongitude,
							TopLeftLatitude, LowRightLongitude, LowRightLatitude);
				}
				//获取GridId--分幅情况
				else
				{
					if(split_3[1].equals("30m"))
					{
						GridDB gridDB=new GridDB("10.3.10.1", "3306", 
								"RSProductDB", "mca", "mca", "grid_30m");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>" + gridId);
					}
					else if(split_3[1].equals("1000")&&split_3[3].contains("Pole")){
						GridDB gridDB=new GridDB("10.3.10.1", "3306", 
								"RSProductDB", "mca", "mca", "grid_1km_h");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>" + gridId);
					}
					else if(split_3[1].equals("1000")&&split_3[3].contains("H")){
						GridDB gridDB=new GridDB("10.3.10.1", "3306", 
								"RSProductDB", "mca", "mca", "grid_1km_l");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>" + gridId);
					}
					else if(split_3[3].contains("Stationay")){
						GridDB gridDB=new GridDB("10.3.10.1", "3306", 
								"RSProductDB", "mca", "mca", "grid_stationary");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>" + gridId);
					}
					else
					{
//						10.3.10.1_3306_mccps_mca_mca_RSProductDB
						GridDB gridDB=new GridDB("10.3.10.1", "3306", 
								"RSProductDB", "mca", "mca", "grid_5km");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>" + gridId);
					}
				}
				//获取SPTypeId
//				SwitchName switchName = new SwitchName(split_3[0]);
				String spType = switchName.ifNOAA(split_3[0]);
				int typeId;
				TypeDB typeDB = new TypeDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", "sp_type");		
				typeId = typeDB.getTypeId(spType);
				System.out.println("SPTId>>>"+typeId);
				
				// 将名字中日期规范转为date字段值
				Date datetimeDate = ParseDate.ParseDateTime(split_3[2]);
				//入库
				String tableName;
				tableName = switchName.ifMOD02(split_3[0]).toLowerCase();
				String qpTableName = "sp_"+tableName;
				StandarProductDB spdb = new StandarProductDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", qpTableName);
				String oldPath = spdb.select("InnerSuffix", "Name", Name);
				
				
				String newPath;
				
				if (oldPath==null) {
					newPath=DataMap.get("url");
				}else {
					newPath=oldPath+";"+DataMap.get("url");
				}
				StandarProduct sp1 = new StandarProduct(i, Name, 
						datetimeDate,"HostName",DataMap.get("url"),newPath, 0, (new Date()), 1, 
						0, "东南亚", 32590,typeId, gridId);

				//判断是否重名
				String nameItemString=spdb.select("Name","Name",Name);
				if(nameItemString!=null&&nameItemString.equals(Name)){
					spdb.upData(sp1);
				}
				else{
					spdb.addData(sp1);
				}

			}
		}
		
		GridDB.closeConnected();
		TypeDB.closeConnected();
		StandarProductDB.closeConnected();	
	}
}
