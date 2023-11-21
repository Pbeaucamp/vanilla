package bpm.vanilla.api.runtime.dto;

import java.util.List;

import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.resource.IFilter;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MetadataFilter extends MetadataResource {
	
	private List<String> values;
	private String operator;
	
	public MetadataFilter() {
		
	}
	
	public MetadataFilter(RepositoryItem it,IBusinessPackage businessPackage,String catName,IFilter filter) throws Exception {
		super(it,businessPackage,catName,"MetadataFilter",filter);
		this.values = filter.getOrigin().getDistinctValues();
	}

	public MetadataFilter(String id, String name, int metadataID, String type, String businessModelName, String businessPackageName,List<String> values) {
		super(id, name, metadataID, type, businessModelName, businessPackageName);
		this.values = values;
	}
	
	public MetadataFilter(FWRFilter filter) {
		super();
		this.type = "MetadataFilter";
		this.name = filter.getName();
		this.businessModelName = filter.getModelParent();
		this.businessPackageName = filter.getPackageParent();
		this.metadataID = filter.getMetadataId();
		this.id = this.metadataID + "." + this.businessModelName + "." + this.businessPackageName + ".Resources.Filters." + this.name;
	}
	
	public List<String> getValues() {
		return values;
	}

	public String getOperator() {
		return operator;
	}
	

	public FWRFilter createComponentFilter() {
		FWRFilter filter = new FWRFilter();
		
		filter.setName(this.name);
		filter.setMetadataId(this.metadataID);
		filter.setModelParent(this.businessModelName);
		filter.setPackageParent(this.businessPackageName);
		filter.setValues(values);
		filter.setOperator(operator);
		
		return filter;
	}
}
