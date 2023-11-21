package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class GedItem {
	
	private int histoId;
	private int createdBy;
	private String gedName;
	private String creationDate;
	private List<GedVersion> versions;
	
	public GedItem(GedDocument gedDoc) {
		this.histoId = gedDoc.getId();
		this.createdBy = gedDoc.getCreatedBy();
		this.gedName = gedDoc.getName();
		
		
		if(gedDoc.getCreationDate() == null) {
			this.creationDate = null;
		}
		else {
			this.creationDate = gedDoc.getCreationDate().toString();
		}
		
		loadVersions(gedDoc);
	}
	
	private void loadVersions(GedDocument gedDoc){
		versions = new ArrayList<>();
		
		for(DocumentVersion docVersion : gedDoc.getDocumentVersions()) {
			versions.add(new GedVersion(docVersion));
		}
	}

	public int getHistoId() {
		return histoId;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public String getGedName() {
		return gedName;
	}
	
	public String getCreationDate() {
		return creationDate;
	}

	public List<GedVersion> getVersions() {
		return versions;
	}
	
	

}
