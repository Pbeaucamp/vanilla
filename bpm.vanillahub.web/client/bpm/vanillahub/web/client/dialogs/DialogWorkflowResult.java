package bpm.vanillahub.web.client.dialogs;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.dialog.DialogLogDetails;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.workflow.commons.beans.Log.Level;
import bpm.workflow.commons.beans.Result;
import bpm.workflow.commons.beans.WorkflowInstance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DialogWorkflowResult extends AbstractDialogBox {

	private static DialogWorkflowResultUiBinder uiBinder = GWT.create(DialogWorkflowResultUiBinder.class);

	interface DialogWorkflowResultUiBinder extends UiBinder<Widget, DialogWorkflowResult> {
	}
	
	interface MyStyle extends CssResource {
		String imgResult();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Image imgResult;
	
	@UiField
	Label lblResult;
	
	private String workflowName;
	private WorkflowInstance instance;

	public DialogWorkflowResult(String workflowName, WorkflowInstance instance) {
		super(Labels.lblCnst.WorkflowResult() + " : " + workflowName, false, true);
		this.workflowName = workflowName;
		this.instance = instance;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		if(instance.getResult() == Result.SUCCESS) {
			imgResult.setResource(Images.INSTANCE.success());
			lblResult.setText(LabelsCommon.lblCnst.SuccessRunWorkflow());
		}
		else {
			imgResult.setResource(Images.INSTANCE.failed());
			lblResult.setText(LabelsCommon.lblCnst.FailedRunWorkflow());
		}
		imgResult.addStyleName(style.imgResult());
		
		createButtonBar(LabelsCommon.lblCnst.ShowLogs(), showLogsHandler, LabelsCommon.lblCnst.Close(), closeHandler);;
	}

	private ClickHandler showLogsHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			String logs = instance.getLogs(Level.ALL);
			
			DialogLogDetails dial = new DialogLogDetails(workflowName, logs);
			dial.center();
		}
	};

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
