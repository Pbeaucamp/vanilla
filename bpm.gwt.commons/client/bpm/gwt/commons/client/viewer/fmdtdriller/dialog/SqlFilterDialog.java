package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.FmdtConstant;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtTableStruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class SqlFilterDialog extends AbstractDialogBox {

	private static SqlFilterDialogUiBinder uiBinder = GWT.create(SqlFilterDialogUiBinder.class);

	interface SqlFilterDialogUiBinder extends UiBinder<Widget, SqlFilterDialog> {
	}

	interface MyStyle extends CssResource {
	}

	@UiField
	MyStyle style;

	@UiField
	TextBox txtName, txtValue, txtField;

	@UiField
	SimplePanel panelColumn;

	Boolean confirm = false;
	FmdtColumn column;
	FmdtFilter filterSql;
	private FmdtQueryDatas queryDatas;
	private final Tree queryTree = new Tree();

	public SqlFilterDialog(FmdtColumn column, FmdtQueryDatas queryDatas) {
		super(LabelsConstants.lblCnst.SqlFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.queryDatas = queryDatas;
		this.column = column;
		init();
	}

	public SqlFilterDialog(FmdtFilter filter, FmdtQueryDatas queryDatas) {
		super(LabelsConstants.lblCnst.SqlFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.queryDatas = queryDatas;
		this.column = filter.getColumn();
		init();

		txtName.setText(filter.getName());
		txtValue.setText(filter.getQuery());
	}

	public SqlFilterDialog(FmdtQueryDatas queryDatas) {
		super(LabelsConstants.lblCnst.SqlFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.queryDatas = queryDatas;
		init();
	}

	private void init() {
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		if (column != null)
			txtField.setText(column.getName());

		txtField.setEnabled(false);
		initColumns();
		panelColumn.add(queryTree);
		queryTree.sinkEvents(Event.ONDBLCLICK);
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
				TreeItem subtable = new TreeItem();
				subtable.setText(subt.getLabel());
				subtable.setUserObject(subt);
				for (FmdtColumn col : table.getColumns()) {
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
					subtable.addItem(subcol);
				}
				tab.addItem(subtable);
			}

			queryTree.addItem(tab);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (column != null && !txtName.getText().isEmpty()) {
				confirm = true;
				filterSql = new FmdtFilter(txtName.getValue(), txtName.getValue(), "", txtValue.getValue(), column, FmdtConstant.FILTERSQL);
				filterSql.setCreate(true);
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

	public FmdtFilter getFilterSql() {
		return filterSql;
	}

	public Boolean getConfirm() {
		return confirm;
	}

}
