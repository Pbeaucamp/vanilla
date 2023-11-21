package bpm.gwt.workflow.commons.client.dialog;

import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowDialog extends AbstractDialogBox {

	private static ConnectionAklaboxDialogUiBinder uiBinder = GWT.create(ConnectionAklaboxDialogUiBinder.class);

	interface ConnectionAklaboxDialogUiBinder extends UiBinder<Widget, WorkflowDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	TextHolderBox txtName;

	@UiField
	TextAreaHolderBox txtDescription;

	@UiField
	Label txtErrorName, txtErrorDescription;

	private WorkspacePanel creationPanel;
	private Workflow workflow;
	
	private boolean modify;

	public WorkflowDialog(WorkspacePanel creationPanel, Workflow workflow, boolean modify) {
		super(LabelsCommon.lblCnst.Save(), false, true);
		this.creationPanel = creationPanel;
		this.workflow = workflow;
		this.modify = modify;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.OK(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);
		
		if(modify) {
			txtName.setVisible(false);
			txtDescription.setText(workflow.getDescription());
		}

		increaseZIndex();
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (!modify) {
				String name = txtName.getText();
				if (name.isEmpty()) {
					return;
				}

				workflow.setName(name);
			}

			String description = txtDescription.getText();
			workflow.setDescription(description);

			creationPanel.saveWorkflow(workflow, modify);

			hide();
		}
	};

}
