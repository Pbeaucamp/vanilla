package bpm.gwt.aklabox.commons.client.customs;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.touch.client.Point;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TextAreaAutoComplete<T> extends Composite {

	public static String ua = GWT.isClient() ? Window.Navigator.getUserAgent().toLowerCase() : "JVM";
	public static final double DEFAULT_TOP = 25;
	public static final double DEFAULT_LEFT = 25;

	private static TextAreaAutoCompleteUiBinder uiBinder = GWT.create(TextAreaAutoCompleteUiBinder.class);

	interface TextAreaAutoCompleteUiBinder extends UiBinder<Widget, TextAreaAutoComplete<?>> {
	}

	interface MyStyle extends CssResource {
		String item();
		String panelDrop();
	}

	@UiField
	MyStyle style;

	@UiField(provided=true)
	LabelRichTextArea input;
	
	@UiField
	HTMLPanel panelDrop;
	
	private IAutoCompleteManager<T> manager;
	private String selectedChar;

	private List<AutoCompleteItem<T>> selectedItems;
	
	private boolean isIE11;

	public TextAreaAutoComplete(IAutoCompleteManager<T> manager, String selectedChar, boolean isIE11) {
		input = new LabelRichTextArea(false);
		initWidget(uiBinder.createAndBindUi(this));
		this.manager = manager;
		this.selectedChar = selectedChar;
		this.isIE11 = isIE11;
		
//		input.setLineHeight(DEFAULT_LINE_HEIGHT);
	}

	public void setPlaceHolder(String placeHolder) {
		input.setPlaceHolder(placeHolder);
	}

	@UiHandler("input")
	public void onKeyUpInput(KeyUpEvent event) {
		if (!isIE11) {
			String filter = getFilter();
			if (filter != null) {
				manager.loadItems(this, filter);
			}
			else {
				panelDrop.setVisible(false);
			}
		}
	}
	
	private String getFilter() {
		String myText = input.getText().substring(0, input.getCursorTextArea());
		if (myText.endsWith(" ")) {
			return null;
		}
		
		String[] words = myText.split("\\s|\n");
		String lastWord = words[words.length - 1];
		
		if (lastWord.startsWith(selectedChar) && lastWord.length() > 2) {
			return lastWord.substring(1, lastWord.length());
		}
		
		return null;
	}
	
	@UiHandler("input")
	public void onInputFocusLost(BlurEvent event) {
		panelDrop.setVisible(false);
	}

	public void loadItems(List<AutoCompleteItem<T>> filteredItems) {
		if (filteredItems != null && !filteredItems.isEmpty()) {
			panelDrop.clear();
			
			for (AutoCompleteItem<T> item : filteredItems) {
				item.addMouseDownHandler(selectionHandler);
				panelDrop.add(item);
			}
			
			setPosition(panelDrop);
			panelDrop.setVisible(true);
		}
		else {
			panelDrop.setVisible(false);
		}
	}

	private void setPosition(HTMLPanel panelDrop) {
		Point coordinate;
		try {
			coordinate = input.getCursorTopAndLeft();
		} catch (Exception e) {
			coordinate = new Point();
		}
		
		panelDrop.getElement().getStyle().setTop(coordinate.getY() + DEFAULT_TOP, Unit.PX);
		panelDrop.getElement().getStyle().setLeft(coordinate.getX() + DEFAULT_LEFT, Unit.PX);
	}

	public List<T> getSelectedItems() {
		String text = input.getText();
		
		List<T> items = new ArrayList<T>();
		List<AutoCompleteItem<T>> removes = new ArrayList<AutoCompleteItem<T>>();
		if (selectedItems != null) {
			for (AutoCompleteItem<T> item : selectedItems) {
				if (check(text, item)) {
					items.add(item.getItem());
				}
				else {
					removes.add(item);
				}
			}
		}
		return items;
	}
	
	private boolean check(String text, AutoCompleteItem<T> item) {
		return text.contains("@" + item.getText());
	}

	public void selectItem(AutoCompleteItem<T> comboItem) {
		if (selectedItems == null) {
			selectedItems = new ArrayList<AutoCompleteItem<T>>();
		}
		selectedItems.add(comboItem);

		if (!isIE11) {
			input.insertHTML(comboItem.getText().substring(comboItem.getPreWord().length())); //+1 because special character
		}
	}
	
	public String getText() {
		return input.getText();
	}

	public void setText(String text) {
		setText(text, true);
	}

	public void setText(String text, boolean initItems) {
		if (initItems) {
			this.selectedItems = null;
		}
		input.setText(text);
	}
	
	private MouseDownHandler selectionHandler = new MouseDownHandler() {

		@Override
		@SuppressWarnings("unchecked")
		public void onMouseDown(MouseDownEvent event) {
			AutoCompleteItem<T> comboItem = (AutoCompleteItem<T>) event.getSource();
			selectItem(comboItem);
		}
	};
	
	public interface IAutoCompleteManager<T> {
		
		public void loadItems(TextAreaAutoComplete<T> source, String filter);
	}
	
	public LabelRichTextArea getInput(){
		return input;
	}
}
