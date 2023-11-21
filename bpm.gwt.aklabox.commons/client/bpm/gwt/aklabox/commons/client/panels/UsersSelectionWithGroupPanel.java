package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.Enterprise.TypeUser;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.CustomCheckboxCell;
import bpm.gwt.aklabox.commons.client.customs.LabelTextBox;
import bpm.gwt.aklabox.commons.client.dialogs.GroupsDialog;
import bpm.gwt.aklabox.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.ResizableImageCell;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class UsersSelectionWithGroupPanel extends CompositeWaitPanel {

	private static UsersSelectionPanelUiBinder uiBinder = GWT.create(UsersSelectionPanelUiBinder.class);

	interface UsersSelectionPanelUiBinder extends UiBinder<Widget, UsersSelectionWithGroupPanel> {
	}

	interface MyStyle extends CssResource {
		String grid();

		String grp();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel toolbar, panelFilter, panelGroups;

	@UiField
	LabelTextBox txtFilter;

	@UiField
	Button btnLoadEnterpriseUsers, btnFilter, btnSelectGroups;

	@UiField
	SimplePanel panelGrid;

	private Enterprise enterprise;

	private ListDataProvider<User> dataProvider;
	private ListHandler<User> sortHandler;
	private MultiSelectionModel<User> selectionModel = new MultiSelectionModel<>();

	private List<Group> groups;
	private List<Group> selectedGroups;

	private List<User> users;
	private List<User> filterUsers;

	public UsersSelectionWithGroupPanel(Enterprise enterprise, boolean displayFilter, Widget... widgets) {
		initWidget(uiBinder.createAndBindUi(this));
		this.enterprise = enterprise;

		if (displayFilter) {
			txtFilter.displayClear(clearHandler);
		}
		else {
			panelFilter.setVisible(false);
		}

		panelGrid.setWidget(buildGrid(selectionModel));

		if (widgets != null) {
			for (Widget widget : widgets) {
				toolbar.add(widget);
			}
		}

		if (enterprise != null) {
			btnLoadEnterpriseUsers.setVisible(true);
		}

		loadGroups();
	}

	public void clearSelection() {
		selectionModel.clear();
	}

	private void loadGroups() {
		AklaCommonService.Connect.getService().getGroups(new GwtCallbackWrapper<List<Group>>(this, true, true) {

			@Override
			public void onSuccess(List<Group> result) {
				UsersSelectionWithGroupPanel.this.groups = result;
			}
		}.getAsyncCallback());
	}

	private void loadUsersForGroups(List<Group> groups) {
		if (groups != null) {
			panelGroups.clear();

			Collections.sort(groups, new Comparator<Group>() {
				@Override
				public int compare(Group o1, Group o2) {
					return o1.getGroupName().compareTo(o2.getGroupName());
				}
			});

			for (Group group : groups) {
				Label lblGroup = new Label(group.getGroupName());
				lblGroup.setStyleName(style.grp());
				panelGroups.add(lblGroup);
			}

			AklaCommonService.Connect.getService().getUsersByGroup(groups, enterprise != null ? enterprise.getEnterpriseId() : null, new GwtCallbackWrapper<List<User>>(this, true, true) {

				@Override
				public void onSuccess(List<User> result) {
					UsersSelectionWithGroupPanel.this.users = result;
					loadUsers(result, null, null);
				}
			}.getAsyncCallback());
		}
	}

	private ClickHandler clearHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			loadUsers(users, null, null);
		}
	};

	@UiHandler("btnLoadEnterpriseUsers")
	public void onLoadEnterpriseUsers(ClickEvent event) {
		AklaCommonService.Connect.getService().getUserEnterprise(enterprise, TypeUser.USER, new GwtCallbackWrapper<List<User>>(this, true, true) {

			@Override
			public void onSuccess(List<User> result) {
				UsersSelectionWithGroupPanel.this.users = result;
				loadUsers(users, null, null);
			}
		}.getAsyncCallback());
	}

	@UiHandler("btnSelectGroups")
	public void onSelectGroup(ClickEvent event) {
		final GroupsDialog dial = new GroupsDialog(LabelsConstants.lblCnst.Groups(), LabelsConstants.lblCnst.Groups(), groups, selectedGroups);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					UsersSelectionWithGroupPanel.this.selectedGroups = dial.getSelectedGroups();
					loadUsersForGroups(selectedGroups);
				}
			}
		});
	}

	@UiHandler("btnFilter")
	public void onFilter(ClickEvent event) {
		String filter = txtFilter.getText().toLowerCase();

		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getEmail().compareToIgnoreCase(o2.getEmail());
			}
		});

		List<User> filterUsers = new ArrayList<>();
		if (users != null) {
			for (User user : users) {
				boolean contains = (user.getFirstName() != null && !user.getFirstName().isEmpty() && user.getFirstName().toLowerCase().contains(filter)) 
						|| (user.getLastName() != null && !user.getLastName().isEmpty() && user.getLastName().toLowerCase().contains(filter)) 
						|| (user.getEmail() != null && !user.getEmail().isEmpty() && user.getEmail().split("@")[0].toLowerCase().contains(filter));
//				if (user.getEmail().contains(filter) || user.getLastName().contains(filter) || user.getFirstName().contains(filter)) {
				if(contains){
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
		
		Collections.sort(users, new Comparator<User>() {
			@Override
			public int compare(User o1, User o2) {
				return o1.getEmail().compareTo(o2.getEmail());
			}
		});

		if (filterUsers == null) {
			this.filterUsers = this.users;
		}
		else {
			this.filterUsers = filterUsers;
		}
		panelGrid.setWidget(buildGrid(selectionModel));
		dataProvider.setList(this.filterUsers);
		sortHandler.setList(dataProvider.getList());

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

	private DataGrid<User> buildGrid(final MultiSelectionModel<User> selectionModel) {
		this.sortHandler = new ListHandler<User>(new ArrayList<User>());

		Column<User, Boolean> checkColumn = new Column<User, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(User object) {
				return selectionModel.isSelected(object);
			}
		};

		Column<User, String> colImage = new Column<User, String>(new ResizableImageCell("20px", "30px")) {
			@Override
			public String getValue(User object) {
				return PathHelper.getRightPath(object.getProfilePic());
			}
		};

		Column<User, String> colEmail = new Column<User, String>(new TextCell()) {
			@Override
			public String getValue(User object) {
				return object.getEmail();
			}
		};
		colEmail.setSortable(true);
		sortHandler.setComparator(colEmail, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				if (o1.getEmail() != null && o2.getEmail() != null) {
					return o1.getEmail().compareTo(o2.getEmail());
				}
				return 0;
			}
		});

		Column<User, String> colName = new Column<User, String>(new TextCell()) {
			@Override
			public String getValue(User object) {
				return object.getFirstName() + " " + object.getLastName();
			}
		};
		colName.setSortable(true);
		sortHandler.setComparator(colName, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				String o1Name = o1.getFirstName() + " " + o1.getLastName();
				String o2Name = o2.getFirstName() + " " + o2.getLastName();
				return o1Name.compareTo(o2Name);
			}
		});

		Header<Boolean> headerCheck = new Header<Boolean>(new CustomCheckboxCell<User>(this.filterUsers, this.selectionModel)) {

			@Override
			public Boolean getValue() {

				return false;
			}

		};

		DataGrid<User> datagrid = new DataGrid<>(99999);
		datagrid.addColumn(checkColumn, headerCheck);
		// datagrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		datagrid.setColumnWidth(checkColumn, 40, Unit.PX);
		datagrid.addColumn(colImage, "");
		datagrid.setColumnWidth(colImage, "10%");
		datagrid.addColumn(colName, LabelsConstants.lblCnst.Name());
		datagrid.setColumnWidth(colName, "30%");
		datagrid.addColumn(colEmail, LabelsConstants.lblCnst.Email());
		datagrid.setColumnWidth(colEmail, "40%");

		datagrid.setAutoHeaderRefreshDisabled(true);
		datagrid.setAutoFooterRefreshDisabled(true);
		datagrid.setSize("98%", "100%");
		datagrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<User> createCheckboxManager());
		datagrid.addStyleName(style.grid());

		// datagrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<T> createCheckboxManager());

		dataProvider = new ListDataProvider<User>();
		dataProvider.addDataDisplay(datagrid);
		datagrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		return datagrid;
	}

	public List<User> getSelectedUsers() {
		List<User> selectedUsers = new ArrayList<>();
		selectedUsers.addAll(selectionModel.getSelectedSet());
		return selectedUsers;
	}

	public void addSelectionChangeHandler(Handler selectionChangeHandler) {
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
	}
}
