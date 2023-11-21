package bpm.gwt.commons.client.viewer;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.UserRunConfiguration;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GedEntryViewer extends Viewer {

	private LaunchReportInformations itemInfo;
	private boolean hasBeenRun = false;
	
	private String reportKey;
	private String selectedFormat;
	
	public GedEntryViewer(VanillaViewer vanillaViewer, PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);
		
		launchItem(item, selectedGroup, availableGroups);
	}

	private void launchItem(PortailRepositoryItem item, Group selectedGroup, List<Group> availableGroups) {
		LaunchReportInformations itemInfo = new LaunchReportInformations(item, selectedGroup, new ArrayList<VanillaGroupParameter>(), new ArrayList<UserRunConfiguration>(), 
				null, null, new ArrayList<String>(), null, availableGroups, null);
		runItem(itemInfo);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if(itemInfo.getItem().getItem().isCommentable()) {
			btnComment.setVisible(true);
		}
		btnExport.setVisible(true);
		btnPrint.setVisible(true);
	}

	@Override
	public void setItemInfo(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
	}

	@Override
	public void runItem(LaunchReportInformations itemInfo) {
		this.itemInfo = itemInfo;
		
		defineToolbar(itemInfo);
		
		ReportingService.Connect.getInstance().openGedDocument(itemInfo.getItem(), new AsyncCallback<DisplayItem>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			public void onSuccess(DisplayItem item) {
				hasBeenRun = true;
				showReport(item.getKey(), item.getOutputFormat());
			}
		});
	}
	
	public void showReport(String reportName, String selectedFormat) {
		this.reportKey = reportName;
		this.selectedFormat = selectedFormat;
		
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET 
						+ "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + reportName 
						+ "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
		reportFrame.setUrl(fullUrl);
	}
	
	public static native void openWindow(String url) /*-{
		$wnd.open(url);
	}-*/;
	
	public static native void printWindow(String url) /*-{
		myWindowToPrint = $wnd.open(url);
		myWindowToPrint.print();
	}-*/;

	@Override
	public void onExportClick(ClickEvent event) {
		if(hasBeenRun){
			String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET 
					+ "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + reportKey 
					+ "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
			ToolsGWT.doRedirect(fullUrl);
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.DocumentNotRun());
		}
	}

	@Override
	public void onPrintClick(ClickEvent event) {
		if(hasBeenRun){
			String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET 
					+ "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + reportKey 
					+ "&" + CommonConstants.REPORT_OUTPUT + "=" + selectedFormat;
	
			printWindow(fullUrl);
		}
		else {
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.DocumentNotRun());
		}
	}

	@Override
	public void onCommentClick(ClickEvent event) {
		CommentPanel commentsPanel = new CommentPanel(this, itemInfo.getItem().getId(), TypeCollaboration.ITEM_NOTE, itemInfo.getSelectedGroup(), itemInfo.getAvailableGroups());
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}
}

