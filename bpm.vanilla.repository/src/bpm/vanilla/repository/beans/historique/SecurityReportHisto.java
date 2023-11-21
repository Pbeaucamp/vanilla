package bpm.vanilla.repository.beans.historique;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "sec_histo_report")
public class SecurityReportHisto {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "group_id")
	private int groupId;
	
	@Column(name = "item_id")
	private int directoryItemId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(int directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

}
