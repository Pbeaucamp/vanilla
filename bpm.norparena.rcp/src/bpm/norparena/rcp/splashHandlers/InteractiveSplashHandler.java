package bpm.norparena.rcp.splashHandlers;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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

import bpm.norparena.ui.menu.Activator;
import bpm.norparena.ui.menu.Messages;
import bpm.norparena.ui.menu.client.preferences.PreferenceConstants;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class InteractiveSplashHandler extends AbstractSplashHandler {

	private final static int F_LABEL_HORIZONTAL_INDENT = 150;
	private final static int F_BUTTON_WIDTH_HINT = 80;
	private final static int F_TEXT_WIDTH_HINT = 220;

	private Composite fCompositeLogin;

	private Text txtVanillaUrl;
	private Text txtLogin, txtPassword;
	private Button loadRepositories;
	private ComboViewer repositories;

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

		GridData dataLogin = new GridData();
		dataLogin.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label labelLogin = new Label(fCompositeLogin, SWT.NONE);
		labelLogin.setText(Messages.DialogRepository_1);
		labelLogin.setLayoutData(dataLogin);

		GridData dataTxtLogin = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataTxtLogin.widthHint = F_TEXT_WIDTH_HINT;
		dataTxtLogin.horizontalSpan = 2;

		txtLogin = new Text(fCompositeLogin, SWT.BORDER);
		txtLogin.setLayoutData(dataTxtLogin);
		txtLogin.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BI_REPOSITIRY_USER));

		GridData dataPass = new GridData();
		dataPass.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label labelPass = new Label(fCompositeLogin, SWT.NONE);
		labelPass.setText(Messages.DialogRepository_2);
		labelPass.setLayoutData(dataPass);

		GridData dataTxtPass = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataTxtPass.widthHint = F_TEXT_WIDTH_HINT;
		dataTxtPass.horizontalSpan = 2;

		int style = SWT.PASSWORD | SWT.BORDER;
		txtPassword = new Text(fCompositeLogin, style);
		txtPassword.setLayoutData(dataTxtPass);
		txtPassword.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BI_REPOSITIRY_PASSWORD));

		GridData dataUrl = new GridData();
		dataUrl.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label labelUrl = new Label(fCompositeLogin, SWT.NONE);
		labelUrl.setText(Messages.DialogRepository_3);
		labelUrl.setLayoutData(dataUrl);

		GridData dataTxtUrl = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataTxtUrl.widthHint = F_TEXT_WIDTH_HINT;
		dataTxtUrl.horizontalSpan = 2;

		txtVanillaUrl = new Text(fCompositeLogin, SWT.BORDER);
		txtVanillaUrl.setLayoutData(dataTxtUrl);
		txtVanillaUrl.setText(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.p_BI_SECURITY_SERVER));

		GridData dataLoad = new GridData(SWT.CENTER, SWT.NONE, false, false);
		dataLoad.widthHint = 220;
		dataLoad.horizontalIndent = 150;
		dataLoad.horizontalSpan = 3;

		loadRepositories = new Button(fCompositeLogin, SWT.PUSH);
		loadRepositories.setText(Messages.DialogRepository_13);
		loadRepositories.setLayoutData(dataLoad);
		loadRepositories.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				initRepositories();
			}
		});

		GridData dataRep = new GridData();
		dataRep.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;

		Label lblRep = new Label(fCompositeLogin, SWT.NONE);
		lblRep.setText(Messages.DialogRepository_4);
		lblRep.setLayoutData(dataRep);

		GridData dataTxtRep = new GridData(SWT.NONE, SWT.NONE, false, false);
		dataTxtRep.widthHint = 185;
		dataTxtRep.horizontalSpan = 2;

		repositories = new ComboViewer(fCompositeLogin, SWT.READ_ONLY);
		repositories.getControl().setLayoutData(dataTxtRep);
		repositories.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateConnectButton();
			}
		});
		repositories.setContentProvider(new ArrayContentProvider());
		repositories.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Repository) element).getName();
			}
		});

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
		initManager();
		if (!repositories.getSelection().isEmpty()) {
			String repNam = ((Repository) ((IStructuredSelection) repositories.getSelection()).getFirstElement()).getName();
			Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_CURRENT_REPOSITORY, repNam);
		}

		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_BI_REPOSITIRY_USER, txtLogin.getText());
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.P_BI_REPOSITIRY_PASSWORD, txtPassword.getText());
		Activator.getDefault().getPreferenceStore().setValue(PreferenceConstants.p_BI_SECURITY_SERVER, txtVanillaUrl.getText());
		
		fAuthenticated = true;
	}

	private void initRepositories() {
		try {
			String vanillUrl = txtVanillaUrl.getText();
			String login = txtLogin.getText();
			String password = txtPassword.getText();

			RemoteVanillaPlatform tmp = new RemoteVanillaPlatform(vanillUrl, login, password);
			User user = tmp.getVanillaSecurityManager().authentify("", login, password, false);

			if (!user.isSuperUser()) {
				throw new Exception("You are not allowed to use Norparena.");
			}

			List<Repository> list = tmp.getVanillaRepositoryManager().getRepositories();
			String[] items = new String[list.size()];
			int i = 0;
			int toSelect = -1;

			for (Repository c : list) {
				if (c.getName().equals(Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_CURRENT_REPOSITORY))) {
					toSelect = i;
				}
				items[i++] = c.getName();
			}

			repositories.setInput(list);

			if (toSelect != -1) {
				repositories.setSelection(new StructuredSelection(list.get(toSelect)));
			}

		} catch (Throwable e) {
			repositories.setInput(Collections.EMPTY_LIST);
			e.printStackTrace();
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.DialogRepository_9, e));
			MessageDialog.openError(getSplash().getShell(), Messages.DialogRepository_10, e.getMessage());
			fAuthenticated = false;
		}
		updateConnectButton();
	}

	protected void initManager() {
		try {
			Repository repDef = (Repository)((IStructuredSelection)repositories.getSelection()).getFirstElement();
			IVanillaContext ctx = new BaseVanillaContext(txtVanillaUrl.getText(), txtLogin.getText(), txtPassword.getText());
			Group dummy = new Group(); dummy.setId(-1);
			IRepositoryContext repCtx = new BaseRepositoryContext(ctx, dummy, repDef);
			RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(ctx);
			
			if(vanillaApi.getVanillaSecurityManager().canAccessApp(-1, Activator.SOFT_ID)) {
				Activator.getDefault().setRepositoryContext(repCtx);
			}
			else {
				MessageDialog.openError(getSplash().getShell(), "User Rights", "You are not allowed to access this application.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getSplash().getShell(), Messages.DialogRepository_12, e.getMessage());
		}
	}

	private void updateConnectButton() {
		try {
			loadRepositories.setEnabled(!(txtLogin.getText().isEmpty() || txtVanillaUrl.getText().isEmpty()));
			fButtonOK.setEnabled(!repositories.getSelection().isEmpty());
		} catch (NullPointerException e) {
		}
	}

}
