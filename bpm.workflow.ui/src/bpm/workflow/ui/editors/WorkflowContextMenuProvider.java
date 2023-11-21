package bpm.workflow.ui.editors;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

import bpm.workflow.runtime.model.activities.BiWorkFlowActivity;
import bpm.workflow.runtime.model.activities.CancelActivity;
import bpm.workflow.runtime.model.activities.ConpensationActivity;
import bpm.workflow.runtime.model.activities.ErrorActivity;
import bpm.workflow.runtime.model.activities.LinkActivity;
import bpm.workflow.runtime.model.activities.MailActivity;
import bpm.workflow.runtime.model.activities.SignalActivity;
import bpm.workflow.runtime.model.activities.TimerActivity;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.actions.ActionChange;
import bpm.workflow.ui.actions.ActionCopy;
import bpm.workflow.ui.actions.ActionLink;
import bpm.workflow.ui.actions.ActionOpenBIW;
import bpm.workflow.ui.actions.ActionPaste;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.LinkEditPart;
import bpm.workflow.ui.gef.part.LoopEditPart;
import bpm.workflow.ui.gef.part.MacroProcessEditPart;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Context menu provider of the node in the graphical part
 * 
 * @author CHARBONNIER, MARTIN
 * 
 */
public class WorkflowContextMenuProvider extends ContextMenuProvider {

	private ActionRegistry actionRegistry;

	public WorkflowContextMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
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

		action = new ActionPaste(getViewer());
		action.setText(Messages.WorkflowContextMenuProvider_1);
		menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
		


		if (!hasLink) {
			action = new ActionLink(objects);
			menu.appendToGroup(GEFActionConstants.GROUP_VIEW, action);
		}

		ISelection s = getViewer().getSelection();
		if (s.isEmpty()) {
			return;
		}

		Object o = ((IStructuredSelection) s).getFirstElement();

		if (o instanceof NodePart) {

			Node n = (Node) ((NodePart) o).getModel();

			Object obj = n.getWorkflowObject();

			action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

			action = new ActionCopy();
			action.setText(Messages.WorkflowContextMenuProvider_0);
			menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);

			if (obj instanceof MailActivity || obj instanceof SignalActivity || obj instanceof CancelActivity || obj instanceof TimerActivity || obj instanceof LinkActivity || obj instanceof ConpensationActivity || obj instanceof ErrorActivity) {
				action = new ActionChange();
				action.setText(Messages.WorkflowContextMenuProvider_2);
				menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
			}
			if (obj instanceof BiWorkFlowActivity) {
				action = new ActionOpenBIW();

				action.setText(Messages.WorkflowContextMenuProvider_3);
				menu.appendToGroup(GEFActionConstants.GROUP_COPY, action);
			}
		}

		else if (o instanceof LoopEditPart) {
			GEFActionConstants.addStandardActionGroups(menu);
			action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		}
		else if (o instanceof MacroProcessEditPart) {
			GEFActionConstants.addStandardActionGroups(menu);
			action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		}
		else if (o instanceof LinkEditPart) {
			GEFActionConstants.addStandardActionGroups(menu);
			action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
			menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
		}
	}

	@SuppressWarnings("rawtypes")
	protected List getSelectedObjects() {
		if (!(getViewer().getSelection() instanceof IStructuredSelection))
			return Collections.EMPTY_LIST;
		return ((IStructuredSelection) getViewer().getSelection()).toList();
	}

	private ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

}
