package bpm.fd.design.ui.component.wizard;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.image.ComponentPicture;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.pages.GeneralPage;
import bpm.fd.design.ui.component.picture.pages.PicturePage;
import bpm.fd.design.ui.wizard.IWizardComponent;

public class WizardPicture extends Wizard implements IWizardComponent{

	private GeneralPage generic;
	private PicturePage labelPage;
	
	
	private IComponentDefinition def;
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		generic = new GeneralPage();
		addPage(generic);
		
		labelPage = new PicturePage();
		addPage(labelPage);
		
		
		super.addPages();
	}

	@Override
	public boolean performFinish() {
		Dictionary dictionaty = Activator.getDefault().getProject().getDictionary();
		Properties prop = generic.getValues();
		
		ComponentPicture component = new ComponentPicture(prop.getProperty(GeneralPage.PROPERTY_NAME), dictionaty);
		component.setComment(prop.getProperty(GeneralPage.PROPERTY_DESCRIPTION));
		
		component.setPictureUrl(labelPage.getPictureUrl());
		
		try {
			dictionaty.addComponent(component);
			
		} catch (DictionaryException e) {
			ErrorDialog.openError(getShell(), Messages.WizardPicture_0, Messages.WizardPicture_1, 
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
		return ComponentPicture.class;
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	public boolean needRepositoryConnections() {
		return false;
	}

}
