package bpm.vanilla.server.commons.pool.exception;

public class PoolException extends Exception{

	public PoolException(String string) {
		super(string);
	}

	public PoolException(String string, Exception ex) {
		super(string, ex);
	}

	
}
