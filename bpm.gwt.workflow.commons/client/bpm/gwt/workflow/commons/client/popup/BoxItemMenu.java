package bpm.gwt.workflow.commons.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class BoxItemMenu extends PopupPanel {

	private enum TypeAction {
		DELETE;
	}

	private static WorkflowMenuUiBinder uiBinder = GWT.create(WorkflowMenuUiBinder.class);

	interface WorkflowMenuUiBinder extends UiBinder<Widget, BoxItemMenu> {
	}

	@UiField
	MenuItem btnDelete;

	@UiField
	HTMLPanel panelMenu;

	private WorkspacePanel creationPanel;

	public BoxItemMenu(WorkspacePanel creationPanel, BoxItem boxItem) {
		setWidget(uiBinder.createAndBindUi(this));
		this.creationPanel = creationPanel;

		btnDelete.setScheduledCommand(new CommandRun(boxItem, TypeAction.DELETE));
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
	}

	private class CommandRun implements Command {

		private BoxItem item;
		private TypeAction action;

		public CommandRun(BoxItem item, TypeAction action) {
			this.item = item;
			this.action = action;
		}

		@Override
		public void execute() {
			hide();

			switch (action) {
			case DELETE:
				creationPanel.removeBox(item, true);
				break;
			default:
				break;
			}
		}
	};
}
