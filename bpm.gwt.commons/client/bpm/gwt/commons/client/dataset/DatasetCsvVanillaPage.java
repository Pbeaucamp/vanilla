package bpm.gwt.commons.client.dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
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

public class DatasetCsvVanillaPage extends DatasetPanel implements IGwtPage {

	private static DatasetCsvVanillaPageUiBinder uiBinder = GWT
			.create(DatasetCsvVanillaPageUiBinder.class);

	interface DatasetCsvVanillaPageUiBinder extends
			UiBinder<Widget, DatasetCsvVanillaPage> {
	}

	@UiField
	SimplePanel availableGridPanel, selectedGridPanel;
	
	private Datasource datasource;
	private Dataset dataset;
	private DatasetWizard parent;
	
	private DataGrid<String> datagridAvailables;
	private ListDataProvider<String> dataproviderAvailables;
	private MultiSelectionModel<String> selectionModelAvailables;
	
	private DataGrid<String> datagridSelected;
	private ListDataProvider<String> dataproviderSelected;
	private MultiSelectionModel<String> selectionModelSelected;
	
	private List<String> csvColumns;

	
	public DatasetCsvVanillaPage(DatasetWizard parent, Datasource selectedDatasource) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.datasource = selectedDatasource;
		createGrids();
		
		CommonService.Connect.getInstance().getDatasetCsvVanillaMetadata(datasource, new AsyncCallback<List<String>>() {	
			@Override
			public void onSuccess(List<String> result) {
				csvColumns = new ArrayList<String>(result);
				if(dataset != null && dataset.getId() > 0) {
					List<String> selected = new ArrayList<String>();
					for(DataColumn col : dataset.getMetacolumns()) {
						result.remove(col.getColumnName());
						selected.add(col.getColumnName());
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

	public DatasetCsvVanillaPage(DatasetWizard parent, Datasource selectedDatasource, Dataset dataset) {
		this(parent, selectedDatasource);
		
		this.dataset = dataset;
	}

	private void createGrids() {
		datagridAvailables = new DataGrid<String>(99999);
		datagridAvailables.setWidth("100%");
		datagridAvailables.setHeight("100%");
		TextCell cell = new TextCell();
		Column<String, String> colName = new Column<String, String>(cell) {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		
		datagridAvailables.addColumn(colName, "Column name");
		
		dataproviderAvailables = new ListDataProvider<String>();
		dataproviderAvailables.addDataDisplay(datagridAvailables);
		
		selectionModelAvailables = new MultiSelectionModel<String>();
		datagridAvailables.setSelectionModel(selectionModelAvailables);
		
		availableGridPanel.add(datagridAvailables);
		
		datagridSelected = new DataGrid<String>(99999);
		datagridSelected.setWidth("100%");
		datagridSelected.setHeight("100%");
		Column<String, String> colNameSel = new Column<String, String>(cell) {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		
		datagridSelected.addColumn(colNameSel, "Column name");
		
		dataproviderSelected = new ListDataProvider<String>();
		dataproviderSelected.addDataDisplay(datagridSelected);
		
		selectionModelSelected = new MultiSelectionModel<String>();
		datagridSelected.setSelectionModel(selectionModelSelected);
		
		selectedGridPanel.add(datagridSelected);
	}
	
	@UiHandler("addColumns")
	public void onAddColumn(ClickEvent e) {
		List<String> available = dataproviderAvailables.getList();
		List<String> selected = dataproviderSelected.getList();
		
		Iterator<String> iter = selectionModelAvailables.getSelectedSet().iterator();
		while(iter.hasNext()) {
			String val = iter.next();
			available.remove(val);
			selected.add(val);
		}
		
		dataproviderAvailables.setList(available);
		dataproviderSelected.setList(selected);
	}
	
	@UiHandler("removeColumns")
	public void onRemoveColumn(ClickEvent e) {
		List<String> available = dataproviderAvailables.getList();
		List<String> selected = dataproviderSelected.getList();
		
		Iterator<String> iter = selectionModelSelected.getSelectedSet().iterator();
		while(iter.hasNext()) {
			String val = iter.next();
			available.add(val);
			selected.remove(val);
		}
		
		dataproviderAvailables.setList(available);
		dataproviderSelected.setList(selected);
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return 1;
	}

	public List<DataColumn> getMetaColumns() {
		List<DataColumn> columns = new ArrayList<DataColumn>();
		
		for(String col : dataproviderSelected.getList()) {
			DataColumn c = new DataColumn();
			c.setColumnName(col);
			c.setColumnLabel(col);
			columns.add(c);
		}
		
		return columns;
	}

	@Override
	public String getQuery(String datasetName) {
		StringBuilder buf = new StringBuilder();
		
		buf.append("<bpm.csv.oda.query>\n");
		
		buf.append("	<columns>\n");
		for(String col : dataproviderSelected.getList()) {
			int index = csvColumns.indexOf(col);
			buf.append("		<column>" + index + "</column>\n");
		}
		buf.append("	</columns>\n");
		
		buf.append("</bpm.csv.oda.query>");
		
		return buf.toString();
	}

}
