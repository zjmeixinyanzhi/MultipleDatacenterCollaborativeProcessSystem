/**
 * WFDataServer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public interface WFDataServer extends java.rmi.Remote {
	public java.lang.String startQuery(java.lang.String request) throws java.rmi.RemoteException;

	public java.lang.String getDataDescription(java.lang.String request) throws java.rmi.RemoteException;

	public int closeQuery(java.lang.String request) throws java.rmi.RemoteException;

	public java.lang.String startDataObtain(java.lang.String request) throws java.rmi.RemoteException;

	public java.lang.String getDataResult(java.lang.String request) throws java.rmi.RemoteException;
}
