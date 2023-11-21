package bpm.gwt.commons.client.viewer;

import java.util.List;

import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.Comment;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;

public class OtherItemViewer extends Viewer {

	private DisplayItem item;
	
	private Group selectedGroup;
	private List<Group> availableGroups;
	
	private TypeCollaboration typeCollaboration;

	private boolean isFromVanillaFile = false;

	public OtherItemViewer(VanillaViewer vanillaViewer, DisplayItem item, Group selectedGroup, List<Group> availableGroups) {
		super(vanillaViewer);
		this.item = item;
		this.selectedGroup = selectedGroup;
		this.availableGroups = availableGroups;

		if (item.getKey() != null && item.getKey().contains("vanilla_files")) {
			isFromVanillaFile = true;
		}

		if (item.isCommentable()) {
			if (item.getType() == Comment.ITEM) {
				typeCollaboration = TypeCollaboration.ITEM_NOTE;
			}
			else if (item.getType() == Comment.DOCUMENT_VERSION) {
				typeCollaboration = TypeCollaboration.DOCUMENT_VERSION;
			}
		}
		
		showReport(item.getKey(), item.getOutputFormat());
		
		defineToolbar(null);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		if(item.isCommentable()) {
			btnComment.setVisible(true);
		}
		btnExport.setVisible(true);
		btnPrint.setVisible(true);
	}

	public void showReport(String reportName, String format) {
		if (isFromVanillaFile) {
			reportFrame.setUrl(reportName);
		}
		else {
			String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" 
					+ CommonConstants.REPORT_HASHMAP_NAME + "=" + reportName + "&" + CommonConstants.REPORT_OUTPUT + "=" + format;
			reportFrame.setUrl(fullUrl);
		}
	}

	@Override
	public void onExportClick(ClickEvent event) {
		if (isFromVanillaFile) {
			reportFrame.setUrl(item.getKey());
		}
		else {
			String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" 
					+ CommonConstants.REPORT_HASHMAP_NAME + "=" + item.getKey() + "&" + CommonConstants.REPORT_OUTPUT + "=" + item.getOutputFormat();
			ToolsGWT.doRedirect(fullUrl);
		}
	}

	@Override
	public void onPrintClick(ClickEvent event) {
		if (isFromVanillaFile) {
			reportFrame.setUrl(item.getKey());
		}
		else {
			String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET + "?" 
					+ CommonConstants.REPORT_HASHMAP_NAME + "=" + item.getKey() + "&" + CommonConstants.REPORT_OUTPUT + "=" + item.getOutputFormat();
			printWindow(fullUrl);
		}
	}

	public static native void openWindow(String url) /*-{
		$wnd.open(url);
	}-*/;

	public static native void printWindow(String url) /*-{
		myWindowToPrint = $wnd.open(url);
		myWindowToPrint.print();
	}-*/;

	@Override
	public void onCommentClick(ClickEvent event) {

		CommentPanel commentsPanel = new CommentPanel(this, item.getItemId(), typeCollaboration, selectedGroup, availableGroups);
		commentsPanel.show();
		commentsPanel.setPopupPosition(event.getClientX() - 332, event.getClientY() + 20);
	}

	@Override
	public void runItem(LaunchReportInformations itemInformations) { }
}
