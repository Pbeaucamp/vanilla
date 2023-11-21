package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;

public class CompartmentHorizontalFigure extends Figure {

	private CompartmentFigure c = new CompartmentFigure();
	private CompartmentAnchor cAnchor = new CompartmentAnchor();

	public CompartmentHorizontalFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setStretchMinorAxis(true);
		layout.setVertical(false);
		layout.setSpacing(2);
		setLayoutManager(layout);
		add(c);
		add(cAnchor);
		setBorder(new CompartmentFigureBorder());
	}

	public class CompartmentFigureBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(1, 0, 0, 0);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(),
					tempRect.getTopRight());
		}
	}

	public CompartmentFigure getColumnsCompartment() {

		return c;
	}

	public CompartmentAnchor getAnchorCompartment() {
		return cAnchor;
	}

}