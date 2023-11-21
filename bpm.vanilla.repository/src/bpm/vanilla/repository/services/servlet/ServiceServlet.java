package bpm.vanilla.repository.services.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.ItemInstance;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.alerts.Action;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.Condition;
import bpm.vanilla.platform.core.beans.alerts.Smtp;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.MetaLink;
import bpm.vanilla.platform.core.beans.meta.MetaValue;
import bpm.vanilla.platform.core.beans.meta.MetaLink.TypeMetaLink;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DatasProvider;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.RepositoryLog;
import bpm.vanilla.platform.core.repository.Revision;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;
import bpm.vanilla.platform.core.repository.services.IDataProviderService;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IMetaService;
import bpm.vanilla.platform.core.repository.services.IModelVersionningService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAlertService;
import bpm.vanilla.platform.core.repository.services.IRepositoryImpactService;
import bpm.vanilla.platform.core.repository.services.IRepositoryLogService;
import bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService;
import bpm.vanilla.platform.core.repository.services.IRepositoryService.ActionType;
import bpm.vanilla.platform.core.repository.services.IWatchListService;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.repository.beans.RepositoryRuntimeComponent;
import bpm.vanilla.repository.services.ServiceBrowseImpl;

public class ServiceServlet extends AbstractRepositoryServlet {

	private static final long serialVersionUID = 1L;

	public ServiceServlet(RepositoryRuntimeComponent component) {
		super(component);
	}

	protected String readByteArrayString(byte[] encodedBytes) throws Exception {
		byte[] decoded = Base64.decodeBase64(encodedBytes);
		return new String(decoded, "UTF-8");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction) xstream.fromXML(req.getInputStream());

			XmlArgumentsHolder args = action.getArguments();

			Object actionResult = null;

			User user = extractUser(req);
			int groupId = -1;
			try {
				groupId = extractGroupId(req);
			} catch (Exception ex) {
				if (user.isSuperUser()) {
					groupId = -1;
				}
				else {
					throw new VanillaException("The user is not super user");
				}
			}
			int repositoryId = -1;
			try {
				repositoryId = extractRepositoryId(req);
			} catch (Exception ex) {
				throw new VanillaException("No repository Id in the http request header");
			}

			actionResult = dispatchHttpCall(action, args, user, groupId, repositoryId, req.getRemoteHost(), resp);

