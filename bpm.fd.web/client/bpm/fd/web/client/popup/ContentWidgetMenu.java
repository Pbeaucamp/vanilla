package bpm.fd.web.client.popup;

import bpm.fd.web.client.widgets.ContainerWidget;
import bpm.fd.web.client.widgets.ContentButton;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentWidgetMenu extends PopupPanel {

	private enum TypeAction {
		DELETE;
	}

	private static ContentWidgetMenuUiBinder uiBinder = GWT.create(ContentWidgetMenuUiBinder.class);

	interface ContentWidgetMenuUiBinder extends UiBinder<Widget, ContentWidgetMenu> {
	}

	@UiField
	MenuItem btnDelete;

	@UiField
	HTMLPanel panelMenu;

	ContainerWidget containerWidget;

	public ContentWidgetMenu(ContainerWidget containerWidget, ContentButton button) {
		setWidget(uiBinder.createAndBindUi(this));
		this.containerWidget = containerWidget;

		btnDelete.setScheduledCommand(new CommandRun(button, TypeAction.DELETE));
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
	}

	private class CommandRun implements Command {

		private ContentButton button;
		private TypeAction action;

		public CommandRun(ContentButton button, TypeAction action) {
			this.button = button;
			this.action = action;
		}

		@Override
		public void execute() {
			hide();

			switch (action) {
			case DELETE:
				containerWidget.removeWidget(button);
				break;
			default:
				break;
			}
		}
	};
}
