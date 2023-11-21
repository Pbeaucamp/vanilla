package bpm.vanillahub.web.client.services;

import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanillahub.core.beans.activities.TypeOpenData;
import bpm.vanillahub.core.beans.activities.attributes.DataGouvSummary;
import bpm.vanillahub.core.beans.activities.attributes.VanillaItemParameter;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("resourcesService")
public interface ResourcesService extends RemoteService {

	public static class Connect {
		private static ResourcesServiceAsync instance;

		public static ResourcesServiceAsync getInstance() {
			if (instance == null) {
				instance = (ResourcesServiceAsync) GWT.create(ResourcesService.class);
			}
			return instance;
		}
	}

	public User manageUser(User user, boolean edit) throws ServiceException;

	public void removeUser(User user) throws ServiceException;

	public List<User> getUsers(TypeResource user) throws ServiceException;
	
	public List<WebServiceMethodDefinition> getWebServiceMethods(VariableString webServiceUrl) throws ServiceException;
	
	public List<Group> getVanillaGroups(String url, String user, String password) throws ServiceException;
	
	public List<Repository> getVanillaRepositories(String url, String user, String password) throws ServiceException;

	public List<RepositoryDirectory> getRepositoryTree(VanillaServer vanillaServer) throws ServiceException;

	public List<VanillaItemParameter> getParameterForItem(VanillaServer vanillaServer, int itemId) throws ServiceException;

	public List<String> getCrawlReference(String url, String reference, int charBefore, int charAfter) throws ServiceException;

	public DataGouvSummary getOpenDataDatasets(TypeOpenData typeOpenData, String url) throws ServiceException;

	public List<Contract> getContracts(VanillaServer vanillaServer) throws ServiceException;
	
	public List<String> getAvailableLangs(AklaboxServer server) throws ServiceException;
}
