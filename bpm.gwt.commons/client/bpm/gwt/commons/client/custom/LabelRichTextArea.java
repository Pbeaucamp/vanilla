package bpm.gwt.commons.client.custom;

import bpm.gwt.commons.client.utils.text.RichTextToolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LabelRichTextArea extends Composite {

	private static LabelTextAreaUiBinder uiBinder = GWT.create(LabelTextAreaUiBinder.class);

	interface LabelTextAreaUiBinder extends UiBinder<Widget, LabelRichTextArea> {
	}
	
	interface MyStyle extends CssResource {
		String resize();
		String toolbar();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Label label;
	
	@UiField
	SimplePanel panelToolbar;
	
	@UiField
	RichTextArea textarea;

	public LabelRichTextArea() {
		initWidget(uiBinder.createAndBindUi(this));
		textarea.getElement().setAttribute("spellcheck", "false");
		
		RichTextToolbar toolbar = new RichTextToolbar(textarea);
		toolbar.addStyleName(style.toolbar());
		panelToolbar.setWidget(toolbar);
	}

	public void setText(String text) {
		textarea.setHTML(text);
	}
	
	public String getText() {
		return textarea.getHTML();
	}
	
	public void clear() {
		textarea.setText("");
	}
	
	public void setEnabled(boolean enabled) {
		textarea.setEnabled(enabled);
	}
	
	public void setPlaceHolder(String placeHolder) {
		label.setText(placeHolder);
	}
	
	public void setResize(boolean resize) {
		if (!resize) {
			textarea.addStyleName(style.resize());
		}
	}
	
	public void setHeight(String height) {
		textarea.setHeight(height);
	}
}
