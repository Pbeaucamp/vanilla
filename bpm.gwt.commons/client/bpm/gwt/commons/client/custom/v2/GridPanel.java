package bpm.gwt.commons.client.custom.v2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.utils.GlobalCSS;
import bpm.gwt.commons.client.utils.ToolsGWT;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Improve datagrid of type <T> with action button on top and pager on bottom.
 * 
 * You can also access helper method to add column or for comparaison
 *
 * @param <T>
 */
public class GridPanel<T> extends Composite {

	private static final int DEFAULT_PAGE_SIZE = 30;

	private static GridPanelUiBinder uiBinder = GWT.create(GridPanelUiBinder.class);

	interface GridPanelUiBinder extends UiBinder<Widget, GridPanel<?>> {
	}

	interface MyStyle extends CssResource {
		String btnAction();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelAction;

	@UiField
	Button btnFilter;

	@UiField
	Label lblAction;

	@UiField
	HTMLPanel panelButtonAction, panelBottom;

	@UiField
	SimplePanel panelActionWidget, panelGridContent, panelGrid, panelPager;

	@UiField
	TextBox txtPageSize;

	private DateTimeFormat df = DateTimeFormat.getFormat(ToolsGWT.DEFAULT_DATE_FORMAT);
	private DateTimeFormat dtf = DateTimeFormat.getFormat(ToolsGWT.DEFAULT_DATE_TIME_FORMAT);
	private DateTimeFormat tf = DateTimeFormat.getFormat(ToolsGWT.DEFAULT_TIME_FORMAT);

	private DataGrid<T> datagrid;

	private ListDataProvider<T> dataProvider;
	private ListHandler<T> sortHandler;
	private SelectionModel<T> selectionModel;
	private SimplePager pager;

	private List<GridHandler<T>> listeners;

	private HandlerRegistration defaultTooltipHandler;

	private HeaderCheckboxCell<T> checkboxCell;
	private List<T> items;
	private boolean isFilter;

	public GridPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		panelGrid.setWidget(buildDataGrid());

		defaultTooltipHandler = datagrid.addCellPreviewHandler(new CellPreviewEvent.Handler<T>() {

			@Override
			public void onCellPreview(final CellPreviewEvent<T> event) {
				if ("mouseover".equals(event.getNativeEvent().getType())) {
					Element cellElement = event.getNativeEvent().getEventTarget().cast();
					int row = event.getIndex();
					int col = event.getColumn();
					T model = (T) datagrid.getVisibleItem(row);

					Column<T, ?> column = datagrid.getColumn(col);
					if (!(column instanceof ColumnTooltip)) {
						Object item = datagrid.getColumn(col).getValue(model);

						// Afin de ne pas surcharger les tooltips des boutons
						// d'action sur les grids
						if (item instanceof String) {
							cellElement.setTitle((String) item);
						}
					}
				}
			}
		});
	}
	
	public void setEmptyTableWidget(Widget widget) {
		datagrid.setEmptyTableWidget(widget);
	}
	
	public Column<T, ?> getColumn(int col) {
		return datagrid.getColumn(col);
	}
	
	public T getVisibleItem(int row) {
		return datagrid.getVisibleItem(row);
	}

	public void setActionPanel(String label, Button... btnActions) {
		setActionPanel(label, false, btnActions);
	}

