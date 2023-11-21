package bpm.fd.design.ui.wizard.migration.datasource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizardPage;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IEditorInput;

import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;
import bpm.fd.design.ui.wizard.pages.DataSourceTypePage;

public class MigrationWizard extends OdaDataSourceWizard{

	private DataSourceSelectPage dsPage;
	private DataSourceWrapperPage wrappedPage;
	
	public MigrationWizard() throws OdaException {
		super();
		
	}
	
	@Override
	public void addPages() {
		
		if (dataSource != null){
			typePage = new DataSourceTypePage("DataSource Type Page", dataSource); //$NON-NLS-1$
		}
		else{
			typePage = new DataSourceTypePage("DataSource Type Page"); //$NON-NLS-1$
		}
		
		typePage.setTitle(Messages.MigrationWizard_2);
		typePage.setDescription(Messages.MigrationWizard_3);
		addPage(typePage);
		this.mProfilePage = new NewConnectionProfileWizardPage();
		
		
		if (dataSource != null){
			mProfilePage.setProfileName(dataSource.getName());
		}
		
		
		wrappedPage = new DataSourceWrapperPage("DataSource Definition"); //$NON-NLS-1$
		wrappedPage.setTitle(Messages.MigrationWizard_5);
	
		
		addPage(wrappedPage);
		
		dsPage = new DataSourceSelectPage("DataSources Selection"); //$NON-NLS-1$
		dsPage.setTitle(Messages.MigrationWizard_7);
		dsPage.setDescription(Messages.MigrationWizard_8);
		
		addPage(dsPage);
	}

	@Override
	public boolean performFinish() {
		
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
			
			MultiStatus mstatus = new MultiStatus(Activator.PLUGIN_ID, IStatus.INFO, Messages.MigrationWizard_9, null);
			
			for(DataSource d : dsPage.getDataSourceToMigrate()){
				d.setPrivateProperties(pp);
				d.setPublicProperties(pu);
				
				
				MultiStatus ms = new MultiStatus(Activator.PLUGIN_ID, IStatus.INFO, Messages.MigrationWizard_10 + d.getName(), null);
				for(DataSet dataset : Activator.getDefault().getProject().getDictionary().getDataSetsFor(d)){
					
					try {
						dataset.buildDescriptor(d);
						ms.add(new Status(IStatus.INFO, Activator.PLUGIN_ID, "DataSet " + dataset.getName() + Messages.MigrationWizard_12)); //$NON-NLS-1$
					} catch (Exception e) {
						e.printStackTrace();
						ms.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "DataSet " + dataset.getName() + Messages.MigrationWizard_14, e)); //$NON-NLS-1$
						
					}
					
				}
				mstatus.add(ms);
			}
			
			ErrorDialog dial = new ErrorDialog(getShell(), Messages.MigrationWizard_15, "", mstatus, IStatus.INFO | IStatus.ERROR); //$NON-NLS-2$
			dial.open();
		
		
			// refresh the editor
			
			try{
				FdEditor editor = (FdEditor)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				IEditorInput editorInput = editor.getStructureEditor().getEditorInput();
				editor.getStructureEditor().init(editor.getStructureEditor().getEditorSite(), editorInput);

			}catch(Exception ex){
				ex.printStackTrace();
			}
			Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_DATASOURCE_CHANGED, null, ds);				}

		
		return b;
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == typePage){
			try {
				initialize( typePage.getExtensionManifest().getExtensionID(), null, "" ); //$NON-NLS-1$
				super.addCustomPages();
				if (this.getCustomStartingPage() instanceof DataSourceWizardPage && dataSource != null){
					((DataSourceWizardPage)this.getCustomStartingPage()).setInitialProperties(dataSource.getProperties());
				}
				if (this.getCustomStartingPage().getControl() == null){
					this.getCustomStartingPage().createControl(typePage.getControl().getParent());
				}
				
				wrappedPage.setWrappedPage(this.getCustomStartingPage());
//				((Composite)this.getCustomStartingPage().getControl()).layout();
				return 	wrappedPage;
			} catch (OdaException e) {
				
				e.printStackTrace();
			}
		}
		else if (page == wrappedPage){
			
			List<DataSource> l = new ArrayList<DataSource>();
			
			for(DataSource d : Activator.getDefault().getProject().getDictionary().getDatasources()){
				if (d.getOdaExtensionId().equals(typePage.getExtensionManifest().getExtensionID())){
					l.add(d);
				}
			}
			
			dsPage.setInput(l);
			return dsPage;
		}
		
		return super.getNextPage(page);
	}
	
	
}
