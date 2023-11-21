package bpm.norparena.mapmanager.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.icons.Icons;
import bpm.norparena.mapmanager.viewers.TableBuildingContentProvider;
import bpm.norparena.mapmanager.viewers.TableCellContentProvider;
import bpm.norparena.mapmanager.viewers.TableColumnLabelProvider;
import bpm.norparena.mapmanager.viewers.TableFloorContentProvider;
import bpm.norparena.mapmanager.viewers.TableLabelProvider;
import bpm.norparena.mapmanager.viewers.TreeAddressContentProvider;
import bpm.norparena.mapmanager.viewers.TreeMapDefinitionContentProvider;
import bpm.norparena.mapmanager.views.mapview.CompositeMapView;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IBuildingFloor;
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.design.ui.dialogs.DialogAddOpenGisMap;
import bpm.vanilla.map.design.ui.dialogs.DialogAddress;
import bpm.vanilla.map.design.ui.dialogs.DialogAddressMapDefinition;
import bpm.vanilla.map.design.ui.dialogs.DialogAddressZone;
import bpm.vanilla.map.design.ui.dialogs.DialogBrowseKml;
import bpm.vanilla.map.design.ui.dialogs.DialogCell;
import bpm.vanilla.map.design.ui.dialogs.DialogFloor;
import bpm.vanilla.map.design.ui.dialogs.DialogMapDefinition;
import bpm.vanilla.map.design.ui.dialogs.DialogMapType;
import bpm.vanilla.map.design.ui.dialogs.DialogOpenLayersMap;
import bpm.vanilla.map.design.ui.dialogs.DialogZoneTerritoryMapping;
import bpm.vanilla.map.design.ui.wizard.map.BuildingDefinitionWizard;
import bpm.vanilla.platform.core.IRepositoryApi;

public class MapView extends ViewPart{
	public static final String ID = "bpm.norparena.mapmanager.views.MapView"; //$NON-NLS-1$
	
	private Button addAddress, addBuilding, addFloor, addCell, addMapDef, addOlMapDef;
	private Button deleteAddress, deleteBuilding, deleteFloor, deleteCell, deleteMapDef;
	private Button updateAddress, updateBuilding, updateFloor, updateCell, updateMapDef;
	private Button browseMapDef;
	private Button createZoneTerritoryMapping;
	private Button associatedZoneAddress;
	private Button associatedMapDefinitionAddress;

	private Action refreshAction;
	
	private IRepositoryApi sock;
	private int repositoryId;
	
	private TreeViewer addressTree;
	private TableViewer buildingTable, floorTable, cellTable;
	private TreeViewer mapTree;
	private CompositeMapView compositeFusionMap;
	private List<IAddress> addresses;
	private List<IBuilding> buildings;
	private List<IMapDefinition> mapDefinitions;
	
//	protected Text filterNameAddress;
	protected Text filterNameBuilding;
//	protected Text filterNameMapDefinition;
	
	private Shell shell;
	
	public MapView() {
		//We load the plugin bpm.vanilla.map.dao

		
		this.sock = Activator.getDefault().getSocket();
		this.repositoryId = bpm.norparena.ui.menu.Activator.getDefault().getRepositoryContext().getRepository().getId();
		this.shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
		try {
			this.buildings = Activator.getDefault().getDefinitionService().getAllBuilding();
			this.addresses = Activator.getDefault().getDefinitionService().getAddressParent();
			this.mapDefinitions = Activator.getDefault().getDefinitionService().getMapDefinitionParent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createContent(container);
	}

	@Override
	public void setFocus() {
		
	}
	
	private void createContent(Composite parent) {
		refreshAction = new Action() {
			public void run() {
				try {
					refreshPartAddress();
					refreshPartBuilding();
					refreshPartMapDefinition();
				} catch (Exception e) {
					MessageDialog.openError(shell, Messages.MapView_1, Messages.MapView_2 +  e.getMessage());
					e.printStackTrace();
				}
			}
		};
		refreshAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Icons.REFRESH));
		refreshAction.setToolTipText(Messages.MapView_3);
		
		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(refreshAction);
		
