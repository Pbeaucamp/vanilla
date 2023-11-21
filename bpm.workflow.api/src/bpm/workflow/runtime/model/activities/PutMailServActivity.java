package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IFileServerPut;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;

/**
 * Not implemented
 * @author Charles MARTIN
 *
 */
public class PutMailServActivity extends AbstractActivity implements IFileServerPut,IComment,IConditionnable {

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public String fileserver;
	private String comment;
	private String varRefName;
	private FileServer server;
	private static int number = 0;
	
	/**
	 * do not use, only for xml
	 */
	public PutMailServActivity(){
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_getFTPResult");
		varSucceed.setType(0);
		number++;
	}
	
	public PutMailServActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_getFTPResult");
		varSucceed.setType(0);
	}
	
	public String getSuccessVariableSuffix() {
		return "_getFTPResult";
	}

	public PutMailServActivity copy() {
		PutMailServActivity a = new PutMailServActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		if (varRefName != null)
			a.setVariable(varRefName);
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("PutMailServActivity");

		if (fileserver != null){
		e.addElement("fileserver").setText(this.fileserver);
		}
		if (varRefName != null) {
			e.addElement("varRefName").setText(varRefName);
		}
		
		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}

	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);
		
		return listeVar;
	}
	
	public String getVariable() {
		return varRefName;
	}



	public void setVariable(String varRefName) {
		this.varRefName = varRefName;
	}
	
	public void setFileServer(String text) {
		this.fileserver = text;
		
	}
	public String getFileServer(){
		return this.fileserver;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String text) {
		this.comment = text;
	}
	public IResource getServer() {
		return server;
	}
	public Class<?> getServerClass() {
		return FileServer.class;
	}
	public void setServer(IResource iserver) {
		this.server = (FileServer)iserver;
	}
	public String getRepository() {
		return null;
	}
	public void setRepository(String repository) {
		
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		//Not used
	}

}
