package bpm.fwr.oda.ui.impl.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.fwr.oda.runtime.impl.Connection;

public class FwrDataSourcePage extends DataSourceWizardPage{

	private ConnectionComposite connectionComposite;
	private Properties prop;
	
	public FwrDataSourcePage(String pageName){
		super(pageName);
	}
	
	@Override
	public Properties collectCustomProperties() {
		prop = new Properties();
		try {
			if (connectionComposite != null){
				prop.put(Connection.USER, connectionComposite.getLogin());
				prop.put(Connection.PASSWORD, connectionComposite.getPassword());
				prop.put(Connection.URL, connectionComposite.getUrl());
				prop.put(Connection.REPOSITORY_URL, connectionComposite.getSelectedRepositoryUrl());
				prop.put(Connection.GROUP_ID, connectionComposite.getSelectedGroupId()+"");
				prop.put(Connection.GROUP_NAME, connectionComposite.getSelectedGroupName());
				prop.put(Connection.FWREPORT_ID, connectionComposite.getFWReportId()+"");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return prop;
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		connectionComposite = new ConnectionComposite(parent, SWT.NONE, this);
	}

	@Override
	public void setInitialProperties(Properties dataSourceProps) {
		
		prop = dataSourceProps;
	}
	
	public void setPingButton(boolean value){
		setPingButtonEnabled(value);
	}

}
