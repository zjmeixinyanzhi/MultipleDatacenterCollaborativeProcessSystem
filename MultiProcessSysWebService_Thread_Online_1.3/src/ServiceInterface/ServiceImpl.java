/**
 * ServiceImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public interface ServiceImpl extends java.rmi.Remote {
    public java.lang.String orderRSDataPlan(java.lang.String strRequesXML) throws java.rmi.RemoteException;
    public java.lang.String systemMonitorInfo(java.lang.String strRequesIP) throws java.rmi.RemoteException;
    public java.lang.String commonProductRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String rnDataService(java.lang.String strRequestXML, java.lang.String serviceType) throws java.rmi.RemoteException;
    public java.lang.String validationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String dataProductSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String commonProductOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String faProducRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String dataProductQuery(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String faProductRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String dataProductViewDetail(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String dataPrdouctQuery(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String taskStatus(java.lang.String strRequestXML) throws java.rmi.RemoteException;
}
