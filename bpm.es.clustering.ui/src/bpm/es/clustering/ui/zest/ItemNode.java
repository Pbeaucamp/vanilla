package bpm.es.clustering.ui.zest;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;

import bpm.es.clustering.ui.composites.VisualMappingComposite;
import bpm.es.clustering.ui.icons.Icons;
import bpm.es.clustering.ui.model.VanillaPlatformModule;
import bpm.vanilla.platform.core.beans.Server;

public class ItemNode extends GraphNode {

	public ItemNode(IContainer graphModel, int style, Object data) {
		super(graphModel, style, data);
	}

	@Override
	protected IFigure createFigureForModel() {
		if (getData() instanceof Server)
			return new ItemFigure((Server) getData());
		else if (getData() instanceof IFigure) {
			return (IFigure) getData();
		}
		else {
			throw new UnsupportedOperationException("could not find a valid figure"); //$NON-NLS-1$
		}
	}

	public Dimension getSize() {
		if (getData() instanceof ItemFigure) {
			Object element = ((ItemFigure) getData()).getContent();
			if (element instanceof String) {
				if (element instanceof String) {
					if (((String) element).equals(VisualMappingComposite.CLUSTERS) || ((String) element).equals(VisualMappingComposite.MASTER)) {
						Dimension dim = new Dimension(125, 80);
						return dim;
					}
					else {
						Dimension dim = new Dimension(180, 80);
						return dim;
					}
				}
			}
			else if (element instanceof VanillaPlatformModule) {
				Dimension dim = new Dimension(200, 80);
				return dim;
			}
			else if (element instanceof Server) {
				if (((Server) element).getComponentStatus() != null) {
					Dimension dim = new Dimension(150, 45);
					return dim;
				}
				else {
					Dimension dim = new Dimension(120, 30);
					return dim;
				}
			}
			else {
				Dimension dim = new Dimension(120, 30);
				return dim;
			}
		}
		return super.getSize();
	}
}
