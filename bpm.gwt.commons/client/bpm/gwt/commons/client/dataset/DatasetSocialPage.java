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
import bpm.vanilla.platform.core.beans.data.DatasourceSocial;

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

public class DatasetSocialPage extends DatasetPanel implements IGwtPage {

	private static DatasetSocialPageUiBinder uiBinder = GWT.create(DatasetSocialPageUiBinder.class);

	interface DatasetSocialPageUiBinder extends UiBinder<Widget, DatasetSocialPage> {
	}


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
	
//	private List<DataColumn> rColumns;

	public DatasetSocialPage(DatasetWizard parent, Datasource datasource, Dataset dataset) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.datasource = datasource;
		this.dataset = dataset;
		createGrids();
		
		loadSocialMetaData();
		

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
	
	private void loadSocialMetaData(){

		CommonService.Connect.getInstance().getSocialDataSetMetaData(dataset, datasource, parent.getUser().getLogin()+parent.getUser().getId(), new AsyncCallback<ArrayList<DataColumn>>() {	
			@Override
			public void onSuccess(ArrayList<DataColumn> result) {
//				rColumns = new ArrayList<DataColumn>(result);
				int i =0;
				if(dataset != null && dataset.getId() > 0) {
					List<DataColumn> selected = new ArrayList<DataColumn>();
//					List<DataColumn> available = new ArrayList<DataColumn>();
					for(DataColumn col : dataset.getMetacolumns()) {
						i=0;
						for(DataColumn dcol : result){
							if(dcol.getColumnLabel().equals(col.getColumnLabel())){
								result.remove(i);
								selected.add(dcol);
								break;
							}
							i++;
						}
						
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

	public Dataset getDataset(){
		return dataset;
	}

	@Override
	public String getQuery(String datasetName) {
		String cols = "";
		for(DataColumn col : dataproviderSelected.getList()){
			cols += "'" +col.getColumnLabel()+ "',";
		}
		cols = cols.substring(0, cols.length()-1);
		
		DatasourceSocial socialdts = (DatasourceSocial) dataset.getDatasource().getObject();
		
		HashMap<String, String> params = new HashMap<String, String>();
		if(socialdts.getParams() != null){
			for(String param : socialdts.getParams().split("&&")){
				params.put(param.split("=")[0], param.split("=")[1]);
			}
		}
		
		String query = "library(twitteR)\n";
		
		switch (socialdts.getFunction()) {
		case TWITTER_USERS:
			String users = "c(";
			for(String user : params.get("userList").split(";")){
				users += "'" + user + "',";
			}
			users = users.substring(0, users.length()-1) + ")";
			
			query+= "resultList <- lookupUsers("+users+")\n";
			query += "resultDF <- twListToDF(resultList)\n";
			break;
		case TWITTER_TRENDS:
			query+= "resultDF <- getTrends("+params.get("woeid")+")\n";
			break;
		case TWITTER_SEARCH:
			query+= "resultList <- searchTwitter('"+params.get("searchString")+"', n="+params.get("n")+
			", lang=NULL, since="+((params.get("since") == null )? "NULL" : "'" + params.get("since") + "'")
			+", until="+((params.get("until") == null )? "NULL" : "'" + params.get("until") + "'")
			+")\n";
			query += "resultDF <- twListToDF(resultList)\n";
			break;
		case TWITTER_TIMELINE:
			query+= "resultList <- userTimeline('"+params.get("user")+"', n="+params.get("n")+")\n";
			query += "resultDF <- twListToDF(resultList)\n";
			break;
		}
		
		query += datasetName + " <- resultDF[c(" + cols + ")]";
		return query;
	}

	public Datasource getDatasource() {
		return datasource;
	}
	
	
}
