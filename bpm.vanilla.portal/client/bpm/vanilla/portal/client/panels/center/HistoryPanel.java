package bpm.vanilla.portal.client.panels.center;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.repository.ReportHistoryDTO;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.services.HistoryService;
import bpm.vanilla.portal.client.utils.ToolsGWT;
import bpm.vanilla.portal.client.widget.custom.CustomResources;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class HistoryPanel extends Tab {

	private static final String DATE_FORMAT = "HH:mm - dd/MM/yyyy";

	private static HistoryPanelUiBinder uiBinder = GWT.create(HistoryPanelUiBinder.class);

	interface HistoryPanelUiBinder extends UiBinder<Widget, HistoryPanel> {
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
	SimplePanel panelContent, panelPager;
	
	@UiField
	DateBox dateBoxFrom, dateBoxTo;
	
	@UiField
	Image btnClear;

	@UiField
	TextBox txtSearch;
	
	private ContentDisplayPanel mainPanel;
	private ListDataProvider<ReportHistoryDTO> dataProvider;
	private ListHandler<ReportHistoryDTO> sortHandler;

	private List<ReportHistoryDTO> result;
	
	private PortailRepositoryItem item;

	public HistoryPanel(ContentDisplayPanel mainPanel, PortailRepositoryItem item, String tabName) {
		super(mainPanel, ToolsGWT.lblCnst.History() + tabName, true);
		this.add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.item = item;

		panelContent.add(createGridData(item != null));
		
		btnClear.setVisible(false);

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
		this.addStyleName(style.mainPanel());

		if (item != null) {
			fetchData();
		}
		else {
			loadResult(new ArrayList<ReportHistoryDTO>());
		}
	}

	public void fetchData() {
		if (item != null) {
			getItemHistory(item, null, null);
		}
		else {
			List<Integer> reportList = getValidObjects();
			getAllHistory(reportList, null, null);
		}
	}

	private void loadResult(List<ReportHistoryDTO> result) {
		if (result == null) {
			dataProvider.setList(new ArrayList<ReportHistoryDTO>());
		}
		else {
			dataProvider.setList(result);
		}
		sortHandler.setList(dataProvider.getList());
	}

	private void getAllHistory(List<Integer> reportList, final Date from, final Date to) {
		showWaitPart(true);

		HistoryService.Connect.getInstance().getHistoryForItemList(reportList, from, to, new AsyncCallback<List<ReportHistoryDTO>>() {

			public void onSuccess(List<ReportHistoryDTO> history) {
				showWaitPart(false);

				if (history == null) {
					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.FailedGetHistory());
				}
				else {
					result = history;
					loadResult(history);
				}
			}

			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedGetHistory());
			}
		});
	}

	private void getItemHistory(PortailRepositoryItem dto, Date from, Date to) {
		showWaitPart(true);

		HistoryService.Connect.getInstance().getAllHistory(dto.getId(), from, to, new AsyncCallback<List<ReportHistoryDTO>>() {

			public void onSuccess(List<ReportHistoryDTO> history) {
				showWaitPart(false);

				if (history == null) {
					MessageHelper.openMessageDialog(ToolsGWT.lblCnst.Error(), ToolsGWT.lblCnst.FailedGetHistory());
				}
				else {
					result = history;
					loadResult(history);
				}
			}

			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.FailedGetHistory());
			}
		});
	}

	private List<Integer> getValidObjects() {
		List<Integer> list = new ArrayList<Integer>();

		PortailRepositoryDirectory ss = biPortal.get().getContentRepository();
		if (ss != null) {
			for (Object o : ss.getItems()) {
				if (o instanceof PortailRepositoryDirectory) {
					PortailRepositoryDirectory dirDto = (PortailRepositoryDirectory) o;
					recFindInDir(dirDto, list);
				}
			}
		}
		return list;
	}

	/**
	 * Recursive!
	 * 
	 * return a list of dirItemIds of available reports
	 * 
	 * @param dirDto
	 * @param list
	 */
	private void recFindInDir(PortailRepositoryDirectory dirDto, List<Integer> list) {
		if(dirDto.getItems() != null) {
			for (Object o : dirDto.getItems()) {
				if (o instanceof PortailRepositoryDirectory) {
					PortailRepositoryDirectory dto = (PortailRepositoryDirectory) o;
					recFindInDir(dto, list);
				}
				else if (o instanceof PortailRepositoryItem) {
					PortailRepositoryItem item = (PortailRepositoryItem) o;
					if (item.isReport()) {
						list.add(item.getId());
					}
				}
			}
		}
	}
	
	private void filterResult(){
		List<ReportHistoryDTO> filterHistory = new ArrayList<ReportHistoryDTO>();
		
		String search = txtSearch.getText();
		if(result != null){
			for(ReportHistoryDTO history : result){
				if(history.getModelName().toLowerCase().contains(search.toLowerCase()) || history.getHistoryName().toLowerCase().contains(search.toLowerCase())){
					filterHistory.add(history);
				}
			}
		}
		
		dataProvider.setList(filterHistory);
	}

	private DataGrid<ReportHistoryDTO> createGridData(boolean fromItem) {
		final DateTimeFormat dateFormatter = DateTimeFormat.getFormat(DATE_FORMAT);

		CustomCell cell = new CustomCell();
		Column<ReportHistoryDTO, String> modelNameColumn = new Column<ReportHistoryDTO, String>(cell) {

			@Override
			public String getValue(ReportHistoryDTO object) {
				return object.getModelName();
			}
		};
		modelNameColumn.setSortable(true);

		Column<ReportHistoryDTO, String> nameColumn = new Column<ReportHistoryDTO, String>(cell) {

			@Override
			public String getValue(ReportHistoryDTO object) {
				return object.getHistoryName();
			}
		};
		nameColumn.setSortable(true);

		Column<ReportHistoryDTO, String> creationDateColumn = new Column<ReportHistoryDTO, String>(cell) {

			@Override
			public String getValue(ReportHistoryDTO object) {
				Date date = object.getCreationDate();
				if (date == null || date.equals(new Date(0))) {
					return ToolsGWT.lblCnst.Never();
				}
				return dateFormatter.format(date);
			}

		};
		creationDateColumn.setSortable(true);

		Column<ReportHistoryDTO, String> formatColumn = new Column<ReportHistoryDTO, String>(cell) {

			@Override
			public String getValue(ReportHistoryDTO object) {
				return object.getHistoryFormat();
			}
		};
		formatColumn.setSortable(true);

		Column<ReportHistoryDTO, String> summaryColumn = new Column<ReportHistoryDTO, String>(cell) {

			@Override
			public String getValue(ReportHistoryDTO object) {
				return object.getHistorySummary();
			}
		};
		summaryColumn.setSortable(true);

		DataGrid.Resources resources = new CustomResources();
		DataGrid<ReportHistoryDTO> dataGrid = new DataGrid<ReportHistoryDTO>(12, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		if(fromItem) {
			dataGrid.addColumn(modelNameColumn, ToolsGWT.lblCnst.HistoricModelName());
		}
		dataGrid.addColumn(nameColumn, ToolsGWT.lblCnst.HistoricName());
		dataGrid.addColumn(creationDateColumn, ToolsGWT.lblCnst.HistoricDateCreation());
		dataGrid.addColumn(formatColumn, ToolsGWT.lblCnst.HistoricFormat());
		dataGrid.addColumn(summaryColumn, ToolsGWT.lblCnst.Summary());
		dataGrid.setEmptyTableWidget(new Label(ToolsGWT.lblCnst.NoResult()));

		dataProvider = new ListDataProvider<ReportHistoryDTO>();
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<ReportHistoryDTO>(new ArrayList<ReportHistoryDTO>());
		sortHandler.setComparator(modelNameColumn, new Comparator<ReportHistoryDTO>() {
			
			@Override
			public int compare(ReportHistoryDTO o1, ReportHistoryDTO o2) {
				return o1.getModelName().compareTo(o2.getModelName());
			}
		});
		sortHandler.setComparator(nameColumn, new Comparator<ReportHistoryDTO>() {
			
			@Override
			public int compare(ReportHistoryDTO o1, ReportHistoryDTO o2) {
				return o1.getHistoryName().compareTo(o2.getHistoryName());
			}
		});
		sortHandler.setComparator(creationDateColumn, new Comparator<ReportHistoryDTO>() {
			
			@Override
			public int compare(ReportHistoryDTO o1, ReportHistoryDTO o2) {
				if(o1.getCreationDate() == null) {
					return -1;
				}
				else if(o2.getCreationDate() == null) {
					return 1;
				}
				
				return o2.getCreationDate().before(o1.getCreationDate()) ? -1 : o2.getCreationDate().after(o1.getCreationDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(formatColumn, new Comparator<ReportHistoryDTO>() {
			
			@Override
			public int compare(ReportHistoryDTO o1, ReportHistoryDTO o2) {
				return o1.getHistoryFormat().compareTo(o2.getHistoryFormat());
			}
		});
		sortHandler.setComparator(summaryColumn, new Comparator<ReportHistoryDTO>() {
			
			@Override
			public int compare(ReportHistoryDTO o1, ReportHistoryDTO o2) {
				return o1.getHistorySummary().compareTo(o2.getHistorySummary());
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);

		SelectionModel<ReportHistoryDTO> selectionModel = new SingleSelectionModel<ReportHistoryDTO>();
		dataGrid.setSelectionModel(selectionModel);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		panelPager.setWidget(pager);

		return dataGrid;
	}

	@UiHandler("btnRefresh")
	public void onRefreshClick(ClickEvent event) {
		fetchData();
		clearSearch();
	}

	@UiHandler("btnSearchDate")
	public void onSearchDateClick(ClickEvent event) {
		clearSearch();
		if(item != null){
			getItemHistory(item, dateBoxFrom.getValue(), dateBoxTo.getValue());
		}
		else {
			List<Integer> reportList = getValidObjects();
			getAllHistory(reportList, dateBoxFrom.getValue(), dateBoxTo.getValue());
		}
	}

	@UiHandler("btnSearch")
	public void onSearchClick(ClickEvent event) {
		if(!txtSearch.getText().isEmpty()) {
			btnClear.setVisible(true);
			
			filterResult();
		}
	}
	
	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		clearSearch();
		
		loadResult(result);
	}
	
	private void clearSearch() {
		txtSearch.setText("");
		
		btnClear.setVisible(false);
	}

	private class CustomCell extends TextCell {

		public CustomCell() {
			super();
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>();
			consumedEvents.add("dblclick");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			ReportHistoryDTO item = (ReportHistoryDTO) context.getKey();
			if (event.getType().equals("dblclick")) {
				showWaitPart(true);

				HistoryService.Connect.getInstance().getHistoricUrl(item, new AsyncCallback<DisplayItem>() {

					public void onSuccess(DisplayItem item) {
						showWaitPart(false);

						mainPanel.openViewer(item);
					}

					public void onFailure(Throwable caught) {
						showWaitPart(false);

						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, "No History");
					}
				});
			}
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
}
