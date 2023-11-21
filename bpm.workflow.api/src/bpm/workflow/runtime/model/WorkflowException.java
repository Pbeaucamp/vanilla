package bpm.workflow.runtime.model;

/**
 * Throws exceptions concerning the workflow
 * @author Ludovic CAMUS
 *
 */
public class WorkflowException extends Exception {

	public WorkflowException() {
		
	}

	public WorkflowException(String message) {
		super(message);
		
	}

	public WorkflowException(Throwable cause) {
		super(cause);
		
	}

	public WorkflowException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
