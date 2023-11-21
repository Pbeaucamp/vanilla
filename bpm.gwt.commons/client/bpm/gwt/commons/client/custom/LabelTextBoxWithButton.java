package bpm.gwt.commons.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LabelTextBoxWithButton extends Composite {

	private static LabelTextBoxWithButtonUiBinder uiBinder = GWT.create(LabelTextBoxWithButtonUiBinder.class);

	interface LabelTextBoxWithButtonUiBinder extends UiBinder<Widget, LabelTextBoxWithButton> {
	}

	@UiField
	LabelTextBox lbl;

	@UiField
	CustomButton btn;
	
	private ButtonClickHandler clickHandler;

	public LabelTextBoxWithButton() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setLabel(String label) {
		lbl.setLabel(label);
	}
	
	public void setButtonLabel(String buttonLabel) {
		btn.setText(buttonLabel);
	}
	
	public void setText(String text) {
		lbl.setText(text);
	}
	
	public void setButtonClickHandler(ButtonClickHandler clickHandler) {
		this.clickHandler = clickHandler;
	}
	
	public void setEnabled(boolean enabled) {
		this.lbl.setEnabled(enabled);
	}

	@UiHandler("btn")
	public void onBtn(ClickEvent event) {
		if (clickHandler != null) {
			clickHandler.onBtnClick();
		}
	}

	public interface ButtonClickHandler {
		
		public void onBtnClick();
	}
}
