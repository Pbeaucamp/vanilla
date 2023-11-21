package bpm.birep.admin.client.dialog;

import org.apache.commons.io.IOUtils;
import org.eclipse.swt.widgets.Shell;

import adminbirep.Activator;

public class SearchReplaceSetup extends DialogSearchReplace {

	public SearchReplaceSetup(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void okPressed() {
		String oldChar = super.searchString.getText();
		String newChar = super.newString.getText();

		try {
			String xml = Activator.getDefault().getVanillaApi().getVanillaSystemManager().getVanillaSetup().getXml().replace(oldChar, newChar);
			Activator.getDefault().getVanillaApi().getVanillaSystemManager().updateVanillaSetup(new SetupDigester(IOUtils.toInputStream(xml, "UTF-8")).getVanillaSetup()); //$NON-NLS-1$
		}
		catch (Exception e) {
			return;
		}
		super.okPressed();
	}
}
