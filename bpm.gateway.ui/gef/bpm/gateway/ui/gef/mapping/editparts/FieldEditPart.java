package bpm.gateway.ui.gef.mapping.editparts;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.ui.gef.mapping.model.FieldModel;
import bpm.gateway.ui.gef.mapping.model.Relation;

public class FieldEditPart  extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener {

	private ConnectionAnchor anchor;
	private int align = PositionConstants.RIGHT;
	private Composite parent;
	
	
	public FieldEditPart(int align, Composite parent){
		this.align = align;
		this.parent = parent;
	}
	
	
	@Override
	public void activate() {
		super.activate();
		((FieldModel)getModel()).addPropertyChangeListener(this);
	}
	
	@Override
	public void deactivate() {
		super.deactivate();
		((FieldModel)getModel()).removePropertyChangeListener(this);
	}
	
	@Override
	protected IFigure createFigure() {
		
		Label l = new Label();
		l.setText(((FieldModel)getModel()).getFieldName());
		l.setLabelAlignment(align);
//		l.setSize(l.getPreferredSize());
//		((AbstractGraphicalEditPart)getParent()).getFigure().setConstraint(l, new GridData(GridData.BEGINNING, GridData.CENTER, true, false));
		return l;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {
			
			@Override
			protected Command getReconnectTargetCommand(ReconnectRequest arg0) {
				
				return null;
			}
			
			@Override
			protected Command getReconnectSourceCommand(ReconnectRequest arg0) {
				
				return null;
			}
			
			protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
				RelationCommand cmd = (RelationCommand) request.getStartCommand();
				cmd.setTarget((FieldModel) getHost().getModel());
				return cmd;
			}
			
			protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
				FieldModel source = (FieldModel) getHost().getModel();
				RelationCommand cmd = new RelationCommand(source, parent);
				request.setStartCommand(cmd);
				return cmd;
			}
		});
	}

	protected ConnectionAnchor getConnectionAnchor() {
		if (anchor == null) {;
			anchor = new ChopboxAnchor(getFigure());
		}
		return anchor;
	}
	
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	
	@Override
	protected List<Relation> getModelSourceConnections() {
		return	((FieldModel)getModel()).getSourceConnections();
	}

	@Override
	protected List<Relation> getModelTargetConnections() {
		return	((FieldModel)getModel()).getTargetConnections();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("linksource")) { //$NON-NLS-1$
			refreshSourceConnections();
		}
		else if (evt.getPropertyName().equals("linktarget")) { //$NON-NLS-1$
			refreshTargetConnections();
		}	
	}
	
}
