package bpm.vanilla.platform.core.exceptions;

public class UserNotFoundException extends VanillaException{

	public UserNotFoundException(String username){
//		super( "The user "  + username +" doesn't exist");
		super( "Incorrect login information");
	}

}
