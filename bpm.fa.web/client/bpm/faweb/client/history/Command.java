package bpm.faweb.client.history;


public interface Command {
	public abstract Object execute();
	
	public abstract void execute(Object gc);
}
