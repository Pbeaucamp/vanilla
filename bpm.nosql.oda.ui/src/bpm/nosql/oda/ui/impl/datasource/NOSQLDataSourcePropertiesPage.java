package bpm.nosql.oda.ui.impl.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.nosql.oda.runtime.impl.Connection;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class NOSQLDataSourcePropertiesPage extends DefaultDataSourcePropertyPage {
	private ConnectionComposite connComposite;

	@Override
	protected void createAndInitCustomControl(Composite parent, Properties profileProps) {
		connComposite = new ConnectionComposite(parent, SWT.NONE, null, getOdaDataSourceId());
		connComposite.fillData(profileProps);
	}

	@Override
	protected Properties collectProperties() {
		Properties profileProps = new Properties();

		if (connComposite != null) {
			if(!getOdaDataSourceId().contains("hbase")){
				profileProps.put(Connection.USER, connComposite.getLogin());
				if (connComposite.getPassword().matches("[0-9a-f]{32}")) {
					profileProps.put(Connection.PASSWORD, connComposite.getPassword());
				}
				else {
					profileProps.put(Connection.PASSWORD, MD5Helper.encode(connComposite.getPassword()));
				}

				profileProps.put(Connection.DATABASE, connComposite.getTxtDb());
				profileProps.put(Connection.PORT, connComposite.getPort());
				profileProps.put(Connection.HOST, connComposite.getHost());
			}else{
				profileProps.put(Connection.CONFIGURATIONFILE, connComposite.getConfigFile());	
			}


		}
		return profileProps;
	}

}
