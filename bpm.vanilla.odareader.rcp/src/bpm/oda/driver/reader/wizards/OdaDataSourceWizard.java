package bpm.oda.driver.reader.wizards;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.NewDataSourceWizard;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizardPage;
import org.eclipse.jface.wizard.IWizardPage;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.model.datasource.DataSource;
import bpm.oda.driver.reader.wizards.pages.DataSourceTypePage;

public class OdaDataSourceWizard extends NewDataSourceWizard {

	private DataSource dataSource;
	private DataSourceTypePage typePage;

	public OdaDataSourceWizard() {
		this.setForcePreviousAndNextButtons(true);
		this.initOdaDesignSession(null, DesignFactory.eINSTANCE.createDesignSessionRequest());
	}

	public OdaDataSourceWizard(DataSource dataSource) throws OdaException {
		this.dataSource = dataSource;

		this.initOdaDesignSession(null, DesignFactory.eINSTANCE.createDesignSessionRequest());
		this.setForcePreviousAndNextButtons(true);
	}

	public void addPages() {

		if (dataSource != null) {
			typePage = new DataSourceTypePage("DataSource Type Page", dataSource);
		}
		else {
			typePage = new DataSourceTypePage("DataSource Type Page");
		}

		typePage.setTitle("DataSource Type Page");
		typePage.setDescription("Select the DataSource type");

		addPage(typePage);

		this.mProfilePage = new NewConnectionProfileWizardPage();
		addPage(this.mProfilePage);

		if (dataSource != null) {
			mProfilePage.setProfileName(dataSource.getName());
		}

	}

	public IWizardPage getNextPage(IWizardPage page) {
		if (page == mProfilePage) {
			try {
				initialize(typePage.getExtensionManifest().getExtensionID(), null, "");

				super.addCustomPages();
				// setInOdaDesignSession(true);
				if (this.getCustomStartingPage() instanceof DataSourceWizardPage && dataSource != null) {

					((DataSourceWizardPage) this.getCustomStartingPage()).setInitialProperties(dataSource.getProperties());
				}

				return this.getCustomStartingPage();
			} catch (OdaException e) {
				e.printStackTrace();
			}
		}

		return super.getNextPage(page);
	}

	public boolean performFinish() {

		boolean b = super.performFinish();

		if (b) {
			DataSourceDesign ds = getDataSourceDesign();

			java.util.Properties pp = new java.util.Properties();

			if (ds.getPrivateProperties() != null) {
				for (Object o : ds.getPrivateProperties().getProperties()) {

					String pName = ((Property) o).getName();
					String pValue = ((Property) o).getValue();
					if (pValue != null) {
						pp.setProperty(pName, pValue);
					}
				}
			}

			java.util.Properties pu = new java.util.Properties();
			if (ds.getPublicProperties() != null) {
				for (Object o : ds.getPublicProperties().getProperties()) {
					String pName = ((Property) o).getName();
					String pValue = ((Property) o).getValue();
					if (pValue != null) {
						pu.setProperty(pName, pValue);
					}

				}
			}

			if (dataSource == null) {
				dataSource = new DataSource(ds.getName(), ds.getOdaExtensionDataSourceId(), ds.getOdaExtensionId(), pu, pp);

				Activator.getInstance().getListDataSource().add(dataSource);
			}
			else {
				dataSource.setOdaExtensionDataSourceId(ds.getOdaExtensionDataSourceId());
				dataSource.setOdaExtensionId(ds.getOdaExtensionId());
				dataSource.setPrivateProperties(pp);
				dataSource.setPublicProperties(pu);
			}

			if (dataSource.getOdaExtensionId().equals(bpm.vanilla.platform.core.repository.DataSource.ODA_EXTENSION)) {
				if (dataSource.getPrivateProperties() != null) {
					String currentOdaExtensionId = dataSource.getPrivateProperties().getProperty(bpm.vanilla.platform.core.repository.DataSource.CURRENT_ODA_EXTENSION);
					String currentOdaDatasourceExtensionId = dataSource.getPrivateProperties().getProperty(bpm.vanilla.platform.core.repository.DataSource.CURRENT_ODA_EXTENSION_DATASOURCE);

					if (currentOdaExtensionId != null && currentOdaDatasourceExtensionId != null) {
						dataSource.setOdaExtensionId(currentOdaExtensionId);
						dataSource.setOdaExtensionDataSourceId(currentOdaDatasourceExtensionId);
					}
				}
			}

		}
		return b;
	}

}
