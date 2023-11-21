package bpm.fd.design.ui.edition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSetWizardPageCore;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.ParameterDescriptor;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.wizard.OdaDataSetWizard;
import bpm.fd.design.ui.wizard.pages.DataSetContentPage;
import bpm.fd.design.ui.wizard.pages.DataSourceSelectionPage;
import bpm.fd.design.ui.wizard.pages.ParameterPage;


public class EditionDataSetWizard extends OdaDataSetWizard{

	private DataSetContentPage contentPage;
	private ParameterPage paramPage;
	private List<ParameterDescriptor> originalParams;
	
	
	public EditionDataSetWizard(DataSet dataSet) throws OdaException {
		super(dataSet);
		this.dataSet = dataSet;
		originalParams = new ArrayList<ParameterDescriptor>(dataSet.getDataSetDescriptor().getParametersDescriptors());
	}

	@Override
	public void addPages() {
		if (getContainer() instanceof WizardDialog){
			((WizardDialog)getContainer()).addPageChangingListener(new IPageChangingListener(){

				public void handlePageChanging(PageChangingEvent event) {
					if (event.getTargetPage() == contentPage){
						contentPage.createViewer(dataSet);
					}
					else if (event.getTargetPage() == paramPage){
						paramPage.setInput(dataSet);
					}
					
				}
				
			});
		}
		
		selectPage = new DataSourceSelectionPage("DataSource Selection Page", dataSet); //$NON-NLS-1$
		selectPage.setTitle(Messages.EditionDataSetWizard_1);
		selectPage.setDescription(Messages.EditionDataSetWizard_2);
		
		addPage(selectPage);

		final IWizardPage p = selectPage.getNextPage();
		IWizardPage page = new WizardPage(p.getName()){

			public void createControl(Composite parent) {
				p.createControl(parent);
				setControl(p.getControl());
				
			}
			
		};
		page.setTitle(p.getTitle());
		page.setDescription(p.getDescription());
		
		addPage(page);
		
		contentPage = new DataSetContentPage("contentPage"); //$NON-NLS-1$
		contentPage.setTitle(Messages.EditionDataSetWizard_4);
		addPage(contentPage);
		
		
		paramPage = new ParameterPage("Parameters"); //$NON-NLS-1$
		paramPage.setTitle(Messages.EditionDataSetWizard_6);

		addPage(paramPage);
		
		
	}

	@Override
	public boolean performFinish() {
		
		boolean b =  super.performFinish();
		
		if (b){
			paramPage.update(getDataSet(), originalParams);
		}
		
		return b;
	}
	
	
	
	@Override
	protected DataSetDesign collectDataSetDesignFromPage(
			DataSetWizardPageCore dataSetPage) {
		
		return super.collectDataSetDesignFromPage(dataSetPage);
	}
	@Override
	protected void finishDataSetDesign() throws OdaException {
		
		super.finishDataSetDesign();
	}
	
	
}
