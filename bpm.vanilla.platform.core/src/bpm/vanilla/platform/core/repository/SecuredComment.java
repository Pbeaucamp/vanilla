package bpm.vanilla.platform.core.repository;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_secured_annotations")
public class SecuredComment implements Serializable {

	private static final long serialVersionUID = 5609194994132410801L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "group_id")
	private int groupId;
	
	@Column(name = "comment_id")
	private int commentId;
	
	public SecuredComment() {
		
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getCommentId() {
		return commentId;
	}
}
