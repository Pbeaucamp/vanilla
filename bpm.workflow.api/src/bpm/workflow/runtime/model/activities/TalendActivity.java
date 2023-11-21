package bpm.workflow.runtime.model.activities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.dom4j.Element;

import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.ActivityVariables;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.model.IComment;
import bpm.workflow.runtime.model.IConditionnable;

public class TalendActivity extends AbstractActivity implements IComment, IConditionnable {
	private static int number = 0;

	public List<ActivityVariables> listeVar = new ArrayList<ActivityVariables>();
	public ActivityVariables varSucceed;

	private String comment;

	private String fileName;
	private String className;

	public TalendActivity() {
		varSucceed = new ActivityVariables();

		varSucceed.setNomInterne("_succeed");
		varSucceed.setType(0);

		number++;
	}

	public TalendActivity(String name) {

		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;

		varSucceed = new ActivityVariables();
		varSucceed.setNomInterne("_succeed");
		varSucceed.setType(0);

	}

	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("talendActivity");

		if(comment != null) {
			e.addElement("comment").setText(comment);
		}
		if(fileName != null) {
			e.addElement("fileName").setText(fileName);
		}
		if(className != null) {
			e.addElement("className").setText(className);
		}

		return e;
	}

	@Override
	public IActivity copy() {
		TalendActivity act = new TalendActivity();
		act.setName("copy of " + name);
		act.setComment(comment);

		act.setFileName(fileName);
		act.setClassName(className);

		return act;
	}

	@Override
	public String getProblems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute() throws Exception {
		if(super.checkPreviousActivities()) {
			try {
				
				String parsedPath = fileName;
				parsedPath = workflowInstance.parseString(parsedPath);
				String command = parsedPath;
				String cmd = "";
				
				
				if(SystemUtils.IS_OS_WINDOWS){
					cmd = "cmd /c "+command;
				}
				else if(SystemUtils.IS_OS_LINUX){
					cmd = command;
				}
				
				Runtime r = Runtime.getRuntime(); 
				Process p = r.exec(cmd);
 				int res = p.waitFor();
				
				if(res != 0) {
					InputStream is = p.getErrorStream();
					throw new Exception(IOUtils.toString(is));
				}
				activityResult = true;

			} catch(Exception e) {
				activityResult = false;
				e.printStackTrace();
			} catch(Error e) {
				activityResult = false;
				e.printStackTrace();
			}
			super.finishActivity();
		}
	}

	@Override
	public List<ActivityVariables> getVariables() {
		return listeVar;
	}

	@Override
	public String getSuccessVariableSuffix() {
		return "_succeed";
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public void setComment(String text) {
		this.comment = text;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
