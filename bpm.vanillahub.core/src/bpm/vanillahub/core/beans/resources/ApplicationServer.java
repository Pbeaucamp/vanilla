package bpm.vanillahub.core.beans.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;

public class ApplicationServer extends Resource {

	private static final long serialVersionUID = 1L;

	public enum TypeServer {
		VANILLA(0), 
		AKLABOX(1),
		LIMESURVEY(2);
		
		private int type;

		private static Map<Integer, TypeServer> map = new HashMap<Integer, TypeServer>();
		static {
	        for (TypeServer typeServer : TypeServer.values()) {
	            map.put(typeServer.getType(), typeServer);
	        }
	    }
		
		private TypeServer(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
		
		public static TypeServer valueOf(int typeServer) {
	        return map.get(typeServer);
	    }
	}
	
	private VariableString url;
	private VariableString login;
	private VariableString password;
	
	private TypeServer typeServer;

	public ApplicationServer(TypeServer typeServer) {
		super("", TypeResource.APPLICATION_SERVER);
		this.typeServer = typeServer;
	}

	public ApplicationServer(String name, TypeServer typeServer) {
		super(name, TypeResource.APPLICATION_SERVER);
		this.typeServer = typeServer;
	}

	public VariableString getUrlVS() {
		return url;
	}

	public String getUrlDisplay() {
		return url.getStringForTextbox();
	}

	public void setUrl(VariableString url) {
		this.url = url;
	}
	
	public String getUrl(List<Parameter> parameters, List<Variable> variables) {
		return url.getString(parameters, variables);
	}
	
	public VariableString getLoginVS() {
		return login;
	}

	public String getLoginDisplay() {
		return login.getStringForTextbox();
	}

	public void setLogin(VariableString login) {
		this.login = login;
	}

	public String getLogin(List<Parameter> parameters, List<Variable> variables) {
		return login.getString(parameters, variables);
	}
	
	public VariableString getPasswordVS() {
		return password;
	}

	public String getPasswordDisplay() {
		return password.getStringForTextbox();
	}

	public void setPassword(VariableString password) {
		this.password = password;
	}

	public String getPassword(List<Parameter> parameters, List<Variable> variables) {
		return password.getString(parameters, variables);
	}
	
	public TypeServer getTypeServer() {
		return typeServer;
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(url != null ? url.getVariables() : new ArrayList<Variable>());
		variables.addAll(login != null ? login.getVariables() : new ArrayList<Variable>());
		variables.addAll(password != null ? password.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(url != null ? url.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(login != null ? login.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(password != null ? password.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}

	public void updateInfo(String name, VariableString url, VariableString login, VariableString password, TypeServer typeServer) {
		setName(name);
		this.url = url;
		this.login = login;
		this.password = password;
		this.typeServer = typeServer;
	}
}
