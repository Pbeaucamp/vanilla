package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.database.ui.viewer.relations.gef.editparts.ColumnPart;
import bpm.database.ui.viewer.relations.model.Column;

public class ColumnFigure extends Figure {

	private static final int GAP_TWEAK = 55;
	private Label labelColumn = new Label();
	//private Label labelAnchorLeft = new Label("X");

	
	private boolean compteur = true;
	private int tableFigureWidth;
	private String name = "";
	private ColumnPart part;
	private Column model;



	public ColumnPart getPart() {
		return part;
	}

	public ColumnFigure(Column model, ColumnPart column) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setVertical(false);
		setLayoutManager(layout);
		
		this.part =column;
		//setBorder(new LineBorder(1));

//		add(labelAnchorLeft);
//		labelAnchorLeft.setVisible(false);

		
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		if(model.exists()) {
			labelColumn.setForegroundColor(ColorConstants.black);
		}
		else {
			labelColumn.setForegroundColor(ColorConstants.red);
		}
		labelColumn.setBackgroundColor(ColorConstants.tooltipBackground);
		add(labelColumn);
		labelColumn.setFont(Fonts.normalSimple);

	
		setOpaque(true);
	}

	public void setLayout(Rectangle rect) {
		setBounds(rect);
		if(model.getColor() != null) {
			this.setBackgroundColor(model.getColor());
		}
	}

	public void setLabelColumn(String name) {

		if (compteur) {
			tableFigureWidth = ((TableFigure) (getParent().getParent()
					.getParent().getParent())).getCurrentWidth();
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

//	public Figure getAnchorLeft() {
//		return labelAnchorLeft;
//	}

	public String getName() {
		return name;
	}

	public void setModel(Column model) {
		this.model = model;
	}

	

	

	
}