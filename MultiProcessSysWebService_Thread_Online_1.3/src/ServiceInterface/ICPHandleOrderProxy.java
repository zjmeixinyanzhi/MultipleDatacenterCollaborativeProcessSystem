package ServiceInterface;

public class ICPHandleOrderProxy implements ICPHandleOrder {
  private String _endpoint = null;
  private ICPHandleOrder iCPHandleOrder = null;
  
  public ICPHandleOrderProxy() {
    _initICPHandleOrderProxy();
  }
  
  public ICPHandleOrderProxy(String endpoint) {
    _endpoint = endpoint;
    _initICPHandleOrderProxy();
  }
  
  private void _initICPHandleOrderProxy() {
    try {
      iCPHandleOrder = (new CPHandleOrderServiceLocator()).getCPHandleOrderPort();
      if (iCPHandleOrder != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iCPHandleOrder)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iCPHandleOrder)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iCPHandleOrder != null)
      ((javax.xml.rpc.Stub)iCPHandleOrder)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ICPHandleOrder getICPHandleOrder() {
    if (iCPHandleOrder == null)
      _initICPHandleOrderProxy();
    return iCPHandleOrder;
  }
  
  public java.lang.String handleProductOrder(java.lang.String paramXml) throws java.rmi.RemoteException{
    if (iCPHandleOrder == null)
      _initICPHandleOrderProxy();
    return iCPHandleOrder.handleProductOrder(paramXml);
  }
  
  
}