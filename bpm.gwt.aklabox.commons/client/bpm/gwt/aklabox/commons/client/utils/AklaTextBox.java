package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.user.client.ui.TextBox;

public class AklaTextBox extends TextBox {

	private String placeholder = "";

	public AklaTextBox() {
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		this.getElement().setAttribute("placeholder", placeholder);
	}

}
