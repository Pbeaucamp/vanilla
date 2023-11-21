package bpm.es.clustering.ui.gef.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.clustering.ui.gef.GefModel;
import bpm.es.clustering.ui.gef.figures.PlatformFigure;

public class PlatformPart extends AbstractGraphicalEditPart{

	@Override
	protected IFigure createFigure() {
//		Figure f = new Figure(){
//			@Override
//			public void add(IFigure figure, Object constraint, int index) {
//				
//				super.add(figure, constraint, index);
//			}
//		};
//		f.setBorder(new LineBorder(1));
//		f.setLayoutManager(new ToolbarLayout(false));
		
		Figure f = new PlatformFigure();
		
		return f;
	}

	@Override
	protected void createEditPolicies() {
		
		
	}
	
	@Override
	protected List getModelChildren() {
		GefModel model = (GefModel)getModel();
		List l = new ArrayList();
		l.addAll(model.getClients());
		l.add(model.getDefaultModule());
		l.addAll(model.getModules());
		
		l.addAll(model.getRepositories());
		
		return l;
	}

}
