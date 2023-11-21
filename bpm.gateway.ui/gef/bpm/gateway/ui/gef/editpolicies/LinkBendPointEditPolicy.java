package bpm.gateway.ui.gef.editpolicies;



import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import bpm.gateway.ui.gef.commands.DeleteBendPointCommand;
import bpm.gateway.ui.gef.commands.LinkCreateBendPointCommand;
import bpm.gateway.ui.gef.commands.MoveBendPointCommand;
import bpm.gateway.ui.gef.model.Link;

public class LinkBendPointEditPolicy extends BendpointEditPolicy{

	@Override
	protected Command getCreateBendpointCommand(BendpointRequest request) {
		LinkCreateBendPointCommand cmd = new LinkCreateBendPointCommand();
		Point p = request.getLocation();
		cmd.setLink((Link) request.getSource().getModel());
		cmd.setLocation(new java.awt.Point(p.x, p.y));
		cmd.setIndex(request.getIndex());
		return cmd;
	}

	@Override
	protected Command getDeleteBendpointCommand(BendpointRequest request) {
		DeleteBendPointCommand cmd = new DeleteBendPointCommand();
		cmd.setLink((Link) request.getSource().getModel());
		cmd.setIndex(request.getIndex());
		return cmd;
	}

	@Override
	protected Command getMoveBendpointCommand(BendpointRequest request) {
		MoveBendPointCommand cmd = new MoveBendPointCommand();
		Point p = request.getLocation();
		cmd.setNewLocation(new java.awt.Point(p.x, p.y));
		cmd.setLink((Link) request.getSource().getModel());
		cmd.setIndex(request.getIndex());
		return cmd;
	}

}
