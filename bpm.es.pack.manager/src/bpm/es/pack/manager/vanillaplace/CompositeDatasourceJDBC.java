package bpm.es.pack.manager.vanillaplace;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.es.pack.manager.I18N.Messages;
import bpm.metadata.layer.physical.sql.FactorySQLConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.workplace.api.datasource.replacement.BIGDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIRTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIWDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FASDDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FAVDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FDDicoDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FMDTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FWRDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceJDBC;
import bpm.vanilla.workplace.core.model.PlaceImportItem;

public class CompositeDatasourceJDBC extends CompositePlaceMapping {

	private ImportItemDatasource item;

	private String driverDs;
	
	private String userDs;
	private String passwordDs;
	private String portDs;
	private String hostDs;
	private String dbNameDs;
	
	private String fmUserDs;
	private String fmPasswordDs;
	
	private Combo driver;
	private Text dataBase, login, password, host, port, fmUser, fmPassword;
	private Label lblDriver, lblLogin, lblPassword, lblDatabase, lblHost, lblPort, lblFmUser, lblFmPassword;
	private Button test;
	
	/*
	 * The type correspond to a version of datasource:
	 *  1 is for a datasource with only databaseUrl, login and password
	 *  2 is for a datasource with databaseName, login, password, host and port
	 */
	private int type;
	
	public static HashMap<String, String> driverName;
	public static HashMap<String, String> driverPrefix;
	
