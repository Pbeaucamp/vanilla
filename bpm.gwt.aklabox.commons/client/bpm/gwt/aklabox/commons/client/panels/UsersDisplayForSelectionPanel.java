package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.dialogs.UsersSelectionWithGroupDialog;
import bpm.gwt.aklabox.commons.client.panels.UsersSelectionPanel.IUserManager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class UsersDisplayForSelectionPanel extends Composite implements IUserManager {

	private static UsersDisplayForSelectionPanelUiBinder uiBinder = GWT.create(UsersDisplayForSelectionPanelUiBinder.class);

	interface UsersDisplayForSelectionPanelUiBinder extends UiBinder<Widget, UsersDisplayForSelectionPanel> {
	}

	@UiField
	SimplePanel panelGridUsers;

	private UsersSelectionPanel usersSelectionPanel;
	private UsersSelectionWithGroupDialog dial;
	
	private List<User> users;
	
	private List<UserSelectionChangeHandler> handlers;

	public UsersDisplayForSelectionPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		this.usersSelectionPanel = new UsersSelectionPanel(this, true, false);
		panelGridUsers.setWidget(usersSelectionPanel);
	}

	public void loadUsers(List<User> users) {
		this.users = users;
		if (users == null) {
			this.users = new ArrayList<>();
		}

		usersSelectionPanel.loadUsers(this.users, null, null);
		
		onUpdateSelection();
	}

	@UiHandler("btnAddUsers")
	public void onAddUsers(ClickEvent event) {
		if (dial == null) {
			dial = new UsersSelectionWithGroupDialog(null);
		}
		else {
			dial.clearSelection();
		}
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					List<User> selectedUsers = dial.getSelectedUsers();
					addUsers(selectedUsers);
				}
			}
		});
	}

	private void addUsers(List<User> selectedUsers) {
		List<User> usersToAdd = new ArrayList<>();
		if (users != null && selectedUsers != null) {
			for (User user : selectedUsers) {
				boolean found = false;
				for (User tmp : users) {
					if (user.getUserId() == tmp.getUserId()) {
						found = true;
						break;
					}
				}

				if (!found) {
					usersToAdd.add(user);
				}
			}
		}
		else {
			usersToAdd.addAll(selectedUsers != null ? selectedUsers : new ArrayList<User>());
		}
		
		if (users == null) {
			this.users = new ArrayList<>();
		}
		for (User user : usersToAdd) {
			users.add(user);
		}
		loadUsers(users);
	}

	public void removeUser(User object) {
		users.remove(object);
		loadUsers(users);
	}
	
	private void onUpdateSelection() {
		if (handlers != null) {
			for (UserSelectionChangeHandler handler : handlers) {
				handler.onUpdateSelection();
			}
		}
	}
	
	public List<User> getSelectedUsers() {
		return users;
	}
	
	public void addUserSelectionChangeHandler(UserSelectionChangeHandler handler) {
		if (handlers == null) {
			this.handlers = new ArrayList<>();
		}
		this.handlers.add(handler);
	}
	
	public interface UserSelectionChangeHandler {

		void onUpdateSelection();
	}
}
