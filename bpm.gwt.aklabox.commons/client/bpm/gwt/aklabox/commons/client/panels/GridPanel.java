package bpm.gwt.aklabox.commons.client.panels;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.gwt.aklabox.commons.client.utils.Constants;
import bpm.gwt.aklabox.commons.client.utils.CustomResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

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
	HTMLPanel panelBottom;

	@UiField
	SimplePanel panelGridContent, panelGrid, panelPager;

	@UiField
	TextBox txtPageSize;

	private DateTimeFormat df = DateTimeFormat.getFormat(Constants.DEFAULT_DATE_FORMAT);
	private DateTimeFormat dtf = DateTimeFormat.getFormat(Constants.DEFAULT_DATE_TIME_FORMAT);
	private DateTimeFormat tf = DateTimeFormat.getFormat(Constants.DEFAULT_TIME_FORMAT);

	private DataGrid<T> datagrid;

	private ListDataProvider<T> dataProvider;
	private ListHandler<T> sortHandler;
	private SelectionModel<T> selectionModel;
	private SimplePager pager;

	private List<GridHandler<T>> listeners;
	
	private HandlerRegistration defaultTooltipHandler;

	public GridPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		panelGrid.setWidget(buildDataGrid());
		
		defaultTooltipHandler = datagrid.addCellPreviewHandler(new CellPreviewEvent.Handler<T>() {

            @Override
            public void onCellPreview(final CellPreviewEvent<T> event) {
                if ("mouseover".equals(event.getNativeEvent().getType())) {
                    String str = "";
                    Element cellElement = event.getNativeEvent().getEventTarget().cast();
                    int row = event.getIndex();
                    int col = event.getColumn();
                    T model = (T) datagrid.getVisibleItem(row);
                    str = "" + datagrid.getColumn(col).getValue(model);
                    cellElement.setTitle(str);

                }
            }
        });
	}

	public void setTopManually(int top) {
		panelGridContent.getElement().getStyle().setTop(top, Unit.PX);
	}

	public void loadItems(List<T> items) {
		
		if (items == null) {
			items = new ArrayList<T>();
		}
		
		dataProvider.setList(items);
		sortHandler.setList(dataProvider.getList());

		if (listeners != null) {
			for (GridHandler<T> listener : listeners) {
				listener.onItemsLoad(items);
			}
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

		DataGrid.Resources resources = new CustomResources();
		this.datagrid = new DataGrid<>(40, resources);
		datagrid.setSize("100%", "100%");

		dataProvider = new ListDataProvider<>();
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
		List<T> items = new ArrayList<>();
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
		if (comparator != null) {
			column.setSortable(true);
			sortHandler.setComparator(column, comparator);
		}

		if (width != null) {
			datagrid.setColumnWidth(column, width);
		}
	}

	public void addColumn(Header<String> columnHeader, Column<T, ?> column, String width, Comparator<T> comparator) {
		datagrid.addColumn(column, columnHeader);
		if (comparator != null) {
			column.setSortable(true);
			sortHandler.setComparator(column, comparator);
		}

		if (width != null) {
			datagrid.setColumnWidth(column, width);
		}
	}
	
	public void addCellPreviewHandler(Handler<T> handler) {
		defaultTooltipHandler.removeHandler();
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
		return date != null ? df.format(date) : LabelsConstants.lblCnst.NotDefine();
	}

	public String formatDateTime(Date date) {
		return date != null ? dtf.format(date) : LabelsConstants.lblCnst.NotDefine();
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
