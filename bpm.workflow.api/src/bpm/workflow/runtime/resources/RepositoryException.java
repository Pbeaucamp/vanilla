package bpm.workflow.runtime.resources;

/**
 * Throws an exception while manipulations with the repository
 * @author Ludovic CAMUS
 *
 */
public class RepositoryException extends Exception {

	public RepositoryException() {
		super();
		
	}

	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public RepositoryException(String message) {
		super(message);
		
	}

	public RepositoryException(Throwable cause) {
		super(cause);
		
	}

}
