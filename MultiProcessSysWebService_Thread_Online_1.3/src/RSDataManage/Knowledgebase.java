package RSDataManage;

/**
 * KnowledgebaseId entity. @author MyEclipse Persistence Tools
 */

public class Knowledgebase implements java.io.Serializable {

	// Fields

	private String productId;
	private String productIdname;
	private String spaceRange;
	private String timeRange;
	private String inputParametersData;
	private String inputParametersProducts;

	// Constructors

	/** default constructor */
	public Knowledgebase() {
	}

	/** minimal constructor */
	public Knowledgebase(String productId) {
		this.productId = productId;
	}

	/** full constructor */
	public Knowledgebase(String productId, String productIdname,
			String spaceRange, String timeRange, String inputParametersData,
			String inputParametersProducts) {
		this.productId = productId;
		this.productIdname = productIdname;
		this.spaceRange = spaceRange;
		this.timeRange = timeRange;
		this.inputParametersData = inputParametersData;
		this.inputParametersProducts = inputParametersProducts;
	}

	// Property accessors

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductIdname() {
		return this.productIdname;
	}

	public void setProductIdname(String productIdname) {
		this.productIdname = productIdname;
	}

	public String getSpaceRange() {
		return this.spaceRange;
	}

	public void setSpaceRange(String spaceRange) {
		this.spaceRange = spaceRange;
	}

	public String getTimeRange() {
		return this.timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}

	public String getInputParametersData() {
		return this.inputParametersData;
	}

	public void setInputParametersData(String inputParametersData) {
		this.inputParametersData = inputParametersData;
	}

	public String getInputParametersProducts() {
		return this.inputParametersProducts;
	}

	public void setInputParametersProducts(String inputParametersProducts) {
		this.inputParametersProducts = inputParametersProducts;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Knowledgebase))
			return false;
		Knowledgebase castOther = (Knowledgebase) other;

		return ((this.getProductId() == castOther.getProductId()) || (this
				.getProductId() != null && castOther.getProductId() != null && this
				.getProductId().equals(castOther.getProductId())))
				&& ((this.getProductIdname() == castOther.getProductIdname()) || (this
						.getProductIdname() != null
						&& castOther.getProductIdname() != null && this
						.getProductIdname()
						.equals(castOther.getProductIdname())))
				&& ((this.getSpaceRange() == castOther.getSpaceRange()) || (this
						.getSpaceRange() != null
						&& castOther.getSpaceRange() != null && this
						.getSpaceRange().equals(castOther.getSpaceRange())))
				&& ((this.getTimeRange() == castOther.getTimeRange()) || (this
						.getTimeRange() != null
						&& castOther.getTimeRange() != null && this
						.getTimeRange().equals(castOther.getTimeRange())))
				&& ((this.getInputParametersData() == castOther
						.getInputParametersData()) || (this
						.getInputParametersData() != null
						&& castOther.getInputParametersData() != null && this
						.getInputParametersData().equals(
								castOther.getInputParametersData())))
				&& ((this.getInputParametersProducts() == castOther
						.getInputParametersProducts()) || (this
						.getInputParametersProducts() != null
						&& castOther.getInputParametersProducts() != null && this
						.getInputParametersProducts().equals(
								castOther.getInputParametersProducts())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getProductId() == null ? 0 : this.getProductId().hashCode());
		result = 37
				* result
				+ (getProductIdname() == null ? 0 : this.getProductIdname()
						.hashCode());
		result = 37
				* result
				+ (getSpaceRange() == null ? 0 : this.getSpaceRange()
						.hashCode());
		result = 37 * result
				+ (getTimeRange() == null ? 0 : this.getTimeRange().hashCode());
		result = 37
				* result
				+ (getInputParametersData() == null ? 0 : this
						.getInputParametersData().hashCode());
		result = 37
				* result
				+ (getInputParametersProducts() == null ? 0 : this
						.getInputParametersProducts().hashCode());
		return result;
	}

}