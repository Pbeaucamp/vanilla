package bpm.vanillahub.runtime.run;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.ActionActivity;
import bpm.vanillahub.core.beans.activities.AklaboxActivity;
import bpm.vanillahub.core.beans.activities.CibleActivity;
import bpm.vanillahub.core.beans.activities.CompressionActivity;
import bpm.vanillahub.core.beans.activities.ConnectorXmlActivity;
import bpm.vanillahub.core.beans.activities.CrawlActivity;
import bpm.vanillahub.core.beans.activities.DataServiceActivity;
import bpm.vanillahub.core.beans.activities.EncryptionActivity;
import bpm.vanillahub.core.beans.activities.GeojsonActivity;
import bpm.vanillahub.core.beans.activities.LimeSurveyInputActivity;
import bpm.vanillahub.core.beans.activities.MailActivity;
import bpm.vanillahub.core.beans.activities.MdmActivity;
import bpm.vanillahub.core.beans.activities.MdmInputActivity;
import bpm.vanillahub.core.beans.activities.MergeFilesActivity;
import bpm.vanillahub.core.beans.activities.OpenDataActivity;
import bpm.vanillahub.core.beans.activities.OpenDataCrawlActivity;
import bpm.vanillahub.core.beans.activities.PreClusterGeoDataActivity;
import bpm.vanillahub.core.beans.activities.RunVanillaItemActivity;
import bpm.vanillahub.core.beans.activities.SocialNetworkActivity;
import bpm.vanillahub.core.beans.activities.SourceActivity;
import bpm.vanillahub.core.beans.activities.ValidationActivity;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.core.beans.resources.Certificat;
import bpm.vanillahub.core.beans.resources.FileXSD;
import bpm.vanillahub.core.beans.resources.ServerMail;
import bpm.vanillahub.core.beans.resources.SocialNetworkServer;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.dao.ResourceDao;
import bpm.vanillahub.runtime.dao.WorkflowDao;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.vanillahub.runtime.mail.MailConfig;
import bpm.vanillahub.runtime.managers.FileManager;
import bpm.vanillahub.runtime.utils.MailHelper;
import bpm.vanillahub.runtime.utils.Utils;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;
import bpm.workflow.commons.beans.activity.StartActivity;
import bpm.workflow.commons.beans.activity.StopActivity;
import bpm.workflow.commons.resources.Cible;
import bpm.workflow.commons.utils.Constants;
import bpm.workflow.commons.utils.VariableHelper;

public class RunManager {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
	private SimpleDateFormat emailDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private IVanillaLogger logger;
	private WorkflowDao workflowManager;
	private FileManager fileManager;

	private List<Variable> currentVariables;
	private List<Parameter> currentParameters;
	private List<Cible> cibles;
	private List<Certificat> certificats;
	private List<Source> sources;
	private List<ServerMail> serverMails;
	private List<FileXSD> fileXSDs;
//	private List<DatabaseServer> databases;
	private List<ApplicationServer> applicationServers;
	private List<SocialNetworkServer> socialServers;

	private HashMap<Activity, IRunner> runners;

	private Activity loopParentActivity;
	private Result loopParentResultActivity;

	private boolean stopByUser = false;

	public RunManager(IVanillaLogger logger, WorkflowDao workflowManager, FileManager fileManager, ResourceDao resourceManager) throws Exception {
		this.logger = logger;
		this.workflowManager = workflowManager;
		this.fileManager = fileManager;

		logger.info("Init resources...");
		this.currentVariables = resourceManager.getVariables();
		this.currentParameters = resourceManager.getParameters();
		this.cibles = resourceManager.getCibles();
		this.certificats = resourceManager.getCertificats();
		this.sources = resourceManager.getSources();
		this.serverMails = resourceManager.getServerMails();
		this.fileXSDs = resourceManager.getFileXSDs();
//		this.databases = resourceManager.getDatabaseServers();
		this.applicationServers = resourceManager.getApplicationServers();
		this.socialServers = resourceManager.getSocialServer();
		logger.info("Resources inited.");
	}

	public List<Parameter> getParameters(Locale locale, Workflow workflow) throws Exception {
		logger.info("Getting parameters for workflow '" + workflow.getName() + "'.");
		if (!workflow.isValid()) {
			logger.error("Workflow '" + workflow.getName() + "' is not valid.");
			throw new Exception("'" + workflow.getName() + "' " + Labels.getLabel(locale, Labels.WorkflowNotValidModifyToLaunch));
		}

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (Activity activity : workflow.getWorkflowModel().getActivities()) {
			IRunner runner = getRunner(null, locale, workflow.getId(), workflow.getName(), null, activity, currentParameters, currentVariables, false, false, false);

			List<Parameter> activityParams = runner.getParameters();
			for (Parameter param : activityParams) {

				boolean found = false;
				for (Parameter currentParam : currentParameters) {
					if (param.getId() == currentParam.getId() && !isParamPresent(parameters, param)) {
						parameters.add(currentParam);
						found = true;
						break;
					}
				}

				if (!found && !isParamPresent(parameters, param)) {
					logger.warn("Variable '" + param.getName() + "' has not been found, we will use an old definition of it.");
					parameters.add(param);
				}
			}
		}

		return parameters;
	}

