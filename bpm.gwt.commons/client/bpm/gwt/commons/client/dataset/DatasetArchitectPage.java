package bpm.gwt.commons.client.dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class DatasetArchitectPage extends DatasetPanel implements IGwtPage {

	private static DatasetArchitectPageUiBinder uiBinder = GWT.create(DatasetArchitectPageUiBinder.class);

	interface DatasetArchitectPageUiBinder extends UiBinder<Widget, DatasetArchitectPage> {
	}
	
	@UiField
	SimplePanel availableGridPanel, selectedGridPanel;
	
	private DatasetWizard parent;
	private Datasource datasource;
	private Dataset dataset;
	
	private DataGrid<DataColumn> datagridAvailables;
	private ListDataProvider<DataColumn> dataproviderAvailables;
	private MultiSelectionModel<DataColumn> selectionModelAvailables;
	
	private DataGrid<DataColumn> datagridSelected;
	private ListDataProvider<DataColumn> dataproviderSelected;
	private MultiSelectionModel<DataColumn> selectionModelSelected;	
	
	public DatasetArchitectPage(DatasetWizard parent, Datasource datasource, Dataset datasets) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.datasource = datasource;
		this.dataset = datasets;
		
		createGrids();
		
		CommonService.Connect.getInstance().getDatasetArchitectMetadata("Architect Dataset", datasource, new AsyncCallback<List<DataColumn>>() {	
			@Override
			public void onSuccess(List<DataColumn> result) {
				//dataproviderAvailables.setList(result);
				
				if(dataset != null && dataset.getId() > 0) {
					List<DataColumn> selected = new ArrayList<DataColumn>();
					for(DataColumn col : dataset.getMetacolumns()) {
						result.remove(col);
						selected.add(col);
						dataproviderAvailables.setList(result);
						dataproviderSelected.setList(selected);
					}
				}
				else {
					dataproviderAvailables.setList(result);
				}

			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});

	}
	
	private void createGrids() {
		datagridAvailables = new DataGrid<DataColumn>(99999);
		datagridAvailables.setWidth("100%");
		datagridAvailables.setHeight("100%");
		TextCell cell = new TextCell();
		Column<DataColumn, String> colName = new Column<DataColumn, String>(cell) {
			@Override
			public String getValue(DataColumn object) {
				return object.getColumnLabel();
			}
		};
		
		datagridAvailables.addColumn(colName, LabelsConstants.lblCnst.Name());
		
		dataproviderAvailables = new ListDataProvider<DataColumn>();
		dataproviderAvailables.addDataDisplay(datagridAvailables);
		
		selectionModelAvailables = new MultiSelectionModel<DataColumn>();
		datagridAvailables.setSelectionModel(selectionModelAvailables);
		
		availableGridPanel.add(datagridAvailables);
		
		datagridSelected = new DataGrid<DataColumn>(99999);
		datagridSelected.setWidth("100%");
		datagridSelected.setHeight("100%");
		Column<DataColumn, String> colNameSel = new Column<DataColumn, String>(cell) {
			@Override
			public String getValue(DataColumn object) {
				return object.getColumnLabel();
			}
		};
		
		datagridSelected.addColumn(colNameSel, LabelsConstants.lblCnst.Name());
		
		dataproviderSelected = new ListDataProvider<DataColumn>();
		dataproviderSelected.addDataDisplay(datagridSelected);
		
		selectionModelSelected = new MultiSelectionModel<DataColumn>();
		datagridSelected.setSelectionModel(selectionModelSelected);
		
		selectedGridPanel.add(datagridSelected);
	}
	
	@UiHandler("addColumns")
	public void onAddColumn(ClickEvent e) {
		List<DataColumn> available = dataproviderAvailables.getList();
		List<DataColumn> selected = dataproviderSelected.getList();
		
		Iterator<DataColumn> iter = selectionModelAvailables.getSelectedSet().iterator();
		while(iter.hasNext()) {
			DataColumn val = iter.next();
			available.remove(val);
			selected.add(val);
		}
		
		dataproviderAvailables.setList(available);
		dataproviderSelected.setList(selected);
		parent.updateBtn();
	}
	
	@UiHandler("removeColumns")
	public void onRemoveColumn(ClickEvent e) {
		List<DataColumn> available = dataproviderAvailables.getList();
		List<DataColumn> selected = dataproviderSelected.getList();
		
		Iterator<DataColumn> iter = selectionModelSelected.getSelectedSet().iterator();
		while(iter.hasNext()) {
			DataColumn val = iter.next();
			available.add(val);
			selected.remove(val);
		}
		
		dataproviderAvailables.setList(available);
		dataproviderSelected.setList(selected);
		parent.updateBtn();
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return (dataproviderSelected.getList().size() > 0);
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	@Override
	public List<DataColumn> getMetaColumns() {
		return new ArrayList<DataColumn>(dataproviderSelected.getList());
	}

	@Override
	public String getQuery(String datasetName) {
		String request = "";
		for(DataColumn d :dataproviderSelected.getList()) {
			request += d.getColumnLabel() + ",";
		}
		return request;
	}


}
