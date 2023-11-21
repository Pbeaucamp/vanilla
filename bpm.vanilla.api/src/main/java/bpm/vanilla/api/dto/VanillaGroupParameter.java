package bpm.vanilla.api.dto;

import java.util.ArrayList;
import java.util.List;

public class VanillaGroupParameter {

	private String name;
	private String promptText;
	private String displayName;
	private List<VanillaParameter> parameters = new ArrayList<VanillaParameter>();
	private boolean isCascadingGroup = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<VanillaParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<VanillaParameter> parameters) {
		this.parameters = parameters;
	}

	public boolean isCascadingGroup() {
		return isCascadingGroup;
	}

	public void setCascadingGroup(boolean isCascadingGroup) {
		this.isCascadingGroup = isCascadingGroup;
	}

}
