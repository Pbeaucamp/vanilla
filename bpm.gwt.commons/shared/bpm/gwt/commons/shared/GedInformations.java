package bpm.gwt.commons.shared;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GedInformations implements IsSerializable {

	private String title;
	private String description;
//	private IDirectoryDTO dir;
	private List<Integer> groupIds;
	
	private Integer documentDefinitionId;
	
	private Date peremptionDate;
	
	public GedInformations() { }
	
	public GedInformations(String title, /*IDirectoryDTO dir, */List<Integer> groupIds,
			String description, Date peremptionDate){
		this.title = title;
//		this.dir = dir;
		this.groupIds = groupIds;
		this.setDescription(description);
		this.setPeremptionDate(peremptionDate);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
//	public IDirectoryDTO getDir() {
//		return dir;
//	}
//	
//	public void setDir(IDirectoryDTO dir) {
//		this.dir = dir;
//	}

	public List<Integer> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Integer> groupIds) {
		this.groupIds = groupIds;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDocumentDefinitionId(Integer documentDefinitionId) {
		this.documentDefinitionId = documentDefinitionId;
	}

	public Integer getDocumentDefinitionId() {
		return documentDefinitionId;
	}

	public void setPeremptionDate(Date peremptionDate) {
		this.peremptionDate = peremptionDate;
	}

	public Date getPeremptionDate() {
		return peremptionDate;
	}
}
