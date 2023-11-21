package bpm.vanilla.platform.core.runtime.components;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.ComponentContext;

import com.google.gson.Gson;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.fm.api.IFreeMetricsManager;
import bpm.fm.api.RemoteFreeMetricsManager;
import bpm.fm.api.model.Axis;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.GroupObservatory;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.ThemeAxis;
import bpm.fm.api.model.ThemeMetric;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.d4c.D4CConnection;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.file.MdmFileServer;
import bpm.gateway.core.transformations.Lookup;
import bpm.gateway.core.transformations.SelectDistinct;
import bpm.gateway.core.transformations.SelectionTransformation;
import bpm.gateway.core.transformations.calcul.Calculation;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.mdm.model.api.IMdmProvider;
import bpm.mdm.model.supplier.Contract;
import bpm.mdm.model.supplier.Supplier;
import bpm.mdm.remote.MdmRemote;
import bpm.metadata.layer.physical.sql.SQLConnectionException;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IResourceManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.CheckResult;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.CkanResource;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.core.beans.resources.ClassField;
import bpm.vanilla.platform.core.beans.resources.ClassField.TypeField;
import bpm.vanilla.platform.core.beans.resources.ClassRule;
import bpm.vanilla.platform.core.beans.resources.ClassRule.TypeRule;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.DatabaseServer;
import bpm.vanilla.platform.core.beans.resources.DocumentSchema;
import bpm.vanilla.platform.core.beans.resources.ISchedule;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.LimeSurveyResponseFormat;
import bpm.vanilla.platform.core.beans.resources.LimeSurveyType;
import bpm.vanilla.platform.core.beans.resources.OperatorClassic;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.beans.resources.RulePatternComparison;
import bpm.vanilla.platform.core.beans.resources.RuleValueComparison;
import bpm.vanilla.platform.core.beans.resources.SimpleKPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult.DataValidationResultStatut;
import bpm.vanilla.platform.core.beans.resources.ValidationRuleResult;
import bpm.vanilla.platform.core.beans.resources.ValidationSchemaResult;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.dao.resource.ResourceDao;
import bpm.vanilla.platform.core.utils.CkanHelper;
import bpm.vanilla.platform.core.utils.D4CHelper;
import bpm.vanilla.platform.core.utils.SchemaHelper;
import bpm.vanilla.platform.core.utils.UTF8ToAnsiUtils;
import bpm.vanillahub.core.IHubWorkflowManager;
import bpm.vanillahub.core.beans.activities.CibleActivity;
import bpm.vanillahub.core.beans.activities.DataServiceActivity;
import bpm.vanillahub.core.beans.activities.DataServiceActivity.TypeService;
import bpm.vanillahub.core.beans.activities.LimeSurveyInputActivity;
import bpm.vanillahub.core.beans.activities.MdmActivity;
import bpm.vanillahub.core.beans.activities.MdmInputActivity;
import bpm.vanillahub.core.beans.activities.MergeFilesActivity;
import bpm.vanillahub.core.beans.activities.RunVanillaItemActivity;
import bpm.vanillahub.core.beans.activities.SourceActivity;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties;
import bpm.vanillahub.core.beans.activities.attributes.ApiKeyProperties;
import bpm.vanillahub.core.beans.activities.attributes.BasicAuthProperties;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties.TypeGrant;
import bpm.vanillahub.core.beans.activities.attributes.APIProperties.TypeSecurity;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.LimeSurveyServer;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.Source.TypeSource;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.vanillahub.remote.RemoteAdminManager;
import bpm.vanillahub.remote.RemoteResourceManager;
import bpm.vanillahub.remote.RemoteWorkflowManager;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Link;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Schedule.Period;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.activity.StartActivity;
import bpm.workflow.commons.beans.activity.StopActivity;
import bpm.workflow.commons.remote.IAdminManager;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.resources.Cible.TypeCible;
import bpm.workflow.commons.utils.SchedulerUtils;

public class ResourceManager extends AbstractVanillaManager implements IResourceManager {
	
	private static final String RGPD_SCHEMA = "rgpd_schema";
	private static final String INTEROP_SCHEMA = "interop_schema";

	private ResourceDao resourceDao;

	private Datasource kpiDatasource, datastoreDatasource;

	public void activate(ComponentContext ctx) {
		super.activate(ctx);
	}

	@Override
	protected void init() throws Exception {
		this.resourceDao = getDao().getResourceDAO();
		if (this.resourceDao == null) {
			throw new Exception("Missing ResourceDao");
		}

		getLogger().info("init done!");
	}

	@Override
	public Resource manageResource(Resource resource, boolean edit) throws Exception {
		return resourceDao.manageResource(resource, edit);
	}

	@Override
	public void removeResource(Resource resource) throws Exception {
		resourceDao.delete(resource);
	}

