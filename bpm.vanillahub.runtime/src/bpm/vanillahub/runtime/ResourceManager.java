package bpm.vanillahub.runtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.service.spi.ServiceException;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
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
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.IHubResourceManager;
import bpm.vanillahub.core.beans.resources.attributes.WebServiceMethodDefinition;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.managers.WebServiceManager;
import bpm.vanillahub.runtime.utils.CrawlHelper;
import bpm.vanillahub.runtime.utils.IOWriter;

public class ResourceManager extends AbstractManager implements IHubResourceManager {
	
	private String fileFolder;
	
	public ResourceManager(ComponentVanillaHub component, String filePath) {
		super(component);
		this.fileFolder = filePath.endsWith("/") ? filePath + Constants.FILES : filePath + "/" + Constants.FILES;
	
		File folder = new File(fileFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	@Override
	protected void init() throws Exception { }

	@Override
	public List<String> getJdbcDrivers() throws Exception {
		VanillaConfiguration appConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		String jdbcXmlFile = appConfig.getProperty(VanillaConfiguration.JDBC_XML_FILE);

		Collection<DriverInfo> infos;
		try {
			infos = ListDriver.getInstance(jdbcXmlFile).getDriversInfo();
		} catch (Exception e) {
			e.printStackTrace();
			getComponent().getLogger().error("The driver list cannot be retrieve.");
			throw new ServiceException(Labels.getLabel(getLocale(), Labels.DriverListNotRetrieve));
		}

		List<String> drivers = new ArrayList<String>();
		if (infos != null) {
			for (DriverInfo info : infos) {
				String className = info.getClassName();
				if (!drivers.contains(className)) {
					drivers.add(className);
				}
			}
		}

		return drivers;
	}

	@Override
	public String testConnection(DatabaseServer databaseServer) throws ServiceException {
		try {
			ConnectionManager manager = ConnectionManager.getInstance();
			VanillaJdbcConnection connexion = manager.getJdbcConnection(databaseServer.getDatabaseUrlVS().getStringForTextbox(), databaseServer.getLogin(), databaseServer.getPassword(), databaseServer.getDriverJdbc());

			boolean res = connexion != null;
			manager.returnJdbcConnection(connexion);
			return res ? null : Labels.getLabel(getLocale(), Labels.ConnectionNotCorrect);
		} catch (Exception e) {
			e.printStackTrace();
			return Labels.getLabel(getLocale(), Labels.ConnectionNotCorrect) + " : " + e.getMessage();
		}
	}

	@Override
	public List<WebServiceMethodDefinition> getWebServiceMethods(VariableString webServiceUrl) throws ServiceException {
		String webService = webServiceUrl.getStringForTextbox();
		return WebServiceManager.getMethodNames(getComponent().getLogger(), webService);
	}

	@Override
	public List<String> getCrawlReference(String url, String reference, int charBefore, int charAfter) throws ServiceException {
		try {
			return CrawlHelper.getReference(url, reference, charBefore, charAfter);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public User manageUser(User user, boolean edit) throws Exception {
		return getComponent().getResourceDao().manageUser(user, edit);
	}

	@Override
	public void removeUser(User user) throws Exception {
		getComponent().getResourceDao().delete(user);
	}

	@Override
	public List<User> getUsers() throws Exception {
		return getComponent().getResourceDao().getUser();
	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) {
		return getComponent().getResourceDao().manageResource(resource, edit);
	}

	@Override
	public void removeResource(Resource resource) {
		getComponent().getResourceDao().delete(resource);
	}

	@Override
	public Resource duplicateResource(int resourceId, String name) throws Exception {
		return getComponent().getResourceDao().duplicate(resourceId, name);
	}

	@Override
	public List<? extends Resource> getResources(TypeResource type) {
		return getComponent().getResourceDao().getResources(type);
	}
	
	@Override
	public CheckResult validScript(Variable variable) {
		try {
			getComponent().getResourceDao().testVariable(getComponent().getLogger(), getLocale(), variable);
			return new CheckResult(Labels.getLabel(getLocale(), Labels.ScriptCorrect), false);
		} catch (Exception e) {
			e.printStackTrace();
			return new CheckResult(e.getMessage(), true);
		}
	}

	@Override
	public void addFile(String fileName, InputStream inputStream) throws Exception {
		File file = new File(fileFolder + cleanName(fileName));
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					throw new ServiceException("'" + fileName + "' " + Labels.getLabel(getLocale(), Labels.CannotBeCreated));
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new ServiceException("'" + fileName + "' " + Labels.getLabel(getLocale(), Labels.CannotBeCreated) + " : " + e.getMessage());
			}
		}
		
		IOWriter.write(inputStream, new FileOutputStream(file), true, true);
	}
	
	private String cleanName(String fileName) {
		return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	}

	@Override
	public List<CkanPackage> getCkanDatasets(String ckanUrl) throws Exception {
		CkanHelper ckanHelper = new CkanHelper(ckanUrl, null, null);
		return ckanHelper.getCkanPackages();
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
