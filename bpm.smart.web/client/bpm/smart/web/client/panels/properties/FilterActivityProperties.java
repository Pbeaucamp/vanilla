package bpm.smart.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.BoxItem;
import bpm.gwt.workflow.commons.client.workflow.CreationPanel;
import bpm.gwt.workflow.commons.client.workflow.WorkspacePanel;
import bpm.gwt.workflow.commons.client.workflow.properties.ColumnAirPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesListBox;
import bpm.gwt.workflow.commons.client.workflow.properties.PropertiesPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.VariablePropertiesText;
import bpm.smart.core.model.workflow.activity.FilterActivity;
import bpm.smart.core.model.workflow.activity.SortingActivity;
import bpm.smart.core.model.workflow.activity.custom.ColumnFilter;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.images.Images;
import bpm.smart.web.client.panels.properties.dialog.ColumnFilterDialog;
import bpm.smart.web.client.panels.properties.handler.RefreshWorkflowHandler;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.Workflow;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class FilterActivityProperties extends PropertiesPanel<Activity> {

	private WorkspacePanel parentCreationPanel;

	private FilterActivity activity;

	private PropertiesListBox lstDatasets;
	private VariablePropertiesText outputText;

	private SimplePanel columnPanel;

	private ColumnAirPanel airColumnPanel;

	public FilterActivityProperties(IResourceManager resourceManager, WorkspacePanel creationPanel, BoxItem item, FilterActivity filterActivity) {
		super(resourceManager, LabelsCommon.lblCnst.Name(), WidgetWidth.PCT, 0, filterActivity.getName(), true, false);

		this.activity = filterActivity;
		this.parentCreationPanel = creationPanel;

		setNameChecker(creationPanel);
		setNameChanger(item);

		addButton(LabelsConstants.lblCnst.RefreshWorkflow(), new RefreshWorkflowHandler(creationPanel, filterActivity));

		outputText = addVariableText(LabelsConstants.lblCnst.OutputDataset(), activity.getOutputDataset(), WidgetWidth.PCT, null);

		List<ListItem> items = new ArrayList<ListItem>();
		items.add(new ListItem(SortingActivity.SortType.ASC.toString(), SortingActivity.SortType.ASC.toString()));
		items.add(new ListItem(SortingActivity.SortType.DESC.toString(), SortingActivity.SortType.DESC.toString()));
		
		lstDatasets = addList(LabelsConstants.lblCnst.Dataset(), new ArrayList<ListItem>(), WidgetWidth.PCT, datasetChangeHandler, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refreshDatasets();
			}
		});

		columnPanel = addSimplePanel(false);
		airColumnPanel = new ColumnAirPanel(false);
		
		airColumnPanel.addButton(LabelsConstants.lblCnst.Filter(), Images.INSTANCE.action_b_24dp(), new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				final DataColumn col = airColumnPanel.getSelectedColumns().get(0);
				List<ColumnFilter> existings = activity.getFiltersByColumn(col);

				final ColumnFilterDialog dial = new ColumnFilterDialog(existings, col);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(dial.isConfirm()) {
							activity.setFiltersForColumn(dial.getColumnFilters(), col);
						}
					}
				});
				dial.center();
			}
		});
		
		columnPanel.add(airColumnPanel);

	}
	
	private ChangeHandler datasetChangeHandler = new ChangeHandler() {
		@Override
		public void onChange(ChangeEvent event) {
			reloadColumns();
		}
	};
	
	private void refreshDatasets() {
		CommonService.Connect.getInstance().getDatasets(new GwtCallbackWrapper<List<Dataset>>(null, false) {
			@Override
			public void onSuccess(List<Dataset> result) {
				List<ListItem> items = new ArrayList<ListItem>();
				
				boolean first = true;
				Integer dsIndex = null;
				int i = 0;
				for(Dataset dataset : result) {
					ListItem item = new ListItem(dataset.getName(), dataset.getName());
					items.add(item);
					if(first) {
						airColumnPanel.init(dataset.getMetacolumns(), null);
						first = false;
					}
					if(activity.getInputDataset().equals(dataset.getName())) {
						dsIndex = i;
					}
					i++;
				}
				
				Workflow workflow = parentCreationPanel.buildWorkflow(false);
				List<String> outputs = workflow.getOutputs();
				for(String out : outputs) {
					ListItem item = new ListItem(out, out);
					items.add(item);
					if(activity.getInputDataset().equals(out)) {
						dsIndex = i;
					}
					i++;
				}
				
				lstDatasets.setItems(items);
				if(dsIndex != null) {
					lstDatasets.setSelectedIndex(dsIndex);
					reloadColumns();
				}
			}
		}.getAsyncCallback());
	}
	
	private void reloadColumns() {
		final String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());

		SmartAirService.Connect.getInstance().getDatasetColumns(datasetName, new GwtCallbackWrapper<List<DataColumn>>(null, false) {
			@Override
			public void onSuccess(List<DataColumn> result) {
//				if(!activity.getInputDataset().isEmpty() && activity.getInputDataset().equals(datasetName)) {
//					if(activity.getColumns() != null && ! activity.getColumns().isEmpty()) {
//						MultiSelectionModel<DataColumn> selection = new MultiSelectionModel<DataColumn>();
//						for(DataColumn col : result) {
//							if(activity.getColumns().contains(col)) {
//								selection.setSelected(col, true);
//							}
//						}
//						airColumnPanel.init(result, selection);
//					}
//					else {
//						airColumnPanel.init(result, null);
//					}
//				}
//				else {
//					airColumnPanel.init(result, null);
//				}
			}
		}.getAsyncCallback());
	}

	@Override
	public Activity buildItem() {
		String datasetName = lstDatasets.getValue(lstDatasets.getSelectedIndex());
		activity.setInputDataset(datasetName);
		activity.setOutputDataset(outputText.getVariableText());
		
//		activity.setColumns(airColumnPanel.getSelectedColumns());
		
		return activity;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
