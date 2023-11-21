package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.v2.GridPanel;
import bpm.gwt.commons.client.custom.v2.HeaderCheckboxCell;
import bpm.gwt.commons.client.custom.v2.IHeaderCheckboxManager;
import bpm.gwt.commons.client.custom.v2.ParameterizedCheckboxCell;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

public class UsersSelectionDialog extends AbstractDialogBox implements IHeaderCheckboxManager<User> {

	private static UsersSelectionDialogUiBinder uiBinder = GWT.create(UsersSelectionDialogUiBinder.class);

	interface UsersSelectionDialogUiBinder extends UiBinder<Widget, UsersSelectionDialog> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	GridPanel<User> grid;

	private HeaderCheckboxCell<User> header;
	private SingleSelectionModel<User> selectionModel;
	
	private List<User> selectedUsers;
	
	private boolean multipleSelection;
	private boolean confirm = false;

	public UsersSelectionDialog(List<User> users) {
		this(users, true);
	}
	
	public UsersSelectionDialog(List<User> users, boolean multipleSelection) {
		super(LabelsConstants.lblCnst.Users(), false, true);
		this.multipleSelection = multipleSelection;

		setWidget(uiBinder.createAndBindUi(this));
		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmationHandler, LabelsConstants.lblCnst.Cancel(), closeHandler);

		buildGrid(multipleSelection);
		loadUsers(users, true);
	}
	
	public void loadUsers(List<User> users, boolean refresh) {
		grid.loadItems(users);
		header.loadItems(grid.getItems());
	}
	
	private void buildGrid(boolean multipleSelection) {
		grid.setTopManually(0);
		
		Column<User, Boolean> columnSelection = new Column<User, Boolean>(new ParameterizedCheckboxCell()) {

			@Override
			public Boolean getValue(User object) {
				return isSelected(object);
			}
		};
		columnSelection.setFieldUpdater(new FieldUpdater<User, Boolean>() {
			@Override
			public void update(int index, User object, Boolean value) {
				selectUser(object, value);
			}
		});

		TextCell txtCell = new TextCell();
		Column<User, String> columnName = new Column<User, String>(txtCell) {

			@Override
			public String getValue(User object) {
				return object.getName();
			}
		};
		
		this.header = new HeaderCheckboxCell<User>(this, null);
		Header<Boolean> headerCheck = new Header<Boolean>(header) {

			@Override
			public Boolean getValue() {
				return true;
			}
		};

		if (multipleSelection) {
			grid.addColumn(headerCheck, columnSelection, "40px", null);
		}
		grid.addColumn(LabelsConstants.lblCnst.User(), columnName, null, null);
		
		selectionModel = new SingleSelectionModel<User>();
		grid.setSelectionModel(selectionModel);
		
		grid.setPageVisible(false);
	}
	
	private boolean isSelected(User user) {
		if (selectedUsers != null) {
			for (User selectedUser : selectedUsers) {
				if (selectedUser.getId() == user.getId()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void selectUser(User user, boolean select) {
		if (selectedUsers == null) {
			selectedUsers = new ArrayList<User>();
		}
		
		if (select) {
			this.selectedUsers.add(user);
		}
		else {
			selectedUsers.remove(user);
		}
		
		grid.refresh();
	}

	@Override
	public void update(User object, Boolean value) {
		selectUser(object, value);
	}
	
	@Override
	public void refreshGrid() {
		grid.refresh();
	}
	
	public List<User> getSelectedUsers() {
		return selectedUsers;
	}
	
	public boolean isConfirm() {
		return confirm;
	}
	
	public User getSelectedUser() {
		return selectionModel.getSelectedObject();
	}

	private ClickHandler confirmationHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (!multipleSelection && getSelectedUser() == null) {
				return;
			}
			
			if (multipleSelection && (selectedUsers == null || selectedUsers.isEmpty())) {
				return;
			}
			
			confirm = true;
			hide();
		}
	};

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};
}
