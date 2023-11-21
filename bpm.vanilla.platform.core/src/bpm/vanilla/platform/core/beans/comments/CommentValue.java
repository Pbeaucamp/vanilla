package bpm.vanilla.platform.core.beans.comments;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rpy_comment_value")
public class CommentValue implements Serializable {

	private static final long serialVersionUID = 6621292022687853808L;

	public enum CommentStatus {
		OLD(0), NOT_VALIDATE(1), VALIDATE(2), UNVALIDATE(3);

		private int type;

		private CommentStatus(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;
	
	@Column(name = "comment_id")
	private int commentId;
	
	@Column(name = "user_id")
	private int userId;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "value", length = 10000000)
	private String value;

	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	private CommentStatus status;

	@Column(name = "creation_date")
	private Date creationDate;

	@Column(name = "parent_id")
	private Integer parentId;
	
	@Transient
	private List<CommentValueParameter> parameterValues;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public CommentStatus getStatus() {
		return status;
	}

	public void setStatus(CommentStatus status) {
		this.status = status;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public List<CommentValueParameter> getParameterValues() {
		return parameterValues;
	}

	public void setParameterValues(List<CommentValueParameter> parameterValues) {
		this.parameterValues = parameterValues != null ? new ArrayList<CommentValueParameter>(parameterValues) : null;
	}

	public void addCommentValueParameter(CommentValueParameter valueParam) {
		if (parameterValues == null) {
			parameterValues = new ArrayList<CommentValueParameter>();
		}
		parameterValues.add(valueParam);
	}
}
