package bpm.aklabox.workflow.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.OrbeonWorkflowInformation;
import bpm.aklabox.workflow.core.model.Owner;
import bpm.aklabox.workflow.core.model.Platform;
import bpm.aklabox.workflow.core.model.RADResultObject;
import bpm.aklabox.workflow.core.model.Version;
import bpm.aklabox.workflow.core.model.Workflow;
import bpm.aklabox.workflow.core.model.WorkflowLog;
import bpm.aklabox.workflow.core.model.Workflow.Type;
import bpm.aklabox.workflow.core.model.activities.Activity;
import bpm.aklabox.workflow.core.model.activities.AklaBoxFiles;
import bpm.aklabox.workflow.core.model.activities.AnalyzeFormActivity;
import bpm.aklabox.workflow.core.model.activities.IActivity;
import bpm.aklabox.workflow.core.model.activities.SourceTarget;
import bpm.aklabox.workflow.core.model.resources.AkLadExportObject;
import bpm.aklabox.workflow.core.model.resources.AklaBoxServer;
import bpm.aklabox.workflow.core.model.resources.FileServer;
import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.FormCellLink;
import bpm.aklabox.workflow.core.model.resources.FormCellResult;
import bpm.aklabox.workflow.core.model.resources.FormEngine;
import bpm.aklabox.workflow.core.model.resources.FormTextTemplate;
import bpm.aklabox.workflow.core.model.resources.IResource;
import bpm.aklabox.workflow.core.model.resources.MailServer;
import bpm.aklabox.workflow.core.model.resources.Resource;
import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.aklabox.workflow.core.model.resources.VanillaServer;
import bpm.aklabox.workflow.core.model.resources.Variable;
import bpm.aklabox.workflow.core.xstream.IXmlActionType;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Form;
import bpm.document.management.core.model.OrganigramElement;
import bpm.document.management.core.model.OrganigramElementSecurity;
import bpm.document.management.core.model.User;

