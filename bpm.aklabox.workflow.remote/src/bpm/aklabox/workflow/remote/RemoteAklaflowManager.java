package bpm.aklabox.workflow.remote;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import bpm.aklabox.workflow.core.IAklaflowManager;
import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.OrbeonWorkflowInformation;
import bpm.aklabox.workflow.core.model.Owner;
import bpm.aklabox.workflow.core.model.Platform;
import bpm.aklabox.workflow.core.model.RADResultObject;
import bpm.aklabox.workflow.core.model.Version;
import bpm.aklabox.workflow.core.model.Workflow;
import bpm.aklabox.workflow.core.model.Workflow.Type;
import bpm.aklabox.workflow.core.model.WorkflowLog;
import bpm.aklabox.workflow.core.model.activities.Activity;
import bpm.aklabox.workflow.core.model.activities.AklaBoxFiles;
import bpm.aklabox.workflow.core.model.activities.AklaflowContext;
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
import bpm.aklabox.workflow.core.xstream.XmlAction;
import bpm.aklabox.workflow.core.xstream.XmlArgumentsHolder;
import bpm.aklabox.workflow.remote.internal.HttpCommunicator;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Form;
import bpm.document.management.core.model.OrganigramElement;
import bpm.document.management.core.model.OrganigramElementSecurity;
import bpm.document.management.core.model.User;
import bpm.vanilla.platform.xstream.DateConverter;

import com.thoughtworks.xstream.XStream;

