package bpm.vanilla.repository.beans.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "sec_runnable_object")
public class RunnableGroup {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "group_id")
	private Integer groupId;
	
	@Column(name = "item_id")
	private Integer directoryItemId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}

	public void setDirectoryItemId(Integer directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();

		buf.append("<runnableGroup>\n");
		buf.append("    <id>" + id + "</id>");
		buf.append("    <groupId>" + groupId + "</groupId>");
		buf.append("    <directoryItemId>" + directoryItemId + "</directoryItemId>");
		buf.append("</runnableGroup>\n");

		return buf.toString();
	}

}
