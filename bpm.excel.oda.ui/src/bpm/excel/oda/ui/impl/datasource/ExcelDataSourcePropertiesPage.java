package bpm.excel.oda.ui.impl.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.excel.oda.runtime.impl.Connection;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class ExcelDataSourcePropertiesPage extends DefaultDataSourcePropertyPage {
	private ConnectionComposite connComposite;
	
	public ExcelDataSourcePropertiesPage() {
		
	}

	@Override
	protected void createAndInitCustomControl(Composite parent,
			Properties profileProps) {
		
		connComposite = new ConnectionComposite(parent, SWT.NONE, null);
		connComposite.fillData(profileProps, true);
		
	}
	
	@Override
	protected Properties collectProperties() {
		Properties profileProps = new Properties();
		if (connComposite != null){

			profileProps.put(Connection.USER, connComposite.getUsername());
			if (connComposite.getPassword().matches("[0-9a-f]{32}")) {
				profileProps.put(Connection.PASSWORD, connComposite.getPassword());
			}
			else {
				profileProps.put(Connection.PASSWORD, MD5Helper.encode(connComposite.getPassword()));
			}
			profileProps.put(Connection.REPOSITORY_ID, connComposite.getRepositoryId() + "");
			profileProps.put(Connection.GROUP_ID, connComposite.getGroupId() + "");
		
			if (connComposite.getDirectoryItem() != null){
				profileProps.put(Connection.DIRECTORY_ITEM_ID, connComposite.getDirectoryItem().getId() + "");	 //$NON-NLS-1$
			}
		
		}
		
		
		return profileProps;
	}
}
