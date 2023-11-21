package bpm.gwt.commons.client.viewer;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.popup.FormatPopup;
import bpm.gwt.commons.client.popup.IChangeFormat;
import bpm.gwt.commons.client.popup.ReportPopup;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.client.viewer.param.ParamDisplayPanel;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ReportGroupViewer extends Viewer implements IChangeFormat, IReportViewer {
	private static final int REPORT_PANEL_SIZE = 215;

	private VanillaViewer vanillaViewer;
	private InfoUser infoUser;

	private ParamDisplayPanel paramDisplayPanel;

	private LaunchReportInformations itemInfo;
	private LaunchReportInformations currentItemInfo;

	public ReportGroupViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, InfoUser infoUser, boolean isDisco) {
		super(vanillaViewer);
		this.vanillaViewer = vanillaViewer;
		this.infoUser = infoUser;

		reportPanel.getElement().getStyle().setLeft(230, Unit.PX);

		paramDisplayPanel = new ParamDisplayPanel(this);
		this.paramPanel.setWidget(paramDisplayPanel);

		vanillaViewer.launchReport(this, item, selectedGroup, isDisco, false);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if (itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}
		btnRun.setVisible(true);
		btnExport.setVisible(true);
		btnShare.setVisible(true);
		btnReports.setVisible(true);
		btnFormats.setVisible(true);
		btnComment.setVisible(true);
	}

	@Override
	public void runItem(final LaunchReportInformations itemInfo) {
		showWaitPart(true);

		defineToolbar(itemInfo);

		LaunchReportInformations itemsToRun = itemInfo;
		if (itemInfo.getReports() == null || itemInfo.getReports().isEmpty()) {
			itemInfo.setToRun(true);
			itemsToRun = this.itemInfo;
		}

		ReportingService.Connect.getInstance().runReports(itemsToRun, new AsyncCallback<LaunchReportInformations>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.ErrorRunningBirt());
			}

			@Override
			public void onSuccess(LaunchReportInformations result) {
				ReportGroupViewer.this.itemInfo = result;

				showWaitPart(false);

				if (result.getReports() != null && !result.getReports().isEmpty()) {
					LaunchReportInformations itemToDisplay = result.getReports().get(0);
					if (itemInfo.getItem() != null) {
						for (LaunchReportInformations report : result.getReports()) {
							if (report.getItem().getId() == itemInfo.getItem().getId()) {
								itemToDisplay = report;
								break;
							}
						}
					}

					loadReport(itemToDisplay);
				}
			}
		});
	}

	private void loadReport(LaunchReportInformations launchReportInformation) {
		this.currentItemInfo = launchReportInformation;

		refreshParamPart();
		showReport(currentItemInfo.getReportKey(), currentItemInfo.getOutputs().get(0));

		btnFormats.setText(currentItemInfo.getOutputs().get(0));
		btnReports.setText(currentItemInfo.getItem().getName());
	}

	public void showReport(String reportName, String selectedFormat) {
		reportPanel.setVisible(true);

		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + reportName + "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
		reportFrame.setUrl(fullUrl);
	}

	@Override
	public void changeReport(LaunchReportInformations itemInfo) {
		loadReport(itemInfo);
	}

	@Override
	public void changeFormat(String selectedFormat) {
		btnFormats.setText(selectedFormat);

		showReport(currentItemInfo.getReportKey(), selectedFormat);
	}

	public void refreshParamPart() {
		paramDisplayPanel.buildContent(currentItemInfo);
	}

	@Override
	public void onRunClick(ClickEvent event) {
		vanillaViewer.launchReport(this, itemInfo.getItem(), itemInfo.getSelectedGroup(), itemInfo.getTypeRun() == TypeRun.DISCO, false);
	}

	@Override
	public void onExportClick(ClickEvent event) {
		String selectedFormat = btnFormats.getText();

		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + currentItemInfo.getReportKey() + "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
		ToolsGWT.doRedirect(fullUrl);
	}

	@Override
	public void onReportChange(ClickEvent event) {
		if (currentItemInfo != null) {
			ReportPopup reportPopup = new ReportPopup(this, itemInfo.getReports());
			reportPopup.setPopupPosition(event.getClientX(), event.getClientY());
			reportPopup.show();
		}
	}

	@Override
	public void onFormatChange(ClickEvent event) {
		if (currentItemInfo != null) {
			FormatPopup displayPopup = new FormatPopup(this, currentItemInfo.getOutputs());
			displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
			displayPopup.show();
		}
	}

	@Override
	public void onShareClick(ClickEvent event) {
		boolean isConnectedToAklabox = infoUser != null ? infoUser.isConnected(IRepositoryApi.AKLABOX) : false;

		SharePopup sharePopup = new SharePopup(this, isConnectedToAklabox);
		sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
		sharePopup.show();
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, itemInfo.getItem().getId(), TypeCollaboration.ITEM_NOTE, itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}

	@Override
	public void expendParam() {
		slideAnimation.run(1000);
	}

	@Override
	public void expendComment() { }

	private Animation slideAnimation = new Animation() {

		@Override
		protected void onUpdate(double progress) {
			int width = REPORT_PANEL_SIZE;
			if (!paramDisplayPanel.isExpend()) {
				int progressValue = (int) (-width * (1 - progress));
				int progressReport = (int) (-width * progress) - 16;
				DOM.setStyleAttribute(paramDisplayPanel.getElement(), "marginLeft", progressValue + "px");
				DOM.setStyleAttribute(reportPanel.getElement(), "left", -progressReport + "px");
				paramDisplayPanel.setImgExpendLeft(-progressValue);
			}
			else {
				int progressValue = (int) (-width * progress);
				int progressReport = (int) (-width * (1 - progress)) - 16;
				DOM.setStyleAttribute(paramDisplayPanel.getElement(), "marginLeft", progressValue + "px");
				DOM.setStyleAttribute(reportPanel.getElement(), "left", -progressReport + "px");
				paramDisplayPanel.setImgExpendLeft(-progressValue);
			}
		}

		@Override
		protected void onComplete() {
			paramDisplayPanel.setExpend(!paramDisplayPanel.isExpend());
			if (paramDisplayPanel.isExpend()) {
				int leftSize = REPORT_PANEL_SIZE + 15;
				DOM.setStyleAttribute(paramDisplayPanel.getElement(), "marginLeft", "0px");
				DOM.setStyleAttribute(reportPanel.getElement(), "left", leftSize + "px");
				paramDisplayPanel.setImgExpendLeft(0);
			}
			else {
				int leftSize = -REPORT_PANEL_SIZE;
				DOM.setStyleAttribute(paramDisplayPanel.getElement(), "marginLeft", leftSize + "px");
				DOM.setStyleAttribute(reportPanel.getElement(), "left", 16 + "px");
				paramDisplayPanel.setImgExpendLeft(REPORT_PANEL_SIZE);
			}
		};
	};

	@Override
	public void openShare(TypeShare typeShare) {
		List<LaunchReportInformations> reportsReady = new ArrayList<LaunchReportInformations>();
		for (LaunchReportInformations report : itemInfo.getReports()) {
			if (report.hasBeenRun()) {
				reportsReady.add(report);
			}
		}

		switch (typeShare) {
		case AKLABOX:
			// AklaboxShareDialog dial = new
			// AklaboxShareDialog(itemInfo.getItem(), reportKey,
			// itemInfo.getOutputs());
			// dial.center();
			break;
		case EMAIL:
			MailShareDialog mailDialog = new MailShareDialog(ReportGroupViewer.this, null, itemInfo, infoUser.getAvailableGroups());
			mailDialog.center();
			break;

		default:
			break;
		}
	}

	@Override
	public void setItemInfo(LaunchReportInformations itemInfo) {
	}
}