public interface IAklaflowManager {
	public enum ActionType implements IXmlActionType {
		CREATE_WORKFLOW, UPDATE_WORKFLOW, DELETE_WORKFLOW, EXECUTE_WORKFLOW, DELETE_WORKFLOW_VERSION, GET_ALL_WORKFLOW, GET_WORKFLOW, 
		DEPLOY_WORKFLOW, GET_ALL_DEPLOYED_WORKFLOW, GET_ALL_WORKFLOW_AND_OWNER, SAVE_WORKFLOW_OWNER, UPDATE_WORKFLOW_OWNER, 
		DELETE_WORKFLOW_OWNER, GET_WORKFLOW_BY_OWNER, GET_OWNER_BY_WORKFLOW, SAVE_WORKFLOW_ACTIVITY, DELETE_ACTIVITY, 
		GET_ALL_WORKFLOW_ACTIVITIES, CLEAR_ALL_ACTIVITIES, GET_WORKFLOW_SOURCE_TARGET, UPDATE_SOURCE_TARGET, SAVE_RESOURCE, 
		UPDATE_RESOURCE, DELETE_RESOURCE, GET_ALL_RESOURCES_BY_OWNER, CONNECT_TO_SERVER, GET_VARIABLE_VALUE, GET_ALL_VARIABLES, 
		GET_ALL_MAIL_SERVERS, GET_ALL_FILE_SERVERS, GET_ALL_FORMS, GET_ALL_AKLABOX_SERVERS, SAVE_WORKFLOW_VERSION, GET_WORKFLOW_VERSIONS, 
		GET_LATEST_WORKFLOW_VERSION, GET_ALL_INSTANCE, SAVE_INSTANCE, DELETE_INSTANCE, UPDATE_INSTANCE, GET_ALL_INSTANCE_BY_USER, 
		GET_ACTIVITY, TEST_ALL_SERVER, GET_FILE_SERVER, GET_AKLABOX_SERVER, GET_MAIL_SERVER, GET_ALL_VERSION, PING_SERVER, 
		GET_AKLABOX_FILES, GET_AKLABOX_FILES_BY_ID, SAVE_AKLABOX_FILES, OCR_DOCUMENT, GET_VARIABLE, DELETE_AKLABOX_FILE,
		SAVE_FORM_CELL, DELETE_FORM_CELL, GET_ALL_FORM_CELLS, SAVE_STANDARD_FORM, GET_FORM_ENGINE, ANALYZE_FORM, UPDATE_FORM_CELL, 
		GET_FORM_AND_CELLS, SAVE_FORM_CELL_RESULT, UPDATE_FORM_CELL_RESULT, GET_ALL_FORM_CELL_RESULT, GET_ALL_DOC_FORM_CELL_RESULT, 
		GET_ALL_STANDARD_FORMS, GET_STANDARD_FORM, GET_FORM_CELL, RENAME_DOC_FORM_CELL, GET_ALL_VANILLA_SERVER,
		SAVE_PLATFORM, UPDATE_PLATFORM, GET_PLATFORM, GET_VANILLA_SERVER, GET_ALL_WORKFLOWS_BY_TYPE, GET_ALL_WORKFLOW_BY_MASTER,
		GET_ACTIVITIES_BY_WORKFLOW, GET_LAST_INSTANCE, SAVE_FORM_CELL_LINK, DELETE_FORM_CELL_LINK, GET_ALL_FORM_CELL_LINKS_BY_OCR_FORM,
		GET_ALL_FORM_CELL_LINKS_BY_AKLA_FORM, GET_ALL_FORM_CELL_LINKS_BY_OCR_FORM_CELL, TAKE_OVER_WORKFLOW, GET_INSTANCE_BY_ID,
		ANALYZE_TEMP_FORM, ANALYZE_FULL_DOCUMENT, ANALYZE_FORM_SIMPLE, GET_ALL_FORM_TEXT, GET_FORM_TEXT_BY_FORM, RAD_DOCUMENT, 
		RAD_MANY_DOCUMENTS_WITH_FORMS, SAVE_FORM_TEXT_TEMPLATE, SAVE_FORM_TEXT_TEMPLATE2, LINK_FORMS, GET_IMAGE_SIZE, GET_BLANK_PAGES, 
		DELETE_STANDARD_FORM, RAD_MANY_DOCUMENTS_WITH_KEYWORDS,	GET_ALL_LANGS, IMPORT_AKLAD_WORKSPACE, IMPORT_AKLAD_WORKSPACE2, 
		EXPORT_AKLAD_WORKSPACE, UPLOAD_FILE_FOR_AKLAD, LIST_WORKSPACE_FILES, GET_ORBEON_INFO, GET_ORBEON_NEXT_ACTIVITY, 
		GET_ALL_INSTANCES, UPDATE_ORBEON_WORKFLOW, SEND_ORBEON_NOTIF, GET_DYNAMIC_SUPERIOR, GET_INSTANCEIDS_MAILING, GET_ORBEON_BY_INSTANCE,
		GET_ALL_VARIABLES_BY_TYPE, GET_OCR_FORM_BY_LINK
	}

	public Workflow createWorkflow(Workflow workflow) throws Exception;

	public void updateWorkflow(Workflow workflow) throws Exception;

	public void deleteWorkflow(Workflow workflow) throws Exception;

	public List<Workflow> getWorkflowByOwner(User user) throws Exception;

	public Workflow getWorkflow(int workflowId) throws Exception;

	public Owner saveWorkflowOwner(Owner owner) throws Exception;

	public List<Owner> getOwnerByWorkflow(int workflowId) throws Exception;

	public void updateWorkflowOwner(Owner owner) throws Exception;

	public void deleteWorkflowOwner(Owner owner) throws Exception;

	public void saveWorkflowActivity(List<Activity> activities, List<SourceTarget> targets, Workflow workflow) throws Exception;

	public void deleteActivity(Activity activity) throws Exception;

	public List<Activity> getAllWorkflowActivities(Workflow workflow) throws Exception;

	public void clearAllActivities(Workflow workflow) throws Exception;

	public List<SourceTarget> getWorkflowSourceTarget(Workflow workflow) throws Exception;

	public void updateSourceTarget(SourceTarget sourceTarget) throws Exception;

	public IResource saveResource(Resource resource) throws Exception;

	public void updateResource(IResource resource) throws Exception;

