package bpm.vanilla.server.ui.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.server.client.communicators.TaskList;
import bpm.vanilla.server.client.communicators.TaskListParser;
import bpm.vanilla.server.ui.Activator;

public class TaskListManager {
	private List<TaskList> lists = new ArrayList<TaskList>();
	
	public TaskListManager(){}
	
	public void loadList(InputStream is) throws Exception{
		lists.add(TaskListParser.parse(is));
	}
	
	public List<TaskList> getLists(){
		return new ArrayList<TaskList>(lists);
	}
	
	public void addList(TaskList list){
		lists.add(list);
		
		//save file
		try{
			File f = new File(Activator.TASK_LIST_FOLDER);
			if (!f.exists()){
				f.mkdirs();
			}
			XMLWriter w = new XMLWriter(new FileOutputStream(Activator.TASK_LIST_FOLDER + "/" + list.getName() + ".xml"), OutputFormat.createPrettyPrint());
			w.write(list.getElement());
			w.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public void removeList(TaskList list){
		lists.add(list);
	}

	public void save() {
		for(TaskList l : lists){
			//save file
			try{
				File f = new File(Activator.TASK_LIST_FOLDER);
				if (!f.exists()){
					f.mkdirs();
				}
				XMLWriter w = new XMLWriter(new FileOutputStream(Activator.TASK_LIST_FOLDER + "/" + l.getName() + ".xml"), OutputFormat.createPrettyPrint());
				w.write(l.getElement());
				w.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
}
