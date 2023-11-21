package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.database.ui.viewer.relations.model.Table;

public class TableFigure extends Figure {

//	private Label labelName = new Label();
//	private CompartmentHorizontalFigure columns = new CompartmentHorizontalFigure();
	private RoundedTableFigure roundedTable = new RoundedTableFigure();
	private Table model;
	public static final int TABLE_FIGURE_DEFWIDTH = 9;
	public static final int TABLE_FIGURE_DEF_BESTHEIGHT = 19;

	public TableFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		add(roundedTable);
		
		/*
		setBorder(new LineBorder(ColorConstants.black, 1));
		setOpaque(true);
		labelName.setForegroundColor(ColorConstants.black);
		labelName.setFont(Fonts.normalBold);
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		add(labelName);
		add(columns);
		*/
	}

	public void setLayout(Rectangle rect) {
		//rect.setHeight(rect.height+5);		
		setBounds(rect);
		roundedTable.setLayout(rect);
		if(model.getColor() != null) {
			roundedTable.setBackgroundColor(model.getColor());
		}
		repaint();
		
	}

	public int getCurrentWidth() {
		return getBounds().width;
	}

	public void setName(String text) {
		roundedTable.setName(text);
	}

	public CompartmentFigure getColumnsCompartment() {
		return roundedTable.getColumnsCompartment();
	}
	/*
	   @Override
		public void paintFigure(Graphics graphics) {		   
	    Rectangle r = getBounds().getCopy();
	    setConstraint(roundedTable, new Rectangle(0, 0, r.width, r.height));
	    super.paintFigure(graphics);
	}
*/
//	public CompartmentAnchor getRightAnchors() {
//		return columns.getAnchorCompartment();
//	}

	public void setModel(Table model) {
		this.model = model;
		
	}

	
	
	/*
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

//	public CompartmentAnchor getRightAnchors() {
//		return columns.getAnchorCompartment();
//	}

*/

}