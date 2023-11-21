package bpm.vanilla.platform.core.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.User;

@Entity
@Table (name = "rpy_annotations")
public class Comment implements Serializable {
	
	private static final long serialVersionUID = 4575157541160639952L;
	
	public static final int DIRECTORY = 0;
	public static final int ITEM = 1;
	public static final int DOCUMENT_VERSION = 2;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "creation_date")
	private Date creationDate;

	@Column(name = "begin_date")
	private Date beginDate;
	
	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "`comment`")
	private String comment;
	
	@Column(name = "creator_id")
	private Integer creatorId;
	
	@Column(name = "type")
	private int type;
	
	@Column(name = "object_id")
	private Integer objectId;
	
	@Column(name = "parent_id")
	private Integer parentId;
	
	@Transient
	private List<SecuredComment> securedGroups = new ArrayList<SecuredComment>();
	
	@Transient
	private List<Comment> childs;
	
	@Transient
	private User user;

	public Comment() {}

	public Comment(int objectId, String comment, Date beginDate, Date endDate, int type, Integer parentId) {
		this.comment = comment;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.type = type;
		this.setObjectId(objectId);
		this.setParentId(parentId);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public Integer getObjectId() {
		return objectId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setSecuredGroups(List<SecuredComment> securedGroups) {
		this.securedGroups = securedGroups;
	}

	public List<SecuredComment> getSecuredGroups() {
		return securedGroups;
	}

	public void setChilds(List<Comment> childs) {
		this.childs = childs;
	}

	public List<Comment> getChilds() {
		return childs;
	}
	
	public void addChild(Comment comment) {
		if(childs == null) {
			childs = new ArrayList<Comment>();
		}
		childs.add(comment);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
