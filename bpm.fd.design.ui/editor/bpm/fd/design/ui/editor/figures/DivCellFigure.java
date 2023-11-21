package bpm.fd.design.ui.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class DivCellFigure extends Figure {

	private static final Font font = new Font(Display.getDefault(), new FontData("Arial", 12, SWT.ITALIC));
	private static final Color nameColor = new Color(Display.getDefault(), 227, 246, 206);
	public static final Color errorColor = new Color(Display.getDefault(), 250, 2, 35);

	private Label name;
	private XYLayout layout;

	public DivCellFigure() {
		layout = new XYLayout();

		setLayoutManager(layout);

		name = new Label();
		name.setOpaque(false);

		add(name);
		name.setFont(font);
		name.setOpaque(true);
//		name.setBorder(new LineBorder(ColorConstants.black, 1, Graphics.LINE_SOLID));
		name.setBackgroundColor(nameColor);
		setConstraint(name, new Rectangle(0, 0, -1, -1));

		this.setBorder(new LineBorder(1));

	}

	public void setLayout(Rectangle rect) {
		Rectangle r = new Rectangle(rect.x, rect.y, rect.width, rect.height);
		setBounds(r);
	}
	
	public void setName(String cellName) {
		name.setText(cellName);
	}
	
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		super.add(figure, constraint, index);
		
	}

}
