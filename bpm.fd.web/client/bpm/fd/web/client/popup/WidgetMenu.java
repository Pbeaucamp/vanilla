package bpm.fd.web.client.popup;

import bpm.fd.web.client.panels.IDropPanel;
import bpm.fd.web.client.widgets.WidgetComposite;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetMenu extends PopupPanel {

	private enum TypeAction {
		DELETE;
	}

	private static WorkflowMenuUiBinder uiBinder = GWT.create(WorkflowMenuUiBinder.class);

	interface WorkflowMenuUiBinder extends UiBinder<Widget, WidgetMenu> {
	}

	@UiField
	MenuItem btnDelete;

	@UiField
	HTMLPanel panelMenu;

	private IDropPanel dropPanel;

	public WidgetMenu(IDropPanel dropPanel, WidgetComposite widget) {
		setWidget(uiBinder.createAndBindUi(this));
		this.dropPanel = dropPanel;

		btnDelete.setScheduledCommand(new CommandRun(widget, TypeAction.DELETE));
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
	}

	private class CommandRun implements Command {

		private WidgetComposite widget;
		private TypeAction action;

		public CommandRun(WidgetComposite widget, TypeAction action) {
			this.widget = widget;
			this.action = action;
		}

		@Override
		public void execute() {
			hide();

			switch (action) {
			case DELETE:
				//REMOVED for now, use back to cancel the supression
//				final InformationsDialog dial = new InformationsDialog(Labels.lblCnst.DeleteConfirmation(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.DeleteConfirmationMessage(), true);
//				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
//					@Override
//					public void onClose(CloseEvent<PopupPanel> event) {
//						if (dial.isConfirm()) {
							for(WidgetComposite comp : widget.getWidgetManager().getSelectedWidgets()) {
								dropPanel.removeWidget(comp, true);
							}
							dropPanel.removeWidget(widget, true);
//						}
//					}
//				});
//				dial.center();
				break;
			default:
				break;
			}
		}
	};
}
