package bpm.gwt.aklabox.commons.client.customs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.User;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class CustomCheckboxCell<T> extends CheckboxCell {
	
	private List<T> groups;
	private SelectionModel<T> selectionModel;
	
	private boolean isSelected = false;
	
	public CustomCheckboxCell(List<T> groups, SelectionModel<T> selectionModel) {
		super();
		this.groups = groups;
		this.selectionModel = selectionModel;
	}
	
	public CustomCheckboxCell(List<T> groups, MultiSelectionModel<T> selectionModel) {
		super();
		this.groups = groups;
		this.selectionModel = selectionModel;
	}

	public void setGroups(List<T> groups) {
		this.groups = groups;
	}
	
	@Override
	public Set<String> getConsumedEvents() {
		Set<String> consumedEvents = new HashSet<String>();
		consumedEvents.add("click");
		return consumedEvents;
	}
	
	@Override
	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {

		isSelected = !isSelected;
		
		if(groups != null){
			for(T group : groups){
				selectionModel.setSelected(group, isSelected);
			}
		}
		
		super.onBrowserEvent(context, parent, value, Document.get().createChangeEvent(), valueUpdater);
	}
}