public class RemoteAklaflowManager implements IAklaflowManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static {
		xstream = new XStream();
		xstream.registerConverter(new DateConverter());
	}

	public RemoteAklaflowManager(AklaflowContext ctx) {
		httpCommunicator.init(ctx.getAklaflowUrl(), ctx.getMail(), ctx.getPassword(), null);
	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public Workflow createWorkflow(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.CREATE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) xstream.fromXML(xml);
	}

	@Override
	public Owner saveWorkflowOwner(Owner owner) throws Exception {
		XmlAction op = new XmlAction(createArguments(owner), IAklaflowManager.ActionType.SAVE_WORKFLOW_OWNER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Owner) xstream.fromXML(xml);
	}

	@Override
	public List<Workflow> getWorkflowByOwner(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_WORKFLOW_BY_OWNER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Workflow>) xstream.fromXML(xml);
	}

	@Override
	public void updateWorkflow(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.UPDATE_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteWorkflow(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.DELETE_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateWorkflowOwner(Owner owner) throws Exception {
		XmlAction op = new XmlAction(createArguments(owner), IAklaflowManager.ActionType.UPDATE_WORKFLOW_OWNER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteWorkflowOwner(Owner owner) throws Exception {
		XmlAction op = new XmlAction(createArguments(owner), IAklaflowManager.ActionType.DELETE_WORKFLOW_OWNER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveWorkflowActivity(List<Activity> activities, List<SourceTarget> targets, Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(activities, targets, workflow), IAklaflowManager.ActionType.SAVE_WORKFLOW_ACTIVITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Activity> getAllWorkflowActivities(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.GET_ALL_WORKFLOW_ACTIVITIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Activity>) xstream.fromXML(xml);
	}

	@Override
	public List<Owner> getOwnerByWorkflow(int workflowId) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId), IAklaflowManager.ActionType.GET_OWNER_BY_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Owner>) xstream.fromXML(xml);
	}

	@Override
	public void deleteActivity(Activity activity) throws Exception {
		XmlAction op = new XmlAction(createArguments(activity), IAklaflowManager.ActionType.DELETE_ACTIVITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void clearAllActivities(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.CLEAR_ALL_ACTIVITIES);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<SourceTarget> getWorkflowSourceTarget(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.GET_WORKFLOW_SOURCE_TARGET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<SourceTarget>) xstream.fromXML(xml);
	}

	@Override
	public void updateSourceTarget(SourceTarget sourceTarget) throws Exception {
		XmlAction op = new XmlAction(createArguments(sourceTarget), IAklaflowManager.ActionType.UPDATE_SOURCE_TARGET);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public IResource saveResource(Resource resource) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource), IAklaflowManager.ActionType.SAVE_RESOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IResource) xstream.fromXML(xml);
	}

	@Override
	public void updateResource(IResource resource) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource), IAklaflowManager.ActionType.UPDATE_RESOURCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteResource(IResource resource) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource), IAklaflowManager.ActionType.DELETE_RESOURCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<IResource> getAllResourcesByOwner(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_ALL_RESOURCES_BY_OWNER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IResource>) xstream.fromXML(xml);
	}

	@Override
	public void connectToServer(IResource resource) throws Exception {
		XmlAction op = new XmlAction(createArguments(resource), IAklaflowManager.ActionType.CONNECT_TO_SERVER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Variable getVariableValue(String variableName) throws Exception {
		XmlAction op = new XmlAction(createArguments(variableName), IAklaflowManager.ActionType.GET_VARIABLE_VALUE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Variable) xstream.fromXML(xml);
	}

	@Override
	public List<MailServer> getAllMailServers(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_ALL_MAIL_SERVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MailServer>) xstream.fromXML(xml);
	}

	@Override
	public List<FileServer> getAllFileServers(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_ALL_FILE_SERVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FileServer>) xstream.fromXML(xml);
	}

	@Override
	public List<Variable> getAllVariables(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_ALL_VARIABLES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Variable>) xstream.fromXML(xml);
	}

	@Override
	public Workflow saveWorkflowVersion(Workflow workflow, String comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, comment), IAklaflowManager.ActionType.SAVE_WORKFLOW_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) xstream.fromXML(xml);
	}

	@Override
	public List<Version> getWorkflowVersions(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.GET_WORKFLOW_VERSIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Version>) xstream.fromXML(xml);
	}

	@Override
	public Version getLatestWorkflowVersion(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.GET_LATEST_WORKFLOW_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Version) xstream.fromXML(xml);
	}
	
	@Override
	public List<FormEngine> getAllForms(User user, String type) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, type), IAklaflowManager.ActionType.GET_ALL_FORMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormEngine>) xstream.fromXML(xml);
	}

	@Override
	public List<AklaBoxServer> getAllAklaBoxServers(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_ALL_AKLABOX_SERVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AklaBoxServer>) xstream.fromXML(xml);
	}

	@Override
	public List<WorkflowLog> executeWorkflow(Instance instance, Workflow workflow, User user, String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance, workflow, user, path), IAklaflowManager.ActionType.EXECUTE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowLog>) xstream.fromXML(xml);
	}

	@Override
	public List<Workflow> getAllWorkflow() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_ALL_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Workflow>) xstream.fromXML(xml);
	}

	@Override
	public List<Instance> getAllInstance(String userEmail) throws Exception {
		XmlAction op = new XmlAction(createArguments(userEmail), IAklaflowManager.ActionType.GET_ALL_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Instance>) xstream.fromXML(xml);
	}

	@Override
	public List<WorkflowLog> saveInstance(Instance instance, Workflow workflow, User user, String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance, workflow, user, path), IAklaflowManager.ActionType.SAVE_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowLog>) xstream.fromXML(xml);
	}

	@Override
	public void deleteInstance(Instance instance) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IAklaflowManager.ActionType.DELETE_INSTANCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<WorkflowLog> updateInstance(Instance instance, Workflow workflow, User user, String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance, workflow, user, path), IAklaflowManager.ActionType.UPDATE_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowLog>) xstream.fromXML(xml);
	}

	@Override
	public Workflow getWorkflow(int workflowId) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId), IAklaflowManager.ActionType.GET_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Workflow) xstream.fromXML(xml);
	}

	@Override
	public void deployWorkflow(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.DEPLOY_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Instance> getAllInstanceByUser(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IAklaflowManager.ActionType.GET_ALL_INSTANCE_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Instance>) xstream.fromXML(xml);
	}

	@Override
	public HashMap<Workflow, List<Owner>> getAllWorkflowAndOwner() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_ALL_WORKFLOW_AND_OWNER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (HashMap<Workflow, List<Owner>>) xstream.fromXML(xml);
	}

	@Override
	public Activity getActivity(String activityId, Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(activityId, workflow), IAklaflowManager.ActionType.GET_ACTIVITY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Activity) xstream.fromXML(xml);
	}

	@Override
	public List<WorkflowLog> testAllServer(HashMap<Activity, IResource> listActivity, Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(listActivity, workflow), IAklaflowManager.ActionType.TEST_ALL_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowLog>) xstream.fromXML(xml);
	}

	@Override
	public MailServer getMailServer(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IAklaflowManager.ActionType.GET_MAIL_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (MailServer) xstream.fromXML(xml);
	}

	@Override
	public FileServer getFileServer(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IAklaflowManager.ActionType.GET_FILE_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FileServer) xstream.fromXML(xml);
	}

	@Override
	public AklaBoxServer getAklaBoxServer(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IAklaflowManager.ActionType.GET_AKLABOX_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AklaBoxServer) xstream.fromXML(xml);
	}

	@Override
	public List<Version> getAllVersion() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_ALL_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Version>) xstream.fromXML(xml);
	}

	@Override
	public List<WorkflowLog> pingServer(Activity activity, String address) throws Exception {
		XmlAction op = new XmlAction(createArguments(activity, address), IAklaflowManager.ActionType.PING_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowLog>) xstream.fromXML(xml);
	}

	@Override
	public void deleteWorkflowVersion(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.DELETE_WORKFLOW_VERSION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<AklaBoxFiles> getAklaBoxFiles(Activity activity, int versionNumber) throws Exception {
		XmlAction op = new XmlAction(createArguments(activity, versionNumber), IAklaflowManager.ActionType.GET_AKLABOX_FILES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AklaBoxFiles>) xstream.fromXML(xml);
	}

	@Override
	public void saveAklaBoxFiles(List<AklaBoxFiles> listAklaBoxFiles, Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(listAklaBoxFiles, workflow), IAklaflowManager.ActionType.SAVE_AKLABOX_FILES);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public String ocrDocument(String source, String lang) throws Exception {
		XmlAction op = new XmlAction(createArguments(source, lang), IAklaflowManager.ActionType.OCR_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public Variable getVariable(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IAklaflowManager.ActionType.GET_VARIABLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Variable) xstream.fromXML(xml);
	}

	@Override
	public List<AklaBoxFiles> getAklaBoxFilesById(int workflowId, int versionNumber) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId, versionNumber), IAklaflowManager.ActionType.GET_AKLABOX_FILES_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AklaBoxFiles>) xstream.fromXML(xml);
	}

	@Override
	public void deleteAklaBoxFile(AklaBoxFiles file) throws Exception {
		XmlAction op = new XmlAction(createArguments(file), IAklaflowManager.ActionType.DELETE_AKLABOX_FILE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public int saveFormCell(FormCell cell) throws Exception {
		XmlAction op = new XmlAction(createArguments(cell), IAklaflowManager.ActionType.SAVE_FORM_CELL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (int) xstream.fromXML(xml);
	}

	@Override
	public void deleteFormCell(FormCell cell) throws Exception {
		XmlAction op = new XmlAction(createArguments(cell), IAklaflowManager.ActionType.DELETE_FORM_CELL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormCell> getAllFormCells(StandardForm form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IAklaflowManager.ActionType.GET_ALL_FORM_CELLS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormCell>) xstream.fromXML(xml);
	}

	@Override
	public StandardForm saveStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList) throws Exception {
		XmlAction op = new XmlAction(createArguments(form, cells, aklaFormList, aklaFormFieldList), IAklaflowManager.ActionType.SAVE_STANDARD_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (StandardForm) xstream.fromXML(xml);
	}

	@Override
	public FormEngine getFormEngine(int formId) throws Exception {
		XmlAction op = new XmlAction(createArguments(formId), IAklaflowManager.ActionType.GET_FORM_ENGINE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormEngine) xstream.fromXML(xml);
	}

	@Override
	public HashMap<StandardForm, List<FormCell>> analyzeForm(AnalyzeFormActivity analyzeForm, List<Documents> docs, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(analyzeForm, docs, user), IAklaflowManager.ActionType.ANALYZE_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (HashMap<StandardForm, List<FormCell>>) xstream.fromXML(xml);
	}

	@Override
	public void updateFormCell(FormCell cell) throws Exception {
		XmlAction op = new XmlAction(createArguments(cell), IAklaflowManager.ActionType.UPDATE_FORM_CELL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public HashMap<StandardForm, List<FormCell>> getFormAndCells(int formId)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(formId), IAklaflowManager.ActionType.GET_FORM_AND_CELLS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (HashMap<StandardForm, List<FormCell>>) xstream.fromXML(xml);
	}

	@Override
	public void saveFormCellResult(FormCellResult cellResult) throws Exception {
		XmlAction op = new XmlAction(createArguments(cellResult), IAklaflowManager.ActionType.SAVE_FORM_CELL_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateFormCellResult(FormCellResult cellResult)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(cellResult), IAklaflowManager.ActionType.UPDATE_FORM_CELL_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public FormCellResult getFormCellResult(Documents doc, FormCell cell)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, cell), IAklaflowManager.ActionType.GET_ALL_FORM_CELL_RESULT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormCellResult) xstream.fromXML(xml);
	}

	@Override
	public List<FormCellResult> getAllDocFormCellResult() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_ALL_DOC_FORM_CELL_RESULT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormCellResult>) xstream.fromXML(xml);
	}

	@Override
	public List<StandardForm> getAllStandardForms(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_ALL_STANDARD_FORMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<StandardForm>) xstream.fromXML(xml);
	}

	@Override
	public StandardForm getStandardForm(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IAklaflowManager.ActionType.GET_STANDARD_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (StandardForm) xstream.fromXML(xml);
	}

	@Override
	public FormCell getFormCell(int cellId) throws Exception {
		XmlAction op = new XmlAction(createArguments(cellId), IAklaflowManager.ActionType.GET_FORM_CELL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormCell) xstream.fromXML(xml);
	}

	@Override
	public void renameDocCellResult(int cellId, Documents doc, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(cellId, doc, user), IAklaflowManager.ActionType.RENAME_DOC_FORM_CELL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<VanillaServer> getAllVanillaServer(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IAklaflowManager.ActionType.GET_ALL_VANILLA_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<VanillaServer>) xstream.fromXML(xml);
	}

	@Override
	public void savePlatform(Platform platform) throws Exception {
		XmlAction op = new XmlAction(createArguments(platform), IAklaflowManager.ActionType.SAVE_PLATFORM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updatePlatform(Platform platform) throws Exception {
		XmlAction op = new XmlAction(createArguments(platform), IAklaflowManager.ActionType.UPDATE_PLATFORM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Platform getPlatform() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_PLATFORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Platform) xstream.fromXML(xml);
	}

	@Override
	public VanillaServer getVanillaServer(int serverId) throws Exception {
		XmlAction op = new XmlAction(createArguments(serverId), IAklaflowManager.ActionType.GET_VANILLA_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (VanillaServer) xstream.fromXML(xml);
	}

	@Override
	public List<Workflow> getAllWorkflowsByType(Type type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), IAklaflowManager.ActionType.GET_ALL_WORKFLOWS_BY_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Workflow>) xstream.fromXML(xml);
	}

	@Override
	public List<Workflow> getAllWorkflowbyMaster(int idParent) throws Exception {
		XmlAction op = new XmlAction(createArguments(idParent), IAklaflowManager.ActionType.GET_ALL_WORKFLOW_BY_MASTER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Workflow>) xstream.fromXML(xml);
	}

	@Override
	public List<IActivity> getActivitiesbyWorkflow(Workflow workflow, User user, String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, user, path), IAklaflowManager.ActionType.GET_ACTIVITIES_BY_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IActivity>) xstream.fromXML(xml);
	}

	@Override
	public Instance getLastInstance(Workflow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IAklaflowManager.ActionType.GET_LAST_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Instance) xstream.fromXML(xml);
	}

	@Override
	public void saveFormCellLink(FormCellLink cellLink) throws Exception {
		XmlAction op = new XmlAction(createArguments(cellLink), IAklaflowManager.ActionType.SAVE_FORM_CELL_LINK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteFormCellLink(FormCellLink cellLink) throws Exception {
		XmlAction op = new XmlAction(createArguments(cellLink), IAklaflowManager.ActionType.DELETE_FORM_CELL_LINK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormCellLink> getAllFormCellLinksbyOCRForm(StandardForm form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IAklaflowManager.ActionType.GET_ALL_FORM_CELL_LINKS_BY_OCR_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormCellLink>) xstream.fromXML(xml);
	}

	@Override
	public List<FormCellLink> getAllFormCellLinksbyAklaForm(Form form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IAklaflowManager.ActionType.GET_ALL_FORM_CELL_LINKS_BY_AKLA_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormCellLink>) xstream.fromXML(xml);
	}

	@Override
	public List<FormCellLink> getAllFormCellLinksbyFormCell(FormCell cell) throws Exception {
		XmlAction op = new XmlAction(createArguments(cell), IAklaflowManager.ActionType.GET_ALL_FORM_CELL_LINKS_BY_OCR_FORM_CELL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormCellLink>) xstream.fromXML(xml);
	}

	@Override
	public List<WorkflowLog> takeOverWorkflow(Instance instance, Workflow workflow, User user, String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance, workflow, user, path), IAklaflowManager.ActionType.TAKE_OVER_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkflowLog>) xstream.fromXML(xml);
	}

	@Override
	public Instance getInstancebyId(int instanceId) throws Exception {
		XmlAction op = new XmlAction(createArguments(instanceId), IAklaflowManager.ActionType.GET_INSTANCE_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Instance) xstream.fromXML(xml);
	}

	@Override
	public HashMap<FormCell, FormCellResult> analyzeTempForm(StandardForm form, Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(form, doc), IAklaflowManager.ActionType.ANALYZE_TEMP_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (HashMap<FormCell, FormCellResult>) xstream.fromXML(xml);
	}

	@Override
	public FormCellResult analyzeFullDocument(Documents doc, String lang) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, lang), IAklaflowManager.ActionType.ANALYZE_FULL_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormCellResult) xstream.fromXML(xml);
	}

	@Override
	public void analyzeForm(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IAklaflowManager.ActionType.ANALYZE_FORM_SIMPLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormTextTemplate> getAllFormText() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_ALL_FORM_TEXT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormTextTemplate>) xstream.fromXML(xml);
	}

	@Override
	public List<FormTextTemplate> getFormTextbyForm(int formId) throws Exception {
		XmlAction op = new XmlAction(createArguments(formId), IAklaflowManager.ActionType.GET_FORM_TEXT_BY_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormTextTemplate>) xstream.fromXML(xml);
	}

