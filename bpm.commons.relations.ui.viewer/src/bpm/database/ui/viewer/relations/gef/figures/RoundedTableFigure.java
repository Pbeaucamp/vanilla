package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public class RoundedTableFigure extends RoundedRectangle {

	private Label labelName = new Label();
	private CompartmentHorizontalFigure columns = new CompartmentHorizontalFigure();
	public static final int TABLE_FIGURE_DEFWIDTH = 9;
	public static final int TABLE_FIGURE_DEF_BESTHEIGHT = 19;

	public RoundedTableFigure() {
	
		ToolbarLayout layout = new ToolbarLayout();
		
		setLayoutManager(layout);
		//setBorder(new LineBorder(ColorConstants.black, 1));
		setOpaque(true);
		labelName.setForegroundColor(ColorConstants.black);
		labelName.setFont(Fonts.normalBold);
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		add(labelName);
		add(columns);
 /*
		
		labelName.setForegroundColor(ColorConstants.black);
		labelName.setFont(Fonts.normalBold);	
		
		//roundedRect.setBorder((Border) new LineBorder(ColorConstants.black, 1));
		roundedRect.setOpaque(true);
		roundedRect.setBackgroundColor(ColorConstants.tooltipBackground);
		roundedRect.add(labelName);
		roundedRect.add(columns);
		add(roundedRect);
		*/
		
	}

	public void setLayout(Rectangle rect) {
		setCornerDimensions(new Dimension(35, 35));
		setBounds(rect);
	}

	
	public void setName(String text) {
		labelName.setText(text);
	}

	public CompartmentFigure getColumnsCompartment() {
		return columns.getColumnsCompartment();
	}


}
