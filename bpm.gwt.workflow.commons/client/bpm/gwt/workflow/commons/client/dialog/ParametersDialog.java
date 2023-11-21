package bpm.gwt.workflow.commons.client.dialog;

import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.workflow.commons.client.IWorkflowManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ParametersDialog extends AbstractDialogBox {

	private static ParametersDialogUiBinder uiBinder = GWT.create(ParametersDialogUiBinder.class);

	interface ParametersDialogUiBinder extends UiBinder<Widget, ParametersDialog> {
	}

	@UiField
	SimplePanel contentPanel;
	
	private IWorkflowManager workflowManager;
	private Workflow workflow;
	
	private ParametersPanel parametersPanel;

	public ParametersDialog(IWorkflowManager workflowManager, Workflow workflow, List<Parameter> parameters, List<ListOfValues> lovs) {
		super(LabelsCommon.lblCnst.Parameters(), false, true);
		this.workflowManager = workflowManager;
		this.workflow = workflow;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.OK(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);
		
		parametersPanel = new ParametersPanel(parameters, lovs);
		contentPanel.setWidget(parametersPanel);
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
			List<Parameter> parameters;
			try {
				parameters = parametersPanel.getParameters();
			} catch (Exception e) {
				MessageHelper.openMessageDialog(LabelsCommon.lblCnst.Error(), e.getMessage());
				return;
			}
			
			workflowManager.runWorkflow(workflow, parameters);
			hide();
		}
	};
}
