package ServiceInterface;

public class ICPStateFeedbackProxy implements ICPStateFeedback {
  private String _endpoint = null;
  private ICPStateFeedback iCPStateFeedback = null;
  
  public ICPStateFeedbackProxy() {
    _initICPStateFeedbackProxy();
  }
  
  public ICPStateFeedbackProxy(String endpoint) {
    _endpoint = endpoint;
    _initICPStateFeedbackProxy();
  }
  
  private void _initICPStateFeedbackProxy() {
    try {
      iCPStateFeedback = (new CPStateFeedbackServiceLocator()).getCPStateFeedbackPort();
      if (iCPStateFeedback != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iCPStateFeedback)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iCPStateFeedback)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iCPStateFeedback != null)
      ((javax.xml.rpc.Stub)iCPStateFeedback)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ICPStateFeedback getICPStateFeedback() {
    if (iCPStateFeedback == null)
      _initICPStateFeedbackProxy();
    return iCPStateFeedback;
  }
  
  public java.lang.String commonProductStateFeedback(java.lang.String paramXml) throws java.rmi.RemoteException{
    if (iCPStateFeedback == null)
      _initICPStateFeedbackProxy();
    return iCPStateFeedback.commonProductStateFeedback(paramXml);
  }
  
  
}