		TabFolder folder = new TabFolder(parent, SWT.NONE);
//		folder.setLayout(new GridLayout(2, false));
		folder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		Composite addressContent = new Composite(folder, SWT.NONE);
		addressContent.setLayout(new GridLayout(7, false));
		addressContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setAddressContent(addressContent);
		
		TabItem address = new TabItem(folder, SWT.BORDER);
		address.setText(Messages.MapView_4);
		address.setControl(addressContent);
		
		Composite buildingContent = new Composite(folder, SWT.NONE);
		buildingContent.setLayout(new GridLayout(3, true));
		buildingContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Composite buildingPart = new Composite(buildingContent, SWT.NONE);
		buildingPart.setLayout(new GridLayout(5, false));
		buildingPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setBuildingPartContent(buildingPart);
		
		Composite floorPart = new Composite(buildingContent, SWT.NONE);
		floorPart.setLayout(new GridLayout(3, false));
		floorPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setFloorPartContent(floorPart);
		
		Composite cellPart = new Composite(buildingContent, SWT.NONE);
		cellPart.setLayout(new GridLayout(3, false));
		cellPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setCellPartContent(cellPart);
		
		TabItem building = new TabItem(folder, SWT.BORDER);
		building.setText(Messages.MapView_5);
		building.setControl(buildingContent);

		Composite mapContent = new Composite(folder, SWT.NONE);
		mapContent.setLayout(new GridLayout(2, false));
		mapContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setMapsContent(mapContent);
		
		TabItem map = new TabItem(folder, SWT.BORDER);
		map.setText(Messages.MapView_6);
		map.setControl(mapContent);
		
		//We set the building input
		addressTree.setInput(addresses);
		buildingTable.setInput(buildings);
		mapTree.setInput(mapDefinitions);
	}
	
	private void setAddressContent(Composite addressContent){
		addAddress = new Button(addressContent, SWT.PUSH);
		addAddress.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		addAddress.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		addAddress.setToolTipText(Messages.MapView_7);
		addAddress.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IAddress address = null;
				try {
					address = Activator.getDefault().getFactoryMap().createAdress();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
				DialogAddress dialAddress = new DialogAddress(shell, addresses, address, false);
				
				if(dialAddress.open() == Dialog.OK){
					try{
						Activator.getDefault().getDefinitionService().saveAddress(address);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(shell, Messages.MapView_8, Messages.MapView_9 + ex.getMessage());
					}
					
					refreshPartAddress();
				}				
			}
		});
		
		updateAddress = new Button(addressContent, SWT.PUSH);
		updateAddress.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		updateAddress.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT));
		updateAddress.setToolTipText(Messages.MapView_10);
		updateAddress.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)addressTree.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IAddress address = (IAddress)ss.getFirstElement();
				
				DialogAddress dialAddress = new DialogAddress(shell, addresses, address, true);
				
				if(dialAddress.open() == Dialog.OK){
					try{
						Activator.getDefault().getDefinitionService().update(address);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(shell, Messages.MapView_11, Messages.MapView_12 + ex.getMessage());
					}
					
					refreshPartAddress();
				}

			}
		});
		
		associatedMapDefinitionAddress = new Button(addressContent, SWT.PUSH);
		associatedMapDefinitionAddress.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		associatedMapDefinitionAddress.setImage(Activator.getDefault().getImageRegistry().get(Icons.ASSOCIATED_ADDRESS_TO_MAP));
		associatedMapDefinitionAddress.setToolTipText(Messages.MapView_13);
		associatedMapDefinitionAddress.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)addressTree.getSelection();
				if (ss.isEmpty()){
					return; 
				}
				IAddress address = (IAddress)ss.getFirstElement();
				
				DialogAddressMapDefinition dialAddressMapDef = new DialogAddressMapDefinition(shell, address, mapDefinitions);
				if(dialAddressMapDef.open() == Dialog.OK){
					
				}

			}
		});
		
		associatedZoneAddress = new Button(addressContent, SWT.PUSH);
		associatedZoneAddress.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		associatedZoneAddress.setImage(Activator.getDefault().getImageRegistry().get(Icons.ASSOCIATED_ADDRESS_ZONE));
		associatedZoneAddress.setToolTipText(Messages.MapView_14);
		associatedZoneAddress.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)addressTree.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IAddress address = (IAddress)ss.getFirstElement();
				
				DialogAddressZone dialAddressZone = new DialogAddressZone(shell, address, mapDefinitions);
				
				if(dialAddressZone.open() == Dialog.OK){
					
				}

			}
		});
		
		deleteAddress = new Button(addressContent, SWT.PUSH);
		deleteAddress.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		deleteAddress.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		deleteAddress.setToolTipText(Messages.MapView_15);
		deleteAddress.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)addressTree.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IAddress address = (IAddress)ss.getFirstElement();

				try{
					Activator.getDefault().getDefinitionService().delete(address);
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(shell, Messages.MapView_16, Messages.MapView_17 + ex.getMessage());
				}
				
				refreshPartAddress();

			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
