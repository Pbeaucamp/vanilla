package bpm.fd.design.ui.component.wizard;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.datagrid.pages.DataGridDataPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardDataGrid extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private DataGridDataPage datasPage;
	
	private IComponentDefinition def;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);

		datasPage = new DataGridDataPage();
		addPage(datasPage);

		
		super.addPages();
	}

	
	
	@Override
	public boolean canFinish() {
		try{
			if (datasPage.getDataGridDatas() == null ){
				return false;
			}
		}catch(Exception e){
			
		}
		return super.canFinish();
	}



	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentDataGrid component = new ComponentDataGrid(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
				
		try{
			component.setComponentDatas(datasPage.getDataGridDatas());
		}catch(Exception e){
			
		}
		
		
		
		
				
		try {
			dictionaty.addComponent(component);
			
			
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardDataGrid_0, Messages.WizardDataGrid_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		def = component;
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		return true;
	}



	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		return super.getNextPage(page);
	}

	public IComponentDefinition getComponent() {
		return def;
	}
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentDataGrid.class;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return false;
	}
}
