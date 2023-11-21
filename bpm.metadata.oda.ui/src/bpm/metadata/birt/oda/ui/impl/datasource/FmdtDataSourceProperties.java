package bpm.metadata.birt.oda.ui.impl.datasource;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;



public class FmdtDataSourceProperties extends DefaultDataSourcePropertyPage {

	private ConnectionComposite connComposite;
	
	public FmdtDataSourceProperties() {}

	@Override
	protected void createAndInitCustomControl(Composite parent, Properties profileProps) {
		connComposite = new ConnectionComposite(parent, SWT.NONE, null, getOdaDataSourceId());
		connComposite.fillData(profileProps);
	}

	@Override
	protected Properties collectProperties() {
		Properties profileProps = new Properties();
		if (connComposite != null){
			
			try {
				connComposite.setProperties(profileProps);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Missing Connection information ", e.getMessage());
			}
		}

		return profileProps;
	}

}
