package bpm.gateway.ui.resource.server.wizard;

import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.d4c.D4CServer;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraServer;
import bpm.gateway.core.server.database.nosql.hbase.HBaseServer;
import bpm.gateway.core.server.database.nosql.mongodb.MongoDbServer;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.ui.i18n.Messages;

public class ServerTypePage extends WizardPage {

	private Button bFiles, bDataBase, bLdap, btnCassandra, btnHabse, bVanillaFile, btnMongoDb, btnD4C;

	private Text name, description;

	protected ServerTypePage(String pageName) {
		super(pageName);

	}

	public void createControl(Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);

	}

	private Control createPageContent(Composite parent) {
		Group general = new Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(2, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.ServerTypePage_0);

		Label l = new Label(general, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ServerTypePage_1);

		name = new Text(general, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				ServerTypePage.this.setMessage(Messages.ServerTypePage_2);

			}

			public void focusLost(FocusEvent e) {
				ServerTypePage.this.setMessage(""); //$NON-NLS-1$

			}

		});
		name.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();

				for (Server s : ResourceManager.getInstance().getServers(getSelectedServerTypeClass())) {
					if (name.getText().equals(s.getName())) {
						setPageComplete(false);
						setErrorMessage(Messages.ServerTypePage_4);
						break;
					}
					else {
						setErrorMessage(null);
					}
				}
			}

		});

		Label l2 = new Label(general, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l2.setText(Messages.ServerTypePage_5);

		description = new Text(general, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		description.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				ServerTypePage.this.setMessage(Messages.ServerTypePage_6);

			}

			public void focusLost(FocusEvent e) {
				ServerTypePage.this.setMessage(""); //$NON-NLS-1$

			}

		});
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Group g = new Group(parent, SWT.NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		g.setText(Messages.ServerTypePage_8);

		RadioListener buttonListener = new RadioListener();

		bFiles = new Button(g, SWT.RADIO);
		bFiles.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bFiles.setText(Messages.ServerTypePage_9);
		bFiles.addSelectionListener(buttonListener);

		// TODO : enable once it is implemented
		bFiles.setEnabled(false);

		bDataBase = new Button(g, SWT.RADIO);
		bDataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bDataBase.setText(Messages.ServerTypePage_10);
		bDataBase.addSelectionListener(buttonListener);
		bDataBase.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				ServerTypePage.this.setMessage(Messages.ServerTypePage_11);

			}

			public void focusLost(FocusEvent e) {
				ServerTypePage.this.setMessage(""); //$NON-NLS-1$

			}
		});

