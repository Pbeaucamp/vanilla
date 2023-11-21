package bpm.gateway.ui.gef.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.MergeStreams;
import bpm.gateway.core.transformations.SelectDistinct;
import bpm.gateway.core.transformations.SelectionTransformation;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.SortTransformation;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.gef.commands.ChangeLayoutCommand;
import bpm.gateway.ui.gef.commands.DeleteCommand;
import bpm.gateway.ui.gef.commands.LinkCommand;
import bpm.gateway.ui.gef.commands.NodeCreationCommand;
import bpm.gateway.ui.gef.figure.FigureGDI;
import bpm.gateway.ui.gef.model.GIDModel;
import bpm.gateway.ui.gef.model.Node;

public class GIDEditPart extends /*AbstractGraphicalEditPart implements
		PropertyChangeListener, NodeEditPart*/NodePart {

	
	private ConnectionAnchor anchor;
	
	private static List<Class> allowedTransfo = new ArrayList<Class>();
	static{
			allowedTransfo.add(DataBaseInputStream.class);
			allowedTransfo.add(SelectDistinct.class);
			allowedTransfo.add(SelectionTransformation.class);
			allowedTransfo.add(SortTransformation.class);
			allowedTransfo.add(MergeStreams.class);
			allowedTransfo.add(AggregateTransformation.class);
			allowedTransfo.add(SimpleMappingTransformation.class);
			allowedTransfo.add(Filter.class);
		
	}
	
	@Override
	protected IFigure createFigure() {
		
		Figure figure = new FigureGDI();
		
		
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
				
				if (getHost() instanceof GIDEditPart){
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
			    		
			    		if (!allowedTransfo.contains(((Node)request.getNewObject()).getGatewayModel().getClass())){
			    			return null;
			    		}

			    		NodeCreationCommand cmd = new NodeCreationCommand();
			            
			            cmd.setParent((GIDModel)getHost().getModel());
			            cmd.setNewObject((Node)request.getNewObject());
			           
			            Rectangle constraint = (Rectangle)getConstraintFor(request);
			            constraint.x = (constraint.x < 0) ? 0 : constraint.x + ((GIDModel)getHost().getModel()).getX();
			            constraint.y = (constraint.y < 0) ? 0 : constraint.y+ ((GIDModel)getHost().getModel()).getY();
			            constraint.width = 50;
			            constraint.height = 50;
			            cmd.setLayout(constraint);
			            return cmd;
			    	}
				}
				
				return null;
			}


		});
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {

			protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
				LinkCommand cmd 
					= (LinkCommand) request.getStartCommand();
				cmd.setTarget((Node) getHost().getModel());
				return cmd;
			}
			
			protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
				Node source = (Node) getHost().getModel();
				LinkCommand cmd = new LinkCommand(source);
				request.setStartCommand(cmd);
				return cmd;
			}

			protected Command getReconnectSourceCommand(ReconnectRequest request) {
//				Link conn = (Link) request.getConnectionEditPart().getModel();
//				Node newSource = (Node) getHost().getModel();
//				ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
//				cmd.setNewSource(newSource);
//				return cmd;
				return null;
			}

			protected Command getReconnectTargetCommand(ReconnectRequest request) {
//				Link conn = (Link) request.getConnectionEditPart().getModel();
//				Node newTarget = (Node) getHost().getModel();
//				ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(conn);
//				cmd.setNewTarget(newTarget);
//				return cmd;
			return null;
			}

		});

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(GIDModel.PROPERTY_LAYOUT) || evt.getPropertyName().equals(GIDModel.PROPERTY_RENAME)) {
			refreshVisuals();
		}
		else if (evt.getPropertyName().equals(GIDModel.PROPERTY_ADD_CHILD)){
			
			refreshChildren();
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
		return((GIDModel)getModel()).getContents();
	}
	
	@Override
	protected void refreshVisuals() {

		GIDModel model = (GIDModel)getModel();
		FigureGDI figure = (FigureGDI)getFigure();
		figure.setName(model.getGatewayModel().getName());
		
//		figure.setName(model.getName());
		figure.setBounds(new Rectangle(model.getX(), model.getY(), model.getWidth(), model.getHeight()));
		
		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		figure.setBackgroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.GID_COLOR_KEY));

//		figure.setLayout();
		
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		((GIDModel)getModel()).addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		((GIDModel)getModel()).removePropertyChangeListener(this);
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
