package bpm.fd.design.ui.properties.model.editors;

import org.eclipse.gef.EditPart;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public interface IComponentEditor extends IPropertyEditor{
	public Composite getControl();
	public void setInput(EditPart editPart, ComponentConfig config, IComponentDefinition component);
	public void resize();
	
	
}
