package bpm.fa.api.connection;

public class ConnectionException extends Exception {
	
	public ConnectionException (String str) {
		super(str);
	}
	
	public ConnectionException (String str, Throwable t) {
		super(str, t);
	}
}
