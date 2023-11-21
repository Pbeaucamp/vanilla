package bpm.workflow.runtime.model.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;

/**
 * Delete a folder and its contents
 * 
 * @author Charles MARTIN
 * 
 */
public class DeleteFolderActivity extends AbstractActivity implements IComment, IServer {

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public String path;
	private String comment;
	private String varRefName;
	public FileServer fileserver;
	private static int number = 0;

	public DeleteFolderActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
	}

	public DeleteFolderActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;

		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);

	}

	public DeleteFolderActivity copy() {
		DeleteFolderActivity a = new DeleteFolderActivity();

		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);
		if(varRefName != null)
			a.setVariable(varRefName);
		return a;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("deleteFolderActivity");
		if(comment != null) {
			e.addElement("comment").setText(comment);
		}
		if(path != null) {
			e.addElement("path").setText(this.path);
		}
		if(varRefName != null) {
			e.addElement("varRefName").setText(varRefName);
		}
		if(fileserver != null) {
			e.addElement("fileserver").setText(fileserver.getName());
		}

		return e;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();

		if(fileserver == null) {
			buf.append("For activity " + name + ", the file server is not set.\n");
		}
		if(path == null) {
			buf.append("For activity " + name + ", the folder name is not set.\n");
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
	 * Set the path of the folder to delete
	 * 
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;

	}

	/**
	 * 
	 * @return the path of the folder to delete
	 */
	public String getPath() {
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
				if(path.contains("{$")) {
					path = workflowInstance.parseString(path);
				}
				String url = fileserver.getUrl();
				String repertoiredef = fileserver.getRepertoireDef();

				if(fileserver.getTypeServ().equalsIgnoreCase("Hard Drive")) {
					try {
						url = workflowInstance.parseString(url);;

						if(!url.endsWith(File.separator) && repertoiredef != null) {
							url = url + File.separator;
						}

						StringBuffer cheminHD = new StringBuffer();
						cheminHD.append(url);

						if(repertoiredef != null) {
							cheminHD.append(repertoiredef + File.separator);
						}

						cheminHD.append(path);
						String testchemin = cheminHD.toString();

						File directory = new File(testchemin);
						for(File file : directory.listFiles()) {
							file.delete();
						}
						directory.delete();

					} catch(Exception e) {
						Logger.getLogger(getClass()).error(e.getMessage(), e);
						throw e;
					}
				}
				else if(fileserver.getTypeServ().equalsIgnoreCase("FTP Server")) {

					FTPClient ftp = new FTPClient();
					ftp.connect(url);
					ftp.login(fileserver.getLogin(), fileserver.getPassword());
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					Logger.getLogger(getClass()).info("Connected to " + url + ".");
					Logger.getLogger(getClass()).info(ftp.getReplyString());

					if(repertoiredef.endsWith(File.separator)) {
						String temp = repertoiredef.substring(0, repertoiredef.length() - 1);
						repertoiredef = temp;
					}
					// List the files in the directory
					ftp.changeWorkingDirectory(repertoiredef + File.separator + path);

					FTPFile[] files = ftp.listFiles();

					for(FTPFile file : files) {
						ftp.deleteFile(repertoiredef + File.separator + path + File.separator + file.getName());

					}

					ftp.dele(repertoiredef + File.separator + path);
					ftp.logout();
					ftp.disconnect();
				}

				activityResult = true;
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
