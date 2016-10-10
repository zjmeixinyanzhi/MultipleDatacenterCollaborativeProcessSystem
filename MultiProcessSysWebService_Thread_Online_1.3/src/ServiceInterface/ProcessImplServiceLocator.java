/**
 * ProcessImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class ProcessImplServiceLocator extends org.apache.axis.client.Service
		implements ServiceInterface.ProcessImplService {

	public ProcessImplServiceLocator() {
	}

	public ProcessImplServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ProcessImplServiceLocator(java.lang.String wsdlLoc,
			javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for ProcessImpl
	// private java.lang.String ProcessImpl_address =
	// "http://localhost:10080/Project1Webservice_new/services/ProcessImpl";

	private java.lang.String ProcessImpl_address = "http://localhost:10080/Project1Webservice/services/ProcessImpl";

	public java.lang.String getProcessImplAddress() {
		return ProcessImpl_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String ProcessImplWSDDServiceName = "ProcessImpl";

	public java.lang.String getProcessImplWSDDServiceName() {
		return ProcessImplWSDDServiceName;
	}

	public void setProcessImplWSDDServiceName(java.lang.String name) {
		ProcessImplWSDDServiceName = name;
	}

	public ServiceInterface.ProcessImpl getProcessImpl()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(ProcessImpl_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getProcessImpl(endpoint);
	}

	public ServiceInterface.ProcessImpl getProcessImpl(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			ServiceInterface.ProcessImplSoapBindingStub _stub = new ServiceInterface.ProcessImplSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getProcessImplWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setProcessImplEndpointAddress(java.lang.String address) {
		ProcessImpl_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		try {
			if (ServiceInterface.ProcessImpl.class
					.isAssignableFrom(serviceEndpointInterface)) {
				ServiceInterface.ProcessImplSoapBindingStub _stub = new ServiceInterface.ProcessImplSoapBindingStub(
						new java.net.URL(ProcessImpl_address), this);
				_stub.setPortName(getProcessImplWSDDServiceName());
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
		if ("ProcessImpl".equals(inputPortName)) {
			return getProcessImpl();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://ServiceInterface",
				"ProcessImplService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("http://ServiceInterface",
					"ProcessImpl"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {

		if ("ProcessImpl".equals(portName)) {
			setProcessImplEndpointAddress(address);
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
