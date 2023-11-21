package bpm.gwt.workflow.commons.client.dialog;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.workflow.commons.beans.ActivityOutput;
import bpm.workflow.commons.beans.WorkflowInstance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DialogOutputs extends AbstractDialogBox {

	private static DialogOutputsUiBinder uiBinder = GWT.create(DialogOutputsUiBinder.class);

	interface DialogOutputsUiBinder extends UiBinder<Widget, DialogOutputs> {
	}
	
	interface MyStyle extends CssResource {
		String container();
		
		String containerFull();
	}
	@UiField
	HTMLPanel panel;
	
	@UiField
	ListBox lstOutputs;

	@UiField
	Frame frame;
	
	@UiField
	MyStyle style;

	public DialogOutputs(WorkflowInstance instance) {
		super(LabelsCommon.lblCnst.Outputs() + " : " + instance.getWorkflowName(), true, true);

		setWidget(uiBinder.createAndBindUi(this));
		
		for(ActivityOutput out : instance.getOutputs()) {
			lstOutputs.addItem(out.getName(), out.getPath());
		}
		
		NativeEvent event = Document.get().createChangeEvent();
		ChangeEvent.fireNativeEvent(event, lstOutputs);
		
	}

	@UiHandler("lstOutputs")
	public void onOutput(ChangeEvent e) {
		String path = lstOutputs.getValue(lstOutputs.getSelectedIndex());

		path = "../" + path.replace("webapps/", "");
		
		frame.setUrl(path);
		
	}
	
	@Override
	public void maximize(boolean maximize) {
		if(maximize){
			panel.setStyleName(style.containerFull());
		} else {
			panel.setStyleName(style.container());
		}
	}
	
}
