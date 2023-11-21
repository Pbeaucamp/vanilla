package bpm.gateway.core;

public interface ServerHostable {
	/**
	 * 
	 * @return the InputStream Server where he comes from
	 */
	public Server getServer();
	
	/**
	 * set the server owning the datastream
	 * @param s
	 */
	public void setServer(Server s);
	
	public Class getServerClass();
}
