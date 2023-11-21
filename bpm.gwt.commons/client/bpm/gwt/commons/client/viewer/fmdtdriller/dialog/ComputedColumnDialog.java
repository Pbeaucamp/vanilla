package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDatas;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtFormula;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ComputedColumnDialog extends AbstractDialogBox {

	private static ComputedColumnDialogUiBinder uiBinder = GWT.create(ComputedColumnDialogUiBinder.class);

	interface ComputedColumnDialogUiBinder extends UiBinder<Widget, ComputedColumnDialog> {
	}

	interface MyStyle extends CssResource {
	}

	private FmdtQueryDatas queryDatas;
	private List<String> valueList = new ArrayList<String>();
	private FmdtFormula formula;

	public static final String[] OPERATORS = new String[] { "+", "-", "*", "/" };
	private final Tree queryTree = new Tree();

	Boolean confirm = false;
	FmdtColumn column;

	@UiField
	MyStyle style;

	@UiField
	TextBox txtName;

	@UiField
	TextArea computedValue;

	@UiField
	SimplePanel panelColumn;

	public ComputedColumnDialog(FmdtQueryDatas datas) {
		super(LabelsConstants.lblCnst.ComputedField(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		this.queryDatas = datas;
		initColumns();
		panelColumn.add(queryTree);

		queryTree.sinkEvents(Event.ONDBLCLICK);
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	public ComputedColumnDialog(FmdtQueryDatas datas, FmdtFormula formula) {
		super(LabelsConstants.lblCnst.ComputedField(), false, true);
		setWidget(uiBinder.createAndBindUi(this));

		this.queryDatas = datas;
		initColumns();
		panelColumn.add(queryTree);

		queryTree.sinkEvents(Event.ONDBLCLICK);
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		txtName.setText(formula.getName());
		computedValue.setText(formula.getScript());
	}

	private void initColumns() {
		TreeItem op = new TreeItem();
		op.setText("Operators");
		for (String opVal : OPERATORS) {
			HTML html = new HTML();
			html.addDoubleClickHandler(new DoubleClickHandler() {
				@Override
				public void onDoubleClick(DoubleClickEvent event) {
					HTML htmlItem = (HTML) event.getSource();
					TreeItem item = (TreeItem) ((Tree) htmlItem.getParent()).getSelectedItem();
					if (item.getParentItem() != null && item.getParentItem().getText().equals("Operators"))
						computedValue.setText(computedValue.getText() + item.getText());
				}
			});
			html.setText(opVal);
			TreeItem val = new TreeItem(html);
			op.addItem(val);
		}
		queryTree.addItem(op);
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
							if (col.getFormula() == null) {
//								if (col.getName().contains(".")) {
//									computedValue.setText(computedValue.getText() + col.getName());
//								} else {
									computedValue.setText(computedValue.getText() + col.getTableOriginName() + "." + col.getName());
//								}
							} else
								computedValue.setText(computedValue.getText() + col.getFormula());
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
				for (FmdtColumn subCol : subt.getColumns()) {
					HTML html = new HTML();
					html.addDoubleClickHandler(new DoubleClickHandler() {
						@Override
						public void onDoubleClick(DoubleClickEvent event) {
							HTML htmlItem = (HTML) event.getSource();
							TreeItem item = (TreeItem) ((Tree) htmlItem.getParent()).getSelectedItem();
							if (item.getUserObject() != null && item.getUserObject() instanceof FmdtColumn) {
								FmdtColumn col = (FmdtColumn) item.getUserObject();
								if (col.getFormula() == null) {
//									if (col.getName().contains(".")) {
//										computedValue.setText(computedValue.getText() + col.getName());
//									} else {
//										computedValue.setText(computedValue.getText() + col.getOriginName());
										computedValue.setText(computedValue.getText() + col.getTableOriginName() + "." + col.getName());
//									}
								} else
									computedValue.setText(computedValue.getText() + col.getFormula());
							}
						}
					});
					html.setText(subCol.getLabel());
					TreeItem subit = new TreeItem(html);
					subit.setUserObject(subCol);
					subtable.addItem(subit);
				}
				tab.addItem(subtable);
			}
			queryTree.addItem(tab);
		}
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			if (!txtName.getText().isEmpty() && !computedValue.getText().isEmpty()) {
				confirm = true;

				for (FmdtTableStruct table : queryDatas.getTables()) {

					for (FmdtColumn column : table.getColumns()) {
						if (column.getOriginName() != null && computedValue.getText().contains(column.getOriginName() + ".")) {
							valueList.add(column.getTableOriginName());
						}
					}
				}
				formula = new FmdtFormula(txtName.getText(), "", computedValue.getText(), valueList);
				formula.setCreated(true);

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

	public FmdtFormula getFormula() {
		return formula;
	}

	public Boolean getConfirm() {
		return confirm;
	}

}
