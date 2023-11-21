package bpm.workflow.ui.gef.figure;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class FigureLoop extends Figure {

	private Label name;
	private ImageFigure image = null;

	public FigureLoop(Image pict, int height, int width) {
		XYLayout layout = new XYLayout();

		setLayoutManager(layout);

		name = new Label();

		add(name);

		setBorder(new CompartmentFigureBorder());

	}

	public class CompartmentFigureBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(24, 24, 24, 24);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			int x = figure.getBounds().x;
			int y = figure.getBounds().y;
			int witdh = figure.getBounds().width;
			int height = figure.getBounds().height;

			Rectangle haut = new Rectangle(x + (witdh / 4), y, (witdh / 2), (height / 2));
			graphics.drawLine(haut.getTopLeft(), haut.getTopRight());

			Rectangle bas = new Rectangle(x + (witdh / 4), y + height - 1, (witdh / 2), (height / 2));
			graphics.drawLine(bas.getTopLeft(), bas.getTopRight());

			Rectangle droite = new Rectangle(x + (3 * (witdh / 4)) - 1, y + (height / 4), (witdh / 4), (height / 2));
			graphics.drawLine(droite.getTopRight(), droite.getBottomRight());

			Rectangle gauche = new Rectangle(x, y + (witdh / 4), (witdh / 2), (height / 2));
			graphics.drawLine(gauche.getTopLeft(), gauche.getBottomLeft());
			graphics.drawArc(x, y, (witdh / 4), (height / 4), 90, 90);
			graphics.drawArc(x + (3 * (witdh / 4)) - 1, y, (witdh / 4), (height / 4), 0, 90);
			graphics.drawArc(x + (3 * (witdh / 4)) - 1, y + (3 * (height / 4)) - 1, (witdh / 4), (height / 4), 270, 90);
			graphics.drawArc(x, y + (3 * (height / 4)) - 1, (witdh / 4), (height / 4), 180, 90);

		}
	}

	public IFigure getFigure() {
		return image;

	}

	public void resizeFigure(int width, int height) {

	}

	public void setLayout(Rectangle rect) {
		Rectangle r = new Rectangle(rect.x, rect.y, getPreferredSize().width, getPreferredSize().height);

		setBounds(r);
	}

	public void setImage(Image picture) {
		this.image = new ImageFigure(picture);
		add(image);
		setConstraint(image, new Rectangle(0, 0, -1, -1));

	}

	public void setName(String text) {
		name.setText(text);
		setConstraint(name, new Rectangle(2, 2, -1, -1));

	}
}
