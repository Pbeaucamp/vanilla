package bpm.fd.design.ui.wizard;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.INewWizard;

import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.pages.DataSourceTypePage;

public class OdaDataSourceWizard extends NewDataSourceWizard implements INewWizard {

	private static final String ODA_DTP = "org.eclipse.datatools.connectivity.oda.dataSource"; //$NON-NLS-1$
	private static final String ODA_FMDT = "bpm.metadata.birt.oda.runtime"; //$NON-NLS-1$
	
	protected DataSourceTypePage typePage;
	
	protected DataSource dataSource;
	
	
	private DataSourceWizardPage myStartPage;
	private String odaDataSourceId;
	public OdaDataSourceWizard() throws OdaException {
		super();
		this.initOdaDesignSession(null, DesignFactory.eINSTANCE.createDesignSessionRequest());

		this.setForcePreviousAndNextButtons(true);
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase#addPages()
	 */
	@Override
	public void addPages() {
		
		if (dataSource != null){
			typePage = new DataSourceTypePage("DataSource Type Page", dataSource); //$NON-NLS-1$
		}
		else{
			typePage = new DataSourceTypePage("DataSource Type Page"); //$NON-NLS-1$
		}
		
		typePage.setTitle(Messages.OdaDataSourceWizard_0);
		typePage.setDescription(Messages.OdaDataSourceWizard_5);
		
		addPage(typePage);
		
		this.mProfilePage = new NewConnectionProfileWizardPage(){
			@Override
			public boolean isPageComplete() {
				if (getProfileName() == null || getProfileName().isEmpty()){
					return false;
				}
				char c = getProfileName().charAt(0);
				if (!Character.isJavaIdentifierStart(c)){
					setErrorMessage(Messages.OdaDataSourceWizard_7);
					return false;
					
				}
				for(int i = 0; i < getProfileName().length(); i++){
					c = getProfileName().charAt(i);
					if ((c < '0' || c >'9') && (c<'a' || c>'z') && (c<'A' || c>'Z') && c !='_'){
						setErrorMessage(Messages.OdaDataSourceWizard_9);
						return false;
					}
				}
				if (dataSource == null){
					if (Activator.getDefault().getProject().getDictionary().getDatasource(getProfileName()) != null){
						setErrorMessage("A DataSource with the same name already exists.");
						return false;
					}
				}
				
				
				setErrorMessage(null);
				return super.isPageComplete();
			}
		};
		addPage(this.mProfilePage);
		
		if (dataSource != null){
			mProfilePage.setProfileName(dataSource.getName());
		}
		
	}



	public OdaDataSourceWizard(DataSource dataSource) throws OdaException {
		this.dataSource = dataSource;
		
		this.initOdaDesignSession(null, DesignFactory.eINSTANCE.createDesignSessionRequest());
		this.setForcePreviousAndNextButtons(true);
		
	}

	
	 
	
	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase#performFinish()
	 */
	@Override
	public boolean performFinish() {
		
		char c = getProfileName().charAt(0);
		if (!Character.isJavaIdentifierStart(c)){
			MessageDialog.openInformation(getShell(), Messages.OdaDataSourceWizard_6,Messages.OdaDataSourceWizard_7);
			return false;
		}
		for(int i = 0; i < getProfileName().length(); i++){
			c = getProfileName().charAt(i);
			if ((c < '0' || c >'9') && (c<'a' || c>'z') && (c<'A' || c>'Z') && c !='_'){
				MessageDialog.openInformation(getShell(), Messages.OdaDataSourceWizard_8,Messages.OdaDataSourceWizard_9);
				return false;
			}
		}
		
		boolean b =  finishDataSource();
		
		
		
		
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
			
			if (dataSource == null){
				dataSource = new DataSource(Activator.getDefault().getProject().getDictionary(), ds.getName(), ds.getOdaExtensionDataSourceId(), ds.getOdaExtensionId(), pu, pp);
				
				try {
					Activator.getDefault().getProject().getDictionary().addDataSource(dataSource);
				} catch (DictionaryException e) {
					e.printStackTrace();
					MessageDialog.openError(getShell(), "DataSource Creation", "Failed to add DataSource : " + e.getMessage());
				}
			}
			else{
				dataSource.setOdaExtensionDataSourceId(ds.getOdaExtensionDataSourceId());
				dataSource.setOdaExtensionId(ds.getOdaExtensionId());
				dataSource.setPrivateProperties(pp);
				dataSource.setPublicProperties(pu);
			}
			
			if(dataSource.getOdaExtensionId().equals(bpm.vanilla.platform.core.repository.DataSource.ODA_EXTENSION)) {
				if(dataSource.getPrivateProperties() != null) {
					String currentOdaExtensionId = dataSource.getPrivateProperties().getProperty(bpm.vanilla.platform.core.repository.DataSource.CURRENT_ODA_EXTENSION);
					String currentOdaDatasourceExtensionId = dataSource.getPrivateProperties().getProperty(bpm.vanilla.platform.core.repository.DataSource.CURRENT_ODA_EXTENSION_DATASOURCE);

					if(currentOdaExtensionId != null && currentOdaDatasourceExtensionId != null) {
						dataSource.setOdaExtensionId(currentOdaExtensionId);
						dataSource.setOdaExtensionDataSourceId(currentOdaDatasourceExtensionId);
					}
				}
			}
			
			Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, dataSource);
		
		
		}
		return b;
	}

	@Override
	protected DataSourceWizardPage getCustomWizardPage() {
		if(myStartPage != null) {
			return myStartPage;
		}
		return super.getCustomWizardPage();
	}
	


	
	protected boolean finishDataSource(){

		return super.performFinish();
	}

	protected Properties collectCustomProperties() {
		if (myStartPage != null)
			return myStartPage.collectCustomProperties();

		return super.collectCustomProperties(); // use own cached properties
	}	
	
	public void addCustomPages(String odaDataSourceId) throws Exception{
		
		if (this.getCustomStartingPage() == null){
			super.addCustomPages();
			this.odaDataSourceId = odaDataSourceId;
			return;
		}
		if (this.odaDataSourceId .equals(odaDataSourceId)){
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
        this.odaDataSourceId = odaDataSourceId;
    }
	
	@Override
	public boolean canFinish() {
		if (myStartPage != null){
			return myStartPage.isPageComplete();
		}
		return super.canFinish();
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == mProfilePage){
			try {
				
				initialize( typePage.getExtensionManifest().getExtensionID(), null, "" ); //$NON-NLS-1$
				
				
				
				addCustomPages(typePage.getExtensionManifest().getExtensionID());
//				setInOdaDesignSession(true);
				if (this.getCustomStartingPage() instanceof DataSourceWizardPage && dataSource != null){
					((DataSourceWizardPage)this.getCustomStartingPage()).setInitialProperties(dataSource.getProperties());
//					myStartPage.setInitialProperties(dataSource.getProperties());
				}
				
				return 	this.getCustomStartingPage();
//				return myStartPage;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return super.getNextPage(page);
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

}
