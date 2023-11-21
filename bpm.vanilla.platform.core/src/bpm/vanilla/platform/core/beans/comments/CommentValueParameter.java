package bpm.vanilla.platform.core.beans.comments;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="rpy_comment_value_parameter")
public class CommentValueParameter implements Serializable {

	private static final long serialVersionUID = -1097592053426060868L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name="id")
	private int id;
	
	@Column(name="comment_param_id")
	private int commentParamId;
	
	@Column(name="value")
	private String value;

	@Column(name="comment_value_id")
	private int commentValueId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCommentParamId() {
		return commentParamId;
	}

	public void setCommentParamId(int commentParamId) {
		this.commentParamId = commentParamId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public int getCommentValueId() {
		return commentValueId;
	}
	
	public void setCommentValueId(int commentValueId) {
		this.commentValueId = commentValueId;
	}
}
