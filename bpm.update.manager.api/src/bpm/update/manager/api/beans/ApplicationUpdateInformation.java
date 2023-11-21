package bpm.update.manager.api.beans;

import java.io.Serializable;

public class ApplicationUpdateInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String appName;
	private String serverFileName;
	
	private boolean isRuntime;
	
	public ApplicationUpdateInformation() { }
	
	public ApplicationUpdateInformation(String appName, boolean isRuntime) {
		this.appName = appName;
		this.isRuntime = isRuntime;
	}

	public String getAppName() {
		return appName;
	}
	
	public String getServerFileName() {
		return serverFileName;
	}
	
	public void setServerFileName(String serverFileName) {
		this.serverFileName = serverFileName;
	}
	
	public boolean isRuntime() {
		return isRuntime;
	}
}
