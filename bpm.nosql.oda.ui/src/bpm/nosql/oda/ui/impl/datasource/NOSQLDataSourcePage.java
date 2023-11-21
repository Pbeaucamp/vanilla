package bpm.nosql.oda.ui.impl.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.nosql.oda.runtime.impl.CassandraConnection;
import bpm.nosql.oda.runtime.impl.Connection;
import bpm.nosql.oda.runtime.impl.HbaseConnection;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class NOSQLDataSourcePage extends DataSourceWizardPage {

	private ConnectionComposite connectionComposite;
	private Properties prop;
	private boolean ConnextionIsOpen;

	public NOSQLDataSourcePage(String pageName) {
		super(pageName);
	}

	@Override
	public Properties collectCustomProperties() {
		prop = new Properties();
			if(connectionComposite != null){
				if(!getOdaDataSourceId().contains("hbase")){
					prop.put(Connection.HOST, connectionComposite.getProperties().getProperty(Connection.HOST));
					prop.put(Connection.PORT, connectionComposite.getProperties().getProperty(Connection.PORT));		
					prop.put(Connection.DATABASE, connectionComposite.getProperties().getProperty(Connection.DATABASE));
					
						if(connectionComposite.getProperties().getProperty(Connection.PASSWORD).matches("[0-9a-f]{32}")){
							prop.put(Connection.PASSWORD, connectionComposite.getProperties().getProperty(Connection.PASSWORD));
						}else {
							prop.put(Connection.PASSWORD, MD5Helper.encode(connectionComposite.getProperties().getProperty(Connection.PASSWORD)));
						}	
					
						if(connectionComposite.getProperties().getProperty(Connection.ISPASSREQUIRED).equals("true")){
							prop.put(Connection.ISPASSREQUIRED, "true");
						}else if(connectionComposite.getProperties().getProperty(Connection.ISPASSREQUIRED).equals("false")){
							prop.put(Connection.ISPASSREQUIRED, "false");
						}
				}else{
					prop.put(Connection.CONFIGURATIONFILE, connectionComposite.getProperties().getProperty(Connection.CONFIGURATIONFILE));
				}

		}
		return prop;
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		connectionComposite = new ConnectionComposite(parent, SWT.NONE, this, getOdaDataSourceId());
		connectionComposite.fillData(prop);
	}

	@Override
	public void setInitialProperties(Properties dataSourceProps) {

		prop = dataSourceProps;

	}

	@Override
	protected void testConnection() {

		if (getOdaDataSourceId().contains("bpm.nosql.oda.runtime.cassandra")) {
			CassandraConnection con = new CassandraConnection();

			try {
				con.open(connectionComposite.getProperties());
				ConnextionIsOpen = con.isOpen();
			} catch (OdaException e) {
				
				e.printStackTrace();
			}
			
			
		}else if(getOdaDataSourceId().contains("bpm.nosql.oda.runtime.hbase"))
		{
			HbaseConnection con = new HbaseConnection();
			
			try {
				con.open(connectionComposite.getProperties());
				ConnextionIsOpen = con.isOpen();
			} catch (OdaException e) {
				
				e.printStackTrace();
			}

		}else{

			Connection con = new Connection();
			try {
				con.open(connectionComposite.getProperties());
				ConnextionIsOpen = con.isOpen();
			} catch (OdaException e) {
				e.printStackTrace();
			}

		}

		if (ConnextionIsOpen) {
			MessageDialog.openInformation(getShell(), "Success", "Connection successful");
		}
		else {
			MessageDialog.openError(getShell(), "Error", "Connection refused");
		}

	}
}
