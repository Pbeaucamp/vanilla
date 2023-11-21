package bpm.vanilla.platform.core.exceptions;

public class LimitNumberConnectionException extends VanillaException {
	
	public LimitNumberConnectionException(Float nbMinBlock){
		super("Max number of connection's try reached. Please try again in " + nbMinBlock.intValue() + " min.");
	}
}
