package bpm.hypervision.client.rcp.splashHandlers;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.splash.AbstractSplashHandler;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.server.client.ui.clustering.menu.Activator;
import bpm.vanilla.server.client.ui.clustering.menu.Messages;
import bpm.vanilla.server.client.ui.clustering.menu.preferences.VsaPreferenceInitializer;

public class InteractiveSplashHandler extends AbstractSplashHandler {

	private final static int F_LABEL_HORIZONTAL_INDENT = 115;
	private final static int F_BUTTON_WIDTH_HINT = 80;
	private final static int F_TEXT_WIDTH_HINT = 200;

	private Composite fCompositeLogin;

	private Text txtVanillaUrl;
	private Text txtLogin, txtPassword;
	private Button saveVanillaUrl;

	private Button fButtonOK;
	private Button fButtonCancel;

	private boolean fAuthenticated;

	public InteractiveSplashHandler() {
	}

	public void init(final Shell splash) {
		super.init(splash);

		// Configure the shell layout
		configureUISplash();
		createUICompositeLogin();

		// Force the splash screen to layout
		splash.layout(true);

		// Keep the splash screen visible and prevent the RCP application from
		// loading until the close button is clicked.
		doEventLoop();
	}

	private void configureUISplash() {
		// Configure layout
		FillLayout layout = new FillLayout();
		getSplash().setLayout(layout);
		// Force shell to inherit the splash background
		getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
	}

	private void doEventLoop() {
		Shell splash = getSplash();
		while (fAuthenticated == false) {
			if (splash.getDisplay().readAndDispatch() == false) {
				splash.getDisplay().sleep();
			}
		}
	}

	private void createUICompositeLogin() {
		// Create the composite
		fCompositeLogin = new Composite(getSplash(), SWT.BORDER);
		GridLayout layout = new GridLayout(3, false);
		fCompositeLogin.setLayout(layout);

		Composite spanner = new Composite(fCompositeLogin, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 3;
		spanner.setLayoutData(data);

		GridData dataUrl = new GridData();
		dataUrl.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label labelUrl = new Label(fCompositeLogin, SWT.NONE);
		labelUrl.setText(Messages.LoginWizard_2);
		labelUrl.setLayoutData(dataUrl);

		GridData dataTxtUrl = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataTxtUrl.widthHint = F_TEXT_WIDTH_HINT;
		dataTxtUrl.horizontalSpan = 2;

		txtVanillaUrl = new Text(fCompositeLogin, SWT.BORDER);
		txtVanillaUrl.setLayoutData(dataTxtUrl);
		txtVanillaUrl.setText(Activator.getDefault().getPreferenceStore().getString(VsaPreferenceInitializer.VANILLA_URL));

		GridData dataLogin = new GridData();
		dataLogin.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label labelLogin = new Label(fCompositeLogin, SWT.NONE);
		labelLogin.setText(Messages.LoginWizard_4);
		labelLogin.setLayoutData(dataLogin);

		GridData dataTxtLogin = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataTxtLogin.widthHint = F_TEXT_WIDTH_HINT;
		dataTxtLogin.horizontalSpan = 2;

		txtLogin = new Text(fCompositeLogin, SWT.BORDER);
		txtLogin.setLayoutData(dataTxtLogin);
		txtLogin.setText(Activator.getDefault().getPreferenceStore().getString(VsaPreferenceInitializer.VANILLA_LOGIN));

		GridData dataPass = new GridData();
		dataPass.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label labelPass = new Label(fCompositeLogin, SWT.NONE);
		labelPass.setText(Messages.LoginWizard_6);
		labelPass.setLayoutData(dataPass);

		GridData dataTxtPass = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataTxtPass.widthHint = F_TEXT_WIDTH_HINT;
		dataTxtPass.horizontalSpan = 2;

		int style = SWT.PASSWORD | SWT.BORDER;
		txtPassword = new Text(fCompositeLogin, style);
		txtPassword.setLayoutData(dataTxtPass);
		txtPassword.setText(Activator.getDefault().getPreferenceStore().getString(VsaPreferenceInitializer.VANILLA_PASSWORD));

		GridData dataSave = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataSave.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;
		dataSave.horizontalSpan = 3;

		saveVanillaUrl = new Button(fCompositeLogin, SWT.CHECK);
		saveVanillaUrl.setText(Messages.LoginWizard_8);
		saveVanillaUrl.setLayoutData(dataSave);
		saveVanillaUrl.setSelection(Activator.getDefault().getPreferenceStore().getBoolean(VsaPreferenceInitializer.SAVE_CONNECTION_INFOS));

		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setVisible(false);

		GridData dataOk = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataOk.widthHint = F_BUTTON_WIDTH_HINT;
		dataOk.verticalIndent = 20;

		fButtonOK = new Button(fCompositeLogin, SWT.PUSH);
		fButtonOK.setText("OK");
		fButtonOK.setLayoutData(dataOk);
		fButtonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleButtonOKWidgetSelected();
			}
		});

		GridData dataCancel = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataCancel.widthHint = F_BUTTON_WIDTH_HINT;
		dataCancel.verticalIndent = 20;

		fButtonCancel = new Button(fCompositeLogin, SWT.PUSH);
		fButtonCancel.setText("Cancel");
		fButtonCancel.setLayoutData(dataCancel);
		fButtonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleButtonCancelWidgetSelected();
			}
		});

		GridData dataLbl = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataLbl.verticalIndent = 30;

		Label lblTmp = new Label(fCompositeLogin, SWT.NONE);
		lblTmp.setLayoutData(dataLbl);
	}

	private void handleButtonCancelWidgetSelected() {
		// Abort the loading of the RCP application
		getSplash().getDisplay().close();
		System.exit(0);
	}

	private void handleButtonOKWidgetSelected() {
		String vanillaUrl = txtVanillaUrl.getText();
		String username = txtLogin.getText();
		String password = txtPassword.getText();
		if ((username.length() > 0) && (password.length() > 0)) {
			try {
				IVanillaContext ctx = new BaseVanillaContext(vanillaUrl, username, password);
				RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(ctx);

				if (vanillaApi.getVanillaSecurityManager().canAccessApp(-1, Activator.SOFT_ID)) {
					Activator.getDefault().setManager(vanillaUrl, username, password);

					fAuthenticated = true;

					MessageDialog.openInformation(getSplash().getShell(), Messages.LoginWizard_12, Messages.LoginWizard_13);
				}
				else {
					MessageDialog.openError(getSplash().getShell(), Messages.LoginWizard_3, Messages.LoginWizard_5);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				MessageDialog.openError(getSplash().getShell(), Messages.LoginWizard_14, Messages.LoginWizard_15 + ex.getMessage());
			}
		}
		else {
			MessageDialog.openError(getSplash(), "Authentication Failed", //$NON-NLS-1$
					"A username and password must be specified to login."); //$NON-NLS-1$
		}

		if (fAuthenticated && saveVanillaUrl.getSelection()) {
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			store.setValue(VsaPreferenceInitializer.VANILLA_URL, vanillaUrl);
			store.setValue(VsaPreferenceInitializer.VANILLA_LOGIN, username);
			store.setValue(VsaPreferenceInitializer.VANILLA_PASSWORD, password);
		}
	}

}
