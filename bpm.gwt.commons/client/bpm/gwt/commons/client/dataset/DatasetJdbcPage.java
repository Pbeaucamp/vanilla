package bpm.gwt.commons.client.dataset;

import java.util.ArrayList;
import java.util.Comparator;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceJdbc;
import bpm.vanilla.platform.core.beans.data.DatasourceType;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class DatasetJdbcPage extends DatasetPanel implements IGwtPage {

	private static DatasetReqPageUiBinder uiBinder = GWT.create(DatasetReqPageUiBinder.class);

	interface DatasetReqPageUiBinder extends UiBinder<Widget, DatasetJdbcPage> {
	}

	
	@UiField 
	TextArea txtReq;
	
	@UiField 
	Button btnRun;
	
	@UiField
	SimplePanel panelGrid, panelPager;

	private Dataset dataset;
	private Datasource dataSource;
	private DatasetWizard parent;
	
	private ListDataProvider<DataColumn> dataProvider = new ListDataProvider<DataColumn>();
	private ListHandler<DataColumn> sortHandler;
	private DataGrid<DataColumn> datagrid;

	public DatasetJdbcPage(DatasetWizard parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		//this.dataProvider.setList(new ArrayList<DataColumn>());
		btnRun.addStyleName(VanillaCSS.COMMONS_BUTTON);
		
		panelGrid.add(createGridData());
	}

	public DatasetJdbcPage(DatasetWizard parent, Datasource datasource, Dataset dataset) {
		this.parent = parent;
		this.dataset = dataset;
		this.dataSource = datasource;
		initWidget(uiBinder.createAndBindUi(this));
		btnRun.addStyleName(VanillaCSS.COMMONS_BUTTON);
		panelGrid.add(createGridData());
		if(dataset == null) {
			this.dataset = new Dataset();
		}
		txtReq.setText(this.dataset.getRequest());
		dataProvider.setList((this.dataset.getMetacolumns()!=null)? this.dataset.getMetacolumns() : new ArrayList<DataColumn>());
		
		//initPage();
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return (dataProvider.getList().size() > 0);
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
	public String getQuery(String datasetName) {
		return txtReq.getText();
	}
	
	public ArrayList<DataColumn> getMetaColumns() {
		return new ArrayList<DataColumn>(dataProvider.getList());
	}

//	public void setDatasource(Datasource currentDataSource) {
//		this.dataSource = currentDataSource;
//	}
	
//	public void initPage(){
//		if(dataSource.getType().equals(DatasourceType.R)){
//			txtReq.setEnabled(false);
//			btnRun.setEnabled(false);
//			if(dataset.getMetacolumns().size() > 0){
//				dataProvider.setList(dataset.getMetacolumns());
//			} else {
//				loadRMetaData();
//			}
//		} else {
//			txtReq.setEnabled(true);
//			btnRun.setEnabled(true);
//			txtReq.setText(dataset.getRequest());
//			if(dataset.getRequest() != ""){
//				onExecuteQueryChange(null);
//			}
//		}
//	}
	
	@UiHandler("btnRun")
	public void onExecuteQueryChange(ClickEvent event) {
		
		String query = getQuery(null);
		
		
		if(dataSource.getType().equals(DatasourceType.JDBC)){
			DatasourceJdbc dts = (DatasourceJdbc)dataSource.getObject();
			parent.showWaitPart(true);
			CommonService.Connect.getInstance().getDataSetMetaData(dts, query, new AsyncCallback<ArrayList<DataColumn>>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					parent.showWaitPart(false);
					ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.UnableToLoadDataSetMetaData());
					
				}

				@Override
				public void onSuccess(ArrayList<DataColumn> result) {
					parent.showWaitPart(false);
					if(result.size() == 0)
						MessageHelper.openMessageDialog(LabelsConstants.lblCnst.UnableToLoadDataSetMetaData(), LabelsConstants.lblCnst.NoData());
					dataset.setMetacolumns(result);
					dataset.setRequest(getQuery(null));
					dataProvider.setList(result);
					parent.updateBtn();
				}

			});
			
			
		} else if(dataSource.getType().equals(DatasourceType.FMDT)) {
			//DatasourceFmdt dts = (DatasourceFmdt)dataSource.getObject();
			// à faire
		}	
	}
	
	
	
	private DataGrid<DataColumn> createGridData() {
		TextCell cell = new TextCell();
		Column<DataColumn, String> nameColumn = new Column<DataColumn, String>(cell) {

			@Override
			public String getValue(DataColumn object) {
				return object.getColumnName();
			}
		};
		nameColumn.setSortable(true);

		Column<DataColumn, String> labelColumn = new Column<DataColumn, String>(cell) {

			@Override
			public String getValue(DataColumn object) {
				return object.getColumnLabel();
			}
		};
		
		Column<DataColumn, String> typeColumn = new Column<DataColumn, String>(cell) {

			@Override
			public String getValue(DataColumn object) {
				
				return object.getColumnType()+"";
			}
		};
		
		Column<DataColumn, String> typenameColumn = new Column<DataColumn, String>(cell) {

			@Override
			public String getValue(DataColumn object) {
				
				return object.getColumnTypeName();
			}
		};
		

		//DataGrid.Resources resources = new CustomResources();
		DataGrid<DataColumn> dataGrid = new DataGrid<DataColumn>(10);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
		// Attention au label 
		dataGrid.addColumn(nameColumn, LabelsConstants.lblCnst.Name());
		dataGrid.addColumn(labelColumn, LabelsConstants.lblCnst.Label());
		dataGrid.addColumn(typeColumn, LabelsConstants.lblCnst.TypeSql());
		dataGrid.addColumn(typenameColumn, LabelsConstants.lblCnst.TypeName());
		
		
		dataGrid.setEmptyTableWidget(new Label(LabelsConstants.lblCnst.NoData()));

		dataProvider = new ListDataProvider<DataColumn>();
		dataProvider.addDataDisplay(dataGrid);
		
		sortHandler = new ListHandler<DataColumn>(new ArrayList<DataColumn>());
		sortHandler.setComparator(nameColumn, new Comparator<DataColumn>() {
			
			@Override
			public int compare(DataColumn m1, DataColumn m2) {
				return m1.getColumnName().compareTo(m2.getColumnName());
			}
		});
		
		dataGrid.addColumnSortHandler(sortHandler);


		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		//pager.addStyleName(style.pager());
		pager.setDisplay(dataGrid);
		
		panelPager.setWidget(pager);

		return dataGrid;
	}

	public DataGrid<DataColumn> getDatagrid() {
		return datagrid;
	}
	
//	public void refresh(){
//		this.dataset = parent.getDataset();
//		initPage();
//	}
	
	public Dataset getDataset(){
		return dataset;
	}
	
	@UiHandler("txtReq")
	public void onNameKeyup(KeyUpEvent event) {
//		dataProvider.setList(new ArrayList<DataColumn>());
//		parent.updateBtn();
	}
}
