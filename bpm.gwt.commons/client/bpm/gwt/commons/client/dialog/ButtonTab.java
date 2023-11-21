package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ButtonTab extends FocusPanel {

	private static ButtonTabUiBinder uiBinder = GWT.create(ButtonTabUiBinder.class);

	interface ButtonTabUiBinder extends UiBinder<Widget, ButtonTab> {
	}
	
	interface MyStyle extends CssResource {
		String selected();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel button;
	
	@UiField
	Label label;
	
	public ButtonTab(String name) {
		setWidget(uiBinder.createAndBindUi(this));
		
		label.setText(name);
	}

	public void select(boolean select) {
		if(select) {
			button.addStyleName(style.selected());
		}
		else {
			button.removeStyleName(style.selected());
		}
	}
}
