package bpm.gwt.commons.client.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.popup.GroupPopup;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.viewer.aklabox.AklaboxShareDialog;
import bpm.gwt.commons.client.viewer.comments.CommentDisplayPanel;
import bpm.gwt.commons.client.viewer.dialog.CmisShareWizard;
import bpm.gwt.commons.client.viewer.dialog.FdExportDialog;
import bpm.gwt.commons.client.viewer.dialog.HistorizeOptionDialog;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.client.viewer.widget.ICanExpand;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FdVanillaViewer extends Viewer implements IReportViewer {
	private static final int REPORT_PANEL_SIZE = 215;
	private static final String DASHBOARD_UUID = "uuid";

	private InfoUser infoUser;
	private LaunchReportInformations itemInfo;
	private CommentDisplayPanel commentDisplayPanel;

	private String dashboardUrl;
	private PanelAnimation commentAnimation;

	public FdVanillaViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, InfoUser infoUser, boolean isDisco) {
		super(vanillaViewer);
		this.infoUser = infoUser;

		commentDisplayPanel = new CommentDisplayPanel(this, infoUser.getUser());
		this.commentPanel.setWidget(commentDisplayPanel);
		commentAnimation = new PanelAnimation(commentDisplayPanel);

		vanillaViewer.launchReport(this, item, selectedGroup, isDisco, false);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if (itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}

		if (infoUser != null && infoUser.canExportDashboard()) {
			btnExport.setVisible(true);
		}
		btnOpenDashInNewTab.setVisible(true);
		btnHistorize.setVisible(true);
		btnShare.setVisible(true);
		btnGroups.setVisible(true);
		btnGroups.setText(itemInfo.getSelectedGroup().getName());
	}
	
	@Override
	public void onHistorizeClick(ClickEvent event) {
		exportDashboard(true);
	}

	@Override
	public void runItem(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;

		showWaitPart(true);

		defineToolbar(itemInfo);

		ReportingService.Connect.getInstance().runDashboard(itemInfo, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedDashboard());
			}

			@Override
			public void onSuccess(String url) {
				showWaitPart(false);

				refreshCommentPart();
				showDashboard(url);
			}
		});
	}

	private void showDashboard(String dashboardUrl) {
		this.dashboardUrl = dashboardUrl;
		reportFrame.setUrl(dashboardUrl);
	}
	
	public void refreshCommentPart() {
		commentDisplayPanel.buildContent(itemInfo);
	}

	@Override
	public void changeGroup(Group selectedGroup) {
		itemInfo.setSelectedGroup(selectedGroup);

		btnGroups.setText(selectedGroup.getName());

		runItem(itemInfo);
	}

	private void exportDashboard(final boolean historize) {
		HashMap<String, List<String>> maps = getQueryParams(dashboardUrl);
		final String uuid = maps.get(DASHBOARD_UUID).get(0);
		if (uuid == null || uuid.isEmpty()) {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), "The dashboard url is malformed and cannot be exported.");
			return;
		}

		ReportingService.Connect.getInstance().getAvailableFolders(uuid, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, "Unable to get available folders from the dashboard.");
			}

			@Override
			public void onSuccess(List<String> result) {
				if (result == null || result.isEmpty()) {
					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), "The dashboard has no folder to export.");
					return;
				}

				FdExportDialog dial = new FdExportDialog(FdVanillaViewer.this, uuid, dashboardUrl, result, historize);
				dial.center();
			}
		});
	}
	
	@Override
	public void onExportClick(ClickEvent event) {
		exportDashboard(false);
	}

	@Override
	public void onOpenDashInNewTabClick(ClickEvent event) {
		if (dashboardUrl != null) {
			openNewTab(dashboardUrl);
		}
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, itemInfo.getItem().getId(), TypeCollaboration.ITEM_NOTE, itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}

	@Override
	public void onGroupChange(ClickEvent event) {
		GroupPopup displayPopup = new GroupPopup(this, itemInfo.getAvailableGroups());
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}

	@Override
	public void onShareClick(ClickEvent event) {
		boolean isConnectedToAklabox = infoUser != null ? infoUser.isConnected(IRepositoryApi.AKLABOX) : false;

		SharePopup sharePopup = null;
		if (isConnectedToAklabox) {
			sharePopup = new SharePopup(this, TypeShare.EMAIL, TypeShare.EXPORT, TypeShare.CMIS, TypeShare.AKLABOX);
		}
		else {
			sharePopup = new SharePopup(this, TypeShare.EMAIL, TypeShare.EXPORT, TypeShare.CMIS);
		}
		sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
		sharePopup.show();
	}

	public static native void openNewTab(String url)/*-{
		$wnd.open(url);
	}-*/;

	public void exportDashboard(String dashboardKey, String selectedFormat) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + dashboardKey + "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
		ToolsGWT.doRedirect(fullUrl);
	}

	private HashMap<String, List<String>> getQueryParams(String url) {
		HashMap<String, List<String>> params = new HashMap<String, List<String>>();
		String[] urlParts = url.split("\\?");
		if (urlParts.length > 1) {
			String query = urlParts[1];
			for (String param : query.split("&")) {
				String[] pair = param.split("=");
				String key = pair[0];
				String value = "";
				if (pair.length > 1) {
					value = pair[1];
				}

				List<String> values = params.get(key);
				if (values == null) {
					values = new ArrayList<String>();
					params.put(key, values);
				}
				values.add(value);
			}
		}

		return params;
	}

	@Override
	public void openShare(final TypeShare typeShare) {
		if (dashboardUrl != null) {
			HashMap<String, List<String>> maps = getQueryParams(dashboardUrl);
			final String uuid = maps.get(DASHBOARD_UUID).get(0);
			if (uuid == null || uuid.isEmpty()) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), "The dashboard url is malformed and cannot be exported.");
				return;
			}

			ReportingService.Connect.getInstance().getAvailableFolders(uuid, new AsyncCallback<List<String>>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, "Unable to get available folders from the dashboard.");
				}

				@Override
				public void onSuccess(List<String> result) {
					if (result == null || result.isEmpty()) {
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), "The dashboard has no folder to share.");
						return;
					}

					switch (typeShare) {
					case AKLABOX:
						AklaboxShareDialog dial = new AklaboxShareDialog(itemInfo.getItem(), uuid, dashboardUrl, itemInfo.getOutputs(), result);
						dial.center();
						break;
					case EMAIL:
						MailShareDialog mailDialog = new MailShareDialog(FdVanillaViewer.this, itemInfo, uuid, dashboardUrl, result, infoUser.getAvailableGroups());
						mailDialog.center();
						break;
					case CMIS:
						CmisShareWizard cmisDial = new CmisShareWizard(FdVanillaViewer.this, itemInfo, uuid, dashboardUrl, result);
						cmisDial.center();
						break;

					default:
						break;
					}
				}
			});
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedRun());
		}
	}

	@Override
	public void expendParam() { }

	@Override
	public void expendComment() {
		commentAnimation.run(1000);
	};

	private class PanelAnimation extends Animation {

		private ICanExpand widgetExpand;

		public PanelAnimation(ICanExpand widgetExpand) {
			this.widgetExpand = widgetExpand;
		}

		@Override
		protected void onUpdate(double progress) {
			int width = REPORT_PANEL_SIZE;
			if (!widgetExpand.isExpend()) {
				int progressValue = (int) (-width * (1 - progress));
				int progressReport = (int) (-width * progress) - 16;
				DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", progressValue + "px");
				DOM.setStyleAttribute(reportPanel.getElement(), "right", -progressReport + "px");
				widgetExpand.setImgExpendLeft(-progressValue);
			}
			else {
				int progressValue = (int) (-width * progress);
				int progressReport = (int) (-width * (1 - progress)) - 16;
				DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", progressValue + "px");
				DOM.setStyleAttribute(reportPanel.getElement(), "right", -progressReport + "px");
				widgetExpand.setImgExpendLeft(-progressValue);
			}
		}

		@Override
		protected void onComplete() {
			widgetExpand.setExpend(!widgetExpand.isExpend());
			if (widgetExpand.isExpend()) {
				int leftSize = REPORT_PANEL_SIZE + 15;
				DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", "0px");
				DOM.setStyleAttribute(reportPanel.getElement(), "right", leftSize + "px");
				widgetExpand.setImgExpendLeft(0);
			}
			else {
				int leftSize = -REPORT_PANEL_SIZE;
				DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", leftSize + "px");
				DOM.setStyleAttribute(reportPanel.getElement(), "right", 16 + "px");
				widgetExpand.setImgExpendLeft(REPORT_PANEL_SIZE);
			}
		};
	}

	public void historizeDashboard(String result, String selectedFormat) {
		itemInfo.getOutputs().clear();
		itemInfo.getOutputs().add(selectedFormat);
		itemInfo.setReportKey(result);
		ReportingService.Connect.getInstance().getAvailableDocuments(itemInfo.getItem().getId(), new AsyncCallback<List<DocumentDefinitionDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetHisto());

				HistorizeOptionDialog dial = new HistorizeOptionDialog(FdVanillaViewer.this, itemInfo.getItem(), itemInfo.getReportKey(), itemInfo.getOutputs(), infoUser.getAvailableGroups(), null);
				dial.center();
			}

			@Override
			public void onSuccess(List<DocumentDefinitionDTO> histoDocs) {
				showWaitPart(false);

				HistorizeOptionDialog dial = new HistorizeOptionDialog(FdVanillaViewer.this, itemInfo.getItem(), itemInfo.getReportKey(), itemInfo.getOutputs(), infoUser.getAvailableGroups(), histoDocs);
				dial.center();
			}
		});
		
	}
}
