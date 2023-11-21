package bpm.vanilla.platform.core.beans;

import java.io.Serializable;

public class ArchiveType implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public enum TypeArchive {
		SEDA, STANDARD
	}
	
	private int id;
	private String name;
	
	private int peremptionMonths;
	private int retentionMonths;
	private int conservationMonths;
	
	private String savePath;
	private boolean archive;
	private TypeArchive type = TypeArchive.STANDARD;

	public int getPeremptionMonths() {
		return peremptionMonths;
	}

	public void setPeremptionMonths(int peremptionMonths) {
		this.peremptionMonths = peremptionMonths;
	}

	public int getRetentionMonths() {
		return retentionMonths;
	}

	public void setRetentionMonths(int retentionnMonths) {
		this.retentionMonths = retentionnMonths;
	}

	public int getConservationMonths() {
		return conservationMonths;
	}

	public void setConservationMonths(int conservationMonths) {
		this.conservationMonths = conservationMonths;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isArchive() {
		return archive;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public TypeArchive getType() {
		return type;
	}

	public void setType(TypeArchive type) {
		this.type = type;
	}

	
}
