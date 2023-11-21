package bpm.mdm.model.serialisation;

public class MdmConfiguration {

	public static final String MDM_FILE_SUFFIX = "xml";
	public static final String MDM_ENTITY_FILE_SUFFIX = "store";
	
	private String mdmPersistanceFolderName = "data";

	/**
	 * @return the mdmPersistanceFolderName
	 */
	public String getMdmPersistanceFolderName() {
		return mdmPersistanceFolderName;
	}

	/**
	 * @param mdmPersistanceFolderName the mdmPersistanceFolderName to set
	 */
	public void setMdmPersistanceFolderName(String mdmPersistanceFolderName) {
		this.mdmPersistanceFolderName = mdmPersistanceFolderName;
	}
	
	
	
}
