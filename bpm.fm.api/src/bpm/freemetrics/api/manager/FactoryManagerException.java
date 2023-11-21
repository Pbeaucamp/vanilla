package bpm.freemetrics.api.manager;

public class FactoryManagerException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6344308472084475695L;
	private String message;
	
	public FactoryManagerException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
