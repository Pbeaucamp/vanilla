package bpm.vanilla.platform.core;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
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
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IResourceManager {

	public static enum ActionType implements IXmlActionType{
		MANAGE_RESOURCE(Level.INFO), REMOVE_RESOURCE(Level.INFO), GET_RESOURCES(Level.INFO), GET_DRIVERS(Level.INFO), 
		TEST_CONNECTION(Level.INFO), VALID_SCRIPT(Level.INFO), MANAGE_CLASSRULE(Level.INFO), REMOVE_CLASSRULE(Level.INFO), GET_CLASSRULES(Level.INFO), 
		GET_CKAN_DATASETS(Level.DEBUG), DUPLICATE_RESOURCE(Level.DEBUG),
		GENERATE_INTEGRATION(Level.INFO), GENERATE_KPI(Level.INFO), GET_VALIDATION_SCHEMAS(Level.INFO), REMOVE_INTEGRATION(Level.INFO), VALIDATE_DATA(Level.INFO);

		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public Resource manageResource(Resource resource, boolean edit) throws Exception;

	public void removeResource(Resource resource) throws Exception;

	public List<? extends Resource> getResources(TypeResource type) throws Exception;

	public List<String> getJdbcDrivers() throws Exception;

	public String testConnection(DatabaseServer databaseServer) throws Exception;
	
	public CheckResult validScript(Variable variable) throws Exception;

	public ClassRule addOrUpdateClassRule(ClassRule classRule) throws Exception;

	public void removeClassRule(ClassRule classRule) throws Exception;

	public List<ClassRule> getClassRules(String identifiant) throws Exception;
	
	public List<CkanPackage> getCkanDatasets(String ckanUrl) throws Exception;

	public Resource duplicateResource(int resourceId, String name) throws Exception;

	public ContractIntegrationInformations generateIntegration(IRepositoryContext ctx, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception;
	
	public ContractIntegrationInformations generateKPI(IRepositoryContext ctx, KPIGenerationInformations infos) throws Exception;
	
//	public ContractIntegrationInformations generateSimpleKPI(IRepositoryContext ctx, SimpleKPIGenerationInformations infos) throws Exception;
	
	public void deleteIntegration(IRepositoryContext ctx, ContractIntegrationInformations integrationInfos) throws Exception;
	
	public List<String> getValidationSchemas() throws Exception;
	
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception;
}
