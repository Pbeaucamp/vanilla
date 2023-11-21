package bpm.gwt.workflow.commons.client.workflow.properties;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.vanilla.platform.core.beans.data.DataColumn;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class ColumnAirPanel extends Composite {

	private static ColumnAirPanelUiBinder uiBinder = GWT.create(ColumnAirPanelUiBinder.class);

	interface ColumnAirPanelUiBinder extends UiBinder<Widget, ColumnAirPanel> {
	}
	
	private DataGrid<DataColumn> columnGrid;
	private ListDataProvider<DataColumn> columnDataProvider;

	private SelectionModel<DataColumn> selectionModel;
	
	@UiField
	SimplePanel gridPanel;
	
	@UiField
	HTMLPanel rootPanel, panelToolbar;
	
	private boolean multi;

	public ColumnAirPanel(boolean multi) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.multi = multi;
		
		columnGrid = new DataGrid<DataColumn>(9999);
		columnGrid.setSize("100%", "300px");

		if(multi) {
			CheckboxCell cell = new CheckboxCell();
			Column<DataColumn, Boolean> colCheck = new Column<DataColumn, Boolean>(cell) {
				@Override
				public Boolean getValue(DataColumn object) {
					return selectionModel.isSelected(object);
				}
			};
			columnGrid.addColumn(colCheck);
			columnGrid.setColumnWidth(colCheck, "50px");
			
			selectionModel = new MultiSelectionModel<DataColumn>();
			columnGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<DataColumn> createCheckboxManager());
		}
		else {
			selectionModel = new SingleSelectionModel<DataColumn>();
			columnGrid.setSelectionModel(selectionModel);
		}

		Column<DataColumn, String> colName = new Column<DataColumn, String>(new TextCell()) {
			@Override
			public String getValue(DataColumn object) {
				return object.getColumnLabel();
			}
		};

		Column<DataColumn, String> colType = new Column<DataColumn, String>(new TextCell()) {
			@Override
			public String getValue(DataColumn object) {
				return object.getColumnTypeName();
			}
		};


		columnGrid.addColumn(colName, LabelsCommon.lblCnst.Name());
		columnGrid.addColumn(colType, LabelsCommon.lblCnst.Type());

		columnDataProvider = new ListDataProvider<DataColumn>();
		columnDataProvider.addDataDisplay(columnGrid);


		
		gridPanel.add(columnGrid);
		panelToolbar.setVisible(false);
	}
	
	public void init(List<DataColumn> columns, SelectionModel<DataColumn> selectedColumns) {
		columnDataProvider.setList(columns);
		if (selectedColumns != null) {
			selectionModel = selectedColumns;
			columnGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<DataColumn> createCheckboxManager());
		}
		else if(!multi) {
			selectionModel = new SingleSelectionModel<DataColumn>();
			columnGrid.setSelectionModel(selectionModel);
		}
	}

	public List<DataColumn> getSelectedColumns() {
		if(multi) {
			return new ArrayList<DataColumn>(((MultiSelectionModel<DataColumn>)selectionModel).getSelectedSet());
		}
		else {
			List<DataColumn> res = new ArrayList<DataColumn>();
			res.add(((SingleSelectionModel<DataColumn>)selectionModel).getSelectedObject());
			return res;
		}
	}
	
	public SelectionModel<DataColumn> getSelectionModel() {
		return selectionModel;
	}
	
	public List<DataColumn> getAllColumns() {
		return new ArrayList<DataColumn>(columnDataProvider.getList());
	}
	
	public void addButton(String title, ImageResource image, ClickHandler clickhandler) {
		panelToolbar.setVisible(true);
		Image toolBarImage = new Image(image);
		toolBarImage.setTitle(title);
		if(clickhandler != null) {
			toolBarImage.addClickHandler(clickhandler);
		}
		panelToolbar.add(toolBarImage);
	}
	
}
