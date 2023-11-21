package bpm.vanilla.portal.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FieldDefinitionDTO implements IsSerializable {
	private int id;
	private String name;
	private boolean stored;
	private boolean analyzed;
	private float boostLvl = 1;
	private boolean system;
	private boolean required;
	private boolean multiple;
	private boolean custom;
	
	public FieldDefinitionDTO() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean isStored() {
		return stored;
	}
	public void setStored(boolean stored) {
		this.stored = stored;
	}
	public boolean isAnalyzed() {
		return analyzed;
	}
	public void setAnalyzed(boolean analyzed) {
		this.analyzed = analyzed;
	}
	public float getBoostLvl() {
		return boostLvl;
	}
	public void setBoostLvl(float boostLvl) {
		this.boostLvl = boostLvl;
	}
	public boolean isSystem() {
		return system;
	}
	public void setSystem(boolean system) {
		this.system = system;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isCustom() {
		return custom;
	}
	public void setCustom(boolean custom) {
		this.custom = custom;
	}

}
