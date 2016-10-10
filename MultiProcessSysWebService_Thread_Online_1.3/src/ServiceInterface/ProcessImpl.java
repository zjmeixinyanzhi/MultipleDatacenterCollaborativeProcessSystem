/**
 * ProcessImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public interface ProcessImpl extends java.rmi.Remote {
    public java.lang.String dataProductQuery(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String orderRSDataRequirement(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String retrievalOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String dataProductViewDetail(java.lang.String strRequestXML) throws java.rmi.RemoteException;
    public java.lang.String getDataResult(java.lang.String taskId) throws java.rmi.RemoteException;
    public java.lang.String startDataObtain(java.lang.String strRequestXML) throws java.rmi.RemoteException;
}
