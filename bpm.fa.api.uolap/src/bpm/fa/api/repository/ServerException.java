package bpm.fa.api.repository;

public class ServerException extends Exception {
	private String mes;
	
	public ServerException(String message){
		mes = message;
	}
	
	public String getMessage(){
		return mes;
	}
}
