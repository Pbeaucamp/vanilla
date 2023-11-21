package bpm.gwt.workflow.commons.client.workflow.properties;

import bpm.gwt.commons.client.custom.TextHolderBox;
import bpm.gwt.workflow.commons.client.utils.ValueChangeHandlerWithError;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class PropertiesText {

	protected TextHolderBox txtValue;
	private Label txtError;
	private Image imgValid;
	
	private String styleWarning, styleError;

	public PropertiesText(TextHolderBox txtValue, Label txtError, Image imgValid, String styleWarning, String styleError) {
		this.txtValue = txtValue;
		this.txtError = txtError;
		this.imgValid = imgValid;
		
		this.styleWarning = styleWarning;
		this.styleError = styleError;
	}

	public void setText(String value) {
		txtValue.setText(value);
	}

	public void setTxtError(String error) {
		txtError.setText(error);

		txtError.removeStyleName(styleWarning);
		txtError.addStyleName(styleError);
	}

	public void setTxtWarning(String error) {
		txtError.setText(error);

		txtError.addStyleName(styleWarning);
		txtError.removeStyleName(styleError);
	}

	public void addValueChangeHandler(PropertiesText txt, ValueChangeHandlerWithError valueChangeHandler) {
		valueChangeHandler.setTxt(txt);
		txtValue.addValueChangeHandler(valueChangeHandler);
	}

	public void setEnabled(boolean enabled) {
		txtValue.setEnabled(enabled);
		txtError.setVisible(enabled);
		if (imgValid != null) {
			imgValid.setVisible(enabled);
		}
	}

	public void setVisible(boolean visible) {
		txtValue.setVisible(visible);
		txtError.setVisible(visible);
		if (imgValid != null) {
			imgValid.setVisible(visible);
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