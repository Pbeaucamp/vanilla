package bpm.workflow.runtime.model.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;

/**
 * Launch an executable file
 * @author Charles MARTIN
 *
 */
public class StarterActivity extends AbstractActivity implements IComment {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	public static String BAT = "bat";
	public static String EXE = "exe";
	public static String SH = "sh";
	public static String PY = "py";
	public static String UNIX_Script = "script unix";
	
	public static String[] AVAILABLES_FORMATS = {BAT, EXE, SH, PY, UNIX_Script};

	private String comment;
	public String path;
	public String type;
	public String plateforme;
	private static int number = 0;


	/**
	 * do not use, for XML parsing only
	 */
	public StarterActivity(){
		number++;
	}

	public StarterActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	public IActivity copy() {
		StarterActivity a = new StarterActivity();
		a.setName("copy of " + name);
		return a;
	}
	/**
	 * 
	 * @return the type of the executable file to launch
	 */
	public String getTypeEXE() {

		return this.type;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();
		if (path == null) {
			buf.append("For activity" + name + "The path of the executable is not set.\n");
		}
		if (type == null) {
			buf.append("For activity" + name + "The type of the executable is not set.\n");
		}
		if (plateforme == null) {
			buf.append("For activity" + name + "The plateforme of the server is not set.\n");
		}

		return buf.toString();
	}

	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("starterEXEActivity");
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		if(this.path != null){
			e.addElement("path").setText(this.path);
		}
		if(this.type != null){
			e.addElement("typeExe").setText(this.type);
		}
		if(this.plateforme != null){
			e.addElement("plateforme").setText(this.plateforme);
		}

		return e;
	}

	/**
	 * Set the path of the executable file (with parameters if there are some)
	 * @param text
	 */
	public void setPath(String text) {
		this.path = text;

	}
	/**
	 * 
	 * @return the path of the executable to launch
	 */
	public String getPath(){
		return this.path;
	}

	/**
	 * Set the type of the executable
	 * @param text
	 */
	public void setTYPEExec(String text) {
		this.type = text;

	}
	/**
	 * 
	 * @return the platform of execution of the workflow
	 */
	public String getPlateforme() {
		return plateforme;
	}

	/**
	 * Set the platform of execution of the workflow
	 * @param plateforme
	 */
	public void setPlateforme(String plateforme) {
		this.plateforme = plateforme;
	}


	public List<String> getParameters() {
		return new ArrayList<String>();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String text) {
		this.comment = text;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			
			try {
				String parsedPath = path;
				parsedPath = workflowInstance.parseString(parsedPath);
//				File f = new File(path);
//				if (!f.canExecute()) {
//					throw new Exception("Invalid File " + path + ". Check if the file exist and can be executed");
//				}
				
				String cmd ="";
				String command = parsedPath;
				
				logger.info("Executing script");
				
				if(plateforme.equalsIgnoreCase("Windows_NT_XP_Vista")){
					logger.info("Windows environnement - NT / XP Vista");
					
					if(type.equalsIgnoreCase(StarterActivity.BAT) || type.equalsIgnoreCase(StarterActivity.SH)){
						cmd = "cmd /c "+command;
					}
					else if(type.equalsIgnoreCase(StarterActivity.EXE)){
						cmd ="cmd /c "+command;
					}
					else if (type.equalsIgnoreCase(StarterActivity.PY)) {
						cmd = command;
					}
				}
				else if(plateforme.equalsIgnoreCase("Windows_95_98")){
					logger.info("Windows environnement - 95 / 98");
					
					if(type.equalsIgnoreCase(StarterActivity.BAT) || type.equalsIgnoreCase(StarterActivity.SH)){
						cmd = "command.com /c "+command;
					}
					else if(type.equalsIgnoreCase(StarterActivity.EXE)){
						cmd ="command.com /cv "+command;
					}
					else if (type.equalsIgnoreCase(StarterActivity.PY)) {
						cmd = command;
					}
				}
				else if(plateforme.equalsIgnoreCase("UNIX")){
					logger.info("Unix environnement");
					
					if(type.equalsIgnoreCase(StarterActivity.SH)){
						cmd="sh " + command;
					}
					else if (type.equalsIgnoreCase(StarterActivity.PY)) {
						cmd = command;
					}
					else if(type.equalsIgnoreCase("UNIX_Script")){
						cmd = command;
					}
				}

				logger.info("Launching cmd '" + cmd + "'");
				Runtime r = Runtime.getRuntime();

				Process proc = r.exec(cmd);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

				String line = null;
				logger.debug("Command output");

				while ((line = stdInput.readLine()) != null)
					logger.debug(line);

				int exitVal = proc.waitFor();
				logger.info("Process finish with value '" + exitVal + "'");
				if (exitVal != 0) {
					InputStream is = proc.getErrorStream();
					throw new Exception(IOUtils.toString(is));
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
