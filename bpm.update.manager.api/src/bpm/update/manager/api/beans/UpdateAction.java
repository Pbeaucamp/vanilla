package bpm.update.manager.api.beans;

import java.io.Serializable;

public enum UpdateAction implements Serializable {
	DOWNLOAD_NEW_VERSION, 
	SAVE_OLD_VERSION, 
	REMOVE_OLD_APPLICATION, 
	UNZIP_NEW_APPLICATION, 
	DEPLOY_NEW_APPLICATION,
	UNDETERMINED;
	
	private UpdateAction() { }
}