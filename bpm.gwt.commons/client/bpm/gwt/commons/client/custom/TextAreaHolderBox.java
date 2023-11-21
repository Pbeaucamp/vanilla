package bpm.gwt.commons.client.custom;

import com.google.gwt.user.client.ui.TextArea;

public class TextAreaHolderBox extends TextArea {

	public void setPlaceHolder(String placeHolder) {
		this.getElement().setAttribute("placeholder", placeHolder);
	}
}
