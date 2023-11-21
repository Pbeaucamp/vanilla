package bpm.gwt.commons.client.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.HbaseTable;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class DatasetHBasePage extends DatasetPanel implements IGwtPage {

	private static DatasetHBasePageUiBinder uiBinder = GWT.create(DatasetHBasePageUiBinder.class);

	interface DatasetHBasePageUiBinder extends UiBinder<Widget, DatasetHBasePage> {
	}

	@UiField
	ListBox lstTables;
	
	@UiField
	SimplePanel availableGridPanel, selectedGridPanel;

	private Datasource datasource;
	private Dataset dataset;
	private DatasetWizard parent;
	
	private DataGrid<DataColumn> datagridAvailables;
	private ListDataProvider<DataColumn> dataproviderAvailables;
	private MultiSelectionModel<DataColumn> selectionModelAvailables;
	
	private DataGrid<DataColumn> datagridSelected;
	private ListDataProvider<DataColumn> dataproviderSelected;
	private MultiSelectionModel<DataColumn> selectionModelSelected;
	
	private List<DataColumn> hbaseColumns;
	private HashMap<String, HbaseTable> hbaseTables = new HashMap<String, HbaseTable>();

	public DatasetHBasePage(DatasetWizard parent, Datasource datasource, Dataset dataset) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.datasource = datasource;
		this.dataset = dataset;
		createGrids();
		
		loadHBaseTables();
		

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
	
	public ArrayList<DataColumn> getMetaColumns() {
		return new ArrayList<DataColumn>(dataproviderSelected.getList());
	}

	public void setDatasource(Datasource currentDataSource) {
		this.datasource = currentDataSource;
	}
	
	@UiHandler("lstTables")
	public void onTableChange(ChangeEvent event){
		if(lstTables.getValue(lstTables.getSelectedIndex()).equals("")){
			dataproviderAvailables.setList(new ArrayList<DataColumn>());
			dataproviderSelected.setList(new ArrayList<DataColumn>());
		} else {
//			loadHBaseMetaData(lstTables.getValue(lstTables.getSelectedIndex()));
			String tableName = lstTables.getValue(lstTables.getSelectedIndex());
			HbaseTable table = hbaseTables.get(tableName);
			
			List<DataColumn> columns = new ArrayList<DataColumn>();
			
			for(String family : table.getFamilies().keySet()) {
				for(String col : table.getFamilies().get(family)) {
					DataColumn c = new DataColumn();
					c.setColumnName(col);
					c.setColumnLabel(col);
					c.setColumnTypeName(family);
					columns.add(c);
				}
			}
			
			dataproviderAvailables.setList(columns);
		}
	}
	
	private void loadHBaseMetaData(String tableName){

		CommonService.Connect.getInstance().getDatasetHBaseMetadata(tableName, datasource, new AsyncCallback<List<DataColumn>>() {	
			@Override
			public void onSuccess(List<DataColumn> result) {
				hbaseColumns = new ArrayList<DataColumn>(result);
				int i =0;
				if(dataset != null && dataset.getId() > 0) {
					List<DataColumn> selected = new ArrayList<DataColumn>();
					List<DataColumn> available = new ArrayList<DataColumn>();
					for(DataColumn col : dataset.getMetacolumns()) {
						i=0;
						for(DataColumn dcol : result){
							if(dcol.getColumnLabel().equals(col.getColumnLabel())){
								result.remove(i);
								break;
							}
							i++;
						}
						selected.add(col);
						dataproviderAvailables.setList(result);
						dataproviderSelected.setList(selected);
						parent.updateBtn();
					}
				}
				else {
					dataproviderAvailables.setList(result);
					parent.updateBtn();
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.UnableToLoadDataSetMetaData(), caught.getMessage(), caught);
				dial.center();
			}
		});
	}

	private void loadHBaseTables() {
		CommonService.Connect.getInstance().getDatasourceHBaseMetadataListTables(datasource, new AsyncCallback<List<HbaseTable>>() {	
			@Override
			public void onSuccess(List<HbaseTable> result) {
				lstTables.addItem("");
				for(HbaseTable table : result){
					lstTables.addItem(table.getName(),table.getName());
					hbaseTables.put(table.getName(), table);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(LabelsConstants.lblCnst.Error(), LabelsConstants.lblCnst.Ok(), LabelsConstants.lblCnst.UnableToLoadHBaseTables(), caught.getMessage(), caught);
				dial.center();
			}
		});
	}
	
	public Dataset getDataset(){
		return dataset;
	}

	public String getQuery(String datasetName) {
		String cols = "";
		String family = "";
		for(DataColumn col : dataproviderSelected.getList()){
			cols += col.getColumnLabel()+ ",";
			family = col.getColumnTypeName();
			col.setColumnTypeName("");
		}
		cols = cols.substring(0, cols.length()-1);
		String tableName = lstTables.getValue(lstTables.getSelectedIndex());

		return "select " + cols + " from " + tableName + "." + family;
	}
}
