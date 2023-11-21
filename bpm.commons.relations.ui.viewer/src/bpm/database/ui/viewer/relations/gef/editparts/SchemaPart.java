package bpm.database.ui.viewer.relations.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;

import bpm.database.ui.viewer.relations.gef.policies.AppEditLayoutPolicy;
import bpm.database.ui.viewer.relations.model.Node;

public class SchemaPart extends AppAbstractEditPart {

	@Override
	protected IFigure createFigure() {
		final IFigure figure = new FreeformLayer();
		figure.setBorder(new MarginBorder(3));
		XYLayout layout = new XYLayout();
//		FlowLayout layout = new FlowLayout(){
//			
//			@Override
//			protected void initVariables(IFigure parent) {
//				super.initVariables(parent);
//				data.maxWidth = getRoot().getViewer().getControl().getSize().x;
//			}
//
//		};
//		layout.setMajorSpacing(20);
//		layout.setMinorSpacing(20);
//		layout.setMajorAlignment(FlowLayout.ALIGN_CENTER);
//		layout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
		figure.setLayoutManager(layout);
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ManhattanConnectionRouter());
		
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
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)) {
			refreshVisuals();
		}
	}
}