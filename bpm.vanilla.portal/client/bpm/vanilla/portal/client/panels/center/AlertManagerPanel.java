package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.vanilla.platform.core.beans.alerts.Action.TypeAction;
import bpm.vanilla.platform.core.beans.alerts.ActionMail;
import bpm.vanilla.platform.core.beans.alerts.Alert;
import bpm.vanilla.platform.core.beans.alerts.Alert.TypeEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertKpi.KpiEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertRepository.ObjectEvent;
import bpm.vanilla.platform.core.beans.alerts.AlertSystem.SystemEvent;
import bpm.vanilla.platform.core.beans.alerts.Subscriber;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.services.AdminService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;
import bpm.vanilla.portal.client.wizard.AddAlertWizard;
import bpm.vanilla.portal.server.BiPortalServiceImpl;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class AlertManagerPanel extends Tab {

	private static AlertManagerPanelUiBinder uiBinder = GWT.create(AlertManagerPanelUiBinder.class);

	interface AlertManagerPanelUiBinder extends UiBinder<Widget, AlertManagerPanel> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
		String pager();
		String imgArrowDocument();
		String labelRowAlert();
		String textAlert();
		String imgDropdown();
		String childEvent();
		String childAlert();
		String cell();
		String selectedchildCell();
		String imgGrid();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel panelToolbar ;
	
	@UiField
	SimplePanel panelEventType, panelGridAlert, panelPager;
	
	@UiField
	CheckBox selectAll;

	private ListDataProvider<TypeEvent> eventDataProvider;
	private ListDataProvider<Alert> dataProvider;
	private MultiSelectionModel<TypeEvent> selectionModelTypeEvent;
	
	private DataGrid<TypeEvent> gridEvents;
	private DataGrid<Alert> gridAlerts;
	
	private ListHandler<Alert> sortHandler;
	
//	private Event selectedDocument;
	private List<String> selectedEvents;
	private List<String> showingSubEvents = new ArrayList<String>();
	private List<Alert> alerts;
	
	private List<String> showingSubscribers = new ArrayList<String>();

	public AlertManagerPanel(TabManager tabManager) {
		super(tabManager, ToolsGWT.lblCnst.AlertManagerTab(), true);
		this.add(uiBinder.createAndBindUi(this));
		
		gridEvents = createEventList();
		panelEventType.add(gridEvents);
		gridAlerts = createGridData();
		panelGridAlert.setWidget(gridAlerts);

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());
		
	    loadEvent();
	    selectAll.setValue(true);
	    
	    initJs(this);
	}
	
	private final native void initJs(AlertManagerPanel panel) /*-{
		var panel = panel;
		$wnd.expendEvent = function(id, index){
			panel.@bpm.vanilla.portal.client.panels.center.AlertManagerPanel::handleEventClick(Ljava/lang/String;Ljava/lang/String;)(id.toString(), index.toString());
		};
		
		$wnd.subClick = function(id, index){
			panel.@bpm.vanilla.portal.client.panels.center.AlertManagerPanel::handleSubEventClick(Ljava/lang/String;)(id.toString());
		};
		
		$wnd.showSubscribers = function(id, index){
			panel.@bpm.vanilla.portal.client.panels.center.AlertManagerPanel::handleAlertClick(Ljava/lang/String;Ljava/lang/String;)(id.toString(), index.toString());
		};
		
		$wnd.deleteSubscriber = function(id){
			panel.@bpm.vanilla.portal.client.panels.center.AlertManagerPanel::handleDeleteSubscriberClick(Ljava/lang/String;)(id.toString());
		};
	}-*/;
	
	private void loadEvent(){
		showWaitPart(true);
		
		AdminService.Connect.getInstance().getAllAlerts(new AsyncCallback<List<Alert>>() {
			
			@Override
			public void onSuccess(List<Alert> result) {
				showWaitPart(false);
				alerts = result;
				fillEvents(Arrays.asList(TypeEvent.values()));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedGetAlerts());
			}
		});
	}
	
	private DataGrid<TypeEvent> createEventList(){
		//EventDefinitionCell eventCell = new EventDefinitionCell();
		TextCell cell = new TextCell();
		
		final Column<TypeEvent, Boolean> checkboxColumn = new Column<TypeEvent, Boolean>(new CheckboxCell(true, true)) {

			@Override
			public Boolean getValue(TypeEvent object) {
				return selectionModelTypeEvent.isSelected(object);
			}
		};
		
		checkboxColumn.setFieldUpdater(new FieldUpdater<TypeEvent, Boolean>() {
			
			@Override
			public void update(int index, TypeEvent object, Boolean value) {
				
				selectionModelTypeEvent.setSelected(object, value);	
			}
		});
		
		final Column<TypeEvent, String> columnName = new Column<TypeEvent, String>(cell) {

			@Override
			public String getValue(TypeEvent object) {
				return object.getLabel();
			}
		};
		
		ImageCell imageCell = new ImageCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				if (value != null) {
					sb.appendHtmlConstant("<img src = '" + PortalImage.INSTANCE.ic_arrow_drop_down_black_36dp().getSafeUri().asString() + "' height = '18px' width = '18px' class='" + style.imgDropdown() + "' onclick='javascript:;'/>");
				}
			}
		};

		final Column<TypeEvent, String> expendEvent = new Column<TypeEvent, String>(imageCell) {

			@Override
			public String getValue(TypeEvent object) {
					return object.toString();
			}
		};
		
		// Add a selection model so we can select cells.
		selectionModelTypeEvent = new MultiSelectionModel<TypeEvent>();
		selectionModelTypeEvent.addSelectionChangeHandler(selectionChangeHandler);

		DataGrid.Resources resources = new CustomResources();
		final DataGrid<TypeEvent> listEvent = new DataGrid<TypeEvent>(1000, resources);
		listEvent.addColumn(checkboxColumn);
		listEvent.addColumn(columnName);
		listEvent.addColumn(expendEvent);
		listEvent.setColumnWidth(checkboxColumn, 15.0, Unit.PCT);
		listEvent.setColumnWidth(columnName, 70.0, Unit.PCT);
		listEvent.setColumnWidth(expendEvent, 15.0, Unit.PCT);
		listEvent.setSize("100%", "100%");
		listEvent.setSelectionModel(selectionModelTypeEvent);
		
		eventDataProvider = new ListDataProvider<TypeEvent>();
		eventDataProvider.addDataDisplay(listEvent);
		
		/**
		 * Renders the data rows that display each contact in the table.
		 */
		class CustomTableBuilder extends AbstractCellTableBuilder<TypeEvent> {

			private final String childCell = " " + style.childEvent();
			private final String selectedchildCell = " " + style.selectedchildCell();
			private final String rowStyle;
			private final String selectedRowStyle;
			private final String cellStyle;
			private final String selectedCellStyle;

			public CustomTableBuilder() {
				super(listEvent);

				// Cache styles for faster access.
				Style styleGrid = listEvent.getResources().style();
				rowStyle = styleGrid.evenRow();
				selectedRowStyle = " " + styleGrid.selectedRow();
				cellStyle = styleGrid.cell() + " " + styleGrid.evenRowCell();
				selectedCellStyle = " " + styleGrid.selectedRowCell();
			}

			@Override
			public void buildRowImpl(TypeEvent rowValue, int absRowIndex) {
				buildEventRow(rowValue, absRowIndex);

				// Display list of scripts.
				if (showingSubEvents.contains(rowValue.toString())) {
					switch(rowValue){
					case OBJECT_TYPE:
						for(ObjectEvent ev2 : ObjectEvent.values()){
							buildSubRow(ev2.toString(), ev2.getLabel(), absRowIndex);
						}
						break;
					case SYSTEM_TYPE:
						for(SystemEvent ev2 : SystemEvent.values()){
							buildSubRow(ev2.toString(), ev2.getLabel(), absRowIndex);
						}
						break;
					case KPI_TYPE:
						for(KpiEvent ev2 : KpiEvent.values()){
							buildSubRow(ev2.toString(), ev2.getLabel(), absRowIndex);
						}
						break;
					default:
						break;
					}
				}
			}

			private void buildEventRow(TypeEvent rowValue, int absRowIndex) {
				// Calculate the row styles.
				SelectionModel<? super TypeEvent> selectionModel = listEvent.getSelectionModel();
				boolean isSelected = (selectionModel == null || rowValue == null) ? false : selectionModel.isSelected(rowValue);
				StringBuilder trClasses = new StringBuilder(rowStyle+ " " /*+ AlertManagerPanel.this.style.mainRow()*/);
				if (isSelected) {
					trClasses.append(selectedRowStyle);
				}

				// Calculate the cell styles.
				String cellStyles = cellStyle  + " " + style.cell();
				if (isSelected) {
					cellStyles += selectedCellStyle;
				}

				TableRowBuilder row = startRow();

				row.className(trClasses.toString());
//				row.attribute("onclick", "setTimeout(function(){clickProject(" + rowValue.getId() + "," + absRowIndex + ");},100)");

				// Checbox column.
				TableCellBuilder td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(0), checkboxColumn, rowValue);

				td.endTD();

				// Name column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(1), columnName, rowValue);

				td.endTD();

				// Button column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				//renderCell(td, createContext(2), expendEvent, rowValue);
				td.startImage().alt(ToolsGWT.lblCnst.SubType()+"s").title(ToolsGWT.lblCnst.SubType()+"s").height(18).width(18)
				.src(PortalImage.INSTANCE.ic_arrow_drop_down_black_36dp().getSafeUri().asString()).className(style.imgDropdown())
				.attribute("onclick","expendEvent('" + rowValue.toString() + "'," + absRowIndex + ");").endImage();
				td.endTD();

				row.endTR();
			}

			private void buildSubRow(String rowValue, String label, int absRowIndex) {
				// Calculate the row styles.

				boolean isSelected = selectedEvents.contains(rowValue);

				StringBuilder trClasses = new StringBuilder(rowStyle);

				// Calculate the cell styles.
				String cellStyles = cellStyle;
				if (isSelected) {
					cellStyles += selectedchildCell;
				}
				else {
					cellStyles += childCell;
				}

				TableRowBuilder row = startRow();
				row.className(trClasses.toString());

				// checbox column.
				TableCellBuilder td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				if(isSelected){
					td.startCheckboxInput().checked().attribute("onchange","subClick('" + rowValue.toString() + "');").endInput();
				} else {
					td.startCheckboxInput().attribute("onchange","subClick('" + rowValue.toString() + "');").endInput();
				}

				td.endTD();

				// Name column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text(label);

				td.endTD();

				// button column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();

				row.endTR();
			}
		}
		listEvent.setTableBuilder(new CustomTableBuilder());
		
		
		
		return listEvent;
	}
	
	private void fillEvents(List<TypeEvent> events){
		eventDataProvider.setList(events != null ? events : new ArrayList<TypeEvent>());
		onSelectAllClick(null);
	}

	private void fillAlerts(List<String> subEvents){
		List<Alert> filtered = new ArrayList<Alert>();

		for(Alert alert: alerts){
			if(alert.getEventObject() != null && subEvents.contains(alert.getEventObject().getSubtypeEventName())){
				filtered.add(alert);
			}
		}
		dataProvider.setList(filtered);
		sortHandler.setList(dataProvider.getList());
	}
	
	private DataGrid<Alert> createGridData() {
		CustomCell cell = new CustomCell();
		final Column<Alert, String> nameColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);
		
		final Column<Alert, String> descColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				return object.getDescription();
			}
		};
		descColumn.setSortable(true);
	    
		final Column<Alert, String> typeActionColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				if(object.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.GATEWAY){
					return ToolsGWT.lblCnst.Gateway();
				}
				else if(object.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.MAIL){
					return ToolsGWT.lblCnst.Mail();
				}
				else if(object.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.WORKFLOW){
					return ToolsGWT.lblCnst.Workflow();
				}
				return String.valueOf(object.getTypeAction());
			}
		};
		typeActionColumn.setSortable(true);
		
		final Column<Alert, String> typeEventColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				return object.getEventObject().getSubtypeEventLabel();
			}
		};
		typeEventColumn.setSortable(true);
	    
		final Column<Alert, String> stateColumn = new Column<Alert, String>(cell) {

			@Override
			public String getValue(Alert object) {
				if(object.getState() == 1){
					return ToolsGWT.lblCnst.Active();
				}
				else {
					return ToolsGWT.lblCnst.Inactive();
				}
			}
		};
		stateColumn.setSortable(true);
		
		ButtonImageCell editCell = new ButtonImageCell(CommonImages.INSTANCE.ic_edit_black_18dp(), style.imgGrid());
		final Column<Alert, String> colEdit = new Column<Alert, String>(editCell) {

			@Override
			public String getValue(Alert object) {
				return "";
			}
		};
		colEdit.setFieldUpdater(new FieldUpdater<Alert, String>() {

			@Override
			public void update(int index, Alert object, String value) {
				final AddAlertWizard wiz = new AddAlertWizard(object);
				wiz.center();
				wiz.addCloseHandler(new CloseHandler<PopupPanel>() {		
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(wiz.isSuccess()) onRefreshClick(null);
					}
				});
			}
		});

		ButtonImageCell deleteCell = new ButtonImageCell(CommonImages.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		final Column<Alert, String> colDelete = new Column<Alert, String>(deleteCell) {

			@Override
			public String getValue(Alert object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<Alert, String>() {

			@Override
			public void update(int index, final Alert object, String value) {
				final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.DeleteAlertConfirm(), true);
				dial.addCloseHandler(new CloseHandler<PopupPanel>() {

					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (dial.isConfirm()) {
							deleteAlert(object);
						}
					}
				});
				dial.center();
			}
		});
		
		ImageCell subscribersCell = new ImageCell() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
				if (value != null && value.equals(TypeAction.MAIL.toString())) {
					sb.appendHtmlConstant("<img src = '" + CommonImages.INSTANCE.ic_group_black_18dp().getSafeUri().asString() + "'  class='" + style.imgGrid() + "' onclick='javascript:;'/>");
				}
			}
		};
		final Column<Alert, String> colSubscribers = new Column<Alert, String>(subscribersCell) {

			@Override
			public String getValue(Alert object) {
				return object.getTypeAction().toString();
			}
		};

		DataGrid.Resources resources = new CustomResources();
		final DataGrid<Alert> dataGrid = new DataGrid<Alert>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.AlertName());
		dataGrid.addColumn(descColumn, ToolsGWT.lblCnst.Description());
		dataGrid.addColumn(typeActionColumn, ToolsGWT.lblCnst.TypeAction());
		dataGrid.addColumn(typeEventColumn, ToolsGWT.lblCnst.TypeEvent());
		dataGrid.addColumn(stateColumn, ToolsGWT.lblCnst.State());
		dataGrid.addColumn(colSubscribers,"");
		dataGrid.addColumn(colEdit,"");
		dataGrid.addColumn(colDelete, "");
		dataGrid.setColumnWidth(nameColumn, 17, Unit.PCT);
		dataGrid.setColumnWidth(descColumn, 17, Unit.PCT);
		dataGrid.setColumnWidth(typeActionColumn, 17, Unit.PCT);
		dataGrid.setColumnWidth(typeEventColumn, 17, Unit.PCT);
		dataGrid.setColumnWidth(stateColumn, 17, Unit.PCT);
		dataGrid.setColumnWidth(colSubscribers,5, Unit.PCT);
		dataGrid.setColumnWidth(colEdit,5, Unit.PCT);
		dataGrid.setColumnWidth(colDelete, 5, Unit.PCT);
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

	    dataProvider = new ListDataProvider<Alert>();
	    dataProvider.addDataDisplay(dataGrid);
	    
	    sortHandler = new ListHandler<Alert>(new ArrayList<Alert>());
		sortHandler.setComparator(nameColumn, new Comparator<Alert>() {
			
			@Override
			public int compare(Alert o1, Alert o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(typeActionColumn, new Comparator<Alert>() {
			
			@Override
			public int compare(Alert o1, Alert o2) {
				String action1 = "";
				String action2 = "";
				
				if(o1.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.GATEWAY){
					action1 = ToolsGWT.lblCnst.Gateway();
				}
				else if(o1.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.MAIL){
					action1 = ToolsGWT.lblCnst.Mail();
				}
				else if(o1.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.WORKFLOW){
					action1 = ToolsGWT.lblCnst.Workflow();
				}
				
				if(o2.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.GATEWAY){
					action2 = ToolsGWT.lblCnst.Gateway();
				}
				else if(o2.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.MAIL){
					action2 = ToolsGWT.lblCnst.Mail();
				}
				else if(o2.getTypeAction() == bpm.vanilla.platform.core.beans.alerts.Action.TypeAction.WORKFLOW){
					action2 = ToolsGWT.lblCnst.Workflow();
				}
				
				return action1.compareTo(action2);
			}
		});
		sortHandler.setComparator(stateColumn, new Comparator<Alert>() {
			
			@Override
			public int compare(Alert o1, Alert o2) {
				String state1 = "";
				String state2 = "";
				
				if(o1.getState() == 1){
					state1 = ToolsGWT.lblCnst.Active();
				}
				else {
					state1 = ToolsGWT.lblCnst.Inactive();
				}
				
				if(o2.getState() == 1){
					state2 = ToolsGWT.lblCnst.Active();
				}
				else {
					state2 = ToolsGWT.lblCnst.Inactive();
				}

				return state1.compareTo(state2);
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);
	    
	    // Add a selection model so we can select cells.
	    SelectionModel<Alert> selectionModel = new SingleSelectionModel<Alert>();
	    dataGrid.setSelectionModel(selectionModel);
		
		// Create a Pager to control the table.
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName(style.pager());
	    pager.setDisplay(dataGrid);
	    
	    panelPager.setWidget(pager);
	    
	    /**
		 * Renders the data rows that display each contact in the table.
		 */
		class CustomTableBuilder extends AbstractCellTableBuilder<Alert> {

			private final String childCell = " " + style.childAlert();
			private final String selectedchildCell = " " + style.selectedchildCell();
			private final String rowStyle;
			private final String selectedRowStyle;
			private final String cellStyle;
			private final String selectedCellStyle;

			public CustomTableBuilder() {
				super(dataGrid);

				// Cache styles for faster access.
				Style styleGrid = dataGrid.getResources().style();
				rowStyle = styleGrid.evenRow();
				selectedRowStyle = " " + styleGrid.selectedRow();
				cellStyle = styleGrid.cell() + " " + styleGrid.evenRowCell();
				selectedCellStyle = " " + styleGrid.selectedRowCell();
			}

			@Override
			public void buildRowImpl(Alert rowValue, int absRowIndex) {
				buildAlertRow(rowValue, absRowIndex);

				// Display list of scripts.
				if (showingSubscribers.contains(rowValue.getId()+"")){
					ActionMail act = (ActionMail) rowValue.getAction().getActionObject();
					for(Subscriber sub : act.getSubscribers()){
						buildSubRow(sub, absRowIndex);
					}
				}
			}

			private void buildAlertRow(Alert rowValue, int absRowIndex) {
				// Calculate the row styles.
				SelectionModel<? super Alert> selectionModel = dataGrid.getSelectionModel();
				boolean isSelected = (selectionModel == null || rowValue == null) ? false : selectionModel.isSelected(rowValue);
				StringBuilder trClasses = new StringBuilder(rowStyle+ " " /*+ AlertManagerPanel.this.style.mainRow()*/);
				if (isSelected) {
					trClasses.append(selectedRowStyle);
				}

				// Calculate the cell styles.
				String cellStyles = cellStyle;
				if (isSelected) {
					cellStyles += selectedCellStyle;
				}

				TableRowBuilder row = startRow();

				row.className(trClasses.toString());

				// name column.
				TableCellBuilder td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(0), nameColumn, rowValue);
				td.endTD();

				// desc column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(1), descColumn, rowValue);
				td.endTD();
				
				// typeaction column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(2), typeActionColumn, rowValue);
				td.endTD();

				// typeevent column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(3), typeEventColumn, rowValue);
				td.endTD();

				// state column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(4), stateColumn, rowValue);
				td.endTD();
				
				// subscribers column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				if (rowValue.getTypeAction() != null && rowValue.getTypeAction().equals(TypeAction.MAIL)) {
					td.startImage().alt(ToolsGWT.lblCnst.Subscribers()).title(ToolsGWT.lblCnst.Subscribers())
					.src(CommonImages.INSTANCE.ic_group_black_18dp().getSafeUri().asString())
					.className(style.imgGrid())
					.attribute("onclick","showSubscribers('" + rowValue.getId() + "'," + absRowIndex + ");").endImage();
				}
				td.endTD();
				
				// edit column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(6), colEdit, rowValue);
				td.endTD();

				// delete column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				renderCell(td, createContext(7), colDelete, rowValue);
				td.endTD();

				

				row.endTR();
			}

			private void buildSubRow(Subscriber rowValue, int absRowIndex) {
				// Calculate the row styles.

				boolean isSelected = false;

				StringBuilder trClasses = new StringBuilder(rowStyle);

				// Calculate the cell styles.
				String cellStyles = cellStyle;
				if (isSelected) {
					cellStyles += selectedchildCell;
				}
				else {
					cellStyles += childCell;
				}

				TableRowBuilder row = startRow();
				row.className(trClasses.toString());

				// name column.
				TableCellBuilder td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text(rowValue.getUserName()).endTD();

				// desc column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text(rowValue.getUserMail()).endTD();
				
				// typeaction column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.startImage().alt(ToolsGWT.lblCnst.Delete()).title(ToolsGWT.lblCnst.Delete())
				.src(CommonImages.INSTANCE.ic_delete_black_18dp().getSafeUri().asString())
				.className(style.imgGrid())
				.attribute("onclick","deleteSubscriber('" + rowValue.getId() + "');").endImage();
				td.endTD();
				
				// typeevent column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();
				
				// state column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();
				
				// edit column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();
				
				// delete column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();

				// subscribers column.
				td = row.startTD();
				td.className(cellStyles);
				td.style().outlineStyle(OutlineStyle.NONE).endStyle();
				td.text("").endTD();

				row.endTR();
			}
		}
		dataGrid.setTableBuilder(new CustomTableBuilder());
	    
	    return dataGrid;
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		loadEvent();
	}
	
	@UiHandler("btnAdd")
	public void onAddAlertClick(ClickEvent event) {
		final AddAlertWizard wiz = new AddAlertWizard();
		wiz.center();
		wiz.addCloseHandler(new CloseHandler<PopupPanel>() {		
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if(wiz.isSuccess()) onRefreshClick(null);
			}
		});
	}
	
	@UiHandler("selectAll")
	public void onSelectAllClick(ClickEvent event) {
		if(selectAll.getValue()){
			selectionModelTypeEvent.setSelected(TypeEvent.SYSTEM_TYPE, true);
			selectionModelTypeEvent.setSelected(TypeEvent.OBJECT_TYPE, true);
			selectionModelTypeEvent.setSelected(TypeEvent.KPI_TYPE, true);
		} else {
			selectionModelTypeEvent.setSelected(TypeEvent.SYSTEM_TYPE, false);
			selectionModelTypeEvent.setSelected(TypeEvent.OBJECT_TYPE, false);
			selectionModelTypeEvent.setSelected(TypeEvent.KPI_TYPE, false);
		}
		selectionChangeHandler.onSelectionChange(null);
	}
	
	private Handler selectionChangeHandler = new Handler() {
		
		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			List<TypeEvent> events = new ArrayList<Alert.TypeEvent>(selectionModelTypeEvent.getSelectedSet());
			List<String> subEvents = new ArrayList<String>();
			for(TypeEvent ev : events){
				switch(ev){
				case OBJECT_TYPE:
					for(ObjectEvent ev2 : ObjectEvent.values()){
						subEvents.add(ev2.toString());
					}
					break;
				case SYSTEM_TYPE:
					for(SystemEvent ev2 : SystemEvent.values()){
						subEvents.add(ev2.toString());
					}
					break;
				case KPI_TYPE:
					for(KpiEvent ev2 : KpiEvent.values()){
						subEvents.add(ev2.toString());
					}
					break;
				default:
					break;
				}
			}
			selectedEvents = subEvents;
			fillAlerts(selectedEvents);
		}
	};
	
	public void dispatchAction(final Alert obj, NativeEvent event){
		if(event.getType().equals("dblclick")){
			
		}
		else if(event.getButton() == NativeEvent.BUTTON_RIGHT){
			
		}
	}
	
