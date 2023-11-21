package bpm.fd.repository.ui.wizard;

import bpm.fd.repository.ui.Messages;
import bpm.fd.repository.ui.wizard.pages.PageContent;
import bpm.vanilla.repository.ui.wizards.page.ExportObjectWizard;

public class FdExportWizard extends ExportObjectWizard{

	
	
	@Override
	protected void addContentPage() {
		
		contentPage = new PageContent("Content"); //$NON-NLS-1$
		contentPage.setTitle(Messages.FdExportWizard_1);
		addPage(contentPage);
	}
}
