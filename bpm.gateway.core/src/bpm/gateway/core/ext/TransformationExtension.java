package bpm.gateway.core.ext;

import bpm.gateway.core.AbrstractDigesterTransformation;

public class TransformationExtension {
	private Class<? extends AbrstractDigesterTransformation> trClass;
	private String transformationDefinitionName;
	private String transformationDefinitionDescription;
	private String category;
	private AbrstractDigesterTransformation callbackDigester;
	
	public Class<? extends AbrstractDigesterTransformation> getTrClass() {
		return trClass;
	}
	public void setTrClass(Class<? extends AbrstractDigesterTransformation> trClass) {
		this.trClass = trClass;
	}
	public String getTransformationDefinitionName() {
		return transformationDefinitionName;
	}
	public void setTransformationDefinitionName(String transformationDefinitionName) {
		this.transformationDefinitionName = transformationDefinitionName;
	}
	public String getTransformationDefinitionDescription() {
		return transformationDefinitionDescription;
	}
	public void setTransformationDefinitionDescription(
			String transformationDefinitionDescription) {
		this.transformationDefinitionDescription = transformationDefinitionDescription;
	}
	
	public AbrstractDigesterTransformation getCallbackDigester() {
		return callbackDigester;
	}
	public void setCallbackDigester(AbrstractDigesterTransformation callbackDigester) {
		this.callbackDigester = callbackDigester;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TransformationExtension)){
			return false;
		}
		
		TransformationExtension ext = (TransformationExtension)obj;
		
		if (ext.getTransformationDefinitionName().equals(getTransformationDefinitionName()) &&
			ext.getTrClass().equals(getTrClass())){
			return true;
		}
		
		return false;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	
	
	
}

