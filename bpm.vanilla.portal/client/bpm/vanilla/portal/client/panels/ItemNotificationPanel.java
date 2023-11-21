package bpm.vanilla.portal.client.panels;

import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ItemNotificationPanel extends Composite {

	private static CommentsNotificationPanelUiBinder uiBinder = GWT.create(CommentsNotificationPanelUiBinder.class);

	interface CommentsNotificationPanelUiBinder extends UiBinder<Widget, ItemNotificationPanel> {
	}
	
	@UiField
	FocusPanel link;
	
	@UiField
	Label lblItem;

	private CommentsNotificationPanel parent;
	private ContentDisplayPanel mainPanel;
	
	private PortailRepositoryItem item;
	
	public ItemNotificationPanel(CommentsNotificationPanel parent, ContentDisplayPanel mainPanel, PortailRepositoryItem item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.mainPanel = mainPanel;
		this.item = item;
		
		lblItem.setText(item.getName());
	}

	@UiHandler("link")
	public void onLinkClick(ClickEvent event) {
		if (ToolsGWT.isRunnable(item) && item.getItem().canRun() && item.getItem().isOn()) {
			mainPanel.openViewer(item);
		}
		parent.hide();
	}
}
