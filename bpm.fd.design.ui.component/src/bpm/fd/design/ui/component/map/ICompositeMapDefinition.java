package bpm.fd.design.ui.component.map;

import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.maps.IMapInfo;

public interface ICompositeMapDefinition {

	public Composite getClient();
	
	public Composite createContent(Composite parent);
	
	public IMapInfo getMapInfo();
	
	public void init(IMapInfo info);
}
