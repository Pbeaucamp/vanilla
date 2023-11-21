package bpm.birep.admin.client.composites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.views.datasources.ViewDataSource;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.repository.DataSource;

public class CompositeDatasource extends Composite {

	private DataSource dataSource;
	private Text name, user, password, url;
	private Combo driverName;

	public CompositeDatasource(Composite parent, int style) {
		super(parent, style);
		
		createContent(parent);
	}
	
	public CompositeDatasource(Composite parent, int style, DataSource dataSource) {
		super(parent, style);
		this.dataSource = dataSource;
		
		createContent(parent);
		fillDatas();
	}
	
	public void createContent(Composite parent){
		this.setLayout(new GridLayout(2, false));
		
		Composite container = new Composite(this, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(container, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeDatasource_0);
		
		name = new Text(container, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l1 = new Label(container, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.CompositeDatasource_1);
		
		url = new Text(container, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Label l2 = new Label(container, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CompositeDatasource_2);
		
		driverName = new Combo(container, SWT.BORDER);
		driverName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		List<String> c = new ArrayList<String>();
		
		
		try{
			for(DriverInfo inf : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()){
				c.add(inf.getName());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		Collections.sort(c);
		driverName.setItems(c.toArray(new String[c.size()]));
		
		
		Label l3 = new Label(container, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeDatasource_3);
		
		user = new Text(container, SWT.BORDER);
		user.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l4 = new Label(container, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.CompositeDatasource_4);
		
		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Button test = new Button(container, SWT.PUSH);
		test.setText(Messages.CompositeDatasource_5);
		test.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		test.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Throwable error = null;
					
					SQLConnection con = new SQLConnection();
					con.setFullUrl(url.getText());
					
					con.setUseFullUrl(true);
					con.setUsername(user.getText());
					con.setDriverName(driverName.getText());
					con.setUsername(user.getText());
					con.setPassword(password.getText());
					
					try{
						con.connect();
						MessageDialog.openInformation(getShell(), Messages.CompositeDatasource_6, Messages.CompositeDatasource_7);
					}catch(Throwable ex){
						ex.printStackTrace();
						error = ex;
						MessageDialog.openError(getShell(), Messages.CompositeDatasource_8, Messages.CompositeDatasource_9 + error.getMessage());
					}

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Composite buttonBar = new Composite(container, SWT.NONE);
		buttonBar.setLayout(new GridLayout(2, true));
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		Button apply = new Button(buttonBar, SWT.PUSH);
		apply.setLayoutData(new GridData(GridData.FILL_BOTH));
		apply.setText(Messages.CompositeDatasource_10);
		apply.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				setDatas();
				try {
					Activator.getDefault().getRepositoryApi().getImpactDetectionService().update(dataSource);
					
					ViewDataSource v = (ViewDataSource)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewDataSource.ID);
					if (v != null){
						v.createModel();
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		Button cancel = new Button(buttonBar, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL_BOTH));
		cancel.setText(Messages.CompositeDatasource_11);
		cancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				fillDatas();
			}
		});

		

	}
	
	public void fillDatas(){
//		if (dataSource == null){
//			return;
//		}
//		
//		if (dataSource.getName() != null){
//			name.setText(dataSource.getName());
//		}
//		else{
//			name.setText(""); //$NON-NLS-1$
//		}
//		
//		if (dataSource.getUser() != null){
//			user.setText(dataSource.getUser());
//		}
//		else{
//			user.setText(""); //$NON-NLS-1$
//		}
//		
//		
//		if (dataSource.getPassword() != null){
//			password.setText(dataSource.getPassword());
//		}
//		else{
//			password.setText(""); //$NON-NLS-1$
//		}
//		
//		
//		if (dataSource.getUrl() != null){
//			url.setText(dataSource.getUrl());
//		}
//		else{
//			url.setText(""); //$NON-NLS-1$
//		}
//		
//		try{
//			boolean found = false;
//			for(DriverInfo inf : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()){
//				
//				if (dataSource.getDriverName() != null && dataSource.getDriverName().equals(inf.getName())){
//					driverName.setText(inf.getName());
//					found = true;
//					break;
//				}
//			}
//			
//			if (! found){
//				for(DriverInfo inf : ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo()){
//					
//					if (dataSource.getUrl() != null && dataSource.getUrl().startsWith(inf.getUrlPrefix())){
//						
//						
//						boolean proceed = MessageDialog.openQuestion(getShell(), Messages.CompositeDatasource_16, "No driver named " + dataSource.getDriverName() + " registered in EntrepriseServices. But we found driver named " + inf.getName() + " using the same Jdbc Url Prefix. Do you want to use it? (you will need to erform an apply to validate the changes on the server)?");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//						if (proceed){
//							driverName.setText(inf.getName());
//							break;
//						}
//						else{
//							continue;
//						}
//					}
//				}
//			}
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
	}
	
	public void setDatas(){
//		if (dataSource == null){
//			dataSource =  new DataSource();
//		}
//		
//		dataSource.setDriverName(driverName.getText());
//		
//		dataSource.setName(name.getText());
//		dataSource.setPassword(password.getText());
//		dataSource.setUrl(url.getText());
//		dataSource.setUser(user.getText());
	}
	
	public void setDataSource(DataSource ds){
		this.dataSource = ds;
		fillDatas();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

}
