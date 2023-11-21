package bpm.gwt.aklabox.commons.client.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.customs.LabelTextBox;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.utils.ButtonImageCell;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;
import bpm.gwt.aklabox.commons.client.utils.ResizableImageCell;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class UsersSelectionPanel extends Composite {

	private static UsersSelectionPanelUiBinder uiBinder = GWT.create(UsersSelectionPanelUiBinder.class);

	interface UsersSelectionPanelUiBinder extends UiBinder<Widget, UsersSelectionPanel> {
	}

	interface MyStyle extends CssResource {
		String grid();
		String imgGrid();
	}

	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel toolbar, panelFilter;
	
	@UiField
	LabelTextBox txtFilter;
	
	@UiField
	Button btnFilter;

	@UiField
	SimplePanel panelGrid;
	
	private IUserManager userManager;

	private ListDataProvider<User> dataProvider;
	private ListHandler<User> sortHandler;
	private MultiSelectionModel<User> selectionModel = new MultiSelectionModel<>();

	private List<User> users;
	private List<User> filterUsers;

	public UsersSelectionPanel(IUserManager userManager, boolean displayFilter, boolean displayCheckbox, Widget... widgets) {
		initWidget(uiBinder.createAndBindUi(this));
		this.userManager = userManager;

		if (displayFilter) {
			txtFilter.displayClear(clearHandler);
		}
		else {
			panelFilter.setVisible(false);
		}

		panelGrid.setWidget(buildGrid(displayCheckbox, selectionModel));
		
		if (widgets != null) {
			for (Widget widget : widgets) {
				toolbar.add(widget);
			}
		}
	}
	
	private ClickHandler clearHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			loadUsers(users, null, null);
		}
	};
	
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
	
	private DataGrid<User> buildGrid(boolean displayCheckbox, final MultiSelectionModel<User> selectionModel) {
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

		ButtonImageCell deleteCell = new ButtonImageCell(CommonImages.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<User, String> colDelete = new Column<User, String>(deleteCell) {

			@Override
			public String getValue(User object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<User, String>() {

			@Override
			public void update(int index, final User object, String value) {
				userManager.removeUser(object);
			}
		});

		DataGrid<User> datagrid = new DataGrid<>(99999);
		if (displayCheckbox) {
			datagrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
			datagrid.setColumnWidth(checkColumn, 40, Unit.PX);
		}
		datagrid.addColumn(colImage, "");
		datagrid.setColumnWidth(colImage, "10%");
		datagrid.addColumn(colName, LabelsConstants.lblCnst.Name());
		datagrid.setColumnWidth(colName, "30%");
		datagrid.addColumn(colEmail, LabelsConstants.lblCnst.Email());
		datagrid.setColumnWidth(colEmail, "40%");
		if (userManager != null) {
			datagrid.addColumn(colDelete);
			datagrid.setColumnWidth(colDelete, "70px");
		}

		datagrid.setAutoHeaderRefreshDisabled(true);
		datagrid.setAutoFooterRefreshDisabled(true);
		datagrid.setSize("98%", "100%");
		datagrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<User> createCheckboxManager());
		datagrid.addStyleName(style.grid());

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
	
	public interface IUserManager {
		void removeUser(User user);
	}
}
