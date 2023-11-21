package bpm.vanillahub.runtime.managers;

import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.core.beans.resources.ApplicationServer.TypeServer;
import bpm.vanillahub.core.beans.resources.LimeSurveyServer;

public class ApplicationServerManager extends ResourceManager<ApplicationServer> {

	private static final String VANILLA_SERVER_FILE_NAME = "vanillaservers.xml";
	
	public ApplicationServerManager(String filePath) {
		super(filePath, VANILLA_SERVER_FILE_NAME, "VanillaServers");
	}

	@Override
	protected void manageResourceForAdd(ApplicationServer resource) {
	}

	@Override
	protected void manageResourceForModification(ApplicationServer newResource, ApplicationServer oldResource) {
		String name = oldResource.getName();
		VariableString url = oldResource.getUrlVS();
		VariableString login = oldResource.getLoginVS();
		VariableString password = oldResource.getPasswordVS();
		TypeServer typeServer = oldResource.getTypeServer();
		
		if (oldResource instanceof VanillaServer) {
			VariableString groupId = ((VanillaServer) oldResource).getGroupId();
			VariableString repoId = ((VanillaServer) oldResource).getRepositoryId();

			((VanillaServer) newResource).updateInfo(name, url, login, password, groupId, repoId, typeServer);
		}
		else if (oldResource instanceof AklaboxServer) {
			((AklaboxServer) newResource).updateInfo(name, url, login, password, typeServer);
		}
		else if (oldResource instanceof LimeSurveyServer) {
			((LimeSurveyServer) newResource).updateInfo(name, url, login, password, typeServer);
		}
	}

}
