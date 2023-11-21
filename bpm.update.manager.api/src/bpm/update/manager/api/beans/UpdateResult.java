package bpm.update.manager.api.beans;

import java.io.Serializable;

public enum UpdateResult implements Serializable {
	FAILED_DOWNLOAD_NEW, 
	FAILED_SAVE_OLD_VERSION, 
	FAILED_REMOVE_OLD_VERSION, 
	FAILED_UNZIP_NEW_VERSION, 
	FAILED_DEPLOY_NEW_VERSION, 
	SUCCESS;
	
	private UpdateResult() { }
}