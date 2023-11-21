package bpm.nosql.oda.ui.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.nosql.oda.runtime.impl.DrillConnection;

public class DrillDatasourcePage extends DataSourceWizardPage {

	private Text txtUrl;
	private Text txtUser;
	private Text txtPassword;
	
	public DrillDatasourcePage(String pageName) {
		super(pageName);
	}

	@Override
	public Properties collectCustomProperties() {
		Properties props = new Properties();
		
		props.put(DrillConnection.URL, txtUrl.getText());
		props.put(DrillConnection.USER, txtUser.getText());
		props.put(DrillConnection.PASSWORD, txtPassword.getText());
		
		return props;
	}

	@Override
	public void createPageCustomControl(Composite arg0) {
		arg0.setLayout(new GridLayout());
		arg0.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite parent = new Composite(arg0, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblUrl = new Label(parent, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUrl.setText("Jdbc Url");
		
		txtUrl = new Text(parent, SWT.BORDER);
		txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblUser = new Label(parent, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUser.setText("Login");
		
		txtUser = new Text(parent, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Label lblPass = new Label(parent, SWT.NONE);
		lblPass.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblPass.setText("Password");
		
		txtPassword = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
	}

	@Override
	public void setInitialProperties(Properties arg0) {
		txtUrl.setText(arg0.getProperty(DrillConnection.URL));
		txtUser.setText(arg0.getProperty(DrillConnection.USER));
		txtPassword.setText(arg0.getProperty(DrillConnection.PASSWORD));
	}

}
