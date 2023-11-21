package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.user.client.ui.TextArea;

public class TextAreaHolderBox extends TextArea {

	public void setPlaceHolder(String placeHolder) {
		this.getElement().setAttribute("placeholder", placeHolder);
	}
}
