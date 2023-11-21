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
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.filter.pages.FilterDatasPage;
import bpm.fd.design.ui.component.filter.pages.FilterOptionsPage;
import bpm.fd.design.ui.component.filter.pages.FilterRendererPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardFilter extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private FilterRendererPage rendererPage;
	private FilterDatasPage datasPage;
	private FilterOptionsPage optionPage;
	
	private IComponentDefinition def;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		

		rendererPage = new FilterRendererPage();
		addPage(rendererPage);
		
		datasPage = new FilterDatasPage();
		addPage(datasPage);
		
		
		optionPage = new FilterOptionsPage();
		addPage(optionPage);
		
		super.addPages();
	}

	
	
	@Override
	public boolean canFinish() {
		try{
			if (rendererPage.getRenderer().getRendererStyle() == FilterRenderer.DATE_PIKER || rendererPage.getRenderer().getRendererStyle() == FilterRenderer.TEXT_FIELD || rendererPage.getRenderer().getRendererStyle() == FilterRenderer.DYNAMIC_TEXT){
				return true;
			}
		}catch(Exception e){
			
		}
		return super.canFinish();
	}



	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentFilterDefinition component = new ComponentFilterDefinition(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
		try{
			component.setRenderer(rendererPage.getRenderer());
			
			
		}catch(Exception e){
			ErrorDialog.openError(getShell(), Messages.WizardFilter_0, Messages.WizardFilter_1, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		try{
			component.setComponentDatas(datasPage.getFilterDatas());
		}catch(Exception e){
			
		}
		
		
		FilterOptions opt = optionPage.getOptions();
		
		if (component.getOptions().get(0) instanceof FilterOptions){
			((FilterOptions)component.getOptions().get(0)).setSelectFirstValue(opt.isSelectFirstValue());
			((FilterOptions)component.getOptions().get(0)).setDefaultValue(opt.getDefaultValue());
			((FilterOptions)component.getOptions().get(0)).setSubmitOnChange(opt.isSubmitOnChange());
			((FilterOptions)component.getOptions().get(0)).setRequired(opt.isRequired());

		}
		
				
		try {
			dictionaty.addComponent(component);
			
			
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardFilter_2, Messages.WizardFilter_3, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		
		def = component;
		return true;
	}



	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		try{
			if (page == rendererPage && 
					(rendererPage.getRenderer().getRendererStyle() == FilterRenderer.DATE_PIKER || 
					 rendererPage.getRenderer().getRendererStyle() == FilterRenderer.TEXT_FIELD ||
					 rendererPage.getRenderer().getRendererStyle() == FilterRenderer.DYNAMIC_TEXT)){
				return optionPage;
			}
		}catch(Exception e){
			
		}
		
		return super.getNextPage(page);
	}

	public IComponentDefinition getComponent() {
		return def;
	}
	
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentFilterDefinition.class;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return false;
	}
}
