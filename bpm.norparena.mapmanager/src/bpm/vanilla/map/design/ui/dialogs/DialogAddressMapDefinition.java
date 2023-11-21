package bpm.vanilla.map.design.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.viewers.MapDefinitionLabelProvider;
import bpm.norparena.mapmanager.viewers.TableColumnLabelProvider;
import bpm.norparena.mapmanager.viewers.TreeMapDefinitionContentProvider;
import bpm.norparena.mapmanager.viewers.MapDefinitionLabelProvider.MapDefinitionAttribute;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IAddressMapDefinitionRelation;
import bpm.vanilla.map.core.design.IMapDefinition;


public class DialogAddressMapDefinition extends Dialog {
	private IAddress address;
	private List<IMapDefinition> mapDefinitions; 

	private TableViewer viewerAddressMap;
	private TreeViewer mapTree;
	
	private List<IMapDefinition> actualMaps;
	private List<IMapDefinition> selectedMaps = new ArrayList<IMapDefinition>();
	private List<IMapDefinition> addedMaps = new ArrayList<IMapDefinition>();
	private List<IMapDefinition> removedMaps = new ArrayList<IMapDefinition>();
	
	public DialogAddressMapDefinition(Shell parentShell, IAddress address, List<IMapDefinition> mapDefinitions) {
		super(parentShell);
		this.address = address;
		this.mapDefinitions = mapDefinitions;
		if(address.getMaps() != null){
			this.actualMaps = address.getMaps();
		}
		else{
			this.actualMaps = new ArrayList<IMapDefinition>();
		}
		for(IMapDefinition map : actualMaps){
			this.selectedMaps.add(map);
		}
	}
	
	
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(800, 600);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		createMapPart(main);
		createMapAddressPart(main);
		
		refreshTableAddressMap();
		
		return main;
	}	
	
	private void createMapPart(Composite parent){
		Composite mapContent = new Composite(parent, SWT.NONE);
		mapContent.setLayout(new GridLayout());
		mapContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblComboMap = new Label(mapContent, SWT.NONE);
		lblComboMap.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lblComboMap.setText(Messages.DialogAddressMapDefinition_0);
		
		mapTree = new TreeViewer(mapContent, SWT.FULL_SELECTION);
		mapTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mapTree.setContentProvider(new TreeMapDefinitionContentProvider());
		mapTree.setLabelProvider(new TableColumnLabelProvider());
		mapTree.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
			}
		});
		
		buildTreeMapDefinitionColumn(mapTree);
	    
		//We set the width of the address label column bigger than the others
		mapTree.getTree().getColumn(0).setWidth(150);
		for (int i = 1, n = mapTree.getTree().getColumnCount(); i < n; i++) {
			mapTree.getTree().getColumn(i).setWidth(100);
		}

		mapTree.getTree().setHeaderVisible(true);
		mapTree.getTree().setLinesVisible(true);
		
	    mapTree.setInput(mapDefinitions);
	}
	
	public void buildTreeMapDefinitionColumn(TreeViewer mapDefinitionTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.DialogAddressMapDefinition_1);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.DialogAddressMapDefinition_2);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getDescription();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.DialogAddressMapDefinition_3);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getFusionMapObject().getName();
				return null;
			}
		});
	}
	
	private void createMapAddressPart(Composite parent){
		Composite mapsAddressContent = new Composite(parent, SWT.NONE);
		mapsAddressContent.setLayout(new GridLayout(2, false));
		mapsAddressContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createButtonPart(mapsAddressContent);
		
		viewerAddressMap = new TableViewer(mapsAddressContent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		viewerAddressMap.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewerAddressMap.setContentProvider(new ArrayContentProvider());
		viewerAddressMap.getTable().setHeaderVisible(true);
		viewerAddressMap.getTable().setLinesVisible(true);
		viewerAddressMap.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
			
		TableViewerColumn col = new TableViewerColumn(viewerAddressMap, SWT.LEFT);
		col.getColumn().setText(Messages.DialogAddressMapDefinition_4);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new MapDefinitionLabelProvider(MapDefinitionAttribute.Label));
		
		col = new TableViewerColumn(viewerAddressMap, SWT.LEFT);
		col.getColumn().setText(Messages.DialogAddressMapDefinition_5);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new MapDefinitionLabelProvider(MapDefinitionAttribute.Description));
		
		col = new TableViewerColumn(viewerAddressMap, SWT.LEFT);
		col.getColumn().setText(Messages.DialogAddressMapDefinition_6);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new MapDefinitionLabelProvider(MapDefinitionAttribute.FusionMap));
	}
	
	private void createButtonPart(Composite parent){
		Composite zonesButton = new Composite(parent, SWT.NONE);
		zonesButton.setLayout(new GridLayout());
		zonesButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		Button addZones = new Button(zonesButton, SWT.PUSH);
		addZones.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		addZones.setText(">>"); //$NON-NLS-1$
		addZones.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)mapTree.getSelection();
				
				if (ss.isEmpty()){
					return; 
				}
				
				IMapDefinition mapDefinition = (IMapDefinition)ss.getFirstElement();
				
				if(!selectedMaps.contains(mapDefinition)){
					selectedMaps.add(mapDefinition);
					boolean found = false;
					for(IMapDefinition tmp : actualMaps){
						if(tmp.getId() == mapDefinition.getId()){
							found = true;
							break;
						}
					}
					if(!found){
						addedMaps.add(mapDefinition);
					}
				}
				refreshTableAddressMap();
			}
		});		
		
		Button removeZones = new Button(zonesButton, SWT.PUSH);
		removeZones.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		removeZones.setText("<<"); //$NON-NLS-1$
		removeZones.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)viewerAddressMap.getSelection();
				if(!selection.isEmpty()){
					IMapDefinition mapDefinition = (IMapDefinition)selection.getFirstElement();
					selectedMaps.remove(mapDefinition);
					boolean found = false;
					for(IMapDefinition tmp : actualMaps){
						if(tmp.getId() == mapDefinition.getId()){
							found = true;
							break;
						}
					}
					if(found){
						removedMaps.add(mapDefinition);
					}
				}
				refreshTableAddressMap();
			}
		});
	}
	
	private void refreshTableAddressMap(){
		viewerAddressMap.setInput(selectedMaps);
	}
	
	@Override
	protected void okPressed() {
		if(!addedMaps.isEmpty()){
			for(IMapDefinition map : addedMaps){
				IAddressMapDefinitionRelation addressMapRelation;
				try {
					addressMapRelation = Activator.getDefault().getFactoryMap().createAddressMapDefinitionRelation();
					addressMapRelation.setAddressId(address.getId());
					addressMapRelation.setMapDefinitionId(map.getId());
					Activator.getDefault().getDefinitionService().saveAddressMapDefinitionRelation(addressMapRelation);
					address.addMap(map);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		if(!removedMaps.isEmpty()){
			for(IMapDefinition mapToRemove : removedMaps){
				try {
					IAddressMapDefinitionRelation addressMapRelation = 
						Activator.getDefault().getDefinitionService().getAddressMapRelationByMapIdAndAddressId(address.getId(), mapToRemove.getId());
					Activator.getDefault().getDefinitionService().delete(addressMapRelation);
					address.removeMaps(mapToRemove);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		super.okPressed();
	}
}
