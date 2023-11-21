package bpm.fd.design.ui.wizard;

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
import org.eclipse.ui.statushandlers.StatusManager;

import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.fmdt.DialogFmdtResourceGeneration;
import bpm.fd.design.ui.wizard.fmdt.FmdDataSetHelper;
import bpm.fd.design.ui.wizard.pages.DataSourceSelectionPage;

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
			properties.setProperty(((Property)o).getName(), v != null ? v : ""); //$NON-NLS-1$
			
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
				DataSetDesign dd = ((DataSetWizard)p.getWizard()).getResponseSession().getResponseDataSetDesign();
				
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
	
	
	@Override
	protected void finishDataSetDesign() throws OdaException {
		
//		super.finishDataSetDesign();
		
		try{
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
				dataSet.buildDescriptor(selectPage.getDataSourceSelected());
				dataSet.setName(selectPage.getDataSetName());
				
				Activator.getDefault().getProject().getDictionary().addDataSet(dataSet);
				Activator.getDefault().firePropertyChange(Activator.PROJECT_CONTENT, null, dataSet);
				
				/*
				 * if this a parametered DataSet on FMDT
				 * we propose to create a DataSet to provide the parameters values
				 * and to create automatically a filter in the dictionary
				 */
				if (dataSet.getOdaExtensionDataSourceId().equals("bpm.metadata.birt.oda.runtime") && !dataSet.getDataSetDescriptor().getParametersDescriptors().isEmpty()){
					
					DialogFmdtResourceGeneration dial = new DialogFmdtResourceGeneration(getShell());
					
					if (dial.open() == DialogFmdtResourceGeneration.OK){
						
						FmdDataSetHelper heper = new FmdDataSetHelper(selectPage.getDataSourceSelected());
						
						IStatus result = heper.generateParameterDataSet(dataSet, dial.getOption());
						StatusManager.getManager().handle(result, StatusManager.LOG | StatusManager.BLOCK);
						
						
					}
					
					
				}
				
			}
			else{
				dataSet.resetDefinition(publicProp,	privateProp,dd.getQueryText(), selectPage.getDataSourceSelected());
				Activator.getDefault().getProject().getDictionary().firePropertyChange(Dictionary.PROPERTY_CONTENT_CHANGED, null, dataSet);
				
				/*
				 * if this a parametered DataSet on FMDT
				 * we propose to create a DataSet to provide the parameters values
				 * and to create automatically a filter in the dictionary
				 */
				if (dataSet.getOdaExtensionDataSourceId().equals("bpm.metadata.birt.oda.runtime") && !dataSet.getDataSetDescriptor().getParametersDescriptors().isEmpty()){
					FmdDataSetHelper heper = new FmdDataSetHelper(selectPage.getDataSourceSelected());
					DialogFmdtResourceGeneration dial = new DialogFmdtResourceGeneration(getShell());
					
					if (dial.open() == DialogFmdtResourceGeneration.OK){
						
						
						
						try{
							heper.generateParameterDataSet(dataSet, dial.getOption());
						}catch(Exception ex){
							
						}
						
					}
					
					
				}
			}
			
			
			
			
		}catch(Exception ex){
			throw new OdaException(ex);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase#performFinish()
	 */
	@Override
	public boolean performFinish() {
		
		if (!canFinish()){
			return false;
		}
		
		try {
			

			finishDataSetDesign();
			
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			ErrorDialog d = new ErrorDialog(getShell(), Messages.OdaDataSetWizard_1, Messages.OdaDataSetWizard_2,
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
		
		
		selectPage = new DataSourceSelectionPage(Messages.OdaDataSetWizard_3, dataSet);
		selectPage.setTitle(Messages.OdaDataSetWizard_4);
		selectPage.setDescription(Messages.OdaDataSetWizard_5);
		
		addPage(selectPage);
		
		if (dataSet != null){
			selectPage.setDataSet(dataSet);
		}
		
			
		addPage(new WizardPage("edition"){ //$NON-NLS-1$

			public void createControl(Composite parent) {
				Composite c = new Composite(parent, SWT.NONE);
				c.setLayoutData(new GridData());
				setControl(c);
				
			}
			
		});
		super.addPages();
	}

	
	
	@Override
	public boolean canFinish() {
		boolean b = super.canFinish();
		
		if ( b){
			if (selectPage != null){
				IWizardPage p = selectPage.getNextPage();
				
				if (p.getControl() != null && !p.isPageComplete()){
					return false;
				}
				
				while(p.getNextPage() != null && p.getNextPage() != p){
					p = p.getNextPage();
					
					if (!p.isPageComplete()){
						return false;
					}
				}
				
			}
		}
		return true;
	}
}
