package bpm.es.pack.manager.vanillaplace.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.birep.admin.client.inport.vanillaplace.wizard.RepositoryPlacementPage;
import bpm.es.pack.manager.I18N.Messages;
import bpm.vanilla.workplace.core.IPackage;


public class PlaceImportWizard extends Wizard implements INewWizard {

	private InfosConnexionPage connexionsPage;
	private RepositoryPlacementPage repoPlacementPage;
	private GroupConfigurationPage groupConfPage;
	
	private IPackage pack;
	private String path;

	public PlaceImportWizard(IPackage pack, String path) {
		this.pack = pack;
		this.path = path;
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) { }

	@Override
	public void addPages() {
		connexionsPage = new InfosConnexionPage("Connexion", null, path); //$NON-NLS-1$
		connexionsPage.setTitle(bpm.es.pack.manager.I18N.Messages.PlaceImportWizard_0);
		connexionsPage.setDescription(Messages.ExportWizard_4);
		addPage(connexionsPage);
		
		groupConfPage= new GroupConfigurationPage("Package Content"); //$NON-NLS-1$
		groupConfPage.setTitle("Package Content"); //$NON-NLS-1$
		groupConfPage.setDescription("This wizard allows to import a package from Vanilla Place."); //$NON-NLS-1$
		addPage(groupConfPage);
			
		repoPlacementPage = new RepositoryPlacementPage(Messages.ExportWizard_11);
		repoPlacementPage.setTitle(Messages.PlaceImportWizard_1);
		repoPlacementPage.setDescription(Messages.PlaceImportWizard_2);
		addPage(repoPlacementPage);
	}

	@Override
	//we define the page order
	//about the model update, it is done in the Pages.getNextPage methode
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	@Override
	public boolean canFinish() {
		return true;
	}

	public IPackage getPackage() {
		return pack;
	}
	
	public String getSelectedDirectoryPath(){
		return repoPlacementPage.getSelectedDirectoryPath();
	}
}
