package bpm.metadata.layer.business;

public class MetaDataException extends Exception {
	private String message;
	
	public MetaDataException(String message){
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	
}
