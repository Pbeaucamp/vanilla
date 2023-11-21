package bpm.google.table.oda.driver.ui.impl.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.google.table.oda.driver.runtime.impl.Connection;


public class GoogleTableDataSourceProperties extends DefaultDataSourcePropertyPage {

	private ConnectionComposite connectionComposite;
	
	public GoogleTableDataSourceProperties(){
		
	}

	

	@Override
	protected void createAndInitCustomControl(Composite parent,
			Properties profileProps) {
		
		connectionComposite = new ConnectionComposite(parent, SWT.NONE, null);
		connectionComposite.initWidgetWithPropertie(profileProps);
	}
	
	
	
	@Override
	public Properties collectCustomProperties(Properties profileProps) {
		
		
		Properties prop = new Properties();
		
		if (connectionComposite != null){
			
			//Test If user has selected a table
			if(connectionComposite.existSelection()){
				
				prop.put(Connection.P_USER, connectionComposite.getUser());
				prop.put(Connection.P_PASS, connectionComposite.getPass());
				prop.put(Connection.P_ID_TABLE, connectionComposite.getIdTable());
				prop.put(Connection.P_COLUMN_COUNT, connectionComposite.getColumnsCount());
				prop.put(Connection.P_COLUMN_NAME, connectionComposite.getColumnsName());
				prop.put(Connection.P_COLUMN_TYPE, connectionComposite.getColumnsType());
				prop.put(Connection.P_ROW_COUNT, connectionComposite.getTableRowCount());
			}
		}
		
		return prop;
		
	}
	
}
