/**
 * ServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class ServiceImplServiceLocator extends org.apache.axis.client.Service
		implements ServiceInterface.ServiceImplService {

	public ServiceImplServiceLocator() {
	}

	public ServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ServiceImplServiceLocator(java.lang.String wsdlLoc,
			javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for ServiceImpl
	// private java.lang.String ServiceImpl_address =
	// "";//"http://localhost:7080/MultiProcessSysWebService_Thread/services/ServiceImpl"
	// private java.lang.String ServiceImpl_address =
	// "http://10.3.10.1:7080/MultiProcessSysWebService_Thread/services/ServiceImpl";
	private java.lang.String ServiceImpl_address = "http://10.3.10.1:7080/MultiProcessSysWebService_Thread/services/ServiceImpl";

	// private java.lang.String ServiceImpl_address =
	// "http://localhost:7080/MultiProcessSysWebService_Thread/services/ServiceImpl";

	public java.lang.String getServiceImplAddress() {
		return ServiceImpl_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String ServiceImplWSDDServiceName = "ServiceImpl";

	public java.lang.String getServiceImplWSDDServiceName() {
		return ServiceImplWSDDServiceName;
	}

	public void setServiceImplWSDDServiceName(java.lang.String name) {
		ServiceImplWSDDServiceName = name;
	}

	public ServiceInterface.ServiceImpl getServiceImpl()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(ServiceImpl_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getServiceImpl(endpoint);
	}

	public ServiceInterface.ServiceImpl getServiceImpl(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			ServiceInterface.ServiceImplSoapBindingStub _stub = new ServiceInterface.ServiceImplSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getServiceImplWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setServiceImplEndpointAddress(java.lang.String address) {
		ServiceImpl_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		try {
			if (ServiceInterface.ServiceImpl.class
					.isAssignableFrom(serviceEndpointInterface)) {
				ServiceInterface.ServiceImplSoapBindingStub _stub = new ServiceInterface.ServiceImplSoapBindingStub(
						new java.net.URL(ServiceImpl_address), this);
				_stub.setPortName(getServiceImplWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException(
				"There is no stub implementation for the interface:  "
						+ (serviceEndpointInterface == null ? "null"
								: serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName,
			Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("ServiceImpl".equals(inputPortName)) {
			return getServiceImpl();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ServiceInterface",
				"ServiceImplService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://ServiceInterface",
					"ServiceImpl"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {

		if ("ServiceImpl".equals(portName)) {
			setServiceImplEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(
					" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
