package bpm.gwt.aklabox.commons.client.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MessageDialog extends ChildDialogComposite{

	private static MessageDialogUiBinder uiBinder = GWT
			.create(MessageDialogUiBinder.class);
	
	@UiField
	Button btnOk, btnCancel;
	
	@UiField
	Label lblMessage;
	
	interface MyStyle extends CssResource {
		String rightAlign();
	}
	
	@UiField
	MyStyle style;
	
	Boolean confirm=false;

	interface MessageDialogUiBinder extends UiBinder<Widget, MessageDialog> {
	}

	public MessageDialog(String message, Boolean cancelRequisite) {
		initWidget(uiBinder.createAndBindUi(this));
		lblMessage.setText(message);
		if(!cancelRequisite){
			btnCancel.setVisible(false);
			btnOk.addStyleName(style.rightAlign());
		}
	}
	
	
	@UiHandler("btnOk")
	void onbtnOk(ClickEvent e){	
		confirm=true;
		closeParent();
	}
	
	@UiHandler("btnCancel")
	void onbtnCancel(ClickEvent e){	
		closeParent();
	}
	
	public Boolean isConfirm(){
		return confirm;
	}

}
