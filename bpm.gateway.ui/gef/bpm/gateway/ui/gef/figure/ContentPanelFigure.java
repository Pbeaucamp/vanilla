package bpm.gateway.ui.gef.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class ContentPanelFigure extends Figure{

	public ContentPanelFigure(){
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);
		setForegroundColor(ColorConstants.black);
		setBorder(new LineBorder(1));
		setOpaque(true);
	}
	
	
	public void setLayout(Rectangle rect){
		setBounds(rect);
	}
}