//		bFreemetrics = new Button(g, SWT.RADIO);
//		bFreemetrics.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		bFreemetrics.setText(Messages.ServerTypePage_13);
//		bFreemetrics.addSelectionListener(buttonListener);

		// bGateway = new Button(g, SWT.RADIO);
		// bGateway.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
		// true, false));
		// bGateway.setText(Messages.ServerTypePage_15);
		// bGateway.addSelectionListener(buttonListener);

		bLdap = new Button(g, SWT.RADIO);
		bLdap.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bLdap.setText(Messages.ServerTypePage_16);
		bLdap.addSelectionListener(buttonListener);

		btnCassandra = new Button(g, SWT.RADIO);
		btnCassandra.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		btnCassandra.setText(Messages.ServerTypePage_3);
		btnCassandra.addSelectionListener(buttonListener);

		btnHabse = new Button(g, SWT.RADIO);
		btnHabse.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		btnHabse.setText(Messages.ServerTypePage_7);
		btnHabse.addSelectionListener(buttonListener);

		btnMongoDb = new Button(g, SWT.RADIO);
		btnMongoDb.setLayoutData(new GridData(new GridData().FILL, GridData.CENTER, true, false));
		btnMongoDb.setText(Messages.ServerTypePage_12);
		btnMongoDb.addSelectionListener(buttonListener);

		btnD4C = new Button(g, SWT.RADIO);
		btnD4C.setLayoutData(new GridData(new GridData().FILL, GridData.CENTER, true, false));
		btnD4C.setText(Messages.ServerTypePage_14);
		btnD4C.addSelectionListener(buttonListener);

		bVanillaFile = new Button(g, SWT.RADIO);
		bVanillaFile.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bVanillaFile.setText(Messages.ServerTypePage_18);
		bVanillaFile.addSelectionListener(buttonListener);
		bVanillaFile.setEnabled(false);
		return g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !name.getText().equals("") && (bDataBase.getSelection() || bFiles.getSelection() || bLdap.getSelection() || btnCassandra.getSelection() || bVanillaFile.getSelection() || btnHabse.getSelection() || btnMongoDb.getSelection() || btnD4C.getSelection()); //$NON-NLS-1$
	}

	/**
	 * 
	 * @return a properties object containing the values filled is that page the
	 *         Keys are : - name - description - type
	 */
	protected Properties getValues() {
		Properties p = new Properties();

		p.setProperty("name", name.getText()); //$NON-NLS-1$
		p.setProperty("description", description.getText()); //$NON-NLS-1$

		p.setProperty("type", getType()); //$NON-NLS-1$

		// TODO: take care of the other Server type

		return p;
	}

	@Override
	public IWizardPage getNextPage() {
		if (bDataBase.getSelection()) {
			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
			page.cleanComposite();
			return getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
		}
//		if (bFreemetrics.getSelection()) {
//			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
//			page.cleanComposite();
//			page.createFreemetricsInfo();
//			return page;
//		}

		if (bVanillaFile.getSelection()) {
			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
			page.cleanComposite();
			final VanillaRepositoryFiilePage r = (VanillaRepositoryFiilePage) getWizard().getPage(ServerWizard.VANILLA_FILE_CONNECTION_PAGE_NAME);
			return r;
		}
		if ((bLdap.getSelection())) {
			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
			page.cleanComposite();
			return getWizard().getPage(ServerWizard.LDAP_CONNECTION_PAGE_NAME);
		}
		if ((btnCassandra.getSelection())) {
			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
			page.cleanComposite();
			return getWizard().getPage(ServerWizard.CASSANDRA_CONNECTION_PAGE_NAME);
		}
		if (btnHabse.getSelection()) {
			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
			page.cleanComposite();
			return getWizard().getPage(ServerWizard.HBASE_CONNECTION_PAGE_NAME);
		}
		if (btnMongoDb.getSelection()) {
			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
			page.cleanComposite();
			return getWizard().getPage(ServerWizard.MONGODB_CONNECTION_PAGE_NAME);
		}
		if (btnD4C.getSelection()) {
			ConnectionPage page = (ConnectionPage) getWizard().getPage(ServerWizard.SQL_CONNECTION_PAGE_NAME);
			page.cleanComposite();
			return getWizard().getPage(ServerWizard.D4C_CONNECTION_PAGE_NAME);
		}

		return this;
	}

	protected String getType() {
		if (bDataBase.getSelection()) {
			return Server.DATABASE_TYPE;
		}
//		if (bFreemetrics.getSelection()) {
//			return Server.FREEMETRICS_TYPE;
//		}
		if ((bLdap.getSelection())) {
			return Server.LDAP_TYPE;
		}

		if ((bVanillaFile.getSelection())) {
			return Server.FILE_TYPE;
		}

		if ((btnCassandra.getSelection())) {
			return Server.CASSANDRA_TYPE;
		}

		if (btnHabse.getSelection()) {
			return Server.HBASE_TYPE;
		}
		if (btnMongoDb.getSelection()) {
			return Server.MONGODB_TYPE;
		}
		if (btnD4C.getSelection()) {
			return Server.D4C_TYPE;
		}
		return null;
	}

	private class RadioListener extends SelectionAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			ServerTypePage.this.getContainer().updateButtons();
		}

	}

	private Class getSelectedServerTypeClass() {
		if (bDataBase.getSelection()) {
			return DataBaseServer.class;
		}
		if (bFiles.getSelection()) {
			return null;
		}
//		if (bFreemetrics.getSelection()) {
//			return null;
//		}
		if (btnCassandra.getSelection()) {
			return CassandraServer.class;
		}
		if (btnHabse.getSelection()) {
			return HBaseServer.class;
		}
		if (btnMongoDb.getSelection()) {
			return MongoDbServer.class;
		}
		if (btnD4C.getSelection()) {
			return D4CServer.class;
		}
		return null;
	}
}
