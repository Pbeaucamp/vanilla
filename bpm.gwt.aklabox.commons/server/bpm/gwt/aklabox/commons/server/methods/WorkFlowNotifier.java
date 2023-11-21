package bpm.gwt.aklabox.commons.server.methods;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.KeywordResults;
import bpm.document.management.core.model.Keywords;
import bpm.document.management.core.model.Notifications;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.WorkFlow;
import bpm.document.management.core.model.WorkFlowDocumentCondition;
import bpm.document.management.core.model.WorkFlowEvents;
import bpm.document.management.core.model.WorkFlowFolderCondition;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.gwt.aklabox.commons.server.i18n.Labels;
import bpm.gwt.aklabox.commons.server.security.CommonSession;

//This class is moving to Runtime
//Edit: not sure ! because of send Emails add
@Deprecated
public class WorkFlowNotifier {
	private IObject object;
	private CommonSession session;

	public WorkFlowNotifier(CommonSession session, IObject object) {
		this.object = object;
		this.session = session;
	}

	public void sendDocCondition(String email, boolean isAlertOne, boolean isAlertTwo, boolean isAlertThree, boolean isAlertFour, boolean isAlertFive) throws Exception {
		User workflowOwner = session.getAklaboxService().getUserInfo(email);
		for (WorkFlow e : session.getAklaboxService().getAllWorkFlow(((Documents) object).getId(), true)) {
			if (e.getUserId() != workflowOwner.getUserId()) {
				WorkFlow w = session.getAklaboxService().getSpecificWorkFlowEachUser(e.getUserId(), object.getId(), true);
				WorkFlowDocumentCondition wc = session.getAklaboxService().getDocCondition(w.getWorkFlowId());
				WorkFlowEvents event = new WorkFlowEvents();
				event.setWorkFlowId(session.getAklaboxService().getSpecificWorkFlowEachUser(e.getUserId(), object.getId(), true).getWorkFlowId());
				event.setWorkFlowEventDate(new Date());
				event.setReceiver(e.getUserId());
				event.setSender(workflowOwner.getUserId());
				Notifications n = new Notifications();
				String message = "";
				n.setDocId(object.getId());
				n.setFileType(AklaboxConstant.DOCUMENT);
				n.setIsTrigger(true);
				n.setNotificationDate(new Date());
				n.setNotifiedBy(session.getAklaboxService().getUserInfo(email).getUserId());
				n.setUserId(e.getUserId());

				if (isAlertOne && wc.isAlertOne()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.ViewedTheDocument) + " " + object.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					session.getAklaboxService().saveNotification(n);
				}
				else if (isAlertTwo && wc.isAlertTwo()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.DownloadedTheDocument) + " " + object.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					session.getAklaboxService().saveNotification(n);
				}
				else if (isAlertThree && wc.isAlertThree()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.UpdatedANewVersionOfDocument) + " " + object.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					session.getAklaboxService().saveNotification(n);
				}
				else if (isAlertFour && wc.isAlertFour()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.PlacedACommentOnTheDocument) + " " + object.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					session.getAklaboxService().saveNotification(n);
				}
				else if (isAlertFive && wc.isAlertFive()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.DeletedTheDocument) + " " + object.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					session.getAklaboxService().saveNotification(n);
				}
			}
		}
	}

	public void sendFolderCondition(Tree tree, String email, boolean isAlertOne, boolean isAlertTwo, boolean isAlertThree, boolean isAlertFour, boolean isAlertFive, boolean isAlertFacture, HttpServletRequest request, String message) throws Exception {
		User workflowOwner = session.getAklaboxService().getUserInfo(email);
		for (WorkFlow e : session.getAklaboxService().getAllWorkFlow(tree.getId(), false)) {
			if (e.getUserId() != workflowOwner.getUserId()) {
				User receiver = session.getAklaboxService().getUserInfoThroughId(e.getUserId()+"");
				WorkFlow w = session.getAklaboxService().getSpecificWorkFlowEachUser(e.getUserId(), tree.getId(), false);
				WorkFlowFolderCondition wf = session.getAklaboxService().getFolderCondition(w.getWorkFlowId());
				WorkFlowEvents event = new WorkFlowEvents();
				event.setWorkFlowId(session.getAklaboxService().getSpecificWorkFlowEachUser(e.getUserId(), tree.getId(), false).getWorkFlowId());
				event.setWorkFlowEventDate(new Date());
				event.setReceiver(e.getUserId());
				event.setSender(workflowOwner.getUserId());
				Notifications n = new Notifications();
				//String message = "";
				n.setDocId(tree.getId());
				n.setFileType(AklaboxConstant.FOLDER);
				n.setIsTrigger(true);
				n.setNotificationDate(new Date());
				n.setNotifiedBy(session.getAklaboxService().getUserInfo(email).getUserId());
				n.setUserId(e.getUserId());

				if (isAlertOne && wf.isAlertOne()) {
					searchKeywordNotifier(event, n, w, object, email);
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.UploadedTheDocument) + " " 
					+ object.getName() + " " + Labels.getLabel(session.getCurrentLocale(), Labels.InsideYourFolder) + " " + tree.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					//session.getAklaboxService().saveNotification(n);
					NotificationHelper.notifyUser(request, session, workflowOwner, receiver, tree, message);
				}
				else if (isAlertTwo && wf.isAlertTwo()) {
					searchKeywordNotifier(event, n, w, object, email);
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.CreateAFolder) + " " 
					+ object.getName() + " " + Labels.getLabel(session.getCurrentLocale(), Labels.InsideYourFolder) + " " + tree.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					//session.getAklaboxService().saveNotification(n);
					NotificationHelper.notifyUser(request, session, workflowOwner, receiver, tree, message);
				}
				else if (isAlertThree && wf.isAlertThree()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.UpdatedANewVersionOfDocument) + " " 
				+ object.getName() + " " + Labels.getLabel(session.getCurrentLocale(), Labels.InsideYourFolder) + " " + tree.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					//session.getAklaboxService().saveNotification(n);
					NotificationHelper.notifyUser(request, session, workflowOwner, receiver, tree, message);
				}
				else if (isAlertFour && wf.isAlertFour()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.DeletedTheDocument) + " " 
				+ object.getName() + " " + Labels.getLabel(session.getCurrentLocale(), Labels.InsideYourFolder) + " " + tree.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					//session.getAklaboxService().saveNotification(n);
					NotificationHelper.notifyUser(request, session, workflowOwner, receiver, tree, message);
				}
				else if (isAlertFive && wf.isAlertFive()) {
					message = email + " " + Labels.getLabel(session.getCurrentLocale(), Labels.DownloadedTheDocument) + " " 
				+ object.getName() + " " + Labels.getLabel(session.getCurrentLocale(), Labels.InsideYourFolder) + " " + tree.getName();
					n.setMessage(message);
					event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					//session.getAklaboxService().saveNotification(n);
					NotificationHelper.notifyUser(request, session, workflowOwner, receiver, tree, message);
				}
				else if (isAlertFacture && wf.isAlertFacture()) {
				//n.setMessage(message);
					//event.setWorkFlowEvent(message);
					session.getAklaboxService().saveWorkFlowEvent(event);
					//session.getAklaboxService().saveNotification(n);
					NotificationHelper.notifyUser(request, session, workflowOwner, receiver, tree, message);
				}
			}
		}
	}

	private void searchKeywordNotifier(WorkFlowEvents event, Notifications n, WorkFlow workFlow, IObject object, String email) throws Exception {
		// TODO search for the keywords as FileNames
		String keyword;
		String compared;
		String message;

		for (Keywords k : session.getAklaboxService().getAllKeywordsPerWorkFlow(workFlow)) {
			keyword = k.getKeyword().toLowerCase();
			compared = object.getName().toLowerCase();
			if (compared.contains(keyword)) {
				message = Labels.getLabel(session.getCurrentLocale(), Labels.YourKeyWord) + " [" + keyword 
						+ "] " + Labels.getLabel(session.getCurrentLocale(), Labels.MatchTheDocument) + " [" + object.getName() 
						+ "] " + Labels.getLabel(session.getCurrentLocale(), Labels.UploadedBy) + email;
				n.setMessage(message);
				event.setWorkFlowEvent(message);
				event.setSender(n.getNotifiedBy());
				event.setReceiver(n.getUserId());
				session.getAklaboxService().saveWorkFlowEvent(event);
				session.getAklaboxService().saveNotification(n);
				saveKeywordResult(object, k);
			}
		}
	}

	private void saveKeywordResult(IObject object, Keywords keyword) throws Exception {
		KeywordResults result = new KeywordResults();
		result.setKeywordId(keyword.getKeywordId());
		result.setDocId(object.getId());
		result.setType(object.getTreeType());
		session.getAklaboxService().saveKeywordResults(result);
	}

}
