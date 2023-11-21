package bpm.vanilla.platform.core.beans.disco;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DiscoReportConfiguration {

	private RepositoryItem item;
	private Group group;
	private List<VanillaGroupParameter> selectedParameters;
	private List<String> selectedFormats;

	public void setSelectedFormats(List<String> selectedFormats) {
		this.selectedFormats = selectedFormats;
	}

	public List<String> getSelectedFormats() {
		return selectedFormats;
	}

	public void addSelectedFormat(String selectedFormat) {
		if (selectedFormats == null) {
			selectedFormats = new ArrayList<String>();
		}
		this.selectedFormats.add(selectedFormat);
	}

	public void setSelectedParameters(List<VanillaGroupParameter> selectedParameters) {
		this.selectedParameters = selectedParameters;
	}

	public List<VanillaGroupParameter> getSelectedParameters() {
		return selectedParameters;
	}

	public void setItem(RepositoryItem item) {
		this.item = item;
	}

	public RepositoryItem getItem() {
		return item;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Group getGroup() {
		return group;
	}

	public String getParamDefinition() {
		if(selectedParameters == null){
			return "";
		}
		
		StringBuilder buf = new StringBuilder();
		for(VanillaGroupParameter groupParam : selectedParameters){
			if(groupParam.getParameters() != null){
				boolean first = true;
				for(VanillaParameter param : groupParam.getParameters()){
					if(first){
						buf.append(param.getName() + " = " + param.getSelectedValues().toString());
						first = false;
					}
					else {
						buf.append("; " + param.getName() + " = " + param.getSelectedValues().toString());
					}
				}
			}
		}
		return buf.toString();
	}

	public String getFormatDefinition() {
		if(selectedFormats == null){
			return "";
		}
		
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		for(String format : selectedFormats){
			if(first){
				buf.append(format);
				first = false;
			}
			else {
				buf.append(", " + format);
			}
		}
		return buf.toString();
	}
}
