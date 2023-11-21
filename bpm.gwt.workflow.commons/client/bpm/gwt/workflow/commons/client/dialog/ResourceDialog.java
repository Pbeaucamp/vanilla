package bpm.gwt.workflow.commons.client.dialog;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.service.WorkflowsService;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanilla.platform.core.beans.resources.Resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class ResourceDialog extends AbstractDialogBox {

	private static ResourceDialogUiBinder uiBinder = GWT.create(ResourceDialogUiBinder.class);

	interface ResourceDialogUiBinder extends UiBinder<Widget, ResourceDialog> {
	}

	private IManager<? extends Resource> manager;

	private PropertiesPanel<Resource> propertiesPanel;

	private boolean edit;

	public ResourceDialog(IManager<? extends Resource> manager, Resource resource, PropertiesPanel<Resource> propertiesPanel) {
		super(resource == null ? LabelsCommon.lblCnst.Add() : LabelsCommon.lblCnst.Edit(), false, true);
		this.manager = manager;
		this.edit = resource != null;
		this.propertiesPanel = propertiesPanel;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsCommon.lblCnst.OK(), confirmHandler, LabelsCommon.lblCnst.Cancel(), cancelHandler);

		setWidget(propertiesPanel);
	}

	private ClickHandler confirmHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if(!propertiesPanel.isValid()) {
				return;
			}
			
			Resource resource = propertiesPanel.buildItem();
			manageResource(resource, edit);
		}
	};

	private void manageResource(Resource resource, boolean edit) {
		showWaitPart(true);

		WorkflowsService.Connect.getInstance().manageResource(resource, edit, new GwtCallbackWrapper<Resource>(this, true) {

			@Override
			public void onSuccess(Resource result) {
				hide();

				manager.loadResources();
			}
		}.getAsyncCallback());
	}

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