	public void deleteResource(IResource resource) throws Exception;

	public List<IResource> getAllResourcesByOwner(User user) throws Exception;

	public void connectToServer(IResource resource) throws Exception;

	public Variable getVariableValue(String variableName) throws Exception;

	public List<Variable> getAllVariables(User user) throws Exception;

	public List<MailServer> getAllMailServers(User user) throws Exception;

	public List<FileServer> getAllFileServers(User user) throws Exception;

	public Workflow saveWorkflowVersion(Workflow workflow, String comment) throws Exception;

	public List<Version> getWorkflowVersions(Workflow workflow) throws Exception;

	public Version getLatestWorkflowVersion(Workflow workflow) throws Exception;

	public List<FormEngine> getAllForms(User user, String type) throws Exception;

	public List<AklaBoxServer> getAllAklaBoxServers(User user) throws Exception;

	public List<VanillaServer> getAllVanillaServer(User user) throws Exception;
	
	public List<WorkflowLog> executeWorkflow(Instance instance, Workflow workflow, User user, String path) throws Exception;

	public List<Workflow> getAllWorkflow() throws Exception;

	public List<Instance> getAllInstance(String userEmail) throws Exception;

	public List<WorkflowLog> saveInstance(Instance instance, Workflow workflow, User user, String path) throws Exception;

	public void deleteInstance(Instance instance) throws Exception;

	public List<WorkflowLog> updateInstance(Instance instance, Workflow workflow, User user, String path) throws Exception;

	public void deployWorkflow(Workflow workflow) throws Exception;

	public List<Instance> getAllInstanceByUser(int userId) throws Exception;

	public HashMap<Workflow, List<Owner>> getAllWorkflowAndOwner() throws Exception;

	public Activity getActivity(String activityId, Workflow workflow) throws Exception;

	public List<WorkflowLog> testAllServer(HashMap<Activity, IResource> listActivity, Workflow workflow) throws Exception;

	public MailServer getMailServer(int id) throws Exception;

	public FileServer getFileServer(int id) throws Exception;

	public AklaBoxServer getAklaBoxServer(int id) throws Exception;

	public List<Version> getAllVersion() throws Exception;

	public List<WorkflowLog> pingServer(Activity activity, String address) throws Exception;

	public void deleteWorkflowVersion(Workflow workflow) throws Exception;

	public List<AklaBoxFiles> getAklaBoxFiles(Activity activity, int versionNumber) throws Exception;

	public List<AklaBoxFiles> getAklaBoxFilesById(int workflowId, int versionNumber) throws Exception;

	public void saveAklaBoxFiles(List<AklaBoxFiles> listAklaBoxFiles, Workflow workflow) throws Exception;

	public String ocrDocument(String source, String lang) throws Exception;

	public Variable getVariable(int id) throws Exception;

	public void deleteAklaBoxFile(AklaBoxFiles file) throws Exception;
	
	public int saveFormCell(FormCell cell) throws Exception;
	
	public void deleteFormCell(FormCell cell) throws Exception;
	
	public List<FormCell> getAllFormCells(StandardForm form) throws Exception;
	
