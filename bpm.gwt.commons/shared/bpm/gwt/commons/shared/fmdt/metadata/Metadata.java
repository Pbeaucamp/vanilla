package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.resources.D4C;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Metadata implements IsSerializable {

	private Integer itemId;
	
	private String name;
	private String description;
	
	private Datasource datasource;
	private List<MetadataModel> models;
	
	private Integer d4cServerId;
	private D4C d4cServer;
	private String d4cOrganisation;

	public Metadata() { }
	
	public Metadata(int itemId, String name, String description) {
		this.itemId = itemId;
		this.name = name;
		this.description = description;
	}
	
	public Metadata(String name, String description, Datasource datasource, MetadataModel model) {
		this.name = name;
		this.description = description;
		this.datasource = datasource;
		
		addModel(model);
	}
	
	public Integer getItemId() {
		return itemId;
	}
	
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	
	public String getName() {
		return name != null ? name : "";
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description != null ? description : "";
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Datasource getDatasource() {
		return datasource;
	}
	
	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}
	
	public List<MetadataModel> getModels() {
		return models;
	}
	
	public void setModels(List<MetadataModel> models) {
		this.models = models;
		if (models != null) {
			for (MetadataModel model : models) {
				model.setParent(this);
			}
		}
	}
	
	public void addModel(MetadataModel model) {
		if (models == null) {
			this.models = new ArrayList<MetadataModel>();
		}
		model.setParent(this);
		this.models.add(model);
	}
	
	public Integer getD4cServerId() {
		return d4cServerId;
	}
	
	public void setD4cServerId(Integer d4cServerId) {
		this.d4cServerId = d4cServerId;
	}
	
	public D4C getD4cServer() {
		return d4cServer;
	}
	
	public void setD4cServer(D4C d4cServer) {
		this.d4cServer = d4cServer;
		setD4cServerId(d4cServer.getId());
	}
	
	public String getD4cOrganisation() {
		return d4cOrganisation;
	}
	
	public void setD4cOrganisation(String d4cOrganisation) {
		this.d4cOrganisation = d4cOrganisation;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
