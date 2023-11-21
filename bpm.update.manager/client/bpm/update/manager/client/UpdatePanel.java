package bpm.update.manager.client;

import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.update.manager.api.beans.GlobalProgress;
import bpm.update.manager.api.beans.Update;
import bpm.update.manager.api.beans.UpdateAction;
import bpm.update.manager.api.beans.UpdateInformations;
import bpm.update.manager.api.beans.UpdateProgress;
import bpm.update.manager.client.I18N.Labels;
import bpm.update.manager.client.services.UpdateService;
import bpm.update.manager.client.utils.ProgressBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class UpdatePanel extends Composite {
	
	private DateTimeFormat df = DateTimeFormat.getFormat("yyyy/MM/dd - HH:mm");

	private static TextEmailDialogUiBinder uiBinder = GWT.create(TextEmailDialogUiBinder.class);

	interface TextEmailDialogUiBinder extends UiBinder<Widget, UpdatePanel> {
	}

	interface MyStyle extends CssResource {
		String progressBar();
		String lblWarningColor();
	}

	@UiField
	MyStyle style;

	@UiField
	Label txtUpdateWarning, lblMessage, lblMessageGlobal, lblDownload;

	@UiField
	SimplePanel progressPanel, globalProgressPanel;

	@UiField
	HTMLPanel panelPatchs, panelPatchsDescription, panelPatchsHistoryDescription, panelContentProgress;

	@UiField
	Button btnUpdate, btnRestart;

	private IWait waitPanel;

	private UpdateInformations updateInfos;

	private ProgressBar globalProgressBar, progressBar;
	private int currentUpdate = -1;
	private UpdateAction currentAction;

	private boolean success = false;
	private int restartTimer;

	public UpdatePanel(IWait waitPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;

		globalProgressBar = new ProgressBar(0, 100);
		globalProgressBar.addStyleName(style.progressBar());
		globalProgressPanel.setWidget(globalProgressBar);

		progressBar = new ProgressBar(0, 100);
		progressBar.addStyleName(style.progressBar());
		progressPanel.setWidget(progressBar);

		panelContentProgress.setVisible(false);
		btnUpdate.setEnabled(false);
		txtUpdateWarning.setVisible(false);

		checkAvailableUpdates();
	}

	private void checkAvailableUpdates() {
		waitPanel.showWaitPart(true);

		UpdateService.Connect.getInstance().checkUpdates(new GwtCallbackWrapper<UpdateInformations>(waitPanel, true) {

			@Override
			public void onSuccess(UpdateInformations result) {
				updateInfos = result;
				if (result.hasUpdate()) {
					btnUpdate.setEnabled(true);

					buildUpdatesDescription(result);
				}
				
				if (result.getUpdateHistory() != null && !result.getUpdateHistory().isEmpty()) {
					buildUpdatesHistoryDescription(result);
				}
			}
		}.getAsyncCallback());
	}

	private void buildUpdatesDescription(UpdateInformations result) {
		txtUpdateWarning.setVisible(result != null && containsPropertiesOrScriptsChange(result));
		
		panelPatchsDescription.clear();

		for (Update update : result.getUpdates()) {
			DisclosurePanel panelUpdate = new DisclosurePanel(update.getVersion());
			panelUpdate.add(new UpdateDescriptionPanel(update));
			panelPatchsDescription.add(panelUpdate);
		}
	}

	private void buildUpdatesHistoryDescription(UpdateInformations result) {
		panelPatchsHistoryDescription.clear();

		for (Update update : result.getUpdateHistory()) {
			DisclosurePanel panelUpdate = new DisclosurePanel(update.getVersion() + " - (" + (update.getUpdateDate() != null ? df.format(update.getUpdateDate()) : Labels.lblCnst.UnknownDate()) + ")");
			panelUpdate.add(new UpdateDescriptionPanel(update));
			panelPatchsHistoryDescription.add(panelUpdate);
		}
	}

	private String buildPatchDescription(UpdateInformations result) {
		StringBuffer buf = new StringBuffer();
		for (Update update : result.getUpdates()) {
			buf.append(" - Patch " + update.getVersion() + "\n");
		}
		return buf.toString();
	}

	@UiHandler("btnRestart")
	public void onRestartClick(ClickEvent event) {
		btnRestart.setEnabled(false);
		
		restartServer();
	}

	@UiHandler("btnUpdate")
	public void onUpdateClick(ClickEvent event) {
		update();
	}

	private boolean containsPropertiesOrScriptsChange(UpdateInformations updateInfos) {
		if (updateInfos.getUpdates() != null) {
			for (Update update : updateInfos.getUpdates()) {
				if ((update.getProperties() != null && !update.getProperties().isEmpty()) || (update.getScripts() != null && !update.getScripts().isEmpty())) {
					return true;
				}
			}
		}
		return false;
	}

	private void update() {
		panelPatchs.setVisible(false);
		panelContentProgress.setVisible(true);
		btnRestart.setVisible(false);
		btnUpdate.setEnabled(false);

		UpdateService.Connect.getInstance().updateApplication(new GwtCallbackWrapper<Void>(null, false) {

			@Override
			public void onSuccess(Void result) {
				btnUpdate.setVisible(false);

				checkProgress();
			}
		}.getAsyncCallback());
	}

	private void checkProgress() {
		UpdateService.Connect.getInstance().getGlobalProgress(new GwtCallbackWrapper<GlobalProgress>(null, false) {

			@Override
			public void onSuccess(GlobalProgress result) {
				if (result != null && !result.isDone()) {
					updateUI(result);

					checkProgress();
				}
				else if (result != null) {
					updateUI(result);
				}
			}
		}.getAsyncCallback());
	}

	private void updateUI(GlobalProgress result) {
		if (result.getUpdateRunning() != currentUpdate) {
			this.currentUpdate = result.getUpdateRunning();

			globalProgressBar.setProgress(result.getUpdateProgress());
			lblMessageGlobal.setText(Labels.lblCnst.Maj() + " " + (result.getUpdateRunning() + 1) + " " + Labels.lblCnst.On() + " " + result.getNbTotalUpdate());
		}

		UpdateProgress currentProgress = result.getCurrentProgress();
		if (currentAction == null || currentProgress.getCurrentAction() != currentAction) {
			this.currentAction = result.getCurrentAction();

			switch (result.getCurrentAction()) {
			case DOWNLOAD_NEW_VERSION:
				lblMessage.setText(Labels.lblCnst.DownloadNewVersion());
				break;
			case SAVE_OLD_VERSION:
				btnUpdate.setVisible(false);

				lblMessage.setText(Labels.lblCnst.SaveOldVersion());
				break;
			case REMOVE_OLD_APPLICATION:
				btnUpdate.setVisible(false);

				lblMessage.setText(Labels.lblCnst.RemoveOldVersion());
				break;
			case UNZIP_NEW_APPLICATION:
				btnUpdate.setVisible(false);

				lblMessage.setText(Labels.lblCnst.UnzipNewApplication());
				break;
			case DEPLOY_NEW_APPLICATION:
				btnUpdate.setVisible(false);

				lblMessage.setText(Labels.lblCnst.DeployNewApplication());
				break;
			default:
				break;
			}
		}

		progressBar.setProgress(currentProgress.getPercentDownload());
		lblDownload.setText(currentProgress.getDownloadMessage());

		if (result.isDone()) {
			btnUpdate.setVisible(false);
			globalProgressBar.setProgress(result.getUpdateProgress());

			switch (currentProgress.getResult()) {
			case FAILED_DOWNLOAD_NEW:
				lblMessage.setText(Labels.lblCnst.FailedDownloadNewVersion());
				break;
			case FAILED_SAVE_OLD_VERSION:
				lblMessage.setText(Labels.lblCnst.FailedSaveOldVersion());
				break;
			case FAILED_REMOVE_OLD_VERSION:
				lblMessage.setText(Labels.lblCnst.FailedRemoveOldVersion());
				break;
			case FAILED_UNZIP_NEW_VERSION:
				lblMessage.setText(Labels.lblCnst.FailedUnzipNewVersion());
				break;
			case FAILED_DEPLOY_NEW_VERSION:
				lblMessage.setText(Labels.lblCnst.FailedDeployNewVersion());
				break;
			case SUCCESS:
				lblMessage.setText(Labels.lblCnst.SuccessInstallation());

				success = true;
				restartServer();
				break;
			default:
				break;
			}
		}
	}
	
	private void restartServer() {
		this.restartTimer = 5;
		
		txtUpdateWarning.setVisible(true);
		txtUpdateWarning.removeStyleName(style.lblWarningColor());
		txtUpdateWarning.setText("");
		
		restartServerAfterTimer();
	}

	private void restartServerAfterTimer() {
		
		Timer t = new Timer() {
			@Override
			public void run() {
				txtUpdateWarning.setText(Labels.lblCnst.RestartingServerIn() + " " + restartTimer + " " + Labels.lblCnst.Seconds() + "...");
				restartTimer--;
				
				if (restartTimer == 0) {
					UpdateService.Connect.getInstance().restartServer(new GwtCallbackWrapper<Boolean>(null, false) {
						
						@Override
						public void onSuccess(Boolean result) {
							if (result) {
								txtUpdateWarning.setText(Labels.lblCnst.RelaunchBrowserInFewMinutes());
							}
							else {
								txtUpdateWarning.setText(Labels.lblCnst.RestartNotPossible());
							}
						}
					}.getAsyncCallback());
				}
				else {
					restartServerAfterTimer();
				}
			}
		};
		t.schedule(1000);
	}

	/**
	 * This method aim to dynamically change the current URL
	 * 
	 * @param url
	 *            the new URL string
	 */
	public static native void changeCurrURL(String url)/*-{
		$wnd.location.replace(url);
	}-*/;
}
