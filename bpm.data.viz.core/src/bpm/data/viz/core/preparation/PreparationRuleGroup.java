package bpm.data.viz.core.preparation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PreparationRuleGroup extends PreparationRule {

	private static final long serialVersionUID = 1L;

	private List<Serializable> groups = new ArrayList<>();
	private HashMap<Serializable, Serializable> mappings = new HashMap<>();
	
	public PreparationRuleGroup() {
		type = RuleType.GROUP;
	}

	public HashMap<Serializable, Serializable> getMappings() {
		return mappings;
	}

	public void setMappings(HashMap<Serializable, Serializable> mappings) {
		this.mappings = mappings;
	}

	public List<Serializable> getGroups() {
		return groups;
	}

	public void setGroups(List<Serializable> groups) {
		this.groups = groups;
	}
	
	public List<String> getGroupsAsString() {
		return new ArrayList(groups);
	}
}
