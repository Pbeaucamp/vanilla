package bpm.gwt.commons.client.viewer;

import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.repository.ReportBackground;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;

public class ReportBackgroundItemViewer extends Viewer {

	private ReportBackground item;

	public ReportBackgroundItemViewer(VanillaViewer vanillaViewer, ReportBackground item) {
		super(vanillaViewer);
		this.item = item;
		
		showReport(item);
		
		defineToolbar(null);
	}

	@Override
	public void defineToolbar(LaunchReportInformations itemInfo) {
		btnExport.setVisible(true);
		btnPrint.setVisible(true);
	}
	
	public void showReport(ReportBackground report) {
		reportFrame.setVisible(true);
		lblReloadReport.setVisible(false);

		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_DOCUMENT_SERVLET + "?" + CommonConstants.DOCUMENT_ID + "=" + report.getId() 
				+ "&" + CommonConstants.DOCUMENT_TYPE + "=" + CommonConstants.DOCUMENT_TYPE_BACKGROUND
				+ "&" + CommonConstants.REPORT_OUTPUT + "=" + report.getOutputFormat();
		reportFrame.setUrl(fullUrl);
	}

	@Override
	public void onExportClick(ClickEvent event) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_DOCUMENT_SERVLET + "?" + CommonConstants.DOCUMENT_ID + "=" + item.getId() 
				+ "&" + CommonConstants.DOCUMENT_TYPE + "=" + CommonConstants.DOCUMENT_TYPE_BACKGROUND
				+ "&" + CommonConstants.REPORT_OUTPUT + "=" + item.getOutputFormat();
		ToolsGWT.doRedirect(fullUrl);
	}

	@Override
	public void onPrintClick(ClickEvent event) {
		String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_DOCUMENT_SERVLET + "?" + CommonConstants.DOCUMENT_ID + "=" + item.getId() 
				+ "&" + CommonConstants.DOCUMENT_TYPE + "=" + CommonConstants.DOCUMENT_TYPE_BACKGROUND
				+ "&" + CommonConstants.REPORT_OUTPUT + "=" + item.getOutputFormat();
		printWindow(fullUrl);
	}

	public static native void openWindow(String url) /*-{
		$wnd.open(url);
	}-*/;

	public static native void printWindow(String url) /*-{
		myWindowToPrint = $wnd.open(url);
		myWindowToPrint.print();
	}-*/;

	@Override
	public void runItem(LaunchReportInformations itemInformations) { }
}
