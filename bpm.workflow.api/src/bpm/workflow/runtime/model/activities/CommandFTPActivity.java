package bpm.workflow.runtime.model.activities;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPReply;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.ICommand;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IFileServerPut;
import bpm.workflow.runtime.resources.IResource;
import bpm.workflow.runtime.resources.servers.FileServer;
import bpm.workflow.runtime.resources.servers.ListServer;

public class CommandFTPActivity extends AbstractActivity implements IFileServerPut, IComment, ICommand {

	private static int number = 0;

	public FileServer fileserver;

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;

	private String comment;
	protected String nomInterne = "_getFTPResult";

	private String command;

	/**
	 * do not use, only for xml
	 */
	public CommandFTPActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}

	public CommandFTPActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;

		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne(nomInterne);
		varSucceed.setType(0);
	}

	public CommandFTPActivity copy() {
		CommandFTPActivity a = new CommandFTPActivity();

		a.setName("copy of " + name);
		a.setDescription(description);
		a.setPositionX(xPos);
		a.setPositionY(yPos);

		return a;
	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("CommandFTPActivity");

		if (fileserver != null) {
			e.addElement("fileserver").setText(fileserver.getName());
		}

		if (command != null) {
			e.addElement("command").setText(command);
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

	public String getPhrase() {
		return "Select the activities which provide files";
	}

	@Override
	public void execute() throws Exception {
		if (super.checkPreviousActivities()) {

			try {
				String repertoiredef1 = fileserver.getRepertoireDef();
				String url1 = fileserver.getUrl();
				StringBuffer cheminbuf1 = new StringBuffer();
				cheminbuf1.append(url1);

				if (repertoiredef1.endsWith(File.separator)) {
					String temp = repertoiredef1.substring(0, repertoiredef1.length() - 1);
					repertoiredef1 = temp;
				}
				cheminbuf1.append(repertoiredef1);

				String command = workflowInstance.parseString(getCommand());

				FTPClient ftp = new FTPClient();
				ftp.connect(url1);
				ftp.login(fileserver.getLogin(), fileserver.getPassword());
				ftp.setType(FTPClient.TYPE_BINARY);
				
				if(repertoiredef1 != null && !repertoiredef1.isEmpty()) {
					ftp.changeDirectory(repertoiredef1);
				}
				
				if(command.contains("RNFR")) {
					try {
						String part1 = command.substring(command.indexOf("RNFR"), command.indexOf("RNTO") - 1);
						String part2 = command.substring(command.indexOf("RNTO"));

						FTPReply ftpReply = ftp.sendCustomCommand(part1);
						if(ftpReply.getCode() != 350 && !ftpReply.isSuccessCode()) {
							StringBuffer message = new StringBuffer();
							for(String mess : ftpReply.getMessages()) {
								message.append(mess + "     ");
							}
							System.out.println(message);
						}
						
						FTPReply ftpReply2 = ftp.sendCustomCommand(part2);
						
						if(!ftpReply2.isSuccessCode()) {
							StringBuffer message = new StringBuffer();
							for(String mess : ftpReply.getMessages()) {
								message.append(mess + "     ");
							}
							
							throw new Exception("Command failed : " + message.toString());
						}
					} catch (Exception e) {
						throw new Exception("A RNFR command must be going with a RNTO command.");
					}
				}
				else {
					FTPReply ftpReply = ftp.sendCustomCommand(command);
					if(!ftpReply.isSuccessCode()) {
						StringBuffer message = new StringBuffer();
						for(String mess : ftpReply.getMessages()) {
							message.append(mess + "     ");
						}
						
						throw new Exception("Command failed : " + message.toString());
					}
				}

				ftp.disconnect(true);

				activityResult = true;
			} catch (Exception e) {
				activityResult = false;
				activityState = ActivityState.FAILED;
				failureCause = e.getMessage();
				e.printStackTrace();
			}

			super.finishActivity();
		}
	}

	@Override
	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}
}
