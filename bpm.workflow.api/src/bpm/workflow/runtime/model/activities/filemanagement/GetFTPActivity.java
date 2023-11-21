package bpm.workflow.runtime.model.activities.filemanagement;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IOutputProvider;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.variables.ListVariable;

/**
 * Get a file from a FTP and put it under Vanilla_files/tempBIW/name of the process
 * @author Charles MARTIN
 *
 */
public class GetFTPActivity extends AbstractActivity implements IOutputProvider, IFileTreatement, IComment, IConditionnable {

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public FileServer fileserver;
	private String comment;
	private static int number = 0;
	private String filePath;
	public String filesName;
	private String pathToStore = ListVariable.VANILLA_TEMPORARY_FILES;
	protected String nomInterne = "_getFTPResult";

	public GetFTPActivity(){
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
		number++;
	}
	
	public GetFTPActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
		filePath = this.getId() + "_outputpath";
	
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		filePath = this.getId() + "_outputpath";
	}
	
	public String getSuccessVariableSuffix() {
		return "_getFTPResult";
	}

	public GetFTPActivity copy() {
		GetFTPActivity a = new GetFTPActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);

		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("GetFTPActivity");
		e.addElement("pathtostore").setText(pathToStore);
		
		if (fileserver != null){
			e.addElement("fileserver").setText(fileserver.getName());
		}
		if (filesName != null) {
			e.addElement("filestotreat").setText(filesName);
		}

		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (fileserver == null) {
			buf.append("For activity " + name + ", the file server is not set.\n");
		}
		if (filesName == null) {
			buf.append("For activity " + name + ", no file has been set.\n");
		}
			
		return buf.toString();
	}

	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);
		
		return listeVar;
	}
	
	public void setServer(String text) {
		fileserver = (FileServer) ListServer.getInstance().getServer(text);
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String text) {
		this.comment = text;
	}
	
	public Class<?> getServerClass() {
		return FileServer.class;
	}
	public IResource getServer() {
		return fileserver;
	}
	public void setServer(IResource server) {
		this.fileserver = (FileServer) server;
	}

	public void decreaseNumber() {
		number--;
	}

	public String getOutputName() {
		return "";
	}
	public void setOutputName(String outputName) {
	}

	public String getOutputVariable() {
		return filePath;
	}

	public String getPathToStore() {
		return pathToStore;
	}

	public void setPathToStore(String pathToStore) {
		this.pathToStore = pathToStore;
	}

	public String getFilesToTreat() {
		return filesName;
	}

	public void setFilesToTreat(String filesName) {
		this.filesName = filesName;		
	}

	public String getType() {
		if (getServer() != null) {
			return this.fileserver.getTypeServ();
		}
		return "";
	}

	public void setNomInterne(String nomInterne) {
		this.nomInterne = nomInterne;
	}

	@Override
	public void execute() throws Exception {
		
		
	}
	
	
}