	private boolean isParamPresent(List<Parameter> parameters, Parameter currentParam) {
		if (parameters != null) {
			for (Parameter param : parameters) {
				if (param.getId() == currentParam.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * We check with the current variables to see if some has been removed or
	 * the value has been changed.
	 * 
	 * @param workflow
	 * @param variables
	 * @return the variables of the selected workflow
	 * @throws Exception
	 */
	private List<Variable> getVariables(Locale locale, Workflow workflow, String launcher, List<Parameter> currentParameters, List<Variable> currentVariables) throws Exception {
		List<Variable> variables = new ArrayList<Variable>();
		for (Activity activity : workflow.getWorkflowModel().getActivities()) {
			IRunner runner = getRunner(null, locale, workflow.getId(), workflow.getName(), launcher, activity, currentParameters, currentVariables, false, false, false);

			List<Variable> activityVars = runner.getVariables();
			for (Variable var : activityVars) {

				boolean found = false;
				for (Variable currentVar : currentVariables) {
					if (var.getId() == currentVar.getId()) {
						variables.add(currentVar);
						found = true;
						break;
					}
				}

				if (!found) {
					logger.warn("Variable '" + var.getName() + "' has not been found, we will use an old definition of it.");
					variables.add(var);
				}
			}
		}
		return variables;
	}

	public WorkflowInstance run(WorkflowProgress workflowProgress, Locale locale, Workflow workflow, String uuid, String launcher, List<Parameter> parameters) throws Exception {
		WorkflowInstance instance = new WorkflowInstance(uuid, workflow);
		instance.setResult(Result.RUNNING);

		return run(workflowProgress, locale, workflow, launcher, parameters, instance);
	}

	public void stopWorkflow() {
		this.stopByUser = true;
	}

	public boolean isStopByUser() {
		return stopByUser;
	}

	private WorkflowInstance run(WorkflowProgress workflowProgress, Locale locale, Workflow workflow, String launcher, List<Parameter> parameters, WorkflowInstance instance) throws Exception {
		if (!workflow.isValid()) {
			throw new Exception("'" + workflow.getName() + "' " + Labels.getLabel(locale, Labels.WorkflowNotValidModifyToLaunch));
		}

		this.stopByUser = false;

		List<Variable> variables = getVariables(locale, workflow, launcher, parameters, currentVariables);
		if (currentVariables != null && !variables.isEmpty()) {
			logger.info("Init variables...");
			variables = VariableHelper.initVariables(logger, locale, parameters, variables, currentVariables);
			logger.info("Variables inited.");
		}
		long startTime = new Date().getTime();

		StartActivity activity = workflow.orderActivities();

		IRunner runner = getRunner(workflowProgress, locale, workflow.getId(), workflow.getName(), launcher, activity, parameters, variables, false, false, true);
		ResultActivity result = runActivity(workflowProgress, locale, workflow.getId(), workflow.getName(), launcher, activity, runner.runActivity(locale, null), parameters, variables, false, false);

		instance.setStopByUser(stopByUser);
		if (result.getResult() == null || result.getResult() == Result.ERROR || stopByUser) {
			instance.setResult(Result.ERROR);
		}
		else {
			instance.setResult(Result.SUCCESS);
		}

		instance.setEndDate(new Date());

		List<ActivityLog> logs = Utils.getLogsAsList(result.getLogs());
		instance.setActivityLogs(logs);

		if (result.sendMailAtTheEnd()) {
			sendMails(locale, workflow, instance, result, result.getServerMail(), result.getMailConfigs(), parameters, variables);
		}
		long endTime = new Date().getTime();

		logger.info("Exécution du workflow en " + (endTime - startTime) + " ms.");

		return workflowManager.add(instance);
	}

	private boolean sendMails(Locale locale, Workflow workflow, WorkflowInstance instance, ResultActivity result, ServerMail serverMail, List<MailConfig> mailConfigs, List<Parameter> parameters, List<Variable> variables) {
		try {
			if (!result.sendOnlyIfError() || result.getResult() != Result.SUCCESS) {
				String fileName = Utils.clearName(workflow.getName()) + "_" + dateFormat.format(new Date()) + "." + Constants.LOG_EXTENSION;
	
				HashMap<String, InputStream> attachments = new HashMap<String, InputStream>();
				if (result.joinLog()) {
					attachments.put(fileName, result.getLogsAsInputStream());
				}
	
				MailHelper mailHelper = new MailHelper(serverMail, parameters, variables);
				int sentEmail = 0;
				for (MailConfig config : mailConfigs) {
					result.addInfo(Labels.getLabel(locale, Labels.SendEmailAt) + " " + config.getRecipient());
	
					String textEmail = config.getText(locale, getDateString(instance.getStartDate()), getDateString(new Date()), instance.getResult() != null && instance.getResult() == Result.ERROR);
	
					mailHelper.sendEmail(serverMail, config, textEmail, attachments, parameters, variables);
					sentEmail++;
				}
	
				result.addInfo(Labels.getLabel(locale, Labels.SendOf) + " " + sentEmail + " " + Labels.getLabel(locale, Labels.Emails));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			result.addError("Erreur " + e.getMessage());
		} catch (Throwable e) {
			e.printStackTrace();
			result.addError("Erreur " + e.getMessage());
		}

		return false;
	}

	private String getDateString(Date date) {
		return emailDateFormat.format(date);
	}

	private ResultActivity runActivity(WorkflowProgress workflowProgress, Locale locale, int workflowId, String workflowName, String launcher, Activity activity, ResultActivity parentResult, List<Parameter> parameters, List<Variable> variables, boolean isLoop, boolean loopEnd) throws Exception {
		if (stopByUser) {
			if (!isLoop) {
				parentResult.addWarning(Labels.getLabel(locale, Labels.InterruptedByUser));
			}
			return parentResult;
		}

		if (parentResult.getResult() == Result.ERROR) {
			return parentResult;
		}

		if (activity.hasChildActivity()) {
			Activity runningActivity = activity.getChildActivity();

			if (loopParentActivity != null && !runningActivity.isLoop()) {
				if (loopParentResultActivity != Result.RUNNING) {
					this.loopParentActivity = null;
				}
				return parentResult;
			}

			ResultActivity result = null;
			if (runningActivity.isLoop() && loopParentActivity == null) {
				loopParentActivity = runningActivity;

				do {
					IRunner runner = getRunner(workflowProgress, locale, workflowId, workflowName, launcher, runningActivity, parameters, variables, true, loopEnd, true);
					ResultActivity runningActivityResult = runner.runActivity(locale, parentResult);

					loopParentResultActivity = runningActivityResult.getResult();
					loopEnd = false;
					if (loopParentResultActivity == Result.SUCCESS || stopByUser) {
						loopEnd = true;
					}

					result = runActivity(workflowProgress, locale, workflowId, workflowName, launcher, runningActivity, runningActivityResult, parameters, variables, true, loopEnd);
					if (result.getResult() == Result.ERROR) {
						return result;
					}

					if (loopEnd) {
						runningActivity = getLastActivityFromLoop(runningActivity);
					}
				} while (loopParentResultActivity == Result.RUNNING);
			}
			else {
				IRunner runner = getRunner(workflowProgress, locale, workflowId, workflowName, launcher, runningActivity, parameters, variables, isLoop, loopEnd, true);
				result = runner.runActivity(locale, parentResult);
			}

			if (runningActivity instanceof StopActivity) {
				return result;
			}
			else {
				return runActivity(workflowProgress, locale, workflowId, workflowName, launcher, runningActivity, result, parameters, variables, isLoop, false);
			}
		}
		else {
			throw new Exception(Labels.getLabel(locale, Labels.WorkflowNotValid));
		}
	}

	private Activity getLastActivityFromLoop(Activity runningActivity) {
		if (runningActivity.getChildActivity().isLoop()) {
			return getLastActivityFromLoop(runningActivity.getChildActivity());
		}
		return runningActivity;
	}

	private IRunner getRunner(WorkflowProgress workflowProgress, Locale locale, int workflowId, String workflowName, String launcher, Activity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, boolean loopEnd, boolean saveRunner) throws Exception {
		if (runners == null) {
			runners = new HashMap<>();
		}
		else if (runners.get(activity) != null) {
			IRunner runner = runners.get(activity);
			runner.setLoop(isLoop);
			runner.setLoopEnd(loopEnd);
			return runner;
		}

		IRunner runner = null;
		if (activity instanceof StartActivity) {
			runner = new StartActivityRunner(workflowProgress, logger, workflowName, launcher, (StartActivity) activity, parameters, variables, isLoop);
		}
		else if (activity instanceof StopActivity) {
			runner = new StopActivityRunner(workflowProgress, logger, workflowName, launcher, (StopActivity) activity, parameters, variables, isLoop);
		}
		else if (activity instanceof MailActivity) {
			runner = new MailActivityRunner(workflowProgress, logger, workflowName, launcher, (MailActivity) activity, parameters, variables, isLoop, serverMails);
		}
		else if (activity instanceof CibleActivity) {
			runner = new CibleActivityRunner(workflowProgress, logger, workflowId, workflowName, launcher, (CibleActivity) activity, parameters, variables, isLoop, cibles);
		}
		else if (activity instanceof CompressionActivity) {
			runner = new CompressionActivityRunner(workflowProgress, logger, workflowName, launcher, (CompressionActivity) activity, parameters, variables, isLoop);
		}
		else if (activity instanceof EncryptionActivity) {
			runner = new EncryptionActivityRunner(workflowProgress, logger, workflowName, launcher, (EncryptionActivity) activity, parameters, variables, isLoop, certificats, fileManager);
		}
		else if (activity instanceof SourceActivity) {
			runner = new SourceActivityRunner(workflowProgress, logger, workflowName, launcher, (SourceActivity) activity, parameters, variables, isLoop, sources);
		}
		else if (activity instanceof ValidationActivity) {
			runner = new ValidationActivityRunner(workflowProgress, logger, workflowName, launcher, (ValidationActivity) activity, parameters, variables, isLoop, fileXSDs, fileManager);
		}
		else if (activity instanceof ConnectorXmlActivity) {
			runner = new ConnectorActivityRunner(workflowProgress, logger, workflowName, launcher, (ConnectorXmlActivity) activity, parameters, variables, isLoop, cibles);
		}
		else if (activity instanceof ActionActivity) {
			runner = new ActionActivityRunner(workflowProgress, logger, workflowName, launcher, (ActionActivity) activity, parameters, variables, isLoop, sources);
		}
		else if (activity instanceof DataServiceActivity) {
			runner = new DataServiceActivityRunner(workflowProgress, logger, workflowName, launcher, (DataServiceActivity) activity, parameters, variables, isLoop);
		}
		else if (activity instanceof RunVanillaItemActivity) {
			runner = new RunVanillaItemActivityRunner(workflowProgress, logger, workflowName, launcher, (RunVanillaItemActivity) activity, parameters, variables, isLoop, applicationServers);
		}
		else if (activity instanceof CrawlActivity) {
			runner = new CrawlActivityRunner(workflowProgress, logger, workflowName, launcher, (CrawlActivity) activity, parameters, variables, isLoop, cibles);
		}
		else if (activity instanceof SocialNetworkActivity) {
			runner = new SocialNetworkRunner(workflowProgress, logger, workflowName, launcher, (SocialNetworkActivity) activity, parameters, variables, isLoop, socialServers);
		}
		else if (activity instanceof OpenDataActivity) {
			runner = new OpenDataActivityRunner(workflowProgress, logger, workflowName, launcher, (OpenDataActivity) activity, parameters, variables, isLoop);
		}
		else if (activity instanceof MdmActivity) {
			runner = new MdmActivityRunner(workflowProgress, logger, workflowName, launcher, (MdmActivity) activity, parameters, variables, isLoop, applicationServers, sources);
		}
		else if (activity instanceof AklaboxActivity) {
			runner = new AklaboxActivityRunner(workflowProgress, logger, workflowName, launcher, (AklaboxActivity) activity, parameters, variables, isLoop, applicationServers);
		}
		else if (activity instanceof GeojsonActivity) {
			runner = new GeojsonActivityRunner(workflowProgress, logger, workflowName, launcher, (GeojsonActivity) activity, parameters, variables, isLoop);
		}
		else if (activity instanceof PreClusterGeoDataActivity) {
			runner = new PreClusterGeoDataActivityRunner(workflowProgress, logger, workflowName, launcher, (PreClusterGeoDataActivity) activity, parameters, variables, isLoop, cibles);
		}
		else if (activity instanceof OpenDataCrawlActivity) {
			runner = new OpenDataCrawlActivityRunner(workflowProgress, logger, workflowName, launcher, (OpenDataCrawlActivity) activity, parameters, variables, isLoop, cibles);
		}
		else if (activity instanceof MdmInputActivity) {
			runner = new MdmInputActivityRunner(workflowProgress, logger, workflowName, launcher, (MdmInputActivity) activity, parameters, variables, isLoop, applicationServers);
		}
		else if (activity instanceof LimeSurveyInputActivity) {
			runner = new LimeSurveyInputActivityRunner(workflowProgress, logger, workflowName, launcher, (LimeSurveyInputActivity) activity, parameters, variables, isLoop, applicationServers);
		}
		else if (activity instanceof MergeFilesActivity) {
			runner = new MergeFilesActivityRunner(workflowProgress, logger, workflowName, launcher, (MergeFilesActivity) activity, parameters, variables, isLoop);
		}

		if (runner == null) {
			throw new Exception(Labels.getLabel(locale, Labels.ActivityNotSupported));
		}
		else {
			runner.setLoopEnd(loopEnd);
			if (saveRunner) {
				runners.put(activity, runner);
			}
			return runner;
		}
	}
}
