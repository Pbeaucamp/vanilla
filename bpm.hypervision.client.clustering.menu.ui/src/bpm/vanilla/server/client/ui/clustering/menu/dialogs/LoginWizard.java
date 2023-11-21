package bpm.vanilla.server.client.ui.clustering.menu.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.preferences.VsaPreferenceInitializer;

public class LoginWizard extends Wizard {

	private class ConnectionPage extends WizardPage {

		private FormToolkit formToolkit;
		private ScrolledForm form;
		private Text vanillaUrl;
		private Text login, password;
		private Button saveVanillaUrl;

		protected ConnectionPage(String pageName) {
			super(pageName);
		}

		@Override
		public void createControl(Composite parent) {
			formToolkit = new FormToolkit(parent.getDisplay());

			form = formToolkit.createScrolledForm(parent);
			form.getBody().setLayout(new GridLayout());
			// form.setText("Vanilla Hypervision");
			// formToolkit.decorateFormHeading(form.getForm());
			form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));

			createConnectionPage(form.getBody());
			setControl(form.getBody());
		}

		private void createConnectionPage(Composite parent) {
			Section connection = formToolkit.createSection(parent, Section.DESCRIPTION | Section.EXPANDED | Section.TITLE_BAR);
			connection.setLayoutData(new GridData(SWT.FILL, GridData.FILL, true, true, 3, 1));
			connection.setLayout(new GridLayout());
			connection.setText(Messages.LoginWizard_0);
			connection.setDescription(Messages.LoginWizard_1);

			Composite main = formToolkit.createComposite(connection, 0);
			main.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, true));
			main.setLayout(new GridLayout(2, false));
			formToolkit.paintBordersFor(main);

			Label l = formToolkit.createLabel(main, Messages.LoginWizard_2);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			vanillaUrl = formToolkit.createText(main, ""); //$NON-NLS-1$
			vanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			vanillaUrl.setText(Activator.getDefault().getPreferenceStore().getString(VsaPreferenceInitializer.VANILLA_URL));

			l = formToolkit.createLabel(main, Messages.LoginWizard_4);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			login = formToolkit.createText(main, ""); //$NON-NLS-1$
			login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			login.setText(Activator.getDefault().getPreferenceStore().getString(VsaPreferenceInitializer.VANILLA_LOGIN));

			l = formToolkit.createLabel(main, Messages.LoginWizard_6);
			l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

			password = formToolkit.createText(main, "", SWT.PASSWORD); //$NON-NLS-1$
			password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			password.setText(Activator.getDefault().getPreferenceStore().getString(VsaPreferenceInitializer.VANILLA_PASSWORD));

			saveVanillaUrl = formToolkit.createButton(main, Messages.LoginWizard_8, SWT.CHECK);
			saveVanillaUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			saveVanillaUrl.setSelection(Activator.getDefault().getPreferenceStore().getBoolean(VsaPreferenceInitializer.SAVE_CONNECTION_INFOS));

			connection.setClient(main);
		}
	}

	private ConnectionPage page;

	@Override
	public void addPages() {
		page = new ConnectionPage(Messages.LoginWizard_9);
		page.setTitle(Messages.LoginWizard_10);
		page.setDescription(Messages.LoginWizard_11);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		try {
			IVanillaContext ctx = new BaseVanillaContext(page.vanillaUrl.getText(), page.login.getText(), page.password.getText());
			RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(ctx);

			if (vanillaApi.getVanillaSecurityManager().canAccessApp(-1, Activator.SOFT_ID)) {
				Activator.getDefault().setManager(page.vanillaUrl.getText(), page.login.getText(), page.password.getText());

				MessageDialog.openInformation(getShell(), Messages.LoginWizard_12, Messages.LoginWizard_13);
			}
			else {
				MessageDialog.openError(getShell(), Messages.LoginWizard_3, Messages.LoginWizard_5);
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.LoginWizard_14, Messages.LoginWizard_15 + ex.getMessage());

			return false;
		}

		if (page.saveVanillaUrl.getSelection()) {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			store.setValue(VsaPreferenceInitializer.VANILLA_URL, page.vanillaUrl.getText());
			store.setValue(VsaPreferenceInitializer.VANILLA_LOGIN, page.login.getText());
			store.setValue(VsaPreferenceInitializer.VANILLA_PASSWORD, page.password.getText());
		}
		return true;
	}

}
