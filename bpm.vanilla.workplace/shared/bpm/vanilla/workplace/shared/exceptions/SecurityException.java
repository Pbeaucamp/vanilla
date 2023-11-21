package bpm.vanilla.workplace.shared.exceptions;

public class SecurityException extends Exception {

	private static final long serialVersionUID = -8080866966222219889L;

	public static int ERROR_TYPE_UNKNOWN 			= -1;
	
	public static int ERROR_TYPE_BAD_USER_PASS 		= 0;

	private int errorType = ERROR_TYPE_UNKNOWN;
	
	/**
	 * ONLY for serialization;
	 */
	public SecurityException() {
		super();
	}

	public SecurityException(int type, String arg0, Throwable arg1) {
		super(arg0, arg1);
		errorType = type;
	}

	public SecurityException(int type, String arg0) {
		super(arg0);
		errorType = type;
	}

	public int getErrorType() {
		return errorType;
	}
	
}
