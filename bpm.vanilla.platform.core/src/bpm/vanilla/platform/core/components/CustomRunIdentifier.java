package bpm.vanilla.platform.core.components;


public class CustomRunIdentifier implements IRunIdentifier {
	
	private String key;
	private int taskId;

	public CustomRunIdentifier(String key, int taskId) {
		this.key = key;
		this.taskId = taskId;
	}

	@Override
	public String getKey() {
		return key;
	}

	public int getTaskId() {
		return taskId;
	}
}
