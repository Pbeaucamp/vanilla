package bpm.vanilla.workplace.client.admin.panel;

import bpm.vanilla.workplace.client.services.AdminService;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ConnectionPanel extends Composite {

	private static ConnectionPanelUiBinder uiBinder = GWT
			.create(ConnectionPanelUiBinder.class);

	interface ConnectionPanelUiBinder extends UiBinder<Widget, ConnectionPanel> {
	}

	@UiField
	TextBox txtUser;
	
	@UiField
	PasswordTextBox txtPassword;
	
	@UiField
	Button btnConnect;

	public ConnectionPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("btnConnect")
	void onClick(ClickEvent e) {
		
		String username = txtUser.getText();
		String password = txtPassword.getText();
		
		AdminService.Connect.getInstance().authentifyUser(username, password, new AsyncCallback<PlaceWebUser>() {
			
			@Override
			public void onSuccess(PlaceWebUser result) {
				if(result != null && result.getIsAdmin()){
//					PanelAdmin admin = new PanelAdmin(result);
					MainPanel mainPanel = new MainPanel(result);
					
					RootPanel.get("root").clear();
					RootPanel.get("root").getElement().getStyle().setBackgroundColor("#CCCCCC");
					RootPanel.get("root").add(mainPanel);
				}
				else {
					//TODO: Errors
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

}
