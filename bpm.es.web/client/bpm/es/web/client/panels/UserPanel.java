package bpm.es.web.client.panels;

import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class UserPanel extends Composite {

	private static UserPanelUiBinder uiBinder = GWT.create(UserPanelUiBinder.class);

	interface UserPanelUiBinder extends UiBinder<Widget, UserPanel> {
	}

	public UserPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void loadUser(User user) {
		if (user != null) {
			
		}
	}

	public User getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConfirm() {
		return true;
	}
}
