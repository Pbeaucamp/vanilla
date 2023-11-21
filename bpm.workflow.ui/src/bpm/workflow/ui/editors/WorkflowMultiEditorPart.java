package bpm.workflow.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.UpdateAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

import bpm.workflow.ui.Messages;

/**
 * The multi-editor of the workspace
 * @author Charles MARTIN
 *
 */
public class WorkflowMultiEditorPart extends MultiPageEditorPart {
	public static final String ID = Messages.WorkflowMultiEditorPart_0;
	private WorkflowPaletteRoot paletteroot;
	private int workflowPageID;
	private int workflowPoolID;
	private List selectionActions = new ArrayList();
	private ActionRegistry actionRegistry;
	private int activePageID;

	public WorkflowMultiEditorPart() {
	}
	
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		createActions();
		
	}
	protected void createActions() {
		IAction action = new DeleteAction((IWorkbenchPart)this);
		selectionActions.add(action);
		addAction(action);	
		
		getSite()
        .getWorkbenchWindow()
        .getSelectionService()
        .addSelectionListener(new ISelectionListener() {
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                updateActions(selectionActions);
                if (activePageID !=  getActivePage()) {
                	if (getActiveEditor() instanceof WorkflowModelEditorPart) {
                		WorkflowModelEditorPart e = (WorkflowModelEditorPart) getActiveEditor();
                		e.refresh();
                	}
                	else if (getActiveEditor() instanceof WorkflowPoolEditorPart) {
                		WorkflowPoolEditorPart e = (WorkflowPoolEditorPart) getActiveEditor();
                		e.refresh();
                	}
                	activePageID = getActivePage();
                }
            }
        });
    }
	public IEditorPart getActive(){
		return getActiveEditor();
	}
	@Override
	protected void createPages() {
        try {
            // create workflow page
            workflowPageID = addPage(new WorkflowModelEditorPart(this), getEditorInput());
            setPageText(workflowPageID, Messages.WorkflowMultiEditorPart_1);
            
            workflowPoolID = addPage(new WorkflowPoolEditorPart(this), getEditorInput());
            setPageText(workflowPoolID, Messages.WorkflowMultiEditorPart_2);

            // set active page
            setActivePage(workflowPageID);
            activePageID = getActivePage();
            
            setPartName(((WorkflowEditorInput) getEditorInput()).getWorkflowModel().getName());

        }
        catch (PartInitException e) {
            ErrorDialog.openError(
                getSite().getShell(),
                Messages.WorkflowMultiEditorPart_3,
                Messages.WorkflowMultiEditorPart_4,
                e.getStatus());
        }

    
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		getEditor(workflowPageID).doSave(monitor);
	}


	@Override
	public void doSaveAs() {
		getEditor(workflowPageID).doSaveAs();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	protected PaletteRoot getPaletteRoot() {
		if (paletteroot == null) {
			paletteroot = new WorkflowPaletteRoot();
		}
		
		return paletteroot;
	}

	
	protected void updateActions(List actionIds) {
		ActionRegistry registry = getActionRegistry();
		for (Object o : actionIds) {
			if (o instanceof UpdateAction)
				((UpdateAction)o).update();
		}
	}
	
	protected ActionRegistry getActionRegistry() {
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();
		return actionRegistry;
	}
	
	protected void addAction(IAction action) {
        getActionRegistry().registerAction(action);
    }


	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {

		return super.getAdapter(adapter);
	}
	
	public EditDomain getDomain(){
		return ((WorkflowModelEditorPart)getEditor(workflowPageID)).getDomain();
	}
	
	public void setPaletteViewer(PaletteViewer viewer){
		((WorkflowModelEditorPart)getEditor(workflowPageID)).setPaletteViewer(viewer);

	}

}

