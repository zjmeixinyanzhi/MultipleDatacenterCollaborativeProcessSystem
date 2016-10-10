package ServiceInterface;

public class TaskExecutionAgentServiceImplProxy implements ServiceInterface.TaskExecutionAgentServiceImpl {
  private String _endpoint = null;
  private ServiceInterface.TaskExecutionAgentServiceImpl taskExecutionAgentServiceImpl = null;
  
  public TaskExecutionAgentServiceImplProxy() {
    _initTaskExecutionAgentServiceImplProxy();
  }
  
  public TaskExecutionAgentServiceImplProxy(String endpoint) {
    _endpoint = endpoint;
    _initTaskExecutionAgentServiceImplProxy();
  }
  
  private void _initTaskExecutionAgentServiceImplProxy() {
    try {
      taskExecutionAgentServiceImpl = (new ServiceInterface.TaskExecutionAgentServiceImplServiceLocator()).getTaskExecutionAgentServiceImpl();
      if (taskExecutionAgentServiceImpl != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)taskExecutionAgentServiceImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)taskExecutionAgentServiceImpl)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (taskExecutionAgentServiceImpl != null)
      ((javax.xml.rpc.Stub)taskExecutionAgentServiceImpl)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ServiceInterface.TaskExecutionAgentServiceImpl getTaskExecutionAgentServiceImpl() {
    if (taskExecutionAgentServiceImpl == null)
      _initTaskExecutionAgentServiceImplProxy();
    return taskExecutionAgentServiceImpl;
  }
  
  public java.lang.String normalizationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (taskExecutionAgentServiceImpl == null)
      _initTaskExecutionAgentServiceImplProxy();
    return taskExecutionAgentServiceImpl.normalizationOrderSubmit(strRequestXML);
  }
  
  public java.lang.String fusionAssimilationOrderSubmit(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (taskExecutionAgentServiceImpl == null)
      _initTaskExecutionAgentServiceImplProxy();
    return taskExecutionAgentServiceImpl.fusionAssimilationOrderSubmit(strRequestXML);
  }
  
  public java.lang.String publicBufferCleanup(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (taskExecutionAgentServiceImpl == null)
      _initTaskExecutionAgentServiceImplProxy();
    return taskExecutionAgentServiceImpl.publicBufferCleanup(strRequestXML);
  }
  
  public java.lang.String getPublicBufferUsedSize(java.lang.String strRequestXML) throws java.rmi.RemoteException{
    if (taskExecutionAgentServiceImpl == null)
      _initTaskExecutionAgentServiceImplProxy();
    return taskExecutionAgentServiceImpl.getPublicBufferUsedSize(strRequestXML);
  }
  
  
}