package bpm.faweb.client.history;


public class CommandRedo implements Command {

private ModificationHistory history;
	
	public CommandRedo(ModificationHistory m){
		this.history = m;
	}

	public Object execute() {
		return history.forward();
	}

	public void execute(Object gc) { }

}
