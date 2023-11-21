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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class PromptFilterDialog extends AbstractDialogBox {

	private static PromptFilterDialogUiBinder uiBinder = GWT.create(PromptFilterDialogUiBinder.class);

	interface PromptFilterDialogUiBinder extends UiBinder<Widget, PromptFilterDialog> {
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
	SimplePanel panelColumn;

	private FmdtFilter filterPrompt;
	private FmdtQueryDatas queryDatas;
	private final Tree queryTree = new Tree();

	public static final String[] OPERATORS = new String[] { "=", "<", "<=", ">", ">=", "!=", "<>", "IN", "NOT IN", "LIKE", "BETWEEN" };
	Boolean confirm = false;
	FmdtColumn column = null;

	public PromptFilterDialog(FmdtColumn column, FmdtQueryDatas queryDatas) {
		super(LabelsConstants.lblCnst.PromptFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.column = column;
		this.queryDatas = queryDatas;
		init();
	}

	public PromptFilterDialog(FmdtFilter filter, FmdtQueryDatas queryDatas) {
		super(LabelsConstants.lblCnst.PromptFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.column = filter.getColumn();
		this.queryDatas = queryDatas;
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
	}

	public PromptFilterDialog(FmdtQueryDatas queryDatas) {
		super(LabelsConstants.lblCnst.PromptFilter(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.queryDatas = queryDatas;
		init();

	}

	private void init() {
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		for (String op : OPERATORS) {
			lstOperatore.addItem(op);
		}
		lstOperatore.setItemSelected(0, true);
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
				TreeItem subTable = new TreeItem();
				subTable.setText(subt.getLabel());
				subTable.setUserObject(subt);
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
					subTable.addItem(subcol);
				}
				tab.addItem(subTable);
			}

			queryTree.addItem(tab);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (column != null && !txtName.getText().isEmpty()) {
				confirm = true;
				List<String> valueList = new ArrayList<String>();
				valueList.add("?");
				filterPrompt = new FmdtFilter(txtName.getValue(), txtName.getValue(), "", column, lstOperatore.getValue(lstOperatore.getSelectedIndex()), valueList, FmdtConstant.FILTERPROMPT);
				filterPrompt.setCreate(true);
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

	public FmdtFilter getFilterPrompt() {
		return filterPrompt;
	}

	public Boolean getConfirm() {
		return confirm;
	}

}
