package bpm.gwt.commons.client.custom;

import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.user.client.ui.TextBox;

public class TextHolderBox extends TextBox {
	
	public TextHolderBox() {
		addStyleName(VanillaCSS.COMMONS_TEXTBOX);
	}

	public void setPlaceHolder(String placeHolder) {
		this.getElement().setAttribute("placeholder", placeHolder);
	}
	
	public void setPassword(String password) {
		if(password.equals("true")) {
			this.getElement().setAttribute("type", "password");
		}
	}
	
	public void setPassword(boolean password) {
		if(password) {
			this.getElement().setAttribute("type", "password");
		}
	}
}
