package bpm.google.table.oda.driver.ui.impl.datasource;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.google.table.oda.driver.runtime.impl.Connection;

public class GoogleTableDataSourcePage extends DataSourceWizardPage{
	
	private ConnectionComposite connectionCompo;
	private Properties prop;	
	
	
	public GoogleTableDataSourcePage(String pageName) {
		super(pageName);
		
	}

	@Override
	public void createPageCustomControl(Composite compo) {
		
		setPageComplete(false);
		setPingButtonVisible(false);
		setMessage("Enter your G-Mail adress and password. Then, select a table.");
		
		connectionCompo = new ConnectionComposite(compo, SWT.NONE, this);
		connectionCompo.initWidgetWithPropertie(prop);
	}

	
	@Override
	public Properties collectCustomProperties() {
		
		prop = new Properties();
		
		if (connectionCompo != null){
			
			//Test If user has selected a table
			if(connectionCompo.existSelection()){
				
				prop.put(Connection.P_USER, connectionCompo.getUser());
				prop.put(Connection.P_PASS, connectionCompo.getPass());
				prop.put(Connection.P_ID_TABLE, connectionCompo.getIdTable());
				prop.put(Connection.P_COLUMN_COUNT, connectionCompo.getColumnsCount());
				prop.put(Connection.P_COLUMN_NAME, connectionCompo.getColumnsName());
				prop.put(Connection.P_COLUMN_TYPE, connectionCompo.getColumnsType());
				prop.put(Connection.P_ROW_COUNT, connectionCompo.getTableRowCount());
				
			}
			
		}
		return prop;
	}



	@Override
	public void setInitialProperties(Properties dataSourceProp) {
		
		prop = dataSourceProp;
		
	}

}
