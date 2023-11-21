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
 * Delete a file on a Hard Disk or on a FTP
 * 
 * @author Charles MARTIN
 * 
 */
public class DeleteFileActivity extends AbstractActivity implements IComment, IServer {

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;
	public String path;
	private String comment;
	private String varRefName;
	public FileServer fileserver;
	private static int number = 0;

	public DeleteFileActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);
		number++;
	}

	public DeleteFileActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;

		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_pathResult");
		varSucceed.setType(0);

	}

	public DeleteFileActivity copy() {
		DeleteFileActivity a = new DeleteFileActivity();

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
		e.setName("deleteFileActivity");
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
			buf.append("For activity " + name + ", the file name is not set.\n");
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
	 * Set the path of the file to delete
	 * 
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;

	}

	/**
	 * 
	 * @return the path of the file to delete
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
				String parsedPath = path;
				if(path.contains("{$")){
					parsedPath = workflowInstance.parseString(path);
				}
				String url = fileserver.getUrl();
				String repertoiredef = fileserver.getRepertoireDef();

				if(fileserver.getTypeServ().equalsIgnoreCase("Hard Drive")) {
					try {

						url = workflowInstance.parseString(url);

						if(!url.endsWith(File.separator) && repertoiredef != null) {
							url = url + File.separator;
						}
						parsedPath = url + parsedPath;
						if(parsedPath.indexOf("*") == -1) {
							Logger.getLogger(getClass()).info("trying to delete file " + parsedPath);

							File filetodelete = new File(parsedPath);
							filetodelete.delete();
						}
						else {
							if(parsedPath.equalsIgnoreCase("*.*")) {
								File f = new File(parsedPath.toString());
								if(f.isDirectory()) {
									for(File child : f.listFiles()) {
										if(child.isFile()) {
											Logger.getLogger(getClass()).info("trying to delete file " + child.getAbsolutePath());
											child.delete();
										}
									}
								}
							}
							else {
								String extension = parsedPath.replace("*.", "");
								File f = new File(parsedPath.toString());
								if(f.isDirectory()) {
									for(File child : f.listFiles()) {
										if(child.isFile() && child.getAbsolutePath().endsWith(extension)) {
											Logger.getLogger(getClass()).info("trying to delete file " + child.getAbsolutePath());
											child.delete();
										}
									}
								}
							}
						}

					} catch(Exception e) {
						e.printStackTrace();
						throw e;
					}
				}

				else if(fileserver.getTypeServ().equalsIgnoreCase("FTP Server")) {

					FTPClient ftp = new FTPClient();
					ftp.connect(url);
					ftp.login(fileserver.getLogin(), fileserver.getPassword());
					ftp.setFileType(FTP.BINARY_FILE_TYPE);

					if(repertoiredef.endsWith(File.separator)) {
						String temp = repertoiredef.substring(0, repertoiredef.length() - 1);
						repertoiredef = temp;
					}
					// List the files in the directory
					ftp.changeWorkingDirectory(repertoiredef);
					FTPFile[] files = ftp.listFiles();

					for(FTPFile file : files) {
						if(file.getName().equalsIgnoreCase(parsedPath)) {
							ftp.deleteFile(repertoiredef + File.separator + file.getName());
						}

					}

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
