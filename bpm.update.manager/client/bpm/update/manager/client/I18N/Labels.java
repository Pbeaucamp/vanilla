package bpm.update.manager.client.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Labels extends Constants {

	public static Labels lblCnst = (Labels) GWT.create(Labels.class);
	
	String Cancel();
	String Close();
	String UpdateApplication();
	String DownloadNewVersion();
	String SaveOldVersion();
	String RemoveOldVersion();
	String InstallNewApplication();
	String UnzipNewApplication();
	String DeployNewApplication();
	String FailedDownloadNewVersion();
	String FailedSaveOldVersion();
	String FailedRemoveOldVersion();
	String FailedInstallNewVersion();
	String FailedUnzipNewVersion();
	String FailedDeployNewVersion();
	String SuccessInstallation();
	String RelaunchBrowser();
	String Loading();
	String Update();
	String ApplicationsRegistered();
	String NoApplicationRegistered();
	String RefreshAtEndInstall();
	String CancelImpossible();
	String Maj();
	String On();
	String UpdatesAvailable();
	String PatchNotes();
	String NoUpdateAvailable();
	String ModificationProperties();
	String DatabaseScripts();
	String ThisUpdateNeedModification();
	String RestartingServerIn();
	String Seconds();
	String RestartServer();
	String RelaunchBrowserInFewMinutes();
	String RestartNotPossible();
	String UpdatesHistory();
	String NoUpdateHistory();
	String UnknownDate();
	String ApplicationsUpdated();
}
