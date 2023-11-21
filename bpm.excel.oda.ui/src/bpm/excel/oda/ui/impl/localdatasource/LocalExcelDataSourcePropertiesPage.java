package bpm.excel.oda.ui.impl.localdatasource;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.swt.widgets.Composite;

public class LocalExcelDataSourcePropertiesPage extends DefaultDataSourcePropertyPage{
	private LocalConnectionComposite composite;
	
	public LocalExcelDataSourcePropertiesPage() {
		
	}

	@Override
	protected void createAndInitCustomControl(Composite parent,
			Properties profileProps) {
		
		composite = new LocalConnectionComposite();
		composite.createContent(parent);
		composite.fill(profileProps);
		
	}
	
	@Override
	protected Properties collectProperties() {
		return composite.getFileInfo();
	}	
	
	
}
