package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class TableFigure extends Figure {

	private Label labelName = new Label();
	private CompartmentHorizontalFigure columns = new CompartmentHorizontalFigure();
	public static final int TABLE_FIGURE_DEFWIDTH = 9;
	public static final int TABLE_FIGURE_DEF_BESTHEIGHT = 19;

	public TableFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(ColorConstants.black, 1));
		setOpaque(true);
		labelName.setForegroundColor(ColorConstants.black);
		labelName.setFont(Fonts.normalBold);
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		add(labelName);
		add(columns);
	}

	public void setLayout(Rectangle rect) {
		setBounds(rect);
	}

	public int getCurrentWidth() {
		return getBounds().width;
	}

	public void setName(String text) {
		labelName.setText(text);
	}

	public CompartmentFigure getColumnsCompartment() {
		return columns.getColumnsCompartment();
	}

	public CompartmentAnchor getRightAnchors() {
		return columns.getAnchorCompartment();
	}

}