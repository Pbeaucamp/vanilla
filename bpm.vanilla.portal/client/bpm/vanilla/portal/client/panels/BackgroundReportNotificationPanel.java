package bpm.vanilla.portal.client.panels;

import java.util.List;

import bpm.gwt.commons.client.dialog.Notification;
import bpm.gwt.commons.client.dialog.NotificationPanel;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.portal.client.panels.navigation.NavigationPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class BackgroundReportNotificationPanel extends NotificationPanel {

	private static BackgroundReportNotificationPanelUiBinder uiBinder = GWT.create(BackgroundReportNotificationPanelUiBinder.class);

	interface BackgroundReportNotificationPanelUiBinder extends UiBinder<Widget, BackgroundReportNotificationPanel> {
	}
	
	@UiField
	HTML txtReports;

	private Notification parent;
	private NavigationPanel navigationPanel;
	
	public BackgroundReportNotificationPanel(NavigationPanel navigationPanel, List<ReportBackground> reports) {
		initWidget(uiBinder.createAndBindUi(this));
		this.navigationPanel = navigationPanel;
		
		StringBuffer buf = new StringBuffer();
		for (ReportBackground report : reports) {
			buf.append(" - ");
			buf.append(report.getName());
			buf.append("</br>");
		}
		txtReports.setHTML(buf.toString());
	}

	@UiHandler("link")
	public void onLink(ClickEvent event) {
		navigationPanel.switchPanel(TypeViewer.REPORT_BACKGROUND);
		parent.hide();
	}

	@Override
	public void setParent(Notification parent) {
		this.parent = parent;
	}
}
