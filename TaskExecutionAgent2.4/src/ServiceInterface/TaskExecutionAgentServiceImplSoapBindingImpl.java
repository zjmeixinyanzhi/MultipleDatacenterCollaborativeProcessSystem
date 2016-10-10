/*
 *程序名称 		: TaskExecutionAgentServiceImplSoapBindingImpl.java
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
/**
 * TaskExecutionAgentServiceImplSoapBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import OrderManage.L3InternalOrder;
import OrderManage.OrderStudio;
import RSDataManage.RSData;
import TaskExeAgent.SystemConfig;


/**
 * @author caoyang
 *
 */

public class TaskExecutionAgentServiceImplSoapBindingImpl implements ServiceInterface.TaskExecutionAgentServiceImpl{
	private OrderStudio orderStudio;	// 这里要用子订单执行代理的jar包，这样才能正确处理订单
	
	public TaskExecutionAgentServiceImplSoapBindingImpl(){
		orderStudio = new OrderStudio();
	}
	
	/*
	 * 方法名称：辐射/几何归一化生产订单提交接口
	 * 参数        ：1. strRequestXML：请求XML
	 *        ：
	 * 返回值    ：消息接收成功（共性产品生产任务单号）/ 失败
	 * 描述        ：主中心的系统运行管理模块向数据中心的子订单运行代理模块提交
	 *        ：辐射归一化/几何归一化产品生产子订单
	 */
    public java.lang.String normalizationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException {
    	String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><NormalizationOrderSubmit><feedback>";
    	L3InternalOrder l3Order = new L3InternalOrder();
    	
    	boolean bRef = false;
    	try{
    		//获取请求XML
			//一级订单号
			String strL1OrderId             = getElementText( strRequestXML, "//NormalizationOrderSubmit/L1OrderId" );
			//二级订单号
			String strL2OrderId             = getElementText( strRequestXML, "//NormalizationOrderSubmit/L2OrderId" );
			//三级订单号
			String strL3OrderId             = getElementText( strRequestXML, "//NormalizationOrderSubmit/L3OrderId" );
			//订单类型：RadNormalization/GeoNormalization，辐射归一化/几何归一化
			String strOrderType             = getElementText( strRequestXML, "//NormalizationOrderSubmit/OrderType" );
			//算法资源：名称及程序
			String strAlgorithmName         = getElementText( strRequestXML, "//NormalizationOrderSubmit/AlgorithmName" );
			//
			String strAlgorithmPath         = getElementText( strRequestXML, "//NormalizationOrderSubmit/AlgorithmPath" );
			//产品名称
			String strProductName           = getElementText( strRequestXML, "//NormalizationOrderSubmit/Parameters/ProductName" );
			//空间范围：四角坐标
			String strULLat                 = getElementText( strRequestXML, "//NormalizationOrderSubmit/Parameters/ULLat" );
			//
			String strULLong                = getElementText( strRequestXML, "//NormalizationOrderSubmit/Parameters/ULLong" );
			//
			String strLRLat                 = getElementText( strRequestXML, "//NormalizationOrderSubmit/Parameters/LRLat" );
			//
			String strLRLong                = getElementText( strRequestXML, "//NormalizationOrderSubmit/Parameters/LRLong" );
			//时相（成像时间）
			String strStartDate             = getElementText( strRequestXML, "//NormalizationOrderSubmit/Parameters/StartDate" );
			//
			String strEndDate               = getElementText( strRequestXML, "//NormalizationOrderSubmit/Parameters/EndDate" );
			//数据列表
			
			ArrayList< RSData > strDataList = getRSDataList( strRequestXML, "//NormalizationOrderSubmit/Datas" );
			
			//System.out.println("\n"+strRequestXML);
			
			l3Order.jobId_L1           = strL1OrderId;
			l3Order.jobId_L2           = strL2OrderId;
			l3Order.jobId              = strL3OrderId;
			l3Order.orderType          = strOrderType;
			l3Order.orderLevel         = "3";
			l3Order.algorithmName      = strAlgorithmName;
			l3Order.algorithmPath      = strAlgorithmPath;
			l3Order.productName        = strProductName;
			l3Order.geoCoverageStr     = strULLat + "," + strULLong + "," + strLRLat + "," + strLRLong;
			l3Order.startDate          = Date.valueOf( strStartDate );
			l3Order.endDate            = Date.valueOf( strEndDate );
			l3Order.workingStatus      = "Ready";
			l3Order.dataList           = strDataList;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			format.setLenient(false);
			java.util.Date now_date = new Date( System.currentTimeMillis() );
			String strSubmitDate = format.format( now_date );
			l3Order.submitDate         = new Date( format.parse( strSubmitDate ).getTime() );
			l3Order.strDataProductList = new ArrayList< String >();
			l3Order.retrievalDataList  = new ArrayList< String >();
			
			bRef = this.orderStudio.addOrder( l3Order );
			
			//test
			System.out.println( "***********************Name : " + strAlgorithmName + " Path : " + strAlgorithmPath );
		}catch( Exception e ){
			e.printStackTrace();
		}
		
    	//生成（响应）结果XML
		
		String strCode;
		String strInfo;
		if( bRef ){
			strCode = "100";
			strInfo = "Success";
		}else{
			strCode = "XXX";
			strInfo = "Fail";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo + "</info>" + "</feedback></NormalizationOrderSubmit>";
		
		return strResponseXML;
    }

    /*
	 * 方法名称：融合/同化生产订单提交接口
	 * 参数        ：1. strRequestXML：请求XML
	 *        ：
	 * 返回值    ：消息接收成功（共性产品生产任务单号）/ 失败
	 * 描述        ：主中心的系统运行管理模块向数据中心的子订单运行代理模块提交
	 *        ：同化/融合生产子订单
	 */
    public java.lang.String fusionAssimilationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException {
    	String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FusionAssimilationOrderSubmit><feedback>";
    	L3InternalOrder l3Order = new L3InternalOrder();
    	
    	System.out.println("\t"+strRequestXML);
    	
    	boolean bRef = false;
    	try{
    		//获取请求XML
			//一级订单号
			String strL1OrderId                             = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/L1OrderId" );
			//二级订单号
			String strL2OrderId                             = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/L2OrderId" );
			//三级订单号
			String strL3OrderId                             = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/L3OrderId" );
			//订单类型：RadNormalization/GeoNormalization，辐射归一化/几何归一化
			String strOrderType                             = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/OrderType" );
			//算法资源：名称及程序
			String strAlgorithmName                         = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/AlgorithmName" );
			//
			String strAlgorithmPath                         = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/AlgorithmPath" );
			//产品名称
			String strProductName                           = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/Parameters/ProductName" );
			//空间范围：四角坐标
			String strULLat                                 = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/Parameters/ULLat" );
			//
			String strULLong                                = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/Parameters/ULLong" );
			//
			String strLRLat                                 = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/Parameters/LRLat" );
			//
			String strLRLong                                = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/Parameters/LRLong" );
			//时相（成像时间）
			String strStartDate                             = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/Parameters/StartDate" );
			//
			String strEndDate                               = getElementText( strRequestXML, "//FusionAssimilationOrderSubmit/Parameters/EndDate" );
			//数据列表
			ArrayList< RSData > strDataList                 = getRSDataList(    strRequestXML, "//FusionAssimilationOrderSubmit/Datas" );
			//共性数据产品列表
			ArrayList< String > strRetrievalDataProductList = getDataList(    strRequestXML, "//FusionAssimilationOrderSubmit/RetrievalDataProducts" );
			
			l3Order.jobId_L1           = strL1OrderId;
			l3Order.jobId_L2           = strL2OrderId;
			l3Order.jobId              = strL3OrderId;
			l3Order.orderLevel         = "3";
			l3Order.orderType          = strOrderType;
			l3Order.algorithmName      = strAlgorithmName;
			l3Order.algorithmPath      = strAlgorithmPath;
			l3Order.productName        = strProductName;
			l3Order.geoCoverageStr     = strULLat + "," + strULLong + "," + strLRLat + "," + strLRLong;
			l3Order.startDate          = Date.valueOf( strStartDate );
			l3Order.endDate            = Date.valueOf( strEndDate );
			l3Order.workingStatus      = "Ready";
			l3Order.dataList           = strDataList;
			l3Order.strDataProductList = new ArrayList< String >();
			l3Order.retrievalDataList  = strRetrievalDataProductList;
			DateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
			format.setLenient( false );
			java.util.Date now_date = new Date( System.currentTimeMillis() );
			String strSubmitDate = format.format( now_date );
			l3Order.submitDate         = new Date( format.parse( strSubmitDate ).getTime() );
			
			bRef = this.orderStudio.addOrder( l3Order );
		}catch( Exception e ){
			e.printStackTrace();
		}
    	
    	//生成（响应）结果XML
		
		String strCode;
		String strInfo;
		if( bRef ){
			strCode = "100";
			strInfo = "Success";
		}else{
			strCode = "XXX";
			strInfo = "Fail";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo + "</info>" + "</feedback></FusionAssimilationOrderSubmit>";
		
		return strResponseXML;
    }
    
    public java.lang.String publicBufferCleanup(java.lang.String strRequestXML) throws java.rmi.RemoteException {
        return null;
    }

	public java.lang.String getPublicBufferUsedSize(java.lang.String strRequestXML) throws java.rmi.RemoteException {
		String strResponseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PublicBufferUsedSize><feedback>";
		String strCmdFilepath = SystemConfig.getSysPath() + "/getbuffersize.sh";
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
		String strPublicBufferUsed = "";
		String strPublicBufferPath = null;
		boolean bRef = false;
    	try{
    		strPublicBufferPath = getElementText( strRequestXML, "//PublicBufferUsedSize/PublicBufferPath" );
    		if( ( null != strPublicBufferPath ) && ( !strPublicBufferPath.isEmpty() ) ){
//	    		String command = SystemConfig.getSysPath() + "/getbuffersize.sh " + strPublicBufferPath;
    			String command = strCmdFilepath + " " + strPublicBufferPath;
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
					
					//分析返回结果
					String cmdReturnValue = null;
					if( ( cmdReturnValue = bufferedReader.readLine() ) != null ){
						bufferedReader.close();
						String [] list = cmdReturnValue.split( " " );
						if( list.length > 1 ){
							strPublicBufferUsed = list[ 0 ];
							bRef = true;
						}
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
    		}
    	}catch( Exception e ){
			e.printStackTrace();
		}
    	
    	//生成（响应）结果XML
		
		String strCode;
		String strInfo;
		if( bRef ){
			strCode = "100";
			strInfo = "Success";
		}else{
			strCode = "XXX";
			strInfo = "Fail";
		}
		strResponseXML += "<code>" + strCode + "</code>" + "<info>" + strInfo + "</info><value>" + strPublicBufferUsed + "</value>" + "</feedback></PublicBufferUsedSize>";
		
		return strResponseXML;
    }

    private String getElementText( String strXML, String strElementPath ) throws DocumentException{
    	try{
			Document dom = DocumentHelper.parseText( strXML );
			return dom.selectSingleNode( strElementPath ).getText();
    	}catch( NullPointerException e ){
    		e.printStackTrace();
    		System.out.println( "<Error>ServiceImplSoapBindingImpl::getElementText | Element path is invalid. \n Please Check the Node of"+strElementPath );
    		return "";
    	}
	}
    
    private ArrayList< String > getDataList( String strXML, String strElementPath ) throws DocumentException{
    	ArrayList< String > dataList = new ArrayList< String >();
    	
    	try{
			Document dom = DocumentHelper.parseText( strXML );			
			List list = dom.selectNodes( strElementPath );			
			Iterator iter = list.iterator();
			while( iter.hasNext() ){
				List list_datas = ( ( Element )iter.next() ).elements();
				Iterator iter_datas = list_datas.iterator();
				while( iter_datas.hasNext() ){
					String strData = "";
					Element element_data = ( Element )iter_datas.next();
					List list_data = element_data.elements();
					
					strData += "Name=" + element_data.attributeValue( "Name" );
					
					Iterator iter_dataelement = list_data.iterator();
					while( iter_dataelement.hasNext() ){
						
						strData += ",";						
						Element element_dataelement = ( Element )iter_dataelement.next();						
						//System.out.println( element_dataelement.getName() + "=" + element_dataelement.getStringValue() );						
						strData += element_dataelement.getName() + "=" + element_dataelement.getStringValue();
					}
					
					//System.out.println( strData );
					
					dataList.add( strData );
				}
			}
    	}catch( NullPointerException e ){
    		e.printStackTrace();
    		System.out.println( "<Error>ServiceImplSoapBindingImpl::getDataList | Element path is invalid." );
    		return new ArrayList< String >();
    	}
		
		return dataList;
	}

    private ArrayList< RSData > getRSDataList( String strXML, String strElementPath ) throws DocumentException{
    	ArrayList< RSData > dataList = new ArrayList< RSData >();
    	
    	try{
			Document dom = DocumentHelper.parseText( strXML );
			
			List list = dom.selectNodes( strElementPath );
			
			Iterator iter = list.iterator();
			while( iter.hasNext() ){
				List list_datas = ( ( Element )iter.next() ).elements();
				Iterator iter_datas = list_datas.iterator();
				while( iter_datas.hasNext() ){
					String strData = "";
					Element element_data = ( Element )iter_datas.next();
					List list_data = element_data.elements();
					
					//System.out.println( "name:" + element_data.getName() );
					//System.out.println( "id  :" + element_data.attributeValue( "id" ) );
					//System.out.println( "name:" + element_data.attributeValue( "Name" ) );
					
					strData += "Name=" + element_data.attributeValue( "Name" );
					
					Iterator iter_dataelement = list_data.iterator();
					while( iter_dataelement.hasNext() ){
						
						strData += ",";
						Element element_dataelement = ( Element )iter_dataelement.next();						
						//System.out.println( element_dataelement.getName() + "=" + element_dataelement.getStringValue() );						
						strData += element_dataelement.getName() + "=" + element_dataelement.getStringValue();
					}
					RSData data=new RSData(strData);
					dataList.add(data);
				}
			}
    	}catch( NullPointerException e ){
    		e.printStackTrace();
    		System.out.println( "<Error>ServiceImplSoapBindingImpl::getDataList | Element path is invalid." );
    		return new ArrayList< RSData >();
    	}
		
		return dataList;
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
