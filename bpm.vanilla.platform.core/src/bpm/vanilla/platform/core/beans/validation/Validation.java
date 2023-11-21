package bpm.vanilla.platform.core.beans.validation;

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

import bpm.vanilla.platform.core.repository.RepositoryItem;

@Entity
@Table(name="rpy_validation")
public class Validation implements Serializable {

	private static final long serialVersionUID = 6931371322049191016L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name="id")
	private int id;

	@Column(name="date_begin")
	private Date dateBegin;

	@Column(name="start_etl_id")
	private int startEtlId;

	@Column(name="end_etl_id")
	private int endEtlId;

	@Column(name="item_id")
	private int itemId;

	@Column(name="alert_id")
	private int alertId;

	@Column(name="admin_user_id")
	private int adminUserId;

	@Column(name="is_actif")
	private boolean isActif = true;

	@Column(name="is_valid")
	private boolean isValid;

	@Column(name="is_offline")
	private boolean isOffline = false;

	@Transient
	private List<UserValidation> commentators;
	
	@Transient
	private List<UserValidation> validators;
	
	@Transient
	private UserValidation nextCommentator;
	
	@Transient
	private RepositoryItem item;
	
	public Validation() {
	}
	
	public Validation(Date dateBegin, int itemId, int adminUserId, boolean isOffline, List<UserValidation> commentators, List<UserValidation> validators) {
		this.itemId = itemId;
		this.adminUserId = adminUserId;
		this.isOffline = isOffline;
		this.commentators = commentators;
		this.validators = validators;
		this.isActif = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Date getDateBegin() {
		return dateBegin;
	}
	
	public void setDateBegin(Date dateBegin) {
		this.dateBegin = dateBegin;
	}

	public int getStartEtlId() {
		return startEtlId;
	}

	public void setStartEtlId(int startEtlId) {
		this.startEtlId = startEtlId;
	}

	public int getEndEtlId() {
		return endEtlId;
	}

	public void setEndEtlId(int endEtlId) {
		this.endEtlId = endEtlId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}
	
	public int getAdminUserId() {
		return adminUserId;
	}
	
	public void setAdminUserId(int adminUserId) {
		this.adminUserId = adminUserId;
	}

	public boolean isActif() {
		return isActif;
	}

	public void setActif(boolean isActif) {
		this.isActif = isActif;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isOffline() {
		return isOffline;
	}
	
	public void setOffline(boolean isOffline) {
		this.isOffline = isOffline;
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
	
	public RepositoryItem getItem() {
		return item;
	}
	
	public void setItem(RepositoryItem item) {
		this.item = item;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Validation) {
			return ((Validation) obj).getId() == getId();
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
