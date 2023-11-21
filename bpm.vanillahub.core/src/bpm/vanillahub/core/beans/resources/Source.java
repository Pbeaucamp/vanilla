package bpm.vanillahub.core.beans.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;

public class Source extends Resource {

	public static final boolean FOLDER_AVAILABLE = true;

	public enum TypeSource {
		WEB_SERVICE(0), FOLDER(1), FTP(2), D4C(3), MAIL(4), SFTP(5);

		private int type;

		private static Map<Integer, TypeSource> map = new HashMap<Integer, TypeSource>();
		static {
			for (TypeSource serverType : TypeSource.values()) {
				map.put(serverType.getType(), serverType);
			}
		}

		private TypeSource(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeSource valueOf(int serverType) {
			return map.get(serverType);
		}
	}

	private TypeSource type;

	private String login;
	private String password;
	
	private boolean useOutputName;
	private VariableString outputName = new VariableString();

	// Options for folder
	private VariableString folderPath = new VariableString();
	private VariableString filter = new VariableString();
	private VariableString attachmentFilter = new VariableString();

	private boolean isNetworkFolder;
	private boolean includeSubfolder;
	
	//Options for ftp
	private VariableString port = new VariableString();
	private boolean isSecured;

	//Options for webservice
	private VariableString url = new VariableString();
	private WebServiceMethodDefinition method;
	
	//Options for D4C
	private CkanPackage ckanPackage;
	
	//Options for MAIL
	private VariableString treatedFolderPath = new VariableString();
	private boolean rejectNonMatchingMail;
	private VariableString rejectedFolderPath = new VariableString();
	private boolean copyFile;

	public Source() {
		super("", TypeResource.SOURCE);
	}

	public Source(String name) {
		super(name, TypeResource.SOURCE);
		this.type = TypeSource.WEB_SERVICE;
	}

	public TypeSource getType() {
		return type;
	}

	public void setType(TypeSource type) {
		this.type = type;
	}

	public VariableString getFolderPathVS() {
		return folderPath;
	}

	public String getFolderPathDisplay() {
		return folderPath.getStringForTextbox();
	}

	public String getFolderPath(List<Parameter> parameters, List<Variable> variables) {
		return folderPath.getString(parameters, variables);
	}

	public void setFolderPath(VariableString folderPath) {
		this.folderPath = folderPath;
	}

	public VariableString getFilterVS() {
		return filter;
	}

	public String getFilterDisplay() {
		return filter.getStringForTextbox();
	}
	
	public String getFilter(List<Parameter> parameters, List<Variable> variables) {
		return filter.getString(parameters, variables);
	}

	public void setFilter(VariableString filter) {
		this.filter = filter;
	}

	public VariableString getAttachmentFilterVS() {
		return attachmentFilter;
	}

	public String getAttachmentFilterDisplay() {
		return attachmentFilter.getStringForTextbox();
	}
	
	public String getAttachmentFilter(List<Parameter> parameters, List<Variable> variables) {
		return attachmentFilter.getString(parameters, variables);
	}

	public void setAttachmentFilter(VariableString filter) {
		this.attachmentFilter = filter;
	}
	
	public boolean useOutputName() {
		return useOutputName;
	}
	
	public void setUseOutputName(boolean useOutputName) {
		this.useOutputName = useOutputName;
	}

	public VariableString getOutputNameVS() {
		return outputName;
	}

	public String getOutputNameDisplay() {
		return outputName.getStringForTextbox();
	}
	
	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return outputName.getString(parameters, variables);
	}

