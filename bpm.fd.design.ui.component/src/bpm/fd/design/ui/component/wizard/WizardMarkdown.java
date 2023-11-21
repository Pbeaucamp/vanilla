package bpm.fd.design.ui.component.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.markdown.ComponentMarkdown;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.markdown.pages.MarkdownPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class WizardMarkdown extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private MarkdownPage markdownPage;
	
	private IComponentDefinition def;
	
	
	/* (non-JavadSoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		markdownPage = new MarkdownPage();
		addPage(markdownPage);
		
		
		super.addPages();
	}

	
	
	
	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentMarkdown component = new ComponentMarkdown(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
		
		RepositoryItem dirIt = markdownPage.getDirectoryItemId();
		
		try {
			List<ComponentParameter> parameters = new ArrayList<ComponentParameter>();
			int i = 0;
			for(Parameter p : bpm.fd.repository.ui.Activator.getDefault().getRepositoryConnection().getRepositoryService().getParameters(dirIt)){
				ComponentParameter cp = new ReportComponentParameter(p.getName(), i++);
				parameters.add(cp);
				
			}
			
			component.defineParameter(parameters);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		component.setDirectoryItemId(dirIt.getId());
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardMarkdown_0, Messages.WizardMarkdown_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		def = component;
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		
		
		
		return true;
	}




	public IComponentDefinition getComponent() {
		return def;
	}
	
	
	
	
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentMarkdown.class;
	}
	
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return true;
	}

}
