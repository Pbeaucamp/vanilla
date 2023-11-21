package bpm.birep.admin.client.views.datalists.wizards;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import adminbirep.Messages;
import bpm.birep.admin.client.views.datalists.wizards.oda.DataSetContentPage;
import bpm.birep.admin.client.views.datalists.wizards.oda.OdaDataSetWizard;
import bpm.birep.admin.client.views.datalists.wizards.oda.OdaDataSourceWizard;
import bpm.vanilla.platform.core.beans.data.OdaInput;




public class EditionDataSetWizard extends OdaDataSetWizard{

	private DataSetContentPage contentPage;
//	private ParameterPage paramPage;
	
	private OdaDataSourceWizard dataSourcerWiz;
	
	public EditionDataSetWizard(OdaInput odaInput) throws OdaException {
		super(odaInput);
		dataSourcerWiz = new OdaDataSourceWizard(odaInput);
	}

	@Override
	public void addPages() {
		if (getContainer() instanceof WizardDialog){
			((WizardDialog)getContainer()).addPageChangingListener(new IPageChangingListener(){

				public void handlePageChanging(PageChangingEvent event) {
					if (event.getTargetPage() == contentPage){
//						contentPage.createViewer(odaInput);
					}
//					else if (event.getTargetPage() == paramPage){
//						paramPage.setInput(dataSet);
//					}
					
				}
				
			});
		}
		dataSourcerWiz.addPages();
		
//		selectPage = new DataSourceSelectionPage("DataSource Selection Page", dataSet);
//		selectPage.setTitle("DataSource Selection");
//		selectPage.setDescription("Select an existing datasource that will hld the dataset");
//		
//		addPage(selectPage);
//
//		final IWizardPage p = selectPage.getNextPage();
//		IWizardPage page = new WizardPage(p.getName()){
//
//			public void createControl(Composite parent) {
//				p.createControl(parent);
//				
//				setControl(p.getControl());
//				
//			}
//			
//		};
//		page.setTitle(p.getTitle());
//		page.setDescription(p.getDescription());
//		
//		addPage(page);
		
		contentPage = new DataSetContentPage("contentPage"); //$NON-NLS-1$
		contentPage.setTitle(Messages.Client_Views_DataLists_EditionDataSetWizard_1);
		addPage(contentPage);
		
		
//		paramPage = new ParameterPage("Parameters");
//		paramPage.setTitle("Parameters");
//		addPage(paramPage);
		
		
	}

	@Override
	public boolean performFinish() {
		
		return super.performFinish();
	}
	
	@Override
	public void createPageControls(Composite pageContainer) {
		
		super.createPageControls(pageContainer);
		dataSourcerWiz.updatePages();
		IWizardPage p = dataSourcerWiz.getStartingPage();
		addPage(p);
		
		while(dataSourcerWiz.getNextPage(p) != null && dataSourcerWiz.getNextPage(p) != p){
			p = dataSourcerWiz.getNextPage(p);
			addPage(p);
		}
		super.addPages();
	}
}
