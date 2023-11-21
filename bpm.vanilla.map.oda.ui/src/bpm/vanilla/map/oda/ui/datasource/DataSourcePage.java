package bpm.vanilla.map.oda.ui.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.vanilla.map.oda.runtime.impl.Connection;

public class DataSourcePage extends DataSourceWizardPage {

	
	private Text vanillaRuntimeUrl;
	
	private Properties initialProperties;
	
	public DataSourcePage(String pageName) {
		super(pageName);
		
	}

	@Override
	protected void testConnection() {
		
		super.testConnection();
	}
	@Override
	public Properties collectCustomProperties() {
		Properties prop = new Properties();
		prop.setProperty(Connection.VANILLA_RUNTIME_URL, vanillaRuntimeUrl.getText());
		return prop;
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		//main.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Vanilla Runtime Url");
		
		vanillaRuntimeUrl = new Text(main, SWT.BORDER);
		vanillaRuntimeUrl.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		fill();
	}

	@Override
	public void setInitialProperties(Properties dataSourceProps) {
		initialProperties = dataSourceProps;
		
	}

	
	private void fill(){
		String vUrl = "http://localhost:7171/VanillaRuntime";
		if (initialProperties != null){
			if (initialProperties.getProperty(Connection.VANILLA_RUNTIME_URL) != null){
				vUrl = initialProperties.getProperty(Connection.VANILLA_RUNTIME_URL);
			}
		}
		
		vanillaRuntimeUrl.setText(vUrl);
	}
}
