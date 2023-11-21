package bpm.nosql.oda.ui.impl.datasource;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.nosql.oda.runtime.impl.Connection;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class ConnectionComposite extends Composite {
	
	private Text login, password, host, port, txtDb, txtConfigFile;
	private Button passProtected, btnConfigFile;
	private boolean pass;
	private ConnectionComposite conComposite;
	private String odaDatasourceId;
	
	public ConnectionComposite(Composite parent, int style, NOSQLDataSourcePage nosqlDataSourcePage, String odaDatasourceId) {
		super(parent, style);
		
		this.odaDatasourceId = odaDatasourceId;
			if(this.odaDatasourceId.contains("hbase")){
				this.setLayout(new GridLayout(3, false));
				buildContentHbase();
			}else{
				this.setLayout(new GridLayout(2, false));
				buildContent();
			}
		
		fillData(null);
	}

	private void buildContent() {
		Label lblHost = new Label(this, SWT.NONE);
		lblHost.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblHost.setText("Host");
		
		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblPort = new Label(this, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblPort.setText("Port");
		
		port = new Text(this, SWT.BORDER);
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		final Label yourDb = new Label(this, SWT.NONE);
		yourDb.setText("Database");
		yourDb.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		txtDb = new Text(this, SWT.BORDER);
		txtDb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		passProtected = new Button(this, SWT.CHECK);
		passProtected.setText("Password Protected");
		passProtected.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		passProtected.addSelectionListener(new SelectionAdapter()  {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(passProtected.getSelection()){
					
					login.setEnabled(true);
					password.setEnabled(true);
					pass = true;
				}
				else{
					login.setEnabled(false);
					password.setEnabled(false);
					pass = false;
				}
			}
			
		
		});
		
		final Label lblLogin = new Label(this, SWT.NONE);
		lblLogin.setText("Login");
		lblLogin.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		login.setEnabled(false);
		
		final Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		password.setEnabled(false);

	}

	private void buildContentHbase() {
		
		
		Label lblConfigFile = new Label(this, SWT.NONE);
		lblConfigFile.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		lblConfigFile.setText("Host");
		
		txtConfigFile = new Text(this, SWT.BORDER);
		txtConfigFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		btnConfigFile = new Button(this, SWT.PUSH);
		btnConfigFile.setText("...");
		btnConfigFile.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		btnConfigFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dial = new FileDialog(getShell());
				String configFile = dial.open();
				try {
					configFile = configFile.replaceAll("\\\\", "/");
					txtConfigFile.setText(configFile);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				
			}
		});
	}
	public String getLogin() {
		return login.getText();
	}

	public String getPassword() {
		return password.getText();
	}

	public String getPort() {
		return port.getText();
	}

	public String getConfigFile(){
		return txtConfigFile.getText();
	}
	
	public String getTxtDb() {
		return txtDb.getText();
	}

	public String getHost() {
		return host.getText();
	}

	public boolean isPass() {
		return pass;
	}
	
	public String getOdaDatasourceId() {
		return odaDatasourceId;
	}

	public ConnectionComposite getConComposite() {
		return conComposite;
	}

	public void fillData(Properties properties){
		
		if (properties != null){
			if(!getOdaDatasourceId().contains("hbase")){
				host.setText(properties.getProperty(Connection.HOST));
				port.setText(properties.getProperty(Connection.PORT));
					
				if(properties.getProperty(Connection.USER) != null){
						login.setText(properties.getProperty(Connection.USER));
					}
				
				if(properties.getProperty(Connection.PASSWORD) != null){
					password.setText(properties.getProperty(Connection.PASSWORD));
				}
				
				txtDb.setText(properties.getProperty(Connection.DATABASE));
			}else{
				txtConfigFile.setText(properties.getProperty(Connection.CONFIGURATIONFILE));
			}

		}
	}
	
	public Properties getProperties(){
		
		Properties prop = new Properties();

			
		String databaseType;
		
			if(odaDatasourceId.contains("cassandra")){
				databaseType = "cassandra";
				prop.put(Connection.USER, login.getText());
				
					if(this.getPassword().matches("[0-9a-f]{32}")){
						prop.put(Connection.PASSWORD, password.getText());
					}else {
						prop.put(Connection.PASSWORD, MD5Helper.encode(password.getText()));
					}	

				prop.put(Connection.DATABASE, txtDb.getText());
				prop.put(Connection.PORT, port.getText());
				prop.put(Connection.HOST, host.getText());
			
					if(pass){
						prop.put(Connection.ISPASSREQUIRED, "true");	
					}else {
						prop.put(Connection.ISPASSREQUIRED, "false" 	);
					}	
			}
			else if(odaDatasourceId.contains("hbase")){
				databaseType ="hbase";
				prop.put(Connection.CONFIGURATIONFILE, txtConfigFile.getText());
				
			}
			else{
				databaseType ="mongodb";
				prop.put(Connection.USER, login.getText());
				
					if(this.getPassword().matches("[0-9a-f]{32}")){
						prop.put(Connection.PASSWORD, password.getText());
					}else {
						prop.put(Connection.PASSWORD, MD5Helper.encode(password.getText()));
					}	
	
				prop.put(Connection.DATABASE, txtDb.getText());
				prop.put(Connection.PORT, port.getText());
				prop.put(Connection.HOST, host.getText());
			
					if(pass){
						prop.put(Connection.ISPASSREQUIRED, "true");	
					}else {
						prop.put(Connection.ISPASSREQUIRED, "false" 	);
					}

			}
			
		prop.put(Connection.DATABASETYPE, databaseType);
		
		return prop;
		
	}
}
