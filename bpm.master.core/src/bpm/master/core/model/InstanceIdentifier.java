package bpm.master.core.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "vdm_master_instance")
public class InstanceIdentifier implements Serializable {

	private static final long serialVersionUID = -8773582787304602064L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "instance_name")
	private String name;
	
	@Column(name = "login")
	private String login;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "running")
	private boolean running = false;
	
	@Column(name = "ozwillo_instance")
	private String ozwilloId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public String getOzwilloId() {
		return ozwilloId;
	}

	public void setOzwilloId(String ozwilloId) {
		this.ozwilloId = ozwilloId;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstanceIdentifier other = (InstanceIdentifier) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