	public void setOutputName(VariableString outputName) {
		this.outputName = outputName;
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

	public WebServiceMethodDefinition getMethod() {
		return method;
	}

	public void setMethod(WebServiceMethodDefinition method) {
		this.method = method;
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

	public boolean isNetworkFolder() {
		return isNetworkFolder;
	}

	public void setNetworkFolder(boolean isNetworkFolder) {
		this.isNetworkFolder = isNetworkFolder;
	}

	public boolean includeSubfolder() {
		return includeSubfolder;
	}
	
	public void setIncludeSubfolder(boolean includeSubfolder) {
		this.includeSubfolder = includeSubfolder;
	}
	
	public boolean isSecured() {
		return isSecured;
	}
	
	public void setSecured(boolean isSecured) {
		this.isSecured = isSecured;
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
	
	public CkanPackage getCkanPackage() {
		return ckanPackage;
	}
	
	public void setCkanPackage(CkanPackage ckanPackage) {
		this.ckanPackage = ckanPackage;
	}

	public VariableString getTreatedFolderPathVS() {
		return treatedFolderPath;
	}

	public String getTreatedFolderPathDisplay() {
		return treatedFolderPath.getStringForTextbox();
	}

	public String getTreatedFolderPath(List<Parameter> parameters, List<Variable> variables) {
		return treatedFolderPath.getString(parameters, variables);
	}

	public void setTreatedFolderPath(VariableString treatedFolderPath) {
		this.treatedFolderPath = treatedFolderPath;
	}
	
	public boolean isRejectNonMatchingMail() {
		return rejectNonMatchingMail;
	}
	
	public void setRejectNonMatchingMail(boolean rejectNonMatchingMail) {
		this.rejectNonMatchingMail = rejectNonMatchingMail;
	}

	public VariableString getRejectedFolderPathVS() {
		return rejectedFolderPath;
	}

	public String getRejectedFolderPathDisplay() {
		return rejectedFolderPath.getStringForTextbox();
	}

	public String getRejectedFolderPath(List<Parameter> parameters, List<Variable> variables) {
		return rejectedFolderPath.getString(parameters, variables);
	}

	public void setRejectedFolderPath(VariableString rejectedFolderPath) {
		this.rejectedFolderPath = rejectedFolderPath;
	}
	
	public boolean isCopyFile() {
		return copyFile;
	}
	
	public void setCopyFile(boolean copyFile) {
		this.copyFile = copyFile;
	}

	public void updateInfo(String name, TypeSource type, VariableString folderPath, VariableString filter, VariableString attachmentFilter, boolean isNetworkFolder, boolean includeSubfolder, boolean isSecured, boolean useOutputName, VariableString outputName, VariableString url, VariableString port, WebServiceMethodDefinition method, String login, String password, CkanPackage ckanPackage, VariableString treatedFolderPath, boolean rejectNonMatchingMail, VariableString rejectedFolderPath, boolean copyFile) {
		setName(name);
		this.type = type;
		this.folderPath = folderPath;
		this.filter = filter;
		this.attachmentFilter = attachmentFilter;
		this.isNetworkFolder = isNetworkFolder;
		this.includeSubfolder = includeSubfolder;
		this.isSecured = isSecured;
		this.useOutputName = useOutputName;
		this.outputName = outputName;
		this.url = url;
		this.port = port;
		this.method = method;
		this.login = login;
		this.password = password;
		this.ckanPackage = ckanPackage;
		this.treatedFolderPath = treatedFolderPath;
		this.rejectNonMatchingMail = rejectNonMatchingMail;
		this.rejectedFolderPath = rejectedFolderPath;
		this.copyFile = copyFile;
	}
	
	@Override
	public List<Variable> getVariables() {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(folderPath != null ? folderPath.getVariables() : new ArrayList<Variable>());
		variables.addAll(filter != null ? filter.getVariables() : new ArrayList<Variable>());
		variables.addAll(attachmentFilter != null ? attachmentFilter.getVariables() : new ArrayList<Variable>());
		variables.addAll(outputName != null ? outputName.getVariables() : new ArrayList<Variable>());
		variables.addAll(url != null ? url.getVariables() : new ArrayList<Variable>());
		variables.addAll(method != null ? method.getWorkflowVariables() : new ArrayList<Variable>());
		variables.addAll(port != null ? port.getVariables() : new ArrayList<Variable>());
		variables.addAll(treatedFolderPath != null ? treatedFolderPath.getVariables() : new ArrayList<Variable>());
		variables.addAll(rejectedFolderPath != null ? rejectedFolderPath.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(folderPath != null ? folderPath.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(filter != null ? filter.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(attachmentFilter != null ? attachmentFilter.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(outputName != null ? outputName.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(url != null ? url.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(method != null ? method.getWorkflowParameters() : new ArrayList<Parameter>());
		parameters.addAll(port != null ? port.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(treatedFolderPath != null ? treatedFolderPath.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(rejectedFolderPath != null ? rejectedFolderPath.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}
}
