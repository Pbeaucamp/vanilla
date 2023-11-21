package bpm.document.management.core.model;

import java.io.Serializable;

public class RuleAlim implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id=0;
	private String name;
	private String frequency;
	private String source ;
	private String destName;
	private int destId;
	private Boolean Ldap=false;
	private String user;
	private String password;
	
	public RuleAlim() {
		super();
	}

	public RuleAlim(String name, String frequency, String source,
			String destName, int destId) {
		super();
		this.name = name;
		this.frequency = frequency;
		this.source = source;
		this.destName = destName;
		this.destId = destId;
	}

	public RuleAlim(String name, String frequency, String source,
			String destName, int destId, Boolean isLdap, String user,
			String password) {
		super();
		this.name = name;
		this.frequency = frequency;
		this.source = source;
		this.destName = destName;
		this.destId = destId;
		this.Ldap = isLdap;
		this.user = user;
		this.password = password;
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

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestName() {
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

	public Boolean isLdap() {
		return Ldap;
	}

	public void setLdap(Boolean isLdap) {
		this.Ldap = isLdap;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
