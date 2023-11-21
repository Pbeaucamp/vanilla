package bpm.vanilla.api.core.model;

import java.util.List;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ItemRunInformations {

	private RepositoryItem item;
	private List<VanillaGroupParameter> groupParams;
	private List<Group> availableGroups;
	
	public ItemRunInformations(RepositoryItem item, List<VanillaGroupParameter> groupParams, List<Group> availableGroups) {
		this.item = item;
		this.groupParams = groupParams;
		this.availableGroups = availableGroups;
	}
	
	public RepositoryItem getItem() {
		return item;
	}
	
	public List<VanillaGroupParameter> getGroupParams() {
		return groupParams;
	}
	
	public List<Group> getAvailableGroups() {
		return availableGroups;
	}
}
