package bpm.vanillahub.web.client.properties.creation;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.vanillahub.core.beans.activities.MergeFilesActivity;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.tabs.resources.ResourceManager;
import bpm.workflow.commons.beans.Activity;

public class MergeFilesActivityProperties extends PropertiesPanel<Activity> {

	private ResourceManager resourceManager;
	private BoxItem item;
	private MergeFilesActivity activity;

	private PropertiesListBox lstTypeLimeSurvey, lstFormats;

	public MergeFilesActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, final MergeFilesActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);
		this.resourceManager = (ResourceManager) resourceManager;
		this.item = item;
		this.activity = activity;

		setNameChecker(creationPanel);
		setNameChanger(item);

//		addVariableText(Labels.lblCnst.LimeSurveyId(), activity.getLimeSurveyIdVS(), WidgetWidth.PCT, null);
		addVariableText(LabelsCommon.lblCnst.OutputFileName(), activity.getOutputFileVS(), WidgetWidth.PCT, null);
//		
//		List<ListItem> types = new ArrayList<ListItem>();
//		types.add(new ListItem(Labels.lblCnst.ClassicLimeSurvey(), LimeSurveyType.LIMESURVEY.getType()));
//		types.add(new ListItem(Labels.lblCnst.LimeSurveyWithShapes(), LimeSurveyType.LIMESURVEY_SHAPES.getType()));
//
//		lstTypeLimeSurvey = addList(Labels.lblCnst.LimeSurveyType(), types, WidgetWidth.LARGE_PX, changeType, null);
//		lstTypeLimeSurvey.setSelectedIndex(activity.getLimeSurveyType().getType());
//		
//		List<ListItem> formats = new ArrayList<ListItem>();
//		formats.add(new ListItem(LimeSurveyResponseFormat.CSV.toString(), LimeSurveyResponseFormat.CSV.getType()));
//		formats.add(new ListItem(LimeSurveyResponseFormat.PDF.toString(), LimeSurveyResponseFormat.PDF.getType()));
//		formats.add(new ListItem(LimeSurveyResponseFormat.XLS.toString(), LimeSurveyResponseFormat.XLS.getType()));
//		formats.add(new ListItem(LimeSurveyResponseFormat.DOC.toString(), LimeSurveyResponseFormat.DOC.getType()));
//		formats.add(new ListItem(LimeSurveyResponseFormat.JSON.toString(), LimeSurveyResponseFormat.JSON.getType()));
//
//		lstFormats = addList(Labels.lblCnst.Format(), formats, WidgetWidth.LARGE_PX, changeFormat, null);
//		lstFormats.setSelectedIndex(activity.getFormat().getType());
		
		addCheckbox(Labels.lblCnst.Loop(), activity.isLoop(), loopChangeHandler);
		
		updateUi();
	}
	
	private void updateUi() {
//		lstFormats.setVisible(activity.getLimeSurveyType() == LimeSurveyType.LIMESURVEY);
	}
	
//	private ChangeHandler changeType = new ChangeHandler() {
//
//		@Override
//		public void onChange(ChangeEvent event) {
//			LimeSurveyType selectedType = LimeSurveyType.valueOf(Integer.parseInt(lstTypeLimeSurvey.getValue()));
//			activity.setLimeSurveyType(selectedType);
//			
//			updateUi();
//		}
//	};
//	
//	private ChangeHandler changeFormat = new ChangeHandler() {
//
//		@Override
//		public void onChange(ChangeEvent event) {
//			LimeSurveyResponseFormat selectedFormat = LimeSurveyResponseFormat.valueOf(Integer.parseInt(lstFormats.getValue()));
//			activity.setFormat(selectedFormat);
//		}
//	};
//
//	private ClickHandler refreshHandler = new ClickHandler() {
//		@Override
//		public void onClick(ClickEvent event) {
//			resourceManager.loadApplicationServers(MergeFilesActivityProperties.this, MergeFilesActivityProperties.this);
//		}
//	};

	private ValueChangeHandler<Boolean> loopChangeHandler = new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			activity.setLoop(event.getValue());
			item.updateStyle(event.getValue());
		}
	};

	@Override
	public boolean isValid() {
		return activity.getOutputFileDisplay() != null && !activity.getOutputFileDisplay().isEmpty();
	}

	@Override
	public Activity buildItem() {
		return activity;
	}
}
