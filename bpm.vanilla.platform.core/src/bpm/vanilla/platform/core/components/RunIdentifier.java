package bpm.vanilla.platform.core.components;


public class RunIdentifier implements IRunIdentifier {
	private int taskId;
	private IVanillaComponentIdentifier identifier;
	
	public RunIdentifier() {
	}

	public RunIdentifier(IVanillaComponentIdentifier identifier, int taskId) {
		super();
		this.identifier = identifier;
		this.taskId = taskId;
	}

	public IVanillaComponentIdentifier getComponentIdentifier() {
		return identifier;
	}

	public int getTaskId() {
		return taskId;
	}

	@Override
	public String getKey() {
		String s = identifier.getComponentUrl() + "-" + identifier.getComponentId() + "-" + taskId;
		return s;
	}
}
