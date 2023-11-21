package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class ColumnFigure extends Figure {

	private static final int GAP_TWEAK = 55;
	private Label labelColumn = new Label();
	private Label labelAnchorLeft = new Label("X");

	
	private boolean compteur = true;
	private int tableFigureWidth;
	private String name = "";



	public ColumnFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(false);
		setLayoutManager(layout);
		//setBorder(new LineBorder(1));

		add(labelAnchorLeft);
		labelAnchorLeft.setVisible(false);

		
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		labelColumn.setForegroundColor(ColorConstants.black);
		labelColumn.setBackgroundColor(ColorConstants.tooltipBackground);
		add(labelColumn);
		labelColumn.setFont(Fonts.normalSimple);

	
		setOpaque(true);
	}

	public void setLayout(Rectangle rect) {
		setBounds(rect);
	}

	public void setLabelColumn(String name) {

		if (compteur) {
			tableFigureWidth = ((TableFigure) (getParent().getParent()
					.getParent())).getCurrentWidth();
			this.name = name;
			compteur = false;
		}

		labelColumn.setText(name);

		
		if (((CompartmentFigure) getParent()).getCurrentWidth() < getWidth()) {
			((ToolbarLayout) getParent().getParent().getLayoutManager())
					.setSpacing(tableFigureWidth - GAP_TWEAK - getWidth());
			((CompartmentFigure) getParent()).setCurrentWidth(getWidth());
		}
	}

	public int getWidth() {
		int w = labelColumn.getTextBounds().width;
		
		return w;
	}

	public Figure getAnchorLeft() {
		return labelAnchorLeft;
	}

	public String getName() {
		return name;
	}

	

	

	
}