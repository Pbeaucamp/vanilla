package bpm.vanilla.api.runtime.dto;

import bpm.vanilla.platform.core.beans.ged.DocumentVersion;

public class GedVersion {
	private int id;
	private int modifiedBy;
	private String format;
	private String summary;
	private String modificationDate;
	private String peremptionDate;
	
	public GedVersion(DocumentVersion docVersion) {
		this.id = docVersion.getId();
		this.modifiedBy = docVersion.getModifiedBy();
		this.format = docVersion.getFormat();
		this.summary = docVersion.getSummary();
		if(docVersion.getModificationDate() == null) {
			this.modificationDate = null;
		}
		else {
			this.modificationDate = docVersion.getModificationDate().toString();
		}
		
		
		if(docVersion.getPeremptionDate() == null) {
			this.peremptionDate = null;
		}
		else {
			this.peremptionDate = docVersion.getPeremptionDate().toString();
		}
		
	}

	public int getId() {
		return id;
	}
	
	public int getModifiedBy() {
		return modifiedBy;
	}


	public String getFormat() {
		return format;
	}

	public String getSummary() {
		return summary;
	}

	public String getModificationDate() {
		return modificationDate;
	}

	public String getPeremptionDate() {
		return peremptionDate;
	}
	
	
}
