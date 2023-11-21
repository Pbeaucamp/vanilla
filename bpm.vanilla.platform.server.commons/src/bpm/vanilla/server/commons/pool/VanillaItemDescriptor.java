package bpm.vanilla.server.commons.pool;

public class VanillaItemDescriptor {
	private String itemName;
	private String objectNature;
	private String objectSubType;
	private String modelType;
	
	/**
	 * @param itemName
	 * @param objectNature
	 * @param objectSubType
	 * @param modelType
	 */
	public VanillaItemDescriptor(String itemName,String objectNature, String objectSubType, String modelType) {
		super();
	
		this.itemName = itemName;
		this.objectNature = objectNature;
		this.objectSubType = objectSubType;
		this.modelType = modelType;
	}

	

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @return the objectNature
	 */
	public String getObjectNature() {
		return objectNature;
	}

	/**
	 * @return the objectSubType
	 */
	public String getObjectSubType() {
		return objectSubType;
	}

	/**
	 * @return the modelType
	 */
	public String getModelType() {
		return modelType;
	}
	
	
	
}
