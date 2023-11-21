package bpm.gwt.commons.shared.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentDefinitionDTO implements IsSerializable, Comparable<DocumentDefinitionDTO> {

	private int id;
	private String name;
	private Date creationDate;
	private int creatorId;
	private float score;
	
	private boolean isGranted;
	private boolean isMdm;
	
	private List<DocumentVersionDTO> versions;
	
	public DocumentDefinitionDTO() { }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getCreatorId() {
		return creatorId;
	}
	
	public void setGranted(boolean isGranted) {
		this.isGranted = isGranted;
	}

	public boolean isGranted() {
		return isGranted;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void addVersion(DocumentVersionDTO version){
		if(versions == null){
			versions = new ArrayList<DocumentVersionDTO>();
		}
		this.versions.add(version);
	}
	
	public void setVersions(List<DocumentVersionDTO> versions){
		this.versions = versions;
	}
	
	public List<DocumentVersionDTO> getVersions(){
		return versions;
	}

	@Override
	public int compareTo(DocumentDefinitionDTO doc) {
		return this.getName().compareTo(doc.getName());
	}

	public boolean isMdm() {
		return isMdm;
	}
	
	public void setMdm(boolean isMdm) {
		this.isMdm = isMdm;
	}
}
