package bpm.smart.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.smart.core.model.workflow.activity.HeadActivity;
import bpm.smart.core.model.workflow.activity.HeadActivity.TypeHead;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.properties.handler.RefreshWorkflowHandler;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

public class HeadActivityProperties extends PropertiesPanel<Activity> {

	private WorkspacePanel parentCreationPanel;
	private HeadActivity activity;

	private PropertiesListBox lstDatasets;
	private VariablePropertiesText outputText, linesNumber;

	public HeadActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, HeadActivity activity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, activity.getName(), true, false);

		this.activity = activity;
		this.parentCreationPanel = creationPanel;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addButton(LabelsConstants.lblCnst.RefreshWorkflow(), new RefreshWorkflowHandler(creationPanel, activity));

		lstDatasets = addList(LabelsConstants.lblCnst.Dataset(), new ArrayList<ListItem>(), WidgetWidth.PCT, null, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshDatasets();
			}
		});
		
		List<ListItem> actions = new ArrayList<ListItem>();
		int i = 0;
		int selectedActionIndex = -1;
		for (TypeHead type : TypeHead.values()) {
			actions.add(new ListItem(type.toString(), type.getType()));

			if (activity.getTypeHead() == type) {
				selectedActionIndex = i;
			}
			i++;
		}

		PropertiesListBox lstType = addList(LabelsConstants.lblCnst.SelectTypeHead(), actions, WidgetWidth.SMALL_PX, changeTypeSource, null);
		if (selectedActionIndex != -1) {
			lstType.setSelectedIndex(selectedActionIndex);
		}

		outputText = addVariableText(LabelsConstants.lblCnst.OutputDataset(), activity.getOutputDatasetVS(), WidgetWidth.PCT, null);
		linesNumber = addVariableText(LabelsConstants.lblCnst.LinesNumber(), activity.getLinesNumberVS(), WidgetWidth.PCT, null);
	}

	private void refreshDatasets() {
		CommonService.Connect.getInstance().getPermittedDatasets(new GwtCallbackWrapper<List<Dataset>>(null, false) {
			@Override
			public void onSuccess(List<Dataset> result) {
				List<ListItem> items = new ArrayList<ListItem>();

				Integer dsIndex = null;
				int i = 0;
				for (Dataset dataset : result) {
					ListItem item = new ListItem(dataset.getName(), dataset.getName());
					items.add(item);
					if (activity.getInputDataset().equals(dataset.getName())) {
						dsIndex = i;
					}
					i++;
				}

				Workflow workflow = parentCreationPanel.buildWorkflow(false);
				List<String> outputs = workflow.getOutputs();
				for (String out : outputs) {
					ListItem item = new ListItem(out, out);
					items.add(item);
					if (activity.getInputDataset().equals(out)) {
						dsIndex = i;
					}
					i++;
				}

				lstDatasets.setItems(items);
				if (dsIndex != null) {
					lstDatasets.setSelectedIndex(dsIndex);
				}
			}
		}.getAsyncCallback());
	}

	private ChangeHandler changeTypeSource = new ChangeHandler() {

		@Override
		public void onChange(ChangeEvent event) {
			ListBox lst = (ListBox) event.getSource();
			int type = Integer.parseInt(lst.getValue(lst.getSelectedIndex()));

			activity.setTypeHead(TypeHead.valueOf(type));
		}
	};

	@Override
	public Activity buildItem() {
		String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());
		activity.setInputDataset(datasetName);
		activity.setOutputDataset(outputText.getVariableText());
		activity.setLinesNumber(linesNumber.getVariableText());

		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
