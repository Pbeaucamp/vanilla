package bpm.vanilla.api.core.exception;

import org.springframework.http.HttpStatus;

public class VanillaApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private VanillaApiError error;
	public VanillaApiException(VanillaApiError error) {
		super(error.getDescription());
		this.error = error;
	}	
	
	public int getErrorCode() {
		return this.error.getCode();
	}
	
	public String getErrorDescription() {
		return this.error.getDescription();
	}
	
	public HttpStatus getErrorStatus() {
		return this.error.getStatus();
	}
	public int getErrorStatusCode() {
		return this.error.getStatus().value();
	}	
	
}
