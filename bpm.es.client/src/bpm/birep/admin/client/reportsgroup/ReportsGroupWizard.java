package bpm.birep.admin.client.reportsgroup;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.inport.vanillaplace.wizard.RepositoryPlacementPage;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.ReportsGroup;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.XStream;

public class ReportsGroupWizard extends Wizard {
	
	private ReportsGroupDefinitionPage definitionPage;
	private RepositoryPlacementPage repositoryPage;
	
	private List<RepositoryItem> items;
	
	public ReportsGroupWizard(List<RepositoryItem> items) {
		super();
		this.items = items;
	}

	@Override
	public void addPages() {
		definitionPage = new ReportsGroupDefinitionPage(Messages.ReportsGroupWizard_0);
		definitionPage.setTitle(Messages.ReportsGroupWizard_1);
		definitionPage.setDescription(Messages.ReportsGroupWizard_2);
		
		addPage(definitionPage);
		
		repositoryPage = new RepositoryPlacementPage(Messages.DisconnectedWizard_6);
		repositoryPage.setTitle(Messages.DisconnectedWizard_7);
		repositoryPage.setDescription(Messages.ReportsGroupWizard_3);
		
		addPage(repositoryPage);
	}

	@Override
	public boolean performFinish() {
		String name = definitionPage.getSelectedName();
		String description = definitionPage.getDescription();
		Object[] checkedGroups = definitionPage.getGroupsChecked();
			
		RepositoryDirectory dir = repositoryPage.getSelectedDirectory();
		
		ReportsGroup reportsGroup = new ReportsGroup();

		if(items == null) {
			return false;
		}
		for(RepositoryItem item : items) {
			reportsGroup.addReport(item.getId());
		}
			
		XStream xstream = new XStream();
		String modelXml = xstream.toXML(reportsGroup);
		

		IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
		
		RepositoryItem newItem = null;
		try {
			newItem = sock.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.REPORTS_GROUP, 0, dir, name, description, "", "", modelXml, true); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DisconnectedWizard_12, Messages.DisconnectedWizard_13 + e.getMessage());
			return false;
		}
		
		StringBuffer errors = new StringBuffer();
		try {

			for(Object g : checkedGroups) {
				try {
					Activator.getDefault().getRepositoryApi().getAdminService().addGroupForItem(((Group) g).getId(), newItem.getId());
				} catch(Exception ex) {
					errors.append(Messages.DialogCustom_5 + ((Group) g).getName() + "\n"); //$NON-NLS-1$
				}
				try {
					sock.getAdminService().setObjectRunnableForGroup(((Group) g).getId(), newItem);
				} catch(Exception ex) {
					errors.append(Messages.DialogCustom_7 + ((Group) g).getName() + "\n"); //$NON-NLS-1$
				}
				try {
					sock.getReportHistoricService().createHistoricAccess(newItem.getId(), ((Group) g).getId());
				} catch(Exception ex) {
					errors.append(Messages.DialogCustom_6 + ((Group) g).getName() + "\n"); //$NON-NLS-1$
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(errors.length() > 0) {
			MessageDialog.openError(getShell(), Messages.DialogCustom_11, Messages.DialogCustom_12 + errors.toString());
		}
			
		return true;
	}

	@Override
	public boolean canFinish() {
		return definitionPage.isPageComplete() && repositoryPage.isPageComplete();
	}
}
