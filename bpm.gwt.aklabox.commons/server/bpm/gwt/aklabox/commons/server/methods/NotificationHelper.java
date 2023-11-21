package bpm.gwt.aklabox.commons.server.methods;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.Notifications;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.Enterprise.TypeUser;
import bpm.gwt.aklabox.commons.client.services.exceptions.ServiceException;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.gwt.aklabox.commons.server.toremove.EmailSender;

public class NotificationHelper {

	public static void notifyEnterpriseAdmins(HttpServletRequest request, CommonSession session, Enterprise enterprise, IObject item, String message) throws ServiceException {
		List<User> admins;
		try {
			admins = session.getAklaboxService().getUserEnterprise(enterprise.getEnterpriseId(), TypeUser.ADMIN);
			if (admins != null) {
				for (User user : admins) {
					notifyUser(request, session, session.getUser(), user, item, message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to notify admins: " + e.getMessage());
		}
	}

	public static void notifyUser(HttpServletRequest request, CommonSession session, User currentUser, User notifyUser, IObject item, String message) throws ServiceException {
		String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		Notifications noti = new Notifications();
		noti.setDocId(item.getId());
		noti.setIsTrigger(true);
		noti.setUserId(notifyUser.getUserId());
		noti.setNotifiedBy(currentUser.getUserId());
		noti.setNotificationDate(new Date());
		noti.setMessage(message);
		noti.setFileType(item.getTreeType());
//		if(notifyUser.getNotificationCheckingDelay() <= 0){
			new Thread(new EmailSender(notifyUser, noti, url, session)).start();
//		}
		
	}
}
