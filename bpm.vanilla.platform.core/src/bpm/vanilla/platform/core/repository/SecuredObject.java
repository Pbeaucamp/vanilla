package bpm.vanilla.platform.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table (name = "sec_object_secured")
public class SecuredObject {
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "group_id")
	private Integer groupId;
	
	@Column(name = "item_id")
	private Integer directoryItemId;
	
	public SecuredObject(){}
	

	public SecuredObject(int id, Integer directoryItemId, Integer groupId) {
		super();
		this.id = id;
		this.directoryItemId = directoryItemId;
		this.groupId = groupId;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public Integer getDirectoryItemId() {
		return directoryItemId;
	}


	public void setDirectoryItemId(Integer directoryItemId) {
		this.directoryItemId = directoryItemId;
	}

	public Integer getGroupId() {
		return groupId;
	}


	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	

}
