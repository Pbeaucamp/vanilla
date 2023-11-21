package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.user.client.ui.TextArea;

public class AklaTextArea extends TextArea {

	private String placeholder = "";

	public AklaTextArea() {
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
		this.getElement().setAttribute("placeholder", placeholder);
	}

}
