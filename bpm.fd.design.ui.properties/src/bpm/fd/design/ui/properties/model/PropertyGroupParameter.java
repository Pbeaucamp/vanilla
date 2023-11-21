package bpm.fd.design.ui.properties.model;

import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.design.ui.properties.i18n.Messages;

public class PropertyGroupParameter extends PropertyGroup{
	
	
	private HashMap<ComponentParameter, PropertyGroup> props = new HashMap<ComponentParameter, PropertyGroup>();
	
	private ComponentConfig config;
	
	public PropertyGroupParameter(ComponentConfig config, CellEditor parameterProviderEditor) {
		super(Messages.PropertyGroupParameter_0, null);
		this.config = config;


		for(ComponentParameter p : config.getParameters()){
			PropertyGroup grp = new PropertyGroup(p.getLabel());
			grp.add(new Property(Messages.PropertyGroupParameter_1, parameterProviderEditor));
			grp.add(new Property(Messages.PropertyGroupParameter_2, new TextCellEditor(parameterProviderEditor.getControl().getParent())));
			props.put(p, grp);
			add(grp);
		}
		
	}
	
	public ComponentParameter removeParameter(Property p){
		for(ComponentParameter cp : config.getParameters()){
			if (cp.getLabel().equals(p.getName())){
				props.remove(cp);
				return cp;
			}
		}
		return null;
	}

	public void setPropertyValue(Property element, Object value) {
		for(ComponentParameter p : props.keySet()){
			if (props.get(p) != null && props.get(p).getProperties().contains(element)){
				if (element.getCellEditor() instanceof TextCellEditor){
					p.setDefaultValue((String)value);
				}
				else{
					if(((IComponentDefinition)value) != null) {
						config.setParameterOrigin(p, ((IComponentDefinition)value).getName());
					}
				}
				return;
			}
		}
		
	}

	public ComponentConfig getConfig(){
		return config;
	}
	
	public String getPropertyValueString(Property element) {
		String res = null;
		for(ComponentParameter p : props.keySet()){
			if (props.get(p) != null && props.get(p).getProperties().contains(element)){
				if (element.getCellEditor() instanceof TextCellEditor){
					res = p.getDefaultValue();
				}
				else{
					res = config.getComponentNameFor(p);
				}
				break;
			}
		}
		if (res == null){
			return Messages.PropertyGroupParameter_3;
		}
		return res;
	}

	public Object getPropertyValue(Property element) {
		for(ComponentParameter p : props.keySet()){
			if (props.get(p) != null && props.get(p).getProperties().contains(element)){
				if (element.getCellEditor() instanceof TextCellEditor){
					return p.getDefaultValue();
				}
				else{
					
					return config.getTargetComponent().getDictionary().getComponent(config.getComponentNameFor(p)) ;
				}
			}
		}
		return ""; //$NON-NLS-1$
	}

}
