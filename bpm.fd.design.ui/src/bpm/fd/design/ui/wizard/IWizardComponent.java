package bpm.fd.design.ui.wizard;

import org.eclipse.ui.INewWizard;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public interface IWizardComponent extends INewWizard{
	public IComponentDefinition getComponent();

	public Class<? extends IComponentDefinition> getComponentClass();
	
	public boolean needRepositoryConnections();
}
