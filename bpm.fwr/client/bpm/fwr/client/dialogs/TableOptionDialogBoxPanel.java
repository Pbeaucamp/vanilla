package bpm.fwr.client.dialogs;

import bpm.fwr.client.widgets.GridOptions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TableOptionDialogBoxPanel extends Composite implements ICustomPanel {

	private static TableOptionDialogBoxPanelUiBinder uiBinder = GWT
			.create(TableOptionDialogBoxPanelUiBinder.class);

	interface TableOptionDialogBoxPanelUiBinder extends
			UiBinder<Widget, TableOptionDialogBoxPanel> {
	}
	
	@UiField
	TextBox txtNbColumns, txtNbRows;
	
	private ICustomDialogBox parent;
	private GridOptions options;

	public TableOptionDialogBoxPanel(ICustomDialogBox parent, GridOptions options) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.options = options;
		
		txtNbColumns.addChangeHandler(changeHandler);
		txtNbRows.addChangeHandler(changeHandler);
	}
	
	private ChangeHandler changeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			if(event.getSource().equals(txtNbColumns)){
				try {
					options.setNbCols(Integer.parseInt(txtNbColumns.getText()));
				}catch (Exception e) {
					e.printStackTrace();
					options.setNbCols(0);
				}
			}
			else if(event.getSource().equals(txtNbRows)){
				try {
					options.setNbRows(Integer.parseInt(txtNbRows.getText()));
				}catch (Exception e) {
					e.printStackTrace();
					options.setNbRows(0);
				}
			}
			parent.updateBtn();
		}
	};

	@Override
	public boolean isComplete() {
		return true;
	}
}
