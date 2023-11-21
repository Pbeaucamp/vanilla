package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.List;

import bpm.document.management.core.model.ILog;
import bpm.document.management.core.model.Log;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;
import bpm.master.core.model.InstanceLog;
import bpm.master.core.model.OzwilloRequest;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class InstanceLogDialog extends AbstractDialogBox {
	
	private static InstanceLogDialogUiBinder uiBinder = GWT
			.create(InstanceLogDialogUiBinder.class);

	interface InstanceLogDialogUiBinder extends
			UiBinder<Widget, InstanceLogDialog> {
	}

	@UiField
	SimplePanel panelContent;
	
	private List<InstanceLog> log;
	private DataGrid<InstanceLog> datagrid;
	
	public InstanceLogDialog(List<InstanceLog> log) {
		super(LabelsConstants.lblCnst.logs(), false, true);
		setWidget(uiBinder.createAndBindUi(this));
		
		this.log = log;
		
		
		createButton(LabelsConstants.lblCnst.Close(), closeHandler);
		
		panelContent.setWidget(createGrid());
		
	}

	public HTMLPanel createGrid() {
		HTMLPanel panel = new HTMLPanel("");
		if(log== null) {
			panel.add(new Label("Aucun Logs"));
		}
		else {
		TextCell cell = new TextCell();
		
		Column<InstanceLog, String> creationDateColumn = new Column<InstanceLog, String>(cell) {
			@Override
			public String getValue(InstanceLog object) {
				return object.getCreationDate().toLocaleString();
			}
		};
		
		Column<InstanceLog, String> dataColumn = new Column<InstanceLog, String>(cell) {
			@Override
			public String getValue(InstanceLog object) {
				return object.getText();
			}
		};
		
		datagrid = new DataGrid<>(999999);
		datagrid.setSize("100%", "100%");
		datagrid.addColumn(creationDateColumn, "Date de création");
		datagrid.addColumn(dataColumn, "Log");		
		
		SimplePanel panelDataGrid = new SimplePanel();
		panelDataGrid.setSize("900px", "350px");
		panelDataGrid.setWidget(datagrid);
		
		
		panel.add(panelDataGrid);
		
		ListDataProvider<InstanceLog> dataprovider = new ListDataProvider<>(log);
		dataprovider.addDataDisplay(datagrid);
		dataprovider.setList(log);
		
		}
		return panel;
	}
	private ClickHandler closeHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			InstanceLogDialog.this.hide();
		}
	};
	
	@Override
	public int getThemeColor() {
		return 0;
	}

}
