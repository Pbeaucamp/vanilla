package bpm.es.clustering.ui.gef.editparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.gef.figures.RepositoryFigure;
import bpm.vanilla.platform.core.beans.Repository;

public class RepositoryPart extends AbstractGraphicalEditPart{

	@Override
	protected IFigure createFigure() {
		Repository model = (Repository)getModel();
		Figure f = new RepositoryFigure(model.getName());
		f.setToolTip(new Label(Messages.RepositoryPart_0 + model.getUrl()));
		return f;
	}

	@Override
	protected void createEditPolicies() {
		
		
	}

}
