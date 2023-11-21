package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.AccessRequest;
import bpm.vanilla.platform.core.beans.AccessRequest.RequestAnswer;
import bpm.vanilla.portal.client.dialog.RequestAccessResponseDialog;
import bpm.vanilla.portal.client.services.AccessRequestService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class AccessRequestPanel extends Tab {

	private static final String DATE_FORMAT = "HH:mm - dd/MM/yyyy";

	private static AccessRequestPanelUiBinder uiBinder = GWT.create(AccessRequestPanelUiBinder.class);

	interface AccessRequestPanelUiBinder extends UiBinder<Widget, AccessRequestPanel> {
	}

	interface MyStyle extends CssResource {
		String mainPanel();

		String pager();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelRequestToMe, panelRequestByMe, panelPagerToMe, panelPagerByMe;

	@UiField
	RadioButton btnShowRequests, btnShowMyDemands;
	
	@UiField
	CheckBox btnShowHistoryDemands;

	private ListDataProvider<AccessRequest> dataProvider;
	private ListHandler<AccessRequest> sortRequestToMeHandler;
	private ListHandler<AccessRequest> sortRequestByMeHandler;

	public AccessRequestPanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.RequestAccess_TabTitle(), true);
		this.add(uiBinder.createAndBindUi(this));

		DataGrid<AccessRequest> dataGridRequestsToMe = createGridDataRequestsToMe();
		DataGrid<AccessRequest> dataGridRequestsMadeByMe = createGridDataDemandesByMe();
		
		panelRequestToMe.add(dataGridRequestsToMe);
		panelRequestByMe.add(dataGridRequestsMadeByMe);
		
	    dataProvider = new ListDataProvider<AccessRequest>();
	    dataProvider.addDataDisplay(dataGridRequestsToMe);
	    dataProvider.addDataDisplay(dataGridRequestsMadeByMe);

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());
		
		btnShowRequests.setValue(true);
		
		loadData();
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadData();
	}

	@UiHandler("btnShowRequests")
	public void onShowRequestsClick(ClickEvent event) {
		loadData();
	}

	@UiHandler("btnShowMyDemands")
	public void onShowMyDemandsClick(ClickEvent event) {
		loadData();
	}

	@UiHandler("btnShowHistoryDemands")
	public void onShowHistoryClick(ClickEvent event) {
		loadData();
	}
	
	private DataGrid<AccessRequest> createGridDataRequestsToMe() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<AccessRequest> dataGridRequestsToMe = new DataGrid<AccessRequest>(12, resources);
		dataGridRequestsToMe.setWidth("100%");
		dataGridRequestsToMe.setHeight("100%");

		TextCell cell = new TextCell();

		Column<AccessRequest, String> requestDateColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				Date date = object.getRequestDate();
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}

		};
		requestDateColumn.setSortable(true);

		Column<AccessRequest, String> objectNameColumn = new Column<AccessRequest, String>(cell) {
			@Override
			public String getValue(AccessRequest object) {
				return object.getItemName();
			}
		};
		objectNameColumn.setSortable(true);

		Column<AccessRequest, String> userNameColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				return object.getUserName();
			}
		};
		userNameColumn.setSortable(true);

		Column<AccessRequest, String> groupNameColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				return object.getGroupName();
			}
		};
		groupNameColumn.setSortable(true);

		Column<AccessRequest, String> userMessageColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				return object.getRequestInfo();
			}
		};

		Column<AccessRequest, String> statusColumn = new Column<AccessRequest, String>(cell) {
			@Override
			public String getValue(AccessRequest object) {
				if(object.getAnswerOp().getAnswerName().equalsIgnoreCase("pending")) {
					return ToolsGWT.lblCnst.Pending();
				}
				else if(object.getAnswerOp().getAnswerName().equalsIgnoreCase("accepted")) {
					return ToolsGWT.lblCnst.Accepted();
				}
				else {
					return object.getAnswerOp().getAnswerName();
				}
			}
		};
		statusColumn.setSortable(true);

		Column<AccessRequest, String> clickApproveColumn = new Column<AccessRequest, String>(new ButtonCell()) {
			@Override
			public String getValue(AccessRequest object) {
				if (object.getAnswerOp() == RequestAnswer.PENDING) {
					return ToolsGWT.lblCnst.RequestAccess_Table_Approve();
				}
				else {
					return null;
				}
			}

			@Override
			public void render(Context context, AccessRequest object, SafeHtmlBuilder sb) {
				if (object.getAnswerOp() == RequestAnswer.PENDING) {
					super.render(context, object, sb);
				}
				else {
					//
				}
			}
		};
		clickApproveColumn.setFieldUpdater(new FieldUpdater<AccessRequest, String>() {
			@Override
			public void update(int index, AccessRequest object, String value) {
				RequestAccessResponseDialog response = new RequestAccessResponseDialog(AccessRequestPanel.this, object, ToolsGWT.lblCnst.RequestAccess_Table_Approve(), true);
				response.center();
			}
		});

		Column<AccessRequest, String> clickRefuseColumn = new Column<AccessRequest, String>(new ButtonCell()) {
			@Override
			public String getValue(AccessRequest object) {
				if (object.getAnswerOp() == RequestAnswer.PENDING) {
					return ToolsGWT.lblCnst.RequestAccess_Table_Refuse();
				}
				else {
					return null;
				}
			}

			@Override
			public void render(Context context, AccessRequest object, SafeHtmlBuilder sb) {
				if (object.getAnswerOp() == RequestAnswer.PENDING) {
					super.render(context, object, sb);
				}
				else {
					//
				}
			}
		};
		clickRefuseColumn.setFieldUpdater(new FieldUpdater<AccessRequest, String>() {
			@Override
			public void update(int index, AccessRequest object, String value) {
				RequestAccessResponseDialog response = new RequestAccessResponseDialog(AccessRequestPanel.this, object, ToolsGWT.lblCnst.RequestAccess_Table_Refuse(), false);
				response.center();
			}
		});

		dataGridRequestsToMe.addColumn(requestDateColumn, ToolsGWT.lblCnst.RequestAccess_Table_RequestDate());
		dataGridRequestsToMe.addColumn(objectNameColumn, ToolsGWT.lblCnst.RequestAccess_Table_ObjectName());
		dataGridRequestsToMe.addColumn(userNameColumn, ToolsGWT.lblCnst.RequestAccess_Table_DemandUser());
		dataGridRequestsToMe.addColumn(groupNameColumn, ToolsGWT.lblCnst.RequestAccess_Table_DemandGroup());
		dataGridRequestsToMe.addColumn(userMessageColumn, ToolsGWT.lblCnst.RequestAccess_Table_UserMessage());
		dataGridRequestsToMe.addColumn(statusColumn, ToolsGWT.lblCnst.RequestAccess_Table_Status());

		dataGridRequestsToMe.addColumn(clickApproveColumn, ToolsGWT.lblCnst.RequestAccess_Table_Approve());
		dataGridRequestsToMe.addColumn(clickRefuseColumn, ToolsGWT.lblCnst.RequestAccess_Table_Refuse());

		dataGridRequestsToMe.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.RequestAccess_Table_Empty()));

		sortRequestToMeHandler = new ListHandler<AccessRequest>(new ArrayList<AccessRequest>());
		sortRequestToMeHandler.setComparator(requestDateColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				if(o1.getRequestDate() == null) {
					return -1;
				}
				else if(o2.getRequestDate() == null) {
					return 1;
				}
				
				return o2.getRequestDate().before(o1.getRequestDate()) ? -1 : o2.getRequestDate().after(o1.getRequestDate()) ? 1 : 0;
			}
		});
		sortRequestToMeHandler.setComparator(objectNameColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				return o1.getItemName().compareTo(o2.getItemName());
			}
		});
		sortRequestToMeHandler.setComparator(userNameColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				return o1.getUserName().compareTo(o2.getUserName());
			}
		});
		sortRequestToMeHandler.setComparator(groupNameColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				return o1.getGroupName().compareTo(o2.getGroupName());
			}
		});
		sortRequestToMeHandler.setComparator(statusColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				return o1.getAnswerOp().getAnswerName().compareTo(o2.getAnswerOp().getAnswerName());
			}
		});
		dataGridRequestsToMe.addColumnSortHandler(sortRequestToMeHandler);
		
		// Add a selection model so we can select cells.
		SelectionModel<AccessRequest> selectionModel = new SingleSelectionModel<AccessRequest>();
		dataGridRequestsToMe.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGridRequestsToMe);

		panelPagerToMe.setWidget(pager);

		return dataGridRequestsToMe;
	}

	private DataGrid<AccessRequest> createGridDataDemandesByMe() {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<AccessRequest> dataGridRequestsMadeByMe = new DataGrid<AccessRequest>(12, resources);
		dataGridRequestsMadeByMe.setWidth("100%");
		dataGridRequestsMadeByMe.setHeight("100%");

		TextCell cell = new TextCell();

		Column<AccessRequest, String> requestDateColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				Date date = object.getRequestDate();
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}

		};
		requestDateColumn.setSortable(true);

		Column<AccessRequest, String> answerDateColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				Date date = object.getAnswerDate();
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}

		};
		answerDateColumn.setSortable(true);

		Column<AccessRequest, String> objectNameColumn = new Column<AccessRequest, String>(cell) {
			@Override
			public String getValue(AccessRequest object) {
				return object.getItemName();
			}
		};
		objectNameColumn.setSortable(true);

		Column<AccessRequest, String> userNameColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				return object.getRequestUserName();
			}
		};
		userNameColumn.setSortable(true);

		Column<AccessRequest, String> answerMessageColumn = new Column<AccessRequest, String>(cell) {

			@Override
			public String getValue(AccessRequest object) {
				return object.getAnswerInfo();
			}
		};

		Column<AccessRequest, String> statusColumn = new Column<AccessRequest, String>(cell) {
			@Override
			public String getValue(AccessRequest object) {
				return object.getAnswerOp().getAnswerName();
			}
		};
		statusColumn.setSortable(true);

		dataGridRequestsMadeByMe.addColumn(requestDateColumn, ToolsGWT.lblCnst.RequestAccess_Table_RequestDate());
		dataGridRequestsMadeByMe.addColumn(answerDateColumn, ToolsGWT.lblCnst.RequestAccess_Table_AnswerDate());
		dataGridRequestsMadeByMe.addColumn(statusColumn, ToolsGWT.lblCnst.RequestAccess_Table_Status());
		dataGridRequestsMadeByMe.addColumn(objectNameColumn, ToolsGWT.lblCnst.RequestAccess_Table_ObjectName());
		dataGridRequestsMadeByMe.addColumn(userNameColumn, ToolsGWT.lblCnst.RequestAccess_Table_AnswerUser());
		dataGridRequestsMadeByMe.addColumn(answerMessageColumn, ToolsGWT.lblCnst.RequestAccess_Table_AnswerMessage());

		dataGridRequestsMadeByMe.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.RequestAccess_Table_Empty()));

		sortRequestByMeHandler = new ListHandler<AccessRequest>(new ArrayList<AccessRequest>());
		sortRequestByMeHandler.setComparator(requestDateColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				if(o1.getRequestDate() == null) {
					return -1;
				}
				else if(o2.getRequestDate() == null) {
					return 1;
				}
				
				return o2.getRequestDate().before(o1.getRequestDate()) ? -1 : o2.getRequestDate().after(o1.getRequestDate()) ? 1 : 0;
			}
		});
		sortRequestByMeHandler.setComparator(answerDateColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				if(o1.getAnswerDate() == null) {
					return -1;
				}
				else if(o2.getAnswerDate() == null) {
					return 1;
				}
				
				return o2.getAnswerDate().before(o1.getAnswerDate()) ? -1 : o2.getAnswerDate().after(o1.getAnswerDate()) ? 1 : 0;
			}
		});
		sortRequestByMeHandler.setComparator(statusColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				return o1.getAnswerOp().getAnswerName().compareTo(o2.getAnswerOp().getAnswerName());
			}
		});
		sortRequestByMeHandler.setComparator(objectNameColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				return o1.getItemName().compareTo(o2.getItemName());
			}
		});
		sortRequestByMeHandler.setComparator(userNameColumn, new Comparator<AccessRequest>() {
			
			@Override
			public int compare(AccessRequest o1, AccessRequest o2) {
				return o1.getUserName().compareTo(o2.getUserName());
			}
		});
		dataGridRequestsMadeByMe.addColumnSortHandler(sortRequestByMeHandler);
		
		// Add a selection model so we can select cells.
		SelectionModel<AccessRequest> selectionModel = new SingleSelectionModel<AccessRequest>();
		dataGridRequestsMadeByMe.setSelectionModel(selectionModel);

		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGridRequestsMadeByMe);

		panelPagerByMe.setWidget(pager);

		return dataGridRequestsMadeByMe;
	}

	public void loadData() {
		showWaitPart(true);
		
		boolean showDemandHistory = btnShowHistoryDemands.getValue();

		if (btnShowRequests.getValue()) {
			panelRequestByMe.setVisible(false);
			panelPagerByMe.setVisible(false);

			panelRequestToMe.setVisible(true);
			panelPagerToMe.setVisible(true);

			AccessRequestService.Connect.getInstance().getMyAccessRequests(showDemandHistory, new AsyncCallback<List<AccessRequest>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					showWaitPart(false);

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());

					dataProvider.setList(new ArrayList<AccessRequest>());
					sortRequestToMeHandler.setList(dataProvider.getList());
				}

				@Override
				public void onSuccess(List<AccessRequest> result) {

					showWaitPart(false);

					dataProvider.setList(result);
					sortRequestToMeHandler.setList(dataProvider.getList());
				}
			});
		}
		else {
			panelRequestToMe.setVisible(false);
			panelPagerToMe.setVisible(false);

			panelRequestByMe.setVisible(true);
			panelPagerByMe.setVisible(true);

			AccessRequestService.Connect.getInstance().getMyAccessDemands(showDemandHistory, new AsyncCallback<List<AccessRequest>>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.Error());

					showWaitPart(false);

					dataProvider.setList(new ArrayList<AccessRequest>());
					sortRequestByMeHandler.setList(dataProvider.getList());
				}

				@Override
				public void onSuccess(List<AccessRequest> result) {

					showWaitPart(false);

					dataProvider.setList(result);
					sortRequestByMeHandler.setList(dataProvider.getList());
				}
			});
		}
	}

}
