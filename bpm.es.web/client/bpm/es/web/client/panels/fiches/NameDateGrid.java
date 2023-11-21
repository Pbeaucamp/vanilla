package bpm.es.web.client.panels.fiches;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bpm.es.web.client.EsWeb.Layout;
import bpm.es.web.client.I18N.Labels;
import bpm.es.web.client.images.Images;
import bpm.es.web.shared.beans.NameDateItem;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.utils.ButtonImageCell;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.FieldUpdater;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class NameDateGrid extends Composite {

	private DateTimeFormat dtfull = DateTimeFormat.getFormat("HH:mm dd/MM/yyyy");
	private DateTimeFormat dtsimple = DateTimeFormat.getFormat("dd/MM/yyyy");

	private static NameDateGridUiBinder uiBinder = GWT.create(NameDateGridUiBinder.class);

	interface NameDateGridUiBinder extends UiBinder<Widget, NameDateGrid> {
	}

	interface MyStyle extends CssResource {
		String imgGrid();

		String imgPlanned();

		String pager();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelGrid;

	@UiField
	Image btnAdd;

	private DataGrid<NameDateItem> datagrid;
	private ListDataProvider<NameDateItem> dataProvider;
	private ListHandler<NameDateItem> sortHandler;

	private List<NameDateItem> items;

	private boolean fullDate;
	
	public NameDateGrid(boolean fullDate, String colName, String colDate, String colDate2) {
		initWidget(uiBinder.createAndBindUi(this));
		this.fullDate = fullDate;

		datagrid = buildGrid(colName, colDate, colDate2);
		panelGrid.setWidget(datagrid);

		loadItems();
	}

	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {

	}

	private void loadItems() {
		// parentPanel.showWaitPart(true);
		//
		// CommonService.Connect.getInstance().getNameDateItems(new GwtCallbackWrapper<List<NameDateItem>>(waitPanel, true) {
		//
		// @Override
		// public void onSuccess(List<NameDateItem> result) {
		// loadNameDateItems(result);
		// }
		// }.getAsyncCallback());
	}

	public void loadItems(List<NameDateItem> result) {
		this.items = result != null ? result : new ArrayList<NameDateItem>();
		dataProvider.setList(items);
		sortHandler.setList(dataProvider.getList());
	}

	private DataGrid<NameDateItem> buildGrid(String colNameLabel, String colDateLabel, String colDate2Label) {
		TextCell txtCell = new TextCell();
		Column<NameDateItem, String> colName = new Column<NameDateItem, String>(txtCell) {

			@Override
			public String getValue(NameDateItem object) {
				return object.getName() != null ? object.getName() : Labels.lblCnst.Unknown();
			}
		};
		colName.setSortable(true);

		Column<NameDateItem, String> colDate = new Column<NameDateItem, String>(txtCell) {

			@Override
			public String getValue(NameDateItem object) {
				if (fullDate) {
					return object.getDate() != null ? dtfull.format(object.getDate()) : Labels.lblCnst.Unknown();
				}
				else {
					return object.getDate() != null ? dtsimple.format(object.getDate()) : Labels.lblCnst.Unknown();
				}
			}
		};
		colDate.setSortable(true);

		Column<NameDateItem, String> colDate2 = new Column<NameDateItem, String>(txtCell) {

			@Override
			public String getValue(NameDateItem object) {
				if (fullDate) {
					return object.getDate2() != null ? dtfull.format(object.getDate2()) : "";
				}
				else {
					return object.getDate2() != null ? dtsimple.format(object.getDate2()) : "";
				}
			}
		};
		colDate2.setSortable(true);

		ButtonImageCell deleteCell = new ButtonImageCell(Images.INSTANCE.ic_delete_black_18dp(), style.imgGrid());
		Column<NameDateItem, String> colDelete = new Column<NameDateItem, String>(deleteCell) {

			@Override
			public String getValue(NameDateItem object) {
				return "";
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<NameDateItem, String>() {

			@Override
			public void update(int index, final NameDateItem object, String value) {

			}
		});

		DataGrid.Resources resources = new CustomResources();
		DataGrid<NameDateItem> dataGrid = new DataGrid<NameDateItem>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		dataGrid.addColumn(colName, colNameLabel);
		dataGrid.addColumn(colDate, colDateLabel);
		if (colDate2Label != null) {
			dataGrid.addColumn(colDate2, colDate2Label);
		}
		dataGrid.addColumn(colDelete);
		dataGrid.setColumnWidth(colDelete, "70px");
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoData()));

		// columns.put(surnameColumn, Labels.lblCnst.Surname());
		// columns.put(nameColumn, Labels.lblCnst.Name());
		// columns.put(mailColumn, Labels.lblCnst.Mail());
		// columns.put(creationDate, Labels.lblCnst.CreationDate());

		dataProvider = new ListDataProvider<NameDateItem>();
		dataProvider.addDataDisplay(dataGrid);

		sortHandler = new ListHandler<NameDateItem>(new ArrayList<NameDateItem>());
		sortHandler.setComparator(colName, new Comparator<NameDateItem>() {

			@Override
			public int compare(NameDateItem o1, NameDateItem o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		sortHandler.setComparator(colDate, new Comparator<NameDateItem>() {

			@Override
			public int compare(NameDateItem o1, NameDateItem o2) {
				if (o1.getDate() == null) {
					return -1;
				}
				else if (o2.getDate() == null) {
					return 1;
				}

				return o2.getDate().before(o1.getDate()) ? -1 : o2.getDate().after(o1.getDate()) ? 1 : 0;
			}
		});
		sortHandler.setComparator(colDate2, new Comparator<NameDateItem>() {

			@Override
			public int compare(NameDateItem o1, NameDateItem o2) {
				if (o1.getDate2() == null) {
					return -1;
				}
				else if (o2.getDate2() == null) {
					return 1;
				}

				return o2.getDate2().before(o1.getDate2()) ? -1 : o2.getDate2().after(o1.getDate2()) ? 1 : 0;
			}
		});
		dataGrid.addColumnSortHandler(sortHandler);
		sortHandler.setList(dataProvider.getList());

		return dataGrid;
	}

	public void editNameDateItem(NameDateItem user) {

	}

	public void manageLayout(Layout layout) {
		// if (layout == Layout.MOBILE) {
		// if (columns != null) {
		// for (Entry<Column<NameDateItem, ?>, String> col : columns.entrySet()) {
		// if (datagrid.getColumnIndex(col.getKey()) > 0) {
		// datagrid.removeColumn(col.getKey());
		// }
		// }
		// }
		// }
		// else if (layout == Layout.TABLET || layout == Layout.COMPUTER) {
		// if (columns != null) {
		// int index = 2;
		// for (Entry<Column<NameDateItem, ?>, String> col : columns.entrySet()) {
		// if (datagrid.getColumnIndex(col.getKey()) < 0) {
		// datagrid.insertColumn(index, col.getKey(), col.getValue());
		// index++;
		// }
		// }
		// }
		// }
	}
}
