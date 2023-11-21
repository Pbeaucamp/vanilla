package bpm.gwt.aklabox.commons.client.customs;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.CssResource;
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

	interface MyStyle extends CssResource {
		String none();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel mainPanel;

	@UiField
	HTMLPanel panelBtn;

	@UiField
	Label label;

	@UiField
	CustomListBox listBox;

	@UiField
	Button btn;

	private List<T> items;

	private boolean addEmptyItem;

	public ListBoxWithButton() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ListBoxWithButton(String label, List<T> lstItem, String btnText, ClickHandler clickHandler) {
		initWidget(uiBinder.createAndBindUi(this));
		this.label.setText(label);

		setList(lstItem);
		if (clickHandler != null) {
			btn.setText(btnText);
			btn.addClickHandler(clickHandler);
			panelBtn.removeStyleName(style.none());
		}
	}

	public void setLabel(String label) {
		this.label.setText(label);
	}

	public void setBtnText(String label) {
		panelBtn.removeStyleName(style.none());
		btn.setText(label);
	}

	public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
		panelBtn.removeStyleName(style.none());
		return btn.addClickHandler(clickHandler);
	}

	public void setEnabledBtn(boolean enabled) {
		btn.setEnabled(enabled);
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
		} else if (!addEmptyItem) {
			return items.get(listBox.getSelectedIndex());
		}
		return null;
	}
	
	public List<T> getSelectedObjects() {
		if (items == null || items.isEmpty()) {
			return null;
		}
		List<T> objects = new ArrayList<>();
		for(int i=0; i< listBox.getItemCount(); i++){
			if(listBox.isItemSelected(i)){
				if (addEmptyItem && listBox.getSelectedIndex() > 0) {
					objects.add(items.get(i - 1));
				} else if (!addEmptyItem) {
					objects.add(items.get(i));
				}
			}
		}
		
		return objects;
	}

	public void clear() {
		listBox.clear();
	}

	public void setEnabled(boolean enabled) {
		listBox.setEnabled(enabled);
	}

	public void setMultipleSelect(boolean enabled) {
		listBox.setMultipleSelect(enabled);
	}

	public HandlerRegistration addChangeHandler(ChangeHandler changeHandler) {
		return listBox.addChangeHandler(changeHandler);
	}

	public void addItem(String item) {
		this.listBox.addItem(item);
	}

	public void addItem(String item, String value) {
		this.listBox.addItem(item, value);
	}

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
	
	public List<Integer> getSelectedIndexes() {
		List<Integer> indexes = new ArrayList<>();
		for(int i=0; i< listBox.getItemCount(); i++){
			if(listBox.isItemSelected(i)){
				int index = listBox.getSelectedIndex();
				if (addEmptyItem) {
					index -= 1;
				}
				indexes.add(index);
			}
		}
		
		return indexes;
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

	public CustomListBox getListBox() {
		return listBox;
	}
}
