package bpm.vanillahub.runtime.run;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import bpm.aklabox.workflow.core.IAklaflowManager;
import bpm.aklabox.workflow.core.model.activities.AklaflowContext;
import bpm.aklabox.workflow.remote.RemoteAklaflowManager;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.remote.RemoteVdmManager;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.AklaboxActivity;
import bpm.vanillahub.core.beans.activities.AklaboxActivity.AklaboxApp;
import bpm.vanillahub.core.beans.activities.attributes.AklaboxDispatch;
import bpm.vanillahub.core.beans.resources.AklaboxServer;
import bpm.vanillahub.core.beans.resources.ApplicationServer;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.workflow.commons.beans.Result;

public class AklaboxActivityRunner extends ActivityRunner<AklaboxActivity> {

	private List<ApplicationServer> vanillaServers;

	public AklaboxActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, AklaboxActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop, List<ApplicationServer> vanillaServers) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.vanillaServers = vanillaServers;
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(vanillaServers);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(vanillaServers);
	}

	@Override
	protected void clearResources() {
		
	}

	@Override
	protected void run(Locale locale) {
		try {
			AklaboxServer server = (AklaboxServer) activity.getResource(vanillaServers);

			AklaboxApp app = activity.getApp();
			
			if(result.isBigFile()) {
				throw new Exception("This activity doesn't support files > 500MB");
			}
			
			if (app == AklaboxApp.AKLABOX) {
				try {
					uploadAklaboxFile(locale, server, result.getFileName(), result.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					addError(e.getMessage());
					result.setResult(Result.ERROR);
				}
			}
			else if (app == AklaboxApp.AKLAD) {
				try {
					String projectName = activity.getProjectName(parameters, variables);
					uploadAkladFile(locale, server, projectName, result.getFileName(), result.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					addError(e.getMessage());
					result.setResult(Result.ERROR);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			addError(e.getMessage());
			result.setResult(Result.ERROR);
		}
	}	
	
	public void uploadAklaboxFile(Locale locale, AklaboxServer server, String fileName, InputStream inputStream) {
		String url = server.getUrl(parameters, variables);
		String login = server.getLogin(parameters, variables);
		String password = server.getPassword(parameters, variables);
		
		VdmContext ctx = new VdmContext(url, login, password, -1);
		User user = new User();
		user.setEmail(login);
		user.setPassword(password);
		
		IVdmManager manager = new RemoteVdmManager(ctx);
		try {
			user = manager.connect(user);
			addInfo(Labels.getLabel(locale, Labels.ConnectedToAklaboxAs) + " '" + user.getEmail() + "'");
		} catch (Exception e) {
			e.printStackTrace();
			
			addError(Labels.getLabel(locale, Labels.ConnectionAklaboxImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			return;
		}
		
		int parentId = checkRules(activity, fileName);
		
		try {
			addInfo(Labels.getLabel(locale, Labels.UploadingFileToAklabox) + " '" + fileName + "'");
			
			Documents doc = new Documents();
			doc.setName(fileName);
			doc.setFileName(fileName);
			doc.setUploadDate(new Date());
			doc.setUserId(user.getUserId());
			doc.setCreationDate(new Date());
			doc.setAuthorName(user.getEmail());
			doc.setLastModified(new Date());
			doc.setLastModifiedBy(user.getEmail());
			doc.setTreeType("Documents");
			doc.setValidationStatus("Waiting");

			doc.setParentId(parentId);
			
			doc.setFileSize((int) inputStream.available());
			doc.setFileExtension(FilenameUtils.getExtension(fileName));
			doc.setOriginalPath(fileName);
			
			doc.setAnnotation("");
			doc.setCheckoutStatus(false);
			doc.setDeleted(false);
			doc.setEncrypt(false);
			doc.setFinished(true);
			doc.setEncryptPassword(null);

			String uniqueID = UUID.randomUUID().toString();
			doc.setUniqueCode(uniqueID);

			HashMap<String, InputStream> map = new HashMap<String, InputStream>();
			map.put(fileName, inputStream);
			
			manager.uploadFiles(map, doc);
		} catch (Exception e) {
			e.printStackTrace();
			
			addError(Labels.getLabel(locale, Labels.CouldNotCreateFile));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
		}

		addInfo(Labels.getLabel(locale, Labels.FileHasBeenUploadedToAklabox));
		setResult(Result.SUCCESS);
	}	
	
	private int checkRules(AklaboxActivity activity, String fileName) {
		if (activity.getRules() != null) {
			for (AklaboxDispatch rule : activity.getRules()) {
				if (checkRule(rule, fileName)) {
					return rule.getAklaboxFolderId();
				}
			}
		}
		return activity.getItemId() != null ? activity.getItemId() : 0;
	}

	private boolean checkRule(AklaboxDispatch rule, String fileName) {
		return Pattern.matches(rule.getPattern(), fileName);
	}

	public void uploadAkladFile(Locale locale, AklaboxServer server, String zipName, String fileName, InputStream inputStream) {
		String url = server.getUrl(parameters, variables);
		String login = server.getLogin(parameters, variables);
		String password = server.getPassword(parameters, variables);
		
		AklaflowContext ctx = new AklaflowContext(url, login, password);
		User user = new User();
		user.setEmail(login);
		user.setPassword(password);
		
		IAklaflowManager manager = new RemoteAklaflowManager(ctx);
		try {
			addInfo(Labels.getLabel(locale, Labels.ConnectedToAklaboxAs) + " '" + user.getEmail() + "'");
		} catch (Exception e) {
			e.printStackTrace();
			
			addError(Labels.getLabel(locale, Labels.ConnectionAklaboxImpossible));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			return;
		}
		
		try {
			addInfo(Labels.getLabel(locale, Labels.UploadingFileToAklabox) + " '" + fileName + "'");
			
			manager.uploadFileForAklad(zipName, fileName, inputStream, user, activity.isRunOcr(), activity.getLang());
		} catch (Exception e) {
			e.printStackTrace();
			
			addError(Labels.getLabel(locale, Labels.CouldNotCreateFile));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);
		}

		addInfo(Labels.getLabel(locale, Labels.FileHasBeenUploadedToAklabox));
		setResult(Result.SUCCESS);
	}
}
