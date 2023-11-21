package bpm.workflow.runtime.model.activities;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.workflow.runtime.model.AbstractActivity;
import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.runtime.resources.TaskListObject;

/**
 * This class is the model reprensetation of an activity
 * that will run a TaskList object from a VanillaRepository
 * on a VanillaRuntimeServer
 * 
 * @author ludo
 *
 */
public class TaskListActivity extends AbstractActivity {

	private TaskListObject taskListObject;
	private TaskListType listType = TaskListType.REPORT;
	private static int number = 0;
	
	public TaskListActivity() {
		number++;
	}
	
	public TaskListActivity(String name) {
		this.name = name.replaceAll("[^a-zA-Z0-9]", "") + number;
		this.id = this.name.replaceAll("[^a-zA-Z0-9]", "");
		number++;
	}

	private String comment;
	
	public IActivity copy() {
		return null;
	}

	public String getProblems() {
		StringBuffer buf = new StringBuffer();

					
		return buf.toString();
	}



	public String getItemName() {
		if (taskListObject != null){
			return taskListObject.getItemName();
		}
		else{
			return null;
		}
			
	}

	public void setItemName(String name) {
		if (taskListObject != null){
			taskListObject.setItemName(name);
		}
	}




	@Override
	public Element getXmlNode() {
		Element e = super.getXmlNode();
		e.setName("taskListActivity");
		e.addElement("type").setText(listType.name());
		
		if(comment != null){
			e.addElement("comment").setText(comment);
		}
		
		if (taskListObject != null){
			e.add(taskListObject.getXmlNode());
		}
				
		
		
		
		return e;
	}
	/**
	 * do not use, only existing for parsing XML
	 */
	public void setTaskListObject(TaskListObject taskListObject){
		this.taskListObject = taskListObject;
	}
	
	
	public TaskListObject getTaskListObject(){
		return this.taskListObject;
	}
	public List<String> getParameters(IRepositoryApi sock) {
		if (taskListObject != null) {
			try {
				return taskListObject.getParameters(sock);
			} catch (Exception e) {
				return new ArrayList<String>();
			}
		}
		else {
			return new ArrayList<String>();
		}
	}

	public TaskListType getType() {
		return listType;
		
	}
	public void setType(String typeName) {
		try{
			listType = TaskListType.valueOf(typeName);
		}catch(Exception ex){
			
		}
		
	}
	
	public void decreaseNumber() {
		number--;
	}

	@Override
	public void execute() throws Exception {
		//Not used.
	}
}
