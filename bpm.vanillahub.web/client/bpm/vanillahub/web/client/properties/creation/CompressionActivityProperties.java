package bpm.vanillahub.web.client.properties.creation;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanillahub.core.beans.activities.CompressionActivity;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.workflow.commons.beans.Activity;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public class CompressionActivityProperties extends PropertiesPanel<Activity> {

	private BoxItem item;
	private CompressionActivity activity;

	public CompressionActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, CompressionActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addVariableText(Labels.lblCnst.CompressionOutputFileName(), activity.getOutputFileVS(), WidgetWidth.PCT, null);
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
	}

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public Activity buildItem() {
		return activity;
	}
}
