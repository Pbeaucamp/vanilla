package bpm.workflow.runtime.model.activities.filemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IAcceptInput;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IFileServerPut;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;

/**
 * Put a file from Vanilla_files/tempBIW/name of the process in a FTP location
 * @author Charles MARTIN
 *
 */
public class PutFTPActivity extends AbstractActivity implements IFileServerPut, IComment, IAcceptInput {

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public FileServer fileserver;
	private String comment;
	private static int number = 0;
	private List<String> filesToMoveProvider = new ArrayList<String>();
	protected String nomInterne = "_getFTPResult";
	
	/**
	 * do not use, only for xml
	 */
	public PutFTPActivity(){
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}
	
	public PutFTPActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}

	public PutFTPActivity copy() {
		PutFTPActivity a = new PutFTPActivity();
		
		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
	
		return a;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("PutFTPActivity");

		if (fileserver != null){
			e.addElement("fileserver").setText(fileserver.getName());
		}
	
		for(String s : filesToMoveProvider){
			e.addElement("filestomove").setText(s);
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
	public String getRepository() {
		return null;
	}
	public void setRepository(String repository) {
		
	}
	

	public void addInput(String input) {
		this.filesToMoveProvider.add(input);
	}


	public List<String> getInputs() {
		return this.filesToMoveProvider;
	}


	public void removeInput(String input) {
		this.filesToMoveProvider.remove(input);
	}


	public void setInputs(List<String> inputs) {
		this.filesToMoveProvider = inputs;		
	}
	
	public String getPhrase() {
		return "Select the activities which provide files";
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {

			try{
				String repertoiredef1 = fileserver.getRepertoireDef();
				String url1 = fileserver.getUrl();
				StringBuffer cheminbuf1 = new StringBuffer();
				cheminbuf1.append(url1);

				if(repertoiredef1.endsWith(File.separator)){
					String temp = repertoiredef1.substring(0, repertoiredef1.length()-1);
					repertoiredef1 = temp;
				}
				cheminbuf1.append(repertoiredef1);
		
				List<String> toMove = new ArrayList<String>();
				String path = "";
				
				for(String s : filesToMoveProvider){
					path = s;
					path = workflowInstance.parseString(path);
					
					if (path.indexOf("root") == -1) {
						toMove.add(path);
					}
					else if (path.indexOf("root") > -1) {
						Document doc = DocumentHelper.parseText(path);
						Element root = doc.getRootElement();
						for (Object o : root.elements("doc")) {							
							toMove.add(((Element) o).element("path").getStringValue());	
						}
					}
						
				}
				for (String filename : toMove) {
					if(!filename.equalsIgnoreCase("0")){
//						FileSystemManager fsManager = VFS.getManager();
//						FileObject object = fsManager.resolveFile(filename);
						InputStream filesource=new FileInputStream(filename);
						try{
//							filesource = object.getContent().getInputStream();
							FTPClient ftp = new FTPClient();
							ftp.connect(url1);
							ftp.login(fileserver.getLogin(), fileserver.getPassword());
							ftp.setFileType(FTP.BINARY_FILE_TYPE);
							boolean dir = ftp.changeWorkingDirectory( repertoiredef1 );
							String name = filename;
							if (name.indexOf(File.separator) > -1) {
								name = name.substring(name.lastIndexOf(File.separator)+1);
							}
							else if (name.indexOf("/") > -1) {
								name = name.substring(name.lastIndexOf("/")+1);
							}
							boolean worked = ftp.storeFile(name, filesource);
							int i = ftp.getReplyCode();
							ftp.logout();
							ftp.disconnect();
		
						}catch(FileNotFoundException nf){
							nf.printStackTrace();
						}catch(IOException io){
							io.printStackTrace();
						}finally{
							try{
		
								filesource.close();
		
							}catch(Exception e){
								e.printStackTrace();
							}
							try{
		
							}catch(Exception e){
								e.printStackTrace();
							}
						} 
					}
				}
				activityResult = true;
			}
			catch(Exception e){
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}
			
			
			
			super.finishActivity();
		}
	}

}
