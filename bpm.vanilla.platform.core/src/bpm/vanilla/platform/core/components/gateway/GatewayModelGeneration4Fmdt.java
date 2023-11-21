package bpm.vanilla.platform.core.components.gateway;

import java.util.List;

public class GatewayModelGeneration4Fmdt {
	
	private String transformationName;
	private int fmdtRepositoryId;
	private int fmdtDirectoryItemId;
	private String fmdtBusinessModelName;
	private String fmdtBusinessPackageName;
	private List<String> fmdtBusinessTableNames;
	private int groupId;
	private String outputType;
	private String encoding;
	private String runtimeUrl;
	
	/**
	 * 
	 * @param transformationName
	 * @param fmdtRepositoryId
	 * @param fmdtDirectoryItemId
	 * @param fmdtBusinessModelName
	 * @param fmdtBusinessPackageName
	 * @param fmdtBusinessTableNames
	 * @param groupId
	 * @param outputType : CSV, XLS or XML, if neither of this values is set, CSV will be 
	 * returned as the default value
	 * @param encoding
	 */
	public GatewayModelGeneration4Fmdt(String transformationName,
			int fmdtRepositoryId, int fmdtDirectoryItemId,
			String fmdtBusinessModelName, String fmdtBusinessPackageName,
			List<String> fmdtBusinessTableNames, int groupId, String outputType, String encoding) {
		super();
		this.transformationName = transformationName;
		this.fmdtRepositoryId = fmdtRepositoryId;
		this.fmdtDirectoryItemId = fmdtDirectoryItemId;
		this.fmdtBusinessModelName = fmdtBusinessModelName;
		this.fmdtBusinessPackageName = fmdtBusinessPackageName;
		this.fmdtBusinessTableNames = fmdtBusinessTableNames;
		this.groupId = groupId;
		this.encoding = encoding;
		this.outputType = outputType;
	}

	/**
	 * @return the outputType
	 */
	public String getOutputType() {
		if (!("CSV".equals(outputType) || "XLS".equals(outputType) || "XML".equals(outputType))){
			return "CSV";
		}
		return outputType;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		if (encoding == null){
			return "UTF-8";
		}
		
		return encoding;
	}

	public String getTransformationName(){
		return transformationName;
	}
	
	public int getFmdtRepositoryId(){
		return fmdtRepositoryId;
	}
	
	public int getFmdtDirectoryItemId(){
		return fmdtDirectoryItemId;
	}
	
	public String getFmdtBusinessModelName(){
		return fmdtBusinessModelName;
	}
	public String getFmdtBusinessPackageName(){
		return fmdtBusinessPackageName;
	}
	public List<String> getFmdtBusinessTableNames(){
		return fmdtBusinessTableNames;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setRuntimeUrl(String runtimeUrl) {
		this.runtimeUrl = runtimeUrl;
	}

	public String getRuntimeUrl() {
		return runtimeUrl;
	}
	

}
