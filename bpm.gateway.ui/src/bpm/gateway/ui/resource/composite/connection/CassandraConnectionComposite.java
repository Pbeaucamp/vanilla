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

import bpm.gateway.core.server.database.nosql.cassandra.CassandraConnection;
import bpm.gateway.core.server.database.nosql.cassandra.CassandraHelper;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.server.wizard.CassandraConnectionPage;

public class CassandraConnectionComposite extends Composite {

	private Text txtName;
	private Text txtLogin;
	private Text txtPassword;
	private Text txtHost;
	private Text txtPort;
	private Text txtKeyspace;
	
	private Button btnTestConnection;
	
	private CassandraConnection connection;
	private CassandraConnectionPage page;
	
	public CassandraConnectionComposite(Composite parent, int style, CassandraConnectionPage page) {
		super(parent, style);
		this.page = page;
		createPageContent(parent);
		
		setBackground(parent);
	}
	
	public CassandraConnectionComposite(Composite parent, int style, CassandraConnectionPage page, CassandraConnection connection) {
		super(parent, style);
		this.page = page;
		this.connection = connection;
		createPageContent(parent);
		
		setBackground(parent);
	}
	
	private void setBackground(Composite parent) {

		this.setBackground(parent.getBackground());
		for(Control c : this.getChildren()){
			c.setBackground(getBackground());
		}

		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

		txtName.setBackground(white);
		txtLogin.setBackground(white);
		txtPassword.setBackground(white);
		txtHost.setBackground(white);
		txtPort.setBackground(white);
		txtKeyspace.setBackground(white);
	}

	private void createPageContent(Composite parent) {
		this.setLayout(new GridLayout(2, false));
		
		Label lblName = new Label(this, SWT.NONE);
		lblName.setText(Messages.CassandraConnectionComposite_0);
		lblName.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtName.addListener(SWT.SELECTED, listener);
		txtName.addListener(SWT.Selection, listener);
		
		Label lblLogin = new Label(this, SWT.NONE);
		lblLogin.setText(Messages.CassandraConnectionComposite_1);
		lblLogin.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		txtLogin = new Text(this, SWT.BORDER);
		txtLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtLogin.addListener(SWT.SELECTED, listener);
		txtLogin.addListener(SWT.Selection, listener);
		
		Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText(Messages.CassandraConnectionComposite_2);
		lblPassword.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		txtPassword = new Text(this, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtPassword.addListener(SWT.SELECTED, listener);
		txtPassword.addListener(SWT.Selection, listener);
		
		Label lblHost = new Label(this, SWT.NONE);
		lblHost.setText(Messages.CassandraConnectionComposite_3);
		lblHost.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		txtHost = new Text(this, SWT.BORDER);
		txtHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtHost.addListener(SWT.SELECTED, listener);
		txtHost.addListener(SWT.Selection, listener);
		
		Label lblPort = new Label(this, SWT.NONE);
		lblPort.setText(Messages.CassandraConnectionComposite_4);
		lblPort.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		txtPort = new Text(this, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtPort.addListener(SWT.SELECTED, listener);
		txtPort.addListener(SWT.Selection, listener);
		
		Label lblKeyspace = new Label(this, SWT.NONE);
		lblKeyspace.setText(Messages.CassandraConnectionComposite_5);
		lblKeyspace.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		txtKeyspace = new Text(this, SWT.BORDER);
		txtKeyspace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtKeyspace.addListener(SWT.SELECTED, listener);
		txtKeyspace.addListener(SWT.Selection, listener);
		
		btnTestConnection = new Button(this, SWT.PUSH);
		btnTestConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		btnTestConnection.setText(Messages.CassandraConnectionComposite_6);
		btnTestConnection.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				connection = getConnection();
				
				try {
					if(CassandraHelper.testConnection(connection)){
						MessageDialog.openInformation(getShell(), 
								Messages.CassandraConnectionComposite_7, Messages.CassandraConnectionComposite_8);
					}
					else {
						MessageDialog.openInformation(getShell(), 
								Messages.CassandraConnectionComposite_9, Messages.CassandraConnectionComposite_10);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), 
							Messages.CassandraConnectionComposite_11, Messages.CassandraConnectionComposite_12 + e1.getMessage());
				}
			}
		});
		
		fillData();
	}
	
	private Listener listener = new Listener(){

		public void handleEvent(Event event) {
			if (event.widget == txtName || event.widget == txtLogin || event.widget == txtPassword || event.widget == txtHost
					|| event.widget == txtPort || event.widget == txtKeyspace){
				if(page != null) {
					page.updateWizardButtons();
				}
			}
		}
	};
	
	private void fillData() {
		if(connection != null) {
			if(connection.getName() != null) {
				txtName.setText(connection.getName());
			}
			if(connection.getUsername() != null) {
				txtLogin.setText(connection.getUsername());
			}
			if(connection.getPassword() != null) {
				txtPassword.setText(connection.getPassword());
			}
			if(connection.getHost() != null) {
				txtHost.setText(connection.getHost());
			}
			if(connection.getPort() != null) {
				txtPort.setText(connection.getPort());
			}
			if(connection.getKeyspace() != null) {
				txtKeyspace.setText(connection.getKeyspace());
			}
		}
	}

	public CassandraConnection getConnection() {
		if(connection == null) {
			connection = new CassandraConnection();
		}
		connection.setName(txtName.getText());
		connection.setUsername(txtLogin.getText());
		connection.setPassword(txtPassword.getText());
		connection.setHost(txtHost.getText());
		connection.setPort(txtPort.getText());
		connection.setKeyspace(txtKeyspace.getText());
		return connection;
	}

	public void fillData(CassandraConnection dataBaseConnection) {
		connection = dataBaseConnection;
		fillData();
	}

}
