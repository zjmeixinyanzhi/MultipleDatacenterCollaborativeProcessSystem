package DataService;
public class InputParametersDataClass {
	private String inputdatatype;
	private String satellite;
	private String sensor;
	private String productweight;
	
	public InputParametersDataClass(String inputdatatype, String satellite, String sensor, String productweight){
		this.inputdatatype=inputdatatype;
		this.satellite=satellite;
		this.sensor=sensor;
		this.productweight=productweight;
	}
	
	public String getInputdatatype() {
		return inputdatatype;
	}
	public void setInputdatatype(String inputdatatype) {
		this.inputdatatype=inputdatatype;
	}
	
	public String getSatellite() {
		return satellite;
	}
	
	public void setSatellite(String satellite) {
		this.satellite=satellite;
	}
	
	public String getSensor() {
		return sensor;
	}
	
	public void setSensor(String sensor) {
		this.sensor=sensor;
	}
	
	public String getProductweight() {
		return productweight;
	}
	
	public void setProductweight(String productweight) {
		this.productweight=productweight;
	}

}