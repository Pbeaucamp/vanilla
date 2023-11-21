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

import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Page for the creation of a DataBase server
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class DataBasePage extends WizardPage {
	private Combo driver;
	private Text host;
	private Text dataBase;
	private Text port;
	private Text login;
	private Text password;
	private Text schema;
	private Text dataSourceName;

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
	protected DataBasePage(String pageName) {
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
		l0.setText(Messages.DataBasePage_0);

		dataSourceName = new Text(container, SWT.BORDER);
		dataSourceName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSourceName.addModifyListener(listener);

		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.DataBasePage_1);

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
		l3.setText(Messages.DataBasePage_2);

		host = new Text(container, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.addModifyListener(listener);

		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.DataBasePage_3);

		port = new Text(container, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		port.addModifyListener(listener);

		Label l5 = new Label(container, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.DataBasePage_4);

		dataBase = new Text(container, SWT.BORDER);
		dataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataBase.addModifyListener(listener);

		Label l6 = new Label(container, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.DataBasePage_5);

		login = new Text(container, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(listener);

		Label l7 = new Label(container, SWT.PASSWORD);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l7.setText(Messages.DataBasePage_6);

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.addModifyListener(listener);

		Label l8 = new Label(container, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l8.setText(Messages.DataBasePage_7);

		schema = new Text(container, SWT.BORDER);
		schema.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		schema.addModifyListener(listener);

		testServer = new Button(composite, SWT.PUSH);
		testServer.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		testServer.setText(Messages.DataBasePage_8);
		testServer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SQLConnection connection = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText(), login.getText(), password.getText(), schema.getText());
					connection.test();
					MessageDialog.openInformation(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DataBasePage_9, Messages.DataBasePage_10);
				} catch(Exception eserver) {
					MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DataBasePage_11, Messages.DataBasePage_12);

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
		if(!login.getText().equals("") && //$NON-NLS-1$
				!host.getText().equals("") && //$NON-NLS-1$
				!dataBase.getText().equals("") && //$NON-NLS-1$
				!driver.getText().equals("")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @return the values of the new DataBase : datasourcename,jdbcDriver,url,port,dataBaseName,username,password,schemaName
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
		if(schema.getText() != null && !schema.getText().equals("")) //$NON-NLS-1$
			p.setProperty("schemaName", schema.getText()); //$NON-NLS-1$

		return p;
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

}
