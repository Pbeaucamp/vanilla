package bpm.vanilla.api.runtime.dto;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;

public abstract class CubeCell {

	private String id;
	private String name;
	private String type;
	private boolean isOLAPMember;

	public CubeCell(Item it, String id, String type) {
		this.id = id;
		this.name = it.getLabel();
		this.type = type;

		this.isOLAPMember = (it instanceof ItemElement) && (!((ItemElement) it).getDataMember().getUniqueName().startsWith("[Measures]"));
	}

	public boolean isOLAPMember() {
		return isOLAPMember;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

}
