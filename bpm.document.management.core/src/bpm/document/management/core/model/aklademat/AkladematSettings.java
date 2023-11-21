package bpm.document.management.core.model.aklademat;

import java.io.Serializable;

public class AkladematSettings implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private AkladematTypeSettings type;
	private String model;

	private IAkladematSettings settings;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AkladematTypeSettings getTypeSettings() {
		return type;
	}
	
	public int getType() {
		return type.getType();
	}

	public void setTypeSettings(AkladematTypeSettings type) {
		this.type = type;
	}
	
	public void setType(int type) {
		this.type = AkladematTypeSettings.valueOf(type);
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public IAkladematSettings getSettings() {
		return settings;
	}

	public void setSettings(IAkladematSettings settings) {
		this.settings = settings;
	}

}
