package bpm.freemetrics.api.manager;

public class ManagerException extends Exception{

	private static final long serialVersionUID = 2469457515321992617L;
	private String message;
	
	public ManagerException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
