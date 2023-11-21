package bpm.es.pack.manager.wizard.imp;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import adminbirep.Activator;
import bpm.birep.admin.client.inport.vanillaplace.wizard.RepositoryPlacementPage;
import bpm.es.pack.manager.I18N.Messages;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class ImportWizard extends Wizard implements INewWizard {

	private InfosConnexionPage connexionsPage;
	private RepositoryPlacementPage repoPlacementPage;
	private GroupConfigurationPage groupConfPage;
	
	private VanillaPackage vanillaPack;
	private String packageCustomName;
	
	private RepositoryDirectory targetDir;
	private boolean replaceOld = false;
	
	public ImportWizard(VanillaPackage vanillaPack, String packageCustomName) {
		this.vanillaPack = vanillaPack;
		this.packageCustomName = packageCustomName;
	}
	
	@Override
	public boolean performFinish() {

		targetDir = repoPlacementPage.getSelectedDirectory();
		replaceOld = groupConfPage.replaceOld();
		
		if(targetDir != null){
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page instanceof InfosConnexionPage && connexionsPage.isCurrentPage()) {
			boolean imp = false;
			List<Group> groups = vanillaPack.getGroups();
			List<Group> existing = null;
			try {
				existing = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
			} catch(Exception e1) {
				e1.printStackTrace();
			}
			if(vanillaPack.isIncludeGroups()) {
				
				for(Group g : groups) {
					boolean finded = false;
					for(Group e : existing) {
						if(g.getName().equals(e.getName())) {
							finded = true;
							break;
						}
					}
					if(!finded) {
						imp = MessageDialog.openConfirm(getShell(), bpm.es.pack.manager.I18N.Messages.ImportWizard_0, bpm.es.pack.manager.I18N.Messages.ImportWizard_1);
						break;
					}
				}
			}
			try {
				HashMap<Integer, Group> newGroupIds = new HashMap<Integer, Group>();
	
				for(Group group : groups) {
					Group look = null;
					for(Group g : existing) {
						if(g.getName().equals(group.getName())) {
							look = g;
							break;
						}
					}
					if(look != null) {
						newGroupIds.put(group.getId(), look);
					}
					else if(imp){
						int id = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().addGroup(group);
						int old = group.getId();
						group.setId(id);
						newGroupIds.put(old, group);
					}
				}
				
				groupConfPage.setGroupMap(newGroupIds);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return groupConfPage;
		}
		else {
			return super.getNextPage(page);
		}
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) { }

	@Override
	public void addPages() {
		connexionsPage = new InfosConnexionPage("Connexion", vanillaPack, packageCustomName); //$NON-NLS-1$
		connexionsPage.setTitle(Messages.ImportWizard_6);
		connexionsPage.setDescription(Messages.ImportWizard_7);
		addPage(connexionsPage);
		
		groupConfPage= new GroupConfigurationPage("Package Content", vanillaPack); //$NON-NLS-1$
		groupConfPage.setTitle(Messages.ImportWizard_2);
		groupConfPage.setDescription(Messages.ImportWizard_3);
		addPage(groupConfPage);
			
		repoPlacementPage = new RepositoryPlacementPage(Messages.ExportWizard_11);
		repoPlacementPage.setTitle(Messages.ImportWizard_4);
		repoPlacementPage.setDescription(Messages.ImportWizard_5);
		addPage(repoPlacementPage);
	}

	@Override
	public boolean canFinish() {
		return repoPlacementPage.getSelectedDirectory() != null;
	}

	public VanillaPackage getPackage() {
		return vanillaPack;
	}
	
	public boolean replaceOld() {
		return replaceOld;
	}

	public RepositoryDirectory getTargetDir() {
		return targetDir;
	}
}
