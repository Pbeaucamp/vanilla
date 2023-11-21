package bpm.workflow.runtime.model.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;


public class CheckPathActivity extends AbstractActivity implements IConditionnable,IComment,IServer{

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public String path;
	private String comment;
	private String varRefName;
	public FileServer fileserver;
	private static int number = 0;
	
	/**
	 * do not use, only for xml
	 */
	public CheckPathActivity(){
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
		number++;
	}
	
	public CheckPathActivity(String name) {
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

	public CheckPathActivity copy() {
		CheckPathActivity a = new CheckPathActivity();
		
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
		e.setName("checkPathActivity");
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
			buf.append("For activity " + name + ", the file server is not set.\n");
		}
		if (path == null) {
			buf.append("For activity " + name + ", the path is not set.\n");
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
	 * Set the path to check
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;
		
	}
	/**
	 * 
	 * @return the path to check
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
		return FileServer.class;
	}
	
	public void setServer(IResource server) {
		this.fileserver = (FileServer) server;
	}
	public void setServer(String text) {

		fileserver = (FileServer) ListServer.getInstance().getServer(text);

	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
	
			try {
				String parsedPath = path;
				if(path.contains("{$")){
					parsedPath = workflowInstance.parseString(path);
				}
				
				String url = workflowInstance.parseString(fileserver.getUrl());
				String repertoiredef = workflowInstance.parseString(fileserver.getRepertoireDef());
				if(fileserver.getTypeServ().equalsIgnoreCase("Hard Drive")){
					try{
						url = workflowInstance.parseString(url);

						if(!url.endsWith(File.separator) && repertoiredef != null){
							url = url + File.separator;
						}

						StringBuffer cheminHD = new StringBuffer();
						cheminHD.append(url);

						if(repertoiredef != null){
							cheminHD.append(repertoiredef + File.separator);
						}

						cheminHD.append(parsedPath);
						
						String testchemin = cheminHD.toString();
						Logger.getLogger(getClass()).info("check if file exist " + testchemin);
						File ips = new File(testchemin);
						if(ips.exists()) {
							activityResult = true;
						}
						else {
							activityResult = false;
						}
						
					}
					catch(Exception e){
						
						Logger.getLogger(getClass()).info("File not found in check file " + e.getMessage());
						throw e;
					}
				}
				else if(fileserver.getTypeServ().equalsIgnoreCase("FTP Server")){

					activityResult = false;

					FTPClient ftp = new FTPClient();
					ftp.connect(url);
					ftp.login( fileserver.getLogin(), fileserver.getPassword() );
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					Logger.getLogger(getClass()).info(" Connected to " + url + ".");
					Logger.getLogger(getClass()).info(ftp.getReplyString());

					if(repertoiredef.endsWith(File.separator)){
						String temp = repertoiredef.substring(0, repertoiredef.length()-1);
						repertoiredef = temp;
					}
					// List the files in the directory
					ftp.changeWorkingDirectory( repertoiredef );
					FTPFile[] files = ftp.listFiles();

					for(FTPFile file : files){
						if(file.getName().equalsIgnoreCase(parsedPath)){
							activityResult = true;
						}
					}

					if (!activityResult)
						Logger.getLogger(getClass()).info("File not found in check file");
					
					ftp.logout();
					ftp.disconnect();
				}
			} catch(Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
			
			super.finishActivity();
		}
	}

}
