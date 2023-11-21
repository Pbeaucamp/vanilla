package bpm.gwt.commons.client.custom;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ListBox<T> extends Composite implements HasChangeHandlers {

	private static ListBoxUiBinder uiBinder = GWT.create(ListBoxUiBinder.class);

	interface ListBoxUiBinder extends UiBinder<Widget, ListBox<?>> {
	}

	interface MyStyle extends CssResource {
		String item();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel input;
	
	@UiField
	Label text;

	@UiField
	HTMLPanel panelDrop;

	private List<ListBoxItem<T>> items;

	private ListBoxItem<T> selectedItem;

	private boolean userHasSelected;

	public ListBox() {
		initWidget(uiBinder.createAndBindUi(this));
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
			String txt = text.getText();
			if (txt.isEmpty()) {
				selectItem(null);
				return;
			}

			ListBoxItem<T> comboItem = foundItem(txt);

			if (comboItem != null) {
				selectItem(comboItem);
			}
			else if (selectedItem != null) {
				selectItem(selectedItem);
			}
			else {
				text.setText("");
			}
		}
	}

	private ListBoxItem<T> foundItem(String text) {
		if (items != null) {
			for (ListBoxItem<T> item : items) {
				if (item.getText().equals(text)) {
					return item;
				}
			}
		}
		return null;
	}

	public void addItem(String text, T item) {
		if (items == null) {
			this.items = new ArrayList<ListBoxItem<T>>();
		}
		boolean first = items.size() == 0;

		ListBoxItem<T> comboItem = new ListBoxItem<T>(text, item);
		comboItem.addMouseDownHandler(selectionHandler);
		items.add(comboItem);
		panelDrop.add(comboItem);
		
		if (first) {
			selectItem(comboItem);
		}
	}

	public T getSelectedItem() {
		return selectedItem != null ? selectedItem.getItem() : null;
	}

	@Override
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return addDomHandler(handler, ChangeEvent.getType());
	}

	private void selectItem(ListBoxItem<T> comboItem) {
		text.setText(comboItem != null ? comboItem.getText() : "");
		selectedItem = comboItem;

		userHasSelected = true;
		panelDrop.setVisible(false);
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
	}

	public void clear() {
		if (items != null)
			items.clear();
		if (panelDrop != null)
			panelDrop.clear();
	}

	private MouseDownHandler selectionHandler = new MouseDownHandler() {

		@Override
		@SuppressWarnings("unchecked")
		public void onMouseDown(MouseDownEvent event) {
			ListBoxItem<T> comboItem = (ListBoxItem<T>) event.getSource();
			selectItem(comboItem);
		}
	};
}
