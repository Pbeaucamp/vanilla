package bpm.fd.api.core.model.components.definition;

import java.util.Collection;
import java.util.List;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.resources.Dictionary;

public interface IComponentDefinition extends IBaseElement{
	public static final String PROPERTY_NAME_CHANGED = "bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition.nameChanged";
	public static final String PARAMETER_CHANGED = "parameterValuesCHanged";
	public IComponentRenderer getRenderer();
	public Dictionary getDictionary();
	
	public boolean hasParameter();
	
	public Collection<ComponentParameter> getParameters();

	public void addComponentParameter(ComponentParameter parameter);
	public void removeComponentParameter(ComponentParameter parameter);
	
	public IComponentDatas getDatas();
	
	public List<IComponentOptions> getOptions();
	
	public IComponentOptions getOptions(Class<?> classOption);
	
	public String getComment();
	
	public void setComment(String comment);
	
	public void setName(String name);
	
	public ComponentStyle getComponentStyle();
	
	/**
	 * 
	 * @return the IComponentDatas index field used as output for this component
	 * or null if the component has no output
	 */
	public Integer getDatasOutputFieldIndex();
	
	/**
	 * 
	 * @return a copy of the component
	 */
	public IComponentDefinition copy();
}