			if (actionResult != null) {
				xstream.toXML(actionResult, resp.getWriter());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
			xstream.toXML(new VanillaException("ServerSide failure : " + ex.getMessage()), resp.getWriter());
		}
	}

	private Object dispatchHttpCall(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp, HttpServletResponse resp) throws Exception {
		Object actionResult = null;
		if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IDataProviderService.ActionType) {
			actionResult = callServiceDataProvider(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IModelVersionningService.ActionType) {
			actionResult = callServiceVersionning(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IRepositoryAlertService.ActionType) {
			actionResult = callServiceAlert(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IDocumentationService.ActionType) {
			actionResult = callServiceDocumentation(action, args, user, groupId, repositoryId, clientIp, resp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IRepositoryAdminService.ActionType) {
			actionResult = callServiceAdmin(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IRepositoryImpactService.ActionType) {
			actionResult = callServiceImpact(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IRepositoryLogService.ActionType) {
			actionResult = callServiceLog(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService.ActionType) {
			actionResult = callServiceReportHistoric(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IRepositoryService.ActionType) {
			actionResult = callServiceBrowse(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof bpm.vanilla.platform.core.repository.services.IWatchListService.ActionType) {
			actionResult = callServiceWatchList(action, args, user, groupId, repositoryId, clientIp);
		}
		else if (action.getActionType() instanceof IMetaService.ActionType) {
			actionResult = callServiceMeta(action, args, user, groupId, repositoryId, clientIp);
		}
		return actionResult;
	}

	private Object callServiceDataProvider(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IDataProviderService.ActionType type = (bpm.vanilla.platform.core.repository.services.IDataProviderService.ActionType) action.getActionType();

			IDataProviderService service = component.getServiceDataprovider(repositoryId, groupId, user, clientIp);
			switch (type) {
			case BREAK_LINK:
				service.breakLink((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case CREATE_PROVIDER:
				actionResult = service.createDatasProvider((DatasProvider) args.getArguments().get(0));
				break;
			case DELETE_PROVIDER:
				service.delete((DatasProvider) args.getArguments().get(0));
				break;
			case FIND_ITEM_PROVIDERS:
				actionResult = service.getForItem((Integer) args.getArguments().get(0));
				break;
			case FIND_PROVIDER:
				actionResult = service.getDatasProvider((Integer) args.getArguments().get(0));
				break;
			case LINK:
				actionResult = service.link((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case LIST_PROVIDER:
				actionResult = service.getAll();
				break;
			case UPDATE:
				service.update((DatasProvider) args.getArguments().get(0));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceVersionning(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IModelVersionningService.ActionType type = (bpm.vanilla.platform.core.repository.services.IModelVersionningService.ActionType) action.getActionType();

			IModelVersionningService service = component.getServiceVersionning(repositoryId, groupId, user, clientIp);
			switch (type) {
			case CHECK_IN:
				service.checkIn((RepositoryItem) args.getArguments().get(0), (String) args.getArguments().get(1), new ByteArrayInputStream(Base64.decodeBase64((byte[]) args.getArguments().get(2))));
				break;
			case CHECK_OUT:
				actionResult = service.checkOut((RepositoryItem) args.getArguments().get(0));
				break;
			case LIST_REVISIONS:
				actionResult = service.getRevisions((RepositoryItem) args.getArguments().get(0));
				break;
			case REVERT:
				service.revertToRevision((RepositoryItem) args.getArguments().get(0), (Integer) args.getArguments().get(1), (String) args.getArguments().get(2));
				break;
			case REVISION_MODEL:
				actionResult = service.getRevision((RepositoryItem) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case SHARE:
				service.share((RepositoryItem) args.getArguments().get(0));
				break;
			case UNLOCK:
				actionResult = service.unlock((RepositoryItem) args.getArguments().get(0));
				break;
			case UPDATE:
				service.updateRevision((Revision) args.getArguments().get(0));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceAlert(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IRepositoryAlertService.ActionType type = (bpm.vanilla.platform.core.repository.services.IRepositoryAlertService.ActionType) action.getActionType();

			IRepositoryAlertService service = component.getServiceAlert(repositoryId, groupId, user, clientIp);

			switch (type) {
			case ADD_ALERT:
				actionResult = service.createAlert((Alert) args.getArguments().get(0));
				break;
			case ADD_SMTP:
				actionResult = service.createSmtpHost((Smtp) args.getArguments().get(0));
				break;
			case DEL_ACTION:
				service.removeAction((Action) args.getArguments().get(0));
				break;
			case DEL_ALERT:
				service.removeAlert((Alert) args.getArguments().get(0));
				break;
			case DEL_COND:
				service.removeCondition((Condition) args.getArguments().get(0));
				break;
			case DEL_SMTP:
				service.removeSmtpHost((Smtp) args.getArguments().get(0));
				break;
			case DEL_SUBS:
				service.removeSubscriber((Subscriber) args.getArguments().get(0));
				break;
			case GET_ALERT_BY_DIRECTORY_ITEM:
				actionResult = service.getAlertsByTypeAndDirectoryItemId((TypeEvent) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			case GET_ALERT_BY_TYPE:
				actionResult = service.getAlertsByType((TypeEvent) args.getArguments().get(0));
				break;
			case GET_ALERTS:
				actionResult = service.getAlerts();
				break;
			case GET_SMTP:
				actionResult = service.getListSmtp();
				break;
			case UPDATE_ALERT:
				service.updateAlert((Alert) args.getArguments().get(0));
				break;
			case UPDATE_SMTP:
				service.updateSmtpHost((Smtp) args.getArguments().get(0));
				break;
			case GET_ALERT:
				actionResult = service.getAlert((Integer) args.getArguments().get(0));
				break;
			case GET_ALERTS_WHITOUT_EVENT:
				actionResult = service.getAlertsWhitoutEvent();
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	@SuppressWarnings("unchecked")
	private Object callServiceDocumentation(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp, HttpServletResponse resp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IDocumentationService.ActionType type = (bpm.vanilla.platform.core.repository.services.IDocumentationService.ActionType) action.getActionType();

			IDocumentationService service = component.getServiceDocumentation(repositoryId, groupId, user, clientIp);

			switch (type) {
			case ADD_COMMENT:
				service.addOrUpdateComment((Comment) args.getArguments().get(0), (List<Integer>) args.getArguments().get(1));
				break;
			case DELETE_COMMENT:
				service.delete((Comment) args.getArguments().get(0));
				break;
			case GET_COMMENTS:
				actionResult = service.getComments((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			case LINK_DOC_TO_ITEM:
				actionResult = service.attachDocumentToItem((RepositoryItem) args.getArguments().get(0), new ByteArrayInputStream(Base64.decodeBase64((byte[]) args.getArguments().get(1))), (String) args.getArguments().get(2), (String) args.getArguments().get(3), (String) args.getArguments().get(4), (String) args.getArguments().get(5));
				break;
			case LIST_REPORT_HISTO_DOC_IDS:
				actionResult = service.getReportHistoricDocumentsId((RepositoryItem) args.getArguments().get(0));
				break;
			case LOAD_EXT_DOC:
				actionResult = service.importExternalDocument((RepositoryItem) args.getArguments().get(0));
				break;
			case LOAD_LINKED_DOC:
				actionResult = service.importLinkedDocument((Integer) args.getArguments().get(0));
				break;
			case MAP_REPORT_TO_DOC:
				service.mapReportItemToReportDocument((RepositoryItem) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Boolean) args.getArguments().get(2));
				break;
			case UPDATE_EXT_DOC:
				ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64((byte[]) args.getArguments().get(1)));
				service.updateExternalDocument((RepositoryItem) args.getArguments().get(0), bis);
				break;
			case ADD_SEC_COMMENT_OBJECT:
				service.addSecuredCommentObject((SecuredCommentObject) args.getArguments().get(0));
				break;
			case ADD_SEC_COMMENT_OBJECTS:
				service.addSecuredCommentObjects((List<SecuredCommentObject>) args.getArguments().get(0));
				break;
			case DELETE_COMMENTS:
				service.deleteComments((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case GET_SEC_COMMENT_OBJECTS:
				actionResult = service.getSecuredCommentObjects((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case REMOVE_SEC_COMMENT_OBJECT:
				service.removeSecuredCommentObject((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			case REMOVE_SEC_COMMENT_OBJECTS:
				service.removeSecuredCommentObjects((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case CAN_COMMENT:
				actionResult = service.canComment((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceAdmin(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IRepositoryAdminService.ActionType type = (bpm.vanilla.platform.core.repository.services.IRepositoryAdminService.ActionType) action.getActionType();

			IRepositoryAdminService service = component.getServiceAdmin(repositoryId, groupId, user);

			switch (type) {
			case ALLOW_RUN:
				service.setObjectRunnableForGroup((Integer) args.getArguments().get(0), (RepositoryItem) args.getArguments().get(1));
				break;
			case CAN_ACCESS_DIR:
				actionResult = service.isDirectoryAccessible((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case CAN_ACCESS_IT:
				actionResult = service.isDirectoryItemAccessible((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case CAN_RUN:
				actionResult = service.canRun((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case DELETE_LINKED_DOC:
				service.removeLinkedDocument((LinkedDocument) args.getArguments().get(0));
				break;
			case FORBID_RUN:
				service.unsetObjectRunnableForGroup((Group) args.getArguments().get(0), (RepositoryItem) args.getArguments().get(1));
				break;
			case GET_GENERATED_RELATIVE_URL:
				actionResult = service.getGeneratedRelativeUrlFor((RepositoryItem) args.getArguments().get(0), (Group) args.getArguments().get(1));
				break;
			case LIST_DELETED_DIRECTORIES:
				actionResult = service.getDeletedDirectories();
				break;
			case LIST_DELETED_ITEMS:
				actionResult = service.getDeletedItems();
				break;
			case LIST_DIRECTORY_GROUPS:
				actionResult = service.getGroupsForDirectory((RepositoryDirectory) args.getArguments().get(0));
				break;
			case LIST_ITEM_ALLOWED_GROUPS:
				actionResult = service.getAllowedGroupId((RepositoryItem) args.getArguments().get(0));
				break;
			case LIST_ITEM_GROUPS:
				actionResult = service.getGroupsForItemId((Integer) args.getArguments().get(0));
				break;
			case LIST_ITEM_LINKED_DOCS:
				actionResult = service.getLinkedDocuments((Integer) args.getArguments().get(0));
				break;
			case LIST_LINKED_GROUPS:
				actionResult = service.getAuthorizedGroupsForLinkedDocument((Integer) args.getArguments().get(0));
				break;
			case LIST_SECURED_DIRS:
				actionResult = service.getSecuredDirectoriesForGroup((Group) args.getArguments().get(0));
				break;
			case LIST_SECURED_ITEMS:
				actionResult = service.getSecuredObjectForGroup((Group) args.getArguments().get(0));
				break;
			case PURGE_ALL:
				actionResult = service.purgeAllDeletedObjects();
				break;
			case PURGE_HISTORIC:
				service.purgeHistoric();
				break;
			case PURGE_OBJECTS:
				actionResult = service.purgeDeletedObjects((List<RepositoryDirectory>) args.getArguments().get(0), (List<RepositoryItem>) args.getArguments().get(1));
				break;
			case RESTORE_DIRECTORY:
				actionResult = service.restoreDirectory((Integer) args.getArguments().get(0));
				break;
			case RESTORE_ITEM:
				actionResult = service.restoreDirectoryItem((Integer) args.getArguments().get(0));
				break;
			case SECUR_DIRECTORY:
				service.addGroupForDirectory((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case SECUR_ITEM:
				service.addGroupForItem((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case SECUR_LINKED:
				service.addGroupForLinkedDocument((Group) args.getArguments().get(0), (LinkedDocument) args.getArguments().get(1));
				break;
			case UNSECUR_DIR:
				service.removeGroupForDirectory((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case UNSECUR_ITEM:
				service.removeGroupForItem((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case UNSECUR_LINKED_DOC:
				service.removeGroupForLinkedDocument((Group) args.getArguments().get(0), (LinkedDocument) args.getArguments().get(1));
				break;
			case UPDATE_ITEM:
				service.update((RepositoryItem) args.getArguments().get(0));
				break;
			case UPDATE_PARAMETER:
				service.updateParameter((Parameter) args.getArguments().get(0));
				break;
			case SECUR_ELEMENTS:
				service.setSecurityForElements((List<IRepositoryObject>) args.getArguments().get(0), (List<Group>) args.getArguments().get(1), (List<Group>) args.getArguments().get(2), (List<Group>) args.getArguments().get(3), (List<Group>) args.getArguments().get(4));
				break;
			case REMOVE_GROUP:
				service.removeGroup((Group) args.getArguments().get(0));
				break;
			case ADD_ITEM_INSTANCE:
				actionResult = service.addItemInstance((ItemInstance) args.getArguments().get(0));
				break;
			case GET_ITEM_INSTANCES:
				actionResult = service.getItemInstances((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			case GET_ITEM_INSTANCES_FOR_ITEM:
				actionResult = service.getItemInstances((Integer) args.getArguments().get(0), (Date) args.getArguments().get(1), (Date) args.getArguments().get(2), (Integer) args.getArguments().get(3));
				break;
			case GET_ITEM_INSTANCE:
				actionResult = service.getItemInstance((Integer) args.getArguments().get(0));
				break;
			case REMOVE_ITEM_INSTANCE:
				service.removeItemInstances((Integer) args.getArguments().get(0));
				break;
			case GET_TOP_TEN:
				actionResult = service.getTopTenItemConsumer((Integer) args.getArguments().get(0), (Date) args.getArguments().get(1), (Date) args.getArguments().get(2), (Integer) args.getArguments().get(3));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceImpact(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IRepositoryImpactService.ActionType type = (bpm.vanilla.platform.core.repository.services.IRepositoryImpactService.ActionType) action.getActionType();

			IRepositoryImpactService service = component.getServiceImpact(repositoryId, groupId, user, clientIp);

			switch (type) {
			case CREATE_DATASOURCE:
				actionResult = service.add((DataSource) args.getArguments().get(0));
				break;
			case DELETE_DATASOURCE:
				actionResult = service.del((DataSource) args.getArguments().get(0));
				break;
			case FIND_DATASOURCE:
				actionResult = service.getById((Integer) args.getArguments().get(0));
				break;
			case GET_DATASOURCE_IMPACTS:
				actionResult = service.getForDataSourceId((Integer) args.getArguments().get(0));
				break;
			case LIST_DATASOURCE:
				actionResult = service.getAllDatasources();
				break;
			case UPDATE_DATASOURCE:
				service.update((DataSource) args.getArguments().get(0));
				break;
			case FIND_DATASOURCE_BY_TYPE:
				actionResult = service.getDatasourcesByType((String) args.getArguments().get(0));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceLog(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IRepositoryLogService.ActionType type = (bpm.vanilla.platform.core.repository.services.IRepositoryLogService.ActionType) action.getActionType();

			IRepositoryLogService service = component.getServiceLog(repositoryId, groupId, user, clientIp);

			switch (type) {
			case ADD_LOG:
				actionResult = service.addLog((RepositoryLog) args.getArguments().get(0));
				break;
			case DEL_LOG:
				service.delReportModel((RepositoryLog) args.getArguments().get(0));
				break;
			case FIND_LOG:
				actionResult = service.getById((Integer) args.getArguments().get(0));
				break;
			case LIST_LOG:
				actionResult = service.getLogs();
				break;
			case LIST_LOG_FOR_ITEM:
				actionResult = service.getLogsFor((RepositoryItem) args.getArguments().get(0));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceReportHistoric(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService.ActionType type = (bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService.ActionType) action.getActionType();

			IRepositoryReportHistoricService service = component.getServiceReportHistoric(repositoryId, groupId, user, clientIp);

			switch (type) {
			case CREATE_ACCESS:
				service.createHistoricAccess((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case DEL_HISTO_ENTRY:
				service.deleteHistoricEntry((Integer) args.getArguments().get(0));
				break;
			case LIST_DOCS_4_GROUP:
				actionResult = service.getHistorizedDocumentIdForGroup((Integer) args.getArguments().get(0));
				break;
			case LIST_DOCS_4_ITEM:
				actionResult = service.getHistorizedDocumentIdFor((Integer) args.getArguments().get(0));
				break;
			case LIST_DOCS_4_ITEM_GROUP:
				actionResult = service.getHistorizedDocumentIdFor((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case LIST_GRANTED_GROUPS_ID:
				actionResult = service.getAuthorizedGroupId((Integer) args.getArguments().get(0));
				break;
			case REMOVE_ACCESS:
				service.removeHistoricAccess((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case ADD_OR_UPDATE_REPORT_BACKGROUND:
				service.addOrUpdateReportBackground((ReportBackground) args.getArguments().get(0));
				break;
			case DEL_REPORT_BACKGROUND:
				service.deleteReportBackground((ReportBackground) args.getArguments().get(0));
				break;
			case GET_REPORT_BACKGROUNDS:
				actionResult = service.getReportBackgrounds((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case GET_REPORT_BACKGROUND:
				actionResult = service.getReportBackground((Integer) args.getArguments().get(0));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceWatchList(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			bpm.vanilla.platform.core.repository.services.IWatchListService.ActionType type = (bpm.vanilla.platform.core.repository.services.IWatchListService.ActionType) action.getActionType();

			IWatchListService service = component.getServiceWatchList(repositoryId, groupId, user, clientIp);

			switch (type) {
			case ADD_TO_WATCHLIST:
				service.addToWatchList((RepositoryItem) args.getArguments().get(0));
				break;
			case GET_LAST_CONSULTED:
				actionResult = service.getLastConsulted();
				break;
			case GET_WATCH_LIST:
				actionResult = service.getWatchList();
				break;
			case REMOVE_FROM_WATCHLIST:
				service.removeFromWatchList((RepositoryItem) args.getArguments().get(0));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private Object callServiceBrowse(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			ActionType type = (ActionType) action.getActionType();

			ServiceBrowseImpl serviceBrowse = component.getServiceBrowse(repositoryId, groupId, user, clientIp);

			switch (type) {
			case CREATE_DIRECTORY:
				actionResult = serviceBrowse.addDirectory((String) args.getArguments().get(0), (String) args.getArguments().get(1), (RepositoryDirectory) args.getArguments().get(2));
				break;
			case CREATE_ITEM:
				actionResult = serviceBrowse.addDirectoryItemWithDisplay((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (RepositoryDirectory) args.getArguments().get(2), (String) args.getArguments().get(3), (String) args.getArguments().get(4), (String) args.getArguments().get(5), (String) args.getArguments().get(6), readByteArrayString((byte[]) args.getArguments().get(7)), (Boolean) args.getArguments().get(8));
				break;
			case CREATE_EXT_DOC_ITEM:
				actionResult = serviceBrowse.addDirectoryItemWithDisplay(IRepositoryApi.EXTERNAL_DOCUMENT, -1, (RepositoryDirectory) args.getArguments().get(0), (String) args.getArguments().get(1), (String) args.getArguments().get(2), (String) args.getArguments().get(3), (String) args.getArguments().get(4), createExtDocItemXml(user, (byte[]) args.getArguments().get(5), (String) args.getArguments().get(1), (String) args.getArguments().get(7)), (Boolean) args.getArguments().get(6), (String) args.getArguments().get(7));
				break;
			case DELETE_DIRECTORY:
				serviceBrowse.delete((RepositoryDirectory) args.getArguments().get(0));
				break;
			case DELETE_ITEM:
				serviceBrowse.delete((RepositoryItem) args.getArguments().get(0));
				break;
			case LIST_CUBE_NAMES:
				actionResult = serviceBrowse.getCubeNames((RepositoryItem) args.getArguments().get(0));
				break;
			case LIST_CUBE_VIEWS:
				actionResult = serviceBrowse.getCubeViews((String) args.getArguments().get(0), (RepositoryItem) args.getArguments().get(1));
				break;
			case LIST_FMDT_DRILLER:
				actionResult = serviceBrowse.getFmdtDrillers((RepositoryItem) args.getArguments().get(0));
				break;
			case DEPENDANT_ITEMS:
				actionResult = serviceBrowse.getDependantItems((RepositoryItem) args.getArguments().get(0));
				break;
			case REQUESTED_ITEMS:
				actionResult = serviceBrowse.getNeededItems((Integer) args.getArguments().get(0));
				break;
			case FIND_DIRECTORY:
				actionResult = serviceBrowse.getDirectory((Integer) args.getArguments().get(0));
				break;
			case LIST_LINKED_DOCUMENT:
				actionResult = serviceBrowse.getLinkedDocumentsForGroup((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case FIND_DIRECTORY_ITEM:
				actionResult = serviceBrowse.getDirectoryItem((Integer) args.getArguments().get(0));
				break;
			case ITEM_PARAMETERS:
				actionResult = serviceBrowse.getParameters((RepositoryItem) args.getArguments().get(0));
				break;
			case DIRECTORY_CONTENT:
				actionResult = serviceBrowse.getDirectoryContent(args.getArguments().get(0) == null ? null : (RepositoryDirectory) args.getArguments().get(0), (Integer) args.getArguments().get(1));
				break;
			case IMPORT_MODEL:
				actionResult = serviceBrowse.loadModel((RepositoryItem) args.getArguments().get(0));
				actionResult = Base64.encodeBase64(((String) actionResult).getBytes("UTF-8"));
				break;
			case UPDATE_DIRECTORY:
				serviceBrowse.update((RepositoryDirectory) args.getArguments().get(0));
				break;
			case UPDATE_MODEL_DEFINITION:
				serviceBrowse.updateModel((RepositoryItem) args.getArguments().get(0), readByteArrayString((byte[]) args.getArguments().get(1)));
				break;
			case CHECK_ITEM_UPATE:
				actionResult = serviceBrowse.checkItemUpdate((RepositoryItem) args.getArguments().get(0), (Date) args.getArguments().get(1));
				break;
			case LIST_CUBE_VIEWS_WITH_IMAGES:
				actionResult = serviceBrowse.getCubeViewsWithImageBytes((String) args.getArguments().get(0), (RepositoryItem) args.getArguments().get(1));
				break;
			case CREATE_DISCONNECTED_PACKAGE:
				actionResult = serviceBrowse.createDisconnectedPackage((String) args.getArguments().get(0), (Integer) args.getArguments().get(1), (List<RepositoryItem>) args.getArguments().get(2));
				break;
			case GET_IMPACT_GRAPH:
				actionResult = serviceBrowse.getImpactGraph((Integer) args.getArguments().get(0));
				break;
			case SEARCH_ITEMS:
				actionResult = serviceBrowse.getItems((String) args.getArguments().get(0));
				break;
			case ADD_OR_UPDATE_VALIDATION:
				actionResult = serviceBrowse.addOrUpdateValidation((Validation) args.getArguments().get(0));
				break;
			case ADD_OR_UPDATE_COMMENT_DEFINITION:
				actionResult = serviceBrowse.addOrUpdateCommentDefinition((CommentDefinition) args.getArguments().get(0));
				break;
			case ADD_OR_UPDATE_COMMENT_VALUE:
				actionResult = serviceBrowse.addOrUpdateCommentValue((CommentValue) args.getArguments().get(0));
				break;
			case DELETE_COMMENT_DEFINITION:
				serviceBrowse.deleteCommentDefinition((CommentDefinition) args.getArguments().get(0));
				break;
			case DELETE_COMMENT_VALUE:
				serviceBrowse.deleteCommentValue((CommentValue) args.getArguments().get(0));
				break;
			case GET_VALIDATIONS:
				actionResult = serviceBrowse.getValidations((Boolean) args.getArguments().get(0));
				break;
			case GET_VALIDATION:
				actionResult = serviceBrowse.getValidation((Integer) args.getArguments().get(0));
				break;
			case GET_VALIDATION_BY_START_ETL:
				actionResult = serviceBrowse.getValidationByStartEtl((Integer) args.getArguments().get(0));
				break;
			case GET_COMMENT_DEFINITION:
				actionResult = serviceBrowse.getCommentDefinition((Integer) args.getArguments().get(0), (String) args.getArguments().get(1));
				break;
			case GET_COMMENT_NOT_VALIDATE:
				actionResult = serviceBrowse.getCommentNotValidate((Integer) args.getArguments().get(0));
				break;
			case GET_COMMENTS:
				actionResult = serviceBrowse.getComments((Integer) args.getArguments().get(0), (String) args.getArguments().get(1), (List<CommentParameter>) args.getArguments().get(2));
				break;
			case GET_COMMENTS_DEFINITION:
				actionResult = serviceBrowse.getCommentDefinitions((Integer) args.getArguments().get(0));
				break;
			case GET_COMMENTS_FOR_USER:
				actionResult = serviceBrowse.getComments((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2));
				break;
			case ADD_SHARED_FILE:
				serviceBrowse.addSharedFile((String) args.getArguments().get(0), new ByteArrayInputStream(Base64.decodeBase64((byte[]) args.getArguments().get(1))));
				break;
			case GET_ITEMS:
				actionResult = serviceBrowse.getItems((List<Integer>) args.getArguments().get(0));
				break;
			case ADD_TEMPLATE:
				serviceBrowse.addTemplate((Template<?>) args.getArguments().get(0));
				break;
			case DELETE_TEMPLATE:
				serviceBrowse.deleteTemplate((Template<?>) args.getArguments().get(0));
				break;
			case GET_TEMPLATE:
				actionResult = serviceBrowse.getTemplate((Integer) args.getArguments().get(0));
				break;
			case GET_TEMPLATES:
				actionResult = serviceBrowse.getTemplates((Boolean) args.getArguments().get(0), (TypeTemplate) args.getArguments().get(1));
				break;
			case ADD_ITEM_METADATA_LINK:
				serviceBrowse.addItemMetadataTableLink((ItemMetadataTableLink) args.getArguments().get(0));
				break;
			case DELETE_ITEM_METADATA_LINK:
				serviceBrowse.deleteItemMetadataTableLink((ItemMetadataTableLink) args.getArguments().get(0));
				break;
			case GET_METADATA_LINKS:
				actionResult = serviceBrowse.getMetadataLinks((Integer) args.getArguments().get(0));
				break;
			case GET_PENDING_ITEMS_TO_COMMENT:
				actionResult = serviceBrowse.getPendingItemsToComment((Integer) args.getArguments().get(0));
				break;
			case GET_VALIDATION_CIRCUITS:
				actionResult = serviceBrowse.getValidationCircuits();
				break;
			case MANAGE_VALIDATION_CIRCUIT:
				actionResult = serviceBrowse.manageValidationCircuit((ValidationCircuit) args.getArguments().get(0), (ManageAction) args.getArguments().get(1));
				break;
			case UPDATE_NEXT_USER_VALIDATION:
				serviceBrowse.updateUserValidation((Integer) args.getArguments().get(0), (Integer) args.getArguments().get(1), (Integer) args.getArguments().get(2), (UserValidationType) args.getArguments().get(3));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	@SuppressWarnings("unchecked")
	private Object callServiceMeta(XmlAction action, XmlArgumentsHolder args, User user, int groupId, int repositoryId, String clientIp) throws Exception {
		Object actionResult;
		try {
			actionResult = null;
			IMetaService.ActionType type = (IMetaService.ActionType) action.getActionType();

			IMetaService service = component.getServiceMeta(repositoryId, groupId, user, clientIp);

			switch (type) {
			case GET_META_BY_FORM:
				actionResult = service.getMetaByForm((Integer) args.getArguments().get(0));
				break;
			case GET_META_LINKS:
				actionResult = service.getMetaLinks((Integer) args.getArguments().get(0), (TypeMetaLink) args.getArguments().get(1), (Boolean) args.getArguments().get(2));
				break;
			case MANAGE_VALUES:
				service.manageMetaValues((List<MetaLink>) args.getArguments().get(0), (ManageAction) args.getArguments().get(1));
				break;
			case GET_ITEMS_BY_META:
				actionResult = service.getItemsByMeta((TypeMetaLink) args.getArguments().get(0), (List<MetaValue>) args.getArguments().get(1));
				break;
			case GET_FORMS:
				actionResult = service.getMetaForms();
				break;
			case GET_META_BY_KEY:
				actionResult = service.getMeta((String) args.getArguments().get(0));
				break;
			case MANAGE_META:
				actionResult = service.manageMeta((Meta) args.getArguments().get(0), (ManageAction) args.getArguments().get(1));
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return actionResult;
	}

	private String createExtDocItemXml(User user, byte[] raw64, String name, String format) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(raw64));

		String fName = "ext_" + new Object().hashCode() + name + "." + format;
		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
		String relPatth = "/external_documents/" + fName;

		File file = new File(basePath + "/" + relPatth);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		IOWriter.write(bis, fileOutputStream, true, true);

		Element e = DocumentHelper.createElement("extdocument");
		e.addElement("author").setText(user.getLogin());
		e.addElement("version").setText("1");
		e.addElement("name").setText(name);
		if (file.getAbsolutePath().contains(".")) {
			format = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf('.')).toLowerCase();
		}
		e.addElement("type").setText(format);
		e.addElement("path").setText(relPatth);

		return e.asXML();

	}
}
