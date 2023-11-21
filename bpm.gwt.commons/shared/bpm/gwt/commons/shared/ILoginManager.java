package bpm.gwt.commons.shared;

import bpm.vanilla.platform.core.beans.User;

public interface ILoginManager {

	public boolean isConnectedToVanilla() throws Exception;

	public User authentify(String login, String password) throws Exception;

	public String getServerSessionId(User user) throws Exception;
	
	public InfoConnection getConnectionInformations();

}
