package bpm.birep.admin.client.disco;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.inport.vanillaplace.wizard.RepositoryPlacementPage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.disco.DiscoPackage;
import bpm.vanilla.platform.core.beans.disco.DiscoReportConfiguration;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.XStream;

public class DisconnectedWizard extends Wizard {
	
	private DisconnectedDefinitionPage definitionPage;
	private DisconnectedReportConfigurationPage reportConfigPage;
	private RepositoryPlacementPage repositoryPage;
//	private DisconnectedSavePage savePage;
	
//	private IRepositoryApi repositoryApi;
	private List<RepositoryItem> items;
//	private List<Group> groups;
	
	public DisconnectedWizard(List<RepositoryItem> items) {
		super();
		this.items = items;
	}

	@Override
	public void addPages() {
		definitionPage = new DisconnectedDefinitionPage(Messages.DisconnectedWizard_0);
		definitionPage.setTitle(Messages.DisconnectedWizard_1);
		definitionPage.setDescription(Messages.DisconnectedWizard_2);
		
		addPage(definitionPage);
		
		reportConfigPage = new DisconnectedReportConfigurationPage(Messages.DisconnectedWizard_3, items);
		reportConfigPage.setTitle(Messages.DisconnectedWizard_4);
		reportConfigPage.setDescription(Messages.DisconnectedWizard_5);
		
		addPage(reportConfigPage);
		
		repositoryPage = new RepositoryPlacementPage(Messages.DisconnectedWizard_6);
		repositoryPage.setTitle(Messages.DisconnectedWizard_7);
		repositoryPage.setDescription(Messages.DisconnectedWizard_8);
		
		addPage(repositoryPage);
		
//		savePage = new DisconnectedSavePage("Save", groups);
//		savePage.setTitle("Save");
//		savePage.setDescription(Messages.DisconnectedWizard_8);
//		
//		addPage(savePage);
	}
	
//	@Override
//	public IWizardPage getNextPage(IWizardPage page) {
//		if(page instanceof DisconnectedDefinitionPage) {
//			if(((DisconnectedDefinitionPage)page).isMobile()) {
//				return reportConfigPage;
//			}
//			else {
//				return savePage;
//			}
//		}
//		return super.getNextPage(page);
//	}

	@Override
	public boolean performFinish() {
		String name = definitionPage.getSelectedName();
		String description = definitionPage.getDescription();
		
//		if (!definitionPage.isMobile()) {
//			int limitRow = savePage.getLimitRows();
//			String pathToSave = savePage.getSelectedPath();
//			String groupName = savePage.getGroupName();
//			
//			DisconnectedManager docManager = new DisconnectedManager(repositoryApi, pathToSave, name.trim(), groupName, limitRow, items);
//			try {
//				docManager.createOfflinePackage();
//			} catch (Exception e) {
//				e.printStackTrace();
//				return false;
//			}
//			
//			return true;
//		}
//		else {
			List<DiscoReportConfiguration> configs = reportConfigPage.getConfigs();
			
			RepositoryDirectory dir = repositoryPage.getSelectedDirectory();
			
			DiscoPackage discoPack = new DiscoPackage();
			discoPack.setName(name);
			discoPack.setDescription(description);
			discoPack.setConfigs(configs);
			
			XStream xstream = new XStream();
			String modelXml = xstream.toXML(discoPack);
			
			IRepositoryApi api = Activator.getDefault().getRepositoryApi();
			try {
				api.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.DISCONNECTED_PCKG, 0, dir, name, description, "", "", modelXml, true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.DisconnectedWizard_12, Messages.DisconnectedWizard_13 + e.getMessage());
				return false;
			}
			
			return true;
//		}
	}

	@Override
	public boolean canFinish() {
		return definitionPage.isPageComplete() && reportConfigPage.isPageComplete() && repositoryPage.isPageComplete();
	}
}
