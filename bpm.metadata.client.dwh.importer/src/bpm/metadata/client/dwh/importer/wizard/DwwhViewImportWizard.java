package bpm.metadata.client.dwh.importer.wizard;

import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import bpm.metadata.MetaData;
import bpm.model.converter.ConverterPluginManager;
import bpm.model.converter.core.IModelConverter;
import bpm.model.converter.core.IModelConverterFactory;
import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.vanilla.repository.ui.connection.DialogConnect;

public class DwwhViewImportWizard extends Wizard implements IImportWizard{

	private PageImportDwhView importPage;
	private PageMetaDataInfos infoPages;
	
	
	
	/**
	 * @param importPage
	 */
	public DwwhViewImportWizard() {
		super();
		
	}

	@Override
	public void addPages() {
		importPage = new PageImportDwhView("dwhview.importpage");
		importPage.setTitle("SqlDesigner DwhView");
		importPage.setDescription("Select a DwhView from the Vanilla Repository");
		addPage(importPage);
		
		infoPages = new PageMetaDataInfos("dwhview.infospage");
		importPage.setTitle("Vanilla MetaData Model Information");
		importPage.setDescription("Define generic properties for the FreeMetaData Model and select the groups that will be allowed to access the model items.");
		addPage(infoPages);
	}
	
	@Override
	public boolean performFinish() {
		
		try{
			DocumentSnapshot snap = ImporterHelper.loadDwhView(importPage.getSelectedItem());
			
			IModelConverterFactory modelConverterFactory = null;
			for(IModelConverterFactory f : ConverterPluginManager.getConverterImplementers()){
				
				if (f.getTargetClassName().equals(MetaData.class.getName())){
					modelConverterFactory = f;
					break;
				}
				
			}
		
			
			IModelConverter converter = modelConverterFactory.createConverter();
			
			
			Object configurationObject = infoPages.getConfigurationContext();
			
			converter.configure(configurationObject);
			MetaData model = (MetaData)converter.convert(snap);
			
			Activator.getDefault().setCurrentModel(model);
			
			return true;
		}catch(Throwable ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), "An error occured", ex.getMessage());
		}
		
		
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (Activator.getDefault().getRepositoryConnection() == null){
			DialogConnect d = new DialogConnect(getShell());
			d.open();
			
			if (Activator.getDefault().getRepositoryConnection() == null){
				performCancel();
			}
		}
		
	}

}
