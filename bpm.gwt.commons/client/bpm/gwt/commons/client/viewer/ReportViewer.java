package bpm.gwt.commons.client.viewer;

import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.popup.FormatPopup;
import bpm.gwt.commons.client.popup.GroupPopup;
import bpm.gwt.commons.client.popup.IChangeFormat;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.viewer.aklabox.AklaboxShareDialog;
import bpm.gwt.commons.client.viewer.comments.CommentDisplayPanel;
import bpm.gwt.commons.client.viewer.dialog.BirtFormatDialogBox;
import bpm.gwt.commons.client.viewer.dialog.BurstSelectGroupsWindow;
import bpm.gwt.commons.client.viewer.dialog.CmisShareWizard;
import bpm.gwt.commons.client.viewer.dialog.HistorizeOptionDialog;
import bpm.gwt.commons.client.viewer.dialog.ItemMetadataLinkDialog;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.client.viewer.param.ParamDisplayPanel;
import bpm.gwt.commons.client.viewer.widget.ICanExpand;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations.TypeRun;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

public class ReportViewer extends Viewer implements IChangeFormat, IReportViewer {
	private static final int REPORT_PANEL_SIZE = 215;

	private VanillaViewer vanillaViewer;
	private LaunchReportInformations itemInfo;
	private InfoUser infoUser;
	private boolean isDisco;
	private boolean forceOpen;

	private ParamDisplayPanel paramDisplayPanel;
	private CommentDisplayPanel commentDisplayPanel;

	private PanelAnimation paramAnimation, commentAnimation;

