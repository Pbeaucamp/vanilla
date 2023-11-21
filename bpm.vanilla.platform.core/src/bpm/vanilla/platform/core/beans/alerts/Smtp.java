package bpm.vanilla.platform.core.beans.alerts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * List of different smtp host
 * Used when a user wants an alert to send a mail
 * 
 * @author vanilla
 *
 */

@Entity
@Table (name = "rpy_alert_smtp")
public class Smtp {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	private int id;
	
	@Column(name = "url")
	private String smtpUrl;
	
	@Column(name = "login")
	private String login;
	
	@Column(name = "password")
	private String password;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setSmtpUrl(String smtpUrl) {
		this.smtpUrl = smtpUrl;
	}
	public String getSmtpUrl() {
		return smtpUrl;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getLogin() {
		return login;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPassword() {
		return password;
	}
}