//		Label filterNameLabel = new Label(addressContent, SWT.NONE);
//		filterNameLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		filterNameLabel.setText("Filter on Label: ");
		
//		filterNameAddress = new Text(addressContent, SWT.BORDER);
//		filterNameAddress.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		filterNameAddress.addModifyListener(new ModifyListener(){
//
//			public void modifyText(ModifyEvent e) {
//				for(ViewerFilter f : addressTree.getFilters()){
//					if (f instanceof NameViewerFilter){
//						((NameViewerFilter)f).name = new String(filterNameAddress.getText());
//						addressTree.refresh();
//						return;
//						
//					}
//				}
//			}
//			
//		});
		
		createTreeAddress(addressContent);
	}
	
	public void createTreeAddress(Composite addressContent){
		addressTree = new TreeViewer(addressContent, SWT.FULL_SELECTION);
		addressTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
		addressTree.setContentProvider(new TreeAddressContentProvider());
//		addressTree.setLabelProvider(new TableColumnLabelProvider());
		
	    //We add a filter on the name
//		NameViewerFilter vf = new NameViewerFilter(filterNameAddress.getText());
//		addressTree.addFilter(vf);
		
		buildTreeAddressColumn(addressTree);

	    //We set the width of the address label column bigger than the others
	    addressTree.getTree().getColumn(0).setWidth(150);
		for (int i = 1, n = addressTree.getTree().getColumnCount(); i < n; i++) {
			addressTree.getTree().getColumn(i).setWidth(100);
		}
	    
		addressTree.getTree().setHeaderVisible(true);
		addressTree.getTree().setLinesVisible(true);
	}
	
	public void buildTreeAddressColumn(TreeViewer addressTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(addressTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.MapView_18);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(addressTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.MapView_19);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getAddressType();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(addressTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.MapView_20);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getBloc();
				return null;
			}
		});
		
		TreeViewerColumn columnArrondissement = new TreeViewerColumn(addressTree, SWT.NONE);
		columnArrondissement.getColumn().setText(Messages.MapView_21);
		columnArrondissement.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getArrondissement();
				return null;
			}
		});
		
		TreeViewerColumn columnStreet1 = new TreeViewerColumn(addressTree, SWT.NONE);
		columnStreet1.getColumn().setText(Messages.MapView_22);
		columnStreet1.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getStreet1();
				return null;
			}
		});
		
		TreeViewerColumn columnStreet2 = new TreeViewerColumn(addressTree, SWT.NONE);
		columnStreet2.getColumn().setText(Messages.MapView_23);
		columnStreet2.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getStreet2();
				return null;
			}
		});
		
		TreeViewerColumn columnZipCode = new TreeViewerColumn(addressTree, SWT.NONE);
		columnZipCode.getColumn().setText(Messages.MapView_24);
		columnZipCode.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return String.valueOf(((IAddress)element).getZipCode());
				return null;
			}
		});
		
		TreeViewerColumn columnInseeCode = new TreeViewerColumn(addressTree, SWT.NONE);
		columnInseeCode.getColumn().setText(Messages.MapView_25);
		columnInseeCode.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return String.valueOf(((IAddress)element).getINSEECode());
				return null;
			}
		});
		
		TreeViewerColumn columnCity = new TreeViewerColumn(addressTree, SWT.NONE);
		columnCity.getColumn().setText(Messages.MapView_26);
		columnCity.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getCity();
				return null;
			}
		});
		
		TreeViewerColumn columnCountry = new TreeViewerColumn(addressTree, SWT.NONE);
		columnCountry.getColumn().setText(Messages.MapView_27);
		columnCountry.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IAddress)
					return ((IAddress)element).getCountry();
				return null;
			}
		});
	}
	
	private void setBuildingPartContent(Composite buildingPart){
		addBuilding = new Button(buildingPart, SWT.PUSH);
		addBuilding.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		addBuilding.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		addBuilding.setToolTipText(Messages.MapView_28);
		addBuilding.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IBuilding building = null;
				try {
					building = Activator.getDefault().getFactoryMap().createBuilding();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				BuildingDefinitionWizard wiz = new BuildingDefinitionWizard(addresses, building, sock, repositoryId, false);
				WizardDialog dial = new WizardDialog(shell, wiz);
				dial.setMinimumPageSize(600, 400);
				dial.create();
			
				if (dial.open() == Dialog.OK) {
					refreshPartBuilding();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		updateBuilding = new Button(buildingPart, SWT.PUSH);
		updateBuilding.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		updateBuilding.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT));
		updateBuilding.setToolTipText(Messages.MapView_29);
		updateBuilding.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)buildingTable.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)ss.getFirstElement();
				
				BuildingDefinitionWizard wiz = new BuildingDefinitionWizard(addresses, building, sock, repositoryId, true);
				WizardDialog dial = new WizardDialog(shell, wiz);
				dial.setMinimumPageSize(600, 400);
				dial.create();
			
				if (dial.open() == Dialog.OK) {
					refreshPartBuilding();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		deleteBuilding = new Button(buildingPart, SWT.PUSH);
		deleteBuilding.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		deleteBuilding.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		deleteBuilding.setToolTipText(Messages.MapView_30);
		deleteBuilding.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)buildingTable.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)ss.getFirstElement();

				try {
					Activator.getDefault().getDefinitionService().delete(building);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				refreshPartBuilding();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		Label filterNameLabel = new Label(buildingPart, SWT.NONE);
		filterNameLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		filterNameLabel.setText(Messages.MapView_31);
		
		filterNameBuilding = new Text(buildingPart, SWT.BORDER);
		filterNameBuilding.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		filterNameBuilding.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				for(ViewerFilter f : buildingTable.getFilters()){
					if (f instanceof NameViewerFilter){
						((NameViewerFilter)f).name = new String(filterNameBuilding.getText());
						buildingTable.refresh();
						return;
						
					}
				}
			}
			
		});
		
		
		buildingTable = new TableViewer(buildingPart);
		buildingTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
		buildingTable.setContentProvider(new TableBuildingContentProvider());
		buildingTable.setLabelProvider(new TableLabelProvider());
		buildingTable.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)buildingTable.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)ss.getFirstElement();
				
				floorTable.setInput(building.getFloors());
				cellTable.setInput(building.getCells());
				
			}
		});
		

	    //We add a filter on the name
		NameViewerFilter vf = new NameViewerFilter(filterNameBuilding.getText());
		buildingTable.addFilter(vf);
	}
	
	private void setFloorPartContent(Composite floorPart){
		addFloor = new Button(floorPart, SWT.PUSH);
		addFloor.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		addFloor.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		addFloor.setToolTipText(Messages.MapView_32);
		addFloor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sbuilding = (IStructuredSelection)buildingTable.getSelection();
				
				if (sbuilding.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)sbuilding.getFirstElement();
				IBuildingFloor floor = null;
				try {
					floor = Activator.getDefault().getFactoryMap().createFloor();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
//				floor.setLabel("");
//				floor.setBuildingId(building.getId());
				
				DialogFloor dialFloor = new DialogFloor(shell, building, floor, sock, repositoryId, false);
				
				if (dialFloor.open() == Dialog.OK) {
					try{
						Activator.getDefault().getDefinitionService().update(building);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(shell, Messages.MapView_33, Messages.MapView_34 + ex.getMessage());
					}
					
					refreshTableFloor(building);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		updateFloor = new Button(floorPart, SWT.PUSH);
		updateFloor.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		updateFloor.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT));
		updateFloor.setToolTipText(Messages.MapView_35);
		updateFloor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sbuilding = (IStructuredSelection)buildingTable.getSelection();
				IStructuredSelection sfloor = (IStructuredSelection)floorTable.getSelection();
				
				if (sbuilding.isEmpty() && sfloor.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)sbuilding.getFirstElement();
				IBuildingFloor floor = (IBuildingFloor)sfloor.getFirstElement();
				
				DialogFloor dialFloor = new DialogFloor(shell, building, floor, sock, repositoryId, true);
				
				if (dialFloor.open() == Dialog.OK) {
					try{
						Activator.getDefault().getDefinitionService().update(building);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(shell, Messages.MapView_36, Messages.MapView_37 + ex.getMessage());
					}
					
					refreshPartBuilding();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		deleteFloor = new Button(floorPart, SWT.PUSH);
		deleteFloor.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		deleteFloor.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		deleteFloor.setToolTipText(Messages.MapView_38);
		deleteFloor.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sbuilding = (IStructuredSelection)buildingTable.getSelection();
				IStructuredSelection ss = (IStructuredSelection)floorTable.getSelection();
				
				if (sbuilding.isEmpty() && ss.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)sbuilding.getFirstElement();
				IBuildingFloor floor = (IBuildingFloor)ss.getFirstElement();
				
				building.removeFloor(floor);

				try {
					Activator.getDefault().getDefinitionService().update(building);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				refreshPartBuilding();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		floorTable = new TableViewer(floorPart);
		floorTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		floorTable.setContentProvider(new TableFloorContentProvider());
		floorTable.setLabelProvider(new TableLabelProvider());
		floorTable.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)floorTable.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IBuildingFloor floor = (IBuildingFloor)ss.getFirstElement();
				
				cellTable.setInput(floor.getCells());
				
			}
		});
	}
	
	private void setCellPartContent(Composite cellPart){
		addCell = new Button(cellPart, SWT.PUSH);
		addCell.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		addCell.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		addCell.setToolTipText(Messages.MapView_39);
		addCell.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sbuilding = (IStructuredSelection)buildingTable.getSelection();
				IStructuredSelection sfloor = (IStructuredSelection)floorTable.getSelection();
				
				if (sfloor.isEmpty() && sbuilding.isEmpty()){
					return; 
				}

				IBuildingFloor floor = (IBuildingFloor)sfloor.getFirstElement();
				IBuilding building = (IBuilding)sbuilding.getFirstElement();
				
				ICell cell = null;
				try {
					cell = Activator.getDefault().getFactoryMap().createCell();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				cell.setLabel(""); //$NON-NLS-1$
				cell.setFloorId(floor.getId());
				cell.setBuildingId(building.getId());
				
				DialogCell dialCell = new DialogCell(shell, building, floor, cell, sock, repositoryId, false);
				
				if (dialCell.open() == Dialog.OK) {
					try{
						Activator.getDefault().getDefinitionService().update(building);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(shell, Messages.MapView_41, Messages.MapView_42 + ex.getMessage());
					}
					
					refreshTableCell(floor);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		updateCell = new Button(cellPart, SWT.PUSH);
		updateCell.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		updateCell.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT));
		updateCell.setToolTipText(Messages.MapView_43);
		updateCell.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sbuilding = (IStructuredSelection)buildingTable.getSelection();
				IStructuredSelection sfloor = (IStructuredSelection)floorTable.getSelection();
				IStructuredSelection sCell = (IStructuredSelection)cellTable.getSelection();
				
				if (sbuilding.isEmpty() && sfloor.isEmpty() && sCell.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)sbuilding.getFirstElement();
				IBuildingFloor floor = (IBuildingFloor)sfloor.getFirstElement();
				ICell cell = (ICell)sCell.getFirstElement();
				
				DialogCell dialCell = new DialogCell(shell, building, floor, cell, sock, repositoryId, true);
				
				if (dialCell.open() == Dialog.OK) {
					try{
						Activator.getDefault().getDefinitionService().update(building);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(shell, Messages.MapView_44, Messages.MapView_45 + ex.getMessage());
					}
					
					refreshPartBuilding();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		deleteCell = new Button(cellPart, SWT.PUSH);
		deleteCell.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		deleteCell.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		deleteCell.setToolTipText(Messages.MapView_46);
		deleteCell.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sbuilding = (IStructuredSelection)buildingTable.getSelection();
				IStructuredSelection sfloor = (IStructuredSelection)floorTable.getSelection();
				IStructuredSelection sCell = (IStructuredSelection)cellTable.getSelection();
				
				if (sbuilding.isEmpty() && sCell.isEmpty()){
					return; 
				}
				
				IBuilding building = (IBuilding)sbuilding.getFirstElement();
				ICell cell = (ICell)sCell.getFirstElement();


				try {
					IBuildingFloor floor;
					if(sfloor.isEmpty()){
						floor = Activator.getDefault().getDefinitionService().getFloor(cell.getFloorId());
					}
					else{
						floor = (IBuildingFloor)sfloor.getFirstElement();
					}
					
					building.removeCell(cell);
					floor.removeCell(cell);
					Activator.getDefault().getDefinitionService().update(building);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(shell, Messages.MapView_47, Messages.MapView_48 + e1.getMessage());
				}
				
				refreshPartBuilding();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		cellTable = new TableViewer(cellPart);
		cellTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		cellTable.setContentProvider(new TableCellContentProvider());
		cellTable.setLabelProvider(new TableLabelProvider());
	}
	
	private void setMapsContent(Composite mapContent){
		
		Composite buttonBar = new Composite(mapContent, SWT.NONE);
		buttonBar.setLayout(new GridLayout(6, false));
		buttonBar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		
		addMapDef = new Button(buttonBar, SWT.PUSH);
		addMapDef.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		addMapDef.setImage(Activator.getDefault().getImageRegistry().get(Icons.CREATE_MAP));
		addMapDef.setToolTipText(Messages.MapView_49);
		addMapDef.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IMapDefinition mapDef = null;
				try {
					mapDef = Activator.getDefault().getFactoryMap().createMapDefinition();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				DialogMapType dial = new DialogMapType(shell);
				if(dial.open() == Dialog.OK) {
					if(dial.getMapType().equals(Messages.MapView_50)) {
						DialogMapDefinition dialMapDefinition = new DialogMapDefinition(shell, mapDef, mapDefinitions, false);
						
						if (dialMapDefinition.open() == Dialog.OK) {
							refreshPartMapDefinition();
						}
					}
					else if(dial.getMapType().equals("OpenGis")) {
						DialogAddOpenGisMap dialog = new DialogAddOpenGisMap(shell);
						if(dialog.open() == Dialog.OK) {
							refreshPartMapDefinition();
						}
					}
					else {
						DialogOpenLayersMap dialMapDefinition = new DialogOpenLayersMap(shell, mapDef, false);
						
						if (dialMapDefinition.open() == Dialog.OK) {
							refreshPartMapDefinition();				
						}
					}
				}
				
				
			}
		});
		
