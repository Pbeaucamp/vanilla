package bpm.fd.design.ui.editor.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.IBaseElement;
import bpm.fd.design.ui.editor.model.IFdObjectProvider;
import bpm.fd.design.ui.editor.policies.FreeLayoutPolicy;


public class ModelPart extends AbstractGraphicalEditPart implements PropertyChangeListener, IFdObjectProvider{
	@Override
	protected IFigure createFigure() {
		IFigure figure = new FreeformLayer();
		figure.setBorder(new MarginBorder(3));
		figure.setLayoutManager(new FreeformLayout());
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));
		
		return figure;
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,new FreeLayoutPolicy());
	}
	@Override
	protected List getModelChildren() {
		
		return ((FdModel)getModel()).getContent();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refresh();
	}
	
	@Override
	public void activate() {
		super.activate();
		((FdModel)getModel()).addPropertyChangeListener(this);
	}
	
	@Override
	public void deactivate() {
		((FdModel)getModel()).removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public IBaseElement getFdObject() {
		return (IBaseElement)getModel();
	}
}
