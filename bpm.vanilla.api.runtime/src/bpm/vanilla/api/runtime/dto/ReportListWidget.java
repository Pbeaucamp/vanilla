package bpm.vanilla.api.runtime.dto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.fwr.api.beans.FWRReport;
import bpm.fwr.api.beans.components.GridComponent;
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

import org.json.JSONArray;

public class ReportListWidget extends ReportWidget {
	
	private List<MetadataComponent> list;
	private List<MetadataPrompt> prompts;
	private List<MetadataFilter> filters;
	
	public ReportListWidget() {
		super();
	}
	
	public ReportListWidget(int id,String type,List<MetadataComponent> list) {
		super(id,type);
		this.list = list;
	}
	
	public ReportListWidget(GridComponent listComponent,int id) {
		this.id = id;
		this.type = ReportWidgetType.LIST;
		
		this.list = new ArrayList<>();
		for(Column column : listComponent.getColumns()) {
			this.list.add(new MetadataColumn(column,"UNDEFINED"));
		}
		
		this.prompts = new ArrayList<>();
		for(IResource prompt : listComponent.getPrompts()) {
			if(prompt instanceof FwrPrompt) {
				this.prompts.add(new MetadataPrompt((FwrPrompt)prompt));
			}
		}
		
		this.filters = new ArrayList<>();
		for(FWRFilter filter : listComponent.getFwrFilters()) {
			this.filters.add(new MetadataFilter(filter));
		}
	}
	
	public ReportListWidget(String listWidgetStr) throws Exception {
		super();
		JSONObject listWidgetJson = new JSONObject(listWidgetStr);
		this.id = listWidgetJson.getInt("id");
		this.type = listWidgetJson.getString("type");
		
		JSONArray listArrayJson = listWidgetJson.getJSONArray("list");
		
		ObjectMapper mapper = new ObjectMapper();
		
		this.list = new ArrayList<>();
		for(int i=0;i<listArrayJson.length();i++) {
			JSONObject listItem = listArrayJson.getJSONObject(i);
			listItem.remove("componentID");
			listItem.remove("index");
			switch(listItem.getString("type")) {
			case "MetadataColumn":
				if(listItem.has("savedQueryName")) {
					listItem.remove("savedQueryName");
				}
				if(listItem.has("businessTableName")) {
					this.list.add(mapper.readValue(listItem.toString(), MetadataColumn.class));
				}
				break;
			}
		}
		
		JSONArray promptArrayJson = listWidgetJson.getJSONArray("prompts");
		this.prompts = new ArrayList<>();
		for(int i=0;i<promptArrayJson.length();i++) {
			JSONObject promptItem = promptArrayJson.getJSONObject(i);
			promptItem.remove("componentID");
			promptItem.remove("index");
			this.prompts.add(mapper.readValue(promptItem.toString(), MetadataPrompt.class));
		}
		
		JSONArray filterArrayJson = listWidgetJson.getJSONArray("filters");
		this.filters = new ArrayList<>();
		for(int i=0;i<filterArrayJson.length();i++) {
			JSONObject filterItem = filterArrayJson.getJSONObject(i);
			filterItem.remove("componentID");
			filterItem.remove("index");
			this.filters.add(mapper.readValue(filterItem.toString(), MetadataFilter.class));
		}
	}

	public List<MetadataComponent> getList() {
		return list;
	}
	
	public List<MetadataPrompt> getPrompts() {
		return prompts;
	}

	public List<MetadataFilter> getFilters() {
		return filters;
	}

	public GridComponent createListComponent(FWRReport fwrReport,Repository repository,Group group,User user,int row,int col) {
		GridComponent listComponent = new GridComponent();
		
		listComponent.setX(col);
		listComponent.setY(row);
		
		for(MetadataComponent metaComponent : this.list) {
			if(metaComponent instanceof MetadataColumn) {
				MetadataColumn metaColumn = (MetadataColumn) metaComponent;
				Column column = metaColumn.createColumn();
				
				if(listComponent.getDataset() == null) {
					listComponent.setDataset(createDataset(repository,group,user,metaColumn));
				}
				
				if(column.getBusinessPackageParent().equals(listComponent.getDataset().getDatasource().getBusinessPackage())) {
					listComponent.getDataset().addColumn(column);
					column.setDatasetParent(listComponent.getDataset());
				}
				
				listComponent.addColumn(column);
			}
		}
		
		
		
		List<IResource> componentPrompts = new ArrayList<>();
		for(MetadataPrompt metaPrompt : this.prompts) {
			FwrPrompt prompt = metaPrompt.createComponentPrompt();
			componentPrompts.add(prompt);
			if(metaPrompt.getBusinessPackageName().equals(listComponent.getDataset().getDatasource().getBusinessPackage())) {
				listComponent.getDataset().addPrompt(prompt);
				fwrReport.addFwrPromptResource(prompt);
			}
		}
		listComponent.setPrompts(componentPrompts);
		
		List<FWRFilter> componentFilters = new ArrayList<>();
		for(MetadataFilter metaFilter : this.filters) {
			FWRFilter filter = metaFilter.createComponentFilter();
			componentFilters.add(filter);
			if(metaFilter.getBusinessPackageName().equals(listComponent.getDataset().getDatasource().getBusinessPackage())) {
				listComponent.getDataset().addFwrFilter(filter);
				fwrReport.addFwrFilter(filter);
			}
		}
		listComponent.setFwrFilters(componentFilters);;
		
		return listComponent;
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
