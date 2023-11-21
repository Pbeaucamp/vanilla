package bpm.fd.web.client.panels.properties.widgets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.fd.core.component.DatagridColumn;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.view.client.ListDataProvider;

public class DatagridHeaderCheckbox extends CheckboxCell {

	private ListDataProvider<DatagridColumn> dataProvider;
	private List<DatagridColumn> columns;
	private boolean isVisible = true;

	public DatagridHeaderCheckbox(ListDataProvider<DatagridColumn> dataProvider, List<DatagridColumn> columns) {
		super();
		this.dataProvider = dataProvider;
		this.columns = columns;
	}

	public void setList(List<DatagridColumn> columns) {
		this.columns = columns;
	}

	@Override
	public Set<String> getConsumedEvents() {
		Set<String> consumedEvents = new HashSet<String>();
		consumedEvents.add("click");
		return consumedEvents;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {

		isVisible = !isVisible;

		if (columns != null) {
			for (DatagridColumn column : columns) {
				column.setVisible(isVisible);
			}
		}

		dataProvider.refresh();

		super.onBrowserEvent(context, parent, value, Document.get().createChangeEvent(), valueUpdater);
	}
}
