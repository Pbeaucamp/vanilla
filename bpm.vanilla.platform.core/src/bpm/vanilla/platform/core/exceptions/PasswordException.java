package bpm.vanilla.platform.core.exceptions;

public class PasswordException extends VanillaException {
	
	public PasswordException(){
//		super("Bad password");
		super( "Incorrect login information");
	}
}
