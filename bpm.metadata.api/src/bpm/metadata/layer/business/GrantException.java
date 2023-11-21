package bpm.metadata.layer.business;

public class GrantException extends Exception {
	private String s;
		
	public GrantException(String s){
		this.s = s;
	}

	@Override
	public String getMessage() {
		return s;
	}

	
}
