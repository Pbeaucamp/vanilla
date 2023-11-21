package bpm.nosql.oda.ui.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.ui.designsession.DesignSessionUtil;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DrillDatasetPage extends DataSetWizardPage {

	private Text txtQuery;
	
	public DrillDatasetPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createPageCustomControl(Composite arg0) {
		Composite parent = new Composite(arg0, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblUrl = new Label(parent, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblUrl.setText("Query");
		
		txtQuery = new Text(parent, SWT.BORDER | SWT.MULTI);
		txtQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setControl(parent);
		
		DataSetDesign dataSetDesign = getInitializationDesign();

		if (dataSetDesign == null || dataSetDesign.getQueryText() == null
				|| dataSetDesign.getQueryText().trim().equals("")) { //$NON-NLS-1$
		}
		else {
			txtQuery.setText(dataSetDesign.getQueryText());
		}
	}

	@Override
	protected DataSetDesign collectDataSetDesign(DataSetDesign dataSetDesign) {
		String queryText = txtQuery.getText();
		dataSetDesign.setQueryText(queryText);

		if(dataSetDesign.getPublicProperties() == null) {
			try {
				dataSetDesign.setPublicProperties(DesignSessionUtil.createDataSetPublicProperties(dataSetDesign.getOdaExtensionDataSourceId(), dataSetDesign.getOdaExtensionDataSetId(), new Properties()));
			} catch(OdaException e) {
				e.printStackTrace();
			}
		}
		return dataSetDesign;
	}
	
	

}
