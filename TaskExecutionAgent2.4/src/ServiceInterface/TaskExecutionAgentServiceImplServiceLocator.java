/**
 * TaskExecutionAgentServiceImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class TaskExecutionAgentServiceImplServiceLocator extends org.apache.axis.client.Service implements ServiceInterface.TaskExecutionAgentServiceImplService {

    public TaskExecutionAgentServiceImplServiceLocator() {
    }


    public TaskExecutionAgentServiceImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TaskExecutionAgentServiceImplServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TaskExecutionAgentServiceImpl
    private java.lang.String TaskExecutionAgentServiceImpl_address = "http://localhost:13080/TaskExecutionAgentWebservice/services/TaskExecutionAgentServiceImpl";

    public java.lang.String getTaskExecutionAgentServiceImplAddress() {
        return TaskExecutionAgentServiceImpl_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TaskExecutionAgentServiceImplWSDDServiceName = "TaskExecutionAgentServiceImpl";

    public java.lang.String getTaskExecutionAgentServiceImplWSDDServiceName() {
        return TaskExecutionAgentServiceImplWSDDServiceName;
    }

    public void setTaskExecutionAgentServiceImplWSDDServiceName(java.lang.String name) {
        TaskExecutionAgentServiceImplWSDDServiceName = name;
    }

    public ServiceInterface.TaskExecutionAgentServiceImpl getTaskExecutionAgentServiceImpl() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TaskExecutionAgentServiceImpl_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTaskExecutionAgentServiceImpl(endpoint);
    }

    public ServiceInterface.TaskExecutionAgentServiceImpl getTaskExecutionAgentServiceImpl(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ServiceInterface.TaskExecutionAgentServiceImplSoapBindingStub _stub = new ServiceInterface.TaskExecutionAgentServiceImplSoapBindingStub(portAddress, this);
            _stub.setPortName(getTaskExecutionAgentServiceImplWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTaskExecutionAgentServiceImplEndpointAddress(java.lang.String address) {
        TaskExecutionAgentServiceImpl_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ServiceInterface.TaskExecutionAgentServiceImpl.class.isAssignableFrom(serviceEndpointInterface)) {
                ServiceInterface.TaskExecutionAgentServiceImplSoapBindingStub _stub = new ServiceInterface.TaskExecutionAgentServiceImplSoapBindingStub(new java.net.URL(TaskExecutionAgentServiceImpl_address), this);
                _stub.setPortName(getTaskExecutionAgentServiceImplWSDDServiceName());
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
        if ("TaskExecutionAgentServiceImpl".equals(inputPortName)) {
            return getTaskExecutionAgentServiceImpl();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ServiceInterface", "TaskExecutionAgentServiceImplService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ServiceInterface", "TaskExecutionAgentServiceImpl"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TaskExecutionAgentServiceImpl".equals(portName)) {
            setTaskExecutionAgentServiceImplEndpointAddress(address);
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
