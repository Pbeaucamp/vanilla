package bpm.fd.api.core.model.parsers;

public abstract class AbstractParserException extends Exception{

	 public abstract Object  getDatas();

	public AbstractParserException() {
		super();
		
	}

	public AbstractParserException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public AbstractParserException(String message) {
		super(message);
		
	}

	public AbstractParserException(Throwable cause) {
		super(cause);
		
	}

		 
	 
}
