package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.FmdtConstant;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtTableStruct;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.ListDataProvider;

public class ComplexFilterDialog extends AbstractDialogBox {

	private static ComplexFilterDialogUiBinder uiBinder = GWT.create(ComplexFilterDialogUiBinder.class);

	interface ComplexFilterDialogUiBinder extends UiBinder<Widget, ComplexFilterDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	TextBox txtName, txtField;

	@UiField
	ListBox lstOperatore;

	@UiField
	Image btnAdd, btnRemove;

	@UiField
	SimplePanel panelDatagrid;

	@UiField
	SimplePanel panelColumn;

	private final DataGrid<String> values = new DataGrid<String>();
	private List<String> valueList = new ArrayList<String>();
	private ListDataProvider<String> valueDataprovider = new ListDataProvider<String>();
	private String selectedValue = null;
	private FmdtFilter filterComplex;
	private FmdtQueryDatas queryDatas;
	private final Tree queryTree = new Tree();

	public static final String[] OPERATORS = new String[] { "=", "<", "<=", ">", ">=", "!=", "<>", "IN", "NOT IN", "LIKE", "BETWEEN" };
	Boolean confirm = false;
	FmdtColumn column = null;

	public ComplexFilterDialog(FmdtColumn column, FmdtQueryDatas datas) {
		super(LabelsConstants.lblCnst.ComplexFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.column = column;
		this.queryDatas = datas;
		init();
		lstOperatore.setItemSelected(0, true);
	}

	public ComplexFilterDialog(FmdtFilter filter, FmdtQueryDatas datas) {
		super(LabelsConstants.lblCnst.ComplexFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.column = filter.getColumn();
		this.queryDatas = datas;
		init();

		txtName.setText(filter.getName());

		int index = 0;
		for (int i = 0; i < lstOperatore.getItemCount(); i++) {
			if (lstOperatore.getItemText(i).equals(filter.getOperator())) {
				index = i;
				break;
			}
		}
		lstOperatore.setItemSelected(index, true);

		valueList = filter.getValues();
		valueDataprovider.setList(valueList);
	}

	public ComplexFilterDialog(FmdtQueryDatas datas) {
		super(LabelsConstants.lblCnst.ComplexFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.queryDatas = datas;
		init();
		lstOperatore.setItemSelected(0, true);
	}

	private void init() {
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		for (String op : OPERATORS) {
			lstOperatore.addItem(op);
		}

		initValueTable();
		values.addCellPreviewHandler(new CellPreviewEvent.Handler<String>() {
			@Override
			public void onCellPreview(CellPreviewEvent<String> event) {
				if ("click".equals(event.getNativeEvent().getType())) {
					selectedValue = valueList.get(values.getKeyboardSelectedRow());
				}
			}
		});
		values.sinkEvents(Event.ONCLICK);

		if (column != null)
			txtField.setText(column.getLabel());

		initColumns();
		panelColumn.add(queryTree);
		queryTree.sinkEvents(Event.ONDBLCLICK);

		txtField.setEnabled(false);
	}

	public void initValueTable() {

		Column<String, String> value = new Column<String, String>(new EditTextCell()) {
			@Override
			public String getValue(String object) {
				return object == null ? "" : object;
			}
		};

		value.setFieldUpdater(new FieldUpdater<String, String>() {
			@Override
			public void update(int index, String object, String value) {
				valueList.remove(index);
				valueList.add(index, value);
				object = value;
			}
		});

		values.addColumn(value, LabelsConstants.lblCnst.Value());
		values.setSize("100%", "100%");
		valueDataprovider.addDataDisplay(values);
		panelDatagrid.setWidget(values);
	}

	private void initColumns() {
		for (FmdtTableStruct table : queryDatas.getTables()) {
			TreeItem tab = new TreeItem();
			tab.setText(table.getLabel());
			tab.setUserObject(table);
			for (FmdtColumn column : table.getColumns()) {
				HTML html = new HTML();
				html.addDoubleClickHandler(new DoubleClickHandler() {
					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						HTML htmlItem = (HTML) event.getSource();
						TreeItem item = (TreeItem) ((Tree) htmlItem.getParent()).getSelectedItem();
						if (item.getUserObject() != null && item.getUserObject() instanceof FmdtColumn) {
							FmdtColumn col = (FmdtColumn) item.getUserObject();
							setColumn(col);
						}
					}
				});
				html.setText(column.getLabel());
				TreeItem col = new TreeItem(html);
				col.setUserObject(column);
				tab.addItem(col);
			}
			for (FmdtTableStruct subt : table.getSubTables()) {
				TreeItem subtab = new TreeItem();
				subtab.setText(subt.getLabel());
				subtab.setUserObject(subt);
				for (FmdtColumn col : subt.getColumns()) {
					HTML html = new HTML();
					html.addDoubleClickHandler(new DoubleClickHandler() {
						@Override
						public void onDoubleClick(DoubleClickEvent event) {
							HTML htmlItem = (HTML) event.getSource();
							TreeItem item = (TreeItem) ((Tree) htmlItem.getParent()).getSelectedItem();
							if (item.getUserObject() != null && item.getUserObject() instanceof FmdtColumn) {
								FmdtColumn col = (FmdtColumn) item.getUserObject();
								setColumn(col);
							}
						}
					});
					html.setText(col.getLabel());
					TreeItem subcol = new TreeItem(html);
					subcol.setUserObject(col);
					subtab.addItem(subcol);
				}
				tab.addItem(subtab);
			}
			
			queryTree.addItem(tab);
		}
	}

	@UiHandler("btnAdd")
	public void onAddClick(ClickEvent event) {

		final FilterValueDialog valueDialog = new FilterValueDialog();

		valueDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (valueDialog.isConfirm()) {
					String operator = lstOperatore.getValue(lstOperatore.getSelectedIndex());
					String value = valueDialog.getValue();
					if (operator.equals("IN") || operator.equals("NOT IN")) {
						valueList.add(value);
					} else if (operator.equals("BETWEEN")) {
						if (valueList.size() < 2) {
							valueList.add(value);
						} else {
							valueList.clear();
							valueList.add(value);
						}
					} else {
						valueList.clear();
						valueList.add(value);
					}
				}

				valueDataprovider.setList(valueList);
			}
		});

		valueDialog.center();

	}

	@UiHandler("btnRemove")
	public void onRemoveClick(ClickEvent event) {
		if (selectedValue != null) {
			valueList.remove(selectedValue);
			values.setVisibleRangeAndClearData(values.getVisibleRange(), true);
			values.setRowData(0, valueList);
			values.redraw();
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (column != null && !txtName.getText().isEmpty()) {
				confirm = true;
				filterComplex = new FmdtFilter(txtName.getValue(), txtName.getValue(), "", column, lstOperatore.getValue(lstOperatore.getSelectedIndex()), valueList, FmdtConstant.FILTERCOMPLEX);
				filterComplex.setCreate(true);
				hide();
			} else
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), "Fill all required fields");
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			hide();
		}
	};

	private void setColumn(FmdtColumn column) {
		this.column = column;
		txtField.setText(column.getLabel());
	}

	public FmdtFilter getFilterComplex() {
		return filterComplex;
	}

	public Boolean getConfirm() {
		return confirm;
	}

}
