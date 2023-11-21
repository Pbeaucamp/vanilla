package bpm.fm.designer.web.client.dialog;

import java.util.List;

import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.images.Images;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.platform.core.beans.data.DatabaseColumn;
import bpm.vanilla.platform.core.beans.data.DatabaseTable;
import bpm.vanilla.platform.core.beans.data.IDatabaseObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ColumnTableSelectionDialog extends AbstractDialogBox {

	private static ColumnTableSelectionDialogUiBinder uiBinder = GWT.create(ColumnTableSelectionDialogUiBinder.class);

	interface ColumnTableSelectionDialogUiBinder extends UiBinder<Widget, ColumnTableSelectionDialog> {
	}

	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	Tree databaseTree;
	
	@UiField
	TextArea txtFormula;

	private int datasourceId;

	private TextBox txtToFill;

	private boolean tableOnly;

	private String tableFilter;
	
	public ColumnTableSelectionDialog(int datasourceId, TextBox txtToFill, boolean tableOnly) {
		this(datasourceId, txtToFill, tableOnly, null);
	}

	public ColumnTableSelectionDialog(int datasourceId, TextBox txtTofill, boolean tableOnly, String tableFilter) {
		super(Messages.lbl.columnTableSelection(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		txtFormula.getElement().setPropertyString("placeholder", Messages.lbl.elementFormula());
		this.datasourceId = datasourceId;
		this.txtToFill = txtTofill;
		this.tableOnly = tableOnly;
		this.tableFilter = tableFilter;
		createButtonBar(Messages.lbl.Ok(), okHandler, Messages.lbl.Cancel(), cancelHandler);
		
		fillTree();
		
		databaseTree.addSelectionHandler(new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				//XXX since we can't define calculations yet, we replace the textArea content.
				
//				String formula = txtFormula.getText();
				
//				if(formula == null || formula.isEmpty()) {
					txtFormula.setText(((IDatabaseObject)event.getSelectedItem().getUserObject()).getName());
//				}
//				else {
//					int cursor = txtFormula.getCursorPos();
//					
//					String start = formula.substring(0, cursor);
//					String end = formula.substring(cursor, formula.length());
//					
//					formula = start + ((IDatabaseObject)event.getSelectedItem().getUserObject()).getName() + end;
//					
//					txtFormula.setText(formula);
//				}
			}
		});
	}

	private void fillTree() {
		MetricService.Connection.getInstance().getDatabaseStructure(datasourceId, new AsyncCallback<List<DatabaseTable>>() {
			
			@Override
			public void onSuccess(List<DatabaseTable> result) {
				databaseTree.clear();
				for(DatabaseTable table : result) {
					if(tableFilter != null && !tableFilter.isEmpty()) {
						if(!table.getName().equals(tableFilter)) {
							continue;
						}
					}
					TreeItem tableItem = new TreeItem(new HTML(new Image(Images.INSTANCE.folder()) + " " + table.getName()));
					tableItem.setUserObject(table);
					databaseTree.addItem(tableItem);
					if(!tableOnly) {
						for(DatabaseColumn col : table.getColumns()) {
							TreeItem colItem = new TreeItem(new HTML(new Image(Images.INSTANCE.column()) + " " + col.getName()));
							colItem.setUserObject(col);
							tableItem.addItem(colItem);
						}
					}
					if(tableFilter != null && !tableFilter.isEmpty()) {
						tableItem.setState(true);
					}
				}
				contentPanel.add(databaseTree);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.databaseStructureError(), caught.getMessage(), caught);
				dial.center();
			}
		});
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			txtToFill.setText(txtFormula.getText());
			ColumnTableSelectionDialog.this.hide();
		}
	};
	
	private ClickHandler cancelHandler = new ClickHandler() {	
		@Override
		public void onClick(ClickEvent event) {
			ColumnTableSelectionDialog.this.hide();
		}
	};
}
