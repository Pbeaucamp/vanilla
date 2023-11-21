package bpm.gwt.aklabox.commons.server.drivers;

import java.util.List;

import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.server.methods.ItemEmails;
import bpm.gwt.aklabox.commons.server.security.CommonSession;

public class CommonDocsController {

	public void sendMail(CommonSession session, User owner, List<User> users, List<Documents> docList, String url, int mailStatus, int protection, String option, String userMessage, String externalMessage) {
		if (!users.isEmpty()) {
			for (User user : users) {
				if (!user.getEmail().equals(owner.getEmail())) {
					Thread th = new Thread(new ItemEmails(option, user, owner, docList, url, session, userMessage, externalMessage));
					th.start();
				}
			}
		}

		//TODO: REFACTOR RIGHT - LATER - Manage notification
//		if (!mails.isEmpty()) {
//			for (FolderShare mail : mails) {
//				if(mail.isRequestEmail()) {
//					User user = new User();
//					user.setEmail(mail.getEmail());
//					user.setPassword(mail.getSharedpassword());
//					user.setAddress(mail.getMessage());
//	
//					if (protection == 1) {
//						if (mailStatus == 1) {
//							Thread th = new Thread(new ItemEmails("SELink", user, owner, docList, url, session, userMessage, externalMessage));
//							th.start();
//						}
//						else if (mailStatus == 2) {
//							Thread th = new Thread(new ItemEmails("SNot", user, owner, docList, url, session, userMessage, externalMessage));
//							th.start();
//							Thread th2 = new Thread(new ItemEmails("SecurityMail", user, owner, docList, url, session, userMessage, externalMessage));
//							th2.start();
//						}
//						else if (mailStatus == 3) {
//							Thread th = new Thread(new ItemEmails("SNot", user, owner, docList, url, session, userMessage, externalMessage));
//							th.start();
//						}
//					}
//				}
//			}
//		}
	}

	public void sendMail(CommonSession session, User owner, List<User> users, Tree folder, String url, int mailStatus, int protection, String option, String userMessage, String externalMessage) {
		if (!users.isEmpty()) {
			for (User user : users) {
				if (!user.getEmail().equals(owner.getEmail())) {
					Thread th = new Thread(new ItemEmails(option, user, owner, folder, url, session, userMessage, externalMessage));
					th.start();
				}
			}
		}

		//TODO: REFACTOR RIGHT - LATER - Manage notification
//		if (!mails.isEmpty()) {
//			for (FolderShare mail : mails) {
//				User user = new User();
//				user.setEmail(mail.getEmail());
//				user.setPassword(mail.getSharedpassword());
//				user.setAddress(mail.getMessage());
//
//				if (protection == 1) {
//					if (mailStatus == 1) {
//						Thread th = new Thread(new ItemEmails("FSELink", user, owner, folder, url, session, userMessage, externalMessage));
//						th.start();
//					}
//					else if (mailStatus == 2) {
//						Thread th = new Thread(new ItemEmails("FSNot", user, owner, folder, url, session, userMessage, externalMessage));
//						th.start();
//						Thread th2 = new Thread(new ItemEmails("FSecurityMail", user, owner, folder, url, session, userMessage, externalMessage));
//						th2.start();
//					}
//					else if (mailStatus == 3) {
//						Thread th = new Thread(new ItemEmails("FSNot", user, owner, folder, url, session, userMessage, externalMessage));
//						th.start();
//					}
//				}
//				else {
//					Thread th = new Thread(new ItemEmails("FExternal", user, owner, folder, url, session, userMessage, externalMessage));
//					th.start();
//				}
//			}
//		}
	}

	
}
