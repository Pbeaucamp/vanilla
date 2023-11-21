package bpm.sqldesigner.ui.editor;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.ui.Activator;
import bpm.sqldesigner.ui.dialog.DialogNewDataWareHouse;
import bpm.sqldesigner.ui.editpart.TablePart;
import bpm.sqldesigner.ui.i18N.Messages;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditor;
import bpm.sqldesigner.ui.snapshot.editor.SnapshotEditorInput;


public class DesignContextualMenuProvider extends ContextMenuProvider{

	private ActionRegistry actionRegistry;
	
	public DesignContextualMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
			setActionRegistry(actionRegistry);	
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		IAction action;
		
		GEFActionConstants.addStandardActionGroups(menu);
		
		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		
		if (!getViewer().getSelection().isEmpty()){
			
			
			
			action = new Action(Messages.DesignContextualMenuProvider_0){
				public void run(){
					if (getViewer().getSelection().isEmpty()){
						return;
					}
					
					
					DatabaseCluster cluster = null;
					Schema schema = null;
					DocumentSnapshot doc = new DocumentSnapshot();
					doc.setName(Messages.DesignContextualMenuProvider_1);
					for(Object o : ((IStructuredSelection)getViewer().getSelection()).toList()){
						if ( o instanceof TablePart){
							doc.addTable((Table)((TablePart)o).getModel());
							schema = ((Schema)((TablePart)o).getParent().getModel());
							cluster = ((Schema)((TablePart)o).getParent().getModel()).getCluster();
						}
					}
					
					final DatabaseCluster fCl = cluster;
					
//					InputDialog d = new InputDialog(getViewer().getControl().getShell(), "New DataWhareHouse View", "DataWareHouse View Name", "newDataWhareHouse", new IInputValidator() {
//						
//						public String isValid(String newText) {
//							for(DocumentSnapshot s : fCl.getDocumentSnapshots()){
//								if (s.getName().equals(fCl.getName())){
//									return "A DataWareHouse View with the same name already exists.";
//								}
//							}
//							return null;
//						}
//					});
					
					DialogNewDataWareHouse d = new DialogNewDataWareHouse(getViewer().getControl().getShell(), fCl);
					if (d.open() == InputDialog.OK){
						
						
						try {
							doc.setName(d.getValue());
							doc.setSchema(schema);
							doc.rebuildLinks();
							
							cluster.addDocumentSnapshot(doc);
							Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new SnapshotEditorInput(doc), SnapshotEditor.ID);
						} catch (Exception e) {
							
							e.printStackTrace();
						}
					}
					
					
					
				}
			};
			action.setId("createSnapshot"); //$NON-NLS-1$
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
			
		}

		
	}
	private ActionRegistry getActionRegistry(){
		return actionRegistry;
	}
	
	private void setActionRegistry(ActionRegistry registry){
		actionRegistry = registry;
	}
}
