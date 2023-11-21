package bpm.vanilla.platform.core.repository;

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

@Entity
@Table (name = "rpy_directory")
public class RepositoryDirectory implements IRepositoryObject {
	
	public static final String ID = "DIR_ID";
	public static final String PARENT_ID = "DIR_PARENT_ID";
	public static final String DATE_CREATION = "DIR_DATE_CREATION";
	public static final String DATE_DELETION = "DIR_DATE_DELETION";
	public static final String OWNER_ID = "DIR_OWNER_ID";
	public static final String DELETED_BY = "DIR_DELETED_BY";
	public static final String NAME = "DIR_NAME";
	public static final String COMMENT = "DIR_COMMENT";
	public static final String VISIBLE = "DIR_VISIBLE";
	public static final String SHOWED = "DIR_SHOWED";
	public static final String PERSO = "DIR_PERSO";
	
	private static final long serialVersionUID = -3011556832308815068L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "parent_id")
	private int parentId = 0;
	
	@Column(name = "creation_date")
	private Date dateCreation = new Date();
	
	@Column(name = "delete_date")
	private Date dateDeletion;

	@Column(name = "owner_id")
	private int ownerId = -1;
	
	@Column(name = "deleted_by")
	private int deletedBy = -1;

	@Column(name = "directory_name")
	private String name = "";
	
	@Column(name = "directory_comment")
	private String comment = "";

	@Column(name = "is_visible")
	private boolean visible = true;
	
	@Column(name = "is_showed")
	private boolean showed = true;
	
	@Column(name = "is_perso")
	private boolean perso = false;
	
	@Transient
	private boolean isCommentable = false;
	
	@Transient
	private List<IRepositoryObject> childs;

	public RepositoryDirectory() {

	}

	public RepositoryDirectory(int id, int parentId, Date dateCreation, Date dateDeletion, int ownerId, int deletedBy, String name, String comment, boolean visible, boolean showed, boolean perso) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.dateCreation = dateCreation;
		this.dateDeletion = dateDeletion;
		this.ownerId = ownerId;
		this.deletedBy = deletedBy;
		this.name = name;
		this.comment = comment;
		this.visible = visible;
		this.showed = showed;
		this.perso = perso;
	}

	public String getComment() {
		if(comment == null) {
			comment = "";
		}
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date creation) {
		this.dateCreation = creation;
	}

	public Date getDateDeletion() {
		return dateDeletion;
	}

	public void setDateDeletion(Date deletion) {
		this.dateDeletion = deletion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isShowed() {
		return showed;
	}

	public void setShowed(boolean showed) {
		this.showed = showed;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public void setPerso(boolean isPerso) {
		this.perso = isPerso;
	}

	public boolean isPerso() {
		return perso;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RepositoryDirectory) {
			return id == ((RepositoryDirectory)obj).getId();
		}
		return super.equals(obj);
	}

	public void setDeletedBy(int deletedBy) {
		this.deletedBy = deletedBy;
	}

	public int getDeletedBy() {
		return deletedBy;
	}

	public void setCommentable(boolean isCommentable) {
		this.isCommentable = isCommentable;
	}

	public boolean isCommentable() {
		return isCommentable;
	}

	public void setChilds(List<IRepositoryObject> childs) {
		this.childs = childs;
	}

	public List<IRepositoryObject> getChilds() {
		return childs;
	}
	
	public void addChild(IRepositoryObject child) {
		if(childs == null) {
			this.childs = new ArrayList<IRepositoryObject>();
		}
		this.childs.add(child);
	}
}