package bpm.workflow.runtime.resources.servers;

/**
 * Throws exception if there is when a server is created
 * @author Ludovic CAMUS
 *
 */
public class FactoryServerException extends Exception {

	public FactoryServerException() {
		super();
		
	}

	public FactoryServerException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public FactoryServerException(String message) {
		super(message);
		
	}

	public FactoryServerException(Throwable cause) {
		super(cause);
		
	}

	

}
