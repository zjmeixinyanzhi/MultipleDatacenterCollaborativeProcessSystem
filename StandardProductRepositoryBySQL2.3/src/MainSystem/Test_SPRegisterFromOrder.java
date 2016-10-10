package MainSystem;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import domain.BDProduct;
import domain.StandarProduct;
import domain.StandarProduct;
import DBSystem.BDProductDB;
import DBSystem.DBConn;
import DBSystem.L2OrderDB;
import DBSystem.L3OrderDB;
import DBSystem.StandarProductDB;
import DBSystem.GridDB;
import DBSystem.SystemConfigDB;
import DBSystem.TypeDB;
import DataNamerParser.ParseDate;
import DataNamerParser.SwitchName;
import FileOperation.FileTraversal;
import LogSystem.SystemLogger;
import OrderManage.L2ExternalOrder;
import OrderManage.L3InternalOrder;
import Storage.Storage;

public class Test_SPRegisterFromOrder {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("输入参数不正确，请重新数据！");
			System.out
					.println("10.3.10.1_3306_mccps_mca_mca_RSProductDB L3Lorder1");
			System.exit(0);
		}
		// 等待时间（H）
		final long WAITHOURS = 24;

		// 数据库连接参数
		// 参数1为数据库连接参数
		// String DBConnectionPara = "10.3.10.1_3306_mccps_caoyang_123456";

		String DBConnectionPara = args[0];
		String[] DBConnection = DBConnectionPara.split("_");
		if (DBConnection.length != 6) {
			System.out.println("<Error>数据库连接参数出错！");
			return;
		}
		
