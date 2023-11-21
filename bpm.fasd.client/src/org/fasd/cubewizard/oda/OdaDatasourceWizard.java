package org.fasd.cubewizard.oda;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.DataSourceWizardInfo;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.UIExtensionManifest;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.UIManifestExplorer;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.NewDataSourceWizard;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizardPage;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;
import org.fasd.datasource.DatasourceOda;
import org.fasd.i18N.LanguageText;

import bpm.vanilla.platform.core.repository.DataSource;

public class OdaDatasourceWizard extends NewDataSourceWizard implements INewWizard {

	private OdaDatasourceTypePage typePage;
	private DatasourceOda datasource;
	private DataSourceWizardPage myStartPage;
	private String odaDataSourceId;

	public OdaDatasourceWizard(DatasourceOda datasource) {
		this.initOdaDesignSession(null, DesignFactory.eINSTANCE.createDesignSessionRequest());
		this.datasource = datasource;
		this.setForcePreviousAndNextButtons(true);
	}

	@Override
	public void addPages() {
		typePage = new OdaDatasourceTypePage("type", datasource); //$NON-NLS-1$
		typePage.setTitle(LanguageText.OdaDatasourceWizard_1);
		typePage.setDescription(LanguageText.OdaDatasourceWizard_2);
		addPage(typePage);

		this.mProfilePage = new NewConnectionProfileWizardPage();

		if (datasource != null && datasource.getId() != null && !datasource.getId().equalsIgnoreCase("")) { //$NON-NLS-1$
			this.mProfilePage.setProfileName(datasource.getName());
		}

		addPage(this.mProfilePage);
	}

	protected boolean finishDataSource() {
		return super.performFinish();
	}

	@Override
	public boolean performFinish() {

		boolean finished = finishDataSource();

		if (finished) {
			DataSourceDesign design = getDataSourceDesign();

			datasource.setName(design.getName());
			datasource.setOdaDatasourceExtensionId(design.getOdaExtensionId());
			datasource.setOdaExtensionId(design.getOdaExtensionId());

			java.util.Properties pp = new java.util.Properties();

			if (design.getPrivateProperties() != null) {
				for (Object o : design.getPrivateProperties().getProperties()) {

					String pName = ((Property) o).getName();
					String pValue = ((Property) o).getValue();
					if (pValue != null) {
						pp.setProperty(pName, pValue);
					}
				}
			}

			java.util.Properties pu = new java.util.Properties();
			if (design.getPublicProperties() != null) {
				for (Object o : design.getPublicProperties().getProperties()) {
					String pName = ((Property) o).getName();
					String pValue = ((Property) o).getValue();
					if (pValue != null) {
						pu.setProperty(pName, pValue);
					}

				}
			}

			datasource.setPrivateProperties(pp);
			datasource.setPublicProperties(pu);

			if (datasource.getOdaExtensionId().equals(DataSource.ODA_EXTENSION)) {
				if (datasource.getPrivateProperties() != null) {
					String currentOdaExtensionId = datasource.getPrivateProperties().getProperty(DataSource.CURRENT_ODA_EXTENSION);
					String currentOdaDatasourceExtensionId = datasource.getPrivateProperties().getProperty(DataSource.CURRENT_ODA_EXTENSION_DATASOURCE);

					if (currentOdaExtensionId != null && currentOdaDatasourceExtensionId != null) {
						datasource.setOdaExtensionId(currentOdaExtensionId);
						datasource.setOdaDatasourceExtensionId(currentOdaDatasourceExtensionId);
					}
				}
			}
		}

		return finished;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizard#getNextPage
	 * (org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == mProfilePage && page.isPageComplete()) {
			try {
				System.out.println(typePage.getExtensionManifest().getExtensionID());
				initialize(typePage.getExtensionManifest().getExtensionID(), null, ""); //$NON-NLS-1$

				addCustomPages(typePage.getExtensionManifest().getExtensionID());

				if (this.getCustomStartingPage() instanceof DataSourceWizardPage && (datasource != null && datasource.getName() != null && !datasource.getName().equalsIgnoreCase(""))) { //$NON-NLS-1$
					((DataSourceWizardPage) this.getCustomStartingPage()).setInitialProperties(datasource.getProperties());
				}

				return this.getCustomStartingPage();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return super.getNextPage(page);
	}

	public DatasourceOda getDatasource() {
		return datasource;
	}

	public void addCustomPages(String odaDataSourceId) throws Exception {

		if (this.getCustomStartingPage() == null) {
			super.addCustomPages();
			this.odaDataSourceId = odaDataSourceId;
			return;
		}
		if (this.odaDataSourceId.equals(odaDataSourceId)) {
			return;
		}
		UIExtensionManifest mnf = UIManifestExplorer.getInstance().getExtensionManifest(odaDataSourceId);

		// get page attributes from ODA wizard page's extension element
		DataSourceWizardInfo wizardInfo = mnf.getDataSourceWizardInfo();
		assert (wizardInfo != null);
		String wizardPageClassName = wizardInfo.getPageClassName();
		String pageTitle = wizardInfo.getPageTitle();

		myStartPage = createWizardPage(wizardPageClassName, pageTitle);
		addPage(myStartPage);
		this.odaDataSourceId = odaDataSourceId;
	}

	@Override
	public IWizardPage getCustomStartingPage() {
		if (myStartPage == null) {
			return super.getCustomStartingPage();
		} else {
			return myStartPage;
		}

	}

	@Override
	protected DataSourceWizardPage getCustomWizardPage() {
		if (myStartPage != null) {
			return myStartPage;
		}
		return super.getCustomWizardPage();
	}

	@Override
	protected Properties collectCustomProperties() {
		if (myStartPage != null) {
			return myStartPage.collectCustomProperties();
		}
		return super.collectCustomProperties();
	}

	@Override
	public boolean canFinish() {
		if (myStartPage != null) {
			return myStartPage.isPageComplete();
		}
		return super.canFinish();

	}

}
