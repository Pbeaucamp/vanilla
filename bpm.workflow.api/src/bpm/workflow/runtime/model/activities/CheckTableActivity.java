package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.ListServer;


public class CheckTableActivity extends AbstractActivity implements IConditionnable,IComment,IServer{

	private List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	private ActivityVariables varSucceed;
	private String path;
	private String comment;
	private String varRefName;
	private DataBaseServer fileserver;
	private static int number = 0;
	
	/**
	 * do not use, only for xml
	 */
	public CheckTableActivity(){
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
		number++;
	}
	
	public CheckTableActivity(String name) {

		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
	
	}
	
	public String getSuccessVariableSuffix() {
		return "_pathResult";
	}

	public CheckTableActivity copy() {
		CheckTableActivity a = new CheckTableActivity();
		
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
		e.setName("checkTableActivity");
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if (path != null){
		e.addElement("path").setText(this.path);
		}
		if (varRefName != null) {
			e.addElement("varRefName").setText(varRefName);
		}
		if (fileserver != null){
			e.addElement("fileserver").setText(fileserver.getName());
			}
		
		
		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		
		if (fileserver == null) {
			buf.append("For activity " + name + ", the database server is not set.\n");
		}
		if (path == null) {
			buf.append("For activity " + name + ", the table is not set.\n");
		}
		
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
	
	/**
	 * Set the name of the table to check
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;
		
	}
	/**
	 * 
	 * @return the name of the table to check
	 */
	public String getPath(){
		return this.path;
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
		return DataBaseServer.class;
	}
	
	public void setServer(IResource server) {
		this.fileserver = (DataBaseServer) server;
	}
	public void setServer(String text) {

		fileserver = (DataBaseServer) ListServer.getInstance().getServer(text);

	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			if(path.contains("{$")){
				path = workflowInstance.parseString(path);
			}
				
			try{
				SQLConnection connection = FactorySQLConnection.getInstance().createConnection(
						fileserver.getJdbcDriver(), 
						fileserver.getUrl(), 
						fileserver.getPort(), 
						fileserver.getDataBaseName(), 
						fileserver.getLogin(), 
						fileserver.getPassword(), 
						fileserver.getSchemaName(),false,"");
				
				
				List<List<String>> res = connection.executeQuery("select * from "+path+" where 1=0", null, null);
				Logger.getLogger(getClass()).info("size of the resultset " + res.size());
				activityResult = true;
			}
			catch(Exception en){
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = en.getMessage();
				en.printStackTrace();
			}
			
			
			super.finishActivity();
		}
	}

}
