package bpm.es.web.client.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.services.AdminService;
import bpm.gwt.commons.client.custom.CustomListBox;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.vanilla.platform.core.beans.SecurityLog;
import bpm.vanilla.platform.core.beans.SecurityLog.TypeSecurityLog;
import bpm.vanilla.platform.core.beans.User;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.view.client.ListDataProvider;

public class LogsPanel extends CompositeWaitPanel {
	
	private static final int ALL_TIME = 0;
	private static final int LAST_MONTH = 1;
	private static final int LAST_WEEK = 2;
	private static final int TODAY = 3;
	
	private DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");

	private static LogsPanelUiBinder uiBinder = GWT.create(LogsPanelUiBinder.class);

	interface LogsPanelUiBinder extends UiBinder<Widget, LogsPanel> {
	}
	
	interface MyStyle extends CssResource {
		String pager();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	CustomListBox lstUsers, lstActions, lstTimes;
	
	@UiField
	SimplePanel panelContent, panelPager;
	
	private ListDataProvider<SecurityLog> dataProvider;
	private ListHandler<SecurityLog> sortHandler;
	
	private List<SecurityLog> logs;

	public LogsPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		
		populateLists();
		
		DataGrid<SecurityLog> datagrid = buildGrid();
		panelContent.setWidget(datagrid);
	}

	private void populateLists() {
		showWaitPart(true);
		
		CommonService.Connect.getInstance().getUsers(new GwtCallbackWrapper<List<User>>(this, false) {

			@Override
			public void onSuccess(List<User> result) {
				lstUsers.addItem(Labels.lblCnst.AllUsers(), "-1");
				if (result != null) {
					for (User user : result) {
						lstUsers.addItem(user.getLogin(), String.valueOf(user.getId()));
					}
				}
				
				lstActions.addItem(Labels.lblCnst.AllActions(), "-1");
				for (TypeSecurityLog typeAction : TypeSecurityLog.values()) {
					lstActions.addItem(getTypeLabel(typeAction), String.valueOf(typeAction.getType()));
				}
				
				lstTimes.addItem(Labels.lblCnst.AllTimes(), String.valueOf(ALL_TIME));
				lstTimes.addItem(Labels.lblCnst.LastMonth(), String.valueOf(LAST_MONTH));
				lstTimes.addItem(Labels.lblCnst.LastWeek(), String.valueOf(LAST_WEEK));
				lstTimes.addItem(Labels.lblCnst.Today(), String.valueOf(TODAY));
				
				lstTimes.setSelectedIndex(LAST_WEEK);
				
				Date startDate = buildStartDate(LAST_WEEK);
				
				loadLogs(null, null, startDate, new Date());
			}
		}.getAsyncCallback());
	}
	
	@UiHandler("btnFilter")
	public void onFilter(ClickEvent event) {
		int selectedUserId = Integer.parseInt(lstUsers.getValue(lstUsers.getSelectedIndex()));
		int typeActionId = Integer.parseInt(lstActions.getValue(lstActions.getSelectedIndex()));
		int timeType = lstTimes.getSelectedIndex();
		
		Integer userId = selectedUserId > 0 ? selectedUserId : null;
		TypeSecurityLog typeSecurityLog = typeActionId >= 0 ? TypeSecurityLog.valueOf(typeActionId) : null;
		
		Date startDate = buildStartDate(timeType);
		loadLogs(userId, typeSecurityLog, startDate, new Date());
	}

	private Date buildStartDate(int timeType) {
		Date startDate = new Date();
		switch (timeType) {
		case ALL_TIME:
			return null;
		case LAST_MONTH:
			CalendarUtil.addDaysToDate(startDate, -30);
			break;
		case LAST_WEEK:
			CalendarUtil.addDaysToDate(startDate, -7);
			break;
		case TODAY:
			CalendarUtil.addDaysToDate(startDate, -1);
			break;
		default:
			break;
		}
		return startDate;
	}

	private void loadLogs(Integer userId, TypeSecurityLog type, Date startDate, Date endDate) {
		showWaitPart(true);

		AdminService.Connect.getInstance().getSecurityLogs(userId, type, startDate, endDate, new GwtCallbackWrapper<List<SecurityLog>>(this, true) {

			@Override
			public void onSuccess(List<SecurityLog> result) {
				loadLogs(result);
			}
		}.getAsyncCallback());
	}
	
