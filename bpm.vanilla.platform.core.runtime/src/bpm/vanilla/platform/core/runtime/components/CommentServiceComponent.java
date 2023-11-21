package bpm.vanilla.platform.core.runtime.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.ComponentContext;

import bpm.vanilla.platform.core.ICommentService;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityState;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.comments.CommentValue.CommentStatus;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValueParameter;
import bpm.vanilla.platform.core.beans.validation.UserValidation;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.runtime.alerts.actions.AlertToGTW;
import bpm.vanilla.platform.core.runtime.alerts.actions.ValidationMail;

public class CommentServiceComponent extends AbstractVanillaManager implements ICommentService {

	private IVanillaContext rootVanillaCtx;

	public void activate(ComponentContext ctx) {
		try {
			super.activate(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void init() throws Exception {
		this.rootVanillaCtx = getRootVanillaContext();
	}

	@Override
	public String getComponentName() {
		return getClass().getName();
	}

	@Override
	public void addComment(int userId, CommentValue comment, int repId) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		repositoryApi.getRepositoryService().addOrUpdateCommentValue(comment);
	}

	@Override
	public void addComments(Validation validation, int userId, List<CommentValue> comments, int repId) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);

		for (CommentValue comment : comments) {
			repositoryApi.getRepositoryService().addOrUpdateCommentValue(comment);
		}

		if (validation != null) {
			RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(validation.getItemId());
			ValidationMail.sendMailToNextCommentator(getRootVanillaApi(), validation, item, userId, false);
		}
	}

