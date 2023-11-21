package bpm.gateway.ui.resource.composite.connection;



import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.exception.JdbcException;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.i18n.Messages;
import bpm.studio.jdbc.management.model.ListDriver;


public class CompositeConnectionSql extends AbstractCompositeConnection {

	/*
	 * model
	 */
//	private DataBaseConnection socket;
	
	/*
	 * widgets
	 */
	private Combo driver;
	private Text host, dataBase, port, login, password;
	private Text name;
	private Button test;
	private Label errorMessage;
	
	private Button useCompleteUrl;
	private Text url;
	
//	private List<Listener> listeners = new ArrayList<Listener>();
	
	
	
	public CompositeConnectionSql(Composite parent, int style) {
		super(parent, style);
		buildContent();
		init();
		
		socket = new DataBaseConnection();
		
		setBackground(parent);
	}
	
	public CompositeConnectionSql(Composite parent, int style, DataBaseConnection connection) {
		super(parent, style);
		buildContent();
		init();
		
		socket = connection;

		fillDatas();
		
		setBackground(parent);
	}
	
	private void setBackground(Composite parent) {

		this.setBackground(parent.getBackground());
		for(Control c : this.getChildren()){
			c.setBackground(getBackground());
		}

		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);

		name.setBackground(white);
		driver.setBackground(white);
		url.setBackground(white);
		host.setBackground(white);
		port.setBackground(white);
		dataBase.setBackground(white);
		login.setBackground(white);
		password.setBackground(white);
	}
		
	private void buildContent(){
		this.setLayout(new GridLayout(2, false));
				
		Label _l = new Label(this, SWT.NONE);
		_l.setLayoutData(new GridData());
		_l.setText(Messages.CompositeConnectionSql_0);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
				updateTestButton();
			}
			
		});
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeConnectionSql_1);
		
		driver = new Combo(this, SWT.READ_ONLY);
		driver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		driver.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				if  (driver.getText().equals(ListDriver.MS_ACCESS) || driver.getText().equals(ListDriver.MS_XLS)){
					host.setEnabled(false);
					port.setEnabled(false);
					
				}
				else{
					host.setEnabled(true);
					port.setEnabled(true);
				}
				notifyListeners(SWT.Selection, new Event());
				updateTestButton();
				
			}
			
		});
		
		useCompleteUrl = new Button(this, SWT.CHECK);
		useCompleteUrl.setText(Messages.CompositeConnectionSql_2);
		useCompleteUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		useCompleteUrl.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (useCompleteUrl.getSelection()){
					url.setEnabled(true);
					host.setEnabled(false);
					port.setEnabled(false);
					dataBase.setEnabled(false);
				}
				else{
					url.setEnabled(false);
					host.setEnabled(true);
					port.setEnabled(true);
					dataBase.setEnabled(true);
				}
			}
			
		});
		
		
		Label _l1 = new Label(this, SWT.NONE);
		_l1.setLayoutData(new GridData());
		_l1.setText(Messages.CompositeConnectionSql_3);
		
		url = new Text(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		url.addModifyListener(new ModifyListener(){


			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				updateTestButton();
			}
			
		});
		url.setEnabled(false);
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.CompositeConnectionSql_4); 
		
		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.addModifyListener(new ModifyListener(){


			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				updateTestButton();
			}
			
		});
		
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(Messages.CompositeConnectionSql_5); 
		
		port = new Text(this, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		port.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				try{
					port.getText();
					notifyListeners(SWT.Selection, new Event());
					updateTestButton();
				}catch(NumberFormatException ex){
					
				}
				
				
			}
		});
		
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.CompositeConnectionSql_6);
		
		dataBase = new Text(this, SWT.BORDER);
		dataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataBase.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				updateTestButton();
			}
		});
		
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(Messages.CompositeConnectionSql_7); 
		
		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				updateTestButton();
			}
		});
		
		
		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(Messages.CompositeConnectionSql_8); 
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		password.addModifyListener(new ModifyListener(){


			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				updateTestButton();
			}
			
		});
		
		test = new Button(this, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		test.setText(Messages.CompositeConnectionSql_9); 
		test.setEnabled(false);
		test.addSelectionListener(new SelectionAdapter(){

			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					testConnection();
					errorMessage.setForeground(null);
					errorMessage.setText(Messages.CompositeConnectionSql_10);
				}catch(Exception ex){
					ex.printStackTrace();
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.CompositeConnectionSql_11, ex));
					MessageDialog.openError(getShell(), Messages.CompositeConnectionSql_12, ex.getMessage());
					errorMessage.setText(Messages.CompositeConnectionSql_13);
					
					ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
					errorMessage.setForeground(reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY));

				}
				
			}
			
		});
		
		errorMessage = new Label(this, SWT.NONE);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		errorMessage.setText(Messages.CompositeConnectionSql_15);
		
		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		
		errorMessage.setForeground(reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY));
	}
	
	/**
	 * return true if all required fields are set
	 * to be used inside Wizard
	 * @return
	 */
	public boolean isFilled(){
		if  (driver.getText().equals(ListDriver.MS_ACCESS) || driver.getText().equals(ListDriver.MS_XLS)){
//			if (name.getText().equals("")  ||  dataBase.getText().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
//				return false;
//			}
//			else{
				return true;
//			}
		}
		
		
		if (useCompleteUrl.getSelection()){
			if (name.getText().equals("") || driver.getText().equals("") || url.getText().equals("") || login.getText().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				return false;
			}
		}
		else{
			if (name.getText().equals("") || driver.getText().equals("") || host.getText().equals("") || dataBase.getText().equals("") /*|| port.getText().equals("") */|| login.getText().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				return false;
			}
		}
		
				
		return true;
	}


	
	private void init(){
		try {
			ListDriver l = JdbcConnectionProvider.getListDriver();			
			driver.setItems(l.getDriversName().toArray(new String[l.getDriversName().size()]));
			
		} catch (JdbcException e) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			MessageDialog.openError(getShell(), Messages.CompositeConnectionSql_14, e.getMessage());
			
		}
	}
	
	private void updateTestButton(){
		test.setEnabled(isFilled());
	}
	
	
	private void fillDatas(){
		if (socket == null){
			return;
		}
		
		name.setText(socket.getName());
		
		if (socket.isUseFullUrl()){
			url.setText(socket.getFullUrl());
			useCompleteUrl.setSelection(true);
			url.setEnabled(true);
		}
		else{
			useCompleteUrl.setSelection(false);
			host.setText(socket.getHost() + ""); //$NON-NLS-1$
			port.setText(socket.getPort() + ""); //$NON-NLS-1$
			dataBase.setText(socket.getDataBaseName() + ""); //$NON-NLS-1$
			url.setEnabled(false);
		}
		
		login.setText(socket.getLogin());
		password.setText(socket.getPassword());
		
		
		for(int i = 0; i < driver.getItemCount(); i++){
			if (driver.getItem(i).equals(socket.getDriverName())){
				driver.select(i);
				break;
			}
		}
	}
	
	/**
	 * define the DataBaseConnection showed
	 * @param sock
	 */
	public void setInput(DataBaseConnection sock){
		this.socket = sock;
		fillDatas();
	}
	
	
	/**
	 * set the DataBaseConnection with the field content
	 */
	public void performChanges(){
		socket.setName(name.getText());
		socket.setLogin(login.getText());
		socket.setPassword(password.getText());
		
		if (!useCompleteUrl.getSelection()){
			socket.setUseFullUrl(false);
			socket.setHost(host.getText());
			socket.setPort(port.getText());
			socket.setDataBaseName(dataBase.getText());
		}
		else{
			socket.setFullUrl(url.getText());
			socket.setUseFullUrl(true);
		}
		
		socket.setDriverName(driver.getText());
	}
	
	public void testConnection() throws Exception{
		//we make a copy to dont change the values before being sure
		//that the parameters are good
		DataBaseConnection socket = new DataBaseConnection();
		socket.setName(name.getText());
		socket.setLogin(login.getText());
		socket.setPassword(password.getText());
		socket.setDriverName(driver.getText());

		if (!useCompleteUrl.getSelection()){
			socket.setUseFullUrl(false);
			socket.setHost(host.getText());
			socket.setPort(port.getText());
			socket.setDataBaseName(dataBase.getText());
		}
		else{
			socket.setFullUrl(url.getText());
			socket.setUseFullUrl(true);
		}
		socket.connect(null);
		socket.disconnect();

	}
	
	/**
	 * 
	 * @return the current DataBaseConnection
	 * 
	 */
	public DataBaseConnection getConnection(){
		return socket;
	}

	public void setError(String string, Color col) {
		errorMessage.setText(string);
		errorMessage.setForeground(col);
	}
}

