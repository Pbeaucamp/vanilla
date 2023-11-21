package bpm.gwt.commons.client.custom.v2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.view.client.SelectionModel;

/**
 * Header for datagrid with a checkbox to enable/disable all items
 * 
 * @param <T>
 */
public class HeaderCheckboxCell<T> extends CheckboxCell {

	private IHeaderCheckboxManager<T> manager;

	private List<T> items;
	private SelectionModel<T> selectionModel;

	private boolean isSelected = false;

	public HeaderCheckboxCell(IHeaderCheckboxManager<T> manager, SelectionModel<T> selectionModel) {
		this(manager, null, selectionModel);
	}

	public HeaderCheckboxCell(IHeaderCheckboxManager<T> manager, List<T> items, SelectionModel<T> selectionModel) {
		super();
		this.manager = manager;
		this.items = items;
		this.selectionModel = selectionModel;
	}

	public List<T> getItems() {
		return items;
	}

	public void loadItems(List<T> items) {
		this.items = items;
	}

	@Override
	public Set<String> getConsumedEvents() {
		Set<String> consumedEvents = new HashSet<String>();
		consumedEvents.add("click");
		return consumedEvents;
	}

	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		this.isSelected = !isSelected;

		if (items != null) {
			for (T item : items) {
				if (selectionModel != null)
					selectionModel.setSelected(item, isSelected);
				if (manager != null)
					manager.update(item, isSelected);
			}
		}
		
		if (manager != null)
			manager.refreshGrid();

		super.onBrowserEvent(context, parent, value, Document.get().createChangeEvent(), valueUpdater);
	}
}