	private void loadLogs(List<SecurityLog> logs) {
		this.logs = logs != null ? logs : new ArrayList<SecurityLog>();
		dataProvider.setList(this.logs);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<SecurityLog> buildGrid() {
		TextCell txtCell = new TextCell();
		Column<SecurityLog, String> loginColumn = new Column<SecurityLog, String>(txtCell) {

			@Override
			public String getValue(SecurityLog object) {
				return object.getUser() != null ? object.getUser().getLogin() : Labels.lblCnst.Unknown();
			}
		};
		loginColumn.setSortable(true);

		Column<SecurityLog, String> actionColumn = new Column<SecurityLog, String>(txtCell) {

			@Override
			public String getValue(SecurityLog object) {
				return getTypeLabel(object.getTypeSecurityLog());
			}
		};
		actionColumn.setSortable(true);

		Column<SecurityLog, String> clientIpColumn = new Column<SecurityLog, String>(txtCell) {

			@Override
			public String getValue(SecurityLog object) {
				return object.getClientIp();
			}
		};
		clientIpColumn.setSortable(true);

		Column<SecurityLog, String> creationDate = new Column<SecurityLog, String>(txtCell) {

			@Override
			public String getValue(SecurityLog object) {
				return object.getDate() != null ? dtf.format(object.getDate()) : Labels.lblCnst.Unknown();
			}
		};
		creationDate.setSortable(true);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<SecurityLog> dataGrid = new DataGrid<SecurityLog>(50, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(loginColumn, Labels.lblCnst.Login());
		dataGrid.setColumnWidth(loginColumn, "150px");
		dataGrid.addColumn(actionColumn, Labels.lblCnst.Action());
		dataGrid.addColumn(clientIpColumn, Labels.lblCnst.ClientIP());
		dataGrid.addColumn(creationDate, Labels.lblCnst.Date());
		dataGrid.setEmptyTableWidget(new Label("No Data"));

		dataProvider = new ListDataProvider<SecurityLog>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<SecurityLog>(new ArrayList<SecurityLog>());
		sortHandler.setComparator(loginColumn, new Comparator<SecurityLog>() {

			@Override
			public int compare(SecurityLog o1, SecurityLog o2) {
				if(o1.getUser() == null) {
					return -1;
				}
				else if(o2.getUser() == null) {
					return 1;
				}
				
				return o1.getUser().getLogin().compareTo(o2.getUser().getLogin());
			}
		});
		sortHandler.setComparator(actionColumn, new Comparator<SecurityLog>() {

			@Override
			public int compare(SecurityLog o1, SecurityLog o2) {
				return o1.getTypeSecurityLog().compareTo(o2.getTypeSecurityLog());
			}
		});
		sortHandler.setComparator(clientIpColumn, new Comparator<SecurityLog>() {

			@Override
			public int compare(SecurityLog o1, SecurityLog o2) {
				return o1.getClientIp().compareTo(o2.getClientIp());
			}
		});
		sortHandler.setComparator(creationDate, new Comparator<SecurityLog>() {

			@Override
			public int compare(SecurityLog o1, SecurityLog o2) {
				if(o1.getDate() == null) {
					return -1;
				}
				else if(o2.getDate() == null) {
					return 1;
				}
				
				return o2.getDate().before(o1.getDate()) ? -1 : o2.getDate().after(o1.getDate()) ? 1 : 0;
			}
		});
		
		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());
		
		// Create a Pager to control the table.
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName(style.pager());
	    pager.setDisplay(dataGrid);

	    panelPager.setWidget(pager);
	    
		return dataGrid;
	}

	private String getTypeLabel(TypeSecurityLog type) {
		switch (type) {
		case LOGIN:
			return Labels.lblCnst.Login();
		case LOGOUT:
			return Labels.lblCnst.Logout();
		case WRONG_PASSWORD:
			return Labels.lblCnst.WrongPassword();
		case TOO_MANY_TRIES:
			return Labels.lblCnst.TooManyConnectionTries();
		case PASSWORD_CHANGE:
			return Labels.lblCnst.PasswordChange();
		default:
			break;
		}
		
		return Labels.lblCnst.Unknown();
	}
}
