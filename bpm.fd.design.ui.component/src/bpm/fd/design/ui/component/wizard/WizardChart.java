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
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.PieGenericOptions;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.chart.pages.ChartDatasPage;
import bpm.fd.design.ui.component.chart.pages.ChartGenericOptionsPage;
import bpm.fd.design.ui.component.chart.pages.ChartNaturePage;
import bpm.fd.design.ui.component.chart.pages.ChartRendererPage;
import bpm.fd.design.ui.component.chart.pages.MultiSeriesChartDatasPage;
import bpm.fd.design.ui.component.chart.pages.PieChartGenericOptionPage;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardChart extends Wizard implements IWizardComponent{

	private GeneralPage generic;
//	private ChartRendererPage rendererPage;
	private ChartDatasPage datasPage;
	private ChartNaturePage naturePage;
	private ChartGenericOptionsPage genericOptionsPage;
	
	private IComponentDefinition def;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
//		rendererPage = new ChartRendererPage();
//		addPage(rendererPage);
		
		naturePage = new ChartNaturePage();
		addPage(naturePage);
		
//		datasPage = new ChartDatasPage();
//		addPage(datasPage);
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentChartDefinition component = new ComponentChartDefinition(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
//		try{
//			component.setRenderer(rendererPage.getRenderer());
//
//		}catch(Exception e){
//			ErrorDialog.openError(getShell(), Messages.WizardChart_0, Messages.WizardChart_1, 
//					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
//			return false;
//		}
		
		try {
			component.setComponentDatas(datasPage.getChartDatas());
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(getShell(), Messages.WizardChart_2, Messages.WizardChart_3, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		try {
			component.setNature(naturePage.getNature());
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog.openError(getShell(), Messages.WizardChart_4, Messages.WizardChart_5, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		try {
			component.setRenderer(naturePage.getRenderer());
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		component.setComponentOption(genericOptionsPage.getOptions());
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardChart_6, Messages.WizardChart_7, 
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			return false;
		}
		
		def = component;
		Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, component);
		
		
		
		return true;
	}
	
	
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == naturePage){
			try{
				if (naturePage.getNature().isMonoSerie()  && (datasPage == null || datasPage instanceof MultiSeriesChartDatasPage)){
					datasPage = new ChartDatasPage();
					datasPage.setWizard(this);
					getContainer().updateButtons();
				}
				else if (!naturePage.getNature().isMonoSerie()  && (datasPage == null || !(datasPage instanceof MultiSeriesChartDatasPage))){
					datasPage = new MultiSeriesChartDatasPage();
					datasPage.setWizard(this);
					getContainer().updateButtons();
				}
				return datasPage;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		
		IWizardPage next =  super.getNextPage(page);
		
		if (next instanceof ChartNaturePage && page instanceof ChartRendererPage && page.isPageComplete()){
			try {
				((ChartNaturePage)next).initForRenderer(((ChartRendererPage)page).getRenderer());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		else if (page == datasPage){
			try {
				IComponentOptions opt = null;
				if (genericOptionsPage != null){
					opt = genericOptionsPage.getOptions();
				}
				
				if (naturePage.getNature() == ChartNature.getNature(ChartNature.PIE) || naturePage.getNature() == ChartNature.getNature(ChartNature.PIE_3D)){
					
					
					
					if (genericOptionsPage == null || !(genericOptionsPage.getClass() == PieChartGenericOptionPage.class)){
						genericOptionsPage = new PieChartGenericOptionPage();
						genericOptionsPage.setWizard(this);
//						addPage(genericOptionsPage);
					}
				}
				else{
					if (genericOptionsPage == null || !(genericOptionsPage.getClass() == ChartGenericOptionsPage.class)){
						genericOptionsPage = new ChartGenericOptionsPage();
						genericOptionsPage.setWizard(this);
//						addPage(genericOptionsPage);
					}
				}
				if (opt != null){
					genericOptionsPage.setOptions(opt, naturePage.getNature());
				}
				else{
					if (genericOptionsPage.getClass() == PieChartGenericOptionPage.class){
						genericOptionsPage.setOptions(new PieGenericOptions(), naturePage.getNature());
					}else if (genericOptionsPage.getClass() == ChartGenericOptionsPage.class){ 
						genericOptionsPage.setOptions(new GenericOptions(), naturePage.getNature());
					}
					
				}
				

				return genericOptionsPage;
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
			
		}
		
		return next;
	}
	
	
	public IComponentDefinition getComponent() {
		return def;
	}
	
	public Class<? extends IComponentDefinition> getComponentClass() {
		return ComponentChartDefinition.class;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return false;
	}
	

	@Override
	public boolean canFinish() {
		boolean dataPagsPresent = datasPage != null;
		
		if (!dataPagsPresent){
			return false;
		}
		return super.canFinish() && datasPage.isPageComplete();
	}
}
