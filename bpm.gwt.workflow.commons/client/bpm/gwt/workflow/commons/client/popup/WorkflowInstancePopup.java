package bpm.gwt.workflow.commons.client.popup;

import java.util.List;

import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.workflow.commons.beans.Workflow;
import bpm.workflow.commons.beans.WorkflowInstance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkflowInstancePopup extends PopupPanel {

	private static LevelPopupUiBinder uiBinder = GWT.create(LevelPopupUiBinder.class);

	interface LevelPopupUiBinder extends UiBinder<Widget, WorkflowInstancePopup> {
	}

	interface MyStyle extends CssResource {
		String labelType();

		String labelTypeEnd();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelMenu;

	private ISelectInstance selector;
	private Workflow workflow;
	
	public WorkflowInstancePopup(ISelectInstance selector, Workflow workflow, List<WorkflowInstance> instances) {
		setWidget(uiBinder.createAndBindUi(this));
		this.selector = selector;
		this.workflow = workflow;

		panelMenu.addStyleName(VanillaCSS.POPUP_PANEL);

		boolean first = true;
		for (WorkflowInstance instance : instances) {
			
			addInstance(instance, first);
			first = false;
		}

		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
	}

	private void addInstance(WorkflowInstance instance, boolean first) {
		Label lblTheme = new Label(workflow.getName() + " - (" + instance.getStartDate().toString() + ")");
		lblTheme.addClickHandler(new InstanceHandler(workflow, instance));
		if (first) {
			lblTheme.addStyleName(style.labelType());
		}
		else {
			lblTheme.addStyleName(style.labelTypeEnd());
		}

		panelMenu.add(lblTheme);
	}

	private class InstanceHandler implements ClickHandler {

		private Workflow workflow;
		private WorkflowInstance instance;

		public InstanceHandler(Workflow workflow, WorkflowInstance instance) {
			this.workflow = workflow;
			this.instance = instance;
		}

		@Override
		public void onClick(ClickEvent event) {
			selector.selectInstance(workflow, instance);
			WorkflowInstancePopup.this.hide();
		}
	}
}
