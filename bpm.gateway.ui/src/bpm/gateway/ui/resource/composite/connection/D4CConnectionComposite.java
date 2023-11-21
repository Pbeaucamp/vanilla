package bpm.gateway.ui.resource.composite.connection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.server.d4c.D4CConnection;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.server.wizard.D4CConnectionPage;
import bpm.vanilla.platform.core.utils.D4CHelper;

public class D4CConnectionComposite extends Composite {

	private D4CConnection connection;
	private D4CConnectionPage page;

	private Text txtName, txtUrl, txtOrg, txtLogin, txtPassword;
	private Button btnTestConnection;

	Listener listener = new Listener() {
		public void handleEvent(Event event) {
			if (event.widget == txtName || event.widget == txtUrl || event.widget == txtOrg || event.widget == txtLogin || event.widget == txtPassword) {
				if (page != null) {
					page.updateWizardButtons();
				}
			}
		}
	};

	public D4CConnectionComposite(Composite parent, int style, D4CConnectionPage page) {
		super(parent, style);
		this.page = page;
		createPageContent(parent);

		setBackground(parent);
	}

	public D4CConnectionComposite(Composite parent, int style, D4CConnectionPage page, D4CConnection connection) {
		super(parent, style);
		this.page = page;
		this.connection = connection;
		createPageContent(parent);

		setBackground(parent);
	}

	private void createPageContent(Composite parent) {
		this.setLayout(new GridLayout(2, false));

		Label lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblName.setText(Messages.D4CConnectionComposite_0);

		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtName.addListener(SWT.SELECTED, listener);
		txtName.addListener(SWT.Selection, listener);

		Label lblUrl = new Label(this, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblUrl.setText(Messages.D4CConnectionComposite_1);

		txtUrl = new Text(this, SWT.BORDER);
		txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtUrl.addListener(SWT.SELECTED, listener);
		txtUrl.addListener(SWT.Selection, listener);

		Label lblOrg = new Label(this, SWT.NONE);
		lblOrg.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblOrg.setText(Messages.D4CConnectionComposite_2);

		txtOrg = new Text(this, SWT.BORDER);
		txtOrg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtOrg.addListener(SWT.SELECTED, listener);
		txtOrg.addListener(SWT.Selection, listener);

		final Label lblApiKey = new Label(this, SWT.NONE);
		lblApiKey.setText(Messages.D4CConnectionComposite_3);
		lblApiKey.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		txtLogin = new Text(this, SWT.BORDER);
		txtLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtLogin.addListener(SWT.SELECTED, listener);
		txtLogin.addListener(SWT.Selection, listener);

		final Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		txtPassword = new Text(this, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtPassword.addListener(SWT.SELECTED, listener);
		txtPassword.addListener(SWT.Selection, listener);

		btnTestConnection = new Button(this, SWT.PUSH);
		btnTestConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		btnTestConnection.setText(Messages.D4CConnectionComposite_4);
		btnTestConnection.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connection = getConnection();

				try {
					D4CHelper helper = new D4CHelper(connection.getUrl(), connection.getOrg(), connection.getLogin(), connection.getPassword());
					if (helper.testConnection()) {
						MessageDialog.openInformation(getShell(), Messages.D4CConnectionComposite_5, Messages.D4CConnectionComposite_6);
					}
					else {
						MessageDialog.openInformation(getShell(), Messages.D4CConnectionComposite_7, Messages.D4CConnectionComposite_8);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.D4CConnectionComposite_9, Messages.D4CConnectionComposite_10 + e1.getMessage());
				}
			}
		});

		fillData();

	}

	public D4CConnection getConnection() {
		if (connection == null) {
			connection = new D4CConnection();
		}
		connection.setName(txtName.getText());
		connection.setUrl(txtUrl.getText());
		connection.setOrg(txtOrg.getText());
		connection.setLogin(txtLogin.getText());
		connection.setPassword(txtPassword.getText());
		return connection;

	}

	public void fillData(D4CConnection dataBaseConnection) {
		connection = dataBaseConnection;
		fillData();
	}

	private void fillData() {
		if (connection != null) {
			txtName.setText(connection.getName() != null ? connection.getName() : ""); //$NON-NLS-1$
			txtUrl.setText(connection.getUrl() != null ? connection.getUrl() : ""); //$NON-NLS-1$
			txtOrg.setText(connection.getOrg() != null ? connection.getOrg() : ""); //$NON-NLS-1$
			txtLogin.setText(connection.getLogin() != null ? connection.getLogin() : ""); //$NON-NLS-1$
			txtPassword.setText(connection.getPassword() != null ? connection.getPassword() : ""); //$NON-NLS-1$
		}
	}

	private void setBackground(Composite parent) {

		this.setBackground(parent.getBackground());
		for (Control c : this.getChildren()) {
			c.setBackground(getBackground());
		}

		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

		txtName.setBackground(white);
		txtUrl.setBackground(white);
		txtOrg.setBackground(white);
		txtLogin.setBackground(white);
		txtPassword.setBackground(white);
	}
}
