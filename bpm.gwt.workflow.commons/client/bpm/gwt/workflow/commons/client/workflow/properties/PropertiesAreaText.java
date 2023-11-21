package bpm.gwt.workflow.commons.client.workflow.properties;

import bpm.gwt.commons.client.custom.TextAreaHolderBox;
import bpm.gwt.workflow.commons.client.utils.ValueChangeHandlerWithError;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class PropertiesAreaText {

	protected TextAreaHolderBox txtValue;
	private Label txtError;
	private Image imgValid;
	
	private String styleSuccess, styleError;

	public PropertiesAreaText(TextAreaHolderBox txtValue, Label txtError, Image imgValid, String styleSuccess, String styleError) {
		this.txtValue = txtValue;
		this.txtError = txtError;
		this.imgValid = imgValid;

		this.styleSuccess = styleSuccess;
		this.styleError = styleError;
	}

	public void setTxtResult(String result, boolean isError) {
		txtError.setText(result);
		if (isError) {
			txtError.removeStyleName(styleSuccess);
			txtError.addStyleName(styleError);
		}
		else {
			txtError.addStyleName(styleSuccess);
			txtError.removeStyleName(styleError);
		}
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