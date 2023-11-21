package bpm.fd.api.core.model.components.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import bpm.fd.api.core.model.structure.Cell;

public class ComponentConfig {
	public static final String dimensionProvided = "DimensionProvided";
	
	private IComponentDefinition componentDef;
	private HashMap<ComponentParameter, String[]> config = new LinkedHashMap<ComponentParameter, String[]>();
	private Cell parent;
	
	
	public ComponentConfig(Cell parent, IComponentDefinition componentDef){
		this.componentDef = componentDef;
		this.parent = parent;
		for(ComponentParameter p :componentDef.getParameters()){
			config.put(p, new String[]{"", null});
		}
	}
	public Cell getCell(){
		return parent;
	}
	
	public IComponentDefinition getTargetComponent(){
		return componentDef;
	}
	
	public void setParameterOrigin(ComponentParameter parameter, String origin){
		String oldValue = config.get(parameter)[0];
		config.put(parameter, new String[]{origin, null});
		componentDef.firePropertyChange(IComponentDefinition.PARAMETER_CHANGED, oldValue, origin);
	}
	
	public boolean isDimensionProvided(ComponentParameter p){
		if (config.get(p) == null){
			return false;
		}
		return config.get(p).equals(dimensionProvided);
	}
	
	public String getComponentNameFor(ComponentParameter p){
		if (isDimensionProvided(p) && componentDef != null){
			return componentDef.getId();
		}
		if (config.get(p) != null){
			return config.get(p)[0];
		}
		return null;
	}
	
	public Set<ComponentParameter> getParameters(){
		for(ComponentParameter p :componentDef.getParameters()){
			for(ComponentParameter pp : config.keySet()){
				if (pp.getName().equals(p.getName()) && p != pp){
					config.put(p, config.get(pp));
					config.remove(pp);
					break;
				}
			}
			
			if (config.get(p) == null){
				config.put(p, new String[]{"", null});
			}
			
			
		}
		
		List<ComponentParameter> toRemove = new ArrayList<ComponentParameter>(config.keySet());
		
		for(ComponentParameter pp : config.keySet()){
			for(ComponentParameter p :componentDef.getParameters()){
				if (p.getName().equals(pp.getName())){
					toRemove.remove(pp);
				}
			}
		}
		for(ComponentParameter p : toRemove){
			config.remove(p);
		}
		
		return config.keySet();
	}

	public String getComponentOutputName(ComponentParameter element) {
		return config.get(element)[1];
	}

	public void setComponentOutputName(ComponentParameter data, String outputName) {
		String[] oldValue = config.get(data);
		String[] v = new String[]{oldValue[0], outputName};
		config.put(data, v);
		componentDef.firePropertyChange(IComponentDefinition.PARAMETER_CHANGED, oldValue, v);

		
	}

	
}
