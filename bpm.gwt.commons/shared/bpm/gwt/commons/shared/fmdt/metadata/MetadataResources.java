package bpm.gwt.commons.shared.fmdt.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataResources implements IsSerializable {
	
	private MetadataPackage parent;
	private List<MetadataResource> resources;
	
	public MetadataResources() { }

	public MetadataResources(List<MetadataResource> resources) {
		setResources(resources);
	}
	
	public MetadataPackage getParent() {
		return parent;
	}
	
	public void setParent(MetadataPackage parent) {
		this.parent = parent;
	}
	
	public List<MetadataResource> getResources() {
		return resources;
	}
	
	public void setResources(List<MetadataResource> resources) {
		this.resources = resources;
		if (resources != null) {
			for (MetadataResource resource : resources) {
				resource.setParent(this);
			}
		}
	}
	
	public void addResource(MetadataResource resource) {
		if (resources == null) {
			this.resources = new ArrayList<MetadataResource>();
		}
		resource.setParent(this);
		this.resources.add(resource);
	}

	public void updateResources(List<MetadataResource> resourcesToRemove, List<MetadataResource> resourcesToAdd) {
		if (resources != null) {
			if (resourcesToRemove != null) {
				for (MetadataResource resource : resourcesToRemove) {
					resources.remove(resource);
				}
			}
			
			if (resourcesToAdd != null) {
				for (MetadataResource resource : resourcesToAdd) {
					resource.setParent(this);
				}
				resources.addAll(resourcesToAdd);
			}
		}
	}
	
	@Override
	public String toString() {
		return "Resources";
	}
}
