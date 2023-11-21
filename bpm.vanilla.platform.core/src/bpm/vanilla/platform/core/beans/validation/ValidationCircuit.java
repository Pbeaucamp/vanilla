package bpm.vanilla.platform.core.beans.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rpy_validation_circuit")
public class ValidationCircuit implements Serializable {

	private static final long serialVersionUID = -241750024214386219L;

	@Id
	@GeneratedValue(generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "user_id")
	private int userId;

	@Column(name = "name")
	private String name;

	@Transient
	private List<UserValidation> commentators;

	@Transient
	private List<UserValidation> validators;

	@Transient
	private UserValidation nextCommentator;

	public ValidationCircuit() {
	}

	public ValidationCircuit(int userId, String name, List<UserValidation> validators, List<UserValidation> commentators) {
		this.userId = userId;
		this.name = name;
		this.validators = validators;
		this.commentators = commentators;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public List<UserValidation> getCommentators() {
		return commentators;
	}

	public void setCommentators(List<UserValidation> commentators) {
		this.commentators = commentators != null ? new ArrayList<UserValidation>(commentators) : null;
	}

	public List<UserValidation> getValidators() {
		return validators;
	}

	public void setValidators(List<UserValidation> validators) {
		this.validators = validators != null ? new ArrayList<UserValidation>(validators) : null;
	}

	public UserValidation getNextCommentator() {
		return nextCommentator;
	}

	public void setNextCommentator(UserValidation nextCommentator) {
		this.nextCommentator = nextCommentator;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ValidationCircuit) {
			return ((ValidationCircuit) obj).getId() == getId();
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
