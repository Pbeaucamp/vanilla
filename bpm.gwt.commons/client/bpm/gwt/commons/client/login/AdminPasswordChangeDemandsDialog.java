package bpm.gwt.commons.client.login;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.LoginService;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.vanilla.platform.core.beans.PasswordBackup;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class AdminPasswordChangeDemandsDialog extends AbstractDialogBox {
	
	private DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");

	private static AdminPasswordChangeDemandsDialogUiBinder uiBinder = GWT.create(AdminPasswordChangeDemandsDialogUiBinder.class);

	interface AdminPasswordChangeDemandsDialogUiBinder extends UiBinder<Widget, AdminPasswordChangeDemandsDialog> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;
	
	private IWait waitPanel;
	
	private ListDataProvider<PasswordBackup> dataProvider;
	private ListHandler<PasswordBackup> sortHandler;

	public AdminPasswordChangeDemandsDialog(IWait waitPanel, List<PasswordBackup> passwordBackups) {
		super(LabelsConstants.lblCnst.PasswordChangeDemands(), false, true);
		this.waitPanel = waitPanel;

		setWidget(uiBinder.createAndBindUi(this));
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);

		panelGrid.setWidget(buildGrid(passwordBackups));
		loadDemands(passwordBackups);
	}
	
	private void loadDemands() {
		LoginService.Connect.getInstance().getPendingPasswordChangeDemands(new GwtCallbackWrapper<List<PasswordBackup>>(waitPanel, true) {

			@Override
			public void onSuccess(List<PasswordBackup> result) {
				if (result == null || result.isEmpty()) {
					hide();
				}
				else {
					loadDemands(result);
				}
			}
		}.getAsyncCallback());
	}

	private void loadDemands(List<PasswordBackup> passwordBackups) {
		dataProvider.setList(passwordBackups != null ? passwordBackups : new ArrayList<PasswordBackup>());
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<PasswordBackup> buildGrid(List<PasswordBackup> passwordBackups) {
		TextCell txtCell = new TextCell();
		Column<PasswordBackup, String> loginColumn = new Column<PasswordBackup, String>(txtCell) {

			@Override
			public String getValue(PasswordBackup object) {
				return object.getUser() != null ? object.getUser().getLogin() : LabelsConstants.lblCnst.Unknown();
			}
		};
		loginColumn.setSortable(true);

		Column<PasswordBackup, String> creationDate = new Column<PasswordBackup, String>(txtCell) {

			@Override
			public String getValue(PasswordBackup object) {
				return object.getCreationDate() != null ? dtf.format(object.getCreationDate()) : LabelsConstants.lblCnst.Unknown();
			}
		};
		creationDate.setSortable(true);
		
		ButtonImageCell acceptCell = new ButtonImageCell(CommonImages.INSTANCE.ic_check_black_27dp(), style.imgGrid());
		Column<PasswordBackup, String> colAccept = new Column<PasswordBackup, String>(acceptCell) {

			@Override
			public String getValue(PasswordBackup object) {
				return "";
			}
		};
		colAccept.setFieldUpdater(new FieldUpdater<PasswordBackup, String>() {

			@Override
			public void update(int index, final PasswordBackup object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.SureAcceptPasswordChangeDemand(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							madeDecision(object, true);
						}
					}
				});
				dial.center();
			}
		});

		ButtonImageCell declineCell = new ButtonImageCell(CommonImages.INSTANCE.ic_decline_black_27dp(), style.imgGrid());
		Column<PasswordBackup, String> colDecline = new Column<PasswordBackup, String>(declineCell) {

			@Override
			public String getValue(PasswordBackup object) {
				return "";
			}
		};
		colDecline.setFieldUpdater(new FieldUpdater<PasswordBackup, String>() {

			@Override
			public void update(int index, final PasswordBackup object, String value) {
				final InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.Confirmation(), LabelsConstants.lblCnst.Cancel(), LabelsConstants.lblCnst.SureDeclinePasswordChangeDemand(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							madeDecision(object, false);
						}
					}
				});
				dial.center();
			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<PasswordBackup> dataGrid = new DataGrid<PasswordBackup>(50, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(loginColumn, LabelsConstants.lblCnst.Login());
		dataGrid.setColumnWidth(loginColumn, "150px");
		dataGrid.addColumn(creationDate, LabelsConstants.lblCnst.Date());
		dataGrid.addColumn(colAccept);
		dataGrid.setColumnWidth(colAccept, "70px");
		dataGrid.addColumn(colDecline);
		dataGrid.setColumnWidth(colDecline, "70px");
		dataGrid.setEmptyTableWidget(new Label("No Data"));

		dataProvider = new ListDataProvider<PasswordBackup>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<PasswordBackup>(new ArrayList<PasswordBackup>());
		sortHandler.setComparator(loginColumn, new Comparator<PasswordBackup>() {

			@Override
			public int compare(PasswordBackup o1, PasswordBackup o2) {
				if (o1.getUser() == null) {
					return -1;
				}
				else if (o2.getUser() == null) {
					return 1;
				}

				return o1.getUser().getLogin().compareTo(o2.getUser().getLogin());
			}
		});

		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}

	private void madeDecision(PasswordBackup object, boolean accept) {
		waitPanel.showWaitPart(true);
		
		String webappUrl = GWT.getHostPageBaseURL();
		
		LoginService.Connect.getInstance().acceptPasswordChangeDemand(webappUrl, object, accept, new GwtCallbackWrapper<Void>(waitPanel, true) {

			@Override
			public void onSuccess(Void result) {
				loadDemands();
			}
		}.getAsyncCallback());
	}

	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			AdminPasswordChangeDemandsDialog.this.hide();
		}
	};
}
