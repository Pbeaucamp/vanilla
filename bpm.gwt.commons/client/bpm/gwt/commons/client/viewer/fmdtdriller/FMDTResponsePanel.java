package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.utils.CustomResources;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.DisplayRowDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.DisplayRowDialog.IRefreshValuesHandler;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.DrillableTablesDialog;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtRow;
import bpm.gwt.commons.shared.fmdt.FmdtRow.Type;
import bpm.gwt.commons.shared.fmdt.FmdtTable;
import bpm.vanilla.platform.core.beans.fmdt.FmdtAggregate;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

public class FMDTResponsePanel extends Composite {

	interface MyStyle extends CssResource {
		String pager();

		String labelRowDocument();

		String textDocument();

		String imgArrowDocument();

		String documentList();

		String panelContent();

		String panelContent1();

		String panelContent2();

		String rowLabel();

		String hover();

	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel dockPanel, leftPanel;

	@UiField
	SimplePanel tableContentPanel, panelPager, queryPanel;

	@UiField
	Label lblQuery;

	@UiField
	Image btnReply, btnFormatSimple, btnFormatAgg;

	private final IWait waitPanel;

	private FmdtQueryDriller driller;
	private FmdtRow columnNames;
	private FmdtTable selectedTable;
	private FmdtTable originTable;
	
	private FmdtTable formattedTable;
	private FmdtTable flatTable;

	private ListDataProvider<FmdtRow> dataProvider;

	private SingleSelectionModel<FmdtRow> selectionModel = new SingleSelectionModel<FmdtRow>();

	private DataGrid<FmdtRow> dataGrid;

	private IRefreshValuesHandler refreshValuesHandler;

	private static FMDTResponsePanelUiBinder uiBinder = GWT.create(FMDTResponsePanelUiBinder.class);

	interface FMDTResponsePanelUiBinder extends UiBinder<Widget, FMDTResponsePanel> {
	}

	public FMDTResponsePanel(List<FmdtTable> data, FmdtQueryDriller driller, final IWait waitPanel, IRefreshValuesHandler refreshValuesHandler) {
		initWidget(uiBinder.createAndBindUi(this));
		this.refreshValuesHandler = refreshValuesHandler;
		this.driller = driller;
		this.waitPanel = waitPanel;
		btnReply.setVisible(false);
		
		initColorList();
		
		this.flatTable=data.get(0);
		
		if(data.size()>1){
			this.formattedTable=data.get(1);
			this.originTable =formattedTable;
			buildTableData(formattedTable);
		}else{
			this.originTable =flatTable;
			buildTableData(flatTable);
		}
		btnFormatAgg.addStyleName(style.hover());
	}

