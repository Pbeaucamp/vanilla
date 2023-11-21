package bpm.vanillahub.remote;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.CheckResult;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.SimpleKPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanillahub.core.IHubResourceManager;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;
import bpm.vanillahub.core.exception.HubException;
import bpm.vanillahub.core.utils.IOWriter;
import bpm.vanillahub.remote.internal.HttpCommunicator;

public class RemoteResourceManager implements IHubResourceManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteResourceManager(String runtimeUrl, String sessionId, Locale locale) {
		httpCommunicator.init(runtimeUrl, sessionId, locale);
	}
	
	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o != null && o instanceof HubException) {
			throw (HubException) o;
		}
		return o;
	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource, edit), IHubResourceManager.ActionType.MANAGE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Resource) handleError(xml);
	}
	
	@Override
	public User manageUser(User user, boolean edit) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, edit), IHubResourceManager.ActionType.MANAGE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) handleError(xml);
	}

	@Override
	public void removeUser(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IHubResourceManager.ActionType.REMOVE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> getUsers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IHubResourceManager.ActionType.GET_USERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<User>) handleError(xml);
	}

	@Override
	public void removeResource(Resource resource) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource), IHubResourceManager.ActionType.REMOVE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(xml);
	}
	
	@Override
	public Resource duplicateResource(int resourceId, String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(resourceId, name), IHubResourceManager.ActionType.DUPLICATE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Resource) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends Resource> getResources(TypeResource type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), IHubResourceManager.ActionType.GET_RESOURCES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<? extends Resource>) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getJdbcDrivers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IHubResourceManager.ActionType.GET_DRIVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) handleError(xml);
	}

	@Override
	public String testConnection(DatabaseServer databaseServer) throws Exception {
		XmlAction op = new XmlAction(createArguments(databaseServer), IHubResourceManager.ActionType.TEST_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getCrawlReference(String url, String reference, int charBefore, int charAfter) throws Exception {
		XmlAction op = new XmlAction(createArguments(url, reference, charBefore, charAfter), IHubResourceManager.ActionType.GET_CRAWL_REFERENCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<WebServiceMethodDefinition> getWebServiceMethods(VariableString webServiceUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(webServiceUrl), IHubResourceManager.ActionType.GET_WEB_SERVICE_METHODS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WebServiceMethodDefinition>) handleError(xml);
	}

	@Override
	public CheckResult validScript(Variable variable) throws Exception {
		XmlAction op = new XmlAction(createArguments(variable), IHubResourceManager.ActionType.VALID_SCRIPT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (CheckResult) handleError(xml);
	}

	@Override
	public void addFile(String fileName, InputStream inputStream) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(inputStream, bos, true, true);

		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());
		
		XmlAction op = new XmlAction(createArguments(fileName, rawBytes), IHubResourceManager.ActionType.ADD_FILE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op));
		handleError(result);
	}

	@Override
	public List<CkanPackage> getCkanDatasets(String ckanUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(ckanUrl), IHubResourceManager.ActionType.GET_CKAN_PACKAGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<CkanPackage>) handleError(xml);
	}

	@Override
	public ClassRule addOrUpdateClassRule(ClassRule classRule) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public void removeClassRule(ClassRule classRule) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public List<ClassRule> getClassRules(String identifiant) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ContractIntegrationInformations generateIntegration(IRepositoryContext ctx, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ContractIntegrationInformations generateKPI(IRepositoryContext ctx, KPIGenerationInformations infos) throws Exception {
		throw new Exception("Not implemented");
	}
	
//	@Override
//	public ContractIntegrationInformations generateSimpleKPI(IRepositoryContext ctx, SimpleKPIGenerationInformations infos) throws Exception {
//		throw new Exception("Not implemented");
//	}
	
	@Override
	public List<String> getValidationSchemas() throws Exception {
		throw new Exception("Not implemented");
	}
	
	@Override
	public void deleteIntegration(IRepositoryContext ctx, ContractIntegrationInformations integrationInfos) throws Exception {
		throw new Exception("Not implemented");
	}

	@Override
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception {
		throw new Exception("Not implemented");
	}
}
