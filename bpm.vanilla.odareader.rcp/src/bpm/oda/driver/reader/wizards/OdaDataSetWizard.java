package bpm.oda.driver.reader.wizards;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizard;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizardPage;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.datasource.DataSource;
import bpm.oda.driver.reader.wizards.pages.DataSourceSelectionPage;


public class OdaDataSetWizard extends DataSetWizard implements INewWizard {
	
	protected DataSourceSelectionPage selectPage;
	protected DataSet dataSet;

	
	
	public OdaDataSetWizard(DataSet dataSet) throws OdaException {
		super();
		this.dataSet = dataSet;
	}
	
	public DataSet getDataSet(){
		return dataSet;
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

	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof DataSetWizardPage){
			try {
				DataSetWizardPage p = (DataSetWizardPage)selectPage.getDataSetPage();

				boolean b = p.getWizard().performFinish();
				DataSetDesign dd = ((OdaDataSetWizard)p.getWizard()).getResponseSession().getResponseDataSetDesign();
				
				Properties publicProp = createProperties( dd.getPrivateProperties());
				Properties privateProp = createProperties( dd.getPublicProperties());
				

				DataSet dataSet = new DataSet(selectPage.getDataSetName(),
						dd.getOdaExtensionDataSetId(),
						dd.getOdaExtensionDataSourceId(),
						publicProp,
						privateProp,
						dd.getQueryText(),
						selectPage.getDataSourceSelected()
						);
				
				dataSet.setName(selectPage.getDataSetName());
			

			}catch(Exception e){
				e.printStackTrace();
			}

		}
		return super.getNextPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase#performFinish()
	 */
	@Override
	public boolean performFinish() {

		
		try {
			DataSetWizardPage p = (DataSetWizardPage)selectPage.getDataSetPage();

			boolean b = p.getWizard().performFinish();
			DataSetDesign dd = ((DataSetWizard)p.getWizard()).getResponseSession().getResponseDataSetDesign();
			
			Properties publicProp = createProperties( dd.getPrivateProperties());
			Properties privateProp = createProperties( dd.getPublicProperties());
			
			
			if (dataSet == null){
				dataSet = new DataSet(selectPage.getDataSetName(),
						dd.getOdaExtensionDataSetId(),
						dd.getOdaExtensionDataSourceId(),
						publicProp,
						privateProp,
						dd.getQueryText(),
						selectPage.getDataSourceSelected()
						);
				
				dataSet.setName(selectPage.getDataSetName());
				
				Activator.getInstance().getListDataSet().add(dataSet);
				
			}
			else{
				int index = Activator.getInstance().getListDataSet().indexOf(dataSet);
				
				DataSet set = Activator.getInstance().getListDataSet().get(index);
				DataSource source = Activator.getInstance().getDataSource(set);
				
				set.setPrivateProperties(privateProp);
				set.setPublicProperties(publicProp);
				set.setQueryText(source, dd.getQueryText());
				set.setResultSet(null);
				set.setResultSetUpdated(true);
				
			}
			
			
			return b;
			
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog d = new ErrorDialog(getShell(), "Error", "Unable to create Dataset",
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage()), IStatus.ERROR);
			d.open();
			return false;
		}
		
	}

	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (!selection.isEmpty() && selection.getFirstElement() instanceof DataSet){
			dataSet = (DataSet)selection.getFirstElement();
			selectPage.setDataSet(dataSet);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		
		
		selectPage = new DataSourceSelectionPage("DataSource Selection Page", dataSet);
		selectPage.setTitle("DataSource Selection");
		selectPage.setDescription("Select an existing datasource that will hold the dataset");
		
		addPage(selectPage);
		
		if (dataSet != null){
			selectPage.setDataSet(dataSet);
		}
		
			
		addPage(new WizardPage("edition"){

			public void createControl(Composite parent) {
				Composite c = new Composite(parent, SWT.NONE);
				c.setLayoutData(new GridData());
				setControl(c);
				
			}
			
		});
		super.addPages();
	}

	
	

}
