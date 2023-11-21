package bpm.gwt.aklabox.commons.client.services.exceptions;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 3832176451298815197L;

	/**
	 * DO NOT USE, for serialization purposes
	 */
	public ServiceException() { }
	
	public ServiceException(String msg) {
		super(msg);
	}
	
	public ServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
