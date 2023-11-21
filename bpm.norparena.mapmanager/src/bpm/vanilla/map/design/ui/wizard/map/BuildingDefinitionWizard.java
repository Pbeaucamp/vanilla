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
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.platform.core.IRepositoryApi;

public class BuildingDefinitionWizard extends Wizard implements INewWizard{
	private static final String BUILDING_PAGE_NAME = Messages.BuildingDefinitionWizard_0;	
	private static final String BUILDING_PAGE_DESCRIPTION = Messages.BuildingDefinitionWizard_1;
	private static final String FLOOR_CELL_PAGE_NAME = Messages.BuildingDefinitionWizard_2;	
	private static final String FLOOR_CELL_PAGE_DESCRIPTION = Messages.BuildingDefinitionWizard_3;
	private static final String ADDRESS_SELECTION_PAGE_NAME = Messages.BuildingDefinitionWizard_4;	
	private static final String ADDRESS_SELECTION_PAGE_DESCRIPTION = Messages.BuildingDefinitionWizard_5;

	private AddressSelectionPage addressPage;
	private BuildingPage buildingPage;
	private FloorCellPage floorCellPage;
	
	private IRepositoryApi sock;
	private int repositoryId;
	
	private IBuilding building;
	private List<IAddress> addresses;
	
	private boolean edit;
	
	public BuildingDefinitionWizard() {
		super();
	}
	
	public BuildingDefinitionWizard(List<IAddress> addresses, IBuilding building, IRepositoryApi sock, int repositoryId, boolean bo) {
		super();
		this.addresses = addresses;
		this.building = building;
		this.edit = bo;
		this.sock = sock;
		this.repositoryId = repositoryId;
	}
	
	@Override
	public boolean performFinish() {		
		if(edit){
			try{
				Activator.getDefault().getDefinitionService().update(building);
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), Messages.BuildingDefinitionWizard_6, Messages.BuildingDefinitionWizard_7 + ex.getMessage());
				return false;
			}
		}
		else{
			try{
				Activator.getDefault().getDefinitionService().saveBuilding(building);
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), Messages.BuildingDefinitionWizard_8, Messages.BuildingDefinitionWizard_9 + ex.getMessage());
				return false;
			}
		}
	}

	@Override
	public void addPages() {		
		buildingPage = new BuildingPage(Messages.BuildingDefinitionWizard_10, building, sock, repositoryId, edit);
//		buildingPage = new BuildingPage("Building");		
		buildingPage.setTitle(BUILDING_PAGE_NAME);
		buildingPage.setDescription(BUILDING_PAGE_DESCRIPTION);
		addPage(buildingPage);
		
		floorCellPage = new FloorCellPage(Messages.BuildingDefinitionWizard_11, building, sock, repositoryId);
//		floorPage = new FloorPage("Floor");
		floorCellPage.setTitle(FLOOR_CELL_PAGE_NAME);
		floorCellPage.setDescription(FLOOR_CELL_PAGE_DESCRIPTION);
		addPage(floorCellPage);
		
		addressPage = new AddressSelectionPage(Messages.BuildingDefinitionWizard_12, building, addresses, true);
		addressPage.setTitle(ADDRESS_SELECTION_PAGE_NAME);
		addressPage.setDescription(ADDRESS_SELECTION_PAGE_DESCRIPTION);
		addPage(addressPage);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page instanceof BuildingPage){
			((BuildingPage)page).setBuildingInformations();
		}
		return super.getNextPage(page);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}
}
