package bpm.faweb.client.history;


public class CommandDo implements Command {
	private ModificationHistory history;
	
	public CommandDo(ModificationHistory m){
		this.history = m;
	}

	public void execute(Object gc) {
		history.memo(gc);
	}
	
	public Object execute() {
		return null;
	}

}