	@Override
	public void modifyComments(Validation validation, int userId, List<CommentValue> comments, int repId, boolean isLastCommentUnvalidate) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);

		for (CommentValue comment : comments) {
			repositoryApi.getRepositoryService().addOrUpdateCommentValue(comment);
		}

		if (validation != null && isLastCommentUnvalidate) {
			RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(validation.getItemId());
			ValidationMail.sendMailToNextCommentator(getRootVanillaApi(), validation, item, userId, false);
		}
	}

	@Override
	public List<CommentValue> getComments(int itemId, int repId, String commentName, List<CommentParameter> parameters) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		return repositoryApi.getRepositoryService().getComments(itemId, commentName, parameters);
	}

	@Override
	public List<CommentValue> getComments(int commentDefinitionId, int repId, int userId) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		return repositoryApi.getRepositoryService().getComments(commentDefinitionId, repId, userId);
	}

	@Override
	public CommentDefinition getCommentDefinition(int itemId, int repId, String commentName) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		return repositoryApi.getRepositoryService().getCommentDefinition(itemId, commentName);
	}

	@Override
	public List<CommentDefinition> getCommentDefinitions(int itemId, int repId) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		return repositoryApi.getRepositoryService().getCommentDefinitions(itemId);
	}

	@Override
	public CommentValue getCommentNotValidate(int commentDefinitionId, int repId) throws Exception {
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		return repositoryApi.getRepositoryService().getCommentNotValidate(commentDefinitionId);
	}

	@Override
	public void validate(Validation validation, int userId, int groupId, int repId) throws Exception {
		IVanillaAPI vanillaApi = getRootVanillaApi();
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(validation.getItemId());
		
		boolean hasValidate = false;
		GatewayRuntimeState state = null;
		if (validation.getEndEtlId() > 0) {
			state = launchEndEtl(validation.getEndEtlId(), repId, groupId);
		}
		else {
			hasValidate = true;
		}

		if (hasValidate || (state != null && state.getState() == ActivityState.ENDED)) {
			List<CommentDefinition> commentDefinitions = repositoryApi.getRepositoryService().getCommentDefinitions(validation.getItemId());
			if (commentDefinitions != null) {
				for (CommentDefinition comDef : commentDefinitions) {
					CommentValue comment = repositoryApi.getRepositoryService().getCommentNotValidate(comDef.getId());
					if (comment != null) {
						comment.setStatus(CommentStatus.VALIDATE);
						repositoryApi.getRepositoryService().addOrUpdateCommentValue(comment);
					}
				}
			}

			validation.setValid(true);
			validation.setOffline(false);
			repositoryApi.getRepositoryService().addOrUpdateValidation(validation);

			ValidationMail.sendFinalMail(vanillaApi, validation, item);
		}
		else {
			ValidationMail.sendMailErrorToAdmin(vanillaApi, validation, item, false);
		}
	}

	@Override
	public void unvalidate(Validation validation, int userId, int groupId, int repId) throws Exception {
		IVanillaAPI vanillaApi = getRootVanillaApi();
		IRepositoryApi repositoryApi = getRootRepositoryApi(repId);
		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(validation.getItemId());
		
		User firstCommentator = getFirstCommentator(vanillaApi, validation);
		
		List<CommentDefinition> commentDefinitions = repositoryApi.getRepositoryService().getCommentDefinitions(validation.getItemId());
		if (commentDefinitions != null) {
			for (CommentDefinition comDef : commentDefinitions) {
				CommentValue comment = repositoryApi.getRepositoryService().getCommentNotValidate(comDef.getId());
				if (comment != null) {
					
					CommentValue newComment = getCommentValueForFirstCommentator(firstCommentator, comment);
					repositoryApi.getRepositoryService().addOrUpdateCommentValue(newComment);
					
					comment.setStatus(CommentStatus.OLD);
					repositoryApi.getRepositoryService().addOrUpdateCommentValue(comment);
				}
			}
		}
		
		ValidationMail.sendMailToNextCommentator(vanillaApi, validation, item, null, true);
	}
	
	@Override
	public void stopValidationProcess(Validation validation, int userId, int groupId, int repId) throws Exception {
		//TODO: Stop process (validate ? invalidate ? remove validation ?)
	}

	private User getFirstCommentator(IVanillaAPI vanillaApi, Validation validation) throws Exception {
		for (UserValidation commentator : validation.getCommentators()) {
			return vanillaApi.getVanillaSecurityManager().getUserById(commentator.getUserId());
		}
		return null;
	}

	private CommentValue getCommentValueForFirstCommentator(User user, CommentValue value) {
		// Create a new comment for the first commentator
		CommentValue newComment = new CommentValue();
		newComment.setCommentId(value.getCommentId());
		newComment.setCreationDate(new Date());
		newComment.setStatus(CommentStatus.UNVALIDATE);
		newComment.setUserId(user.getId());
		newComment.setUserName(user.getName());
		newComment.setValue(value.getValue());

		if (value.getParameterValues() != null) {
			List<CommentValueParameter> parameters = new ArrayList<CommentValueParameter>();
			for (CommentValueParameter param : value.getParameterValues()) {
				CommentValueParameter newParam = new CommentValueParameter();
				newParam.setCommentParamId(param.getCommentParamId());
				newParam.setCommentValueId(param.getCommentValueId());
				newParam.setValue(param.getValue());
				
				parameters.add(newParam);
			}
			newComment.setParameterValues(parameters);
		}

		return newComment;
	}

	private GatewayRuntimeState launchEndEtl(int endEtlId, int repId, int groupId) throws Exception {
		IObjectIdentifier identifier = new ObjectIdentifier(repId, endEtlId);

		AlertToGTW alertGtw = new AlertToGTW(getRootRepositoryContext(repId, groupId), identifier, new ArrayList<VanillaGroupParameter>());
		return alertGtw.runGTW(false);
	}

	private IVanillaAPI getRootVanillaApi() {
		return new RemoteVanillaPlatform(rootVanillaCtx);
	}

	private IRepositoryApi getRootRepositoryApi(int repId) throws Exception {
		return new RemoteRepositoryApi(getRootRepositoryContext(repId, null));
	}

	private IRepositoryContext getRootRepositoryContext(int repId, Integer groupId) {
		Group dummyGroup = new Group();
		dummyGroup.setId(groupId != null ? groupId : -1);

		Repository rep = new Repository();
		rep.setId(repId);

		return new BaseRepositoryContext(rootVanillaCtx, dummyGroup, rep);
	}

	private IVanillaContext getRootVanillaContext() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String vanillaUrl = config.getVanillaServerUrl();
		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanillaUrl, login, password);
	}
}
