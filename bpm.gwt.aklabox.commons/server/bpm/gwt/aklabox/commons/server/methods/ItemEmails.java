package bpm.gwt.aklabox.commons.server.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import bpm.document.management.core.model.Campaign;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.document.management.core.utils.EmailTemplate;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.vanilla.platform.core.config.ConfigurationManager;

//TODO: Supposed to be runtime size
@Deprecated
public class ItemEmails implements Runnable {

	private CommonSession session;

	private String emailType;
	private User user;
	private User owner;
	private List<Documents> docList;
	private List<User> loaders;
	private String url;
	private EmailTemplate emailTemplate = new EmailTemplate();
	private Campaign campaign;
	private String subject = "";
	private String message = "";
	private String userMessage;
	private String externalMessage;
	private Tree folder;

	private String groupName;

	public ItemEmails(String emailType, User user, User owner, List<Documents> docList, String url, CommonSession session, String userMessage, String externalMessage) {
		this.emailType = emailType;
		this.user = user;
		this.owner = owner;
		this.docList = docList;
		this.url = url;
		this.session = session;
		this.userMessage = userMessage;
		this.externalMessage = externalMessage;
	}

	public ItemEmails(String emailType, List<User> loaders, User user, Campaign campaign, String url, CommonSession session) {
		this.emailType = emailType;
		this.campaign = campaign;
		this.user = user;
		this.loaders = loaders;
		this.url = url;
		this.session = session;
	}

	public ItemEmails(String emailType, User user, User owner, List<Documents> docList, String url, String subject, String message, CommonSession session) {
		this.emailType = emailType;
		this.user = user;
		this.owner = owner;
		this.docList = docList;
		this.url = url;
		this.subject = subject;
		this.message = message;
		this.session = session;
	}

	public ItemEmails(String emailType, User user, User owner, List<Documents> docList, String url, CommonSession session) {
		this.emailType = emailType;
		this.user = user;
		this.owner = owner;
		this.docList = docList;
		this.url = url;
		this.session = session;
	}

	public ItemEmails(String emailType, User user, User owner, Tree folder, String url, CommonSession session, String userMessage, String externalMessage) {
		this.emailType = emailType;
		this.user = user;
		this.owner = owner;
		this.userMessage = userMessage;
		this.folder = folder;
		this.url = url;
		this.session = session;
	}

	public ItemEmails(String groupName, String emailType, User user, User owner, List<Documents> docList, String url, CommonSession session) {
		this.groupName = groupName;
		this.emailType = emailType;
		this.user = user;
		this.owner = owner;
		this.docList = docList;
		this.url = url;
		this.session = session;
	}

