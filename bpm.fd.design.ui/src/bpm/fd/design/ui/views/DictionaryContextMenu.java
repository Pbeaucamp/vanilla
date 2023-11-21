package bpm.fd.design.ui.views;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

public class DictionaryContextMenu extends ContextMenuProvider{

private ActionRegistry actionRegistry;
	
	public DictionaryContextMenu(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		setActionRegistry(actionRegistry);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		IAction action;

		
		GEFActionConstants.addStandardActionGroups(menu);
		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
	
	}
	
	
	
	
	
	
		
	
	private ActionRegistry getActionRegistry(){
		return actionRegistry;
	}
	
	private void setActionRegistry(ActionRegistry registry){
		actionRegistry = registry;
	}

	
	
}
