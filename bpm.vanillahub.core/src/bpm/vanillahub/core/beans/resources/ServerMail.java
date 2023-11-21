package bpm.vanillahub.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;

public class ServerMail extends Resource {

	private VariableString smtpHost = new VariableString();
	private VariableString port = new VariableString(25);
	
	private VariableString fromEmail;

	private boolean secured;
	private boolean tls;
	
	private String login;
	private String password;
	
	public ServerMail() {
		super("", TypeResource.MAIL);
	}
	
	public ServerMail(String name) {
		super(name, TypeResource.MAIL);
	}

	public VariableString getSmtpHostVS() {
		return smtpHost;
	}
	
	public String getSmtpHostDisplay() {
		return smtpHost.getStringForTextbox();
	}

	public String getSmtpHost(List<Parameter> parameters, List<Variable> variables) {
		return smtpHost.getString(parameters, variables);
	}

	public void setSmtpHost(VariableString smtpHost) {
		this.smtpHost = smtpHost;
	}
	
//	public void addVariableToSmtpHost(Variable variable) {
//		this.smtpHost.addVariable(variable);
//	}

	public VariableString getPortVS() {
		return port;
	}
	
	public String getPortDisplay() {
		return port.getStringForTextbox();
	}

	public int getPort(List<Parameter> parameters, List<Variable> variables) {
		return Integer.parseInt(port.getString(parameters, variables));
	}

	public void setPort(VariableString port) {
		this.port = port;
	}
	
//	public void addVariableToPort(Variable variable) {
//		this.port.addVariable(variable);
//	}

	public VariableString getFromEmailVS() {
		return fromEmail;
	}
	
	public String getFromEmailDisplay() {
		return fromEmail.getStringForTextbox();
	}

	public String getFromEmail(List<Parameter> parameters, List<Variable> variables) {
		return fromEmail.getString(parameters, variables);
	}

	public void setFromEmail(VariableString fromEmail) {
		this.fromEmail = fromEmail;
	}
	
//	public void addVariableToFromEmail(Variable variable) {
//		this.fromEmail.addVariable(variable);
//	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public boolean isTls() {
		return tls;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
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

	public void updateInfo(String name, VariableString smtpHost, VariableString port, VariableString fromEmail, boolean secured, boolean tls, String login, String password) {
		setName(name);
		this.smtpHost = smtpHost;
		this.port = port;
		this.fromEmail = fromEmail;
		this.secured = secured;
		this.tls = tls;
		this.login = login;
		this.password = password;
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(smtpHost != null ? smtpHost.getVariables() : new ArrayList<Variable>());
		variables.addAll(port != null ? port.getVariables() : new ArrayList<Variable>());
		variables.addAll(fromEmail != null ? fromEmail.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(smtpHost != null ? smtpHost.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(port != null ? port.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(fromEmail != null ? fromEmail.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}

}
