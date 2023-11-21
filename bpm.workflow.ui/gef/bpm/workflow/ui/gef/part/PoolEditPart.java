package bpm.workflow.ui.gef.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;

import bpm.workflow.runtime.model.IActivity;
import bpm.workflow.ui.gef.editpolicies.NodeEditPolicy;
import bpm.workflow.ui.gef.figure.FigurePool;
import bpm.workflow.ui.gef.model.Pool;

public class PoolEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {

	private IFigure figure;

	@Override
	protected IFigure createFigure() {
		figure = new FigurePool();

		ConnectionLayer connLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));

		return figure;
	}

	@Override
	protected void refreshVisuals() {
		FigurePool figure = (FigurePool) getFigure();
		Pool model = (Pool) getModel();
		figure.setConstraint(new GridData(GridData.FILL, GridData.FILL, true, true));
		figure.setProcessName(model.getName());

	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NodeEditPolicy());

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals(Pool.PROPERTY_ADD_CHILD)) {
			refreshChildren();
		}
		if(evt.getPropertyName().equals(Pool.PROPERTY_REMOVE_CHILD)) {
			refreshChildren();
		}

	}

	public void activate() {
		super.activate();
		if(getModel() instanceof Pool) {
			((Pool) getModel()).addPropertyChangeListener(this);
		}
	}

	public void deactivate() {
		super.deactivate();
		if(getModel() instanceof Pool) {
			((Pool) getModel()).removePropertyChangeListener(this);
		}
	}

	protected List getModelChildren() {
		return ((Pool) getModel()).getChildren();
	}

	@Override
	public IFigure getContentPane() {
		Pool model = (Pool) getModel();
		int max = 0;
		for(IActivity a : model.getPoolModel().getChildrens()) {
			if(a.getPositionX() > max) {
				max = a.getPositionX();
			}
		}
		return ((FigurePool) getFigure()).getContent(max + 100);
	}

}
