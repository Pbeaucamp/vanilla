package bpm.gwt.commons.client.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.FormFilterPanel;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.forms.Form;
import bpm.vanilla.platform.core.beans.forms.FormField;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class FormEditDialog extends AbstractDialogBox {

	private static FormEditDialogUiBinder uiBinder = GWT.create(FormEditDialogUiBinder.class);

	interface FormEditDialogUiBinder extends UiBinder<Widget, FormEditDialog> {}

	@UiField
	HTMLPanel filterPanel;

	@UiField
	SimplePanel gridPanel;
	
	private Form form;
	
	private ListDataProvider<Map<String, FormField>> dataProvider = new ListDataProvider<Map<String, FormField>>();
	private SingleSelectionModel<Map<String, FormField>> selectionModel = new SingleSelectionModel<Map<String, FormField>>();
	
	private Map<String, FormField> selectedLine;
	
	public FormEditDialog(Form form) {
		super("Edit value", false, true);
		this.form = form;

		setWidget(uiBinder.createAndBindUi(this));
		filterPanel.add(new FormFilterPanel(form));

		createButtonBar(LabelsConstants.lblCnst.Confirmation(), confirmHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);
	}

	private ClickHandler confirmHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			selectedLine = selectionModel.getSelectedObject();
			FormEditDialog.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			FormEditDialog.this.hide();
		}
	};
	
	public Map<String, FormField> getSelectedLine() {
		return selectedLine;
	}
	
	@UiHandler("btnAdd")
	public void onAdd(ClickEvent event) {
		filterPanel.add(new FormFilterPanel(form));
	}
	
	@UiHandler("btnExecute")
	public void onExec(ClickEvent event) {
		for(int i = 0 ; i < filterPanel.getWidgetCount() ; i++) {
			FormFilterPanel p = (FormFilterPanel) filterPanel.getWidget(i);
			p.fillField();
		}
		
		CommonService.Connect.getInstance().executeFormSearchQuery(form, new GwtCallbackWrapper<List<Map<String, FormField>>>(null, true, true) {
			@Override
			public void onSuccess(List<Map<String, FormField>> result) {
				buildDatagrid(result);
				
			}
		}.getAsyncCallback());
	}

	private void buildDatagrid(List<Map<String, FormField>> result) {
		TextCell cell = new TextCell();
		Map<String, FormField> columns = result.get(0);
		
		DataGrid<Map<String, FormField>> grid = new DataGrid<Map<String, FormField>>();
		grid.setSize("100%", "100%");
		
		dataProvider.addDataDisplay(grid);
		grid.setSelectionModel(selectionModel);
		
		for(final String col : columns.keySet()) {
			Column<Map<String, FormField>, String> gridCol = new Column<Map<String,FormField>, String>(cell) {
				@Override
				public String getValue(Map<String, FormField> object) {
					return object.get(col).getValue();
				}
			};
			grid.addColumn(gridCol, col);
		}
		
		gridPanel.clear();
		gridPanel.add(grid);
		
		dataProvider.setList(result);
	}
}