	public void buildTableData(final FmdtTable table) {

		this.selectedTable = table;
		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<FmdtRow>(100, resources);
		dataGrid.setSize("100%", "100%");

		List<FmdtRow> datas = new ArrayList<FmdtRow>();
		datas.addAll(table.getDatas());
		columnNames = datas.get(0);
		datas.remove(0);

		boolean setSize = false;
		if (columnNames.getValues().size() > 4) {
			setSize = true;
		}
		Cell<String> txtCell = new TextCell();

		for (int i = 0; i < columnNames.getValues().size(); i++) {
			final int lineIndex = i;

			if (table.getRelatedTables().keySet().contains(table.getColumns().get(i).getName())) {
				SafeHtmlRenderer<String> anchorRenderer = new AbstractSafeHtmlRenderer<String>() {
					@Override
					public SafeHtml render(String object) {
						SafeHtmlBuilder sb = new SafeHtmlBuilder();
						sb.appendHtmlConstant("<a href=\"javascript:;\">").appendEscaped(object).appendHtmlConstant("</a>");
						return sb.toSafeHtml();
					}
				};
				Column<FmdtRow, String> col = new Column<FmdtRow, String>(new ClickableTextCell(anchorRenderer)) {
					@Override
					public String getValue(FmdtRow object) {
						return object.getValues().get(lineIndex);
					}
				};
				col.setDefaultSortAscending(false);
				col.setFieldUpdater(new FieldUpdater<FmdtRow, String>() {
					@Override
					public void update(int index, FmdtRow object, String value) {
						HashMap<String, String> row = new HashMap<String, String>();
						for (int i = 0; i < object.getValues().size(); i++) {
							row.put(columnNames.getValues().get(i), object.getValues().get(i));
						}

						List<String> tableNames = table.getRelatedTables().get(table.getColumns().get(lineIndex).getName());
						if (tableNames != null && tableNames.size() > 1) {
							DrillableTablesDialog dial = new DrillableTablesDialog(FMDTResponsePanel.this, driller, table, table.getColumns().get(lineIndex).getName(), row);
							dial.center();
						} else if (tableNames != null && tableNames.size() == 1) {
							if (table.getColumns().get(lineIndex) instanceof FmdtColumn)
								loadDrillTable(tableNames.get(0), row, ((FmdtColumn) table.getColumns().get(lineIndex)).getTableName());
							else if (table.getColumns().get(lineIndex) instanceof FmdtAggregate)
								loadDrillTable(tableNames.get(0), row, ((FmdtAggregate) table.getColumns().get(lineIndex)).getTable());
						}
					}
				});
				dataGrid.addColumn(col, columnNames.getValues().get(i));

				if (setSize) {
					dataGrid.setColumnWidth(col, "150px");
				}
			} else {
				if (selectedTable.isEditable()) {
					txtCell = new EditTextCell();
				}

				Column<FmdtRow, String> col = new Column<FmdtRow, String>(txtCell) {

					@Override
					public String getValue(FmdtRow object) {
						return object.getValues().get(lineIndex);
					}
				};
				col.setDefaultSortAscending(false);
				dataGrid.addColumn(col, columnNames.getValues().get(i));

				if (setSize) {
					dataGrid.setColumnWidth(col, "200px");
				}

				if (selectedTable.isEditable()) {
					col.setFieldUpdater(new FieldUpdater<FmdtRow, String>() {
						@Override
						public void update(int index, FmdtRow object, String value) {

							if (!object.getValues().get(lineIndex).equals(value)) {
								if (object.getType() != Type.ADDED) {
									object.setType(Type.UPDATED);
								}

								object.getValues().set(lineIndex, value);

								dataGrid.redraw();
							}
						}

					});
				}
			}

			dataGrid.setRowStyles(new RowStyles<FmdtRow>() {
				@Override
				public String getStyleNames(FmdtRow row, int rowIndex) {
					if (row.getTypeRow().equals(FmdtRow.LABEL)) {
						return style.rowLabel();
					}
					return null;
				}
			});

		}

		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoData()));

		dataProvider = new ListDataProvider<FmdtRow>();
		dataProvider.addDataDisplay(dataGrid);

		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);

		dataGrid.setSelectionModel(selectionModel);

		tableContentPanel.clear();
		tableContentPanel.setWidget(dataGrid);

		panelPager.setVisible(true);
		queryPanel.setVisible(true);
		panelPager.setWidget(pager);

		lblQuery.setText(table.getQuery().replace("=", "= "));

		dataGrid.addDomHandler(new DoubleClickHandler() {
			@SuppressWarnings("unchecked")
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				DataGrid<FmdtRow> grid = (DataGrid<FmdtRow>) event.getSource();
				int row = grid.getKeyboardSelectedRow();
				FmdtRow item = grid.getVisibleItem(row);

				final DisplayRowDialog displayRow = new DisplayRowDialog(waitPanel, columnNames, item, selectedTable, driller, refreshValuesHandler);
				displayRow.center();

			}
		}, DoubleClickEvent.getType());

		dataProvider.setList(datas);
	}

	private void loadDrillTable(String drillTable, HashMap<String, String> row, String tableName) {
		showWaitPart(true);
		if (tableName != null && tableName.length() > 0)
			FmdtServices.Connect.getInstance().drillOnTable(driller, tableName, drillTable, row, new AsyncCallback<FmdtTable>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					showWaitPart(false);
				}

				@Override
				public void onSuccess(FmdtTable result) {
					showWaitPart(false);
					leftPanel.removeFromParent();
					tableContentPanel.removeStyleName(style.panelContent2());
					tableContentPanel.addStyleName(style.panelContent1());
					btnReply.setVisible(true);
					buildTableData(result);
				}
			});
	}

	public void showWaitPart(boolean visible) {
		waitPanel.showWaitPart(visible);
	}

