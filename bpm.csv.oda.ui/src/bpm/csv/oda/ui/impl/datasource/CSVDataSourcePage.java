package bpm.csv.oda.ui.impl.datasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import bpm.csv.oda.runtime.impl.Connection;
import bpm.vanilla.platform.core.utils.MD5Helper;

public class CSVDataSourcePage extends DataSourceWizardPage {
	private ConnectionComposite connectionComposite;
	private Properties prop;

	public CSVDataSourcePage(String pageName) {
		super(pageName);
	}

	public CSVDataSourcePage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public Properties collectCustomProperties() {
		prop = new Properties();

		if(connectionComposite != null) {
			prop.put(Connection.USER, connectionComposite.getUsername());
			prop.put(Connection.PASSWORD, MD5Helper.encode(connectionComposite.getPassword()));
			prop.put(Connection.REPOSITORY_ID, connectionComposite.getRepositoryId() + "");
			prop.put(Connection.GROUP_ID, connectionComposite.getGroupId() + "");

			prop.put(Connection.CSV_SEPARATOR, connectionComposite.getSeparator());

			prop.put(Connection.DATE_FORMAT, connectionComposite.getDateFormat());

			if(connectionComposite.getDirectoryItem() != null) {
				prop.put(Connection.DIRECTORY_ITEM_ID, connectionComposite.getDirectoryItem().getId() + ""); //$NON-NLS-1$
			}
		}

		return prop;
	}

	@Override
	public void createPageCustomControl(Composite parent) {
		connectionComposite = new ConnectionComposite(parent, SWT.NONE, this);

		connectionComposite.addListener(SWT.SELECTED, new Listener() {

			public void handleEvent(Event event) {
				setPingButtonEnabled((Boolean) event.data);

			}

		});

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
