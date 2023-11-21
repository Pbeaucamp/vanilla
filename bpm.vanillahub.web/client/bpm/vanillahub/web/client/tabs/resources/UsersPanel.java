package bpm.vanillahub.web.client.tabs.resources;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.workflow.commons.client.IManager;
import bpm.gwt.workflow.commons.client.IResourceManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.images.Images;
import bpm.gwt.workflow.commons.client.tabs.HorizontalTab;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanillahub.web.client.I18N.Labels;
import bpm.vanillahub.web.client.dialogs.ChangePasswordDialog;
import bpm.vanillahub.web.client.dialogs.UserDialog;
import bpm.vanillahub.web.client.services.ResourcesService;

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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class UsersPanel extends HorizontalTab implements IManager<User> {

	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private static UserPanelUiBinder uiBinder = GWT.create(UserPanelUiBinder.class);

	interface UserPanelUiBinder extends UiBinder<Widget, UsersPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel gridPanel;

	private ResourceManager resourceManager;

	private ListDataProvider<User> dataProvider;
	private ListHandler<User> sortHandler;

	public UsersPanel(IResourceManager resourceManager) {
		super(Labels.lblCnst.Users(), bpm.vanillahub.web.client.images.Images.INSTANCE.action_b_24dp());
		this.add(uiBinder.createAndBindUi(this));
		this.resourceManager = (ResourceManager) resourceManager;

		this.addStyleName(style.mainPanel());

		DataGrid<User> datagrid = createGridUsers();
		gridPanel.setWidget(datagrid);

		loadResources();
	}

	@Override
	public void loadResources() {
		resourceManager.loadUsers(this, this);
	}

	@Override
	public void loadResources(List<User> result) {
		if (result != null) {
			dataProvider.setList(result);
			sortHandler.setList(dataProvider.getList());
		}
		else {
			dataProvider.setList(new ArrayList<User>());
		}
	}

	private DataGrid<User> createGridUsers() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		TextCell cell = new TextCell();
		Column<User, String> colIsAdmin = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				if (object.isSuperUser()) {
					return Labels.lblCnst.Administrator();
				}
				else {
					return Labels.lblCnst.Reader();
				}
			}

		};
		colIsAdmin.setSortable(true);

		Column<User, String> colLogin = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				return object.getName();
			}
		};
		colLogin.setSortable(true);

		Column<User, String> colEmail = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				return object.getBusinessMail();
			}
		};
		colEmail.setSortable(true);

		Column<User, String> colFonction = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				return object.getFunction();
			}
		};
		colFonction.setSortable(true);

		Column<User, String> colCreationDate = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				if (object.getCreation() != null) {
					return dateFormatter.format(object.getCreation());
				}
				else {
					return LabelsCommon.lblCnst.Unknown();
				}
			}
		};
		colCreationDate.setSortable(true);

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
				UserDialog dial = new UserDialog(UsersPanel.this, resourceManager, dataProvider.getList(), object);
				dial.center();
			}
		});

		ButtonImageCell passwordCell = new ButtonImageCell(bpm.vanillahub.web.client.images.Images.INSTANCE.encryption_b_24dp(), style.imgGrid());
		Column<User, String> colChangePassword = new Column<User, String>(passwordCell) {

			@Override
			public String getValue(User object) {
				return "";
			}
		};
		colChangePassword.setFieldUpdater(new FieldUpdater<User, String>() {

			@Override
			public void update(int index, User object, String value) {
				ChangePasswordDialog dial = new ChangePasswordDialog(UsersPanel.this, object, true);
				dial.center();
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
				final InformationsDialog dial = new InformationsDialog(LabelsCommon.lblCnst.Information(), LabelsCommon.lblCnst.Confirmation(), LabelsCommon.lblCnst.Cancel(), Labels.lblCnst.DeleteUserConfirm(), true);
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

		sortHandler = new ListHandler<User>(new ArrayList<User>());
		sortHandler.setComparator(colIsAdmin, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return ((Boolean) o1.isSuperUser()).compareTo((Boolean) o2.isSuperUser());
			}
		});
		sortHandler.setComparator(colLogin, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(colEmail, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getBusinessMail().compareTo(o2.getBusinessMail());
			}
		});
		sortHandler.setComparator(colFonction, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				if (o1.getFunction() == null) {
					return -1;
				}
				else if (o2.getFunction() == null) {
					return 1;
				}

				return o1.getFunction().compareTo(o2.getFunction());
			}
		});
		sortHandler.setComparator(colCreationDate, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				if (o1.getCreation() == null) {
					return -1;
				}
				else if (o2.getCreation() == null) {
					return 1;
				}

				return o2.getCreation().before(o1.getCreation()) ? -1 : o2.getCreation().after(o1.getCreation()) ? 1 : 0;
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<User> dataGrid = new DataGrid<User>(30, resources);
		dataGrid.setSize("100%", "100%");
		dataGrid.addColumn(colIsAdmin, LabelsCommon.lblCnst.Status());
		dataGrid.setColumnWidth(colIsAdmin, "150px");
		dataGrid.addColumn(colLogin, LabelsCommon.lblCnst.Login());
		dataGrid.setColumnWidth(colLogin, "150px");
		dataGrid.addColumn(colEmail, Labels.lblCnst.Email());
		dataGrid.setColumnWidth(colEmail, "250px");
		dataGrid.addColumn(colFonction, Labels.lblCnst.Fonction());
		dataGrid.setColumnWidth(colFonction, "150px");
		dataGrid.addColumn(colCreationDate, LabelsCommon.lblCnst.CreationDate());
		dataGrid.setColumnWidth(colCreationDate, "150px");
		dataGrid.addColumn(colEdit);
		dataGrid.setColumnWidth(colEdit, "70px");
		dataGrid.addColumn(colChangePassword);
		dataGrid.setColumnWidth(colChangePassword, "70px");
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.addColumnSortHandler(sortHandler);

		dataProvider = new ListDataProvider<User>();
		dataProvider.addDataDisplay(dataGrid);

		SelectionModel<User> selectionModel = new SingleSelectionModel<User>();
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	@UiHandler("btnAddUser")
	public void onAddUserClick(ClickEvent event) {
		UserDialog dial = new UserDialog(this, resourceManager, dataProvider.getList(), null);
		dial.center();
	}

	private void deleteUser(User user) {
		showWaitPart(true);

		ResourcesService.Connect.getInstance().removeUser(user, new GwtCallbackWrapper<Void>(this, true) {

			@Override
			public void onSuccess(Void result) {
				loadResources();
			}
		}.getAsyncCallback());
	}
}
