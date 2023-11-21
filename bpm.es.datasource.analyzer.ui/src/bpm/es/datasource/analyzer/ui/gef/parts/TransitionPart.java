package bpm.es.datasource.analyzer.ui.gef.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;

public class TransitionPart extends AbstractConnectionEditPart{

	@Override
	protected void createEditPolicies() {
		
		
	}
	
	protected IFigure createFigure() {

		PolylineConnection connection = (PolylineConnection) super.createFigure();
		connection.setTargetDecoration(new PolygonDecoration()); // arrow at target endpoint

		
		return connection;
	}

}