//		addOlMapDef = new Button(buttonBar, SWT.PUSH);
//		addOlMapDef.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		addOlMapDef.setImage(Activator.getDefault().getImageRegistry().get(Icons.CREATE_MAP));
//		addOlMapDef.setToolTipText("Add OpenLayers Map");
//		addOlMapDef.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				IMapDefinition mapDef = null;
//				try {
//					mapDef = Activator.getDefault().getFactoryMap().createMapDefinition();
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				
//				DialogOpenLayersMap dialMapDefinition = new DialogOpenLayersMap(shell, mapDef, false);
//				
//				if (dialMapDefinition.open() == Dialog.OK) {
//					refreshPartMapDefinition();				
//				}
//			}
//		});
		
		updateMapDef = new Button(buttonBar, SWT.PUSH);
		updateMapDef.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		updateMapDef.setImage(Activator.getDefault().getImageRegistry().get(Icons.UPDATE_MAP));
		updateMapDef.setToolTipText(Messages.MapView_51);
		updateMapDef.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection smap = (IStructuredSelection)mapTree.getSelection();
				
				if (smap.isEmpty()){
					return; 
				}
				
				IMapDefinition mapDef = (IMapDefinition)smap.getFirstElement();
				
				if(mapDef.getFusionMapObjectId() != null) {

					DialogMapDefinition dialMapDefinition = new DialogMapDefinition(shell, mapDef, mapDefinitions, true);
					
					if (dialMapDefinition.open() == Dialog.OK) {
						refreshPartMapDefinition();				
					}
				}
				
				else if(mapDef.getOpenLayersObjectId() != null) {
					DialogOpenLayersMap dial = new DialogOpenLayersMap(shell, mapDef, true);
					if(dial.open() == Dialog.OK) {
						refreshPartMapDefinition();
					}
				}
			}
		});
		
		browseMapDef = new Button(buttonBar, SWT.PUSH);
		browseMapDef.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		browseMapDef.setImage(Activator.getDefault().getImageRegistry().get(Icons.BROWSE));
		browseMapDef.setToolTipText(Messages.MapView_52);
		browseMapDef.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection smap = (IStructuredSelection)mapTree.getSelection();
				
				if (smap.isEmpty()){
					return; 
				}
				
				IMapDefinition mapDef = (IMapDefinition)smap.getFirstElement();
				
				if(mapDef.getFusionMapObject() != null && mapDef.getKmlObject() == null){
					DialogBrowseKml dialBrowse = new DialogBrowseKml(shell, mapDef, false);
					if(dialBrowse.open() == Dialog.OK){
						
					}
				}
				else if(mapDef.getFusionMapObject() == null && mapDef.getKmlObject() != null){
					DialogBrowseKml dialBrowse = new DialogBrowseKml(shell, mapDef, true);
					if(dialBrowse.open() == Dialog.OK){
						
					}
				}
				else if(mapDef.getFusionMapObject() != null && mapDef.getKmlObject() != null){
					
				}
				else{
					MessageDialog.openError(shell, Messages.MapView_53, Messages.MapView_54);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		deleteMapDef = new Button(buttonBar, SWT.PUSH);
		deleteMapDef.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		deleteMapDef.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		deleteMapDef.setToolTipText(Messages.MapView_55);
		deleteMapDef.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection smap = (IStructuredSelection)mapTree.getSelection();
				
				if (smap.isEmpty()){
					return; 
				}
				
				IMapDefinition mapDef = (IMapDefinition)smap.getFirstElement();
					
				try{
					Activator.getDefault().getDefinitionService().delete(mapDef);
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(shell, Messages.MapView_56, Messages.MapView_57 + e1.getMessage());
				}
				
				refreshPartMapDefinition();
			}
		});
		
		createZoneTerritoryMapping = new Button(buttonBar, SWT.PUSH);
		createZoneTerritoryMapping.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		createZoneTerritoryMapping.setImage(Activator.getDefault().getImageRegistry().get(Icons.ZONE_TERRITORY_MAPPING));
		createZoneTerritoryMapping.setToolTipText(Messages.MapView_58);
		createZoneTerritoryMapping.setEnabled(false);
		createZoneTerritoryMapping.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection smap = (IStructuredSelection)mapTree.getSelection();
				
				if (smap.isEmpty()){
					return; 
				}
				
				IMapDefinition mapDef = (IMapDefinition)smap.getFirstElement();
					
				if(mapDef.getMapType() != null && mapDef.getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
					try {
						DialogZoneTerritoryMapping dial = new DialogZoneTerritoryMapping(shell, mapDef);
						dial.open();
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
				}
			}
		});
		
		
		
