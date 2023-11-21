package bpm.fd.web.client.panels.properties;

import bpm.fd.core.IComponentOption;
import bpm.fd.core.component.DatagridComponent;
import bpm.gwt.commons.client.custom.CustomCheckbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class DataGridOptionsProperties extends CompositeProperties<IComponentOption> {

	private static DataGridOptionsPropertiesUiBinder uiBinder = GWT.create(DataGridOptionsPropertiesUiBinder.class);

	interface DataGridOptionsPropertiesUiBinder extends UiBinder<Widget, DataGridOptionsProperties> {
	}
	
	@UiField
	CustomCheckbox showHeader, includeTotals;

	public DataGridOptionsProperties(DatagridComponent component) {
		initWidget(uiBinder.createAndBindUi(this));
		
		showHeader.setValue(component.isHeadersVisible());
		includeTotals.setValue(component.isIncludeTotal());
	}

	@Override
	public void buildProperties(IComponentOption component) {
		DatagridComponent datagrid = (DatagridComponent) component;

		datagrid.setHeadersVisible(showHeader.getValue());
		datagrid.setIncludeTotal(includeTotals.getValue());
	}

}