//	private class TableDefinitionCell extends AbstractCell<String> {
//
//		@Override
//		public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
//			String doubleArrowUrl = GWT.getHostPageBaseURL() + "images/documentManager/double_arrow.png";
//
//			sb.appendHtmlConstant("<div class='" + style.labelRowDocument() + "'>");
//			sb.appendHtmlConstant("<div class='" + style.textDocument() + "'>");
//			sb.appendEscaped(value);
//			sb.appendHtmlConstant("</div>");
//			sb.appendHtmlConstant("<img src='" + doubleArrowUrl + "' class='" + style.imgArrowDocument() + "' />");
//			sb.appendHtmlConstant("</div>");
//		}
//	}

	public FmdtTable getSelectedTable() {
		return selectedTable;
	}

	public FmdtRow getColumnNames() {
		return columnNames != null ? columnNames : new FmdtRow();
	}

	@UiHandler("btnReply")
	public void onReplyClick(ClickEvent event) {
		btnReply.setVisible(false);
		tableContentPanel.removeStyleName(style.panelContent1());
		tableContentPanel.addStyleName(style.panelContent2());
		dockPanel.add(leftPanel);
		buildTableData(originTable);
	}

	@UiHandler("btnFormatSimple")
	public void onFlatFormatClick(ClickEvent event) {
		btnFormatSimple.addStyleName(style.hover());
		btnFormatAgg.removeStyleName(style.hover());
		/*
		FmdtServices.Connect.getInstance().getRequestValue(driller.getBuilder(), driller, false, new AsyncCallback<FmdtTable>() {
			@Override
			public void onFailure(Throwable caught) {
				waitPanel.showWaitPart(false);

				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(FmdtTable table) {
				waitPanel.showWaitPart(false);
				List<FmdtRow> datas = new ArrayList<FmdtRow>();
				datas.addAll(table.getDatas());
				datas.remove(0);
				dataProvider.setList(datas);
			}
		});
		*/
		List<FmdtRow> datas = new ArrayList<FmdtRow>();
		datas.addAll(flatTable.getDatas());
		datas.remove(0);
		dataProvider.setList(datas);
		this.selectedTable = flatTable;
		originTable=flatTable;
	}

	@UiHandler("btnFormatAgg")
	public void onFormatClick(ClickEvent event) {
		btnFormatAgg.addStyleName(style.hover());
		btnFormatSimple.removeStyleName(style.hover());
		if (!driller.getBuilder().getFormulas().isEmpty() || !driller.getBuilder().getAggregates().isEmpty()) {
		/*
		
			FmdtServices.Connect.getInstance().getRequestValue(driller.getBuilder(), driller, true, new AsyncCallback<FmdtTable>() {
				@Override
				public void onFailure(Throwable caught) {
					waitPanel.showWaitPart(false);

					caught.printStackTrace();
					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
				}

				@Override
				public void onSuccess(FmdtTable table) {
					waitPanel.showWaitPart(false);
					List<FmdtRow> datas = new ArrayList<FmdtRow>();
					datas.addAll(table.getDatas());
					datas.remove(0);
					dataProvider.setList(datas);
				}
			});
			*/
			List<FmdtRow> datas = new ArrayList<FmdtRow>();
			datas.addAll(formattedTable.getDatas());
			datas.remove(0);
			dataProvider.setList(datas);
			this.selectedTable = formattedTable;
			originTable= formattedTable;
		}
		
	}

	private void initColorList() {
		if (driller.getBuilder().getColumns().size() - 1 > 0) {
			int nb = driller.getBuilder().getColumns().size() - 1;
			int[] listcolor = new int[nb];
			int marge = 92 / nb;
			int color = 100;
			for (int i = 0; i < nb; i++) {
				color += marge;
				listcolor[i] = color;
			}
//			listColor = listcolor;
		}

	}

	public FmdtTable getFormattedTable() {
		return formattedTable;
	}

	public FmdtTable getFlatTable() {
		return flatTable;
	}
	
	

}