//		Label filterNameLabel = new Label(buttonBar, SWT.NONE);
//		filterNameLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		filterNameLabel.setText("Filter on Label: ");
//		
//		filterNameMapDefinition = new Text(buttonBar, SWT.BORDER);
//		filterNameMapDefinition.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		filterNameMapDefinition.addModifyListener(new ModifyListener(){
//
//			public void modifyText(ModifyEvent e) {
//				for(ViewerFilter f : mapTree.getFilters()){
//					if (f instanceof NameViewerFilter){
//						((NameViewerFilter)f).name = new String(filterNameMapDefinition.getText());
//						mapTree.refresh();
//						return;
//					}
//				}
//			}
//			
//		});
		
		createTableMapDefinition(mapContent);
	}
	
	public void createTableMapDefinition(Composite mapContent){
		mapTree = new TreeViewer(mapContent, SWT.FULL_SELECTION);
		mapTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mapTree.setContentProvider(new TreeMapDefinitionContentProvider());
		mapTree.setLabelProvider(new TableColumnLabelProvider());
		
//		mapTable = new TableViewer(mapContent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
//		mapTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		mapTable.setContentProvider(new TableMapDefinitionContentProvider());
//		ITableLabelProvider labelProvider = new TableColumnLabelProvider();
//		mapTable.setLabelProvider(labelProvider);
//		mapTable.setSorter(new TableViewerSorter(labelProvider));
		mapTree.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (mapTree.getSelection().isEmpty()){
					compositeFusionMap.setInput(null);
				}
				else{
					compositeFusionMap.setInput(((IMapDefinition)((IStructuredSelection)mapTree.getSelection()).getFirstElement()));
					if(((IMapDefinition)((IStructuredSelection)mapTree.getSelection()).getFirstElement()).getMapType() != null && ((IMapDefinition)((IStructuredSelection)mapTree.getSelection()).getFirstElement()).getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
						createZoneTerritoryMapping.setEnabled(true);
					}
					else {
						createZoneTerritoryMapping.setEnabled(false);
					}
				}

			}
		});
	    
	    //We add a filter on the name
