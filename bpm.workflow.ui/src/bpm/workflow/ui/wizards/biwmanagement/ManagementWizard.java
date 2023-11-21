package bpm.workflow.ui.wizards.biwmanagement;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.vanilla.repository.ui.wizards.page.ExportObjectWizard;
import bpm.workflow.ui.Messages;

public class ManagementWizard extends ExportObjectWizard implements INewWizard {

	@Override
	public boolean performFinish() {
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

	@Override
	protected void addContentPage() {
		contentPage = new PageRepository(Messages.ManagementWizard_0);
		contentPage.setTitle(Messages.ManagementWizard_1);

		contentPage.setDescription(Messages.ManagementWizard_2);
		addPage(contentPage);
	}

}