//	private class EventDefinitionCell extends AbstractCell<Event> {
//
//		@Override
//		public void render(com.google.gwt.cell.client.Cell.Context context, Event value, SafeHtmlBuilder sb) {
//			String doubleArrowUrl = GWT.getHostPageBaseURL() + "images/documentManager/double_arrow.png";
//			
//			sb.appendHtmlConstant("<div class='" + style.imgArrowDocument() + "'>");
//			sb.appendHtmlConstant("<div class='" + style.textAlert() + "'>");
//			sb.appendEscaped(value.getName());
//			sb.appendHtmlConstant("<br/>");
//			sb.appendHtmlConstant(getTypeEvent(value));
//			sb.appendHtmlConstant("</div>");
//		    sb.appendHtmlConstant("<img src='" + doubleArrowUrl + "' class='" + style.labelRowAlert() + "' />");
//			sb.appendHtmlConstant("</div>");
//		}
//	}
	
//	private String getTypeEvent(Event ev){
//		String typeEvent = "<b>" + ToolsGWT.lblCnst.TypeEvent() + " : </b>";
//		if(ev.getTypeEvent() == Event.EVENT_OBJECT_TYPE){
//			if(ev.getSubtypeEvent() == Event.EVENT_OBJECT_BIRT_SUBTYPE){
//				return typeEvent + ToolsGWT.lblCnst.BirtReport();
//			}
//			else if(ev.getSubtypeEvent() == Event.EVENT_OBJECT_FWR_SUBTYPE){
//				return typeEvent + ToolsGWT.lblCnst.FreeWebReport();
//			}
//			else if(ev.getSubtypeEvent() == Event.EVENT_OBJECT_FWR_SUBTYPE){
//				return typeEvent + ToolsGWT.lblCnst.Gateway();
//			}
//		}
//		else if(ev.getTypeEvent() == Event.EVENT_SYSTEM_TYPE){
//			if(ev.getSubtypeEvent() == Event.EVENT_SYSTEM_CONNECT_TO_PORTAL_SUBTYPE){
//				return typeEvent + ToolsGWT.lblCnst.ConnectToPortal();
//			}
//			else if(ev.getSubtypeEvent() == Event.EVENT_SYSTEM_DECONNECT_FROM_PORTAL_SUBTYPE){
//				return typeEvent + ToolsGWT.lblCnst.DisconnectFromPortal();
//			}
//		}
//		return ToolsGWT.lblCnst.Unknown();
//	}
	
	private class CustomCell extends TextCell {
		
		public CustomCell() {
			super();
		}
		
		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			consumedEvents.add("contextmenu");
			return consumedEvents;
		}
		
		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			Alert obj = (Alert)context.getKey();
			dispatchAction(obj, event);
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
	
	public void handleEventClick(String ev, String index) {
		if (showingSubEvents.contains(ev)) {
//			showingSubEvents.clear();
			showingSubEvents.remove(ev);
		}
		else {
//			showingSubEvents.clear();
			showingSubEvents.add(ev);
		}
		
		gridEvents.redrawRow(Integer.parseInt(index));
//		datagrid.redraw();

	}
	
	public void handleSubEventClick(String ev) {
		if (selectedEvents.contains(ev)) {
			selectedEvents.remove(ev);
		}
		else {
			selectedEvents.add(ev);
		}
		fillAlerts(selectedEvents);
	}
	
	public void handleAlertClick(String id, String index) {
		if (showingSubscribers.contains(id)) {
			showingSubscribers.remove(id);
		}
		else {
			showingSubscribers.add(id);
		}
		
		gridAlerts.redrawRow(Integer.parseInt(index));
	}
	
	public void handleDeleteSubscriberClick(String id) {
		LOOP:for(Alert alert : alerts){
			if(alert.getTypeAction().equals(TypeAction.MAIL)){
				for(final Subscriber sub : ((ActionMail)alert.getAction().getActionObject()).getSubscribers()){
					if(sub.getId() == Integer.parseInt(id)){
						final InformationsDialog dial = new InformationsDialog(ToolsGWT.lblCnst.Information(), ToolsGWT.lblCnst.Ok(), ToolsGWT.lblCnst.Cancel(), ToolsGWT.lblCnst.DeleteSubscriberConfirm(), true);
						dial.addCloseHandler(new CloseHandler<PopupPanel>() {

							@Override
							public void onClose(CloseEvent<PopupPanel> event) {
								if (dial.isConfirm()) {
									deleteSubscriber(sub);
								}
							}
						});
						dial.center();
						break LOOP;
					}
				}
			}
		}
	}
	
	public void deleteAlert(Alert alert) {
		BiPortalServiceImpl.Connect.getInstance().deleteAlert(alert, new GwtCallbackWrapper<Void>(this, true) {
			
			@Override
			public void onSuccess(Void result) {
				showWaitPart(false);
				loadEvent();
			}
			
		}.getAsyncCallback());
	}
	
	public void deleteSubscriber(Subscriber sub) {
		BiPortalServiceImpl.Connect.getInstance().deleteSubscriber(sub, new GwtCallbackWrapper<Void>(this, true) {
			
			@Override
			public void onSuccess(Void result) {
				showWaitPart(false);
				loadEvent();
			}
			
		}.getAsyncCallback());
	}
}
