package bpm.gateway.ui.editors;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

import bpm.gateway.ui.actions.ActionCopy;
import bpm.gateway.ui.actions.ActionLink;
import bpm.gateway.ui.actions.ActionPaste;
import bpm.gateway.ui.gef.part.LinkEditPart;
import bpm.gateway.ui.i18n.Messages;

public class GatewayContextMenuProvider extends ContextMenuProvider {

	
	private ActionRegistry actionRegistry;
	
	public GatewayContextMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		setActionRegistry(actionRegistry);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void buildContextMenu(IMenuManager menu) {
		IAction action;
		
		GEFActionConstants.addStandardActionGroups(menu);
		
		List objects = getSelectedObjects();
		if (objects == null || objects.isEmpty()) {
			return;
		}
		
		boolean hasLink = false;
		for (int i = 0; i < objects.size(); i++) {
			EditPart object = (EditPart) objects.get(i);
			if (object instanceof LinkEditPart) {
				hasLink = true;
				break;
			}
		}

		if (!hasLink) {
			action = new ActionLink(objects);
			menu.appendToGroup(GEFActionConstants.GROUP_VIEW, action);
			
			
			action = new ActionCopy();
			action.setText(Messages.GatewayContextMenuProvider_0);
			menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
			
			action = new ActionPaste(getViewer());
			action.setText(Messages.GatewayContextMenuProvider_1);
			menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		}
		
		action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

	}
	
	@SuppressWarnings("rawtypes")
	protected List getSelectedObjects() {
		if (!(getViewer().getSelection() instanceof IStructuredSelection))
			return Collections.EMPTY_LIST;
		return ((IStructuredSelection) getViewer().getSelection()).toList();
	}

	private ActionRegistry getActionRegistry(){
		return actionRegistry;
	}
	
	private void setActionRegistry(ActionRegistry registry){
		actionRegistry = registry;
	}
}
