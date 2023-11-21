package bpm.vanilla.platform.core.remote.impl.components.internal;

import bpm.vanilla.platform.core.components.IRunIdentifier;

public class SimpleRunTaskId implements IRunIdentifier {
	private int taskId;
	
	public SimpleRunTaskId() {
	}

	public SimpleRunTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getTaskId() {
		return taskId;
	}

	@Override
	public String getKey() {
		return null;
	}
}
