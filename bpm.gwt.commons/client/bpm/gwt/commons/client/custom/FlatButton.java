package bpm.gwt.commons.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FlatButton extends Button {

	private static FlatButtonUiBinder uiBinder = GWT.create(FlatButtonUiBinder.class);

	interface FlatButtonUiBinder extends UiBinder<Widget, FlatButton> {
	}

	public FlatButton() {
//		initWidget(uiBinder.createAndBindUi(this));
	}

}
