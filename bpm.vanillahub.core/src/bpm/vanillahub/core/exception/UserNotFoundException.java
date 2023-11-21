package bpm.vanillahub.core.exception;

public class UserNotFoundException extends HubException{

	public UserNotFoundException(String username){
		super( "The user "  + username +" doesn't exist");
	}

}
