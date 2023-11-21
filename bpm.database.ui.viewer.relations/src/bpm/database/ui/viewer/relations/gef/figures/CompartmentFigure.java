package bpm.database.ui.viewer.relations.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;


public class CompartmentFigure extends Figure {

	private int currentWidth=0;
	
	public CompartmentFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setSpacing(2);
		setLayoutManager(layout);

	}

	public int getCurrentWidth() {
		return currentWidth;
	}
	public void setCurrentWidth(int currentWidth) {
		this.currentWidth = currentWidth;
	}
	
	
}