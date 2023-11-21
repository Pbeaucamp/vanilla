package bpm.gwt.aklabox.commons.server.security;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import bpm.aklabox.workflow.core.IAklaflowManager;
import bpm.aklabox.workflow.core.model.activities.AklaflowContext;
import bpm.aklabox.workflow.remote.RemoteAklaflowManager;
import bpm.document.management.core.IAkladematManager;
import bpm.document.management.core.IDocumentManager;
import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.Customer;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.core.utils.MailConfig;
import bpm.document.management.manager.core.IVdmAdminManager;
import bpm.document.management.manager.core.model.VdmAdminContext;
import bpm.document.management.manager.remote.RemoteVdmAdminManager;
import bpm.document.management.remote.RemoteAkladematManager;
import bpm.document.management.remote.RemoteDocumentManager;
import bpm.document.management.remote.RemoteVdmManager;
import bpm.gwt.aklabox.commons.server.servlets.ObjectInputStream;
import bpm.vanilla.platform.core.config.ConfigurationAklaboxConstants;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public abstract class CommonSession {
	public static final String SESSION_ID = "sessionID";

	private String sessionId;

	private User user;
	private String runtimeUrl;

	private IVdmManager aklaboxService;
	private IAkladematManager akladematService;
	private IVdmAdminManager adminService;
	private IAklaflowManager aklaflowService;
	private IDocumentManager documentService;
	
	private boolean isAnonymous = false;
	private Customer customer;
	
	// Used to stock streams of file which can be downloaded
	private HashMap<String, ObjectInputStream> streams = new HashMap<String, ObjectInputStream>();
	
	private Integer acteTypeDocumentId;
	private Integer chorusTypeDocumentId;
	
	private Locale currentLocale;

	public CommonSession() { }

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}
	
	public void init(User user) {

		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();

//		String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
//		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
		String runtimeUrl = config.getVanillaServerUrl();
//		
		VdmContext vdmContext = new VdmContext();
		if (user.getUserId() != 0) {
			//If it goes in there you have been victim of the amazing exception handling in aklabox.
			//good luck with that !
//			vdmContext.setUserId(user.getUserId());
		}
		vdmContext.setMail(user.getEmail());
		vdmContext.setPassword(user.getPassword());
		vdmContext.setVdmUrl(runtimeUrl);

		VdmAdminContext vdmAdminContext = new VdmAdminContext(runtimeUrl, user.getEmail(), user.getPassword());
		
		this.user = user;
		this.runtimeUrl = runtimeUrl;
		this.aklaboxService = new RemoteVdmManager(vdmContext);
		try {
			this.user = this.aklaboxService.connect(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.akladematService = new RemoteAkladematManager(vdmContext);
		this.adminService = new RemoteVdmAdminManager(vdmAdminContext);
		this.documentService = new RemoteDocumentManager(vdmContext);
		
		AklaflowContext aklaContext = new AklaflowContext();
		aklaContext.setMail(user.getEmail());
		aklaContext.setPassword(user.getPassword());
		aklaContext.setAklaflowUrl(runtimeUrl);
		
		this.aklaflowService = new RemoteAklaflowManager(aklaContext);
		
		String client = config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CLIENT);
		this.customer = Customer.fromValue(client);
		
		String acteTypeDocumentId = config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_ACTE_TYPE);
		String chorusTypeDocumentId = config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CHORUS_TYPE);
		this.acteTypeDocumentId = acteTypeDocumentId != null && !acteTypeDocumentId.isEmpty() ? Integer.parseInt(acteTypeDocumentId) : null;
		this.chorusTypeDocumentId = chorusTypeDocumentId != null && !chorusTypeDocumentId.isEmpty() ? Integer.parseInt(chorusTypeDocumentId) : null;

		String locale = config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_LOCALE);
		this.currentLocale = locale != null && !locale.isEmpty() ? new Locale(locale) : new Locale("en");
	}
	
	public IVdmManager getAklaboxService() {
		return aklaboxService;
	}
	
	public IAkladematManager getAkladematService() {
		return akladematService;
	}

	public IVdmAdminManager getAdminService() {
		return adminService;
	}
	
	public IDocumentManager getDocumentService() {
		return documentService;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public User getUser() {
		return user;
	}
	
	public Locale getCurrentLocale() {
		return currentLocale;
	}
	
	public String getRuntimeUrl() {
		return runtimeUrl;
	}

	public boolean isAnonymous() {
		return isAnonymous;
	}

	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public void sendMail(String receiver, String sender, String content, String title, boolean isRichText, HashMap<String, InputStream> hashMap) throws Exception {
		MailConfig mailConfig = new MailConfig(receiver, sender, content, title, isRichText);
		aklaboxService.sendEmail(mailConfig, hashMap);
	}	
	
	public void addStream(String name, String format, ByteArrayInputStream stream) {
		if (streams == null) {
			streams = new HashMap<String, ObjectInputStream>();
		}

		ObjectInputStream objectInputStream = streams.get(name);
		if (objectInputStream == null) {
			objectInputStream = new ObjectInputStream();
			objectInputStream.addStream(format, stream);

			this.streams.put(name, objectInputStream);
		}
		else {
			objectInputStream.addStream(format, stream);
		}
	}

	public ByteArrayInputStream getStream(String name, String format) {
		return streams.get(name).getStream(format);
	}
	
	public IAklaflowManager getAklaflowService() {
		return aklaflowService;
	}

	public Customer getCustomer() {
		return customer;
	}
	
	public Integer getActeTypeDocumentId() {
		return acteTypeDocumentId;
	}
	
	public Integer getChorusTypeDocumentId() {
		return chorusTypeDocumentId;
	}

	public abstract String getApplicationId();
}