	static{
		driverName = new HashMap<String, String>();
		driverPrefix = new HashMap<String, String>();
		Document doc;
		try {
			doc = DocumentHelper.parseText(IOUtils.toString(new FileInputStream("resources/driverjdbc.xml"), "UTF-8")); //$NON-NLS-1$ //$NON-NLS-2$
			Element root = doc.getRootElement();
			
			for(Object e : root.elements("driver")){ //$NON-NLS-1$
				driverName.put(((Element)e).attributeValue("name"), ((Element)e).attributeValue("className")); //$NON-NLS-1$ //$NON-NLS-2$
				driverPrefix.put(((Element)e).attributeValue("name"), ((Element)e).attributeValue("prefix")); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (DocumentException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public CompositeDatasourceJDBC(Composite parent, int style, ImportItemDatasource item) {
		super(parent, style);
		this.item = item;
		this.driverDs = item.getDriver();
		this.userDs = item.getUser();
		this.passwordDs = item.getPassword();
		this.portDs = item.getPort();
		this.hostDs = item.getHost();
		this.dbNameDs = item.getDbName();
		this.fmUserDs = item.getFmUser();
		this.fmPasswordDs = item.getFmPassword();
		
		buildContent();
		fillDatas();
	}

	protected void buildContent(){
		this.setLayout(new GridLayout(2, false));

		lblDriver = new Label(this, SWT.NONE);
		lblDriver.setLayoutData(new GridData());
		lblDriver.setText(bpm.es.pack.manager.I18N.Messages.CompositeDatasourceJDBC_3); 
		
		driver = new Combo(this, SWT.READ_ONLY);
		driver.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		driver.setItems(driverName.keySet().toArray(new String[driverName.size()]));
			
		
		lblLogin = new Label(this, SWT.NONE);
		lblLogin.setLayoutData(new GridData());
		lblLogin.setText(Messages.CompositeConnectionSql_11);
		
		login = new Text(this, SWT.BORDER);
		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		
		lblPassword = new Label(this, SWT.NONE);
		lblPassword.setLayoutData(new GridData());
		lblPassword.setText(Messages.CompositeConnectionSql_12);
		
		password = new Text(this, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		lblDatabase = new Label(this, SWT.NONE);
		lblDatabase.setLayoutData(new GridData());
		lblDatabase.setText(Messages.CompositeConnectionSql_10); 
		
		dataBase = new Text(this, SWT.BORDER);
		dataBase.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		lblHost = new Label(this, SWT.NONE);
		lblHost.setLayoutData(new GridData());
		lblHost.setText(Messages.CompositeConnectionSql_8);
		
		host = new Text(this, SWT.BORDER);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			
		
		lblPort = new Label(this, SWT.NONE);
		lblPort.setLayoutData(new GridData());
		lblPort.setText(Messages.CompositeConnectionSql_9); 
		
		port = new Text(this, SWT.BORDER);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		lblFmUser = new Label(this, SWT.NONE);
		lblFmUser.setLayoutData(new GridData());
		lblFmUser.setText(Messages.CompositeDatasourceJDBC_0);
		
		fmUser = new Text(this, SWT.BORDER);
		fmUser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		lblFmPassword = new Label(this, SWT.NONE);
		lblFmPassword.setLayoutData(new GridData());
		lblFmPassword.setText(Messages.CompositeDatasourceJDBC_1);
		
		fmPassword = new Text(this, SWT.BORDER | SWT.PASSWORD);
		fmPassword.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		test = new Button(this, SWT.PUSH);
		test.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		test.setText(Messages.CompositeDatasourceJDBC_2);
		test.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				testConnection();
			}
		});
		
		super.buildContent();
	}

	public IDatasource buildDatasource(IDatasource ds) {
		ModelDatasourceJDBC datasource = (ModelDatasourceJDBC)ds;
		if(type == 0){
			String drClass = driverName.get(driver.getText());
			String driverN = driver.getText();
			String po = port.getText();
			String ho = host.getText();
			String dbName = dataBase.getText();
			String user = login.getText();
			String pass = password.getText();
			
			fillCurrentDatasource(driverN, drClass, po, ho, dbName, user, pass);
			
			if(datasource.isUseFullUrl()){
				datasource.setDriver(driverN);
				datasource.setDriverClass(drClass);
				datasource.setPort(po);
				datasource.setHost(ho);
				datasource.setDbName(dbName);
				datasource.setFullUrl(buildFullUrl(driverN, po, ho, dbName));
			}
			else {
				datasource.setDriver(driverN);
				datasource.setDriverClass(drClass);
				datasource.setPort(po);
				datasource.setHost(ho);
				datasource.setDbName(dbName);
			}
			datasource.setUser(user);
			datasource.setPassword(pass);
			
			if(!fmUser.isDisposed() && !fmPassword.isDisposed()){
				String fmUs = fmUser.getText();
				String fmPass = fmPassword.getText();
				
				datasource.setFmUser(fmUs);
				datasource.setFmPassword(fmPass);
				
				fillCurrentDatasourceFm(fmUs, fmPass);
			}
		}
		else if(type == 1){
			String fmUs = fmUser.getText();
			String fmPass = fmPassword.getText();
			
			datasource.setFmUser(fmUs);
			datasource.setFmPassword(fmPass);
			
			fillCurrentDatasourceFm(fmUs, fmPass);
		}
		
		return datasource;
	}

	public void fillDatas() {
		if (driverDs != null){
			if (driverDs.contains(".")){ //$NON-NLS-1$
				int i =0;
				for(String s : driverName.values()){
					if (s.equals(driverDs)){
						driver.select(i);
						break;
					}
					i++;
				}
			}
			else{
				int i =0;
				for(String s : driverName.keySet()){
					if (s.equals(driverDs)){
						driver.select(i);
						break;
					}
					i++;
				}
			}
		}
		
		if (userDs != null){
			login.setText(userDs);
		}
		
		if (passwordDs != null){
			password.setText(passwordDs);
		}
		
		if(dbNameDs != null){
			dataBase.setText(dbNameDs);
		}
		
		if (hostDs != null || portDs != null){
			host.setText(hostDs);
			port.setText(portDs);

			type = 0;
			
			if(fmUserDs != null){
				fmUser.setText(fmUserDs);
				fmPassword.setText(fmPasswordDs);
			}
			else {
				lblFmUser.dispose();
				fmUser.dispose();
				
				lblFmPassword.dispose();
				fmPassword.dispose();
			}
		}
		else if(fmUserDs != null){
			fmUser.setText(fmUserDs);
			fmPassword.setText(fmPasswordDs);
			
			driver.dispose();
			dataBase.dispose();
			login.dispose();
			password.dispose();
			host.dispose();
			port.dispose();
			
			lblDriver.dispose();
			lblLogin.dispose();
			lblPassword.dispose();
			lblDatabase.dispose();
			lblHost.dispose();
			lblPort.dispose();
			
			type = 1;
		}
	}
	
	private void fillCurrentDatasource(String dr, String drClass, String po, String ho, String dbName, 
			String user, String pass){
		this.item.setDriver(dr);
		this.item.setDriverClass(drClass);
		this.item.setPort(po);
		this.item.setHost(ho);
		this.item.setDbName(dbName);
		this.item.setUser(user);
		this.item.setPassword(pass);
	}

	private void fillCurrentDatasourceFm(String fmUser, String fmPassword){
		this.item.setFmUser(fmUser);
		this.item.setFmPassword(fmPassword);
	}
	
	@Override
	public void performChanges() throws Exception{
		IDatasourceReplacement remplacement = null;
		
		for(PlaceImportItem it : item.getItems().keySet()){
			int type = it.getItem().getType();
			Integer subtype = it.getItem().getSubtype();
			switch(type){
			case IRepositoryApi.FASD_TYPE:
				remplacement = new FASDDatasourceReplacement();
				break;
			case IRepositoryApi.FD_TYPE:
				remplacement = new FDDicoDatasourceReplacement();
				break;
			case IRepositoryApi.FD_DICO_TYPE:
				remplacement = new FDDicoDatasourceReplacement();
				break;
			case IRepositoryApi.FMDT_TYPE:
				remplacement = new FMDTDatasourceReplacement();
				break;
			case IRepositoryApi.GTW_TYPE:
				remplacement = new BIGDatasourceReplacement();
				break;
			case IRepositoryApi.GED_TYPE:	
				break;
			case IRepositoryApi.BIW_TYPE:
				remplacement = new BIWDatasourceReplacement();
				break;
			case IRepositoryApi.EXTERNAL_DOCUMENT:
				break;
			case IRepositoryApi.FWR_TYPE:
				remplacement = new FWRDatasourceReplacement();
				break;
			case IRepositoryApi.FAV_TYPE:
				remplacement = new FAVDatasourceReplacement();
				break;
			case IRepositoryApi.CUST_TYPE:
				if(subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE){
					remplacement = new BIRTDatasourceReplacement();
					break;
				}
			}
			
			if(remplacement != null){
				for(IDatasource ds : item.getDatasource(it)){
					IDatasource datasource = buildDatasource(ds);
					
					String xml = it.getXml();
					it.setXml(remplacement.replaceElement(xml, datasource));
				}
			}
		}
	}
	
	private String buildFullUrl(String driver, String port, String host, String dbName){
		String driverPref = driverPrefix.get(driver);
		return driverPref + host + ":" + port + "/" + dbName; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testConnection() {
		try {
			SQLConnection sock = null;
			sock = FactorySQLConnection.getInstance().createConnection(driver.getText(), host.getText(), 
					port.getText(), dataBase.getText(), login.getText(), password.getText(), null, false, ""); //$NON-NLS-1$
			sock.test();
			MessageDialog.openInformation(getShell(), "Informations", "Connection successfull"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e1.getMessage()); //$NON-NLS-1$
		}
	}
}
