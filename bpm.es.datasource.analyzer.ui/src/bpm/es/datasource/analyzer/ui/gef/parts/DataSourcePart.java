package bpm.es.datasource.analyzer.ui.gef.parts;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.datasource.analyzer.ui.gef.model.Diagram;
import bpm.vanilla.platform.core.repository.DataSource;


public class DataSourcePart extends AbstractGraphicalEditPart{

	@Override
	protected IFigure createFigure() {
		Label l = new Label();
		l.setText(((DataSource)getModel()).getName());
		return l;
	}

	@Override
	protected void createEditPolicies() {
		
		
	}
	
	/**
	 * @see NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
	return new TopAnchor(getFigure(), 10);
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new TopAnchor(getFigure(), 10);
	}

	/**
	 * @see NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new BottomAnchor(getFigure(), 10);
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new BottomAnchor(getFigure(), 10);
	}

	@Override
	protected List getModelTargetConnections() {
		Diagram d = (Diagram)getParent().getModel();
		
		
		return d.getOutgoingLinks(getModel());
	}
}
