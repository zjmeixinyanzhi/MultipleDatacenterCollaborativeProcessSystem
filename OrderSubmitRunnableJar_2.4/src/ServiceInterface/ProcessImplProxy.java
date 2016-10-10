package ServiceInterface;

public class ProcessImplProxy implements ServiceInterface.ProcessImpl {
  private String _endpoint = null;
  private ServiceInterface.ProcessImpl processImpl = null;
  
  public ProcessImplProxy() {
    _initProcessImplProxy();
  }
  
  public ProcessImplProxy(String endpoint) {
    _endpoint = endpoint;
    _initProcessImplProxy();
  }
  
  private void _initProcessImplProxy() {
    try {
      processImpl = (new ServiceInterface.ProcessImplServiceLocator()).getProcessImpl();
      if (processImpl != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)processImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)processImpl)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (processImpl != null)
      ((javax.xml.rpc.Stub)processImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ServiceInterface.ProcessImpl getProcessImpl() {
    if (processImpl == null)
      _initProcessImplProxy();
    return processImpl;
  }
  
  public java.lang.String dataProductQuery(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (processImpl == null)
      _initProcessImplProxy();
    return processImpl.dataProductQuery(strRequestXML);
  }
  
  public java.lang.String orderRSDataRequirement(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (processImpl == null)
      _initProcessImplProxy();
    return processImpl.orderRSDataRequirement(strRequestXML);
  }
  
  public java.lang.String retrievalOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (processImpl == null)
      _initProcessImplProxy();
    return processImpl.retrievalOrderSubmit(strRequestXML);
  }
  
  public java.lang.String dataProductViewDetail(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (processImpl == null)
      _initProcessImplProxy();
    return processImpl.dataProductViewDetail(strRequestXML);
  }
  
  public java.lang.String getDataResult(java.lang.String taskId) throws java.rmi.RemoteException{
    if (processImpl == null)
      _initProcessImplProxy();
    return processImpl.getDataResult(taskId);
  }
  
  public java.lang.String startDataObtain(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (processImpl == null)
      _initProcessImplProxy();
    return processImpl.startDataObtain(strRequestXML);
  }
  
  
}