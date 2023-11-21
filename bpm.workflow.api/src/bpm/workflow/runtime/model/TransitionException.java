package bpm.workflow.runtime.model;

/**
 * Throws an exception concerning Transitions
 * @author Ludovic CAMUS
 *
 */
public class TransitionException extends Exception {

	public TransitionException() {
		
	}

	public TransitionException(String message) {
		super(message);
		
	}

	public TransitionException(Throwable cause) {
		super(cause);
		
	}

	public TransitionException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
