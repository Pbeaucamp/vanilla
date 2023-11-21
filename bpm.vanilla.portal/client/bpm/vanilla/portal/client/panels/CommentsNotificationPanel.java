package bpm.vanilla.portal.client.panels;

import java.util.List;

import bpm.gwt.commons.client.dialog.Notification;
import bpm.gwt.commons.client.dialog.NotificationPanel;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class CommentsNotificationPanel extends NotificationPanel {

	private static CommentsNotificationPanelUiBinder uiBinder = GWT.create(CommentsNotificationPanelUiBinder.class);

	interface CommentsNotificationPanelUiBinder extends UiBinder<Widget, CommentsNotificationPanel> {
	}
	
	@UiField
	HTMLPanel panelItems;

	private Notification parent;
	
	public CommentsNotificationPanel(ContentDisplayPanel mainPanel, List<PortailRepositoryItem> items) {
		initWidget(uiBinder.createAndBindUi(this));

		for (PortailRepositoryItem item : items) {
			ItemNotificationPanel notificationPanel = new ItemNotificationPanel(this, mainPanel, item);
			panelItems.add(notificationPanel);
		}
	}

	@Override
	public void setParent(Notification parent) {
		this.parent = parent;
	}

	public void hide() {
		parent.hide();
	}
}
