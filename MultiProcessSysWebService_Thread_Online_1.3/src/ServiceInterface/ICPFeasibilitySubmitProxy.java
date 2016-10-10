package ServiceInterface;

public class ICPFeasibilitySubmitProxy implements ICPFeasibilitySubmit {
  private String _endpoint = null;
  private ICPFeasibilitySubmit iCPFeasibilitySubmit = null;
  
  public ICPFeasibilitySubmitProxy() {
    _initICPFeasibilitySubmitProxy();
  }
  
  public ICPFeasibilitySubmitProxy(String endpoint) {
    _endpoint = endpoint;
    _initICPFeasibilitySubmitProxy();
  }
  
  private void _initICPFeasibilitySubmitProxy() {
    try {
      iCPFeasibilitySubmit = (new CPFeasibilitySubmitServiceLocator()).getCPFeasibilitySubmitPort();
      if (iCPFeasibilitySubmit != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iCPFeasibilitySubmit)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iCPFeasibilitySubmit)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iCPFeasibilitySubmit != null)
      ((javax.xml.rpc.Stub)iCPFeasibilitySubmit)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ICPFeasibilitySubmit getICPFeasibilitySubmit() {
    if (iCPFeasibilitySubmit == null)
      _initICPFeasibilitySubmitProxy();
    return iCPFeasibilitySubmit;
  }
  
  public java.lang.String commonProductFeasibilitySubmit(java.lang.String paramXml) throws java.rmi.RemoteException{
    if (iCPFeasibilitySubmit == null)
      _initICPFeasibilitySubmitProxy();
    return iCPFeasibilitySubmit.commonProductFeasibilitySubmit(paramXml);
  }
  
  
}