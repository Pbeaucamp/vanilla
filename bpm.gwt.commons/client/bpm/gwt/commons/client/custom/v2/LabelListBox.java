package bpm.gwt.commons.client.custom.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomListBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ListBox with a label
 * 
 * You can also display a {@link ButtonTooltip}
 */
public class LabelListBox<T> extends CompositeData<T> implements ChangeHandler {

	private static ListBoxWithButtonUiBinder uiBinder = GWT.create(ListBoxWithButtonUiBinder.class);

	interface ListBoxWithButtonUiBinder extends UiBinder<Widget, LabelListBox<?>> {
	}
	
	interface MyStyle extends CssResource {
		String panelText();
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelLabel;
	
	@UiField
	SimplePanel panelText;

	@UiField
	Label label, required;

	@UiField
	CustomListBox listBox;

	private List<T> items;

	private boolean addEmptyItem;

	public LabelListBox() {
		initWidget(uiBinder.createAndBindUi(this));
		addChangeHandler(this);
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void setList(T[] arrayItem) {
		setList(arrayItem, false);
	}

	public void setList(T[] arrayItem, boolean addEmptyItem) {
		setList(Arrays.asList(arrayItem), addEmptyItem);
	}

	public void setList(List<T> lstItem) {
		setList(lstItem, false);
	}

	public void setList(List<T> lstItem, boolean addEmptyItem) {
		if (lstItem == null) {
			return;
		}
		this.addEmptyItem = addEmptyItem;
		items = lstItem;

		listBox.clear();
		if (addEmptyItem) {
			listBox.addItem("", "");
		}

		for (Object item : lstItem) {
			listBox.addItem(item.toString(), item.toString());
		}
	}

	public String getSelectedItem() {
		return listBox.getValue(listBox.getSelectedIndex());
	}

	public int getSelectedItemAsInteger() {
		return Integer.parseInt(listBox.getValue(listBox.getSelectedIndex()));
	}

	public void setSelectedIndex(int index) {
		this.listBox.setSelectedIndex(index);
	}

	public T getSelectedObject() {
		if (items == null || items.isEmpty()) {
			return null;
		}
		if (addEmptyItem && listBox.getSelectedIndex() > 0) {
			return items.get(listBox.getSelectedIndex() - 1);
		}
		else if (!addEmptyItem) {
			return items.get(listBox.getSelectedIndex());
		}
		return null;
	}

	public void clear() {
		listBox.clear();
		if (items != null) {
			items.clear();
		}
	}

	public void setEnabled(boolean enabled) {
		listBox.setEnabled(enabled);
	}

	public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
		return listBox.addChangeHandler(changeHandler);
	}

//	public void addItem(T item) {
//		addItem(item.toString(), item);
//	}

//	public void addItem(String item, int value) {
//		this.listBox.addItem(item, String.valueOf(value));
//	}

	public void addEmptyItem() {
		this.addEmptyItem = true;
		listBox.addItem("", "");
	}

	public void addItem(String label, T value) {
		if (items == null) {
			items = new ArrayList<T>();
		}
		if (value != null) {
			items.add(value);
			listBox.addItem(label, value.toString());
		}
		else {
			listBox.addItem("", "");
		}
	}

	public void removeItem(T value) {
		if (value != null && items != null) {
			int index = -1;
			for (int i=0; i<items.size(); i++) {
				T item = items.get(i);
				if (item.equals(value)) {
					index = i;
					break;
				}
			}
			items.remove(value);
			if (index != -1) {
				listBox.removeItem(index);
			}
		}
	}

	public int getSelectedIndex() {
		int index = listBox.getSelectedIndex();
		if (addEmptyItem) {
			index -= 1;
		}
		return index;
	}

	@Override
	public void setWidth(String width) {
		listBox.setWidth(width);
	}

	public List<T> getList() {
		return items;
	}

	public int getItemCount() {
		return listBox.getItemCount();
	}

	public String getValue(int index) {
		return listBox.getValue(index);
	}

	public void setLabelWidth(int width) {
		this.panelLabel.setWidth(width + "px");
	}

	public void setListWidth(int width) {
		this.listBox.setWidth(width + "px");
	}

	@Override
	public T getValue() {
		return getSelectedObject();
	}

	@Override
	public void setValue(T value) {
		if (value != null) {
			int selectedIndex = items != null ? items.indexOf(value) : -1;
			if (addEmptyItem) {
				selectedIndex += 1;
			}
			listBox.setSelectedIndex(selectedIndex);
		}
		else {
			listBox.setSelectedIndex(0);
		}
	}

	@Override
	public Label getRequired() {
		return required;
	}

	@Override
	public void onChange(ChangeEvent event) {
		onValueChange();
	}
	
	public void setPanelTextMarginLeft(boolean isMarginLeft){
		if(isMarginLeft){
			this.panelText.setStyleName(style.panelText());
		}
		else{
			this.panelText.removeStyleName(style.panelText());
		}
	}

}
