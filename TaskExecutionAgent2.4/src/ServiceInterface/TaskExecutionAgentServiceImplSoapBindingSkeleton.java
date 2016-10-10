/**
 * TaskExecutionAgentServiceImplSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class TaskExecutionAgentServiceImplSoapBindingSkeleton implements ServiceInterface.TaskExecutionAgentServiceImpl, org.apache.axis.wsdl.Skeleton {
    private ServiceInterface.TaskExecutionAgentServiceImpl impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("normalizationOrderSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "normalizationOrderSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "normalizationOrderSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("normalizationOrderSubmit") == null) {
            _myOperations.put("normalizationOrderSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("normalizationOrderSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("fusionAssimilationOrderSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "fusionAssimilationOrderSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "fusionAssimilationOrderSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("fusionAssimilationOrderSubmit") == null) {
            _myOperations.put("fusionAssimilationOrderSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("fusionAssimilationOrderSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("publicBufferCleanup", _params, new javax.xml.namespace.QName("http://ServiceInterface", "publicBufferCleanupReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "publicBufferCleanup"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("publicBufferCleanup") == null) {
            _myOperations.put("publicBufferCleanup", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("publicBufferCleanup")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getPublicBufferUsedSize", _params, new javax.xml.namespace.QName("http://ServiceInterface", "getPublicBufferUsedSizeReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "getPublicBufferUsedSize"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getPublicBufferUsedSize") == null) {
            _myOperations.put("getPublicBufferUsedSize", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getPublicBufferUsedSize")).add(_oper);
    }

    public TaskExecutionAgentServiceImplSoapBindingSkeleton() {
        this.impl = new ServiceInterface.TaskExecutionAgentServiceImplSoapBindingImpl();
    }

    public TaskExecutionAgentServiceImplSoapBindingSkeleton(ServiceInterface.TaskExecutionAgentServiceImpl impl) {
        this.impl = impl;
    }
    public java.lang.String normalizationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.normalizationOrderSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String fusionAssimilationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.fusionAssimilationOrderSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String publicBufferCleanup(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.publicBufferCleanup(strRequestXML);
        return ret;
    }

    public java.lang.String getPublicBufferUsedSize(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.getPublicBufferUsedSize(strRequestXML);
        return ret;
    }

}
