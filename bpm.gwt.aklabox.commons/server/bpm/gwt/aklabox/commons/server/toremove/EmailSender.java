package bpm.gwt.aklabox.commons.server.toremove;

import java.io.InputStream;
import java.util.HashMap;

import bpm.document.management.core.model.Notifications;
import bpm.document.management.core.model.User;
import bpm.document.management.core.utils.EmailTemplate;
import bpm.gwt.aklabox.commons.server.security.CommonSession;

public class EmailSender implements Runnable {

	private CommonSession session;
	private Notifications noti;
	private User user;
	private String url;
	private EmailTemplate emailTemplate = new EmailTemplate();
	private HashMap<String, InputStream> hashMap = new HashMap<String, InputStream>();

	public EmailSender(User user, Notifications noti, String url, CommonSession session) {
		this.session = session;
		this.noti = noti;
		this.url = url;
		this.user = user;
	}

//	public EmailSender(User user, Notifications noti, String url, CommonSession session, HashMap<String, InputStream> hashMap) {
//		this.session = session;
//		this.noti = noti;
//		this.url = url;
//		this.user = user;
//		this.hashMap = hashMap;
//	}

	@Override
	public void run() {
		try {

			if (user.isNotifiedByPlatform()) {
				session.getAklaboxService().saveNotification(noti);
			}

			if (user.isNotifiedByEmail()) {
				String sender = "vanilla@bpm-conseil.com";

				String title = "Aklabox Alertes et Notifications";

				String content = emailTemplate.getTemplate(user, url, noti.getMessage());
				
				session.sendMail(user.getEmail(), sender, content, title, true, hashMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
