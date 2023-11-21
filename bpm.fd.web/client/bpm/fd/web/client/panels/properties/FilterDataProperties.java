package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentData;
import bpm.fd.core.component.FilterComponent;
import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.vanilla.platform.core.beans.data.Dataset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class FilterDataProperties extends CompositeProperties<IComponentData> implements IComponentDataProperties {

	private static FilterDataPropertiesUiBinder uiBinder = GWT.create(FilterDataPropertiesUiBinder.class);

	interface FilterDataPropertiesUiBinder extends UiBinder<Widget, FilterDataProperties> {
	}
	
	@UiField
	ListBoxWithButton columnLabel, columnValue, columnOrder;
	
	private Dataset selectedDataset;

	private FilterComponent component;

	public FilterDataProperties(FilterComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.component = component;
	}
	
	@Override
	public void setDataset(Dataset dataset, boolean refresh) {
		selectedDataset = dataset;
		
		columnLabel.setList(dataset.getMetacolumns());
		columnValue.setList(dataset.getMetacolumns());
		columnOrder.setList(dataset.getMetacolumns(), true);
		
		if(component.getDataset() != null && component.getDataset().getId() == dataset.getId()) {
			if(component.getColumnLabelIndex() != null) {
				columnLabel.setSelectedIndex(component.getColumnLabelIndex());
			}
			if(component.getColumnValueIndex() != null) {
				columnValue.setSelectedIndex(component.getColumnValueIndex());
			}
			if(component.getColumnOrderIndex() != null) {
				columnOrder.setSelectedIndex(component.getColumnOrderIndex() + 1);
			}
		}
	}

	@Override
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}

	@Override
	public void buildProperties(IComponentData component) {
		FilterComponent comp = (FilterComponent) component;
		
		comp.setColumnLabelIndex(columnLabel.getSelectedIndex());
		comp.setColumnValueIndex(columnValue.getSelectedIndex());
		if(columnOrder.getSelectedIndex() > -1) {
			comp.setColumnOrderIndex(columnOrder.getSelectedIndex());
		}
		
	}

}
