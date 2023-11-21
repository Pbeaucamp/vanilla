package bpm.faweb.client.history;


public class Make {
	private Command undoCommand;
	private Command redoCommand;
	private Command doCommand;
	
	public Make(Command un, Command re, Command d) {
		this.undoCommand = un;
		this.redoCommand = re;
		this.doCommand = d;	
	}
	
	public void do_(Object gridCube){
		doCommand.execute(gridCube);
	}
	
	public Object Undo(){
		return undoCommand.execute();
	}
	
	public Object Redo(){
		return redoCommand.execute();
	}

}
