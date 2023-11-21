package bpm.es.clustering.ui.gef.editparts;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.clustering.ui.Messages;
import bpm.es.clustering.ui.gef.figures.ClientFigure;
import bpm.vanilla.platform.core.beans.Server;

public class ClientPart extends AbstractGraphicalEditPart{

	@Override
	protected IFigure createFigure() {
		Server s = (Server)getModel();
		Figure f = new ClientFigure(s.getComponentNature(), s.getName());
		f.setToolTip(new Label(Messages.ClientPart_0 + s.getUrl()));
		return f;
	}

	@Override
	protected void createEditPolicies() {
		
		
	}

}
