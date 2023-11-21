package bpm.metadata.birt.oda.ui.impl.datasource;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


public class FmdtDataSourcePage extends DataSourceWizardPage {
	

	private ConnectionComposite connectionComposite;
	private Properties prop;	
	
	
	public FmdtDataSourcePage(String pageName) {
		super(pageName);
	}

	public FmdtDataSourcePage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public Properties collectCustomProperties() {
		prop = new Properties();
		
		if (connectionComposite != null){
			try {
				connectionComposite.setProperties(prop);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Missing Connection information ", e.getMessage());
			}
		}

		return prop;
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		
		connectionComposite = new ConnectionComposite(parent, SWT.NONE, this, getOdaDataSourceId());
		
		connectionComposite.addListener(SWT.SELECTED, new Listener(){
			public void handleEvent(Event event) {
				setPingButtonEnabled((Boolean)event.data);
			}
		});
		
		connectionComposite.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					getContainer().updateButtons();
				} catch(Exception e) {
				}
			}
		});
		
		connectionComposite.fillData(prop);
	}

	@Override
	public void setInitialProperties(Properties dataSourceProps) {
		prop = dataSourceProps;
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		setPingButtonEnabled(false);
	}

	public void setPingButton(boolean value){
		setPingButtonEnabled(value);
	}

	@Override
	public boolean isPageComplete() {
		if (connectionComposite == null){
			return false;
		}
		return connectionComposite.isFilled();
	}

	@Override
	public IWizardPage getNextPage() {
		return getWizard().getNextPage(this);
	}
}
