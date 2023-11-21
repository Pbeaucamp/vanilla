package bpm.faweb.client.history;


public class CommandUndo implements Command {
	private ModificationHistory history;
	
	public CommandUndo(ModificationHistory m){
		this.history = m;
	}

	public Object execute() {
		return history.back();
	}

	public void execute(Object gc) { }

}
