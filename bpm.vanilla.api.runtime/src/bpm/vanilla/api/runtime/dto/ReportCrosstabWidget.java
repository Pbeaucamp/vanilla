package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.components.CrossComponent;
import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.DataSource;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.api.beans.dataset.FwrPrompt;
import bpm.fwr.api.beans.dataset.IResource;
import bpm.vanilla.api.runtime.dto.ReportSheet.ReportWidgetType;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;

public class ReportCrosstabWidget extends ReportWidget {

	private List<MetadataComponent> rows;
	private List<MetadataComponent> cols;
	private List<MetadataComponent> cells;
	private List<MetadataPrompt> prompts;
	private List<MetadataFilter> filters;
	
	public ReportCrosstabWidget() {
		super();
	}

	public ReportCrosstabWidget(int id,String type,List<MetadataComponent> rows, List<MetadataComponent> cols, List<MetadataComponent> cells,List<MetadataPrompt> prompts, List<MetadataFilter> filters) {
		super(id,type);
		this.rows = rows;
		this.cols = cols;
		this.cells = cells;
		this.prompts = prompts;
		this.filters = filters;
	}
	
	public ReportCrosstabWidget(String crosstabWidgetStr) throws Exception {
		JSONObject crosstabWidgetJson = new JSONObject(crosstabWidgetStr);
		this.id = crosstabWidgetJson.getInt("id");
		this.type = crosstabWidgetJson.getString("type");
		
		JSONArray rowsArrayJson = crosstabWidgetJson.getJSONArray("rows");
		this.rows = loadList(rowsArrayJson);
		
		JSONArray colsArrayJson = crosstabWidgetJson.getJSONArray("cols");
		this.cols = loadList(colsArrayJson);
		
		JSONArray cellsArrayJson = crosstabWidgetJson.getJSONArray("cells");
		this.cells = loadList(cellsArrayJson);
		
		ObjectMapper mapper = new ObjectMapper();
		
		JSONArray promptArrayJson = crosstabWidgetJson.getJSONArray("prompts");
		this.prompts = new ArrayList<>();
		for(int i=0;i<promptArrayJson.length();i++) {
			JSONObject promptItem = promptArrayJson.getJSONObject(i);
			promptItem.remove("componentID");
			promptItem.remove("index");
			this.prompts.add(mapper.readValue(promptItem.toString(), MetadataPrompt.class));
		}
		
		JSONArray filterArrayJson = crosstabWidgetJson.getJSONArray("filters");
		this.filters = new ArrayList<>();
		for(int i=0;i<filterArrayJson.length();i++) {
			JSONObject filterItem = filterArrayJson.getJSONObject(i);
			filterItem.remove("componentID");
			filterItem.remove("index");
			this.filters.add(mapper.readValue(filterItem.toString(), MetadataFilter.class));
		}
	}
	
	public ReportCrosstabWidget(CrossComponent crosstabComponent,int id) {
		this.id = id;
		this.type = ReportWidgetType.CROSSTAB;
		
		this.rows = new ArrayList<>();
		this.rows.addAll(loadColumnList(crosstabComponent.getCrossRows(),"DIMENSION"));
		
		this.cols = new ArrayList<>();
		this.cols.addAll(loadColumnList(crosstabComponent.getCrossCols(),"DIMENSION"));
		
		this.cells = new ArrayList<>();
		this.cells.addAll(loadColumnList(crosstabComponent.getCrossCells(),"SUM"));
		
		for(IResource prompt : crosstabComponent.getPrompts()) {
			if(prompt instanceof FwrPrompt) {
				this.prompts.add(new MetadataPrompt((FwrPrompt)prompt));
			}
		}
		
		for(FWRFilter filter : crosstabComponent.getFwrFilters()) {
			this.filters.add(new MetadataFilter(filter));
		}
	}
	
	public List<MetadataComponent> getRows() {
		return rows;
	}

	public List<MetadataComponent> getCols() {
		return cols;
	}

	public List<MetadataComponent> getCells() {
		return cells;
	}

	private List<MetadataColumn> loadColumnList(List<Column> list,String columnType){
		List<MetadataColumn> metaColumns = new ArrayList<>();
		for(Column column : list) {
			metaColumns.add(new MetadataColumn(column,columnType));
		}
		return metaColumns;
	}
	
	
	private List<MetadataComponent> loadList(JSONArray listArrayJson) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<MetadataComponent> list = new ArrayList<>();
		
