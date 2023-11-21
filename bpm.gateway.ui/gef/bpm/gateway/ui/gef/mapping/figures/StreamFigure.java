package bpm.gateway.ui.gef.mapping.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;

public class StreamFigure extends RectangleFigure {

	

	private Label title;

	public StreamFigure(String name){
		ToolbarLayout layout = new ToolbarLayout();
		this.setLayoutManager(layout);

		this.setLineStyle(Graphics.LINE_SOLID);
		this.setLineWidth(1);
		this.setForegroundColor(ColorConstants.black);
		this.setLayoutManager(new ToolbarLayout());
		
		

		this.setBackgroundColor(ColorConstants.tooltipBackground);

		RectangleFigure rect = new RectangleFigure();
		rect.setBackgroundColor(ColorConstants.lightBlue);
		rect.setLineWidth(1);
		rect.setLineStyle(Graphics.LINE_SOLID);
		rect.setLayoutManager(new ToolbarLayout());
		this.add(rect);
		title = new Label(name);
		rect.add(title);
		
		
		
		
	}
	
	public void setTitle(String text){
		title.setText(text);
		
	}

	
	
}
