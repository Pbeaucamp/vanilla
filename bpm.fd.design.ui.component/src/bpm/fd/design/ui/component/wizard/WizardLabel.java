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
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.component.text.pages.LabelPage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardLabel extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private LabelPage labelPage;
	
	
	private IComponentDefinition def;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		labelPage = new LabelPage();
		addPage(labelPage);
		
		
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		LabelComponent component = new LabelComponent(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
		
		int i = 0;
		for(String s : labelPage.getParameterNames()){
			ComponentParameter p = new ReportComponentParameter(s, i++);
			component.addComponentParameter(p);
		}
		component.setComponentOption(labelPage.getOptions());
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardLabel_0, Messages.WizardLabel_1, 
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
		return LabelComponent.class;
	}
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return false;
	}
}