//		String ip, String port, String mccps_sid, String user,
//		String passwd

		DBConn connection = new DBConn(DBConnection[0], DBConnection[1],
				DBConnection[2], DBConnection[3], DBConnection[4]);

		L2OrderDB l2OrderDB = new L2OrderDB();
		L3OrderDB l3OrderDB = new L3OrderDB();
		
		Logger logger = SystemLogger.getInstance().getSysLogger();
		
		SystemConfigDB systemConfigDB = new SystemConfigDB();
		String hostName = systemConfigDB.getHostName();
		

		// 参数二为三级订单号
		String l3OrderId = args[1];
		String prel3OrderId = "";

		ArrayList<L3InternalOrder> l3InternalOrders = l3OrderDB
				.search(" where JobId='" + l3OrderId + "'");
		L3InternalOrder curL3order = new L3InternalOrder();
		L3InternalOrder preL3Order = new L3InternalOrder();
		try {
			curL3order = l3InternalOrders.get(0);
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			logger.error("未找到" + l3OrderId + "当前订单");
			l3OrderDB.setOrderStatus(l3OrderId, "Error");
			return;
		}
		// 查询上级订单号
		L2ExternalOrder l2ExternalOrder = new L2ExternalOrder();
		try {
			l2ExternalOrder = l2OrderDB.search(
					" where JobId='" + curL3order.jobId_L2 + "'").get(0);
			// 获取下个
			String[] l3Orders = l2ExternalOrder.l3orderlist.split(";");
			for (int i = 0; i < l3Orders.length; i++) {
				if (l3Orders[i].equals(curL3order.jobId)
						&& l3Orders[i - 1] != null) {
					prel3OrderId = l3Orders[i - 1];
					break;
				}
			}
			// 查询产品列表
			preL3Order = l3OrderDB
					.search(" where JobId='" + prel3OrderId + "'").get(0);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("未找到" + l3OrderId + "上级订单");
			l3OrderDB.setOrderStatus(l3OrderId, "Error");
			return;
		}
		System.out.println("上级三级订单为："+prel3OrderId);
		// 读取本订单的上级订单，作为输入数据，并将该标准产品信息更新至本订单的产品列表
		curL3order.strDataProductList = preL3Order.strDataProductList;

		if (curL3order.strDataProductList == null) {
			logger.error(l3OrderId + "订单產品為空！");
			l3OrderDB.setOrderStatus(l3OrderId, "Error");
		}

		// 依次读取数据列表，提取产品路径信息，GridFTP传输，产品入库

		/*
		
		Iterator<String> iterator = curL3order.strDataProductList.iterator();

		while (iterator.hasNext()) {
			String dataListInfo = (String) iterator.next();

			// 获取產品文件列表信息
			// 拆分文件夹字符串；获取文件名、文件路径
			System.out.print(dataListInfo);
			// split_1 like
			// "Name="+file.getName()+",url="+file.getAbsolutePath()+";"
			String[] split_1 = dataListInfo.split(",");
			HashMap<String, String> DataMap = new HashMap<String, String>(
					split_1.length);
			for (int i1 = 0; i1 < split_1.length; i1++) {
				String substring = split_1[i1];
				String[] split_2 = substring.split("=");

				DataMap.put(split_2[0], split_2[1]);

			}
			String Name = DataMap.get("Name");
			// String Path = DataMap.get("url");
			// split_3 like HJ1ACCD1.30.2014181031948.HXXVXX.000000.hdf
			// or MCD12Q1.A2013001.h27v05.051.2014308192438.hdf.h5
			String[] split_3 = Name.split("\\.");
			SwitchName switchName = new SwitchName();
			// BD
			if (switchName.ifBd(split_3[0]) == true) {
				// 获取GridId
				int gridId;
				GridDB gridDB = new GridDB(DBConnection[0], DBConnection[1],
						DBConnection[5], DBConnection[3], DBConnection[4], "grid_1km_l");
				gridId = gridDB.getGridId(split_3[2]);
				System.out.println("Code>>" + split_3[2] + ">>>" + gridId);
				// 获取BDTypeId
				int typeId;
				TypeDB typeDB = new TypeDB(DBConnection[0], DBConnection[1],
						DBConnection[5], DBConnection[3], DBConnection[4], "bd_type");
				typeId = typeDB.getTypeId(split_3[0].toLowerCase());
				System.out.println("SPTId>>>" + typeId);
				// 将名字中日期规范转为date字段值
				Date datetimeDate = ParseDate.ParseDateTime(split_3[4]);
				// 入库
				String tableName;
				// tableName = switchName.ifMOD02(split_3[0]).toLowerCase();
				tableName = split_3[0].toLowerCase();
				String qpTableName = "bd_" + tableName;
				// StandarProduct sp1 = new StandarProduct(i, Name,
				// datetimeDate,hostName,DataMap.get("url"), 0, 0, (new
				// Date()), 1,
				// 0, "东南亚", 32590,typeId, gridId);
				BDProductDB bddb = new BDProductDB(DBConnection[0], DBConnection[1],
						DBConnection[5], DBConnection[3], DBConnection[4], qpTableName);
				String oldPath = bddb.select("InnerSuffix", "Name", Name);

				String newPath;

				if (oldPath == null) {
					newPath = DataMap.get("url");
				} else {
					newPath = oldPath + ";" + DataMap.get("url");
				}
				BDProduct bd1 = new BDProduct(0, Name, datetimeDate,
						(new Date()), DataMap.get("url"), hostName, newPath,
						1, 0, "东南亚", typeId, gridId);

				// StandarProductDB spdb = new StandarProductDB("10.3.10.1",
				// "3306", "RSProductDB", "mca", "mca", qpTableName);

				// 判断是否重名
				String nameItemString = bddb.select("Name", "Name", Name);
				if (nameItemString != null && nameItemString.equals(Name)) {
					bddb.upData(bd1);
				} else {
					bddb.addData(bd1);
				}
			}
			// SP
			if (switchName.ifBd(split_3[0]) == false) {
				int gridId;
				// 不分幅情况
				if (split_3[3].equals("HXXVXX")) {
					double TopLeftLongitude = Double.parseDouble(DataMap
							.get("ullon"));// 将字符串转化为等效双精度浮点数
					double TopLeftLatitude = Double.parseDouble(DataMap
							.get("ullat"));
					double LowRightLongitude = Double.parseDouble(DataMap
							.get("lrlon"));
					double LowRightLatitude = Double.parseDouble(DataMap
							.get("lrlat"));

					// 添加经纬度到latlonggrid表
//					String ip, String port, String mccps_sid, String user,
//					String passwd
					GridDB gridDB2 = new GridDB(DBConnection[0], DBConnection[1],
							DBConnection[5], DBConnection[3], DBConnection[4],  "grid_30m_s");
					gridId = (int) gridDB2.getGridLongAltID(TopLeftLongitude,
							TopLeftLatitude, LowRightLongitude,
							LowRightLatitude);
				}
				// 获取GridId--分幅情况
				else {
					if (split_3[1].equals("30m")) {
						GridDB gridDB = new GridDB(DBConnection[0], DBConnection[1],
								DBConnection[5], DBConnection[3], DBConnection[4], "grid_30m");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>"
								+ gridId);
					} else if (split_3[1].equals("1000")
							&& split_3[3].contains("Pole")) {
						GridDB gridDB = new GridDB(DBConnection[0], DBConnection[1],
								DBConnection[5], DBConnection[3], DBConnection[4],  "grid_1km_h");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>"
								+ gridId);
					} else if (split_3[1].equals("1000")
							&& split_3[3].contains("H")) {
						GridDB gridDB = new GridDB(DBConnection[0], DBConnection[1],
								DBConnection[5], DBConnection[3], DBConnection[4],  "grid_1km_l");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>"
								+ gridId);
					} else if (split_3[3].contains("Stationay")) {
						GridDB gridDB = new GridDB(DBConnection[0], DBConnection[1],
								DBConnection[5], DBConnection[3], DBConnection[4],  "grid_stationary");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>"
								+ gridId);
					} else {
						// 10.3.10.1_3306_mccps_mca_mca_RSProductDB
						GridDB gridDB = new GridDB(DBConnection[0], DBConnection[1],
								DBConnection[5], DBConnection[3], DBConnection[4],  "grid_5km");
						gridId = gridDB.getGridId(split_3[3]);
						System.out.println("Code>>" + split_3[3] + ">>>"
								+ gridId);
					}
				}
				// 获取SPTypeId
				// SwitchName switchName = new SwitchName(split_3[0]);
				String spType = switchName.ifNOAA(split_3[0]);
				int typeId;
				TypeDB typeDB = new TypeDB(DBConnection[0], DBConnection[1],
						DBConnection[5], DBConnection[3], DBConnection[4], "sp_type");
				typeId = typeDB.getTypeId(spType);
				System.out.println("SPTId>>>" + typeId);

				// 将名字中日期规范转为date字段值
				Date datetimeDate = ParseDate.ParseDateTime(split_3[2]);
				// 入库
				String tableName;
				tableName = switchName.ifMOD02(split_3[0]).toLowerCase();
				String qpTableName = "sp_" + tableName;
				StandarProductDB spdb = new StandarProductDB(DBConnection[0], DBConnection[1],
						DBConnection[5], DBConnection[3], DBConnection[4],  qpTableName);
				String oldPath = spdb.select("InnerSuffix", "Name", Name);

				String newPath;

				if (oldPath == null) {
					newPath = DataMap.get("url");
				} else {
					newPath = oldPath + ";" + DataMap.get("url");
				}
				StandarProduct sp1 = new StandarProduct(0, Name, datetimeDate,
						hostName, DataMap.get("url"), newPath, 0,
						(new Date()), 1, 0, "东南亚", 32590, typeId, gridId);

				// 判断是否重名
				String nameItemString = spdb.select("Name", "Name", Name);
				if (nameItemString != null && nameItemString.equals(Name)) {
					spdb.upData(sp1);
				} else {
					spdb.addData(sp1);
				}
			}
		}
		
		*/

		// 更新订单产品列表与状态
		l3OrderDB.setDataProductListByL4OrderProductList(l3OrderId,
				curL3order.strDataProductList);
		l3OrderDB.setOrderStatus(l3OrderId, "Finish");

		GridDB.closeConnected();
		TypeDB.closeConnected();
		StandarProductDB.closeConnected();
	}
}
