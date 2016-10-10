package ServiceInterface;

public class ServiceImplProxy implements ServiceInterface.ServiceImpl {
  private String _endpoint = null;
  private ServiceInterface.ServiceImpl serviceImpl = null;
  
  public ServiceImplProxy() {
    _initServiceImplProxy();
  }
  
  public ServiceImplProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiceImplProxy();
  }
  
  private void _initServiceImplProxy() {
    try {
      serviceImpl = (new ServiceInterface.ServiceImplServiceLocator()).getServiceImpl();
      if (serviceImpl != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)serviceImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)serviceImpl)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (serviceImpl != null)
      ((javax.xml.rpc.Stub)serviceImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ServiceInterface.ServiceImpl getServiceImpl() {
    if (serviceImpl == null)
      _initServiceImplProxy();
    return serviceImpl;
  }
  
  public java.lang.String taskStatus(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (serviceImpl == null)
      _initServiceImplProxy();
    return serviceImpl.taskStatus(strRequestXML);
  }
  
  public java.lang.String orderRSDataPlan(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (serviceImpl == null)
      _initServiceImplProxy();
    return serviceImpl.orderRSDataPlan(strRequestXML);
  }
  
  public java.lang.String commonProductRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (serviceImpl == null)
      _initServiceImplProxy();
    return serviceImpl.commonProductRequireSubmit(strRequestXML);
  }
  
  public java.lang.String faProductRequireSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (serviceImpl == null)
      _initServiceImplProxy();
    return serviceImpl.faProductRequireSubmit(strRequestXML);
  }
  
  public java.lang.String dataProductSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (serviceImpl == null)
      _initServiceImplProxy();
    return serviceImpl.dataProductSubmit(strRequestXML);
  }
  
  public java.lang.String commonProductOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (serviceImpl == null)
      _initServiceImplProxy();
    return serviceImpl.commonProductOrderSubmit(strRequestXML);
  }
  
  
}