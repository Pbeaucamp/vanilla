package bpm.vanillahub.core.exception;

public class VanillaSessionExpiredException extends HubException{

	private static final long serialVersionUID = 1L;

	public VanillaSessionExpiredException(String sessionId){
		super("The session " + sessionId + " has expired");
	}
}
