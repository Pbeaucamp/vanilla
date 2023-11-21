package bpm.gwt.commons.client.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.LabelTextBox;
import bpm.vanilla.platform.core.beans.resources.D4C;

public class AddD4CDialog extends AbstractDialogBox {

	private static AddD4CDialogUiBinder uiBinder = GWT.create(AddD4CDialogUiBinder.class);

	interface AddD4CDialogUiBinder extends UiBinder<Widget, AddD4CDialog> {
	}
	
	interface MyStyle extends CssResource {
		String maxHeight();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	LabelTextBox txtName, txtUrl, txtLogin, txtPassword;
	
	private D4C item;

	private boolean isConfirm = false;

	public AddD4CDialog(boolean full) {
		this(null, full);
	}

	public AddD4CDialog(D4C item, boolean full) {
		super(LabelsConstants.lblCnst.D4CServer(), false, true);
		this.item = item;
		
		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
		
		if (item != null) {
			txtName.setText(item.getName());
			txtUrl.setText(item.getUrl());
			txtLogin.setText(item.getLogin());
			txtPassword.setText(item.getPassword());
		}
		
		txtLogin.setVisible(full);
		txtPassword.setVisible(full);
	}
	
	public boolean isConfirm() {
		return isConfirm;
	}

	public D4C getItem() {
		String name = txtName.getText();
		String url = txtUrl.getText();
		String login = txtLogin.getText();
		String password = txtPassword.getText();
		
		if (item == null) {
			item = new D4C();
		}
		item.setName(name);
		item.setUrl(url);
		item.setLogin(login != null && !login.isEmpty() ? login : null);
		item.setPassword(password != null && !password.isEmpty() ? password : null);

		return item;
	}

	private boolean isComplete() {
		return !txtName.getText().isEmpty() && !txtUrl.getText().isEmpty();
	}

	private ClickHandler okHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if (isComplete()) {
				isConfirm = true;
				
				hide();
			}
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

}
