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
import bpm.norparena.mapmanager.fusionmap.viewers.FusionMapEntityLabelProvider;
import bpm.norparena.mapmanager.fusionmap.viewers.FusionMapEntityLabelProvider.FusionMapEntityAttribute;
import bpm.norparena.mapmanager.viewers.TableColumnLabelProvider;
import bpm.norparena.mapmanager.viewers.TreeMapDefinitionContentProvider;
import bpm.norparena.mapmanager.views.mapview.CompositeMapView;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IAddressZone;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;


public class DialogAddressZone extends Dialog {
	private IAddress address;
	private List<IMapDefinition> mapDefinitions; 

	private TableViewer viewerAddressZone;
	private TreeViewer mapTree;
	private CompositeMapView compositeFusionMap;
	
	private List<IFusionMapSpecificationEntity> actualZones;
	private List<IFusionMapSpecificationEntity> selectedZones = new ArrayList<IFusionMapSpecificationEntity>();
	private List<IFusionMapSpecificationEntity> addedZones = new ArrayList<IFusionMapSpecificationEntity>();
	private List<IFusionMapSpecificationEntity> removedZones = new ArrayList<IFusionMapSpecificationEntity>();
	
	public DialogAddressZone(Shell parentShell, IAddress address, List<IMapDefinition> mapDefinitions) {
		super(parentShell);
		this.address = address;
		this.mapDefinitions = mapDefinitions;
		if(address.getAddressZones() != null){
			this.actualZones = address.getAddressZones();
		}
		else{
			this.actualZones = new ArrayList<IFusionMapSpecificationEntity>();
		}
		for(IFusionMapSpecificationEntity entity : actualZones){
			this.selectedZones.add(entity);
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
		
		createZonesPart(main);
		createZonesAddressPart(main);
		
		refreshTableAddressZone();
		
		return main;
	}	
	
	private void createZonesPart(Composite parent){
		Composite zonesContent = new Composite(parent, SWT.NONE);
		zonesContent.setLayout(new GridLayout());
		zonesContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label lblComboMap = new Label(zonesContent, SWT.NONE);
		lblComboMap.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		lblComboMap.setText(Messages.DialogAddressZone_0);
		
		mapTree = new TreeViewer(zonesContent, SWT.FULL_SELECTION);
		mapTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mapTree.setContentProvider(new TreeMapDefinitionContentProvider());
		mapTree.setLabelProvider(new TableColumnLabelProvider());
		mapTree.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (mapTree.getSelection().isEmpty()){
					compositeFusionMap.setInput(null);
				}
				else{
					compositeFusionMap.setInput(((IMapDefinition)((IStructuredSelection)mapTree.getSelection()).getFirstElement()));
				}

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
		
	    compositeFusionMap = new CompositeMapView();
	    compositeFusionMap.createContent(zonesContent).setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	
	    mapTree.setInput(mapDefinitions);
	}
	
	public void buildTreeMapDefinitionColumn(TreeViewer mapDefinitionTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.DialogAddressZone_1);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.DialogAddressZone_2);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getDescription();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.DialogAddressZone_3);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getFusionMapObject().getName();
				return null;
			}
		});
	}
	
	private void createZonesAddressPart(Composite parent){
		Composite zonesAddressContent = new Composite(parent, SWT.NONE);
		zonesAddressContent.setLayout(new GridLayout(2, false));
		zonesAddressContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createButtonPart(zonesAddressContent);
		
		viewerAddressZone = new TableViewer(zonesAddressContent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		viewerAddressZone.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewerAddressZone.setContentProvider(new ArrayContentProvider());
		viewerAddressZone.getTable().setHeaderVisible(true);
		viewerAddressZone.getTable().setLinesVisible(true);
		viewerAddressZone.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
			
		TableViewerColumn col = new TableViewerColumn(viewerAddressZone, SWT.LEFT);
		col.getColumn().setText(Messages.DialogAddressZone_4);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.InternalId));
		
		col = new TableViewerColumn(viewerAddressZone, SWT.LEFT);
		col.getColumn().setText(Messages.DialogAddressZone_5);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.LongName));
		
		col = new TableViewerColumn(viewerAddressZone, SWT.LEFT);
		col.getColumn().setText(Messages.DialogAddressZone_6);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.ShortName));
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
				IFusionMapSpecificationEntity entity = compositeFusionMap.getSelection();
				
				if (entity == null){
					return; 
				}
				
				if(!selectedZones.contains(entity)){
					selectedZones.add(entity);
					boolean found = false;
					for(IFusionMapSpecificationEntity tmp : actualZones){
						if(tmp.getId() == entity.getId()){
							found = true;
							break;
						}
					}
					if(!found){
						addedZones.add(entity);
					}
				}
				refreshTableAddressZone();
			}
		});		
		
		Button removeZones = new Button(zonesButton, SWT.PUSH);
		removeZones.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		removeZones.setText("<<"); //$NON-NLS-1$
		removeZones.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)viewerAddressZone.getSelection();
				if(!selection.isEmpty()){
					IFusionMapSpecificationEntity entity = (IFusionMapSpecificationEntity)selection.getFirstElement();
					selectedZones.remove(entity);
					boolean found = false;
					for(IFusionMapSpecificationEntity tmp : actualZones){
						if(tmp.getId() == entity.getId()){
							found = true;
							break;
						}
					}
					if(found){
						removedZones.add(entity);
					}
				}
				refreshTableAddressZone();
			}
		});
	}
	
	private void refreshTableAddressZone(){
		viewerAddressZone.setInput(selectedZones);
	}
	
	@Override
	protected void okPressed() {
		if(!addedZones.isEmpty()){
			for(IFusionMapSpecificationEntity zone : addedZones){
				IAddressZone addressZoneRelation;
				try {
					addressZoneRelation = Activator.getDefault().getFactoryMap().createAddressZone();
					addressZoneRelation.setAddressId(address.getId());
					addressZoneRelation.setMapZoneId(zone.getId());
					Activator.getDefault().getDefinitionService().saveAddressZone(addressZoneRelation);
					address.addAddressZones(zone);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		if(!removedZones.isEmpty()){
			for(IFusionMapSpecificationEntity zone : removedZones){
				try {
					IAddressZone addressZone = Activator.getDefault().getDefinitionService().getAddressZoneByZoneAndAddressId(address.getId(), zone.getId());
					Activator.getDefault().getDefinitionService().delete(addressZone);
					address.removeAddressZones(zone);
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
		super.okPressed();
	}
}
