package DataService;
public class InputParametersProductsClass {
	private String producttag;
	private String productid;
	
	public InputParametersProductsClass(String producttag, String productid){
		this.producttag=producttag;
		this.productid=productid;
	}
	
	public String getProducttag() {
		return producttag;
	}
	public void setProducttag(String producttag) {
		this.producttag=producttag;
	}
	
	public String getProductid() {
		return productid;
	}
	
	public void setProductid(String productid) {
		this.productid=productid;
	}
	
	
}
