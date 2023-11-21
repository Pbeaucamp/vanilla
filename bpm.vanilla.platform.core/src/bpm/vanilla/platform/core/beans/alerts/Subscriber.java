package bpm.vanilla.platform.core.beans.alerts;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.User;

/**
 * A subscriber of an alert
 * 
 * @author vanilla
 *
 */

@Entity
@Table (name = "rpy_alert_subscriber")
public class Subscriber implements Serializable{
	
	private static final long serialVersionUID = -2613815562605891403L;
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "alert_id")
	private int alertId;
	
	@Column(name = "user_id")
	private int userId;
	
	@Column(name = "group_id")
	private int groupId;

	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "user_mail")
	private String userMail;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}
	
	public int getAlertId() {
		return alertId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getGroupId() {
		return groupId;
	}

	public Subscriber() {
		super();
	}
	
	public Subscriber(User user) {
		super();
		this.userId = user.getId();
		this.userMail = user.getBusinessMail();
		this.userName = user.getName();
	}
}
