package bpm.gwt.commons.client.viewer.fmdtdriller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.ComplexFilterDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.ComputedColumnDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.PromptFilterDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.SqlFilterDialog;
import bpm.gwt.commons.client.viewer.fmdtdriller.dialog.ViewQueryDialog;
import bpm.gwt.commons.shared.fmdt.FmdtConstant;
import bpm.gwt.commons.shared.fmdt.FmdtQueryBuilder;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.vanilla.platform.core.beans.fmdt.FmdtAggregate;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFormula;
import bpm.vanilla.platform.core.beans.fmdt.FmdtTableStruct;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class DesignerPanel extends Composite implements IExpand {

	private static final int DEFAULT_BOTTOM = 230;
	private static final int DEFAULT_HEIGHT = 220;
	private static final int COLLAPSE_BOTTOM = 40;
	private static final int COLLAPSE_HEIGHT = 30;

	private static DesignerPanelUiBinder uiBinder = GWT.create(DesignerPanelUiBinder.class);

	interface DesignerPanelUiBinder extends UiBinder<Widget, DesignerPanel> {
	}

	interface MyStyle extends CssResource {
		String columnsList();

		String buttonlayout();

		String dataBuilder();

		String btnRequestBuilder();

		String btnRequestDisabled();

		String hover();

		String requestValue();

		String panelValuedef2();
		
		String colUndefined();
		String colDimension();
		String colMeasure();
		String colProperty();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelButtons, panelValue, doublePanelButton, panelButtons1, panelButtons2;

	@UiField
	SimplePanel panelData, panelResources, panelTree, panelDescription;

	@UiField
	Image btnAdd, btnRemove, btnUp, btnDown, btnCreateComputed, btnCreateComplex, btnCreateSql, btnCreatePrompt, btnEdit, btnViewQuery, btnClear;

	@UiField
	CheckBox checkboxDistinct, checkboxLimit;

	@UiField
	TextBox txtLimit, txtSearch;

	private FmdtQueryBuilder builder;
	private FmdtQueryDatas queryDatas;
	private final DataGrid<QueryRow> queryColumnTable = new DataGrid<QueryRow>();
	private final DataGrid<QueryRow> queryResourceTable = new DataGrid<QueryRow>();

	private SingleSelectionModel<QueryRow> selectionModel = new SingleSelectionModel<QueryRow>();
	private ListDataProvider<QueryRow> columns;
	private ListDataProvider<QueryRow> resources;

	private Tree queryTree;
	private MetadataItemDescriptionPanel metadataItemDescription;

	private TreeItem selectedNode = null;
	private final List<String> aggregList = new ArrayList<String>(Arrays.asList("None", "SUM", "AVG", "COUNT", "DISTINCT COUNT", "MAX", "MIN"));
	private int metadataId;
	private String modelName, packageName;
	private Boolean change = false;
	private String actualSearch = "";

	public DesignerPanel(FmdtQueryBuilder builder, FmdtQueryDatas datas, int metadataId, String modelName, String packageName) {
		initWidget(uiBinder.createAndBindUi(this));
		this.builder = builder;
		this.queryDatas = datas;
		this.metadataId = metadataId;
		this.modelName = modelName;
		this.packageName = packageName;

		queryTree = new Tree();
		initColumns();
		metadataItemDescription = new MetadataItemDescriptionPanel(this, panelTree, panelDescription);

		panelTree.setWidget(queryTree);
		panelDescription.setWidget(metadataItemDescription);

		panelTree.getElement().getStyle().setBottom(COLLAPSE_BOTTOM, Unit.PX);
		panelDescription.setHeight(COLLAPSE_HEIGHT + "px");

		queryColumnTable.setAutoHeaderRefreshDisabled(true);
		queryColumnTable.setAutoFooterRefreshDisabled(true);
		queryColumnTable.setLoadingIndicator(null);
		queryColumnTable.setPageSize(100);
		queryColumnTable.setVisibleRange(0, 100);
		queryResourceTable.setAutoHeaderRefreshDisabled(true);
		queryResourceTable.setAutoFooterRefreshDisabled(true);
		queryResourceTable.setLoadingIndicator(null);
		queryResourceTable.setPageSize(100);
		queryResourceTable.setVisibleRange(0, 100);

		columns = initColumnRow();
		resources = initResourceRow();

		initColumnTable();

		redrawPanelButton();

		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				redrawPanelButton();
			}

		});

		initResourceTable();

		checkboxDistinct.setValue(builder.isDistinct());
		checkboxLimit.setValue(builder.isLimit());
		txtLimit.setEnabled(builder.isLimit());
		if (builder.getNbLimit() > 0)
			txtLimit.setText(String.valueOf(builder.getNbLimit()));

		txtLimit.addKeyPressHandler(new NumbersOnlyKeyPressHandler());
		txtLimit.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				change = true;

			}
		});

		checkboxDistinct.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				change = true;

			}
		});
		checkboxLimit.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				change = true;

			}
		});

		columns.addDataDisplay(queryColumnTable);
		resources.addDataDisplay(queryResourceTable);
		queryColumnTable.redraw();
		queryResourceTable.redraw();

		queryTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				setSelected(true, event.getSelectedItem());
				if (selectedNode != null)
					setSelected(false, selectedNode);

				selectedNode = event.getSelectedItem();
				selectionModel.clear();
				tablesColumnSel();

				String description = getDescription(selectedNode);
				metadataItemDescription.updateDescription(description);
			}
		});

		selectionModel.addSelectionChangeHandler(new Handler() {

			@Override
			@SuppressWarnings("rawtypes")
			public void onSelectionChange(SelectionChangeEvent event) {
				if (columns.getList().contains(((SingleSelectionModel) event.getSource()).getSelectedObject())) {
					queryColumnSel();
				}
				else if (resources.getList().contains(((SingleSelectionModel) event.getSource()).getSelectedObject())) {
					resourcesSel();
				}
			}
		});

		noSel();
	}

	@Override
	public void expand(boolean expand) {
		if (expand) {
			panelTree.getElement().getStyle().setBottom(DEFAULT_BOTTOM, Unit.PX);
			panelDescription.setHeight(DEFAULT_HEIGHT + "px");
		}
		else {
			panelTree.getElement().getStyle().setBottom(COLLAPSE_BOTTOM, Unit.PX);
			panelDescription.setHeight(COLLAPSE_HEIGHT + "px");
		}
	}

	private String getDescription(TreeItem selectedNode) {
		Object item = selectedNode != null ? selectedNode.getUserObject() : null;
		if (item == null) {
			return "";
		}

		if (item instanceof FmdtTableStruct) {
			return ((FmdtTableStruct) item).getDescription();
		}
		else if (item instanceof FmdtColumn) {
			return ((FmdtColumn) item).getDescription();
		}

		return "";
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {
		TreeItem item = queryTree.getSelectedItem();
		if (item != null) {
			FmdtData dataSelected = (FmdtData) item.getUserObject();
			add(dataSelected);
		}
	}

	public void add(FmdtData dataSelected) {
		if (dataSelected instanceof FmdtColumn) {
			Boolean exist = false;
			for (QueryRow row : columns.getList()) {
				if (row.getData().getName().equals(dataSelected.getName())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				columns.getList().add(new QueryRow(dataSelected.getName(), dataSelected.getLabel(), String.valueOf(columns.getList().size() + 1), "None", dataSelected));
				builder.getColumns().add((FmdtColumn) dataSelected);
				redrawColumn();
			}
		}
		else if (dataSelected instanceof FmdtTableStruct) {

			for (FmdtColumn column : ((FmdtTableStruct) dataSelected).getColumns()) {
				Boolean exist = false;
				for (QueryRow row : columns.getList()) {
					if (row.getData().getName().equals(dataSelected.getName())) {
						exist = true;
						break;
					}
				}
				if (!exist) {
					columns.getList().add(new QueryRow(column.getName(), column.getLabel(), String.valueOf(columns.getList().size() + 1), "None", column));
					builder.getColumns().add((FmdtColumn) column);
				}
			}
			redrawColumn();
		}
		else if (dataSelected instanceof FmdtFilter) {
			Boolean exist = false;
			for (QueryRow row : resources.getList()) {
				if (row.getData().getName().equals(dataSelected.getName())) {
					exist = true;
					break;
				}
			}
			if (!exist) {
				String value = null;
				if (((FmdtFilter) dataSelected).getType().equals(FmdtConstant.FILTERPROMPT)) {
					value = "?";
				}
				else {
					for (String val : ((FmdtFilter) dataSelected).getValues()) {
						if (value != null)
							value += ", " + val;
						else
							value = val;
					}
				}
				String operator;
				if (((FmdtFilter) dataSelected).getType().equals(FmdtConstant.FILTERSQL)) {
					operator = ((FmdtFilter) dataSelected).getQuery();
				}
				else
					operator = ((FmdtFilter) dataSelected).getOperator();

				resources.getList().add(new QueryRow(dataSelected.getLabel(), ((FmdtFilter) dataSelected).getColumn().getLabel(), value, operator, dataSelected));
				redrawResources();
			}
		}
	}

	@UiHandler("btnRemove")
	public void onRemoveClick(ClickEvent event) {
		if (selectionModel.getSelectedObject() != null) {
			QueryRow selectedRow = selectionModel.getSelectedObject();
			if (selectedRow.getData() instanceof FmdtFilter) {

				resources.getList().remove(selectedRow);
				queryResourceTable.setVisibleRangeAndClearData(queryResourceTable.getVisibleRange(), true);
				redrawResources();
			}
			else {
				int index = columns.getList().indexOf(selectedRow);
				for (int i = index; i < columns.getList().size(); i++)
					columns.getList().get(i).setValue(String.valueOf(Integer.parseInt(columns.getList().get(i).getValue()) - 1));
				columns.getList().remove(selectedRow);
				queryColumnTable.setVisibleRangeAndClearData(queryColumnTable.getVisibleRange(), true);
				redrawColumn();
				noSel();
			}
			selectedRow = null;
		}
	}

	@UiHandler("btnUp")
	public void onUpClick(ClickEvent event) {
		if (selectionModel.getSelectedObject() != null) {
			QueryRow selectedRow = selectionModel.getSelectedObject();
			if (columns.getList().indexOf(selectedRow) > 0) {

				int num = Integer.parseInt(selectedRow.getValue());
				int index = columns.getList().indexOf(selectedRow);

				QueryRow row = columns.getList().get(index - 1);
				row.setValue(String.valueOf(num));

				selectedRow.setValue(String.valueOf(num - 1));

				columns.getList().set(index - 1, selectedRow);
				columns.getList().set(index, row);

				redrawColumn();
			}
		}
	}

	@UiHandler("btnDown")
	public void onDownlick(ClickEvent event) {
		if (selectionModel.getSelectedObject() != null) {
			QueryRow selectedRow = selectionModel.getSelectedObject();
			if (columns.getList().contains(selectedRow) && columns.getList().indexOf(selectedRow) < columns.getList().size() - 1) {

				int num = Integer.parseInt(selectedRow.getValue());
				int index = columns.getList().indexOf(selectedRow);

				QueryRow row = columns.getList().get(index + 1);
				row.setValue(String.valueOf(num));

				selectedRow.setValue(String.valueOf(num + 1));

				columns.getList().set(index + 1, selectedRow);
				columns.getList().set(index, row);

				redrawColumn();
			}
		}
	}

	@UiHandler("btnCreateComputed")
	public void onCreateComputedClick(ClickEvent event) {

		final ComputedColumnDialog computedColumn = new ComputedColumnDialog(queryDatas);

		computedColumn.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (computedColumn.getConfirm()) {
					FmdtFormula computed = computedColumn.getFormula();

					columns.getList().add(new QueryRow(computed.getName(), computed.getName(), String.valueOf(columns.getList().size() + 1), "None", computed));
					redrawColumn();
				}
			}
		});
		computedColumn.center();
	}

	@UiHandler("btnCreateComplex")
	public void onCreateComplexlick(ClickEvent event) {
		final ComplexFilterDialog complexFilter;

		if (selectedNode != null && selectedNode.getUserObject() instanceof FmdtColumn)
			complexFilter = new ComplexFilterDialog((FmdtColumn) selectedNode.getUserObject(), queryDatas);
		else
			complexFilter = new ComplexFilterDialog(queryDatas);

		complexFilter.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (complexFilter.getConfirm()) {
					FmdtFilter complex = complexFilter.getFilterComplex();
					String value = null;
					for (String val : complex.getValues()) {
						if (value != null)
							value += ", " + val;
						else
							value = val;
					}
					resources.getList().add(new QueryRow(complex.getName(), complex.getColumn().getLabel(), value, complex.getOperator(), complex));
					redrawResources();
				}
			}
		});
		complexFilter.center();

	}

	@UiHandler("btnCreateSql")
	public void onCreateSqlClick(ClickEvent event) {
		final SqlFilterDialog sqlFilter;

		if (selectedNode != null && selectedNode.getUserObject() instanceof FmdtColumn)
			sqlFilter = new SqlFilterDialog((FmdtColumn) selectedNode.getUserObject(), queryDatas);
		else
			sqlFilter = new SqlFilterDialog(queryDatas);

		sqlFilter.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (sqlFilter.getConfirm()) {
					FmdtFilter sql = sqlFilter.getFilterSql();
					String value = null;

					resources.getList().add(new QueryRow(sql.getName(), sql.getColumn().getLabel(), value, sql.getQuery(), sql));
					redrawResources();
				}
			}
		});
		sqlFilter.center();

	}

	@UiHandler("btnCreatePrompt")
	public void onCreatePromptClick(ClickEvent event) {
		final PromptFilterDialog promptFilter;
		if (selectedNode != null && selectedNode.getUserObject() instanceof FmdtColumn)
			promptFilter = new PromptFilterDialog((FmdtColumn) selectedNode.getUserObject(), queryDatas);
		else
			promptFilter = new PromptFilterDialog(queryDatas);

		promptFilter.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (promptFilter.getConfirm()) {
					FmdtFilter prompt = promptFilter.getFilterPrompt();

					resources.getList().add(new QueryRow(prompt.getName(), prompt.getColumn().getLabel(), prompt.getValues().get(0), prompt.getOperator(), prompt));
					redrawResources();
				}
			}
		});
		promptFilter.center();
	}

	@UiHandler("btnEdit")
	public void onEditClick(ClickEvent event) {
		if (selectionModel.getSelectedObject() != null) {
			final QueryRow selectedRow = selectionModel.getSelectedObject();
			if (selectedRow.getData() instanceof FmdtFormula) {
				final int index = columns.getList().indexOf(selectedRow);
				final ComputedColumnDialog computedColumn = new ComputedColumnDialog(queryDatas, (FmdtFormula) selectedRow.getData());

				computedColumn.addCloseHandler(new CloseHandler<PopupPanel>() {
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if (computedColumn.getConfirm()) {
							FmdtFormula computed = computedColumn.getFormula();

							columns.getList().remove(selectedRow);
							columns.getList().add(index, new QueryRow(computed.getName(), computed.getName(), String.valueOf(columns.getList().size() + 1), "None", computed));
							redrawColumn();
						}
					}
				});
				computedColumn.center();
			}
			else if (selectedRow.getData() instanceof FmdtFilter) {
				final int index = resources.getList().indexOf(selectedRow);

				FmdtFilter selectedFilter = (FmdtFilter) selectedRow.getData();

				if (selectedFilter.getType().equals(FmdtConstant.FILTERCOMPLEX)) {

					final ComplexFilterDialog complexFilter = new ComplexFilterDialog(selectedFilter, queryDatas);

					complexFilter.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (complexFilter.getConfirm()) {
								FmdtFilter complex = complexFilter.getFilterComplex();
								String value = null;
								for (String val : complex.getValues()) {
									if (value != null)
										value += ", " + val;
									else
										value = val;
								}
								resources.getList().remove(selectedRow);
								resources.getList().add(index, new QueryRow(complex.getName(), complex.getColumn().getLabel(), value, complex.getOperator(), complex));
								redrawResources();
							}
						}
					});
					complexFilter.center();
				}
				else if (selectedFilter.getType().equals(FmdtConstant.FILTERSQL)) {
					final SqlFilterDialog sqlFilter = new SqlFilterDialog(selectedFilter, queryDatas);

					sqlFilter.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (sqlFilter.getConfirm()) {
								FmdtFilter sql = sqlFilter.getFilterSql();
								String value = null;
								resources.getList().remove(selectedRow);
								resources.getList().add(index, new QueryRow(sql.getName(), sql.getColumn().getLabel(), value, sql.getQuery(), sql));
								redrawResources();
							}
						}
					});
					sqlFilter.center();
				}
				else if (selectedFilter.getType().equals(FmdtConstant.FILTERPROMPT)) {
					final PromptFilterDialog promptFilter = new PromptFilterDialog(selectedFilter, queryDatas);

					promptFilter.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(CloseEvent<PopupPanel> event) {
							if (promptFilter.getConfirm()) {
								FmdtFilter prompt = promptFilter.getFilterPrompt();
								resources.getList().remove(selectedRow);
								resources.getList().add(index, new QueryRow(prompt.getName(), prompt.getColumn().getLabel(), prompt.getValues().get(0), prompt.getOperator(), prompt));
								redrawResources();
							}
						}
					});
					promptFilter.center();
				}
			}
		}
	}

	@UiHandler("btnViewQuery")
	public void onViewClick(ClickEvent event) {
		refreshBuilder();
		FmdtServices.Connect.getInstance().getRequest(builder, metadataId, modelName, packageName, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
			}

			@Override
			public void onSuccess(String text) {
				ViewQueryDialog viewQuery = new ViewQueryDialog(text);
				viewQuery.center();
			}
		});
	}

	@UiHandler("btnClear")
	public void onClearClick(ClickEvent event) {
		columns.getList().clear();

		queryColumnTable.setRowCount(columns.getList().size(), true);
		redrawColumn();

		selectionModel.clear();
		noSel();
	}

	@UiHandler("btnClearResources")
	public void onClearResourcesClick(ClickEvent event) {
		resources.getList().clear();

		queryResourceTable.setRowCount(resources.getList().size(), true);
		redrawResources();
		
		selectionModel.clear();
		noSel();
	}

	@UiHandler("checkboxLimit")
	protected void onccheckboxLimit(ClickEvent event) {
		txtLimit.setEnabled(checkboxLimit.getValue());
	}

	@UiHandler("btnSearch")
	public void onSearch(ClickEvent e) {
		actualSearch = txtSearch.getText();
		initColumns();
	}

	private void initColumns() {
		queryTree.clear();
		if (queryDatas != null && queryDatas.getTables() != null) {
			for (FmdtTableStruct table : queryDatas.getTables()) {
				boolean keepTable = true;
				TreeItem tab = new TreeItem(new HTML(new Image(CommonImages.INSTANCE.folder()) + table.getLabel()));
				tab.setUserObject(table);
				for (FmdtColumn column : table.getColumns()) {
					if(column.getLabel().toLowerCase().contains(actualSearch.toLowerCase())) {
						TreeItem col = new MetadataTreeItem(column.getLabel(), CommonImages.INSTANCE.log_column(), column, new MetadataItemDoubleClickHandler(this, column));
						col.setStyleName(getColumnStyle(column));
						tab.addItem(col);
						keepTable = true;
					}
				}
				for (FmdtTableStruct subt : table.getSubTables()) {
					TreeItem subtab = new TreeItem(new HTML(new Image(CommonImages.INSTANCE.folder()) + subt.getLabel()));
					subtab.setUserObject(subt);
					for (FmdtColumn column : subt.getColumns()) {
						if(column.getLabel().toLowerCase().contains(actualSearch.toLowerCase())) {
							TreeItem col = new MetadataTreeItem(column.getLabel(), CommonImages.INSTANCE.log_column(), column, new MetadataItemDoubleClickHandler(this, column));
							col.setStyleName(getColumnStyle(column));
							subtab.addItem(col);
							keepTable = true;
						}
					}
					tab.addItem(subtab);
				}

				if (keepTable) {
					queryTree.addItem(tab);
				}
			}
			for (FmdtFilter filter : queryDatas.getFilters()) {
				TreeItem fil = null;
				if (filter.getType().equals(FmdtConstant.FILTERPROMPT))
					fil = new MetadataTreeItem(filter.getLabel(), CommonImages.INSTANCE.prompt_24(), filter, new MetadataItemDoubleClickHandler(this, filter));
				else
					fil = new MetadataTreeItem(filter.getLabel(), CommonImages.INSTANCE.filtre_24(), filter, new MetadataItemDoubleClickHandler(this, filter));
				fil.setUserObject(filter);
				if (filter.getLabel().toLowerCase().contains(actualSearch.toLowerCase())) {
					queryTree.addItem(fil);
				}

			}
		}
		else
			MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), "No data, check if your table are explorable and not corrupted");
	}

	private String getColumnStyle(FmdtColumn column) {
		switch (column.getType()) {
		case UNDEFINED:
			return style.colUndefined();
		case DIMENSION:
			return style.colDimension();
		case COUNTRY:
			return style.colDimension();
		case CITY:
			return style.colDimension();
		case MEASURE:
			return style.colMeasure();
		case PROPERTY:
			return style.colProperty();
		default:
			break;
		}
		return style.colUndefined();
	}

	private static class QueryRow {
		private final String name;
		private final String label;
		private String value;
		private String operator;
		private FmdtData data;

		public QueryRow(String name, String label, String value, String operator, FmdtData data) {
			this.name = name;
			this.label = label;
			this.value = value;
			this.operator = operator;
			this.data = data;
		}

		public FmdtData getData() {
			return data;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

	}

	private void initColumnTable() {
		CustomCell cell = new CustomCell();
		Column<QueryRow, String> fieldColumn = new Column<QueryRow, String>(cell) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.label;
			}
		};

		Column<QueryRow, String> aggregateColumn = new Column<QueryRow, String>(new CustomSelectionCell(aggregList)) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.operator;
			}
		};
		aggregateColumn.setFieldUpdater(new FieldUpdater<QueryRow, String>() {
			@Override
			public void update(int index, QueryRow object, String value) {
				object.operator = value;
				change = true;
			}
		});

		Column<QueryRow, String> labelColumn = new Column<QueryRow, String>(cell) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.label;
			}
		};

		Column<QueryRow, String> ordinalColumn = new Column<QueryRow, String>(cell) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.value;
			}
		};
		queryColumnTable.setSize("100%", "100%");
		queryColumnTable.addColumn(fieldColumn, LabelsConstants.lblCnst.Field());
		queryColumnTable.setColumnWidth(fieldColumn, 27.0, Unit.PCT);
		queryColumnTable.addColumn(aggregateColumn, LabelsConstants.lblCnst.Aggregate());
		queryColumnTable.setColumnWidth(aggregateColumn, 23.0, Unit.PCT);
		queryColumnTable.addColumn(labelColumn, LabelsConstants.lblCnst.Label());
		queryColumnTable.setColumnWidth(labelColumn, 27.0, Unit.PCT);
		queryColumnTable.addColumn(ordinalColumn, LabelsConstants.lblCnst.OrdinalPosition());
		queryColumnTable.setColumnWidth(ordinalColumn, 23.0, Unit.PCT);
		panelData.setWidget(queryColumnTable);
		queryColumnTable.setSelectionModel(selectionModel);
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
			QueryRow obj = (QueryRow) context.getKey();
			dispatchAction(obj, event);
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}

	private class CustomSelectionCell extends SelectionCell {

		public CustomSelectionCell(List<String> options) {
			super(options);
		}

		@Override
		public Set<String> getConsumedEvents() {
			Set<String> consumedEvents = new HashSet<String>(super.getConsumedEvents());
			consumedEvents.add("dblclick");
			return consumedEvents;
		}

		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
			QueryRow obj = (QueryRow) context.getKey();
			dispatchAction(obj, event);
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
	}
	
	public void dispatchAction(QueryRow obj, NativeEvent event) {
		if (event.getType().equals("dblclick")) {
			if (obj != null) {
				QueryRow selectedRow = obj;
				if (selectedRow.getData() instanceof FmdtFilter) {

					resources.getList().remove(selectedRow);
					queryResourceTable.setVisibleRangeAndClearData(queryResourceTable.getVisibleRange(), true);
					redrawResources();
				}
				else {
					int index = columns.getList().indexOf(selectedRow);
					for (int i = index; i < columns.getList().size(); i++)
						columns.getList().get(i).setValue(String.valueOf(Integer.parseInt(columns.getList().get(i).getValue()) - 1));
					columns.getList().remove(selectedRow);
					queryColumnTable.setVisibleRangeAndClearData(queryColumnTable.getVisibleRange(), true);
					redrawColumn();
					noSel();
				}
				selectedRow = null;
			}
		}
	}

	private void initResourceTable() {
		CustomCell cell = new CustomCell();
		Column<QueryRow, String> resourceColumn = new Column<QueryRow, String>(cell) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.name;
			}
		};

		Column<QueryRow, String> labelColumn = new Column<QueryRow, String>(cell) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.label;
			}
		};

		Column<QueryRow, String> operatorColumn = new Column<QueryRow, String>(cell) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.operator;
			}
		};

		Column<QueryRow, String> valueColumn = new Column<QueryRow, String>(cell) {
			@Override
			public String getValue(QueryRow queryRow) {
				return queryRow.value;
			}
		};
		queryResourceTable.setSize("100%", "100%");
		queryResourceTable.addColumn(resourceColumn, LabelsConstants.lblCnst.Resource());
		queryResourceTable.setColumnWidth(resourceColumn, 25.0, Unit.PCT);
		queryResourceTable.addColumn(labelColumn, LabelsConstants.lblCnst.Field());
		queryResourceTable.setColumnWidth(labelColumn, 25.0, Unit.PCT);
		queryResourceTable.addColumn(operatorColumn, LabelsConstants.lblCnst.Operator());
		queryResourceTable.setColumnWidth(operatorColumn, 20.0, Unit.PCT);
		queryResourceTable.addColumn(valueColumn, LabelsConstants.lblCnst.Value());
		queryResourceTable.setColumnWidth(valueColumn, 30.0, Unit.PCT);
		panelResources.setWidget(queryResourceTable);
		queryResourceTable.setSelectionModel(selectionModel);
	}

	private ListDataProvider<QueryRow> initColumnRow() {
		ListDataProvider<QueryRow> provider = new ListDataProvider<QueryRow>();
		List<QueryRow> rows = new ArrayList<DesignerPanel.QueryRow>();
		for (FmdtData column : builder.getListColumns()) {
			QueryRow row = null;
			if (column instanceof FmdtColumn)
				row = new QueryRow(column.getName(), column.getLabel(), String.valueOf(rows.size() + 1), "None", (FmdtData) column);
			if (column instanceof FmdtFormula)
				row = new QueryRow(column.getName(), column.getName(), String.valueOf(rows.size() + 1), ((FmdtFormula) column).getOperator(), (FmdtData) column);
			if (column instanceof FmdtAggregate)
				row = new QueryRow(((FmdtAggregate) column).getOutputName(), ((FmdtAggregate) column).getOutputName(), String.valueOf(rows.size() + 1), ((FmdtAggregate) column).getOperator(), (FmdtData) column);

			rows.add(row);
		}
		provider.setList(rows);
		return provider;
	}

	private ListDataProvider<QueryRow> initResourceRow() {
		ListDataProvider<QueryRow> provider = new ListDataProvider<QueryRow>();
		List<QueryRow> rows = new ArrayList<DesignerPanel.QueryRow>();

		for (FmdtFilter filter : builder.getListFilters()) {
			String value = null;
			if (filter.getType().equals(FmdtConstant.FILTERPROMPT))
				value = "?";
			if (filter.getType().equals(FmdtConstant.FILTERCOMPLEX)) {
				for (String val : filter.getValues()) {
					if (value != null)
						value += ", " + val;
					else
						value = val;
				}
			}
			QueryRow row = new QueryRow(filter.getName(), filter.getColumn().getLabel(), value, filter.getOperator(), (FmdtData) filter);
			if (filter.getType().equals(FmdtConstant.FILTERSQL))
				row.setOperator(filter.getQuery());

			rows.add(row);
		}

		provider.setList(rows);
		return provider;
	}

	private void setSelected(boolean selected, TreeItem widget) {
		if (!selected) {
			widget.removeStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		else {
			widget.addStyleName(VanillaCSS.TREE_ITEM_SELECTED);
		}
		widget.setSelected(selected);
	}

	public FmdtQueryBuilder refreshBuilder() {
		List<FmdtColumn> listCols = new ArrayList<FmdtColumn>();
		List<FmdtAggregate> aggregates = new ArrayList<FmdtAggregate>();
		List<FmdtFormula> formulas = new ArrayList<FmdtFormula>();

		List<FmdtFilter> filters = new ArrayList<FmdtFilter>();
		List<FmdtFilter> promptFilters = new ArrayList<FmdtFilter>();

		List<FmdtData> currListCols = new ArrayList<FmdtData>();
		List<FmdtFilter> currListFilters = new ArrayList<FmdtFilter>();

		for (QueryRow row : queryColumnTable.getVisibleItems()) {
			if (row.getData() instanceof FmdtColumn) {
				if (row.getOperator().equals("None")) {
					listCols.add((FmdtColumn) row.getData());
					currListCols.add((FmdtColumn) row.getData());
				}
				else {
					FmdtAggregate aggregate = new FmdtAggregate();
					FmdtColumn col = (FmdtColumn) row.getData();
					aggregate.setCol(col.getName());
					aggregate.setName(col.getLabel());
					aggregate.setOutputName(col.getLabel());
					aggregate.setTable(col.getTableName());
					aggregate.setOperator(row.getOperator());
					aggregate.setJavaType(col.getJavaType());
					aggregate.setSqlType(col.getSqlType());
					aggregate.setBasedOnFormula(false);

					aggregates.add(aggregate);
					currListCols.add(aggregate);
				}
			}
			if (row.getData() instanceof FmdtFormula) {
				if (row.getOperator().equals("None")) {
					formulas.add((FmdtFormula) row.getData());
					currListCols.add((FmdtFormula) row.getData());
				}
				else {
					FmdtAggregate aggregate = new FmdtAggregate();
					FmdtFormula col = (FmdtFormula) row.getData();
					aggregate.setName(col.getName());
					aggregate.setOutputName(col.getLabel());
					aggregate.setOperator(row.getOperator());
					aggregate.setFunction(col.getScript());
					aggregate.setFormulaData(col.getDataStreamInvolved());
					aggregate.setBasedOnFormula(true);

					aggregates.add(aggregate);
					currListCols.add(aggregate);
				}
			}
			if (row.getData() instanceof FmdtAggregate) {
				FmdtAggregate aggregate = (FmdtAggregate) row.getData();
				if (row.getOperator().equals("None")) {
					if (aggregate.getFunction() != null && !aggregate.getFunction().isEmpty()) {
						FmdtFormula formula = new FmdtFormula(aggregate.getName(), aggregate.getDescription(), aggregate.getFunction(), aggregate.getFormulaData());
						formula.setCreated(true);
						formulas.add(formula);
						currListCols.add(formula);
					}
					else {
						FmdtColumn col = new FmdtColumn(aggregate.getName(), aggregate.getOutputName(), aggregate.getDescription(), aggregate.getTable());
						col.setJavaType(aggregate.getJavaType());
						col.setSqlType(aggregate.getSqlType());
						listCols.add(col);
						currListCols.add(col);
					}
				}
				else {
					aggregate.setOperator(row.getOperator());
					aggregates.add(aggregate);
					currListCols.add(aggregate);
				}
			}
		}
		for (QueryRow row : queryResourceTable.getVisibleItems()) {
			FmdtFilter filter = (FmdtFilter) row.getData();
			currListFilters.add(filter);
			if (filter.getType().equals(FmdtConstant.FILTERPROMPT))
				promptFilters.add(filter);
			else
				filters.add(filter);
		}

		builder.setAggregates(aggregates);
		builder.setColumns(listCols);
		builder.setFormulas(formulas);
		builder.setFilters(filters);
		builder.setPromptFilters(promptFilters);

		builder.setListColumns(currListCols);
		builder.setListFilters(currListFilters);

		builder.setDistinct(checkboxDistinct.getValue());
		builder.setLimit(checkboxLimit.getValue());
		if (checkboxLimit.getValue() && !txtLimit.getText().isEmpty())
			builder.setNbLimit(Integer.parseInt(txtLimit.getText()));
		else
			builder.setNbLimit(0);

		return builder;
	}

	public FmdtQueryBuilder getBuilder() {
		return builder;
	}

	public void addButtonToolbar(ImageResource resource, String title, ClickHandler clickHandler) {
		Image imgToolbar = new Image(resource);
		imgToolbar.setTitle(title);
		imgToolbar.addClickHandler(clickHandler);
		imgToolbar.addStyleName(style.btnRequestBuilder());

		panelButtons.add(imgToolbar);
	}

	public class NumbersOnlyKeyPressHandler implements KeyPressHandler {
		@Override
		public void onKeyPress(KeyPressEvent event) {
			if (event.getNativeEvent().getKeyCode() != KeyCodes.KEY_DELETE && event.getNativeEvent().getKeyCode() != KeyCodes.KEY_BACKSPACE && event.getNativeEvent().getKeyCode() != KeyCodes.KEY_LEFT && event.getNativeEvent().getKeyCode() != KeyCodes.KEY_RIGHT) {
				String c = event.getCharCode() + "";
				if (RegExp.compile("[^0-9]").test(c))
					txtLimit.cancelKey();
			}
		}
	}

	private void redrawPanelButton() {
		if (Window.getClientHeight() < 720) {
			panelValue.addStyleName(style.panelValuedef2());
			panelButtons.setVisible(false);
			doublePanelButton.setVisible(true);
			initdoublebuttonPanel();
		}
		else {
			doublePanelButton.setVisible(false);
			panelValue.removeStyleName(style.panelValuedef2());
			panelButtons.setVisible(true);
			initbuttonPanel();
		}
	}

	private void initdoublebuttonPanel() {
		if (panelButtons1.getWidgetCount() < 5) {
			panelButtons1.clear();
			panelButtons1.add(btnAdd);
			panelButtons1.add(btnRemove);
			panelButtons1.add(btnUp);
			panelButtons1.add(btnDown);
			panelButtons1.add(btnCreateComputed);
			panelButtons1.add(btnCreateComplex);

			panelButtons2.clear();
			panelButtons2.add(btnCreateSql);
			panelButtons2.add(btnCreatePrompt);
			panelButtons2.add(btnEdit);
			panelButtons2.add(btnViewQuery);
			panelButtons2.add(btnClear);
		}
	}

	private void initbuttonPanel() {
		if (panelButtons.getWidgetCount() < 5) {
			panelButtons.clear();
			panelButtons.add(btnAdd);
			panelButtons.add(btnRemove);
			panelButtons.add(btnUp);
			panelButtons.add(btnDown);
			panelButtons.add(btnCreateComputed);
			panelButtons.add(btnCreateComplex);
			panelButtons.add(btnCreateSql);
			panelButtons.add(btnCreatePrompt);
			panelButtons.add(btnEdit);
			panelButtons.add(btnViewQuery);
			panelButtons.add(btnClear);
		}
	}

	private void noSel() {
		btnAdd.removeStyleName(style.hover());
		btnRemove.removeStyleName(style.hover());
		btnDown.removeStyleName(style.hover());
		btnUp.removeStyleName(style.hover());
		btnEdit.removeStyleName(style.hover());

		btnAdd.setResource(CommonImages.INSTANCE.add_gray_24());
		btnRemove.setResource(CommonImages.INSTANCE.delete_gray_24());
		btnDown.setResource(CommonImages.INSTANCE.ic_down_gray());
		btnUp.setResource(CommonImages.INSTANCE.ic_up_gray());
		btnEdit.setResource(CommonImages.INSTANCE.ic_create_gray());
	}

	private void tablesColumnSel() {
		btnRemove.removeStyleName(style.hover());
		btnDown.removeStyleName(style.hover());
		btnUp.removeStyleName(style.hover());

		btnAdd.addStyleName(style.hover());

		btnAdd.setResource(CommonImages.INSTANCE.add_24());

		btnRemove.setResource(CommonImages.INSTANCE.delete_gray_24());
		btnDown.setResource(CommonImages.INSTANCE.ic_down_gray());
		btnUp.setResource(CommonImages.INSTANCE.ic_up_gray());
		btnEdit.setResource(CommonImages.INSTANCE.ic_create_gray());
	}

	private void queryColumnSel() {
		btnAdd.removeStyleName(style.hover());

		btnRemove.addStyleName(style.hover());
		btnDown.addStyleName(style.hover());
		btnUp.addStyleName(style.hover());
		btnEdit.addStyleName(style.hover());

		btnAdd.setResource(CommonImages.INSTANCE.add_gray_24());

		btnRemove.setResource(CommonImages.INSTANCE.delete_24());
		btnDown.setResource(CommonImages.INSTANCE.ic_down());
		btnUp.setResource(CommonImages.INSTANCE.ic_up());
		btnEdit.setResource(CommonImages.INSTANCE.ic_create_black_36dp());
	}

	private void resourcesSel() {
		btnAdd.removeStyleName(style.hover());
		btnDown.removeStyleName(style.hover());
		btnUp.removeStyleName(style.hover());

		btnRemove.addStyleName(style.hover());
		btnEdit.addStyleName(style.hover());

		btnAdd.setResource(CommonImages.INSTANCE.add_gray_24());
		btnDown.setResource(CommonImages.INSTANCE.ic_down_gray());
		btnUp.setResource(CommonImages.INSTANCE.ic_up_gray());

		btnRemove.setResource(CommonImages.INSTANCE.delete_24());
		btnEdit.setResource(CommonImages.INSTANCE.ic_create_black_36dp());
	}

	public Boolean isChange() {
		selectionModel.clear();
		noSel();
		if (!builder.getPromptFilters().isEmpty())
			return true;
		return change;
	}

	public void setChange(Boolean change) {
		this.change = change;
	}

	private void redrawColumn() {
		queryColumnTable.redraw();
		change = true;
	}

	private void redrawResources() {
		queryResourceTable.redraw();
		change = true;
	}

}
