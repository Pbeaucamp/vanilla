package bpm.vanilla.platform.core.remote.impl;

import java.util.List;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.CheckResult;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RemoteResourceManager implements IResourceManager {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	public RemoteResourceManager(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
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
		return o;
	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource, edit), IResourceManager.ActionType.MANAGE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Resource) handleError(xml);
	}

	@Override
	public void removeResource(Resource resource) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource), IResourceManager.ActionType.REMOVE_RESOURCE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public Resource duplicateResource(int resourceId, String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(resourceId, name), IResourceManager.ActionType.DUPLICATE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Resource) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends Resource> getResources(TypeResource type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), IResourceManager.ActionType.GET_RESOURCES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<? extends Resource>) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getJdbcDrivers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IResourceManager.ActionType.GET_DRIVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<String>) handleError(xml);
	}

	@Override
	public String testConnection(DatabaseServer databaseServer) throws Exception {
		XmlAction op = new XmlAction(createArguments(databaseServer), IResourceManager.ActionType.TEST_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String) handleError(xml);
	}

	@Override
	public CheckResult validScript(Variable variable) throws Exception {
		XmlAction op = new XmlAction(createArguments(variable), IResourceManager.ActionType.VALID_SCRIPT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (CheckResult) handleError(xml);
	}

	@Override
	public ClassRule addOrUpdateClassRule(ClassRule classRule) throws Exception {
		XmlAction op = new XmlAction(createArguments(classRule), IResourceManager.ActionType.MANAGE_CLASSRULE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ClassRule) handleError(xml);
	}

	@Override
	public void removeClassRule(ClassRule classRule) throws Exception {
		XmlAction op = new XmlAction(createArguments(classRule), IResourceManager.ActionType.REMOVE_CLASSRULE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ClassRule> getClassRules(String identifiant) throws Exception {
		XmlAction op = new XmlAction(createArguments(identifiant), IResourceManager.ActionType.GET_CLASSRULES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ClassRule>) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CkanPackage> getCkanDatasets(String ckanUrl) throws Exception {
		XmlAction op = new XmlAction(createArguments(ckanUrl), IResourceManager.ActionType.GET_CKAN_DATASETS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<CkanPackage>) handleError(xml);
	}

	@Override
	public ContractIntegrationInformations generateIntegration(IRepositoryContext ctx, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception {
		XmlAction op = new XmlAction(createArguments(ctx, integrationInfos, modifyMetadata, modifyIntegration), IResourceManager.ActionType.GENERATE_INTEGRATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ContractIntegrationInformations) handleError(xml);
	}

	@Override
	public ContractIntegrationInformations generateKPI(IRepositoryContext ctx, KPIGenerationInformations infos) throws Exception {
		XmlAction op = new XmlAction(createArguments(ctx, infos), IResourceManager.ActionType.GENERATE_KPI);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ContractIntegrationInformations) handleError(xml);
	}

//	@Override
//	public ContractIntegrationInformations generateSimpleKPI(IRepositoryContext ctx, SimpleKPIGenerationInformations infos) throws Exception {
//		XmlAction op = new XmlAction(createArguments(ctx, infos), IResourceManager.ActionType.GENERATE_SIMPLE_KPI);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
//		return (ContractIntegrationInformations) handleError(xml);
//	}
	
	@Override
	public void deleteIntegration(IRepositoryContext ctx, ContractIntegrationInformations infos) throws Exception {
		XmlAction op = new XmlAction(createArguments(ctx, infos), IResourceManager.ActionType.REMOVE_INTEGRATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getValidationSchemas() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IResourceManager.ActionType.GET_VALIDATION_SCHEMAS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<String>) handleError(xml);
	}

	@Override
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception {
		XmlAction op = new XmlAction(createArguments(d4cUrl, d4cObs, datasetId, resourceId, contractId, schemas), IResourceManager.ActionType.VALIDATE_DATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ValidationDataResult) handleError(xml);
	}
}
