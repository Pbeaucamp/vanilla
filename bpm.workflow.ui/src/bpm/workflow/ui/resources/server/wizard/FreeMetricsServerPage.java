package bpm.workflow.ui.resources.server.wizard;

import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.freemetrics.api.manager.FactoryManager;
import bpm.freemetrics.api.utils.Tools;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Page for the creation of a freemetrics server
 * 
 * @author Charles MARTIN
 * 
 */
public class FreeMetricsServerPage extends WizardPage {
	private Combo driver;
	private Text host;
	private Text dataBase;
	private Text port;
	private Text login;
	private Text password;
	private Text dataSourceName;
	private Text fmLogin;
	private Text fmPassword;
	private Button testServer;

	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
		}
	};

	/**
	 * Create a page with the specified name
	 * 
	 * @param pageName
	 */
	protected FreeMetricsServerPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);

	}

	private void createPageContent(Composite composite) {
		Composite container = new Composite(composite, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l0 = new Label(container, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l0.setText(Messages.FreeMetricsServerPage_0);

		dataSourceName = new Text(container, SWT.BORDER);
		dataSourceName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSourceName.addModifyListener(listener);

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.FreeMetricsServerPage_1);

		driver = new Combo(container, SWT.READ_ONLY);
		driver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		try {
			driver.setItems(ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversName().toArray(new String[ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversName().size()]));
		} catch(Exception e) {
			e.printStackTrace();
		}
		driver.addModifyListener(listener);

		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.FreeMetricsServerPage_2);

		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.addModifyListener(listener);

		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.FreeMetricsServerPage_3);

		port = new Text(container, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		port.addModifyListener(listener);

		Label l5 = new Label(container, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.FreeMetricsServerPage_4);

		dataBase = new Text(container, SWT.BORDER);
		dataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataBase.addModifyListener(listener);

		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.FreeMetricsServerPage_5);

		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(listener);

		Label l7 = new Label(container, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.FreeMetricsServerPage_6);

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.addModifyListener(listener);

		Label l9 = new Label(container, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l9.setText(Messages.FreeMetricsServerPage_7);

		fmLogin = new Text(container, SWT.BORDER);
		fmLogin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fmLogin.addModifyListener(listener);

		Label l10 = new Label(container, SWT.NONE);
		l10.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l10.setText(Messages.FreeMetricsServerPage_8);

		fmPassword = new Text(container, SWT.BORDER | SWT.PASSWORD);
		fmPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fmPassword.addModifyListener(listener);

		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.FreeMetricsServerPage_9);
		testServer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText(), login.getText(), password.getText(), dataBase.getText());

					FactoryManager.init("", Tools.OS_TYPE_WINDOWS); //$NON-NLS-1$
					FactoryManager.getInstance().getManager();

					MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FreeMetricsServerPage_11, Messages.FreeMetricsServerPage_12);
				} catch(Exception eserver) {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.FreeMetricsServerPage_13, Messages.FreeMetricsServerPage_14);

				}
			}

		});

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if(!login.getText().equals("") && !host.getText().equals("") && !dataBase.getText().equals("") && !driver.getText().equals("") && !fmLogin.getText().equals("") && !fmPassword.getText().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return the values for the new freemetrics server : datasourcename,jdbcDriver,url,port,dataBaseName,username,password,fmLogin,fmPassword
	 */
	public Properties getValues() {
		Properties p = new Properties();
		p.setProperty("datasourcename", dataSourceName.getText()); //$NON-NLS-1$
		p.setProperty("jdbcDriver", driver.getText()); //$NON-NLS-1$
		p.setProperty("url", host.getText()); //$NON-NLS-1$
		p.setProperty("port", port.getText()); //$NON-NLS-1$
		p.setProperty("dataBaseName", dataBase.getText()); //$NON-NLS-1$
		p.setProperty("username", login.getText()); //$NON-NLS-1$
		p.setProperty("password", password.getText()); //$NON-NLS-1$
		p.setProperty("fmLogin", fmLogin.getText()); //$NON-NLS-1$
		p.setProperty("fmPassword", fmPassword.getText()); //$NON-NLS-1$

		return p;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

}
