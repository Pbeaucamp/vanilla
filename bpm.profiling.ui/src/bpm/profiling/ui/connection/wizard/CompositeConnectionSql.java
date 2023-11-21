package bpm.profiling.ui.connection.wizard;



import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.profiling.runtime.core.Connection;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.ListDriver;



public class CompositeConnectionSql extends Composite {

	private Combo driver;
	private Text host, dataBase, port, login, password, schema;
	private Text name;
	private Button ok, cancel;
	

	private Connection connection;
	 
	
	public CompositeConnectionSql(Composite parent, int style) {
		super(parent, style);
		buildContent();
	}
	
	
	
	
	
	private void createButtonBar(){
		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		ok = new Button(c, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.setText("Apply");
		ok.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				//TODO
			}
			
		});
		
		cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter(){
			@Override

			public void widgetSelected(SelectionEvent e) {
				fillData();
			}

		});
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout(2, false));
				
		Label _l = new Label(this, SWT.NONE);
		_l.setLayoutData(new GridData());
		_l.setText("name"); 
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Driver"); 
		
		driver = new Combo(this, SWT.READ_ONLY);
		driver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		driver.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
			
		});
		
		try{
			for(String s : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversName()){
				driver.add(s);
			}
		}catch(Throwable e){
			e.printStackTrace();
			
		}
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText("host"); 
		
		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.addModifyListener(new ModifyListener(){


			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
			
		});
		
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText("port"); 
		
		port = new Text(this, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		port.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				try{
					Integer.parseInt(port.getText());
					notifyListeners(SWT.SELECTED, new Event());
				}catch(NumberFormatException ex){
					
				}
				
				
			}
		});
		
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText("database"); //$NON-NLS-1$
		
		dataBase = new Text(this, SWT.BORDER);
		dataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataBase.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
		});
		
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText("login"); 
		
		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
		});
		
		
		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText("password"); 
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData());
		l7.setText("schema"); 
		
		schema = new Text(this, SWT.BORDER | SWT.PASSWORD);
		schema.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button test = new Button(this, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		test.setText("TestConnection"); //$NON-NLS-1$
		test.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				setConnection();
				try{
					connection.connect();
					MessageDialog.openInformation(getShell(), "Connection test", "Succeed");
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Connection test failed", ex.getMessage());
				}
				
			}
			
		});
	}

	private void fillData(){
		//TODO
	}

	public void setConnection(){
		if (connection == null){
			connection = new Connection();
		}
		
		connection.setName(name.getText());
		connection.setDatabaseName(dataBase.getText());
		connection.setDriverName(driver.getText());
		connection.setHost(host.getText());
		connection.setLogin(login.getText());
		connection.setPassword(password.getText());
		connection.setPort(port.getText());
		
	}
	
	
	
	
	
	/**
	 * return true if all required fields are set
	 * to be used inside Wizard
	 * @return
	 */
	public boolean isFilled(){
		if (name.getText().equals("") || driver.getText().equals("") || host.getText().equals("") || dataBase.getText().equals("") || port.getText().equals("") || login.getText().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			return false;
		}
		
		
		return true;
	}
	
	public Connection getConnection(){
		setConnection();
		return connection;
	}
}
