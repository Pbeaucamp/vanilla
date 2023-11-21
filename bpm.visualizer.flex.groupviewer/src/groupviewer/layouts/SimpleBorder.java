package groupviewer.layouts;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;

public class SimpleBorder extends AbstractBorder {
	public static final int WEIGHT = 2;

	public Insets getInsets(IFigure figure) {
		return new Insets(WEIGHT, WEIGHT, WEIGHT, WEIGHT);

	}

	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		graphics.setLineWidth(WEIGHT);
		graphics.setLineCap(SWT.CAP_SQUARE);
		graphics.drawRectangle(figure.getBounds());
	}

}