	public StandardForm saveStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList) throws Exception;
	
	public FormEngine getFormEngine(int formId) throws Exception;

	public HashMap<StandardForm, List<FormCell>> analyzeForm(AnalyzeFormActivity analyzeForm, List<Documents> docs, User user) throws Exception;
	
	public void updateFormCell(FormCell cell) throws Exception;
	
	public HashMap<StandardForm, List<FormCell>> getFormAndCells(int formId) throws Exception;
	
	public void saveFormCellResult(FormCellResult cellResult) throws Exception;
	
	public void updateFormCellResult(FormCellResult cellResult) throws Exception;
	
	public FormCellResult getFormCellResult(Documents doc, FormCell cell) throws Exception;
	
	public List<FormCellResult> getAllDocFormCellResult() throws Exception;
	
	public List<StandardForm> getAllStandardForms(User user) throws Exception;
	
	public StandardForm getStandardForm(int id) throws Exception;
	
	public FormCell getFormCell(int cellId) throws Exception;
	
	public void renameDocCellResult(int cellId, Documents doc, User user) throws Exception;
	
	public void savePlatform(Platform platform) throws Exception;
	
	public void updatePlatform(Platform platform) throws Exception;
	
	public Platform getPlatform() throws Exception;
	
	public VanillaServer getVanillaServer(int serverId)  throws Exception;

	public List<Workflow> getAllWorkflowsByType(Type type) throws Exception;

	public List<Workflow> getAllWorkflowbyMaster(int idParent) throws Exception;

	public List<IActivity> getActivitiesbyWorkflow(Workflow workflow, User user, String path) throws Exception;

	public Instance getLastInstance(Workflow workflow) throws Exception;

	public void saveFormCellLink(FormCellLink cellLink) throws Exception;

	public void deleteFormCellLink(FormCellLink cellLink) throws Exception;

	public List<FormCellLink> getAllFormCellLinksbyOCRForm(StandardForm form) throws Exception;

	public List<FormCellLink> getAllFormCellLinksbyAklaForm(Form form) throws Exception;

	public List<FormCellLink> getAllFormCellLinksbyFormCell(FormCell cell) throws Exception;

	public List<WorkflowLog> takeOverWorkflow(Instance instance, Workflow workflow, User user, String path) throws Exception;

	public Instance getInstancebyId(int instanceId) throws Exception;

	public HashMap<FormCell, FormCellResult> analyzeTempForm(StandardForm form, Documents doc) throws Exception;
	
	public FormCellResult analyzeFullDocument(Documents doc, String lang) throws Exception;

	public void analyzeForm(Documents doc) throws Exception;

	public List<FormTextTemplate> getAllFormText() throws Exception;

	public List<FormTextTemplate> getFormTextbyForm(int formId) throws Exception;

	//public RADResultObject radDocument(Documents doc, int learningFolderId, User user) throws Exception;

	public List<RADResultObject> radDocumentsWithForms(List<Documents> docs, int learningFolderId, User user, List<String> langs, String DefaultLang, boolean forceDefault) throws Exception;

	public void saveFormTextTemplate(StandardForm standardForm) throws Exception;

	public void saveFormTextTemplate(StandardForm standardForm, FormCellResult res) throws Exception;

	public void linkForms(int aklaboxFormId, int ocrFormId) throws Exception;

	public int[] getImageSize(String path) throws Exception;

	public List<Documents> getBlankPages(List<Documents> docs) throws Exception;

	public void deleteStandardForm(StandardForm form) throws Exception;

	public List<RADResultObject> radDocumentsWithKeyWords(List<Documents> docs, User user, List<String> langs, String DefaultLang, boolean forceDefault) throws Exception;

	public List<String> getAvailableLangs() throws Exception;

	public InputStream exportAkladWorkSpace(List<AkLadExportObject> docs) throws Exception;

	public List<AkLadExportObject> importAkladWorkSpace(InputStream importStream, User user) throws Exception;
	
	public List<AkLadExportObject> importAkladWorkSpace(String path, User user) throws Exception;

	public void uploadFileForAklad(String zipName, String docName, InputStream importStream, User user, boolean doOCR, String langOCR) throws Exception;

	public List<String> listWorkspaceFiles() throws Exception;

	public OrbeonWorkflowInformation getOrbeonInformation(Documents doc) throws Exception;

	public List<Activity> getOrbeonNextActivity(Workflow workflow, Instance instance) throws Exception;
	
	public void updateOrbeonWorkflow(List<WorkflowLog> logs, User user, Workflow workflow, Instance instance, boolean validate, List<String> sectionNames) throws Exception;

	public List<Instance> getAllInstances() throws Exception;

	public void sendOrbeonNotification(int userId, Instance instance) throws Exception;

	public List<OrganigramElementSecurity> getDynamicSuperior(Activity nextActivity, Instance instance, OrganigramElement dynamicElem, User baseUser) throws Exception;
	
	public List<String> getInstanceIdsForMailing() throws Exception;
	
	public OrbeonWorkflowInformation getOrbeonInformation(String instanceId) throws Exception;

	public List<Variable> getAllVariablesbyType(String type) throws Exception;

	public StandardForm getStandardFormbyLink(int id) throws Exception;
	 
}