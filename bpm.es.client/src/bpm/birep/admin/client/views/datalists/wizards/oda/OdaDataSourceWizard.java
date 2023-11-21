package bpm.birep.admin.client.views.datalists.wizards.oda;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
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

import adminbirep.Messages;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.repository.DataSource;




public class OdaDataSourceWizard extends NewDataSourceWizard {
	private DataSourceWizardPage myStartPage;
	private DataSourceTypePage typePage;
	private String currentExtensionId;
	private OdaInput odaInput;
	
	
	public OdaDataSourceWizard(OdaInput odaInput) throws OdaException {
		super();
		this.initOdaDesignSession(null, DesignFactory.eINSTANCE.createDesignSessionRequest());
		this.odaInput = odaInput;
		this.setForcePreviousAndNextButtons(true);
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase#addPages()
	 */
	@Override
	public void addPages() {
		
		typePage = new DataSourceTypePage(Messages.Client_Views_DataLists_OdaDataSourceWizard_0, odaInput);
		typePage.setTitle(Messages.Client_Views_DataLists_OdaDataSourceWizard_1);
		typePage.setDescription(Messages.Client_Views_DataLists_OdaDataSourceWizard_2);
		
		addPage(typePage);
		
//		this.mProfilePage = new NewConnectionProfileWizardPage();
//		addPage(this.mProfilePage);
//		
//		if (dataSource != null){
//			mProfilePage.setProfileName(dataSource.getName());
//		}
		this.mProfilePage = new NewConnectionProfileWizardPage();
	}



	

	
	 
	
	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean b =  super.performFinish();
		
		if (b){
			DataSourceDesign ds = getDataSourceDesign();
			
			java.util.Properties pp = new java.util.Properties();
				
			if (ds.getPrivateProperties() != null){
				for(Object o :  ds.getPrivateProperties().getProperties()){
					
					String pName = ((Property)o).getName();
					String pValue = ((Property)o).getValue();
					if (pValue != null){
						pp.setProperty(pName, pValue);
					}
				}
			}
			
			
			java.util.Properties pu = new java.util.Properties();
			if (ds.getPublicProperties() != null){
				for(Object o :  ds.getPublicProperties().getProperties()){
					String pName = ((Property)o).getName();
					String pValue = ((Property)o).getValue();
					if (pValue != null){
						pu.setProperty(pName, pValue);
					}
					
				}
			}
			
			odaInput.setDatasourcePrivateProperties(pp);
			odaInput.setDatasourcePublicProperties(pu);
			odaInput.setOdaExtensionDataSourceId(ds.getOdaExtensionDataSourceId());
			odaInput.setOdaExtensionId(ds.getOdaExtensionId());
			odaInput.setName(typePage.getSelectedName());
			odaInput.setDescription(typePage.getDescription());
			
			if(odaInput.getOdaExtensionId().equals(DataSource.ODA_EXTENSION)) {
				if(odaInput.getDatasetPrivateProperties() != null) {
					String currentOdaExtensionId = odaInput.getDatasetPrivateProperties().getProperty(DataSource.CURRENT_ODA_EXTENSION);
					String currentOdaDatasourceExtensionId = odaInput.getDatasetPrivateProperties().getProperty(DataSource.CURRENT_ODA_EXTENSION_DATASOURCE);

					if(currentOdaExtensionId != null && currentOdaDatasourceExtensionId != null) {
						odaInput.setOdaExtensionId(currentOdaExtensionId);
						odaInput.setOdaExtensionDataSourceId(currentOdaDatasourceExtensionId);
					}
				}
			}
			
			return true;
		}
		return false;
	}

	



	public void updatePages() {
		try {
			
			if (!typePage.getExtensionManifest().getExtensionID().equals(currentExtensionId)){
				initialize( typePage.getExtensionManifest().getExtensionID(), null, "" ); //$NON-NLS-1$
				
				Properties p = new Properties();
				p.putAll(odaInput.getDatasourcePrivateProperties());
				p.putAll(odaInput.getDatasourcePublicProperties());
				addCustomPages(typePage.getExtensionManifest().getExtensionID());
				if (odaInput.getOdaExtensionDataSourceId() != null){
					((DataSourceWizardPage)this.getCustomStartingPage()).setInitialProperties(p);
				}
				
//				return 	this.getCustomStartingPage();
				
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == typePage){
			try {
				
				if (!typePage.getExtensionManifest().getExtensionID().equals(currentExtensionId)){
					initialize( typePage.getExtensionManifest().getExtensionID(), null, "" ); //$NON-NLS-1$
					
					Properties p = new Properties();
					p.putAll(odaInput.getDatasourcePrivateProperties());
					p.putAll(odaInput.getDatasourcePublicProperties());
					addCustomPages(typePage.getExtensionManifest().getExtensionID());
					if (odaInput.getOdaExtensionDataSourceId() != null){
						((DataSourceWizardPage)this.getCustomStartingPage()).setInitialProperties(p);
					}
				
				}
				return 	this.getCustomStartingPage();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		return super.getNextPage(page);
	}

	public void addCustomPages(String odaDataSourceId) throws Exception{
		
		if (this.getCustomStartingPage() == null){
			super.addCustomPages();
			this.currentExtensionId = odaDataSourceId;
			return;
		}
		if (this.currentExtensionId .equals(odaDataSourceId)){
			return;
		}
		UIExtensionManifest mnf =  UIManifestExplorer.getInstance().getExtensionManifest(  odaDataSourceId );
        
        // get page attributes from ODA wizard page's extension element
        DataSourceWizardInfo wizardInfo = 
        	mnf.getDataSourceWizardInfo();
        assert( wizardInfo != null );
        String wizardPageClassName = wizardInfo.getPageClassName();
        String pageTitle = wizardInfo.getPageTitle();
        
        myStartPage = createWizardPage( wizardPageClassName, pageTitle );
        addPage( myStartPage );
        this.currentExtensionId = odaDataSourceId;
    }
	
	@Override
	public IWizardPage getCustomStartingPage() {
		if (myStartPage == null){
			return super.getCustomStartingPage();
		}
		else{
			return myStartPage;
		}
		
	}

	@Override
	public boolean canFinish() {
		if (myStartPage != null){
			return myStartPage.isPageComplete();
		}
		return super.canFinish();
		
	}
}
