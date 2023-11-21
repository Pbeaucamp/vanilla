package groupviewer.layouts;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class RoundBorder extends AbstractBorder {

	public static final int RADIX = 10;
	
	public Insets getInsets(IFigure figure) {
		return null;
	}

	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		Rectangle bounds = figure.getBounds().crop(new Insets(1,1,1,1));
		graphics.drawRoundRectangle(bounds, 5, 5);		
	}
	
}
