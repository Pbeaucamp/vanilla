package bpm.es.sessionmanager.zest;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;

import bpm.es.sessionmanager.api.UserWrapper;
import bpm.es.sessionmanager.api.server.VanillaServer;

public class ClientNode extends GraphNode {
	//private Object dataperso;
	
	public ClientNode(IContainer graphModel, int style, Object data) {
		//this.dataperso = data;
		super(graphModel, style, data);
	}

	@Override
	protected IFigure createFigureForModel() {
		if (getData() instanceof UserWrapper)
			return new ClientFigure((UserWrapper)getData());
		else if (getData() instanceof IFigure) {
			return (IFigure)getData();
		}
		else {
			throw new UnsupportedOperationException("could not find a valid figure"); //$NON-NLS-1$
		}
	}
	
	public Dimension getSize() {
		if (getData() instanceof ClientFigure) {
			UserWrapper user = ((ClientFigure)getData()).getUser();
			if (user.isConnected()) {
				Dimension dim = new Dimension(125, 60);
				return dim;
				//return super.getSize();
			}
			else {
				Dimension dim = new Dimension(75, 20);
				return dim;
				//return super.getSize();
			}
		}
		return super.getSize();
	}
}
