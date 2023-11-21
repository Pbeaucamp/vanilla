package bpm.freematrix.reborn.web.client.main.home.application.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DetailsApplicationsItem extends Composite {

	private static DetailsApplicationsItemUiBinder uiBinder = GWT
			.create(DetailsApplicationsItemUiBinder.class);

	interface DetailsApplicationsItemUiBinder extends
			UiBinder<Widget, DetailsApplicationsItem> {
	}

	@UiField Label lblNum, lblApplicationName, lblValue;
	@UiField HTMLPanel lblHealthUp, lblHealthDown, lblHealthEqual;
	
	public DetailsApplicationsItem(String num, String name, String value, int health) {
		initWidget(uiBinder.createAndBindUi(this));
		name = name.replace("[", "");
		name = name.replace("]", "");
		if(health < 0) {
			lblHealthDown.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			lblHealthUp.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthEqual.getElement().getStyle().setDisplay(Display.NONE);
		}
		else if(health == 0) {
			lblHealthDown.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthUp.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthEqual.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}
		else if(health > 0) {
			lblHealthDown.getElement().getStyle().setDisplay(Display.NONE);
			lblHealthUp.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			lblHealthEqual.getElement().getStyle().setDisplay(Display.NONE);
		}
		lblNum.setText(num + ".");
		lblApplicationName.setText(name);
		lblValue.setText(value);
	}


}
