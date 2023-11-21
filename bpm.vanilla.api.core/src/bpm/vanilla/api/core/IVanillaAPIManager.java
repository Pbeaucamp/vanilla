package bpm.vanilla.api.core;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.api.core.model.ItemRunInformations;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.resources.AbstractD4CIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractIntegrationInformations;
import bpm.vanilla.platform.core.beans.resources.ContractType;
import bpm.vanilla.platform.core.beans.resources.KPIGenerationInformations;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.ValidationDataResult;
import bpm.workflow.commons.beans.Schedule;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

public interface IVanillaAPIManager extends IAPIManager {

	public ContractIntegrationInformations generateIntegration(int repositoryId, int groupId, AbstractD4CIntegrationInformations integrationInfos, boolean modifyMetadata, boolean modifyIntegration) throws Exception;
	
	public ContractIntegrationInformations getIntegrationProcessByLimesurvey(String limesurveyId) throws Exception;
	
	public ContractIntegrationInformations getIntegrationProcessByContract(int contractId) throws Exception;
	
	public List<Workflow> getVanillaHubs() throws Exception;
	
	public String runVanillaHub(int workflowId, List<Parameter> parameters) throws Exception;
	
	public WorkflowInstance getVanillaHubProgress(int workflowId, String uuid) throws Exception;
	
	public ItemRunInformations getItemInformations(User user, int repositoryId, int groupId, int itemId) throws Exception;
	
	public String runETL(User user, int repositoryId, int groupId, int itemId, List<VanillaGroupParameter> parameters) throws Exception;
	
	public String runWorkflow(User user, int repositoryId, int groupId, int itemId, List<VanillaGroupParameter> parameters) throws Exception;
	
	public InputStream runReport(User user, int repositoryId, int groupId, int itemId, String outputName, String format, List<VanillaGroupParameter> parameters, List<String> mails) throws Exception;
	
	public ContractIntegrationInformations generateKpi(int repositoryId, int groupId, KPIGenerationInformations infos) throws Exception;
	
//	public ContractIntegrationInformations generateSimpleKpi(int repositoryId, int groupId, SimpleKPIGenerationInformations infos) throws Exception;
	
	public List<ContractIntegrationInformations> getIntegrationKPIByDatasetId(String datasetId) throws Exception;
	
	public List<ContractIntegrationInformations> getIntegrationByOrganisation(String organisation, ContractType type) throws Exception;
	
	public List<String> getValidationSchemas() throws Exception;
	
	public void deleteIntegration(int repositoryId, int groupId, ContractIntegrationInformations infos) throws Exception;
	
	public void updateSchedule(Schedule schedule) throws Exception;
	
	public ValidationDataResult validateData(String d4cUrl, String d4cObs, String datasetId, String resourceId, int contractId, List<String> schemas) throws Exception;
}
