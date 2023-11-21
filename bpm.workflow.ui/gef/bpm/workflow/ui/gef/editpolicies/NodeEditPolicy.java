package bpm.workflow.ui.gef.editpolicies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import bpm.workflow.ui.gef.commands.ChangeLayoutCommand;
import bpm.workflow.ui.gef.commands.LoopCreationCommand;
import bpm.workflow.ui.gef.commands.MacroProcessCreationCommand;
import bpm.workflow.ui.gef.commands.NodeCreationCommand;
import bpm.workflow.ui.gef.model.ContainerPanelModel;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.MacroProcessModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.model.Pool;
import bpm.workflow.ui.gef.part.ContentPanelEditPart;
import bpm.workflow.ui.gef.part.ContentPoolEditPart;
import bpm.workflow.ui.gef.part.NodePart;
import bpm.workflow.ui.gef.part.PoolEditPart;

public class NodeEditPolicy extends XYLayoutEditPolicy {

	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {

		ChangeLayoutCommand command = new ChangeLayoutCommand();
		command.setModel(child.getModel());
		command.setLayout((Rectangle) constraint);

		return command;
	}

	protected Command getCreateCommand(CreateRequest request) {

		if(request.getType() == REQ_CREATE && getHost() instanceof PoolEditPart) {
			if(request.getNewObject() instanceof LoopModel) {
				LoopCreationCommand cmd = new LoopCreationCommand();

				cmd.setParent((Pool) getHost().getModel());
				cmd.setNewObject((LoopModel) request.getNewObject());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;
				constraint.width = 200;
				constraint.height = 200;
				cmd.setLayout(constraint);
				return cmd;
			}
			if(request.getNewObject() instanceof MacroProcessModel) {
				MacroProcessCreationCommand cmd = new MacroProcessCreationCommand();

				cmd.setParent((Pool) getHost().getModel());
				cmd.setNewObject((MacroProcessModel) request.getNewObject());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;
				constraint.width = 200;
				constraint.height = 200;
				cmd.setLayout(constraint);
				return cmd;
			}
			if(request.getNewObject() instanceof Node) {
				NodeCreationCommand cmd = new NodeCreationCommand();
				Rectangle bounds = ((PoolEditPart) getHost()).getFigure().getBounds();
				cmd.setParent((Pool) getHost().getModel());
				cmd.setNewObject((Node) request.getNewObject());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;
				constraint.width = 50;
				constraint.height = 50;
				cmd.setLayout(constraint, bounds);
				return cmd;
			}
		}
		else if(request.getType() == REQ_CREATE && getHost() instanceof ContentPoolEditPart) {

		}
		else if(request.getType() == REQ_CREATE && getHost() instanceof ContentPanelEditPart) {
			if(request.getNewObject() instanceof LoopModel) {
				try {
					LoopCreationCommand cmd = new LoopCreationCommand();

					cmd.setParent((ContainerPanelModel) getHost().getModel());
					cmd.setNewObject((LoopModel) request.getNewObject());

					Rectangle constraint = (Rectangle) getConstraintFor(request);
					constraint.x = (constraint.x < 0) ? 0 : constraint.x;
					constraint.y = (constraint.y < 0) ? 0 : constraint.y;
					constraint.width = 200;
					constraint.height = 200;
					cmd.setLayout(constraint);
					return cmd;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(request.getNewObject() instanceof MacroProcessModel) {
				MacroProcessCreationCommand cmd = new MacroProcessCreationCommand();

				cmd.setParent((ContainerPanelModel) getHost().getModel());
				cmd.setNewObject((MacroProcessModel) request.getNewObject());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;
				constraint.width = 200;
				constraint.height = 200;
				cmd.setLayout(constraint);
				return cmd;
			}
			if(request.getNewObject() instanceof Node) {

				NodeCreationCommand cmd = new NodeCreationCommand();
				cmd.setParent((ContainerPanelModel) getHost().getModel());
				cmd.setNewObject((Node) request.getNewObject());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;
				constraint.width = 50;
				constraint.height = 50;
				cmd.setLayout(constraint);
				return cmd;
			}

		}
		else if(request.getType() == REQ_CREATE && getHost() instanceof LoopModel) {

			if(request.getNewObject() instanceof Node) {

				NodeCreationCommand cmd = new NodeCreationCommand();
				cmd.setParent((ContainerPanelModel) getHost().getModel());
				cmd.setNewObject((Node) request.getNewObject());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;
				constraint.width = 50;
				constraint.height = 50;
				cmd.setLayout(constraint);
				return cmd;
			}

		}
		else if(request.getType() == REQ_CREATE && getHost() instanceof MacroProcessModel) {

			if(request.getNewObject() instanceof Node) {

				NodeCreationCommand cmd = new NodeCreationCommand();
				cmd.setParent((ContainerPanelModel) getHost().getModel());
				cmd.setNewObject((Node) request.getNewObject());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;
				constraint.width = 50;
				constraint.height = 50;
				cmd.setLayout(constraint);
				return cmd;
			}

		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.XYLayoutEditPolicy#getConstraintFor(org.eclipse.gef.requests.ChangeBoundsRequest, org.eclipse.gef.GraphicalEditPart)
	 */
	@Override
	protected Object getConstraintFor(ChangeBoundsRequest request, GraphicalEditPart child) {
		if(child instanceof NodePart) {

			if(request.getType() == REQ_RESIZE || request.getType() == REQ_RESIZE_CHILDREN) {
				return null;
			}
		}

		return super.getConstraintFor(request, child);
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		if(child instanceof NodePart) {
			return new NonResizableEditPolicy();
		}

		else {
			return super.createChildEditPolicy(child);
		}

	}

}
