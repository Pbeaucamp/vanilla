package bpm.vanilla.platform.core.beans.tasks;

/**
 * Represent the tasks Priority:
 * 
 * HIGH_PRIORITY tasks will be executed before NORMAL ones
 * NORMAL priorities will be executed before LOW ones
 * @author ludo
 *
 */
public enum TaskPriority {
	HIGH_PRIORITY, NORMAL_PRIORITY, LOW_PRIORITY;
	
	private static final String[] priorityNames = new String[]{"Low", "Normal", "Hight"};
	
	/**
	 * 
	 * @return a Label for the TaskPriority
	 */
	public String getLabel(){
		switch(this){
		case LOW_PRIORITY:
			return priorityNames[0];
		case NORMAL_PRIORITY:
			return priorityNames[1];
		case HIGH_PRIORITY:
			return priorityNames[2];
		}
		
		return null;
	}
	
	public static TaskPriority get(String value){
		if (priorityNames[0].equals(value) || (LOW_PRIORITY + "").equals(value)){
			return LOW_PRIORITY;
		}
		if (priorityNames[1].equals(value) || (NORMAL_PRIORITY + "").equals(value)){
			return NORMAL_PRIORITY;
		}
		if (priorityNames[2].equals(value) || (HIGH_PRIORITY + "").equals(value)){
			return HIGH_PRIORITY;
		}
		
		return NORMAL_PRIORITY;
	}
}
