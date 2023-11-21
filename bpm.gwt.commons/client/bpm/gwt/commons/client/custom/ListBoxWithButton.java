package bpm.gwt.commons.client.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ListBoxWithButton<T> extends Composite {

	private static ListBoxWithButtonUiBinder uiBinder = GWT.create(ListBoxWithButtonUiBinder.class);

	interface ListBoxWithButtonUiBinder extends UiBinder<Widget, ListBoxWithButton<?>> {
	}

	@UiField
	Label label;

	@UiField
	CustomListBox listBox;

	@UiField
	HTMLPanel mainPanel;

	private Button btn;

	private List<T> items;

	private boolean addEmptyItem;

	public ListBoxWithButton() {
		initWidget(uiBinder.createAndBindUi(this));
		
		listBox.getElement().setId(new Object().hashCode() + "");
	}

	public ListBoxWithButton(String label, List<T> lstItem, String btnText, ClickHandler clickHandler) {
		initWidget(uiBinder.createAndBindUi(this));

		this.label.setText(label);
		setList(lstItem);
		if (clickHandler != null) {
			if (btn == null) {
				btn = new Button();
				btn.getElement().getStyle().setMarginTop(10, Unit.PX);
				mainPanel.add(btn);
			}
			btn.setText(btnText);
			btn.addClickHandler(clickHandler);
		}
		
		listBox.getElement().setId(new Object().hashCode() + "");
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void setBtnText(String label) {
		if (btn == null) {
			btn = new Button();
			btn.getElement().getStyle().setMarginTop(10, Unit.PX);
			mainPanel.add(btn);
		}
		this.btn.setText(label);
	}

	public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
		if (btn == null) {
			btn = new Button();
			btn.getElement().getStyle().setMarginTop(10, Unit.PX);
			mainPanel.add(btn);
		}
		return btn.addClickHandler(clickHandler);
	}

	public void setList(List<T> lstItem) {
		setList(lstItem, false);
	}

	public void setListWithEmptyItem(List<T> lstItem) {
		setList(lstItem, true);
	}

	public void setList(List<T> lstItem, boolean addEmptyItem) {
		if (lstItem == null) {
			return;
		}
		this.addEmptyItem = addEmptyItem;

		clear();
		if (addEmptyItem) {
			listBox.addItem(LabelsConstants.lblCnst.NoSelection(), "");
		}

		for (T item : lstItem) {
			addItem(item);
		}
	}

	public void setList(T[] lstItem) {
		setList(Arrays.asList(lstItem), false);
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
		if (items == null || items.isEmpty() || listBox.getSelectedIndex() == -1) {
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
		if (items != null) {
			items.clear();
		}
		listBox.clear();
	}

	public void setEnabled(boolean enabled) {
		listBox.setEnabled(enabled);
	}

	public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
		return listBox.addChangeHandler(changeHandler);
	}

	public void addItem(T item) {
		addItem(item.toString(), item);
	}

	public void addItem(String value, T item) {
		if (items == null) {
			items = new ArrayList<T>();
		}
		items.add(item);
		listBox.addItem(value, value);
	}

	public void addItem(String item) {
		this.listBox.addItem(item);
	}

//	public void addItem(String item, String value) {
//		this.listBox.addItem(item, value);
//	}

	public void addItem(String item, int value) {
		this.listBox.addItem(item, String.valueOf(value));
	}

	public int getSelectedIndex() {
		int index = listBox.getSelectedIndex();
		if (addEmptyItem) {
			index -= 1;
		}
		return index;
	}

	public void setSelectedObject(T object) {
		if (object != null) {
			int index = items.indexOf(object);
			if (addEmptyItem) {
				index += 1;
			}
			listBox.setItemSelected(index, true);
		}
	}

	public void setWidth(String width) {
		listBox.setWidth(width);
	}

	public void fireChange() {
		NativeEvent event = Document.get().createChangeEvent();
		DomEvent.fireNativeEvent(event, listBox);
	}

	public int getItemAsInteger(int index) {
		return Integer.parseInt(listBox.getValue(index));
	}

	public int getItemCount() {
		return listBox.getItemCount();
	}

	public void setMultiple(boolean b) {
		listBox.setMultipleSelect(b);
	}

	public List<T> getSelectedItems() {
		if (items == null || items.isEmpty()) {
			return null;
		}
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				list.add(items.get(addEmptyItem ? i - 1 : i));
			}
		}

		return list;
	}

	public List<T> getSelectedItemsValue() {
		if (items == null || items.isEmpty()) {
			return null;
		}
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				list.add(items.get(addEmptyItem ? i - 1 : i));
			}
		}

		return list;
	}

	public CustomListBox getListBox() {
		return listBox;
	}

	public void setVisibleItemCount(int visibleItems) {
		listBox.setVisibleItemCount(visibleItems);
	}

	public static class KeyValueItem {
		private String key;
		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			KeyValueItem other = (KeyValueItem) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			}
			else if (!key.equals(other.key))
				return false;
			return true;
		}

	}
}
