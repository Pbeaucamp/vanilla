package bpm.gwt.aklabox.commons.shared;

import bpm.aklabox.workflow.core.model.Platform;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InfoConnection implements IsSerializable {

	private Platform platform;
	
	public InfoConnection() { }
	
	public InfoConnection(Platform platform) {
		this.platform = platform;
	}
	
	public Platform getPlatform() {
		return platform;
	}
}
