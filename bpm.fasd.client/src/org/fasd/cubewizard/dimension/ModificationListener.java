package org.fasd.cubewizard.dimension;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class ModificationListener implements ModifyListener {

	private WizardPage page;

	public ModificationListener(WizardPage page) {
		this.page = page;
	}

	public void modifyText(ModifyEvent e) {
		page.getWizard().getContainer().updateButtons();
	}

}
