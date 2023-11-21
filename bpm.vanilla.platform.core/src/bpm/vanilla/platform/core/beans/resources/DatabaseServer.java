package bpm.vanilla.platform.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

public class DatabaseServer extends Resource {

	private String driverJdbc;
	private VariableString databaseUrl = new VariableString();
	
	private String login;
	private String password;

	private VariableString query = new VariableString();
	
	public DatabaseServer() {
		super("", TypeResource.DATABASE_SERVER);
	}

	public DatabaseServer(String name) {
		super(name, TypeResource.DATABASE_SERVER);
	}

	public String getDriverJdbc() {
		return driverJdbc;
	}

	public void setDriverJdbc(String driverJdbc) {
		this.driverJdbc = driverJdbc;
	}
	
	public VariableString getDatabaseUrlVS() {
		return databaseUrl;
	}

	public String getDatabaseUrlDisplay() {
		return databaseUrl.getStringForTextbox();
	}

	public String getDatabaseUrl(List<Parameter> parameters, List<Variable> variables) {
		return databaseUrl.getString(parameters, variables);
	}

	public void setDatabaseUrl(VariableString databaseUrl) {
		this.databaseUrl = databaseUrl;
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
	
	public VariableString getQueryVS() {
		return query;
	}

	public String getQueryDisplay() {
		return query.getStringForTextbox();
	}

	public String getQuery(List<Parameter> parameters, List<Variable> variables) {
		return query.getString(parameters, variables);
	}

	public void setQuery(VariableString query) {
		this.query = query;
	}

	public void updateInfo(String name, String driverJdbc, VariableString databaseUrl, String login, String password, VariableString query) {
		setName(name);
		this.driverJdbc = driverJdbc;
		this.databaseUrl = databaseUrl;
		this.login = login;
		this.password = password;
		this.query = query;
	}

	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(databaseUrl != null ? databaseUrl.getVariables() : new ArrayList<Variable>());
		variables.addAll(query != null ? query.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(databaseUrl != null ? databaseUrl.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(query != null ? query.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}
}

