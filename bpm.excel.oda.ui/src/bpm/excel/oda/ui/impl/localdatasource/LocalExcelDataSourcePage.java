package bpm.excel.oda.ui.impl.localdatasource;

import java.io.File;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;

import bpm.excel.oda.runtime.impl.Connection;

public class LocalExcelDataSourcePage extends DataSourceWizardPage{
	private LocalConnectionComposite composite;
	private Properties initialProperties;
	
	
	public LocalExcelDataSourcePage(String pageName) {
		super(pageName);
	}
	@Override
	public Properties collectCustomProperties() {
		
		return composite.getFileInfo();
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		composite = new LocalConnectionComposite();
		composite.createContent(parent);
		fill();
	}

	@Override
	public void setInitialProperties(Properties dataSourceProps) {
		initialProperties = dataSourceProps;
		
		
	}
	private void fill(){
		
		if (initialProperties != null){
			composite.fill(initialProperties);
		}
		
		
	}
	
	@Override
	protected void testConnection() {
		String file = composite.getFileInfo().getProperty(Connection.FILE_NAME);
		if(file != null) {
			File f = new File(file);
			if(f.canRead()) {
				MessageDialog.openInformation(getShell(), "Success", "Connection successful");
				return;
			}
		}
		MessageDialog.openError(getShell(), "Error", "Can't open the file : " + file);
 	}
	
}
