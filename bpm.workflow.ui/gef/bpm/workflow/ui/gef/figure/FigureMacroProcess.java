package bpm.workflow.ui.gef.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class FigureMacroProcess extends Figure {

	private ImageFigure image = null;

	public FigureMacroProcess(Image pict, int width) {
		XYLayout layout = new XYLayout();

		setLayoutManager(layout);

		ImageData data = pict.getImageData().scaledTo(width, pict.getBounds().height);

		Image toto = new Image(pict.getDevice(), data);

		pict = toto;

		image = new ImageFigure(pict);

		image.setSize(width, pict.getBounds().height);
		image.setBorder(null);
		add(image);

		setConstraint(image, new Rectangle(0, 0, -1, -1));
		setBorder(new LineBorder(1));

	}

	public IFigure getFigure() {
		return image;

	}

	public void resizeFigure(int width) {
		remove(image);
		Image pict = image.getImage();

		image = null;
		ImageData data = pict.getImageData().scaledTo(width, pict.getBounds().height);

		Image toto = new Image(pict.getDevice(), data);
		pict = toto;

		image = new ImageFigure(pict);
		image.setSize(width, pict.getBounds().height);
		image.setBorder(null);
		add(image);
		setConstraint(image, new Rectangle(0, 0, -1, -1));

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
}
