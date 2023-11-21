package bpm.workflow.commons.resources;

import java.util.Date;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;

public class UserWrapper extends Resource {

	private static final long serialVersionUID = 1L;
	
	private String password;
	private String email;
	private String fonction;
	
	private Date creationDate;
	
	private String locale;
	private boolean isAdmin;
	
	public UserWrapper() {
	}
	
	public UserWrapper(int id, String login, String password, String email, String fonction, Date creationDate, boolean isAdmin) {
		super(login, TypeResource.USER);
		setId(id);
		this.password = password;
		this.email = email;
		this.fonction = fonction;
		this.creationDate = creationDate;
		this.isAdmin = isAdmin;
	}

	public String getEmail() {
		return email;
	}
	
	public String getFonction() {
		return fonction;
	}
	
	public String getPassword() {
		return password;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getLocale() {
		return locale;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public List<Variable> getVariables() {
		return null;
	}

	@Override
	public List<Parameter> getParameters() {
		return null;
	}
}
