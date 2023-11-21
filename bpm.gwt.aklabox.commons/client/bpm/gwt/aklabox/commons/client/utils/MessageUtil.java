package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MessageUtil extends Composite {

	private static MessageUtilUiBinder uiBinder = GWT.create(MessageUtilUiBinder.class);

	interface MessageUtilUiBinder extends UiBinder<Widget, MessageUtil> {
	}

	@UiField
	Label lblMessage;

	public MessageUtil(String message, boolean isSuccess) {
		initWidget(uiBinder.createAndBindUi(this));
		lblMessage.setText(message);
		if (isSuccess) {
			lblMessage.addStyleName("success");
		}
		else {
			lblMessage.addStyleName("error");
		}
	}

}
