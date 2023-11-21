package bpm.oda.driver.reader.wizards.gateway;

public class GatewayBean {
	private String stepOutputType;
	private String outputFileName;
	
	private String destinationName;
	private Object destinattionFolder;
	private String destinationType = "FileSystem";
	private Object storageDestinationFolder;
	private String groupName;
	
	private boolean isDelete = true;
	
	/**
	 * @param stepOutputType the stepOutputType to set
	 */
	public void setStepOutputType(String stepOutputType) {
		this.stepOutputType = stepOutputType;
	}
	
	/**
	 * @param outputFileName the outputFileName to set
	 */
	public void setStepOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
	/**
	 * @param destinationName the destinationName to set
	 */
	public void setStorageDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	/**
	 * @param destinattionFolder the destinattionFolder to set
	 */
	public void setStorageDestinationFolder(Object destinattionFolder) {
		this.destinattionFolder = destinattionFolder;
	}
	/**
	 * @param destinationType the destinationType to set
	 */
	public void setStorageDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}
	/**
	 * @return the stepOutputType
	 */
	public String getStepOutputType() {
		return stepOutputType;
	}
	/**
	 * @return the outputFileName
	 */
	public String getStepOutputFileName() {
		return outputFileName;
	}
	/**
	 * @return the destinationName
	 */
	public String getStorageDestinationName() {
		return destinationName;
	}
	/**
	 * @return the destinattionFolder
	 */
	public Object getStorageDestinattionFolder() {
		return storageDestinationFolder;
	}
	/**
	 * @return the destinationType
	 */
	public String getStorageDestinationType() {
		return destinationType;
	}

	public void setStorageDestinattionFolder(Object folderObject) {
		storageDestinationFolder = folderObject;
		
	}

	public Object getStepDestinationFolder() {
		return storageDestinationFolder;
	}

	public void setDeleteFile(boolean selection) {
		isDelete = selection;
		
	}
	
	public boolean getDeleteFile(){
		return isDelete;
	}

	public void setGroupName(String text) {
		this.groupName = text;
		
	}
	
	public String getGroupName() {
		return this.groupName ;
		
	}
	
}
