/**
 * Service1Soap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface Service1Soap extends java.rmi.Remote {
    public java.lang.String orderCommRSDataRequirement(java.lang.String orderXML) throws java.rmi.RemoteException;
    public java.lang.String commOrderSubmit(java.lang.String cosXML) throws java.rmi.RemoteException;
    public java.lang.String commDataProductQuery(java.lang.String queryXml) throws java.rmi.RemoteException;
}