	@Override
	public void run() {
		try {
			// Replacing the url with a property because it's not working as
			// expected when behind a reverse proxy (also provide the server ip
			// to the user is ugly)
			String propUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.aklabox.external.server.url");
			if (propUrl != null) {
				url = propUrl;
			}

			long l = 0;
			for (int i = 0; i < user.getEmail().length(); i++)
				l = l * 127 + user.getEmail().charAt(i);

			String external = String.valueOf(Math.abs(l));

			String sender = "vanilla@bpm-conseil.com";

			HashMap<String, InputStream> map = new HashMap<String, InputStream>();

			String title = "";
			String text = "";

			if (emailType.equals("SendDoc")) {
				title = "<p>Aklabox : 1 Document reçu</p>";

				if (docList != null && !docList.isEmpty()) {
					for (Documents doc : docList) {

						text = doc.getName() + " vous a été envoyé.";
						//TODO: REFACTOR ENCRYPTION - Refactor the system someday
//						new EncryptionTemplate().decrypt(doc, session);
						FileInputStream file = new FileInputStream(new File(doc.getFilePath()));
						map.put(doc.getFileName(), file);
					}
				}
			}
			else if (emailType.equals("Tasks")) {
				title = "Aklabox : Task Management";
				text = "<p> Une tâche vous a été attribuée. S'il vous plaît vérifier votre compte pour la consulter.</p>";
			}
			else if (emailType.equals("Message")) {
				title = subject;
				text = "<p>" + message + "</p>";

				for (Documents d : docList) {
					//TODO: REFACTOR ENCRYPTION - Refactor the system someday
//					new EncryptionTemplate().decrypt(d, session);
					FileInputStream file = new FileInputStream(new File(d.getFilePath()));
					map.put(d.getFileName(), file);
				}
			}
			else if (emailType.equals("AklaBox Versioning")) {
				if (docList.size() > 0) {
					title = subject + ": " + docList.get(0).getFileName();
				}
				else {
					title = subject;
				}
				text = "<p>" + message + "</p>";

				for (Documents d : docList) {
					//TODO: REFACTOR ENCRYPTION - Refactor the system someday
//					new EncryptionTemplate().decrypt(d, session);
					FileInputStream file = new FileInputStream(new File(d.getFilePath()));
					map.put(d.getFileName(), file);
				}
				for (Documents doc : docList) {
					//TODO: REFACTOR RIGHT - Visualiseur de document (avec connexion)
					text += "<p>Vous pouvez voir" + " " + doc.getName() + " " + "en cliquant sur le lien suivant :" + " " + "<a href='" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "' style='color:blue'>" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "</a></p>";
				}

			}
			else if (emailType.equals("AklaBox: Shared Workspace")) {
				title = subject;
				text = "<p>" + message + "</p>";

				for (Documents d : docList) {
					//TODO: REFACTOR ENCRYPTION - Refactor the system someday
//					new EncryptionTemplate().decrypt(d, session);
					FileInputStream file = new FileInputStream(new File(d.getFilePath()));
					map.put(d.getFileName(), file);
				}
				text += "<p> Vous pouvez consulter les documents nouvellement ajoutés en cliquant sur ce lien. <a href='" + url + "' style='color:blue'>" + url + "</a></p>";

			}
			else if (emailType.equals("AklaBox Folder Shared: New Documents")) {
				title = emailType + " inside " + folder.getName();
				text = "<p>" + userMessage + "</p>";

				if (folder.isShare()) {
					text += "<p>Vous pouvez voir" + " " + folder.getName() + " " + "en cliquant sur le lien suivant :" + " " + "<a href='" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "' style='color:blue'>" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "</a></p>";
				}
			}

			else if (emailType.equals("Workspace")) {
				title = "Aklabox Espace de travail : " + groupName + " avec " + docList.size() + " documents à l'intérieur";
				text = "<p>L'utilisateur Aklabox " + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un espace de travail avec vous.</p>";
				text += "<p> Le code pin de cet espace de travail est " + user.getVerificationCode() + "</p>";
				text += "<p> Vous pouvez visiter l'espace de travail en cliquant sur ce lien. <a href='" + url + "' style='color:blue'>" + url + "</a></p>";
			}
			else if (emailType.equals("Campaign")) {
				title = "Aklabox Campaign";
				text += "<p style='font-weight: bold; font-size: 1em;margin-top: 50px;'>Campaign Subject : " + campaign.getSubject() + "</p>";

				text += "<p>" + campaign.getComment() + "</p>";

				text += "<div style='font-weight: bold; font-size: 1em;margin-top: 20px;'>Due Date:</div><div>" + campaign.getExpirationDate() + "</div>";

				text += "<div style='font-weight: bold; font-size: 1em;margin-top: 20px;'>Campaign Members:</div>";

				for (User loader : loaders) {
					text += "<div>" + loader.getFirstName() + " " + loader.getLastName() + "</br>";

				}

				text += "<div style='font-weight: bold; font-size: 1em;margin-top: 20px;'>Campaign Manager: ";
				text += user.getFirstName() + user.getLastName() + "<div>";
				text += user.getCompany();

			}
			else if (emailType.equals("SecurityMail")) {
				title = "Aklabox accès sécurisé";

				text = "<p>L'utilisateur" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "vous a envoyé un mot de passe pour accéder aux documents partagés. Le mot de passe est" + " " + user.getPassword() + " " + ".</p>";

				text += "<p>Les documents accessibles sont : </p>";

				for (Documents doc : docList) {
					text += "<p>" + doc.getName() + "</p>";
				}
			}
			else if (emailType.equals("FSecurityMail")) {
				title = "Aklabox accès sécurisé";

				text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "vous a envoyé un mot de passe pour accéder au dossier partagé. Le mot de passe est" + " " + user.getPassword() + " " + ".</p>";

				text += "<p>Le dossier accessible avec ce mot de passe est " + folder.getName() + "</p>";
			}
			else if (folder != null) {
				title = "Aklabox dossier partagé: " + folder.getName();

				if (emailType.equals("FInternal") || emailType.equals("FUInternal")) {
					if (emailType.equals("FInternal")) {
						text = "<p>L'utilisateur Aklabox " + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un dossier avec vous.</p>";
					}
					else {
						text = "<p>L'utilisateur Aklabox " + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un dossier avec vous.</p>";
					}

					if (userMessage != null) {
						if (!userMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du dossier: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'> - " + userMessage + "</p>";
						}
					}
					text += "<p>Vous pouvez voir le dossier" + " " + folder.getName() + " " + "en cliquant sur ce lien :" + " " + "<a href='" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "' style='color:blue'>" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "</a></p>";
				}
				else if (emailType.equals("FExternal") || emailType.equals("FUExternal")) {
					if (emailType.equals("FExternal")) {

						text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un dossier avec vous.</p>";
					}
					else {
						text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un dossier avec vous.</p>";
					}

					if (externalMessage != null) {
						if (!externalMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du dossier: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'>" + externalMessage + "</p>";
						}
					}

					if (!user.getAddress().equals("No Message")) {
						text += "<p style='color:red'>Message du propriétaire du dossier: </p>";
						text += "<p style='margin-left:40px;font-weight:bold'>" + user.getAddress() + "</p>";
					}

					text += "<p>Vous pouvez voir le dossier" + " " + folder.getName() + " " + "en cliquant sur ce lien :" + " " + "<a href='" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "' style='color:blue'>" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "</a></p>";
				}
				else if (emailType.equals("FSELink")) {
					text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un dossier avec vous.</p>";

					text += "<p>Le mot de passe pour accéder au dossier est " + user.getPassword() + "</p>";

					if (externalMessage != null) {
						if (!externalMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du dossier: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'>" + externalMessage + "</p>";
						}
					}

					if (!user.getAddress().equals("No Message")) {
						text += "<p style='color:red'>Message du propriétaire du dossier: </p>";
						text += "<p style='margin-left:40px;font-weight:bold'>" + user.getAddress() + "</p>";
					}
					text += "<p>Vous pouvez voir le dossier" + " " + folder.getName() + " " + "en cliquant sur ce lien :" + " " + "<a href='" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "' style='color:blue'>" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "</a></p>";
				}
				else if (emailType.equals("FSNot")) {
					text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un dossier avec vous.</p>";

					if (externalMessage != null) {
						if (!externalMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du dossier: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'>" + externalMessage + "</p>";
						}
					}

					if (!user.getAddress().equals("No Message")) {
						text += "<p style='color:red'>Message du propriétaire du dossier: </p>";
						text += "<p style='margin-left:40px;font-weight:bold'>" + user.getAddress() + "</p>";
					}

					text += "<p>Vous pouvez voir le dossier" + " " + folder.getName() + " " + "en cliquant sur ce lien :" + " " + "<a href='" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "' style='color:blue'>" + url + "/FolderView?page=" + external + "&hash=" + folder.getUniqueCode() + "</a></p>";
				}
			}
			else {
				if (docList.size() > 0) {
					title = "Aklabox document partagé: " + docList.get(0).getFileName();
				}
				else {
					title = "Aklabox document partagé ";
				}

				if (emailType.equals("Internal") || emailType.equals("UInternal")) {
					if (emailType.equals("Internal")) {
						text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé " + " " + docList.size() + " " + "documents avec vous.</p>";
					}
					else {
						text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un document avec vous.</p>";
					}

					if (userMessage != null) {
						if (!userMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du document: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'> - " + userMessage + "</p>";
						}
					}

					for (Documents doc : docList) {
						//TODO: REFACTOR RIGHT - Visualiseur de document (avec connexion)
						text += "<p>Vous pouvez voir le document" + " " + doc.getName() + " " + "en cliquant sur ce lien:" + " " + "<a href='" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "' style='color:blue'>" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "</a></p>";
					}
				}
				else if (emailType.equals("External") || emailType.equals("UExternal")) {

					if (emailType.equals("External")) {

						text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé " + " " + docList.size() + " " + "documents avec vous.</p>";
					}
					else {
						text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé un document avec vous.</p>";
					}

					if (externalMessage != null) {
						if (!externalMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du document: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'>" + externalMessage + "</p>";
						}
					}

					if (!user.getAddress().equals("No Message")) {
						text += "<p style='color:red'>Message du propriétaire du document: </p>";
						text += "<p style='margin-left:40px;font-weight:bold'>" + user.getAddress() + "</p>";
					}

					for (Documents doc : docList) {
						//TODO: REFACTOR RIGHT - Visualiseur de document (avec connexion)
						text += "<p>Vous pouvez voir le document" + " " + doc.getName() + " " + "en cliquant sur ce lien:" + " " + "<a href='" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "' style='color:blue'>" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "</a></p>";
					}
				}
				else if (emailType.equals("SELink")) {
					text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé " + " " + docList.size() + " " + "documents avec vous.</p>";

					text += "<p>Le mot de passe pour accéder aux documents est " + user.getPassword() + "</p>";

					if (externalMessage != null) {
						if (!externalMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du document: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'>" + externalMessage + "</p>";
						}
					}

					if (!user.getAddress().equals("No Message")) {
						text += "<p style='color:red'>Message du propriétaire du document: </p>";
						text += "<p style='margin-left:40px;font-weight:bold'>" + user.getAddress() + "</p>";
					}

					for (Documents doc : docList) {
						//TODO: REFACTOR RIGHT - Visualiseur de document (avec connexion)
						text += "<p>Vous pouvez voir le document" + " " + doc.getName() + " " + "en cliquant sur ce lien:" + " " + "<a href='" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "' style='color:blue'>" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "</a></p>";
					}
				}
				else if (emailType.equals("SNot")) {
					text = "<p>L'utilisateur Aklabox" + " " + "<b>" + owner.getFirstName() + " " + owner.getLastName() + "</b>" + " " + "a partagé " + " " + docList.size() + " " + "documents avec vous.</p>";
					if (externalMessage != null) {
						if (!externalMessage.equals("")) {
							text += "<p style='color:red'>Message du propriétaire du document: </p>";
							text += "<p style='margin-left:40px;font-weight:bold'>" + externalMessage + "</p>";
						}
					}

					if (!user.getAddress().equals("No Message")) {
						text += "<p style='color:red'>Message du propriétaire du document: </p>";
						text += "<p style='margin-left:40px;font-weight:bold'>" + user.getAddress() + "</p>";
					}

					for (Documents doc : docList) {
						//TODO: REFACTOR RIGHT - Visualiseur de document (avec connexion)
						text += "<p>Vous pouvez voir le document" + " " + doc.getName() + " " + "en cliquant sur ce lien:" + " " + "<a href='" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "' style='color:blue'>" + url + "/DocView?page=" + external + "&hash=" + doc.getUniqueCode() + "</a></p>";
					}
				}
			}

			String content = emailTemplate.getTemplate(user, url, text);

			if (emailType.equals("SendDoc") || emailType.equals("Message")) {
				session.sendMail(user.getEmail(), sender, content, title, true, map);
			}
			else {
				session.sendMail(user.getEmail(), sender, content, title, true, new HashMap<String, InputStream>());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
