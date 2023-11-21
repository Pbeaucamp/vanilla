package bpm.vanillahub.core;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;

public interface IHubResourceManager extends IResourceManager {
	
	public static final String VANILLA_HUB_SERVLET = "/hubResourceServlet";
	
	public static enum ActionType implements IXmlActionType{
		MANAGE_USER, REMOVE_USER, GET_USERS, MANAGE_RESOURCE, REMOVE_RESOURCE, GET_RESOURCES, GET_DRIVERS, 
		TEST_CONNECTION, GET_CRAWL_REFERENCE,
		GET_WEB_SERVICE_METHODS, VALID_SCRIPT, ADD_FILE, GET_CKAN_PACKAGE, DUPLICATE_RESOURCE;

		@Override
		public Level getLevel() {
			return null;
		}
	}

	public User manageUser(User user, boolean edit) throws Exception;
	
	public void removeUser(User user) throws Exception;

	public List<User> getUsers() throws Exception;
	
	public List<String> getCrawlReference(String url, String reference, int charBefore, int charAfter) throws Exception;
	
	public List<WebServiceMethodDefinition> getWebServiceMethods(VariableString webServiceUrl) throws Exception;

	public void addFile(String fileName, InputStream inputStream) throws Exception;
}
