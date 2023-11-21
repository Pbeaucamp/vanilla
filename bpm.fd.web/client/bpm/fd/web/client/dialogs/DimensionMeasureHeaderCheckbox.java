package bpm.fd.web.client.dialogs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.fd.core.component.CubeElement;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.view.client.ListDataProvider;

public class DimensionMeasureHeaderCheckbox extends CheckboxCell {

	private ListDataProvider<CubeElement> dataProvider;
	private List<CubeElement> columns;
	private boolean isVisible = true;

	public DimensionMeasureHeaderCheckbox(ListDataProvider<CubeElement> dataProvider, List<CubeElement> columns) {
		super();
		this.dataProvider = dataProvider;
		this.columns = columns;
	}

	public void setList(List<CubeElement> columns) {
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
			for (CubeElement column : columns) {
				column.setVisible(isVisible);
			}
		}

		dataProvider.refresh();

		super.onBrowserEvent(context, parent, value, Document.get().createChangeEvent(), valueUpdater);
	}
}
