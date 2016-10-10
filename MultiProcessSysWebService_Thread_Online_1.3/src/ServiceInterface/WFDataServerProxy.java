package ServiceInterface;

public class WFDataServerProxy implements ServiceInterface.WFDataServer {
	private String _endpoint = null;
	private ServiceInterface.WFDataServer wFDataServer = null;

	public WFDataServerProxy() {
		_initWFDataServerProxy();
	}

	public WFDataServerProxy(String endpoint) {
		_endpoint = endpoint;
		_initWFDataServerProxy();
	}

	private void _initWFDataServerProxy() {
		try {
			wFDataServer = (new ServiceInterface.WFDataServerServiceLocator()).getWFDataServer();
			if (wFDataServer != null) {
				if (_endpoint != null)
					((javax.xml.rpc.Stub) wFDataServer)._setProperty("javax.xml.rpc.service.endpoint.address",
							_endpoint);
				else
					_endpoint = (String) ((javax.xml.rpc.Stub) wFDataServer)
							._getProperty("javax.xml.rpc.service.endpoint.address");
			}

		} catch (javax.xml.rpc.ServiceException serviceException) {
		}
	}

	public String getEndpoint() {
		return _endpoint;
	}

	public void setEndpoint(String endpoint) {
		_endpoint = endpoint;
		if (wFDataServer != null)
			((javax.xml.rpc.Stub) wFDataServer)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

	}

	public ServiceInterface.WFDataServer getWFDataServer() {
		if (wFDataServer == null)
			_initWFDataServerProxy();
		return wFDataServer;
	}

	public java.lang.String startQuery(java.lang.String request) throws java.rmi.RemoteException {
		if (wFDataServer == null)
			_initWFDataServerProxy();
		return wFDataServer.startQuery(request);
	}

	public java.lang.String getDataDescription(java.lang.String request) throws java.rmi.RemoteException {
		if (wFDataServer == null)
			_initWFDataServerProxy();
		return wFDataServer.getDataDescription(request);
	}

	public int closeQuery(java.lang.String request) throws java.rmi.RemoteException {
		if (wFDataServer == null)
			_initWFDataServerProxy();
		return wFDataServer.closeQuery(request);
	}

	public java.lang.String startDataObtain(java.lang.String request) throws java.rmi.RemoteException {
		if (wFDataServer == null)
			_initWFDataServerProxy();
		return wFDataServer.startDataObtain(request);
	}

	public java.lang.String getDataResult(java.lang.String request) throws java.rmi.RemoteException {
		if (wFDataServer == null)
			_initWFDataServerProxy();
		return wFDataServer.getDataResult(request);
	}

}