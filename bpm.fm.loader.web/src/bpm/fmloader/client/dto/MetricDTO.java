package bpm.fmloader.client.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetricDTO implements IsSerializable {

	private int id;
	private String name;
	private boolean isCompteur;
	private String unit;
	
	public MetricDTO() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompteur() {
		return isCompteur;
	}

	public void setCompteur(boolean isCompteur) {
		this.isCompteur = isCompteur;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	
	
}
