package bpm.gateway.core.exception;

import bpm.gateway.core.Server;

public class ServerException extends Exception {

	protected Server serverSource;
	
	public ServerException(String message, Throwable throwable, Server server) {
		super(message, throwable);
		this.serverSource = server;
	}

	public ServerException(String message, Server server) {
		super(message);
		this.serverSource = server;

	}

	@Override
	public String getMessage() {
		if (serverSource != null){
			return "server " + serverSource.getName() + " : " + super.getMessage();
		}
		else{
			return super.getMessage();
		}
		
	}
	
	

	
}
