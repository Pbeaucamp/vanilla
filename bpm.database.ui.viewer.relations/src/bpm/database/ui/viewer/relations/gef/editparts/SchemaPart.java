package bpm.database.ui.viewer.relations.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;

import bpm.database.ui.viewer.relations.gef.policies.AppEditLayoutPolicy;
import bpm.database.ui.viewer.relations.model.Node;

public class SchemaPart extends AppAbstractEditPart {

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
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());

	}
	

	

	public List<Node> getModelChildren() {
		return ((Node) getModel()).getChildrenArray();
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
//		if (evt.getPropertyName().equals(Node.PROPERTY_ADD))
//			refreshChildren();
//		if (evt.getPropertyName().equals(Node.PROPERTY_REMOVE))
//			refreshChildren();
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT))
			refreshVisuals();
//		if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
//			refreshSourceConnections();
//		}
//		if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP) ){
//			refreshTargetConnections();
//		}
	}
}