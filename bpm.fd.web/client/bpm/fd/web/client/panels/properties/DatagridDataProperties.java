package bpm.fd.web.client.panels.properties;

import java.util.List;

import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.core.IComponentData;
import bpm.fd.core.component.ChartDrill;
import bpm.fd.core.component.DatagridColumn;
import bpm.fd.core.component.DatagridComponent;
import bpm.fd.web.client.panels.properties.widgets.DatagridColumnsPanel;
import bpm.gwt.commons.client.custom.CustomCheckbox;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class DatagridDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static ChartDataPropertiesUiBinder uiBinder = GWT.create(ChartDataPropertiesUiBinder.class);

	interface ChartDataPropertiesUiBinder extends UiBinder<Widget, DatagridDataProperties> {
	}

	@UiField
	ListBoxWithButton lstOrderType, lstOrderIndex;

	@UiField
	CustomCheckbox drill;

	@UiField
	DatagridColumnsPanel datagridColumnsPanel;

	private DatagridComponent component;
	private Dataset selectedDataset;

	public DatagridDataProperties(DatagridComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		this.component = component;

		lstOrderType.setList(OrderingType.getOrderTypes());
		lstOrderType.setSelectedIndex(OrderingType.getOrderTypes().indexOf(component.getOrderType()));

		if (component.getDrill() != null) {
			drill.setValue(true);
		}
	}

	@Override
	public void buildProperties(IComponentData component) {
		DatagridComponent comp = (DatagridComponent) component;

		comp.setOrderType((OrderingType) lstOrderType.getSelectedObject());
		comp.setOrderFieldIndex(lstOrderIndex.getSelectedIndex());

		comp.setDrill(drill.getValue() ? new ChartDrill() : null);
		
		List<DatagridColumn> columns = datagridColumnsPanel.getColumns();
		comp.setColumns(columns);
	}

	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		selectedDataset = dataset;

		lstOrderIndex.setList(dataset.getMetacolumns());
		if (component.getOrderFieldIndex() != null && component.getOrderFieldIndex() < dataset.getMetacolumns().size()) {
			lstOrderIndex.setSelectedIndex(component.getOrderFieldIndex());
		}

		datagridColumnsPanel.loadColumns(component, dataset.getMetacolumns(), refresh);
	}

	@Override
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}

	// public void refresh() {
	// dataprovider.refresh();
	// }

	public Dataset getSelectedDataset() {
		return selectedDataset;
	}
}
