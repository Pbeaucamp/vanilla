package bpm.gateway.core.exception;


public class JdbcException extends ServerException {

	public JdbcException(String message) {
		super(message, null);
	}

	public JdbcException(String message, Throwable t) {
		super(message, t, null);
	}

}
