package bpm.fm.designer.web.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.fm.api.model.Theme;
import bpm.fm.designer.web.client.Messages;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.custom.CustomCheckboxCell;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class ThemeDatagrid extends SimplePanel {

	private SelectionModel<Theme> selectionModel;
	private ListDataProvider<Theme> dataProvider;
	
	public ThemeDatagrid(List<Theme> groups, MultiSelectionModel<Theme> multiSelectionModel, int height) {
		this.selectionModel = multiSelectionModel;
		if(groups == null) {
			groups = new ArrayList<Theme>();
		}
	    
		CheckboxCell cell = new CheckboxCell();
		Column<Theme, Boolean> modelNameColumn = new Column<Theme, Boolean>(cell) {
	
			@Override
			public Boolean getValue(Theme object) {
				return selectionModel.isSelected(object);
			}
		};
	    
		TextCell txtCell = new TextCell();
		Column<Theme, String> nameColumn = new Column<Theme, String>(txtCell) {
	
			@Override
			public String getValue(Theme object) {
				return object.getName();
			}
		};
		
		Header<Boolean> headerCheck = new Header<Boolean>(new CustomCheckboxCell<Theme>(groups, selectionModel)) {
		
			@Override
			public Boolean getValue() {
				return false;
			}
		};
		
		Header<String> headerName = new Header<String>(new TextCell()) {
		
			@Override
			public String getValue() {
				return Messages.lbl.themes();
			}
		};
		
		DataGrid.Resources resources = new CustomResources();
		DataGrid<Theme> dataGrid = new DataGrid<Theme>(10000, resources);
		dataGrid.setWidth("100%");
		dataGrid.setHeight(height + "px");
		dataGrid.addColumn(modelNameColumn, headerCheck);
		dataGrid.setColumnWidth(modelNameColumn, "40px");
		dataGrid.addColumn(nameColumn, headerName);
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoGroup()));
		
		dataProvider = new ListDataProvider<Theme>(groups);
		dataProvider.addDataDisplay(dataGrid);
		
		dataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<Theme> createCheckboxManager());
		
		this.setWidget(dataGrid);
	}

}
