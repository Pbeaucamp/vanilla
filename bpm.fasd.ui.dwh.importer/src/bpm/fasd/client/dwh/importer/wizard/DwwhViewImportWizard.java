package bpm.fasd.client.dwh.importer.wizard;



import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.fasd.olap.FAModel;
import org.freeolap.FreemetricsPlugin;

import bpm.fasd.client.dwh.importer.Messages;
import bpm.model.converter.ConverterPluginManager;
import bpm.model.converter.core.IModelConverter;
import bpm.model.converter.core.IModelConverterFactory;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.connection.DialogConnect;

public class DwwhViewImportWizard extends Wizard implements IImportWizard{

	private PageImportDwhView importPage;
	private PageMetaDataInfos infoPages;
	private DocumentSnapshot dwhView;
	
	private RepositoryItem repositoryItem;
	
	/**
	 * @param importPage
	 */
	public DwwhViewImportWizard() {
		super();
		
	}

	@Override
	public void addPages() {
		importPage = new PageImportDwhView("dwhview.importpage"); //$NON-NLS-1$
		importPage.setTitle(Messages.DwwhViewImportWizard_1);
		importPage.setDescription(Messages.DwwhViewImportWizard_2);
		addPage(importPage);
		
		infoPages = new PageMetaDataInfos("dwhview.infospage"); //$NON-NLS-1$
		importPage.setTitle(Messages.DwwhViewImportWizard_4);
		importPage.setDescription(Messages.DwwhViewImportWizard_5);
		addPage(infoPages);
	}
	
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == importPage){
			try{
				//test to avoid loosing the defined tables type
				if (repositoryItem == null || repositoryItem.getId() != importPage.getSelectedItem().getId()){
					repositoryItem = importPage.getSelectedItem();
					dwhView = ImporterHelper.loadDwhView(repositoryItem);
					infoPages.setTables(dwhView);
				}
				
				
				
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return super.getNextPage(page);
	}
	
	@Override
	public boolean performFinish() {
		
		try{
			
			
			DocumentSnapshot snap = ImporterHelper.loadDwhView(importPage.getSelectedItem());
			
			IModelConverterFactory modelConverterFactory = null;
			for(IModelConverterFactory f : ConverterPluginManager.getConverterImplementers()){
				
				if (f.getTargetClassName().equals(FAModel.class.getName())){
					modelConverterFactory = f;
					break;
				}
				
			}
		
			
			IModelConverter converter = modelConverterFactory.createConverter();
			
			
			Object configurationObject = infoPages.getConfigurationContext();
			
			converter.configure(configurationObject);
			Object model = converter.convert(snap);
			
			FreemetricsPlugin.getDefault().setCurrentModel(model);
			
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (FreemetricsPlugin.getDefault().getRepositoryConnection() == null){
			DialogConnect d = new DialogConnect(getShell());
			d.open();
			
			
		}
		
	}
	
	

}
