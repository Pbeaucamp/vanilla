package bpm.gwt.commons.client.custom;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.user.client.ui.ListBox;

public class CustomListBox extends ListBox {
	
	public CustomListBox() {
		super();
		addStyleName(VanillaCSS.COMMONS_LISTBOX);
	}
	
	public String getSelectedValue() {
		return getValue(getSelectedIndex());
	}
}