	@Override
	public Resource duplicateResource(int resourceId, String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends Resource> getResources(TypeResource type) throws Exception {
		return resourceDao.getResources(type);
	}

	@Override
	public List<String> getJdbcDrivers() throws Exception {
		VanillaConfiguration appConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		String jdbcXmlFile = appConfig.getProperty(VanillaConfiguration.JDBC_XML_FILE);

		Collection<DriverInfo> infos;
		try {
			infos = ListDriver.getInstance(jdbcXmlFile).getDriversInfo();
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().error("The driver list cannot be retrieve.");
			throw new Exception("Unable to retrieve drivers");
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
	public String testConnection(DatabaseServer databaseServer) throws Exception {
		try {
			ConnectionManager manager = ConnectionManager.getInstance();
			VanillaJdbcConnection connexion = manager.getJdbcConnection(databaseServer.getDatabaseUrlVS().getStringForTextbox(), databaseServer.getLogin(), databaseServer.getPassword(), databaseServer.getDriverJdbc());

			boolean res = connexion != null;
			manager.returnJdbcConnection(connexion);
			return res ? null : "Connection not correct";
		} catch (Exception e) {
			e.printStackTrace();
			return "Connection not correct : " + e.getMessage();
		}
	}

	@Override
	public CheckResult validScript(Variable variable) throws Exception {
		try {
			resourceDao.testVariable(getLogger(), null, variable);
			return new CheckResult("Script correct", false);
		} catch (Exception e) {
			e.printStackTrace();
			return new CheckResult(e.getMessage(), true);
		}
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public ClassRule addOrUpdateClassRule(ClassRule classRule) throws Exception {
		return resourceDao.addOrUpdateClassRule(classRule);
	}

	@Override
	public void removeClassRule(ClassRule classRule) throws Exception {
		resourceDao.delete(classRule);
	}

	@Override
	public List<String> getValidationSchemas() throws Exception {
		// ClassDefinition classDef = new ClassDefinition();
		// classDef.setIdentifiant("schema_validation");

		String schemaPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/";
		// List<Path> jsonSchemas = Files.walk(Paths.get(schemaPath))
		// .filter(p -> p.toString().endsWith(".json"))
		// .collect(Collectors.toList());

		List<String> jsonSchemas = new ArrayList<String>();
		File schemasDir = new File(schemaPath);
		for (File file : schemasDir.listFiles()) {
			if (!file.isDirectory() && file.getAbsolutePath().endsWith(".json")) {
				jsonSchemas.add(file.getName());
			}
		}

		// List<ClassRule> classRules = getClassRules("schema_validation");

		List<String> schemas = new ArrayList<String>();
		for (String schema : jsonSchemas) {
			// Get filename without extension
			String name = schema.toString().replaceFirst("[.][^.]+$", "");
			//
			// ClassDefinition classSchema =
			// SchemaHelper.loadSchema(schema.toString(), name);
			// classDef.addClass(classSchema);

			schemas.add(name);
		}

		// clearParentField(classDef);

		return schemas;
	}

	@Override
	public List<ClassRule> getClassRules(String identifiant) throws Exception {
		return resourceDao.getClassRule(identifiant);
	}

	@Override
	public List<CkanPackage> getCkanDatasets(String ckanUrl) throws Exception {
		CkanHelper ckanHelper = new CkanHelper(ckanUrl, null, null);
		return ckanHelper.getCkanPackages();
	}
	
	/**
	 * This method delete an integration process
	 * 
	 * It deletes
	 *   VanillaHub
	 *   Integration entry
	 * 
	 * @param integrationInfos
	 * @throws Exception
	 */
	@Override
	public void deleteIntegration(IRepositoryContext ctx, ContractIntegrationInformations integrationInfos) throws Exception {
		String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
		String login = ctx.getVanillaContext().getLogin();
		String password = ctx.getVanillaContext().getPassword();

		// Create HUB to retrieve data from service, integrate in
		// Architect and load in Data4Citizen
		IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
		User user = manager.login(login, password, null);
		String sessionId = manager.connect(user);
		
		int hubId = integrationInfos.getHubId();
		
		IHubWorkflowManager hubManager = new RemoteWorkflowManager(vanillaHubUrl, sessionId, null);
		if (hubId > 0) {
			Workflow workflow = hubManager.getWorkflow(hubId, true);
			if (workflow != null) {
				hubManager.removeWorkflow(workflow);
			}
		}
		
		// Delete integration process
		if (integrationInfos.getId() > 0) {
			IMdmProvider mdmProvider = getMdmProvider(ctx);
			mdmProvider.removeIntegrationInfos(integrationInfos);
		}
		else if (integrationInfos.getContractId() != null && integrationInfos.getContractId() > 0) {
			IMdmProvider mdmProvider = getMdmProvider(ctx);
			ContractIntegrationInformations integration = mdmProvider.getIntegrationInfosByContract(integrationInfos.getContractId());
			mdmProvider.removeIntegrationInfos(integration);
		}
		
		//TODO: Deletes ETLs inside the VanillaHub ?
	}

	/**
	 * This method create a workflow to integrate data from various source
	 * (limesurvey, database, etc...) The workflow - create an Architect entry -
	 * create a file from the data - load the file in architect - validate the
	 * data against various schemas - load the data in Data4Citizen with a
	 * VanillaHub workflow
	 * 
	 * @param ctx
	 * @param type
	 * @throws Exception
	 */
	@Override
	public ContractIntegrationInformations generateIntegration(IRepositoryContext ctx, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception {
		Contract contract = getContract(ctx, integrationInfos.getContractId(), integrationInfos.getValidationSchemas(), integrationInfos.getMetadata());

		// TODO: Use connected user in ctx (not root)

		switch (integrationInfos.getType()) {
		case DOCUMENT:
			if (modifyMetadata) {
				return modifyDocumentIntegration(ctx, contract, (ContractIntegrationInformations) integrationInfos);
			}
			else {
				return generateDocumentIntegration(ctx, contract, (ContractIntegrationInformations) integrationInfos);
			}
		case API:
			if (modifyMetadata) {
				return modifyAPIIntegration(ctx, contract, (ContractIntegrationInformations) integrationInfos, modifyIntegration);
			}
			else {
				return generateAPIIntegration(ctx, contract,(ContractIntegrationInformations) integrationInfos);
			}
		case LIMESURVEY:
		case LIMESURVEY_SHAPES:
		case LIMESURVEY_VMAP:
			if (modifyMetadata) {
				return modifyLimeSurveyIntegration(ctx, contract, (ContractIntegrationInformations) integrationInfos, modifyIntegration);
			}
			else {
				return generateLimeSurveyIntegration(ctx, contract, (ContractIntegrationInformations) integrationInfos);
			}
		case SFTP:
			if (modifyMetadata) {
				return modifySFTPIntegration(ctx, contract, (ContractIntegrationInformations) integrationInfos, modifyIntegration);
			}
			else {
				return generateSFTPIntegration(ctx, contract, (ContractIntegrationInformations) integrationInfos);
			}
		case KPI:
			if (modifyMetadata) {
				return modifySimpleKPI(ctx, contract, (SimpleKPIGenerationInformations) integrationInfos, modifyIntegration);
			}
			else {
				return generateSimpleKPI(ctx, contract, (SimpleKPIGenerationInformations) integrationInfos);
			}
		default:
			break;
		}

		return null;
	}

	private IMdmProvider getMdmProvider(IVanillaContext ctx) {
		return new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl());
	}

	private IMdmProvider getMdmProvider(IRepositoryContext ctx) {
		return getMdmProvider(ctx.getVanillaContext());
	}

	private Contract getContract(IRepositoryContext ctx, Integer contractId, List<String> validationSchemas, List<MetaValue> metadata) throws Exception {
		IMdmProvider mdmProvider = getMdmProvider(ctx);
		Contract contract = contractId != null && contractId > 0 ? mdmProvider.getContract(contractId) : null;
		if (contract != null && validationSchemas != null) {
			defineContractValidationSchemas(ctx, contract, validationSchemas);
		}
//		if (contract != null && metadata != null) {
//			defineContractMetadata(ctx, contract, metadata);
//		}
		return contract;
	}
	
	private ContractIntegrationInformations modifyLimeSurveyIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations infos, boolean modifyIntegration) throws Exception {
		int hubId = infos.getHubId();
		
		IHubWorkflowManager hubManager = getWorkflowManager(ctx);
		Workflow workflow = hubManager.getWorkflow(hubId, false);
		
		// Build schedule
		bpm.workflow.commons.beans.Schedule hubSchedule = buildSchedule(infos.getSchedule());
		hubSchedule.setId(workflow.getSchedule() != null ? workflow.getSchedule().getId() : 0);
		workflow.setSchedule(hubSchedule);
		hubManager.manageWorkflow(workflow, true);
		
		ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations();
		integrationInfos.setType(ContractType.LIMESURVEY);
		integrationInfos.setHubId(workflow.getId());
		return integrationInfos;
	}

	private ContractIntegrationInformations generateLimeSurveyIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations integrationInfos) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

		/* Variable definitions */
//		String nameService = clearValue(integrationInfos.getNameService(), "-");
		String limeSurveyId = integrationInfos.getItem();
		String outputName = integrationInfos.getOutputName();
		
		String d4cOrg = integrationInfos.getTargetOrganisation();
		String datasetId = integrationInfos.getTargetDatasetName();
		String datasetName = integrationInfos.getTargetDatasetCustomName();

		String cleanDatasetName = cleanDatasetName(datasetName);
		String prefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + cleanDatasetName;
		String contractName = prefix + "_contract";

		ContractType type = integrationInfos.getType();
		try {
			// If the contractId is not define we generate an Architect contract
			if (contract == null) {
				contract = buildArchitect(ctx, contractName, type, integrationInfos.getValidationSchemas(), integrationInfos.getMetadata());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Architect: " + e.getMessage(), e);
		}

		String hubName = prefix + "_LS";
		String vanillaServerName = "Architect_Process_Vanilla_Server_" + ctx.getGroup().getId();
		String limeSurveyServerName = "Architect_Process_LimeSurvey_Server_" + ctx.getGroup().getId();
		String hubCibleName = "D4C_Cible_LimeSurvey_" + new Object().hashCode();

		String limeSurveyUrl = config.getProperty(VanillaConfiguration.P_LIMESURVEY_URL);
		String limeSurveyLogin = config.getProperty(VanillaConfiguration.P_LIMESURVEY_LOGIN);
		String limeSurveyPassword = config.getProperty(VanillaConfiguration.P_LIMESURVEY_PASSWORD);
		LimeSurveyType limeSurveyType = integrationInfos.getType() == ContractType.LIMESURVEY ? LimeSurveyType.LIMESURVEY : (integrationInfos.getType() == ContractType.LIMESURVEY_VMAP ? LimeSurveyType.LIMESURVEY_VMAP : LimeSurveyType.LIMESURVEY_SHAPES);

		String d4cUrl = integrationInfos.getD4cUrl();
		String d4cLogin = getD4cLogin(config, integrationInfos);
		String d4cPassword = getD4cPassword(config, integrationInfos);

		Workflow workflow = null;
		try {
			String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
			String login = ctx.getVanillaContext().getLogin();
			String password = ctx.getVanillaContext().getPassword();

			// Create HUB to retrieve data from service, integrate in
			// Architect and load in Data4Citizen
			IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
			User user = manager.login(login, password, null);
			String sessionId = manager.connect(user);

			Activity source = buildLimeSurveySource(vanillaHubUrl, sessionId, limeSurveyServerName, limeSurveyUrl, limeSurveyLogin, limeSurveyPassword, limeSurveyId, limeSurveyType, outputName);
			workflow = buildHub(ctx, vanillaHubUrl, sessionId, user, contract, hubName, vanillaServerName, hubCibleName, source, type, limeSurveyType, d4cUrl, d4cOrg, d4cLogin, d4cPassword, datasetId, integrationInfos.getSchedule());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		try {
			// Save integration process
			integrationInfos.setContractId(contract.getId());
			integrationInfos.setDocumentId(contract.getDocId());
			integrationInfos.setHubId(workflow.getId());
			integrationInfos.setHubName(hubName);
			
			IMdmProvider mdmProvider = getMdmProvider(ctx);
			mdmProvider.saveOrUpdateIntegrationInfos(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		return integrationInfos;
	}
	
	private ContractIntegrationInformations modifyDocumentIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations infos) throws Exception {
		int hubId = infos.getHubId();

		ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations();
		integrationInfos.setType(ContractType.DOCUMENT);
		integrationInfos.setHubId(hubId);
		integrationInfos.setContractId(contract.getId());
		integrationInfos.setDocumentId(contract.getDocId());
		return integrationInfos;
	}

	private ContractIntegrationInformations generateDocumentIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations integrationInfos) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		/* Variable definitions */
//		String nameService = clearValue(integrationInfos.getNameService(), "-");
		String d4cOrg = integrationInfos.getTargetOrganisation();
		String d4cDatasetId = integrationInfos.getTargetDatasetName();
		String d4cDatasetName = integrationInfos.getTargetDatasetCustomName();

		String cleanDatasetName = cleanDatasetName(d4cDatasetName);
		String prefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + cleanDatasetName;
		String contractName = prefix + "_contract";

		ContractType type = integrationInfos.getType();

		try {
			// If the contractId is not define we generate an Architect contract
			if (contract == null) {
				contract = buildArchitect(ctx, contractName, type, integrationInfos.getValidationSchemas(), integrationInfos.getMetadata());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Architect: " + e.getMessage(), e);
		}

		String hubName = contract.getName() + "_HUB_DOCUMENT";
		String vanillaServerName = "Architect_Process_Vanilla_Server_" + ctx.getGroup().getId();
		String hubCibleName = "D4C_Cible_DOCUMENT_" + d4cDatasetId;

		String d4cUrl = integrationInfos.getD4cUrl();
		String d4cLogin = getD4cLogin(config, integrationInfos);
		String d4cPassword = getD4cPassword(config, integrationInfos);

		Workflow workflow = null;
		int userId = -1;
		try {
			String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
			String login = ctx.getVanillaContext().getLogin();
			String password = ctx.getVanillaContext().getPassword();

			// Create HUB to retrieve data from service, integrate in
			// Architect and load in Data4Citizen
			IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
			User user = manager.login(login, password, null);
			String sessionId = manager.connect(user);
			userId = user.getId();

			workflow = buildHub(ctx, vanillaHubUrl, sessionId, user, contract, hubName, vanillaServerName, hubCibleName, null, type, null, d4cUrl, d4cOrg, d4cLogin, d4cPassword, d4cDatasetId, integrationInfos.getSchedule());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		try {
			// We create a dummy document upload to init contract
			String format = integrationInfos.getOutputName().lastIndexOf(".") > 0 ? integrationInfos.getOutputName().substring(integrationInfos.getOutputName().lastIndexOf(".") + 1) : "";
			List<Integer> groupIds = new ArrayList<Integer>();
			groupIds.add(ctx.getGroup().getId());
			int repositoryId = ctx.getRepository().getId();
			
			String dummyContent = "This is a dummy file";
			GedDocument doc = null;
			try (InputStream is = IOUtils.toInputStream(dummyContent)) {
				IGedComponent gedManager = new RemoteGedComponent(ctx.getVanillaContext());
				doc = gedManager.createDocumentThroughServlet(integrationInfos.getOutputName(), format, userId, groupIds, repositoryId, is);
			
				contract.setFileVersions(doc);
				
				contract.setVersionId(null);
				IMdmProvider provider = resetMdmProvider(ctx.getVanillaContext());
				provider.addContract(contract);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Save integration process
			integrationInfos.setContractId(contract.getId());
			integrationInfos.setDocumentId(doc != null ? doc.getId() : -1);
			integrationInfos.setHubId(workflow.getId());
			integrationInfos.setHubName(hubName);

			IMdmProvider mdmProvider = getMdmProvider(ctx);
			mdmProvider.saveOrUpdateIntegrationInfos(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		return integrationInfos;
	}

	private String getD4cLogin(VanillaConfiguration config, AbstractD4CIntegrationInformations infos) {
		return infos != null && infos.getD4cLogin() != null ? infos.getD4cLogin() : config.getProperty(VanillaConfiguration.P_D4C_LOGIN);
	}

	private String getD4cPassword(VanillaConfiguration config, AbstractD4CIntegrationInformations infos) {
		return infos != null && infos.getD4cPassword() != null ? infos.getD4cPassword() : config.getProperty(VanillaConfiguration.P_D4C_PASSWORD);
	}
	
	private ContractIntegrationInformations modifyAPIIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations infos, boolean modifyIntegration) throws Exception {
		int hubId = infos.getHubId();
		
		IHubWorkflowManager hubManager = getWorkflowManager(ctx);
		Workflow workflow = hubManager.getWorkflow(hubId, false);
		
		if (modifyIntegration) {
			String apiURL = infos.getItem();
			String outputName = infos.getOutputName();
			
			List<Activity> activities = workflow.getWorkflowModel().getActivities();
			if (activities != null) {
				for (Activity activity : activities) {
					if (activity.getType() == TypeActivity.DATA_SERVICE) {
						((APIProperties) ((DataServiceActivity) activity).getAttribute()).setOutputName(new VariableString(outputName));
						((APIProperties) ((DataServiceActivity) activity).getAttribute()).setUr(new VariableString(apiURL));
					}
				}
			}
		}
		
		// Build schedule
		bpm.workflow.commons.beans.Schedule hubSchedule = buildSchedule(infos.getSchedule());
		hubSchedule.setId(workflow.getSchedule() != null ? workflow.getSchedule().getId() : 0);
		workflow.setSchedule(hubSchedule);
		hubManager.manageWorkflow(workflow, true);
		
		ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations();
		integrationInfos.setType(ContractType.API);
		integrationInfos.setHubId(workflow.getId());
		return integrationInfos;
	}
	
	private ContractIntegrationInformations generateAPIIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations integrationInfos) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		/* Variable definitions */
//		String nameService = clearValue(integrationInfos.getNameService(), "-");
		String apiURL = integrationInfos.getItem();
		String outputName = integrationInfos.getOutputName();
		String d4cOrg = integrationInfos.getTargetOrganisation();
		String d4cDatasetId = integrationInfos.getTargetDatasetName();
		String d4cDatasetName = integrationInfos.getTargetDatasetCustomName();

		String cleanDatasetName = cleanDatasetName(d4cDatasetName);
		String prefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + cleanDatasetName;
		String contractName = prefix + "_contract";

		ContractType type = integrationInfos.getType();
		try {
			// If the contractId is not define we generate an Architect contract
			if (contract == null) {
				contract = buildArchitect(ctx, contractName, type, integrationInfos.getValidationSchemas(), integrationInfos.getMetadata());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Architect: " + e.getMessage(), e);
		}

		String hubName = contract.getName() + "_HUB_API";
		String vanillaServerName = "Architect_Process_Vanilla_Server_" + ctx.getGroup().getId();
		String hubCibleName = "D4C_Cible_API_" + d4cDatasetId;

		String d4cUrl = integrationInfos.getD4cUrl();
		String d4cLogin = getD4cLogin(config, integrationInfos);
		String d4cPassword = getD4cPassword(config, integrationInfos);

		Workflow workflow = null;
		try {
			String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
			String login = ctx.getVanillaContext().getLogin();
			String password = ctx.getVanillaContext().getPassword();

			// Create HUB to retrieve data from service, integrate in
			// Architect and load in Data4Citizen
			IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
			User user = manager.login(login, password, null);
			String sessionId = manager.connect(user);

			Activity source = buildAPISource(vanillaHubUrl, sessionId, apiURL, outputName, integrationInfos.getAdditionnalInfos());
			workflow = buildHub(ctx, vanillaHubUrl, sessionId, user, contract, hubName, vanillaServerName, hubCibleName, source, type, null, d4cUrl, d4cOrg, d4cLogin, d4cPassword, d4cDatasetId, integrationInfos.getSchedule());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		try {
			// Save integration process
			integrationInfos.setContractId(contract.getId());
			integrationInfos.setDocumentId(contract.getDocId());
			integrationInfos.setHubId(workflow.getId());
			integrationInfos.setHubName(hubName);

			IMdmProvider mdmProvider = getMdmProvider(ctx);
			mdmProvider.saveOrUpdateIntegrationInfos(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		return integrationInfos;
	}
	
	private IHubWorkflowManager getWorkflowManager(IRepositoryContext ctx) throws Exception {
		String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
		String login = ctx.getVanillaContext().getLogin();
		String password = ctx.getVanillaContext().getPassword();
		
		IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
		User user = manager.login(login, password, null);
		String sessionId = manager.connect(user);
		return new RemoteWorkflowManager(vanillaHubUrl, sessionId, null);
	}
	
	private ContractIntegrationInformations modifySFTPIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations infos, boolean modifyIntegration) throws Exception {
		int hubId = infos.getHubId();
		
		IHubWorkflowManager hubManager = getWorkflowManager(ctx);
		Workflow workflow = hubManager.getWorkflow(hubId, false);
		
		if (modifyIntegration) {
			String documentRegex = infos.getItem();
			
			List<Activity> activities = workflow.getWorkflowModel().getActivities();
			if (activities != null) {
				for (Activity activity : activities) {
					if (activity.getType() == TypeActivity.SOURCE) {
						((SourceActivity) activity).setCibleItem(new VariableString(documentRegex));
					}
				}
			}
		}
		
		// Build schedule
		bpm.workflow.commons.beans.Schedule hubSchedule = buildSchedule(infos.getSchedule());
		hubSchedule.setId(workflow.getSchedule() != null ? workflow.getSchedule().getId() : 0);
		workflow.setSchedule(hubSchedule);
		hubManager.manageWorkflow(workflow, true);
		
		ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations();
		integrationInfos.setType(ContractType.SFTP);
		integrationInfos.setHubId(workflow.getId());
		return integrationInfos;
	}

	private ContractIntegrationInformations generateSFTPIntegration(IRepositoryContext ctx, Contract contract, ContractIntegrationInformations integrationInfos) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		/* Variable definitions */
//		String nameService = clearValue(integrationInfos.getNameService(), "-");
		String d4cOrg = integrationInfos.getTargetOrganisation();
		String d4cDatasetId = integrationInfos.getTargetDatasetName();
		String d4cDatasetName = integrationInfos.getTargetDatasetCustomName();
		String documentRegex = integrationInfos.getItem();

		String cleanDatasetName = cleanDatasetName(d4cDatasetName);
		String prefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + cleanDatasetName;
		String contractName = prefix + "_contract";
		
		ContractType type = integrationInfos.getType();
		try {
			// If the contractId is not define we generate an Architect contract
			if (contract == null) {
				contract = buildArchitect(ctx, contractName, type, integrationInfos.getValidationSchemas(), integrationInfos.getMetadata());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Architect: " + e.getMessage(), e);
		}

		String hubName = prefix + "_SFTP";
		String vanillaServerName = "Architect_Process_Vanilla_Server_" + ctx.getGroup().getId();
		String sftpServerName = "Architect_Process_SFTP_Server_" + ctx.getGroup().getId();
		String hubCibleName = "D4C_Cible_SFTP_" + d4cDatasetId;

		String d4cUrl = integrationInfos.getD4cUrl();
		String d4cLogin = getD4cLogin(config, integrationInfos);
		String d4cPassword = getD4cPassword(config, integrationInfos);

		String sftpUrl = config.getProperty(VanillaConfiguration.P_SFTP_URL);
		String sftpPort = config.getProperty(VanillaConfiguration.P_SFTP_PORT);
		String sftpLogin = config.getProperty(VanillaConfiguration.P_SFTP_LOGIN);
		String sftpPassword = config.getProperty(VanillaConfiguration.P_SFTP_PASSWORD);
		String folderPath = "/user_" + d4cOrg;

		Workflow workflow = null;
		try {
			String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
			String login = ctx.getVanillaContext().getLogin();
			String password = ctx.getVanillaContext().getPassword();

			// Create HUB to retrieve data from service, integrate in
			// Architect and load in Data4Citizen
			IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
			User user = manager.login(login, password, null);
			String sessionId = manager.connect(user);

			Activity source = buildSFTPSource(vanillaHubUrl, sessionId, sftpServerName, sftpUrl, sftpPort, sftpLogin, sftpPassword, folderPath, documentRegex);
			workflow = buildHub(ctx, vanillaHubUrl, sessionId, user, contract, hubName, vanillaServerName, hubCibleName, source, type, null, d4cUrl, d4cOrg, d4cLogin, d4cPassword, d4cDatasetId, integrationInfos.getSchedule());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		try {
			// Save integration process
			integrationInfos.setContractId(contract.getId());
			integrationInfos.setHubId(workflow.getId());
			integrationInfos.setHubName(hubName);

			IMdmProvider mdmProvider = getMdmProvider(ctx);
			mdmProvider.saveOrUpdateIntegrationInfos(integrationInfos);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		return integrationInfos;
	}

	public static String clearValue(String value, String spaceReplacement) {
		if (value == null) {
			return value;
		}

		value = value.replace("\uFEFF", "");
		value = value.replaceAll("\\s+", spaceReplacement);
		value = value.toLowerCase();
		value = value.replaceAll("'", "");
		value = StringUtils.stripAccents(value);
		return value;
	}

	private Contract buildArchitect(IRepositoryContext ctx, String contractName, ContractType type, List<String> validationSchemas, List<MetaValue> metadata) throws Exception {
		IMdmProvider provider = resetMdmProvider(ctx.getVanillaContext());

		Supplier supplier = getSupplier(provider, type, contractName);

		List<Supplier> suppliers = new ArrayList<Supplier>();
		suppliers.add(supplier);
		suppliers = provider.saveSuppliers(suppliers);

		supplier = getSupplier(provider, type, contractName);

		List<Integer> groupIds = resetMdmProvider(ctx.getVanillaContext()).getSupplierSecurity(supplier.getId());
		if (!groupIds.contains(ctx.getGroup().getId()))
			provider.addSecuredSupplier(supplier.getId(), ctx.getGroup().getId());

		Contract contract = null;
		for (Supplier supp : suppliers) {
			for (Contract cont : supp.getContracts()) {
				if (cont.getName().equals(contractName)) {
					contract = cont;
					break;
				}
			}
		}
		suppliers = provider.saveSuppliers(suppliers);

		if (validationSchemas != null) {
			defineContractValidationSchemas(ctx, contract, validationSchemas);
		}
		
		// We disable metadata for now
//		if (metadata != null) {
//			defineContractMetadata(ctx, contract, metadata);
//		}
		
		return contract;
	}

	private void defineContractValidationSchemas(IRepositoryContext ctx, Contract contract, List<String> validationSchemas) throws Exception {
		IMdmProvider mdmProvider = getMdmProvider(ctx);

		// First we remove all the schemas from the contract
		List<DocumentSchema> schemas = mdmProvider.getDocumentSchemas(contract.getId());
		if (schemas != null) {
			for (DocumentSchema docSchema : schemas) {
				mdmProvider.removeDocumentSchema(docSchema);
			}
		}
		
		// We add the rgpd schema to every contract
		if (!validationSchemas.contains(RGPD_SCHEMA)) {
			validationSchemas.add(RGPD_SCHEMA);
		}
		if (!validationSchemas.contains(INTEROP_SCHEMA)) {
			validationSchemas.add(INTEROP_SCHEMA);
		}

		for (String validationSchema : validationSchemas) {
			DocumentSchema docItem = new DocumentSchema();
			docItem.setContractId(contract.getId());
			docItem.setSchema(validationSchema);

			mdmProvider.saveOrUpdateDocumentSchema(docItem);
		}
	}

//	private void defineContractMetadata(IRepositoryContext ctx, Contract contract, List<MetaValue> metadata) throws Exception {
//		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
//		
//		List<MetaLink> contractMetaLinks = repositoryApi.getMetaService().getMetaLinks(contract.getId(), TypeMetaLink.ARCHITECT, true);
//		// Step 1 - Remove any MetaLink and MetaValue for the selected Contract
//		repositoryApi.getMetaService().manageMetaValues(contractMetaLinks, ManageAction.DELETE);
//		
//		List<MetaLink> links = new ArrayList<MetaLink>();
//		for (MetaValue value : metadata) {
//			
//			String key = value.getMetaKey();
//			
//			// Step 2 - Check if the Meta exist
//			Meta meta = repositoryApi.getMetaService().getMeta(key);
//			if (meta == null) {
//				meta = new Meta();
//				meta.setKey(key);
//				meta.setLabel(key);
//				meta.setType(TypeMeta.STRING);
//
//				meta = repositoryApi.getMetaService().manageMeta(meta, ManageAction.SAVE_OR_UPDATE);
//			}
//			
//			MetaLink link = new MetaLink(meta);
//			link.setItemId(contract.getId());
//			link.setType(TypeMetaLink.ARCHITECT);
//			link.setValue(value);
//
//			links.add(link);
//		}
//		
//		repositoryApi.getMetaService().manageMetaValues(links, ManageAction.SAVE_OR_UPDATE);
//	}

	// private RepositoryItem buildETL(IRepositoryContext ctx,
	// RepositoryDirectory target, String etlName, String dbUrl,
	// String dbLogin, String dbPassword, String dbDriver, String dbTable,
	// Contract contract) throws Exception {
	// DocumentGateway model = createDocument(ctx, etlName,
	// ctx.getVanillaContext().getLogin());
	//
	// MdmFileServer serverMDM = new MdmFileServer("mdmserver", "",
	// ctx.getVanillaContext().getVanillaUrl(),
	// ctx.getVanillaContext().getLogin(),
	// ctx.getVanillaContext().getPassword(), ctx.getRepository().getId() + "");
	//
	// DataBaseServer serverDB = new DataBaseServer();
	// serverDB.setName("DBServer_" + dbTable);
	// DataBaseConnection dbCon = new DataBaseConnection();
	// dbCon.setName("DBServer_" + dbTable);
	// dbCon.setLogin(dbLogin);
	// dbCon.setPassword(dbPassword);
	// dbCon.setFullUrl(dbUrl);
	// String driver = null;
	// for (DriverInfo info :
	// ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo())
	// {
	// if (info.getClassName().equals(dbDriver)) {
	// driver = info.getName();
	// break;
	// }
	// }
	// dbCon.setDriverName(driver);
	// dbCon.setUseFullUrl(true);
	//
	// // dbCon.setServer(server);
	// serverDB.addConnection(dbCon);
	// serverDB.setCurrentConnection(dbCon);
	// dbCon.setServer(serverDB);
	//
	// DataBaseInputStream inputDB = new DataBaseInputStream();
	// inputDB.setName("DataBaseInputStream");
	// inputDB.setDefinition("SELECT * FROM " + dbTable);
	// inputDB.setPositionX(200);
	// inputDB.setPositionY(200);
	// inputDB.setServer(serverDB);
	//
	// FileOutputCSV outputCsv = new FileOutputCSV();
	// outputCsv.setName("Output_MDM");
	// outputCsv.setPositionX(100);
	// outputCsv.setPositionY(50);
	// outputCsv.setTemporaryFilename("");
	// outputCsv.setUseMdm(true);
	// outputCsv.setDelete(false);
	// outputCsv.setSeparator(',');
	// outputCsv.setAppend(false);
	// outputCsv.setServer(serverMDM);
	// outputCsv.setContract(contract);
	// outputCsv.setDefinition(String.valueOf(contract.getId()));
	// outputCsv.setSupplierId(contract.getParent().getId());
	// outputCsv.setContractId(contract.getId());
	//
	// /*
	// * add Transformation to the model
	// */
	// model.addTransformation(inputDB);
	// model.addTransformation(outputCsv);
	// model.addServer(serverDB);
	// model.addServer(serverMDM);
	//
	// /*
	// * link the transformations
	// */
	// inputDB.addOutput(outputCsv);
	//
	// String modelETL = model.getElement().asXML();
	//
	// IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
	// RepositoryItem itemETL =
	// repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.GTW_TYPE,
	// -1, target, etlName, "", "", "", modelETL, true);
	// return itemETL;
	// }

	private Activity buildLimeSurveySource(String vanillaHubUrl, String sessionId, String limeSurveyServerName, String limeSurveyUrl, String limeSurveyLogin, String limeSurveyPassword, String limeSurveyId, LimeSurveyType limeSurveyType, String outputName) throws Exception {
		IResourceManager resourceManager = new RemoteResourceManager(vanillaHubUrl, sessionId, null);

		LimeSurveyServer limeSurveyServer = getLimeSurveyServer(resourceManager, limeSurveyServerName, limeSurveyUrl, limeSurveyLogin, limeSurveyPassword);

		LimeSurveyInputActivity itemActivity = new LimeSurveyInputActivity("LimeSurvey_Input");
		itemActivity.setResource(limeSurveyServer);
		itemActivity.setLimeSurveyId(new VariableString(limeSurveyId));
		itemActivity.setLimeSurveyType(limeSurveyType);
		itemActivity.setFormat(LimeSurveyResponseFormat.CSV);
		itemActivity.setOutputName(new VariableString(outputName));
		itemActivity.setLeft(150);
		itemActivity.setTop(100);
		if (limeSurveyType != null && limeSurveyType == LimeSurveyType.LIMESURVEY_SHAPES) {
			itemActivity.setLoop(true);
		}

		return itemActivity;
	}

	private Activity buildAPISource(String vanillaHubUrl, String sessionId, String apiURL, String outputName, HashMap<String, String> additionnalInfos) throws Exception {
		
		String serviceType = additionnalInfos != null ? additionnalInfos.get(AbstractD4CIntegrationInformations.KEY_INFO_SERVICE_TYPE) : null;
		String layer = additionnalInfos != null ? additionnalInfos.get(AbstractD4CIntegrationInformations.KEY_INFO_LAYER) : null;
		
		String authType = additionnalInfos != null ? additionnalInfos.get(AbstractD4CIntegrationInformations.KEY_INFO_AUTH_TYPE) : null;
		String login = additionnalInfos != null ? additionnalInfos.get(AbstractD4CIntegrationInformations.KEY_INFO_AUTH_LOGIN) : null;
		String password = additionnalInfos != null ? additionnalInfos.get(AbstractD4CIntegrationInformations.KEY_INFO_AUTH_PASSWORD) : null;
		String apiKeyName = additionnalInfos != null ? additionnalInfos.get(AbstractD4CIntegrationInformations.KEY_INFO_AUTH_API_KEY_NAME) : null;
		String apiKey = additionnalInfos != null ? additionnalInfos.get(AbstractD4CIntegrationInformations.KEY_INFO_AUTH_API_KEY) : null;
		
		APIProperties properties = new APIProperties();
		properties.setOutputName(new VariableString(outputName));
		properties.setTypeGrant(TypeGrant.CLIENT_CREDENTIALS);
		
		if (authType == null || authType.equalsIgnoreCase(TypeSecurity.NO_AUTH.toString())) {
			properties.setTypeSecurity(TypeSecurity.NO_AUTH);
		}
		else if (authType.equalsIgnoreCase(TypeSecurity.OAUTH20.toString())) {
			properties.setTypeSecurity(TypeSecurity.OAUTH20);
			// Not supported for now
		}
		else if (authType.equalsIgnoreCase(TypeSecurity.BASIC_AUTH.toString())) {
			properties.setTypeSecurity(TypeSecurity.BASIC_AUTH);
			
			BasicAuthProperties basicAuthProperties = new BasicAuthProperties();
			basicAuthProperties.setLogin(new VariableString(login));
			basicAuthProperties.setPassword(new VariableString(password));
			properties.setSecurity(basicAuthProperties);
		}
		else if (authType.equalsIgnoreCase(TypeSecurity.API_KEY.toString())) {
			properties.setTypeSecurity(TypeSecurity.API_KEY);
			
			ApiKeyProperties apiKeyProperties = new ApiKeyProperties();
			apiKeyProperties.setApiKeyName(new VariableString(apiKeyName));
			apiKeyProperties.setApiKey(new VariableString(apiKey));
			properties.setSecurity(apiKeyProperties);
		}
		else {
			properties.setTypeSecurity(TypeSecurity.NO_AUTH);
		}
		
		properties.setUnzip(false);
		properties.setUr(new VariableString(apiURL));
		if (layer != null) {
			properties.setLayer(new VariableString(layer));
		}

		DataServiceActivity itemActivity = new DataServiceActivity("API_Input");
		itemActivity.setAttribute(properties);
		if (serviceType != null && serviceType.equalsIgnoreCase("WFS")) {
			itemActivity.setTypeService(TypeService.WFS);
		}
		else {
			itemActivity.setTypeService(TypeService.API);
		}
		itemActivity.setLeft(150);
		itemActivity.setTop(100);

		return itemActivity;
	}

	private Activity buildSFTPSource(String vanillaHubUrl, String sessionId, String sftpServerName, String sftpUrl, String sftpPort, String sftpLogin, String sftpPassword, String folderPath, String documentRegex) throws Exception {
		IResourceManager resourceManager = new RemoteResourceManager(vanillaHubUrl, sessionId, null);

		Source sftpServer = getSFTPServer(resourceManager, sftpServerName, sftpUrl, sftpPort, sftpLogin, sftpPassword, folderPath);

		SourceActivity itemActivity = new SourceActivity("SFTP_Input");
		itemActivity.setResource(sftpServer);
		itemActivity.setCibleItem(new VariableString(documentRegex));
		itemActivity.setLeft(150);
		itemActivity.setTop(100);

		return itemActivity;
	}

	private Workflow buildHub(IRepositoryContext ctx, String vanillaHubUrl, String sessionId, User user, Contract contract, String hubName, String vanillaServerName, String hubCibleName, 
			Activity source, ContractType type, LimeSurveyType limeSurveyType, String d4cUrl, String d4cOrg, String d4cLogin, String d4cPassword, String datasetName, ISchedule schedule) throws Exception {
		IHubWorkflowManager hubManager = new RemoteWorkflowManager(vanillaHubUrl, sessionId, null);
		IResourceManager resourceManager = new RemoteResourceManager(vanillaHubUrl, sessionId, null);

		StartActivity startActivity = new StartActivity("Start");
		startActivity.setLeft(50);
		startActivity.setTop(100);

		VanillaServer vanillaServer = getVanillaServer(resourceManager, ctx, vanillaServerName);

		MergeFilesActivity mergeActivity = null;
		if (limeSurveyType != null && limeSurveyType == LimeSurveyType.LIMESURVEY_SHAPES) {
			mergeActivity = new MergeFilesActivity("Merge_shapes");
			mergeActivity.setLeft(350);
			mergeActivity.setTop(300);
			mergeActivity.setOutputFile(new VariableString(contract.getName() + "_merge.shp"));
			mergeActivity.setLoop(true);
		}

		MdmActivity mdmActivity = new MdmActivity("Architect_Target");
		mdmActivity.setResource(vanillaServer);
		mdmActivity.setLeft(350);
		mdmActivity.setTop(100);
		mdmActivity.setContractId(contract.getId());
		mdmActivity.setContractName(contract.getName());
		mdmActivity.setValidateData(false);

		MdmInputActivity mdmInputActivity = new MdmInputActivity("Architect_Input");
		mdmInputActivity.setResource(vanillaServer);
		mdmInputActivity.setLeft(500);
		mdmInputActivity.setTop(100);
		mdmInputActivity.setContractId(contract.getId());
		mdmInputActivity.setContractName(contract.getName());
		mdmInputActivity.setValidateData(true);

		Cible cibleD4C = getCibleData4Citizen(resourceManager, hubCibleName, d4cUrl, d4cOrg, d4cLogin, d4cPassword);

		CibleActivity cibleActivity = new CibleActivity("D4C");
		cibleActivity.setResource(cibleD4C);
		cibleActivity.setTargetItem(new VariableString(datasetName));
		cibleActivity.setLeft(750);
		cibleActivity.setTop(100);

		StopActivity stopActivity = new StopActivity("Stop");
		stopActivity.setLeft(900);
		stopActivity.setTop(100);

		List<Link> links = new ArrayList<Link>();
		if (limeSurveyType != null && limeSurveyType == LimeSurveyType.LIMESURVEY_SHAPES) {
			links.add(new Link(startActivity, source));
			links.add(new Link(source, mergeActivity));
			links.add(new Link(mergeActivity, mdmActivity));
			links.add(new Link(mdmActivity, mdmInputActivity));
		}
		else if (type == ContractType.DOCUMENT) {
			links.add(new Link(startActivity, mdmInputActivity));
		}
		else {
			links.add(new Link(startActivity, source));
			links.add(new Link(source, mdmActivity));
			links.add(new Link(mdmActivity, mdmInputActivity));
		}
		links.add(new Link(mdmInputActivity, cibleActivity));
		links.add(new Link(cibleActivity, stopActivity));

		// Generate VanillaHub
		Workflow workflow = new Workflow();
		workflow.setName(hubName);
		workflow.setAuthor(user.getId(), user.getName());
		workflow.getWorkflowModel().addActivity(startActivity);
		if (limeSurveyType != null && limeSurveyType == LimeSurveyType.LIMESURVEY_SHAPES) {
			workflow.getWorkflowModel().addActivity(source);
			workflow.getWorkflowModel().addActivity(mergeActivity);
			workflow.getWorkflowModel().addActivity(mdmActivity);
		}
		else if (type != ContractType.DOCUMENT) {
			workflow.getWorkflowModel().addActivity(source);
			workflow.getWorkflowModel().addActivity(mdmActivity);
		}
		workflow.getWorkflowModel().addActivity(mdmInputActivity);
		workflow.getWorkflowModel().addActivity(cibleActivity);
		workflow.getWorkflowModel().addActivity(stopActivity);
		workflow.getWorkflowModel().setLinks(links);

		// Build schedule
		bpm.workflow.commons.beans.Schedule hubSchedule = buildSchedule(schedule);
		workflow.setSchedule(hubSchedule);
		
		return hubManager.manageWorkflow(workflow, false);
	}

	@SuppressWarnings("unchecked")
	private VanillaServer getVanillaServer(IResourceManager resourceManager, IRepositoryContext ctx, String vanillaServerName) throws Exception {
		List<ApplicationServer> resources = (List<ApplicationServer>) resourceManager.getResources(TypeResource.APPLICATION_SERVER);
		if (resources != null) {
			for (ApplicationServer server : resources) {
				if (server.getName().equals(vanillaServerName)) {
					return (VanillaServer) server;
				}
			}
		}

		VariableString url = new VariableString(ctx.getVanillaContext().getVanillaUrl());
		VariableString login = new VariableString(ctx.getVanillaContext().getLogin());
		VariableString password = new VariableString(ctx.getVanillaContext().getPassword());
		VariableString repositoryId = new VariableString(ctx.getRepository().getId());
		VariableString groupId = new VariableString(ctx.getGroup().getId());

		VanillaServer server = new VanillaServer(vanillaServerName);
		server.setUrl(url);
		server.setLogin(login);
		server.setPassword(password);
		server.setRepositoryId(repositoryId);
		server.setGroupId(groupId);

		return (VanillaServer) resourceManager.manageResource(server, false);
	}

	@SuppressWarnings("unchecked")
	private LimeSurveyServer getLimeSurveyServer(IResourceManager resourceManager, String limeSurveyServerName, String limeSurveyUrl, String limeSurveyLogin, String limeSurveyPassword) throws Exception {
		List<ApplicationServer> resources = (List<ApplicationServer>) resourceManager.getResources(TypeResource.APPLICATION_SERVER);
		if (resources != null) {
			for (ApplicationServer server : resources) {
				if (server.getName().equals(limeSurveyServerName)) {
					return (LimeSurveyServer) server;
				}
			}
		}

		VariableString url = new VariableString(limeSurveyUrl);
		VariableString login = new VariableString(limeSurveyLogin);
		VariableString password = new VariableString(limeSurveyPassword);

		LimeSurveyServer server = new LimeSurveyServer(limeSurveyServerName);
		server.setUrl(url);
		server.setLogin(login);
		server.setPassword(password);

		return (LimeSurveyServer) resourceManager.manageResource(server, false);
	}

	@SuppressWarnings("unchecked")
	private Source getSFTPServer(IResourceManager resourceManager, String sftpServerName, String sftpUrl, String sftpPort, String sftpLogin, String sftpPassword, String folderPath) throws Exception {
		List<Source> resources = (List<Source>) resourceManager.getResources(TypeResource.SOURCE);
		if (resources != null) {
			for (Source server : resources) {
				if (server.getName().equals(sftpServerName)) {
					return (Source) server;
				}
			}
		}

		Source server = new Source(sftpServerName);
		server.setType(TypeSource.SFTP);
		server.setUrl(new VariableString(sftpUrl));
		server.setPort(new VariableString(sftpPort));
		server.setFolderPath(new VariableString(folderPath));
		server.setSecured(true);
		server.setLogin(sftpLogin);
		server.setPassword(sftpPassword);

		return (Source) resourceManager.manageResource(server, false);
	}

	@SuppressWarnings("unchecked")
	private Cible getCibleData4Citizen(IResourceManager resourceManager, String cibleName, String d4cUrl, String d4cOrg, String login, String password) throws Exception {
		VariableString d4cUrlVar = new VariableString(d4cUrl);
		boolean secured = true;
//		CkanPackage dataset = new CkanPackage(null, datasetName, "");

		//We try to find if a target with the same url and organisation exist
		
		List<Cible> resources = (List<Cible>) resourceManager.getResources(TypeResource.CIBLE);
		if (resources != null) {
			for (Cible cible : resources) {
				String targetUrl = cible.getUrlDisplay();
				String targetOrg = cible.getOrg();
				
				if (targetUrl != null && targetUrl.equals(d4cUrl) && targetOrg != null && targetOrg.equals(d4cOrg)) {
					cible.setUrl(d4cUrlVar);
					cible.setSecured(secured);
					cible.setLogin(login);
					cible.setPassword(password);
//					cible.setCkanPackage(dataset);
					cible.setOrg(d4cOrg);
					cible.setFolder(new VariableString());
					cible.setValidateData(true);

					return (Cible) resourceManager.manageResource(cible, true);
				}
			}
		}

		Cible cible = new Cible(cibleName);
		cible.setType(TypeCible.D4C);
		cible.setUrl(d4cUrlVar);
		cible.setSecured(secured);
		cible.setLogin(login);
		cible.setPassword(password);
//		cible.setCkanPackage(dataset);
		cible.setOrg(d4cOrg);
		cible.setFolder(new VariableString());
		cible.setValidateData(true);

		return (Cible) resourceManager.manageResource(cible, false);
	}

	// private DocumentGateway createDocument(IRepositoryContext ctx, String
	// name, String author) {
	//
	// DocumentGateway doc = new DocumentGateway();
	// doc.setAuthor(author);
	// doc.setCreationDate(Calendar.getInstance().getTime());
	// doc.setName(name);
	//
	// doc.setRepositoryContext(ctx);
	//
	// return doc;
	// }

	private IMdmProvider resetMdmProvider(IVanillaContext ctx) {
		try {
			IMdmProvider provider = new MdmRemote(ctx.getLogin(), ctx.getPassword(), ctx.getVanillaUrl(), null, null);
			return provider;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
			// MessageDialog.openError(getWorkbench().getActiveWorkbenchWindow().getShell(),
			// "Loading Mdm Model", "Unable to load model : " + e.getMessage());
		}
	}

	private Supplier getSupplier(IMdmProvider provider, ContractType type, String contractName) {
		Supplier supplier = null;
		try {
			String name = type.toString() + "_Supplier";

			List<Supplier> existingSuppliers = provider.getSuppliers();
			for (Supplier supp : existingSuppliers) {
				if (supp.getName().equals(name)) {
					supplier = supp;
					break;
				}
			}

			if (supplier == null) {
				supplier = new Supplier();
				supplier.setName(name);
				supplier.setExternalId(name + "Id");
				supplier.setExternalSource(name);
			}

			for (Contract cont : supplier.getContracts()) {
				if (cont.getName().equals(contractName)) {
					return supplier;
				}
			}

			Contract contract = new Contract();
			contract.setType(type);
			contract.setName(contractName);
			contract.setExternalId(contractName + "Id");
			contract.setExternalSource(contractName);

			contract.setParent(supplier);
			supplier.addContract(contract);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return supplier;
	}
	
	private ContractIntegrationInformations modifySimpleKPI(IRepositoryContext ctx, Contract contract, SimpleKPIGenerationInformations infos, boolean modifyIntegration) throws Exception {
		int hubId = infos.getHubId();
		
		IHubWorkflowManager hubManager = getWorkflowManager(ctx);
		Workflow workflow = hubManager.getWorkflow(hubId, false);
		
		if (modifyIntegration) {
			String sqlQuery = infos.getSqlQuery();

			String selectedDatasetId = infos.getSourceDatasetId();
			String cleanDatasetId = cleanDatasetName(selectedDatasetId);
			String prefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + cleanDatasetId.substring(0, 5);

			String etlName = "etl_join_kpi_" + prefix;
			RepositoryDirectory target = getTarget(ctx, infos, prefix);
			RepositoryItem etl = buildSimpleKPIEtl(ctx, target, etlName, sqlQuery, contract);
			
			List<Activity> activities = workflow.getWorkflowModel().getActivities();
			if (activities != null) {
				for (Activity activity : activities) {
					if (activity.getType() == TypeActivity.VANILLA_ITEM) {
						((RunVanillaItemActivity) activity).setItemId(etl.getId());
						((RunVanillaItemActivity) activity).setItemName(etl.getName());
					}
				}
			}
		}
		
		// Build schedule
		bpm.workflow.commons.beans.Schedule hubSchedule = buildSchedule(infos.getSchedule());
		hubSchedule.setId(workflow.getSchedule() != null ? workflow.getSchedule().getId() : 0);
		workflow.setSchedule(hubSchedule);
		hubManager.manageWorkflow(workflow, true);
		
		ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations();
		integrationInfos.setType(ContractType.KPI);
		integrationInfos.setHubId(workflow.getId());
		return integrationInfos;
	}

	private ContractIntegrationInformations generateSimpleKPI(IRepositoryContext ctx, Contract contract, SimpleKPIGenerationInformations infos) throws Exception {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		String d4cUrl = infos.getD4cUrl();
		String d4cLogin = getD4cLogin(config, infos);
		String d4cPassword = getD4cPassword(config, infos);

		String observatory = infos.getSourceOrganisation();
		String selectedDatasetId = infos.getSourceDatasetId();
		String targetOrganisation = infos.getTargetOrganisation();
		String datasetId = infos.getTargetDatasetName();
		String datasetName = infos.getTargetDatasetCustomName();
		
//		String cleanDatasetId = cleanDatasetName(selectedDatasetId);
		String cleanDatasetName = cleanDatasetName(datasetName);
		String prefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + cleanDatasetName;

		Group group = ctx.getGroup();
		List<Group> groups = new ArrayList<Group>();
		groups.add(group);
		
		String contractName = prefix + "_contract";
		
		try {
			// If the contractId is not define we generate an Architect contract
			if (contract == null) {
				contract = buildArchitect(ctx, contractName, ContractType.KPI, infos.getValidationSchemas(), infos.getMetadata());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Architect: " + e.getMessage(), e);
		}

		String sqlQuery = infos.getSqlQuery();

		RepositoryDirectory target = getTarget(ctx, infos, prefix);

		String etlName = prefix + "_KPI";

		RepositoryItem etl = buildSimpleKPIEtl(ctx, target, etlName, sqlQuery, contract);

		String hubName = prefix + "_KPI";
		String vanillaServerName = "Vanilla_Server_KPI_" + ctx.getGroup().getId();
		String hubCibleName = "D4C_Cible_KPI_" + contractName + "_" + new Object().hashCode();

		Workflow workflow = null;
		try {
			String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
			String login = ctx.getVanillaContext().getLogin();
			String password = ctx.getVanillaContext().getPassword();

			// Create HUB to retrieve data from service, integrate in
			// Architect and load in Data4Citizen
			IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
			User user = manager.login(login, password, null);
			String sessionId = manager.connect(user);

			IResourceManager resourceManager = new RemoteResourceManager(vanillaHubUrl, sessionId, null);
			Cible targetD4C = getCibleData4Citizen(resourceManager, hubCibleName, d4cUrl, targetOrganisation, d4cLogin, d4cPassword);
			
			List<RepositoryItem> items = new ArrayList<RepositoryItem>();
			items.add(etl);
			workflow = buildEtlHub(ctx, vanillaHubUrl, sessionId, user, hubName, vanillaServerName, items, contract, targetD4C, datasetId, infos.getSchedule());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		try {
			String kpiName = "Indicateurs (" + datasetId + ")";

			// Save integration process
			ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations();
			integrationInfos.setType(ContractType.KPI);
			integrationInfos.setContractId(contract.getId());
			integrationInfos.setDocumentId(contract.getDocId());
			integrationInfos.setNameService(kpiName);
			integrationInfos.setHubId(workflow.getId());
			integrationInfos.setHubName(hubName);
			integrationInfos.setTargetOrganisation(observatory);
			integrationInfos.setItem(selectedDatasetId);
			integrationInfos.setTargetDatasetName(datasetId);
			integrationInfos.setSourceOrganisation(observatory);

			IMdmProvider mdmProvider = getMdmProvider(ctx);
			mdmProvider.saveOrUpdateIntegrationInfos(integrationInfos);

			return integrationInfos;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}
	}
	
	private RepositoryDirectory getTarget(IRepositoryContext ctx, SimpleKPIGenerationInformations infos, String prefix) throws Exception {
		int folderTargetId = infos.getFolderTargetId();
		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		RepositoryDirectory target = repositoryApi.getRepositoryService().getDirectory(folderTargetId);
		if (target == null) {
			throw new Exception("There is no vanilla directory available.");
		}

		// We create a folder for each process
		return repositoryApi.getRepositoryService().addDirectory(prefix, "", target);
	}

	private RepositoryItem buildSimpleKPIEtl(IRepositoryContext ctx, RepositoryDirectory target, String etlName, String query, Contract contract) throws Exception {
		DataBaseServer serverDB = buildDatastoreDatabaseServer(ctx.getVanillaContext());

		DocumentGateway gateway = new DocumentGateway();
		gateway.setName(etlName);
		gateway.addServer(serverDB);

		DataBaseInputStream inputDB = new DataBaseInputStream();
		inputDB.setName("DataBaseInputStream");
		inputDB.setDefinition(query);
		inputDB.setPositionX(200);
		inputDB.setPositionY(200);
		inputDB.setServer(serverDB);
		gateway.addTransformation(inputDB);

		inputDB.refreshDescriptor();

		MdmFileServer serverMDM = new MdmFileServer("mdmserver", 
				"", 
				ctx.getVanillaContext().getVanillaUrl(), 
				ctx.getVanillaContext().getLogin(), 
				ctx.getVanillaContext().getPassword(), 
				ctx.getRepository().getId() + "");
		gateway.addServer(serverMDM);

		FileOutputCSV outputCsv = new FileOutputCSV();
		outputCsv.setName("Output_MDM");
		outputCsv.setPositionX(100);
		outputCsv.setPositionY(50);
		outputCsv.setTemporaryFilename("");
		outputCsv.setUseMdm(true);
		outputCsv.setDelete(false);
		outputCsv.setSeparator(',');
		outputCsv.setAppend(false);
		outputCsv.setServer(serverMDM);
		outputCsv.setContract(contract);
		outputCsv.setDefinition(String.valueOf(contract.getId()));
		outputCsv.setSupplierId(contract.getParent().getId());
		outputCsv.setContractId(contract.getId());
		gateway.addTransformation(outputCsv);

		/*
		 * link the transformations
		 */
		inputDB.addOutput(outputCsv);
		outputCsv.addInput(inputDB);

		// We publish the ETL
		String modelETL = gateway.getElement().asXML();

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		RepositoryItem itemETL = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.GTW_TYPE, -1, target, etlName, "", "", "", modelETL, true);
		return itemETL;
	}

	private DataBaseServer buildDatastoreDatabaseServer(IVanillaContext ctx) throws Exception {
		Datasource ds = getDatastoreJdbcDatasource();
		DatasourceJdbc datasource = (DatasourceJdbc) ds.getObject();

		DataBaseServer server = new DataBaseServer();
		server.setName("ckan_datastore");

		DataBaseConnection dbCon = new DataBaseConnection();
		dbCon.setName("ckan_datastore");
		dbCon.setLogin(datasource.getUser());
		dbCon.setPassword(datasource.getPassword());
		dbCon.setUseFullUrl(datasource.getFullUrl());
		if (datasource.getFullUrl()) {
			dbCon.setFullUrl(datasource.getUrl());
		}
		else {
			dbCon.setHost(datasource.getHost());
			dbCon.setPort(datasource.getPort());
			dbCon.setDataBaseName(datasource.getDatabaseName());
		}

		String driverName = findDriver(datasource.getDriver());
		dbCon.setDriverName(driverName);

		// dbCon.setServer(server);
		server.addConnection(dbCon);
		server.setCurrentConnection(dbCon);
		return server;
	}

	private Datasource getDatastoreJdbcDatasource() throws Exception {
		if (datastoreDatasource != null) {
			return datastoreDatasource;
		}

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String jdbcUrl = config.getProperty(VanillaConfiguration.P_CKAN_DATASTORE_DB_JDBCURL);
		String user = config.getProperty(VanillaConfiguration.P_CKAN_DATASTORE_DB_USERNAME);
		String password = config.getProperty(VanillaConfiguration.P_CKAN_DATASTORE_DB_PASSWORD);
		String driverClass = config.getProperty(VanillaConfiguration.P_CKAN_DATASTORE_DB_DRIVERCLASSNAME);

		DatasourceJdbc jdbc = new DatasourceJdbc();
		jdbc.setDatabaseName("ckan_datastore");
		jdbc.setDriver(driverClass);
		jdbc.setFullUrl(true);
		jdbc.setUrl(jdbcUrl);
		jdbc.setUser(user);
		jdbc.setPassword(password);

		Datasource datasource = new Datasource();
		datasource.setName("ckan_datastore");
		datasource.setType(DatasourceType.JDBC);
		datasource.setObject(jdbc);

		this.datastoreDatasource = datasource;
		return datastoreDatasource;
	}

	@Override
	public ContractIntegrationInformations generateKPI(IRepositoryContext ctx, KPIGenerationInformations infos) throws Exception {
		IFreeMetricsManager kpiManager = getKPIManager(ctx);

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String schemaKpi = config.getProperty(VanillaConfiguration.P_KPI_DATA_DB_SCHEMA);
		String ckanUrl = config.getProperty(VanillaConfiguration.P_CKAN_URL);
		String ckanApiKey = config.getProperty(VanillaConfiguration.P_CKAN_API_KEY);

		Group group = ctx.getGroup();

		List<Group> groups = new ArrayList<Group>();
		groups.add(group);

		// Manage observatory
		String observatory = infos.getObservatory();

		Observatory selectedObservatory = null;
		try {
			List<Observatory> observatories = kpiManager.getObservatories();
			if (observatories != null && !observatories.isEmpty()) {
				for (Observatory obs : observatories) {
					if (obs.getName().equals(observatory)) {
						selectedObservatory = obs;
						break;
					}
				}
			}

			if (selectedObservatory == null) {
				selectedObservatory = createObservatory(kpiManager, observatory, groups);
			}

			selectedObservatory = kpiManager.getObservatoryById(selectedObservatory.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (selectedObservatory == null) {
			throw new Exception("Unable to create the observatory.");
		}

		// Manage theme
		String theme = infos.getTheme();

		Theme selectedTheme = null;
		try {
			List<Theme> themes = kpiManager.getThemes();
			if (themes != null && !themes.isEmpty()) {
				for (Theme the : themes) {
					if (the.getName().equals(theme)) {
						selectedTheme = the;
						break;
					}
				}
			}

			if (selectedTheme == null) {
				selectedTheme = createTheme(kpiManager, theme, groups);
			}

			// Check if the theme is associated to the observatory
			boolean found = false;
			if (selectedObservatory.getThemes() != null) {
				for (Theme the : selectedObservatory.getThemes()) {
					if (the.getId() == selectedTheme.getId()) {
						found = true;
						break;
					}
				}
			}

			if (!found) {
				selectedObservatory.addTheme(selectedTheme);
				kpiManager.updateObservatory(selectedObservatory);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (selectedTheme == null) {
			throw new Exception("Unable to create the theme.");
		}

		// Get file from the dataset for the specified resource
		String d4cUrl = config.getProperty(VanillaConfiguration.P_D4C_URL);
		String d4cLogin = config.getProperty(VanillaConfiguration.P_D4C_LOGIN);
		String d4cPassword = config.getProperty(VanillaConfiguration.P_D4C_PASSWORD);

		// String d4cUrl = infos.getD4cUrl();
		String selectedDatasetId = infos.getDatasetId();
		// String selectedResourceFormat = "csv"; // Only support csv for now
		String selectedResourceId = infos.getResourceId();
		// String selectedResourceUrl = infos.getResourceUrl();

		D4CHelper helper = new D4CHelper(d4cUrl, observatory, d4cLogin, d4cPassword);
		CkanPackage pack = helper.findCkanPackage(selectedDatasetId);
		CkanResource resource = findResource(pack, selectedResourceId);
		if (resource == null) {
			throw new Exception("Could not find resource with ID '" + selectedResourceId + "' for dataset '" + selectedDatasetId + "'");
		}

		String cleanDatasetId = cleanDatasetName(pack.getId());
		String prefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "_" + cleanDatasetId.substring(0, 5);

		int folderTargetId = infos.getFolderTargetId();

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		RepositoryDirectory target = repositoryApi.getRepositoryService().getDirectory(folderTargetId);
		if (target == null) {
			throw new Exception("There is no vanilla directory available.");
		}

		// We create a folder for each process
		target = repositoryApi.getRepositoryService().addDirectory(prefix, "", target);

		List<RepositoryItem> items = new ArrayList<RepositoryItem>();

		char separator = ',';
		String loadTableName = cleanTableName(prefix + "_load");
		RepositoryItem etlLoad = buildETLLoad(ctx, target, helper, d4cUrl, observatory, d4cLogin, d4cPassword, pack, resource, separator, schemaKpi, loadTableName);
		items.add(etlLoad);

		// Manage and load axes
		List<String> axes = infos.getAxes();
		List<String> metrics = infos.getMetrics();
		if (axes == null || axes.isEmpty() || metrics == null || metrics.isEmpty()) {
			throw new Exception("Axes or metrics are not defined");
		}

		List<Axis> fmAxis = new ArrayList<Axis>();
		for (String axe : axes) {
			String etlName = "etl_axe_" + cleanValue(axe);
			String tableName = cleanTableName(prefix + "_axe_" + cleanValue(axe));
			String columnId = "id_axe_" + cleanValue(axe);
			String columnName = "axe_value";

			String axeTableName = createKPIAxeTable(ctx.getVanillaContext(), schemaKpi, tableName, columnId, columnName);
			Axis fmAx = createAxisAndLevel(ctx.getVanillaContext(), kpiManager, axe, axeTableName, columnId, columnName, selectedTheme);

			fmAxis.add(fmAx);

			RepositoryItem itemAxe = buildKPIAxeEtl(ctx, target, etlName, loadTableName, axeTableName, fmAx.getChildren().get(0));
			items.add(itemAxe);
		}

		Date selectedDate = infos.getSelectedDate();

		// Manage and load metrics
		for (String metricName : metrics) {
			String etlName = "etl_metric_" + cleanValue(metricName);
			String metricTableName = createKPIMetricTable(ctx.getVanillaContext(), schemaKpi, prefix, metricName, axes);
			Metric metric = createMetric(ctx.getVanillaContext(), kpiManager, metricName, metricTableName, fmAxis, selectedTheme);

			RepositoryItem itemMetric = buildKPIMetricEtl(ctx, target, etlName, loadTableName, metricTableName, fmAxis, metric, selectedDate);
			items.add(itemMetric);
		}

		String metricsName = StringUtils.join(metrics, "_");

		String hubName = prefix + "_HUB_KPI_" + metricsName;
		String vanillaServerName = "Vanilla_Server_KPI_" + ctx.getGroup().getId();

		Workflow workflow = null;
		try {
			String vanillaHubUrl = ctx.getVanillaContext().getVanillaUrl();
			String login = ctx.getVanillaContext().getLogin();
			String password = ctx.getVanillaContext().getPassword();

			// Create HUB to retrieve data from service, integrate in
			// Architect and load in Data4Citizen
			IAdminManager manager = new RemoteAdminManager(vanillaHubUrl, null, null);
			User user = manager.login(login, password, null);
			String sessionId = manager.connect(user);

			workflow = buildEtlHub(ctx, vanillaHubUrl, sessionId, user, hubName, vanillaServerName, items, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}

		try {
			String kpiName = "Indicateurs (" + StringUtils.join(metrics, ",") + ") - Theme (" + theme + ")";

			// Save integration process
			ContractIntegrationInformations integrationInfos = new ContractIntegrationInformations();
			integrationInfos.setType(ContractType.KPI);
//			integrationInfos.setContractId(contract.getId());
			integrationInfos.setNameService(kpiName);
			integrationInfos.setHubId(workflow.getId());
			integrationInfos.setHubName(hubName);
			integrationInfos.setTargetOrganisation(observatory);
			integrationInfos.setItem(selectedDatasetId);
			integrationInfos.setTargetDatasetName(pack.getName());

			IMdmProvider mdmProvider = getMdmProvider(ctx);
			mdmProvider.saveOrUpdateIntegrationInfos(integrationInfos);

			return integrationInfos;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to create Hub: " + e.getMessage(), e);
		}
	}

	private String cleanTableName(String tableName) {
		// We need to substring the table name as mysql is limited to 64 char
		return tableName.length() > 63 ? tableName.substring(0, 63) : tableName;
	}

	private CkanResource findResource(CkanPackage pack, String selectedResourceId) {
		if (pack.getResources() != null) {
			for (CkanResource resource : pack.getResources()) {
				if (resource.getId().equals(selectedResourceId)) {
					return resource;
				}
			}
		}
		return null;
	}

	private Workflow buildEtlHub(IRepositoryContext ctx, String vanillaHubUrl, String sessionId, User user, String hubName, String vanillaServerName, 
			List<RepositoryItem> items, Contract inputContract, Cible targetD4C, String datasetName, ISchedule schedule) throws Exception {
		IHubWorkflowManager hubManager = new RemoteWorkflowManager(vanillaHubUrl, sessionId, null);
		IResourceManager resourceManager = new RemoteResourceManager(vanillaHubUrl, sessionId, null);

		// Generate VanillaHub
		Workflow workflow = new Workflow();
		workflow.setName(hubName);
		workflow.setAuthor(user.getId(), user.getName());
		List<Link> links = new ArrayList<Link>();

		StartActivity startActivity = new StartActivity("Start");
		startActivity.setLeft(50);
		startActivity.setTop(100);
		workflow.getWorkflowModel().addActivity(startActivity);

		VanillaServer vanillaServer = getVanillaServer(resourceManager, ctx, vanillaServerName);

		Activity previousActivity = startActivity;
		int i = 0;
		for (RepositoryItem item : items) {
			RunVanillaItemActivity runItem = new RunVanillaItemActivity(item.getName());
			runItem.setLeft(150 + (i*150));
			runItem.setTop(100);
			runItem.setItemId(item.getId());
			runItem.setItemName(item.getName());
			runItem.setResource(vanillaServer);
			workflow.getWorkflowModel().addActivity(runItem);

			links.add(new Link(previousActivity, runItem));
			previousActivity = runItem;
			
			i++;
		}
		
		if (inputContract != null && targetD4C != null) {
			MdmInputActivity mdmInputActivity = new MdmInputActivity("Architect_Input");
			mdmInputActivity.setResource(vanillaServer);
			mdmInputActivity.setLeft(150 + (i*150));
			mdmInputActivity.setTop(100);
			mdmInputActivity.setContractId(inputContract.getId());
			mdmInputActivity.setContractName(inputContract.getName());
			mdmInputActivity.setValidateData(true);
			
			workflow.getWorkflowModel().addActivity(mdmInputActivity);
			links.add(new Link(previousActivity, mdmInputActivity));
			i++;

			CibleActivity cibleActivity = new CibleActivity("D4C");
			cibleActivity.setResource(targetD4C);
			cibleActivity.setTargetItem(new VariableString(datasetName));
			cibleActivity.setLeft(150 + (i*150));
			cibleActivity.setTop(100);

			workflow.getWorkflowModel().addActivity(cibleActivity);
			links.add(new Link(mdmInputActivity, cibleActivity));
			i++;
			
			previousActivity = cibleActivity;
		}
		
		
		

		StopActivity stopActivity = new StopActivity("Stop");
		stopActivity.setLeft(150 + (i*150));
		stopActivity.setTop(100);
		links.add(new Link(previousActivity, stopActivity));
		workflow.getWorkflowModel().addActivity(stopActivity);

		workflow.getWorkflowModel().setLinks(links);
		
		// Build schedule
		bpm.workflow.commons.beans.Schedule hubSchedule = buildSchedule(schedule);
		workflow.setSchedule(hubSchedule);

		return hubManager.manageWorkflow(workflow, false);
	}

	private Schedule buildSchedule(ISchedule ischedule) {
		if (ischedule == null) {
			return null;
		}
		
		Schedule schedule = (Schedule) ischedule;
		
		Period period = Period.valueOf(schedule.getPeriod().getType());
		int interval = schedule.getInterval();
		
		Calendar c = Calendar.getInstance();
		c.setTime(schedule.getBeginDate());
		
		// We need to add an interval to the definition to avoid the ETL to be run twice on creation
		SchedulerUtils.addInterval(period, c, interval);
		
		Schedule hubSchedule = new Schedule();
		hubSchedule.setBeginDate(c.getTime());
		hubSchedule.setInterval(interval);
		hubSchedule.setOn(schedule.isOn());
		hubSchedule.setPeriod(period);
		hubSchedule.setStopDate(schedule.getStopDate());
		hubSchedule.setWorkflowId(schedule.getWorkflowId());
		return hubSchedule;
	}

	private Axis createAxisAndLevel(IVanillaContext ctx, IFreeMetricsManager kpiManager, String axe, String tableName, String columnId, String columnName, Theme theme) throws Exception {
		Datasource datasource = getKpiJdbcDatasource(ctx);

		Axis axis = new Axis();
		axis.setName(axe);

		Level level = new Level();
		level.setColumnId(columnId);
		level.setColumnName(columnName);
		level.setDatasourceId(datasource.getId());
		level.setLevelIndex(0);
		level.setName(axe);
		level.setParent(axis);
		level.setTableName(tableName);

		axis.addChild(level);

		axis = kpiManager.addAxis(axis);

		ThemeAxis themeAxis = new ThemeAxis();
		themeAxis.setAxisId(axis.getId());
		themeAxis.setThemeId(theme.getId());

		List<ThemeAxis> themesAxis = new ArrayList<ThemeAxis>();
		themesAxis.add(themeAxis);

		kpiManager.addThemeAxis(themesAxis);

		return axis;
	}

	private Metric createMetric(IVanillaContext ctx, IFreeMetricsManager kpiManager, String metricName, String tableName, List<Axis> axis, Theme theme) throws Exception {
		Datasource datasource = getKpiJdbcDatasource(ctx);

		Metric metric = new Metric();
		metric.setDirection(Metric.DIRECTIONS.get(0));
		metric.setMetricType(Metric.METRIC_TYPES.get(0));
		metric.setName(metricName);
		metric.setOperator("sum");

		metric = kpiManager.addMetric(metric);

		ThemeMetric themeMetric = new ThemeMetric();
		themeMetric.setMetricId(metric.getId());
		themeMetric.setThemeId(theme.getId());

		List<ThemeMetric> themeMetrics = new ArrayList<ThemeMetric>();
		themeMetrics.add(themeMetric);

		kpiManager.addThemeMetric(themeMetrics);

		FactTable factTable = (FactTable) metric.getFactTable();
		factTable.setDatasource(datasource);
		factTable.setDatasourceId(datasource.getId());
		factTable.setMetricId(metric.getId());
		factTable.setPeriodicity(FactTable.PERIODICITY_MONTHLY);
		factTable.setDateColumn("metric_date");
		factTable.setTableName(tableName);
		factTable.setValueColumn("metric_value");

		List<FactTableAxis> factTableAxis = new ArrayList<FactTableAxis>();
		for (Axis ax : axis) {
			String columnId = "id_axe_" + cleanValue(ax.getName());

			FactTableAxis ftAxis = new FactTableAxis();
			ftAxis.setAxis(ax);
			ftAxis.setAxisId(ax.getId());
			ftAxis.setColumnId(columnId);

			factTableAxis.add(ftAxis);
		}

		factTable.setFactTableAxis(factTableAxis);

		metric = kpiManager.updateMetric(metric);

		return metric;
	}

	private RepositoryItem buildETLLoad(IRepositoryContext ctx, RepositoryDirectory target, D4CHelper helper, String ckanUrl, String organisation, String login, String password, CkanPackage pack, CkanResource resource, char separator, String schema, String loadTableName) throws Exception {

		String datasetId = pack.getId().replace("-", "_");

		List<String> headers = new ArrayList<String>();

		CSVFormat format = CSVFormat.newFormat(separator).withQuote('"');
		try (InputStream inputStream = helper.getResourceFile(resource); CSVParser parser = CSVParser.parse(inputStream, Charset.forName("UTF-8"), format)) {

			Iterator<CSVRecord> csvIterator = parser.iterator();
			CSVRecord recordHeader = csvIterator.next();

			Iterator<String> line = recordHeader.iterator();

			while (line.hasNext()) {
				String value = line.next();
				value = UTF8ToAnsiUtils.removeUTF8BOM(value);
				headers.add(value);
			}
		} catch (Exception e) {
			throw new Exception("Unable to load file to database", e);
		}

		if (headers == null || headers.isEmpty()) {
			throw new Exception("Unable to get columns");
		}

		String etlName = "etl_load_" + datasetId;

		String tableOutput = createKPILoadTable(ctx.getVanillaContext(), schema, loadTableName, datasetId, headers);
		return buildKPILoadEtl(ctx, target, etlName, ckanUrl, organisation, login, password, pack, resource, tableOutput, separator);
	}

	private String createKPIAxeTable(IVanillaContext ctx, String schema, String tableName, String columnId, String columnName) throws Exception {
		StringBuffer buf = new StringBuffer();
		buf.append("CREATE TABLE " + schema + "." + tableName + " (\r\n");
		buf.append("  " + columnId + " INT NOT NULL AUTO_INCREMENT,\r\n");
		buf.append("  " + columnName + " LONGTEXT NULL,\r\n");
		buf.append("  PRIMARY KEY (" + columnId + "));\r\n");

		try {
			executeQuery(ctx, buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
			if (!e.getMessage().contains("already exists")) {
				// We do nothing else we throw the exception
				throw e;
			}
		}

		return tableName;
	}

	private String createKPIMetricTable(IVanillaContext ctx, String schema, String cleanDatasetId, String metric, List<String> axes) throws Exception {
		String tableName = cleanTableName(cleanDatasetId + "_metric_" + cleanValue(metric));
		String columnId = "id_" + tableName;

		StringBuffer buf = new StringBuffer();
		buf.append("CREATE TABLE " + schema + "." + tableName + " (\r\n");
		buf.append("  " + columnId + " INT NOT NULL AUTO_INCREMENT,\r\n");
		buf.append("  metric_value DOUBLE DEFAULT NULL,\r\n");
		buf.append("  metric_date DATETIME DEFAULT NULL,\r\n");

		for (String axe : axes) {
			buf.append("  id_axe_" + cleanValue(axe) + " INT DEFAULT NULL,\r\n");
		}

		buf.append("  PRIMARY KEY (" + columnId + "));\r\n");

		try {
			executeQuery(ctx, buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
			if (!e.getMessage().contains("already exists")) {
				// We do nothing else we throw the exception
				throw e;
			}
		}

		return tableName;
	}

	private String createKPILoadTable(IVanillaContext ctx, String schema, String tableName, String datasetId, List<String> columns) throws Exception {
		String columnId = "id_" + tableName;

		StringBuffer buf = new StringBuffer();
		buf.append("CREATE TABLE " + schema + "." + tableName + " (\r\n");
		buf.append("  " + columnId + " INT NOT NULL AUTO_INCREMENT,\r\n");
		for (String column : columns) {
			buf.append(column + " LONGTEXT DEFAULT NULL,\r\n");
		}

		buf.append("  PRIMARY KEY (" + columnId + "));\r\n");

		try {
			executeQuery(ctx, buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
			if (!e.getMessage().contains("already exists")) {
				// We do nothing else we throw the exception
				throw e;
			}
		}

		return tableName;
	}

	private void executeQuery(IVanillaContext ctx, String query) throws Exception {
		VanillaJdbcConnection con = null;
		VanillaPreparedStatement stmt = null;
		try {
			con = getKpiJdbcConnection(ctx);

			stmt = con.prepareQuery(query);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLConnectionException(e.getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (con != null) {
				ConnectionManager.getInstance().returnJdbcConnection(con);
			}
		}
	}

	private VanillaJdbcConnection getKpiJdbcConnection(IVanillaContext ctx) throws Exception {
		Datasource datasource = getKpiJdbcDatasource(ctx);
		DatasourceJdbc jdbc = (DatasourceJdbc) datasource.getObject();
		return ConnectionManager.getInstance().getJdbcConnection(jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword(), jdbc.getDriver());
	}

	private Datasource getKpiJdbcDatasource(IVanillaContext ctx) throws Exception {
		if (kpiDatasource != null) {
			return kpiDatasource;
		}

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String jdbcUrl = config.getProperty(VanillaConfiguration.P_KPI_DATA_DB_JDBCURL);
		String user = config.getProperty(VanillaConfiguration.P_KPI_DATA_DB_USERNAME);
		String password = config.getProperty(VanillaConfiguration.P_KPI_DATA_DB_PASSWORD);
		String driverClass = config.getProperty(VanillaConfiguration.P_KPI_DATA_DB_DRIVERCLASSNAME);

		List<DatasourceType> types = new ArrayList<DatasourceType>();
		types.add(DatasourceType.JDBC);

		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(ctx);
		List<Datasource> datasources = vanillaApi.getVanillaPreferencesManager().getDatasources(types);
		if (datasources != null) {
			for (Datasource datasource : datasources) {
				if (datasource.getObject() instanceof DatasourceJdbc) {
					DatasourceJdbc dsJdbc = (DatasourceJdbc) datasource.getObject();
					if (dsJdbc.getUrl().equals(jdbcUrl) && dsJdbc.getDriver().equals(driverClass)) {
						return datasource;
					}
				}
			}
		}

		DatasourceJdbc jdbc = new DatasourceJdbc();
		jdbc.setDatabaseName("vanilla_kpi_data");
		jdbc.setDriver(driverClass);
		jdbc.setFullUrl(true);
		jdbc.setUrl(jdbcUrl);
		jdbc.setUser(user);
		jdbc.setPassword(password);

		Datasource datasource = new Datasource();
		datasource.setName("vanilla_kpi_data");
		datasource.setType(DatasourceType.JDBC);
		datasource.setObject(jdbc);

		this.kpiDatasource = vanillaApi.getVanillaPreferencesManager().addDatasource(datasource, false);
		return kpiDatasource;
	}

	private String cleanDatasetName(String value) {
		return value.replace(" ", "_");
	}

	private String cleanValue(String value) {
		return value.replace(" ", "_").replace("-", "_");
	}

	private Observatory createObservatory(IFreeMetricsManager kpiManager, String observatory, List<Group> groups) throws Exception {
		Observatory obs = new Observatory();
		obs.setName(observatory);
		obs.setGroups(groups);

		obs = kpiManager.addObservatory(obs);

		List<GroupObservatory> groupsObservatory = new ArrayList<GroupObservatory>();
		for (Group group : groups) {
			GroupObservatory groupObs = new GroupObservatory();
			groupObs.setGroupId(group.getId());
			groupObs.setObservatoryId(obs.getId());
			groupsObservatory.add(groupObs);
		}

		kpiManager.addGroupObservatories(groupsObservatory);

		return obs;
	}

	private Theme createTheme(IFreeMetricsManager kpiManager, String theme, List<Group> groups) throws Exception {
		Theme the = new Theme();
		the.setName(theme);

		return kpiManager.addTheme(the);
	}

	private IFreeMetricsManager getKPIManager(IRepositoryContext ctx) {
		return new RemoteFreeMetricsManager(ctx.getVanillaContext());
	}

	private RepositoryItem buildKPIAxeEtl(IRepositoryContext ctx, RepositoryDirectory target, String etlName, String tableInput, String tableOutput, Level level) throws Exception {
		DataBaseServer serverDB = buildKpiDatabaseServer(ctx.getVanillaContext());

		DocumentGateway gateway = new DocumentGateway();
		gateway.setName(etlName);
		gateway.addServer(serverDB);

		DataBaseInputStream inputDB = new DataBaseInputStream();
		inputDB.setName("DataBaseInputStream");
		inputDB.setDefinition("SELECT * FROM " + tableInput);
		inputDB.setPositionX(200);
		inputDB.setPositionY(200);
		inputDB.setServer(serverDB);
		gateway.addTransformation(inputDB);

		inputDB.refreshDescriptor();

		SelectionTransformation select = new SelectionTransformation();
		select.setName("SelectColumns");
		select.setPositionX(200);
		select.setPositionY(400);
		gateway.addTransformation(select);

		inputDB.addOutput(select);
		select.addInput(inputDB);

		select.setInited();
		select.refreshDescriptor();

		StreamDescriptor dbDescriptor = inputDB.getDescriptor(null);

		for (int j = 0; j < dbDescriptor.getColumnCount(); j++) {
			StreamElement element = dbDescriptor.getStreamElements().get(j);
			if (!element.name.equals(level.getName())) {
				select.desactiveStreamElement(inputDB, element.getFullName());
			}
		}

		select.refreshDescriptor();

		SelectDistinct distinct = new SelectDistinct();
		distinct.setName("SelectDistinct");
		distinct.setPositionX(400);
		distinct.setPositionY(400);
		gateway.addTransformation(distinct);

		select.addOutput(distinct);
		distinct.addInput(select);

		distinct.setInited();
		distinct.refreshDescriptor();

		DataBaseOutputStream outputDB = new DataBaseOutputStream();
		outputDB.setName("DataBaseOutputStream");
		outputDB.setTableName(tableOutput);
		outputDB.setDefinition("select * from " + tableOutput);
		outputDB.setPositionX(600);
		outputDB.setPositionY(200);
		outputDB.setTruncate(true);
		outputDB.setServer(serverDB);
		gateway.addTransformation(outputDB);

		distinct.addOutput(outputDB);
		outputDB.addInput(distinct);

		outputDB.setInited();
		outputDB.refreshDescriptor();

		StreamDescriptor inputDescriptor = distinct.getDescriptor(null);

		for (int j = 0; j < inputDescriptor.getColumnCount(); j++) {
			String inputColumn = inputDescriptor.getColumnName(j);
			if (inputColumn.equals(level.getName())) {
				outputDB.createMapping(distinct, j, 1);
				break;
			}
		}

		// We publish the ETL
		String modelETL = gateway.getElement().asXML();

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		RepositoryItem itemETL = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.GTW_TYPE, -1, target, etlName, "", "", "", modelETL, true);
		return itemETL;
	}

	private RepositoryItem buildKPIMetricEtl(IRepositoryContext ctx, RepositoryDirectory target, String etlName, String tableInput, String tableOutput, List<Axis> axes, Metric metric, Date selectedDate) throws Exception {
		// Define date
		// TODO: option to use a column as date
		long time = selectedDate != null ? selectedDate.getTime() : new Date().getTime();

		DataBaseServer serverDB = buildKpiDatabaseServer(ctx.getVanillaContext());

		DocumentGateway gateway = new DocumentGateway();
		gateway.setName(etlName);
		gateway.addServer(serverDB);

		DataBaseInputStream inputDB = new DataBaseInputStream();
		inputDB.setName("DataBaseInputStream");
		inputDB.setDefinition("SELECT * FROM " + tableInput);
		inputDB.setPositionX(200);
		inputDB.setPositionY(400);
		inputDB.setServer(serverDB);
		gateway.addTransformation(inputDB);

		inputDB.refreshDescriptor();

		int inputX = 400;
		Transformation lastBox = inputDB;
		for (Axis axe : axes) {
			Level level = axe.getChildren().get(0);
			String axeName = cleanValue(level.getName());

			DataBaseInputStream inputAxis = new DataBaseInputStream();
			inputAxis.setName("Axis_" + level.getName() + "_input");
			inputAxis.setDefinition("SELECT * FROM " + level.getTableName());
			inputAxis.setPositionX(inputX);
			inputAxis.setPositionY(200);
			inputAxis.setServer(serverDB);
			gateway.addTransformation(inputAxis);

			inputAxis.refreshDescriptor();

			Lookup lookupAxis = new Lookup();
			lookupAxis.setName("Axis_" + level.getName() + "_lookup");
			lookupAxis.setPositionX(inputX);
			lookupAxis.setPositionY(400);
			gateway.addTransformation(lookupAxis);

			lastBox.addOutput(lookupAxis);
			lookupAxis.addInput(lastBox);

			inputAxis.addOutput(lookupAxis);
			lookupAxis.addInput(inputAxis);

			lookupAxis.setAsMaster(lastBox);

			int loadIndex = -1;
			int axeIndex = -1;
			StreamDescriptor descriptor = lastBox.getDescriptor(null);

			for (int i = 0; i < descriptor.getColumnCount(); i++) {
				StreamElement element = descriptor.getStreamElements().get(i);
				if (element.name.equals(axeName)) {
					loadIndex = i;
					break;
				}
			}
			descriptor = inputAxis.getDescriptor(null);
			for (int i = 0; i < descriptor.getColumnCount(); i++) {
				StreamElement element = descriptor.getStreamElements().get(i);
				if (element.name.equals(level.getColumnName())) {
					axeIndex = i;
					break;
				}
			}
			lookupAxis.createMapping(loadIndex, axeIndex);
			lookupAxis.setInited();
			lookupAxis.refreshDescriptor();

			lastBox = lookupAxis;
			inputX += 200;
		}

		String metricValue = "metric_value";
		String metricDate = "metric_date";

		Script scriptMetricValue = new Script();
		scriptMetricValue.setName(metricValue);
		scriptMetricValue.setType(bpm.gateway.core.server.userdefined.Variable.FLOAT);
		scriptMetricValue.setScriptFunction("parseFloat({$" + metric.getName() + "})");

		Script scriptMetricDate = new Script();
		scriptMetricDate.setName(metricDate);
		scriptMetricDate.setType(bpm.gateway.core.server.userdefined.Variable.DATE);
		scriptMetricDate.setScriptFunction("new Date(" + time + ")");

		Calculation calculation = new Calculation();
		calculation.setName("Calculation");
		calculation.setPositionX(inputX);
		calculation.setPositionY(400);
		calculation.addScript(scriptMetricValue);
		calculation.addScript(scriptMetricDate);
		gateway.addTransformation(calculation);

		lastBox.addOutput(calculation);
		calculation.addInput(lastBox);

		calculation.setInited();
		calculation.refreshDescriptor();

		inputX += 200;

		DataBaseOutputStream outputDB = new DataBaseOutputStream();
		outputDB.setName("DataBaseOutputStream");
		outputDB.setTableName(tableOutput);
		outputDB.setDefinition("select * from " + tableOutput);
		outputDB.setPositionX(inputX);
		outputDB.setPositionY(400);
		outputDB.setTruncate(true);
		outputDB.setServer(serverDB);
		gateway.addTransformation(outputDB);

		calculation.addOutput(outputDB);
		outputDB.addInput(calculation);

		outputDB.setInited();
		outputDB.refreshDescriptor();

		StreamDescriptor inputDescriptor = calculation.getDescriptor(null);
		StreamDescriptor outputDescriptor = outputDB.getDescriptor(null);

		for (int i = 0; i < inputDescriptor.getColumnCount(); i++) {
			String inputColumn = inputDescriptor.getColumnName(i);
			if (inputColumn.equals(metricValue)) {
				outputDB.createMapping(calculation, i, findColumnIndex(outputDescriptor, metricValue));
			}
			else if (inputColumn.equals(metricDate)) {
				outputDB.createMapping(calculation, i, findColumnIndex(outputDescriptor, metricDate));
			}
			else {
				for (Axis axe : axes) {
					String axeId = "id_axe_" + cleanValue(axe.getName());
					if (inputColumn.equals(axeId)) {
						outputDB.createMapping(calculation, i, findColumnIndex(outputDescriptor, axeId));
						break;
					}
				}
			}
		}

		// We publish the ETL
		String modelETL = gateway.getElement().asXML();

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		RepositoryItem itemETL = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.GTW_TYPE, -1, target, etlName, "", "", "", modelETL, true);
		return itemETL;
	}

	private int findColumnIndex(StreamDescriptor descriptor, String value) throws Exception {
		for (int i = 0; i < descriptor.getColumnCount(); i++) {
			String inputColumn = descriptor.getColumnName(i);
			if (inputColumn.equals(value)) {
				return i;
			}
		}
		throw new Exception("Axis are not correctly defined in output database.");
	}

	private DataBaseServer buildKpiDatabaseServer(IVanillaContext ctx) throws Exception {
		Datasource ds = getKpiJdbcDatasource(ctx);
		DatasourceJdbc datasource = (DatasourceJdbc) ds.getObject();

		DataBaseServer server = new DataBaseServer();
		server.setName("vanilla_kpi_data");

		DataBaseConnection dbCon = new DataBaseConnection();
		dbCon.setName("vanilla_kpi_data");
		dbCon.setLogin(datasource.getUser());
		dbCon.setPassword(datasource.getPassword());
		dbCon.setUseFullUrl(datasource.getFullUrl());
		if (datasource.getFullUrl()) {
			dbCon.setFullUrl(datasource.getUrl());
		}
		else {
			dbCon.setHost(datasource.getHost());
			dbCon.setPort(datasource.getPort());
			dbCon.setDataBaseName(datasource.getDatabaseName());
		}

		String driverName = findDriver(datasource.getDriver());
		dbCon.setDriverName(driverName);

		// dbCon.setServer(server);
		server.addConnection(dbCon);
		server.setCurrentConnection(dbCon);
		return server;
	}

	private D4CServer buildD4CServer(String url, String organisation, String login, String password) throws ServerException {
		D4CServer server = new D4CServer();
		server.setName(organisation);

		D4CConnection d4cCon = new D4CConnection();
		d4cCon.setName(organisation);
		d4cCon.setOrg(organisation);
		d4cCon.setLogin(login);
		d4cCon.setPassword(password);
		d4cCon.setUrl(url);

		server.addConnection(d4cCon);
		server.setCurrentConnection(d4cCon);
		return server;
	}

	private RepositoryItem buildKPILoadEtl(IRepositoryContext ctx, RepositoryDirectory target, String etlName, String ckanUrl, String organisation, String login, String password, CkanPackage pack, CkanResource resource, String tableOutput, char separator) throws Exception {
		DataBaseServer serverDB = buildKpiDatabaseServer(ctx.getVanillaContext());
		D4CServer serverD4C = buildD4CServer(ckanUrl, organisation, login, password);

		FileInputCSV inputCsv = new FileInputCSV();
		inputCsv.setName("D4C_CSV");
		inputCsv.setTemporaryFilename("");
		inputCsv.setSeparator(separator);
		inputCsv.setServer(serverD4C);
		inputCsv.setFromUrl(false);

		D4CInput d4cInput = new D4CInput();
		d4cInput.setName("D4CInput");
		d4cInput.setSelectedPackage(pack);
		d4cInput.setSelectedResource(resource);
		d4cInput.setPositionX(200);
		d4cInput.setPositionY(200);
		d4cInput.setServer(serverD4C);
		d4cInput.setFileTransfo(inputCsv);

		DataBaseOutputStream outputDB = new DataBaseOutputStream();
		outputDB.setName("DataBaseOutputStream");
		outputDB.setTableName(tableOutput);
		outputDB.setDefinition("select * from " + tableOutput);
		outputDB.setPositionX(600);
		outputDB.setPositionY(200);
		outputDB.setTruncate(true);
		outputDB.setServer(serverDB);

		try {
			d4cInput.addOutput(outputDB);
			outputDB.addInput(d4cInput);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DocumentGateway gateway = new DocumentGateway();
		gateway.setName(etlName);
		gateway.addTransformation(d4cInput);
		gateway.addTransformation(outputDB);
		gateway.addServer(serverDB);
		gateway.addServer(serverD4C);

		d4cInput.refreshDescriptor();

		outputDB.setDocumentGateway(gateway);
		outputDB.setInited();
		outputDB.refreshDescriptor();

		StreamDescriptor inputDescriptor = d4cInput.getDescriptor(null);

		for (int i = 0; i < outputDB.getDescriptor(d4cInput).getColumnCount(); i++) {
			StreamDescriptor descriptor = outputDB.getDescriptor(d4cInput);

			String outputColumn = descriptor.getColumnName(i);
			Integer index = null;
			for (int j = 0; j < inputDescriptor.getColumnCount(); j++) {
				String inputColumn = inputDescriptor.getColumnName(j);
				if (inputColumn.equals(outputColumn)) {
					index = j;
					break;
				}
			}

			if (index != null) {
				outputDB.createMapping(d4cInput, index, i);
			}
		}

		// We publish the ETL
		String modelETL = gateway.getElement().asXML();

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(ctx);
		RepositoryItem itemETL = repositoryApi.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.GTW_TYPE, -1, target, etlName, "", "", "", modelETL, true);
		return itemETL;
	}

	private String findDriver(String driver) throws Exception {
		Collection<DriverInfo> infos = ListDriver.getInstance(bpm.studio.jdbc.management.config.IConstants.getJdbcDriverXmlFile()).getDriversInfo();
		if (infos != null) {
			for (DriverInfo info : infos) {
				if (driver.equals(info.getClassName())) {
					return info.getName();
				}
			}
		}
		return "MySQL";
	}
	

	
	private IVanillaContext getRootVanillaContext() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		
		String vanUrl = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanUrl, login, password);
	}

	private IVanillaAPI getRootVanillaApi() {
		return new RemoteVanillaPlatform(getRootVanillaContext());
	}
	
	/**
	 * Part Data Validation
	 */
	@Override
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception {
		IVanillaContext ctx = getRootVanillaContext();
		IVanillaAPI vanillaApi = getRootVanillaApi();
		IMdmProvider mdmComponent = getMdmProvider(ctx);
		
		List<DocumentSchema> contractSchemas = mdmComponent.getDocumentSchemas(contractId);
		List<DocumentSchema> schemasToCheck = new ArrayList<DocumentSchema>();

		if (contractSchemas != null) {
			if (schemas == null || schemas.isEmpty()) {
				schemasToCheck.addAll(contractSchemas);
			}
			else {
				for (DocumentSchema schema : contractSchemas) {
					for (String schemaToCheck : schemas) {
						if (schema.getSchema().equals(schemaToCheck)) {
							schemasToCheck.add(schema);
						}
					}
				}
			}
		}
		
		if (schemasToCheck == null || schemasToCheck.isEmpty()) {
			ValidationDataResult result = new ValidationDataResult(contractId, -1, -1, DataValidationResultStatut.NO_VALIDATION, "");
			vanillaApi.getGlobalManager().manageItem(result, ManageAction.SAVE);
			return result;
		}
		
		List<ClassRule> classRules = getRootVanillaApi().getResourceManager().getClassRules("schema_validation");
		getLogger().info("Found " + (classRules != null ? classRules.size() : 0) + " rules. Checking which one are associated...");
		
		List<ClassRule> rulesToApply = new ArrayList<ClassRule>();
		for (DocumentSchema schema : schemasToCheck) {
			if (classRules != null) {
				for (ClassRule rule : classRules) {
					if (rule.getParentPath().contains(schema.getSchema())) {
						rulesToApply.add(rule);
					}
				}
			}
		}

		String schemaPath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_FILES) + "schemas/";

		ClassDefinition classDef = new ClassDefinition();
		classDef.setIdentifiant("schema_validation");
		
		for (DocumentSchema schema : schemasToCheck) {
			ClassDefinition classSchema = SchemaHelper.loadSchema(schemaPath + schema.getSchema() + ".json", schema.getSchema(), true);
			classDef.addClass(classSchema);
		}
		
		ReflectionHelper.buildClassDefinitionWithRules(null, classDef, rulesToApply, false);
		long start = System.currentTimeMillis();
		List<ValidationSchemaResult> schemasResult = checkData(resourceId, classDef);
		long end = System.currentTimeMillis();
		System.out.println("Elapsed Time in milli seconds: "+ (end - start));

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String d4cLogin = getD4cLogin(config, null);
		String d4cPassword = getD4cPassword(config, null);
		D4CHelper helper = new D4CHelper(d4cUrl, d4cObs, d4cLogin, d4cPassword);
		
		CkanPackage existingDataset = helper.findCkanPackage(datasetId);

		Gson gson = new Gson();
		String existingDataValidationJson = existingDataset.getExtras().get("data_validation");
		ValidationDataResult existingDataValidation = existingDataValidationJson != null && !existingDataValidationJson.isEmpty() ? gson.fromJson(existingDataValidationJson, ValidationDataResult.class) : null;
		if (existingDataValidation != null) {
			existingDataValidation.setStatut(DataValidationResultStatut.SUCCESS);
			existingDataValidation.setValidationDate(new Date());
			
			// Checking which schema need to be updated
			for (ValidationSchemaResult schemaResult : schemasResult) {
				Integer index = -1;
				boolean found = false;
				if (existingDataValidation.getSchemaValidationResults() != null) {
					for (int i = 0; i < existingDataValidation.getSchemaValidationResults().size(); i++) {
						ValidationSchemaResult existingSchema = existingDataValidation.getSchemaValidationResults().get(i);
						if (schemaResult.getSchema().equals(existingSchema.getSchema())) {
							found = true;
							index = i;
							break;
						}
					}
				}
				
				if (found) {
					existingDataValidation.getSchemaValidationResults().set(index, schemaResult);
				}
				else {
					existingDataValidation.addSchemaValidationResult(schemaResult);
				}
			}
		}
		else {
			existingDataValidation = new ValidationDataResult(contractId, resourceId, DataValidationResultStatut.SUCCESS, "");
			existingDataValidation.setSchemaValidationResults(schemasResult);
		}
		
		vanillaApi.getGlobalManager().manageItem(existingDataValidation, ManageAction.SAVE);
		existingDataValidation.resetDetails();
		
		// We update metadata in D4C
		boolean hasRgpdValue = false;

		//Checking if there is RGPD data
		List<MetaLink> links = new ArrayList<MetaLink>();
		if (existingDataValidation != null) {
			if (existingDataValidation.getSchemaValidationResults() != null) {
				for (ValidationSchemaResult schema : existingDataValidation.getSchemaValidationResults()) {
					if (schema.getSchema().contains("rgpd")) {
						Meta metaRgpd = new Meta();
						metaRgpd.setKey("data_rgpd");
						
						hasRgpdValue = schema.getColumnsWithError() != null && schema.getColumnsWithError().size() > 0;
						
						MetaLink linkRgpd = new MetaLink(metaRgpd);
						linkRgpd.setValue(new MetaValue("data_rgpd", hasRgpdValue ? "true" : "false"));
//						linkRgpd.setValue(new MetaValue("data_rgpd", hasRgpdValue ? "true" : "false"));
						
						links.add(linkRgpd);
					}
					else if (schema.getSchema().contains("interop")) {
						Meta metaInterop = new Meta();
						metaInterop.setKey("data_interop");
						
						MetaLink linkInterop = new MetaLink(metaInterop);
						linkInterop.setValue(new MetaValue("data_interop", schema.getColumnsWithError() != null && schema.getColumnsWithError().size() > 0 ? "true" : "false"));
						
						links.add(linkInterop);
					}
				}
			}
			
			// We had the validation result to the metadata
			String dataValidation = gson.toJson(existingDataValidation);
	
			Meta metaRgpd = new Meta();
			metaRgpd.setKey("data_validation");
			
			MetaLink linkRgpd = new MetaLink(metaRgpd);
			linkRgpd.setValue(new MetaValue("data_validation", dataValidation));
			
			links.add(linkRgpd);
		}
		
		// If the dataset has rgpd values, we set it to private
		if (hasRgpdValue) {
			existingDataset.setPrivate(true);
		}
		// We init the metadata to only update new meta
		existingDataset.setExtras(new HashMap<String, String>());
		
		if (existingDataset != null) {
			if (links != null) {
				for (MetaLink link : links) {
					existingDataset.putExtra(link.getMeta().getKey(), link.getValue() != null ? link.getValue().getValue() : null);
				}
			}

			helper.manageDataset(existingDataset, true);
		}
		
		return existingDataValidation;
	}
	
	private List<ValidationSchemaResult> checkData(String resourceId, ClassDefinition classDef) {
		
		HashMap<String, ValidationSchemaResult> schemasValidationResult = new HashMap<String, ValidationSchemaResult>();
		
		VanillaJdbcConnection connection = null;
		VanillaPreparedStatement stmt = null;
		try {
			// Getting schema_rpgd and schema interop if selected
			List<ClassDefinition> columnsMatchSchemas = new ArrayList<ClassDefinition>();
			if (classDef != null && classDef.getClasses() != null) {
				for (ClassDefinition childClass : classDef.getClasses()) {
					if (childClass.getIdentifiant().equals(INTEROP_SCHEMA) || childClass.getIdentifiant().equals(RGPD_SCHEMA)) {
						columnsMatchSchemas.add(childClass);
					}
				}
			}
	
			String query = "SELECT * FROM \"" + resourceId + "\"";
			
			Datasource datastoreSource = getDatastoreJdbcDatasource();
			
			connection = ConnectionManager.getInstance().getJdbcConnection((DatasourceJdbc) datastoreSource.getObject());
			stmt = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(1000);
			
			ResultSetMetaData metadata = stmt.getQueryMetadata(query);
			
			// We build the query by removing column such as _id and _full_text
			HashMap<String, Integer> columnNames = new HashMap<String, Integer>();
			
			StringBuffer buf = new StringBuffer("SELECT ");
			boolean first = true;
			int index = 1;
			
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				String columnName = metadata.getColumnName(i);
				if (!columnName.equals("_id") && !columnName.equals("_full_text")) {
					if (!first) {
						buf.append(", ");
					}
					first= false;
					
					buf.append(columnName);
					columnNames.put(columnName, index);
					index++;
					
					// If rgpd_schema or interop_schema is defined, we check if a column match to an rgpd or interop one
					for (ClassDefinition columnsMatchSchema : columnsMatchSchemas) {
						if (isColumnMatchAndSetRealColumn(columnsMatchSchema, columnName)) {
							ValidationSchemaResult validationResult = schemasValidationResult.get(columnsMatchSchema.getIdentifiant());
							if (validationResult == null) {
								validationResult = new ValidationSchemaResult(-1, columnsMatchSchema.getIdentifiant(), 0, 0);
								schemasValidationResult.put(columnsMatchSchema.getIdentifiant(), validationResult);
							}
							validationResult.addColumnWithError(columnName);
						}
					}
				}
			}
			buf.append(" FROM \"" + resourceId + "\"");
			
			try (ResultSet resultSet = stmt.executeQuery(buf.toString())) {
				
				while (resultSet.next()) {

					for (ClassDefinition schemaClassDef : classDef.getClasses()) {
						String schema = schemaClassDef.getIdentifiant();
						
						ValidationSchemaResult validationResult = schemasValidationResult.get(schema);
						if (validationResult == null) {
							validationResult = new ValidationSchemaResult(-1, schema, 0, 0);
							schemasValidationResult.put(schema, validationResult);
						}
						
						boolean isError = false;
						
						// We need to check each line with the validation schema
						if (schemaClassDef.getFields() != null) {
							for (ClassField field : schemaClassDef.getFields()) {
								//Matching all field
								if (field.getCleanFieldName().equals(ClassField.GLOBAL_FIELD)) {
									if (field.getRules() != null) {
										for (String columnName : columnNames.keySet()) {
											
											//TODO: Check type and manage
											Object objectValue = resultSet.getObject(columnNames.get(columnName));
											String value = String.valueOf(objectValue);
			
											for (ClassRule rule : field.getRules()) {
												if (rule.isEnabled()) {
													ValidationRuleResult result = checkData(rule, field, columnName, value);
													if (result != null) {
														validationResult.addRuleResult(result);
														validationResult.incrementColumnLine(columnName);
														isError = true;
													}
												}
											}
										}
									}
								}
								else {
									Integer indexColumn = columnNames.get(field.getCleanFieldName());
									if (indexColumn != null) {
										// If the field exist, we test the data
										
										//TODO: Check type and manage
										Object objectValue = resultSet.getObject(indexColumn);
										String value = String.valueOf(objectValue);
										
										if (field.getRules() != null) {
											for (ClassRule rule : field.getRules()) {
												if (rule.isEnabled()) {
													ValidationRuleResult result = checkData(rule, field, field.getCleanFieldName(), value);
													if (result != null) {
														validationResult.addRuleResult(result);
														validationResult.incrementColumnLine(field.getCleanFieldName());
														isError = true;
													}
												}
											}
										}
									}
								}
							}
						}
						
						validationResult.incrementLine(isError);
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if (stmt != null) {
				stmt.close();
			}
			
			if (connection != null) {
				ConnectionManager.getInstance().returnJdbcConnection(connection);
			}
		} catch (Exception e) {
			e.printStackTrace();

			try {
				if (stmt != null) {
					stmt.close();
				}
				
				if (connection != null) {
					ConnectionManager.getInstance().returnJdbcConnection(connection);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return new ArrayList<ValidationSchemaResult>(schemasValidationResult.values());
	}
	
	private boolean isColumnMatchAndSetRealColumn(ClassDefinition classDef, String columnName) {
		for (ClassField field : classDef.getFields()) {
			if (field.getPossibleNames() != null && field.getPossibleNames().contains(columnName)) {
				// We have a match, we set the field name of the schema as the name of the column for the rules check later
				field.setName(columnName);
				field.setCleanFieldName(columnName);
				return true;
			}
		}
		return false;
	}

	private ValidationRuleResult checkData(ClassRule rule, ClassField field, String fieldName, String value) {
		if (field.isRequired() && (value == null || value.isEmpty())) {
			// Checking if value not null and not empty
			return new ValidationRuleResult(fieldName, rule.getMainClassIdentifiant(), rule.getName(), value);
		}
		
		if (rule.getType() == TypeRule.VALUE_COMPARAISON) {
			OperatorClassic firstOperator = ((RuleValueComparison) rule.getRule()).getFirstOperator();
			String firstValue = ((RuleValueComparison) rule.getRule()).getFirstValue();
			
			try {
				boolean checkValue = checkValue(field, value, firstOperator, firstValue);
				if (!checkValue) {
					return new ValidationRuleResult(fieldName, rule.getMainClassIdentifiant(), rule.getName(), value);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ValidationRuleResult(fieldName, rule.getMainClassIdentifiant(), rule.getName(), value);
			}
			
			if (((RuleValueComparison) rule.getRule()).hasLastValue()) {
				try {
					boolean checkValue = checkValue(field, value, firstOperator, firstValue);
					if (!checkValue) {
						return new ValidationRuleResult(fieldName, rule.getMainClassIdentifiant(), rule.getName(), value);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return new ValidationRuleResult(fieldName, rule.getMainClassIdentifiant(), rule.getName(), value);
				}
			}
		}
		
		if (rule.getType() == TypeRule.PATTERN) {
			String regex = ((RulePatternComparison) rule.getRule()).getRegex();
			boolean errorIfMatch = ((RulePatternComparison) rule.getRule()).isErrorIfMatch();
			
			Pattern pattern = Pattern.compile(regex);
			if (errorIfMatch && pattern.matcher(value).find() || !errorIfMatch && !pattern.matcher(value).find()) {
				return new ValidationRuleResult(fieldName, rule.getMainClassIdentifiant(), rule.getName(), value);
			}
		}
		return null;
	}

	private static boolean checkValue(ClassField field, String myValue, OperatorClassic operator, String value) throws Exception {
		if (operator == OperatorClassic.IN) {
			return checkINValue(myValue, value);
		}

		boolean isNumeric = false;

		double myValueDouble = 0;
		double valueDouble = 0;

		if (field.getType() == TypeField.NUMERIC) {
			try {
				isNumeric = true;

				myValueDouble = Double.parseDouble(myValue);
				valueDouble = Double.parseDouble(value.toString());
			} catch (Exception e) {
				isNumeric = false;
			}
		}
		//TODO: Support compare date
//		else if (field.getType() == TypeField.DATE) {
//			try {
//				myValueString = myValue.substring(0, 4);
//			} catch (Exception e) {
//				throwException(rule, "La valeur doit être de type année.");
//			}
//		}

		return checkValue(operator, isNumeric, myValueDouble, valueDouble, myValue, value);
	}

	private static boolean checkINValue(String myValue, String value) throws Exception {
		String[] values = value.split(";");
		if (values != null) {
			for (String val : values) {
				if (myValue.equals(val)) {
					return true;
				}
			}
		}

		return false;
	}

	private static boolean checkValue(OperatorClassic operator, boolean isNumeric, double myValueDouble, double valueDouble, String myValueString, String valueString) throws Exception {
		switch (operator) {
		case INF:
			return isNumeric ? myValueDouble < valueDouble : myValueString.compareTo(valueString) < 0;
		case INF_OR_EQUAL:
			return isNumeric ? myValueDouble <= valueDouble : myValueString.compareTo(valueString) <= 0;
		case EQUAL:
			return isNumeric ? myValueDouble == valueDouble : myValueString.compareTo(valueString) == 0;
		case NOT_EQUAL:
			return isNumeric ? myValueDouble != valueDouble : checkNotEqualValue(myValueString, valueString);
		case SUP_OR_EQUAL:
			return isNumeric ? myValueDouble >= valueDouble : myValueString.compareTo(valueString) >= 0;
		case SUP:
			return isNumeric ? myValueDouble >= valueDouble : myValueString.compareTo(valueString) >= 0;
		case IN:
			return false;
		case CONTAINS:
			return myValueString.contains(valueString);
		case REGEX:
			return checkValue(myValueString, valueString);
		default:
			break;
		}
		return false;
	}

	private static boolean checkNotEqualValue(String myValue, String value) throws Exception {
		String[] values = value.split(";");
		if (values != null) {
			for (String val : values) {
				if (myValue.equals(val)) {
					return false;
				}
			}
		}

		return true;
	}

	private static boolean checkValue(String myValue, String regex) {
		return myValue.matches(regex);
	}
}
