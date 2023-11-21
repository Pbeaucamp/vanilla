package bpm.fd.web.client.panels.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.fd.core.ComponentParameter;
import bpm.fd.core.IComponentData;
import bpm.fd.core.component.ChartComponent;
import bpm.fd.core.component.DatagridComponent;
import bpm.fd.core.component.DynamicLabelComponent;
import bpm.fd.core.component.FilterComponent;
import bpm.fd.core.component.GaugeComponent;
import bpm.fd.core.component.KpiChartComponent;
import bpm.fd.core.component.MapComponent;
import bpm.fd.core.component.RChartComponent;
import bpm.fd.web.client.ClientSession;
import bpm.fd.web.client.MainPanel;
import bpm.fd.web.client.services.DashboardService;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.dataset.DatasetCreationDialog;
import bpm.gwt.commons.client.datasource.DatasourceDatasetManager;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetParameter;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DataProperties extends CompositeProperties<IComponentData> {

	private static DataPropertiesUiBinder uiBinder = GWT.create(DataPropertiesUiBinder.class);

	interface DataPropertiesUiBinder extends UiBinder<Widget, DataProperties> {
	}

	@UiField
	SimplePanel panelDataOptions;

	@UiField
	ListBoxWithButton<Datasource> lstDatasources;

	@UiField
	ListBoxWithButton<Dataset> lstDatasets;

	private IWait waitPanel;

	private CompositeProperties<IComponentData> compositeProperties;

	private List<Datasource> datasources;
	private List<Dataset> datasets = new ArrayList<>();

	private PropertiesPanel propertiesPanel;

	public DataProperties(IWait waitPanel, IComponentData component, PropertiesPanel propertiesPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		this.propertiesPanel = propertiesPanel;

		if (component instanceof ChartComponent) {
			this.compositeProperties = new ChartDataProperties((ChartComponent) component);
		}
		else if (component instanceof RChartComponent) {
			this.compositeProperties = new RChartDataProperties((RChartComponent) component);
		}
		else if (component instanceof FilterComponent) {
			this.compositeProperties = new FilterDataProperties((FilterComponent) component);
		}
		else if (component instanceof GaugeComponent) {
			this.compositeProperties = new GaugeDataProperties((GaugeComponent) component);
		}
		else if (component instanceof DatagridComponent) {
			this.compositeProperties = new DatagridDataProperties((DatagridComponent) component);
		}
		else if (component instanceof MapComponent) {
			this.compositeProperties = new MapDataProperties((MapComponent) component);
		}
		else if (component instanceof KpiChartComponent) {
			this.compositeProperties = new KpiDataProperties((KpiChartComponent) component, this);
		}
		else if (component instanceof DynamicLabelComponent) {
			this.compositeProperties = new DynamicLabelDataProperties((DynamicLabelComponent) component);
		}

		panelDataOptions.add(compositeProperties);

		if (datasets != null && !datasets.isEmpty()) {
			((IComponentDataProperties) compositeProperties).setDataset(datasets.get(0), false);
		}

		loadLists();

		if (component.getDataset() != null) {
			int datasourceId = component.getDataset().getDatasourceId();
			int datasetId = component.getDataset().getId();

			int i = 0;
			for (Datasource ds : datasources) {
				if (ds.getId() == datasourceId) {
					lstDatasources.setSelectedIndex(i);
					DashboardService.Connect.getInstance().getDatasets(ds, new GwtCallbackWrapper<List<Dataset>>(waitPanel, true, true) {

						@Override
						public void onSuccess(List<Dataset> result) {
							ds.setDatasets(result);
							
							loadDataset(datasetId, ds);
						}
					}.getAsyncCallback());
					break;
				}
				i++;
			}

		}
	}
	
	private void loadDataset(int datasetId, Datasource ds) {
		this.datasets = ds.getDatasets();
		lstDatasets.setList(datasets);
		int j = 0;
		for (Dataset d : ds.getDatasets()) {
			if (d.getId() == datasetId) {
				lstDatasets.setSelectedIndex(j);
				((IComponentDataProperties) compositeProperties).setDataset(d, false);
				loadDatasetInformation(d);
				break;
			}
			j++;
		}
	}

	public void hideDatasetSelection() {
		lstDatasources.removeFromParent();
		lstDatasets.removeFromParent();
	}

	private void loadLists() {
		Datasource datasource = null;

		this.datasources = ClientSession.getInstance().getDatasources();
		if (lstDatasources.getSelectedObject() != null) {
			datasource = lstDatasources.getSelectedObject();
		}
		else if (datasources != null && !datasources.isEmpty()) {
			datasource = datasources.get(0);
		}

		lstDatasources.setList(datasources);
		lstDatasources.setSelectedObject(datasource);
		
		if (datasource != null) {
			Datasource selectedSource = datasource;
			DashboardService.Connect.getInstance().getDatasets(selectedSource, new GwtCallbackWrapper<List<Dataset>>(waitPanel, true, true) {

				@Override
				public void onSuccess(List<Dataset> result) {
					selectedSource.setDatasets(result);
					
					loadDatasetList(selectedSource);
				}
			}.getAsyncCallback());
		}
	}

	private void loadDatasetList(Datasource selectedSource) {
		Dataset selectedSet = null;
		this.datasets = selectedSource.getDatasets();
		
		if (lstDatasets.getSelectedObject() != null) {
			selectedSet = lstDatasets.getSelectedObject();
		}
		else if (selectedSource.getDatasets() != null && !selectedSource.getDatasets().isEmpty()) {
			selectedSet = selectedSource.getDatasets().get(0);
		}

		lstDatasets.setList(datasets);
		lstDatasets.setSelectedObject(selectedSet);

		if (selectedSet != null) {
			((IComponentDataProperties) compositeProperties).setDataset(selectedSet, false);
			loadDatasetInformation(selectedSet);
		}
	}

	private void loadDatasetInformation(Dataset dataset) {
		List<ComponentParameter> result = new ArrayList<ComponentParameter>();
		for (DatasetParameter param : dataset.getParameters()) {
			ComponentParameter p = new ComponentParameter();
			p.setIndex(param.getIndex());
			p.setName(param.getName());
			result.add(p);
		}
		propertiesPanel.loadParameters(null, result);
	}

	private void refreshDatasourceDataset() {
		DashboardService.Connect.getInstance().getDatasources(new GwtCallbackWrapper<List<Datasource>>(null, false) {
			@Override
			public void onSuccess(List<Datasource> result) {
				ClientSession.getInstance().setDatasources(result);

				loadLists();
			}
		}.getAsyncCallback());

	}

	@UiHandler("lstDatasources")
	public void onDatasourceChange(ChangeEvent event) {
		Datasource selected = lstDatasources.getSelectedObject();
		lstDatasets.setList(selected.getDatasets());

		DashboardService.Connect.getInstance().getDatasets(selected, new GwtCallbackWrapper<List<Dataset>>(waitPanel, true, true) {

			@Override
			public void onSuccess(List<Dataset> result) {
				selected.setDatasets(result);

				DataProperties.this.datasets = selected.getDatasets();
				if (datasets != null && !datasets.isEmpty()) {
					((IComponentDataProperties) compositeProperties).setDataset(datasets.get(0));
					loadDatasetInformation(datasets.get(0));
				}
			}
		}.getAsyncCallback());
	}

	@UiHandler("lstDatasets")
	public void onDatasetChange(ChangeEvent event) {
		Dataset selected = lstDatasets.getSelectedObject();
		((IComponentDataProperties) compositeProperties).setDataset(selected);
		loadDatasetInformation(selected);
	}

	@UiHandler("lstDatasources")
	public void onDatasourceClick(ClickEvent event) {
		User user = MainPanel.getInstance().getInfoUser().getUser();

		DatasourceDatasetManager dialog = new DatasourceDatasetManager(user);
		dialog.setModal(true);
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				refreshDatasourceDataset();
			}
		});
		dialog.center();
	}

	@UiHandler("lstDatasets")
	public void onDatasetClick(ClickEvent event) {
		User user = MainPanel.getInstance().getInfoUser().getUser();

		Datasource ds = (Datasource) lstDatasources.getSelectedObject();
		if (ds != null) {
			DatasetCreationDialog dial = new DatasetCreationDialog(user, ds, (Dataset) lstDatasets.getSelectedObject());
			dial.setModal(true);
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					refreshDatasourceDataset();
				}
			});
			dial.center();
		}
	}

	@Override
	public void buildProperties(IComponentData component) {
		component.setDataset((Dataset) lstDatasets.getSelectedObject());

		compositeProperties.buildProperties(component);
	}
}
