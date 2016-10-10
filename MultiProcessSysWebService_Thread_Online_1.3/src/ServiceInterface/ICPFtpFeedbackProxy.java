package ServiceInterface;

public class ICPFtpFeedbackProxy implements ICPFtpFeedback {
  private String _endpoint = null;
  private ICPFtpFeedback iCPFtpFeedback = null;
  
  public ICPFtpFeedbackProxy() {
    _initICPFtpFeedbackProxy();
  }
  
  public ICPFtpFeedbackProxy(String endpoint) {
    _endpoint = endpoint;
    _initICPFtpFeedbackProxy();
  }
  
  private void _initICPFtpFeedbackProxy() {
    try {
      iCPFtpFeedback = (new CPFtpFeedbackServiceLocator()).getCPFtpFeedbackPort();
      if (iCPFtpFeedback != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iCPFtpFeedback)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iCPFtpFeedback)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iCPFtpFeedback != null)
      ((javax.xml.rpc.Stub)iCPFtpFeedback)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ICPFtpFeedback getICPFtpFeedback() {
    if (iCPFtpFeedback == null)
      _initICPFtpFeedbackProxy();
    return iCPFtpFeedback;
  }
  
  public java.lang.String commonProductSubmit(java.lang.String paramXml) throws java.rmi.RemoteException{
    if (iCPFtpFeedback == null)
      _initICPFtpFeedbackProxy();
    return iCPFtpFeedback.commonProductSubmit(paramXml);
  }
  
  
}