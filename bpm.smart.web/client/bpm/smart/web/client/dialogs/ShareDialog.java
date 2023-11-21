package bpm.smart.web.client.dialogs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.UsersProjectsShares;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.panels.resources.NavigationPanel;
import bpm.smart.web.client.services.SmartAirService;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class ShareDialog extends AbstractDialogBox {
	private static ShareDialogUiBinder uiBinder = GWT.create(ShareDialogUiBinder.class);

	interface ShareDialogUiBinder extends UiBinder<Widget, ShareDialog> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	Label lblName;

	@UiField
	SimplePanel panelGrid;// , panelPager;

	@UiField
	MyStyle style;

	private AirProject project;
	private List<User> users = new ArrayList<User>();

	private ListDataProvider<User> dataProvider;
	private ListHandler<User> sortHandler;
	private MultiSelectionModel<User> selectionModel;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	public ShareDialog(NavigationPanel parent, AirProject project) {
		super(lblCnst.ShareProject(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.project = project;

		createButtonBar(lblCnst.Apply(), okHandler, lblCnst.Cancel(), cancelHandler);

		lblName.setText(lblCnst.ShareOfProject() + this.project.getName());

		panelGrid.add(UserGridData());

		loadUsers();
	}

	private void loadUsers() {
		SmartAirService.Connect.getInstance().getUsers(new AsyncCallback<List<User>>() {

			@Override
			public void onFailure(Throwable caught) {

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToLoadUsers());
			}

			@Override
			public void onSuccess(List<User> result) {
				for (User user : result) {
					if (user.getId() == project.getIdUserCreator()) {
						result.remove(result.indexOf(user));
						break;
					}
				}

				dataProvider.setList(result);
				sortHandler.setList(dataProvider.getList());

				SmartAirService.Connect.getInstance().getSharedProjectsUsersbyIdProject(project.getId(), new AsyncCallback<List<UsersProjectsShares>>() {

					@Override
					public void onFailure(Throwable caught) {

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToLoadUsers());
					}

					@Override
					public void onSuccess(List<UsersProjectsShares> result) {
						for (User user : dataProvider.getList()) {
							for (UsersProjectsShares ups : result) {
								if (ups.getIdUser() == user.getId()) {
									selectionModel.setSelected(user, true);
								}

							}
						}
					}
				});

			}
		});

	}

	private DataGrid<User> UserGridData() {

		TextCell cell = new TextCell();
		Column<User, Boolean> checkboxColumn = new Column<User, Boolean>(new CheckboxCell(true, true)) {

			@Override
			public Boolean getValue(User object) {
				return selectionModel.isSelected(object);
			}
		};

		Column<User, String> nameColumn = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);

		Column<User, String> mailColumn = new Column<User, String>(cell) {

			@Override
			public String getValue(User object) {
				return object.getBusinessMail();
			}
		};
		mailColumn.setSortable(true);

		checkboxColumn.setFieldUpdater(new FieldUpdater<User, Boolean>() {

			@Override
			public void update(int index, User object, Boolean value) {

				selectionModel.setSelected(object, value);
			}
		});

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<User> dataGrid = new DataGrid<User>(15);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		// Attention au label
		dataGrid.addColumn(checkboxColumn, "");
		dataGrid.addColumn(nameColumn, lblCnst.Name());
		dataGrid.addColumn(mailColumn, lblCnst.Mail());
		dataGrid.setColumnWidth(checkboxColumn, 15.0, Unit.PCT);
		dataGrid.setColumnWidth(nameColumn, 35.0, Unit.PCT);
		dataGrid.setColumnWidth(mailColumn, 50.0, Unit.PCT);

		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProvider = new ListDataProvider<User>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<User>(new ArrayList<User>());
		sortHandler.setComparator(nameColumn, new Comparator<User>() {

			@Override
			public int compare(User m1, User m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		sortHandler.setComparator(mailColumn, new Comparator<User>() {

			@Override
			public int compare(User m1, User m2) {
				return m1.getBusinessMail().compareTo(m2.getBusinessMail());
			}
		});

		dataGrid.addColumnSortHandler(sortHandler);

		// Add a selection model so we can select cells.
		selectionModel = new MultiSelectionModel<User>();
		selectionModel.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModel);

		return dataGrid;
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			users.clear();
			users.addAll(selectionModel.getSelectedSet());
		}
	};

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			SmartAirService.Connect.getInstance().shareProject(project.getId(), users, new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					String msg = "";
					for (User user : users) {
						msg += user.getName() + ", ";
					}
					msg = msg.substring(0, msg.length() - 2) + " " + lblCnst.CanAccessToThisProject();

					MessageHelper.openMessageDialog(lblCnst.Information(), msg);
					ShareDialog.this.hide();
				}

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToShareProject());
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			ShareDialog.this.hide();
		}
	};

}