//	@Override
//	public RADResultObject radDocument(Documents doc, int learningFolderId, User user) throws Exception {
//		XmlAction op = new XmlAction(createArguments(doc, learningFolderId, user), IAklaflowManager.ActionType.RAD_DOCUMENT);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (RADResultObject) xstream.fromXML(xml);
//	}

	@Override
	public List<RADResultObject> radDocumentsWithForms(List<Documents> docs, int learningFolderId, User user, List<String> langs, String defaultLang, boolean forceDefault) throws Exception {
		XmlAction op = new XmlAction(createArguments(docs, learningFolderId, user, langs, defaultLang, forceDefault), IAklaflowManager.ActionType.RAD_MANY_DOCUMENTS_WITH_FORMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RADResultObject>) xstream.fromXML(xml);
	}

	@Override
	public void saveFormTextTemplate(StandardForm standardForm) throws Exception {
		XmlAction op = new XmlAction(createArguments(standardForm), IAklaflowManager.ActionType.SAVE_FORM_TEXT_TEMPLATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveFormTextTemplate(StandardForm standardForm, FormCellResult res) throws Exception {
		XmlAction op = new XmlAction(createArguments(standardForm, res), IAklaflowManager.ActionType.SAVE_FORM_TEXT_TEMPLATE2);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void linkForms(int aklaboxFormId, int ocrFormId) throws Exception {
		XmlAction op = new XmlAction(createArguments(aklaboxFormId, ocrFormId), IAklaflowManager.ActionType.LINK_FORMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public int[] getImageSize(String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(path), IAklaflowManager.ActionType.GET_IMAGE_SIZE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (int[]) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getBlankPages(List<Documents> docs) throws Exception {
		XmlAction op = new XmlAction(createArguments(docs), IAklaflowManager.ActionType.GET_BLANK_PAGES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public void deleteStandardForm(StandardForm form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IAklaflowManager.ActionType.DELETE_STANDARD_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public List<RADResultObject> radDocumentsWithKeyWords(List<Documents> docs, User user, List<String> langs, String defaultLang, boolean forceDefault) throws Exception {
		XmlAction op = new XmlAction(createArguments(docs, user, langs, defaultLang, forceDefault), IAklaflowManager.ActionType.RAD_MANY_DOCUMENTS_WITH_KEYWORDS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RADResultObject>) xstream.fromXML(xml);
	}

	@Override
	public List<String> getAvailableLangs() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_ALL_LANGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public InputStream exportAkladWorkSpace(List<AkLadExportObject> docs)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(docs), IAklaflowManager.ActionType.EXPORT_AKLAD_WORKSPACE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (InputStream) xstream.fromXML(xml);
	}

	@Override
	public List<AkLadExportObject> importAkladWorkSpace(InputStream importStream, User user)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(importStream, user), IAklaflowManager.ActionType.IMPORT_AKLAD_WORKSPACE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AkLadExportObject>) xstream.fromXML(xml);
	}
	
	@Override
	public List<AkLadExportObject> importAkladWorkSpace(String path, User user)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(path, user), IAklaflowManager.ActionType.IMPORT_AKLAD_WORKSPACE2);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AkLadExportObject>) xstream.fromXML(xml);
	}

	@Override
	public void uploadFileForAklad(String zipName, String docName, InputStream importStream, User user, boolean doOCR, String langOCR) throws Exception {
		XmlAction op = new XmlAction(createArguments(zipName, docName, importStream ,user, doOCR, langOCR), IAklaflowManager.ActionType.UPLOAD_FILE_FOR_AKLAD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<String> listWorkspaceFiles() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.LIST_WORKSPACE_FILES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public OrbeonWorkflowInformation getOrbeonInformation(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IAklaflowManager.ActionType.GET_ORBEON_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (OrbeonWorkflowInformation) xstream.fromXML(xml);
	}

	@Override
	public List<Activity> getOrbeonNextActivity(Workflow workflow, Instance instance) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow, instance), IAklaflowManager.ActionType.GET_ORBEON_NEXT_ACTIVITY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Activity>) xstream.fromXML(xml);
	}

	@Override
	public void updateOrbeonWorkflow(List<WorkflowLog> logs, User user, Workflow workflow, Instance instance, boolean validate, List<String> sectionNames) throws Exception {
		XmlAction op = new XmlAction(createArguments(logs, user, workflow, instance, validate, sectionNames), IAklaflowManager.ActionType.UPDATE_ORBEON_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Instance> getAllInstances() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_ALL_INSTANCES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Instance>) xstream.fromXML(xml);
	}

	@Override
	public void sendOrbeonNotification(int userId, Instance instance) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, instance), IAklaflowManager.ActionType.SEND_ORBEON_NOTIF);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<OrganigramElementSecurity> getDynamicSuperior(Activity nextActivity, Instance instance, OrganigramElement dynamicElem, User baseUser) throws Exception {
		XmlAction op = new XmlAction(createArguments(nextActivity, instance, dynamicElem, baseUser), IAklaflowManager.ActionType.GET_DYNAMIC_SUPERIOR);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<OrganigramElementSecurity>) xstream.fromXML(xml);
	}

	@Override
	public List<String> getInstanceIdsForMailing() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IAklaflowManager.ActionType.GET_INSTANCEIDS_MAILING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	public OrbeonWorkflowInformation getOrbeonInformation(String instanceId) throws Exception {
		XmlAction op = new XmlAction(createArguments(instanceId), IAklaflowManager.ActionType.GET_ORBEON_BY_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (OrbeonWorkflowInformation) xstream.fromXML(xml);
	}

	@Override
	public List<Variable> getAllVariablesbyType(String type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), IAklaflowManager.ActionType.GET_ALL_VARIABLES_BY_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Variable>) xstream.fromXML(xml);
	}

	@Override
	public StandardForm getStandardFormbyLink(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IAklaflowManager.ActionType.GET_OCR_FORM_BY_LINK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml.equals("") ? null : (StandardForm) xstream.fromXML(xml);
	}
}