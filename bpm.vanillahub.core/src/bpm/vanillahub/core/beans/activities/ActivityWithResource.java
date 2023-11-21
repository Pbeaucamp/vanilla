package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public abstract class ActivityWithResource<T extends Resource> extends Activity {

	private T resource;
	
	public ActivityWithResource() {
		super();
	}
	
	public ActivityWithResource(TypeActivity type, String name) {
		super(type, name);
	}
	
	public void setResource(T resource) {
		this.resource = resource;
	}
	
	public int getResourceId() {
		return resource != null ? resource.getId() : -1;
	}
	
	public Resource getResource(List<? extends Resource> resources) {
		return resource != null ? findResource(resource, resources) : null;
	}

	protected Resource findResource(T resource, List<? extends Resource> resources) {
		if(resources != null) {
			for(Resource currentResource : resources) {
				if (resource.getId() == currentResource.getId()) {
					return currentResource;
				}
			}
		}
		return this.resource;
	}
	
	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		Resource resource = getResource(resources);
		return resource != null ? resource.getVariables() : new ArrayList<Variable>();
	}
	
	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		Resource resource = getResource(resources);
		return resource != null ? resource.getParameters() : new ArrayList<Parameter>();
	}
	
}
