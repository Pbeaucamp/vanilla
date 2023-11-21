package bpm.gwt.commons.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfoWebapp implements IsSerializable{

	public String name;
	public boolean isConnected;
	public boolean canAccess;
	public String url;
	
	public InfoWebapp() { }

	public InfoWebapp(String name, boolean isConnected, boolean canAccess, String url) {
		this.name = name;
		this.isConnected = isConnected;
		this.canAccess = canAccess;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public boolean canAccess() {
		return canAccess && url != null && !url.isEmpty();
	}
	
	public String getUrl() {
		return url;
	}
}
