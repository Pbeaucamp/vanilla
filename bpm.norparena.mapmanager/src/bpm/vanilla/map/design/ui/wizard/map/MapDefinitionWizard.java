package bpm.vanilla.map.design.ui.wizard.map;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IMapDefinition;

public class MapDefinitionWizard extends Wizard implements INewWizard{
	private static final String ADDRESS_SELECTION_PAGE_NAME = Messages.MapDefinitionWizard_0;	
	private static final String ADDRESS_SELECTION_PAGE_DESCRIPTION = Messages.MapDefinitionWizard_1;
	private static final String MAP_DEFINITION_PAGE_NAME = Messages.MapDefinitionWizard_2;	
	private static final String MAP_DEFINITION_PAGE_DESCRIPTION = Messages.MapDefinitionWizard_3;

	private AddressSelectionPage addressPage;
	private MapDefinitionPage mapDefinitionMap;
	
	private List<IAddress> addresses;
	private IMapDefinition mapDef;
	
	private boolean edit;
	
	public MapDefinitionWizard(List<IAddress> addresses, IMapDefinition mapDefinition, boolean bo) {
		super();
		this.edit = bo;
		this.mapDef = mapDefinition;
		this.addresses = addresses;
	}
	
	@Override
	public boolean performFinish() {		
		if(edit){
			try{
				Activator.getDefault().getDefinitionService().update(mapDef);
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), Messages.MapDefinitionWizard_4, Messages.MapDefinitionWizard_5 + ex.getMessage());
				return false;
			}
		}
		else{
			try{
				Activator.getDefault().getDefinitionService().saveMapDefinition(mapDef);
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), Messages.MapDefinitionWizard_6, Messages.MapDefinitionWizard_7 + ex.getMessage());
				return false;
			}
		}
	}

	@Override
	public void addPages() {
		mapDefinitionMap = new MapDefinitionPage(Messages.MapDefinitionWizard_8, mapDef, edit);
		mapDefinitionMap.setTitle(MAP_DEFINITION_PAGE_NAME);
		mapDefinitionMap.setDescription(MAP_DEFINITION_PAGE_DESCRIPTION);
		addPage(mapDefinitionMap);
		
		addressPage = new AddressSelectionPage(Messages.MapDefinitionWizard_9, mapDef, addresses, false);
		addressPage.setTitle(ADDRESS_SELECTION_PAGE_NAME);
		addressPage.setDescription(ADDRESS_SELECTION_PAGE_DESCRIPTION);
		addPage(addressPage);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
	
	
}
