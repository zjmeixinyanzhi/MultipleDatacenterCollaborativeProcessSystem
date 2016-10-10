/**
 * WFDataServerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class WFDataServerServiceLocator extends org.apache.axis.client.Service
		implements ServiceInterface.WFDataServerService {

	public WFDataServerServiceLocator() {
	}

	public WFDataServerServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public WFDataServerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for WFDataServer
	private java.lang.String WFDataServer_address = "http://localhost:8080/DDSS/services/WFDataServer";

	public java.lang.String getWFDataServerAddress() {
		return WFDataServer_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String WFDataServerWSDDServiceName = "WFDataServer";

	public java.lang.String getWFDataServerWSDDServiceName() {
		return WFDataServerWSDDServiceName;
	}

	public void setWFDataServerWSDDServiceName(java.lang.String name) {
		WFDataServerWSDDServiceName = name;
	}

	public ServiceInterface.WFDataServer getWFDataServer() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(WFDataServer_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getWFDataServer(endpoint);
	}

	public ServiceInterface.WFDataServer getWFDataServer(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			ServiceInterface.WFDataServerSoapBindingStub _stub = new ServiceInterface.WFDataServerSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getWFDataServerWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setWFDataServerEndpointAddress(java.lang.String address) {
		WFDataServer_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (ServiceInterface.WFDataServer.class.isAssignableFrom(serviceEndpointInterface)) {
				ServiceInterface.WFDataServerSoapBindingStub _stub = new ServiceInterface.WFDataServerSoapBindingStub(
						new java.net.URL(WFDataServer_address), this);
				_stub.setPortName(getWFDataServerWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
				+ (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("WFDataServer".equals(inputPortName)) {
			return getWFDataServer();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://thgrid.org/ddss/rs", "WFDataServerService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://thgrid.org/ddss/rs", "WFDataServer"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {

		if ("WFDataServer".equals(portName)) {
			setWFDataServerEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address)
			throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
