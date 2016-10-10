package DataService;

import java.util.*;

import DBManage.RSProductTagDB;
import DBManage.TestDBConnection;
import RSDataManage.Knowledgebase;

/**
 * @author Aaron XML两个字段
 */
public class OutOrderXmlClass {
	private String xmlnodename;
	private String xmlnodevalue;

	public OutOrderXmlClass(String xmlnodename, String xmlnodevalue) {
		this.xmlnodename = xmlnodename;
		this.xmlnodevalue = xmlnodevalue;
	}

	public String getXmlnodename() {
		return xmlnodename;
	}

	public void setXmlnodename(String xmlnodename) {
		this.xmlnodename = xmlnodename;
	}

	public String getXmlnodevalue() {
		return xmlnodevalue;
	}

	public void setXmlnodevalue(String xmlnodevalue) {
		this.xmlnodevalue = xmlnodevalue;
	}

	public static void main(String[] args) {
		TestDBConnection test = new TestDBConnection();
		test.GetConnection();
		
		ArrayList<String> dataPlanTypeLists=new ArrayList<String>();		
		
		//知识库代码
		/*
		DBManage.RSProductKnowledgeDB rSProductKnowledgeDBInstance = new DBManage.RSProductKnowledgeDB();
		Map<String, List<DataService.OutOrderXmlClass>> OutOrderXMLMapSumMain = null;
		OutOrderXMLMapSumMain=rSProductKnowledgeDBInstance.InstanceReturn("QP_NDVI_1KM");
		
		System.out.println("最终返回的数据解析结果Map");
		for (Map.Entry<String, List<DataService.OutOrderXmlClass>> entry : OutOrderXMLMapSumMain
				.entrySet()) {
//			System.out.println("\nkey=" + entry.getKey());
			List<DataService.OutOrderXmlClass> list = entry.getValue();
			Iterator<DataService.OutOrderXmlClass> iterator = list.iterator();
			//
			String item="";
//			String satellite=null;
//			String sensor=null;
			
			String satellite = null;
			int satelliteCount=0;
			String sensor = null;
			int sensorCount=0;
			
			while (iterator.hasNext()) {
				DataService.OutOrderXmlClass outOrderXmlClass = (DataService.OutOrderXmlClass) iterator
						.next();
				if (outOrderXmlClass.getXmlnodename().equals("satellite")) {
					satellite=outOrderXmlClass.getXmlnodevalue();
					satelliteCount++;
				}
				if (outOrderXmlClass.getXmlnodename().equals("sensor")) {
					sensor=outOrderXmlClass.getXmlnodevalue();
					sensorCount++;
				}
				
//				System.out.println(outOrderXmlClass.getXmlnodename() + "="
//						+ outOrderXmlClass.getXmlnodevalue());
				
				if (satellite!=null&&sensor!=null&&satelliteCount==sensorCount) {
					dataPlanTypeLists.add(sensor+"@"+satellite);
				}
			}
		
//			System.out.println();
		}		
		
		Iterator<String> iterator=dataPlanTypeLists.iterator();
		while (iterator.hasNext()) {
			String string = (String) iterator.next();
			System.out.println(string);
		}
		
		*/
		
		
		
		
//		/*

		DBManage.RSProductKnowledgeDB rSProductKnowledgeDBInstance = new DBManage.RSProductKnowledgeDB();
		Map<String, List<DataService.OutOrderXmlClass>> OutOrderXMLMapSumMain = null;

		Iterator<Knowledgebase> iterator = rSProductKnowledgeDBInstance
				.searchAll("").iterator();

		while (iterator.hasNext()) {
			Knowledgebase knowledgebase = (Knowledgebase) iterator.next();
			System.out.println(knowledgebase.getProductId());

//			ArrayList<String> infos = new ArrayList<String>();

			ArrayList<String> infoSet = new ArrayList<String>();
			OutOrderXMLMapSumMain = rSProductKnowledgeDBInstance
					.InstanceReturn(knowledgebase.getProductId());

			System.out.println("最终返回的数据解析结果Map");
			for (Map.Entry<String, List<DataService.OutOrderXmlClass>> entry : OutOrderXMLMapSumMain
					.entrySet()) {

				//
				infoSet.clear();
				infoSet=new ArrayList<String>();
				String curInfo = "";

				// System.out.println("\nkey=" + entry.getKey());
				List<DataService.OutOrderXmlClass> list = entry.getValue();
				Iterator<DataService.OutOrderXmlClass> iterator1 = list
						.iterator();
				//
				String weight = null;
				
				//成对出现
				String satellite = null;
				int satelliteCount=0;
				String sensor = null;
				int sensorCount=0;

				while (iterator1.hasNext()) {
					DataService.OutOrderXmlClass outOrderXmlClass = (DataService.OutOrderXmlClass) iterator1
							.next();
					if (outOrderXmlClass.getXmlnodename().equals("satellite")) {
						satellite = outOrderXmlClass.getXmlnodevalue();
						satelliteCount++;
					}
					if (outOrderXmlClass.getXmlnodename().equals("sensor")) {
						sensor = outOrderXmlClass.getXmlnodevalue();
						sensorCount++;
					}
//					if (outOrderXmlClass.getXmlnodename().equals(
//							"productweight")) {
//						weight = outOrderXmlClass.getXmlnodevalue();
//					}

					// System.out.println(outOrderXmlClass.getXmlnodename() +
					// "="
					// + outOrderXmlClass.getXmlnodevalue());

					// if (satellite != null && sensor != null && weight !=
					// null) {

					if (satellite != null && sensor != null && satelliteCount==sensorCount ) {
						curInfo = "satellite=" + satellite + ",sensor="
								+ sensor;
						
						 
						if (!infoSet.contains(curInfo)) {
							System.out.println(curInfo);
							infoSet.add(curInfo);
						}
					
//						if (knowledgebase.getProductId().equals("QP_NDVI_1KM")) {
							
//						}
						
					}

					// if (satellite != null && sensor != null && weight !=
					// null) {
					// curInfo = "satellite=" + satellite + ",sensor="
					// + sensor + ",weight=" + weight;
					//
					// }
//					 System.out.println(curInfo);
				}
				
			

			}

			String[] infoArray = new String[infoSet.size()];
			infoSet.toArray(infoArray);
			System.out.print(">>>>>>");
			for (Iterator iterator2 = infoSet.iterator(); iterator2.hasNext();) {
				String string = (String) iterator2.next();
				System.out.print(string+";");
			}
			
//			System.out.println("\n>>>>>>"+String.join(";", infoArray));

			RSProductTagDB rsProductDB = new RSProductTagDB();
			
			System.out.println(">>"+knowledgebase.getInputParametersProducts());

			rsProductDB.updateDatas(knowledgebase.getInputParametersData(),
					knowledgebase.getInputParametersProducts(),
					knowledgebase.getProductId());

			rsProductDB.updateAllRawDatas(String.join(";", infoArray),
					knowledgebase.getProductId());

		}
//		*/

	}

}
