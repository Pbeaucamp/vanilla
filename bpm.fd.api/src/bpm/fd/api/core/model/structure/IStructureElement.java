package bpm.fd.api.core.model.structure;

import java.util.List;

import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public interface IStructureElement extends IBaseElement{
	public static final String P_CONTENT_ADDED = "bpm.fd.api.core.model.structure.properties.contentAdded";
	public static final String P_CONTENT_REMOVED = "bpm.fd.api.core.model.structure.properties.contentRemoved";
	public static final String P_CONTENT_HOVER = "bpm.fd.api.core.model.structure.properties.hover";
	public static final String P_CONTENT_HOVER_RELEASE = "bpm.fd.api.core.model.structure.properties.hoverRelease";
	
	
	
	
	public IStructureElement getParentStructureElement();
	public void setParentStructureElement(IStructureElement parent);
	public List<IBaseElement> getContent();
	
	public boolean removeFromContent(IStructureElement element);
	public boolean addToContent(IStructureElement element);
	
	public void removeComponent(IComponentDefinition component);
	
	
	/**
	 * 
	 * @param oldComponentName
	 * @return the Cell with a component which have a parameter set by a componentNamed oldComponentname
	 */
	public List<Cell> getCellsUsingParameterProviderNames(String oldComponentName);

//	public IStyle getStyle(Class<? extends IStyle> styleClass);
//	public Class<? extends IStyle>[] getAvailableStyles();
}
