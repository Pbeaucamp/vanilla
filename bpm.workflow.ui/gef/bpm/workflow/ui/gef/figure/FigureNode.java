package bpm.workflow.ui.gef.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class FigureNode extends Figure {
	private Label name = new Label();
	private ImageFigure image = null;
	public final static Color BLUE = new Color(Display.getCurrent(), 0, 0, 255);
	public final static Color GREEN = new Color(Display.getCurrent(), 17, 125, 0);
	public final static Color RED = new Color(Display.getCurrent(), 255, 0, 0);
	public final static Color MARRON = new Color(Display.getCurrent(), 112, 67, 0);

	private XYLayout layout;

	public FigureNode(Image pict) {
		layout = new XYLayout();

		if(pict != null) {
			image = new ImageFigure(pict);

		}

		layout = new XYLayout();
		setLayoutManager(layout);
		if(image != null) {
			add(image);

			setConstraint(image, new Rectangle(0, 0, -1, -1));
		}
		add(name);

	}

	public IFigure getFigure() {
		return image;

	}

	public void setImage(Image picture) {
		this.image = new ImageFigure(picture);
		add(image);
		setConstraint(image, new Rectangle(0, 0, -1, -1));
	}

	public void setLayout(Rectangle rect) {

		Rectangle r = new Rectangle(rect.x, rect.y, getPreferredSize().width, getPreferredSize().height);

		setBounds(r);

	}

	public void setName(String text) {
		name.setText(text);
		if(image != null) {
			setConstraint(name, new Rectangle(0, image.getPreferredSize().height, -1, -1));
		}
	}

	public void setCommentColor(Color color) {
		name.setBackgroundColor(color);
		name.setForegroundColor(color);
		image.setBackgroundColor(color);
		image.setForegroundColor(color);

	}

}