//		NameViewerFilter vf = new NameViewerFilter(filterNameAddress.getText());
//		mapTree.addFilter(vf);
	    
		buildTreeMapDefinitionColumn(mapTree);
	    
		//We set the width of the address label column bigger than the others
		mapTree.getTree().getColumn(0).setWidth(150);
		for (int i = 1, n = mapTree.getTree().getColumnCount(); i < n; i++) {
			mapTree.getTree().getColumn(i).setWidth(100);
		}

		mapTree.getTree().setHeaderVisible(true);
		mapTree.getTree().setLinesVisible(true);
	    
	    compositeFusionMap = new CompositeMapView();
	    compositeFusionMap.createContent(mapContent).setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	}
	
	public void buildTreeMapDefinitionColumn(TreeViewer mapDefinitionTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.MapView_59);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.MapView_60);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getDescription();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.MapView_61);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					if(((IMapDefinition)element).getFusionMapObject() != null) {
						return ((IMapDefinition)element).getFusionMapObject().getName();
					}
					else if(((IMapDefinition)element).getOpenLayersMapObject() != null) {
						return ((IMapDefinition)element).getOpenLayersMapObject().getName();
					}
				return null;
			}
		});
		
		TreeViewerColumn columnType = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnType.getColumn().setText(Messages.MapView_62);
		columnType.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition) {
					IMapDefinition map = (IMapDefinition) element;
					if(map.getFusionMapObject() != null) {
						return Messages.MapView_63;
					}
					else if(map.getOpenLayersMapObject() != null) {
						return Messages.MapView_64;
					}
				}
				return null;
			}
		});
	}
	
	private void refreshPartAddress(){
		try {
			addresses = Activator.getDefault().getDefinitionService().getAddressParent();
			addressTree.setInput(addresses);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void refreshPartBuilding(){
		try{
			buildings = Activator.getDefault().getDefinitionService().getAllBuilding();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		buildingTable.setInput(buildings);
		floorTable.setInput(new ArrayList<IBuildingFloor>());	
		cellTable.setInput(new ArrayList<ICell>());
	}
	
	private void refreshPartMapDefinition(){
		try{
			mapDefinitions = Activator.getDefault().getDefinitionService().getMapDefinitionParent();
			mapTree.setInput(mapDefinitions);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void refreshTableFloor(IBuilding building){
		floorTable.setInput(building.getFloors());		
	}
	
	private void refreshTableCell(IBuildingFloor floor){
		cellTable.setInput(floor.getCells());		
	}
	
	public class NameViewerFilter extends ViewerFilter{

		private String name = ""; //$NON-NLS-1$
		
		public NameViewerFilter(String name){
			this.name = name;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (element instanceof IAddress){
				return ((IAddress)element).getLabel()!= null && ((IAddress)element).getLabel().startsWith(name);
			}
			else if (element instanceof IBuilding){
				return ((IBuilding)element).getLabel().startsWith(name);
			}
			else if (element instanceof IMapDefinition){
				return ((IMapDefinition)element).getLabel().startsWith(name);
			}
			return true;
		}
		
	}
}
