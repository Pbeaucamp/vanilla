package bpm.gateway.ui.splashHandlers;

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
import org.eclipse.ui.splash.AbstractSplashHandler;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.exceptions.LicenceExpiredException;
import bpm.vanilla.platform.core.utils.LicenceChecker;

public class LoginSplashHandler extends AbstractSplashHandler {
	private final static int F_LABEL_HORIZONTAL_INDENT = 30;

	private boolean licenceValid = false;

	public LoginSplashHandler() {
	}

	public void init(final Shell splash) {
		super.init(splash);

		// Configure the shell layout
		configureUISplash();
		if (Activator.HAS_LICENCE) {
			try {
				checkLicence();
				licenceValid = true;
			} catch (LicenceExpiredException e) {
				e.printStackTrace();
				licenceValid = false;
			}
		}

		if (Activator.HAS_LICENCE && !licenceValid) {
			createUILicence();
		}
		else {
			licenceValid = true;
		}

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

	private void createUILicence() {
		// Create the composite
		Composite mainComp = new Composite(getSplash(), SWT.BORDER);
		GridLayout layout = new GridLayout();
		mainComp.setLayout(layout);

		Composite spanner = new Composite(mainComp, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		data.horizontalSpan = 3;
		data.verticalIndent = 150;
		data.horizontalIndent = 100;
		spanner.setLayoutData(data);

		GridData dataLbl = new GridData();
		dataLbl.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label labelLogin = new Label(mainComp, SWT.NONE);
		labelLogin.setText(Messages.LoginSplashHandler_0);
		labelLogin.setLayoutData(dataLbl);

		GridData dataLoad = new GridData(SWT.CENTER, SWT.NONE, false, false);
		dataLoad.widthHint = 100;
		dataLoad.horizontalIndent = 120;
		dataLoad.horizontalSpan = 3;

		Button closeButton = new Button(mainComp, SWT.PUSH);
		closeButton.setText(Messages.LoginSplashHandler_1);
		closeButton.setLayoutData(dataLoad);
		closeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Abort the loading of the RCP application
				getSplash().getDisplay().close();
				System.exit(0);
			}
		});
	}

	private void checkLicence() throws LicenceExpiredException {
		String applicationId = IRepositoryApi.BIG;
		String password = Activator.LICENCE_PASSWORD;

		boolean checkLicence = LicenceChecker.checkLicence(applicationId, password, true);
		if (checkLicence) {
			throw new LicenceExpiredException();
		}
	}

	private void doEventLoop() {
		Shell splash = getSplash();
		while (!licenceValid) {
			if (splash.getDisplay().readAndDispatch() == false) {
				splash.getDisplay().sleep();
			}
		}
	}

}
