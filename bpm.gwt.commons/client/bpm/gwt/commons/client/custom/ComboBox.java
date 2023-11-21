package bpm.gwt.commons.client.custom;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ComboBox<T> extends Composite implements HasChangeHandlers {

	private static ComboBoxUiBinder uiBinder = GWT.create(ComboBoxUiBinder.class);

	interface ComboBoxUiBinder extends UiBinder<Widget, ComboBox<?>> {
	}

	interface MyStyle extends CssResource {
		String item();
	}

	@UiField
	MyStyle style;

	@UiField
	LabelTextBox input;

	@UiField
	HTMLPanel panelDrop;

	private List<ComboBoxItem<T>> items;
	private List<ComboBoxItem<T>> filteredItems;

	private ComboBoxItem<T> selectedItem;
	
	private boolean userHasSelected;

	public ComboBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPlaceHolder(String placeHolder) {
		input.setPlaceHolder(placeHolder);
	}

	@UiHandler("input")
	public void onKeyUpInput(KeyUpEvent event) {
		String filter = input.getText();
		this.filteredItems = filter(filter);

		refreshItems(filteredItems);
	}

	@UiHandler("input")
	public void onInput(ClickEvent event) {
		this.userHasSelected = false;
		panelDrop.setVisible(true);
	}

	@UiHandler("input")
	public void onInputFocusLost(BlurEvent event) {
		panelDrop.setVisible(false);

		if (!userHasSelected) {
			String text = input.getText();
			if (text.isEmpty()) {
				selectItem(null);
				return;
			}
			
			ComboBoxItem<T> comboItem = foundItem(text);
			
			if (comboItem != null) {
				selectItem(comboItem);
			}
			else if (selectedItem != null) {
				selectItem(selectedItem);
			}
			else {
				input.setText("");
			}
		}
	}

	private ComboBoxItem<T> foundItem(String text) {
		if (items != null) {
			for (ComboBoxItem<T> item : items) {
				if (item.getText().equals(text)) {
					return item;
				}
			}
		}
		return null;
	}

	private List<ComboBoxItem<T>> filter(String filter) {
		List<ComboBoxItem<T>> filteredItems = new ArrayList<>();
		if (items != null) {
			for (ComboBoxItem<T> item : items) {
				if (item.getText().startsWith(filter)) {
					filteredItems.add(item);
				}
			}
		}
		return filteredItems;
	}

	private void refreshItems(List<ComboBoxItem<T>> filteredItems) {
		panelDrop.clear();
		
		for (ComboBoxItem<T> item : filteredItems) {
			panelDrop.add(item);
		}
	}

	public void addItem(String text, T item) {
		if (items == null) {
			this.items = new ArrayList<>();
		}

		ComboBoxItem<T> comboItem = new ComboBoxItem<T>(text, item);
		comboItem.addMouseDownHandler(selectionHandler);
		items.add(comboItem);
		panelDrop.add(comboItem);
	}

	public T getSelectedItem() {
		return selectedItem != null ? selectedItem.getItem() : null;
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}
	
	private void selectItem(ComboBoxItem<T> comboItem) {
		input.setText(comboItem != null ? comboItem.getText() : "");
		selectedItem = comboItem;
		
		userHasSelected = true;
	}
	
	private MouseDownHandler selectionHandler = new MouseDownHandler() {

		@Override
		@SuppressWarnings("unchecked")
		public void onMouseDown(MouseDownEvent event) {
			ComboBoxItem<T> comboItem = (ComboBoxItem<T>) event.getSource();
			selectItem(comboItem);
		}
	};
}
