package bpm.workflow.runtime.model.activities.filemanagement;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.ICanFillVariable;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;
import bpm.workflow.runtime.model.IOutput;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;

/**
 * 
 * @author vanilla
 *
 */
public class BrowseContentActivity extends AbstractActivity implements IOutput, IComment, IConditionnable, IFileTreatement, ICanFillVariable {
	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public FileServer fileserver;
	private String comment;
	public String filesName;
	private static int number = 0;
	private String currentFilePath;
	public static final String SUCCES_SUFFIX = "_browsecontent";
	private static final String OUTPUT_PREFIX = "fileName_";
	
	private List<String> outputs = new ArrayList<String>();
	private String varToFill;
	
	public BrowseContentActivity() {
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_browsecontent");
		varSucceed.setType(0);
		number++;
	}
	
	public BrowseContentActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
		
		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne(SUCCES_SUFFIX);
		varSucceed.setType(0);
		currentFilePath = this.getId() + "_outputpath";
		
		varToFill = currentFilePath;
		
		outputs.add(OUTPUT_PREFIX + this.id);
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		currentFilePath = this.getId() + "_outputpath";
		
		varToFill = currentFilePath;
	}

	public String getOutputVariable() {
		return currentFilePath;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	public String getSuccessVariableSuffix() {
		return SUCCES_SUFFIX;
	}

	public List<ActivityVariables> getVariables() {
		listeVar.add(varSucceed);
		return listeVar;
	}

	public IActivity copy() {
		return null;
	}

	public void decreaseNumber() {
		number--;		
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
	
	public String getType() {
		return fileserver.getTypeServ();
	}

	public IResource getServer() {
		return fileserver;
	}

	public Class<?> getServerClass() {
		return FileServer.class;
	}

	public void setServer(IResource server) {
		if (server instanceof FileServer) {
			fileserver = (FileServer) server;
		}
	}
	public void setServer(String text) {
		fileserver = (FileServer) ListServer.getInstance().getServer(text);
	}

	public String getFilesToTreat() {
		return filesName;
	}

	public void setFilesToTreat(String filesName) {
		this.filesName = filesName;
	}
	
	/**
	 * ONLY FOR DIGESTER
	 * @param name
	 */
	public void setParameter(String name) {
		this.outputs = new ArrayList<String>();
		this.outputs.add(name);
	}
	
	/**
	 * ONLY FOR DIGESTER
	 * @param name
	 */
	public void setVarToFill(String name) {
		this.varToFill = name;
	}

	public void addParameterMapping(String activity, String variableName) {
		this.varToFill = variableName;;
	}


	public void removeParameterMapping(String form, String biObject) {
		this.varToFill = null;
	}
	
	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("browsecontent");
		if (fileserver != null){
			e.addElement("fileserver").setText(fileserver.getName());
		}
		if (filesName != null) {
			e.addElement("filestotreat").setText(filesName);
		}
		
		if (varToFill != null) {
			e.addElement("vartofill").setText(varToFill);
		}
		
		e.addElement("parameter").setText(outputs.get(0));
		
		return e;
		
	}

	public List<String> getParameters(IRepositoryApi sock) {
		return outputs;
	}
	
	private List<String> allreadyTreated = new ArrayList<String>();

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			try {
				try {
					if (fileserver.getTypeServ().equalsIgnoreCase(FileServer.SERVER_FTP)) {
						browseFtpContent();
					}
					else if (fileserver.getTypeServ().equals(FileServer.SERVER_FILES)) {
						activityResult = browseHDContent();
					}

				} catch(Exception e) {
					//return true only when there's no file left
					activityResult = true;
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

	private boolean browseHDContent() {

		String repertoiredef = workflowInstance.parseString(fileserver.getRepertoireDef());
		String url = workflowInstance.parseString(fileserver.getUrl());
		String filestotreat = workflowInstance.parseString(filesName);
		
		String path = url;

		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}

		if(repertoiredef != null){
			path += repertoiredef;
		}
		
		if (!path.endsWith(File.separator)) {
			path += "/";
		}
		if (!path.endsWith("/")) {
			path += "/";
		}
		
		File repertoire = new File(path);
		String [] listefichiers = repertoire.list();
		
		for (int i = 0; i < listefichiers.length; i++) {
			if (allreadyTreated.contains(listefichiers[i])) {
				continue;
			}
			File f = new File(path + listefichiers[i]);
			
			if (f.isDirectory()) {
				continue;
			}
			allreadyTreated.add(listefichiers[i]);
			
			if (filestotreat.equals("*.*")) {
				if  (varToFill != null) {
					workflowInstance.getOrCreateVariable(currentFilePath).addValue(path + listefichiers[i], true);
				}
			}
			else if (filestotreat.indexOf("*.") > -1) {
				String extension = filestotreat.replace("*.", "");
				if (listefichiers[i].endsWith(extension)) {
					if  (currentFilePath != null) {
						workflowInstance.getOrCreateVariable(currentFilePath).addValue(path + listefichiers[i]);
					}
				}
				else {
					continue;
				}
			}
			else {
				if (listefichiers[i].equals(filestotreat)) {
					if  (varToFill != null) {
						workflowInstance.getOrCreateVariable(currentFilePath).addValue(path + listefichiers[i]);
					}
				}
			}
			
//			return false;
			
		}
		
		return true;
		
	}

	private void browseFtpContent() throws Exception {
		try {
			FTPClient ftp = new FTPClient();
			ftp.connect(fileserver.getUrl());
			ftp.login( fileserver.getLogin(), fileserver.getPassword() );
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			String repertoiredef = fileserver.getRepertoireDef();
			if(repertoiredef != null && repertoiredef.endsWith(File.separator)){
				String temp = repertoiredef.substring(0, repertoiredef.length()-1);
				repertoiredef = temp;
			}
			
			ftp.changeWorkingDirectory( repertoiredef );
			FTPFile[] files = ftp.listFiles();

			for (FTPFile file : files) {
				if (allreadyTreated.contains(file.getName()) || file.isDirectory() || file.isSymbolicLink() || file.isUnknown()) {
					continue;
				}
				
				String filePath = "temp/" + file.getName();
				
				ftp.retrieveFile(repertoiredef + "/" + file.getName(), new FileOutputStream(filePath));
				
				allreadyTreated.add(file.getName());
				if (filesName.equalsIgnoreCase("*.*")) {
					
					if  (varToFill != null) {
						workflowInstance.getOrCreateVariable(varToFill).addValue(filePath);
						return;
					}

				}
				else if (filesName.indexOf("*.") > -1) {
					String extension = filesName.replace("*.", "");
					if (file.getName().endsWith(extension)) {			
						if  (varToFill != null) {
							workflowInstance.getOrCreateVariable(varToFill).addValue(filePath);
							return;
						}
					}
					
				}
				else {
					if (filesName.contains(file.getName())) {
						if  (varToFill != null) {
							workflowInstance.getOrCreateVariable(varToFill).addValue(filePath);
							return;
						}

					}
				}

			}
			activityResult = true;
			finishActivity();
		}
		catch (Exception e) {
			e.printStackTrace(); 
			activityResult = false;
			activityState = ActivityState.FAILED;
			failureCause = e.getMessage();
			
			super.finishActivity();
		}
	}

}
