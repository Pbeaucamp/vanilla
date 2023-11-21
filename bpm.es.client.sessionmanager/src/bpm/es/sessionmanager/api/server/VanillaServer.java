package bpm.es.sessionmanager.api.server;

import adminbirep.Activator;

public class VanillaServer {

	private String name = null;
	private int id;
	private String host;

	public VanillaServer() throws Exception {
		host = Activator.getDefault().getVanillaApi().getVanillaUrl();
	}

	public String getName() {
		if (name != null)
			return name;
		else
			return host;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String ip) {
		this.host = ip;
	}

}
