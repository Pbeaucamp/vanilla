package bpm.vanilla.map.design.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import bpm.norparena.mapmanager.viewers.TreeMapDefinitionContentProvider;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.IMapDefinitionRelation;

public class DialogSelectMapDefinitionParent extends Dialog {

	private List<IMapDefinition> mapDefinitions;
	private IMapDefinition mapDefinition;
	
	private TreeViewer mapDefinitionTree;
	
	private boolean edit;
	
	public DialogSelectMapDefinitionParent(Shell parentShell, List<IMapDefinition> mapDefinitions, 
			IMapDefinition mapDefinition,
			boolean edit) {
		super(parentShell);
		this.mapDefinitions = mapDefinitions;
		this.mapDefinition = mapDefinition;
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
		
		createTableMapDefinition(main);
		
		return main;
	}
	
	public void createTableMapDefinition(Composite mapDefinitionContent){
		Label lblSelectParent = new Label(mapDefinitionContent, SWT.NONE);
		lblSelectParent.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblSelectParent.setText(Messages.DialogSelectMapDefinitionParent_0);
		
		mapDefinitionTree = new TreeViewer(mapDefinitionContent, SWT.FULL_SELECTION);
		mapDefinitionTree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		mapDefinitionTree.setContentProvider(new TreeMapDefinitionContentProvider());
		
		buildTreeMapDefinitionColumn(mapDefinitionTree);

	    //We set the width of the address label column bigger than the other
		mapDefinitionTree.getTree().getColumn(0).setWidth(150);
		for (int i = 1, n = mapDefinitionTree.getTree().getColumnCount(); i < n; i++) {
			mapDefinitionTree.getTree().getColumn(i).setWidth(100);
		}
		
		mapDefinitionTree.getTree().setHeaderVisible(true);
		mapDefinitionTree.getTree().setLinesVisible(true);
	    
	    if(edit){
		    for(IMapDefinition mapDefinitionTemp : mapDefinitions){
		    	if(mapDefinitionTemp.getId() == mapDefinitionTemp.getId()){
		    		mapDefinitions.remove(mapDefinitionTemp);
		    		break;
		    	}
		    }
	    }
	    
	    mapDefinitionTree.setInput(mapDefinitions);
	}
	
	public void buildTreeMapDefinitionColumn(TreeViewer mapDefinitionTree){
		TreeViewerColumn columnLabel = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnLabel.getColumn().setText(Messages.DialogSelectMapDefinitionParent_1);
		columnLabel.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getLabel();
				return null;
			}
		});
		
		TreeViewerColumn columnTypeOfAddress = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnTypeOfAddress.getColumn().setText(Messages.DialogSelectMapDefinitionParent_2);
		columnTypeOfAddress.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition)
					return ((IMapDefinition)element).getDescription();
				return null;
			}
		});
		
		TreeViewerColumn columnBloc = new TreeViewerColumn(mapDefinitionTree, SWT.NONE);
		columnBloc.getColumn().setText(Messages.DialogSelectMapDefinitionParent_3);
		columnBloc.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof IMapDefinition) {
					if(((IMapDefinition)element).getFusionMapObject() != null) {
						return ((IMapDefinition)element).getFusionMapObject().getName();
					}
					else if(((IMapDefinition)element).getOpenLayersMapObject() != null) {
						return ((IMapDefinition)element).getOpenLayersMapObject().getName();
					}
				}
				return null;
			}
		});
	}
	
	@Override
	protected void okPressed() {
		IStructuredSelection ss = (IStructuredSelection)mapDefinitionTree.getSelection();

		IMapDefinition mapDefinitionParent = (IMapDefinition)ss.getFirstElement();
		//We create a new relation between two MapDefinition
		IMapDefinitionRelation relation = null;
		
		if(edit && !ss.isEmpty()){
			if(mapDefinition.getMapDefinitionRelation() == null){
				try {
					relation = Activator.getDefault().getFactoryMap().createMapDefinitionRelation();
				} catch (Exception e) {
					e.printStackTrace();
				}
				relation.setParentId(mapDefinitionParent.getId());
				relation.setChildId(mapDefinition.getId());
				mapDefinition.setMapRelation(relation);
			}
			else{
				mapDefinition.getMapDefinitionRelation().setParentId(mapDefinitionParent.getId());
			}
			super.okPressed(); 
		}
		else if (!ss.isEmpty()){
			try {
				relation = Activator.getDefault().getFactoryMap().createMapDefinitionRelation();
			} catch (Exception e) {
				e.printStackTrace();
			}
			relation.setParentId(mapDefinitionParent.getId());
			mapDefinition.setMapRelation(relation);
			
			super.okPressed(); 
		}
	}
}
