package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataPrompt extends MetadataResource {
	
	private String promptType;
	private List<String> values;
	private List<String> selectedValues;
	private String operator;
	
	public MetadataPrompt() {
		
	}
	
	public MetadataPrompt(RepositoryItem it,IBusinessPackage businessPackage,String catName,Prompt prompt) throws Exception {
		super(it,businessPackage,catName,"MetadataPrompt",prompt);
		this.promptType = "List Box";
		this.values = prompt.getOrigin().getDistinctValues();
		this.selectedValues = new ArrayList<>();
		if(this.values.size() > 0) {
			this.selectedValues.add(this.values.get(0));
		}
		this.operator = prompt.getOperator();
	}

	public MetadataPrompt(String id, String name, int metadataID, String type, String businessModelName, String businessPackageName, String promptType,List<String> values,List<String> selectedValues) {
		super(id, name, metadataID, type, businessModelName, businessPackageName);
		this.promptType = promptType;
		this.values = values;
		this.selectedValues = selectedValues;
	}
	
	public MetadataPrompt(FwrPrompt prompt) {
		super();
		this.type = "MetadataPrompt";
		this.name = prompt.getName();
		this.businessModelName = prompt.getModelParent();
		this.businessPackageName = prompt.getPackageParent();
		this.metadataID = prompt.getMetadataId();
		this.id = this.metadataID + "." + this.businessModelName + "." + this.businessPackageName + ".Resources.Prompts." + this.name;
		
		switch(prompt.getType()) {
		case VanillaParameter.TEXT_BOX:
			this.promptType = "Text Box";
			break;
		case VanillaParameter.LIST_BOX:
			this.promptType = "List Box";
		}
		
		this.values = prompt.getValues();
		this.selectedValues = prompt.getSelectedValues();
	}
	
	public String getPromptType() {
		return promptType;
	}

	public List<String> getValues() {
		return values;
	}

	public List<String> getSelectedValues() {
		return selectedValues;
	}

	public String getOperator() {
		return operator;
	}

	public FwrPrompt createComponentPrompt() {
		FwrPrompt prompt = new FwrPrompt();
		
		prompt.setName(this.name);
		prompt.setMetadataId(this.metadataID);
		prompt.setModelParent(this.businessModelName);
		prompt.setPackageParent(this.businessPackageName);
		
		switch(this.promptType) {
		case "Text Box":
			prompt.setType(VanillaParameter.TEXT_BOX);
			break;
		case "List Box":
			prompt.setType(VanillaParameter.LIST_BOX);
			break;
		}
		
		prompt.setValues(values);
		prompt.setSelectedValues(selectedValues);
		prompt.setOperator(operator);
		
		return prompt;
	}
}
