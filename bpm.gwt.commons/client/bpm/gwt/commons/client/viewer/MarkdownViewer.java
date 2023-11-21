package bpm.gwt.commons.client.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.popup.FormatPopup;
import bpm.gwt.commons.client.popup.GroupPopup;
import bpm.gwt.commons.client.popup.IChangeFormat;
import bpm.gwt.commons.client.popup.SharePopup;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.viewer.dialog.HistorizeOptionDialog;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.client.viewer.param.ParamDisplayPanel;
import bpm.gwt.commons.client.viewer.widget.ICanExpand;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MarkdownViewer extends Viewer implements IChangeFormat, IReportViewer {
	private static final int REPORT_PANEL_SIZE = 215;
	private static final String DASHBOARD_UUID = "uuid";

	private PortailRepositoryItem item;
	private Group selectedGroup;
	private List<Group> availableGroups;
	private LaunchReportInformations itemInfo;
	private VanillaViewer vanillaViewer;

	private RScriptModel scriptModel;
	private RScript script;

	private List<String> formats = Arrays.asList(CommonConstants.FORMAT_AUTO, CommonConstants.FORMAT_HTML, CommonConstants.FORMAT_PDF, CommonConstants.FORMAT_DOCX);
	private String selectedFormat = CommonConstants.FORMAT_AUTO;

	private ParamDisplayPanel paramDisplayPanel;
	private PanelAnimation paramAnimation;

	public MarkdownViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);
		this.item = item;
		this.selectedGroup = selectedGroup;
		this.availableGroups = availableGroups;
		this.vanillaViewer = vanillaViewer;
		LaunchReportInformations itemInfo = new LaunchReportInformations();
		itemInfo.setItem(item);

		reportPanel.getElement().getStyle().setLeft(215, Unit.PX);

		paramDisplayPanel = new ParamDisplayPanel(this);
		paramDisplayPanel.hideReportsElements();
		this.paramPanel.setWidget(paramDisplayPanel);
		paramAnimation = new PanelAnimation(paramDisplayPanel, true);

		vanillaViewer.launchReport(this, item, selectedGroup, false, false);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if (itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}

		btnExport.setVisible(true);
		btnPrint.setVisible(true);
		btnShare.setVisible(true);
		btnFormats.setVisible(true);
		btnFormats.setText(formats.get(0));
		btnHistorize.setVisible(true);

	}

	@Override
	public void runItem(LaunchReportInformations itemInfo) {
		itemInfo.getItem().getItem().setType(IRepositoryApi.R_MARKDOWN_TYPE);
		this.itemInfo = itemInfo;
		defineToolbar(itemInfo);
		showWaitPart(true);
		ReportingService.Connect.getInstance().executeMarkdown(itemInfo, selectedFormat, new AsyncCallback<RScriptModel>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			public void onSuccess(RScriptModel scriptModel) { // TODO recuperer le tableau des paramssi liste de valeurs pour inserer dans listbox
				MarkdownViewer.this.scriptModel = scriptModel;
				CommonService.Connect.getInstance().getRScriptbyId(scriptModel.getIdScript(), new AsyncCallback<RScript>() {

					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						showWaitPart(false);
						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
					}

					public void onSuccess(RScript script) {
						MarkdownViewer.this.script = script;
						showWaitPart(false);

						refreshParamPart();
						int i = 0;
						for (String output : MarkdownViewer.this.scriptModel.getOutputVarstoString()) {
							if(selectedFormat.equals(CommonConstants.FORMAT_HTML) || selectedFormat.equals(CommonConstants.FORMAT_AUTO)){
								btnFormats.setText(selectedFormat);
								showHTML(script.getName() + i);
							}
							else if (selectedFormat.equals(CommonConstants.FORMAT_PDF)) {
								btnFormats.setText(selectedFormat);
								showPDF(script.getName() + i);
							}
							else if (selectedFormat.equals(CommonConstants.FORMAT_DOCX)) {
								showWORD(script.getName() + i);
							}
						}

					}
				});
			}
		});
	}

	private void showHTML(String name) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + name + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_HTML;
		reportFrame.setUrl(fullUrl);
	}

	private void showPDF(String name) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + name + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_PDF;
		reportFrame.setUrl(fullUrl);
	}

	private void showWORD(String name) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + name + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_DOCX;
		reportFrame.setUrl(fullUrl);
	}

	@Override
	public void onExportClick(ClickEvent event) {
		if (reportFrame.getUrl() != null) {
			ToolsGWT.doRedirect(reportFrame.getUrl());
		}

	}

	@Override
	public void onOpenDashInNewTabClick(ClickEvent event) {
		if (reportFrame.getUrl() != null) {
			openNewTab(reportFrame.getUrl());
		}
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, item.getId(), TypeCollaboration.ITEM_NOTE, selectedGroup, availableGroups);
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}

	@Override
	public void onGroupChange(ClickEvent event) {
		GroupPopup displayPopup = new GroupPopup(this, availableGroups);
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}

	public static native void openNewTab(String url)/*-{
		$wnd.open(url);
	}-*/;

	@Override
	public void changeFormat(String selectedFormat) {
		this.selectedFormat = selectedFormat;
		btnFormats.setText(selectedFormat);
		runItem(itemInfo);
	}

	@Override
	public void onFormatChange(ClickEvent event) {
		FormatPopup displayPopup = new FormatPopup(this, formats);
		displayPopup.setPopupPosition(event.getClientX(), event.getClientY());
		displayPopup.show();
	}

	@Override
	public void onPrintClick(ClickEvent event) {
		printWindow(reportFrame.getUrl());
	}

	public static native void printWindow(String url) /*-{
		var myWindowToPrint = $wnd.open(url);
		myWindowToPrint.print();
	}-*/;

	@Override
	public void onShareClick(ClickEvent event) {
		SharePopup sharePopup = new SharePopup(this, TypeShare.EMAIL);
		sharePopup.setPopupPosition(event.getClientX(), event.getClientY());
		sharePopup.show();
	}

	@Override
	public void openShare(TypeShare typeShare) {
		if (reportFrame.getUrl() != null) {
			switch (typeShare) {
			case EMAIL:
				MailShareDialog mailDialog = new MailShareDialog(MarkdownViewer.this, script.getName(), availableGroups);
				mailDialog.center();
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
	public void expendParam() {
		paramAnimation.run(1000);
	}

	@Override
	public void expendComment() {
	}

	public void refreshParamPart() {
		paramDisplayPanel.buildContent(itemInfo);
		paramDisplayPanel.hideReportsElements();
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
				int leftSize = REPORT_PANEL_SIZE;
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
				
				final List<String> availableFormats = new ArrayList<String>();
				availableFormats.add(selectedFormat);

				ReportingService.Connect.getInstance().getAvailableDocuments(itemInfo.getItem().getId(), new AsyncCallback<List<DocumentDefinitionDTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetHisto());

						HistorizeOptionDialog dial = new HistorizeOptionDialog(MarkdownViewer.this, itemInfo.getItem(), script.getName(), availableFormats, groups, null);
						dial.center();
					}

					@Override
					public void onSuccess(List<DocumentDefinitionDTO> histoDocs) {
						showWaitPart(false);

						HistorizeOptionDialog dial = new HistorizeOptionDialog(MarkdownViewer.this, itemInfo.getItem(), script.getName(), availableFormats, groups, histoDocs);
						dial.center();
					}
				});
			}
		});
	}
}
