package bpm.es.datasource.analyzer.ui.gef.parts;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.datasource.analyzer.ui.gef.model.Diagram;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;

public class ItemPart extends AbstractGraphicalEditPart implements NodeEditPart{
	private RepositoryLabelProvider labelProvider = new RepositoryLabelProvider();
	@Override
	protected IFigure createFigure() {
		
		
//		Label l = new Label(){
//			
//			protected void paintFigure(Graphics g) {
//				super.paintFigure(g);
//				Rectangle r = getTextBounds();
//
//				r.resize(-1, -1);
//				r.expand(1, 1);
//				r.width -= 1;
//				r.x -= 2;
//				g.drawLine(r.x, r.y, r.right(), r.y); //Top line
//				g.drawLine(r.x, r.bottom(), r.right(), r.bottom()); //Bottom line
//				g.drawLine(r.x, r.bottom(), r.x, r.y); //left line
//
//				g.drawLine(r.right() + 7, r.y + r.height / 2, r.right(), r.y);
//				g.drawLine(r.right()+7, r.y + r.height / 2, r.right(), r.bottom());
//			}
//		};
		
		
		Label l = new Label();
		
		l.setText(((RepositoryItem)getModel()).getItemName());
		
		
		l.setIconTextGap(4);
		
		l.setIcon(labelProvider.getImage((RepositoryItem)getModel()));
		l.setBorder(new MarginBorder(2,0,2,9));
		return l;
	}

	@Override
	protected void createEditPolicies() {
		
		
	}
	
	
	
	/**
	 * @see NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new EllipseAnchor(getFigure());
//	return new TopAnchor(getFigure(), 10);
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
//		return new TopAnchor(getFigure(), 10);
		return new EllipseAnchor(getFigure());
	}

	/**
	 * @see NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
//		return new BottomAnchor(getFigure(), 10);
		return new EllipseAnchor(getFigure());
	}

	/**
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
//		return new BottomAnchor(getFigure(), 10);
		return new EllipseAnchor(getFigure());
	}

	@Override
	protected List getModelSourceConnections() {
		Diagram d = (Diagram)getParent().getModel();
		
		
		return d.getIncomingLinks(getModel());
	}
}
