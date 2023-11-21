package bpm.fd.design.ui.component.wizard;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.link.ComponentLink;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.link.page.LinkDefinitionPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardLink extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private LinkDefinitionPage linkPage;
	
	
	private IComponentDefinition def;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		linkPage = new LinkDefinitionPage();
		addPage(linkPage);
		
		
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentLink component = new ComponentLink(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));

		component.setComponentOption(linkPage.getOptions());
		
		int i = 0;
		for(String s : linkPage.getParameterNames()){
			ComponentParameter p = new ReportComponentParameter(s, i++);
			component.addComponentParameter(p);
		}
		
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardLink_0, Messages.WizardLink_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		
		def = component;
		
		return true;
	}
	
	
	
	

	
	public IComponentDefinition getComponent() {
		return def;
	}
	
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentLink.class;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return false;
	}
}
