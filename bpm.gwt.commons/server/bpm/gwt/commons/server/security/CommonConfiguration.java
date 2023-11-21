package bpm.gwt.commons.server.security;

import java.util.Date;

import javax.activation.DataHandler;

import org.apache.log4j.Logger;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class CommonConfiguration {

	private static CommonConfiguration config;

	private static Logger logger = Logger.getLogger(CommonConfiguration.class);
	private VanillaConfiguration vanillaConfig;
	private IVanillaAPI vanillaApi;
	
	private String vanillaRuntimeUrl;

	public CommonConfiguration() {
		logger.info("Creating new PortalConfiguration...");
		connect();
	}

	public BaseVanillaContext getRootContext() {
		this.vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.vanillaRuntimeUrl = vanillaConfig.getVanillaServerUrl();

		if (vanillaRuntimeUrl == null || vanillaRuntimeUrl.isEmpty()) {
			return null;
		}

		String rootUser = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String rootPass = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		return new BaseVanillaContext(vanillaRuntimeUrl, rootUser, rootPass);
	}

	public static CommonConfiguration getInstance() {
		if (config == null) {
			config = new CommonConfiguration();
		}

		return config;
	}

	public void reconnect() {
		connect();
	}

	private void connect() {
		IVanillaContext context = getRootContext();
		if (context != null) {
			this.vanillaApi = new RemoteVanillaPlatform(context);
		}
	}

	/**
	 * Be carefull, only use that for the method "change password" if the user
	 * who change the password are the super admin
	 */
	public void setRootPassword(String rootPassword) {
		vanillaConfig.setProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD, rootPassword);
	}

	public void setFeedback(boolean allowFeedback) {
		vanillaConfig.setProperty(VanillaConfiguration.P_FEEDBACK_ALLOWED, String.valueOf(allowFeedback));
	}

	public String getBirtViewerUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_BIRT_VIEWER_URL);
	}

	public String getBirtViewerPath() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_BIRT_VIEWER_PATH);
	}

	public String getDefaultTheme() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_DEFAULT_THEME);
	}

	public String getMailUser() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_MAIL_USER);
	}

	public String getMailPassword() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_MAIL_PASSWORD);
	}

	public String getMailHost() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_MAIL_HOST);
	}

	public String getMailPort() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_MAIL_PORT);
	}

	public String getMailFrom() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_MAIL_FROM);
	}

	public boolean isMailNeedAuth() {
		String auth = vanillaConfig.getProperty(VanillaConfiguration.P_MAIL_AUTH);
		return auth != null && auth.equalsIgnoreCase("true");
	}

	public String getFawebUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_FAWEB);
	}

	public String getFdwebUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_FDWEB);
	}

	public String getMetadataUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_FMDTWEB);
	}

	public String getArchitectUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_ARCHITECTWEB);
	}

	public String getDataPreparationUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_DATAPREPARATION);
	}

	public String getFmloaderwebUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_FMLOADERWEB);
	}

	public String getFmuserwebUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_FMUSERWEB);
	}

	public String getFmDesignerWebUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_FMDESIGNERWEB);
	}

	public String getFwrUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_FWR);
	}
	
	public String getAirUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_AIR);
	}
	
	public String getHubUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_HUB);
	}

	public String getUrlDeconnect() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_DECONNECT);
	}

	public String getUpdateManagerUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_UPDATE_MANAGER_URL);
	}

	public boolean isUseBirtViewer() {
		String useBirtViewer = vanillaConfig.getProperty(VanillaConfiguration.P_USE_BIRT_VIEWER);
		return useBirtViewer != null && useBirtViewer.equalsIgnoreCase("true");
	}

	public boolean canExportDashboard() {
		String wkhtmltopdfPath = vanillaConfig.getProperty(VanillaConfiguration.P_DASHBOARD_EXPORT_WKHTMLTOPDF_PATH);
		return wkhtmltopdfPath != null && !wkhtmltopdfPath.isEmpty();
	}

	public IVanillaAPI getRootVanillaApi() {
		return vanillaApi;
	}

	public void addImageForUser(int userId, DataHandler imgData, String format) throws Exception {
		getRootVanillaApi().getVanillaSecurityManager().addUserImage(userId, imgData, format);
	}

	public byte[] getImageForUser(int userId) throws Exception {
		return getRootVanillaApi().getVanillaSecurityManager().getImageForUserId(userId);
	}

	public byte[] getImageForGroup(int groupId) throws Exception {
		return getRootVanillaApi().getVanillaSecurityManager().getImageForGroupId(groupId);
	}

	public void changeThemeForUser(User user, String vanillaTheme) throws Exception {
		user.setVanillaTheme(vanillaTheme);
		getRootVanillaApi().getVanillaSecurityManager().updateUser(user);
	}

	public void modifyMail(User user, String newMail, boolean privateMail) throws Exception {
		if (privateMail) {
			user.setPrivateMail(newMail);
		}
		else {
			user.setBusinessMail(newMail);
		}

		getRootVanillaApi().getVanillaSecurityManager().updateUser(user);
	}

	public void modifyPassword(User user, String password, String newPassword, boolean withoutConfirm) throws Exception {
		if (!withoutConfirm && !user.getPassword().equals(Security.encodeMD5(password))) {
			throw new bpm.gwt.commons.client.services.exception.SecurityException(bpm.gwt.commons.client.services.exception.SecurityException.ERROR_TYPE_BAD_USER_PASS, "Wrong password for user " + user.getLogin());
		}

		user.setPassword(Security.encodeMD5(newPassword));
		user.setDatePasswordModification(new Date());
		user.setPasswordChange(0);
		user.setPasswordReset(0);

		getRootVanillaApi().getVanillaSecurityManager().updateUser(user);
	}

	public String getRootUser() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
	}

	public String getHelpUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_HELP_URL);
	}

	public boolean feedbackIsNotInited() {
		String feedbackAllowed = vanillaConfig.getProperty(VanillaConfiguration.P_FEEDBACK_ALLOWED);
		String feedbackUrl = getFeedbackUrl();

		return (feedbackAllowed == null || feedbackAllowed.isEmpty()) && (feedbackUrl != null && !feedbackUrl.isEmpty());
	}

	public String getFeedbackUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_FEEDBACK_URL);
	}

	public boolean isFeedbackAllowed() {
		String feedbackAllowed = vanillaConfig.getProperty(VanillaConfiguration.P_FEEDBACK_ALLOWED);
		return feedbackAllowed != null && !feedbackAllowed.isEmpty() ? Boolean.parseBoolean(feedbackAllowed) : false;
	}

	public boolean canSendFeedbackMessage() {
		String feedbackAllowed = vanillaConfig.getProperty(VanillaConfiguration.P_FEEDBACK_CAN_SEND_MESSAGE);
		return feedbackAllowed != null && !feedbackAllowed.isEmpty() ? Boolean.parseBoolean(feedbackAllowed) : false;
	}
	
	public String getKpiMapUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_WEBAPPS_KpiMap);
	}
	
	public String getVanillaRuntimeUrl() {
		return vanillaRuntimeUrl;
	}
	
	public String getVanillaRuntimeExternalUrl() {
		return vanillaConfig.getVanillaExternalUrl();
	}
	
	public String getCkanUrl() {
		return vanillaConfig.getProperty(VanillaConfiguration.P_CKAN_URL);
	}
}
