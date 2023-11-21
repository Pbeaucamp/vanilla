package bpm.gwt.commons.client.viewer.aklabox;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.User;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCheckboxCell;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.TypeShareAklabox;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class AklaboxRightsView extends Composite {

	private static AklaboxRightsViewUiBinder uiBinder = GWT.create(AklaboxRightsViewUiBinder.class);

	interface AklaboxRightsViewUiBinder extends UiBinder<Widget, AklaboxRightsView> {
	}

	interface MyStyle extends CssResource {
		String datagrid();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	@UiField
	RadioButton btnPrivate, btnPublic, btnUsersShare, btnGroupsShare;

	private IWait waitPanel;

	private DataGrid<User> userDataGrid;
	private SelectionModel<User> selectionUser;
	private ListDataProvider<User> userDataProvider;
	private List<User> aklaUsers;

	private DataGrid<Group> groupDataGrid;
	private SelectionModel<Group> selectionGroup;
	private ListDataProvider<Group> groupDataProvider;
	private List<Group> aklaGroups;

	private TypeShareAklabox type;
	
	public AklaboxRightsView(IWait waitPanel, PortailRepositoryItem item) {
		initWidget(uiBinder.createAndBindUi(this));
		this.waitPanel = waitPanel;
		
		this.type = TypeShareAklabox.PRIVATE;
		
		btnPrivate.setValue(true);
		btnPublic.setValue(true);
		refreshUi(false);
	}

	@UiHandler("btnPrivate")
	public void onPrivateClick(ClickEvent event) {
		refreshUi(false);
		
		this.type = TypeShareAklabox.PRIVATE;
	}

	@UiHandler("btnShared")
	public void onSharedClick(ClickEvent event) {
		refreshUi(true);
	}

	@UiHandler("btnPublic")
	public void onPublicClick(ClickEvent event) {
		refreshSharePart();
	}

	@UiHandler("btnUsersShare")
	public void onUsersClick(ClickEvent event) {
		refreshSharePart();
	}

	@UiHandler("btnGroupsShare")
	public void onGroupsClick(ClickEvent event) {
		refreshSharePart();
	}

	private void refreshUi(boolean isShared) {
		btnPublic.setEnabled(isShared);
		btnUsersShare.setEnabled(isShared);
		btnGroupsShare.setEnabled(isShared);

		if (!isShared) {
			panelGrid.clear();
			
			this.type = TypeShareAklabox.PRIVATE;
		}
		else {
			refreshSharePart();
		}
	}
	
	private void refreshSharePart() {
		if (btnUsersShare.getValue()) {
			setUserGrid();
			
			this.type = TypeShareAklabox.USERS_SHARE;
		}
		else if (btnGroupsShare.getValue()) {
			setGroupGrid();
			
			this.type = TypeShareAklabox.GROUPS_SHARE;
		}
		else {
			panelGrid.clear();
			
			this.type = TypeShareAklabox.PUBLIC;
		}
	}

	private void showDatagrid(DataGrid<?> dataGrid) {
		panelGrid.setWidget(dataGrid);
	}

	public void setUserGrid() {
		this.selectionUser = new MultiSelectionModel<User>();

		if (aklaUsers != null && userDataProvider != null && userDataGrid != null) {
			userDataProvider.setList(aklaUsers);
			showDatagrid(userDataGrid);
		}
		else {
			waitPanel.showWaitPart(true);

			LoginService.Connect.getInstance().getAklaboxUsers(new AsyncCallback<List<User>>() {

				@Override
				public void onSuccess(List<User> result) {
					userDataGrid = buildUserPart(result);
					showDatagrid(userDataGrid);

					waitPanel.showWaitPart(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					waitPanel.showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
				}
			});
		}
	}

	private DataGrid<User> buildUserPart(List<User> aklaUsers) {
		this.aklaUsers = aklaUsers;

		CheckboxCell cell = new CheckboxCell();
		Column<User, Boolean> modelNameColumn = new Column<User, Boolean>(cell) {

			@Override
			public Boolean getValue(User object) {
				return selectionUser.isSelected(object);
			}
		};

		TextCell txtCell = new TextCell();
		Column<User, String> nameColumn = new Column<User, String>(txtCell) {

			@Override
			public String getValue(User object) {
				return object.getEmail();
			}
		};

		Header<Boolean> headerCheck = new Header<Boolean>(new CustomCheckboxCell<User>(aklaUsers, selectionUser)) {

			@Override
			public Boolean getValue() {
				return false;
			}
		};

		Header<String> headerName = new Header<String>(new TextCell()) {

			@Override
			public String getValue() {
				return LabelsConstants.lblCnst.Users();
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<User> dataGrid = new DataGrid<User>(10000, resources);
		dataGrid.addStyleName(style.datagrid());
		dataGrid.addColumn(modelNameColumn, headerCheck);
		dataGrid.setColumnWidth(modelNameColumn, "40px");
		dataGrid.addColumn(nameColumn, headerName);
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoUser()));

		userDataProvider = new ListDataProvider<User>(aklaUsers);
		userDataProvider.addDataDisplay(dataGrid);

		dataGrid.setSelectionModel(selectionUser, DefaultSelectionEventManager.<User> createCheckboxManager());

		return dataGrid;
	}

	public void setGroupGrid() {
		this.selectionGroup = new MultiSelectionModel<Group>();

		if (aklaGroups != null && groupDataProvider != null && groupDataGrid != null) {
			groupDataProvider.setList(aklaGroups);
			showDatagrid(groupDataGrid);
		}
		else {
			waitPanel.showWaitPart(true);

			LoginService.Connect.getInstance().getAklaboxGroups(new AsyncCallback<List<Group>>() {

				@Override
				public void onSuccess(List<Group> result) {
					groupDataGrid = buildGroupPart(result);
					showDatagrid(groupDataGrid);

					waitPanel.showWaitPart(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					waitPanel.showWaitPart(false);

					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
				}
			});
		}
	}

	private DataGrid<Group> buildGroupPart(List<Group> aklaGroups) {
		this.aklaGroups = aklaGroups;

		CheckboxCell cell = new CheckboxCell();
		Column<Group, Boolean> modelNameColumn = new Column<Group, Boolean>(cell) {

			@Override
			public Boolean getValue(Group object) {
				return selectionGroup.isSelected(object);
			}
		};

		TextCell txtCell = new TextCell();
		Column<Group, String> nameColumn = new Column<Group, String>(txtCell) {

			@Override
			public String getValue(Group object) {
				return object.getGroupName();
			}
		};

		Header<Boolean> headerCheck = new Header<Boolean>(new CustomCheckboxCell<Group>(aklaGroups, selectionGroup)) {

			@Override
			public Boolean getValue() {
				return false;
			}
		};

		Header<String> headerName = new Header<String>(new TextCell()) {

			@Override
			public String getValue() {
				return LabelsConstants.lblCnst.Groups();
			}
		};

		DataGrid.Resources resources = new CustomResources();
		DataGrid<Group> dataGrid = new DataGrid<Group>(10000, resources);
		dataGrid.addStyleName(style.datagrid());
		dataGrid.addColumn(modelNameColumn, headerCheck);
		dataGrid.setColumnWidth(modelNameColumn, "40px");
		dataGrid.addColumn(nameColumn, headerName);
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoGroup()));

		groupDataProvider = new ListDataProvider<Group>(aklaGroups);
		groupDataProvider.addDataDisplay(dataGrid);

		dataGrid.setSelectionModel(selectionGroup, DefaultSelectionEventManager.<Group> createCheckboxManager());

		return dataGrid;
	}

	public TypeShareAklabox getTypeShare() {
		return type;
	}

	public List<User> getSelectedUsers() {
		if(type == TypeShareAklabox.USERS_SHARE) {
			if(aklaUsers != null && selectionUser != null) {
				List<User> users = new ArrayList<User>();
				for (User user : aklaUsers) {
					if (selectionUser.isSelected(user)) {
						users.add(user);
					}
				}
				return users;
			}
		}
		return null;
	}

	public List<Group> getSelectedGroups() {
		if(type == TypeShareAklabox.GROUPS_SHARE) {
			if(aklaGroups != null && selectionGroup != null) {
				List<Group> groups = new ArrayList<Group>();
				for (Group group : aklaGroups) {
					if (selectionGroup.isSelected(group)) {
						groups.add(group);
					}
				}
				return groups;
			}
		}
		return null;
	}
}
