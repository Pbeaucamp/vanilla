package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.user.client.ui.ListBox;

public class CustomListBox extends ListBox {
	
	public CustomListBox() {
		super();
		addStyleName("commons-listbox");
	}
	
	public String getSelectedValue() {
		return getValue(getSelectedIndex());
	}
}
