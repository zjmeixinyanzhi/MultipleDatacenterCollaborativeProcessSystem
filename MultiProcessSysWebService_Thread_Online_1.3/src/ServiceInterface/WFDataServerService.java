/**
 * WFDataServerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public interface WFDataServerService extends javax.xml.rpc.Service {
	public java.lang.String getWFDataServerAddress();

	public ServiceInterface.WFDataServer getWFDataServer() throws javax.xml.rpc.ServiceException;

	public ServiceInterface.WFDataServer getWFDataServer(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
