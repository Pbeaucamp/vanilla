package bpm.es.gedmanager.wizards;


import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;

import bpm.es.gedmanager.Messages;
import bpm.vanilla.platform.core.beans.data.OdaInput;



public class OdaDataSetWizard extends DataSetWizard {
	protected OdaInput odaInput;
	private DataSetTypePage typePage;
	
	public OdaDataSetWizard(OdaInput odaInput) throws OdaException {
		super();
		this.odaInput = odaInput;
	}
	
	
	
	private java.util.Properties createProperties(org.eclipse.datatools.connectivity.oda.design.Properties properties){
		java.util.Properties p = new java.util.Properties();
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
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.NewDataSourceWizardBase#performFinish()
	 */
	@Override
	public boolean performFinish() {
		boolean b = typePage.getNextPage().getWizard().performFinish();
		DataSetDesign dd;
		try {
			dd = ((DataSetWizard)typePage.getNextPage().getWizard()).getResponseSession().getResponseDataSetDesign();
			
			Properties publicProp = createProperties( dd.getPrivateProperties());
			Properties privateProp = createProperties( dd.getPublicProperties());
			odaInput.setQueryText(dd.getQueryText());
			odaInput.setDatasetPublicProperties(publicProp);
			odaInput.setDatasetPrivateProperties(privateProp);
			
			//TODO rebuildescriptor
			return b;
		} catch (OdaException e) {
			
			e.printStackTrace();
		}
		
		return false;

	}

	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		typePage = new DataSetTypePage("DataSet Type", odaInput); //$NON-NLS-1$
		
//		if (typePage.isMultipleDataSetType()){
			addPage(typePage);
//		}
//		else{
//			typePage.setWizard(this);
//			addPage(typePage.getNextPage());
//		}
		
		
	
		setForcePreviousAndNextButtons(true);
			
		super.addPages();
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getStartingPage()
	 */
	@Override
	public IWizardPage getStartingPage() {
		if (typePage != null && !typePage.isMultipleDataSetType()){
			return typePage.getNextPage();
		}
		return super.getStartingPage();
	}
}
