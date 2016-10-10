/**
 * CPHandleOrderServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class CPHandleOrderServiceLocator extends org.apache.axis.client.Service implements CPHandleOrderService {

    public CPHandleOrderServiceLocator() {
    }


    public CPHandleOrderServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CPHandleOrderServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CPHandleOrderPort
    private java.lang.String CPHandleOrderPort_address = "http://219.237.222.107:7080/crsan/services/CPHandleOrderService";

    public java.lang.String getCPHandleOrderPortAddress() {
        return CPHandleOrderPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CPHandleOrderPortWSDDServiceName = "CPHandleOrderPort";

    public java.lang.String getCPHandleOrderPortWSDDServiceName() {
        return CPHandleOrderPortWSDDServiceName;
    }

    public void setCPHandleOrderPortWSDDServiceName(java.lang.String name) {
        CPHandleOrderPortWSDDServiceName = name;
    }

    public ICPHandleOrder getCPHandleOrderPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CPHandleOrderPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCPHandleOrderPort(endpoint);
    }

    public ICPHandleOrder getCPHandleOrderPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            CPHandleOrderServiceSoapBindingStub _stub = new CPHandleOrderServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getCPHandleOrderPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCPHandleOrderPortEndpointAddress(java.lang.String address) {
        CPHandleOrderPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ICPHandleOrder.class.isAssignableFrom(serviceEndpointInterface)) {
                CPHandleOrderServiceSoapBindingStub _stub = new CPHandleOrderServiceSoapBindingStub(new java.net.URL(CPHandleOrderPort_address), this);
                _stub.setPortName(getCPHandleOrderPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CPHandleOrderPort".equals(inputPortName)) {
            return getCPHandleOrderPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://common.order.connector.dataservice.gov.org/", "CPHandleOrderService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://common.order.connector.dataservice.gov.org/", "CPHandleOrderPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CPHandleOrderPort".equals(portName)) {
            setCPHandleOrderPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
