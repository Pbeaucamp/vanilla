package bpm.gwt.workflow.commons.client.popup;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.workflow.commons.client.IWorkflowManager;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowMenu extends PopupPanel {

	private enum TypeAction {
		RUN, 
		HISTORICS, 
		SCHEDULE;
	}

	private static WorkflowMenuUiBinder uiBinder = GWT.create(WorkflowMenuUiBinder.class);

	interface WorkflowMenuUiBinder extends UiBinder<Widget, WorkflowMenu> {
	}

	@UiField
	MenuItem btnRun, btnHistorics, btnSchedule;

	@UiField
	HTMLPanel panelMenu;

	private IWorkflowManager workflowManager;

	public WorkflowMenu(IWorkflowManager workflowManager, Workflow workflow) {
		setWidget(uiBinder.createAndBindUi(this));
		this.workflowManager = workflowManager;

		btnRun.setScheduledCommand(new CommandRun(workflow, TypeAction.RUN));
		btnHistorics.setScheduledCommand(new CommandRun(workflow, TypeAction.HISTORICS));
		btnSchedule.setScheduledCommand(new CommandRun(workflow, TypeAction.SCHEDULE));
		
		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);
	}

	private class CommandRun implements Command {

		private Workflow item;
		private TypeAction action;

		public CommandRun(Workflow item, TypeAction action) {
			this.item = item;
			this.action = action;
		}

		@Override
		public void execute() {
			hide();

			switch (action) {
			case RUN:
				workflowManager.runWorkflow(item);
				break;
			case HISTORICS:
				workflowManager.showHistoricsWorkflow(item);
				break;
			case SCHEDULE:
				workflowManager.scheduleWorkflow(item);
				break;
			default:
				break;
			}
		}
	};
}