		for(int i=0;i<listArrayJson.length();i++) {
			JSONObject listItem = listArrayJson.getJSONObject(i);
			listItem.remove("componentID");
			listItem.remove("axe");
			listItem.remove("index");
			switch(listItem.getString("type")) {
			case "MetadataColumn":
				if(listItem.has("savedQueryName")) {
					listItem.remove("savedQueryName");
				}
				if(listItem.has("businessTableName")) {
					list.add(mapper.readValue(listItem.toString(), MetadataColumn.class));
				}
				break;
			}
		}
		
		return list;
	}
	
	
	public CrossComponent createCrosstabComponent(FWRReport fwrReport,Repository repository,Group group,User user,int row,int col) {
		CrossComponent crosstabComponent = new CrossComponent();
		
		crosstabComponent.setX(col);
		crosstabComponent.setY(row);
		
		List<Column> crossRows = createCrossList(repository,group,user,crosstabComponent,this.rows);
		crosstabComponent.setCrossRows(crossRows);
		
		List<Column> crossCols = createCrossList(repository,group,user,crosstabComponent,this.cols);
		crosstabComponent.setCrossCols(crossCols);
		
		List<Column> crossCells = createCrossList(repository,group,user,crosstabComponent,this.cells);
		crosstabComponent.setCrossCells(crossCells);
		
		List<IResource> componentPrompts = new ArrayList<>();
		for(MetadataPrompt metaPrompt : this.prompts) {
			FwrPrompt prompt = metaPrompt.createComponentPrompt();
			componentPrompts.add(prompt);
			if(metaPrompt.getBusinessPackageName().equals(crosstabComponent.getDataset().getDatasource().getBusinessPackage())) {
				crosstabComponent.getDataset().addPrompt(prompt);
				fwrReport.addFwrPromptResource(prompt);
			}
		}
		crosstabComponent.setPrompts(componentPrompts);
		
		List<FWRFilter> componentFilters = new ArrayList<>();
		for(MetadataFilter metaFilter : this.filters) {
			FWRFilter filter = metaFilter.createComponentFilter();
			componentFilters.add(filter);
			if(metaFilter.getBusinessPackageName().equals(crosstabComponent.getDataset().getDatasource().getBusinessPackage())) {
				crosstabComponent.getDataset().addFwrFilter(filter);
				fwrReport.addFwrFilter(filter);
			}
		}
		crosstabComponent.setFwrFilters(componentFilters);;
		
		return crosstabComponent;
	}
	
	public List<Column> createCrossList(Repository repository,Group group,User user,CrossComponent crosstabComponent,List<MetadataComponent> list) {
		
		List<Column> crosstabList = new ArrayList<>();
		
		for(MetadataComponent metaComponent : list) {
			if(metaComponent instanceof MetadataColumn) {
				MetadataColumn metaColumn = (MetadataColumn) metaComponent;
				Column column = metaColumn.createColumn();
				
				if(crosstabComponent.getDataset() == null) {
					crosstabComponent.setDataset(createDataset(repository,group,user,metaColumn));
				}
				
				if(column.getBusinessPackageParent().equals(crosstabComponent.getDataset().getDatasource().getBusinessPackage())) {
					crosstabComponent.getDataset().addColumn(column);
					column.setDatasetParent(crosstabComponent.getDataset());
				}
				
				crosstabList.add(column);
				
			}
			
		}
		
		return crosstabList;
	}
	
	
	public DataSet createDataset(Repository repository,Group group,User user,MetadataColumn metaColumn) {
		
		DataSource dataS = new DataSource();
		dataS.setBusinessModel(metaColumn.getBusinessModelName());
		dataS.setBusinessPackage(metaColumn.getBusinessPackageName());
		dataS.setConnectionName("Default");
		dataS.setGroup(group.getName());
		dataS.setItemId(metaColumn.getMetadataID());
		dataS.setRepositoryId(repository.getId());
		dataS.setName("datasource_" + System.currentTimeMillis());
		dataS.setPassword(user.getPassword());
		dataS.setUrl(repository.getUrl());
		dataS.setUser(user.getLogin());

		DataSet dataset = new DataSet();
		dataset.setLanguage("fr");
		dataset.setDatasource(dataS);
		dataset.setName("dataset_" + System.currentTimeMillis());
		
		return dataset;
	}
	
}
