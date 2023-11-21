package bpm.workflow.ui.gef.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.graphics.Image;

import bpm.workflow.runtime.model.WorkflowObject;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.gef.commands.ChangeLayoutCommand;
import bpm.workflow.ui.gef.commands.DeleteCommand;
import bpm.workflow.ui.gef.commands.NodeCreationCommand;
import bpm.workflow.ui.gef.figure.FigureLoop;
import bpm.workflow.ui.gef.model.LoopModel;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.icons.IconsNames;



public class LoopEditPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, NodeEditPart {

	
	private ConnectionAnchor anchor;
	
	
	@Override
	protected IFigure createFigure() {
		
		FigureLoop figure =null;
		WorkflowObject a = ((LoopModel)getModel()).getWorkflowObject();
		Image pict = Activator.getDefault().getImageRegistry().get(IconsNames.LOOP_INDIC);
		
		figure = new FigureLoop(pict,a.getPositionHeight(),a.getPositionWidth());
		setColor(figure, a);
		
		
		return figure;
	}
	
	

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){
			protected Command createDeleteCommand(GroupRequest deleteRequest){
				DeleteCommand command = new DeleteCommand();
				command.setModel(getHost().getModel());
				command.setParentModel(getHost().getParent().getModel());
				return command;
			}
			@Override
			public Command getCommand(Request request) {
				
				
				return super.getCommand(request);
			}
		});
		installEditPolicy(EditPolicy.LAYOUT_ROLE,new XYLayoutEditPolicy(){

			@Override
			protected Command createChangeConstraintCommand(EditPart child,
					Object constraint) {
				ChangeLayoutCommand command = new ChangeLayoutCommand();
				command.setModel(child.getModel());
				Rectangle rect = (Rectangle)constraint;
				
				if (getHost() instanceof LoopEditPart){
					rect.x += getHostFigure().getBounds().x;
					rect.y += getHostFigure().getBounds().y;
				}
				command.setLayout(rect);
				
				
				return command;
			}

			@Override
			protected Command getCreateCommand(CreateRequest request) {
				if (request.getType() == REQ_CREATE ) {
		            
			    	if (request.getNewObject() instanceof Node){
			    		
			    		
			    		NodeCreationCommand cmd = new NodeCreationCommand();
			            
			            cmd.setParent((LoopModel)getHost().getModel());
			            cmd.setNewObject((Node)request.getNewObject());
			           
			            Rectangle constraint = (Rectangle)getConstraintFor(request);
			            constraint.x = (constraint.x < 0) ? 0 : constraint.x+ ((LoopModel)getHost().getModel()).getX();
			            constraint.y = (constraint.y < 0) ? 0 : constraint.y+ ((LoopModel)getHost().getModel()).getY();
			            constraint.width = 50;
			            constraint.height = 50;
			            cmd.setLayout(constraint);
			            return cmd;
			    	}
				}
				return null;
			}
			
		});
	
//		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {
//
//			protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
//				LinkCommand cmd 
//					= (LinkCommand) request.getStartCommand();
//				cmd.setTarget((Node) getHost().getModel());
//				return cmd;
//			}
//			
//			protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
//				Node source = (Node) getHost().getModel();
//				LinkCommand cmd = new LinkCommand(source);
//				request.setStartCommand(cmd);
//				return cmd;
//			}
//
//			protected Command getReconnectSourceCommand(ReconnectRequest request) {
//				Link conn = (Link) request.getConnectionEditPart().getModel();
//				Node newSource = (Node) getHost().getModel();
//				ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
//				cmd.setNewSource(newSource);
//				return cmd;
//				return null;
//			}
//
//			protected Command getReconnectTargetCommand(ReconnectRequest request) {
//				Link conn = (Link) request.getConnectionEditPart().getModel();
//				Node newTarget = (Node) getHost().getModel();
//				ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
//				cmd.setNewTarget(newTarget);
//				return cmd;
//			return null;
//			}
//		});

	}

	private void setColor(FigureLoop figure, WorkflowObject a) {
		
	
//			figure.setForegroundColor(ColorConstants.black);
		
		
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(LoopModel.PROPERTY_LAYOUT)) {
			refreshVisuals();
		}
		else if (evt.getPropertyName().equals(LoopModel.PROPERTY_ADD_CHILD)){
			
			refreshChildren();
		}
		if (evt.getPropertyName().equals(Node.PROPERTY_RENAME)) {
			refreshVisuals();
		}
		if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
			refreshSourceConnections();
		}
		if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP) ){
			refreshTargetConnections();
		}

	}
	
	@Override
	protected List getModelChildren() {
		return((LoopModel)getModel()).getContents();
	}
	
	@Override
	protected void refreshVisuals() {

		LoopModel model = (LoopModel)getModel();
		FigureLoop figure = (FigureLoop)getFigure();
		figure.setName(model.getWorkflowObject().getName());
		figure.resizeFigure(model.getWidth(), model.getHeight());
		figure.setBounds(new Rectangle(model.getX(), model.getY(), model.getWidth(), model.getHeight()));
		
		
		
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		((LoopModel)getModel()).addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		((LoopModel)getModel()).removePropertyChangeListener(this);
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
	protected List getModelSourceConnections() {
		return	((Node)getModel()).getSourceLink();
	}

	@Override
	protected List getModelTargetConnections() {
		return	((Node)getModel()).getTargetLink();
	}
}
