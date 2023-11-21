package bpm.entrepriseservices.externalobject.wizard;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import adminbirep.Activator;
import bpm.entrepriseservices.externalobject.Messages;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.core.beans.PublicUrl;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.utils.MD5Helper;


public class ExternalUrlWizard extends Wizard implements INewWizard {
	private RepositoryItem directoryItem;
	private ParametersPage paramPage;
	private PropertiesPage propPage;
	
	public ExternalUrlWizard(RepositoryItem directoryItem) {
		this.directoryItem = directoryItem;
	}

	@Override
	public boolean performFinish() {
		if (propPage.isPageComplete()) {
			Group g = propPage.selectedGroup;
			User u = propPage.selectedUser;
			String output = propPage.selectedOutput;
			
			try {
				int dsId = -1;
				if (propPage.selectedDataSource != null)
					dsId = propPage.selectedDataSource.getId();
				
				PublicUrl pu = createPublicUrl(directoryItem, Activator.getDefault().getCurrentRepository().getId(), g, u, propPage.selectedEndDate, output, dsId);
				
				for (Parameter p : paramPage.values.keySet()) {
					String value = paramPage.values.get(p);
					addPublicParameter(pu, p.getName(), value);
				}
				
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			};
		}
		return false;
	}
	
	public PublicUrl createPublicUrl(RepositoryItem item, int repId, Group group, User user, Date endDate, String outputFormat, int datasourceId) throws Exception {
		PublicUrl url = new PublicUrl();
		url.setItemId(item.getId());
		url.setRepositoryId(repId);
		url.setGroupId(group.getId());
		url.setEndDate(endDate);
		url.setCreationDate(new Date());
		url.setOutputFormat(outputFormat);
		url.setUserId(user.getId());
		url.setDatasourceId(datasourceId);
		String key = MD5Helper.encode(url.getCreationDate().toString() + new Object().hashCode());
		url.setPublicKey(key);

		int i = Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().savePublicUrl(url);
		return Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().getPublicUrlById(i);
	}
	
	public void addPublicParameter(PublicUrl pu, String parameterName, String parameterValue) throws Exception {
		if (pu.getId() <= 0) {
			throw new Exception(Messages.ExternalUrlWizard_4);
		}
		PublicParameter pp = new PublicParameter();
		pp.setPublicUrlId(pu.getId());
		pp.setParameterName(parameterName);
		pp.setParameterValue(parameterValue);

		int ppId = Activator.getDefault().getVanillaApi().getVanillaExternalAccessibilityManager().addPublicParameter(pp);
		pp.setId(ppId);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) { }

	@Override
	public void addPages() {
		propPage = new PropertiesPage(this, Messages.ExternalUrlWizard_0, directoryItem);
		propPage.setTitle(Messages.ExternalUrlWizard_1);
		addPage(propPage);
		
		paramPage = new ParametersPage(Messages.ExternalUrlWizard_2);
		paramPage.setTitle(Messages.ExternalUrlWizard_3);
		addPage(paramPage);
	}

	public void loadParameters(List<Parameter> parameters) {
		paramPage.loadParameters(parameters);
	}
}
