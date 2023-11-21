package bpm.vanilla.platform.core.runtime.alerts;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;
import bpm.vanilla.platform.core.beans.alerts.Action.TypeAction;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.alerts.ActionGateway;
import bpm.vanilla.platform.core.beans.alerts.ActionWorkflow;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.listeners.event.impl.ObjectExecutedEvent;
import bpm.vanilla.platform.core.repository.IAlertExecutor;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.alerts.actions.AlertToGTW;
import bpm.vanilla.platform.core.runtime.alerts.actions.AlertToWKF;
import bpm.vanilla.platform.core.runtime.alerts.actions.ValidationMail;

public class ValidationRuntime implements IAlertExecutor {

	private Logger logger = Logger.getLogger(getClass());

	private IVanillaAPI vanillaApi;
	private IRepositoryApi repositoryApi;

	private List<Validation> validations;
	private ObjectExecutedEvent triggerEvent;

	public ValidationRuntime(IVanillaAPI vanillaApi, IRepositoryApi repositoryApi, List<Validation> validations, ObjectExecutedEvent triggerEvent) {
		this.vanillaApi = vanillaApi;
		this.repositoryApi = repositoryApi;
		this.validations = validations;
		this.triggerEvent = triggerEvent;
	}

	@Override
	public void execute() throws Exception {
		boolean success = triggerEvent.getState().getTaskResult() == ActivityResult.SUCCEED;
		for (Validation validation : validations) {
			if(validation.isActif()) {
				RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(validation.getItemId());
				
				if (success) {
					Alert alert = repositoryApi.getAlertService().getAlert(validation.getAlertId());
					if (alert != null) {
						logger.info("Execute alert for validation on item with id = " + validation.getItemId());
						executeAction(validation, alert, item);
					}
	
					logger.info("Set validation to not valid on item with id = " + validation.getItemId());
					validation.setValid(false);
					repositoryApi.getRepositoryService().addOrUpdateValidation(validation);
				}
				else {
					ValidationMail.sendMailErrorToAdmin(vanillaApi, validation, item, true);
				}
			}
		}
	}

	private void executeAction(Validation validation, Alert alert, RepositoryItem item) throws Exception {
		TypeAction typeAction = alert.getTypeAction();
		if (typeAction == TypeAction.GATEWAY) {
			ActionGateway act = (ActionGateway) alert.getAction().getActionObject();
			logger.info("Launch gateway with id = " + act.getDirectoryItemId());

			int repId = act.getRepositoryId();
			IObjectIdentifier identifier = new ObjectIdentifier(repId, act.getDirectoryItemId());

			AlertToGTW alertToGTW = new AlertToGTW(getRepositoryContext(repId, triggerEvent.getGroupId()), identifier, new ArrayList<VanillaGroupParameter>());
			alertToGTW.runGTW(false);
		}
		else if (typeAction == TypeAction.WORKFLOW) {
			ActionWorkflow act = (ActionWorkflow) alert.getAction().getActionObject();
			logger.info("Launch workflow with id = " + act.getDirectoryItemId());

			int repId = act.getRepositoryId();
			IObjectIdentifier identifier = new ObjectIdentifier(repId, act.getDirectoryItemId());

			AlertToWKF alertToWKF = new AlertToWKF(getRepositoryContext(repId, triggerEvent.getGroupId()), identifier, new ArrayList<VanillaGroupParameter>());
			alertToWKF.runWKF(false);
		}
		else if (typeAction == TypeAction.MAIL) {
			ValidationMail.sendMailToNextCommentator(vanillaApi, validation, item, null, false);
		}
	}

	private IRepositoryContext getRepositoryContext(int repId, Integer groupId) {
		Group group = new Group();
		group.setId(groupId != null ? groupId : -1);

		Repository repository = new Repository();
		repository.setId(repId);

		return new BaseRepositoryContext(vanillaApi.getVanillaContext(), group, repository);
	}
}
