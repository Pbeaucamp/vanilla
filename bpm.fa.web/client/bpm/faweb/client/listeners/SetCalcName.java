package bpm.faweb.client.listeners;

import bpm.faweb.shared.infoscube.Calcul;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class SetCalcName implements ValueChangeHandler<String> {//implements KeyboardListener {
	private TextBox box;
	private Calcul calcul;
	
	public SetCalcName(TextBox box, Calcul c) {
		this.box = box;
		this.calcul = c;
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String n = SetCalcName.this.box.getText();
		calcul.setTitle(n);
	}

}
