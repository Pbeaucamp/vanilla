package bpm.gateway.core.exception;

import bpm.gateway.core.Server;

public class ConnectionException extends ServerException {

	public ConnectionException(String message, Server server) {
		super(message, server);
		
	}

}
