package bpm.gateway.core.transformations.outputs;

import java.util.HashMap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SlowChangingDimension1 extends InsertOrUpdate {
	private Integer targetVersionIndex;
	
	public void setDefinition(String definition) {
		this.targetDefinitionSql = definition;
		if (definition.contains(" from ")){
			targetTableName = definition.substring(definition.indexOf(" from ") + 6).trim();
		}
		
		/*
		 * clean all informations because input has changed
		 */
		mappingKeyNames = new HashMap<String, String>();
		mappingFieldNames = new HashMap<String, String>();
		
		targetVersionIndex = null;		
		
		refreshDescriptor();
		fireProperty(PROPERTY_INPUT_CHANGED);
	}
	
	
	/**
	 * 
	 * @return return the Target element index for the Row Version
	 */
	public Integer getTargetVersionIndex() {
		return targetVersionIndex;
	}

	/**
	 * 
	 *set the Target element index for the Row Version
	 */
	public void setTargetVersionIndex(Integer targetVersionIndex) {
		this.targetVersionIndex = targetVersionIndex;
	}
	
	/**
	 * 
	 *set the Target element index for the Row Version
	 */
	public void setTargetVersionIndex(String targetVersionIndex) {
		try{
			this.targetVersionIndex = Integer.parseInt(targetVersionIndex);
		}catch(NumberFormatException e){
			
		}
	}
	
	
	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("slowChangingDimension1");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		
		if (targetServer != null){
			e.addElement("serverRef").setText(targetServer.getName());
		}
		
		if (targetDefinitionSql != null){
			e.addElement("definition").setText(targetDefinitionSql);
		}
		
		if (targetVersionIndex != null){
			e.addElement("targetVersionIndex").setText(targetVersionIndex + "");
		}
		
		for(String input : mappingKeyNames.keySet()){
			Element k = e.addElement("mappingKeyName");
			k.addElement("inputName").setText(input);
			k.addElement("outputName").setText(mappingKeyNames.get(input));
		}
		
		for(String input : mappingFieldNames.keySet()){
			Element k = e.addElement("mappingFieldsName");
			k.addElement("inputName").setText(input);
			k.addElement("outputName").setText(mappingFieldNames.get(input));
		}
			
		if (descriptor != null){
			e.add(descriptor.getElement());
		}
		
		return e;
	}
}
