package bpm.vanillahub.web.client.services;

import java.util.List;

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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ResourcesServiceAsync {

	public void manageUser(User user, boolean edit, AsyncCallback<User> callback);

	public void removeUser(User user, AsyncCallback<Void> callback);

	public void getUsers(TypeResource user, AsyncCallback<List<User>> asyncCallback);

	public void getWebServiceMethods(VariableString value, AsyncCallback<List<WebServiceMethodDefinition>> callback);

	public void getVanillaGroups(String url, String user, String password, AsyncCallback<List<Group>> callback);

	public void getVanillaRepositories(String url, String user, String password, AsyncCallback<List<Repository>> callback);

	public void getRepositoryTree(VanillaServer vanillaServer, AsyncCallback<List<RepositoryDirectory>> asyncCallback);

	public void getParameterForItem(VanillaServer vanillaServer, int itemId, AsyncCallback<List<VanillaItemParameter>> asyncCallback);

	public void getCrawlReference(String url, String reference, int charBefore, int charAfter, AsyncCallback<List<String>> asyncCallback);

	public void getOpenDataDatasets(TypeOpenData typeOpenData, String url, AsyncCallback<DataGouvSummary> callback);

	public void getContracts(VanillaServer vanillaServer, AsyncCallback<List<Contract>> callback);
	
	public void getAvailableLangs(AklaboxServer server, AsyncCallback<List<String>> callback);
}
