package bpm.datasource.vanilla.oda.ui.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.platform.core.repository.DataSource;

public class VanillaDatasourceOdaPage extends DataSourceWizardPage {
	private ConnectionComposite connectionComposite;
	private Properties prop;

	public VanillaDatasourceOdaPage(String pageName) {
		super(pageName);
	}

	public VanillaDatasourceOdaPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public Properties collectCustomProperties() {
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

	@Override
	public void createPageCustomControl(Composite parent) {
		connectionComposite = new ConnectionComposite(parent, SWT.NONE, this);
	}

	@Override
	public void setInitialProperties(Properties dataSourceProps) {
		prop = dataSourceProps;

	}

	public void setPingButton(boolean value) {
		setPingButtonEnabled(value);
	}

	@Override
	public boolean isPageComplete() {
		return connectionComposite.isPageComplete();
	}

	public void updateButtons() {
		getContainer().updateButtons();
	}

}
