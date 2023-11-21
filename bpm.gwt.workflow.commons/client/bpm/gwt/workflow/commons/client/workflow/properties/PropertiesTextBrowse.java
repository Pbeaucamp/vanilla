package bpm.gwt.workflow.commons.client.workflow.properties;

import bpm.gwt.commons.client.custom.TextHolderBox;

import com.google.gwt.user.client.ui.Button;

public class PropertiesTextBrowse {

	private TextHolderBox txtValue;
	private Button btn;

	public PropertiesTextBrowse(TextHolderBox txtValue, Button btn) {
		this.txtValue = txtValue;
		this.btn = btn;
	}

	public void setText(String value) {
		txtValue.setText(value);
	}

	public void setEnabled(boolean enabled) {
		txtValue.setEnabled(enabled);
		if (btn != null) {
			btn.setEnabled(enabled);
		}
	}

	public void setVisible(boolean visible) {
		txtValue.setVisible(visible);
		if (btn != null) {
			btn.setVisible(visible);
		}
	}

	public void setPlaceHolder(String placeHolder) {
		txtValue.setPlaceHolder(placeHolder);
	}

	public String getText() {
		return txtValue.getText();
	}

	public int getTextAsInteger() {
		try {
			return Integer.parseInt(txtValue.getText());
		} catch (Exception e) {
			return 0;
		}
	}
}