package bpm.vanilla.server.commons.server.commands;

import java.io.Serializable;

import bpm.vanilla.server.commons.server.Server;

/**
 * Base class that will perform any job on the server by calling the perform
 * method
 * 
 * @author ludo
 * 
 */
public abstract class ServerCommand implements Serializable {

	private static final long serialVersionUID = -378011208613833658L;
	
	private transient Server server;

	public ServerCommand() {
	}

	public ServerCommand(Server server) {
		this.server = server;
	}

	protected Server getServer() {
		return server;
	}

	protected void setServer(Server s) {
		server = s;
	}
}
