package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.List;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.tools.SecurityHelper;
import bpm.studio.jdbc.management.model.ListDriver;

public class CompositeConnectionSql extends Composite {

	private int begin = 0;
	private int step = 1000;
	
	private class Secu{
		public String groupName;
		Boolean visible;
	}
	
	private Combo driver;
	private Text host, dataBase, port, login, password;//, schema;
	private Text dataSourceName, name;
	private Text fullUrl;
	private Button useFullUrl;
	private boolean containName;
	
	private Button ok, cancel;
	
	private Viewer v;
	private CheckboxTreeViewer tableGroups;
	
	private SQLConnection sock = null; 
	private SQLDataSource datasource = null;
	
	public CompositeConnectionSql(Composite parent, int style, boolean containName) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.containName = containName;
		buildContent(this);
	}
	
	public CompositeConnectionSql(Composite parent, int style, boolean containName, SQLConnection sock) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.sock = sock;
		this.containName = containName;
		buildContent(this);
		fillData();

	}
	
	public CompositeConnectionSql(Composite parent, int style, boolean containName, Viewer v,  SQLConnection sock, SQLDataSource datasource) {
		super(parent, style);
		this.sock = sock;
		this.containName = containName;
		this.v = v;
		this.datasource = datasource;
		this.setLayout(new GridLayout());
		
		TabFolder folder = new TabFolder(this, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		TabItem general = new TabItem(folder, SWT.NONE);
		general.setControl(buildContent(folder));
		general.setText(Messages.CompositeConnectionSql_11);
		
		TabItem security = new TabItem(folder, SWT.NONE);
		security.setControl(createSecurity(folder));
		security.setText(Messages.CompositeConnectionSql_16);
		
		
		fillData();
		createButtonBar();
		
		

	}
	
	
	private void fillGroups(){
		for(Secu s : (List<Secu>)tableGroups.getInput()){

			if (datasource.isGranted(sock, s.groupName)){
				s.visible = true;
				
				tableGroups.setChecked(s, true);
			}
			else{
				s.visible = false;
				tableGroups.setChecked(s, false);
			}
		}
		tableGroups.refresh();		
//		List<Secu> ss =  (List<Secu>)tableGroups.getInput();
//		for(Secu s : ss){
//			tableGroups.setChecked(s, s.visible);
//
//		}
//		Object[] c = tableGroups.getCheckedElements();
		
	}
	private Control createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData());
		
		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setToolTipText("Check All"); //$NON-NLS-1$
		checkAll.setImage(Activator.getDefault().getImageRegistry().get("check")); //$NON-NLS-1$
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				tableGroups.setAllChecked(true);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					datasource.securizeConnection(sock.getName(), ((Secu)o).groupName, true);
					SecurityHelper.grantGlobal(Activator.getDefault().getCurrentModel(), ((Secu)o).groupName, true);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		ToolItem uncheckAll = new ToolItem(toolbar, SWT.PUSH);
		uncheckAll.setToolTipText("Uncheck All"); //$NON-NLS-1$
		uncheckAll.setImage(Activator.getDefault().getImageRegistry().get("uncheck")); //$NON-NLS-1$
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableGroups.setAllChecked(false);
				for(Object o : ((IStructuredContentProvider)tableGroups.getContentProvider()).getElements(tableGroups.getInput())){
					datasource.securizeConnection(sock.getName(), ((Secu)o).groupName, false);
					SecurityHelper.grantGlobal(Activator.getDefault().getCurrentModel(), ((Secu)o).groupName, false);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		return toolbar;
	}
	private Control createSecurity(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l0 = new Label(parent, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(Messages.CompositeBusinessPackage_2); //$NON-NLS-1$

		
		createToolbar(parent);
		
		tableGroups = new CheckboxTreeViewer(parent, SWT.V_SCROLL  | SWT.VIRTUAL);
		tableGroups.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		tableGroups.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Secu> list = (List<Secu>) inputElement;
				return list.toArray(new Secu[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
			}

			public Object[] getChildren(Object parentElement) {
				return null;
			}

			public Object getParent(Object element) {
				
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}
			
		});
		
		tableGroups.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Secu)element).groupName;
			}
			
		});
		
		//XXX
		tableGroups.addCheckStateListener(new ICheckStateListener() {		
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				//If the group is checked, give access to everything
				Secu s = (Secu) event.getElement();
				if(event.getChecked()) {
					SecurityHelper.grantGlobal(Activator.getDefault().getCurrentModel(), s.groupName, true);
				}
				else {
					SecurityHelper.grantGlobal(Activator.getDefault().getCurrentModel(), s.groupName, false);
				}
				
			}
		});
		
		List<Secu> ss = new ArrayList<Secu>();
		
		for(String s : GroupHelper.getGroups(begin, step)){
			Secu c = new Secu();
			c.groupName = s;
			c.visible = false;
			ss.add(c);
		}
		tableGroups.setInput(ss);
		
		
		
		return parent;
	}
	
	private void createButtonBar(){
		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, true));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		ok = new Button(c, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.setText(Messages.CompositeConnectionSql_0); //$NON-NLS-1$
		ok.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					sock.setDriverName(driver.getText());
					sock.setHost(host.getText());
					sock.setPortNumber(port.getText());
					sock.setDataBaseName(dataBase.getText());
					sock.setUsername(login.getText());
					sock.setPassword(password.getText());
					sock.setUseFullUrl(useFullUrl.getSelection());
					sock.setFullUrl(fullUrl.getText());
					sock.setName(name.getText());
					setConnection();
					
					if (datasource != null){
						((SQLDataSource)datasource).getConnections().set(0, sock);
					}
					
					if (v != null){
						v.refresh();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		});
		
		cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText(Messages.CompositeConnectionSql_1); //$NON-NLS-1$
		cancel.addSelectionListener(new SelectionAdapter(){
			@Override

			public void widgetSelected(SelectionEvent e) {
				fillData();
			}

		});
	}
	
	private Composite buildContent(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		if (containName){
			Label l0 = new Label(main, SWT.NONE);
			l0.setLayoutData(new GridData());
			l0.setText(Messages.CompositeConnectionSql_2); //$NON-NLS-1$
			
			dataSourceName = new Text(main, SWT.BORDER);
			dataSourceName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			dataSourceName.addModifyListener(new ModifyListener(){

				public void modifyText(ModifyEvent e) {
					notifyListeners(SWT.SELECTED, new Event());
					
				}
			});
			
		}
		
		Label _l = new Label(main, SWT.NONE);
		_l.setLayoutData(new GridData());
		_l.setText(Messages.CompositeConnectionSql_3); //$NON-NLS-1$
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.setText("Default");
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeConnectionSql_4); //$NON-NLS-1$
		
		driver = new Combo(main, SWT.READ_ONLY);
		driver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		driver.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
			
		});
		
		try{
			for(String s : ListDriver.getInstance(bpm.studio.jdbc.management.config.IConstants.getJdbcDriverXmlFile()).getDriversName()){
				driver.add(s);
			}
		}catch(Exception e){
			MessageDialog.openError(getShell(), Messages.CompositeConnectionSql_5, e.getMessage()); //$NON-NLS-1$
			Activator.getLogger().error(e.getMessage(), e);
		}
		
		useFullUrl = new Button(main, SWT.CHECK);
		useFullUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		useFullUrl.setText(Messages.CompositeConnectionSql_21);
		useFullUrl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fullUrl.setEnabled(useFullUrl.getSelection());
				host.setEnabled(!useFullUrl.getSelection());
				port.setEnabled(!useFullUrl.getSelection());
				dataBase.setEnabled(!useFullUrl.getSelection());
				notifyListeners(SWT.SELECTED, new Event());
			}
		});
		
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.CompositeConnectionSql_22); 
		
		fullUrl = new Text(main, SWT.BORDER);
		fullUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fullUrl.setEnabled(false);
		fullUrl.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
		});
		
		l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.CompositeConnectionSql_6); //$NON-NLS-1$
		
		host = new Text(main, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		host.addModifyListener(new ModifyListener(){


			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
			
		});
		
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setLayoutData(new GridData());
		l3.setText(Messages.CompositeConnectionSql_7); //$NON-NLS-1$
		
		port = new Text(main, SWT.BORDER);
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
		
		Label l4 = new Label(main, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.CompositeConnectionSql_8); //$NON-NLS-1$
		
		dataBase = new Text(main, SWT.BORDER);
		dataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataBase.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
		});
		
		
		Label l5 = new Label(main, SWT.NONE);
		l5.setLayoutData(new GridData());
		l5.setText(Messages.CompositeConnectionSql_9); //$NON-NLS-1$
		
		login = new Text(main, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		login.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
				
			}
		});
		
		
		Label l6 = new Label(main, SWT.NONE);
		l6.setLayoutData(new GridData());
		l6.setText(Messages.CompositeConnectionSql_10); //$NON-NLS-1$
		
		password = new Text(main, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button test = new Button(main, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		test.setText(Messages.CompositeConnectionSql_12); //$NON-NLS-1$
		test.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SQLConnection sock = null;
					if (useFullUrl.getSelection()){
						sock = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText(), login.getText(), password.getText(), /*schema.getText()*/ null, useFullUrl.getSelection(), fullUrl.getText());
					}
					else{
						sock = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText(), login.getText(), password.getText(), /*schema.getText()*/ null);
					}
					
					sock.setName(name.getText());
					sock.connect();
					MessageDialog.openInformation(getShell(), Messages.CompositeConnectionSql_13, Messages.CompositeConnectionSql_14); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.CompositeConnectionSql_15, e1.getMessage()); //$NON-NLS-1$
				}
			}
			
		});
		
		return main;
	}

	private void fillData(){
		if (tableGroups != null){
			fillGroups();
		}
		
		if (sock != null){
			name.setText(sock.getName());
			host.setText(sock.getHost());
			dataBase.setText(sock.getDataBaseName());
			port.setText(sock.getPortNumber());
			login.setText(sock.getUsername());
			password.setText(sock.getPassword());
			
			useFullUrl.setSelection(sock.isUseFullUrl());
			fullUrl.setEnabled(sock.isUseFullUrl());
			host.setEnabled(!useFullUrl.getSelection());
			port.setEnabled(!useFullUrl.getSelection());
			dataBase.setEnabled(!useFullUrl.getSelection());
			if (sock.getFullUrl() != null){
				fullUrl.setText(sock.getFullUrl());
			}
			else{
				fullUrl.setText(""); //$NON-NLS-1$
			}
			
			driver.setText(sock.getDriverName());
		}
	}
	public void setConnection(SQLConnection sock) throws Exception{
		this.sock = sock;
		dataSourceName.setText(sock.getName());
		fillData();
	}
	public void setConnection() throws Exception{
		if (useFullUrl.getSelection()){
			if (!login.getText().equals("")) { //$NON-NLS-1$
				sock = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText(), login.getText(), password.getText(), null, useFullUrl.getSelection(), fullUrl.getText());	
				sock.setName(name.getText());
				sock.connect();
			}
			else {
				sock = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText(), useFullUrl.getSelection(), fullUrl.getText());
				sock.setName(name.getText());
				sock.connect();
			}
		}
		else{
			if (!login.getText().equals("")) { //$NON-NLS-1$
				sock = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText(), login.getText(), password.getText(), /*schema.getText()*/ null);	
				sock.setName(name.getText());
				sock.connect();
			}
			else {
				sock = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), port.getText(), dataBase.getText());
				sock.setName(name.getText());
				sock.connect();
			}
		}
		

			
		if (datasource != null){
			List<Secu> groups = (List<Secu>)tableGroups.getInput();
			
			for(Secu s : groups){
				if (tableGroups.getChecked(s)){
					s.visible = true;
					datasource.securizeConnection(sock.getName(), s.groupName, true);
				}
				else{
					s.visible = false;
					datasource.securizeConnection(sock.getName(), s.groupName, false);
				}
			}			
			
		}
	}
	
	public SQLConnection getConnection(){
		return sock;
	}
	
	public String getConnectionName(){
		return dataSourceName.getText();
	}
	
	/**
	 * return true if all required fields are set
	 * to be used inside Wizard
	 * @return
	 */
	public boolean isFilled(){
		if (dataSourceName != null){
			if (useFullUrl.getSelection()){
				if (dataSourceName.getText().equals("") || driver.getText().equals("") || fullUrl.getText().equals("")  /*|| port.getText().equals("")*/){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
					return false;
				}
			}
			else{
				if (dataSourceName.getText().equals("") || driver.getText().equals("") || host.getText().equals("") || dataBase.getText().equals("") /*|| port.getText().equals("")*/){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
					return false;
				}
			}
			
		}
		else{
			if (useFullUrl.getSelection()){
				if (driver.getText().equals("") || fullUrl.getText().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					return false;
				}
			}
			else{
				if (driver.getText().equals("") || host.getText().equals("") || dataBase.getText().equals("")){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					return false;
				}
			}
			
		}
		
		return true;
	}
	
	
}
