/**
 * ServiceImplSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ServiceInterface;

public class ServiceImplSoapBindingSkeleton implements ServiceInterface.ServiceImpl, org.apache.axis.wsdl.Skeleton {
    private ServiceInterface.ServiceImpl impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequesXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("orderRSDataPlan", _params, new javax.xml.namespace.QName("http://ServiceInterface", "orderRSDataPlanReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "orderRSDataPlan"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("orderRSDataPlan") == null) {
            _myOperations.put("orderRSDataPlan", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("orderRSDataPlan")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequesIP"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("systemMonitorInfo", _params, new javax.xml.namespace.QName("http://ServiceInterface", "systemMonitorInfoReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "systemMonitorInfo"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("systemMonitorInfo") == null) {
            _myOperations.put("systemMonitorInfo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("systemMonitorInfo")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("commonProductRequireSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "commonProductRequireSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "commonProductRequireSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("commonProductRequireSubmit") == null) {
            _myOperations.put("commonProductRequireSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("commonProductRequireSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "ServiceType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("rnDataService", _params, new javax.xml.namespace.QName("http://ServiceInterface", "rnDataServiceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "rnDataService"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("rnDataService") == null) {
            _myOperations.put("rnDataService", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("rnDataService")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("validationOrderSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "validationOrderSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "validationOrderSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("validationOrderSubmit") == null) {
            _myOperations.put("validationOrderSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("validationOrderSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("dataProductSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "dataProductSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "dataProductSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("dataProductSubmit") == null) {
            _myOperations.put("dataProductSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("dataProductSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("commonProductOrderSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "commonProductOrderSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "commonProductOrderSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("commonProductOrderSubmit") == null) {
            _myOperations.put("commonProductOrderSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("commonProductOrderSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("faProducRequireSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "faProducRequireSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "faProducRequireSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("faProducRequireSubmit") == null) {
            _myOperations.put("faProducRequireSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("faProducRequireSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("dataProductQuery", _params, new javax.xml.namespace.QName("http://ServiceInterface", "dataProductQueryReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "dataProductQuery"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("dataProductQuery") == null) {
            _myOperations.put("dataProductQuery", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("dataProductQuery")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("faProductRequireSubmit", _params, new javax.xml.namespace.QName("http://ServiceInterface", "faProductRequireSubmitReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "faProductRequireSubmit"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("faProductRequireSubmit") == null) {
            _myOperations.put("faProductRequireSubmit", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("faProductRequireSubmit")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("dataProductViewDetail", _params, new javax.xml.namespace.QName("http://ServiceInterface", "dataProductViewDetailReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "dataProductViewDetail"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("dataProductViewDetail") == null) {
            _myOperations.put("dataProductViewDetail", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("dataProductViewDetail")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("dataPrdouctQuery", _params, new javax.xml.namespace.QName("http://ServiceInterface", "dataPrdouctQueryReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "dataPrdouctQuery"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("dataPrdouctQuery") == null) {
            _myOperations.put("dataPrdouctQuery", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("dataPrdouctQuery")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://ServiceInterface", "strRequestXML"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("taskStatus", _params, new javax.xml.namespace.QName("http://ServiceInterface", "taskStatusReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://ServiceInterface", "taskStatus"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("taskStatus") == null) {
            _myOperations.put("taskStatus", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("taskStatus")).add(_oper);
    }

    public ServiceImplSoapBindingSkeleton() {
        this.impl = new ServiceInterface.ServiceImplSoapBindingImpl();
    }

    public ServiceImplSoapBindingSkeleton(ServiceInterface.ServiceImpl impl) {
        this.impl = impl;
    }
    public java.lang.String orderRSDataPlan(java.lang.String strRequesXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.orderRSDataPlan(strRequesXML);
        return ret;
    }

    public java.lang.String systemMonitorInfo(java.lang.String strRequesIP) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.systemMonitorInfo(strRequesIP);
        return ret;
    }

    public java.lang.String commonProductRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.commonProductRequireSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String rnDataService(java.lang.String strRequestXML, java.lang.String serviceType) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.rnDataService(strRequestXML, serviceType);
        return ret;
    }

    public java.lang.String validationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.validationOrderSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String dataProductSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.dataProductSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String commonProductOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.commonProductOrderSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String faProducRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.faProducRequireSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String dataProductQuery(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.dataProductQuery(strRequestXML);
        return ret;
    }

    public java.lang.String faProductRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.faProductRequireSubmit(strRequestXML);
        return ret;
    }

    public java.lang.String dataProductViewDetail(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.dataProductViewDetail(strRequestXML);
        return ret;
    }

    public java.lang.String dataPrdouctQuery(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.dataPrdouctQuery(strRequestXML);
        return ret;
    }

    public java.lang.String taskStatus(java.lang.String strRequestXML) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.taskStatus(strRequestXML);
        return ret;
    }

}
