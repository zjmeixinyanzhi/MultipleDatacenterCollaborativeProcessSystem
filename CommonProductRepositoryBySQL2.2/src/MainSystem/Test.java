package MainSystem;

import java.util.Date;
import java.util.HashMap;

import domain.CommonProduct;
import DBSystem.CommonProductDB;
import DBSystem.GridDB;
import DBSystem.TypeDB;
import DataNamerParser.ParseDate;
import DataNamerParser.SwitchName;
import FileOperation.FileTraversal;

public class Test 
{
	public static void main(String[] args)
	{
		//遍历文件夹
//		String filePath = "/public/MuSyQ/LST1000M";
		String filePath = args[0];
		FileTraversal fileTraversal = new FileTraversal();
		//获取文件列表信息
		String dataListInfo = fileTraversal.getFileName(filePath);
		//拆分文件夹字符串；获取文件名、文件路径
		String[] datalist = dataListInfo.split(";");
		System.out.print(dataListInfo);
		for(int i=0;i<datalist.length;i++)
		{
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
			String[] split_3 = Name.split("\\.");
			SwitchName switchName = new SwitchName(split_3[1]);
			split_3[1] = switchName.ifVI(split_3[1]);
			//获取GridId
			int gridId;
			//不分幅情况
			if (split_3[2].equals("30m_s")) 
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
				if(split_3[2].equals("30m"))
				{
					GridDB gridDB=new GridDB("10.3.10.1", "3306", 
							"RSProductDB", "mca", "mca", "grid_30m");
					gridId = gridDB.getGridId(split_3[4]);
					System.out.println("Code>>" + split_3[4] + ">>>" + gridId);
				}
				else if(split_3[2].equals("5km"))
				{
					gridId = 1;
					System.out.println("Code>>" + split_3[4] + ">>>" + gridId);
				}
				else
				{
//					10.3.10.1_3306_mccps_mca_mca_RSProductDB
					GridDB gridDB=new GridDB("10.3.10.1", "3306", 
							"RSProductDB", "mca", "mca", "grid_1km_l");
					gridId = gridDB.getGridId(split_3[4]);
					System.out.println("Code>>" + split_3[4] + ">>>" + gridId);
				}
			}
			//获取QPTypeId
			int typeId;
			String qpType = split_3[1]+"_"+split_3[2];
			TypeDB typeDB = new TypeDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", "qp_type");		
			typeId = typeDB.getTypeId(qpType);
			System.out.println("SPTId>>>"+typeId);
			
			// 将名字中日期规范转为date字段值
			Date datetimeDate = ParseDate.ParseDateTime(split_3[3]);
			//入库
			String qpTableName = "qp_"+split_3[1]+"_"+split_3[2];
			CommonProductDB spdb = new CommonProductDB("10.3.10.1", "3306", "RSProductDB", "mca", "mca", qpTableName);
			//查询InnerSuffix
			String oldPath = spdb.select("InnerSuffix", "Name", Name);
			
			
			String newPath;
			
			if (oldPath==null) {
				newPath=DataMap.get("url");
			}else {
				newPath=oldPath+";"+DataMap.get("url");
			}

			CommonProduct sp1 = new CommonProduct(i, Name, 
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
//			spdb.addData(sp1);
//			spdb.upData(sp1);
		}
		
		GridDB.closeConnected();
		TypeDB.closeConnected();
		CommonProductDB.closeConnected();	
	}
}
