package bpm.vanilla.map.design.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.viewers.TableColumnLabelProvider;
import bpm.norparena.mapmanager.viewers.TreeMapDefinitionContentProvider;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IAddressMapDefinitionRelation;
import bpm.vanilla.map.core.design.IMapDefinition;

public class DialogSelectMapDefinition extends Dialog {

	private List<IMapDefinition> mapDefinitions;
	private IAddress address;
	
	private TreeViewer mapTree;
	
	private boolean edit;
	
	public DialogSelectMapDefinition(Shell parentShell, List<IMapDefinition> mapDefinitions, 
			IAddress address, boolean edit) {
		super(parentShell);
		this.mapDefinitions = mapDefinitions;
		this.address = address;
		this.edit = edit;
	}
	
	
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 400);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		createTreeMap(main);
		
		return main;
	}
	
	public void createTreeMap(Composite mapContent){
		Label lblSelectMap = new Label(mapContent, SWT.NONE);
		lblSelectMap.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblSelectMap.setText(Messages.DialogSelectMapDefinition_0);
		
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
	    
	    if(edit){
//		    for(IAddress addressTemp : addresses){
//		    	if(addressTemp.getId() == address.getId()){
//		    		addresses.remove(addressTemp);
//		    		break;
//		    	}
//		    }
	    }
	    
	    mapTree.setInput(mapDefinitions);
	}
	
	public void buildTreeMapDefinitionColumn(TreeViewer mapDefinitionTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.DialogSelectMapDefinition_1);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.DialogSelectMapDefinition_2);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getDescription();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.DialogSelectMapDefinition_3);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getFusionMapObject().getName();
				return null;
			}
		});
	}
	
	@Override
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection)mapTree.getSelection();
		
		if(!ss.isEmpty()){
			IMapDefinition map = (IMapDefinition)ss.getFirstElement();
			
			//We create a new relation between an address and a map
			IAddressMapDefinitionRelation relation = null;
			try {
				relation = Activator.getDefault().getFactoryMap().createAddressMapDefinitionRelation();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
//			if(edit){
//				relation.setAddressId(address.getId());
//				relation.setMapDefinitionId(map.getId());
//				address.setAddressMapDefinitionRelation(relation);
//				address.setMapDefinition(map);
//			}
//			else{
//				relation.setMapDefinitionId(map.getId());
//				address.setAddressMapDefinitionRelation(relation);
//				address.setMapDefinition(map);
//			}
			super.okPressed(); 
		}
	}
}
