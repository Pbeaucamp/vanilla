package bpm.fwr.api.beans.dataset;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;

@SuppressWarnings("serial")
public class FwrPrompt implements IResource {

	private String name;
	private String question = "";
	private String operator;

	private boolean isChildPrompt;
	private String parentPromptName;
	private String originDataStreamName;

	private int type = VanillaParameter.TEXT_BOX;
	private String paramType = VanillaParameter.PARAM_TYPE_SIMPLE;

	private DataSet dataset;

	// Metadata informations
	private int metadataId;
	private String packageParent;
	private String modelParent;

	private List<String> values;
	private List<String> selectedValues;

	public FwrPrompt() {
		super();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * VanillaParameter.TEXT_BOX VanillaParameter.LIST_BOX
	 * 
	 * @return
	 */
	public int getType() {
		return type;
	}

	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}

	public DataSet getDataset() {
		return dataset;
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
	}

	public int getMetadataId() {
		return metadataId;
	}

	public void setPackageParent(String packageParent) {
		this.packageParent = packageParent;
	}

	public String getPackageParent() {
		return packageParent;
	}

	public void setModelParent(String modelParent) {
		this.modelParent = modelParent;
	}

	public String getModelParent() {
		return modelParent;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public List<String> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setChildPrompt(boolean isChildPrompt) {
		this.isChildPrompt = isChildPrompt;
	}

	public boolean isChildPrompt() {
		return isChildPrompt;
	}

	public void setParentPromptName(String parentPromptName) {
		this.parentPromptName = parentPromptName;
	}

	public String getParentPromptName() {
		return parentPromptName;
	}

	public void setOriginDataStreamName(String originDataStreamName) {
		this.originDataStreamName = originDataStreamName;
	}

	public String getOriginDataStreamName() {
		return originDataStreamName;
	}

	public void initSelectedValues(String selectedValue) {
		this.selectedValues = new ArrayList<String>();
		this.selectedValues.add(selectedValue);
	}

	public void setSelectedValues(List<String> selectedValues) {
		this.selectedValues = selectedValues;
	}

	public void addSelectedValues(String selectedValue) {
		if (selectedValues == null) {
			this.selectedValues = new ArrayList<String>();
		}
		this.selectedValues.add(selectedValue);
	}

	public String getSelectedValuesToString(boolean multiple) {
		StringBuffer buf = new StringBuffer();
		if (selectedValues != null && multiple) {
			for (int i = 0; i < selectedValues.size(); i++) {
				if (i == 0) {
					buf.append(selectedValues.get(i));
				}
				else {
					buf.append("," + selectedValues.get(i));
				}
			}
		}
		else if(selectedValues != null && !selectedValues.isEmpty()) {
			buf.append(selectedValues.get(0));
		}
		return buf.toString();
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

	/**
	 * VanillaParameter.PARAM_TYPE_SIMPLE VanillaParameter.PARAM_TYPE_MULTI
	 * 
	 * @return
	 */
	public String getParamType() {
		return paramType;
	}

	public List<String> getSelectedValues() {
		return selectedValues != null ? selectedValues : new ArrayList<String>();
	}
}
