package bpm.document.management.core.xstream;

public class SessionExpiredException extends Exception {

	private static final long serialVersionUID = 1L;

	public SessionExpiredException() { }
	
	public SessionExpiredException(String message) {
		super(message);
	}
}
