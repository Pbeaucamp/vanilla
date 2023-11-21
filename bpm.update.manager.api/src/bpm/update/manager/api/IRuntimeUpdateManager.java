package bpm.update.manager.api;

import java.util.List;

import bpm.update.manager.api.xstream.IXmlActionType;

public interface IRuntimeUpdateManager {
	
	public enum ActionType implements IXmlActionType {
		UNDEPLOY, SHUTDOWN
	}
	
	public void undeploy(List<String> plugins) throws Exception;
	
	public void shutdownOsgi() throws Exception;
}
