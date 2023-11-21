package bpm.es.web.client.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.es.web.client.EsWeb;
import bpm.es.web.client.EsWeb.Layout;
import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.dialogs.UserDialog;
import bpm.es.web.client.images.Images;
import bpm.es.web.client.services.AdminService;
import bpm.es.web.client.utils.ActivationImageCell;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.DisableableCheckboxCell;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class UsersPanel extends CompositeWaitPanel {
	
	private DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static UsersPanelUiBinder uiBinder = GWT.create(UsersPanelUiBinder.class);

	interface UsersPanelUiBinder extends UiBinder<Widget, UsersPanel> {
	}
	
	interface MyStyle extends CssResource {
		String imgGrid();
		String imgPlanned();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	SimplePanel panelGrid;
	
	private DataGrid<User> datagrid;
	private ListDataProvider<User> dataProvider;
	private ListHandler<User> sortHandler;
	
	private List<User> users;
	
	private LinkedHashMap<Column<User, ?>, String> columns = new LinkedHashMap<>();

	public UsersPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.datagrid = buildGrid();
		panelGrid.setWidget(datagrid);
		
		loadUsers();
		
		manageLayout(EsWeb.getInstance().getLayout());
	}
	
	@UiHandler("btnAddUser")
	public void onAddUser(ClickEvent event) {
		UserDialog dial = new UserDialog(this, null);
		dial.center();
	}
	
	private void loadUsers() {
		showWaitPart(true);
		
		CommonService.Connect.getInstance().getUsers(new GwtCallbackWrapper<List<User>>(this, true) {

			@Override
			public void onSuccess(List<User> result) {
				loadUsers(result);
			}
		}.getAsyncCallback());
	}

	private void loadUsers(List<User> result) {
		this.users = result != null ? result : new ArrayList<User>();
		dataProvider.setList(users);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<User> buildGrid() {
		DisableableCheckboxCell cell = new DisableableCheckboxCell();
		Column<User, Boolean> isAdminColumn = new Column<User, Boolean>(cell) {

			@Override
			public Boolean getValue(User object) {
				return object.isSuperUser();
			}
		};

		TextCell txtCell = new TextCell();
		Column<User, String> loginColumn = new Column<User, String>(txtCell) {

			@Override
			public String getValue(User object) {
				return object.getLogin();
			}
		};
		loginColumn.setSortable(true);

		Column<User, String> surnameColumn = new Column<User, String>(txtCell) {

			@Override
			public String getValue(User object) {
				return object.getSurname();
			}
		};
		surnameColumn.setSortable(true);

		Column<User, String> nameColumn = new Column<User, String>(txtCell) {

			@Override
			public String getValue(User object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		Column<User, String> mailColumn = new Column<User, String>(txtCell) {

			@Override
			public String getValue(User object) {
				return object.getBusinessMail();
			}
		};
		mailColumn.setSortable(true);

		Column<User, String> creationDate = new Column<User, String>(txtCell) {

			@Override
			public String getValue(User object) {
				return object.getCreation() != null ? dtf.format(object.getCreation()) : Labels.lblCnst.Unknown();
			}
		};
		creationDate.setSortable(true);
		
		ActivationImageCell planificationCell = new ActivationImageCell(style.imgPlanned());
		Column<User, String> colActivate = new Column<User, String>(planificationCell) {

			@Override
			public String getValue(User object) {
				if (object.isSuperUser()) {
					return ActivationImageCell.ACTIVATE;
				}
				else {
					return ActivationImageCell.DEACTIVATE;
				}
			}
		};
		colActivate.setFieldUpdater(new FieldUpdater<User, String>() {

			@Override
			public void update(int index, User object, String value) {
				if (value.equals(ActivationImageCell.DEACTIVATE)) {
					
				}
				else if (value.equals(ActivationImageCell.ACTIVATE)) {
					
				}
				manageUser(object, true);
			}
		});
		
		ButtonImageCell editCell = new ButtonImageCell(Images.INSTANCE.ic_edit_black_18dp(), style.imgGrid());
		Column<User, String> colEdit = new Column<User, String>(editCell) {

			@Override
			public String getValue(User object) {
				return "";
			}
		};
		colEdit.setFieldUpdater(new FieldUpdater<User, String>() {

			@Override
			public void update(int index, User object, String value) {
				editUser(object);
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<User, String> colDelete = new Column<User, String>(deleteCell) {

			@Override
			public String getValue(User object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<User, String>() {

			@Override
			public void update(int index, final User object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), Labels.lblCnst.DeleteUserConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteUser(object);
						}
					}
				});
				dial.center();
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<User> dataGrid = new DataGrid<User>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(isAdminColumn, Labels.lblCnst.Administrator());
		dataGrid.setColumnWidth(isAdminColumn, "70px");
		dataGrid.addColumn(loginColumn, Labels.lblCnst.Login());
		dataGrid.setColumnWidth(loginColumn, "150px");
		dataGrid.addColumn(surnameColumn, Labels.lblCnst.Surname());
		dataGrid.addColumn(nameColumn, Labels.lblCnst.Name());
		dataGrid.addColumn(mailColumn, Labels.lblCnst.Mail());
		dataGrid.addColumn(creationDate, Labels.lblCnst.CreationDate());
		dataGrid.addColumn(colActivate, Labels.lblCnst.Activation());
		dataGrid.setColumnWidth(colActivate, "150px");
		dataGrid.addColumn(colEdit);
		dataGrid.setColumnWidth(colEdit, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label("No Data"));
		
		columns.put(surnameColumn, Labels.lblCnst.Surname());
		columns.put(nameColumn, Labels.lblCnst.Name());
		columns.put(mailColumn, Labels.lblCnst.Mail());
		columns.put(creationDate, Labels.lblCnst.CreationDate());

		dataProvider = new ListDataProvider<User>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<User>(new ArrayList<User>());
		sortHandler.setComparator(loginColumn, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getLogin().compareTo(o2.getLogin());
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}

	public void editUser(User user) {
		UserDialog dial = new UserDialog(this, user);
		dial.center();
	}

	public void manageUser(User user, boolean edit) {
		showWaitPart(true);

		AdminService.Connect.getInstance().manageUser(user, edit, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				loadUsers();
			}
		}.getAsyncCallback());
	}

	private void deleteUser(User user) {
		showWaitPart(true);

		AdminService.Connect.getInstance().deleteUser(user, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				loadUsers();
			}
		}.getAsyncCallback());
	}

	public void manageLayout(Layout layout) {
		if (layout == Layout.MOBILE) {
			if (columns != null) {
				for (Entry<Column<User, ?>, String> col : columns.entrySet()) {
					if (datagrid.getColumnIndex(col.getKey()) > 0) {
						datagrid.removeColumn(col.getKey());
					}
				}
			}
		}
		else if (layout == Layout.TABLET || layout == Layout.COMPUTER) {
			if (columns != null) {
				int index = 2;
				for (Entry<Column<User, ?>, String> col : columns.entrySet()) {
					if (datagrid.getColumnIndex(col.getKey()) < 0) {
						datagrid.insertColumn(index, col.getKey(), col.getValue());
						index++;
					}
				}
			}
		}
	}
}
