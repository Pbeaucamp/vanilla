package bpm.gateway.ui.gef.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;

public class FigureGDI extends RectangleFigure{

	private Label name;
	
	public FigureGDI(){
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);
		
		name = new Label();
		
		add(name);
		
		
		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		setBackgroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.GID_COLOR_KEY));
		setForegroundColor(ColorConstants.black);
		setBorder(new LineBorder(1));
		
		

	}
	
	public void setName(String text) {
		name.setText(text);
		setConstraint(name, new Rectangle(0,0, -1, -1));
	}
}
