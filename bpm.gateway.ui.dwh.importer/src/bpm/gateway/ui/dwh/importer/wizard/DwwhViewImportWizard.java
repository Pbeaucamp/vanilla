package bpm.gateway.ui.dwh.importer.wizard;



import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import bpm.gateway.core.Server;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.dwhview.DwhDbConnection;
import bpm.gateway.ui.Activator;
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
		importPage.setDescription("Define generic properties for the Metadata Model and select the groups that will be allowed to access the model items.");
		addPage(infoPages);
	}
	
	@Override
	public boolean performFinish() {
		
		try{
			DocumentSnapshot snap = ImporterHelper.loadDwhView(importPage.getSelectedItem());
			
			IModelConverterFactory modelConverterFactory = null;
			for(IModelConverterFactory f : ConverterPluginManager.getConverterImplementers()){
				
				if (f.getTargetClassName().equals(DwhDbConnection.class.getName())){
					modelConverterFactory = f;
					break;
				}
				
			}
		
			
			IModelConverter converter = modelConverterFactory.createConverter();
			
			
			Object configurationObject = infoPages.getConfigurationContext();
			
			converter.configure(configurationObject);
			Server model = (Server)converter.convert(snap);
			
			ResourceManager.getInstance().addServer(model);
			
			ResourceManager.getInstance().fireContentChange();
			
			
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
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
