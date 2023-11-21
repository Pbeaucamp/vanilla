package bpm.vanilla.server.ui.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.platform.core.beans.tasks.TaskList;
import bpm.vanilla.platform.core.beans.tasks.TaskListParser;
import bpm.vanilla.server.ui.Activator;
import bpm.vanilla.server.ui.Messages;

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
			list.saveIn(new FileOutputStream(Activator.TASK_LIST_FOLDER + "/" + list.getName() + ".xml")); //$NON-NLS-1$ //$NON-NLS-2$
//			XMLWriter w = new XMLWriter(, OutputFormat.createPrettyPrint());
//			w.write(list.getElement());
//			w.close();
		}catch(Throwable ex){
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
				FileOutputStream fos = new FileOutputStream(Activator.TASK_LIST_FOLDER + "/" + l.getName() + ".xml");  //$NON-NLS-1$ //$NON-NLS-2$
				l.saveIn(fos);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
}
