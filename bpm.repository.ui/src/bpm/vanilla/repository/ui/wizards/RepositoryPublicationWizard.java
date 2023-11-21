package bpm.vanilla.repository.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.wizards.page.RepositoryExportPage;
import bpm.vanilla.repository.ui.wizards.page.RepositorySelectionPage;

public abstract class RepositoryPublicationWizard extends Wizard{

//	private RepositorySelectionPage connectionPage;
	private RepositoryExportPage contentPage;
	
	
	@Override
	public void addPages() {
//		connectionPage = new RepositorySelectionPage("Define Repository Connection");
//		connectionPage.setDescription("Select or define a Repository connection");
//		connectionPage.setTitle("Connection");
//		addPage(connectionPage);
		contentPage = new RepositoryExportPage(Messages.RepositoryPublicationWizard_0);
		contentPage.setDescription(Messages.RepositoryPublicationWizard_1);
		contentPage.setTitle(Messages.RepositoryPublicationWizard_2);
		addPage(contentPage);
	
	}
	
	@Override
	public boolean performFinish() {
		
		return true;
	}

//	@Override
//	public IWizardPage getNextPage(IWizardPage page) {
//		if (page == connectionPage){
//			contentPage.fillContent(connectionPage.getRepositoryContext(), connectionPage.getRepositoryConnection());
//		}
//		return super.getNextPage(page);
//	}
}
