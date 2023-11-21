package bpm.vanilla.platform.core.beans.validation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import bpm.vanilla.platform.core.beans.User;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="rpy_user_validation")
public class UserValidation implements Serializable {

	private static final long serialVersionUID = -241750024214386219L;

	public enum UserValidationType {
		VALIDATOR(0), 
		COMMENTATOR(1);
		
		private int type;
		
		private UserValidationType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}
	
	public enum UserValidationStatus {
		VALIDATION(0), 
		CIRCUIT(1);
		
		private int type;
		
		private UserValidationStatus(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name="id")
	private int id;

	@Column(name="user_id")
	private int userId;
	
	@Column(name="type")
	@Enumerated(EnumType.ORDINAL)
	private UserValidationType type;
	
	@Column(name="status")
	@Enumerated(EnumType.ORDINAL)
	private UserValidationStatus status;

	@Column(name="user_order")
	private int userOrder;
	
	@Column(name="parent_id")
	private int parentId;
	
	@Transient
	private User user;
	
	public UserValidation() {
	}
	
	public UserValidation(User user, UserValidationType type, int userOrder) {
		this.user = user;
		this.userId = user.getId();
		this.type = type;
		this.userOrder = userOrder;
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

	public UserValidationType getType() {
		return type;
	}

	public void setType(UserValidationType type) {
		this.type = type;
	}
	
	public UserValidationStatus getStatus() {
		return status;
	}
	
	public void setStatus(UserValidationStatus status) {
		this.status = status;
	}

	public int getUserOrder() {
		return userOrder;
	}

	public void setUserOrder(int userOrder) {
		this.userOrder = userOrder;
	}
	
	public int getParentId() {
		return parentId;
	}
	
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UserValidation) {
			return ((UserValidation) obj).getId() == getId() && ((UserValidation) obj).getUserId() == getUserId();
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
