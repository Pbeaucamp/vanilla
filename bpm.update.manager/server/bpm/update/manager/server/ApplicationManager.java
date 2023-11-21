package bpm.update.manager.server;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.update.manager.api.beans.ApplicationUpdateInformation;
import bpm.update.manager.api.beans.InstallationInformations;
import bpm.update.manager.api.beans.Update;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.server.config.ApplicationConfiguration;
import bpm.update.manager.server.config.ConfigurationManager;

import com.thoughtworks.xstream.XStream;

public class ApplicationManager {

	private static ApplicationManager instance;

	private Logger logger = Logger.getLogger(getClass());
	private UpdateManager updateManager;
	
	private UpdateInformations appsInfos;

	public static ApplicationManager getInstance() {
		if (instance == null) {
			instance = new ApplicationManager();
		}
		return instance;
	}

	public ApplicationManager() {
		logger.info("Initialize Update Manager...");
		ApplicationConfiguration appConfig = ConfigurationManager.getInstance().getConfiguration();

		String managerUrl = appConfig.getProperty(ApplicationConfiguration.MANAGER_URL);
		String managerLogin = appConfig.getProperty(ApplicationConfiguration.MANAGER_LOGIN);
		String managerPassword = appConfig.getProperty(ApplicationConfiguration.MANAGER_PASSWORD);
		
		String updateUrl = appConfig.getProperty(ApplicationConfiguration.UPDATE_URL);
		int updatePort = Integer.parseInt(appConfig.getProperty(ApplicationConfiguration.UPDATE_PORT));
		String login = appConfig.getProperty(ApplicationConfiguration.UPDATE_LOGIN);
		String password = appConfig.getProperty(ApplicationConfiguration.UPDATE_PASSWORD);

		String runtimeInstallationPath = appConfig.getProperty(ApplicationConfiguration.RUNTIME_INSTALLATION_PATH);
		String webappInstallationPath = appConfig.getProperty(ApplicationConfiguration.WEBAPP_INSTALLATION_PATH);
		String savePath = appConfig.getProperty(ApplicationConfiguration.SAVE_PATH);
		String historyPath = appConfig.getProperty(ApplicationConfiguration.HISTORY_PATH);
		
		boolean isUnix = Boolean.parseBoolean(appConfig.getProperty(ApplicationConfiguration.IS_UNIX));
		String restartCommand = appConfig.getProperty(ApplicationConfiguration.RESTART_COMMAND);
		
		InstallationInformations installInfos = new InstallationInformations(updateUrl, updatePort, login, password, managerUrl, managerLogin, 
				managerPassword, runtimeInstallationPath, webappInstallationPath, savePath, historyPath, isUnix, restartCommand);
		
		this.updateManager = new UpdateManager(installInfos);
		configureApplications(appConfig);
	}

	public void reloadApplications() {
		ConfigurationManager.getInstance().reloadConfiguration();
		
		ApplicationConfiguration appConfig = ConfigurationManager.getInstance().getConfiguration();
		configureApplications(appConfig);
	}
	
	public UpdateManager getUpdateManager() {
		return updateManager;
	}
	
	public UpdateInformations getAppsInfos() {
		return appsInfos;
	}

	private void configureApplications(ApplicationConfiguration appConfig) {
		String patchName = appConfig.getProperty(ApplicationConfiguration.PATCH_NAME);
		String patchVersion = appConfig.getProperty(ApplicationConfiguration.PATCH_VERSION);
		
		this.appsInfos = new UpdateInformations(patchName, patchVersion, loadHistory(appConfig));
		
		int nbApplications = 0;
		while (true) {
			nbApplications++;
			String applicationName = null;
			try {
				applicationName = appConfig.getProperty(ApplicationConfiguration.APPLICATION_NAME + "." + nbApplications);
				if (applicationName == null || applicationName.isEmpty()) {
					break;
				}
			} catch (Exception e) {
				break;
			}
		}
		nbApplications--;

		for (int i = 1; i <= nbApplications; i++) {
			registerApplication(appConfig, appsInfos, i);
		}
	}

	private List<Update> loadHistory(ApplicationConfiguration appConfig) {
		String historyPath = appConfig.getProperty(ApplicationConfiguration.HISTORY_PATH);
		historyPath = historyPath.endsWith("/") ? historyPath : historyPath + "/";
		
		File historyFolder = new File(historyPath);
		if (!historyFolder.exists()) {
			logger.info("Create update history folder (" + historyPath + ")");
			historyFolder.mkdirs();
		}

		List<Update> updates = new ArrayList<>();
		
		File[] files = historyFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				try (FileInputStream fis = new FileInputStream(file)) {
					Update update = (Update) new XStream().fromXML(fis);
					logger.info("Update history found.");
					updates.add(update);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Unable to load this update history. The file '" + file.getName() + "' seems to be incorrect.");
				}
			}
		}

		return updates;
	}

	private void registerApplication(ApplicationConfiguration appConfig, UpdateInformations appsInfos, int indexApp) {
		try {

			String appName = appConfig.getProperty(ApplicationConfiguration.APPLICATION_NAME + "." + indexApp);
			boolean isRuntime = Boolean.parseBoolean(appConfig.getProperty(ApplicationConfiguration.APPLICATION_IS_RUNTIME + "." + indexApp));
			
			appsInfos.addAppInfos(new ApplicationUpdateInformation(appName, isRuntime));
		} catch (Exception e) {
			e.printStackTrace();

			try {
				String appName = appConfig.getProperty(ApplicationConfiguration.APPLICATION_NAME + indexApp);
				logger.error("Application's configuration (id = " + indexApp + ", name = " + appName + ") is malformed. Please check the configuration file.");
			} catch (Exception e1) {
				logger.error("Application's configuration (id = " + indexApp + ") is malformed. Please check the configuration file.");
			}
		}
	}
	
	public void updatePlatformVersion(Update update) {
		ApplicationConfiguration appConfig = ConfigurationManager.getInstance().getConfiguration();
		appConfig.setProperty(ApplicationConfiguration.PATCH_VERSION, update.getVersion());
		reloadApplications();
	}

	public List<String> getApplications() throws ServiceException {
		List<String> applications = new ArrayList<String>();
		if (appsInfos != null && !appsInfos.getAppInfos().isEmpty()) {
			for (ApplicationUpdateInformation app : appsInfos.getAppInfos()) {
				applications.add(app.getAppName());
			}
		}
		return applications;
	}
}
