package bpm.vanilla.server.commons.server.tasks;

import java.util.Comparator;


public class TaskPriorityComparator implements Comparator<ITask>{

	public static TaskPriorityComparator instance = new TaskPriorityComparator();
	
	public int compare(ITask t1, ITask t2) {
		
		return t1.getTaskPriority().compareTo(t2.getTaskPriority());
	}

}
