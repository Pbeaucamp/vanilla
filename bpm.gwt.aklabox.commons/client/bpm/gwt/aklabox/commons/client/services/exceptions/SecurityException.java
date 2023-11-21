package bpm.gwt.aklabox.commons.client.services.exceptions;

public class SecurityException extends ServiceException {

	private static final long serialVersionUID = 5235425223076208823L;

	public static int ERROR_TYPE_UNKNOWN 			= -1;
	
	public static int ERROR_TYPE_BAD_USER_PASS 		= 0;
	public static int ERROR_TYPE_CAS_NOT_ALLOWED	= 1;
	public static int ERROR_TYPE_SESSION_EXPIRED	= 2;
	public static int ERROR_TYPE_SESSION_INVALID	= 3;

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
