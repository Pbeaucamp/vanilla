package bpm.workflow.ui.splashHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

public class LoginSplashHandler extends AbstractSplashHandler {

	public LoginSplashHandler() {
	}

	public void init(final Shell splash) {
		super.init(splash);

		// Configure the shell layout
		configureUISplash();

		// Force the splash screen to layout
		splash.layout(true);
	}

	private void configureUISplash() {
		// Configure layout
		FillLayout layout = new FillLayout();
		getSplash().setLayout(layout);
		// Force shell to inherit the splash background
		getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
	}

}
