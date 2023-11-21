package bpm.gateway.ui.resource.composite.connection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import bpm.gateway.core.server.database.nosql.mongodb.*;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.resource.server.wizard.MongoDbConnectionPage;

public class MongoDbConnectionComposite extends Composite {

	private Text txtHost, txtPort, txtLogin, txtDb, txtName;
	private Button btnTestConnection, passProtected;
	private Text txtPassword;	
	private MongoDbConnection connection;
	private MongoDbConnectionPage page;
	private Boolean pass;
	
	public Boolean getPass() {
		return pass;
	}

	public void setPass(Boolean pass) {
		this.pass = pass;
	}

	Listener listener = new Listener(){
		public void handleEvent(Event event) {
			if (event.widget == txtHost || event.widget == txtPort){
				if(page != null) {
					page.updateWizardButtons();
					

				}
			}
		}
	};
	
	public MongoDbConnectionComposite(Composite parent, int style, MongoDbConnectionPage page) {
		super(parent, style);
		this.page = page;
		createPageContent(parent);
		
		setBackground(parent);
	}
	
	public MongoDbConnectionComposite(Composite parent, int style, MongoDbConnectionPage page, MongoDbConnection connection) {
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
		lblName.setText(Messages.MongoDbConnectionComposite_0);
		
		txtName = new Text(this, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtName.addListener(SWT.SELECTED, listener);
		txtName.addListener(SWT.Selection, listener);
		
		
		Label lblHost = new Label(this, SWT.NONE);
		lblHost.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblHost.setText(Messages.MongoDbConnectionComposite_1);
		
		txtHost = new Text(this, SWT.BORDER);
		txtHost.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtHost.addListener(SWT.SELECTED, listener);
		txtHost.addListener(SWT.Selection, listener);
		
		Label lblPort = new Label(this, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblPort.setText(Messages.MongoDbConnectionComposite_2);
		
		txtPort = new Text(this, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtPort.addListener(SWT.SELECTED, listener);
		txtPort.addListener(SWT.Selection, listener);
		
		final Label lblLogin = new Label(this, SWT.NONE);
		lblLogin.setText(Messages.MongoDbConnectionComposite_3);
		lblLogin.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblLogin.setVisible(false);
		
		txtLogin = new Text(this, SWT.BORDER);
		txtLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtLogin.addListener(SWT.SELECTED, listener);
		txtLogin.addListener(SWT.Selection, listener);
		txtLogin.setVisible(false);
		
		final Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText(Messages.MongoDbConnectionComposite_4);
		lblPassword.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblPassword.setVisible(false);
		
		txtPassword = new Text(this, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtPassword.addListener(SWT.SELECTED, listener);
		txtPassword.addListener(SWT.Selection, listener);
		txtPassword.setVisible(false);
		
		final Label yourDb = new Label(this, SWT.NONE);
		yourDb.setText(Messages.MongoDbConnectionComposite_5);
		yourDb.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		yourDb.setVisible(false);
		
		txtDb = new Text(this, SWT.BORDER);
		txtDb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtDb.addListener(SWT.SELECTED, listener);
		txtDb.addListener(SWT.Selection, listener);;
		txtDb.setVisible(false);
		
		passProtected = new Button(this, SWT.CHECK);
		passProtected.setText(Messages.MongoDbConnection_1);
		passProtected.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		passProtected.addSelectionListener(new SelectionAdapter()  {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(passProtected.getSelection()){
					lblLogin.setVisible(true);
					txtLogin.setVisible(true);
					lblPassword.setVisible(true);
					txtPassword.setVisible(true);
					yourDb.setVisible(true);
					txtDb.setVisible(true);
					pass = true;
				}
				else{
					lblLogin.setVisible(false);
					txtLogin.setVisible(false);
					lblPassword.setVisible(false);
					txtPassword.setVisible(false);
					yourDb.setVisible(false);
					txtDb.setVisible(false);
					pass = false;
				}
			}
			
		
		});
		
		btnTestConnection = new Button(this, SWT.PUSH);
		btnTestConnection.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		btnTestConnection.setText(Messages.MongoDbConnectionComposite_6);
		btnTestConnection.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				connection = getConnection();
			
				try {
					if(MongoDbHelper.testConnection(connection,pass)){
						MessageDialog.openInformation(getShell(), 
								Messages.MongoDbConnectionComposite_7, Messages.MongoDbConnectionComposite_8);
						
					}
					else {
						MessageDialog.openInformation(getShell(), 
								Messages.MongoDbConnectionComposite_9, Messages.MongoDbConnectionComposite_10);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), 
							Messages.MongoDbConnectionComposite_11, Messages.MongoDbConnectionComposite_12 + e1.getMessage());
				}
			}
		});
		
		fillData();
		
	}
	
	public MongoDbConnection getConnection(){
		if(connection == null) {
			connection = new MongoDbConnection();
		}
		connection.setHost(txtHost.getText());
		connection.setPort(txtPort.getText());
		connection.setPass(pass != null ? pass : false);
		connection.setLogin(txtLogin.getText());
		connection.setPassword(txtPassword.getText());
		connection.setYourDb(txtDb.getText());
		connection.setName(txtName.getText());
		return connection;
		
	}
	
	public void fillData(MongoDbConnection dataBaseConnection) {
		connection = dataBaseConnection;
		fillData();
	}
	
	private void fillData() {
		if(connection != null) {
			if(connection.getName() != null) {
				txtName.setText(connection.getName());
			}
			if(connection.getHost() != null){
				txtHost.setText(connection.getHost());
			}
			if(connection.getPort() != null){
				txtPort.setText(connection.getPort());
			}
			
		}
	}
	
	private void setBackground(Composite parent) {

		this.setBackground(parent.getBackground());
		for(Control c : this.getChildren()){
			c.setBackground(getBackground());
		}

		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

		txtHost.setBackground(white);
		txtPort.setBackground(white);
		txtDb.setBackground(white);
		txtLogin.setBackground(white);
		txtPassword.setBackground(white);
		txtName.setBackground(white);
	}
}
