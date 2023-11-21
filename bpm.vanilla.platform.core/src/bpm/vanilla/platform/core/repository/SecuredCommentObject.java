package bpm.vanilla.platform.core.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_secured_annotations_object")
public class SecuredCommentObject {
	
	public static final String COMMENT_ID = "COMMENT_ID";
	public static final String COMMENT_OBJECT_ID = "COMMENT_OBJECT_ID";
	public static final String COMMENT_GROUP_ID = "COMMENT_GROUP_ID";
	public static final String COMMENT_TYPE = "COMMENT_TYPE";
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "object_id")
	private Integer objectId;
	
	@Column(name = "group_id")
	private Integer groupId;
	
	@Column(name = "type")
	private Integer type;

	public SecuredCommentObject() {
		
	}

	public SecuredCommentObject(int id, Integer objectId, Integer groupId, Integer type) {
		this.id = id;
		this.objectId = objectId;
		this.groupId = groupId;
		this.type = type;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public Integer getObjectId() {
		return objectId;
	}

}
