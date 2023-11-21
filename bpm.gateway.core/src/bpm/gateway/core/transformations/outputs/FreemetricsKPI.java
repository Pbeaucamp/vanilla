package bpm.gateway.core.transformations.outputs;

import bpm.gateway.core.Server;

public interface FreemetricsKPI {
	
	public Server getServer();
	public void setServer(Server server);
	public void setServer(String serverName);

}