	public ReportViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, InfoUser infoUser, boolean isDisco, boolean forceOpen) {
		super(vanillaViewer);
		this.vanillaViewer = vanillaViewer;
		this.infoUser = infoUser;
		this.isDisco = isDisco;
		this.forceOpen = forceOpen;

		reportPanel.getElement().getStyle().setLeft(230, Unit.PX);
		reportPanel.getElement().getStyle().setRight(230, Unit.PX);

		paramDisplayPanel = new ParamDisplayPanel(this);
		this.paramPanel.setWidget(paramDisplayPanel);
		paramAnimation = new PanelAnimation(paramDisplayPanel, true);

		commentDisplayPanel = new CommentDisplayPanel(this, infoUser.getUser());
		this.commentPanel.setWidget(commentDisplayPanel);
		commentAnimation = new PanelAnimation(commentDisplayPanel, false);

		vanillaViewer.launchReport(this, item, selectedGroup, isDisco, forceOpen);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if (itemInfo.getTypeRun() == TypeRun.DISCO) {
			btnRun.setVisible(true);
			btnExport.setVisible(true);
			btnPrint.setVisible(true);
			btnFormats.setVisible(true);
			btnFormats.setText(itemInfo.getOutputs().get(0));
		}
		else {
			if (itemInfo.getItem().getItem().isCommentable()) {
				btnComment.setVisible(true);
			}
			btnRun.setVisible(true);
			btnExport.setVisible(true);
			btnPrint.setVisible(true);
			btnHistorize.setVisible(true);
			btnBurst.setVisible(true);
			btnShare.setVisible(true);
			btnReport.setVisible(itemInfo.getMetadataLinks() != null && !itemInfo.getMetadataLinks().isEmpty());
			btnReport.setTitle(LabelsConstants.lblCnst.ValuesExplorer());
			btnGroups.setVisible(true);
			btnGroups.setText(itemInfo.getSelectedGroup().getName());
			btnFormats.setVisible(true);
			btnFormats.setText(itemInfo.getOutputs().get(0));
			btnComment.setVisible(true);
		}
	}

	@Override
	public void runItem(final LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
		showWaitPart(true);

		defineToolbar(itemInfo);

		ReportingService.Connect.getInstance().runReport(itemInfo, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.ErrorRunningBirt());
			}

			@Override
			public void onSuccess(String result) {
				showWaitPart(false);

				itemInfo.setReportKey(result);

				switch (itemInfo.getTypeRun()) {
				case BACKGROUND:
					getViewer().postProcess();
					getViewer().close();
					break;
				case EXPORT:
					exportReport(result, itemInfo.getOutputs().get(0));
					break;
				case PRINT:
					printReport(result, itemInfo.getOutputs().get(0));
					break;
				case RUN:
				case DISCO:
					itemInfo.setHasBeenRun(true);

					refreshParamPart();
					refreshCommentPart();

					showReport(result, itemInfo.getOutputs().get(0));
					break;

				default:
					break;
				}
			}
		});
	}

	public void showReport(String reportName, String selectedFormat) {
		reportFrame.setVisible(true);
		lblReloadReport.setVisible(false);

		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + reportName + "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
		reportFrame.setUrl(fullUrl);
	}

	public void printReport(String reportName, String selectedFormat) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + reportName + "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;

		printWindow(fullUrl);
	}

	public void exportReport(String reportName, String selectedFormat) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + reportName + "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
		ToolsGWT.doRedirect(fullUrl);
	}

	@Override
	public void changeFormat(String selectedFormat) {
		btnFormats.setText(selectedFormat);

		showReport(itemInfo.getReportKey(), selectedFormat);
	}

	@Override
	public void changeGroup(Group selectedGroup) {
		itemInfo.setSelectedGroup(selectedGroup);

		btnGroups.setText(selectedGroup.getName());

		if (itemInfo.getGroupParameters() != null && !itemInfo.getGroupParameters().isEmpty()) {
			updateParameters();
		}
		else {
			runItem(itemInfo);
		}
	}

	public static native void openWindow(String url) /*-{
		$wnd.open(url);
	}-*/;

	public static native void printWindow(String url) /*-{
		var myWindowToPrint = $wnd.open(url);
		myWindowToPrint.print();
	}-*/;

	public void refreshParamPart() {
		paramDisplayPanel.buildContent(itemInfo);
	}
	
	public void refreshCommentPart() {
		commentDisplayPanel.buildContent(itemInfo);
	}

	private void updateParameters() {
		showWaitPart(true);

		reportFrame.setVisible(false);
		lblReloadReport.setVisible(true);

		ReportingService.Connect.getInstance().getParameters(itemInfo, new AsyncCallback<List<VanillaGroupParameter>>() {
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(List<VanillaGroupParameter> groupParams) {
				showWaitPart(false);

				itemInfo.setGroupParameters(groupParams);

				refreshParamPart();
			}
		});
	}

	public LaunchReportInformations getItemInfo() {
		return itemInfo;
	}

	@Override
	public void onRunClick(ClickEvent event) {
		vanillaViewer.launchReport(this, itemInfo.getItem(), itemInfo.getSelectedGroup(), itemInfo.getTypeRun() == TypeRun.DISCO, forceOpen);
	}

	@Override
	public void onExportClick(ClickEvent event) {
//		itemInfo.setTypeRun(TypeRun.EXPORT);

		BirtFormatDialogBox dial = new BirtFormatDialogBox(this, LabelsConstants.lblCnst.ExportReport(), itemInfo, TypeRun.EXPORT);
		dial.center();
	}

	@Override
	public void onPrintClick(ClickEvent event) {
//		itemInfo.setTypeRun(TypeRun.PRINT);

		BirtFormatDialogBox dial = new BirtFormatDialogBox(this, LabelsConstants.lblCnst.PrintReport(), itemInfo, TypeRun.PRINT);
		dial.center();
	}
	
	@Override
	public void onReportClick(ClickEvent event) {
		ItemMetadataLinkDialog dial = new ItemMetadataLinkDialog(this, LabelsConstants.lblCnst.MetadataLinks(), itemInfo);
		dial.center();
	}

	@Override
	public void onHistorizeClick(ClickEvent event) {
		showWaitPart(true);

		ReportingService.Connect.getInstance().getAvailableGroupsForHisto(itemInfo.getItem().getId(), new AsyncCallback<List<Group>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetGroupHisto());
			}

			@Override
			public void onSuccess(final List<Group> groups) {
				if (groups == null || groups.isEmpty()) {
					showWaitPart(false);

					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NoGroupGranted());
					return;
				}

				ReportingService.Connect.getInstance().getAvailableDocuments(itemInfo.getItem().getId(), new AsyncCallback<List<DocumentDefinitionDTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetHisto());

						HistorizeOptionDialog dial = new HistorizeOptionDialog(ReportViewer.this, itemInfo.getItem(), itemInfo.getReportKey(), itemInfo.getOutputs(), groups, null);
						dial.center();
					}

					@Override
					public void onSuccess(List<DocumentDefinitionDTO> histoDocs) {
						showWaitPart(false);

						HistorizeOptionDialog dial = new HistorizeOptionDialog(ReportViewer.this, itemInfo.getItem(), itemInfo.getReportKey(), itemInfo.getOutputs(), groups, histoDocs);
						dial.center();
					}
				});
			}
		});
	}

	@Override
	public void onGroupChange(ClickEvent event) {
		GroupPopup displayPopup = new GroupPopup(this, itemInfo.getAvailableGroups());
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}

	@Override
	public void onFormatChange(ClickEvent event) {
		FormatPopup displayPopup = new FormatPopup(this, itemInfo.getOutputs());
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}

	@Override
	public void onBurstClick(ClickEvent event) {
		if (itemInfo.hasBeenRun()) {
			showWaitPart(true);

			ReportingService.Connect.getInstance().getAvailableGroupsForRun(itemInfo.getItem().getId(), false, new AsyncCallback<List<Group>>() {

				@Override
				public void onSuccess(final List<Group> groupsRun) {
					if (groupsRun != null && !groupsRun.isEmpty()) {
						ReportingService.Connect.getInstance().getAvailableGroupsForHisto(itemInfo.getItem().getId(), new AsyncCallback<List<Group>>() {

							@Override
							public void onFailure(Throwable caught) {
								showWaitPart(false);

								caught.printStackTrace();

								ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.NoGroupAvailable());
							}

							@Override
							public void onSuccess(List<Group> result) {
								showWaitPart(false);

								BurstSelectGroupsWindow burstDialog = new BurstSelectGroupsWindow(ReportViewer.this, itemInfo, groupsRun, result);
								burstDialog.center();
							}
						});
					}
					else {
						showWaitPart(false);

						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.NoGroupAvailable());
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetGroup());
				}
			});
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedRun());
		}
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

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, itemInfo.getItem().getId(), TypeCollaboration.ITEM_NOTE, itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 350, event.getClientY() + 20);
	}

	@Override
	public void expendParam() {
		paramAnimation.run(1000);
	}

	@Override
	public void expendComment() {
		commentAnimation.run(1000);
	}

	@Override
	public void openShare(TypeShare typeShare) {
		if (itemInfo.hasBeenRun()) {
			switch (typeShare) {
			case AKLABOX:
				AklaboxShareDialog dial = new AklaboxShareDialog(itemInfo.getItem(), itemInfo.getReportKey(), itemInfo.getOutputs());
				dial.center();
				break;
			case EMAIL:
				MailShareDialog mailDialog = new MailShareDialog(ReportViewer.this, itemInfo.getReportKey(), itemInfo, infoUser.getAvailableGroups());
				mailDialog.center();
				break;
			case CMIS:
				CmisShareWizard cmisDial = new CmisShareWizard(ReportViewer.this, itemInfo.getReportKey(), itemInfo);
				cmisDial.center();
				break;

			default:
				break;
			}
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.NeedRun());
		}
	}

	@Override
	public void setItemInfo(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
	}

	private class PanelAnimation extends Animation {

		private ICanExpand widgetExpand;
		private boolean isLeft;

		public PanelAnimation(ICanExpand widgetExpand, boolean isLeft) {
			this.widgetExpand = widgetExpand;
			this.isLeft = isLeft;
		}

		@Override
		protected void onUpdate(double progress) {
			int width = REPORT_PANEL_SIZE;
			if (!widgetExpand.isExpend()) {
				int progressValue = (int) (-width * (1 - progress));
				int progressReport = (int) (-width * progress) - 16;
				if (isLeft) {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginLeft", progressValue + "px");
					DOM.setStyleAttribute(reportPanel.getElement(), "left", -progressReport + "px");
				}
				else {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", progressValue + "px");
					DOM.setStyleAttribute(reportPanel.getElement(), "right", -progressReport + "px");
				}
				widgetExpand.setImgExpendLeft(-progressValue);
			}
			else {
				int progressValue = (int) (-width * progress);
				int progressReport = (int) (-width * (1 - progress)) - 16;
				if (isLeft) {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginLeft", progressValue + "px");
					DOM.setStyleAttribute(reportPanel.getElement(), "left", -progressReport + "px");
				}
				else {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", progressValue + "px");
					DOM.setStyleAttribute(reportPanel.getElement(), "right", -progressReport + "px");
				}
				widgetExpand.setImgExpendLeft(-progressValue);
			}
		}

		@Override
		protected void onComplete() {
			widgetExpand.setExpend(!widgetExpand.isExpend());
			if (widgetExpand.isExpend()) {
				int leftSize = REPORT_PANEL_SIZE + 15;
				if (isLeft) {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginLeft", "0px");
					DOM.setStyleAttribute(reportPanel.getElement(), "left", leftSize + "px");
				}
				else {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", "0px");
					DOM.setStyleAttribute(reportPanel.getElement(), "right", leftSize + "px");
				}
				widgetExpand.setImgExpendLeft(0);
			}
			else {
				int leftSize = -REPORT_PANEL_SIZE;
				if (isLeft) {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginLeft", leftSize + "px");
					DOM.setStyleAttribute(reportPanel.getElement(), "left", 16 + "px");
				}
				else {
					DOM.setStyleAttribute(widgetExpand.getElement(), "marginRight", leftSize + "px");
					DOM.setStyleAttribute(reportPanel.getElement(), "right", 16 + "px");
				}
				widgetExpand.setImgExpendLeft(REPORT_PANEL_SIZE);
			}
		};
	};
}
