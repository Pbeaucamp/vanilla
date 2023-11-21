package bpm.vanilla.platform.core.beans.comments;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="rpy_comment_parameter")
public class CommentParameter implements Serializable {

	private static final long serialVersionUID = -7374712143839841244L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name="id")
	private int id;
	
	@Column(name="parameter_identifier")
	private String parameterIdentifier;
	
	@Column(name="prompt")
	private String prompt;

	@Column(name="comment_definition_id")
	private int commentDefinitionId;
	
	@Column(name="default_value")
	private String defaultValue;

	private String value;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getParameterIdentifier() {
		return parameterIdentifier;
	}

	public void setParameterIdentifier(String parameterIdentifier) {
		this.parameterIdentifier = parameterIdentifier;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	
	public int getCommentDefinitionId() {
		return commentDefinitionId;
	}
	
	public void setCommentDefinitionId(int commentDefinitionId) {
		this.commentDefinitionId = commentDefinitionId;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
}
