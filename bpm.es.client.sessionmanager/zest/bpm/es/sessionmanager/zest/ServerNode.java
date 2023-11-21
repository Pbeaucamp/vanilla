package bpm.es.sessionmanager.zest;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;

import bpm.es.sessionmanager.api.server.VanillaServer;

public class ServerNode extends GraphNode {
	//private Object dataperso;
	
	public ServerNode(IContainer graphModel, int style, Object data) {
		//this.dataperso = data;
		super(graphModel, style, data);
	}

	@Override
	protected IFigure createFigureForModel() {
		if (getData() instanceof VanillaServer)
			return new ServerFigure((VanillaServer)getData());
		else if (getData() instanceof IFigure) {
			return (IFigure)getData();
		}
		else {
			throw new UnsupportedOperationException("could not find a valid figure"); //$NON-NLS-1$
		}
	}
	
	@Override
	public Dimension getSize() {
		Dimension dim = new Dimension(200, 40);
		return dim;
		//return super.getSize();
	}
}
