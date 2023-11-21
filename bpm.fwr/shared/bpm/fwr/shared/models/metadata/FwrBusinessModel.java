package bpm.fwr.shared.models.metadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import bpm.fwr.api.beans.dataset.FwrRelationStrategy;
import bpm.metadata.layer.business.RelationStrategy;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FwrBusinessModel extends FwrBusinessObject implements IsSerializable {

	private LinkedHashMap<String, String> locales = new LinkedHashMap<String, String>();

	private FwrMetadata metadataParent;
	
	private boolean isOnOlap = false;
	private List<FwrBusinessPackage> businessPackages;
	private List<FwrRelationStrategy> relations;
	
	public FwrBusinessModel() {
		super();
	}
	
	public FwrBusinessModel(String name, String description, List<FwrBusinessPackage> l) {
		super(name, description);
		this.businessPackages = l;
	}

	public LinkedHashMap<String, String> getLocales() {
		return locales;
	}

	public void setLocales(LinkedHashMap<String, String> locales) {
		this.locales = locales;
	}

	public void setOnOlap(boolean isOnOlap) {
		this.isOnOlap = isOnOlap;
	}

	public boolean isOnOlap() {
		return isOnOlap;
	}

	public void setBusinessPackages(List<FwrBusinessPackage> businessPackages) {
		this.businessPackages = businessPackages;
	}

	public List<FwrBusinessPackage> getBusinessPackages() {
		return businessPackages;
	}

	public void setMetadataParent(FwrMetadata metadataParent) {
		this.metadataParent = metadataParent;
	}

	public FwrMetadata getMetadataParent() {
		return metadataParent;
	}

	public void setRelations(List<FwrRelationStrategy> relations) {
		this.relations = relations;
	}

	public List<FwrRelationStrategy> getRelations() {
		return relations;
	}

	public void addRelations(FwrRelationStrategy relation) {
		if(relations == null) {
			this.relations = new ArrayList<FwrRelationStrategy>();
		}
		this.relations.add(relation);
	}

}
