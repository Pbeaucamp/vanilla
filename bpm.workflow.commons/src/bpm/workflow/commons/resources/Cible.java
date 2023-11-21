package bpm.workflow.commons.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.resources.attributes.FTPAction;

public class Cible extends Resource {

	private static final long serialVersionUID = 1L;

	public enum TypeCible {
		FTP(0), HTTP(1), FOLDER(2), CKAN(3), SFTP(4), D4C(5);

		private int type;

		private static Map<Integer, TypeCible> map = new HashMap<Integer, TypeCible>();
		static {
			for (TypeCible serverType : TypeCible.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private TypeCible(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeCible valueOf(int serverType) {
			return map.get(serverType);
		}
	}

	public TypeCible type;

	private VariableString url = new VariableString();
	private VariableString httpFileParam = new VariableString();
	private VariableString port = new VariableString();

	private boolean secured;

	private String login;
	private String password;

	private VariableString folder;
	private List<FTPAction> actions;
	
	private boolean isNetworkFolder;
	private boolean override;

	private String org;
	private String apiKey;
	//To replace with VariableString
//	private VariableString org = new VariableString();
//	private VariableString apiKey = new VariableString();
	private CkanPackage ckanPackage;
	
	private boolean validateData;

	public Cible() {
		super("", TypeResource.CIBLE);
	}

	public Cible(String name) {
		super(name, TypeResource.CIBLE);
		this.type = TypeCible.FTP;
	}

	public TypeCible getType() {
		return type;
	}

	public void setType(TypeCible type) {
		this.type = type;
	}

	public VariableString getUrlVS() {
		return url;
	}

	public String getUrlDisplay() {
		return url.getStringForTextbox();
	}

	public String getUrl(List<Parameter> parameters, List<Variable> variables) {
		return url.getString(parameters, variables);
	}

	public void setUrl(VariableString url) {
		this.url = url;
	}

	public VariableString getHttpFileParamVS() {
		return httpFileParam;
	}

	public String getHttpFileParamDisplay() {
		return httpFileParam.getStringForTextbox();
	}

	public String getHttpFileParam(List<Parameter> parameters, List<Variable> variables) {
		return httpFileParam.getString(parameters, variables);
	}

	public void setHttpFileParam(VariableString httpFileParam) {
		this.httpFileParam = httpFileParam;
	}

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

	public VariableString getFolderVS() {
		return folder;
	}

	public String getFolderDisplay() {
		return folder.getStringForTextbox();
	}

	public String getFolder(List<Parameter> parameters, List<Variable> variables) {
		return folder.getString(parameters, variables);
	}

	public void setFolder(VariableString folder) {
		this.folder = folder;
	}

	public boolean isSecured() {
		return secured;
	}

	public void setSecured(boolean secured) {
		this.secured = secured;
	}

	public String getLogin() {
		return login != null ? login : "";
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password != null ? password : "";
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isNetworkFolder() {
		return isNetworkFolder;
	}

	public void setNetworkFolder(boolean isNetworkFolder) {
		this.isNetworkFolder = isNetworkFolder;
	}
	
	public boolean isOverride() {
		return override;
	}
	
	public void setOverride(boolean override) {
		this.override = override;
	}

	public List<FTPAction> getActions() {
		return actions;
	}

	public void setActions(List<FTPAction> actions) {
		this.actions = actions;
	}
	
	public String getOrg() {
		return org != null ? org : "";
	}
	
	public void setOrg(String org) {
		this.org = org;
	}
	
	public String getApiKey() {
		return apiKey != null ? apiKey : "";
	}
	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

//	public VariableString getOrgVS() {
//		return org;
//	}
//
//	public String getOrgDisplay() {
//		return org.getStringForTextbox();
//	}
//
//	public String getOrg(List<Parameter> parameters, List<Variable> variables) {
//		return org.getString(parameters, variables);
//	}
//
//	public void setOrg(VariableString org) {
//		this.org = org;
//	}
//
//	public VariableString getApiKeyVS() {
//		return apiKey;
//	}
//
//	public String getApiKeyDisplay() {
//		return apiKey.getStringForTextbox();
//	}
//
//	public String getApiKey(List<Parameter> parameters, List<Variable> variables) {
//		return apiKey.getString(parameters, variables);
//	}
//
//	public void setApiKey(VariableString apiKey) {
//		this.apiKey = apiKey;
//	}
	
	public CkanPackage getCkanPackage() {
		return ckanPackage;
	}
	
	public void setCkanPackage(CkanPackage ckanPackage) {
		this.ckanPackage = ckanPackage;
	}
	
	public boolean isValidateData() {
		return validateData;
	}
	
	public void setValidateData(boolean validateData) {
		this.validateData = validateData;
	}
	
	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(url != null ? url.getVariables() : new ArrayList<Variable>());
		variables.addAll(httpFileParam != null ? httpFileParam.getVariables() : new ArrayList<Variable>());
		variables.addAll(port != null ? port.getVariables() : new ArrayList<Variable>());
		variables.addAll(folder != null ? folder.getVariables() : new ArrayList<Variable>());
//		variables.addAll(org != null ? org.getVariables() : new ArrayList<Variable>());
//		variables.addAll(apiKey != null ? apiKey.getVariables() : new ArrayList<Variable>());

		if (actions != null) {
			for (FTPAction action : actions) {
				variables.addAll(action.getVariables());
			}
		}

		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(url != null ? url.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(httpFileParam != null ? httpFileParam.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(port != null ? port.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(folder != null ? folder.getParameters() : new ArrayList<Parameter>());
//		parameters.addAll(org != null ? org.getParameters() : new ArrayList<Parameter>());
//		parameters.addAll(apiKey != null ? apiKey.getParameters() : new ArrayList<Parameter>());

		if (actions != null) {
			for (FTPAction action : actions) {
				parameters.addAll(action.getParameters());
			}
		}

		return parameters;
	}
}