	public void setActionPanel(String label, boolean showFilter, Button... btnActions) {
		panelAction.setVisible(true);
		lblAction.setText(label);

		if (showFilter) {
			buildCheckboxFilter();
		}

		if (btnActions != null) {
			for (Button btnAction : btnActions) {
				btnAction.setHeight(25);
				btnAction.setImageSize(24);
				btnAction.addStyleName(style.btnAction());
				btnAction.addStyleName(GlobalCSS.INLINE);

				panelButtonAction.add(btnAction);
			}
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				panelGridContent.getElement().getStyle().setTop(panelAction.getOffsetHeight(), Unit.PX);
			}
		});
	}

	public void setActionWidget(Widget widget) {
		panelActionWidget.setWidget(widget);
		panelActionWidget.setVisible(true);
	}

	public void setTopManually(int top) {
		panelGridContent.getElement().getStyle().setTop(top, Unit.PX);
	}

	public void updateActionLabel(String label) {
		lblAction.setText(label);
	}

	public void loadItems(List<T> items) {
		if (items == null) {
			items = new ArrayList<T>();
		}
		this.items = items;
		
		//On supprime la selection s'il y en a une
		clearFilter();
		
		refreshItems(items);

		if (listeners != null) {
			for (GridHandler<T> listener : listeners) {
				listener.onItemsLoad(items);
			}
		}
	}

	public void clearSelection() {
		if (selectionModel != null) {
			if (selectionModel instanceof MultiSelectionModel) {
				((MultiSelectionModel<T>) selectionModel).clear();
			}
			else if (selectionModel instanceof SingleSelectionModel) {
				((SingleSelectionModel<T>) selectionModel).clear();
			}
		}
	}

	private void refreshItems(List<T> items) {
		dataProvider.setList(items);
		sortHandler.setList(dataProvider.getList());
		
		if (checkboxCell != null) {
			checkboxCell.loadItems(items);
		}
	}

	public void refresh() {
		dataProvider.refresh();
	}

	public void redrawRow(int index) {
		datagrid.redrawRow(index);
	}

	private DataGrid<T> buildDataGrid() {
		sortHandler = new ListHandler<T>(new ArrayList<T>());

//		DataGrid.Resources resources = new CustomResources();
		this.datagrid = new DataGrid<T>(40);
		datagrid.setSize("100%", "100%");

		dataProvider = new ListDataProvider<T>();
		dataProvider.addDataDisplay(datagrid);

		datagrid.addColumnSortHandler(sortHandler);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		this.pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(datagrid);

		txtPageSize.setText(String.valueOf(DEFAULT_PAGE_SIZE));

		panelPager.add(pager);

		return datagrid;
	}

	public void setSelectionModel(SelectionModel<T> selectionModel) {
		this.selectionModel = selectionModel;
		if (selectionModel instanceof MultiSelectionModel<?>) {
			datagrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<T> createCheckboxManager());
		}
		else {
			datagrid.setSelectionModel(selectionModel);
		}
	}

	public SelectionModel<T> getSelectionModel() {
		return selectionModel;
	}

	public List<T> getSelectedItems() {
		List<T> items = new ArrayList<T>();
		if (selectionModel != null && selectionModel instanceof MultiSelectionModel<?>) {
			items.addAll(((MultiSelectionModel<T>) selectionModel).getSelectedSet());
		}
		return items;
	}

	public List<T> getItems() {
		return dataProvider.getList();
	}

	public void setSelectedItems(List<T> items) {
		for (T item : items) {
			selectionModel.setSelected(item, true);
		}
	}

	public void setRowStyles(RowStyles<T> rowStyles) {
		datagrid.setRowStyles(rowStyles);
	}

	public void addColumn(String columnName, Column<T, ?> column, String width, Comparator<T> comparator) {
		datagrid.addColumn(column, columnName);
		addColumn(column, width, comparator);
	}

	public void addColumn(Header<?> columnHeader, Column<T, ?> column, String width, Comparator<T> comparator) {
		datagrid.addColumn(column, columnHeader);
		addColumn(column, width, comparator);
	}

	private void addColumn(Column<T, ?> column, String width, Comparator<T> comparator) {
		if (comparator != null) {
			column.setSortable(true);
			sortHandler.setComparator(column, comparator);
		}

		if (width != null) {
			datagrid.setColumnWidth(column, width);
		}
	}

	private void buildCheckboxFilter() {
		btnFilter.setVisible(true);

		this.selectionModel = new MultiSelectionModel<T>();
		datagrid.setSelectionModel(selectionModel);
		
		Column<T, Boolean> colCheck = new Column<T, Boolean>(new CheckboxCell(false, true)) {
			@Override
			public Boolean getValue(T object) {
				return selectionModel.isSelected(object);
			}
		};
		colCheck.setFieldUpdater(new FieldUpdater<T, Boolean>() {
			@Override
			public void update(int index, T object, Boolean value) {
				((MultiSelectionModel<T>) selectionModel).setSelected(object, value);
			}
		});

		this.checkboxCell = new HeaderCheckboxCell<T>(null, selectionModel);
		Header<Boolean> headerCheck = new Header<Boolean>(checkboxCell) {

			@Override
			public Boolean getValue() {
				return false;
			}
		};

		addColumn(headerCheck, colCheck, "40px", null);
	}

	@UiHandler("btnFilter")
	public void onFilter(ClickEvent event) {
		this.isFilter = !isFilter;
		btnFilter.setText(isFilter ? LabelsConstants.lblCnst.DisplayAll() : LabelsConstants.lblCnst.Filter());
		
		List<T> filterItems = items;
		if (isFilter) {
			filterItems = new ArrayList<T>();
			filterItems.addAll(((MultiSelectionModel<T>) selectionModel).getSelectedSet());
		}
		refreshItems(filterItems);
	}
	
	private void clearFilter() {
		this.isFilter = false;
		btnFilter.setText(LabelsConstants.lblCnst.Filter());

		if (selectionModel != null && selectionModel instanceof MultiSelectionModel<?>) {
			((MultiSelectionModel<T>) selectionModel).clear();
		}
	}

	public void removeTooltipHandler() {
		defaultTooltipHandler.removeHandler();
	}

	public void addCellPreviewHandler(Handler<T> handler) {
		removeTooltipHandler();
		datagrid.addCellPreviewHandler(handler);
	}

	@UiHandler("txtPageSize")
	public void onPageSize(ValueChangeEvent<String> event) {
		try {
			int pageSize = Integer.parseInt(txtPageSize.getText());
			pager.setPageSize(pageSize);
		} catch (Exception e) {
			txtPageSize.setText(String.valueOf(DEFAULT_PAGE_SIZE));
		}
	}

	public void addGridHandler(GridHandler<T> handler) {
		if (listeners == null) {
			this.listeners = new ArrayList<GridHandler<T>>();
		}
		this.listeners.add(handler);
	}

	public String formatDate(Date date) {
		return date != null ? df.format(date) : LabelsConstants.lblCnst.NotDefined();
	}

	public String formatDateTime(Date date) {
		return date != null ? dtf.format(date) : LabelsConstants.lblCnst.NotDefined();
	}

	public String formatTime(Date date) {
		return date != null ? tf.format(date) : " / ";
	}

	public int compare(Long value1, Long value2) {
		if (value1 == null && value2 == null) {
			return 0;
		}
		else if (value1 == null) {
			return -1;
		}
		else if (value2 == null) {
			return 1;
		}
		return value1.compareTo(value2);
	}

	public int compare(BigDecimal value1, BigDecimal value2) {
		if (value1 == null && value2 == null) {
			return 0;
		}
		else if (value1 == null) {
			return -1;
		}
		else if (value2 == null) {
			return 1;
		}
		return value1.compareTo(value2);
	}

	public int compare(String value1, String value2) {
		if (value1 == null && value2 == null) {
			return 0;
		}
		else if (value1 == null) {
			return -1;
		}
		else if (value2 == null) {
			return 1;
		}
		return value1.compareTo(value2);
	}

	public int compare(Date value1, Date value2) {
		if (value1 == null && value2 == null) {
			return 0;
		}
		else if (value1 == null) {
			return -1;
		}
		else if (value2 == null) {
			return 1;
		}
		return value1.compareTo(value2);
	}

	public int compare(Integer value1, Integer value2) {
		if (value1 == null && value2 == null) {
			return 0;
		}
		else if (value1 == null) {
			return -1;
		}
		else if (value2 == null) {
			return 1;
		}
		return value1.compareTo(value2);
	}

	public int compare(Boolean value1, Boolean value2) {
		if (value1 == null && value2 == null) {
			return 0;
		}
		else if (value1 == null) {
			return -1;
		}
		else if (value2 == null) {
			return 1;
		}
		return value1.compareTo(value2);
	}

	public void setPageVisible(boolean visible) {
		panelBottom.setVisible(visible);
	}
}
