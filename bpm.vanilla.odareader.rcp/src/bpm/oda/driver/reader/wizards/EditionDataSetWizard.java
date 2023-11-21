package bpm.oda.driver.reader.wizards;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.wizards.pages.DataSourceSelectionPage;
import bpm.oda.driver.reader.wizards.pages.ParameterPage;


public class EditionDataSetWizard extends OdaDataSetWizard{

	private ParameterPage paramPage;
	
	public EditionDataSetWizard(DataSet dataSet) throws OdaException {
		super(dataSet);
		this.dataSet = dataSet;
		
	}

	@Override
	public void addPages() {
		if (getContainer() instanceof WizardDialog){
		
			((WizardDialog)getContainer()).addPageChangingListener(new IPageChangingListener(){

				public void handlePageChanging(PageChangingEvent event) {
					
					 if (event.getTargetPage() == paramPage){
						paramPage.setInput(dataSet);
					}
					
				}
				
			});
		}
		
		selectPage = new DataSourceSelectionPage("DataSource Selection Page", dataSet);
		selectPage.setTitle("DataSource Selection");
		selectPage.setDescription("Select an existing datasource that will hld the dataset");
		
		addPage(selectPage);
		
		for(final IWizardPage wizardPages : selectPage.getNextPage().getWizard().getPages()){
			
			IWizardPage page = new WizardPage(wizardPages.getName()){

				public void createControl(Composite parent) {
					wizardPages.createControl(parent);
					
					setControl(wizardPages.getControl());
					
				}
				
			};
			page.setTitle(wizardPages.getTitle());
			page.setDescription(wizardPages.getDescription());
			
			addPage(page);
			
		}
		
		paramPage = new ParameterPage("Parameters");
		paramPage.setTitle("Parameters");
		addPage(paramPage);
		
	}
	
	private Properties createProperties(org.eclipse.datatools.connectivity.oda.design.Properties properties){
		Properties p = new Properties();
		if (properties == null){
			return p;
		}
		for(int i = 0; i < properties.getProperties().size(); i++){
			Object o = properties.getProperties().get(i);
			String v = ((Property)o).getValue();
			properties.setProperty(((Property)o).getName(), v != null ? v : "");
			
		}
		return p;
	}

	@Override
	public boolean performFinish() {
		
		return super.performFinish();
	}
	
	
}
