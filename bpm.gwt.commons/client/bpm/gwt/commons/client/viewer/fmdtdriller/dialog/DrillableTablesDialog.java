package bpm.gwt.commons.client.viewer.fmdtdriller.dialog;

import java.util.HashMap;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.client.services.FmdtServices;
import bpm.gwt.commons.client.viewer.fmdtdriller.FMDTResponsePanel;
import bpm.gwt.commons.shared.fmdt.FmdtQueryDriller;
import bpm.gwt.commons.shared.fmdt.FmdtTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DrillableTablesDialog extends AbstractDialogBox {

	private static DrillableTablesDialogUiBinder uiBinder = GWT.create(DrillableTablesDialogUiBinder.class);

	interface DrillableTablesDialogUiBinder extends UiBinder<Widget, DrillableTablesDialog> {
	}

	@UiField
	HTMLPanel contentPanel;

	private FMDTResponsePanel fmdtDrillerPanel;

	private FmdtQueryDriller fmdtDriller;
	private FmdtTable selectedTable;
	private HashMap<String, String> values;

	private ListBox listTables;

	public DrillableTablesDialog(FMDTResponsePanel fmdtDrillerPanel, FmdtQueryDriller fmdtDriller, FmdtTable selectedTable, String field, HashMap<String, String> values) {
		super(LabelsConstants.lblCnst.ChooseTable(), false, true);

		this.fmdtDrillerPanel = fmdtDrillerPanel;
		this.fmdtDriller = fmdtDriller;
		this.selectedTable = selectedTable;
		this.values = values;

		setWidget(uiBinder.createAndBindUi(this));

		listTables = new ListBox(false);
		listTables.setWidth("325px");
		listTables.getElement().getStyle().setMargin(10, Unit.PX);
		for (String tab : selectedTable.getRelatedTables().get(field)) {
			listTables.addItem(tab, tab);
		}

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(listTables);
		mainPanel.setCellVerticalAlignment(listTables, HorizontalPanel.ALIGN_TOP);
		mainPanel.setCellHorizontalAlignment(listTables, HorizontalPanel.ALIGN_CENTER);

		contentPanel.add(mainPanel);
		createButtonBar(LabelsConstants.lblCnst.Ok(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			fmdtDrillerPanel.showWaitPart(true);

			FmdtServices.Connect.getInstance().drillOnTable(fmdtDriller, selectedTable.getName(), listTables.getValue(listTables.getSelectedIndex()), values, new AsyncCallback<FmdtTable>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					fmdtDrillerPanel.showWaitPart(false);
				}

				@Override
				public void onSuccess(FmdtTable result) {
					DrillableTablesDialog.this.hide();

					fmdtDrillerPanel.buildTableData(result);

					fmdtDrillerPanel.showWaitPart(false);
				}
			});
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			DrillableTablesDialog.this.hide();
		}
	};
}
