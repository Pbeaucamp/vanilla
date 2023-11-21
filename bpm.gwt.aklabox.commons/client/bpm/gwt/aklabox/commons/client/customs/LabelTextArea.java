package bpm.gwt.aklabox.commons.client.customs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LabelTextArea extends Composite {

	private static LabelTextAreaUiBinder uiBinder = GWT.create(LabelTextAreaUiBinder.class);

	interface LabelTextAreaUiBinder extends UiBinder<Widget, LabelTextArea> {
	}

	interface MyStyle extends CssResource {
		String resize();
	}

	@UiField
	MyStyle style;

	@UiField
	Label label;

	@UiField
	TextAreaHolderBox textarea;

	public LabelTextArea() {
		initWidget(uiBinder.createAndBindUi(this));
		textarea.getElement().setAttribute("spellcheck", "false");
	}

	public void setText(String text) {
		textarea.setText(text);
	}

	public String getText() {
		return textarea.getText();
	}

	public void clear() {
		textarea.setText("");
	}

	public void setEnabled(boolean enabled) {
		textarea.setEnabled(enabled);
	}

	public void setPlaceHolder(String placeHolder) {
		label.setText(placeHolder);
		textarea.setPlaceHolder(placeHolder);
	}

	public void setResize(boolean resize) {
		if (!resize) {
			textarea.addStyleName(style.resize());
		}
	}

	public void setHeight(String height) {
		textarea.setHeight(height);
	}

	public void setWidth(String width) {
		textarea.setWidth(width);
	}

	public void setLineHeight(double value) {
		textarea.getElement().getStyle().setLineHeight(value, Unit.PX);
	}

	public int getCursorPosition() {
		return textarea.getCursorPos();
	}

	public int getVisibleLines() {
		return textarea.getVisibleLines();
	}

	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return textarea.addKeyUpHandler(handler);
	}

	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return textarea.addDomHandler(handler, BlurEvent.getType());
	}
}
