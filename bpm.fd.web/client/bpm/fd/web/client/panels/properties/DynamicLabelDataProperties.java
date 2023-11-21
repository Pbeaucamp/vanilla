package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentData;
import bpm.fd.core.component.DynamicLabelComponent;
import bpm.fd.core.component.FilterComponent;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class DynamicLabelDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static FilterDataPropertiesUiBinder uiBinder = GWT.create(FilterDataPropertiesUiBinder.class);

	interface FilterDataPropertiesUiBinder extends UiBinder<Widget, DynamicLabelDataProperties> {
	}
	
	@UiField
	ListBoxWithButton<DataColumn> columnValue;

	private DynamicLabelComponent component;
	
	private Dataset selectedDataset;

	public DynamicLabelDataProperties(DynamicLabelComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		this.component = component;
	}
	
	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		selectedDataset = dataset;
		
		columnValue.setList(dataset.getMetacolumns());
		
		if(component.getDataset() != null && component.getDataset().getId() == dataset.getId()) {
			if(component.getColumnValueIndex() != null) {
				columnValue.setSelectedIndex(component.getColumnValueIndex());
			}
		}
	}

	@Override
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}

	@Override
	public void buildProperties(IComponentData component) {
		DynamicLabelComponent comp = (DynamicLabelComponent) component;
		comp.setColumnValueIndex(columnValue.getSelectedIndex());
	}

}
