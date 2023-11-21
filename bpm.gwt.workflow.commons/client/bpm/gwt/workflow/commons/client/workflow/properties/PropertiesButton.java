package bpm.gwt.workflow.commons.client.workflow.properties;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class PropertiesButton {

	protected Button button;
	private Label txtError;
	
	private String styleSuccess, styleError;

	public PropertiesButton(Button button, Label txtError, String styleSuccess, String styleError) {
		this.button = button;
		this.txtError = txtError;
		
		this.styleSuccess = styleSuccess;
		this.styleError = styleError;
	}

	public void setTxtError(String error) {
		txtError.setText(error);

		txtError.removeStyleName(styleSuccess);
		txtError.addStyleName(styleError);
	}

	public void setTxtResult(String success) {
		txtError.setText(success);

		txtError.addStyleName(styleSuccess);
		txtError.removeStyleName(styleError);
	}

	public void setVisible(boolean visible) {
		button.setVisible(visible);
		txtError.setVisible(visible);
	}
}