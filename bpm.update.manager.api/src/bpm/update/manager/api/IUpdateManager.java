package bpm.update.manager.api;

import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.api.xstream.IXmlActionType;

public interface IUpdateManager {
	
	public enum ActionType implements IXmlActionType {
		HAS_UPDATE,
		UPDATE,
		RESTART_SERVER
	}
	
	public UpdateInformations hasUpdate() throws Exception;
	
	public void updateApplication(boolean goBackPreviousApp, UpdateInformations appsInfos) throws Exception;
	
	public boolean restartServer() throws Exception;
}
