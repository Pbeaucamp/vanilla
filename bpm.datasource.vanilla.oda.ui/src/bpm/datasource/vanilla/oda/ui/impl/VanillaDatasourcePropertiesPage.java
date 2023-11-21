package bpm.datasource.vanilla.oda.ui.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.platform.core.repository.DataSource;

public class VanillaDatasourcePropertiesPage extends DefaultDataSourcePropertyPage {
	
	private ConnectionComposite connectionComposite;

	@Override
	protected void createAndInitCustomControl(Composite parent, Properties profileProps) {
		connectionComposite = new ConnectionComposite(parent, SWT.NONE, null);
	}

	@Override
	protected Properties collectProperties() {
		Properties props = new Properties();
		
		if(connectionComposite.getDatasource() != null) {
			Properties privateProperties = connectionComposite.getDatasource().getDatasourcePrivateProperties();
			Properties publicProperties = connectionComposite.getDatasource().getDatasourcePublicProperties();
			
			privateProperties.put(DataSource.CURRENT_ODA_EXTENSION, "bpm.metadata.birt.oda.runtime");
			privateProperties.put(DataSource.CURRENT_ODA_EXTENSION_DATASOURCE, "bpm.metadata.birt.oda.runtime");
			
			props.putAll(privateProperties);
			props.putAll(publicProperties);
		}
		
		return props;
	}

}
