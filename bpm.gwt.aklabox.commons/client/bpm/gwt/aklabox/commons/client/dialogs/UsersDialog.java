package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.CustomDatagrid;
import bpm.gwt.aklabox.commons.client.customs.LabelTextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

public class UsersDialog extends AbstractDialogBox {

	private static UsersDialogUiBinder uiBinder = GWT.create(UsersDialogUiBinder.class);

	interface UsersDialogUiBinder extends UiBinder<Widget, UsersDialog> {
	}
	
	@UiField
	SimplePanel panelContent;
	
	@UiField
	LabelTextBox txtFilter;
	
	private MultiSelectionModel<User> selectionModel;
	
	private boolean confirm = false;

	private List<User> users;

	private String header;

	public UsersDialog(String title, String header, List<User> users) {
		super(title, false, true);
		
		this.users = users;
		this.header = header;
		
		setWidget(uiBinder.createAndBindUi(this));
		
		selectionModel = new MultiSelectionModel<>();
		CustomDatagrid<User> gridUsers = new CustomDatagrid<>(users, selectionModel, 250, LabelsConstants.lblCnst.NoUsers(), header);
		panelContent.setWidget(gridUsers);
		
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}
	
	public List<User> getSelectedUsers() {
		List<User> users = new ArrayList<>();
		users.addAll(selectionModel.getSelectedSet());
		return users;
	}
	
	public boolean isConfirm() {
		return confirm;
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			confirm = false;
			hide();
		}
	};

	private List<User> filterUsers;
	
	@UiHandler("btnFilter")
	public void onFilter(ClickEvent event) {
		String filter = txtFilter.getText();
		
		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getEmail().compareToIgnoreCase(o2.getEmail());
			}
		});
		
		List<User> filterUsers = new ArrayList<>();
		if (users != null) {
			for (User user : users) {
				if (user.getEmail().contains(filter) || user.getLastName().contains(filter) || user.getFirstName().contains(filter)) {
					filterUsers.add(user);
				}
			}
		}
		
		loadUsers(users, filterUsers, null);
	}
	
	public void loadUsers(List<User> users, List<User> filterUsers, List<User> selectedUsers) {
		this.users = users;
		if (users == null) {
			this.users = new ArrayList<>();
		}
		
		if (filterUsers == null) {
			this.filterUsers = this.users;
		}
		else {
			this.filterUsers = filterUsers;
		}
		CustomDatagrid<User> gridUsers = new CustomDatagrid<>(filterUsers, selectionModel, 250, LabelsConstants.lblCnst.NoUsers(), header);
		panelContent.setWidget(gridUsers);
		
		if (selectedUsers != null) {
			selectionModel.clear();
			if (selectedUsers != null) {
				for (User selectedUser : selectedUsers) {
					for (User user : users) {
						if (selectedUser.getUserId() == user.getUserId()) {
							selectionModel.setSelected(user, true);
							break;
						}
					}
				}
			}
		}
		
	}

	@Override
	public int getThemeColor() {
		return 0;
	}
}
