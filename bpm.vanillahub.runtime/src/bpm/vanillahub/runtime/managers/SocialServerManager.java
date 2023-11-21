package bpm.vanillahub.runtime.managers;

import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer.SocialNetworkType;

public class SocialServerManager extends ResourceManager<SocialNetworkServer> {

	private static final String SOCIAL_NETWORK_SERVER_FILE_NAME = "socialserver.xml";
	
	public SocialServerManager(String filePath) {
		super(filePath, SOCIAL_NETWORK_SERVER_FILE_NAME, "SocialServers");
	}

	@Override
	protected void manageResourceForAdd(SocialNetworkServer resource) {
	}

	@Override
	protected void manageResourceForModification(SocialNetworkServer newResource, SocialNetworkServer oldResource) {
		String name = oldResource.getName();
		SocialNetworkType type = oldResource.getType();
		VariableString token = oldResource.getTokenVS();
		newResource.updateInfo(name, type, token);
	}

}
