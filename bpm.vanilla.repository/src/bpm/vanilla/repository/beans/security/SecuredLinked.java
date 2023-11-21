package bpm.vanilla.repository.beans.security;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "sec_linked_secured")
public class SecuredLinked {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "linked_doc_id")
	private int linkedDocumentId;
	
	@Column(name = "group_id")
	private int groupId;

	public SecuredLinked() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLinkedDocumentId() {
		return linkedDocumentId;
	}

	public void setLinkedDocumentId(int linkedDocumentId) {
		this.linkedDocumentId = linkedDocumentId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

}
