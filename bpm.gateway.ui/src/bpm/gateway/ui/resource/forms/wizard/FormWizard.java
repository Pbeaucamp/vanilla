package bpm.gateway.ui.resource.forms.wizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.gateway.core.forms.Form;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;

public class FormWizard extends Wizard implements INewWizard {

	protected static final String GENERAL_PAGE_NAME = "Form Definition"; //$NON-NLS-1$
	protected static final String MAPPING_PAGE_NAME = "Form Mapping"; //$NON-NLS-1$
	
	private FormDefinitionPage definitionPage;
	private FormMappingPage mappingPage;
	
	
	public FormWizard() {
		if (Activator.getDefault().getRepositoryContext() == null){
			MessageDialog.openInformation(getShell(), Messages.FormWizard_8, Messages.FormWizard_9);
			throw new RuntimeException(Messages.FormWizard_7);
		}
	}

	@Override
	public boolean performFinish() {
		Form f = new Form();
		f.setName(definitionPage.getFormName());
		f.setDirectoryItemId(definitionPage.dirIt.getId());
		
		
		
		for(Parameter p : mappingPage.getMap().keySet()){
			f.map(p, mappingPage.getMap().get(p));
		}
		
		Activator.getDefault().getCurrentInput().getDocumentGateway().addForm(f);
		
		
		
		return true;
		
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		

	}
	@Override
	public void addPages() {

		definitionPage = new FormDefinitionPage(GENERAL_PAGE_NAME);
		definitionPage.setDescription(Messages.FormWizard_2);
		definitionPage.setTitle(Messages.FormWizard_3);
		addPage(definitionPage);
		
		mappingPage = new FormMappingPage(MAPPING_PAGE_NAME);
		mappingPage.setDescription(Messages.FormWizard_4);
		mappingPage.setTitle(Messages.FormWizard_5);
		addPage(mappingPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == definitionPage){
			try {
				mappingPage.fill(Activator.getDefault().getRepositoryConnection(), definitionPage.dirIt);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.FormWizard_6, e.getMessage());
				return page;
			}
		}
		return super.getNextPage(page);
	}
	
	
}

