package bpm.gateway.ui.gef.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.InlineFlow;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

public class FigureAnnote extends RectangleFigure {

	private Label name;
		
	private ToolbarLayout layout;
	
	public FigureAnnote(){
		layout = new ToolbarLayout();
		setLayoutManager(layout);
		
		this.setLineStyle(Graphics.LINE_SOLID);
		this.setLineWidth(1);
		this.setBackgroundColor(ColorConstants.tooltipBackground);
		
		InlineFlow in = new InlineFlow();
		in.setBackgroundColor(ColorConstants.tooltipBackground);
		
		
		name = new Label();
		name.setLabelAlignment(PositionConstants.LEFT);
		add(name);

//		setConstraint(name, new Rectangle(0,image.getPreferredSize().height, -1, -1));
		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		
//		setBackgroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.ANNOTE_COLOR_KEY));
	}
	
	
	public void setColor(Color c){
		this.setBackgroundColor(c);
	}
	
	public void setLayout(Rectangle rect) {
       
        Rectangle r = new Rectangle(rect.x, rect.y,rect.width, rect.height);
        setBounds(r);
        this.layout();

	}

	public void setName(String text) {
		name.setText(text);
//		setConstraint(name, new Rectangle(0,image.getPreferredSize().height, -1, -1));
	}
	
	public String getName(){
		return new String(name.getText());
	}
}
