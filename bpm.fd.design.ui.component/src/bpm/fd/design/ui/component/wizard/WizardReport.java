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
import bpm.fd.api.core.model.components.definition.report.ComponentReport;
import bpm.fd.api.core.model.components.definition.report.ReportComponentParameter;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.component.report.pages.ReportPage;
import bpm.fd.design.ui.wizard.IWizardComponent;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class WizardReport extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private ReportPage reportPage;
	
	private IComponentDefinition def;
	
	
	/* (non-JavadSoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		reportPage = new ReportPage();
		addPage(reportPage);
		
		
		super.addPages();
	}

	
	
	
	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentReport component = new ComponentReport(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
		
		RepositoryItem dirIt = reportPage.getDirectoryItemId();
		
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
			ErrorDialog.openError(getShell(), Messages.WizardReport_0, Messages.WizardReport_1, 
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
		return ComponentReport.class;
	}
	
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return true;
	}

}
