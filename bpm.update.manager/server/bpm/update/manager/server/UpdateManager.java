package bpm.update.manager.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.update.manager.api.IUpdateManager;
import bpm.update.manager.api.beans.ApplicationUpdateInformation;
import bpm.update.manager.api.beans.GlobalProgress;
import bpm.update.manager.api.beans.InstallationInformations;
import bpm.update.manager.api.beans.Update;
import bpm.update.manager.api.beans.UpdateAction;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.api.beans.UpdateProgress;
import bpm.update.manager.api.beans.UpdateResult;
import bpm.update.manager.api.utils.Constants;
import bpm.update.manager.api.utils.FTPManager;

import com.thoughtworks.xstream.XStream;

public class UpdateManager implements IUpdateManager {

	private Logger logger = Logger.getLogger(UpdateManager.class);
	private SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");

	private UpdateRunner runner;
	private GlobalProgress progress;

	private InstallationInformations installInfos;

	public UpdateManager(InstallationInformations installInfos) {
		this.installInfos = installInfos;
	}

	public GlobalProgress getProgress() {
		return progress;
	}

	@Override
	public UpdateInformations hasUpdate() throws ServiceException {
		logger.info("Check updates...");

		if (installInfos.getUpdateUrl() == null || installInfos.getUpdateUrl().isEmpty() || installInfos.getUpdatePort() < 0) {
			logger.warn("Update properties are not correctly set in the application.properties file. Auto update are disabled.");
			return null;
		}

		FTPManager manager = new FTPManager(installInfos.getUpdateUrl(), installInfos.getUpdatePort(), true, installInfos.getUpdateLogin(), installInfos.getUpdatePassword());
		try {
			manager.connect();
		} catch (Exception e) {
			manager.disconnect();

			logger.warn("Unable to connect to the server to manage updates. Auto update are disabled.");
			return null;
		}

		try {
			FTPFile[] files = manager.getFolderList();

			UpdateInformations platformInformations = ApplicationManager.getInstance().getAppsInfos();
			checkFiles(manager, platformInformations, files);
			manager.disconnect();
			return platformInformations;
		} catch (Exception e) {
			manager.disconnect();

			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	private void checkFiles(FTPManager manager, UpdateInformations updateInfos, FTPFile[] files) throws Exception {
		logger.info("Checking updates...");
		updateInfos.clearUpdates();

		boolean updateFound = false;
		if (files != null && files.length != 0) {
			for (FTPFile file : files) {
				if (file.getName().contains(updateInfos.getPatchName() + "_")) {
					String fileName = file.getName();
					int indexUnderscore = fileName.lastIndexOf("_");

					if (indexUnderscore < 0) {
						logger.warn("The file " + fileName + " is malformed. It should be like " + updateInfos.getPatchName() + "_X.X.X");
						continue;
					}

					String version = file.getName().substring(indexUnderscore + 1, file.getName().length());
					logger.info("Actual version : " + updateInfos.getPatchVersion());
					logger.info("Server version : " + version);

					if (version.compareTo(updateInfos.getPatchVersion()) > 0) {
						Update update = readPatchDescriptionFile(version, manager, file);
						updateInfos.addUpdate(update);
						updateFound = true;
					}
				}
			}
		}

		if (!updateFound) {
			logger.info("No update found.");
		}
	}

	private Update readPatchDescriptionFile(String version, FTPManager manager, FTPFile patch) throws Exception {
		String descriptor = patch.getName() + "/" + Constants.DESCRIPTOR;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			manager.downloadFile(descriptor, outputStream, new UpdateProgress());

			try (InputStream is = new ByteArrayInputStream(outputStream.toByteArray())) {
				Update update = (Update) new XStream().fromXML(is);
				update.setVersion(version);
				update.setAppNames(getAppNames(manager, patch));
				return update;
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Unable to check the patch's descriptor of '" + patch.getName() + "' : " + e.getMessage());
			}
		} catch (Exception e) {

			e.printStackTrace();
			logger.info("Unable to check the patch's descriptor of '" + patch.getName() + "' : " + e.getMessage());
			throw new Exception("Unable to check the patch's descriptor of '" + patch.getName() + "' : " + e.getMessage());
		}
	}

	private List<String> getAppNames(FTPManager manager, FTPFile patch) throws Exception {
		FTPFile[] files = manager.getFileList(patch.getName());

		List<String> appNames = new ArrayList<>();
		for (FTPFile file : files) {
			if (!file.getName().contains(Constants.DESCRIPTOR)) {
				appNames.add(file.getName());
			}
		}
		return appNames;
	}

	public void updateApplication(boolean goBackPreviousApp, UpdateInformations appsInfos) {
		runner = new UpdateRunner(installInfos, goBackPreviousApp, appsInfos);
		runner.start();
	}

	public boolean cancel() {
		if (runner != null && progress.getCurrentAction() == UpdateAction.DOWNLOAD_NEW_VERSION) {
			runner.interrupt();
			return true;
		}
		return false;
	}

	@Override
	public boolean restartServer() {
		try {
			logger.info("Restart the server to take any changes into account.");

			String command = installInfos.getRestartCmdFile();
			if (command == null || command.isEmpty()) {
				return false;
			}

			if (installInfos.isUnix()) {
				command = "./" + command;
			}
			else {
				// TODO: To change windows
				command = "cmd /c start " + command;
				// command = command;
				// command = "cmd /c " + "C:/BPM/Test/Update/vanilla-tomcat-mysql-5.2_RC1/stop-vanilla.cmd";
				// command = "C:/BPM/Test/Update/vanilla-tomcat-mysql-5.2_RC1/stop-vanilla.cmd";
				// ProcessBuilder pb = new ProcessBuilder(command);
				// pb.redirectErrorStream(true);
				// Process process = pb.start();
				//
				// BufferedReader inStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				// while (inStreamReader.readLine() != null) {
				// System.out.println(inStreamReader.readLine());
				// }
				// return false;
			}

			final String restartCommand = command;
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					restartServer(restartCommand);
				}
			}, 4000);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Unable to restart the server : " + e.getMessage());
			return false;
		}
	}

	private void restartServer(String command) {
		try {
			logger.info("Running command '" + command + "'");

			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(command);

			InputStream inputStream = process.getInputStream();
			String processXml = IOUtils.toString(inputStream, "UTF-8");

			BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String lineErr = errStreamReader.readLine();
			while (lineErr != null) {
				logger.info(lineErr);
				lineErr = errStreamReader.readLine();
			}

			logger.info("Restart informations");
			logger.info(processXml);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Unable to restart the server : " + e.getMessage());
		}
	}

	private class UpdateRunner extends Thread {

		private InstallationInformations installInfos;
		private boolean goBackPreviousApp;
		private UpdateInformations appsInfos;
		private String patchName;

		private List<File> oldsPlugins;

		public UpdateRunner(InstallationInformations installInfos, boolean goBackPreviousApp, UpdateInformations appsInfos) {
			this.installInfos = installInfos;
			this.goBackPreviousApp = goBackPreviousApp;
			this.appsInfos = appsInfos;
			this.patchName = appsInfos.getPatchName();
		}

		@Override
		public void run() {
			if (appsInfos.getUpdates() != null) {
				progress = new GlobalProgress(appsInfos.getUpdates().size());

				boolean error = false;
				for (Update update : appsInfos.getUpdates()) {

					progress.incrementUpdate();

					if (update.getAppNames() != null) {
						for (String appName : update.getAppNames()) {

							ApplicationUpdateInformation appInfo = findApp(patchName + "_" + update.getVersion(), appName, appsInfos.getAppInfos());
							if (appInfo != null) {
								if (appInfo.isRuntime()) {
									error = updateRuntime(appInfo);
								}
								else {
									error = updateWebapp(appInfo);
								}

								if (error) {
									break;
								}
							}
							else {
								logger.info("The application '" + appName + "' is not registered on the platform and will not be updated.");
							}
						}
					}

					if (!error) {
						registerUpdate(update);
					}
				}
			}
		}

		private void registerUpdate(Update update) {
			ApplicationManager.getInstance().updatePlatformVersion(update);

			File historyFolder = new File(installInfos.getHistoryPath());
			if (!historyFolder.exists()) {
				logger.info("Create update history folder (" + installInfos.getHistoryPath() + ")");
				historyFolder.mkdirs();
			}

			File f = new File(installInfos.getHistoryPath() + appsInfos.getPatchName() + "_" + update.getVersion() + "_" + getDateAsString());
			try (OutputStreamWriter fos = new OutputStreamWriter(new FileOutputStream(f), "UTF-8")) {
				update.setUpdateDate(new Date());

				new XStream().toXML(update, fos);
				logger.info("Update history saved.");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Unable to save this update history. Maybe some informations in the configuration file are not correct.");
			}
		}

		private ApplicationUpdateInformation findApp(String patchName, String appName, List<ApplicationUpdateInformation> appInfos) {
			if (appsInfos != null) {
				for (ApplicationUpdateInformation appInfo : appInfos) {
					if (appName.equals(appInfo.getAppName() + ".zip")) {
						appInfo.setServerFileName(patchName + "/" + appName);
						return appInfo;
					}
				}
			}
			return null;
		}

		private boolean updateWebapp(ApplicationUpdateInformation appInfo) {
			boolean error = false;

			if (goBackPreviousApp) {
				logger.info("Restoring last application backup if it exist.");
				restoreApplication();
			}
			else {
				logger.info("Download new application's version = " + appInfo.getServerFileName() + ".");
				progress.setCurrentAction(UpdateAction.DOWNLOAD_NEW_VERSION);

				try {
					File newApplication = downloadNewVersion(installInfos.getWebappInstallationPath(), appInfo.getServerFileName(), appInfo.getAppName());
					if (newApplication != null) {
						logger.info("New version downloaded.");

						logger.info("Saving old version.");
						progress.setCurrentAction(UpdateAction.SAVE_OLD_VERSION);
						boolean saveOldVersion = saveOldWebapp(installInfos.getWebappInstallationPath(), appInfo.getAppName());
						if (saveOldVersion) {
							logger.info("Old version saved.");

							logger.info("Undeploying old application (keeping a backup).");
							progress.setCurrentAction(UpdateAction.REMOVE_OLD_APPLICATION);
							boolean removeOldApplication = removeOldWebapp(installInfos.getWebappInstallationPath(), appInfo.getAppName());
							if (removeOldApplication) {
								logger.info("Old application was undeploy.");

								logger.info("Installing the new version.");
								progress.setCurrentAction(UpdateAction.UNZIP_NEW_APPLICATION);
								boolean installSuccess = installNewWebappVersion(installInfos.getWebappInstallationPath(), newApplication);
								if (installSuccess) {
									logger.info("The new version was unzip.");

									logger.info("Deploying the new version.");
									progress.setCurrentAction(UpdateAction.DEPLOY_NEW_APPLICATION);
									boolean deploySuccess = deployNewWebappVersion(appInfo.getAppName());
									if (deploySuccess) {
										logger.info("Installation was successfull.");
										progress.setResult(UpdateResult.SUCCESS);
									}
									else {
										error = true;
										progress.setResult(UpdateResult.FAILED_DEPLOY_NEW_VERSION);
										logger.warn("Application NOT UPDATED. Unable to deploy the new version.");
									}
								}
								else {
									error = true;
									progress.setResult(UpdateResult.FAILED_UNZIP_NEW_VERSION);
									logger.warn("Application NOT UPDATED. Unable to unzip the new version.");
								}
							}
							else {
								error = true;
								progress.setResult(UpdateResult.FAILED_REMOVE_OLD_VERSION);
								logger.warn("Old application could not be removed.");
							}
						}
						else {
							error = true;
							progress.setResult(UpdateResult.FAILED_SAVE_OLD_VERSION);
							logger.warn("Application NOT UPDATED. Unable to save the old version.");
						}
					}
					else {
						error = true;
						progress.setResult(UpdateResult.FAILED_DOWNLOAD_NEW);
						logger.warn("Application NOT UPDATED. File not found or no update available.");
					}
				} catch (Exception e) {
					error = true;
					logger.error("Application NOT UPDATED. " + e.getMessage());
					e.printStackTrace();
					progress.setResult(UpdateResult.FAILED_DOWNLOAD_NEW);
				}
			}

			progress.setDone(true);
			return error;
		}

		private boolean updateRuntime(ApplicationUpdateInformation appInfo) {
			boolean error = false;

			if (goBackPreviousApp) {
				logger.info("Restoring last application backup if it exist.");
				restoreApplication();
			}
			else {
				logger.info("Download new application's version = " + appInfo.getServerFileName() + ".");
				progress.setCurrentAction(UpdateAction.DOWNLOAD_NEW_VERSION);

				try {
					File newApplication = downloadNewVersion(installInfos.getRuntimeInstallationPath(), appInfo.getServerFileName(), appInfo.getAppName());
					if (newApplication != null) {
						logger.info("New version downloaded.");

						logger.info("Installing the new version.");
						progress.setCurrentAction(UpdateAction.UNZIP_NEW_APPLICATION);
						boolean installSuccess = installNewRuntimeVersion(installInfos.getRuntimeInstallationPath(), newApplication);
						if (installSuccess) {
							logger.info("The new version was unzip.");

							logger.info("Saving old plugins.");
							progress.setCurrentAction(UpdateAction.SAVE_OLD_VERSION);
							boolean saveOldVersion = saveOldPlugins(appInfo.getAppName());
							if (saveOldVersion) {
								logger.info("Old version saved.");

								logger.info("Undeploying old plugins (keeping a backup).");
								progress.setCurrentAction(UpdateAction.REMOVE_OLD_APPLICATION);
								boolean removeOldApplication = removeOldPlugins(installInfos.getRuntimeInstallationPath(), oldsPlugins);
								if (removeOldApplication) {
									logger.info("Old application was undeploy.");

									logger.info("Deploying the new version.");
									progress.setCurrentAction(UpdateAction.DEPLOY_NEW_APPLICATION);
									boolean deploySuccess = true;
									if (deploySuccess) {
										logger.info("Installation was successfull.");
										progress.setResult(UpdateResult.SUCCESS);
									}
									else {
										error = true;
										progress.setResult(UpdateResult.FAILED_DEPLOY_NEW_VERSION);
										logger.warn("Application NOT UPDATED. Unable to deploy the new version.");
									}
								}
								else {
									error = true;
									progress.setResult(UpdateResult.FAILED_REMOVE_OLD_VERSION);
									logger.warn("Old application could not be removed.");
								}
							}
							else {
								error = true;
								progress.setResult(UpdateResult.FAILED_SAVE_OLD_VERSION);
								logger.warn("Application NOT UPDATED. Unable to save the old version.");
							}
						}
						else {
							error = true;
							progress.setResult(UpdateResult.FAILED_UNZIP_NEW_VERSION);
							logger.warn("Application NOT UPDATED. Unable to unzip the new version.");
						}
					}
					else {
						error = true;
						progress.setResult(UpdateResult.FAILED_DOWNLOAD_NEW);
						logger.warn("Application NOT UPDATED. File not found or no update available.");
					}
				} catch (Exception e) {
					error = true;
					logger.error("Application NOT UPDATED. " + e.getMessage());
					e.printStackTrace();
					progress.setResult(UpdateResult.FAILED_DOWNLOAD_NEW);
				}
			}

			progress.setDone(true);
			return error;
		}

		private void restoreApplication() {
			// TODO Auto-generated method stub

		}

		private File downloadNewVersion(String installationPath, String serverName, String appName) throws Exception, IOException {
			File newAppFile = new File(installationPath + appName + ".zip");
			if (!newAppFile.exists()) {
				newAppFile.createNewFile();
			}

			FTPManager ftpManager = new FTPManager(installInfos.getUpdateUrl(), installInfos.getUpdatePort(), true, installInfos.getUpdateLogin(), installInfos.getUpdatePassword());
			try (FileOutputStream fileOutputStream = new FileOutputStream(newAppFile)) {
				ftpManager.connect();
				ftpManager.downloadFile(serverName, fileOutputStream, progress.getCurrentProgress());
				ftpManager.disconnect();

				return newAppFile;
			} catch (Exception e) {
				ftpManager.disconnect();

				e.printStackTrace();
				throw new Exception("Unable to download the new application : " + e.getMessage());
			}
		}

		private boolean saveOldWebapp(String installationPath, String appName) throws IOException {
			String oldApplicationName = appName + "_old_" + getDateAsString() + ".zip";

			logger.info("Create backup folder (" + installInfos.getSavePath() + ")");
			File saveFolder = new File(installInfos.getSavePath());
			if (!saveFolder.exists()) {
				saveFolder.mkdirs();
			}

			logger.info("Zip old application for backup (" + oldApplicationName + ")");
			File oldAppFile = new File(installInfos.getSavePath() + oldApplicationName);
			if (!oldAppFile.exists()) {
				oldAppFile.createNewFile();
			}

			progress.setProgress(50);

			File webapp = new File(installationPath + appName);
			if (webapp.exists() && webapp.isDirectory()) {
				try (FileOutputStream fos = new FileOutputStream(oldAppFile); ZipOutputStream zos = new ZipOutputStream(fos);) {
					zipWebapp(webapp.getParentFile(), webapp, zos);

					progress.setProgress(100);

					logger.info("Application backup created with success.");
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			logger.warn("Old application (Path = '" + webapp.getPath() + "' does not exist. Maybe some informations in the configuration file are not correct.");

			progress.setProgress(100);

			logger.info("Could not create application backup.");
			return false;
		}

		private boolean saveOldPlugins(String appName) throws IOException {
			String oldApplicationName = appName + "_old_" + getDateAsString() + ".zip";

			File saveFolder = new File(installInfos.getSavePath());
			if (!saveFolder.exists()) {
				saveFolder.mkdirs();
			}

			logger.info("Zip old plugins for backup (" + oldApplicationName + ")");
			File oldAppFile = new File(installInfos.getSavePath() + oldApplicationName);
			if (!oldAppFile.exists()) {
				oldAppFile.createNewFile();
			}

			progress.setProgress(50);

			if (oldsPlugins != null && !oldsPlugins.isEmpty()) {
				try (FileOutputStream fos = new FileOutputStream(oldAppFile); ZipOutputStream zos = new ZipOutputStream(fos);) {
					zipPlugins(oldsPlugins, zos);

					progress.setProgress(100);

					logger.info("Plugins backup created with success.");
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			logger.warn("Old plugins does not exist. Maybe some informations in the configuration file are not correct.");

			progress.setProgress(100);

			logger.info("Could not create plugins backup.");
			return true;
		}

		private boolean removeOldPlugins(String installationPath, List<File> oldsPlugins) {
			logger.info("Undeploy old plugins");

			progress.setProgress(0);

			try {
				progress.setProgress(50);

				undeployPlugins(installationPath, oldsPlugins);

				progress.setProgress(100);

				logger.info("Olds plugins undeploy.");
				return true;
			} catch (Exception e) {
				e.printStackTrace();

				progress.setProgress(100);

				logger.warn("Could not undeploy olds plugins.");
				return false;
			}
		}

		private void undeployPlugins(String installationPath, List<File> oldFilePlugins) throws Exception {
			for (File oldPlugin : oldFilePlugins) {
				if (oldPlugin.isDirectory()) {
					FileUtils.deleteDirectory(oldPlugin);
				}
				else if (!oldPlugin.delete()) {
					logger.warn("Unable to delete old plugin '" + oldPlugin.getName() + "'");
				}
			}
		}

		private void zipWebapp(File webapp, File folder, ZipOutputStream zos) throws FileNotFoundException, IOException {
			for (File file : folder.listFiles()) {
				if (file.isDirectory()) {
					zipWebapp(webapp, file, zos);
				}
				else {
					addFileToZip(webapp, file, zos);
				}
			}
		}

		private void addFileToZip(File webapp, File file, ZipOutputStream zos) throws FileNotFoundException, IOException {
			FileInputStream fis = new FileInputStream(file);

			String zipFilePath = file.getCanonicalPath().substring(webapp.getCanonicalPath().length() + 1, file.getCanonicalPath().length());
			logger.debug("Writing '" + zipFilePath + "' to zip file with Current Dir = " + webapp.getName() + " and Path = " + file.getCanonicalPath());
			ZipEntry zipEntry = new ZipEntry(zipFilePath);
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
			fis.close();
		}

		private void zipPlugins(List<File> plugins, final ZipOutputStream zos) throws FileNotFoundException, IOException {
			for (File plugin : plugins) {
				if (plugin.isDirectory()) {
					final Path folder = Paths.get(plugin.getPath());
					
					logger.debug("Writing folder '" + plugin.getName() + "' to zip file.");
					final String prefix = plugin.getName();
					
					Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
						
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							zos.putNextEntry(new ZipEntry(prefix + "/" + folder.relativize(file).toString()));
							Files.copy(file, zos);
							zos.closeEntry();
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
							zos.putNextEntry(new ZipEntry(prefix + "/" + folder.relativize(dir).toString() + "/"));
							zos.closeEntry();
							return FileVisitResult.CONTINUE;
						}
					});
				}
				else {
					addPluginToZip(plugin, zos);
				}
			}
		}

		private void addPluginToZip(File plugin, ZipOutputStream zos) throws FileNotFoundException, IOException {
			FileInputStream fis = new FileInputStream(plugin);

			logger.debug("Writing '" + plugin.getName() + "' to zip file.");
			ZipEntry zipEntry = new ZipEntry(plugin.getName());
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
			fis.close();
		}

		private boolean removeOldWebapp(String installationPath, String appName) {
			logger.info("Undeploy old application (" + appName + ")");

			progress.setProgress(50);

			try {
				// undeployWebapp(appName);
				File webapp = new File(installationPath + appName);
				if (webapp.exists() && webapp.isDirectory()) {
					FileUtils.deleteDirectory(webapp);

					logger.info("Old application '" + appName + "' deleted with success.");
				}

				progress.setProgress(100);

				logger.info("Old application deleted.");
				return true;
			} catch (Exception e) {
				e.printStackTrace();

				progress.setProgress(100);

				logger.warn("Could not delete old application.");
				return false;
			}
		}

		private boolean installNewWebappVersion(String installationPath, File newAppFile) throws ServiceException {
			byte[] buffer = new byte[1024];

			progress.setProgress(50);

			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(newAppFile))) {
				ZipEntry ze = zis.getNextEntry();
				while (ze != null) {

					String fileName = ze.getName();
					File newFile = new File(installationPath + fileName);

					logger.debug("File unzip : " + newFile.getAbsoluteFile());

					new File(newFile.getParent()).mkdirs();

					if (ze.isDirectory()) {
						newFile.mkdir();
					}
					else {
						FileOutputStream fos = new FileOutputStream(newFile);

						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}

						fos.close();
					}
					ze = zis.getNextEntry();
				}

				zis.closeEntry();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new ServiceException("Unable to unzip the new application : " + ex.getMessage());
			}

			logger.info("Deleting application's zip");
			if (!newAppFile.delete()) {
				logger.info("Unable to delete ");
			}

			progress.setProgress(100);

			logger.info("New application unziped.");
			return true;
		}

		private boolean installNewRuntimeVersion(String installationPath, File newAppFile) throws Exception {
			this.oldsPlugins = new ArrayList<>();

			byte[] buffer = new byte[1024];

			progress.setProgress(50);

			File folder = new File(installationPath);
			List<File> plugins = new ArrayList<>();
			if (folder.exists() && folder.isDirectory()) {
				plugins = Arrays.asList(folder.listFiles());
			}

			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(newAppFile))) {
				ZipEntry ze = zis.getNextEntry();
				while (ze != null) {

					String fileName = ze.getName();
					File newFile = new File(installationPath + fileName);

					File oldPlugin = findOldPlugin(plugins, fileName);
					if (oldPlugin != null) {
						if (!oldsPlugins.contains(oldPlugin)) {
							oldsPlugins.add(oldPlugin);
						}
					}

					logger.debug("File unzip : " + newFile.getAbsoluteFile());

					new File(newFile.getParent()).mkdirs();

					if (ze.isDirectory()) {
						newFile.mkdir();
					}
					else {
						FileOutputStream fos = new FileOutputStream(newFile);

						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}

						fos.close();
					}
					ze = zis.getNextEntry();
				}

				zis.closeEntry();

				progress.setProgress(100);

				logger.info("New plugins unziped.");
				return true;
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new Exception("Unable to unzip the plugins : " + ex.getMessage());
			}
		}

		private File findOldPlugin(List<File> plugins, String plugin) {
			String pluginName = plugin.split("_")[0];

			logger.info("Looking for " + pluginName);

			for (File file : plugins) {
				String fileName = file.getName().split("_")[0];

				logger.info("Testing with " + fileName);

				if (pluginName.equals(fileName)) {
					return file;
				}
			}
			return null;
		}

		private boolean deployNewWebappVersion(String appName) throws ServiceException {
			logger.info("Deploy new application (" + appName + ")");

			progress.setProgress(50);

			try {
				deploy(appName);

				progress.setProgress(100);

				logger.info("New application deployed.");
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ServiceException("Unable to deploy the new application : " + ex.getMessage());
			}
		}

		private void deploy(String appName) throws ClientProtocolException, IOException, ServiceException {
			String param = "text/deploy?path=/" + appName;
			String url = installInfos.getManagerUrl() + param;

			BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(installInfos.getManagerLogin(), installInfos.getManagerPassword()));

			HttpGet req = new HttpGet(url);
			String response = executeRequest(req, credsProvider);

			if (response.contains("ECHEC")) {
				logger.error(response);
				throw new ServiceException(response);
			}
			else {
				logger.info(response);
			}
		}

		// public void undeployWebapp(String appName) throws ClientProtocolException, IOException, ServiceException {
		// String param = "text/undeploy?path=/" + appName;
		// String url = installInfos.getManagerUrl() + param;
		//
		// BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		// credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(installInfos.getManagerLogin(), installInfos.getManagerPassword()));
		//
		// HttpGet req = new HttpGet(url);
		// String response = executeRequest(req, credsProvider);
		//
		// if (response.contains("ECHEC")) {
		// logger.error(response);
		// throw new ServiceException(response);
		// }
		// else {
		// logger.info(response);
		// }
		// }

		private String executeRequest(HttpRequestBase requestBase, CredentialsProvider credsProvider) throws ClientProtocolException, IOException {
			CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			HttpResponse response = client.execute(requestBase);
			HttpEntity responseEntity = response.getEntity();

			InputStream responseStream = responseEntity.getContent();
			String result = IOUtils.toString(responseStream, "UTF-8");
			responseStream.close();

			return result;
		}

		private String getDateAsString() {
			return sdf.format(new Date());
		}

		// OSGI Part not working (the redeploy), for now we will use a script to delete the work folder and restart all the tomcat

		// private String executeRequest(HttpRequestBase requestBase) throws ClientProtocolException, IOException {
		// CloseableHttpClient client = HttpClients.custom().build();
		// HttpResponse response = client.execute(requestBase);
		// HttpEntity responseEntity = response.getEntity();
		//
		// InputStream responseStream = responseEntity.getContent();
		// String result = IOUtils.toString(responseStream, "UTF-8");
		// responseStream.close();
		//
		// return result;
		// }

		// TODO: Not working for now
		// private boolean deployNewRuntimeVersion(String vanillaRuntimeUrl) throws Exception {
		// logger.info("Redeploy Runtime (" + vanillaRuntimeUrl + ")");
		//
		// progress.setProgress(50);
		//
		// try {
		// redeploy(vanillaRuntimeUrl);
		//
		// progress.setProgress(100);
		//
		// logger.info("Runtime redeployed.");
		// return true;
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// throw new Exception("Unable to redeploy the Runtime : " + ex.getMessage());
		// }
		// }

		// TODO: Not working for now
		// private void redeploy(String vanillaRuntimeUrl) throws ClientProtocolException, IOException, Exception {
		// String param = "/sp_redeploy";
		// String url = vanillaRuntimeUrl + param;
		//
		// HttpGet req = new HttpGet(url);
		// String response = executeRequest(req);
		//
		// if (response.contains("ECHEC")) {
		// logger.error(response);
		// throw new Exception(response);
		// }
		// else {
		// logger.info(response);
		// }
		// }

		// TODO: Not working for now
		// private boolean removeOldPlugins(String installationPath, List<File> oldsPlugins) {
		// logger.info("Undeploy old plugins");
		//
		// progress.setProgress(0);
		//
		// try {
		// undeployPlugins(installationPath, oldsPlugins);
		//
		// progress.setProgress(50);
		//
		// try {
		// logger.info("Shutdown OSGI");
		// shutdownOsgi();
		//
		// Thread.sleep(20000);
		// } catch(Exception e) {
		// logger.info("OSGI should be shutdown. Waiting 10 seconds.");
		//
		// try {
		// Thread.sleep(20000); //1000 milliseconds is one second.
		// } catch(InterruptedException ex) { }
		// }
		//
		// progress.setProgress(100);
		//
		// logger.info("Olds plugins undeploy.");
		// return true;
		// } catch (Exception e) {
		// e.printStackTrace();
		//
		// progress.setProgress(100);
		//
		// logger.warn("Could not undeploy olds plugins.");
		// return false;
		// }
		// }

		// TODO: Not working for now
		// private void undeployPlugins(String installationPath, List<File> oldFilePlugins) throws Exception {
		// List<String> oldPlugins = new ArrayList<>();
		// for (File oldPlugin : oldFilePlugins) {
		// oldPlugins.add(oldPlugin.getName());
		// }
		//
		// RemoteRuntimeUpdateManager runtimeUpdateManager = new RemoteRuntimeUpdateManager(installInfos.getRuntimeUrl(), installInfos.getRuntimeLogin(), installInfos.getRuntimePassword());
		// runtimeUpdateManager.undeploy(oldPlugins);
		//
		// for (File oldPlugin : oldFilePlugins) {
		// if(!oldPlugin.delete()) {
		// logger.warn("Unable to delete old plugin '" + oldPlugin.getName() + "'");
		// }
		// }
		// }

		// TODO: Not working for now
		// private void shutdownOsgi() throws Exception {
		// RemoteRuntimeUpdateManager runtimeUpdateManager = new RemoteRuntimeUpdateManager(installInfos.getRuntimeUrl(), installInfos.getRuntimeLogin(), installInfos.getRuntimePassword());
		// runtimeUpdateManager.shutdownOsgi();
		// }
	}
}
