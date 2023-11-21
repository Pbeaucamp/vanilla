package bpm.database.ui.viewer.relations.gef.editparts;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LayeredPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.GuideLayer;
import org.eclipse.gef.editparts.ScalableRootEditPart;

public class FmdtRootPart extends ScalableRootEditPart {

	public FmdtRootPart() {
		super();
	}
	
	public void refresh2(){
		refresh();
	}

	@Override
	protected LayeredPane createPrintableLayers() {
		LayeredPane pane = new LayeredPane();
		Layer layer = new Layer();
		layer.setLayoutManager(new StackLayout());
		pane.add(layer, PRIMARY_LAYER);

		pane.add(new ConnectionLayer(), CONNECTION_LAYER);

		return pane;
	}

	@Override
	protected void createLayers(LayeredPane layeredPane) {
		layeredPane.add(createGridLayer(), GRID_LAYER);

		layeredPane.add(getScaledLayers(), SCALABLE_LAYERS);
		layeredPane.add(new Layer() {
			public Dimension getPreferredSize(int wHint, int hHint) {
				return new Dimension();
			}
		}, HANDLE_LAYER);
		layeredPane.add(new FeedbackLayer(), FEEDBACK_LAYER);
		layeredPane.add(new GuideLayer(), GUIDE_LAYER);
	}

	class FeedbackLayer extends Layer {
		FeedbackLayer() {
			setEnabled(false);
		}

		/**
		 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
		 */
		public Dimension getPreferredSize(int wHint, int hHint) {
			Rectangle rect = new Rectangle();
			for (int i = 0; i < getChildren().size(); i++)
				rect.union(((IFigure) getChildren().get(i)).getBounds());
			return rect.getSize();
		}

	}
}
