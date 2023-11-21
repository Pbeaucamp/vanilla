package bpm.workflow.runtime.model.activities.filemanagement;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;

/**
 * Put a file from Vanilla_files/tempBIW/name of the process in a FTP location
 * @author Charles MARTIN
 *
 */
public class PutHardDActivity extends AbstractActivity implements IAcceptInput, IFileServer, IComment {

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public FileServer fileserver;

	private String comment;
	private static int number = 0;
	
	
	/**
	 * do not use, only for xml
	 */
	public PutHardDActivity(){
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_getFTPResult");
		varSucceed.setType(0);
		number++;
	}
	
	public PutHardDActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_getFTPResult");
		varSucceed.setType(0);
	}

	public PutHardDActivity copy() {
		PutHardDActivity a = new PutHardDActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("PutHardDActivity");


		if (fileserver != null){
			e.addElement("fileserver").setText(fileserver.getName());
		}
		
		
		
		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();

		if (fileserver == null) {
			buf.append("For activity " + name + ", the file server is not set.\n");
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
	public IResource getServer() {
		return fileserver;
	}
	public Class<?> getServerClass() {
		return FileServer.class;
	}
	public void setServer(IResource server) {
		this.fileserver = (FileServer) server;
	}
	
	public void decreaseNumber() {
		number--;
	}

	public void addInput(String input) {
		
		
	}

	public List<String> getInputs() {
		return new ArrayList<String>();
	}

	public void removeInput(String input) {
		
		
	}

	public void setInputs(List<String> inputs) {
		
		
	}


	public String getType() {
		
		return null;
	}
	
	public String getPhrase() {
		return "Select the activities which provide files";
	}

	@Override
	public void execute() throws Exception {
		
		
	}

}
